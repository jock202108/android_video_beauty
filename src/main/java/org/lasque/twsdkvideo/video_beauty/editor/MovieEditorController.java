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
 * ?????????????????????
 */
public class MovieEditorController {
    private static final String TAG = "MovieEditorController";
    /* --------------- SDK?????? ----------------*/
    //????????????????????????
    private TuSdkMovieEditor mMovieEditor;
    private FragmentActivity currentActivity;
    //???????????????
    private float mMasterVolume = 0.5f;
    //???????????????????????????
    private TuSdkMediaAudioEffectData mMusicEffectData;
    //MV????????????
    private TuSdkMediaEffectData mMVEffectData;
    /*---------------- ???????????? ----------------*/
    //??????Activity?????????
    private WeakReference<MovieEditorActivity> mWeakActivity;
    //??????View
    private VideoContent mHolderView;
    //????????????
    public ImageView mPlayBtn;
    //?????????????????????
    public FrameLayout mProgressContent;
    //????????????
    public CircleProgressView mProgress;
    //???????????????
    private EditorAnimator mEditorAnimator;

    /*---------- ???????????? ---------*/
    //???????????????????????????
    public static EditorComponent mCurrentComponent;
    //???????????????
    private EditorHomeComponent mHomeComponent;
    //????????????
    private EditorFilterComponent mFilterComponent;
    //MV??????
    private EditorMVComponent mMVComponent;
    //????????????
    private EditorMusicComponent mMusicComponent;
    //????????????
    private EditorTextComponent mTextComponent;
    //????????????
    private EditorEffectComponent mEffectComponent;
    //????????????
    private EditorStickerComponent mStickerComponent;
    //????????????
    private EditorTrimComponent mTrimComponent;
    //????????????????????????
    private EditorTrimTimeComponent mTrimTimeComponent;
    //??????????????????
    private EditorEffectTransitionsComponent mTransitionsComponent;

    private EditorDynamicStickerComponent mDynamicStickerComponent;

    private EditorVoiceoverComponent mVoiceoverComponent;

    //???????????????
    private List<Bitmap> mThumbBitmapList = new ArrayList<>();
    //??????????????????
    private boolean isSaving = false;
    private float mCurrentSpeed = 1f;
    private float mCurrentLeftPercent = 0f;
    private float mCurrentRightPercent = 1.0f;

    //????????????????????????????????????????????????onStateChanged???0???1?????????????????????1????????????????????????????????????
    private boolean isPlaying = false;
    private boolean needLoop = true;//????????????????????????

    private boolean isNeedRelease = false;
    public boolean isSaveing = false;
    private MyThread myThread;

    private List<TuSdkVideoImageExtractor.VideoImage> copyVideoImagesList;

    /**
     * ????????????????????????????????????
     **/
    private EditorTextAndStickerRankHelper mImageTextRankHelper;
    private ViewGroup mMusicLayout;


    private Handler mHandler ;

    /**
     * ?????????????????????????????????????????????????????????????????????Activity???????????????????????????
     * ????????????
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
     * ????????????
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
                // ??????
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
                // ??????
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
     * ????????????
     **/
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        long changeTime = 0;

