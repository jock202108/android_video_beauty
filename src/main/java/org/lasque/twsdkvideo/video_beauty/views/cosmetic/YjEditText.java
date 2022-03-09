package org.lasque.twsdkvideo.video_beauty.views.cosmetic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import org.lasque.twsdkvideo.video_beauty.R;

//自定义View继承系统EditText
@SuppressLint("AppCompatCustomView")
public class YjEditText extends EditText {
    private static final int DRAWABLE_LEFT = 0;
    private static final int DRAWABLE_TOP = 1;
    private static final int DRAWABLE_RIGHT = 2;
    private static final int DRAWABLE_BOTTOM = 3;
    private Boolean clickable = true;
    private Drawable mClearDrawable;
    public YjEditText(Context context) {
        super(context);
        init();
    }

    public YjEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YjEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        //获取右侧删除按钮的图片
        mClearDrawable = getResources().getDrawable(R.drawable.ic_edit_close);
    }

    @Override //当文本发生变化时
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        /**设置是否显示删除按钮.
         当此EditText的isEnabled()等于true(激活状态) 并且hasFocus()等于true(拥有焦点) 并且已输入内容text长度大于0
         设置显示删除按钮
         */
        setClearIconVisible(isEnabled() && hasFocus() && text.length() > 0);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        //当焦点状态发生变化时设置是否显示删除按钮
        setClearIconVisible(isEnabled() && focused && length() > 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            //当点击动作为MotionEvent.ACTION_UP时
            case MotionEvent.ACTION_UP:
                /**
                 * 获取右侧图片
                 * getCompoundDrawables() :返回左，上，右和下边框的drawable
                 * 我们只有右侧的,也就是数组中的第三个
                 */
                Drawable drawable = getCompoundDrawables()[DRAWABLE_RIGHT];
                /**
                 * 如果drawable 不为空,代表右侧有图片,
                 * 点击位置的X 坐标,位于drawable中,此处只计算了x坐标,如果你觉得不够精确,可以自行添加y轴坐标的计算
                 * 设置text为 "",清空输入内容
                 */
                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                    setText("");
                }
                break;
        }
        return isEnabled() && super.onTouchEvent(event);
    }

    /**
     * 设置是否可点击界面的子view
     */
    public void setClickableChild(Boolean clickable){
        this.clickable = clickable;
    }

    private void setClearIconVisible(boolean visible) {
        /**
         * 设置此View的上下左右的Drawables
         * 如果visible等于true,则使用我们的mClearDrawable ;false时,置空
         */
        setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[DRAWABLE_LEFT], getCompoundDrawables()[DRAWABLE_TOP],
                visible ? mClearDrawable : null, getCompoundDrawables()[DRAWABLE_BOTTOM]);
    }
}
