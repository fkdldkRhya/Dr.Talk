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
    // 초기 실행 감지
    private boolean isInit;
    // 정렬
    public int sortType;
    // 로딩 Thread 실행 감지
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
    // RHYA.Network SharedPreferences 관리자
    private RhyaSharedPreferences rhyaSharedPreferences;
    // 파일 이름
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
     * onCreate - 생성 후 작업
     *
     * @param savedInstanceState [Override]
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 초기화
        rhyaNetworkAPI = new RhyaNetworkAPI(getContext());
        rhyaAESManager = new RhyaAESManager(getContext());
        isInit = false;
    }


    /**
     * onViewCreated - View 생성 후 작업
     *
     * @param view               [Override]
     * @param savedInstanceState [Override]
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rhyaSharedPreferences = new RhyaSharedPreferences(getContext(), true);

        // 정렬 방식 설정
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

        // 경로 설정
        String rootSavePath = requireActivity().getFilesDir().getAbsolutePath();
        DrListFileName = createString(new String[] { rootSavePath, File.separator, "drinfo.json" });
        ImageSavePath = createString(new String[] { rootSavePath, File.separator, "res_image" });

        // UI 초기화
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        drListRecyclerView = view.findViewById(R.id.drListRecyclerView);
        progressWheel = view.findViewById(R.id.progress_wheel);
        imageView = view.findViewById(R.id.imageView);

        // Adapter
        drListAdapter = new DrListAdapter(requireActivity());

        // Adapter 연결
        drListRecyclerView.setAdapter(drListAdapter);
        drListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 데이터 삽입
        drInfoArrayList = new ArrayList<>();
        reloadThread(false);

        // 아이템 설정
        drListAdapter.setDrList(RhyaApplication.DrInfoData.getDrListItemArrayList());

        // 새로고침 이벤트 설정
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 리로딩
            reloadThread(true);
        });
    }


    /**
     * reloadThread - 의사 리스트 로딩 함수
     */
    private void reloadThread(boolean isRewrite) {
        // 서버 데이터 문자열
        final String DrListMessageJsonKey = "message";
        final String SplitText = "#";
        final String SplitStopString = "No";
        final String DefaultImage = "#DEF";

        // 초기화
        RhyaApplication.DrInfoData.clearDrListItemArrayList();

        // 실행 여부 확인
        if (!isLoadTask) {
            // 실행 여부 변수 변경
            isLoadTask = true;
            // 비동기 작업 수행
            new RhyaAsyncTask<String, String>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    // 로딩 화면 설정
                    requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.VISIBLE));
                    requireActivity().runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                    requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.GONE));

                    // Json key 데이터
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

                    // Json 작성 여부 변수
                    boolean isChangeFile = false;

                    // 파일 정보
                    File rootJsonFile = new File(DrListFileName);

                    // 파일 확인
                    if (rootJsonFile.exists()) {
                        // 파일 읽기
                        String readFile = readToFile(DrListFileName);
                        // 파일 복호화
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

                            // 오류 메시지 출력
                            ShowToastMessage(e.getMessage());
                        }

                        // 비동기 작업 인자 설정
                        arg = readFile;
                    }

                    // 새로고침 확인
                    if (isRewrite) {
                        // 파일 읽지 않음
                        arg = null;
                        // 리스트 초기화
                        drInfoArrayList.clear();
                    }

                    // Null 확인
                    if (arg != null) {
                        // Json 데이터 추출
                        try {
                            // Json 파싱
                            JSONArray jsonArray = new JSONArray(arg);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonGetObject = jsonArray.getJSONObject(i);
                                // Json 데이터 설정
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

                            // 오류 메시지 출력
                            ShowToastMessage(e.getMessage());
                        }
                    }

                    // 데이터 절약 모드 확인 - 1
                    boolean resultSaveData = false;
                    Activity activity_check = getActivity();
                    if (activity_check != null) {
                        if (!activity_check.isFinishing()) {
                            resultSaveData = rhyaSharedPreferences.getStringNoAES(rhyaNetworkAPI.rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_SAVE_DATA_ON_OFF, requireContext()).equals(rhyaSharedPreferences.SHARED_PREFERENCES_SETTING_VALUE_ON);
                        }
                    }
                    // 데이터 절약 모드 확인 - 2
                    if (!resultSaveData || isInit) {
                        // 예외 처리
                        try {
                            // 서버 접속
                            ContentValues urlParm = new ContentValues(); // 파라미터
                            urlParm.put(rhyaNetworkAPI.DR_TALK_AUTH_TOKEN_PARM, rhyaNetworkAPI.getAutoLogin());
                            RhyaRequestHttpURLConnection requestHttpURLConnection = new RhyaRequestHttpURLConnection();
                            String result = requestHttpURLConnection.request(rhyaNetworkAPI.DR_TALK_GET_DR_LIST, urlParm); // 해당 URL로 부터 결과물을 얻어온다.
                            // 서버 데이터 확인
                            JSONObject jsonObject = new JSONObject(result);

                            // 결과 확인
                            try {
                                // 성공 확인
                                if (!jsonObject.getString(json_key_result).equals(json_result_success)) {
                                    // Activity 종료 확인
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        if (!activity.isFinishing()) {
                                            // 메시지 출력
                                            requireActivity().runOnUiThread(() -> ((MainActivity) getActivity()).onErrorAccountShowMsg());
                                        }
                                    }
                                }
                            }catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            String[] split = jsonObject.getString(DrListMessageJsonKey).split(SplitText);
                            for (String value : split) {
                                // 중지 문자열 확인
                                if (value.equals(SplitStopString)) {
                                    continue;
                                }

                                // 의사 아이디 확인
                                boolean isToF = false;

                                // 파일 확인
                                if (arg != null) {
                                    // 작업 진행 여부 확인
                                    for (DrInfo drInfo : drInfoArrayList) {
                                        if (drInfo.dr_id.equals(value)) {
                                            isToF = true;

                                            break;
                                        }
                                    }
                                }

                                // 작업 진행 여부
                                if (!isToF) {
                                    // 예외 처리
                                    try {
                                        // Json 파싱
                                        urlParm.put(rhyaNetworkAPI.DR_TALK_INPUT_DR_ID, value);
                                        String doctor_info = requestHttpURLConnection.request(rhyaNetworkAPI.DR_TALK_GET_DR_INFO, urlParm);
                                        doctor_info = URLDecoder.decode(doctor_info, "UTF-8");
                                        jsonObject = new JSONObject(doctor_info);

                                        // 결과 확인
                                        try {
                                            // 성공 확인
                                            if (!jsonObject.getString(json_key_result).equals(json_result_success)) {
                                                // Activity 종료 확인
                                                Activity activity = getActivity();
                                                if (activity != null) {
                                                    if (!activity.isFinishing()) {
                                                        // 메시지 출력
                                                        requireActivity().runOnUiThread(() -> ((MainActivity) getActivity()).onErrorAccountShowMsg());
                                                    }
                                                }
                                            }
                                        }catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                        // Json 데이터
                                        String image = jsonObject.getString(json_key_image);
                                        // 데이터 설정
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

                                        // 이미지 다운로드 폴더 확인
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
                                        // 이미지 파일명 중복 확인
                                        if (downloadImagePath_image.exists()) {
                                            if (!downloadImagePath_image.delete()) {
                                                continue;
                                            }
                                        }
                                        // 이미지 다운로드 여부 검사
                                        if (!image.equals(DefaultImage)) {
                                            // 이미지 다운로드
                                            downloadFile(
                                                    new URL(
                                                            createString(
                                                                    new String[] { rhyaNetworkAPI.FILE_DOWNLOAD_APP_URL, image }
                                                            )
                                                    ), downloadImagePath_image);
                                        }

                                        // 없는 의사 정보 설정
                                        if (!isChangeFile) { isChangeFile = true; }
                                        drInfoArrayList.add(drInfo);
                                    }catch (Exception e) {
                                        e.printStackTrace();

                                        // 오류 메시지 출력
                                        ShowToastMessage(e.getMessage());

                                        break;
                                    }
                                }
                            }

                            // 파일 생성 여부
                            if (isChangeFile) {
                                // 없는 의사 정보 파일 생성
                                JSONArray drInfoJsonRootArray = new JSONArray();
                                for (DrInfo drInfo : drInfoArrayList) {
                                    // 데이터 삽입
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
                                // 데이터 암호화
                                String writeData = drInfoJsonRootArray.toString();
                                writeData = rhyaAESManager.aesEncode(writeData);
                                // 파일 생성
                                writeToFile(DrListFileName, writeData);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            // 오류 메시지 출력
                            ShowToastMessage(e.getMessage());
                        }
                    }

                    // Activity 종료 확인
                    Activity activity = getActivity();
                    if (activity != null) {
                        if (!activity.isFinishing()) {
                            // RecyclerView 데이터 생성
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
                            // 데이터 초기화
                            drInfoArrayList.clear();
                            // 데이터 정렬
                            drListSort();
                            // RecyclerView 데이터 설정
                            requireActivity().runOnUiThread(() -> drListAdapter.setDrList(RhyaApplication.DrInfoData.getDrListItemArrayList()));
                            // 로딩 화면 해제
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                        }
                    }

                    // 종료 확인
                    isLoadTask = false;
                    // 초기화 확인
                    if (!isInit) { isInit = true; }


                    return null;
                }

                @Override
                protected void onPostExecute(String result) { }
            }.execute(null);
        }
    }

    /**
     * drListSort - 의사 리스트 정렬
     */
    private void drListSort() {
        // 정렬 방식 저장
        rhyaSharedPreferences.setStringNoAES(rhyaSharedPreferences.SHARED_PREFERENCES_DR_LIST_SORT_TYPE, Integer.toString(sortType), requireContext());

        // 정렬
        if (this.sortType == 1) {
            Collections.sort(RhyaApplication.DrInfoData.getDrListItemArrayList(), new RhyaComparator.DescendingName());
        }else if (this.sortType == 0) {
            Collections.sort(RhyaApplication.DrInfoData.getDrListItemArrayList(), new RhyaComparator.AscendingName());
        }else {
            Collections.sort(RhyaApplication.DrInfoData.getDrListItemArrayList(), new RhyaComparator.DescendingName());
        }
    }


    /**
     * drListSortThread - 의사 리스트 정렬 Thread
     */
    public void drListSortThread() {
        // 실행 여부 확인
        if (!isLoadTask) {
            // 실행 여부 변수 변경
            isLoadTask = true;
            // 비동기 작업 수행
            new RhyaAsyncTask<String, String>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    // Activity 종료 확인
                    Activity activity = getActivity();
                    if (activity != null) {
                        if (!activity.isFinishing()) {
                            // 로딩 화면 설정
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.GONE));

                            // 데이터 초기화
                            drInfoArrayList.clear();
                            // 데이터 정렬
                            drListSort();
                            // RecyclerView 데이터 설정
                            requireActivity().runOnUiThread(() -> drListAdapter.setDrList(RhyaApplication.DrInfoData.getDrListItemArrayList()));
                            // 로딩 화면 해제
                            requireActivity().runOnUiThread(() -> progressWheel.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> imageView.setVisibility(View.GONE));
                            requireActivity().runOnUiThread(() -> drListRecyclerView.setVisibility(View.VISIBLE));
                            requireActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));

                            // 종료 확인
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
     * createString - String 문자열 생성
     * @param array string 배열
     * @return 생성된 문자열
     */
     private String createString(String[] array) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String string : array) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
     }



    /**
     * downloadFile - 파일 다운로드
     * @param url 다운로드 URL
     * @param file 다운로드 경로
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
     * writeToFile - 파일 쓰기 함수
     *
     * @param file 파일 이름
     * @param data 파일 내용
     */
    private void writeToFile(String file, String data) {
        // 파일 쓰기 변수 초기화
        BufferedWriter buf;
        FileWriter fw;

        // 예외 처리
        try {
            // 파일 쓰기
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
     * writeToFile - 파일 읽기 함수
     *
     * @param file 파일 이름
     * @return 파일 데이터
     */
    private String readToFile(String file) {
        // 파일 읽기 변수 초기화
        BufferedReader buf;
        FileReader fr;
        String readLine;

        // 예외 처리
        try {
            // 파일 읽기
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
     * Toast 메시지 출력
     * @param msg 메시지 내용
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