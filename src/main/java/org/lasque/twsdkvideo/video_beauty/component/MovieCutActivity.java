package org.lasque.twsdkvideo.video_beauty.component;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lasque.tusdk.api.video.retriever.TuSDKVideoImageExtractor;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.suit.TuSdkMediaSuit;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.RectHelper;
import org.lasque.tusdk.core.utils.StringHelper;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.TuSdkDate;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.ImageOrientation;
import org.lasque.tusdk.core.view.widget.button.TuSdkNavigatorBackButton;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.ScreenAdapterActivity;
import org.lasque.twsdkvideo.video_beauty.album.MovieInfo;
import org.lasque.twsdkvideo.video_beauty.views.editor.LineView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

/** ???????????? **/
public class MovieCutActivity extends ScreenAdapterActivity {

    /** ???????????? */
    private TuSdkNavigatorBackButton mBackBtn;
    /** ?????????????????? */
    private Button mStarCutBtn;
    /** MediaPlayer ????????? */
    private MediaPlayer mMediaPlayer;
    /** ?????????????????? */
    private SurfaceView mSurfaceView;
    //????????????
    private Button mPlayButton;
    /** PLAY TIME TextView */
    private TextView mPlayTextView;
    /** LEFT TIME TextView */
    private TextView mLeftTextView;
    /** RIGHT TIME TextView */
    private TextView mRightTextView;
    private LineView mRangeSelectionBar;
    /** ?????????????????? */
    private FrameLayout mLoadContent;
    /** ???????????? */
    private CircleProgressView mCircleView;
    /** ?????????????????? */
    private String mInputPath;
    /** ??????????????? */
    private int mVideoTotalTime;
    /** ??????????????????????????? */
    private int mStart_time;
    /** ??????????????????????????? */
    private int mEnd_time;
    /** ??????????????????????????????????????? */
    private boolean isMoveLeft;
    /** ??????????????????????????????????????? */
    private boolean isMoveRight;

