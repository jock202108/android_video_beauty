package org.lasque.twsdkvideo.video_beauty.utils;

public class AppColorUtils {

    public static String getHexString(int color) {
        String s = "#";
        int colorStr = (color & 0xff000000) | (color & 0x00ff0000) | (color & 0x0000ff00) | (color & 0x000000ff);
        s = s + Integer.toHexString(colorStr);
        return s;
    }
}
