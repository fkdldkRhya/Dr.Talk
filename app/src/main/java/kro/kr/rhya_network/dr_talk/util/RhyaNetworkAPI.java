package kro.kr.rhya_network.dr_talk.util;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * RHYA.Network Server connection API
 */
public class RhyaNetworkAPI {
    // Activity Context
    private final Context mContext;
    // SharedPreferences Manager
    public final RhyaSharedPreferences rhyaSharedPreferences;
    // 로그인 URL 주소
    public final String LOGIN_UI_URL = "https://rhya-network.kro.kr/MainServer/webpage/jsp/user_account/sign_in.jsp?rpid=13&ctoken=1";
    // 로그인 성공 URL 주소
    public final String LOGIN_SUCCESS_URL = "https://rhya-network.kro.kr/MainServer/webpage/jsp/contest/2021_Link_Revolution_Idea_Tone_Contest/login_success.jsp";
    private String GET_AUTH_FULL_URL = "<Null>";
    // Auth 데이터
    public final String GET_AUTH_URL = "https://rhya-network.kro.kr/MainServer/webpage/jsp/user_account/auth_token_checker.jsp";
    // 사용약관 URL 주소
    public final String TERMS_OF_USE_URL = "https://rhya-network.kro.kr/MainServer/webpage/jsp/main/rhya_network_terms.jsp";
    // Cookie 데이터
    public final String COOKIE_NAME_USER_UUID = "AutoLogin_UserUUID";
    public final String COOKIE_NAME_TOKEN_UUID = "AutoLogin_TokenUUID";
    public final String COOKIE_NAME_AUTH_TOKEN = "AuthTokenResult";
    public final String COOKIE_DATA_AUTH_CHECKER = "\"<SUCCESS>\"";
    public final String COOKIE_DATA_AUTH_FAIL = "\"<Null>\"";
    // Dr.Talk URL 데이터
    public final String DR_TALK_AUTH_TOKEN_PARM = "authtoken";
    public final String DR_TALK_INPUT_DR_ID = "drid";
    public final String DR_TALK_GET_DR_INFO = "https://rhya-network.kro.kr/MainServer/webpage/jsp/contest/2021_Link_Revolution_Idea_Tone_Contest/dr_info.jsp";
    public final String DR_TALK_GET_DR_LIST = "https://rhya-network.kro.kr/MainServer/webpage/jsp/contest/2021_Link_Revolution_Idea_Tone_Contest/dr_list.jsp";
    public final String DR_TALK_GET_USER_INFO = "https://rhya-network.kro.kr/MainServer/webpage/jsp/contest/2021_Link_Revolution_Idea_Tone_Contest/get_info.jsp";
    // 파일 다운로드 URL
    public final String FILE_DOWNLOAD_APP_URL = "https://rhya-network.kro.kr/MainServer/webpage/jsp/file_manager/file_download_app.jsp?fileid=";


    /**
     * 생성자 - 초기화 작업
     */
    public RhyaNetworkAPI(Context context) {
        mContext = context;
        rhyaSharedPreferences = new RhyaSharedPreferences(context, false);
    }


    /**
     * 자동 로그인 설정 저장 함수
     * @param value token
     */
    public void setAutoLogin(String value) throws NoSuchPaddingException,
                                                  InvalidAlgorithmParameterException,
                                                  UnsupportedEncodingException,
                                                  IllegalBlockSizeException,
                                                  BadPaddingException,
                                                  NoSuchAlgorithmException,
                                                  InvalidKeyException {
        rhyaSharedPreferences.setString(rhyaSharedPreferences.SHARED_PREFERENCES_AUTH_TOKEN, value, mContext);
    }


    /**
     * 자동 로그인 인증 토큰 불러오기
     * @return 자동 로그인 토큰 데이터
     */
    public String getAutoLogin() throws NoSuchPaddingException,
                                        InvalidKeyException,
                                        UnsupportedEncodingException,
                                        IllegalBlockSizeException,
                                        BadPaddingException,
                                        NoSuchAlgorithmException,
                                        InvalidAlgorithmParameterException {
        return rhyaSharedPreferences.getString(rhyaSharedPreferences.SHARED_PREFERENCES_AUTH_TOKEN, mContext);
    }


    /**
     * URL 이동 범위 확인
     * @param url 이동 URL
     * @return True / False
     */
    public boolean isUrlOK(String url) {
        return url.contains("https://rhya-network.kro.kr/MainServer/webpage/jsp");
    }


    /**
     * 자동 로그인 토큰 발급 URL 생성
     * @param user_uuid 계정 아이디
     * @param user_token 사용자 자동 로그인 토큰
     */
    public void createGenAuthTokenURL(String user_uuid, String user_token) {
        // Null 확인
        if (user_uuid == null || user_token == null) {
            GET_AUTH_FULL_URL = rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE;

            return;
        }

        // URL 생성
        StringBuilder sb = new StringBuilder();
        // Auth token 발급 데이터
        String GET_AUTH_MAIN_URL = "https://rhya-network.kro.kr/MainServer/webpage/jsp/user_account/auth_token.jsp";
        sb.append(GET_AUTH_MAIN_URL);
        sb.append("?");
        String GET_AUTH_PARAMETER_USER = "user";
        sb.append(GET_AUTH_PARAMETER_USER);
        sb.append("=");
        sb.append(user_uuid);
        sb.append("&");
        String GET_AUTH_PARAMETER_TOKEN = "token";
        sb.append(GET_AUTH_PARAMETER_TOKEN);
        sb.append("=");
        sb.append(user_token);
        sb.append("&");
        String GET_AUTH_PARAMETER_NAME = "name";
        sb.append(GET_AUTH_PARAMETER_NAME);
        sb.append("=");
        String GET_AUTH_TOKEN_SERVICE_NAME = "kro_kr_rhya__network_dr__talk";
        sb.append(GET_AUTH_TOKEN_SERVICE_NAME);

        GET_AUTH_FULL_URL = sb.toString();
    }


    /**
     * Auth token 확인 url 생성
     * @param token Auth token
     * @return url
     */
    public String getCheckAuthTokenURL(String token) {
        // URL 생성
        StringBuilder sb = new StringBuilder();
        // Auth token 발급 데이터
        sb.append(GET_AUTH_URL);
        sb.append("?");
        String GET_AUTH_PARAMETER_TOKEN = "token";
        sb.append(GET_AUTH_PARAMETER_TOKEN);
        sb.append("=");
        sb.append(token);
        sb.append("&");
        String GET_AUTH_PARAMETER_NAME = "name";
        sb.append(GET_AUTH_PARAMETER_NAME);
        sb.append("=");
        String GET_AUTH_TOKEN_SERVICE_NAME = "kro_kr_rhya__network_dr__talk";
        sb.append(GET_AUTH_TOKEN_SERVICE_NAME);

        return sb.toString();
    }


    /**
     * 생성된 Auth token 생성 URL 반환
     * @return Url
     */
    public String getGenAuthTokenURL() {
        return GET_AUTH_FULL_URL;
    }
}
