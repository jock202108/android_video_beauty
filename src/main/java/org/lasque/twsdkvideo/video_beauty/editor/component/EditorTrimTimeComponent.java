package org.lasque.twsdkvideo.video_beauty.editor.component;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkMediaFilesCuterImpl;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.album.MovieInfo;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.TextWidthUtils;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;
import org.lasque.twsdkvideo.video_beauty.views.editor.SpeedView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2019/3/4 11:10
 * @Copright (c) 2019 tw. All rights reserved.
 * <p>
 * 裁剪组件
 */
public class EditorTrimTimeComponent extends EditorComponent {
    /** 裁剪视图 **/
//    private EditorCutView mEditorCutView;
    /**
     * 底部视图
     **/
    private View mBottomView;

    /**
     * 适配器
     **/
//    private TrimRecyclerAdapter mTrimAdapter;
    /**
     * 备份数据
     **/
    private int mBackupIndex = 0;
    /**
     * 当前剪裁后的持续时间   微秒
     **/
    private long mDurationTimeUs;
    /**
     * 左边控件选择的时间     微秒
     **/
    private long mLeftTimeRangUs;
    /**
     * 右边控件选择的时间     微秒
     **/
    private long mRightTimeRangUs;
    /**
     * 最小裁切时间
     */
    private long mMinCutTimeUs = 1 * 1000000;
    /**
     * 裁切工具
     */
    private TuSdkMediaFilesCuterImpl cuter;
    /**
     * 是否已经设置总时间
     **/
    private boolean isSetDuration = false;
    /**
     * 是否正在裁剪中
     **/
    private boolean isCutting = false;


    //播放器
    private TuSdkEditorPlayer mEditorPlayer;
    //播放视图
    private SelesView mVideoView;

    private static final String TAG = "EditorTrimTimeComponent";

    // 总共的时长
    private long mVideoTotalUs = 0;

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorTrimTimeComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.TrimTime;
    }

    @Override
    public void attach() {
        mVideoTotalUs = TuSDKMediaUtils.getVideoInfo(getEditorController().getActivity().mVideoPath).durationTimeUs;
         if(getEditorController().isAlbum){
             getEditorController().getHeaderView().addView(getHeaderView());

         }


        getEditorController().getBottomView().addView(getBottomView());
//        getEditorController().getVideoContentView().setClickable(false);
        if (!getEditorController().isAlbum) {
            getEditorController().getPlayBtn().setClickable(true);
            getEditorController().getPlayBtn().setOnClickListener(mOnClickListener);
        }
        getEditorPlayer().addProgressListener(mPlayProgressListener);
        if(!getEditorController().isAlbum){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRangeView.setLeftBarPosition(getEditorController().getmCurrentLeftPercent());
                    mRangeView.setRightBarPosition(getEditorController().getmCurrentRightPercent());
                    mEditorPlayer.seekTimeUs((long) (getEditorController().getmCurrentLeftPercent() * mVideoTotalUs));
                    setVideoPlayPercent(getEditorController().getmCurrentLeftPercent());
                    ThreadHelper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mEditorPlayer.isPause())
                                mEditorPlayer.startPreview();
                        }
                    }, 70);
                }
            }, 10);
        }

        loadVideoThumbList();
//        if (mTrimAdapter != null) mTrimAdapter.setSelectItem(mBackupIndex);
    }

    @Override
    public void detach() {
        getEditorPlayer().removeProgressListener(mPlayProgressListener);
//        getEditorController().getVideoContentView().setClickable(true);
    }

    /**
     * 头部视图
     **/
    private View mHeaderView;

    @Override
    public View getHeaderView() {
        if(getEditorController().isAlbum){
            if (mHeaderView == null) {
                mHeaderView = initHeadView();
            }
            return mHeaderView;
        }else{
            return  null;
        }

    }

    /**
     * 初始化headView
     *
     * @return View
     */
    private View initHeadView() {
        if (mHeaderView == null) {
            View headView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_cut_navigation, null);
            mHeaderView = headView;
            ImageView lsqBack = mHeaderView.findViewById(R.id.lsq_back);
            lsqBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getEditorController().getActivity().finish();
                }
            });
            ConstraintLayout.LayoutParams lsqBackLayoutParams = (ConstraintLayout.LayoutParams) lsqBack.getLayoutParams();
            lsqBackLayoutParams.topMargin = TuSdkContext.dip2px(28) + VideoBeautyPlugin.statusBarHeight;
            lsqBack.setLayoutParams(lsqBackLayoutParams);
        }
        return mHeaderView;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    MovieEditorActivity mActivity;
    //时间选择
    private TuSdkMovieScrollContent mRangeView;
    //刻度
