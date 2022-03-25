package kro.kr.rhya_network.dr_talk.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import kro.kr.rhya_network.dr_talk.R;
import kro.kr.rhya_network.dr_talk.activity.ChattingActivity;
import kro.kr.rhya_network.dr_talk.activity.ShowDrInfoActivity;
import kro.kr.rhya_network.dr_talk.data.DrListItem;

public class ChattingMainAdapter extends RecyclerView.Adapter<ChattingMainAdapter.ViewHolder> {
    // UI 데이터
    private ArrayList<DrListItem> drListItems;
    private Context context;
    // 부모 데이터
    private final Activity pActivity;


    /**
     * 생성자 - 변수 입력 및 초기화
     */
    public ChattingMainAdapter(Activity activity) {
        pActivity = activity;
    }

    @NonNull
    @Override
    public ChattingMainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_search_catting_item_layout, parent, false);
        context = view.getContext();

        return new ChattingMainAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(drListItems.get(position));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDrList(ArrayList<DrListItem> list) {
        this.drListItems = list;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return drListItems.size();
    }

    /**
     * Dr. List 뷰 홀더
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        // UI 오브젝트
        private final ImageView drImage;
        private final TextView drName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // UI 요소 가져오기
            drImage = itemView.findViewById(R.id.drImage);
            drName = itemView.findViewById(R.id.drName);

            // 클릭 리스너 설정
            itemView.setOnClickListener(v -> {
                // 정상 클릭 확인
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 데이터 리스트로부터 아이템 데이터 참조.
                    DrListItem item = drListItems.get(pos);
                    // Intent 생성
                    Intent intent = new Intent(v.getContext(), ChattingActivity.class);
                    // Intent 데이터 설정
                    intent.putExtra("dr_info", item);
                    // 화면 넘기기
                    v.getContext().startActivity(intent);
                    pActivity.overridePendingTransition(R.anim.anim_next_activity_up_enter,R.anim.anim_next_activity_stay);
                }
            });
        }

        public void onBind(DrListItem item){
            // 데이터 설정
            Glide.with(context)
                    .load(item.getDr_image())
                    .apply(new RequestOptions().circleCrop().centerCrop()).circleCrop()
                    .placeholder(R.drawable.ic_applogo_doctor)
                    .error(R.drawable.ic_patient)
                    .fallback(R.drawable.ic_applogo_doctor)
                    .skipMemoryCache(false)
                    .into(drImage);
            drName.setText(item.getDr_name());
        }
    }
}
