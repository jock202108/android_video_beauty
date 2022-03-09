package org.lasque.twsdkvideo.video_beauty.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.view.TuSdkViewInterface;

@SuppressLint("AppCompatCustomView")
public class TuSdkTextView extends EditText implements TuSdkViewInterface {
    /**
     * 边框宽度
     */
    private int mStrokeWidth;

    /**
     * 边框颜色
     */
    private int mStrokeColor;

    /**
     * 是否设置边框
     */
    private boolean isSetStroke;

    /** 描边TextView **/
    private EditText outlineTextView = null;

    /** 描边画笔 **/
    private TextPaint mPaint;

    /** 描边宽度 **/
    private int mPaintWidth = TuSdkContext.dip2px(0);

    /** 描边 **/
    private int mPainColor = TuSdkContext.getColor(android.R.color.transparent);


    public TuSdkTextView(Context context) {
        super(context);
        outlineTextView = new EditText(context);
        outlineTextView.setEnabled(false);
        initView();
    }

    public TuSdkTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        outlineTextView = new EditText(context,attrs);
        outlineTextView.setEnabled(false);
        initView();
    }

    public TuSdkTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        outlineTextView = new EditText(context,attrs,defStyleAttr);
        outlineTextView.setEnabled(false);
        initView();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if(outlineTextView == null)return;
        outlineTextView.setText(text,type);
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        if(outlineTextView == null)return;
        outlineTextView.setAlpha(alpha);
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);
        if(outlineTextView == null)return;
        outlineTextView.setGravity(gravity);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if(outlineTextView == null)return;
        outlineTextView.setPadding(left, top, right, bottom);
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        if(outlineTextView == null)return;
        outlineTextView.setLineSpacing(add,mult);
    }


    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        if(outlineTextView == null)return;
        outlineTextView.setTypeface(tf);

    }

    /**
     * 初始化视图
     */
    protected void initView() {
        mPaint = outlineTextView.getPaint();
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        outlineTextView.setTextColor(mPainColor);// 描边颜色
        outlineTextView.setGravity(getGravity());
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
    }

    /**
     * 设置文字描边的宽度
     * @since V3.3.2
     **/
    public void setTextStrokeWidth(int strokeWidth) {
        this.mPaintWidth = strokeWidth;
        mPaint.setStrokeWidth(strokeWidth);
        this.invalidate();
    }

    /**
     * 设置文字描边的颜色
     * @param color
     * @since V3.3.2
     */
    public void setTextStrokeColor(int color) {
        this.mPainColor = color;
        outlineTextView.setTextColor(color);
        this.invalidate();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText()))
        {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.setTextSize(TuSdkContext.px2sp(getTextSize()));
        outlineTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outlineTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //倾斜度45,上下左右居中
        outlineTextView.draw(canvas);
        super.onDraw(canvas);
        drawStroke(canvas);
    }


    /**
     * 绘制边框
     *
     * @param canvas
     */
    protected void drawStroke(Canvas canvas) {
        if (!this.isSetStroke) return;

        // 边框绘制采用的是居中方式，所以需要计算偏移位置
        float strokeOffset = this.mStrokeWidth * 0.5f;

        RectF rectF = new RectF(strokeOffset, strokeOffset, getWidth()
                - strokeOffset, getHeight() - strokeOffset);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.mStrokeColor);
        paint.setStrokeWidth(this.mStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rectF, 0, 0, paint);

        if (this.mStrokeWidth == 0) {
            this.isSetStroke = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setLetterSpacing(float letterSpacing) {
        super.setLetterSpacing(letterSpacing);
        if(outlineTextView == null)return;
        outlineTextView.setLetterSpacing(letterSpacing);
    }

    /**
     * 设置边框
     *
     * @param strokeColor 边框颜色
     * @param strokeWidth 边框宽度
     */
    public void setStroke(int strokeColor, int strokeWidth) {
        this.mStrokeColor = strokeColor;
        this.mStrokeWidth = strokeWidth > 0 ? strokeWidth : 0;
        this.isSetStroke = true;
        this.invalidate();
    }

    /**
     * 删除边框
     */
    public void removeStroke() {
        this.mStrokeColor = 0;
        this.mStrokeWidth = 0;
    }

    /**
     * 绑定视图
     */
    @Override
    public void loadView() {

    }

    /**
     * 视图加载完成
     */
    @Override
    public void viewDidLoad() {

    }

    /**
     * 视图即将销毁
     */
    @Override
    public void viewWillDestory() {

    }

    /**
     * 视图需要重置
     */
    @Override
    public void viewNeedRest() {

    }

    public void setUnderlineText(boolean underline) {
        getPaint().setUnderlineText(underline);
        mPaint.setUnderlineText(underline);
    }
}