//    private RulerView mRulerView;
    private TextView mTimeRangView;
    private TextView mTotalTimeTv;
    private boolean isEnable = true;
    private float mCurrentRangTime = 0f;
    boolean isShowToast = false;

    private synchronized View initBottomView() {
        if (mBottomView == null) {
            mActivity = getEditorController().getActivity();
            View bottomView;
            if (getEditorController().isAlbum) {
                bottomView = LayoutInflater.from(mActivity).inflate(R.layout.lsq_cut_trim_time_bottom, null);

            } else {
                bottomView = LayoutInflater.from(mActivity).inflate(R.layout.lsq_editor_component_trim_time_bottom, null);

            }
            bottomView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    bottomView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.e("sdfsdfsfsfsfsffdssd","onGlobalLayout="+bottomView.getHeight());
                }
            });


            mRangeView = bottomView.findViewById(R.id.lsq_range_line);
            View lsqNext  = bottomView.findViewById(R.id.lsq_next);
            if (getEditorController().isAlbum) {
                lsqNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startCompound(getEditorController().getActivity().mVideoPath, null);
                    }
                });
            }

            mRangeView.setType(1);
            mRangeView.setShowSelectBar(true);
            mRangeView.setNeedShowCursor(true);
            mTimeRangView = bottomView.findViewById(R.id.lsq_range_time);
            mTotalTimeTv = bottomView.findViewById(R.id.lsq_total_time);
            View ibFilterBack = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_back1);
            ibFilterBack.setOnClickListener(mOnClickListener);
            View ibFilterSure = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_save);
            ibFilterSure.setOnClickListener(mOnClickListener);
            mBottomView = bottomView;

            setOnPlayPointerChangeListener(new TuSdkMovieScrollContent.OnPlayProgressChangeListener() {
                @Override
                public void onProgressChange(float percent) {
                    if (!mEditorPlayer.isPause()) {
                        mEditorPlayer.pausePreview();
                        getEditorController().mPlayBtn.setVisibility(View.VISIBLE);
                    }
                    mEditorPlayer.seekTimeUs((long) (percent * mDurationTimeUs));
                    float currentTime = mVideoTotalUs * percent;
                    float index = (float) (currentTime - mLeftTimeRangUs) / 1000000.0f;
                    setIndexTime(index);

                }
            });


            getLineView().setExceedCriticalValueListener(new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
                @Override
                public void onMaxValueExceed() {
                }

                @Override
                public void onMinValueExceed() {
                    Integer minTime = (int) (mMinCutTimeUs / 1000000);

                    @SuppressLint("StringFormatMatches") String tips = String.format(getEditorController().getActivity().getString(R.string.lsq_min_time_effect_tips), minTime);
                    if (!isShowToast) {
                        isShowToast = true;
                        ToastUtils.showRedToast(getEditorController().getActivity(), tips);
                        ThreadHelper.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowToast = false;//如果只提示一次可以注释调这一行
                            }
                        }, 3000);
                    }

//                    TuSdk.messageHub().showToast(getEditorController().getActivity(), tips);
                }
            });
            getLineView().setOnTouchSelectBarListener(new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
                @Override
                public void onTouchBar(float leftPercent, float rightPerchent, int type) {
                    ThreadHelper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getEditorPlayer().isPause()) {
                                getEditorPlayer().startPreview();
                            }
                            //    isPlaying = true;
                        }
                    }, 70);

                }
            });

            setOnSelectCeoverTimeListener(new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
                @Override
                public void onSelectRangeChanged(float leftPercent, float rightPercent, int type) {
                    getEditorController().setmCurrentLeftPercent(leftPercent);
                    getEditorController().setmCurrentRightPercent(rightPercent);
                    getLineView().setCursorMinPercent(leftPercent);
                    getLineView().setCursorMaxPercent(rightPercent);
                    ;
                    if (type == 0) {
                        mLeftTimeRangUs = (long) ((leftPercent * mVideoTotalUs) / getEditorController().getmCurrentSpeed());
                        float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
                        if (selectTime < 1.0) selectTime = 1.0f;
                        setRangTime(selectTime);

                        setVideoPlayPercent(leftPercent);
                        mEditorPlayer.seekTimeUs((long) (leftPercent * mVideoTotalUs));
                        setIndexTime(0);

                    } else if (type == 1) {
                        mRightTimeRangUs = (long) (rightPercent * mVideoTotalUs / getEditorController().getmCurrentSpeed());
                        float selectTime = (mRightTimeRangUs - mLeftTimeRangUs) / 1000000.0f;
                        if (selectTime < 1.0) selectTime = 1.0f;
                        setRangTime(selectTime);
                        setVideoPlayPercent(rightPercent);
                        mEditorPlayer.seekTimeUs((long) (rightPercent * mVideoTotalUs));
                        setIndexTime(selectTime);
                    }
                }
            });

