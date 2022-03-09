/**
 * TuSDK
 * twsdkvideo
 * ImageAlbumFragment.java
 *
 * @author H.ys
 * @Date 2019/5/31 17:14
 * @Copyright (c) 2019 tw. All rights reserved.
 */
package org.lasque.twsdkvideo.video_beauty.album;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import org.greenrobot.eventbus.EventBus;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.api.extend.TuSdkMediaProgress;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaFormat;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoQuality;
import org.lasque.tusdk.core.seles.tusdk.TuSDKMediaTransitionWrap;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlHelper;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import org.lasque.tusdk.video.editor.TuSdkMediaTransitionEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;

import org.lasque.tusdk.api.image.TuSDKMediaMovieCompositionComposer;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.ScrollGridLayoutManager;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.event.StickerEvent;
import org.lasque.twsdkvideo.video_beauty.utils.MD5Util;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;


import static android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;

import at.grabner.circleprogress.CircleProgressView;


/**
 * 图片相册
 */
public class ImageAlbumFragment extends Fragment {

    /**
     * 默认图片合成每张图片的显示时间
     */
    public static final float DEFAULT_IMAGE_SHOW_DURATION = 2;

    /* 最大选择数量 */
    protected int mSelectMax = 1;

    /* 从哪个路口进入 */
    protected int mEnterType;

    //转码进度视图
    private FrameLayout mLoadContent;
    private CircleProgressView mLoadProgress;

    private RecyclerView mRecyclerView;

    private ImageAlbumAdapter mImageAlbumAdapter;

    private int mCurrentPos = -1;

    private LoadVideoTask mLoadImageTask;

    private boolean isEnable = true;

    private Hashtable<String, List<ImageSqlInfo>> htImg;

    public OnMediaInfoIndexListener onMediaInfoIndexListener;

    public interface OnMediaInfoIndexListener {
        void onDataMediaInfoIndex(List<String> listIndex, Hashtable<String, List<ImageSqlInfo>> hashtable);
    }

