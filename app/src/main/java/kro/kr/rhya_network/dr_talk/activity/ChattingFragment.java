package kro.kr.rhya_network.dr_talk.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.adapter.ChattingMainAdapter;
import kro.kr.rhya_network.dr_talk.adapter.DrListAdapter;
import kro.kr.rhya_network.dr_talk.application.RhyaApplication;
import kro.kr.rhya_network.dr_talk.data.DrInfo;
import kro.kr.rhya_network.dr_talk.data.DrListItem;
import kro.kr.rhya_network.dr_talk.util.RhyaAsyncTask;
import kro.kr.rhya_network.dr_talk.util.RhyaNetworkAPI;
import kro.kr.rhya_network.dr_talk.util.RhyaRequestHttpURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChattingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChattingFragment extends Fragment {
    public static Context context;
    public static Activity activity;
    // ?????? Thread ?????? ??????
    public boolean isLoadTask = false;
    // UI Object
    private RecyclerView recyclerView;
    private ProgressWheel progressWheel;
    private ImageView imageView;
    // Adapter
    private ChattingMainAdapter chattingAdapter;
    private RhyaNetworkAPI rhyaNetworkAPI;

    public ChattingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChattingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChattingFragment newInstance() {
        return new ChattingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatting, container, false);
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
        context = requireContext();
        activity = requireActivity();
        recyclerView = view.findViewById(R.id.chattingRecyclerView);
        progressWheel = view.findViewById(R.id.progress_wheel);
        imageView = view.findViewById(R.id.imageView);

        // Adapter
        chattingAdapter = new ChattingMainAdapter(requireActivity());
        rhyaNetworkAPI = new RhyaNetworkAPI(requireContext());
        // Adapter ??????
        chattingAdapter.setDrList(new ArrayList<>());
        recyclerView.setAdapter(chattingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reloadThread();
    }


    /**
     * reloadThread - ?????? ????????? ?????? ??????
     */
    public void reloadThread() {
        final String rootSavePath = requireActivity().getFilesDir().getAbsolutePath();
        final String chatListFileName = createString(new String[] { rootSavePath, File.separator, "chatList.json" });

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
                    requireActivity().runOnUiThread(() -> recyclerView.setVisibility(View.GONE));



                    // ?????? ??????
                    File rootJsonFile = new File(chatListFileName);
                    String readFile = null;
                    // ?????? ??????
                    if (rootJsonFile.exists()) {
                        // ?????? ??????
                        readFile = readToFile(chatListFileName);
                    }



                    ArrayList<DrListItem> addChat = new ArrayList<>();

                    // Json key ?????????
                    final String json_key_result = "result";
                    final String json_result_success = "S";
                    final String json_key_id = "id";
                    final String json_key_name = "name";
                    final String json_key_type = "type";
                    final String json_key_phone = "phone";
                    final String json_key_image = "image";
                    final String json_key_location = "location";
                    final String json_key_location_name = "location_name";
                    final String json_key_location_phone = "location_phone";
                    final String json_key_description = "description";
                    final String json_key_license = "license";
                    final String json_key_others = "others";
                    final String json_key_foc = "foc";
                    final String json_key_is_doctor = "isdoctor";
                    final String json_key_email = "email";
                    final String json_key_birthday = "birthday";
                    final String json_key_reg_date = "rdate";

                    // ?????? ??????
                    try {
                        // ?????? ??????
                        ContentValues urlParm = new ContentValues(); // ????????????
                        urlParm.put(rhyaNetworkAPI.DR_TALK_AUTH_TOKEN_PARM, rhyaNetworkAPI.getAutoLogin());
                        RhyaRequestHttpURLConnection requestHttpURLConnection = new RhyaRequestHttpURLConnection();
                        String result = requestHttpURLConnection.request(rhyaNetworkAPI.DR_TALK_GET_USER_INFO, urlParm); // ?????? URL ??? ?????? ???????????? ????????????.
                        result = URLDecoder.decode(result, "UTF-8");
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
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Json ????????? ??????
                        try {
                            // ?????? ??????
                            RhyaApplication.MyInfo.MyInfoVO myInfoVO;
                            if (jsonObject.getString(json_key_is_doctor).equals("T")) {
                                myInfoVO = new RhyaApplication.MyInfo.MyInfoVO(true,
                                        jsonObject.getString(json_key_id),
                                        jsonObject.getString(json_key_email),
                                        jsonObject.getString(json_key_birthday),
                                        jsonObject.getString(json_key_reg_date),
                                        jsonObject.getString(json_key_name),
                                        jsonObject.getString(json_key_description),
                                        jsonObject.getString(json_key_type),
                                        jsonObject.getString(json_key_phone),
                                        jsonObject.getString(json_key_image),
                                        jsonObject.getString(json_key_location),
                                        jsonObject.getString(json_key_location_name),
                                        jsonObject.getString(json_key_location_phone),
                                        jsonObject.getString(json_key_license),
                                        jsonObject.getString(json_key_others),
                                        jsonObject.getString(json_key_foc));
                            } else {
                                myInfoVO = new RhyaApplication.MyInfo.MyInfoVO(false,
                                        jsonObject.getString(json_key_id),
                                        jsonObject.getString(json_key_email),
                                        jsonObject.getString(json_key_birthday),
                                        jsonObject.getString(json_key_reg_date),
                                        jsonObject.getString(json_key_name),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null);
                            }

                            // ????????? ??????
                            RhyaApplication.MyInfo.myInfoVO = myInfoVO;
                        } catch (JSONException e) {
                            e.printStackTrace();

                            // ?????? ????????? ??????
                            ShowToastMessage(e.getMessage());
                        }
                    }catch (Exception ex) {}

                    if (RhyaApplication.MyInfo.myInfoVO != null) {
                        if (!RhyaApplication.MyInfo.myInfoVO.isDoctor()) {
                            if (readFile != null) {
                                try {
                                    JSONArray jsonArray = new JSONArray(readFile);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonGetObject = jsonArray.getJSONObject(i);
                                        String drID = jsonGetObject.getString("id");

                                        for (DrListItem drListItem : RhyaApplication.DrInfoData.getDrListItemArrayList()) {
                                            if (drListItem.getDr_id().equals(drID)) {
                                                addChat.add(drListItem);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else {

                            // RecyclerView ????????? ??????
                            requireActivity().runOnUiThread(() -> chattingAdapter.setDrList(addChat));

                            // ?????? ?????? ??????
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> recyclerView.setVisibility(View.VISIBLE));
                            // ?????? ??????
                            while (true) {
                                try {
                                    Thread.sleep(2000);
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        if (activity.isFinishing()) {
                                            break;
                                        }
                                    }else {
                                        break;
                                    }

                                    RhyaRequestHttpURLConnection requestHttpURLConnection = new RhyaRequestHttpURLConnection();
                                    String result = requestHttpURLConnection.request("https://rhya-network.kro.kr/MainServer/webpage/jsp/contest/2021_Link_Revolution_Idea_Tone_Contest/check_msg.jsp", null);
                                    if (result.contains("Y")) {
                                        DrListItem item = RhyaApplication.DrInfoData.getDrListItemArrayList().get(1);
                                        item.setDr_image("");
                                        item.setDr_name("?????????");
                                        RhyaApplication.checker = 1;

                                        addChat.add(item);
                                        break;
                                    }
                                }catch (Exception ex) {
                                }
                            }
                        }
                    }else {
                        if (readFile != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(readFile);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonGetObject = jsonArray.getJSONObject(i);
                                    String drID = jsonGetObject.getString("id");

                                    for (DrListItem drListItem : RhyaApplication.DrInfoData.getDrListItemArrayList()) {
                                        if (drListItem.getDr_id().equals(drID)) {
                                            addChat.add(drListItem);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                    // RecyclerView ????????? ??????
                    requireActivity().runOnUiThread(() -> chattingAdapter.setDrList(addChat));

                    // ?????? ?????? ??????
                    requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                    requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                    requireActivity().runOnUiThread(() -> recyclerView.setVisibility(View.VISIBLE));

                    // ?????? ??????
                    isLoadTask = false;

                    return null;
                }

                @Override
                protected void onPostExecute(String result) { }
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
}