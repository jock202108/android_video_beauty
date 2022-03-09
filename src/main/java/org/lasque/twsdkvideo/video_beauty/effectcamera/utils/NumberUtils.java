package org.lasque.twsdkvideo.video_beauty.effectcamera.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class NumberUtils {

    /**
     * 格式化为两位保留两位小数
     *
     * @return 格式化之后的数字
     */
    public static float formatFloat2f(float num) {
        DecimalFormat fnum = new DecimalFormat(".00");
        return Float.valueOf(fnum.format(num));
    }
}
