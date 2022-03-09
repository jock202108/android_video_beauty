/**
 *
 * twsdkvideo
 * MovieAlbumFragment.java
 *
 * @author H.ys
 * @Date 2019/5/31 17:05
 * @Copyright (c) 2019 tw. All rights reserved.
 */
package org.lasque.twsdkvideo.video_beauty.album;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.common.TuSDKMediaDataSource;
import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.media.codec.suit.mutablePlayer.AVAssetFile;
import org.lasque.tusdk.core.media.codec.video.TuSdkVideoInfo;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.impl.view.widget.TuProgressHub;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.utils.DarkModeUtils;
import org.lasque.twsdkvideo.video_beauty.utils.MD5Util;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MovieAlbumFragment extends Fragment {

    /* 最小视频时长(单位：ms) */
    private static int MIN_VIDEO_DURATION = 1000;
    /* 最大视频时长(单位：ms) */
    private static int MAX_VIDEO_DURATION = 1000*60*5;
    /** 最大边长限制 **/
    private static final int MAX_SIZE = 4096;
   // private static final int MAX_SIZE = 2000;

    /* 确定按钮 */
    protected TextView mConfirmButton;
    /* 返回按钮 */
    protected TextView mBackButton;
    /* 最大选择数量 */
    protected int mSelectMax = 1;

    private RecyclerView mRecyclerView;

    private MovieAlbumAdapter mVideoAlbumAdapter;

    private int mCurrentPos = -1;

    private LoadVideoTask mLoadVideoTask;

    private boolean isEnable = true;

    public void setIsEnable(boolean isEnable){
        this.isEnable = isEnable;
        mRecyclerView.setEnabled(isEnable);
    }

    private Hashtable<String, List<MovieInfo>> htImg;

    public boolean isEnable(){
        return isEnable;
    }

    public OnMediaInfoIndexListener onMediaInfoIndexListener;

    public interface OnMediaInfoIndexListener {
        void onDataMediaInfoIndex(List<String> listIndex, Hashtable<String, List<MovieInfo>> hashtable);
    }

    private View.OnClickListener mNextStepClickListener = new TuSdkViewHelper.OnSafeClickListener() {
        @Override
        public void onSafeClick(View v) {
            if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo().size() <= 0)
                TuSdk.messageHub().showToast(getActivity(), R.string.lsq_select_video_hint);
            else
                handleIntentAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.movie_album_fragment, container, false);
        baseView.setBackgroundColor(DarkModeUtils.getColor(getActivity(),R.color.lsq_color_white,R.color.color_121921));
        mRecyclerView = (RecyclerView) baseView.findViewById(R.id.lsq_movie_selector_recyclerView);
        TextView tvTimeTip = baseView.findViewById(R.id.time_tip);
        tvTimeTip.setTextColor(DarkModeUtils.getColor(getActivity(),R.color.lsq_color_black,R.color.lsq_color_white));
        tvTimeTip.setBackgroundColor(DarkModeUtils.getColor(getActivity(),R.color.lsq_color_white,R.color.color_121921));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mSelectMax = getActivity().getIntent().getIntExtra("selectMax", 1);

        htImg = new Hashtable<>();
        return baseView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            MovieInfo info = (MovieInfo) data.getSerializableExtra("videoInfo");
            if (info != null && !contains(mVideoAlbumAdapter.getSelectedVideoInfo(), info))
                mVideoAlbumAdapter.updateSelectedVideoPosition(mCurrentPos);
            else if (info == null && mVideoAlbumAdapter.getVideoInfoList().size() > 0 && mCurrentPos != -1)
                // 取消选中
                if (contains(mVideoAlbumAdapter.getSelectedVideoInfo(), mVideoAlbumAdapter.getVideoInfoList().get(mCurrentPos)))
                    mVideoAlbumAdapter.updateSelectedVideoPosition(mCurrentPos);
        }
    }

    private boolean contains(List<MovieInfo> movieInfos, MovieInfo movieInfo) {
        for (MovieInfo info : movieInfos) {
            if (info.getPath().equals(movieInfo.getPath())) {
                return true;
            }
        }
        return false;
    }

    public void setOnMovieInfoIndexListener(OnMediaInfoIndexListener onMediaInfoIndexListener){
        this.onMediaInfoIndexListener = onMediaInfoIndexListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoadVideoTask = new LoadVideoTask();
        mLoadVideoTask.execute();
    }

    /**
     * 将扫描的视频添加到集合中
     */
    public List<MovieInfo> getVideoList() {
        List<MovieInfo> videoInfo = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, "date_added desc");
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            //（暂无此需求）根据时间长短加入显示列表
            //if (duration > 0 && duration < MAX_VIDEO_DURATION) {}
