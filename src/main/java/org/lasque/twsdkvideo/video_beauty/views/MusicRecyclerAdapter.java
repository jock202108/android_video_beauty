package org.lasque.twsdkvideo.video_beauty.views;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.data.BackgroundMusicBean;
import org.quanqi.circularprogress.CircularProgressView;

import java.util.ArrayList;
import java.util.List;

/**
 * 滤镜适配器
 *
 * @author xujie
 * @Date 2018/9/18
 */

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder> {

    private List<BackgroundMusicBean> musicBeans;
    private long mCurrentVideoTotalTimeUs;
    // 当前选中的位置
    //  private int mCurrentPosition = -1;

    public interface ItemClickListener {
        void onItemClick(String musicCode, int position);
    }

    public ItemClickListener listener;
    public ItemClickListener trimClickListener;
    private Context mContext;

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setItemTrimClickListener(ItemClickListener listener) {
        this.trimClickListener = listener;
    }

    public MusicRecyclerAdapter(Context context, long currentVideoTotalTimeUs) {
        super();
        mContext = context;
        mCurrentVideoTotalTimeUs = currentVideoTotalTimeUs;
        musicBeans = new ArrayList<>();
    }

    public void setMusicList(List<BackgroundMusicBean> musicList) {
        this.musicBeans = musicList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        for (int i = 0; i < musicBeans.size(); i++) {
            musicBeans.get(i).setSelect(false);
        }
        if (position != -1) {
            musicBeans.get(position).setSelect(true);
        }

        notifyDataSetChanged();
    }

    public void setCurrentBeanNotSelect(int position) {
        musicBeans.get(position).setSelect(false);
        musicBeans.get(position).setDownLoading(false);
        notifyDataSetChanged();
    }

    public List<BackgroundMusicBean> getMusicList() {
        return this.musicBeans;
    }

    @Override
    public int getItemCount() {
        return musicBeans.size();
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_recycler_item_view, null);
        MusicViewHolder viewHolder = new MusicViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MusicViewHolder musicViewHolder, @SuppressLint("RecyclerView") final int position) {
        BackgroundMusicBean backgroundMusicBean = musicBeans.get(position);
        String musicCode = backgroundMusicBean.getTitle().toLowerCase();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) musicViewHolder.itemLayout.getLayoutParams();
        if (position == 0) {
            layoutParams.topMargin = TuSdkContext.dip2px(18);
        } else {
            layoutParams.topMargin = TuSdkContext.dip2px(8);
        }
        layoutParams.width = (int) VideoBeautyPlugin.screenWidth;
        long duration = backgroundMusicBean.getDuration();

        musicViewHolder.author.setText(backgroundMusicBean.getMusicAuthor() + "  ·  " + backgroundMusicBean.getDurationFormatStr());

        musicViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        if (backgroundMusicBean.isSelect()) {
            musicViewHolder.mTitleView.setVisibility(View.VISIBLE);
            musicViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
            if(backgroundMusicBean.isDownLoading()){
                musicViewHolder.lottieAnimationView.setVisibility(View.GONE);

            }else {
                musicViewHolder.lottieAnimationView.setVisibility(View.VISIBLE);
                musicViewHolder.lottieAnimationView.playAnimation();
            }

            if(backgroundMusicBean.getLocalPath() != null && !backgroundMusicBean.getLocalPath().equals("")){
                musicViewHolder.trimFl.setVisibility(mCurrentVideoTotalTimeUs < duration?View.VISIBLE:View.GONE);
            }else {
                musicViewHolder.trimFl.setVisibility(View.GONE);
            }

        }
        // 显示正常布局
        else {
            musicViewHolder.mTitleView.setVisibility(View.VISIBLE);
            musicViewHolder.mSelectLayout.setVisibility(View.GONE);
            musicViewHolder.lottieAnimationView.setVisibility(View.GONE);
            musicViewHolder.lottieAnimationView.pauseAnimation();
            musicViewHolder.trimFl.setVisibility(View.GONE);

        }
        if (backgroundMusicBean.isSelect()) {
            musicViewHolder.mTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            musicViewHolder.mTitleView.setMarqueeRepeatLimit(-1);
        } else {
            musicViewHolder.mTitleView.setEllipsize(TextUtils.TruncateAt.END);
        }
        musicViewHolder.mTitleView.setText(backgroundMusicBean.getTitle());
        musicViewHolder.mItemImage.setPadding(0, 0, 0, 0);
        // 如果是网络图片就加载网络图片
        Glide.with(mContext).load(backgroundMusicBean.getImageUrl()).into(musicViewHolder.mItemImage);
        // 如果正在下载就加载loading圈

        if (backgroundMusicBean.isDownLoading()) {
            Animation myAlphaAnimation= AnimationUtils.loadAnimation(mContext, R.anim.loading);
            myAlphaAnimation.setInterpolator(new LinearInterpolator());
            musicViewHolder.mCircularProgressView.startAnimation(myAlphaAnimation);
            musicViewHolder.mCircularProgressView.setVisibility(View.VISIBLE);
        } else {
            musicViewHolder.mCircularProgressView.setVisibility(View.GONE);
        }



        // 反馈点击
        final String finalMusicCode = musicCode;
        musicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(getAudioPrefix() + finalMusicCode, position);
            }
        });
        musicViewHolder.trimFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trimClickListener.onItemClick(getAudioPrefix() + finalMusicCode, position);
            }
        });
        musicViewHolder.itemView.setTag(position);
    }

    /**
     * 缩略图前缀
     *
     * @return
     */
    protected String getThumbPrefix() {
        return "lsq_mixing_thumb_";
    }

    /**
     * Item名称前缀
     *
     * @return
     */
    protected String getTextPrefix() {
        return "lsq_mixing_";
    }

    private String getAudioPrefix() {
        return "lsq_audio_";
    }

    ;

    class MusicViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public FrameLayout mSelectLayout;
        public TextView author;
        public FrameLayout mImageLayout;
        public View trimFl;
        public ImageView mCircularProgressView;
        public LottieAnimationView lottieAnimationView;
        public ConstraintLayout itemLayout;

        public MusicViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.lsq_item_title);
            mItemImage = itemView.findViewById(R.id.lsq_item_image);
            mSelectLayout = itemView.findViewById(R.id.lsq_select_layout);
            mImageLayout = itemView.findViewById(R.id.lsq_image_layout);
            mCircularProgressView = itemView.findViewById(R.id.progress_view);
            lottieAnimationView = itemView.findViewById(R.id.lottie_likeanim);
            itemLayout = itemView.findViewById(R.id.item_layout);
            author = itemView.findViewById(R.id.author);
            trimFl = itemView.findViewById(R.id.trimIv);
            mItemImage.setCornerRadiusDP(8);
        }
    }
}
