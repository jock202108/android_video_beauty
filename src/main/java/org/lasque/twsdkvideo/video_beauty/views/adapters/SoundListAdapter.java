package org.lasque.twsdkvideo.video_beauty.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.video.editor.TuSdkMediaSkinFaceEffect;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;
import org.lasque.twsdkvideo.video_beauty.views.BeautyRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.adapters.OnItemClickListener;

import java.util.ArrayList;



public class SoundListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<SoundBean> mDatas;
    public OnItemClickListener listener;



    public SoundListAdapter(Context context) {
        mContext = context;
        
    }

    public void setDate(ArrayList<SoundBean> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_sound, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
       SoundBean bean =  mDatas.get(position);
        normalHolder.mSoundLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
        Glide.with(mContext).load(bean.getSoundPic()).into(normalHolder.mSoundImage);
        normalHolder.mSoundImage.setCornerRadius(8);
        normalHolder. mSoundTitle.setText(bean.getSoundTitle());
        normalHolder. mSoundContent.setText(bean.getSoundContent());
        normalHolder. mSoundTime.setText(bean.getDurationFormatStr());
       int resId = bean.isCollect()?R.drawable.ic_collect:R.drawable.ic_un_collect;
        normalHolder. mCollectImage.setImageResource(resId);
        int playId= bean.isPlaying()?R.drawable.ic_playing:R.drawable.ic_play;
        normalHolder. mPlayImage.setImageResource(playId);
        normalHolder.mUserSoundImage.setVisibility(bean.isPlaying()?View.VISIBLE:View.GONE);
        normalHolder.mRlUseSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectedSound(position);
            }
        });
        if(bean.isPlaying()){
            normalHolder.mPlayImage.setVisibility(View.GONE);
            if(bean.isLoading()){
                normalHolder.mLoadingImage.setVisibility(View.VISIBLE);
                Animation myAlphaAnimation= AnimationUtils.loadAnimation(mContext, R.anim.loading);
                myAlphaAnimation.setInterpolator(new LinearInterpolator());
                normalHolder.mLoadingImage.startAnimation(myAlphaAnimation);
                normalHolder.lottieAnimationView.setVisibility(View.GONE);
            }else {
                normalHolder.mLoadingImage.setVisibility(View.GONE);
                normalHolder.lottieAnimationView.setVisibility(View.VISIBLE);
                normalHolder.lottieAnimationView.playAnimation();
            }


    }else {
        normalHolder.mPlayImage.setVisibility(View.VISIBLE);
            normalHolder.mLoadingImage.setVisibility(View.GONE);
        normalHolder.lottieAnimationView.setVisibility(View.GONE);
        normalHolder.lottieAnimationView.pauseAnimation();
    }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mSoundLl;
        public TuSdkImageView mSoundImage;
        public TextView mSoundTitle;
        public TextView mSoundContent;
        public TextView mSoundTime;
        public ImageView mCollectImage;
        public ImageView mPlayImage;
        public ImageView mUserSoundImage;
        public ImageView mLoadingImage;
        public LottieAnimationView lottieAnimationView;
        public RelativeLayout mRlUseSound;


        public NormalHolder(View itemView) {
            super(itemView);
            mSoundLl = itemView.findViewById(R.id.sound_ll);
            mSoundImage = itemView.findViewById(R.id.img_sound);
            mSoundTitle = itemView.findViewById(R.id.tv_title);
            mSoundContent = itemView.findViewById(R.id.tv_content);
            mSoundTime = itemView.findViewById(R.id.tv_time);
            mCollectImage = itemView.findViewById(R.id.img_collect);
            mPlayImage = itemView.findViewById(R.id.play_image);
            mUserSoundImage = itemView.findViewById(R.id.img_use_sound);
            lottieAnimationView = itemView.findViewById(R.id.lottie_likeanim);
            mRlUseSound = itemView.findViewById(R.id.rl_use_sound);
            mLoadingImage = itemView.findViewById(R.id.progress_view);

        }

    }
}
