package org.lasque.twsdkvideo.video_beauty.editor.component;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioRenderEntry;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioEffects;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorAudioMixerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.BackgroundMusicBean;
import org.lasque.twsdkvideo.video_beauty.data.BackgroundMusicSelected;
import org.lasque.twsdkvideo.video_beauty.data.GVisionSoundBean;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.event.CloseSoundFragmentEvent;
import org.lasque.twsdkvideo.video_beauty.event.RecommendBgMusicEvent;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.utils.DownloadUtil;
import org.lasque.twsdkvideo.video_beauty.utils.FileUtils;
import org.lasque.twsdkvideo.video_beauty.utils.TimeUtils;
import org.lasque.twsdkvideo.video_beauty.views.CompoundConfigView;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewParams;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewSeekBar;
import org.lasque.twsdkvideo.video_beauty.views.MusicRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.EditorAnimator;
import org.lasque.twsdkvideo.video_beauty.views.editor.MusicTrimView;
import org.lasque.twsdkvideo.video_beauty.views.fragments.BaseFullBottomSheetFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:52
 * @Copright (c) 2018 tw. All rights reserved.
 * <p>
 * 音乐组件
 */
public class EditorMusicComponent extends EditorComponent {
    /**
     * 主音轨音量
     */
    private float mMasterVolume = -1;
    /**
     * 次音轨音量
     */
    private float mOtherVolume = -1;
    /**
     * 底部布局
     */
    private View mBottomView;
    private View volumeLayout;
    /**
     * 音乐列表
     */
    private RecyclerView mMusicRecycle;
    private View sureIcon;
    private View closePop;
    private View lsqMusicLayout;
    private View lsqVoiceVolumeConfigLayout;
    private View lsqTrimMusicLayout;
    /**
     * 音乐适配器
     */
    private MusicRecyclerAdapter mMusicAdapter;
    /**
     * 音量调节器
     */
    private CompoundConfigView mVolumeConfigView;

    /**
     * 音乐列表数据源
     */
    private ArrayList<BackgroundMusicBean> mDataList = new ArrayList<>();

    /**
     * 当前选中的位置
     */
    private int currentSelectIndex = -1;

    public static BackgroundMusicSelected backgroundMusicSelected;

    MediaPlayer mediaPlayer = new MediaPlayer();

