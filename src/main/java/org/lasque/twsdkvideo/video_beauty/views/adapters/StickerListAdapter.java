package org.lasque.twsdkvideo.video_beauty.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;
import org.lasque.twsdkvideo.video_beauty.views.SquareImageView;
import org.lasque.twsdkvideo.video_beauty.views.TileRecycleAdapter;

import java.util.ArrayList;

/**
 * 贴纸adpater
 */
public class StickerListAdapter extends RecyclerView.Adapter<StickerListAdapter.StickerViewHolder> {
    private Context mContext;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnClickListener {
        void onClick(View var1);
    }
    private  OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position,int resId);

    }

    /**
     * 贴纸图片
     **/
    private int[] images = {R.drawable.sticker_add_icon,R.drawable.sticker_10342, R.drawable.sticker_10344, R.drawable.sticker_10345, R.drawable.sticker_10346, R.drawable.sticker_10348, R.drawable.sticker_10347, R.drawable.sticker_10343, R.drawable.sticker_10341};


    public StickerListAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public StickerListAdapter.StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.lsq_tile_recycle_item, parent, false);





        return new StickerListAdapter.StickerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mImage.setImageResource(images[position]);
        FrameLayout.LayoutParams layoutParams   = (FrameLayout.LayoutParams) holder.mImage.getLayoutParams();
        if(position<=3){//如果在第一行
            layoutParams.setMargins(0,TuSdkContext.dip2px(24),0,0);
        }else{
            layoutParams.setMargins(0,TuSdkContext.dip2px(16),0,0);
        }
        holder.mImage.setLayoutParams(layoutParams);
        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position,images[position]);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return images.length;
    }

    class StickerViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView mImage;

        public StickerViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.lsq_tile_image);
        }
    }
}
