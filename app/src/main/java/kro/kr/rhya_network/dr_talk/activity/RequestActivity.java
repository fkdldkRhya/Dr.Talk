package kro.kr.rhya_network.dr_talk.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.regex.Pattern;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.util.AppLockChecker;
import kro.kr.rhya_network.dr_talk.util.DialogManager;
import kro.kr.rhya_network.dr_talk.util.DozeModeChecker;
import kro.kr.rhya_network.dr_talk.util.PermissionChecker;
import kro.kr.rhya_network.dr_talk.util.RhyaNetworkAPI;

/*
 * activity_request.xml 파일의 컨트롤러
 *
 * 앱 권한 확인 및 권한 요구 와 이용약관 동의
 */
public class RequestActivity extends AppCompatActivity {
    // UI 요소
    private TextView touTextView;
    private CheckBox touCheckbox;
    private Button permissionRequestButton;
    // 권한 확인 관리자
    private PermissionChecker permissionChecker;
    // Doze 모드 확인 관리자
    private DozeModeChecker dozeModeChecker;
    // RHYA.Network API
    private RhyaNetworkAPI rhyaAPI;
    // 권한 요청 CODE
    private final int PERMISSION_REQUEST_CODE_STORAGE = 1000;
    private final int PERMISSION_REQUEST_CODE_CAMERA  = 1001;
    // Dialog 관리자
    private DialogManager dialogManager;


    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        touTextView = findViewById(R.id.touTextView);
        touCheckbox = findViewById(R.id.touCheckbox);
        permissionRequestButton = findViewById(R.id.permissionRequestButton);

        permissionChecker = new PermissionChecker(this);

        dozeModeChecker = new DozeModeChecker();

        rhyaAPI = new RhyaNetworkAPI(this);

        dialogManager = new DialogManager();

        // 텍스트 설정
        touTextView.setText("개인정보 수집 및 이용 동의서에 동의합니다.");
    }


    /**
     * onStart - 리스너 등록 및 UI 작업
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();

        // 개인정보 수집 및 이용 동의서 하이퍼링크 설정
        Pattern patternTOU = Pattern.compile("개인정보 수집 및 이용 동의서");

        Linkify.TransformFilter transformFilter = (match, url) -> rhyaAPI.TERMS_OF_USE_URL;
        Linkify.addLinks(touTextView, patternTOU, "",null, transformFilter);

        // 개인정보 수집 및 이용 동의서 동의 체크박스 리스너 설정
        touCheckbox.setOnClickListener(v -> {
            // 버튼 상태 변경
            permissionRequestButton.setEnabled(touCheckbox.isChecked());
        });

        // 권한 승인 버튼 리스너
        permissionRequestButton.setOnClickListener(v -> requestPermission());
    }


    /**
     * onRequestPermissionsResult - 권한 요청 결과 처리 함수
     * @param requestCode [Override]
     * @param permissions [Override]
     * @param grantResults [Override]
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            // 저장소 권한 요청 결과
            case PERMISSION_REQUEST_CODE_STORAGE:
                // 저장소 권한 확인
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                }else {
                    Toast.makeText(this.getApplicationContext(), "앱을 원활하게 사용하기 위해서는 저장소 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                break;

            case PERMISSION_REQUEST_CODE_CAMERA:
                // 저장소 권한 확인
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                }else {
                    Toast.makeText(this.getApplicationContext(), "앱을 원활하게 사용하기 위해서는 카메라 사용 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 종료 메시지 출력
     */
    @Override
    public void onBackPressed() {
        // Dialog 생성
        dialogManager.createDialog_v3(this,
                getWindowManager().getDefaultDisplay(),
                "정말로 종료하시겠습니까?",
                "종료",
                "취소",
                false,
                new DialogManager.DialogListener_v3() {
                    @Override
                    public void onClickListenerButtonYes(Dialog dialog) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                    @Override
                    public void onClickListenerButtonNo(Dialog dialog) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    /**
     * 권한 요청 함수
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("BatteryLife")
    private void requestPermission() {
        // Doze 모드 확인
        if (!dozeModeChecker.CheckDozeMode(getApplicationContext())) {
            // 화이트 리스트 등록 안 됨
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent);

            return;
        }

        // 저장소 권한 확인
        if (!permissionChecker.CheckPermission_WRITE_EXTERNAL_STORAGE()) {
            // 저장소 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE_STORAGE);

            return;
        }

        // 카메라 권한 확인
        if (!permissionChecker.CheckPermission_CAMERA()) {
            // 카메라 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE_CAMERA);

            return;
        }

        // 화면 전환
        NextActivity();
    }


    /**
     * 화면 전환 함수
     */
    private void NextActivity() {
        // 잠금 확인
        AppLockChecker appLockChecker = new AppLockChecker();
        appLockChecker.appLockCheckResultForTask(this, this, appLockChecker.checkAppLock(this));
    }
}
