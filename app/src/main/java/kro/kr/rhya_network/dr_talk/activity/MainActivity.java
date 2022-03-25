package kro.kr.rhya_network.dr_talk.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.adapter.RhyaAdapter;
import kro.kr.rhya_network.dr_talk.util.DialogManager;
import kro.kr.rhya_network.dr_talk.util.NetworkChecker;


/*
 * activity_main.xml 파일의 컨트롤러
 *
 * 앱의 메인 화면
 */
public class MainActivity extends AppCompatActivity {
    public static Fragment fragment;
    // Context
    private Context context;
    // 뒤로가기 버튼 시간
    private long backBtnTime = 0;
    // 현재 Fragment 번호
    private int nowFragment = 0;
    // Network 감지
    private NetworkChecker networkChecker;
    // Dialog 관리자
    private DialogManager dialogManager;
    // Dialog
    private Dialog mainTaskDialog;
    private Dialog networkDialog;
    // UI Object
    private ImageButton searchButton;
    private ImageButton settingButton;
    private ViewPager2 viewPager;
    private TabLayout mainTabLayout;
    // Fragment adapter
    private RhyaAdapter rhyaAdapter;
    // Fragment list
    private Fragment drListFragment;
    private Fragment chattingFragment;
    private Fragment aiFragment;
    private Fragment myInfoFragment;
    // PopupMenu
    private PopupMenu settingPopupMenu;
    // Color
    private final int tabItemSelectColor = Color.parseColor("#3386FF");
    private final int tabItemUnSelectColor = Color.parseColor("#000000");


    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        dialogManager = new DialogManager();

