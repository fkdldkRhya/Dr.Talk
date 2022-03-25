package kro.kr.rhya_network.dr_talk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.util.RhyaSharedPreferences;

/*
 * activity_pin_lock.xml 파일의 컨트롤러
 *
 * App 실행 잠금 - PIN
 */
public class PINLockActivity extends AppCompatActivity {
    // Context
    private Context context;
    // 뒤로가기 버튼 시간
    private long backBtnTime = 0;
    // UI Object
    private TextView pinTitle;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    // RHYA SharedPreferences
    private RhyaSharedPreferences rhyaSharedPreferences;
    // 인자
    private int arg;
    // PIN 재입력 감지
    private boolean isReInput;
    // 입력한 PIN
    private String inputPIN;



    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock);

        context = this;

        rhyaSharedPreferences = new RhyaSharedPreferences(context, false);

        arg = getIntent().getIntExtra("type", 0);

        isReInput = false;

        pinTitle = findViewById(R.id.pinTitleTextView);

        // 인자 확인
        if (arg == 1) {
            // PIN 설정
            pinTitle.setText(R.string.activity_app_lock_setting__set_pin_title);
        }

        // UI 설정
        Animation animationShake = AnimationUtils.loadAnimation(context, R.anim.anim_shake_v1);
        mPinLockView = findViewById(R.id.pin_lock_view);
        mIndicatorDots = findViewById(R.id.indicator_dots);
        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                // 예외 처리
                try {
                    // 인자 비교
                    if (arg == 0) { // PIN 입력
                        // 데이터
                        String appLockData = rhyaSharedPreferences.getString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_DATA, context);
                        // 비밀번호 비교
                        if (appLockData.equals(pin)) { // 일치
                            // 화면 전환
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            // 종료
                            finish();

                            overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_stay);
                        } else { // 불일치
                            pinTitle.setText(R.string.activity_app_lock_setting__set_pin_inc_re_title);

                            // 초기화 및 애니메이션 재생
                            mIndicatorDots.startAnimation(animationShake);
                            mPinLockView.resetPinLockView();
                        }
                    }else if (arg == 1){ // PIN 설정
                        // 재입력 확인
                        if (isReInput) {
                            // PIN 확인
                            if (pin.equals(inputPIN)) { // PIN 같은
                                // PIN 설정
                                rhyaSharedPreferences.setString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE, rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PIN_LOCK, context);
                                rhyaSharedPreferences.setString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_DATA, inputPIN, context);

                                // 종료
                                finish();
                                overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
                            }else { // PIN 다름
                                pinTitle.setText(R.string.activity_app_lock_setting__set_pin_inc_re_title);

                                // 초기화 및 애니메이션 재생
                                mIndicatorDots.startAnimation(animationShake);
                                mPinLockView.resetPinLockView();
                            }
                        }else { // 처음 입력
                            isReInput = true;

                            pinTitle.setText(R.string.activity_app_lock_setting__set_pin_re_title);

                            inputPIN = pin;

                            // 초기화
                            mPinLockView.resetPinLockView();
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();

                    // 오류 메시지 출력
                    showToastMessage(ex.getMessage());
                }
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        });
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }


    /**
     * 종료 확인 메시지 출력
     */
    @Override
    public void onBackPressed() {
        // 인자 확인
        if (arg == 1) {
            // 종료
            super.onBackPressed();

            overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
        }else {
            long curTime = System.currentTimeMillis();
            long gapTime = curTime - backBtnTime;

            if(0 <= gapTime && 2000 >= gapTime) {
                // 종료
                super.onBackPressed();
            } else {
                backBtnTime = curTime;
                Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Toast 메시지 출력
     * @param msg 메시지 내용
     *
     */
    private void showToastMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
