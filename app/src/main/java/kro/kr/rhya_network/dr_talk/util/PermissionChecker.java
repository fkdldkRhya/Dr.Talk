package kro.kr.rhya_network.dr_talk.util;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/*
 * App Permission Verification Manager
 */
public class PermissionChecker {
    // Context
    private final Context context;

    /**
     * 생성자 - 초기화
     * @param context Activity context
     */
    public PermissionChecker(Context context) {
        this.context = context;
    }


    /**
     * 권한 부여 확인 - 카메라 사용 권한
     * @return 권한 부여 여부
     */
    public boolean CheckPermission_CAMERA() {
        // 권한 확인
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 권한 부여 확인 - 외부 저장소 접근 권한
     * @return 권한 부여 여부
     */
    public boolean CheckPermission_WRITE_EXTERNAL_STORAGE() {
        // 권한 확인
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
