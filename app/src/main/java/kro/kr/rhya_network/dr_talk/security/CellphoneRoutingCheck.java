package kro.kr.rhya_network.dr_talk.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class CellphoneRoutingCheck {
    public boolean checkSuperUser(){
        return checkRootedFiles() || checkSuperUserCommand() || checkSuperUserCommand2() || checkTags();
    }

    private boolean checkRootedFiles(){
        final String[] files = {
                "/sbin/su",
                "/system/su",
                "/system/bin/su",
                "/system/sbin/su",
                "/system/xbin/su",
                "/system/xbin/mu",
                "/system/bin/.ext/.su",
                "/system/usr/su-backup",
                "/data/data/com.noshufou.android.su",
                "/system/app/Superuser.apk",
                "/system/app/su.apk",
                "/system/bin/.ext",
                "/system/xbin/.ext",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su"};

        for (String s : files) {
            File file = new File(s);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    /*
    루팅이 된 기기는 일반적으로 Build.TAGS 값이 제조사 키값이 아닌 test 키 값을 가지고 있습니다.
    */
    private boolean checkTags() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkSuperUserCommand(){
        try {
            Runtime.getRuntime().exec("su");
            return true;
        } catch (Error | Exception ignored) { }

        return false;
    }

    private boolean checkSuperUserCommand2() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }
}