        @Override
        public void onStateChanged(int state) {//0 ???????????????  1?????????
            if (mCurrentComponent instanceof EditorHomeComponent || mCurrentComponent instanceof EditorTrimTimeComponent) {
                mPlayBtn.setVisibility(state == 1 && !isSaving && !needLoop ? View.VISIBLE : View.GONE);

                if (state == 0) {//0 ???????????????

                } else {// 1?????????
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
//            Log.i("======Video onProgress", "???????????????????????? playbackTimeUs" + playbackTimeUs + "??????????????? mCurrentLeftPercent * totalTimeUs" + mCurrentLeftPercent * totalTimeUs);
            if ((mCurrentComponent instanceof EditorTrimTimeComponent))
                getTrimTimeComponent().setVideoPlayPercent(percentage);
//            if (playPercent < mCurrentLeftPercent || playPercent >= mCurrentRightPercent) {
            //??????1s ?????????????????????????????????????????????????????????????????????????????????????????????
            // ??????????????????????????????
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
                    if (needLoop) { //????????????????????????
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
     * ????????????
     **/
    private TuSdkEditorSaver.TuSdkSaverProgressListener mSaveProgressListener = new TuSdkEditorSaver.TuSdkSaverProgressListener() {
        @Override
        public void onProgress(float progress) {
            if (mPlayBtn.getVisibility() == View.VISIBLE) mPlayBtn.setVisibility(View.GONE);
            mProgress.setValue(progress * 100);
        }

        @Override
        public void onCompleted(TuSdkMediaDataSource outputFile) {
            //????????????????????? outputFile.getPath()
            isSaving = false;
            mProgressContent.setVisibility(View.GONE);
            mProgress.setValue(0);
            //android 11 ??????????????????????????????
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
            /// ????????????????????????????????????,??????????????????
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
            //??????
            if (mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i) instanceof TuSdkMediaFilterEffectData) {
                String filterCode = ((TuSdkMediaFilterEffectData) mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i)).getFilterCode();
                resultBean.getFilterCodes().add(filterCode);
                //????????????
            } else if (mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i) instanceof TuSdkMediaAudioEffectData) {
                String musicPath = getRealPath(((TuSdkMediaAudioEffectData) mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i)).getAudioEntry().getUri());
                resultBean.getMusicPaths().add(musicPath);
                //??????
            } else if (mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i) instanceof TuSdkMediaSceneEffectData) {
                String effectCode = ((TuSdkMediaSceneEffectData) mMovieEditor.getEditorEffector().getAllMediaEffectData().get(i)).getEffectCode();
                resultBean.getEffectCodes().add(effectCode);
            }
            //
        }
        //??????muscid
        if (EditorMusicComponent.backgroundMusicSelected != null) {
            resultBean.setMusicId(EditorMusicComponent.backgroundMusicSelected.getMusicId());
        } else if (AppConstants.shootBackgroundMusicBean != null) {
            resultBean.setMusicId(AppConstants.shootBackgroundMusicBean.getMusicId());
        }
        //????????????id
        if (getActivity().getStickerIds() != null && getActivity().getStickerIds().size() > 0) {
            resultBean.setStickerIds(getActivity().getStickerIds());
        }

        AppConstants.shootBackgroundMusicBean = null;

        //


        //  JSONObject.fromObject(Bean).toString()
        //    new JSONObject();

