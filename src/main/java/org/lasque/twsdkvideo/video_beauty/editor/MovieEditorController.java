package org.lasque.twsdkvideo.video_beauty.editor;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.google.gson.Gson;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.encoder.video.TuSDKVideoEncoderSetting;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.TuSdkVideoImageExtractor;
import org.lasque.tusdk.core.seles.output.SelesView;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorSaver;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorSaverImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorTranscoder;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditorImpl;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.video.editor.TuSdkMediaAudioEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaFilterEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaSceneEffectData;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.album.MovieInfo;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.ResultBean;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorDynamicStickerComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorEffectComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorFilterComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorHomeComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorMVComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorMusicComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorStickerComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorTextComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorTrimComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorEffectTransitionsComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorTrimTimeComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorVoiceoverComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.helper.EditorTextAndStickerRankHelper;
import org.lasque.twsdkvideo.video_beauty.editor.component.helper.GifBitmapUtils;
import org.lasque.twsdkvideo.video_beauty.editor.component.helper.GiphyHelper;
import org.lasque.twsdkvideo.video_beauty.utils.AppManager;
import org.lasque.twsdkvideo.video_beauty.utils.BitmapUtils;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.EditorAnimator;
import org.lasque.twsdkvideo.video_beauty.views.fragments.StickerBottomSheetFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import androidx.fragment.app.FragmentActivity;

import at.grabner.circleprogress.CircleProgressView;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 11:37
 * @Copright (c) 2018 tw. All rights reserved.
 * <p>
 * 视频编辑控制类
 */
public class MovieEditorController {
    private static final String TAG = "MovieEditorController";
    /* --------------- SDK相关 ----------------*/
    //当前的视频编辑器
    private TuSdkMovieEditor mMovieEditor;
    private FragmentActivity currentActivity;
    //主音轨音量
    private float mMasterVolume = 0.5f;
    //音乐特效数据的备份
    private TuSdkMediaAudioEffectData mMusicEffectData;
    //MV特效数据
    private TuSdkMediaEffectData mMVEffectData;
    /*---------------- 视图相关 ----------------*/
    //当前Activity的引用
    private WeakReference<MovieEditorActivity> mWeakActivity;
    //视频View
    private VideoContent mHolderView;
    //播放按钮
    public ImageView mPlayBtn;
    //加载进度父视图
    public FrameLayout mProgressContent;
    //加载进度
    public CircleProgressView mProgress;
    //动画控制器
    private EditorAnimator mEditorAnimator;

    /*---------- 组件实例 ---------*/
    //当前正在使用的组件
    public static EditorComponent mCurrentComponent;
    //主页面组件
    private EditorHomeComponent mHomeComponent;
    //滤镜组件
    private EditorFilterComponent mFilterComponent;
    //MV组件
    private EditorMVComponent mMVComponent;
    //音效组件
    private EditorMusicComponent mMusicComponent;
    //文字组件
    private EditorTextComponent mTextComponent;
    //特效组件
    private EditorEffectComponent mEffectComponent;
    //贴纸组件
    private EditorStickerComponent mStickerComponent;
    //裁剪组件
    private EditorTrimComponent mTrimComponent;
    //裁剪时间范围组件
    private EditorTrimTimeComponent mTrimTimeComponent;
    //转场特效组件
    private EditorEffectTransitionsComponent mTransitionsComponent;

    private EditorDynamicStickerComponent mDynamicStickerComponent;

    private EditorVoiceoverComponent mVoiceoverComponent;

    //缩略图集合
    private List<Bitmap> mThumbBitmapList = new ArrayList<>();
    //是否正在保存
    private boolean isSaving = false;
    private float mCurrentSpeed = 1f;
    private float mCurrentLeftPercent = 0f;
    private float mCurrentRightPercent = 1.0f;

    //是否正在播放（播放回调里有类似的onStateChanged的0、1值改变，目前看1的时候是准备好可以播放）
    private boolean isPlaying = false;
    private boolean needLoop = true;//是否需要循环播放

    private boolean isNeedRelease = false;
    public boolean isSaveing = false;
    private MyThread myThread;

    private List<TuSdkVideoImageExtractor.VideoImage> copyVideoImagesList;

    /**
     * 贴纸和文字备份数据帮助类
     **/
    private EditorTextAndStickerRankHelper mImageTextRankHelper;
    private ViewGroup mMusicLayout;


    private Handler mHandler ;

    /**
     * 静态内部类不会隐式地持有他们外部类的引用，所以Activity实例不会在配置变化
     * 中被泄露
     */
    private static class MyThread extends Thread {
        private boolean mRunning = false;
        private Runnable runnable;

        public MyThread(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            mRunning = true;
            if(mRunning){
                runnable.run();
            }
        }

        public void close() {
            mRunning = false;
        }
    }

    /**
     * 转码回调
     **/
    private TuSdkEditorTranscoder.TuSdkTranscoderProgressListener mOnTranscoderProgressListener = new TuSdkEditorTranscoder.TuSdkTranscoderProgressListener() {
        @Override
        public void onProgressChanged(float percentage) {
            mProgress.setValue(percentage * 100);
        }

        @Override
        public void onLoadComplete(TuSDKVideoInfo outputVideoInfo, TuSdkMediaDataSource outputVideoSource) {
            mProgressContent.setVisibility(View.GONE);
            mHolderView.setClickable(true);
            mProgress.setValue(0);
            mPlayBtn.setVisibility(View.GONE);
            getHomeComponent().setEnable(true);
            //        mMovieEditor.setDataSource(outputVideoSource);

            int width = TuSdkContext.getScreenSize().width;
            int height = TuSdkContext.getScreenSize().height;
            if (isAlbum || getActivity().isTrim) {
                // 横屏
                Log.e("hh", (float) width / (float) height + "");
                if (videoWidth > videoHeight) {
                    if(!isAlbum){
                          mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //  mMovieEditor.getEditorPlayer().setOutputRatio((float) VideoBeautyPlugin.screenWidth / (float) VideoBeautyPlugin.screenHeight, false);
                            mMovieEditor.getEditorPlayer().setOutputRatio((float) width / (float) height, false);

                        }
                    }, 2000);
                    }
                }
                // 竖屏
                else {
                    float ration = (float) width / (float) height;
                    mMovieEditor.getEditorPlayer().getDisplayView().setFillMode(SelesView.SelesFillModeType.PreserveAspectRatioAndFill);
                    mMovieEditor.getEditorPlayer().setOutputRatio((float) width / (float) height, true);

                }


            }
        }

        @Override
        public void onError(Exception e) {
            if (e != null) TLog.e(e);
            mProgressContent.setVisibility(View.GONE);
            mProgress.setValue(0);
            TuSdk.messageHub().showError(getActivity(), R.string.lsq_editor_load_error);
            getHomeComponent().setEnable(true);
        }
    };

    boolean isFirst = true;
    /**
     * 播放回调
     **/
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        long changeTime = 0;

        @Override
        public void onStateChanged(int state) {//0 是正常播放  1是暂停
            if (mCurrentComponent instanceof EditorHomeComponent || mCurrentComponent instanceof EditorTrimTimeComponent) {
                mPlayBtn.setVisibility(state == 1 && !isSaving && !needLoop ? View.VISIBLE : View.GONE);

                if (state == 0) {//0 是正常播放

                } else {// 1是暂停
                    if (isFirst) {
                        ThreadHelper.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isSaveing) {
                                    if (getMovieEditor().getEditorPlayer().isPause()) {
                                        getMovieEditor().getEditorPlayer().startPreview();
                                    }
                                }

                                //    isPlaying = true;
                            }
                        }, isAlbum ? 1500 : 150);
                    }
                    isFirst = false;
                }
            }
        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            TLog.d("[debug] current editor player playbackTimeUs = " + playbackTimeUs + " totalTimeUs = " + totalTimeUs);
            playbackTimeUs = (long) (percentage * totalTimeUs);
