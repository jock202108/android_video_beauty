package org.lasque.twsdkvideo.video_beauty.views.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import org.greenrobot.eventbus.EventBus;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.utils.BitmapUtils;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;
import org.lasque.twsdkvideo.video_beauty.views.adapters.StickerListAdapter;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.MyRecyclerView;


public class StickerListFragment extends Fragment {

    MyRecyclerView mStickerRv;
    SmartRefreshLayout refreshLayout;
    StickerListAdapter adapter;
    /**
     * 默认持续时间
     **/
    private long defaultDurationUs = 1 * 1000000;
    int page = 1;
    private  MovieEditorController mEditorController;
    private BottomSheetDialogFragment mBottomSheetDialogFragment;

    public StickerListFragment() {
    }

    public StickerListFragment setEditorController(MovieEditorController mEditorController) {
        this.mEditorController = mEditorController;
        return this;
    }



    public static StickerListFragment newInstance(String param1, String musicName) {
        StickerListFragment fragment = new StickerListFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sticker_list, container, false);
        initView(view);
        return view;
    }


    void initView(View view) {
        mStickerRv = view.findViewById(R.id.mRv);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
        mStickerRv.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mStickerRv.setNestedScrollingEnabled(false);
        adapter = new StickerListAdapter(getContext());
        adapter.setOnItemClickListener(new StickerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,int resId) {
                if(position==0){
                    // todo 进入相册选择
                    AlbumUtils.openPictureAlbum(MovieEditorActivity.class.getName(), Constants.MAX_EDITOR_SELECT_MUN, AppConstants.STICKER_ENTER);
                }else{
                    mEditorController.getMovieEditor().getEditorPlayer().pausePreview();
                    Utils.addImageSticker(BitmapFactory.decodeResource(mEditorController.getActivity().getResources(), resId),mEditorController.getMovieEditor().getEditorPlayer(),mEditorController.getActivity().getImageStickerView(),mEditorController.getActivity());
                }
                if(mBottomSheetDialogFragment!=null){
                    mBottomSheetDialogFragment.dismiss();
                }
            }
        });
        mStickerRv.setAdapter(adapter);
    }

    public StickerListFragment setmBottomSheetDialogFragment(BottomSheetDialogFragment mBottomSheetDialogFragment) {
        this.mBottomSheetDialogFragment = mBottomSheetDialogFragment;

        return this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBottomSheetDialogFragment= null;
        mEditorController= null;
    }
}
