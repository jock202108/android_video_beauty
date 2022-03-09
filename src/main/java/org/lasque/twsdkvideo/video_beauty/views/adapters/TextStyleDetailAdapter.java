package org.lasque.twsdkvideo.video_beauty.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.TextStyleBean;
import org.lasque.twsdkvideo.video_beauty.data.TextStyleDetailBean;
import org.lasque.twsdkvideo.video_beauty.views.CircleRelativeLayout;

import java.util.ArrayList;

/*------ 样式详情Adapter********/
public class TextStyleDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<TextStyleDetailBean> mDatas;
    public OnStyleDetailItemClickListener listener;


    public TextStyleDetailAdapter(Context context) {
        mContext = context;

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setDate(ArrayList<TextStyleDetailBean> data) {
        this.mDatas = data;
       notifyDataSetChanged();
    }


    public void setCurrentPosition(int position) {
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).setSelect(false);
        }
        if(position != -1){
            mDatas.get(position).setSelect(true);
        }
        notifyDataSetChanged();
    }

    public void setCurrentClickItem(int position) {
      boolean isSelect =   mDatas.get(position).isSelect();
        mDatas.get(position).setSelect(!isSelect);
        notifyDataSetChanged();
    }

    public void setListener(OnStyleDetailItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_child_style, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TextStyleDetailAdapter.NormalHolder normalHolder = (TextStyleDetailAdapter.NormalHolder) holder;
        TextStyleDetailBean textStyleDetailBean = mDatas.get(position);
        // 如果是颜色,背景和描边
        if(textStyleDetailBean.getParentStyleKind().equals(mContext.getString(R.string.color)) || textStyleDetailBean.getParentStyleKind().equals(mContext.getString(R.string.background))|| textStyleDetailBean.getParentStyleKind().equals(mContext.getString(R.string.stroke))){
            normalHolder.mRvStorkStyle.setVisibility(View.VISIBLE);
            normalHolder.mImageView.setVisibility(View.GONE);
            normalHolder.mRvStorkStyle.setSelected(textStyleDetailBean.isSelect());
            int padding = textStyleDetailBean.isSelect()?TuSdkContext.dip2px(3.8f): TuSdkContext.dip2px(1.2f);
            normalHolder.mRvStorkStyle.setPadding(padding,padding,padding,padding);
            normalHolder.mCircleRelativeLayout.setColor(textStyleDetailBean.getColor());
        }else {
            normalHolder.mRvStorkStyle.setVisibility(View.GONE);
            normalHolder.mImageView.setVisibility(View.VISIBLE);
            normalHolder.mImageView.setImageResource(textStyleDetailBean.isSelect() ? textStyleDetailBean.getSelectImageId() : textStyleDetailBean.getUnSelectImageId());
        }


        normalHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(mDatas,position,textStyleDetailBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class NormalHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public RelativeLayout mRvStorkStyle;
        public CircleRelativeLayout mCircleRelativeLayout;

        public NormalHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_style);
            mRvStorkStyle = itemView.findViewById(R.id.stork_style);
            mCircleRelativeLayout = itemView.findViewById(R.id.solid_style);
        }

    }
}