//            Log.i("======Video onProgress", "播放起开始的时间 playbackTimeUs" + playbackTimeUs + "设置的时间 mCurrentLeftPercent * totalTimeUs" + mCurrentLeftPercent * totalTimeUs);
            if ((mCurrentComponent instanceof EditorTrimTimeComponent))
                getTrimTimeComponent().setVideoPlayPercent(percentage);
//            if (playPercent < mCurrentLeftPercent || playPercent >= mCurrentRightPercent) {
            //多给1s 的缓冲时间，因为重新设置播放进度之后播放器播放开始时间有误差，
            // 时光逆转需要加个判断
            boolean needToLoop = false;
            if (!mMovieEditor.getEditorPlayer().isReversing()) {
                needToLoop = playbackTimeUs < mCurrentLeftPercent * totalTimeUs || playbackTimeUs >= mCurrentRightPercent * totalTimeUs;
            } else {
                needToLoop = playbackTimeUs <= mCurrentLeftPercent * totalTimeUs || playbackTimeUs > mCurrentRightPercent * totalTimeUs;
            }

            if (needToLoop) {
                long l = System.currentTimeMillis();

                if ((mCurrentComponent instanceof EditorTrimTimeComponent)) {
                    getTrimTimeComponent().setVideoPlayPercent(mCurrentLeftPercent);
                    ThreadHelper.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getMovieEditor().getEditorPlayer().isPause()) {
                                getMovieEditor().getEditorPlayer().startPreview();
                            }
                            //    isPlaying = true;
                        }
                    }, 70);
                } else {
                    if (needLoop) { //如果需要循环播放
                        ThreadHelper.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getMovieEditor().getEditorPlayer().isPause()) {
                                    getMovieEditor().getEditorPlayer().startPreview();
                                }
                                //    isPlaying = true;
                            }
                        }, 70);
                    }
                }
                if (l - changeTime < 1000) {
                    changeTime = l;
                    return;
                }
                changeTime = l;
                getMovieEditor().getEditorPlayer().seekTimeUs((long) (mCurrentLeftPercent * totalTimeUs));

            }
        }
    };

    /**
     * 保存回调
     **/
    private TuSdkEditorSaver.TuSdkSaverProgressListener mSaveProgressListener = new TuSdkEditorSaver.TuSdkSaverProgressListener() {
        @Override
        public void onProgress(float progress) {
            if (mPlayBtn.getVisibility() == View.VISIBLE) mPlayBtn.setVisibility(View.GONE);
            mProgress.setValue(progress * 100);
        }

        @Override
        public void onCompleted(TuSdkMediaDataSource outputFile) {
            //文件保存路径为 outputFile.getPath()
            isSaving = false;
            mProgressContent.setVisibility(View.GONE);
            mProgress.setValue(0);
            //android 11 需要拷贝一份到相册。
            String videoPath = outputFile.getPath();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                Uri uri = saveVideoToAlbumIfNeed(currentActivity, outputFile.getPath());
//                videoPath = getRealPath(uri);
//            }

            if ((mCurrentLeftPercent > 0 || mCurrentRightPercent < 1) && !isAlbum) {
                getTrimTimeComponent().startCompound(videoPath, path -> {
                    onTrimCompleted(path, false);
                });
            } else {
                onTrimCompleted(videoPath, false);
            }
            /// 清除添加到保存器里的贴纸,防止重复添加
            ((TuSdkEditorSaverImpl) getMovieEditor().getEditorSaver()).clearAllEffect();
        }

        @Override
        public void onError(Exception e) {
            isSaving = false;
            mProgressContent.setVisibility(View.GONE);
            mProgress.setValue(0);
            TuSdk.messageHub().showError(getActivity(), R.string.new_movie_error_saving);
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            }, 1500);
        }
    };

    private void onTrimCompleted(String videoPath, boolean original) {
        ResultBean resultBean = new ResultBean(videoPath);

        for (int i = 0; i < mMovieEditor.getEditorEffector().getAllMediaEffectData().size(); i++) {
            //滤镜
            if (mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i) instanceof TuSdkMediaFilterEffectData) {
                String filterCode = ((TuSdkMediaFilterEffectData) mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i)).getFilterCode();
                resultBean.getFilterCodes().add(filterCode);
                //背景音乐
            } else if (mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i) instanceof TuSdkMediaAudioEffectData) {
                String musicPath = getRealPath(((TuSdkMediaAudioEffectData) mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i)).getAudioEntry().getUri());
                resultBean.getMusicPaths().add(musicPath);
                //特效
            } else if (mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i) instanceof TuSdkMediaSceneEffectData) {
                String effectCode = ((TuSdkMediaSceneEffectData) mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i)).getEffectCode();
                resultBean.getEffectCodes().add(effectCode);
            }
            //
        }
        //设置muscid
        if (EditorMusicComponent.backgroundMusicSelected != null) {
            resultBean.setMusicId(EditorMusicComponent.backgroundMusicSelected.getMusicId());
        } else if (AppConstants.shootBackgroundMusicBean != null) {
            resultBean.setMusicId(AppConstants.shootBackgroundMusicBean.getMusicId());
        }
        //设置贴纸id
        if (getActivity().getStickerIds() != null && getActivity().getStickerIds().size() > 0) {
            resultBean.setStickerIds(getActivity().getStickerIds());
        }

        AppConstants.shootBackgroundMusicBean = null;

        //


        //  JSONObject.fromObject(Bean).toString()
        //    new JSONObject();

        //        JsonHelper.json()
        // 下一步输出视频
        if (AppConstants.EDIT_TYPE == 1) {
            String jsonString = new Gson().toJson(resultBean);
//            VideoBeautyPlugin.result.success(jsonString);
//            VideoBeautyPlugin.result = null;
            if (mPlayBtn.getVisibility() == View.GONE) mPlayBtn.setVisibility(View.VISIBLE);
            getHomeComponent().setEnable(true);
            getPlayBtn().setClickable(true);
            if (!original) {
                getMovieEditor().getEditorSaver().destroy();
                if (!getActivity().isFromDraft) {
                    //删除临时文件
                    AlbumUtils.deleteFile(getActivity().mVideoPath);
                }
            }
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            }, 1500);
            AppManager.getInstance().finishAllActivity();
        } else {
            getActivity().finish();
            List<MovieInfo> trimPath = new ArrayList<>();
            trimPath.add(new MovieInfo(videoPath, 0));
            // Intent intent = new Intent(MovieRecordFullScreenActivity.this, MovieEditorPreviewActivity.class);
            try {
                Intent intent = new Intent(getActivity(), Class.forName("org.lasque.twsdkvideo.video_beauty.editor.MovieEditorCutActivity"));
                intent.putExtra("videoPaths", (Serializable) trimPath);
                getActivity().startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private String getRealPath(Uri fileUrl) {
        String fileName = null;
        if (fileUrl != null) {
            if (fileUrl.getScheme().toString().compareTo("content") == 0) // content://开头的uri
            {
                Cursor cursor = getActivity().getContentResolver().query(fileUrl, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        fileName = cursor.getString(column_index); // 取出文件路径
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) // file:///开头的uri
            {
                fileName = fileUrl.getPath();
            }
        }
        return fileName;
    }

    public Uri saveVideoToAlbumIfNeed(ComponentActivity currentActivity, final String path) {
        File file = new File(path);
        Uri uri = null;
        // 通过插入到相册的方案
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, file.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        ContentResolver contentResolver = currentActivity.getContentResolver();
        uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            return null;
        }
        // 拷贝到指定uri,如果没有这步操作，android11不会在相册显示
        try {
            OutputStream out = currentActivity.getContentResolver().openOutputStream(uri);
            copyFile(path, out);
            setSaving(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通知刷新相册
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        scanIntent.setData(uri);
        currentActivity.sendBroadcast(scanIntent);
        TuSdk.messageHub().showSuccess(currentActivity, R.string.new_movie_saved);
        return uri;
    }

    public boolean copyFile(String oldPath, OutputStream out) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                // 读入原文件
                InputStream inStream = new FileInputStream(oldPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    out.write(buffer, 0, byteread);
                }
                inStream.close();
                out.close();
                return true;
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 声音加载状态的回调
     */
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioTaskStateListener = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                TuSdk.messageHub().dismissRightNow();
                /**  这里不调用 会导致添加背景音效失败 */
                mMovieEditor.getEditorMixer().notifyLoadCompleted();
            }

        }
    };

    TuSdkSize mCurrentPreviewSize;

    //构造函数
    public MovieEditorController(int videoWidth, int videoHeight, float currentSpeed, float currentLeftPercent, float currentRightPercent, boolean isAlbum, MovieEditorActivity activity, VideoContent holderView, TuSdkMovieEditor.TuSdkMovieEditorOptions options) {
        currentActivity = activity;
        mHolderView = holderView;
        this.isAlbum = isAlbum;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.mCurrentSpeed = currentSpeed;
        this.mCurrentLeftPercent = currentLeftPercent;
        this.mCurrentRightPercent = currentRightPercent;

        mWeakActivity = new WeakReference<>(activity);
        mImageTextRankHelper = new EditorTextAndStickerRankHelper();
        mMovieEditor = new TuSdkMovieEditorImpl(activity, holderView, options);
        //设置音效回调 不设置会导致添加背景音效失败
        mMovieEditor.getEditorMixer().addTaskStateListener(mAudioTaskStateListener);
        //设置转码回调
        mMovieEditor.getEditorTransCoder().addTransCoderProgressListener(mOnTranscoderProgressListener);
        //设置播放回调
        mMovieEditor.getEditorPlayer().addProgressListener(mPlayProgressListener);
        mMovieEditor.getEditorPlayer().addPreviewSizeChangeListener(new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
            @Override
            public void onPreviewSizeChanged(TuSdkSize previewSize) {
                mCurrentPreviewSize = previewSize;
//                mEditorAnimator.setCurrentPreviewSize(previewSize);

            }
        });

        //初始化视图
        init();
        //之前在裁剪页面预加载过 则不用开启转码
//        if(isAlbum){
//            mMovieEditor.setEnableTranscode(false);
//        }else{
//            mMovieEditor.setEnableTranscode(true);
//        }
        mMovieEditor.setEnableTranscode(false);

//        mProgress.setVisibility(View.VISIBLE);

      mMovieEditor.getEditorPlayer().setBackGround(TuSdkContext.getColor(R.color.lsq_edit_player_color));
   //     mMovieEditor.getEditorPlayer().setBackGround(TuSdkContext.getColor(R.color.circular_red));

        mMovieEditor.loadVideo();

        Message message = new Message();
        message.what = 0;
        message.obj = options.videoDataSource.getPath();
        mHandler.sendMessageDelayed(message, 0);
        //加载缩略图
        //    loadVideoThumbList(options.videoDataSource.getPath());

    }

    /**
     * 动画改变监听回调
     **/
    private EditorAnimator.OnAnimationEndListener mOnAnimationEndListener = new EditorAnimator.OnAnimationEndListener() {
        @Override
        public void onShowAnimationStartListener() {
            mCurrentComponent.onAnimationStart();
        }

        @Override
        public void onShowAnimationEndListener() {
            if (mCurrentComponent == getTextComponent()) {
                getTextComponent().backUpDatas();
            }
            if (mCurrentComponent == getStickerComponent()) {
                getStickerComponent().backUpDatas();
            }
            if (mCurrentComponent == getDynamicStickerComponent()) {
                getDynamicStickerComponent().backUpDatas();
            }
            mCurrentComponent.onAnimationEnd();
        }

        @Override
        public void onHideAnimationStartListener() {

        }

        @Override
        public void onHideAnimationEndListener() {

        }
    };
    public int videoWidth, videoHeight;
    public boolean isAlbum;//是从相册跳转过来的

    //构造函数
    public MovieEditorController(int videoWidth, int videoHeight, float currentSpeed, float currentLeftPercent, float currentRightPercent, boolean isAlbum, MovieEditorActivity activity, VideoContent holderView, ArrayList<TuSdkMediaTimeSlice> timeSlice, TuSdkMovieEditor.TuSdkMovieEditorOptions options) {
        currentActivity = activity;
        mHolderView = holderView;
        this.isAlbum = isAlbum;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.mCurrentSpeed = currentSpeed;
        this.mCurrentLeftPercent = currentLeftPercent;
        this.mCurrentRightPercent = currentRightPercent;

        mWeakActivity = new WeakReference<>(activity);
        mImageTextRankHelper = new EditorTextAndStickerRankHelper();
        mMovieEditor = new TuSdkMovieEditorImpl(activity, holderView, options);
        //设置音效回调
        mMovieEditor.getEditorMixer().addTaskStateListener(mAudioTaskStateListener);
        //设置转码回调
        mMovieEditor.getEditorTransCoder().addTransCoderProgressListener(mOnTranscoderProgressListener);
        //设置播放回调
        mMovieEditor.getEditorPlayer().addProgressListener(mPlayProgressListener);
        //初始化视图
        init();
        //之前在裁剪页面预加载过 则不用开启转码
//        if(isAlbum){
//            mMovieEditor.setEnableTranscode(false);
//        }else{
//            mMovieEditor.setEnableTranscode(true);
//        }
        mMovieEditor.setEnableTranscode(false);
        //设置需要编辑的时间区间
        mMovieEditor.getEditorPlayer().setEditTimeSlice(timeSlice);
        mMovieEditor.loadVideo();

        Message message = new Message();
        message.what = 0;
        message.obj = options.videoDataSource.getPath();
        mHandler.sendMessageDelayed(message, 0);
        //加载缩略图
        //  loadVideoThumbList(options.videoDataSource.getPath());
    }

    /**
     * 获取首帧图
     */
    public  Bitmap  getFirstFrameBitmap(String path){
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file=new File(path);//实例化File对象，文件路径为/storage/emulated/0/shipin.mp4 （手机根目录）
        if(!file.exists()){
           return null;
        }
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime(0);  //0表示首帧图片
        mmr.release(); //释放MediaMetadataRetrieve
        return bitmap;
    }

    /**
     * 加载视频缩略图
     */
    public void loadVideoThumbList(String videoPath) {
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                Bitmap originalBitMap =  getFirstFrameBitmap(videoPath);
                Bitmap bitmap = BitmapUtils.getTransparentBitmap(originalBitMap,50);
                getMVComponent().addFirstFrameCoverBitmap(bitmap);
                getTextComponent().addFirstFrameCoverBitmap(bitmap);
                getEffectComponent().addFirstFrameCoverBitmap(bitmap);
                getStickerComponent().addFirstFrameCoverBitmap(bitmap);
                getTransitionsComponent().addFirstFrameCoverBitmap(bitmap);
                getDynamicStickerComponent().addFirstFrameCoverBitmap(bitmap);
                getTrimTimeComponent().addFirstFrameCoverBitmap(bitmap);
                getVoiceoverComponent().addFirstFrameCoverBitmap(bitmap);
                originalBitMap.recycle();
            }
        });


        mThumbBitmapList = new ArrayList<>();
        List<TuSdkMediaDataSource> sourceList = new ArrayList<>();
        sourceList.add(TuSdkMediaDataSource.create(videoPath).get(0));
        try {
            /** 准备视频缩略图抽取器 */
            final TuSdkVideoImageExtractor imageThumbExtractor = new TuSdkVideoImageExtractor(sourceList);
            imageThumbExtractor
                    .setOutputImageSize(TuSdkSize.create(100, 100 * videoHeight / videoWidth)) // 设置抽取的缩略图大小
                    .setExtractFrameCount(10) // 设置抽取的图片数量
                    .setImageListener(new TuSdkVideoImageExtractor.TuSdkVideoImageExtractorListener() {

                        /**
                         * 输出一帧略图信息
                         *
                         * @param videoImage 视频图片
                         * @since v3.2.1
                         */
                        @Override
                        public void onOutputFrameImage(final TuSdkVideoImageExtractor.VideoImage videoImage) {

                            if (isNeedRelease) {
                                ThreadHelper.runThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imageThumbExtractor.release();
                                        isNeedWait = false;
                                    }
                                });
                               // imageThumbExtractor.setImageListener(null);
                                return;
                            }

                            ThreadHelper.post(new Runnable() {
                                @Override
                                public void run() {
                                    mThumbBitmapList.add(videoImage.bitmap);
                                    getMVComponent().addCoverBitmap(videoImage.bitmap);
                                    getTextComponent().addCoverBitmap(videoImage.bitmap);
                                    getEffectComponent().addCoverBitmap(videoImage.bitmap);
                                    getStickerComponent().addCoverBitmap(videoImage.bitmap);
                                    getTransitionsComponent().addCoverBitmap(videoImage.bitmap);
                                    getDynamicStickerComponent().addCoverBitmap(videoImage.bitmap);
                                    getTrimTimeComponent().addCoverBitmap(videoImage.bitmap);
                                    getVoiceoverComponent().addCoverBitmap(videoImage.bitmap);

                                }
                            });
                        }

                        /**
                         * 抽取器抽取完成
                         *
                         * @since v3.2.1
                         */
                        @Override
                        public void onImageExtractorCompleted(List<TuSdkVideoImageExtractor.VideoImage> videoImagesList) {
                            try {
                                copyVideoImagesList = videoImagesList;
                                /** 注意： videoImagesList 需要开发者自己释放 bitmap */
                                if(imageThumbExtractor != null){
                                    ThreadHelper.runThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageThumbExtractor.release();
                                            isNeedWait = false;
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                               
                            }

                        }
                    })
                    .extractImages(); // 抽取图片
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取缩略图列表
     *
     * @return
     */
    public List<Bitmap> getThumbBitmapList() {
        return mThumbBitmapList;
    }

    /**
     * 备忘音频特效数据
     *
     * @param mediaEffectData 音频特效数据
     */
    public void setMusicEffectData(TuSdkMediaAudioEffectData mediaEffectData) {
        this.mMusicEffectData = mediaEffectData;
    }

    /**
     * 获取音频备忘数据
     *
     * @return 获取备忘音频特效数据
     */
    public TuSdkMediaAudioEffectData getMusicEffectData() {
        return this.mMusicEffectData;
    }

    /**
     * 备忘MV特效数据
     *
     * @param mediaEffectData MV特效数据
     */
    public void setMVEffectData(TuSdkMediaEffectData mediaEffectData) {
        this.mMVEffectData = mediaEffectData;
    }

    /**
     * 获取MV备忘数据
     *
     * @return 获取备忘MV特效数据
     */
    public TuSdkMediaEffectData getMVEffectData() {
        return this.mMVEffectData;
    }

    /**
     * 获取最近一次备忘的音频/MV特效数据
     *
     * @return 获取备忘MV特效数据
     **/
    public TuSdkMediaEffectData getMediaEffectData() {
        return this.mMVEffectData == null ? mMusicEffectData : mMVEffectData;
    }

    /**
     * 获取主音量
     *
     * @return
     */
    public float getMasterVolume() {
        return mMasterVolume;
    }

    /**
     * 设置主音量
     *
     * @param volume
     */
    public void setMasterVolume(float volume) {
        this.mMasterVolume = volume;
    }

    private boolean isNeedWait = true;

    /**
     * 初始化视图与动画控制器
     *
     * @since V3.0.0
     */
    private void init() {
        HandlerThread handlerThread = new HandlerThread("LoadImage");
        handlerThread.start();
       mHandler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    //加载缩略图
                    loadVideoThumbList((String) msg.obj);
                }
            }
        };
        if (isAlbum) {
            switchComponent(EditorComponent.EditorComponentType.TrimTime);
        } else {
            switchComponent(EditorComponent.EditorComponentType.Home);
        }


        //初始化动画控制器(缩放预览图，改变底部视图宽高)

        mEditorAnimator = new EditorAnimator(this, mHolderView, videoWidth > videoHeight, isAlbum);
        mEditorAnimator.setAnimationEndListener(mOnAnimationEndListener);

        mPlayBtn = getActivity().findViewById(R.id.lsq_play_btn);
        mProgressContent = getActivity().findViewById(R.id.lsq_editor_load);
        mProgress = getActivity().findViewById(R.id.lsq_editor_load_parogress);
        mHolderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentComponent instanceof EditorHomeComponent || isSaveing) {
                    return;
                }


                if (mCurrentComponent instanceof EditorEffectComponent) {
                    if (((EditorEffectComponent) mCurrentComponent).getmScreenFragment() != null) {
                        ((EditorEffectComponent) mCurrentComponent).getmScreenFragment().editorPlayClick(mPlayBtn.getVisibility() == View.VISIBLE);
                    }
                    if (((EditorEffectComponent) mCurrentComponent).getmTimeFragment() != null) {
                        ((EditorEffectComponent) mCurrentComponent).getmTimeFragment().editorPlayClick(mMovieEditor.getEditorPlayer().isPause());

                    }
                    if (((EditorEffectComponent) mCurrentComponent).getmMagicFragment() != null) {
                        ((EditorEffectComponent) mCurrentComponent).getmMagicFragment().editorPlayClick(mMovieEditor.getEditorPlayer().isPause());

                    }
                }

