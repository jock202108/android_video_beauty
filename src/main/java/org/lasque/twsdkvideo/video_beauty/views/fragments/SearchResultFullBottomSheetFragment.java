package org.lasque.twsdkvideo.video_beauty.views.fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import org.lasque.twsdkvideo.video_beauty.event.SearchResultListEvent;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.utils.DownloadUtil;
import org.lasque.twsdkvideo.video_beauty.utils.FileUtils;
import org.lasque.twsdkvideo.video_beauty.utils.KeyBoardUtils;
import org.lasque.twsdkvideo.video_beauty.utils.TimeUtils;
import org.lasque.twsdkvideo.video_beauty.views.adapters.OnItemClickListener;
import org.lasque.twsdkvideo.video_beauty.views.adapters.SoundListAdapter;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.MyRecyclerView;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.YjEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import at.grabner.circleprogress.CircleProgressView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.DownloadStatus;
import zlc.season.rxdownload.RxDownload;

/**
 * @Author: 搜索结果页面
 * @Date: 2019/11/14
 * @Describe:
 */
public class SearchResultFullBottomSheetFragment extends BottomSheetDialogFragment {

    private Context mContext;
    private View view;
    MyRecyclerView mSoundRv;
    SoundListAdapter adapter;
    ArrayList<SoundBean> mDatas = new ArrayList<>();
    MediaPlayer mediaPlayer = new MediaPlayer();
    String searchStr;
    SmartRefreshLayout refreshLayout;
    int page = 1;
    //转码进度视图
    private FrameLayout mLoadContent;
    private CircleProgressView mLoadProgress;
    LinearLayout currentMusicLl;
    TextView tvCurrentMusic;
    RelativeLayout reMusicCancel;

    public SearchResultFullBottomSheetFragment(String searchStr) {
        this.searchStr = searchStr;
    }

    public SearchResultFullBottomSheetFragment() {
    }


    public static SearchResultFullBottomSheetFragment getInstance() {
        return new SearchResultFullBottomSheetFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //返回BottomSheetDialog的实例

        return new BottomSheetDialog(this.getContext());
    }


    @Override
    public void onStart() {
        super.onStart();
        //获取dialog对象
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        //把windowsd的默认背景颜色去掉，不然圆角显示不见
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //获取diglog的根部局
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            //获取根部局的LayoutParams对象
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getPeekHeight();
            //修改弹窗的最大高度，不允许上滑（默认可以上滑）
            bottomSheet.setLayoutParams(layoutParams);

            final BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            //peekHeight即弹窗的最大高度
            behavior.setPeekHeight(getPeekHeight());
            // 初始为展开状态
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            ImageView mClose = view.findViewById(R.id.close);
            //设置监听
            mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyBoardUtils.hideShowKeyboard(Objects.requireNonNull(getActivity()));
                    //关闭弹窗
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    EventBus.getDefault().post(new CloseEvent(true));
                }
            });
        }

    }

    /**
     * 弹窗高度，默认为屏幕高度的四分之三
     * 子类可重写该方法返回peekHeight
     *
     * @return height
     */
    protected int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.search_result_bottomsheet, container, false);
        initViews(view);
        sendBroadcast(true);
        return view;
    }

    private void initViews(View view) {
        EventBus.getDefault().register(this);
        ImageView backImage = view.findViewById(R.id.back);
        ImageView closeImage = view.findViewById(R.id.close);
        YjEditText mEditText = view.findViewById(R.id.mEditText);
        mEditText.setText(searchStr);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mLoadContent = view.findViewById(R.id.lsq_editor_cut_load);
        mLoadProgress = view.findViewById(R.id.lsq_editor_cut_load_parogress);
        mSoundRv = view.findViewById(R.id.mRv);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        currentMusicLl = view.findViewById(R.id.ll_current_music);
        tvCurrentMusic = view.findViewById(R.id.tv_current_music);
        reMusicCancel = view.findViewById(R.id.music_cancel);
        reMusicCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMusic();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                sendBroadcast(false);

            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                EventBus.getDefault().post(new CloseEvent(false));
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String str = v.getText().toString().trim();
                        searchStr = str;
                        if(!searchStr.isEmpty()){
                            sendBroadcast(true);
                        }
                        KeyBoardUtils.hideShowKeyboard(getContext());
                        break;
                }
                return false;
            }
        });




        mSoundRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new SoundListAdapter(getContext());
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SoundBean  bean =  mDatas.get(position);
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

            }

            @Override
            public void onSelectedSound(int position) {
                downLoad(mDatas.get(position));

            }
        });
        mSoundRv.setAdapter(adapter);
        adapter.setDate(mDatas);
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
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void playOnlineSound(SoundBean bean) {
        try {
            if(mediaPlayer != null){
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
            }else {
                mediaPlayer =  new MediaPlayer();
            }

            mediaPlayer.setDataSource(bean.getSoundUrl());
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

    private void sendBroadcast(boolean isRefresh) {
        if (isRefresh) page = 1;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.to.flutter");
        intent.putExtra("type", 1);
        intent.putExtra("keyword", searchStr);
        intent.putExtra("pageNum", page);
        Objects.requireNonNull(getContext()).sendBroadcast(intent);
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
            if(downloadFile.exists() && downloadFile.isFile() && FileUtils.fileIsUsable(mContext,downloadFile)){
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
                                if(FileUtils.fileIsUsable(mContext,downloadFile)){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSearchMusicList(SearchResultListEvent message) {
        setData(message.content);
    }

    void setData(String data) {
        if (data.contains("error")) {
            return;
        }
        if(page == 1){
            mDatas.clear();
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

}