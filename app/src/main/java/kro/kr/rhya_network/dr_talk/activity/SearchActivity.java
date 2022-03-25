package kro.kr.rhya_network.dr_talk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.adapter.SearchDrListAdapter;
import kro.kr.rhya_network.dr_talk.application.RhyaApplication;
import kro.kr.rhya_network.dr_talk.data.DrListItem;
import kro.kr.rhya_network.dr_talk.sort.HangulInitialSearch;

public class SearchActivity extends AppCompatActivity {
    // UI 오브젝트
    private ImageButton backButton;
    private ImageButton removeButton;
    private EditText inputSearchText;
    private RecyclerView recyclerView;
    // 검색 타입 설정
    private int searchBaseData = 0;
    // Adapter
    private SearchDrListAdapter searchDrListAdapter;


    /**
     * onCreate - 초기화
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // UI 오브젝트 가져오기
        backButton = findViewById(R.id.backButton);
        removeButton = findViewById(R.id.allRemoveButton);
        inputSearchText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.searchResultRecyclerView);

        // 검색 데이터 분류
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", -1);
        if (type != -1) {
            // 분류
            // - 0 : 의사 리스트 검색
            // - 1 : 채팅 리스트 검색
            searchBaseData = type;

            // Adpater 설정
            if (type == 0) {
                // Adapter 설정
                searchDrListAdapter = new SearchDrListAdapter(this);
                // Adapter 연결
                recyclerView.setAdapter(searchDrListAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            }
        }else {
            // 오류 메시지 출력
            Toast.makeText(this, "넘겨받은 인자 값이 잘못되었습니다. 다시 시도해 주세요.",Toast.LENGTH_SHORT).show();
        }

        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener(v -> BackActivity());

        // 텍스트 지우기 버튼 이벤트
        removeButton.setOnClickListener(v -> inputSearchText.setText(""));
        // 텍스트 변경 이벤트
        inputSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력 텍스트
                String inputText = s.toString();

                // 리스트 뷰 데이터
                ArrayList<DrListItem> inputList = new ArrayList<>();;

                // 입력 길이 확인
                if (inputText.replace(" ", "").length() < 1) {
                    // 데이터 초기화
                    searchDrListAdapter.setDrList(inputList, "");

                    return;
                }

                // 형식 확인
                if (searchBaseData == 0) { // 의사 이름, 병원 이름, 전화번호
                    for (DrListItem item : RhyaApplication.DrInfoData.getDrListItemArrayList()) {
                        // 확인
                        if (item.getDr_name().contains(inputText) ||
                            item.getDr_location_name().contains(inputText) ||
                            item.getDr_location_phone().contains(inputText)) {
                            // 데이터 추가
                            inputList.add(item);
                        }else { // 초성 검색 - 의사 이름, 병원 이름
                            HangulInitialSearch hangulInitialSearch = new HangulInitialSearch();
                            if (hangulInitialSearch.matchString(item.getDr_name(), inputText) ||
                                hangulInitialSearch.matchString(item.getDr_location_name(), inputText)) {
                                // 데이터 추가
                                inputList.add(item);
                            }
                        }
                    }
                }

                // 아이템 설정
                searchDrListAdapter.setDrList(inputList, inputText);
            }
        });

        // 키보드 올리기
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputSearchText, 0);

        // 기본 데이터 설정
        searchDrListAdapter.setDrList(new ArrayList<>(), "");
    }


    /**
     *  onBackPressed - 뒤로가기 버튼 이벤트
     */
    @Override
    public void onBackPressed() {
        // 종료
        BackActivity();
    }


    /**
     *  BackActivity - 이전 액티비티로 돌아감
     */
    private void BackActivity() {
        finish();
        overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
    }
}
