package org.lasque.twsdkvideo.video_beauty.effectcamera.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.lasque.tusdkpulse.core.TuSdkContext;
import org.lasque.tusdkpulse.core.struct.TuSdkSize;


public class VideoContent extends RelativeLayout {


    public VideoContent(Context context) {
        super(context);
    }

    public VideoContent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置宽度
     *
     * @param width
     */
    public void setWidth(int width) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if(params == null) return;
        params.width = width;
        setLayoutParams(params);
    }

    /**
     * 设置当前控件高度
     *
     * @param height
     */
    public void setHeight(int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if(params == null) return;
        params.height = height;
        setLayoutParams(params);
    }

    /**
     * 重设宽高 默认居中
     * @param size
     */
    public void resize(TuSdkSize size){
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.width = size.width+5;
        layoutParams.height = size.height+5;
        layoutParams.leftMargin = (TuSdkContext.getScreenSize().width - layoutParams.width) / 2;
        layoutParams.topMargin = (TuSdkContext.getScreenSize().height -layoutParams.height) / 2;
    }
}
