package org.lasque.twsdkvideo.video_beauty.editor.component;

import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Lolita;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Monster;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal;
import static org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine.TuSdkSoundPitchType.Uncle;

import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioRenderEntry;
import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.audio.TuSdkAudioRecordCuter;
import org.lasque.tusdk.core.audio.TuSdkAudioRecorder;
import org.lasque.tusdk.core.decoder.TuSDKAudioInfo;
import org.lasque.tusdk.core.media.codec.audio.TuSdkAudioInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorAudioMixerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.video.editor.TuSdkMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaTransitionEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.CustomAudioRenderEntry;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;
import org.lasque.twsdkvideo.video_beauty.views.HorizontalProgressBar;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.airbnb.lottie.LottieAnimationView;

public class EditorVoiceoverComponent extends EditorComponent {

    /**
     * 当前录音的场景数据备忘列表
     **/
    public static LinkedList<CustomAudioRenderEntry> mMementoVoiceList = new LinkedList<>();
    private TuSdkSize mCurrentPreviewSize = null;
    private View mBottomView;
    private TuSdkMovieScrollPlayLineView mLineView;
    private static final String TAG = "EditorVoiceoverComponent";
    private Handler mHandler = new Handler();
    private static final int LONG_CLICK_RECORD = 2;
    private ImageButton mRecordBtn;
    private static final int RECORDING = 1;
    /**
     * 音频文件录制实例
     */
    private TuSdkAudioRecorder mAudioRecorder;
    private TuSdkMovieEditor mMovieEditor;
    private ImageView mDeletedBtn;
    private LottieAnimationView audioRecording;
    private ImageView checkBox;

    /**
     * 默认选择
     */
    private int mCurrentPos = 2;
    private LinearLayout mSoundTypeBar;
    /**
     * holdView 原始的高度
     */
    private int originalHoldViewHeight = 0;
    /**
     * 返回
     */
    private View mBackBtn;
    /**
     * 播放
     */
    private View mSaveButton;
    private VideoContent mHolderView;
    /**
     * 当前组件是否被选择
     **/
    public boolean isOnSelect = false;
    public boolean isContinue = true;
    /**
     * 当前是否允许绘制特效色块
     **/
    public boolean mDrawColorState = false;
    /**
     * 更新进度
     **/
    private boolean isPreState = true;
    public boolean hasMediaAudioEffectData = false;

    /**
     * 应用特效的开始时间
     **/
    private long mStartTimeUs;
    /**
     * 当前应用的场景数据列表
     **/
    private LinkedList<CustomAudioRenderEntry> mDataList;


    private boolean keepOrigianlSound = true;
    private boolean mMementoKeepOrigianlSound = true;

    /**
     * 音效的顺序
     **/
    private TuSdkAudioPitchEngine.TuSdkSoundPitchType[] mSoundTypes =
            new TuSdkAudioPitchEngine.TuSdkSoundPitchType[]{Monster, Uncle, Normal, Girl, Lolita};

    private String[] colors = new String[]{"lsq_scence_effect_color_LiveShake01", "lsq_scence_effect_color_LiveMegrim01", "lsq_scence_effect_color_EdgeMagic01", "lsq_scence_effect_color_LiveFancy01_1", "lsq_scence_effect_color_LiveSoulOut01"};
    private TextView tip;
    private View checkBoxText;