//            if(usePathCheck4K(path)){
//                continue;
//            }
            videoInfo.add(new MovieInfo(path, duration));
            if (duration == 0) {
                TuSDKVideoInfo vInfo = TuSDKMediaUtils.getVideoInfo(path);
                if (vInfo == null) continue;
                if (vInfo.durationTimeUs > 0) {
                    videoInfo.add(new MovieInfo(path, (int) (vInfo.durationTimeUs / 1000)));
                }
            }
        }
        cursor.close();
        return videoInfo;
    }

    public void handleAllIndex(List<MovieInfo> videoList){
        //List<MovieInfo> videoList = getVideoList();
        if(videoList != null && videoList.size() > 0)
        {
            String
                    dirName;
            for(MovieInfo movieInfo : videoList)
            {
                if(movieInfo != null)
                {
                    // 文件夹
                    if(!TextUtils.isEmpty(dirName = dirName(movieInfo.getPath())))
                    {
                        List<MovieInfo> ltMediaMovieInfo = htImg.get(dirName);
                        if(ltMediaMovieInfo == null)
                        {
                            htImg.put(dirName, ltMediaMovieInfo = new ArrayList<>());
                        }
                        ltMediaMovieInfo.add(movieInfo);
                    }
                }
            }
        }
    }

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
     * 检测是否4K视频
     * @param position
     * @return
     */
    private boolean check4K(int position) {
        MovieInfo info = mVideoAlbumAdapter.getVideoInfoList().get(position);
        MediaFormat mediaFormat = TuSDKMediaUtils.getVideoFormat(new TuSDKMediaDataSource(info.getPath()));
        // 当文件异常时直接拦截掉
        if(mediaFormat == null){
            return true;
        }
        TuSdkVideoInfo videoInfo = new TuSdkVideoInfo(mediaFormat);
        if (!TuSDKMediaUtils.isVideoSizeSupported(videoInfo.size,mediaFormat.getString(MediaFormat.KEY_MIME)) || videoInfo.size.maxSide() > MAX_SIZE) {
            TuSdkViewHelper.toast(getActivity(), R.string.lsq_loadvideo_failed);
            return true;
        }
        return false;
    }

    /**
     * 检测是否4K视频
     * @param path
     * @return
     */
    private boolean usePathCheck4K(String path) {

        MediaFormat mediaFormat = TuSDKMediaUtils.getVideoFormat(new TuSDKMediaDataSource(path));
        // 当文件异常时直接拦截掉
        if(mediaFormat == null){
            return true;
        }
        TuSdkVideoInfo videoInfo = new TuSdkVideoInfo(mediaFormat);
        if (!TuSDKMediaUtils.isVideoSizeSupported(videoInfo.size,mediaFormat.getString(MediaFormat.KEY_MIME)) || videoInfo.size.maxSide() > MAX_SIZE) {
            return true;
        }
        return false;
    }

    /**
     *  RecyclerView中item的点击事件，得到点击item的视频信息
     */
    private MovieAlbumAdapter.OnItemClickListener mOnItemClickListener = new MovieAlbumAdapter.OnItemClickListener() {
        @Override
        public void onSelectClick(View view, int position) {
            if (!isEnable) return;
            if (check4K(position)) return;

            mVideoAlbumAdapter.updateSelectedVideoPosition(position);
        }

        @Override
        public void onClick(View view, int position) {
            //if (!isEnable) return; 需求暂时没有CheckBox
            if (check4K(position)) return;
            MovieInfo info = mVideoAlbumAdapter.getVideoInfoList().get(position);
            AVAssetFile assetFile = new AVAssetFile(new File(info.getPath()));
            /// 暂时不支持没有音轨的视频
            if(assetFile.createExtractor().getTrackCount() <= 1){
                ToastUtils.showRedToast(getActivity(),getContext().getString(R.string.lsq_select_include_audio));
                return;
            }

            if (info.getDuration() < MIN_VIDEO_DURATION) {
                ToastUtils.showRedToast(getActivity(),getContext().getString(R.string.lsq_album_select_min_time));
                return;

            }else if(info.getDuration()>MAX_VIDEO_DURATION){
                ToastUtils.showRedToast(getActivity(),getContext().getString(R.string.video_length_cannot_exceed_5_minutes));
                return;
            }
            mCurrentPos = position;
            mVideoAlbumAdapter.updateSelectedVideoPosition(position);
            // todo 点击进入预览页面
//            // 视频路径
//            List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();
//            Intent intent = new Intent(getActivity(), MovieEditorPreviewActivity.class);
//            // 要跳转的视频裁剪类名
//            intent.putExtra("cutClassName", getActivity().getIntent().getStringExtra("cutClassName"));
//            intent.putExtra("selectMax", mSelectMax);
//            intent.putExtra("currentVideoPath", mVideoAlbumAdapter.getVideoInfoList().get(position));
//            intent.putExtra("videoPaths", (Serializable) videoPath);
//            startActivityForResult(intent, 100);
            handleIntentAction();
        }
    };

    /**
     *  处理跳转事件
     */
    public void handleIntentAction() {
        if (mVideoAlbumAdapter == null || mVideoAlbumAdapter.getSelectedVideoInfo().size() <= 0)
            return;

        // 要跳转的视频裁剪类名
        String className = getActivity().getIntent().getStringExtra("cutClassName");
        // 视频路径
        List<MovieInfo> videoPath = mVideoAlbumAdapter.getSelectedVideoInfo();
        long totalTime = 0;
        for (MovieInfo info : videoPath) {
            totalTime += info.getDuration();
        }

        if (totalTime < MIN_VIDEO_DURATION) {
            TuSdk.messageHub().showToast(getActivity(), R.string.lsq_album_select_min_time);
            return;
        }
        if(className.equals(MovieEditorActivity.class.getName())){
            try {
              //  getActivity().finish();
//                Display display = getActivity().getWindowManager().getDefaultDisplay();
//
//               int screenWidth = display.getWidth(); 
//               int screenHeight = display.getHeight();
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath.get(0).getPath());
                int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); //宽
                int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                int videoWidth = 0;
                int videoHeight = 0;
                if("90".equals(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))||"270".equals(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))){
                    videoWidth = height;
                    videoHeight = width;
                }else{
                    videoWidth = width;
                    videoHeight = height;
                }
                Intent intent = new Intent(getActivity(), MovieEditorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("videoPath", videoPath.get(0).getPath());
                intent.putExtra("videoWidth", videoWidth);
                intent.putExtra("videoHeight", videoHeight);
                intent.putExtra("isUpload",true);
                intent.putExtra("router", "MovieAlbumFragment");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            Intent intent = null;
            try {
                intent = new Intent(getActivity(), Class.forName(className));
                intent.putExtra("videoPaths", (Serializable) videoPath);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    public View.OnClickListener getNextStepClickListener() {
        return mNextStepClickListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoadVideoTask.cancel(false);
    }

    /**
     * 相册加载
     */
    class LoadVideoTask extends AsyncTask<Void, Integer, List<MovieInfo>> {

        @Override
        protected List<MovieInfo> doInBackground(Void... voids) {
            return getVideoList();
        }

        @Override
        protected void onPreExecute() {
            TuProgressHub.showToast(getActivity(), "数据加载中...");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfos) {
            TuProgressHub.dismiss();
            handleAllIndex(movieInfos);
            if (movieInfos == null) movieInfos = new ArrayList<>();
            if (mVideoAlbumAdapter == null) {
                mVideoAlbumAdapter = new MovieAlbumAdapter(getActivity(), movieInfos, mSelectMax);
                mRecyclerView.setAdapter(mVideoAlbumAdapter);
                mVideoAlbumAdapter.setOnItemClickListener(mOnItemClickListener);
            }
            if (mVideoAlbumAdapter.getVideoInfoList().size() != movieInfos.size() || !(MD5Util.crypt(mVideoAlbumAdapter.getVideoInfoList().toString()).equals(MD5Util.crypt(movieInfos.toString())))) {
                mVideoAlbumAdapter.setVideoInfoList(movieInfos);
            }
            //返回目录数据
            if(onMediaInfoIndexListener != null){
                onMediaInfoIndexListener.onDataMediaInfoIndex(getAllDir(), htImg);
            }
        }
    }

    public List<MovieInfo> getMediaList(String dirName)
    {
        if(!TextUtils.isEmpty(dirName))
        {
            if(getContext().getResources().getString(R.string.record_local_album_all_index).equals(dirName))
            {
                List<MovieInfo> lt = new ArrayList<>();
                for(Map.Entry<String, List<MovieInfo>> entry : htImg.entrySet())
                {
                    List<MovieInfo> ltTmp = entry.getValue();
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
            List<MovieInfo> movieInfos = getMediaList(mediaTitle);
            if (mVideoAlbumAdapter.getVideoInfoList().size() != movieInfos.size() || !(MD5Util.crypt(mVideoAlbumAdapter.getVideoInfoList().toString()).equals(MD5Util.crypt(movieInfos.toString())))) {
                mVideoAlbumAdapter.setVideoInfoList(movieInfos);
            }
        }
    }
}
