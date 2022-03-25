package kro.kr.rhya_network.dr_talk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.adapter.ChattingMainAdapter;
import kro.kr.rhya_network.dr_talk.util.RhyaNetworkAPI;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AIFragment extends Fragment {
    private Activity activity;
    private ValueCallback mFilePathCallback;

    public AIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AIFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AIFragment newInstance() {
        return new AIFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ai, container, false);
    }

    public class AndroidBridge {
        private final Handler handler = new Handler();

        @JavascriptInterface
        public void showResult(String result, String p1, String p2, String p3) {
            activity.runOnUiThread(() -> {
                Intent intent = new Intent(getContext(), AIResultActivity.class);
                // Intent 데이터 설정
                intent.putExtra("result", result);
                intent.putExtra("p1", p1);
                intent.putExtra("p2", p2);
                intent.putExtra("p3", p3);
                // 화면 넘기기
                startActivity(intent);
                activity.overridePendingTransition(R.anim.anim_next_activity_up_enter,R.anim.anim_next_activity_stay);
            });
        }
    }


    /**
     * onViewCreated - View 생성 후 작업
     *
     * @param view               [Override]
     * @param savedInstanceState [Override]
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        WebView m_webView = (WebView) view.findViewById(R.id.webView);
        ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result.getResultCode(), result.getData()));
                        mFilePathCallback = null;
                    }
                });

        // WebView 설정
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.setWebChromeClient(new WebChromeClient());
        m_webView.setWebViewClient(new WebViewClient());
        m_webView.getSettings().setDomStorageEnabled(true);
        m_webView.setNetworkAvailable(true);
        m_webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityResult.launch(intent);
                return true;
            }
        });


        m_webView.addJavascriptInterface(new AndroidBridge(),"androidFunction");
        m_webView.loadUrl("file:///android_asset/index.html");

    }
}