//            setOnPlayingSpeedChangeListener(mPlayingSpeedListener);

            //  loadVideoThumbList();
            initPlayer();
            setMinCutTimeUs(mMinCutTimeUs / (float) mDurationTimeUs);
            setMaxCutTimeUs(1.0f);


        }
        return mBottomView;
    }

    //播放器回调
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
//            mDurationTimeUs = mEditorPlayer.getTotalTimeUs();
//            if (!isCutting)
//                getEditorController().mPlayBtn.setVisibility(state == 0 ? View.GONE : View.VISIBLE);

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            long indexTimel = playbackTimeUs - mLeftTimeRangUs;
            float index = indexTimel / 1000000.0f;
            setIndexTime((float) (playbackTimeUs - mLeftTimeRangUs) / (float) 1000000);
//            if (getLineView() == null) return;
//            TLog.e("playbackTimeUs %s", playbackTimeUs);
//            float playPercent = (float) playbackTimeUs / (float) totalTimeUs;
//            Log.i("--------onProgress", "totalTimeUs   =" + totalTimeUs + " playbackTimeUs =" + playbackTimeUs + "playPercent" + playPercent);

//            setVideoPlayPercent(playPercent);
//            if (playPercent < getEditorController().getmCurrentLeftPercent() || playPercent >= getEditorController().getmCurrentRightPercent()) {
//                mEditorPlayer.seekTimeUs((long) (getEditorController().getmCurrentLeftPercent()*mEditorPlayer.getTotalTimeUs()));
//                setVideoPlayPercent(getEditorController().getmCurrentLeftPercent());
//                ThreadHelper.postDelayed(new Runnable(){
//                    @Override
//                    public void run() {
//                        if(mEditorPlayer.isPause())
//                        mEditorPlayer.startPreview();
//                    }
//                },70);
//            }
        }
    };

    /**
     * 初始化播放器
     **/
    public void initPlayer() {
        mEditorPlayer = getEditorPlayer();
        mDurationTimeUs += mVideoTotalUs;
        float duration = mDurationTimeUs / 1000000.0f;
        mRightTimeRangUs = mDurationTimeUs;
        setRangTime(duration);
        setIndexTime(0.0f);


    }

    /**
     * 获取临时文件路径
     */
    protected File getOutputTempFilePath() {
        return new File(TuSdk.getAppTempPath(), String.format("lsq_%s.mp4", StringHelper.timeStampString()));
    }

    /**
     * 加载视频缩略图
     */
    public void loadVideoThumbList() {
        List<Bitmap> thumbBitmapList = getEditorController().getThumbBitmapList();
        for (Bitmap bitmap : thumbBitmapList) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    addBitmap(bitmap);
                }
            });
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lsq_play_btn) {
                if (mEditorPlayer.isPause()) {
                    mEditorPlayer.startPreview();
                } else {
                    mEditorPlayer.pausePreview();
                }
            } else if (id == R.id.lsq_back1) {
                if (getEditorController().getmCurrentLeftPercent() > 0 || getEditorController().getmCurrentRightPercent() < 1) {
                    DialogHelper.remindCenter(mActivity, mActivity.getString(R.string.dialog_title_discard_edits_tips), new DialogHelper.onRemindSureClickListener() {
                        @Override
                        public void onSureClick() {
                            getEditorController().setmCurrentRightPercent(1);
                            getEditorController().setmCurrentLeftPercent(0);
                            getEditorController().onBackEvent();
                        }
                    });
                } else {
                    getEditorController().onBackEvent();
                }
                getEditorPlayer().seekTimeUs(0);
                getEditorPlayer().startPreview();

            } else if (id == R.id.lsq_save) {
//                setEnable(false);
                mEditorPlayer.pausePreview();
//                getEditorController().mPlayBtn.setVisibility(View.GONE);
//                startCompound();
//                getEditorController().mProgressContent.setVisibility(View.VISIBLE);
                getEditorController().onBackEvent();
            }
        }
    };

    /**
     * 裁剪完成监听
     **/
    public static interface OnTrimCompleteListener {
        /**
         * 裁剪完
         **/
        void onCompleted(String vidioPath);

    }

    /**
     * 开始合成视频 todo 最外面保存时调用？
     */
    public void startCompound(String videoPath, OnTrimCompleteListener onTrimCompleteListener) {
        if ((mRightTimeRangUs - mLeftTimeRangUs) > 60 * 1000000.0f) {

            ToastUtils.showRedToast(getEditorController().getActivity(), getEditorController().getActivity().getResources().getString(R.string.you_can_select_up_to_60_seconds));

            return;
        }

        if (cuter != null) {
            return;
        }

        if(onTrimCompleteListener == null){
            getEditorController().isSaveing = true;
            getEditorController().getMovieEditor().getEditorPlayer().pausePreview();
            FrameLayout frameLayout = getEditorController().getActivity().findViewById(R.id.fl_contain);
            frameLayout.removeView(getEditorController().getPlayBtn());
        }
        isCutting = true;

        boolean enableAudioCheck = false;

        List<TuSdkMediaDataSource> sourceList = new ArrayList<>();

        // 遍历视频源
        sourceList.add(TuSdkMediaDataSource.create(videoPath).get(0));
        TuSDKVideoInfo videoInfo = TuSDKMediaUtils.getVideoInfo(videoPath);
        if (videoInfo.fps >= 55) {//????
            enableAudioCheck = true;
        }


        // 准备切片时间
        // 把记录的位置同步过来,避免时间为0
        mLeftTimeRangUs = (long) ((getEditorController().getmCurrentLeftPercent() * getEditorController().getMovieEditor().getEditorPlayer().getTotalTimeUs()) / getEditorController().getmCurrentSpeed());
        mRightTimeRangUs = (long) (getEditorController().getmCurrentRightPercent() * getEditorController().getMovieEditor().getEditorPlayer().getTotalTimeUs() / getEditorController().getmCurrentSpeed());
        TuSdkMediaTimeSlice tuSdkMediaTimeSlice = new TuSdkMediaTimeSlice((long) (mLeftTimeRangUs * getEditorController().getmCurrentSpeed()), (long) (mRightTimeRangUs * getEditorController().getmCurrentSpeed()));
//        tuSdkMediaTimeSlice.speed = mVideoPlayer.speed();
        tuSdkMediaTimeSlice.speed = 1.0f;
        // 准备裁剪对象
        cuter = new TuSdkMediaFilesCuterImpl();
        // 设置裁剪切片时间
        cuter.setTimeSlice(tuSdkMediaTimeSlice);
        // 设置数据源
        cuter.setMediaDataSources(sourceList);
        // 设置文件输出路径
        cuter.setOutputFilePath(getOutputTempFilePath().getPath());
        cuter.setEnableAudioCheck(enableAudioCheck);
        // 准备视频格式
        MediaFormat videoFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(cuter.preferredOutputSize().width, cuter.preferredOutputSize().height,
                30, TuSdkVideoQuality.RECORD_MEDIUM2.getBitrate(), MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0, 0);
        // 设置视频输出格式
        cuter.setOutputVideoFormat(videoFormat);
        // 设置音频输出格式
        cuter.setOutputAudioFormat(TuSdkMediaFormat.buildSafeAudioEncodecFormat());
        // 开始裁剪
        cuter.run(new TuSdkMediaProgress() {
            /**
             *  裁剪进度回调
             * @param progress        进度百分比 0-1
             * @param mediaDataSource 当前处理的视频媒体源
             * @param index           当前处理的视频索引
             * @param total           总共需要处理的文件数
             */
            @Override
            public void onProgress(final float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        getEditorController().mProgressContent.setVisibility(View.VISIBLE);
                        getEditorController().mProgress.setValue(progress * 100);
                    }
                });
            }

            /**
             *  裁剪结束回调
             * @param e 如果成功则为Null
             * @param outputFile 输出文件路径
             * @param total 处理文件总数
             */
            @Override
            public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
                isCutting = false;
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        setEnable(true);
                        getEditorController().mProgressContent.setVisibility(View.GONE);
                        getEditorController().mProgress.setValue(0);
