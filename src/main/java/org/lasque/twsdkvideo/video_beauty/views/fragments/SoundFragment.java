package org.lasque.twsdkvideo.video_beauty.views.fragments;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.GVisionSoundBean;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;
import org.lasque.twsdkvideo.video_beauty.event.CloseEvent;
import org.lasque.twsdkvideo.video_beauty.event.CollectEvent;
import org.lasque.twsdkvideo.video_beauty.event.MusicListEvent;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.event.SkipSearchEvent;
import org.lasque.twsdkvideo.video_beauty.utils.DownloadUtil;
import org.lasque.twsdkvideo.video_beauty.utils.FileUtils;
import org.lasque.twsdkvideo.video_beauty.utils.TimeUtils;
import org.lasque.twsdkvideo.video_beauty.views.adapters.OnItemClickListener;
import org.lasque.twsdkvideo.video_beauty.views.adapters.SoundListAdapter;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.MyRecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import at.grabner.circleprogress.CircleProgressView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.DownloadStatus;
import zlc.season.rxdownload.RxDownload;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundFragment extends Fragment {

    private static final String ARG_PARAM1 = "TYPE";
    private static final String ARG_PARAM2 = "music_name";

    private int mParam1;
    private String mMusicName;
    MyRecyclerView mSoundRv;
    SmartRefreshLayout refreshLayout;
    SoundListAdapter adapter;
    ImageView backImage;
    LinearLayout currentMusicLl;
    TextView tvCurrentMusic;
    RelativeLayout reMusicCancel;
    ArrayList<SoundBean> mDatas = new ArrayList<>();
    MediaPlayer mediaPlayer = new MediaPlayer();
    //Loading视图
    private FrameLayout mLoadContent;
    private CircleProgressView mLoadProgress;
    int page = 1;

    public SoundFragment() {
        // Required empty public constructor
    }

    public static SoundFragment newInstance(String param1,String musicName) {
        SoundFragment fragment = new SoundFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, Integer.parseInt(param1));
        args.putString(ARG_PARAM2, musicName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mMusicName = getArguments().getString(ARG_PARAM2);
        }
    }


    private void sendBroadcast(boolean isRefresh) {
        if (isRefresh) page = 1;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.to.flutter");
        intent.putExtra("type", mParam1);
        intent.putExtra("pageNum", page);
        intent.putExtra("state", 0);
        Objects.requireNonNull(getContext()).sendBroadcast(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound, container, false);
        initView(view);
        sendBroadcast(true);
        return view;
    }


    void initView(View view) {
        EventBus.getDefault().register(this);
        currentMusicLl = view.findViewById(R.id.ll_current_music);
        tvCurrentMusic = view.findViewById(R.id.tv_current_music);
        if(mMusicName != null && !mMusicName.equals("")){
            setCurrentMusicBar(mMusicName,true);
        }
        reMusicCancel = view.findViewById(R.id.music_cancel);
        mSoundRv = view.findViewById(R.id.mRv);
        mSoundRv.setNestedScrollingEnabled(false);
        backImage = view.findViewById(R.id.back);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        mLoadContent = view.findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = view.findViewById(R.id.lsq_editor_cut_load_parogress);
        reMusicCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SelectSoundEvent(null, AppConstants.ENTER_STATE));
                setCurrentMusicBar("",false);
                EventBus.getDefault().post(new CloseEvent(true));
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                sendBroadcast(false);

            }
        });
        mSoundRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new SoundListAdapter(getContext());
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
               SoundBean  bean =  mDatas.get(position);
                try {
                    /// 如果正在播放
                    if(bean.isPlaying()){
                        bean.setPlaying(false);
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.reset();
                        }
                    }else {
                        for (int i = 0; i < mDatas.size(); i++) {
                            mDatas.get(i).setPlaying(false);
                            mDatas.get(i).setLoading(false);
                        }
                        mDatas.get(position).setPlaying(true);
                        mDatas.get(position).setLoading(true);
                        playOnlineSound(mDatas.get(position));
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onSelectedSound(int position) {
                downLoad( mDatas.get(position));
            }
        });
        mSoundRv.setAdapter(adapter);
        adapter.setDate(mDatas);

    }

    private void downLoad( SoundBean bean) {
        if(!AppConstants.callList.isEmpty()){
            for (int i = 0; i < AppConstants.callList.size(); i++) {
                AppConstants.callList.get(i).cancel();
            }
            AppConstants.callList.clear();
        }
       String[]  str = bean.getSoundUrl().split("/");
       StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; i++) {
            if(i == str.length -1){
                sb.append(str[i]);
            }
        }
        String musicName = sb.toString();
        try {
            String dir =  DownloadUtil.isExistDir(getContext(),"download");
            File downloadFile = new File(dir, musicName);
        if(downloadFile.exists() && downloadFile.isFile() && FileUtils.fileIsUsable(getActivity(),downloadFile)){
            useMusic(downloadFile.getAbsolutePath(), bean);
        }else {
            mLoadContent.setVisibility(View.VISIBLE);
            DownloadUtil.get(getContext()).download(bean.getSoundUrl(), "download", musicName,new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(String localPath) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadContent.setVisibility(View.GONE);
                            if(FileUtils.fileIsUsable(getActivity(),downloadFile)){
                                useMusic(localPath, bean);
                            }else {
                                EventBus.getDefault().post(new CloseEvent(true));
                                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string. download_fail),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                @Override
                public void onDownloading(int progress) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadProgress.setValue(progress);
                        }
                    });
                }
                @Override
                public void onDownloadFailed(Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadContent.setVisibility(View.GONE);
                            // useMusic(localPath, bean);
                        }
                    });
                }
            });
        } } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void useMusic(String localPath, SoundBean bean) {
        bean.setLocalPath(localPath);
        EventBus.getDefault().post(new SelectSoundEvent(bean, AppConstants.ENTER_STATE));
        EventBus.getDefault().post(new CloseEvent(true));
    }

    void  finishMusic(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            for (int i = 0; i < mDatas.size(); i++) {
                mDatas.get(i).setPlaying(false);
            }
            adapter.notifyDataSetChanged();
        }
    }


    void  setCurrentMusicBar(String musicName,boolean visibility){
        int stata = visibility?View.VISIBLE:View.GONE;
        currentMusicLl.setVisibility(stata);
        tvCurrentMusic.setText(getString(R.string.current_music)+musicName);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void playOnlineSound(SoundBean bean) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
            } else {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.setDataSource(bean.getSoundUrl());
         //   AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.lsq_audio_lively);
//            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
//                    file.getLength());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    bean.setLoading(false);
                    adapter.notifyDataSetChanged();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    finishMusic();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMusicList(MusicListEvent message) {
        // Log.e("hh", message.content);
        setData(message.content);
    }


    void setData(String data) {
        if (data.contains("error")) {
            return;
        }
        Gson gson = new Gson();
        List<GVisionSoundBean> dataList = gson.fromJson(data, new TypeToken<List<GVisionSoundBean>>() {
        }.getType());

        if(dataList.size() == 0){
            refreshLayout.finishLoadMoreWithNoMoreData();
        }else {
            refreshLayout.finishLoadMore();
            page++;
        }
        for (int i = 0; i < dataList.size(); i++) {
            GVisionSoundBean gVisionSoundBean = dataList.get(i);
            SoundBean bean = new SoundBean(
                    gVisionSoundBean.getMusicId(),
                    gVisionSoundBean.getMusicUrl(),
                    gVisionSoundBean.getMusicCoverUrl(),
                    gVisionSoundBean.getMusicTitle(),
                    gVisionSoundBean.getMusicAuthor(),
                    gVisionSoundBean.getDuration(),
                    false,
                    false,
                    false);
            mDatas.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectMusic(CollectEvent message) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void skipSearch(SkipSearchEvent message) {
        finishMusic();
    }
}