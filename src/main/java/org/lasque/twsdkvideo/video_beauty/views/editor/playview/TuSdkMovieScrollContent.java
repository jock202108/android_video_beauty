package org.lasque.twsdkvideo.video_beauty.views.editor.playview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

/**
 * 滚动根视图
 *
 * @author MirsFang
 */
public class TuSdkMovieScrollContent extends RelativeLayout {
    private static final String TAG = "TuSdkMovieScrollContent";
    private TuSdkMovieCoverListView mCoverListView;
    private TuSdkRangeSelectionBar mSelectRange;
    protected TuSdkMovieColorGroupView mColorGroupView;
    private OnPlayProgressChangeListener progressChangeListener;
    private ImageView mCursorView;
    private boolean isAddedCoverList;
    private boolean isMeasureBarWidth;
    private boolean isNeedShowCursor = false;
    /**
     * 是否正在触摸中
     **/
    private boolean isTouching = false;

    private int mType = 0;
    private float mCursorMinPercent = 0;
    private float mCursorMaxPercent = 1;
    private boolean isEnable = true;
    private float cursorHeight=0;
    private float coverListHeight;
    private float coverListTopMargin;
    private int mOutlineType = 0;
    private View centerTopLine;
    private View centerCoverLine;
    private View centerBottomLine;
    private View leftBarBg;
    private View leftBarLine;
    private View rightBarBg;
    private View rightBarLine;

    public float getCursorMinPercent() {
        return mCursorMinPercent;
    }

    public void setCursorMinPercent(float mCursorMinPercent) {
        this.mCursorMinPercent = mCursorMinPercent;
    }

    public void setCursorListHeight(float coverListHeight) {
        this.coverListHeight = coverListHeight;
    }

    public void setCursorListTopMargin(float coverListTopMargin) {
        this.coverListTopMargin = coverListTopMargin;
    }

    public float getCursorMaxPercent() {
        return mCursorMaxPercent;
    }

    public void setCursorMaxPercent(float mCursorMaxPercent) {
        this.mCursorMaxPercent = mCursorMaxPercent;
    }

    public interface OnPlayProgressChangeListener {
        void onProgressChange(float percent);
    }

    public TuSdkMovieScrollContent(Context context) {
        super(context);
        init(context, null);
    }

    public TuSdkMovieScrollContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setProgressChangeListener(OnPlayProgressChangeListener progressChangeListener) {
        this.progressChangeListener = progressChangeListener;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MovieScrollContent);
        cursorHeight = attributes.getDimension(R.styleable.MovieScrollContent_cursorHeight, 0);
        coverListHeight = attributes.getDimension(R.styleable.MovieScrollContent_coverListHeight, 0);
        cursorHeight = attributes.getDimension(R.styleable.MovieScrollContent_cursorHeight, 0);
        coverListTopMargin = attributes.getDimension(R.styleable.MovieScrollContent_coverListTopMargin, 0);
        mOutlineType = attributes.getInt(R.styleable.MovieScrollContent_outlineType, mOutlineType);

        mCoverListView = new TuSdkMovieCoverListView(getContext());
        mSelectRange = (TuSdkRangeSelectionBar) LayoutInflater.from(getContext()).inflate(R.layout.lsq_range_selection, null);
        centerTopLine = mSelectRange.findViewById(R.id.lsq_center_top_line);
        centerCoverLine = mSelectRange.findViewById(R.id.lsq_center_cover);
        centerBottomLine = mSelectRange.findViewById(R.id.lsq_center_bottom_line);
        leftBarBg = mSelectRange.findViewById(R.id.lsq_left_bar_bg);
        leftBarLine = mSelectRange.findViewById(R.id.lsq_left_bar_line);
        rightBarBg = mSelectRange.findViewById(R.id.lsq_right_bar_bg);
        rightBarLine = mSelectRange.findViewById(R.id.lsq_right_bar_line);
        setOutLineType();

