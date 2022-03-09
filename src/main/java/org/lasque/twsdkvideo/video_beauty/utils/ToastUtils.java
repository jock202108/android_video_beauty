package org.lasque.twsdkvideo.video_beauty.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ToastUtils {
    public static void showToast(Context context, String msg) {
        Toast toast = new Toast(context);
        //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
        toast.setGravity(Gravity.TOP, 0, 50);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.layout_beauty_toast, null);
        TextView tvMessage = view.findViewById(R.id.tv_toast);
        //设置文本
        tvMessage.setText(msg);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();
    }

    public static void showCustomToast(Activity context, String msg) {
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.layout_beauty_toast, null);
        LinearLayout toastLl = view.findViewById(R.id.ll_toast);
        toastLl.setSelected(VideoBeautyPlugin.themeMode != 0);
        TextView tvMessage = view.findViewById(R.id.tv_toast);
        tvMessage.setTextColor(VideoBeautyPlugin.themeMode == 0 ? context.getResources().getColor(R.color.lsq_light_blue) : context.getResources().getColor(R.color.lsq_color_white));
        //设置文本
        tvMessage.setText(msg);
        Configuration.Builder cfg = new Configuration.Builder();
        cfg
                .setInAnimation(R.anim.crouton_in) // 设置入场动画
                .setOutAnimation(R.anim.crouton_out)// 设置出厂动画
                .setDuration(2000);// 设置时间
        // 在子view布局弹出
        Crouton.make(context, view, R.id.mToast, cfg.build()).show();
    }

    public static void showRedToast(Activity context, String msg) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_min_record_tips_toast, null);
        LinearLayout toastLl = view.findViewById(R.id.ll_toast);
        toastLl.setSelected(VideoBeautyPlugin.themeMode != 0);
        TextView tvMessage = view.findViewById(R.id.tv_toast);
//        tvMessage.setTextColor(VideoBeautyPlugin.themeMode == 0 ? context.getResources().getColor(R.color.lsq_light_blue) : context.getResources().getColor(R.color.lsq_color_white));
        //设置文本
        tvMessage.setText(msg);
        Configuration.Builder cfg = new Configuration.Builder();
        cfg
                .setInAnimation(R.anim.crouton_in) // 设置入场动画
                .setOutAnimation(R.anim.crouton_out)// 设置出厂动画
                .setDuration(2000);// 设置时间
        // 在子view布局弹出
        Crouton.make(context, view, R.id.mToast, cfg.build()).show();
    }

}
