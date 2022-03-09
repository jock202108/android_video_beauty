package org.lasque.twsdkvideo.video_beauty.effectcamera.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.lasque.tusdkpulse.core.struct.ViewSize;
import org.lasque.tusdkpulse.impl.view.widget.TuSeekBar;


public class TuSeekBarPressure extends TuSeekBar {

    private View mSecondSeek;

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

        int secondBtnWidth = ViewSize.create(getSecondView()).width;

        int offset = (int) Math.floor(mTotalWidth * this.mSecondProgress);

        this.setMarginLeft(this.getSecondView(), offset - secondBtnWidth / 2
                + mPadding);
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