        mColorGroupView = new TuSdkMovieColorGroupView(getContext());
        mCursorView = new ImageView(getContext());
        setPercent(0);


    }

    public void setOutlineType(int outlineType) {
        this.mOutlineType = outlineType;
        setOutLineType();
    }

    /**
     * 设置选中框颜色类型
     * 1 白色
     * 2 红色
     * 3 之后中间红，两边没有 bar
     */
    private void setOutLineType() {
        if (mOutlineType == 0) {
            centerTopLine.setVisibility(View.VISIBLE);
            centerBottomLine.setVisibility(View.VISIBLE);
            leftBarBg.setVisibility(View.VISIBLE);
            leftBarLine.setVisibility(View.VISIBLE);
            rightBarBg.setVisibility(View.VISIBLE);
            rightBarLine.setVisibility(View.VISIBLE);
            centerCoverLine.setVisibility(View.VISIBLE);

            centerTopLine.setBackgroundColor(getResources().getColor(R.color.lsq_color_white));
            centerCoverLine.setVisibility(View.GONE);
            centerBottomLine.setBackgroundColor(getResources().getColor(R.color.lsq_color_white));
            leftBarBg.setBackgroundResource(R.drawable.lsq_left_bar_rect);
            leftBarLine.setBackgroundResource(R.drawable.lsq_left_bar_line);
            rightBarBg.setBackgroundResource(R.drawable.lsq_right_bar_rect);
            rightBarLine.setBackgroundResource(R.drawable.lsq_right_bar_line);
        } else if(mOutlineType == 1){
            centerTopLine.setVisibility(View.VISIBLE);
            centerBottomLine.setVisibility(View.VISIBLE);
            leftBarBg.setVisibility(View.VISIBLE);
            leftBarLine.setVisibility(View.VISIBLE);
            rightBarBg.setVisibility(View.VISIBLE);
            rightBarLine.setVisibility(View.VISIBLE);
            centerCoverLine.setVisibility(View.VISIBLE);

            centerTopLine.setBackgroundColor(getResources().getColor(R.color.lsq_range_red));
            centerCoverLine.setBackgroundColor(getResources().getColor(R.color.lsq_center_red_cover));
            centerBottomLine.setBackgroundColor(getResources().getColor(R.color.lsq_range_red));
            leftBarBg.setBackgroundResource(R.drawable.lsq_left_bar_rect);
            leftBarLine.setBackgroundResource(R.drawable.lsq_left_bar_line_text);
            rightBarBg.setBackgroundResource(R.drawable.lsq_right_bar_rect);
            rightBarLine.setBackgroundResource(R.drawable.lsq_right_bar_line_text);
        }else if(mOutlineType == 2){
            centerTopLine.setVisibility(View.GONE);
            centerCoverLine.setVisibility(View.VISIBLE);
            centerCoverLine.setBackgroundColor(getResources().getColor(R.color.lsq_center_red_cover));
            centerBottomLine.setVisibility(View.GONE);
            leftBarBg.setVisibility(View.GONE);
            leftBarLine.setVisibility(View.GONE);
            rightBarBg.setVisibility(View.GONE);
            rightBarLine.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int width = getWidth();
        int height = getHeight();
        if (coverListHeight != 0) {
            height = (int) coverListHeight;
        }
        if (isAddedCoverList && isMeasureBarWidth && mType == 0) return;
        if (!isAddedCoverList) {

            LayoutParams layoutParams0 = new LayoutParams(width, height);
            layoutParams0.leftMargin = TuSdkContext.dip2px(14);
            layoutParams0.rightMargin = TuSdkContext.dip2px(14);

            if (coverListTopMargin != 0) {
                layoutParams0.topMargin = (int) coverListTopMargin;
            }
            addView(mCoverListView, layoutParams0);


            //添加画色控件
            LayoutParams layoutParams2 = new LayoutParams(width, getHeight());
            if (coverListTopMargin != 0) {
                layoutParams2.topMargin = TuSdkContext.dip2px(2.4f);
                layoutParams2.bottomMargin = TuSdkContext.dip2px(5f);
            }
            layoutParams2.leftMargin = TuSdkContext.dip2px(14);
            layoutParams2.rightMargin = TuSdkContext.dip2px(14);
            addView(mColorGroupView, layoutParams2);
            isAddedCoverList = true;

            //添加选区控件
            if (mType == 1) {
                LayoutParams layoutParams1 = new LayoutParams(width, height);
                if (coverListTopMargin != 0) {
                    layoutParams1.topMargin = (int) coverListTopMargin;
                }
                addView(mSelectRange, layoutParams1);
                // 重新测量
                mSelectRange.measure(mSelectRange.getMeasuredWidth(), mSelectRange.getMeasuredHeight());
            }

            if (isNeedShowCursor) {
                float cursorHt=TuSdkContext.dip2px(99);
                if(cursorHeight!=0){
                    cursorHt = cursorHeight;
                }
                LayoutParams layoutParams3 = new LayoutParams(TuSdkContext.dip2px(3), (int) cursorHt);
//                layoutParams3.leftMargin = TuSdkContext.dip2px(11);
                mCursorView.setBackgroundColor(getResources().getColor(R.color.lsq_filter_title_color));
                mCursorView.setBackgroundResource(R.drawable.lsq_cursor_bar);

                addView(mCursorView, layoutParams3);
            }
        }

        /** 第一测量getBarWidth()为0 **/
        if (mSelectRange.getBarWidth() != 0 && mType == 1 && !isMeasureBarWidth) {
            LayoutParams mCoverLayoutParams = (LayoutParams) mCoverListView.getLayoutParams();
            mCoverLayoutParams.leftMargin = mSelectRange.getBarWidth();
            mCoverListView.setLayoutParams(mCoverLayoutParams);

            LayoutParams mSelectLayoutParams = (LayoutParams) mSelectRange.getLayoutParams();
            mSelectLayoutParams.width = mSelectLayoutParams.width + mSelectRange.getBarWidth() * 2;
            mSelectRange.setLayoutParams(mSelectLayoutParams);

            LayoutParams mColorLayoutParams = (LayoutParams) mColorGroupView.getLayoutParams();
            mColorLayoutParams.leftMargin = mSelectRange.getBarWidth();
            mColorGroupView.setLayoutParams(mColorLayoutParams);

            isMeasureBarWidth = true;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    /**
     * 是否需要展示游标
     **/
    public void setNeedShowCursor(boolean needShowCursor) {
        this.isNeedShowCursor = needShowCursor;
    }

    /**
     * 添加图片
     */
    public void addBitmap(Bitmap bitmap) {
        mCoverListView.addBitmap(bitmap);
    }

    /**
     * 添加首帧图片
     */
    public void addFirstFrameBitmap(Bitmap bitmap) {
        mCoverListView.addFirstFrameBitmap(bitmap);
    }

    /**
     * 获取总的宽度
     */
    public int getTotalWidth() {
        return mCoverListView.getTotalWidth();
    }

    /**
     * 更新宽度
     **/
    public void updateScrollPercent(float percent) {
        TuSdkMovieColorRectView rectView = mColorGroupView.getLastColorRect();
        if (rectView == null) return;
        int distance = (int) (getTotalWidth() * (percent - rectView.getStartPercent()));
        rectView.setEndPercent(percent);
        mColorGroupView.updateLastWidth(distance);
    }

    /**
     * 添加一个颜色区块
     **/
    public void addColorRect(TuSdkMovieColorRectView rectView) {
        mColorGroupView.addColorRect(rectView);
    }

    /**
     * 删除一个颜色区块
     **/
    public TuSdkMovieColorRectView deletedColorRect() {
        return mColorGroupView.removeLastColorRect();
    }

    public void deletedColorRect(TuSdkMovieColorRectView rectView) {
        mColorGroupView.removeColorRect(rectView);
    }

    /**
     * 设置类型
     **/
    public void setType(int type) {
        this.mType = type;
    }

    /**
     * 设置Bar改变的监听
     **/
    public void setSelectRangeChangedListener(TuSdkRangeSelectionBar.OnSelectRangeChangedListener changedListener) {
        mSelectRange.setSelectRangeChangedListener(changedListener);
    }

    /**
     * 设置Bar的临界值监听
     **/
    public void setExceedCriticalValueListener(TuSdkRangeSelectionBar.OnExceedCriticalValueListener exceedValueListener) {
        mSelectRange.setExceedCriticalValueListener(exceedValueListener);
    }

    public void setOnTouchSelectBarListener(TuSdkRangeSelectionBar.OnTouchSelectBarListener onTouchSelectBarListener) {
        mSelectRange.setOnTouchSelectBarListener(onTouchSelectBarListener);
    }

    /**
     * 设置颜色选择监听
     **/
    public void setOnSelectColorRectListener(TuSdkMovieColorGroupView.OnSelectColorRectListener onSelectColorRectListener) {
        mColorGroupView.setOnSelectColorRectListener(onSelectColorRectListener);
    }

    /**
     * 最小区间占比
     **/
    public void setMinWidth(float minPercent) {
        mSelectRange.setMinWidth(minPercent);
    }

    /**
     * 最大区间占比
     **/
    public void setMaxWidth(float maxPercent) {
        mSelectRange.setMaxWidth(maxPercent);
    }

    /**
     * 移动左边Bar的位置
     **/
    public void setLeftBarPosition(float percent) {
        mSelectRange.setLeftBarPosition(percent);
    }

    /**
     * 移动右边Bar的位置
     **/
    public void setRightBarPosition(float percent) {
        mSelectRange.setRightBarPosition(percent);
    }

    /**
     * 获取左边Bar的进度
     **/
    public float getLeftBarPercent() {
        return mSelectRange.getLeftBarPercent();
    }

    /**
     * 获取右边Bar的进度
     **/
    public float getRightBarPercent() {
        return mSelectRange.getRightBarPercent();
    }

    /**
     * 是否正在显示选择控件
     *
     * @return
     */
    public boolean isShowSelectBar() {
        return mSelectRange.getVisibility() == VISIBLE;
    }

    /**
     * 设置是否显示选择控件
     *
     * @param showSelectBar
     */
    public void setShowSelectBar(boolean showSelectBar) {
        mSelectRange.setVisibility(showSelectBar ? VISIBLE : INVISIBLE);
    }

    public boolean isContain(TuSdkMovieColorRectView rectView) {
        return mColorGroupView.isContain(rectView);
    }

    /**
     * 清楚当前所有颜色区域
     **/
    public void clearAllColorRect() {
        mColorGroupView.clearAllColorRect();
    }

    /**
     * 设置游标进度
     **/
    public void setPercent(final float percent) {
        if (isTouching) return;
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                mCursorView.setX(mCoverListView.getWidth() * percent + TuSdkContext.dip2px(14));
            }
        });
    }

    private float startX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnable) return false;
        if (isTouchPointInView(mCursorView, event.getRawX()) || startX > 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouching = true;
                    startX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
//                    if(mCursorView.getX() < mCoverListView.getLeft() || event.getX() < mCoverListView.getLeft()){
//                        mCursorView.setX(mCoverListView.getLeft());
//                        return false;
//                    }
//
//                    if(mCursorView.getX() >= mCoverListView.getRight() || event.getX() >=  mCoverListView.getRight()){
//                        mCursorView.setX(mCoverListView.getRight()- mCursorView.getWidth()/2);
//                        return false;
//                    }
                    float minLeft = mCoverListView.getLeft() + mCoverListView.getWidth() * mCursorMinPercent;
                    float maxRight = mCoverListView.getLeft() + mCoverListView.getWidth() * mCursorMaxPercent;
                    if (event.getX() >= maxRight || mCursorView.getX() >= maxRight) {
                        mCursorView.setX(maxRight - mCursorView.getWidth() / 2);
                        return false;
                    }
                    if (event.getX() < minLeft || mCursorView.getX() < minLeft) {
                        mCursorView.setX(minLeft);
                        return false;
                    }

                    mCursorView.setX(event.getX());
                    float percent = mCursorView.getX() / mCoverListView.getWidth();
                    if (percent > 1) percent = 1f;
                    if (percent < 0.06) percent = 0f;
                    if (progressChangeListener != null)
                        progressChangeListener.onProgressChange(percent);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mCursorView.getX() < mCoverListView.getLeft() && event.getX() < mCoverListView.getLeft()) {
                        mCursorView.setX(mCoverListView.getLeft());
                        isTouching = false;
                        return false;
                    }

                    if (mCursorView.getX() >= mCoverListView.getRight() && event.getX() >= mCoverListView.getRight()) {
                        mCursorView.setX(mCoverListView.getRight() - mCursorView.getWidth() / 2);
                        isTouching = false;
                        return false;
                    }

                    float percent1 = mCursorView.getX() / mCoverListView.getWidth();
                    if (percent1 > 1) percent1 = 1f;
                    isTouching = false;
                    startX = -1;
                    break;
                case MotionEvent.ACTION_UP:
                    isTouching = false;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchPointInView(View view, float x) {
        int diff = 40;
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int right = left + view.getMeasuredWidth();
        if ((x >= left && x <= right) || (x >= left - diff && x <= right + diff)) {
            return true;
        }
        return false;
    }

    public void release() {
        if (mCoverListView != null) mCoverListView.release();
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
        if (mSelectRange != null) mSelectRange.setEnable(isEnable);
    }
}
