package org.lasque.twsdkvideo.video_beauty.views.newFilterUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.tusdk.FilterOption;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * filter滤镜的adapter
 * TuSDK
 * $
 *
 * @author H.ys
 * @Date $ $
 * @Copyright (c) 2019 tw. All rights reserved.
 */
public class FilterOptionRecyclerAdapter extends RecyclerView.Adapter<FilterOptionRecyclerAdapter.FilterViewHolder> {

    private List<FilterOption> mOptions;
    private ArrayList<Integer> filterTypeItemNameList;
    private Context mContext;

    // 当前选中
    private int mCurrentPosition = -1;

    // 是否显示调节图
    private boolean isShowParameter = false;

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public ItemClickListener listener;

    public void setItemCilckListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public FilterOptionRecyclerAdapter(List<FilterOption> options) {
        this.mOptions = options;
    }

    public FilterOptionRecyclerAdapter(List<FilterOption> options, ArrayList filterTypeItemNameList) {
        this.mOptions = options;
        this.filterTypeItemNameList = filterTypeItemNameList;
    }

    public void setFilterList(List<FilterOption> options) {
        this.mOptions = options;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        if (position == mCurrentPosition) return;
        int lastPosition = mCurrentPosition;
        this.mCurrentPosition = position;
        if (lastPosition != -1)
            notifyItemChanged(lastPosition);
        if (mCurrentPosition != -1)
            notifyItemChanged(mCurrentPosition);
        if (position == -1) {
            isShowParameter = false;
        }
    }

    public void changeShowParameterState() {
        isShowParameter = !isShowParameter;
        notifyItemChanged(mCurrentPosition);
    }

    public String getFilterCode(int position) {
        return mOptions.get(position).code;
    }


    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(TuSdkContext.getLayoutResId("lsq_filter_recycler_item_view"), null);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder filterViewHolder, @SuppressLint("RecyclerView") final int position) {
        String filterCode = mOptions.get(position).code;
        String imageCode = filterCode.toLowerCase().replaceAll("_", "");
        String filterImageName = getThumbPrefix() + imageCode;
        filterViewHolder.mImageLayout.setVisibility(View.VISIBLE);
//        if(position == 0){
//            filterViewHolder.mNoneLayout.setVisibility(View.VISIBLE);
//            filterViewHolder.mTitleView.setVisibility(View.GONE);
//            filterViewHolder.mSelectLayout.setVisibility(View.GONE);
//            filterViewHolder.mImageLayout.setVisibility(View.GONE);
//        }else
        if (position == mCurrentPosition) {
            filterViewHolder.mNoneLayout.setVisibility(View.GONE);
          //  filterViewHolder.mTitleView.setVisibility(View.GONE);
            /** 点击选中处理 */
            filterViewHolder.mFitterLl.setBackground( TuSdkContext.getDrawable(R.drawable.sticker_cell_background));
         //   filterViewHolder.mSelectLayout.setVisibility(View.VISIBLE);

            if (isShowParameter && mOptions.get(position).groupId != 252) {
                filterViewHolder.mImageParameter.setVisibility(View.VISIBLE);
            } else {
                filterViewHolder.mImageParameter.setVisibility(View.GONE);
            }
        } else {
            filterViewHolder.mNoneLayout.setVisibility(View.GONE);
            filterViewHolder.mFitterLl.setBackground( null);
            filterViewHolder.mTitleView.setVisibility(View.VISIBLE);
            filterViewHolder.mSelectLayout.setVisibility(View.GONE);
        }
        //TDOO filter具体名称
        //filterViewHolder.mTitleView.setText(TuSdkContext.getString(getTextPrefix() + filterCode));
        if(filterTypeItemNameList != null && filterTypeItemNameList.size() > 0) {
            filterViewHolder.mTitleView.setText(mContext.getResources().getString(filterTypeItemNameList.get(position)));
        }

        Bitmap filterImage = TuSdkContext.getRawBitmap(filterImageName);
        if (filterImage != null) {
            filterViewHolder.mItemImage.setImageBitmap(filterImage);
        }
        // 反馈点击
        filterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(position);
                notifyItemChanged(mCurrentPosition);
                notifyItemChanged(position);
                mCurrentPosition = position;
            }
        });
        filterViewHolder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }


    /**
     * 缩略图前缀
     *
     * @return
     */
    protected String getThumbPrefix() {
        return "lsq_filter_thumb_";
    }

    /**
     * Item名称前缀
     *
     * @return
     */
    protected String getTextPrefix() {
        return "lsq_filter_";
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public FrameLayout mSelectLayout;
        public FrameLayout mNoneLayout;
        public RelativeLayout mImageLayout;
        public ImageView mImageParameter;
        public LinearLayout mFitterLl;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(TuSdkContext.getIDResId("lsq_item_title"));
            mItemImage = (TuSdkImageView) itemView.findViewById(TuSdkContext.getIDResId("lsq_item_image"));
            mSelectLayout = (FrameLayout) itemView.findViewById(TuSdkContext.getIDResId("lsq_select_layout"));
            mNoneLayout = (FrameLayout) itemView.findViewById(TuSdkContext.getIDResId("lsq_none_layout"));
            mImageLayout = (RelativeLayout) itemView.findViewById(TuSdkContext.getIDResId("lsq_image_layout"));
            mImageParameter = (ImageView) itemView.findViewById(TuSdkContext.getIDResId("lsq_filter_parameter"));
            mFitterLl = (LinearLayout) itemView.findViewById(TuSdkContext.getIDResId("fitter_ll"));
            mItemImage.setCornerRadiusDP(7);
        }
    }
}
