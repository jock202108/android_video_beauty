package org.lasque.twsdkvideo.video_beauty.views;

import static org.lasque.twsdkvideo.video_beauty.utils.Constants.PARTICLE_CODES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class MagicRecyclerAdapter extends RecyclerView.Adapter<MagicRecyclerAdapter.MagicViewHolder> {

    private List<String> mMagicString;
    private int mCurrentPosition = -1;
    private boolean isCanDeleted = false;
    private long currentTimeMillis = 0;
    private  Context mContext;


    public static int[] PARTICLE_CODES = {  R.string.lsq_filter_snow01,R.string.lsq_filter_Music,R.string.lsq_filter_Bubbles,R.string.lsq_filter_Surprise,R.string.lsq_filter_Flower,
            R.string.lsq_filter_Money,R.string.lsq_filter_Burning};

    public int getmCurrentPosition() {
        return mCurrentPosition;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public interface ItemClickListener {
        void onItemClick(int position, MagicViewHolder MagicViewHolder);
    }

    public interface OnItemTouchListener {
        void onItemTouch(MotionEvent event, int position, MagicViewHolder MagicViewHolder);
    }

    public ItemClickListener listener;
    public OnItemTouchListener onItemTouchListener;

    public void setItemCilckListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemTouchListener(OnItemTouchListener onItemTouchListener) {
        this.onItemTouchListener = onItemTouchListener;
    }

    public MagicRecyclerAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        mMagicString = new ArrayList<>();
    }

    public void setMagicList(List<String> MagicList) {
        this.mMagicString = MagicList;
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }

    public void setCanDeleted(boolean isCanDeleted) {
        this.isCanDeleted = isCanDeleted;
    }

    public boolean isCanDeleted() {
        return isCanDeleted;
    }

    public List<String> getMagicList() {
        return this.mMagicString;
    }

    @Override
    public int getItemCount() {
        return mMagicString.size();
    }

    @Override
    public MagicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lsq_magic_recycler_item_view, null);
        MagicViewHolder viewHolder = new MagicViewHolder(view);
        return viewHolder;
    }

    public String getParticleCode(int position) {
        return mMagicString.get(position);
    }

    @Override
    public void onBindViewHolder(final MagicViewHolder magicViewHolder, @SuppressLint("RecyclerView") final int position) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) magicViewHolder.item_view.getLayoutParams();

        if (position == 0) {
            layoutParams.leftMargin = TuSdkContext.dip2px(18);
        } else {
            layoutParams.leftMargin = TuSdkContext.dip2px(3);
        }
        magicViewHolder.item_view.setLayoutParams(layoutParams);
        String magicCode = mMagicString.get(position);
        magicCode = magicCode.toLowerCase();
        String magicImageName = getThumbPrefix() + magicCode;
        magicViewHolder.mImageLayout.setVisibility(View.VISIBLE);
        magicViewHolder.effectStroke.setSelected(false);
//         if (position == mCurrentPosition) {
//          //  magicViewHolder.mNoneLayout.setVisibility(View.GONE);
////            magicViewHolder.mTitleView.setVisibility(View.GONE);
////            magicViewHolder.mSelectLayout.setVisibility(View.VISIBLE);
//            magicViewHolder.effectStroke.setSelected(true);
//        } else {
//          //  magicViewHolder.mNoneLayout.setVisibility(View.GONE);
//
////            magicViewHolder.mSelectLayout.setVisibility(View.GONE);
//             magicViewHolder.effectStroke.setSelected(false);
//
//
//        }
//        magicViewHolder.mTitleView.setText(TuSdkContext.getString(getTextPrefix() + magicCode));

        magicViewHolder.mTitleView.setText(mContext.getResources().getString(PARTICLE_CODES[position]));


        magicViewHolder.mTitleView.setVisibility(View.VISIBLE);
        Bitmap filterImage = TuSdkContext.getRawBitmap(magicImageName);
        if (filterImage != null) {
            magicViewHolder.mItemImage.setImageBitmap(filterImage);
        }
        // 反馈点击
        magicViewHolder.itemView.setClickable(true);
//        magicViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentTimeMillis = System.currentTimeMillis();
////                if(listener != null)
////                    listener.onItemClick(position,magicViewHolder);
////                notifyItemChanged(mCurrentPosition);
////                notifyItemChanged(position);
////                if(position == 0)return;
////                mCurrentPosition = position;
//            }
//        });
        magicViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onItemTouchListener == null) return false;
                onItemTouchListener.onItemTouch(event, position, magicViewHolder);
            //    magicViewHolder.itemView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        magicViewHolder.itemView.setTag(position);
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

    public String getMagicCode(int position) {
        if (mMagicString == null && mMagicString.size() < position) return "None";
        return mMagicString.get(position);
    }

    public class MagicViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleView;
        public TuSdkImageView mItemImage;
        public FrameLayout mSelectLayout;

        public RelativeLayout mImageLayout;
        public View item_view;
        public RelativeLayout effectStroke;

        public MagicViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.lsq_item_title);
            mItemImage = itemView.findViewById(R.id.lsq_item_image);
            mSelectLayout = itemView.findViewById(R.id.lsq_select_layout);
            mImageLayout = itemView.findViewById(R.id.lsq_image_layout);
            item_view = itemView.findViewById(R.id.item_view);
            effectStroke = itemView.findViewById(R.id.effect_stroke);
            mItemImage.setCornerRadiusDP(5);
        }
    }
}