    /**
     * 音频混合之后的回调
     **/
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioDecoderTask = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                if (MovieEditorController.mCurrentComponent instanceof EditorMusicComponent) {
                    // 启动视频预览
                    if (!getEditorPlayer().isPause()) {
                        getEditorPlayer().pausePreview();
                    }
                    getEditorPlayer().seekTimeUs((long) (getEditorPlayer().getTotalTimeUs() * getEditorController().getmCurrentLeftPercent()));

//                    getEditorPlayer().seekOutputTimeUs(0);
                    startPreview();
                }
            }

        }
    };


    private View doneText;
    private MusicTrimView musicTrimView;
    private TextView trimMusicBeginTv;
    private ImageButton trimClose;
    private ImageButton trimSure;


    private void setMasterVolume(float volume) {
        //如果拍摄已经混音了,强制为0
        if (AppConstants.shootBackgroundMusicBean != null) {
            mMasterVolume = 0f;
            getEditorAudioMixer().setMasterAudioTrack(0f);
            setSeekBarProgress(0, 0);
        } else {
            mMasterVolume = volume;
            getEditorAudioMixer().setMasterAudioTrack(volume);
            setSeekBarProgress(0, volume);
        }
    }

    private void setOtherVolume(float volume) {
//        // 如果当前没有选中
//        if (currentSelectIndex != -1) {
//            getEditorAudioMixer().setSecondAudioTrack(volume);
//            mOtherVolume = volume;
//            mediaPlayer.setVolume(volume,volume);
//            setSeekBarProgress(1, volume);
//        } else {
//            mOtherVolume = volume;
//            getEditorAudioMixer().setSecondAudioTrack(volume);
//            setSeekBarProgress(1, volume);
//            mediaPlayer.setVolume(volume,volume);
//        }
        getEditorAudioMixer().setSecondAudioTrack(volume);
        mOtherVolume = volume;
        mediaPlayer.setVolume(volume,volume);
        setSeekBarProgress(1, volume);
    }

    //播放器回调
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            long currentProgress = (long) (playbackTimeUs - getEditorController().getmCurrentLeftPercent() * totalTimeUs) / 1000;
            if (musicTrimView != null && !getEditorController().getActivity().isFinishing() && !getEditorController().getActivity().isDestroyed()) {
                musicTrimView.setProgress(currentProgress);
            }


        }
    };

    /**
     * 音量调节
     */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mSeekBarChangeDelegateLisntener = new ConfigViewSeekBar.ConfigSeekbarDelegate() {
        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg) {

            // 主音轨
            if (arg.getKey().equals("origin")) {
                setMasterVolume(arg.getPercentValue());
            }
            // 次音轨
            else if (arg.getKey().equals("dubbing")) {
                setOtherVolume(arg.getPercentValue());
            }
        }
    };
    private EditorHomeComponent homeComponent;

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorMusicComponent(MovieEditorController editorController, EditorAnimator mEditorAnimator, VideoContent mHolderView, boolean horizontalScreen, EditorHomeComponent homeComponent) {
        super(editorController);
        this.homeComponent = homeComponent;
        mComponentType = EditorComponentType.Music;
        //todo 是否需要除以1000
        mMusicAdapter = new MusicRecyclerAdapter(mHolderView.getContext(), (long) getEditorController().getCurrentTotalTimeUs() / 1000);
        getEditorAudioMixer().addTaskStateListener(mAudioDecoderTask);
        getVolumeConfigView();
    }

    public EditorMusicComponent setHeadAction() {
        return this;
    }


    /**
     * 获取音量调节栏
     */
    public CompoundConfigView getVolumeConfigView() {
        if (mVolumeConfigView == null) {
            mVolumeConfigView = getBottomView().findViewById(R.id.lsq_voice_volume_config_view1);
            mVolumeConfigView.setDelegate(mSeekBarChangeDelegateLisntener);

            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(TuSdkContext.getString("origin"), mMasterVolume);
            params.appendFloatArg(TuSdkContext.getString("dubbing"), mOtherVolume);
            mVolumeConfigView.setCompoundConfigView(params);
            mVolumeConfigView.showView(false);
        }
        return mVolumeConfigView;
    }

    // 从搜索页面返回编辑页面,如果有音乐暂停了,就继续播放
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void playMusic(CloseSoundFragmentEvent closeSoundFragmentEvent) {
        if(closeSoundFragmentEvent.type == 1 && currentSelectIndex != -1){
            BackgroundMusicBean backgroundMusicBean =  mDataList.get(currentSelectIndex);
            backgroundMusicBean.setSelect(false);
            itemClick(backgroundMusicBean.getMusicId(),currentSelectIndex);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectMusic(SelectSoundEvent selectSoundEvent) {
        if (selectSoundEvent.type == 1) {
            SoundBean soundBean = selectSoundEvent.soundBean;
            if(soundBean == null){
             mDataList.get(currentSelectIndex).setSelect(false);
                currentSelectIndex = -1;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                mMusicAdapter.notifyDataSetChanged();
            }else {
                BackgroundMusicBean bean = new BackgroundMusicBean();
                bean.setLocalPath(soundBean.getLocalPath());
                bean.setSoundUrl(soundBean.getSoundUrl());
                bean.setTitle(soundBean.getSoundTitle());
                bean.setImageUrl(soundBean.getSoundPic());
                bean.setMusicAuthor(soundBean.getSoundContent());
                bean.setDuration(soundBean.getDuration());
                bean.setMusicId(soundBean.getSoundsId());
                bean.setSelect(false);
                bean.setFromSearch(true);
                BackgroundMusicBean backgroundMusicBean = mDataList.get(0);
                /// 如果第一个是搜索音乐,就直接移除第一个
                if (backgroundMusicBean.isFromSearch()) {
                    mDataList.remove(0);
                }

                ///如果第一个不是搜索的,查看列表中有无同样的音乐,有的话就移除
                for (int i = 0; i < mDataList.size(); i++) {
                    BackgroundMusicBean bgBean = mDataList.get(i);
                    if (bean.getSoundUrl().equals(bgBean.getSoundUrl())) {
                        mDataList.remove(i);
                        break;
                    }
                }
                mDataList.add(0, bean);
                currentPosition = 0;
                currentSelectIndex = 0;
                itemClick(bean.getTitle(), currentSelectIndex);
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecommend(RecommendBgMusicEvent event) {
        setData(event.content);
    }

    void setData(String data) {
        if (data.contains("error")) {
            return;
        }
        Gson gson = new Gson();
        List<GVisionSoundBean> dataList = gson.fromJson(data, new TypeToken<List<GVisionSoundBean>>() {
        }.getType());

        for (int i = 0; i < dataList.size(); i++) {
            if (AppConstants.shootBackgroundMusicBean != null && AppConstants.shootBackgroundMusicBean.getMusicId().equals(dataList.get(i).getMusicId())) {
                continue;
            }
            BackgroundMusicBean bean = new BackgroundMusicBean();
            bean.setTitle(dataList.get(i).getMusicTitle());
            bean.setImageUrl(dataList.get(i).getMusicCoverUrl());
            bean.setSoundUrl(dataList.get(i).getMusicUrl());
            bean.setMusicAuthor(dataList.get(i).getMusicAuthor());
            bean.setDuration(dataList.get(i).getDuration());
            bean.setLocalPath("");
            bean.setFromSearch(false);
            bean.setMusicId(dataList.get(i).getMusicId());
            mDataList.add(bean);
        }
        mMusicAdapter.notifyDataSetChanged();
    }

    public void cleanMusic() {
        currentSelectIndex = -1;
        backgroundMusicSelected = null;
        if (AppConstants.shootBackgroundMusicBean != null) {
            if (mMusicAdapter.getMusicList() != null && mMusicAdapter.getMusicList().size() > 0) {
//                BackgroundMusicBean backgroundMusicBean = mMusicAdapter.getMusicList().get(0);
//                startMusic(backgroundMusicBean, 0);
                currentSelectIndex = -1;
                mMusicAdapter.setCurrentPosition(-1);
                float trunkVolume = ((TuSdkEditorAudioMixerImpl) getEditorAudioMixer()).getMixerAudioRender().getTrunkVolume();
                setMasterVolume(0f);
                setOtherVolume(trunkVolume);
            }
        } else {
            //float trunkVolume = ((TuSdkEditorAudioMixerImpl) getEditorAudioMixer()).getMixerAudioRender().getTrunkVolume();
            currentSelectIndex = -1;
            mMusicAdapter.setCurrentPosition(-1);
            setMasterVolume(0.5f);
            setOtherVolume(0);
            homeComponent.setSoundName(getEditorController().getActivity().getString(R.string.add_sound));

        }
    }


    @Override
    public void attach() {
        EventBus.getDefault().register(this);
        // 再次进入把当前选中的移到第一位
        if (currentSelectIndex != -1 && !mDataList.isEmpty()) {
            BackgroundMusicBean backgroundMusicBean = mDataList.get(currentSelectIndex);
            int index = mDataList.indexOf(backgroundMusicBean);
            Collections.swap(mDataList, index, 0);
            currentSelectIndex = 0;
            // startMusic(backgroundMusicBean, 0);
            for (int i = 0; i < mDataList.size(); i++) {
                mDataList.get(i).setSelect(false);
            }
            // pausePreview();
            cancelBackMusic(false);
            if(mMusicRecycle!= null){
                mMusicRecycle.scrollToPosition(0);
            }
            itemClick(backgroundMusicBean.getMusicId(), currentSelectIndex);

        }
        getEditorController().getBottomView().addView(getBottomView());
        startPreview();
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setClickable(true);
        getEditorController().getPlayBtn().setOnClickListener(mOnClickListener);
        // 如果已经保存过特效
        if (backgroundMusicSelected != null) {
            mVolumeConfigView.showView(true);
            mMusicAdapter.setCurrentPosition(currentSelectIndex);
        } else {// 如果没有保存过特效，用次音轨代替主音轨
            if (AppConstants.shootBackgroundMusicBean != null) {
                if (mMusicAdapter.getMusicList() != null && mMusicAdapter.getMusicList().size() > 0) {
                    BackgroundMusicBean backgroundMusicBean = mMusicAdapter.getMusicList().get(0);
                    //    startMusic(backgroundMusicBean, 0);
                    cancelBackMusic(false);
                    itemClick(backgroundMusicBean.getMusicId(), 0);
                    currentSelectIndex = 0;
                   // float trunkVolume = ((TuSdkEditorAudioMixerImpl) getEditorAudioMixer()).getMixerAudioRender().getTrunkVolume();
                    setMasterVolume(0f);
                    setOtherVolume(mMementoOtherVolume);
                }
            } else {
                //float trunkVolume = ((TuSdkEditorAudioMixerImpl) getEditorAudioMixer()).getMixerAudioRender().getTrunkVolume();
                setMasterVolume(0.5f);
                setOtherVolume(0.5f);
            }
        }
        getEditorPlayer().addProgressListener(mPlayProgressListener);
    }

    @Override
    public void detach() {
        EventBus.getDefault().unregister(this);
        mVolumeConfigView.showView(false);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getPlayBtn().setClickable(false);
    }




    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.to.flutter");
        intent.putExtra("type", 5);
        intent.putExtra("pageNum", 1);
        intent.putExtra("state", 0);
        Objects.requireNonNull(getEditorController().getActivity()).sendBroadcast(intent);
    }


    @Override
    public View getHeaderView() {
        return null;
    }

    private void cancelBackMusicNoRefresh(){
        previewStartMusicTime = 0;
        currentStartMusicTime = 0;
        if (EditorVoiceoverComponent.mMementoVoiceList.size() == 0) {
            getEditorController().getMovieEditor().getEditorMixer().clearAllAudioData();
        } else {
            //混音数据
            LinkedList<TuSDKAudioRenderEntry> audioRenderEntryLinkedList = new LinkedList();
            //添加录音的数据
            for (int i = 0; i < EditorVoiceoverComponent.mMementoVoiceList.size(); i++) {
                audioRenderEntryLinkedList.add(EditorVoiceoverComponent.mMementoVoiceList.get(i).getTuSDKAudioRenderEntry());
            }
            getEditorController().getMovieEditor().getEditorMixer().setAudioRenderEntryList(audioRenderEntryLinkedList);
        }
        getEditorController().getMovieEditor().getEditorMixer().loadAudio();
    }

    private void cancelBackMusic(boolean isBack) {
        previewStartMusicTime = 0;
        currentStartMusicTime = 0;
        /**
         * 只添加录音的
         */
        if (EditorVoiceoverComponent.mMementoVoiceList.size() == 0) {
            getEditorController().getMovieEditor().getEditorMixer().clearAllAudioData();
        } else {
            //混音数据
            LinkedList<TuSDKAudioRenderEntry> audioRenderEntryLinkedList = new LinkedList();
            //添加录音的数据
            for (int i = 0; i < EditorVoiceoverComponent.mMementoVoiceList.size(); i++) {
                audioRenderEntryLinkedList.add(EditorVoiceoverComponent.mMementoVoiceList.get(i).getTuSDKAudioRenderEntry());
            }
            getEditorController().getMovieEditor().getEditorMixer().setAudioRenderEntryList(audioRenderEntryLinkedList);
        }
        getEditorController().getMovieEditor().getEditorMixer().loadAudio();
        mMusicAdapter.setCurrentPosition(-1);
        mSelectIndex = -1;
        backgroundMusicSelected = null;
        if (isBack) {
            getEditorController().onBackEvent();
        }
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            if (mDataList.size() == 0) {
                sendBroadcast();
            }
            mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_music_bottom, null);
            mMusicRecycle = mBottomView.findViewById(R.id.music_recycler);
            doneText = mBottomView.findViewById(R.id.done_text);
            doneText.setOnClickListener(mOnClickListener);
            View videoView = mBottomView.findViewById(R.id.video_view);
            videoView.setOnClickListener(mOnClickListener);
            LinearLayout.LayoutParams doneTextLayoutParams = (LinearLayout.LayoutParams) doneText.getLayoutParams();
            doneTextLayoutParams.topMargin = VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(12);
            doneText.setLayoutParams(doneTextLayoutParams);

            lsqMusicLayout = mBottomView.findViewById(R.id.lsq_music_layout);

            lsqVoiceVolumeConfigLayout = mBottomView.findViewById(R.id.lsq_voice_volume_config_layout);

            lsqTrimMusicLayout = mBottomView.findViewById(R.id.trim_music_item_layout);
            trimMusicBeginTv = mBottomView.findViewById(R.id.trim_music_begin_time);
            musicTrimView = mBottomView.findViewById(R.id.trim_music_trim_view);
            trimClose = mBottomView.findViewById(R.id.trim_close);
            trimSure = mBottomView.findViewById(R.id.trim_sure);
            trimClose.setOnClickListener(mOnClickListener);
            trimSure.setOnClickListener(mOnClickListener);

            sureIcon = mBottomView.findViewById(R.id.sure_icon);
            closePop = mBottomView.findViewById(R.id.close_pop);
            closePop.setOnClickListener(mOnClickListener);
            sureIcon.setOnClickListener(mOnClickListener);
            mBottomView.findViewById(R.id.search_icon).setOnClickListener(mOnClickListener);
            mBottomView.findViewById(R.id.search_sounds_text).setOnClickListener(mOnClickListener);
            volumeLayout = mBottomView.findViewById(R.id.volume_layout);
            volumeLayout.setOnClickListener(mOnClickListener);
            mMusicRecycle.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.VERTICAL, false));
            if (AppConstants.shootBackgroundMusicBean != null) {
                mDataList.add(AppConstants.shootBackgroundMusicBean);
            }

            mMusicAdapter.setMusicList(mDataList);
            mMusicAdapter.setItemClickListener(mOnItemClickListener);
            mMusicAdapter.setItemTrimClickListener(mOnItemTrimClickListener);
            mMusicRecycle.setAdapter(mMusicAdapter);
        } else {
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {

    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lsq_play_btn) {
                if (getEditorPlayer().isPause()) {
                    startPreview();
                }
            } else if (id == R.id.search_icon || id == R.id.search_sounds_text) {
                pausePreview();
                stopMusic();
                String currentMusicName ="";
                if(currentSelectIndex != -1){
                   currentMusicName =  mDataList.get(currentSelectIndex).getTitle();
                }
                BaseFullBottomSheetFragment.getInstance(1, currentMusicName).show(getEditorController().getActivity().getSupportFragmentManager(), "dialog");
            } else if (id == R.id.volume_layout) {
                previousMasterVolume = mMasterVolume;
                previousOtherVolume = mOtherVolume;
                changeLayout(MusicLayoutType.volumeLayout);
            } else if (id == R.id.sure_icon) {
                changeLayout(MusicLayoutType.musicListLayout);
            } else if (id == R.id.close_pop) {
                //恢复上次音轨音量
                setMasterVolume(previousMasterVolume);
                setOtherVolume(previousOtherVolume);
                ConfigViewParams params = new ConfigViewParams();
                params.appendFloatArg(TuSdkContext.getString("origin"), mMasterVolume);
                params.appendFloatArg(TuSdkContext.getString("dubbing"), mOtherVolume);
                mVolumeConfigView.setCompoundConfigView(params);
                changeLayout(MusicLayoutType.musicListLayout);

            } else if (id == R.id.done_text || id == R.id.video_view) {
                if (mVolumeConfigView.isShown() || musicTrimView.isShown()) {
                    return;
                }
                stopMusic();
                if (currentSelectIndex != -1) {
                    startMusic(mDataList.get(currentSelectIndex), currentSelectIndex);

                } else {
                    cancelBackMusicNoRefresh();
                    currentSelectIndex = -1;
                    homeComponent.setSoundName(getEditorController().getActivity().getString(R.string.add_sound));
                }
                getVolumeConfigView().showView(false);
                if (backgroundMusicSelected != null) {
                    mMementoOtherVolume = mOtherVolume;
                } else {
                    mMementoOtherVolume = mOtherVolume;
                }
                mMementoEffectIndex = mSelectIndex;
                //这是主音轨的音量
                if (AppConstants.shootBackgroundMusicBean != null) {
                    getEditorAudioMixer().setMasterAudioTrack(0f);
                } else {
                    getEditorAudioMixer().setMasterAudioTrack(mMasterVolume);
                }
                getEditorController().onBackEvent();
            } else if (id == R.id.trim_close) {

//                startPreview();
                changeAudioEffect(currentPosition, previewStartMusicTime);
                changeLayout(MusicLayoutType.musicListLayout);
            } else if (id == R.id.trim_sure) {
                changeAudioEffect(currentPosition, currentStartMusicTime);
                previewStartMusicTime = currentStartMusicTime;
                changeLayout(MusicLayoutType.musicListLayout);
            }
        }


    };

    private float previousMasterVolume = 0;

    private float previousOtherVolume = 0;

    enum MusicLayoutType {
        volumeLayout,
        musicListLayout,
        trimMusicLayout
    }

    private void changeLayout(MusicLayoutType type) {
        switch (type) {
            case volumeLayout:
                doneText.setVisibility(View.INVISIBLE);
                volumeLayout.setVisibility(View.INVISIBLE);
                lsqMusicLayout.setVisibility(View.GONE);
                lsqVoiceVolumeConfigLayout.setVisibility(View.VISIBLE);
                lsqTrimMusicLayout.setVisibility(View.GONE);
                mVolumeConfigView.showView(true);
                break;
            case musicListLayout:
                doneText.setVisibility(View.VISIBLE);
                volumeLayout.setVisibility(View.VISIBLE);
                lsqMusicLayout.setVisibility(View.VISIBLE);
                lsqVoiceVolumeConfigLayout.setVisibility(View.GONE);
                lsqTrimMusicLayout.setVisibility(View.GONE);
                mVolumeConfigView.showView(false);
                break;
            case trimMusicLayout:
                lsqMusicLayout.setVisibility(View.GONE);
                lsqVoiceVolumeConfigLayout.setVisibility(View.GONE);
                lsqTrimMusicLayout.setVisibility(View.VISIBLE);
                mVolumeConfigView.showView(false);

                break;
            default:

                break;
        }
    }

    private MusicRecyclerAdapter.ItemClickListener mOnItemClickListener = new MusicRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(final String musicCode, final int position) {
//            BackgroundMusicBean backgroundMusicBean = mDataList.get(position);
//            // 点击重置状态
//            if (backgroundMusicBean.isSelect()) {
//                mMusicAdapter.setCurrentBeanNotSelect(position);
//                pausePreview();
//                cancelBackMusic(false);
//                currentSelectIndex = -1;
//                homeComponent.setSoundName(getEditorController().getActivity().getString(R.string.add_sound));
//
//            } else {// 选中选中
//                startMusic(backgroundMusicBean, position);
//            }
            itemClick(musicCode, position);
        }
    };

    // 点击下载音乐条目
    private void itemClick(String musicCode, int position) {
        for (int i = 0; i < mDataList.size(); i++) {
           // mDataList.get(i).setSelect(false);
            mDataList.get(i).setDownLoading(false);
        }
        mMusicAdapter.notifyDataSetChanged();
        BackgroundMusicBean bean = mDataList.get(position);
        try {
            /// 如果正在播放
            if (bean.isSelect()) {
                bean.setSelect(false);
                currentSelectIndex = -1;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
            } else {
                currentSelectIndex = position;
                cancelBackMusic(false);
                for (int i = 0; i < mDataList.size(); i++) {
                    mDataList.get(i).setSelect(false);
                    mDataList.get(i).setDownLoading(false);
                }
                mDataList.get(position).setSelect(true);
                playOnlineSound(mDataList.get(position), position);
            }

            mMusicAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 点击在线播放
    private void playOnlineSound(BackgroundMusicBean bean, int position) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
            } else {
                mediaPlayer = new MediaPlayer();
            }
            String[] str = bean.getSoundUrl().split("/");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length; i++) {
                if (i == str.length - 1) {
                    sb.append(str[i]);
                }
            }
            String musicName = sb.toString();
            String dir = DownloadUtil.isExistDir(getEditorController().getActivity(), "download");
            File downloadFile = new File(dir, musicName);
            // 本地存在
            if (downloadFile.exists() && downloadFile.isFile() && FileUtils.fileIsUsable(getEditorController().getActivity(), downloadFile)) {
                mediaPlayer.setDataSource(downloadFile.getAbsolutePath());
            }else {
                bean.setDownLoading(true);
                mMusicAdapter.notifyDataSetChanged();
                mediaPlayer.setDataSource(bean.getSoundUrl());
            }

            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    bean.setDownLoading(false);
                    mMusicAdapter.notifyDataSetChanged();
                    backgroundDownLoadMusic(bean, position);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {


                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    // 后台下载音乐
    private void backgroundDownLoadMusic(BackgroundMusicBean bean, int position) {

        String[] str = bean.getSoundUrl().split("/");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            if (i == str.length - 1) {
                sb.append(str[i]);
            }
        }
        String musicName = sb.toString();
        try {
            String dir = DownloadUtil.isExistDir(getEditorController().getActivity(), "download");
            File downloadFile = new File(dir, musicName);
            // 本地存在
            if (downloadFile.exists() && downloadFile.isFile() && FileUtils.fileIsUsable(getEditorController().getActivity(), downloadFile)) {
                bean.setLocalPath(downloadFile.getAbsolutePath());
                mMusicAdapter.notifyDataSetChanged();
                return;
            }
            // 本地不存在
            else {
                DownloadUtil.get(getEditorController().getActivity()).download(bean.getSoundUrl(), "download", musicName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(String localPath) {
                        getEditorController().getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (FileUtils.fileIsUsable(getEditorController().getActivity(), downloadFile)) {
                                    bean.setLocalPath(downloadFile.getAbsolutePath());
                                    mMusicAdapter.notifyDataSetChanged();
                                } 
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 停止播放
    public   void  stopMusic(){
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }
        }
    }

    // 开始下载并且混音
    private void startMusic(BackgroundMusicBean bean, int position) {
        if (!AppConstants.callList.isEmpty()) {
            for (int i = 0; i < AppConstants.callList.size(); i++) {
                AppConstants.callList.get(i).cancel();
            }
            AppConstants.callList.clear();
        }
        String[] str = bean.getSoundUrl().split("/");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            if (i == str.length - 1) {
                sb.append(str[i]);
            }
        }
        String musicName = sb.toString();
        try {
            String dir = DownloadUtil.isExistDir(getEditorController().getActivity(), "download");
            File downloadFile = new File(dir, musicName);
            // 本地存在
            if (downloadFile.exists() && downloadFile.isFile() && FileUtils.fileIsUsable(getEditorController().getActivity(), downloadFile)) {
                bean.setLocalPath(downloadFile.getAbsolutePath());
                setItemOnClick(bean.getTitle(), position);
            }
            // 本地不存在
            else {
                bean.setDownLoading(true);
                mMusicAdapter.notifyDataSetChanged();
                DownloadUtil.get(getEditorController().getActivity()).download(bean.getSoundUrl(), "download", musicName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(String localPath) {
                        getEditorController().getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bean.setDownLoading(false);
                                if (FileUtils.fileIsUsable(getEditorController().getActivity(), downloadFile)) {
                                    bean.setLocalPath(downloadFile.getAbsolutePath());
                                    mMusicAdapter.setCurrentPosition(position);
                                    // todo 第一次加载慢
                                    setItemOnClick(bean.getTitle(), position);
                                } else {
                                    mMusicAdapter.notifyDataSetChanged();
                                    Toast.makeText(getEditorController().getActivity(), getEditorController().getActivity().getResources().getString(R.string.download_fail), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {
                        getEditorController().getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        getEditorController().getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bean.setDownLoading(false);
                                mMusicAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    float currentLeftPercent = 0;
    private MusicRecyclerAdapter.ItemClickListener mOnItemTrimClickListener = new MusicRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(final String musicCode, final int position) {
            BackgroundMusicBean backgroundMusicBean = mDataList.get(position);
            changeLayout(MusicLayoutType.trimMusicLayout);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopMusic();
                    if (position != currentPosition || currentLeftPercent != getEditorController().getmCurrentLeftPercent()) {
                        previewStartMusicTime = 0;
                        currentStartMusicTime = 0;
                    }
                    musicTrimView.setOnStartProgressChangeListener(getOnStartProgressChangeListener(position));
                    musicTrimView.setDuration(backgroundMusicBean.getDuration(), (long) (getEditorController().getCurrentTotalTimeUs() / 1000), previewStartMusicTime);
//                    getEditorPlayer().seekTimeUs((long) (getEditorPlayer().getTotalTimeUs() * getEditorController().getmCurrentLeftPercent()));
                    currentPosition = position;
                }
            }, 10);

        }
    };

    long previewStartMusicTime = 0;
    long currentStartMusicTime = 0;
    int currentPosition = 0;


    @NonNull
    private MusicTrimView.OnStartProgressChangeListener getOnStartProgressChangeListener(int position) {
        return new MusicTrimView.OnStartProgressChangeListener() {
            @Override
            public void onChange(long start) {
                //需要测试视频的位置如何从裁剪标记位置开始混和音乐
                currentStartMusicTime = start;
                getEditorPlayer().seekTimeUs((long) (getEditorPlayer().getTotalTimeUs() * getEditorController().getmCurrentLeftPercent()));
//                startPreview();
                changeAudioEffect(position, start);
                String tips = String.format(getEditorController().getActivity().getString(R.string.lsq_begin_recording_form), TimeUtils.duration(start));
                trimMusicBeginTv.setText(tips);

            }
        };
    }


    /**
     * 条目点击(正常播放)
     */
    private void setItemOnClick(String musicCode, int position) {
        if (TuSdkViewHelper.isFastDoubleClick()) return;
        // 播放器重置到最开始
        homeComponent.setSoundName(musicCode);
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                // 应用配音特效
                if (position != currentPosition || currentLeftPercent != getEditorController().getmCurrentLeftPercent()) {
                    previewStartMusicTime = 0;
                    currentStartMusicTime = 0;
                    changeAudioEffect(position, 0);
                } else {
                    changeAudioEffect(currentPosition, previewStartMusicTime);
                }
                currentLeftPercent = getEditorController().getmCurrentLeftPercent();
            }
        });
    }

    /**
     * 应用背景音乐特效
     *
     * @param position
     */
    protected void changeAudioEffect(int position, long startTime) {
        mSelectIndex = position;
        currentSelectIndex = position;
        if (position >= 0) {
            applyAudioEffect(mDataList.get(position), true, startTime);
            if (AppConstants.shootBackgroundMusicBean != null) {
                getEditorAudioMixer().setMasterAudioTrack(0f);
            }
            setSeekBarProgress(0, mMasterVolume);
            if (mOtherVolume != 0f) {
                setSeekBarProgress(1, mOtherVolume);
            } else {
                setSeekBarProgress(1, 0.5f);
                getEditorAudioMixer().setSecondAudioTrack(0.5f);
            }
        }
    }

    /**
     * 设置配音音效
     *
     * @param
     */
    private void applyAudioEffect(BackgroundMusicBean backgroundMusicBean, boolean isLooping, long startTime) {
        //混音数据
        LinkedList<TuSDKAudioRenderEntry> audioRenderEntryLinkedList = new LinkedList();
        //添加录音的数据
        for (int i = 0; i < EditorVoiceoverComponent.mMementoVoiceList.size(); i++) {
            audioRenderEntryLinkedList.add(EditorVoiceoverComponent.mMementoVoiceList.get(i).getTuSDKAudioRenderEntry());
        }
        //添加背景音乐的数据
        TuSdkMediaDataSource tuSdkMediaDataSource = new TuSdkMediaDataSource(getEditorController().getActivity(), Uri.fromFile(new File(backgroundMusicBean.getLocalPath())));
//        tuSdkMediaDataSource.setFileDescriptorOffset(startTime*1000);
        TuSDKAudioRenderEntry curAudioRenderEntry = new TuSDKAudioRenderEntry(tuSdkMediaDataSource);
        curAudioRenderEntry.setTimeRange(TuSdkTimeRange.makeTimeUsRange((long) (getEditorPlayer().getTotalTimeUs() * getEditorController().getmCurrentLeftPercent()), (long) (getEditorPlayer().getTotalTimeUs() * getEditorController().getmCurrentRightPercent())));
        curAudioRenderEntry.setCutTimeRange(TuSdkTimeRange.makeTimeUsRange(startTime * 1000, Integer.MAX_VALUE));
        curAudioRenderEntry.setLooping(true);
        backgroundMusicSelected = new BackgroundMusicSelected(curAudioRenderEntry, backgroundMusicBean.getMusicId());
        audioRenderEntryLinkedList.add(curAudioRenderEntry);
        getEditorController().getMovieEditor().getEditorMixer().setAudioRenderEntryList(audioRenderEntryLinkedList);
        getEditorController().getMovieEditor().getEditorMixer().loadAudio();
    }


    private void setSeekBarProgress(int index, float progress) {
        if (AppConstants.shootBackgroundMusicBean != null && index == 0) {
            progress = 0f;
        }
        mVolumeConfigView.getSeekBarList().get(index).setProgress(progress);
    }

    /**
     * 开始预览
     */
    private void startPreview() {
        getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    /**
     * 暂停预览
     */
    private void pausePreview() {
        getEditorPlayer().pausePreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMusic();
    }
}