        //        JsonHelper.json()
        // ?????????????????????
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
                    //??????????????????
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
            if (fileUrl.getScheme().toString().compareTo("content") == 0) // content://?????????uri
            {
                Cursor cursor = getActivity().getContentResolver().query(fileUrl, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    try {
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        fileName = cursor.getString(column_index); // ??????????????????
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } finally {
                        cursor.close();
                    }
                }
            } else if (fileUrl.getScheme().compareTo("file") == 0) // file:///?????????uri
            {
                fileName = fileUrl.getPath();
            }
        }
        return fileName;
    }

    public Uri saveVideoToAlbumIfNeed(ComponentActivity currentActivity, final String path) {
        File file = new File(path);
        Uri uri = null;
        // ??????????????????????????????
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, file.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
        ContentResolver contentResolver = currentActivity.getContentResolver();
        uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            return null;
        }
        // ???????????????uri,???????????????????????????android11?????????????????????
        try {
            OutputStream out = currentActivity.getContentResolver().openOutputStream(uri);
            copyFile(path, out);
            setSaving(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ??????????????????
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
                // ???????????????
                InputStream inStream = new FileInputStream(oldPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //????????? ????????????
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
     * ???????????????????????????
     */
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioTaskStateListener = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                TuSdk.messageHub().dismissRightNow();
                /**  ??????????????? ????????????????????????????????? */
                mMovieEditor.getEditorMixer().notifyLoadCompleted();
            }

        }
    };

    TuSdkSize mCurrentPreviewSize;

    //????????????
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
        //?????????????????? ??????????????????????????????????????????
        mMovieEditor.getEditorMixer().addTaskStateListener(mAudioTaskStateListener);
        //??????????????????
        mMovieEditor.getEditorTransCoder().addTransCoderProgressListener(mOnTranscoderProgressListener);
        //??????????????????
        mMovieEditor.getEditorPlayer().addProgressListener(mPlayProgressListener);
        mMovieEditor.getEditorPlayer().addPreviewSizeChangeListener(new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
            @Override
            public void onPreviewSizeChanged(TuSdkSize previewSize) {
                mCurrentPreviewSize = previewSize;
//                mEditorAnimator.setCurrentPreviewSize(previewSize);

            }
        });

        //???????????????
        init();
        //????????????????????????????????? ?????????????????????
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
        //???????????????
        //    loadVideoThumbList(options.videoDataSource.getPath());

    }

    /**
     * ????????????????????????
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
    public boolean isAlbum;//???????????????????????????

    //????????????
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
        //??????????????????
        mMovieEditor.getEditorMixer().addTaskStateListener(mAudioTaskStateListener);
        //??????????????????
        mMovieEditor.getEditorTransCoder().addTransCoderProgressListener(mOnTranscoderProgressListener);
        //??????????????????
        mMovieEditor.getEditorPlayer().addProgressListener(mPlayProgressListener);
        //???????????????
        init();
        //????????????????????????????????? ?????????????????????
//        if(isAlbum){
//            mMovieEditor.setEnableTranscode(false);
//        }else{
//            mMovieEditor.setEnableTranscode(true);
//        }
        mMovieEditor.setEnableTranscode(false);
        //?????????????????????????????????
        mMovieEditor.getEditorPlayer().setEditTimeSlice(timeSlice);
        mMovieEditor.loadVideo();

        Message message = new Message();
        message.what = 0;
        message.obj = options.videoDataSource.getPath();
        mHandler.sendMessageDelayed(message, 0);
        //???????????????
        //  loadVideoThumbList(options.videoDataSource.getPath());
    }

    /**
     * ???????????????
     */
    public  Bitmap  getFirstFrameBitmap(String path){
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();//?????????MediaMetadataRetriever??????
        File file=new File(path);//?????????File????????????????????????/storage/emulated/0/shipin.mp4 ?????????????????????
        if(!file.exists()){
           return null;
        }
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime(0);  //0??????????????????
        mmr.release(); //??????MediaMetadataRetrieve
        return bitmap;
    }

    /**
     * ?????????????????????
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
            /** ?????????????????????????????? */
            final TuSdkVideoImageExtractor imageThumbExtractor = new TuSdkVideoImageExtractor(sourceList);
            imageThumbExtractor
                    .setOutputImageSize(TuSdkSize.create(100, 100 * videoHeight / videoWidth)) // ??????????????????????????????
                    .setExtractFrameCount(10) // ???????????????????????????
                    .setImageListener(new TuSdkVideoImageExtractor.TuSdkVideoImageExtractorListener() {

                        /**
                         * ????????????????????????
                         *
                         * @param videoImage ????????????
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
                         * ?????????????????????
                         *
                         * @since v3.2.1
                         */
                        @Override
                        public void onImageExtractorCompleted(List<TuSdkVideoImageExtractor.VideoImage> videoImagesList) {
                            try {
                                copyVideoImagesList = videoImagesList;
                                /** ????????? videoImagesList ??????????????????????????? bitmap */
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
                    .extractImages(); // ????????????
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public List<Bitmap> getThumbBitmapList() {
        return mThumbBitmapList;
    }

    /**
     * ????????????????????????
     *
     * @param mediaEffectData ??????????????????
     */
    public void setMusicEffectData(TuSdkMediaAudioEffectData mediaEffectData) {
        this.mMusicEffectData = mediaEffectData;
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????????????????
     */
    public TuSdkMediaAudioEffectData getMusicEffectData() {
        return this.mMusicEffectData;
    }

    /**
     * ??????MV????????????
     *
     * @param mediaEffectData MV????????????
     */
    public void setMVEffectData(TuSdkMediaEffectData mediaEffectData) {
        this.mMVEffectData = mediaEffectData;
    }

    /**
     * ??????MV????????????
     *
     * @return ????????????MV????????????
     */
    public TuSdkMediaEffectData getMVEffectData() {
        return this.mMVEffectData;
    }

    /**
     * ?????????????????????????????????/MV????????????
     *
     * @return ????????????MV????????????
     **/
    public TuSdkMediaEffectData getMediaEffectData() {
        return this.mMVEffectData == null ? mMusicEffectData : mMVEffectData;
    }

    /**
     * ???????????????
     *
     * @return
     */
    public float getMasterVolume() {
        return mMasterVolume;
    }

    /**
     * ???????????????
     *
     * @param volume
     */
    public void setMasterVolume(float volume) {
        this.mMasterVolume = volume;
    }

    private boolean isNeedWait = true;

    /**
     * ?????????????????????????????????
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
                    //???????????????
                    loadVideoThumbList((String) msg.obj);
                }
            }
        };
        if (isAlbum) {
            switchComponent(EditorComponent.EditorComponentType.TrimTime);
        } else {
            switchComponent(EditorComponent.EditorComponentType.Home);
        }


        //????????????????????????(??????????????????????????????????????????)

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
                    //???????????????/trimTime/????????????????????????????????????
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
     * ??????{@link MovieEditorActivity} ??????
     *
     * @return MovieEditorActivity Activity?????????
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
     * ?????????????????????????????? {@link TuSdkMovieEditor}
     *
     * @return TuSdkMovieEditor ????????????????????????
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
     * ??????????????????
     *
     * @return ViewGroup ?????????View
     */
    public ViewGroup getHeaderView() {
        return getActivity().getHeaderView();
    }


    /**
     * ???????????????View
     *
     * @return ViewGroup?????????View
     */
    public ViewGroup getBottomView() {
        return getActivity().getBottomView();
    }

    public ViewGroup getTitleView() {
        return getActivity().getTitleView();
    }

    /**
     * ??????????????????View
     *
     * @since V3.0.0
     */
    public VideoContent getVideoContentView() {
        return mHolderView;
    }

    /**
     * ??????????????????
     *
     * @since V3.0.0
     */
    public ImageView getPlayBtn() {
        return mPlayBtn;
    }


    private StickerBottomSheetFragment stickerBottomSheetFragment;

    /**
     * ????????????
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
                //????????????????????????
                getActivity().getTextStickerView().setItemViewEnable(true);
                getActivity().getTextStickerView().setItemViewEnable(true);
                mCurrentComponent = getHomeComponent();
                break;
            case Filter:
                //?????????????????????
                mCurrentComponent = getFilterComponent().setHeadAction();
                break;
            case MV:
                //?????????MV??????
                mCurrentComponent = getMVComponent();
                break;
            case Music:
                //?????????????????????
                mCurrentComponent = getMusicComponent().setHeadAction();
                break;
            case Text:
                getActivity().getTextStickerView().setItemViewEnable(true);
                //?????????????????????
                mCurrentComponent = getTextComponent().setHeadAction();
                break;
            case Effect:
                //?????????????????????(??????????????????????????????????????????)
                mCurrentComponent = getEffectComponent().setHeadAction();
                break;
            case Sticker:
                //?????????????????????
                mCurrentComponent = getStickerComponent().setHeadAction();

                if (stickerBottomSheetFragment == null) {
                    stickerBottomSheetFragment = StickerBottomSheetFragment.getInstance().setmEditorController(this);
                }
                stickerBottomSheetFragment.show(getActivity().getSupportFragmentManager(), "dialog");

                break;
            case Trim:
                //?????????????????????
                mCurrentComponent = getTrimComponent();
            case TrimTime:
                //?????????????????????????????????
                mCurrentComponent = getTrimTimeComponent();
                break;
            case TransitionsEffect:
                //?????????????????????
                mCurrentComponent = getTransitionsComponent();
                break;
            case DynamicSticker:
                mCurrentComponent = getDynamicStickerComponent();
                //???????????????
            case Voiceover:
                mCurrentComponent = getVoiceoverComponent().setHeadAction();
            default:
                break;
        }
        clearHeaderAndBottom();
        mCurrentComponent.attach();
    }


    /**
     * ??????Header ??? Bottom ??????View
     *
     * @since V3.0.0
     **/
    private void clearHeaderAndBottom() {
        getHeaderView().removeAllViews();
        getBottomView().removeAllViews();
    }

    /**
     * ?????????????????????????????????
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
     * ????????????
     **/
    public void saveVideo() {
        mProgressContent.setVisibility(View.VISIBLE);
        if (!mMovieEditor.getEditorPlayer().isPause()) {
            mMovieEditor.getEditorPlayer().pausePreview();
        }
        //??????Sticker??????
        // ???????????????????????????
       TuSdkSize outTuSdkSize =  ((TuSdkEditorPlayerImpl) mMovieEditor.getEditorPlayer()).getOutputSize();
        Utils.stickerHandleCompleted(getActivity(), outTuSdkSize,mCurrentPreviewSize,TuSdkContext.getScreenSize(), this);

        //??????????????????
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
                //?????????????????????
                onTrimCompleted(getActivity().mVideoPath, true);
            } else {
                mMovieEditor.saveVideo();


            }

        } else {//?????????????????????

            if ((!(mCurrentLeftPercent > 0 || mCurrentRightPercent < 1)) && !((mMovieEditor.getEditorEffector().getAllMediaEffectData().size() > 0 || getActivity().getImageStickerView().getStickerItems().size() > 0 || EditorVoiceoverComponent.mMementoVoiceList.size() > 0 || EditorMusicComponent.backgroundMusicSelected != null))) {
                //?????????????????????
                onTrimCompleted(getActivity().mVideoPath, true);
            } else {
                mMovieEditor.saveVideo();
            }

        }

    }


    /**
     * ?????????????????????
     *
     * @return true ???????????? false ??????????????????????????????
     * @since v 3.1.0
     */
    public boolean isSaving() {
        return isSaving;
    }

    /**
     * ???????????????????????????
     *
     * @return true ???????????? false ??????????????????????????????
     * @since v 3.1.0
     */
    private void setSaving(boolean isSaving) {
        this.isSaving = isSaving;
        if (isSaving) {
            getHomeComponent().setEnable(false);
            getPlayBtn().setClickable(false);
        }
    }




    /* ------------- ?????????????????? --------------- */

    public void goTextComponent() {
        if (mEditorAnimator != null) {
            mEditorAnimator.animatorSwitchComponent(EditorComponent.EditorComponentType.Text);
        } else {
            switchComponent(EditorComponent.EditorComponentType.Text);
        }
    }

    /**
     * ?????????????????????
     *
     * @return EditorHomeComponent ??????????????????
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
                                //Effect ??????
                                mMovieEditor.getEditorEffector().removeAllMediaEffect();
                                getEffectComponent().cleanEffect();
                                //?????????????????????
                                getActivity().getImageStickerView().removeAllSticker();
                                //????????????
                                getVoiceoverComponent().cleanAudio();
                                //??????????????????
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
                            //?????????????????????????????????
                            componentEnum = EditorComponent.EditorComponentType.Filter;
                            break;
                        case MVTab:
                            //?????????????????????MV??????
                            componentEnum = EditorComponent.EditorComponentType.MV;
                            break;
                        case MusicTab:
                            //?????????????????????????????????
                            componentEnum = EditorComponent.EditorComponentType.Music;
                            break;
                        case TextTab:
                            //?????????????????????????????????
                            componentEnum = EditorComponent.EditorComponentType.Text;
                            break;
                        case EffectTab:
                            //????????????????????????????????? (??????????????????????????????????????????)
                            componentEnum = EditorComponent.EditorComponentType.Effect;
                            break;
                        case Sticker:
                            //???????????????????????????
//                            componentEnum = EditorComponent.EditorComponentType.Sticker;
                            openGiphy();
                            return;
//                           break;
                        case Trim:
                            //???????????????????????????
                            componentEnum = EditorComponent.EditorComponentType.Trim;
                            break;
                        case TrimTime:
                            //???????????????????????????
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
         * ????????????
         */
        public ArrayList<String> pngFilePathsFromGifUrl;
        /**
         * ???????????? ????????????
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
                // ??????????????????????????????200,????????????????????????400???
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
     * ??????????????????
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
     * ??????MV??????
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
     * ??????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ??????????????????????????????
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

    //??????????????????
    public EditorVoiceoverComponent getVoiceoverComponent() {
        if (mVoiceoverComponent == null) {
            mVoiceoverComponent = new EditorVoiceoverComponent(this, mHolderView);
        }
        return mVoiceoverComponent;
    }



    /* ---------------------------- ??????Activity??????????????? --------------------- */

    /**
     * ??????Activity???OnCreate
     *
     * @since V3.0.0
     */
    public void onCreate() {

        if (mCurrentComponent == null) return;
        mCurrentComponent.onCreate();
    }

    /**
     * ??????Activity???onStart
     *
     * @since V3.0.0
     */
    public void onStart() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onStart();
    }

    /**
     * ??????Activity???onResume
     *
     * @since V3.0.0
     */
    public void onResume() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onResume();
    }

    /**
     * ??????Activity???onPause
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
     * ??????Activity???onStop
     *
     * @since V3.0.0
     */
    public void onStop() {
        if (mCurrentComponent == null) return;
        mCurrentComponent.onStop();
    }

    /**
     * ??????Activity???onDestroy
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
     * ?????????????????????????????????
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
     * ????????????????????????????????? ????????????
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