    public EditorVoiceoverComponent(MovieEditorController editorController, VideoContent mHolderView) {
        super(editorController);
        mDataList = new LinkedList<>();
        this.mHolderView = mHolderView;
        mMovieEditor = getEditorController().getMovieEditor();
        mComponentType = EditorComponent.EditorComponentType.Voiceover;
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);
    }

    public void backBtnTip(){
        if (mDataList.size() > 0) {
            DialogHelper.closeTipDialog(getEditorController().getActivity(), getEditorController().getActivity().getResources().getString(R.string.discard_voiceover), new DialogHelper.onDiscardClickListener() {
                @Override
                public void onDiscardClick() {
                    disCardVoiceover();
                }
            });
        } else {
            disCardVoiceover();
        }
    }

    public void cleanAudio(){
          if(mMementoVoiceList.size()>0){
              mMementoVoiceList.clear();
              mMementoKeepOrigianlSound = true;
              mLineView.clearAllColorRect();
              mMovieEditor.getEditorMixer().clearAllAudioData();
              mMovieEditor.getEditorMixer().loadAudio();
          }
    }


    private void disCardVoiceover() {
        //1.先清除
        getEditorAudioMixer().clearAllAudioData();
        for (int i = 0; i < mDataList.size(); i++) {
            mLineView.deletedColorRect();
        }
        mDataList.clear();
        keepOrigianlSound = true;
        //2.再把上次的加回来
        LinkedList<TuSDKAudioRenderEntry> audioRenderEntryLinkedList = new LinkedList();
        for (int i = 0; i < mMementoVoiceList.size(); i++) {
            audioRenderEntryLinkedList.add(mMementoVoiceList.get(i).getTuSDKAudioRenderEntry());
        }
        //3.记得把背景音乐的加回来
        if (EditorMusicComponent.backgroundMusicSelected != null) {
            audioRenderEntryLinkedList.add(EditorMusicComponent.backgroundMusicSelected.getCurAudioRenderEntry());
        }
        mMovieEditor.getEditorMixer().setAudioRenderEntryList(audioRenderEntryLinkedList);
        mMovieEditor.getEditorMixer().loadAudio();
        if (mMementoKeepOrigianlSound) {
            checkBox.setBackgroundResource(R.drawable.checkbox_selected_icon);
            getEditorAudioMixer().setMasterAudioTrack(1);
        } else {
            getEditorAudioMixer().setMasterAudioTrack(0);
            checkBox.setBackgroundResource(R.drawable.checkbox_unselected_icon);
        }
        //3.回到主页
        mHolderView.setHeight(originalHoldViewHeight);
        getEditorController().onBackEvent();
    }

    public EditorVoiceoverComponent setHeadAction() {
        mBackBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_back1);
        mSaveButton = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_save);

        if (mBackBtn != null) {
            mBackBtn.setOnClickListener(new View.OnClickListener() {//点击关闭按钮 回复上次一次应用的特效
                @Override
                public void onClick(View view) {
                    backBtnTip();
                }

            });
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//应用特效，添加到备忘中
                    mMementoVoiceList.clear();
                    mMementoVoiceList.addAll(mDataList);
                    mMementoKeepOrigianlSound = keepOrigianlSound;
                    mDataList.clear();
                    mLineView.clearAllColorRect();
                    //回到主页
                    mHolderView.setHeight(originalHoldViewHeight);
                    getEditorController().onBackEvent();
                }
            });
        }
        return this;
    }

    private void setAudioRender() {
        //录音清空，并且背景音乐也为空
        if (mDataList.size() == 0 && EditorMusicComponent.backgroundMusicSelected == null) {
            mMovieEditor.getEditorMixer().clearAllAudioData();
        } else {
            LinkedList<TuSDKAudioRenderEntry> audioRenderEntryLinkedList = new LinkedList();
            for (int i = 0; i < mDataList.size(); i++) {
                audioRenderEntryLinkedList.add(mDataList.get(i).getTuSDKAudioRenderEntry());
            }
            //记得把背景音乐的加回来
            if (EditorMusicComponent.backgroundMusicSelected != null) {
                audioRenderEntryLinkedList.add(EditorMusicComponent.backgroundMusicSelected.getCurAudioRenderEntry());
            }
            mMovieEditor.getEditorMixer().setAudioRenderEntryList(audioRenderEntryLinkedList);
        }
        mMovieEditor.getEditorMixer().loadAudio();
    }

    private boolean isFirstPress = false;
    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (getEditorController().getActivity().getImageStickerView() == null) return;
            mCurrentPreviewSize = TuSdkSize.create(previewSize.width, previewSize.height);
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    getEditorController().getActivity().getImageStickerView().resize(previewSize, getEditorController().getVideoContentView());
                }
            });

        }
    };

    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mBottomView == null) return;
            if (MovieEditorController.mCurrentComponent instanceof EditorVoiceoverComponent) {
                setPlayState(state);
            }
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (!(MovieEditorController.mCurrentComponent instanceof EditorVoiceoverComponent)) {
                return;
            }
            if (mBottomView == null || isAnimationStaring) return;