    private TuSdkMediaProgress mMediaProgress = new TuSdkMediaProgress() {
        @Override
        public void onProgress(final float progress, TuSdkMediaDataSource mediaDataSource, int index, int total) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mLoadContent.setVisibility(View.VISIBLE);
                    mLoadProgress.setValue(progress * 100);
                    ((MediaAlbumActivity) getActivity()).setEnable(false);
                    if (progress >= 1) {
                        mLoadContent.setVisibility(View.GONE);
                        mLoadProgress.setValue(0);
                        ((MediaAlbumActivity) getActivity()).setEnable(true);
                    }
                }
            });
        }

        @Override
        public void onCompleted(Exception e, TuSdkMediaDataSource outputFile, int total) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    ((MediaAlbumActivity) getActivity()).setEnable(true);
                    mLoadContent.setVisibility(View.GONE);
                    mLoadProgress.setValue(0);
                }
            });
            if (!new File(outputFile.getPath()).exists()) {
                TuSdk.messageHub().showToast(getActivity(), R.string.lsq_not_file);
                return;
            }
            Intent intent = new Intent(getActivity(), MovieEditorActivity.class);
            intent.putExtra("videoPath", outputFile.getPath());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            tuSDKMediaMovieCompositionComposer.cancelExport();
        }
    };

    public void setOnImangeInfoIndexListener(OnMediaInfoIndexListener onMediaInfoIndexListener){
        this.onMediaInfoIndexListener = onMediaInfoIndexListener;
    }

    private View.OnClickListener mNextStepClickListener = new TuSdkViewHelper.OnSafeClickListener() {
        @Override
        public void onSafeClick(View v) {
            if (mEnterType == AppConstants.STICKER_ENTER) {
                if (mImageAlbumAdapter.getSelectedVideoInfo().size() >= 1) {
                    StickerEvent stickerEvent = new StickerEvent();
                    stickerEvent.setImageSqlInfo(mImageAlbumAdapter.getSelectedVideoInfo().get(0));
                    EventBus.getDefault().post(stickerEvent);
                    getActivity().finish();
                }
            } else {
                if (mImageAlbumAdapter == null || mImageAlbumAdapter.getSelectedVideoInfo().size() < 3)
                    TuSdk.messageHub().showToast(getActivity(), R.string.lsq_select_image_hint);
                else {
                    startImageToVideo();
                }
            }
        }
    };

    private TuSDKMediaMovieCompositionComposer tuSDKMediaMovieCompositionComposer;

    /**
     *
     */
    private void startImageToVideo() {
        tuSDKMediaMovieCompositionComposer = new TuSDKMediaMovieCompositionComposer();
        tuSDKMediaMovieCompositionComposer.setImageSource(mImageAlbumAdapter.getSelectedVideoInfo());
        tuSDKMediaMovieCompositionComposer.setVideoFormat(TuSdkMediaFormat.buildSafeVideoEncodecFormat(tuSDKMediaMovieCompositionComposer.getRecommendOutputSize(), TuSdkVideoQuality.RECORD_HIGH1, COLOR_FormatSurface));
        tuSDKMediaMovieCompositionComposer.setMediaProgress(mMediaProgress);
        tuSDKMediaMovieCompositionComposer.setDuration(DEFAULT_IMAGE_SHOW_DURATION);
        addEffectData(mImageAlbumAdapter.getSelectedVideoInfo());
//        设置是否保存到相册,与设置OutputFilePath相冲突
//        tuSDKMediaMovieCompositionComposer.saveToAlbum(true);
        tuSDKMediaMovieCompositionComposer.setOutputFilePath(TuSdk.getAppTempPath().getPath() + "/LSQ_" + System.currentTimeMillis() + ".mp4");
        tuSDKMediaMovieCompositionComposer.startExport();


    }

    private void addEffectData(List<ImageSqlInfo> selectedVideoInfo) {
        for (int i = 1; i < selectedVideoInfo.size(); i++) {
            TuSdkMediaTransitionEffectData data = new TuSdkMediaTransitionEffectData(TuSDKMediaTransitionWrap.TuSDKMediaTransitionType.TuSDKMediaTransitionTypePullInLeft);
            long startTime = (long) ((i * DEFAULT_IMAGE_SHOW_DURATION * TuSdkTimeRange.BASE_TIME_US) - (TuSdkTimeRange.BASE_TIME_US / 5));
            long endTime = (long) (((i + 1) * DEFAULT_IMAGE_SHOW_DURATION * TuSdkTimeRange.BASE_TIME_US) + (TuSdkTimeRange.BASE_TIME_US / 5 * 4));
            data.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTime, endTime));
            tuSDKMediaMovieCompositionComposer.addMediaEffect(data);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.movie_album_fragment, container, false);
        mRecyclerView = (RecyclerView) baseView.findViewById(R.id.lsq_movie_selector_recyclerView);
        GridLayoutManager gridLayoutManager = new ScrollGridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mLoadContent = baseView.findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = baseView.findViewById(R.id.lsq_editor_cut_load_parogress);
        mSelectMax = getActivity().getIntent().getIntExtra("selectMax", 1);
        mEnterType = getActivity().getIntent().getIntExtra("enterType", -1);
        htImg = new Hashtable<>();
        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoadImageTask = new LoadVideoTask();
        mLoadImageTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadImageTask != null)
            mLoadImageTask.cancel(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 101) {
            ImageSqlInfo info = (ImageSqlInfo) data.getSerializableExtra("videoInfo");
            if (info != null && !contains(mImageAlbumAdapter.getSelectedVideoInfo(), info))
                mImageAlbumAdapter.updateSelectedImagePosition(mCurrentPos);
            else if (info == null && mImageAlbumAdapter.getVideoInfoList().size() > 0 && mCurrentPos != -1)
                // 取消选中
                if (contains(mImageAlbumAdapter.getSelectedVideoInfo(), mImageAlbumAdapter.getVideoInfoList().get(mCurrentPos)))
                    mImageAlbumAdapter.updateSelectedImagePosition(mCurrentPos);
        }
    }

    private boolean contains(List<ImageSqlInfo> movieInfos, ImageSqlInfo movieInfo) {
        for (ImageSqlInfo info : movieInfos) {
            if (info.path.equals(movieInfo.path)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 将扫描的视频添加到集合中
     */
    public List<ImageSqlInfo> getImageList() {
        return ImageSqlHelper.getPhotoList(getActivity().getContentResolver(), true);
    }

    public View.OnClickListener getNextStepClickListener() {
        return mNextStepClickListener;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable;
        mRecyclerView.setEnabled(isEnable);
        ((ScrollGridLayoutManager) mRecyclerView.getLayoutManager()).setScrollEnabled(isEnable);
    }

    public boolean isEnable() {
        return isEnable;
    }

    /**
     * 处理所有的目录
     * @param imageSqlInfoList
     */
    public void handleAllIndex(List<ImageSqlInfo> imageSqlInfoList){
        if(imageSqlInfoList != null && imageSqlInfoList.size() > 0)
        {
            String
                    dirName;
            for(ImageSqlInfo imageSqlInfo : imageSqlInfoList)
            {
                if(imageSqlInfo != null)
                {
                    // 文件夹
                    if(!TextUtils.isEmpty(dirName = dirName(imageSqlInfo.path)))
                    {
                        List<ImageSqlInfo> ltImageSqlInfo = htImg.get(dirName);
                        if(ltImageSqlInfo == null)
                        {
                            htImg.put(dirName, ltImageSqlInfo = new ArrayList<>());
                        }
                        ltImageSqlInfo.add(imageSqlInfo);
                    }
                }
            }
        }
    }

    /**
     * 获取文件夹名
     * @param path
     * @return
     */
    private String dirName(String path)
    {
        if(!TextUtils.isEmpty(path))
        {
            try
            {
                String s1 = path.substring(0, path.lastIndexOf(File.separator));
                return s1.substring(s1.lastIndexOf(File.separator) + 1);
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
            }
        }
        return null;
    }

    public List<String> getAllDir()
    {
        if(htImg != null && htImg.size() > 0)
        {
            Set<String> set = htImg.keySet();
            if(set.size() > 0)
            {
                String[] sArr = new String[set.size()];
                set.toArray(sArr);

                List<String> lt = new ArrayList<>(set.size() + 1);
                lt.add(getContext().getResources().getString(R.string.record_local_album_all_index));
                lt.addAll(Arrays.asList(sArr));

                return lt;
            }
        }
        return null;
    }

    /**
     * 相册加载
     */
    class LoadVideoTask extends AsyncTask<Void, Integer, List<ImageSqlInfo>> {

        @Override
        protected List<ImageSqlInfo> doInBackground(Void... voids) {
            return getImageList();
        }

        @Override
        protected void onPreExecute() {
            TuProgressHub.showToast(getActivity(), "数据加载中...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<ImageSqlInfo> imageInfos) {
            TuProgressHub.dismiss();
            handleAllIndex(imageInfos);
            if (imageInfos == null) imageInfos = new ArrayList<>();
            if (mImageAlbumAdapter == null) {
                mImageAlbumAdapter = new ImageAlbumAdapter(getActivity(), imageInfos, mSelectMax);
                mImageAlbumAdapter.setOnItemClickListener(new ImageAlbumAdapter.OnItemClickListener() {
                    @Override
                    public void onSelectClick(View view, ImageSqlInfo item, int position) {
                        if (!isEnable) return;
                        mImageAlbumAdapter.updateSelectedImagePosition(position);
                    }

                    @Override
                    public void onClick(View view, ImageSqlInfo item, int position) {
                        if (!isEnable) return;

                        mImageAlbumAdapter.updateSelectedImagePosition(position);
                        //没有next按钮，点击直接打开
                        if (mEnterType == AppConstants.STICKER_ENTER) {
                            if (mImageAlbumAdapter.getSelectedVideoInfo().size() >= 1) {
                                StickerEvent stickerEvent = new StickerEvent();
                                stickerEvent.setImageSqlInfo(mImageAlbumAdapter.getSelectedVideoInfo().get(0));
                                EventBus.getDefault().post(stickerEvent);
                                getActivity().finish();
                            }
                        } else {
                            if (mImageAlbumAdapter == null || mImageAlbumAdapter.getSelectedVideoInfo().size() < 3)
                                TuSdk.messageHub().showToast(getActivity(), R.string.lsq_select_image_hint);
                            else {
                                startImageToVideo();
                            }
                        }

                        /*if(mEnterType != AppConstants.STICKER_ENTER){
                            mCurrentPos = position;
                            Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("currentImage", item);
                            bundle.putSerializable("imagePath", (ArrayList<ImageSqlInfo>) mImageAlbumAdapter.getSelectedVideoInfo());
                            bundle.putInt("selectMax", mSelectMax);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 101);
                        }*/
                    }
                });
                mRecyclerView.setAdapter(mImageAlbumAdapter);
            }
            if (!mImageAlbumAdapter.getVideoInfoList().equals(imageInfos)) {
                mImageAlbumAdapter.setVideoInfoList(imageInfos);
            }

            //返回目录数据
            if(onMediaInfoIndexListener != null){
                onMediaInfoIndexListener.onDataMediaInfoIndex(getAllDir(), htImg);
            }
        }
    }

    public List<ImageSqlInfo> getMediaList(String dirName)
    {
        if(!TextUtils.isEmpty(dirName))
        {
            if(getContext().getResources().getString(R.string.record_local_album_all_index).equals(dirName))
            {
                List<ImageSqlInfo> lt = new ArrayList<>();
                for(Map.Entry<String, List<ImageSqlInfo>> entry : htImg.entrySet())
                {
                    List<ImageSqlInfo> ltTmp = entry.getValue();
                    if(ltTmp != null && ltTmp.size() > 0)
                    {
                        lt.addAll(ltTmp);
                    }
                }
                return lt;
            }
            return htImg.get(dirName);
        }
        return null;
    }

    public void setVideoAlbumAdapterData(String mediaTitle){
        if(!TextUtils.isEmpty(mediaTitle)) {
            List<ImageSqlInfo> movieInfos = getMediaList(mediaTitle);
            if (mImageAlbumAdapter.getVideoInfoList().size() != movieInfos.size() || !(MD5Util.crypt(mImageAlbumAdapter.getVideoInfoList().toString()).equals(MD5Util.crypt(movieInfos.toString())))) {
                mImageAlbumAdapter.setVideoInfoList(movieInfos);
            }
        }
    }
}
