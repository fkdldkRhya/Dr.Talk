package kro.kr.rhya_network.dr_talk.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.adapter.ChattingSubAdapter;
import kro.kr.rhya_network.dr_talk.application.RhyaApplication;
import kro.kr.rhya_network.dr_talk.data.DrListItem;
import kro.kr.rhya_network.dr_talk.util.RhyaAsyncTask;
import kro.kr.rhya_network.dr_talk.util.RhyaRequestHttpURLConnection;

public class ChattingActivity extends AppCompatActivity {
    // UI Object
    private TextView name;
    private ListView chattingRecyclerView;
    private EditText sendTextEditText;
    private ImageButton sendButton;

    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        ArrayList<DrListItem> itemList = new ArrayList<>();
        ChattingSubAdapter chattingSubAdapter = new ChattingSubAdapter(itemList,getLayoutInflater());

        // UI 초기화
        name = findViewById(R.id.drName);
        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);
        sendButton = findViewById(R.id.sendButton);
        sendTextEditText = findViewById(R.id.sendTextEditText);

        DrListItem item = getIntent().getParcelableExtra("dr_info");

        // Adapter 연결
        chattingRecyclerView.setAdapter(chattingSubAdapter);

        name.setText(item.getDr_name());

        if (RhyaApplication.checker == 1) {
            DrListItem drListItem = new DrListItem("<P>", "최시훈", null, "안녕하세요.", null, null, null, null ,null, null, null, null);
            itemList.add(drListItem);
            chattingSubAdapter.notifyDataSetChanged();
            sendTextEditText.setText("");
        }

        sendButton.setOnClickListener((v) -> {
            DrListItem drListItem = new DrListItem("<ME>", RhyaApplication.MyInfo.myInfoVO.getName(), null, sendTextEditText.getText().toString(), null, null, null, null ,null, null, null, null);
            itemList.add(drListItem);
            chattingSubAdapter.notifyDataSetChanged();
            sendTextEditText.setText("");

            // 서버 접속
            new RhyaAsyncTask<String, String>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    RhyaRequestHttpURLConnection requestHttpURLConnection = new RhyaRequestHttpURLConnection();
                    String result = requestHttpURLConnection.request("https://rhya-network.kro.kr/MainServer/webpage/jsp/contest/2021_Link_Revolution_Idea_Tone_Contest/send_msg.jsp", null);

                    return null;
                }

                @Override
                protected void onPostExecute(String result) { }
            }.execute(null);
        });
    }
}
