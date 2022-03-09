package org.lasque.twsdkvideo.video_beauty.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.tusdk.core.view.widget.TuSdkViewPager;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.event.CloseEvent;
import org.lasque.twsdkvideo.video_beauty.event.CloseSoundFragmentEvent;
import org.lasque.twsdkvideo.video_beauty.event.SkipSearchEvent;
import org.lasque.twsdkvideo.video_beauty.views.TabPagerIndicator;
import org.lasque.twsdkvideo.video_beauty.views.adapters.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: david.lvfujiang
 * @Date: 2019/11/14
 * @Describe:
 */
public class BaseFullBottomSheetFragment extends BottomSheetDialogFragment {

    private Context mContext;
    private View view;
    private int mType;
    private  String mMusicName;
    private static final String ARG_PARAM1 = "TYPE";
    private static final String ARG_PARAM2 = "music_name";
    public static BaseFullBottomSheetFragment getInstance(int param1,String param2) {
        BaseFullBottomSheetFragment fragment = new BaseFullBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_PARAM1);
            mMusicName = getArguments().getString(ARG_PARAM2);
            AppConstants.ENTER_STATE = mType;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //返回BottomSheetDialog的实例

        return new BottomSheetDialog(this.getContext());
    }

  

    // 弹窗关闭的时候回调
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // 发送Event
        EventBus.getDefault().post(new CloseSoundFragmentEvent(mType));
    
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

            ImageView mReBack = view.findViewById(R.id.close);
            //设置监听
            mReBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //关闭弹窗
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
        EventBus.getDefault().register(this);
        mContext = getContext();
        view = inflater.inflate(R.layout.dialog_bottomsheet, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
    
    
    private void initViews(View view) {
         List<Fragment> fragmentList = new ArrayList<>();
        //造数据
        fragmentList.add(SoundFragment.newInstance("0",mMusicName));
       // fragmentList.add(SoundFragment.newInstance("1"));
        TabPagerIndicator tablayout = view.findViewById(R.id.tab_layout);
        TuSdkViewPager viewPager = view.findViewById(R.id.view_pager);
        LinearLayout searchLl = view.findViewById(R.id.search_ll);
        searchLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SkipSearchEvent());
                new SearchFullBottomSheetFragment().show(getChildFragmentManager(), "search");
              //  useMusic();
            }
        });


        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getChildFragmentManager(),fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(1);
        tablayout.setViewPager(viewPager,0);
        tablayout.setTabItems(Arrays.asList(getString(R.string.tv_discover),getString(R.string.tv_favorites)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeList(CloseEvent closeEvent) {
       if(closeEvent.isCloseList){
           dismiss();
       }
    }



}