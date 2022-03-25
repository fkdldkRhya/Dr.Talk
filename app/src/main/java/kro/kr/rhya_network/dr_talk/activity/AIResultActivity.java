package kro.kr.rhya_network.dr_talk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kro.kr.rhya_network.dr_talk.R;

public class AIResultActivity extends AppCompatActivity {
    /**
     * onCreate - 초기화 작업 진행
     * @param savedInstanceState [Override]
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_result);

        TextView ai_result = findViewById(R.id.resultText);
        TextView ai_result_p_v_1 = findViewById(R.id.resultP1v);
        TextView ai_result_p_v_2 = findViewById(R.id.resultP2v);
        TextView ai_result_p_v_3 = findViewById(R.id.resultP3v);
        ProgressBar ai_result_p_1 = findViewById(R.id.progressBar1);
        ProgressBar ai_result_p_2 = findViewById(R.id.progressBar2);
        ProgressBar ai_result_p_3 = findViewById(R.id.progressBar3);
        Button backBtn = findViewById(R.id.backButtonMain);

        Intent intent = getIntent();
        ai_result.setText(intent.getStringExtra("result"));
        ai_result_p_v_1.setText(String.format("%s%%", intent.getStringExtra("p1")));
        ai_result_p_v_2.setText(String.format("%s%%", intent.getStringExtra("p3")));
        ai_result_p_v_3.setText(String.format("%s%%", intent.getStringExtra("p2")));
        ai_result_p_1.setProgress(Integer.parseInt(intent.getStringExtra("p1")));
        ai_result_p_2.setProgress(Integer.parseInt(intent.getStringExtra("p3")));
        ai_result_p_3.setProgress(Integer.parseInt(intent.getStringExtra("p2")));

        backBtn.setOnClickListener(v -> {
            finish();

            overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
        });
    }


    /**
     * 종료 확인 메시지 출력
     */
    @Override
    public void onBackPressed() {
        finish();

        overridePendingTransition(R.anim.anim_next_activity_stay, R.anim.anim_next_activity_down_exit);
    }
}
