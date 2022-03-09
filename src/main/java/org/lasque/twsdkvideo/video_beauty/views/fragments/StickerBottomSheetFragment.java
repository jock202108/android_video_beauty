package org.lasque.twsdkvideo.video_beauty.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.widget.TuSdkViewPager;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.event.CloseEvent;
import org.lasque.twsdkvideo.video_beauty.event.SkipSearchEvent;
import org.lasque.twsdkvideo.video_beauty.views.TabPagerIndicator;
import org.lasque.twsdkvideo.video_beauty.views.adapters.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 贴纸弹窗
 */
public class StickerBottomSheetFragment extends BottomSheetDialogFragment {

    // 编辑控制器
    private MovieEditorController mEditorController;
    private Context mContext;
    private View view;

    public static StickerBottomSheetFragment getInstance() {
        StickerBottomSheetFragment fragment = new StickerBottomSheetFragment();
//        Bundle args = new Bundle();
        return fragment;
    }



    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mEditorController != null) {
            mEditorController.onBackEvent();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(this.getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取dialog对象
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        //把windowsd的默认背景颜色去掉，不然圆角显示不见
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //把背景改成透明的
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.dimAmount = 0.0f;
            dialog.getWindow().setAttributes(params);
        }
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

        }

    }

    /**
     * 弹窗高度，默认为屏幕高度的四分之三
     * 子类可重写该方法返回peekHeight
     *
     * @return height
     */
    protected int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels-(VideoBeautyPlugin.statusBarHeight+ TuSdkContext.dip2px(22));
        //设置弹窗高度为屏幕高度的3/4
        return peekHeight;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.sticker_dialog_bottomsheet, container, false);
        initViews(view);
        return view;
    }
    

    private void initViews(View view) {
        List<Fragment> fragmentList = new ArrayList<>();
        //造数据
       
        fragmentList.add(StickerListFragment.newInstance("0",null).setEditorController(mEditorController).setmBottomSheetDialogFragment(this));
        fragmentList.add(EmojisListFragment.newInstance().setEditorController(mEditorController).setmBottomSheetDialogFragment(this));
     //    fragmentList.add(SoundFragment.newInstance("1"));
        TabPagerIndicator tablayout = view.findViewById(R.id.tab_layout);
        TuSdkViewPager viewPager = view.findViewById(R.id.view_pager);
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getChildFragmentManager(),fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(1);
        tablayout.setViewPager(viewPager,0);
        tablayout.setTabItems(Arrays.asList(getString(R.string.tab_stickers),getString(R.string.tab_emojis)));
    }

    public StickerBottomSheetFragment setmEditorController(MovieEditorController mEditorController) {
        this.mEditorController = mEditorController;
        return this;
    }
}
