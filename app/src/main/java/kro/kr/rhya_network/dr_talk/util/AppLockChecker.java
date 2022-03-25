package kro.kr.rhya_network.dr_talk.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.activity.LoginActivity;
import kro.kr.rhya_network.dr_talk.activity.PINLockActivity;

/**
 * 앱 잠금 확인
 */
public class AppLockChecker {
    /**
     * 앱 잠금 확인
     */
    public int checkAppLock(Context context) {
        try {
            // RHYA SharedPreferences
            RhyaSharedPreferences rhyaSharedPreferences = new RhyaSharedPreferences(context, false);
            String result = rhyaSharedPreferences.getString(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE, context);
            // 데이터 비교
            if (!result.equals(rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE)) {
                // 형식 비교
                if (result.equals(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PIN_LOCK)) { // PIN 잠금
                    return 2;
                }else if (result.equals(rhyaSharedPreferences.SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PATTERN_LOCK)) { // Pattern 잠금
                    return 3;
                }else { // 알 수 없는 형식
                    return 4;
                }
            }else { // 기본 데이터 - 잠금 X
                return 1;
            }
        }catch (Exception ex) { // 예외 발생
            ex.printStackTrace();

            return 0;
        }
    }


    /**
     * 앱 잠금 확인 작업 진행
     * @param activity activity
     * @param context context
     * @param result result
     */
    public void appLockCheckResultForTask(Activity activity, Context context, int result) {
        switch (result) {
            case 0:
            case 3:
            case 4: { // 예외 발생 or 패턴 잠금 or 알 수 없는 형식
                DialogManager dialogManager = new DialogManager();
                // Dialog 생성
                dialogManager.createDialog_v1(context,
                        activity.getWindowManager().getDefaultDisplay(),
                        "앱의 중요 데이터가 허가되지 않은 방법으로 수정되어 Dr.Talk을 사용할 수 없습니다.",
                        "종료",
                        false,
                        dialog -> {
                            activity.moveTaskToBack(true);
                            activity.finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        });

                break;
            }

            case 1: { // 기본 데이터
                // 화면 전환 --> activity_login.xml
                Intent intent = new Intent(context, LoginActivity.class);
                activity.startActivity(intent);
                // 종료
                activity.finish();

                activity.overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_stay);

                break;
            }

            case 2: { // PIN 잠금
                // 화면 전환 --> activity_login.xml
                Intent intent = new Intent(context, PINLockActivity.class);
                intent.putExtra("type", 0);
                activity.startActivity(intent);
                // 종료
                activity.finish();

                activity.overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_stay);

                break;
            }
        }
    }
}
