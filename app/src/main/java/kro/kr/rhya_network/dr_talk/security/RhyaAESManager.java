package kro.kr.rhya_network.dr_talk.security;

import android.content.Context;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kro.kr.rhya_network.dr_talk.util.RhyaSharedPreferences;

/*
 * RHYA.Network AES 암/복호화 관리자
 */
public class RhyaAESManager {
    // 암호화 데이터
    private final String secretKey;
    // 암호화 기본 베이스 키
    private final String AES_KEY_GEN_BASE_VALUE = "zeB4t95kYCTAz4erCnrCHGHcbewY3awR";


    /**
     * 생성자 - 초기화
     */
    public RhyaAESManager(Context context) {
        // SharedPreferences Manager
        RhyaSharedPreferences rhyaSharedPreferences = new RhyaSharedPreferences(context, true);

        // 암호화 키 데이터 가져오기
        String getValue = rhyaSharedPreferences.getStringNoAES(rhyaSharedPreferences.SHARED_PREFERENCES_AES_KEY, context);
        if (getValue.equals(rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE)) {
            // 등록된 키 없음

            // 키 생성
            String genKey = getRandomKey();
            // 키 등록
            rhyaSharedPreferences.setStringNoAES(rhyaSharedPreferences.SHARED_PREFERENCES_AES_KEY,genKey, context);
            secretKey = genKey;
        }else {
            // 등록된 키 있음

            // 키 추출
            secretKey = getValue;
        }
    }


    /**
     * aesDecode - AES256 암호화
     * @param str 암호화 텍스트
     * @return 암호화된 텍스트
     */
    public String aesEncode(String str) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(getIV());
        SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

        return Base64.encodeToString(cipher.doFinal(textBytes), Base64.DEFAULT);
    }


    /**
     * aesDecode - AES256 복호화
     * @param str 복호화 텍스트
     * @return 복호화된 텍스트
     */
    public String aesDecode(String str) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] textBytes = Base64.decode(str, Base64.DEFAULT);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(getIV());
        SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);

        return new String(cipher.doFinal(textBytes), "UTF-8");
    }


    /**
     * getIV - IV 생성
     * @return iv
     */
    private byte[] getIV() {
        return secretKey.substring(0, 16).getBytes();
    }


    /**
     * 암호화 키 랜덤 생성
     * @return random aes key [ 32 len ]
     */
    private String getRandomKey() {
        UUID genUUID = UUID.randomUUID();
        RhyaMD5 md5 = new RhyaMD5();
        return md5.getMD5(genUUID.toString().concat(AES_KEY_GEN_BASE_VALUE));
    }
}
