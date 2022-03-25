package kro.kr.rhya_network.dr_talk.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.application.RhyaApplication;

public class EditMyInfoActivity extends AppCompatActivity {
    // UI Object
    private ImageButton backBtn;
    private ImageView userImageView;
    private Button userImageChgBtn;
    private EditText userIDEditText;
    private EditText userNameEditText;
    private EditText userEmailEditText;
    private EditText userBirthdayEditText;
    private EditText drPhoneEditText;
    private EditText drLocationEditText;
    private EditText drTypeEditText;
    private EditText drDescriptionEditText;
    private EditText drLicensesEditText;
    private EditText drFocEditText;
    private EditText drOtherEditText;
    // 데이터 변경 감지
    private boolean isChangeData;


    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);

        isChangeData = false;

        // UI 초기화
        backBtn = findViewById(R.id.backButton);
        userImageView = findViewById(R.id.userImageView);
        userIDEditText = findViewById(R.id.userIDEditText);
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userBirthdayEditText = findViewById(R.id.userBirthdayEditText);
        userImageChgBtn = findViewById(R.id.editImageButton);
        drLicensesEditText = findViewById(R.id.drLicensesEditText);
        drPhoneEditText = findViewById(R.id.drPhoneEditText);
        drLocationEditText = findViewById(R.id.drLocationEditText);
        drTypeEditText = findViewById(R.id.drTypeEditText);
        drDescriptionEditText = findViewById(R.id.drDescriptionEditText);
        drFocEditText = findViewById(R.id.drFocEditText);
        drOtherEditText = findViewById(R.id.drOtherEditText);
        // 사용자 데이터 확인
        userIDEditText.setText(RhyaApplication.MyInfo.myInfoVO.getId());
        userNameEditText.setText(RhyaApplication.MyInfo.myInfoVO.getName());
        userEmailEditText.setText(RhyaApplication.MyInfo.myInfoVO.getEmail());
        userBirthdayEditText.setText(RhyaApplication.MyInfo.myInfoVO.getBirthday());
        // 이미지 설정
        if (RhyaApplication.MyInfo.myInfoVO.isDoctor()) { // 의사 O
            // 경로 설정
            String rootSavePath = getFilesDir().getAbsolutePath();
            String imageSavePath = createString(new String[] { rootSavePath, File.separator, "res_image" });

            Glide.with(this)
                    .load(createString(new String[] { imageSavePath, File.separator, RhyaApplication.MyInfo.myInfoVO.getDr_image() }))
                    .apply(new RequestOptions().circleCrop().centerCrop()).circleCrop()
                    .placeholder(R.drawable.ic_applogo_doctor)
                    .error(R.drawable.ic_applogo_doctor)
                    .fallback(R.drawable.ic_applogo_doctor)
                    .skipMemoryCache(false)
                    .into(userImageView); // 의사 이미지 설정
            // 공백 대체 문자열
            final String nullStringForPhone = "No";
            final String nullStringForString = "#NO";

            // 의사 데이터 설정
            drPhoneEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_location_phone());
            drLocationEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_location());
            drTypeEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_type());
            if (!RhyaApplication.MyInfo.myInfoVO.getDr_description().equals(nullStringForString)) {
                drDescriptionEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_description());
            }
            if (!RhyaApplication.MyInfo.myInfoVO.getDr_location_phone().equals(nullStringForPhone)) {
                drPhoneEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_location_phone());
            }
            if (!RhyaApplication.MyInfo.myInfoVO.getDr_license().equals(nullStringForString)) {
                drLicensesEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_license());
            }
            if (!RhyaApplication.MyInfo.myInfoVO.getDr_others().equals(nullStringForString)) {
                drOtherEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_others());
            }
            if (!RhyaApplication.MyInfo.myInfoVO.getDr_foc().equals(nullStringForString)) {
                drFocEditText.setText(RhyaApplication.MyInfo.myInfoVO.getDr_foc());
            }
            // 학력/자격먼허 Edit text 리스너
            drLicensesEditText.setOnTouchListener((v, event) -> {
                if (drLicensesEditText.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            });
            // 주요 진료 분야 Edit text 리스너
            drFocEditText.setOnTouchListener((v, event) -> {
                if (drFocEditText.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            });
            // 기타 Edit text 리스너
            drOtherEditText.setOnTouchListener((v, event) -> {
                if (drOtherEditText.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    }
                }
                return false;
            });
        }else { // 의사 X
            // 의사 데이터 비활성화
            // 버튼 비활성화
            userImageChgBtn.setEnabled(false);
            userImageChgBtn.setVisibility(View.GONE);
            // 환자 이미지 설정
            userImageView.setImageResource(R.drawable.ic_patient);
        }

        // 종료 버튼 리스너 등록
        backBtn.setOnClickListener(v -> {
            // 종료
            BackActivity();
        });
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
     *  BackActivity - 이전 액티비티로 돌아감
     */
    private void BackActivity() {
        finish();
        overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
    }
}
