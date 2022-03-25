package kro.kr.rhya_network.dr_talk.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.util.CookieFinder;
import kro.kr.rhya_network.dr_talk.util.DialogManager;
import kro.kr.rhya_network.dr_talk.util.NetworkChecker;
import kro.kr.rhya_network.dr_talk.util.RhyaNetworkAPI;


/*
 * activity_login.xml 파일의 컨트롤러
 *
 * 로그인 및 자동 로그인 관리
 */
public class LoginActivity extends AppCompatActivity {
    // Context
    private Context context;
    // UI 요소
    private WebView webView;
    // RHYA.Network API
    private RhyaNetworkAPI rhyaAPI;
    // Network 감지
    private NetworkChecker networkChecker;
    // Cookie 찾기
    private CookieFinder cookieFinder;
    // Dialog
    private Dialog mainTaskDialog;
    private Dialog networkDialog;
    // Dialog 관리자
    private DialogManager dialogManager;
    // Web view time out check
    boolean timeout;


    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        webView = findViewById(R.id.webView);

        rhyaAPI = new RhyaNetworkAPI(this);

        cookieFinder = new CookieFinder();

        dialogManager = new DialogManager();

        // -------------------------------------------------------------------------------
        // Progress Dialog 설정 - MainTask [ 기본 설정 ]
        mainTaskDialog = new Dialog(LoginActivity.this);
        mainTaskDialog.getWindow().setBackgroundDrawable(dialogManager.background);
        mainTaskDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainTaskDialog.setContentView(R.layout.custom_dialog_v2);
        mainTaskDialog.setCancelable(false);
        // Progress Dialog 설정 - MainTask [ 사이즈 설정 ]
        WindowManager.LayoutParams params_mainTaskDialog = mainTaskDialog.getWindow().getAttributes();
        Display display_mainTaskDialog = getWindowManager().getDefaultDisplay();
        display_mainTaskDialog.getRealSize(dialogManager.size);
        params_mainTaskDialog.width = (int) Math.round((dialogManager.size.x * 0.8));
        params_mainTaskDialog.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mainTaskDialog.getWindow().setAttributes(params_mainTaskDialog);
        // Progress Dialog 설정 - MainTask [ 내용 설정 ]
        TextView message_MainTask = mainTaskDialog.findViewById(R.id.MessageText);
        message_MainTask.setText("작업 처리 중...");
        // -------------------------------------------------------------------------------
        // 1 Button Dialog 설정 - Network [ 기본 설정 ]
        networkDialog = new Dialog(LoginActivity.this);
        networkDialog.getWindow().setBackgroundDrawable(dialogManager.background);
        networkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        networkDialog.setContentView(R.layout.custom_dialog_v1);
        networkDialog.setCancelable(false);
        // 1 Button Dialog 설정 - Network [ 사이즈 설정 ]
        WindowManager.LayoutParams params_networkDialog = mainTaskDialog.getWindow().getAttributes();
        Display display_networkDialog = getWindowManager().getDefaultDisplay();
        display_networkDialog.getRealSize(dialogManager.size);
        params_networkDialog.width = (int) Math.round((dialogManager.size.x * 0.8));
        params_networkDialog.height = WindowManager.LayoutParams.WRAP_CONTENT;
        networkDialog.getWindow().setAttributes(params_networkDialog);
        // 1 Button Dialog 설정 - Network [ 내용 설정 ]
        TextView message_Network = networkDialog.findViewById(R.id.MessageText);
        Button button_Network = networkDialog.findViewById(R.id.Btn1);
        message_Network.setText("인터넷에 연결되어 있지 않습니다. 인터넷 연결을 확인해 주세요.");
        button_Network.setText("종료");
        button_Network.setOnClickListener(view1 -> {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        });
        // -------------------------------------------------------------------------------

