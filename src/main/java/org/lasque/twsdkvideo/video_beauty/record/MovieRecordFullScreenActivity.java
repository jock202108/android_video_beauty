package org.lasque.twsdkvideo.video_beauty.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoEncoderSetting;
import org.lasque.tusdk.core.video.TuSDKVideoResult;
import org.lasque.twsdkvideo.video_beauty.SimpleCameraActivity;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.album.MovieInfo;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.BackgroundMusicBean;
import org.lasque.twsdkvideo.video_beauty.data.GVisionDynamicStickerBean;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.SpUtils;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;
import org.lasque.twsdkvideo.video_beauty.views.record.RecordView;
import org.quanqi.circularprogress.CircularProgressView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.microshow.rxffmpeg.RxFFmpegCommandList;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;


/**
 *
 */
public class MovieRecordFullScreenActivity extends SimpleCameraActivity implements
        RecordView.TuSDKMovieRecordDelegate, TuSdkRecorderVideoCamera.TuSdkRecorderVideoCameraCallback {
    // 录制界面视图
    protected RecordView mRecordView;
    private RelativeLayout mLoadContent;
    private CircularProgressView mLoadProgress;
    private int screenWidth;
    private int screenHeight;
    //标识区别（由于sdk的播放状态缺陷增加的标志位，
    // isFromPause是否是onPause(有可能是home或者是跳转到编辑页)
    private boolean isFromPause;
    // isSavedVideo是否是成功保存视频跳转到编辑页
    private boolean isSavedVideo;
    private boolean isFirst = true;

    /**
     * k
     * 编辑类ClassName
     */

    protected int getLayoutId() {
        return R.layout.activity_new_record_full_screen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(getLayoutId());
        AppConstants.isSaveDraft = false;
        // 开启ffmpeg日志
        RxFFmpegInvoke.getInstance().setDebug(true);
//        RecordView  mRecordView = (RecordView) findViewById(R.id.lsq_movie_record_view);
//        mRecordView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mRecordView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                mRecordViewHeight = mRecordView.getHeight();
//
//            }
//        });
//        Display display = getWindowManager().getDefaultDisplay();
//        screenWidth = display.getWidth(); // 屏幕宽
//        screenHeight = display.getHeight(); // 屏幕高
        screenWidth = TuSdkContext.getScreenSize().width;
        screenHeight = TuSdkContext.getScreenSize().height;
        mLoadContent = findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = findViewById(R.id.progress_view);
        mLoadContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initCamera();
        getRecordView();
        // 设置录制界面背景为透明色
//        hideNavigationBar();
        TuSdk.messageHub().applyToViewWithNavigationBarHidden(true);

        EventBus.getDefault().register(this);
        //请求贴纸面具
        sendBroadcast();
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.to.flutter");
        intent.putExtra("type", VideoBeautyPlugin.kGetDynamicStickerList);
        Objects.requireNonNull(this).sendBroadcast(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // showRecover();
        getRecordView().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getRecordView().onStop();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_bottom_out);
    }

    // 暂时不需要
    private void showRecover() {
        if (isFirst) {
            String recoverUrl = new SpUtils(this).getString(SpUtils.recoverKey, "");
            if (!recoverUrl.equals("")) {
                DialogHelper.recordRecover(this, new DialogHelper.onRecordRecoverClickListener() {
                    @Override
                    public void onContinue() {
                        extracted(recoverUrl, MovieRecordFullScreenActivity.this, screenWidth, screenHeight, true);
                    }

                    @Override
                    public void onStartNewClick() {
                        new SpUtils(MovieRecordFullScreenActivity.this).remove(SpUtils.recoverKey);
                    }
                });
            }
            isFirst = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mRecordView != null) {
            mRecordView.resetFilter();
            mRecordView.recycleBitmap();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setEffect(GVisionDynamicStickerBean gVisionDynamicStickerBean) {
        if (mRecordView != null) {
            mRecordView.init(getSupportFragmentManager(), getLifecycle(), gVisionDynamicStickerBean);
        }
    }

    @Override
    protected void onResume() {
        // 设置标志以使此Activity处于唤醒状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        super.onResume();
        if (mRecordView != null) {
            mRecordView.onResume();
        }
//        setWindowBrightness(255);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //非跳转到编辑页的onPause，对应onResume需要显示的UI和做到的操作
                getRecordView().recordReStartCheck(false);
                if (isSavedVideo) {
                    getRecordView().initOnResumeRecordProgress();
                    isSavedVideo = false;
                }
            }
        });
    }



    @Override
    protected void onPause() {
        // 同步闪关灯状态
        getRecordView().updateFlashMode(CameraConfigs.CameraFlash.Off);
        // mVideoCamera.pauseRecording();
        //mVideoCamera.cancelRecording();
        super.onPause();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //判断是否跳转到编辑页，onPause需要做到的操作
                if (isSavedVideo) {
                    getRecordView().initOnPauseSavedStatus();
                } else {
                    getRecordView().initOnPauseRecordProgress();
                }

            }
        });
        isFromPause = true;
    }

    /**
     * 录制界面视图
     */
    protected RecordView getRecordView() {
        if (mRecordView == null) {
            mRecordView = (RecordView) findViewById(R.id.lsq_movie_record_view);
            mRecordView.setDelegate(this);
            mRecordView.setUpCamera(this, mVideoCamera);

            mRecordView.initFilterGroupsViews(getSupportFragmentManager(), getLifecycle(), Constants.getCameraFilters(true));
        }

        return mRecordView;
    }

    private void setWindowBrightness(int brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }

    protected void initCamera() {
        super.initCamera();
        if (getIntent() != null && getIntent().hasExtra("isDirectEdit")) {
            //设置录制完毕后跳转
            mVideoCamera.enableDirectEdit(true);
        }
        // 设置相机事件回调
        mVideoCamera.setRecorderVideoCameraCallback(this);
        mVideoCamera.setMinRecordingTime(Constants.MIN_RECORDING_TIME);
        mVideoCamera.setMaxRecordingTime(Constants.MAX_RECORDING_TIME);
        // 设置使用录制相机最小空间限制,开发者可根据需要自行设置（默认：50M）
        mVideoCamera.setMinAvailableSpaceBytes(1024 * 1024 * 50l);

        // 开启人脸检测 开启后方可使用人脸贴纸及微整形功能
        mVideoCamera.setEnableFaceDetection(true);

        // 编码配置
        TuSdkRecorderVideoEncoderSetting encoderSetting = TuSdkRecorderVideoEncoderSetting.getDefaultRecordSetting();
        // 输出全屏尺寸
        encoderSetting.videoSize = TuSdkSize.create(screenWidth, screenHeight);
        // 这里可以修改帧率和码率; RECORD_MEDIUM2第一个参数代表帧率，第二参数代表码率;选择VideoQuality参数尽量选用RECORD开头(专门为视频录制设计)
        encoderSetting.videoQuality = TuSdkVideoQuality.RECORD_HIGH3;

        encoderSetting.enableAllKeyFrame = true;

        mVideoCamera.setVideoEncoderSetting(encoderSetting);
        //   mVideoCamera.setSaveToAlbum(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q?false:true);//保存为私有目录
        mVideoCamera.setSaveToAlbum(false);//保存为私有目录

    }

    @Override
    protected void pauseCameraCapture() {
        super.pauseCameraCapture();
    }

    /**
     * ----------- 注意事项：如果视频录制完成后需要跳转到视频编辑页面,需要将录制视频页面销毁掉; 视频编辑跳转视频录制也是如此 ---------------------------
     */
    @Override
    public void onMovieRecordComplete(final TuSDKVideoResult result) {
        //   Log.e("hh","当前的线程----"+Thread.currentThread().getName());
        if (!mVideoCamera.isDirectEdit() && 1 == 2) {
            mRecordView.updateViewOnMovieRecordComplete(isRecording());
        } else {
            final ArrayList<TuSdkMediaTimeSlice> recordTimeSlices = mVideoCamera.getRecordTimeSlice();
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    Intent intent = new Intent(MovieRecordFullScreenActivity.this, MovieEditorActivity.class);
//                    intent.putExtra("isDirectEdit", mVideoCamera.isDirectEdit());
//                    intent.putExtra("videoPath", result.videoPath.getAbsolutePath());
//                    intent.putExtra("timeRange", recordTimeSlices);
//                    startActivity(intent);
//                    finishRecordActivity();
                    //成功保存了视频
                    isSavedVideo = true;
                    // 视频路径
                    List<MovieInfo> videoPath = new ArrayList<>();
                    videoPath.add(new MovieInfo(result.videoPath.getPath(), 0));
                    // Intent intent = new Intent(MovieRecordFullScreenActivity.this, MovieEditorPreviewActivity.class);
                    long time = System.currentTimeMillis();

                        if (AppConstants.musicLocalPath != null && AppConstants.musicLocalPath != "") {
                            try {
                                startMovieMixer(result.videoPath.getPath(), AppConstants.musicLocalPath, getFilesDir().getPath() + "/" + time + ".mp4");
                            } catch (Exception e) {

                            }


                        } else {
                            try {
//                        Intent   intent = new Intent(MovieRecordFullScreenActivity.this, Class.forName("org.lasque.twsdkvideo.video_beauty.editor.MovieEditorCutActivity"));
//                        intent.putExtra("videoPaths", (Serializable) videoPath);
//                        intent.putExtra("screenWidth", screenWidth);
//                        intent.putExtra("screenHeight", screenHeight);
                                AppConstants.shootBackgroundMusicBean = null;
                                if(AppConstants.isSaveDraft){
                                    sendDraftBroadcast(result.videoPath.getPath());
                                    AppConstants.isSaveDraft = false;
                                    return;
                                }
                                extracted(videoPath.get(0).getPath(), MovieRecordFullScreenActivity.this, screenWidth, screenHeight, false);

//                        Intent intent = new Intent(MovieRecordFullScreenActivity.this,MovieEditorActivity.class);
//                        intent.putExtra("videoPath",  result.videoPath.getPath());
//                        startActivity(intent);


//                        finishRecordActivity();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }


            }, 500);
        }
    }

    /// 发送存草稿广播
    private void sendDraftBroadcast(String videoPath) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.to.flutter");
        intent.putExtra("type", VideoBeautyPlugin.kGetShootDraft);
        intent.putExtra("videoUrl", videoPath);
        Objects.requireNonNull(this).sendBroadcast(intent);
    }


    private void extracted(String outPath, Activity activity, int screenWidth, int screenHeight, boolean isRecover) {
        if (!isRecover) {
            new SpUtils(activity).putString(SpUtils.recoverKey, outPath);
        }
        Intent intent = new Intent(activity, MovieEditorActivity.class);
        intent.putExtra("videoPath", outPath);
        if (mRecordView != null) {
            ArrayList<String> stickerIds = Utils.removeDuplicate(mRecordView.getStickerIds());
            stickerIds = Utils.removeEmpty(stickerIds);
            intent.putExtra("stickerIds", stickerIds);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("videoWidth", screenWidth);
        intent.putExtra("videoHeight", screenHeight);
        activity.startActivity(intent);
    }

    @Override
    public void onMovieRecordProgressChanged(float progress,
                                             float durationTime) {
        mRecordView.updateViewOnMovieRecordProgressChanged(progress, durationTime);
    }

    @Override
    public void onMovieRecordStateChanged(TuSdkRecorderVideoCamera.RecordState state) {
        mRecordView.updateMovieRecordState(state, isRecording());
    }

    @Override
    public void onMovieRecordFailed(TuSdkRecorderVideoCamera.RecordError error) {
        TLog.e("RecordError : %s", error);
        mRecordView.updateViewOnMovieRecordFailed(error, isRecording());
    }


    @Override
    public void stopRecording() {
        if (mVideoCamera.isRecording()) {
            mVideoCamera.stopRecording();
        }
//        mRecordView.updateViewOnStopRecording(mVideoCamera.isRecording());
    }

    @Override
    public void pauseRecording() {
        mVideoCamera.pauseRecording();
    }


    @Override
    public void startRecording() {
        if (!mVideoCamera.isRecording()) {
            mVideoCamera.startRecording();
        }

//        mRecordView.updateViewOnStartRecording(mVideoCamera.isRecording());
    }

    @Override
    public boolean isRecording() {
        return mVideoCamera.isRecording();
    }

    @Override
    public void finishRecordActivity() {
        this.finish();
    }


    /**
     * 生成命令行
     *
     * @param videoPath
     * @param audioPath
     * @param outputPath
     * @return
     */
    private String[] getBoxblur(String videoPath, String audioPath, String outputPath) {
        RxFFmpegCommandList cmdlist = new RxFFmpegCommandList();
        cmdlist.append("-i");
        cmdlist.append(videoPath);
        cmdlist.append("-i");
        cmdlist.append(audioPath);
        cmdlist.append("-filter_complex");
        cmdlist.append("[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + 0.0f + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + 1.5f + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]");
        cmdlist.append("-map");
        cmdlist.append("[aout]");
        cmdlist.append("-ac");
        cmdlist.append("2");
        cmdlist.append("-c:v");
        cmdlist.append("copy");
        cmdlist.append("-map");
        cmdlist.append("0:v:0");
        cmdlist.append("-preset");// 指定编码的配置
        cmdlist.append("superfast");
        cmdlist.append(outputPath);
        return cmdlist.build();
    }


    /**
     * ffmpeg 混音
     *
     * @param videoPath
     * @param audioPath
     * @param outputPath
     */
    private void ffmpegMixer(String videoPath, String audioPath, String outputPath) {
        String[] commands = getBoxblur(videoPath, audioPath, outputPath);
        MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber(this, outputPath, videoPath);
        //开始执行FFmpeg命令
        RxFFmpegInvoke.getInstance()
                .runCommandRxJava(commands)
                .subscribe(myRxFFmpegSubscriber);
    }


    /**
     * 混合音视频
     *
     * @param videoPath
     * @param audioPath
     * @param outputPath
     */
    private void startMovieMixer(String videoPath, String audioPath, String outputPath) {
        ffmpegMixer(videoPath, audioPath, outputPath);

    }

    public class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        private WeakReference<MovieRecordFullScreenActivity> mWeakReference;
        private String outPath;
        private String mVideoPath;

        public MyRxFFmpegSubscriber(MovieRecordFullScreenActivity activity, String outPath, String videoPath) {
            mWeakReference = new WeakReference<>(activity);
            this.outPath = outPath;
            this.mVideoPath = videoPath;
        }

        @Override
        public void onFinish() {

            List<MovieInfo> videoPath = new ArrayList<>();
            videoPath.add(new MovieInfo(outPath, 0));
            if(AppConstants.isSaveDraft){
                sendDraftBroadcast(videoPath.get(0).getPath());
                AppConstants.isSaveDraft = false;
            }else {
                extracted(videoPath.get(0).getPath(), mWeakReference.get(), mWeakReference.get().screenWidth, mWeakReference.get().screenHeight, false);
            }

//        File file = new File(AppConstants.musicLocalPath);
//        if(file.exists()){
//            file.delete();
//        }
            /// 混音完成,删除原来的视频
            AlbumUtils.deleteFile(mVideoPath);
            AppConstants.musicLocalPath = "";
            RxFFmpegInvoke.getInstance().onCancel();
            //取消后加上这句好像就不报错了
            RxFFmpegInvoke.getInstance().exit();
        }


        @Override
        public void onProgress(int progress, long progressTime) {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(String message) {
            AppConstants.shootBackgroundMusicBean = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mRecordView != null) {
                mRecordView.hanldeCloseButton();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
