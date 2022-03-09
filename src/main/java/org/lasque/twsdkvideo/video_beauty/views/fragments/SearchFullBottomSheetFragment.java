package org.lasque.twsdkvideo.video_beauty.views.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.SearchBean;
import org.lasque.twsdkvideo.video_beauty.event.CloseEvent;
import org.lasque.twsdkvideo.video_beauty.event.DimEvent;
import org.lasque.twsdkvideo.video_beauty.event.MusicListEvent;
import org.lasque.twsdkvideo.video_beauty.utils.KeyBoardUtils;
import org.lasque.twsdkvideo.video_beauty.utils.SpUtils;
import org.lasque.twsdkvideo.video_beauty.views.adapters.OnItemClickListener;
import org.lasque.twsdkvideo.video_beauty.views.adapters.SearchAdapter;
import org.lasque.twsdkvideo.video_beauty.views.adapters.SoundListAdapter;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.MyRecyclerView;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.YjEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: 搜索页面
 * @Date: 2019/11/14
 * @Describe:
 */
public class SearchFullBottomSheetFragment extends BottomSheetDialogFragment {

    private Context mContext;
    private View view;
    MyRecyclerView recyclerView;
    SearchAdapter adapter;
    ArrayList<SearchBean> mDatas = new ArrayList<>();
    int page =1;

    public static SearchFullBottomSheetFragment getInstance() {
        return new SearchFullBottomSheetFragment();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
        view = inflater.inflate(R.layout.search_bottomsheet, container, false);
        getSpData(false);
        initViews(view);
        return view;
    }

    private void getSpData(boolean isRefresh) {
        mDatas.clear();
        String searchData = new SpUtils(mContext).getString(SpUtils.searchKey);
        String[] data = searchData.split(",");
        for (int i = 0; i < data.length; i++) {
            if (!data[i].isEmpty()) {
                mDatas.add(new SearchBean(data[i], true));
            }

        }
        if(isRefresh){
            adapter.notifyDataSetChanged();
        }

    }

    // 移除历史记录
    private void removeSpStr(int position) {
        String content =  mDatas.get(position).getContent();
        mDatas.remove(position);
        adapter.notifyDataSetChanged();


        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mDatas.size(); i++) {
            sb.append(mDatas.get(i).getContent()).append(",");
        }
        String str = sb.toString();
        new SpUtils(getContext()).putString(SpUtils.searchKey, str);

    }

    // 添加历史记录到sp
    private void  addSpStr(String str){
        String searchData = new SpUtils(mContext).getString(SpUtils.searchKey);
        String[] data = searchData.split(",");
        ArrayList<String> dataList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (!data[i].isEmpty()) {
                dataList.add(data[i]);
            }

        }
        dataList.add(0,str+",");

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dataList.size(); i++) {
            sb.append(dataList.get(i)).append(",");
        }
        String sbStr = sb.toString();
        new SpUtils(getContext()).putString(SpUtils.searchKey, sbStr);

    }




    private void initViews(View view) {
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        YjEditText editText = view.findViewById(R.id.mEditText);
        ImageView closeImage = view.findViewById(R.id.close);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {


                               InputMethodManager inputManager =
                                       (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);

                           }

                       },
                1000);
        recyclerView = view.findViewById(R.id.mRv);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CloseEvent(true));
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              String  str =  s.toString().trim();
                if(str.isEmpty()){
                    getSpData(true);
                }else {
                    getDimData(str);
                    sendBroadcast(true,str);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        new SearchResultFullBottomSheetFragment(v.getText().toString().trim()).show(getChildFragmentManager(), "result");
                        addSpStr(v.getText().toString());
                        break;
                }
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        adapter = new SearchAdapter(getContext());
        adapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                new SearchResultFullBottomSheetFragment(mDatas.get(position).getContent()).show(getChildFragmentManager(), "result");
               if(!mDatas.get(position).isHistory()){
                   addSpStr(mDatas.get(position).getContent());
               }
            }

            @Override
            public void onSelectedSound(int position) {
                removeSpStr(position);

            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setDate(mDatas);

    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeSearch(CloseEvent closeEvent) {
        dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dimData(DimEvent dimEvent) {
        /// TODO 模糊音逻辑

    }

    void getDimData(String searchStr){
        mDatas.clear();
        SearchBean bean =    new SearchBean(searchStr,false);
        mDatas.add(bean);
        adapter.notifyDataSetChanged();
    }

    private void sendBroadcast(boolean isRefresh,String searchStr) {
        if (isRefresh) page = 1;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.to.flutter");
        intent.putExtra("type", 4);
        intent.putExtra("keyword", searchStr);
        intent.putExtra("pageNum", page);
        Objects.requireNonNull(getContext()).sendBroadcast(intent);
    }

}