//            mLineView.seekTo(percentage);
            if (MovieEditorController.mCurrentComponent instanceof EditorVoiceoverComponent) {
                moveToPercent(percentage, playbackTimeUs);
                if (!isFirstPress) {
                    return;
                }
                //如果录音的进度已经超过了视频的总长度
                if (playbackTimeUs >= mMovieEditor.getEditorPlayer().getTotalTimeUs()) {
                    TuSdkViewHelper.toast(getEditorController().getActivity(), R.string.lsq_max_audio_record_time);
                    onTouchActionUP(true);
                    return;
                }
                for (int i = 0; i < mDataList.size(); i++) {
                    if (playbackTimeUs >= mDataList.get(i).getTuSDKAudioRenderEntry().getTimeRange().getStartTimeUS() && playbackTimeUs <= mDataList.get(i).getTuSDKAudioRenderEntry().getTimeRange().getEndTimeUS()) {
                        TuSdkViewHelper.toast(getEditorController().getActivity(), R.string.Can_not_record_over_previous_voiceover);
                        onTouchActionUP(true);
                        return;
                    }
                }

            }
        }
    };

    private void moveToPercent(float percentage, long playbackTimeUs) {
        currentPercent = percentage;
        if (mLineView != null) {
            if (isOnSelect) return;
            if (mDrawColorState) {
                isPreState = mDrawColorState;
                mLineView.seekTo(percentage);
            } else {
                if (isPreState) {
                    isPreState = false;
                    return;
                }
                if (isContinue && !mMovieEditor.getEditorPlayer().isPause()) {
                    mLineView.seekTo(percentage);
                }
            }
        }


    }

    /**
     * 设置播放状态
     *
     * @param state 0 播放  1 暂停
     * @since V3.0.0
     */
    public void setPlayState(int state) {
        if (MovieEditorController.mCurrentComponent instanceof EditorVoiceoverComponent) {
            if (state == 0) {
                mDrawColorState = true;
                isContinue = false;
                getEditorController().getPlayBtn().setVisibility(View.GONE);
            } else {
                mDrawColorState = false;
                //暂停的时候停止添加颜色
                mLineView.endAddColorRect();
                hasMediaAudioEffectData = false;
                getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
            }
            hasMediaAudioEffectData = false;
        }

    }


    @Override
    public void attach() {
        if (originalHoldViewHeight == 0) {
            originalHoldViewHeight = mHolderView.getHeight();
        }
        //如果在背景音乐那里修改了，需要同步状态
        float trunkVolume = ((TuSdkEditorAudioMixerImpl) getEditorAudioMixer()).getMixerAudioRender().getTrunkVolume();
        if (trunkVolume == 0) {
            keepOrigianlSound = false;
            mMementoKeepOrigianlSound = false;
        } else {
            keepOrigianlSound = true;
            mMementoKeepOrigianlSound = true;
        }
        getEditorController().getBottomView().addView(getBottomView());
        dealCheckBox();
        getEditorPlayer().pausePreview();
        getEditorPlayer().seekOutputTimeUs((long) (getEditorController().getmCurrentLeftPercent()*getEditorPlayer().getOutputTotalTimeUS()));
//        if (mLineView != null) {
//            mLineView.seekTo(0);
//            Log.e("sdfsdfsfsfsdf","......366...."+0);
//        }
        for (CustomAudioRenderEntry item : mMementoVoiceList) {
            recoveryEffect(item);
        }
        mLineView.setCursorMinPercent(getEditorController().getmCurrentLeftPercent());
        mLineView.setCursorMaxPercent(getEditorController().getmCurrentRightPercent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLineView.seekTo(getEditorPlayer().isReversing() ? getEditorController().getmCurrentRightPercent() : getEditorController().getmCurrentLeftPercent());
                mLineView.setPercent(getEditorController().getmCurrentLeftPercent());
            }
        },100);
    }

    /**
     * 恢复之前应用的特效
     **/
    private void recoveryEffect(CustomAudioRenderEntry customAudioRenderEntry) {
        for(;;){

            if(getEditorPlayer().getTotalTimeUs()>0){
                break;
            }
        }



        float startPercent = (customAudioRenderEntry.getTuSDKAudioRenderEntry().getTimeRange().getStartTimeUS()) / (float) getEditorPlayer().getTotalTimeUs();
        float endPercent = (customAudioRenderEntry.getTuSDKAudioRenderEntry().getTimeRange().getEndTimeUS()) / (float) getEditorPlayer().getTotalTimeUs();

        if(customAudioRenderEntry.isReversing()==mMovieEditor.getEditorPlayer().isReversing()){
            mLineView.recoverColorRect(TuSdkContext.getColor(customAudioRenderEntry.getColor()), startPercent, endPercent);

        }else{
            mLineView.recoverColorRect(TuSdkContext.getColor(customAudioRenderEntry.getColor()), 1-endPercent,1-startPercent );

        }
         mDataList.add(customAudioRenderEntry);
    }

    /**
     * 获取临时文件路径
     */
    protected File getOutputTempFilePath() {
        return new File(TuSdk.getAppTempPath(), String.format("lsq_temp_%s.aac", StringHelper.timeStampString()));
    }

    private MediaFormat getOutputAudioFormat() {
        return TuSdkMediaFormat.buildSafeAudioEncodecFormat();
    }

    @Override
    public void detach() {
        // getEditorPlayer().seekTimeUs(0);
        //重置播放进度
        mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
        getEditorPlayer().startPreview();
        mCurrentPos = 2;
        if (mAudioRecorder != null) mAudioRecorder.releas();
        mAudioRecorder = null;
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    private float currentPercent;

    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {


//            Log.e("sfsdfsdfdsfsdfsdfds","getEditorPlayer().getInputTotalTimeUs()="+getEditorPlayer().getInputTotalTimeUs()+".....getEditorPlayer().getOutputTotalTimeUS()"+getEditorPlayer().getOutputTotalTimeUS()+"...........getEditorPlayer().getTotalTimeUs()="+getEditorPlayer().getTotalTimeUs());



            long seekUs1;
            if (getEditorPlayer().isReversing()) {
                seekUs1 = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
               // getEditorPlayer().seekOutputTimeUs(seekUs1);
            } else {
                seekUs1 = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
              //  getEditorPlayer().seekInputTimeUs(seekUs1);
            }

          boolean hasAudio =false;
            for (int i = 0; i < mDataList.size(); i++) {
                if (seekUs1 >= mDataList.get(i).getTuSDKAudioRenderEntry().getTimeRange().getStartTimeUS() && seekUs1 <= mDataList.get(i).getTuSDKAudioRenderEntry().getTimeRange().getEndTimeUS()) {
                    hasAudio = true;
                    break;
                }
            }

            if(hasAudio){
                mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_unable_pressed);
                mRecordBtn.setImageResource(0);
                mRecordBtn.setEnabled(false);
                tip.setText(getEditorController().getActivity().getResources().getString(R.string.cannot_record_over_previous_voiceover));
            }else{
                if(mSoundTypeBar.getVisibility()==View.VISIBLE){
                    mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                    mRecordBtn.setImageResource(0);
                    mRecordBtn.setEnabled(true);
                    tip.setText(getEditorController().getActivity().getResources().getString(R.string.lsq_start_audiotrecord_hint));

                }
               }

         //   sdfdsfsfsf





       //     getEditorPlayer().getInputTotalTimeUs()



//            if(progress*){
//
//            }

            currentPercent = progress;
            if (!isTouching) return;
            if (isTouching)
                getEditorPlayer().pausePreview();
//            long current = (long) (progress * getEditorPlayer().getTotalTimeUs());
            if (getEditorPlayer().isPause()) {
                long seekUs;
                if (getEditorPlayer().isReversing()) {
                    seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                    getEditorPlayer().seekOutputTimeUs(seekUs);
                } else {
                    seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                    getEditorPlayer().seekInputTimeUs(seekUs);
                }
            }

        }

        @Override
        public void onCancelSeek() {
            //纠偏
            float progress_1 = getEditorPlayer().getCurrentTimeUs() / (float) getEditorPlayer().getTotalTimeUs();
            mLineView.seekTo(progress_1);
        }
    };

    private TuSdkMovieScrollContent.OnPlayProgressChangeListener onPlayProgressChangeListener = new TuSdkMovieScrollContent.OnPlayProgressChangeListener() {
        @Override
        public void onProgressChange(float progress) {
            currentPercent = progress;
            long seekUs;
            if (getEditorPlayer().isReversing()) {
                seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                getEditorPlayer().seekOutputTimeUs(seekUs);
            } else {
                seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                getEditorPlayer().seekInputTimeUs(seekUs);
            }
            mLineView.seekTo(progress);
        }
    };


    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.record_bottom_view, null);
            mRecordBtn = bottomView.findViewById(R.id.lsq_recordButton);
            audioRecording = bottomView.findViewById(R.id.audio_recording);
            checkBox = bottomView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(mOnClickListener);
            checkBoxText = bottomView.findViewById(R.id.checkbox_text);
            checkBoxText.setOnClickListener(mOnClickListener);
            mRecordBtn.setOnTouchListener(mOnTouchListener);
            mDeletedBtn = bottomView.findViewById(R.id.lsq_record_deleted);
            mBottomView = bottomView;
            mDeletedBtn.setOnClickListener(mOnClickListener);
            mLineView = bottomView.findViewById(R.id.lsq_editor_scene_play_range);
            mLineView.setType(0);
            mLineView.setOnProgressChangedListener(mOnScrollingPlayListener);
            mLineView.setOnPlayPointerChangeListener(onPlayProgressChangeListener);
            mSoundTypeBar = bottomView.findViewById(R.id.lsq_editor_audio_record_type_bar);
            tip = bottomView.findViewById(R.id.tip);


            int childCount = mSoundTypeBar.getChildCount();
            for (int i = 0; i < childCount; i++) {
                mSoundTypeBar.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectSoundType(Integer.parseInt((String) view.getTag()));
                    }
                });
            }
        }
        return mBottomView;
    }


    //点击回调
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lsq_record_deleted) {
                if (mOnRecordTouchListener != null)
                    mOnRecordTouchListener.onDeletedSegment();
            } else if (id == R.id.checkbox || id == R.id.checkbox_text) {
                keepOrigianlSound = !keepOrigianlSound;
                dealCheckBox();
            }
        }
    };

    private void dealCheckBox() {
        if (keepOrigianlSound) {
            checkBox.setBackgroundResource(R.drawable.checkbox_selected_icon);
            getEditorAudioMixer().setMasterAudioTrack(1);
        } else {
            getEditorAudioMixer().setMasterAudioTrack(0);
            checkBox.setBackgroundResource(R.drawable.checkbox_unselected_icon);
        }
    }

    /**
     * 切换录音的类型
     *
     * @param index
     */
    private void selectSoundType(int index) {
        int childCount = mSoundTypeBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Button btn = (Button) mSoundTypeBar.getChildAt(i);
            int currentIndex = Integer.parseInt((String) btn.getTag());
            LinearLayout.LayoutParams btnLayoutParams = (LinearLayout.LayoutParams) btn.getLayoutParams();
            if (index == currentIndex) {
                btn.setBackgroundResource(R.drawable.tusdk_view_widget_speed_button_bg);
                btn.setTextColor(getEditorController().getActivity().getResources().getColor(R.color.lsq_color_black));
                btnLayoutParams.setMargins(0, TuSdkContext.dip2px(2), 0, TuSdkContext.dip2px(2));
            } else {
                btn.setBackgroundResource(0);
                btn.setTextColor(getEditorController().getActivity().getResources().getColor(R.color.lsq_color_white));
                btnLayoutParams.setMargins(0, 0, 0, 0);
            }
            btn.setLayoutParams(btnLayoutParams);
        }
        mCurrentPos = index;
        //  mAudioRecorder.setSoundPitchType(mSoundTypes[index]);
    }


    /**
     * 录制监听
     **/
    private TuSdkAudioRecorder.TuSdkAudioRecorderListener mAudioRecorderListener = new TuSdkAudioRecorder.TuSdkAudioRecorderListener() {
        @Override
        public void onRecordProgress(long durationTimeUS, float percent) {

        }

        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onRecordError(int code) {
            switch (code) {
                case PERMISSION_ERROR:
                    TuSdk.messageHub().showError(getEditorController().getActivity(), R.string.lsq_record_dialog_message);
                    break;
                case PARAMETRTS_ERROR:
                    TLog.e("%s record parameter invalid ！", TAG);
                    break;
            }
        }
    };

    /**
     * 更新按钮显示状态
     **/
    private void updateBtnState() {
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                mDeletedBtn.setVisibility(mDataList.size() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    /**
     * 录制按钮点击事件
     */
    public interface OnRecordTouchListener {
        /** 开始录制音频 */
        void onStartRecordAudio();

        /** 暂停录制音频 */
        void onPauseRecordAudio(boolean isHasAudio);

        /** 删除上段音频 */
        void onDeletedSegment();

        /** 下一步 **/
        void onNextStep();
    }

    /**
     * 录制按钮
     **/
    private OnRecordTouchListener mOnRecordTouchListener = new OnRecordTouchListener() {
        @Override
        public void onStartRecordAudio() {
            tip.setVisibility(View.INVISIBLE);
            //开始录音的时候把原声去掉
            getEditorAudioMixer().setMasterAudioTrack(0);
            //如果超过最大录制时长，提示用户
            long totalUs = mMovieEditor.getEditorPlayer().getTotalTimeUs();
            float percent = mLineView.getCurrentPercent();
            long mEndTimeUs = (long) (totalUs * percent);
            if (mEndTimeUs >= mMovieEditor.getEditorPlayer().getInputTotalTimeUs()) {
                TuSdkViewHelper.toast(getEditorController().getActivity(), R.string.lsq_max_audio_record_time);
                return;
            }
            newAudioRecorder();
            mAudioRecorder.setSoundPitchType(mSoundTypes[mCurrentPos]);
            // 如果是未开启的状态  开启录音
            if (!mAudioRecorder.isStart()) {
                mAudioRecorder.start();
            }
            //如果是暂停状态 恢复录音
            if (mAudioRecorder.isPause()) {
                mAudioRecorder.resume();
            }
        }

        @Override
        public void onPauseRecordAudio(boolean isHasAudio) {
            tip.setVisibility(View.VISIBLE);
            if (mAudioRecorder == null) {
                return;
            }
            if (!mAudioRecorder.isPause()) {
                mAudioRecorder.pause();
            }
            mMovieEditor.getEditorPlayer().pausePreview();
            updateRecordButtonResource(LONG_CLICK_RECORD,isHasAudio);
            pressTime = 0;
            //   mLineView.endAddColorRect();
            isFirstPress = false;
            //手抬起来之后
            isTouching = false;
            mHandler.removeMessages(0);
            onReleaseAudioEffect();
            //保存音效
            if (mOnRecordTouchListener != null) {
                mOnRecordTouchListener.onNextStep();
            }
        }

        @Override
        public void onDeletedSegment() {
            mMovieEditor.getEditorPlayer().pausePreview();
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLineView.endAddColorRect();

                    mLineView.deletedColorRect();
                    if(mDataList.size()<1)
                        return;
                    CustomAudioRenderEntry customAudioRenderEntry = mDataList.removeLast();
                    mLineView.seekTo((customAudioRenderEntry.getTuSDKAudioRenderEntry().getTimeRange().getStartTimeUS()) / (float) getEditorPlayer().getTotalTimeUs());
                    mMovieEditor.getEditorPlayer().seekInputTimeUs(customAudioRenderEntry.getTuSDKAudioRenderEntry().getTimeRange().getStartTimeUS());
                    setAudioRender();
                    //删除录音
                    updateBtnState();
                }
            }, 70);
        }

        @Override
        public void onNextStep() {
            //录音结束，恢复原音
            dealCheckBox();
            mAudioRecorder.stop();
            getEditorPlayer().setVideoSoundVolume(1);
            applyAudioEffect(Uri.fromFile(mAudioRecorder.getOutputFileTemp()), false);
        }
    };


    private void newAudioRecorder() {
        TuSDKAudioInfo audioInfo = TuSDKAudioInfo.createWithMediaFormat(getOutputAudioFormat());
        TuSdkAudioRecorder.TuSdkAudioRecorderSetting setting = new TuSdkAudioRecorder.TuSdkAudioRecorderSetting();
        setting.bitRate = audioInfo.sampleRate;
        setting.channelCount = audioInfo.channel;
        setting.sampleRate = audioInfo.sampleRate;
        mAudioRecorder = new TuSdkAudioRecorder(setting, mAudioRecorderListener);
        mAudioRecorder.setOutputFile(getOutputTempFilePath());
        mAudioRecorder.setMaxRecordTime(mMovieEditor.getEditorPlayer().getOutputTotalTimeUS());
        mAudioRecorder.setSoundPitchType(mSoundTypes[mCurrentPos]);
    }


    /**
     * 保存配音音效
     *
     * @param audioPathUri
     */
    private void applyAudioEffect(Uri audioPathUri, boolean isLooping) {
        if (audioPathUri == null) return;
        long totalUs = mMovieEditor.getEditorPlayer().getTotalTimeUs();
        float percent = mLineView.getCurrentPercent();
        long mEndTimeUs = (long) (totalUs * percent);
        TuSdkMediaDataSource tuSdkMediaDataSource = new TuSdkMediaDataSource(getEditorController().getActivity(), audioPathUri);
        TuSDKAudioRenderEntry tuSDKAudioRenderEntry = new TuSDKAudioRenderEntry(tuSdkMediaDataSource);
        tuSDKAudioRenderEntry.setTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs, mEndTimeUs));
        tuSDKAudioRenderEntry.setVolume(1);
        CustomAudioRenderEntry customAudioRenderEntry = new CustomAudioRenderEntry();
        customAudioRenderEntry.setTuSDKAudioRenderEntry(tuSDKAudioRenderEntry);
        customAudioRenderEntry.setColor(colors[mCurrentPos]);
        customAudioRenderEntry.setReversing(mMovieEditor.getEditorPlayer().isReversing());
        mDataList.add(customAudioRenderEntry);
        mMovieEditor.getEditorPlayer().pausePreview();
        //释放老的资源
        if (mAudioRecorder != null) mAudioRecorder.releas();
        mAudioRecorder = null;
        //保存配音音效
        updateBtnState();
        //添加到预览效果
        setAudioRender();


    }

    private void onTouchActionUP(boolean isHasAudio) {

        if (mAudioRecorder != null) {
            if (mOnRecordTouchListener != null)
                mOnRecordTouchListener.onPauseRecordAudio(isHasAudio);
        }
    }

    private void onReleaseAudioEffect() {
        mMovieEditor.getEditorPlayer().pausePreview();
    }

    boolean isTouching = false;
    long pressTime = 0;
    boolean actionLongPress = true;
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {


        //开始录制时间
        @Override
        public boolean onTouch(View v, MotionEvent event) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    actionLongPress= true;
                    long totalUs = mMovieEditor.getEditorPlayer().getTotalTimeUs();
                    float percent = mLineView.getCurrentPercent();
                    long mEndTimeUs = (long) (totalUs * percent);
                    if (mEndTimeUs >= mMovieEditor.getEditorPlayer().getInputTotalTimeUs()) {
                        TuSdkViewHelper.toast(getEditorController().getActivity(), R.string.lsq_max_audio_record_time);
                        return false;
                    }
                    if (isFirstPress) {//如果是点击，再次按下需要停止
                        onTouchActionUP(false);
                    } else {
                        isFirstPress = true;
                        pressTime = System.currentTimeMillis();
                        if (mOnRecordTouchListener != null)
                            mOnRecordTouchListener.onStartRecordAudio();
                        if (isTouching) return false;
                        isTouching = true;
                        onPressAudioEffect();
                        mDeletedBtn.setVisibility(View.INVISIBLE);
                        updateRecordButtonResource(RECORDING,false);

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isFirstPress && mAudioRecorder != null) {
                        if (mAudioRecorder.isPause()) {
                            updateRecordButtonResource(LONG_CLICK_RECORD,false);
                        }
                    }
                    return false;
                case MotionEvent.ACTION_UP:
                    if ((System.currentTimeMillis() - pressTime) > 500) {
                        if (isFirstPress) {
                            onTouchActionUP(false);
                        }
                    }else{
                        actionLongPress = false;
                        updateRecordButtonResource(RECORDING,false);
                    }
                    break;
                default:
                    return false;
            }
            return false;
        }

        private void onPressAudioEffect() {
            long totalUs = mMovieEditor.getEditorPlayer().getTotalTimeUs();
            float percent = mLineView.getCurrentPercent();
            mStartTimeUs = (long) (totalUs * percent);
            if (mMovieEditor.getEditorPlayer().isPause()) {
                mMovieEditor.getEditorPlayer().startPreview();
            }
            mLineView.endAddColorRect();
            mLineView.addColorRect(TuSdkContext.getColor(colors[mCurrentPos]));
            hasMediaAudioEffectData = true;
        }


    };


    /**
     * 改变录制按钮视图
     *
     * @param type
     */
    private void updateRecordButtonResource(int type,boolean isHasAudio) {
        ConstraintLayout.LayoutParams btnLayoutParams = (ConstraintLayout.LayoutParams) mRecordBtn.getLayoutParams();

        switch (type) {
            case LONG_CLICK_RECORD:
                audioRecording.pauseAnimation();
                audioRecording.setVisibility(View.INVISIBLE);
                mSoundTypeBar.setVisibility(View.VISIBLE);
                checkBoxText.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);

                if(isHasAudio){
                    mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_unable_pressed);
                    tip.setText(getEditorController().getActivity().getResources().getString(R.string.cannot_record_over_previous_voiceover));
                    mRecordBtn.setImageResource(0);
                    mRecordBtn.setEnabled(false);
                }else{
                    mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                    mRecordBtn.setImageResource(0);
                    mRecordBtn.setEnabled(true);
                    tip.setText(getEditorController().getActivity().getResources().getString(R.string.lsq_start_audiotrecord_hint));

                }
                btnLayoutParams.topMargin = TuSdkContext.dip2px(94);
                btnLayoutParams.width = TuSdkContext.dip2px(64);
                btnLayoutParams.height = TuSdkContext.dip2px(64);
                // mRecordBtn.setLayoutParams(btnLayoutParams);
                break;
            case RECORDING:
                audioRecording.playAnimation();
                audioRecording.setVisibility(View.VISIBLE);
                mSoundTypeBar.setVisibility(View.INVISIBLE);
                checkBoxText.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                if(!actionLongPress){
                    mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);

                }else{
                   mRecordBtn.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);

                }

                mRecordBtn.setImageResource(0);
                btnLayoutParams.topMargin = TuSdkContext.dip2px(86);
                btnLayoutParams.width = TuSdkContext.dip2px(91);
                btnLayoutParams.height = TuSdkContext.dip2px(91);
                //   mRecordBtn.setLayoutParams(btnLayoutParams);
                break;
        }
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        if (mLineView != null)
            mLineView.addBitmap(bitmap);
    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        getBottomView();
        if (mLineView != null)
            mLineView.addFirstFrameBitmap(bitmap);

    }

}
