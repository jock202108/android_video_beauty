package org.lasque.twsdkvideo.video_beauty.views.fragments;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorStickerImageBackups;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;
import org.lasque.twsdkvideo.video_beauty.views.adapters.EmojisListAdapter;
import org.lasque.twsdkvideo.video_beauty.views.adapters.StickerListAdapter;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.MyRecyclerView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

/**
 * 表情符号
 */
public class EmojisListFragment extends Fragment {

    MyRecyclerView mStickerRv;
    SmartRefreshLayout refreshLayout;
    EmojisListAdapter adapter;
    /**
     * 默认持续时间
     **/
    private long defaultDurationUs = 1 * 1000000;
    int page = 1;
    private MovieEditorController mEditorController;
    private BottomSheetDialogFragment mBottomSheetDialogFragment;

    public EmojisListFragment() {
    }

    public EmojisListFragment setEditorController(MovieEditorController mEditorController) {
        this.mEditorController = mEditorController;
        return this;
    }



    public static EmojisListFragment newInstance() {
        EmojisListFragment fragment = new EmojisListFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sticker_list, container, false);
        initView(view);
        return view;
    }

    /**
     * 贴纸控件代理
     **/
    private StickerView.StickerViewDelegate mStickerDelegate = new StickerView.StickerViewDelegate() {
        @Override
        public boolean canAppendSticker(StickerView view, StickerData sticker) {
            return true;
        }

        @Override
        public boolean canAppendSticker(StickerView view, StickerDynamicData sticker) {

            return true;
        }

        @Override
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerData stickerData, String s, boolean b)  {

        }

        @Override
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerDynamicData stickerDynamicData, String s, boolean b) {

        }

        @Override
        public void onStickerItemViewReleased(StickerItemViewInterface stickerItemViewInterface, PointF pointF) {

        }

        @Override
        public void onCancelAllStickerSelected() {

        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
        }

        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
        }

        @Override
        public void onStickerItemViewMove(StickerItemViewInterface stickerItemViewInterface, Rect rect, PointF pointF) {

        }
    };


    void initView(View view) {
        mStickerRv = view.findViewById(R.id.mRv);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
        mStickerRv.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mStickerRv.setNestedScrollingEnabled(false);
        adapter = new EmojisListAdapter(getContext());
        adapter.setOnItemClickListener(new EmojisListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,int resId) {
                mEditorController.getMovieEditor().getEditorPlayer().pausePreview();

                mEditorController.getActivity().getImageStickerView().setDelegate(mStickerDelegate);

                Utils.addImageSticker(BitmapFactory.decodeResource(mEditorController.getActivity().getResources(), resId),mEditorController.getMovieEditor().getEditorPlayer(),mEditorController.getActivity().getImageStickerView(),mEditorController.getActivity());
                if(mBottomSheetDialogFragment!=null){
                    mBottomSheetDialogFragment.dismiss();
                }
            }
        });
        mStickerRv.setAdapter(adapter);
    }

    public EmojisListFragment setmBottomSheetDialogFragment(BottomSheetDialogFragment mBottomSheetDialogFragment) {
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
