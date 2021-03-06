package kro.kr.rhya_network.dr_talk.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.adapter.DrListAdapter;
import kro.kr.rhya_network.dr_talk.application.RhyaApplication;
import kro.kr.rhya_network.dr_talk.data.DrInfo;
import kro.kr.rhya_network.dr_talk.data.DrListItem;
import kro.kr.rhya_network.dr_talk.security.RhyaAESManager;
import kro.kr.rhya_network.dr_talk.sort.RhyaComparator;
import kro.kr.rhya_network.dr_talk.util.RhyaAsyncTask;
import kro.kr.rhya_network.dr_talk.util.RhyaNetworkAPI;
import kro.kr.rhya_network.dr_talk.util.RhyaRequestHttpURLConnection;
import kro.kr.rhya_network.dr_talk.util.RhyaSharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrListFragment extends Fragment {
    // ?????? ?????? ??????
    private boolean isInit;
    // ??????
    public int sortType;
    // ?????? Thread ?????? ??????
    public boolean isLoadTask = false;
    // UI Object
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressWheel progressWheel;
    private ImageView imageView;
    private RecyclerView drListRecyclerView;
    // Adapter
    private DrListAdapter drListAdapter;
    // Add values
    private ArrayList<DrInfo> drInfoArrayList;
    // RHYA.Network API
    private RhyaNetworkAPI rhyaNetworkAPI;
    // RHYA.Network AES Manager
    private RhyaAESManager rhyaAESManager;
    private String ImageSavePath;
    // RHYA.Network SharedPreferences ?????????
    private RhyaSharedPreferences rhyaSharedPreferences;
    // ?????? ??????
    private String DrListFileName;


    public DrListFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment dr_list.
     */
    // TODO: Rename and change types and number of parameters
    public static DrListFragment newInstance() {
        return new DrListFragment();
    }


    /**
     * onCreate - ?????? ??? ??????
     *
     * @param savedInstanceState [Override]
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ?????????
        rhyaNetworkAPI = new RhyaNetworkAPI(getContext());
        rhyaAESManager = new RhyaAESManager(getContext());
        isInit = false;
    }


    /**
     * onViewCreated - View ?????? ??? ??????
     *
     * @param view               [Override]
     * @param savedInstanceState [Override]
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rhyaSharedPreferences = new RhyaSharedPreferences(getContext(), true);

        // ?????? ?????? ??????
        try {
            String result = rhyaSharedPreferences.getStringNoAES(rhyaSharedPreferences.SHARED_PREFERENCES_DR_LIST_SORT_TYPE, requireContext());
            if (result.equals(rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE)) {
                sortType = 0;
            }else {
                sortType = Integer.parseInt(result);
            }

            if ((sortType != 0) && (sortType != 1)) {
                sortType = 0;
            }
        }catch (Exception ex) {
            ex.printStackTrace();

            sortType = 0;
        }

        // ?????? ??????
        String rootSavePath = requireActivity().getFilesDir().getAbsolutePath();
        DrListFileName = createString(new String[] { rootSavePath, File.separator, "drinfo.json" });
        ImageSavePath = createString(new String[] { rootSavePath, File.separator, "res_image" });

        // UI ?????????
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        drListRecyclerView = view.findViewById(R.id.drListRecyclerView);
        progressWheel = view.findViewById(R.id.progress_wheel);
        imageView = view.findViewById(R.id.imageView);

        // Adapter
        drListAdapter = new DrListAdapter(requireActivity());

        // Adapter ??????
        drListRecyclerView.setAdapter(drListAdapter);
        drListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // ????????? ??????
        drInfoArrayList = new ArrayList<>();
        reloadThread(false);

        // ????????? ??????
        drListAdapter.setDrList(RhyaApplication.DrInfoData.getDrListItemArrayList());

        // ???????????? ????????? ??????
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // ?????????
            reloadThread(true);
        });
    }


    /**
     * reloadThread - ?????? ????????? ?????? ??????
     */
    private void reloadThread(boolean isRewrite) {
        // ?????? ????????? ?????????
        final String DrListMessageJsonKey = "message";
        final String SplitText = "#";
        final String SplitStopString = "No";
        final String DefaultImage = "#DEF";

        // ?????????
        RhyaApplication.DrInfoData.clearDrListItemArrayList();

        // ?????? ?????? ??????
        if (!isLoadTask) {
            // ?????? ?????? ?????? ??????
            isLoadTask = true;
            // ????????? ?????? ??????
            new RhyaAsyncTask<String, String>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    // ?????? ?????? ??????
                    requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.VISIBLE));
                    requireActivity().runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                    requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.GONE));

                    // Json key ?????????
                    final String json_key_result = "result";
                    final String json_result_success = "S";
                    final String json_key_id = "id";
                    final String json_key_name = "name";
                    final String json_key_type = "type";
                    final String json_key_image = "image";
                    final String json_key_phone = "phone";
                    final String json_key_location = "location";
                    final String json_key_location_name = "location_name";
                    final String json_key_location_phone = "location_phone";
                    final String json_key_description = "description";
                    final String json_key_license = "license";
                    final String json_key_others = "others";
                    final String json_key_foc = "foc";

                    // Json ?????? ?????? ??????
                    boolean isChangeFile = false;

                    // ?????? ??????
                    File rootJsonFile = new File(DrListFileName);

                    // ?????? ??????
                    if (rootJsonFile.exists()) {
                        // ?????? ??????
                        String readFile = readToFile(DrListFileName);
                        // ?????? ?????????
                        try {
                            readFile = rhyaAESManager.aesDecode(readFile);
                        } catch (UnsupportedEncodingException |
                                InvalidAlgorithmParameterException |
                                InvalidKeyException |
                                NoSuchAlgorithmException |
                                BadPaddingException |
                                IllegalBlockSizeException |
                                NoSuchPaddingException e) {
                            e.printStackTrace();

                            readFile = null;

                            // ?????? ????????? ??????
                            ShowToastMessage(e.getMessage());
                        }

                        // ????????? ?????? ?????? ??????
                        arg = readFile;
                    }

                    // ???????????? ??????
                    if (isRewrite) {
                        // ?????? ?????? ??????
                        arg = null;
                        // ????????? ?????????
                        drInfoArrayList.clear();
                    }

                    // Null ??????
                    if (arg != null) {
                        // Json ????????? ??????
                        try {
                            // Json ??????
                            JSONArray jsonArray = new JSONArray(arg);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonGetObject = jsonArray.getJSONObject(i);
                                // Json ????????? ??????
                                DrInfo drInfo = new DrInfo();
                                drInfo.dr_id = jsonGetObject.getString(json_key_id);
                                drInfo.dr_name = jsonGetObject.getString(json_key_name);
                                drInfo.dr_image = jsonGetObject.getString(json_key_image);
                                drInfo.dr_type = jsonGetObject.getString(json_key_type);
                                drInfo.dr_phone = jsonGetObject.getString(json_key_phone);
                                drInfo.dr_location = jsonGetObject.getString(json_key_location);
                                drInfo.dr_location_name = jsonGetObject.getString(json_key_location_name);
                                drInfo.dr_location_phone = jsonGetObject.getString(json_key_location_phone);
                                drInfo.dr_description = jsonGetObject.getString(json_key_description);
                                drInfo.dr_license = jsonGetObject.getString(json_key_license);
                                drInfo.dr_others = jsonGetObject.getString(json_key_others);
                                drInfo.dr_foc = jsonGetObject.getString(json_key_foc);

                                drInfoArrayList.add(drInfo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // ?????? ????????? ??????
                            ShowToastMessage(e.getMessage());
                        }
                    }

                    // ????????? ?????? ?????? ?????? - 1
                    boolean resultSaveData = false;
                    Activity activity_check = getActivity();
                    if (activity_check != null) {
                        if (!activity_check.isFinishing()) {
                            resultSaveData = rhyaSharedPreferences.getStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_SAVE_DATA_ON_OFF, requireContext()).equals(rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_ON);
                        }
                    }
                    // ????????? ?????? ?????? ?????? - 2
                    if (!resultSaveData || isInit) {
                        // ?????? ??????
                        try {
                            // ?????? ??????
                            ContentValues urlParm = new ContentValues(); // ????????????
                            urlParm.put(rhyaNetworkAPI.DR_TALK_AUTH_TOKEN_PARM, rhyaNetworkAPI.getAutoLogin());
                            RhyaRequestHttpURLConnection requestHttpURLConnection = new RhyaRequestHttpURLConnection();
                            String result = requestHttpURLConnection.request(rhyaNetworkAPI.DR_TALK_GET_DR_LIST, urlParm); // ?????? URL??? ?????? ???????????? ????????????.
                            // ?????? ????????? ??????
                            JSONObject jsonObject = new JSONObject(result);

                            // ?????? ??????
                            try {
                                // ?????? ??????
                                if (!jsonObject.getString(json_key_result).equals(json_result_success)) {
                                    // Activity ?????? ??????
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        if (!activity.isFinishing()) {
                                            // ????????? ??????
                                            requireActivity().runOnUiThread(() -> ((MainActivity) getActivity()).onErrorAccountShowMsg());
                                        }
                                    }
                                }
                            }catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            String[] split = jsonObject.getString(DrListMessageJsonKey).split(SplitText);
                            for (String value : split) {
                                // ?????? ????????? ??????
                                if (value.equals(SplitStopString)) {
                                    continue;
                                }

                                // ?????? ????????? ??????
                                boolean isToF = false;

                                // ?????? ??????
                                if (arg != null) {
                                    // ?????? ?????? ?????? ??????
                                    for (DrInfo drInfo : drInfoArrayList) {
                                        if (drInfo.dr_id.equals(value)) {
                                            isToF = true;

                                            break;
                                        }
                                    }
                                }

                                // ?????? ?????? ??????
                                if (!isToF) {
                                    // ?????? ??????
                                    try {
                                        // Json ??????
                                        urlParm.put(rhyaNetworkAPI.DR_TALK_INPUT_DR_ID, value);
                                        String doctor_info = requestHttpURLConnection.request(rhyaNetworkAPI.DR_TALK_GET_DR_INFO, urlParm);
                                        doctor_info = URLDecoder.decode(doctor_info, "UTF-8");
                                        jsonObject = new JSONObject(doctor_info);

                                        // ?????? ??????
                                        try {
                                            // ?????? ??????
                                            if (!jsonObject.getString(json_key_result).equals(json_result_success)) {
                                                // Activity ?????? ??????
                                                Activity activity = getActivity();
                                                if (activity != null) {
                                                    if (!activity.isFinishing()) {
                                                        // ????????? ??????
                                                        requireActivity().runOnUiThread(() -> ((MainActivity) getActivity()).onErrorAccountShowMsg());
                                                    }
                                                }
                                            }
                                        }catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                        // Json ?????????
                                        String image = jsonObject.getString(json_key_image);
                                        // ????????? ??????
                                        DrInfo drInfo = new DrInfo();
                                        drInfo.dr_id = value;
                                        drInfo.dr_name = jsonObject.getString(json_key_name);
                                        drInfo.dr_image = image;
                                        drInfo.dr_type = jsonObject.getString(json_key_type);
                                        drInfo.dr_phone = jsonObject.getString(json_key_phone);
                                        drInfo.dr_location = jsonObject.getString(json_key_location);
                                        drInfo.dr_location_name = jsonObject.getString(json_key_location_name);
                                        drInfo.dr_location_phone = jsonObject.getString(json_key_location_phone);
                                        drInfo.dr_description = jsonObject.getString(json_key_description);
                                        drInfo.dr_license = jsonObject.getString(json_key_license);
                                        drInfo.dr_others = jsonObject.getString(json_key_others);
                                        drInfo.dr_foc = jsonObject.getString(json_key_foc);

                                        // ????????? ???????????? ?????? ??????
                                        File rootImagePathCheck = new File(ImageSavePath);
                                        if (!rootImagePathCheck.exists()) {
                                            if (!rootImagePathCheck.mkdir()) {
                                                continue;
                                            }
                                        }
                                        File downloadImagePath_image = new File(
                                                createString(
                                                        new String[] { ImageSavePath, File.separator, image }
                                                ));
                                        // ????????? ????????? ?????? ??????
                                        if (downloadImagePath_image.exists()) {
                                            if (!downloadImagePath_image.delete()) {
                                                continue;
                                            }
                                        }
                                        // ????????? ???????????? ?????? ??????
                                        if (!image.equals(DefaultImage)) {
                                            // ????????? ????????????
                                            downloadFile(
                                                    new URL(
                                                            createString(
                                                                    new String[] { rhyaNetworkAPI.FILE_DOWNLOAD_APP_URL, image }
                                                            )
                                                    ), downloadImagePath_image);
                                        }

                                        // ?????? ?????? ?????? ??????
                                        if (!isChangeFile) { isChangeFile = true; }
                                        drInfoArrayList.add(drInfo);
                                    }catch (Exception e) {
                                        e.printStackTrace();

                                        // ?????? ????????? ??????
                                        ShowToastMessage(e.getMessage());

                                        break;
                                    }
                                }
                            }

                            // ?????? ?????? ??????
                            if (isChangeFile) {
                                // ?????? ?????? ?????? ?????? ??????
                                JSONArray drInfoJsonRootArray = new JSONArray();
                                for (DrInfo drInfo : drInfoArrayList) {
                                    // ????????? ??????
                                    JSONObject input_jsonObject = new JSONObject();
                                    input_jsonObject.put(json_key_id, drInfo.dr_id);
                                    input_jsonObject.put(json_key_name, drInfo.dr_name);
                                    input_jsonObject.put(json_key_image, drInfo.dr_image);
                                    input_jsonObject.put(json_key_phone, drInfo.dr_phone);
                                    input_jsonObject.put(json_key_type, drInfo.dr_type);
                                    input_jsonObject.put(json_key_location, drInfo.dr_location);
                                    input_jsonObject.put(json_key_location_name, drInfo.dr_location_name);
                                    input_jsonObject.put(json_key_location_phone, drInfo.dr_location_phone);
                                    input_jsonObject.put(json_key_description, drInfo.dr_description);
                                    input_jsonObject.put(json_key_license, drInfo.dr_license);
                                    input_jsonObject.put(json_key_others, drInfo.dr_others);
                                    input_jsonObject.put(json_key_foc, drInfo.dr_foc);

                                    drInfoJsonRootArray.put(input_jsonObject);
                                }
                                // ????????? ?????????
                                String writeData = drInfoJsonRootArray.toString();
                                writeData = rhyaAESManager.aesEncode(writeData);
                                // ?????? ??????
                                writeToFile(DrListFileName, writeData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            // ?????? ????????? ??????
                            ShowToastMessage(e.getMessage());
                        }
                    }

                    // Activity ?????? ??????
                    Activity activity = getActivity();
                    if (activity != null) {
                        if (!activity.isFinishing()) {
                            // RecyclerView ????????? ??????
                            for (DrInfo drInfo : drInfoArrayList) {
                                RhyaApplication.DrInfoData.addDrListItemArrayList(
                                        new DrListItem(
                                                drInfo.dr_id,
                                                drInfo.dr_name,
                                                createString(new String[] { ImageSavePath, File.separator, drInfo.dr_image }),
                                                drInfo.dr_description,
                                                drInfo.dr_type,
                                                drInfo.dr_phone,
                                                drInfo.dr_location,
                                                drInfo.dr_location_name,
                                                drInfo.dr_location_phone,
                                                drInfo.dr_license,
                                                drInfo.dr_others,
                                                drInfo.dr_foc)
                                );
                            }
                            // ????????? ?????????
                            drInfoArrayList.clear();
                            // ????????? ??????
                            drListSort();
                            // RecyclerView ????????? ??????
                            requireActivity().runOnUiThread(() -> drListAdapter.setDrList(RhyaApplication.DrInfoData.getDrListItemArrayList()));
                            // ?????? ?????? ??????
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                        }
                    }

                    // ?????? ??????
                    isLoadTask = false;
                    // ????????? ??????
                    if (!isInit) { isInit = true; }


                    return null;
                }

                @Override
                protected void onPostExecute(String result) { }
            }.execute(null);
        }
    }

    /**
     * drListSort - ?????? ????????? ??????
     */
    private void drListSort() {
        // ?????? ?????? ??????
        rhyaSharedPreferences.setStringNoAES(rhyaSharedPreferences.SHARED_PREFERENCES_DR_LIST_SORT_TYPE, Integer.toString(sortType), requireContext());

        // ??????
        if (this.sortType == 1) {
            Collections.sort(RhyaApplication.DrInfoData.getDrListItemArrayList(), new RhyaComparator.DescendingName());
        }else if (this.sortType == 0) {
            Collections.sort(RhyaApplication.DrInfoData.getDrListItemArrayList(), new RhyaComparator.AscendingName());
        }else {
            Collections.sort(RhyaApplication.DrInfoData.getDrListItemArrayList(), new RhyaComparator.DescendingName());
        }
    }


    /**
     * drListSortThread - ?????? ????????? ?????? Thread
     */
    public void drListSortThread() {
        // ?????? ?????? ??????
        if (!isLoadTask) {
            // ?????? ?????? ?????? ??????
            isLoadTask = true;
            // ????????? ?????? ??????
            new RhyaAsyncTask<String, String>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    // Activity ?????? ??????
                    Activity activity = getActivity();
                    if (activity != null) {
                        if (!activity.isFinishing()) {
                            // ?????? ?????? ??????
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.GONE));

                            // ????????? ?????????
                            drInfoArrayList.clear();
                            // ????????? ??????
                            drListSort();
                            // RecyclerView ????????? ??????
                            requireActivity().runOnUiThread(() -> drListAdapter.setDrList(RhyaApplication.DrInfoData.getDrListItemArrayList()));
                            // ?????? ?????? ??????
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));

                            // ?????? ??????
                            isLoadTask = false;
                        }
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                }
            }.execute(null);
        }
    }


    /**
     * createString - String ????????? ??????
     * @param array string ??????
     * @return ????????? ?????????
     */
     private String createString(String[] array) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : array) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
     }



    /**
     * downloadFile - ?????? ????????????
     * @param url ???????????? URL
     * @param file ???????????? ??????
     * @throws IOException IOException
     */
    private void downloadFile(URL url, File file) throws IOException {
        URLConnection connection = url.openConnection();
        connection.connect();
        int count;
        // this will be useful so that you can show a tipical 0-100%
        // progress bar

        // download the file
        InputStream input = new BufferedInputStream(url.openStream(),
                8192);

        // Output stream
        OutputStream output = new FileOutputStream(file);

        byte[] data = new byte[1024];

        while ((count = input.read(data)) != -1) {
            // writing data to file
            output.write(data, 0, count);
        }

        // flushing output
        output.flush();

        // closing streams
        output.close();
        input.close();
    }



    /**
     * writeToFile - ?????? ?????? ??????
     *
     * @param file ?????? ??????
     * @param data ?????? ??????
     */
    private void writeToFile(String file, String data) {
        // ?????? ?????? ?????? ?????????
        BufferedWriter buf;
        FileWriter fw;

        // ?????? ??????
        try {
            // ?????? ??????
            fw = new FileWriter(file, false);
            buf = new BufferedWriter(fw);
            buf.append(data);
            buf.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();

            ShowToastMessage(e.getMessage());
        }
    }


    /**
     * writeToFile - ?????? ?????? ??????
     *
     * @param file ?????? ??????
     * @return ?????? ?????????
     */
    private String readToFile(String file) {
        // ?????? ?????? ?????? ?????????
        BufferedReader buf;
        FileReader fr;
        String readLine;

        // ?????? ??????
        try {
            // ?????? ??????
            fr = new FileReader(file);
            buf = new BufferedReader(fr);

            StringBuilder readText = new StringBuilder();
            while ((readLine = buf.readLine()) != null) {
                readText.append(readLine);
                readText.append(System.lineSeparator());
            }

            buf.close();
            fr.close();

            return readText.toString();
        } catch (IOException e) {
            e.printStackTrace();

            ShowToastMessage(e.getMessage());
        }

        return null;
    }


    /**
     * Toast ????????? ??????
     * @param msg ????????? ??????
     *
     */
    private void ShowToastMessage(String msg) {
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dr_list, container, false);
    }
}