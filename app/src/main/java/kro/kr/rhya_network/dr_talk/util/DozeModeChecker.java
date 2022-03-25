package kro.kr.rhya_network.dr_talk.util;

import android.content.Context;
import android.os.PowerManager;

/*
 * Doze mode checker
 */
public class DozeModeChecker {
    /**
     * Doze 모드 확인 함수
     * @param context This context
     * @return 화이트 리스트 등록 여부
     */
    public boolean CheckDozeMode(Context context) {
        // Doze 모드 확인
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // 화이트 리스트 등록 안 됨
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }

        return true;
    }
}