//                        getEditorController().mPlayBtn.setVisibility(mVideoPlayer.isPause()?View.VISIBLE:View.GONE);
                        getEditorController().mPlayBtn.setVisibility(getEditorController().getMovieEditor().getEditorPlayer().isPause() ? View.VISIBLE : View.GONE);
                    }
                });
                if (getEditorController().isAlbum) {

                    Intent intent = new Intent(getEditorController().getActivity(), MovieEditorActivity.class);
                    intent.putExtra("videoPath", outputFile.getPath());
                    //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("isTrim", true);

                    intent.putExtra("videoWidth", getEditorController().videoWidth);
                    intent.putExtra("videoHeight", getEditorController().videoHeight);
                    getEditorController().getActivity().startActivity(intent);
                    getEditorController().getActivity().finish();


                } else {
                    onTrimCompleteListener.onCompleted(outputFile.getPath());
//                mActivity.mVideoPath = outputFile.getPath();//todo jump?
//                mActivity.initEditorController();
                    getEditorController().onBackEvent();
                }

                cuter = null;
            }
        });
    }


    /**
     * 开始预览
     */
    private void startPreview() {
        getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        if (mBottomView != null)
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    addBitmap(bitmap);
                }
            });
    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        if (mBottomView != null)
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                   mRangeView.addFirstFrameBitmap(bitmap);
                }
            });
    }

    public void setMinCutTimeUs(float timeUs) {
        mRangeView.setMinWidth(timeUs);
    }

    public void setMaxCutTimeUs(float timeUs) {
        mRangeView.setMaxWidth(timeUs);
    }

    /**
     * 设置时间区间
     *
     * @param times
     */
    public void setRangTime(float times) {
        mCurrentRangTime = times;
        String rangeTime = String.format("%.1f %s", times, "s");
//        mTimeRangView.setText(rangeTime);
        mTotalTimeTv.setText(rangeTime);
    }

    /**
     * 设置光标时间
     *
     * @param times
     */
    public void setIndexTime(float times) {
        if (times < 0) {
            times = 0.0f;
        }
        String rangeTime = String.format("%.1f %s", times, "s");
        mTimeRangView.setText(rangeTime);
    }

    public float getRangTime() {
        return mCurrentRangTime;
    }

    /**
     * 设置封面图
     *
     * @param coverList 封面图列表
     */
    public void setCoverList(List<Bitmap> coverList) {
        if (coverList == null) {
            return;
        }
//        mRangeView.setBitmapList(coverList);
//        mRangeView.setMinSelectTimeUs(mMinCutTimeUs);
    }


    /**
     * 设置选择区间回调
     *
     * @param onSelectTimeChangeListener
     */
    public void setOnSelectCeoverTimeListener(TuSdkRangeSelectionBar.OnSelectRangeChangedListener onSelectTimeChangeListener) {
        if (onSelectTimeChangeListener == null) {
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setSelectRangeChangedListener(onSelectTimeChangeListener);
    }

    /**
     * 播放指针 位置改变监听
     *
     * @param progressChangeListener
     */
    public void setOnPlayPointerChangeListener(TuSdkMovieScrollContent.OnPlayProgressChangeListener progressChangeListener) {
        if (progressChangeListener == null) {
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setProgressChangeListener(progressChangeListener);
    }


    /**
     * 设置播放进度
     *
     * @param percent 播放进度的百分比
     */
    public void setVideoPlayPercent(float percent) {
        if (mRangeView == null) return;
        if (percent < 0) {
            TLog.e("setSelectCoverTimeListener is null !!!");
            return;
        }
        mRangeView.setPercent(percent);
    }

    public TuSdkMovieScrollContent getLineView() {
        return mRangeView;
    }

    public void addBitmap(Bitmap bitmap) {
        mRangeView.addBitmap(bitmap);
    }

    /**
     * 设置是否启用
     **/
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
        if (mRangeView != null) {
            mRangeView.setEnable(isEnable);
        }
    }

    public void setOnPlayingSpeedChangeListener(SpeedView.OnPlayingSpeedChangeListener listener) {
//        if (mPlayingSpeedView == null) return;
//        mPlayingSpeedView.setPlayingSpeedChangeListener(listener);
    }

}