        // -------------------------------------------------------------------------------
        // Progress Dialog 설정 - MainTask [ 기본 설정 ]
        mainTaskDialog = new Dialog(MainActivity.this);
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
        networkDialog = new Dialog(MainActivity.this);
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
                    MainActivity.this.runOnUiThread(() -> networkDialog.show());
                }
            }

            @Override
            public void isConnection() {
                // Dialog 숨기기
                if (networkDialog.isShowing()) {
                    MainActivity.this.runOnUiThread(() -> networkDialog.dismiss());
                }
            }
        });

        // UI Object 설정
        searchButton = findViewById(R.id.searchButton);
        settingButton = findViewById(R.id.settingButton);
        viewPager = findViewById(R.id.mainViewPager);
        mainTabLayout = findViewById(R.id.mainTabLayout);

        // Fragment 설정
        if (drListFragment == null) { drListFragment = DrListFragment.newInstance(); }
        if (chattingFragment == null) { chattingFragment = ChattingFragment.newInstance(); }
        if (aiFragment == null) { aiFragment = AIFragment.newInstance(); }
        if (myInfoFragment == null) { myInfoFragment = MyInfoFragment.newInstance(); }

        // Adapter 설정
        rhyaAdapter = new RhyaAdapter(getSupportFragmentManager(), getLifecycle());
        rhyaAdapter.addFragment(drListFragment);
        rhyaAdapter.addFragment(chattingFragment);
        rhyaAdapter.addFragment(aiFragment);
        rhyaAdapter.addFragment(myInfoFragment);

        // PopupMenu 설정
        Context wrapper = new ContextThemeWrapper(this, R.style.RoundPopupMenuStyle);
        settingPopupMenu = new PopupMenu(wrapper, settingButton, Gravity.END);
        getMenuInflater().inflate(R.menu.menu_setting_popupmenu, settingPopupMenu.getMenu());

        // Tab layout 설정
        viewPager.setAdapter(rhyaAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mainTabLayout, viewPager, true, (tab, position) -> {
            // 텝 레이아웃이랑 연결
            switch (position){
                case 0:
                    tab.setText("의사");
                    tab.setIcon(R.drawable.ic_doctor_with_stethoscope);
                    break;

                case 1:
                    tab.setText("채팅");
                    tab.setIcon(R.drawable.ic_chat);
                    break;

                case 2:
                    tab.setText("AI");
                    tab.setIcon(R.drawable.ic_artificial_intelligence);
                    break;

                case 3:
                    tab.setText("내정보");
                    tab.setIcon(R.drawable.ic_baseline_more_horiz_x30);
                    break;

            }

        });

        tabLayoutMediator.attach();

        // Tab item icon 설정
        Objects.requireNonNull(
                Objects.requireNonNull(
                        mainTabLayout.getTabAt(0)
                ).getIcon()
        ).setColorFilter(tabItemSelectColor, PorterDuff.Mode.SRC_IN);
        for (int i = 1; i < mainTabLayout.getTabCount(); i++) {
            Objects.requireNonNull(
                    Objects.requireNonNull(
                            mainTabLayout.getTabAt(i)
                    ).getIcon()
            ).setColorFilter(tabItemUnSelectColor, PorterDuff.Mode.SRC_IN);
        }

        // Fragment , Tab layout 리스너 설정
        mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 색상 변경
                Objects.requireNonNull(
                        tab.getIcon()
                ).setColorFilter(tabItemSelectColor, PorterDuff.Mode.SRC_IN);

                // 번호 설정
                nowFragment = tab.getPosition();

                // Fragment 변경
                switch (tab.getPosition())
                {
                    case 0 : // 의사
                        viewPager.setCurrentItem(0);
                        // 검색, 설정 버튼 활성화
                        searchButton.setVisibility(View.VISIBLE);
                        settingButton.setVisibility(View.VISIBLE);

                        break;
                    case 1 : // 채팅
                        viewPager.setCurrentItem(1);
                        // 검색, 설정 버튼 비활성화
                        searchButton.setVisibility(View.GONE);
                        settingButton.setVisibility(View.GONE);

                        break;
                    case 2 : // AI
                        viewPager.setCurrentItem(2);
                        // 검색, 설정 버튼 비활성화
                        searchButton.setVisibility(View.GONE);
                        settingButton.setVisibility(View.GONE);

                        break;
                    case 3 : // 내정보
                        viewPager.setCurrentItem(3);
                        // 검색, 설정 버튼 비활성화
                        searchButton.setVisibility(View.GONE);
                        settingButton.setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 색상 변경
                Objects.requireNonNull(
                        tab.getIcon()
                ).setColorFilter(tabItemUnSelectColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 설정 버튼 클릭 이벤트 설정
        settingButton.setOnClickListener(v -> settingPopupMenu.show());
        // PopupMenu 이벤트 설정
        settingPopupMenu.setOnMenuItemClickListener(item -> {
            if (((DrListFragment) drListFragment).isLoadTask) {
                Toast.makeText(context, "작업이 진행 중입니다. 작업이 종료된 후에 시도해 주세요.",Toast.LENGTH_SHORT).show();

                return false;
            }


            if (item.getTitle().equals("오름차순 정렬")) {
                ((DrListFragment) drListFragment).sortType = 0;

            }else if (item.getTitle().equals("내림차순 정렬")) {
                ((DrListFragment) drListFragment).sortType = 1;

            }

            ((DrListFragment) drListFragment).drListSortThread();

            return false;
        });

        // 검색 버튼 클릭 이벤트 설정
        searchButton.setOnClickListener(v -> {
            // Intent 생성
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            // 데이터 설정
            intent.putExtra("type", nowFragment);
            // 화면 넘기기
            startActivity(intent);
            overridePendingTransition(R.anim.anim_next_activity_up_enter, R.anim.anim_next_activity_stay);
        });

        fragment = chattingFragment;
    }


    /**
     * onStart - UI 작업 진행
     */
    @Override
    protected void onStart() {
        super.onStart();
    }


    /**
     * 종료 확인 메시지 출력
     */
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            // 종료
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 계정 작업 중 오류 발생 메시지 출력
     */
    public void onErrorAccountShowMsg() {
        dialogManager.createDialog_v1(context,
                getWindowManager().getDefaultDisplay(),
                "작업 중 알 수 없는 오류가 발생하였습니다. 앱을 다시 시작해주세요.",
                "종료",
                false,
                dialog -> {
                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                });
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