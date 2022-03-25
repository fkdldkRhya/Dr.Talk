package kro.kr.rhya_network.dr_talk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.data.DrListItem;

/*
 * activity_show_dr_info.xml 파일의 컨트롤러
 *
 * 의사 정보 출력
 */
public class ShowDrInfoActivity extends AppCompatActivity {
    // UI Object
    private ImageButton backButton;
    private ImageView drImageView;
    private kro.kr.rhya_network.dr_talk.util.AutoResizeTextView drNameTextView;
    private kro.kr.rhya_network.dr_talk.util.AutoResizeTextView drDescriptionTextView;
    private kro.kr.rhya_network.dr_talk.util.AutoResizeTextView drPhoneTextView;
    private kro.kr.rhya_network.dr_talk.util.AutoResizeTextView drLocationTextView;
    private kro.kr.rhya_network.dr_talk.util.AutoResizeTextView drTypeTextView;
    private TextView drLicenseTextView;
    private TextView drOthersTextView;
    private TextView drFocTextView;
    private Button chatButton;
    //private Button reviewButton;
    // 데이터
    private DrListItem item;


    /**
     * onCreate - 초기화
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dr_info);

        // UI 초기화
        backButton = findViewById(R.id.backButton);
        drImageView = findViewById(R.id.drImageView);
        drNameTextView = findViewById(R.id.drNameTextView);
        drDescriptionTextView = findViewById(R.id.descriptionTextView);
        drPhoneTextView = findViewById(R.id.phoneTextView);
        drLocationTextView = findViewById(R.id.locationTextView);
        drTypeTextView = findViewById(R.id.typeTextView);
        drLicenseTextView = findViewById(R.id.drLicenseTextView);
        drOthersTextView = findViewById(R.id.drOthersTextView);
        drFocTextView = findViewById(R.id.drFocTextView);
        chatButton = findViewById(R.id.chattingButton);
        //reviewButton = findViewById(R.id.reviewButton);

        // 인자 받기
        item = getIntent().getParcelableExtra("dr_info");

        // 공백 대체 문자열
        final String nullStringForPhone = "No";
        final String nullStringForString = "#NO";
        final String nullStringForResult = "등록되지 않음";

        // 리스너 등록
        backButton.setOnClickListener(v -> {
            BackActivity();
        });
        /*
        reviewButton.setOnClickListener(v -> {
            // 종료
            BackActivity();
        });
        */
        chatButton.setOnClickListener(b -> {
            final String rootSavePath = getFilesDir().getAbsolutePath();
            final String chatListFileName = createString(new String[] { rootSavePath, File.separator, "chatList.json" });
            try {
                writeToFile(chatListFileName, new JSONArray().put(new JSONObject().put("id", item.getDr_id())).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((ChattingFragment) MainActivity.fragment).reloadThread();

            Intent intent = new Intent(this, ChattingActivity.class);
            // Intent 데이터 설정
            intent.putExtra("dr_info", item);
            // 화면 넘기기
            startActivity(intent);
            overridePendingTransition(R.anim.anim_next_activity_up_enter,R.anim.anim_next_activity_stay);
        });
        // UI 데이터 설정
        Glide.with(this)
                .load(item.getDr_image())
                .apply(new RequestOptions().circleCrop().centerCrop()).circleCrop()
                .placeholder(R.drawable.ic_applogo_doctor)
                .error(R.drawable.ic_applogo_doctor)
                .fallback(R.drawable.ic_applogo_doctor)
                .skipMemoryCache(false)
                .into(drImageView);
        drNameTextView.setText(item.getDr_name());

        // Null 확인
        // =========================================================================== //
        if (!item.getDr_description().equals(nullStringForString)) {
            drDescriptionTextView.setText(item.getDr_description());
        }
        if (!item.getDr_location_phone().equals(nullStringForPhone)) {
            drPhoneTextView.setText(item.getDr_location_phone());
        }else {
            drPhoneTextView.setText(nullStringForResult);
        }

        if (!item.getDr_license().equals(nullStringForString)) {
            drLicenseTextView.setText(item.getDr_license());
        }else {
            drLicenseTextView.setText(nullStringForResult);
        }

        if (!item.getDr_others().equals(nullStringForString)) {
            drOthersTextView.setText(item.getDr_others());
        }else {
            drOthersTextView.setText(nullStringForResult);
        }

        if (!item.getDr_foc().equals(nullStringForString)) {
            drFocTextView.setText(item.getDr_foc());
        }else {
            drFocTextView.setText(nullStringForResult);
        }
        // =========================================================================== //

        drLocationTextView.setText(createString(new String[] { item.getDr_location(), " - ", item.getDr_location_name()}));
        drTypeTextView.setText(item.getDr_type());
    }

    
    /**
     *  onBackPressed - 뒤로가기 버튼 이벤트
     */
    @Override
    public void onBackPressed() {
        // 종료
        BackActivity();
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
        }
    }


    /**
     *  BackActivity - 이전 액티비티로 돌아감
     */
    private void BackActivity() {
        finish();
        overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
    }
}
