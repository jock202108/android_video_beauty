package org.lasque.twsdkvideo.video_beauty.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.SearchBean;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<SearchBean> mDatas;
    public OnItemClickListener listener;



    public SearchAdapter(Context context) {
        mContext = context;
        
    }

    public void setDate(ArrayList<SearchBean> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
        normalHolder.mSearchResult.setText(mDatas.get(position).getContent());
        normalHolder.ivEnter.setSelected(mDatas.get(position).isHistory());
        normalHolder.ivEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null && v.isSelected()){
                    listener.onSelectedSound(position);
                }
            }
        });
        normalHolder.itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(position);
                }
            }
        });
     
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        public TextView mSearchResult;
        public RelativeLayout itemSearch;
        public ImageView ivEnter;


        public NormalHolder(View itemView) {
            super(itemView);
            mSearchResult = itemView.findViewById(R.id.tv_search_result);
            itemSearch = itemView.findViewById(R.id.item_search);
            ivEnter = itemView.findViewById(R.id.iv_enter);
        }

    }
}
