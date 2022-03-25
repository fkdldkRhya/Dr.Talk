package kro.kr.rhya_network.dr_talk.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kro.kr.rhya_network.dr_talk.security.CellphoneRoutingCheck;
import kro.kr.rhya_network.dr_talk.util.AppLockChecker;
import kro.kr.rhya_network.dr_talk.util.DialogManager;
import kro.kr.rhya_network.dr_talk.util.DozeModeChecker;
import kro.kr.rhya_network.dr_talk.util.PermissionChecker;
import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.util.RhyaSharedPreferences;

/*
 * activity_start_logo.xml 파일의 컨트롤러
 *
 * 앱 로고 표시 및 권한 확인 후 화면 전환
 * - 권한이 있을 경우 : 로딩 activity 전환
 * - 권한이 없을 경우 : 권한 요구 activity 전환
 */
public class StartLogoActivity extends AppCompatActivity {
    // Activity
    private Activity activity;
    // Context
    private Context context;
    // UI 요소
    private ImageView appLogoImage;
    private TextView appTitleText;
    // 권한 확인 관리자
    private PermissionChecker permissionChecker;
    // Doze 모드 확인 관리자
    private DozeModeChecker dozeModeChecker;


    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_logo);

        context = this;
        activity = this;

        appLogoImage = findViewById(R.id.appLogoImageView);
        appTitleText = findViewById(R.id.appTitleTextView);

        permissionChecker = new PermissionChecker(this);

        dozeModeChecker = new DozeModeChecker();
    }


    /**
     * onStart - 리스너 등록 및 실행
     */
    @Override
    protected void onStart() {
        super.onStart();

        // 루팅 감지
        CellphoneRoutingCheck cellphoneRoutingCheck = new CellphoneRoutingCheck();
        /*
        if (cellphoneRoutingCheck.checkSuperUser()) { // 루팅됨 오류 발생
            DialogManager dialogManager = new DialogManager();
            // Dialog 생성
            dialogManager.createDialog_v1(this,
                    getWindowManager().getDefaultDisplay(),
                    "디바이스가 허가되지 않은 방법으로 수정되어 Dr.Talk을 사용할 수 없습니다.",
                    "종료",
                    false,
                     dialog -> {
                         moveTaskToBack(true);
                         finish();
                         android.os.Process.killProcess(android.os.Process.myPid());
                     });

            return;
        }
        */
        // 애니메이션 설정
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_fast_speed_fade_in);
        // 애니메이션 리스너 설정
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // UI 요소 속성 변경 - Visibility
                appLogoImage.setVisibility(View.VISIBLE);
                appTitleText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // App 권한 확인
                if (permissionChecker.CheckPermission_CAMERA() &&
                    permissionChecker.CheckPermission_WRITE_EXTERNAL_STORAGE() &&
                    dozeModeChecker.CheckDozeMode(getApplicationContext())) {

                    // 잠금 확인
                    AppLockChecker appLockChecker = new AppLockChecker();
                    appLockChecker.appLockCheckResultForTask(activity, context, appLockChecker.checkAppLock(context));
                }else {
                    // 화면 전환 --> activity_request_permission.xml
                    Intent intent = new Intent(context, RequestActivity.class);
                    startActivity(intent);
                    // 종료
                    finish();

                    overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_stay);
                }
           }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No task
            }
        });
        // 애니메이션 재생
        appLogoImage.startAnimation(animation);
        appTitleText.startAnimation(animation);
    }


    /**
     * 뒤로가기 버튼 활성화 X
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
}