//

                if (mPlayBtn.getVisibility() == View.GONE) {
                    needLoop = false;
                    mMovieEditor.getEditorPlayer().pausePreview();
                } else {
                    //如果在主页/trimTime/添加背景音乐需要循环播放
                    if (mCurrentComponent instanceof EditorHomeComponent || mCurrentComponent instanceof EditorTrimTimeComponent) {
                        needLoop = true;
                    } else {
                        needLoop = false;
                    }
                    mMovieEditor.getEditorPlayer().startPreview();
                }


            }
        });
    }

    /**
     * 获取{@link MovieEditorActivity} 引用
     *
     * @return MovieEditorActivity Activity的引用
     * @since V3.0.0
     */
    public MovieEditorActivity getActivity() {
        if (mWeakActivity == null || mWeakActivity.get() == null) {
            TLog.e("%s MovieEditorActivity is null !!!", TAG);
            return null;
        }
        return mWeakActivity.get();
    }

    /**
     * 获取视频编辑器的实例 {@link TuSdkMovieEditor}
     *
     * @return TuSdkMovieEditor 视频编辑器的实例
     * @since V3.0.0
     */
    public TuSdkMovieEditor getMovieEditor() {
        if (mMovieEditor == null) {
            TLog.e("%s TuSdkMovieEditor is null !!!", TAG);
            return null;
        }
        return mMovieEditor;
    }

    /**
     * 获取顶部的头
     *
     * @return ViewGroup 头部的View
     */
    public ViewGroup getHeaderView() {
        return getActivity().getHeaderView();
    }


    /**
     * 获取底部的View
     *
     * @return ViewGroup底部的View
     */
    public ViewGroup getBottomView() {
        return getActivity().getBottomView();
    }

    public ViewGroup getTitleView() {
        return getActivity().getTitleView();
    }

    /**
     * 获取视频的父View
     *
     * @since V3.0.0
     */
    public VideoContent getVideoContentView() {
        return mHolderView;
    }

    /**
     * 获取播放按钮
     *
     * @since V3.0.0
     */
    public ImageView getPlayBtn() {
        return mPlayBtn;
    }


    private StickerBottomSheetFragment stickerBottomSheetFragment;

    /**
     * 切换组件
     *
     * @since V3.0.0
     */
    public void switchComponent(EditorComponent.EditorComponentType componentEnum) {
        if (mCurrentComponent != null) mCurrentComponent.detach();

        if (componentEnum == EditorComponent.EditorComponentType.Home ||
                componentEnum == EditorComponent.EditorComponentType.TrimTime ||
                componentEnum == EditorComponent.EditorComponentType.Music) {
            needLoop = true;
        } else {
            needLoop = false;
        }
        getActivity().getTextStickerView().setItemViewEnable(false);


        extracted(componentEnum);
    }

    private void extracted(EditorComponent.EditorComponentType componentEnum) {
        switch (componentEnum) {
            case Home:
                //切换到主页面组件
                getActivity().getTextStickerView().setItemViewEnable(true);
                getActivity().getTextStickerView().setItemViewEnable(true);
                mCurrentComponent = getHomeComponent();
                break;
            case Filter:
                //切换到滤镜组件
                mCurrentComponent = getFilterComponent().setHeadAction();
                break;
            case MV:
                //切换到MV组件
                mCurrentComponent = getMVComponent();
                break;
            case Music:
                //切换到配音组件
                mCurrentComponent = getMusicComponent().setHeadAction();
                break;
            case Text:
                getActivity().getTextStickerView().setItemViewEnable(true);
                //切换到文字组件
                mCurrentComponent = getTextComponent().setHeadAction();
                break;
            case Effect:
                //切换到特效组件(场景特效、时间特效、粒子特效)
                mCurrentComponent = getEffectComponent().setHeadAction();
                break;
            case Sticker:
                //切换到贴纸组件
                mCurrentComponent = getStickerComponent().setHeadAction();

                if (stickerBottomSheetFragment == null) {
                    stickerBottomSheetFragment = StickerBottomSheetFragment.getInstance().setmEditorController(this);
                }
                stickerBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "dialog");

                break;
            case Trim:
                //切换到裁剪组件
                mCurrentComponent = getTrimComponent();
            case TrimTime:
                //切换到裁剪时间范围组件
                mCurrentComponent = getTrimTimeComponent();
                break;
            case TransitionsEffect:
                //切换到转场特效
                mCurrentComponent = getTransitionsComponent();
                break;
            case DynamicSticker:
                mCurrentComponent = getDynamicStickerComponent();
                //切换到录音
            case Voiceover:
                mCurrentComponent = getVoiceoverComponent().setHeadAction();
            default:
                break;
        }
        clearHeaderAndBottom();
        mCurrentComponent.attach();
    }


    /**
     * 清楚Header 和 Bottom 里的View
     *
     * @since V3.0.0
     **/
    private void clearHeaderAndBottom() {
        getHeaderView().removeAllViews();
        getBottomView().removeAllViews();
    }

    /**
     * 获取当前正在使用的组件
     *
     * @return EditorComponent
     */
    public EditorComponent getCurrentComponent() {
        if (mCurrentComponent == null) {
            switchComponent(EditorComponent.EditorComponentType.Home);
        }
        return mCurrentComponent;
    }

    /**
     * 保存视频
     **/
    public void saveVideo() {
        mProgressContent.setVisibility(View.VISIBLE);
        if (!mMovieEditor.getEditorPlayer().isPause()) {
            mMovieEditor.getEditorPlayer().pausePreview();
        }
        //处理Sticker特效
        // 视频原来的输出尺寸
       TuSdkSize outTuSdkSize =  ((TuSdkEditorPlayerImpl) mMovieEditor.getEditorPlayer()).getOutputSize();
        Utils.stickerHandleCompleted(getActivity(), outTuSdkSize,mCurrentPreviewSize,TuSdkContext.getScreenSize(), this);

        //设置保存回调
        mPlayBtn.setVisibility(View.GONE);
        mMovieEditor.getEditorSaver().addSaverProgressListener(mSaveProgressListener);
        setSaving(true);
        mHolderView.setClickable(false);
        ((TuSdkMovieEditorImpl) mMovieEditor).getVideoEncoderSetting().videoQuality = TuSDKVideoEncoderSetting.VideoQuality.RECORD_HIGH1;

        ((TuSdkMovieEditorImpl) mMovieEditor).getVideoEncoderSetting().videoSize = new TuSdkSize(videoWidth, videoHeight);
        isNeedRelease = true;
        ThreadHelper.runThread(new Runnable() {
            @Override
            public void run() {
                while (isNeedWait){

                }
            }
        });
        if (getActivity().isTrim) {
            if (!((mMovieEditor.getEditorEffector().getAllMediaEffectData().size() > 0 || getActivity().getImageStickerView().getStickerItems().size() > 0 || EditorVoiceoverComponent.mMementoVoiceList.size() > 0 || EditorMusicComponent.backgroundMusicSelected != null))) {
                //直接原视频导出
                onTrimCompleted(getActivity().mVideoPath, true);
            } else {
                mMovieEditor.saveVideo();


            }

        } else {//从拍摄页过来的

            if ((!(mCurrentLeftPercent > 0 || mCurrentRightPercent < 1)) && !((mMovieEditor.getEditorEffector().getAllMediaEffectData().size() > 0 || getActivity().getImageStickerView().getStickerItems().size() > 0 || EditorVoiceoverComponent.mMementoVoiceList.size() > 0 || EditorMusicComponent.backgroundMusicSelected != null))) {
                //直接原视频导出
                onTrimCompleted(getActivity().mVideoPath, true);
            } else {
                mMovieEditor.saveVideo();
            }

        }

    }


    /**
     * 是否正在保存中
     *
     * @return true 正在保存 false 已经保存完毕或者出错
     * @since v 3.1.0
     */
    public boolean isSaving() {
        return isSaving;
    }

    /**
     * 设置正在保存的状态
     *
     * @return true 正在保存 false 已经保存完毕或者出错
     * @since v 3.1.0
     */
    private void setSaving(boolean isSaving) {
        this.isSaving = isSaving;
        if (isSaving) {
            getHomeComponent().setEnable(false);
            getPlayBtn().setClickable(false);
        }
    }




    /* ------------- 获取组件实例 --------------- */

    public void goTextComponent() {
        if (mEditorAnimator != null) {
            mEditorAnimator.animatorSwitchComponent(EditorComponent.EditorComponentType.Text);
        } else {
            switchComponent(EditorComponent.EditorComponentType.Text);
        }
    }

    /**
     * 获取主页面组件
     *
     * @return EditorHomeComponent 主页组件实例
     * @since V3.0.0
     */
    public EditorHomeComponent getHomeComponent() {
        if (mHomeComponent == null) {
            mHomeComponent = new EditorHomeComponent(this);
            mHomeComponent.setItemOnClickListener(new EditorHomeComponent.OnItemClickListener() {
                @Override
                public void onClick(EditorHomeComponent.TabType tabType) {
                    TLog.d("%s select tab type is : %s", TAG, tabType);
                    EditorComponent.EditorComponentType componentEnum = EditorComponent.EditorComponentType.Home;
                    if (tabType == EditorHomeComponent.TabType.TrimTime && (mMovieEditor.getEditorEffector().getAllMediaEffectData().size() > 0 || getActivity().getImageStickerView().getStickerItems().size() > 0 || EditorVoiceoverComponent.mMementoVoiceList.size() > 0 || EditorMusicComponent.backgroundMusicSelected != null)) {
                        DialogHelper.discardEditsTip(mMovieEditor.getContext(), new DialogHelper.onRemindSureClickListener() {
                            @Override
                            public void onSureClick() {
                                //Effect 清空
                                mMovieEditor.getEditorEffector().removeAllMediaEffect();
                                getEffectComponent().cleanEffect();
                                //贴纸、文字清空
                                getActivity().getImageStickerView().removeAllSticker();
                                //录音清空
                                getVoiceoverComponent().cleanAudio();
                                //背景音乐清空
                                getMusicComponent().cleanMusic();
                                extracted(tabType, componentEnum);
                                // extracted(componentEnum);
                            }
                        });
                        return;
                    }
                    extracted(tabType, componentEnum);
                }

                private void extracted(EditorHomeComponent.TabType tabType, EditorComponent.EditorComponentType componentEnum) {
                    switch (tabType) {
                        case FilterTab:
                            //当前已经切换为滤镜组件
                            componentEnum = EditorComponent.EditorComponentType.Filter;
                            break;
                        case MVTab:
                            //当前已经切换为MV组件
                            componentEnum = EditorComponent.EditorComponentType.MV;
                            break;
                        case MusicTab:
                            //当前已经切换为配音组件
                            componentEnum = EditorComponent.EditorComponentType.Music;
                            break;
                        case TextTab:
                            //当前已经切换为文字组件
                            componentEnum = EditorComponent.EditorComponentType.Text;
                            break;
                        case EffectTab:
                            //当前已经切换为特效组件 (场景特效、时间特效、粒子特效)
                            componentEnum = EditorComponent.EditorComponentType.Effect;
                            break;
                        case Sticker:
                            //当前切换为贴纸组件
//                            componentEnum = EditorComponent.EditorComponentType.Sticker;
                            openGiphy();
                            return;
//                           break;
                        case Trim:
                            //当前切换为裁剪组件
                            componentEnum = EditorComponent.EditorComponentType.Trim;
                            break;
                        case TrimTime:
                            //当前切换为裁剪组件
                            componentEnum = EditorComponent.EditorComponentType.TrimTime;
                            break;
                        case TransitionsEffect:
                            componentEnum = EditorComponent.EditorComponentType.TransitionsEffect;
                            break;
                        case DynamicStickers:
                            componentEnum = EditorComponent.EditorComponentType.DynamicSticker;
                            break;
                        case Voiceover:
                            componentEnum = EditorComponent.EditorComponentType.Voiceover;
                            break;
                        default:
                            break;
                    }
                    if (mEditorAnimator != null) {
                        mEditorAnimator.animatorSwitchComponent(componentEnum);
                    } else {
                        switchComponent(componentEnum);
                    }
                }
            });
        }
        return mHomeComponent;
    }

    public static class GifValue {
        /**
         * 图片地址
         */
        public ArrayList<String> pngFilePathsFromGifUrl;
        /**
         * 平均每帧 间隔时长
         */
        public int duration;
    }


    /**
     * Giphy Dialog
     */
    private void openGiphy() {
        if(getHomeComponent()!=null){
            getHomeComponent().hideHeaderView();
        }
        GiphyHelper.openGiphy(getActivity(), new GiphyDialogFragment.GifSelectionListener() {
            @Override
            public void onGifSelected(@NonNull Media media, @Nullable String s, @NonNull GPHContentType gphContentType) {
                if(getHomeComponent()!=null){
                    getHomeComponent().showHeaderView();
                }


//                String gifUrl = media.getImages().getOriginal().getGifUrl();
                // 目前获取到图片宽高为200,源图片也有可能是400的
                String gifUrl = media.getImages().getFixedWidth().getGifUrl();
//                String gifUrl2 = media.getImages().getDownsizedSmall().getGifUrl();
//                String gifUrl3 = media.getImages().getPreview().getGifUrl();
//                String gifUrl = media.getImages().getFixedWidthSmall().getGifUrl();

                if(myThread!=null){
                    myThread.close();
                }
                myThread = new MyThread(new Runnable() {
                    @Override
                    public void run() {
                        GifValue gifValue = GifBitmapUtils.getPNGFilePathsFromGifUrl(getActivity().getImageStickerView(), gifUrl, getActivity(), gphContentType, new GifBitmapUtils.DownloadProgress() {
                            @Override
                            public void onDownloadProgress(float percent) {
                                ThreadHelper.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!mProgressContent.isShown())
                                            mProgressContent.setVisibility(View.VISIBLE);
                                        mProgress.setValue(percent * 100);
                                    }
                                }, 0);


                            }
                        });
                        ThreadHelper.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressContent.setVisibility(View.GONE);
                                mProgress.setValue(0);
                                try {
                                    Utils.addGifSticker(getActivity().getImageStickerView(), getMovieEditor().getEditorPlayer().getInputTotalTimeUs(), gifValue, getActivity());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                });
                myThread.start();

            }

            @Override
            public void onDismissed(@NonNull GPHContentType gphContentType) {
                if(getHomeComponent()!=null){
                    getHomeComponent().showHeaderView();
                }
            }

            @Override
            public void didSearchTerm(@NonNull String s) {

            }
        });
    }


    /**
     * 获取滤镜组件
     *
     * @return EditorFilterComponent
     * @since V3.0.0
     */
    public EditorFilterComponent getFilterComponent() {
        if (mFilterComponent == null) {
            mFilterComponent = new EditorFilterComponent(this);
        }
        return mFilterComponent;
    }

    /**
     * 获取MV组件
     *
     * @return EditorMVComponent
     * @since V3.0.0
     */
    public EditorMVComponent getMVComponent() {
        if (mMVComponent == null) {
            mMVComponent = new EditorMVComponent(this);
        }
        return mMVComponent;
    }

    /**
     * 获取音效组件
     *
     * @return EditorMusicComponent
     * @since V3.0.0
     */
    public EditorMusicComponent getMusicComponent() {
        if (mMusicComponent == null) {
            mMusicComponent = new EditorMusicComponent(this, mEditorAnimator, mHolderView, videoWidth > videoHeight, getHomeComponent());


        }
        return mMusicComponent;
    }


    /**
     * 获取文字组件
     *
     * @return EditorTextComponent
     * @since V3.0.0
     */
    public EditorTextComponent getTextComponent() {
        if (mTextComponent == null) {
            mTextComponent = new EditorTextComponent(this);
        }
        return mTextComponent;
    }

    /**
     * 获取特效组件
     *
     * @return EditorEffectComponent
     * @since V3.0.0
     */
    public EditorEffectComponent getEffectComponent() {
        if (mEffectComponent == null) {
            mEffectComponent = new EditorEffectComponent(this);
        }
        return mEffectComponent;
    }

    /**
     * 获取贴纸组件
     *
     * @return EditorStickerComponent
     * @since V3.3.2
     */
    public EditorStickerComponent getStickerComponent() {
        if (mStickerComponent == null) {
            mStickerComponent = new EditorStickerComponent(this, mHolderView);
        }
        return mStickerComponent;
    }

    /**
     * 获取裁剪组件
     *
     * @return EditorTrimComponent
     * @since V3.3.2
     */
    public EditorTrimComponent getTrimComponent() {
        if (mTrimComponent == null) {
            mTrimComponent = new EditorTrimComponent(this);
        }
        return mTrimComponent;
    }

    /**
     * 获取裁剪时间范围组件
     *
     * @return EditorTrimTimeComponent
     * @since V3.3.2
     */
    public EditorTrimTimeComponent getTrimTimeComponent() {
        if (mTrimTimeComponent == null) {
            mTrimTimeComponent = new EditorTrimTimeComponent(this);
        }
        return mTrimTimeComponent;
    }

    /**
     * @return
     */
    public EditorEffectTransitionsComponent getTransitionsComponent() {
        if (mTransitionsComponent == null) {
            mTransitionsComponent = new EditorEffectTransitionsComponent(this);
        }
        return mTransitionsComponent;
    }

    public EditorDynamicStickerComponent getDynamicStickerComponent() {
        if (mDynamicStickerComponent == null) {
            mDynamicStickerComponent = new EditorDynamicStickerComponent(this);
        }
        return mDynamicStickerComponent;
    }

    //获取录音组件
    public EditorVoiceoverComponent getVoiceoverComponent() {
        if (mVoiceoverComponent == null) {
            mVoiceoverComponent = new EditorVoiceoverComponent(this, mHolderView);
        }
        return mVoiceoverComponent;
    }



    /* ---------------------------- 同步Activity的生命周期 --------------------- */

    /**
     * 同步Activity的OnCreate
     *
     * @since V3.0.0
     */
    public void onCreate() {

        if (mCurrentComponent == null) return;
        mCurrentComponent.onCreate();
    }

    /**
     * 同步Activity的onStart
     *
     * @since V3.0.0
     */
    public void onStart() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onStart();
    }

    /**
     * 同步Activity的onResume
     *
     * @since V3.0.0
     */
    public void onResume() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onResume();
    }

    /**
     * 同步Activity的onPause
     *
     * @since V3.0.0
     */
    public void onPause() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onPause();
        if (!mMovieEditor.getEditorPlayer().isPause())
            mMovieEditor.getEditorPlayer().pausePreview();
    }

    /**
     * 同步Activity的onStop
     *
     * @since V3.0.0
     */
    public void onStop() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onStop();
    }

    /**
     * 同步Activity的onDestroy
     *
     * @since V3.0.0
     */
    public void onDestroy() {
        if(myThread!=null){
            myThread.close();
        }
        if (mCurrentComponent == null) return;
        mCurrentComponent.onDestroy();
        for (Bitmap bitmap : mThumbBitmapList) {
            if (!bitmap.isRecycled()) bitmap.recycle();
        }
        if (copyVideoImagesList != null) {
            for (TuSdkVideoImageExtractor.VideoImage videoImage : copyVideoImagesList) {
                if (!videoImage.bitmap.isRecycled()) videoImage.bitmap.recycle();
            }
        }
        mMovieEditor.getEditorPlayer().destroy();
        isNeedRelease = true;
        mHandler.removeMessages(0);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public EditorTextAndStickerRankHelper getImageTextRankHelper() {
        return mImageTextRankHelper;
    }

    /**
     * 组件点击返回时间的回调
     */
    public void onBackEvent() {
        if (mCurrentComponent == null) {
            getActivity().finish();
            return;
        }
        switch (mCurrentComponent.getComponentEnum()) {
            case Home:
                getActivity().finish();
                break;
            case Filter:
            case MV:
            case Music:
            case Text:
            case Effect:
            case Sticker:
            case Trim:
            case TrimTime:
            case TransitionsEffect:
            case Voiceover:
            case DynamicSticker:
                if (mEditorAnimator != null) {
                    mEditorAnimator.animatorSwitchComponent(EditorComponent.EditorComponentType.Home);
                } else {
                    switchComponent(EditorComponent.EditorComponentType.Home);
                }

        }
    }

    public float getmCurrentSpeed() {
        return mCurrentSpeed;
    }

    public void setmCurrentSpeed(float mCurrentSpeed) {
        this.mCurrentSpeed = mCurrentSpeed;
    }

    /**
     * 当前裁剪视频的时间长度 （微秒）
     *
     * @return
     */
    public float getCurrentTotalTimeUs() {
        long totalTimeUs = getMovieEditor().getEditorPlayer().getTotalTimeUs();
        while (totalTimeUs == 0) {
            totalTimeUs = getMovieEditor().getEditorPlayer().getTotalTimeUs();
        }

        return totalTimeUs * (mCurrentRightPercent - mCurrentLeftPercent);
    }

    public float getmCurrentLeftPercent() {
        return mCurrentLeftPercent;
    }

    public void setmCurrentLeftPercent(float mCurrentLeftPercent) {
        this.mCurrentLeftPercent = mCurrentLeftPercent;
    }

    public float getmCurrentRightPercent() {
        return mCurrentRightPercent;
    }

    public void setmCurrentRightPercent(float mCurrentRightPercent) {
        this.mCurrentRightPercent = mCurrentRightPercent;
    }
}
