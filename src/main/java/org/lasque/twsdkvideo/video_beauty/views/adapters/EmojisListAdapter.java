package org.lasque.twsdkvideo.video_beauty.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.views.SquareImageView;

/**
 * 贴纸adpater
 */
public class EmojisListAdapter extends RecyclerView.Adapter<EmojisListAdapter.EmojisListViewHolder> {
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


    private int[] images = {

            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,
            R.drawable.sticker_emoji_one,R.drawable.sticker_emoji_two, R.drawable.sticker_emoji_three, R.drawable.sticker_emoji_four, R.drawable.sticker_emoji_five,



    };

    public EmojisListAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public EmojisListAdapter.EmojisListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.lsq_tile_recycle_item, parent, false);
        
        return new EmojisListAdapter.EmojisListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojisListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mImage.setImageResource(images[position]);
        FrameLayout.LayoutParams layoutParams   = (FrameLayout.LayoutParams) holder.mImage.getLayoutParams();
//        if(position<=3){//如果在第一行
//            layoutParams.setMargins(0, TuSdkContext.dip2px(24),0,0);
//        }else{
//            layoutParams.setMargins(0,TuSdkContext.dip2px(16),0,0);
//        }
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

    class EmojisListViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView mImage;

        public EmojisListViewHolder(View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.lsq_tile_image);
        }
    }
}
