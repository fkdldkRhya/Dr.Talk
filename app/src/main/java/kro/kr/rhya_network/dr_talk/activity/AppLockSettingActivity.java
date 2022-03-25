package kro.kr.rhya_network.dr_talk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.util.RhyaSharedPreferences;

public class AppLockSettingActivity extends AppCompatActivity {
    // Activity
    private Activity activity;
    // Context
    private Context context;
    // UI Object
    private ImageButton backButton;
    private CheckBox setLockButtonPin;
    private ImageButton settingLockAppPin;
    // RHYA SharedPreferences
    private RhyaSharedPreferences rhyaSharedPreferences;


    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_setting);

        context = this;
        activity = this;

        rhyaSharedPreferences = new RhyaSharedPreferences(context, false);

        // UI 초기화
        backButton = findViewById(R.id.backButton);
        setLockButtonPin = findViewById(R.id.settingButton_set_pin);
        settingLockAppPin = findViewById(R.id.setting_lock_app);
    }


    /**
     * onStart - 리스너 등록 및 데이터 설정
     */
    @Override
    protected void onStart() {
        super.onStart();

        // 데이터 확인
        try {
            // PIN 설정 확인
            setLockButtonPin.setChecked(rhyaSharedPreferences.getString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE, context).equals(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PIN_LOCK));
        }catch (Exception ex) {
            ex.printStackTrace();

            // 오류 메시지 출력
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }

        // 리스너 등록
        backButton.setOnClickListener(v -> {
            // 종료
            BackActivity();
        });
        setLockButtonPin.setOnClickListener(v -> {
            // 예외 처리
            try {
                // 체크 확인
                if (setLockButtonPin.isChecked()) {
                    // PIN 입력
                    Intent intent = new Intent(context, PINLockActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);

                    overridePendingTransition(R.anim.anim_next_activity_up_enter,R.anim.anim_next_activity_stay);
                }else {
                    // PIN 해제
                    rhyaSharedPreferences.removeString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE, context);
                    rhyaSharedPreferences.removeString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_DATA, context);
                }
            }catch (Exception ex) {
                ex.printStackTrace();

                // 오류 메시지 출력
                Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
            }
        });
        settingLockAppPin.setOnClickListener(v -> {
            // 예외 처리
            try {
                // PIN 설정
                if (!rhyaSharedPreferences.getString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE, context).equals(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PIN_LOCK)) {
                    Toast.makeText(context, "PIN이 설정되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                }else {
                    // PIN 입력
                    Intent intent = new Intent(context, PINLockActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);

                    overridePendingTransition(R.anim.anim_next_activity_up_enter,R.anim.anim_next_activity_stay);
                }
            }catch (Exception ex) {
                ex.printStackTrace();

                // 오류 메시지 출력
                Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *  BackActivity - 이전 액티비티로 돌아감
     */
    private void BackActivity() {
        finish();
        overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
    }
}
