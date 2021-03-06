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
        //??????BottomSheetDialog?????????

        return new BottomSheetDialog(this.getContext());
    }

  

    // ???????????????????????????
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // ??????Event
        EventBus.getDefault().post(new CloseSoundFragmentEvent(mType));
    
    }

    @Override
    public void onStart() {
        super.onStart();
        //??????dialog??????
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        //???windowsd??????????????????????????????????????????????????????
        dialog.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //??????diglog????????????
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            //??????????????????LayoutParams??????
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getPeekHeight();
            //?????????????????????????????????????????????????????????????????????
            bottomSheet.setLayoutParams(layoutParams);

            final BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            //peekHeight????????????????????????
            behavior.setPeekHeight(getPeekHeight());
            // ?????????????????????
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            ImageView mReBack = view.findViewById(R.id.close);
            //????????????
            mReBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //????????????
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            });
        }

    }

    /**
     * ???????????????????????????????????????????????????
     * ??????????????????????????????peekHeight
     *
     * @return height
     */
    protected int getPeekHeight() {
        int peekHeight = getResources().getDisplayMetrics().heightPixels;
        //????????????????????????????????????3/4
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
        //?????????
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