        // Network 상태 변경 리스너
        networkChecker = new NetworkChecker(this);
        networkChecker.setNetworkListener(new NetworkChecker.NetworkListener() {
            @Override
            public void isDisconnection() {
                // Dialog 보여주기
                if (!networkDialog.isShowing()) {
                    LoginActivity.this.runOnUiThread(() -> networkDialog.show());
                }
            }

            @Override
            public void isConnection() {
                // Dialog 숨기기
                if (networkDialog.isShowing()) {
                    LoginActivity.this.runOnUiThread(() -> networkDialog.dismiss());
                }
            }
        });
    }


    /**
     * onStart - WebView 작업 진행
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onStart() {
        super.onStart();

        // WebView 설정
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookies(null);
        cookieManager.removeAllCookies(null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setTextZoom(100);
        webView.setWebViewClient(new WebViewClient() {
            /**
             * WebView 로딩 시작
             * @param view [ Override ]
             * @param url [ Override ]
             * @param favicon [ Override ]
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                // Progress Dialog 시작
                mainTaskDialog.show();
            }


            /**
             * WebView 로딩 성공 확인
             * @param view [ Override ]
             * @param url [ Override ]
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Time out 해제
                timeout = false;

                // 예외 처리
                try {
                    // URL 검사
                    if (!rhyaAPI.isUrlOK(url)) {
                        // Progress Dialog 가 Null 이 아니고 보일 때만 종료
                        if (mainTaskDialog != null && mainTaskDialog.isShowing()) {
                            mainTaskDialog.dismiss();
                        }

                        // 오류 메시지 출력
                        dialogManager.createDialog_v1(context,
                                getWindowManager().getDefaultDisplay(),
                                "해당 페이지에서는 작업을 수행할 수 없습니다. 종료 후 다시 시도해 주세요.",
                                "종료",
                                false,
                                (dialog) -> {
                                    moveTaskToBack(true);
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                });
                        return;
                    }

                    // URL 변경 확인
                    if (url.equals(rhyaAPI.LOGIN_SUCCESS_URL)) {
                        // 로그인 성공 - 쿠키 데이터 가져오기
                        final String cookieResult = cookieManager.getCookie(url);
                        final String userUUID = cookieFinder.findCookie(cookieResult, rhyaAPI.COOKIE_NAME_USER_UUID);
                        final String tokenUUID = cookieFinder.findCookie(cookieResult, rhyaAPI.COOKIE_NAME_TOKEN_UUID);

                        /*
                        // = [ 로그 작성 ] =====================================
                        System.out.println("Dr.Talk Log : Connection --> " + url);
                        System.out.println("Dr.Talk Log : Cookie [ ALL ] --> "  + cookieResult);
                        System.out.println("Dr.Talk Log : Cookie [ UserUUID ] --> "  + userUUID);
                        System.out.println("Dr.Talk Log : Cookie [ TokenUUID ] --> "  + tokenUUID);
                        System.out.println("Dr.Talk Log : getAutoLogin --> "  + rhyaAPI.getAutoLogin());
                        // ====================================================
                        */

                        // Cookie 데이터 확인
                        if (userUUID == null || tokenUUID == null) {
                            // 로그인 성공 - 자동 로그인 등록 실패
                            rhyaAPI.setAutoLogin(rhyaAPI.rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE);
                            rhyaAPI.createGenAuthTokenURL(null, null);
                        }else {
                            // 로그인 성공 - App 전용 자동 로그인 토큰 발급

                            // URL 생성
                            rhyaAPI.createGenAuthTokenURL(userUUID, tokenUUID);

                            // URL 이동
                            webView.loadUrl(rhyaAPI.getGenAuthTokenURL());

                            return;
                        }
                    }
                    else if (url.equals(rhyaAPI.getGenAuthTokenURL())) {
                        // 발급 성공 - 쿠키 데이터 가져오기
                        final String cookieResult = cookieManager.getCookie(url);
                        final String authToken = cookieFinder.findCookie(cookieResult, rhyaAPI.COOKIE_NAME_AUTH_TOKEN);

                        /*
                        // = [ 로그 작성 ] =====================================
                        System.out.println("Dr.Talk Log : Connection --> " + url);
                        System.out.println("Dr.Talk Log : Cookie [ ALL ] --> "  + cookieResult);
                        System.out.println("Dr.Talk Log : Cookie [ AuthToken ] --> " + authToken);
                        System.out.println("Dr.Talk Log : getAutoLogin --> "  + rhyaAPI.getAutoLogin());
                        // ====================================================
                        */

                        // Cookie 데이터 확인
                        if (authToken == null || authToken.equals(rhyaAPI.COOKIE_DATA_AUTH_FAIL)) {
                            // 발급 성공 - 자동 로그인 등록 실패
                            rhyaAPI.setAutoLogin(rhyaAPI.rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE);
                            rhyaAPI.createGenAuthTokenURL(null, null);
                        }else {
                            // 발급 성공 - App 전용 자동 로그인 토큰 발급 성공
                            rhyaAPI.setAutoLogin(authToken);
                        }

                        // Progress Dialog 가 Null 이 아니고 보일 때만 종료
                        if (mainTaskDialog != null && mainTaskDialog.isShowing()) {
                            mainTaskDialog.dismiss();
                        }

                        NextActivity();
                    }
                    else if (url.contains(rhyaAPI.getCheckAuthTokenURL(rhyaAPI.getAutoLogin()))) {
                        // 토큰 확인  - 쿠키 데이터 가져오기
                        final String cookieResult = cookieManager.getCookie(url);
                        final String authTokenCheckResult = cookieFinder.findCookie(cookieResult, rhyaAPI.COOKIE_NAME_AUTH_TOKEN);

                        /*
                        // = [ 로그 작성 ] =====================================
                        System.out.println("Dr.Talk Log : Connection --> " + url);
                        System.out.println("Dr.Talk Log : Cookie [ ALL ] --> "  + cookieResult);
                        System.out.println("Dr.Talk Log : Cookie [ AuthTokenCheckResult ] --> "  + authTokenCheckResult);
                        System.out.println("Dr.Talk Log : getAutoLogin --> "  + rhyaAPI.getAutoLogin());
                        // ====================================================
                        */

                        // 쿠키 결과 확인
                        if (authTokenCheckResult != null && authTokenCheckResult.equals(rhyaAPI.COOKIE_DATA_AUTH_CHECKER)) {
                            NextActivity();
                        } else {
                            rhyaAPI.setAutoLogin(rhyaAPI.rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE);
                            rhyaAPI.createGenAuthTokenURL(null, null);

                            // URL 이동
                            webView.loadUrl(rhyaAPI.LOGIN_UI_URL);
                        }
                    }

                    // Progress Dialog 가 Null 이 아니고 보일 때만 종료
                    if (mainTaskDialog != null && mainTaskDialog.isShowing()) {
                        mainTaskDialog.dismiss();
                    }
                }catch (UnsupportedEncodingException |
                        InvalidAlgorithmParameterException |
                        InvalidKeyException |
                        NoSuchAlgorithmException |
                        BadPaddingException |
                        IllegalBlockSizeException |
                        NoSuchPaddingException ex) {

                    ex.printStackTrace();

                    // 오류 발생 데이터 제거
                    rhyaAPI.rhyaSharedPreferences.removeString(rhyaAPI.rhyaSharedPreferences.SHARED_PREFERENCES_AES_KEY, context);
                    rhyaAPI.rhyaSharedPreferences.removeString(rhyaAPI.rhyaSharedPreferences.SHARED_PREFERENCES_AUTH_TOKEN, context);

                    // 예외 발생 처리
                    dialogManager.createDialog_v1(context,
                            getWindowManager().getDefaultDisplay(),
                            ex.getMessage(),
                            "종료",
                            false,
                            dialog -> {
                                moveTaskToBack(true);
                                finish();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            });
                }
            }


            /**
             * WebView 로딩 오류 발생
             * @param view [ Override ]
             * @param request [ Override ]
             * @param error [ Override ]
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                // Progress Dialog 가 Null 이 아니고 보일 때만 종료
                if (mainTaskDialog != null && mainTaskDialog.isShowing()) {
                    mainTaskDialog.dismiss();
                }

                // 오류 메시지 출력
                dialogManager.createDialog_v1(context,
                        getWindowManager().getDefaultDisplay(),
                        "UI 로딩 중 오류 발생! 종료 후 다시 시도해 주세요.",
                        "종료",
                        false,
                        (dialog) -> {
                            moveTaskToBack(true);
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        });
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        // 예외 처리
        try {
            // 자동 로그인 검사
            if (!rhyaAPI.getAutoLogin().equals(rhyaAPI.rhyaSharedPreferences.DEFAULT_RETURN_STRING_VALUE)) {
                // 토큰 검사
                webView.loadUrl(rhyaAPI.getCheckAuthTokenURL(rhyaAPI.getAutoLogin()));
            }else {
                // URL 로딩 - 로그인
                webView.loadUrl(rhyaAPI.LOGIN_UI_URL);
            }
            // Time out 확인
            new Thread(() -> {
                timeout = true;

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(timeout) {
                    // 오류 메시지 출력
                    runOnUiThread(() ->
                            dialogManager.createDialog_v1(context,
                                    getWindowManager().getDefaultDisplay(),
                                    "Time out : 10000 - 다시 시도해 주십시오.",
                                    "종료",
                                    false,
                                    (dialog) -> {
                                        moveTaskToBack(true);
                                        finish();
                                        android.os.Process.killProcess(android.os.Process.myPid());
                                    })
                    );
                }
            }).start();
        }catch (UnsupportedEncodingException |
                InvalidAlgorithmParameterException |
                InvalidKeyException |
                NoSuchAlgorithmException |
                BadPaddingException |
                IllegalBlockSizeException |
                NoSuchPaddingException ex) {

            ex.printStackTrace();

            // 오류 발생 데이터 제거
            rhyaAPI.rhyaSharedPreferences.removeString(rhyaAPI.rhyaSharedPreferences.SHARED_PREFERENCES_AES_KEY, context);
            rhyaAPI.rhyaSharedPreferences.removeString(rhyaAPI.rhyaSharedPreferences.SHARED_PREFERENCES_AUTH_TOKEN, context);

            // 예외 발생 처리
            dialogManager.createDialog_v1(context,
                    getWindowManager().getDefaultDisplay(),
                    ex.getMessage(),
                    "종료",
                    false,
                    dialog -> {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    });
        }
    }

    /**
     * 종료 메시지 출력
     */
    @Override
    public void onBackPressed() {
        // Dialog 생성
        dialogManager.createDialog_v3(context,
                getWindowManager().getDefaultDisplay(),
                "정말로 종료하시겠습니까?",
                "종료",
                "취소",
                false,
                new DialogManager.DialogListener_v3() {
                    @Override
                    public void onClickListenerButtonYes(Dialog dialog) {
                        moveTaskToBack(true);
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                    @Override
                    public void onClickListenerButtonNo(Dialog dialog) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
    }


    /**
     * NextActivity - Activity 전환
     */
    private void NextActivity() {
        // 화면 전환 --> activity_main.xml
        // 애니메이션 [ 오른쪽 --> 왼쪽 ]
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_stay);
    }


    /**
     * onPause - 앱 일시 중지
     */
    @Override
    protected void onPause() {
        super.onPause();

        // 네트워크 상태 변경 감지 해제
        networkChecker.unregister();
    }


    /**
     * onResume - 앱 동작 재계
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 네트워크 상태 변경 감지 설정
        networkChecker.register();
    }
}