    private boolean isMoveStartTime;
    /** ?????????????????? */
    private boolean isPlay;
    /** ?????????????????? */
    private boolean isPause;
    /**
     * ???????????????????????????
     * true ?????????????????????
     * false ??????????????????
     */
    private boolean isInit = false;
    /** ???????????? ?????????????????? */
    private boolean isFirstLoadVideo = false;
    /** ????????????????????? **/
    private boolean isCutting = false;
    /** ?????????????????????,??????s */
    private TuSdkTimeRange mCuTimeRange;
    /** VideoInfo */
    private TuSDKVideoInfo videoInfo;

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            /** ??????????????????  */
            initMediaPlayer(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
        }
    };

    /** ??????????????? */
    public void destoryMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /** ?????????????????? **/
    private TuSdkMediaProgress mCuterMediaProgress = new TuSdkMediaProgress() {

        @Override
        public void onProgress(float progress, TuSdkMediaDataSource mediaDataSource,
                               int index, int total) {
            mCircleView.setValue(progress * 100);
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
            Toast.makeText(getBaseContext(), e == null ? getResources().getString(R.string.lsq_movie_cut_done) : getResources().getString(R.string.lsq_movie_cut_error), Toast.LENGTH_SHORT).show();
            mLoadContent.setVisibility(View.GONE);
            mCircleView.setValue(0);
            isCutting = false;
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mInputPath);
                mMediaPlayer.prepareAsync();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    private LineView.OnSelectTimeChangeListener onSelectTimeChangeListener = new LineView.OnSelectTimeChangeListener() {
        @Override
        public void onTimeChange(long startTime, long endTime, long selectTime, float startTimePercent, float endTimePercent, float selectTimePercent) {
            setTotalTime((float) (endTime - startTime) / (float)1000);
        }

        @Override
        public void onLeftTimeChange(long startTime, float startTimePercent) {
            mCuTimeRange.setStartTimeUs(startTime);
            setTextTime(mLeftTextView, (int) (startTime / 1000));
        }

        @Override
        public void onRightTimeChange(long endTime, float endTimePercent) {
            mCuTimeRange.setEndTimeUs(endTime);
            setTextTime(mRightTextView, (int) (endTime / 1000));
        }

        @Override
        public void onMaxValue() {

        }

        @Override
        public void onMinValue() {

        }
    };

    /** ?????????????????? **/
    private LineView.OnPlayPointerChangeListener onPlayPointerChangeListener =  new LineView.OnPlayPointerChangeListener() {
        @Override
        public void onPlayPointerPosition(long playPointerPositionTime, float playPointerPositionTimePercent) {
            if(mMediaPlayer!=null && mRangeSelectionBar.getTouchingState()) {
                mMediaPlayer.seekTo((int) (playPointerPositionTime / 1000));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_cut);
        initView();
        initData();
    }

    private void initData(){
        videoInfo = TuSDKMediaUtils.getVideoInfo(mInputPath);
        setTotalTime(videoInfo.durationTimeUs / 1000f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            preparePlay();
            isPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPause && isPlay) {
            pauseVideo();
            isPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryMediaPlayer();
    }

    /**
     * ?????????View
     */
    private void initView() {

        List<MovieInfo> mInputPaths = (List<MovieInfo>) getIntent().getSerializableExtra("videoPaths");
        mInputPath = mInputPaths.get(0).getPath();

        mBackBtn = findViewById(R.id.lsq_backButton);
        mBackBtn.setOnClickListener(mOnClickListener);

        TextView titleView = findViewById(R.id.lsq_titleView);
        titleView.setText(TuSdkContext.getString("lsq_movie_cut_text"));

        mPlayButton = findViewById(R.id.lsq_play_btn);
        mPlayButton.setClickable(false);
        mStarCutBtn = findViewById(R.id.lsq_movie_cut_btn);
        mStarCutBtn.setOnClickListener(mOnClickListener);


        mSurfaceView = (SurfaceView) this.findViewById(R.id.lsq_video_view);
        mSurfaceView.setOnClickListener(mOnClickListener);


        mPlayTextView = this.findViewById(R.id.lsq_play_time);
        mLeftTextView = this.findViewById(R.id.lsq_left_time);
        mRightTextView = this.findViewById(R.id.lsq_right_time);

        mLeftTextView.setText(R.string.lsq_text_time_tv);
        mRightTextView.setText(R.string.lsq_text_time_tv);

        mRangeSelectionBar = this.findViewById(R.id.lsq_range_line);
        mRangeSelectionBar.setInitType(LineView.LineViewType.DrawPointer, getResources().getColor(R.color.lsq_color_white));
        mRangeSelectionBar.setOnSelectTimeChangeListener(onSelectTimeChangeListener);
        mRangeSelectionBar.setOnPlayPointerChangeListener(onPlayPointerChangeListener);
        mRangeSelectionBar.loadView();

        TuSdk.messageHub().applyToViewWithNavigationBarHidden(false);

        mLoadContent = findViewById(R.id.lsq_editor_cut_load);
        mCircleView = findViewById(R.id.circleView);

        // ?????????????????????
        loadVideoThumbList();
        showPlayButton();
        isFirstLoadVideo = false;
        // ?????????????????????
        mCuTimeRange = new TuSdkTimeRange();

        mSurfaceView.getHolder().addCallback(mCallback);
    }

    /** ????????????????????? */
    public void loadVideoThumbList() {
        if (mRangeSelectionBar != null) {
            TuSdkSize tuSdkSize = TuSdkSize.create(TuSdkContext.dip2px(56),
                    TuSdkContext.dip2px(56));
            TuSDKVideoImageExtractor extractor = TuSDKVideoImageExtractor.createExtractor();

            extractor.setOutputImageSize(tuSdkSize)
                    .setVideoDataSource(TuSDKMediaDataSource.create(mInputPath))
                    .setExtractFrameCount(20);

            extractor.asyncExtractImageList(new TuSDKVideoImageExtractor.TuSDKVideoImageExtractorDelegate() {
                @Override
                public void onVideoImageListDidLoaded(List<Bitmap> images) {
                    if(mMediaPlayer != null) {
                        mRangeSelectionBar.setTotalTimeUs(mMediaPlayer.getDuration() * 1000);
                        setTextTime(mRightTextView, mMediaPlayer.getDuration());
                    }
                }

                @Override
                public void onVideoNewImageLoaded(Bitmap bitmap) {
                    mRangeSelectionBar.addBitmap(bitmap);
                }

            });
        }
    }

    /** ???????????? **/
    private void setTextTime(TextView textView,int times){
        TuSdkDate date = TuSdkDate.create(times);
        int minute = date.minute();
        int second = date.second();
        textView.setText(String.format("%02d:%02d",minute,second));
    }

    /** ??????????????? ?????? **/
    private void setTotalTime(float times){
        float totalTime = times/(float)1000;
        mPlayTextView.setText(String.format(getString(R.string.lsq_movie_cut_selecttime) + "%.1fs",totalTime));

    }

    /**
     * ??????????????????
     */
    /** ?????????????????? */
    public void initMediaPlayer(SurfaceHolder holder) {
        isInit = false;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // ????????????????????????SurfaceView
        mMediaPlayer.setDisplay(holder);

        // ???????????????????????????
        try {
            setDataSource(mInputPath);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

    }

    private void setDataSource(String mInputPath) {
        if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mInputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            pauseVideo();
        }
    };


    /** ???????????????????????? */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                int time = mMediaPlayer.getCurrentPosition();
                time = (time < mStart_time) ? (int) mStart_time : time;
                if (time >= (int) mStart_time && time < mEnd_time) {
                    /** ???????????????????????????  */
                    if (mRangeSelectionBar != null) {
                        float percent =  ((float) time  / (float)mVideoTotalTime );
                        mRangeSelectionBar.pointerMoveToPercent(percent);
                    }
                } else {
                    showPlayButton();
                    /** ????????????  */
                    pauseVideo();
                    /** ??????????????????  */
                    ThreadHelper.cancel(this);
                }
                /** ??????????????????  */
                ThreadHelper.post(this);
            }
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

            if (!isFirstLoadVideo) {
                isFirstLoadVideo = true;
                // ?????????????????????
                mVideoTotalTime = mMediaPlayer.getDuration();
                mEnd_time = mVideoTotalTime;
                mCuTimeRange.setStartTime(0.0f);
                mCuTimeRange.setEndTime(mVideoTotalTime);

                seekToStart();
                isInit = true;
                return;
            }
            if (!isInit) {
                seekToStart();
                isInit = true;
                return;
            }

            playVideo();
        }
    };

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            if (isMoveLeft || isMoveRight || isMoveStartTime) {
                mp.pause();
                isMoveLeft = false;
                isMoveRight = false;
                isMoveStartTime = false;
                // ??????????????????
                showPlayButton();
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            // ????????????????????????????????????
            setVideoSize(mSurfaceView, width, height);
        }
    };

    public void setVideoSize(SurfaceView surfaceView, int width, int height) {
        if (surfaceView != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = (int) dm.widthPixels;
            int screenHeight = (int) (360 * dm.density);

            Rect boundingRect = new Rect();
            boundingRect.left = 0;
            boundingRect.right = screenWidth;
            boundingRect.top = 0;
            boundingRect.bottom = screenHeight;
            Rect rect = RectHelper.makeRectWithAspectRatioInsideRect(new TuSdkSize(width, height), boundingRect);

            int w = rect.right - rect.left;
            int h = rect.bottom - rect.top;
            RelativeLayout.LayoutParams lp = new RelativeLayout
                    .LayoutParams(w, h);
            lp.setMargins(rect.left, rect.top, 0, 0);
            surfaceView.setLayoutParams(lp);
        }
    }

    /** ???????????? */
    public void pauseVideo() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            showPlayButton();
            isPlay = false;
        }
    }

    /** ?????????????????? */
    public void showPlayButton() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.VISIBLE);
            mPlayButton.setBackgroundResource(R.drawable.lsq_editor_ic_play);
        }
    }

    /** ?????????????????? */
    public void hidePlayButton() {
        if (mPlayButton != null) {
            mPlayButton.setVisibility(View.VISIBLE);
            mPlayButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    public void seekToStart() {
        isMoveStartTime = true;
        mStart_time = (mStart_time > 1) ? mStart_time : 1;
        mMediaPlayer.seekTo((int) mStart_time);
        mMediaPlayer.start();
    }

    /**
     * ????????????
     * ???????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????(?????????prepareAsync()????????????,
     * ???onPrepared()?????????????????????)
     */
    public void playVideo() {
        if (!isInit) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            return;
        }
        if (mMediaPlayer == null) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
            return;
        }
        isPlay = true;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        // ???????????????????????????
        mMediaPlayer.seekTo((int) mStart_time);
        mMediaPlayer.start();

        /** ???????????????,????????????????????????  */
        ThreadHelper.runThread(new Runnable() {

            @Override
            public void run() {
                ThreadHelper.post(runnable);
            }
        });

        // ?????????????????????????????????
        hidePlayButton();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lsq_backButton) {
                finish();
            } else if (id == R.id.lsq_movie_mixer_btn) {
            } else if (id == R.id.lsq_movie_cut_btn) {
                startMovieClipper();
            } else if (id == R.id.lsq_video_view) {
                if (!isPlay) {
                    // ????????????
                    preparePlay();
                } else {
                    // ??????
                    pauseVideo();
                }
            }
        }
    };

    /**
     * ????????????
     */
    private void startMovieClipper() {
        if (isCutting) return;
        isCutting = true;

        MediaFormat ouputVideoFormat = getOutputVideoFormat(videoInfo);
        MediaFormat ouputAudioFormat = getOutputAudioFormat();

        TuSdkMediaTimeSlice timeSlice = new TuSdkMediaTimeSlice(mCuTimeRange.getStartTimeUS(), mCuTimeRange.getEndTimeUS());

        //???????????? 0 ~ 1
        RectF rectDrawF = new RectF(0, 0, 1, 1);
        //????????????0 ~ 1
        RectF rectCutF = new RectF(0, 0, 1, 1);

        TuSdkMediaSuit.cuter(new TuSdkMediaDataSource(mInputPath), getOutPutFilePath(), ouputVideoFormat, ouputAudioFormat, ImageOrientation.Up,
                rectDrawF, rectCutF, timeSlice, mCuterMediaProgress);
        mLoadContent.setVisibility(View.VISIBLE);
    }


    /**
     * ???????????????????????????????????????
     *
     * @param videoInfo ?????????????????????
     * @return MediaFormat
     */
    protected MediaFormat getOutputVideoFormat(TuSDKVideoInfo videoInfo) {
        int fps = videoInfo.fps;
        int bitrate = videoInfo.bitrate;
        TuSdkSize size = TuSdkSize.create(videoInfo.width,videoInfo.height);

        if (videoInfo.videoOrientation == ImageOrientation.Right
                || videoInfo.videoOrientation == ImageOrientation.Left
                || videoInfo.videoOrientation == ImageOrientation.RightMirrored
                || videoInfo.videoOrientation == ImageOrientation.LeftMirrored)
            size.set(size.height,size.width);

        MediaFormat mediaFormat = TuSdkMediaFormat.buildSafeVideoEncodecFormat(size.width, size.height,
                fps, bitrate, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface, 0, 1);

        return mediaFormat;
    }

    /**
     * ?????????????????????????????????
     *
     * @return MediaFormat
     */
    protected MediaFormat getOutputAudioFormat() {
        MediaFormat audioFormat = TuSdkMediaFormat.buildSafeAudioEncodecFormat();
        return audioFormat;
    }

    private String getOutPutFilePath() {
        return new File(AlbumHelper.getAblumPath(),
                String.format("lsq_cut_%s.mp4", StringHelper.timeStampString())).toString();
    }


    /**
     * ??????????????????
     * ???????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????(?????????prepareAsync()????????????,
     * ???onPrepared()?????????????????????)
     */
    public void preparePlay() {
        if (!isInit) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            return;
        }
        if (mMediaPlayer == null) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_empty_error);
            return;
        }

        // ?????????????????????
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // ???????????????????????????????????????
            setDataSource(mInputPath);
            // ??????????????????
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            TuSdk.messageHub().showToast(this, R.string.lsq_video_read_prepare);
            e.printStackTrace();
        }
    }

}
