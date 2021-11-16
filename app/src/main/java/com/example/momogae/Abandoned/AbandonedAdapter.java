package com.example.momogae.Abandoned;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.momogae.R;

import java.util.ArrayList;

public class AbandonedAdapter extends RecyclerView.Adapter<AbandonedAdapter.MyViewHolder> {

    private ArrayList<AbandonedModel> mList;
    private LayoutInflater mInflate;
    private Context mContext;

    public AbandonedAdapter(Context context, ArrayList<AbandonedModel> items) {
        this.mList = items;
        this.mInflate = LayoutInflater.from(context);
        this.mContext = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //뷰 홀더와 레이아웃 파일을 연결해주는 역할
        View view = mInflate.inflate(R.layout.item_abandoned, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { //데이터를 뷰홀더에 바인딩 해주는 부분
        Glide.with(mContext) //popfile은 유기동물 사진이므로 glide를 통해 이미지를 홀더에 넣음
                .load(mList.get(position).popfile)
                .into(holder.popfile);

        holder.kindCd.setText("품종 : "+mList.get(position).kindCd);
        holder.sexCd.setText("성별 : "+mList.get(position).sexCd);
        holder.specialMark.setText("특징 : "+mList.get(position).specialMark);
        holder.careNm.setText("보호소 이름 : "+mList.get(position).careNm);
        holder.careTel.setText("보호소 번호 : "+mList.get(position).careTel);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView popfile;
        public TextView kindCd;
        public TextView sexCd;
        public TextView specialMark;
        public TextView careNm;
        public TextView careTel;


        public MyViewHolder(View itemView) {
            super(itemView);

            popfile = itemView.findViewById(R.id.popfile);
            kindCd = itemView.findViewById(R.id.kindCd);
            sexCd = itemView.findViewById(R.id.sexCd);
            specialMark = itemView.findViewById(R.id.specialMark);
            careNm = itemView.findViewById(R.id.careNm);
            careTel = itemView.findViewById(R.id.careTel);


        }
    }

}
