package org.lasque.twsdkvideo.video_beauty.utils;

import android.content.Context;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

public class TextWidthUtils {

    public static float getTextWith(Context context,TextView tvSoundName, String text){
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvSoundName.measure(spec, spec);

        // getMeasuredWidth
        int measuredWidth = tvSoundName.getMeasuredWidth();

        // new textpaint measureText
        TextPaint newPaint = new TextPaint();
        float textSize =context. getResources().getDisplayMetrics().scaledDensity * 15;
        newPaint.setTextSize(textSize);
        float newPaintWidth = newPaint.measureText(text);

        // textView getPaint measureText
        TextPaint textPaint = tvSoundName.getPaint();
        float textPaintWidth = textPaint.measureText(text);
        return  textPaintWidth;
    }
}
