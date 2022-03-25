package kro.kr.rhya_network.dr_talk.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class ChattingSubAdapter extends BaseAdapter {
    ArrayList<DrListItem> messageItems;
    LayoutInflater layoutInflater;

    public ChattingSubAdapter(ArrayList<DrListItem> messageItems, LayoutInflater layoutInflater) {
        this.messageItems = messageItems;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        DrListItem item=messageItems.get(position);

        //재활용할 뷰는 사용하지 않음!!
        View itemView=null;

        //메세지가 내 메세지인지??
        if(item.getDr_id().equals("<ME>")){
            itemView= layoutInflater.inflate(R.layout.chatting_send,viewGroup,false);

            //만들어진 itemView에 값들 설정
            ImageView iv= itemView.findViewById(R.id.iv);
            TextView tvName= itemView.findViewById(R.id.tv_name);
            TextView tvMsg= itemView.findViewById(R.id.tv_msg);
            tvName.setText(item.getDr_name());
            tvMsg.setText(item.getDr_description());
            Glide.with(itemView).load(R.drawable.ic_patient).into(iv);
        }else if (item.getDr_id().equals("<P>")){
            itemView= layoutInflater.inflate(R.layout.chatting_res,viewGroup,false);

            //만들어진 itemView에 값들 설정
            ImageView iv= itemView.findViewById(R.id.iv);
            TextView tvName= itemView.findViewById(R.id.tv_name);
            TextView tvMsg= itemView.findViewById(R.id.tv_msg);
            tvName.setText(item.getDr_name());
            tvMsg.setText(item.getDr_description());
            Glide.with(itemView).load(R.drawable.ic_patient).into(iv);
        }else {
            itemView= layoutInflater.inflate(R.layout.chatting_res,viewGroup,false);

            //만들어진 itemView에 값들 설정
            ImageView iv= itemView.findViewById(R.id.iv);
            TextView tvName= itemView.findViewById(R.id.tv_name);
            TextView tvMsg= itemView.findViewById(R.id.tv_msg);
            tvName.setText(item.getDr_name());
            tvMsg.setText(item.getDr_description());
            Glide.with(itemView).load(R.drawable.ic_applogo_doctor).into(iv);
        }


        return itemView;
    }
}
