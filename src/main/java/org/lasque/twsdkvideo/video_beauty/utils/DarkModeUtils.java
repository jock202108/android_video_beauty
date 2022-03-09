package org.lasque.twsdkvideo.video_beauty.utils;

import android.content.Context;

import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;

public class DarkModeUtils {
    
    public static int getColor(Context context,int lightColor,int darkColor){
        int color = VideoBeautyPlugin.themeMode == 0?context.getResources().getColor(lightColor):context.getResources().getColor(darkColor);
        return  color;
    }

    public static int getImageResource(Context context,int lightRsId,int darkRsId){
        int resId = VideoBeautyPlugin.themeMode == 0?lightRsId:darkRsId;
        return  resId;
    }

    public static int getTextColor(Context context,int lightColor,int darkColor){
        int color = VideoBeautyPlugin.themeMode == 0?lightColor:darkColor;
        return  color;
    }
}
