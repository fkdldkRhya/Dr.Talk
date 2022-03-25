package kro.kr.rhya_network.dr_talk.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * RHYA.Network MD5 생성
 */
public class RhyaMD5 {
    /**
     * getMD5 - MD5 생성 함수
     * @param input 원본 문자열
     * @return 암호화 문자열
     */
    public String getMD5(String input) {
        String MD5;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] byteData = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }
}
