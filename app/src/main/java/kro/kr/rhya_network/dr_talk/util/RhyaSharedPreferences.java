package kro.kr.rhya_network.dr_talk.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kro.kr.rhya_network.dr_talk.security.RhyaAESManager;

/*
 * SharedPreferences 관리자
 */
public class RhyaSharedPreferences {
    /*
     * SharedPreferences 저장 정보
     */
    public final String SHARED_PREFERENCES_APP_NAME = "RHYA.Network_PREFERENCES"; // 설정 저장 이름
    public final String SHARED_PREFERENCES_AUTH_TOKEN = "AUTO_LOGIN_AUTH_TOKEN"; // 사용자 인증 토큰 데이터
    public final String SHARED_PREFERENCES_AES_KEY = "AES_SECRET_KEY"; // AES 암/복호화 키
    public final String SHARED_PREFERENCES_DR_LIST_SORT_TYPE = "DR_LIST_SORT_TYPE"; // 의사 정보 정령 방식
    public final String SHARED_PREFERENCES_APP_LOCK_TYPE = "APP_LOCK_TYPE"; // 앱 잠금 형식
    public final String SHARED_PREFERENCES_APP_LOCK_DATA = "APP_LOCK_DATA"; // 앱 잠금 데이터
    public final String SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PIN_LOCK = "LOCK_TYPE_PIN"; // 앱 잠금 형식 내용
    public final String SHARED_PREFERENCES_APP_LOCK_TYPE_VALUE_PATTERN_LOCK = "LOCK_TYPE_PATTERN"; // 앱 잠금 형식 내용
    public final String SHARED_PREFERENCES_SETTING_NOTICE_ON_OFF = "NOTICE"; // 알림 설정 KEY
    public final String SHARED_PREFERENCES_SETTING_SAVE_DATA_ON_OFF = "SAVE_DATA"; // 알림 설정 KEY
    public final String SHARED_PREFERENCES_SETTING_VALUE_ON = "ON"; // 알림 ON
    public final String SHARED_PREFERENCES_SETTING_VALUE_OFF = "OFF"; // 알림 OFF
    // String 기본 반환 데이터
    public final String DEFAULT_RETURN_STRING_VALUE = "<Null>";
    // AES 암호화 관리자
    private final RhyaAESManager rhyaAESManager;


    /**
     * 생성자 - 초기화 작업
     */
    public RhyaSharedPreferences(Context context, boolean noInit) {
        if (!noInit) {
            rhyaAESManager = new RhyaAESManager(context);
        }else {
            rhyaAESManager = null;
        }
    }



    // SharedPreferences 함수
    // **********************************************************************
    /**
     * SharedPreferences [ String ] 설정
     * @param key 설정 이름
     * @param value 설정 데이터
     */
    public void setString(String key, String value, Context mContext) throws NoSuchPaddingException,
            InvalidKeyException,
            UnsupportedEncodingException,
            IllegalBlockSizeException,
            BadPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {

        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        assert rhyaAESManager != null;
        value = rhyaAESManager.aesEncode(value);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    /**
     * SharedPreferences [ String ] 가져오기
     * @param key 설정 이름
     * @return 설정 데이터
     */
    public String getString(String key, Context mContext) throws NoSuchPaddingException,
            InvalidKeyException,
            UnsupportedEncodingException,
            IllegalBlockSizeException,
            BadPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {

        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        String returnValue = prefs.getString(key, DEFAULT_RETURN_STRING_VALUE);

        if (returnValue.equals(DEFAULT_RETURN_STRING_VALUE)) {
            return DEFAULT_RETURN_STRING_VALUE;
        }

        assert rhyaAESManager != null;
        return rhyaAESManager.aesDecode(returnValue);
    }
    /**
     * SharedPreferences (No Encrypt) [ String ] 설정
     * @param key 설정 이름
     * @param value 설정 데이터
     */
    public void setStringNoAES(String key, String value, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    /**
     * SharedPreferences (No Decrypt) [ String ] 가져오기
     * @param key 설정 이름
     * @return 설정 데이터
     */
    public String getStringNoAES(String key, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);
        return prefs.getString(key, DEFAULT_RETURN_STRING_VALUE);
    }
    /**
     * SharedPreferences [ String ] 제거
     * @param key 설정 이름
     */
    public void removeString(String key, Context mContext) {

        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
    // **********************************************************************
}
