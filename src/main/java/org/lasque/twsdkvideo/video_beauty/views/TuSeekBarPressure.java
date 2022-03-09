package org.lasque.twsdkvideo.video_beauty.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.lasque.tusdk.core.struct.ViewSize;
import org.lasque.tusdk.impl.view.widget.TuSeekBar;

/**
 * TuSDK
 * org.lasque.tusdkdemohelper.tusdk.newUI.CustomUi
 * qiniu-PLDroidMediaStreamingDemo
 *
 * @author H.ys
 * @Date 2020/9/1  14:14
 * @Copyright (c) 2020 tw. All rights reserved.
 */
public class TuSeekBarPressure extends TuSeekBar {

    private View mSecondSeek, mLeftSecondSeek;

    private float mSecondProgress;

    public TuSeekBarPressure(Context context) {
        super(context);
    }

    public TuSeekBarPressure(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TuSeekBarPressure(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public View getSecondView(){
        if (mSecondSeek == null){
            mSecondSeek = getViewById("lsq_seekSecond");
        }
        return mSecondSeek;
    }

    public View getLeftSecondView(){
        if (mLeftSecondSeek == null){
            mLeftSecondSeek = getViewById("lsq_left_seekSecond");
        }
        return mLeftSecondSeek;
    }

    public void setSecondProgress(float progress){
        if (progress < 0)
        {
            progress = 0;
        }
        else if (progress > 1)
        {
            progress = 1;
        }
        mSecondProgress = progress;

        //和富贵沟通，需求改为不显示 30/75 点
        /*int secondBtnWidth = ViewSize.create(getSecondView()).width;

        int offset = (int) Math.floor(mTotalWidth * this.mSecondProgress);

        this.setMarginLeft(this.getSecondView(), offset - secondBtnWidth / 2
                + mPadding);

        //增加点
        int leftSecondBtnWidth = ViewSize.create(getLeftSecondView()).width;

        this.setMarginLeft(this.getLeftSecondView(), (offset - leftSecondBtnWidth / 2
                + mPadding) / 3);*/
    }

    public int getDropWidth(){
        return mBtnWidth;
    }
    public int getBtnPadding(){
        return mPadding;
    }

    @Override
    protected void onSizeChanged(int i, int i1, int i2, int i3) {
        super.onSizeChanged(i, i1, i2, i3);
        setSecondProgress(mSecondProgress);
    }
}
