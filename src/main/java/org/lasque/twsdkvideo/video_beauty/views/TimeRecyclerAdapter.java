package org.lasque.twsdkvideo.video_beauty.views;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.twsdkvideo.video_beauty.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 滤镜适配器
 *
 * @author xujie
 * @Date 2018/9/18
 */

public class TimeRecyclerAdapter extends RecyclerView.Adapter<TimeRecyclerAdapter.TimeViewHolder> {

    private List<String> mTimeString;
    private int mCurrentPosition = -1;
    private Context mContext;


    public static int[] TIME_EFFECT_CODES = {R.string.lsq_filter_None,R.string.lsq_filter_repeated,R.string.lsq_filter_slowmotion,R.string.lsq_filter_reverse};


    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemTouchListener {
        void onItemTouch(MotionEvent event, int position, TimeViewHolder TimeViewHolder);
    }

    public ItemClickListener listener;
    public OnItemTouchListener onItemTouchListener;

    public void setItemCilckListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public TimeRecyclerAdapter(Context context) {
        super();
        this.mContext = context;
        mTimeString = new ArrayList<>();
    }

    public void setTimeList(List<String> timeList) {
        this.mTimeString = timeList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }

    public List<String> getTimeList() {
        return this.mTimeString;
    }

    @Override
    public int getItemCount() {
        return mTimeString.size();
    }

    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_recycler_item_view, null);
        TimeViewHolder viewHolder = new TimeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TimeViewHolder timeViewHolder, @SuppressLint("RecyclerView") final int position) {

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) timeViewHolder.item_view.getLayoutParams();

        if (position == 0) {
            layoutParams.leftMargin = TuSdkContext.dip2px(18);
        } else {
            layoutParams.leftMargin = TuSdkContext.dip2px(3);
        }
        String timeCode = mTimeString.get(position);
        timeCode = timeCode.toLowerCase();
        String timeImageName = getThumbPrefix() + timeCode;
        timeViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        if (position == 0) {
            timeViewHolder.mItemImage.setImageDrawable(TuSdkContext.getDrawable(R.drawable.none_btn_icon));
            timeViewHolder.mRlSelect.setSelected(false);
            timeViewHolder.mTitleView.setText(TuSdkContext.getString(getTextPrefix() + timeCode));
            timeViewHolder.mTitleView.setText(mContext.getResources().getString(R.string.none_effect));
        } else {
            if (position == mCurrentPosition) {
                //  timeViewHolder.mNoneLayout.setVisibility(View.GONE);
//            timeViewHolder.mTitleView.setVisibility(View.GONE);
                //  timeViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
                timeViewHolder.mRlSelect.setSelected(true);
            } else {
                //  timeViewHolder.mNoneLayout.setVisibility(View.GONE);
//            timeViewHolder.mTitleView.setVisibility(View.VISIBLE);
                // timeViewHolder.mSelectLayout.setVisibility(View.GONE);
                timeViewHolder.mRlSelect.setSelected(false);
            }
            timeViewHolder.mTitleView.setText(mContext.getResources().getString(TIME_EFFECT_CODES[position]));
            int timeId = TuSdkContext.getDrawableResId(timeImageName);
            RoundedCorners roundedCorners = new RoundedCorners(TuSdkContext.dip2px(5));
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(timeViewHolder.mItemImage.getWidth(), timeViewHolder.mItemImage.getHeight());
            Glide.with(timeViewHolder.mItemImage.getContext()).asGif().load(timeId).apply(options).into(timeViewHolder.mItemImage);
        }
        // 反馈点击
        timeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(position);
            }
        });
        timeViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onItemTouchListener == null) return false;
                onItemTouchListener.onItemTouch(event, position, timeViewHolder);
                return false;
            }
        });
        timeViewHolder.itemView.setTag(position);
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

    public String getTimeCode(int position) {
        if (mTimeString == null && mTimeString.size() < position) return "None";
        return mTimeString.get(position);
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public ImageView mSelectLayout;
        public RelativeLayout mRlSelect;
        public RelativeLayout mImageLayout;
        public View item_view;

        public TimeViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.lsq_item_title);
            mItemImage = itemView.findViewById(R.id.lsq_item_image);
            mSelectLayout = itemView.findViewById(R.id.lsq_select_layout);
            mImageLayout = itemView.findViewById(R.id.lsq_image_layout);
            item_view =   itemView.findViewById(R.id.item_view);
            mRlSelect = itemView.findViewById(R.id.effect_stroke);
        }
    }
}
