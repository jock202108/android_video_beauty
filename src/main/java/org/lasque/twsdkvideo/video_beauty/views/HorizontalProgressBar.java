package org.lasque.twsdkvideo.video_beauty.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import androidx.annotation.Nullable;

import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.twsdkvideo.video_beauty.R;

import java.util.LinkedList;

/**
 * @author xujie
 * @Date 2018/11/12
 */

public class HorizontalProgressBar extends View {

    // 记录每次暂停时的进度
    private LinkedList<Float> mPauseProgressList;
    // 进度值
    private float mProgress;

    private Paint mProgressPaint;
    private Paint mBackgroundPaint;
    // 进度条颜色
    private int mProgressColor;
    // 进度条背景色
    private int mBackgroundColor;
    // 默认高度
    private float mDefaultHeight;
    //圆角进度条的rectf
    private RectF mRectF;
    private RectF mBackgroundRectF;
    //圆角矩形角度
    private float mRxy;


    public HorizontalProgressBar(Context context) {
        this(context,null);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context, attrs);
        init();
    }

    private void init(){
        mPauseProgressList = new LinkedList<>();
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);
        mBackgroundPaint = new Paint(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mRectF = new RectF();
        mBackgroundRectF = new RectF();
        mRxy = mDefaultHeight / 2;
    }

    private void getAttrs(Context context,AttributeSet attrs){
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        mProgressColor = attributes.getColor(R.styleable.HorizontalProgressBar_progressColor, 0xFFCC0000);
        mBackgroundColor = attributes.getColor(R.styleable.HorizontalProgressBar_background_color,0x00ffffff);
        mDefaultHeight = attributes.getDimension(R.styleable.HorizontalProgressBar_defaultHeight,TuSdkContext.dip2px(8f));
        attributes.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        if(mRectF != null){
            mRectF.set(0f,0f,width * mProgress,mDefaultHeight);
        }
        if(mBackgroundRectF != null){
            mBackgroundRectF.set(0f,0f,width,mDefaultHeight);
        }
        //尝试xfermode 方式效果出不来，改为shader方式 int是两个色值，一个进度的红色一个背景的灰色，后续可以放到color里
        @SuppressLint("DrawAllocation") LinearGradient mProgressBgGradient = new LinearGradient(0, 0, width, 0,
                new int[]{mProgressColor, mBackgroundColor},
                new float[]{mProgress, mProgress},
                Shader.TileMode.CLAMP
        );
        mProgressPaint.setShader(mProgressBgGradient);
        canvas.drawRoundRect(mBackgroundRectF, mRxy, mRxy, mProgressPaint);
        //canvas.drawRoundRect(mRectF, mRxy, mRxy, mBackgroundPaint);

    }

    /** 清除记录 */
    public void clearProgressList(){
        mPauseProgressList.clear();
        setProgress(0);
    }

    /** 获取当前录制记录数 */
    public int getRecordProgressListSize(){
        return mPauseProgressList.size();
    }

    /** 暂停录制 (将添加一个进度) **/
    public synchronized void pauseRecord(){
        if(mPauseProgressList == null)return;
        if (getProgress() > 1) return;
        mPauseProgressList.addLast(getProgress());
    }

    /**
     * 移除上一个片段
     * @return true 删除成功  false 删除失败
     */
    public synchronized boolean removePreSegment() {
        if(mPauseProgressList == null || mPauseProgressList.size() == 0) return false;
        mPauseProgressList.removeLast();
        if(mPauseProgressList.size() != 0) setProgress(mPauseProgressList.getLast());
        else setProgress(0);
        return true;
    }

    public synchronized void setProgress(float progress){
        this.mProgress = progress;
        postInvalidate();
    }
    public synchronized float getProgress(){
        return mProgress;
    }
}
