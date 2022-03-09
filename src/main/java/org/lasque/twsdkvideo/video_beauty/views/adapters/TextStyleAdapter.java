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

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.SearchBean;
import org.lasque.twsdkvideo.video_beauty.data.TextStyleBean;

import java.util.ArrayList;

/*******主样式********/
public class TextStyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<TextStyleBean> mDatas;
    public OnItemClickListener listener;


    public TextStyleAdapter(Context context) {
        mContext = context;

    }

    public void setDate(ArrayList<TextStyleBean> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }


    public void setCurrentPosition(int position) {
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setSelect(false);
        }
        mDatas.get(position).setSelect(true);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_text_style, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TextStyleAdapter.NormalHolder normalHolder = (TextStyleAdapter.NormalHolder) holder;
        TextStyleBean textStyleBean = mDatas.get(position);
        normalHolder.mTvTextStyle.setText(textStyleBean.getStyleName());
        normalHolder.mRlTextStyle.setSelected(textStyleBean.isSelect());
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) normalHolder.mRlTextStyle.getLayoutParams();
        if (position == 0) {
            layoutParams.leftMargin = TuSdkContext.dip2px(18);
        } else {
            layoutParams.leftMargin = TuSdkContext.dip2px(10);
        }
        normalHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class NormalHolder extends RecyclerView.ViewHolder {
        public TextView mTvTextStyle;
        public RelativeLayout mRlTextStyle;

        public NormalHolder(View itemView) {
            super(itemView);
            mTvTextStyle = itemView.findViewById(R.id.tv_text_style);
            mRlTextStyle = itemView.findViewById(R.id.rl_text_style);
        }

    }
}
