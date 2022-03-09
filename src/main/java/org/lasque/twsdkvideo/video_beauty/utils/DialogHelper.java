package org.lasque.twsdkvideo.video_beauty.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;


public class DialogHelper {

    private static void setCustomerDialogAttributes(Context context, Dialog dlg, View contentView, int gravity, boolean canceledOnTouchOutside, boolean cancelable) {
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = gravity;
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.setWindowAnimations(R.style.promptdialog_anim); //设置窗口弹出动画
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        lp.width = display.getWidth() - 220;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        if (dlg.isShowing()) {
            dlg.dismiss();
        }
        window.setAttributes(params);
        dlg.onWindowAttributesChanged(params);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.setCancelable(cancelable);
        dlg.setContentView(contentView);
    }

    // 没有动画的dialog样式
    private static void setCustomerDialogNoAnimationAttributes(Context context, Dialog dlg, View contentView, int gravity, boolean canceledOnTouchOutside, boolean cancelable) {
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = gravity;
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        //window.setWindowAnimations(R.style.promptdialog_anim); //设置窗口弹出动画
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        lp.width = display.getWidth() - 220;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        if (dlg.isShowing()) {
            dlg.dismiss();
        }
        window.setAttributes(params);
        dlg.onWindowAttributesChanged(params);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.setCancelable(cancelable);
        dlg.setContentView(contentView);
    }


    public static void remindCenter(Context context, String titlestr, final onRemindSureClickListener listener) {
        boolean isDarkTheme = VideoBeautyPlugin.themeMode != 0;
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_remind_tips, null);
        layout.setSelected(isDarkTheme);
        final TextView title = (TextView) layout.findViewById(R.id.remind_title);
        title.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final Button cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        final Button discardBtn = (Button) layout.findViewById(R.id.btn_sure);
        title.setText(titlestr);
        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onSureClick();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        setCustomerDialogAttributes(context, dlg, layout, Gravity.CENTER, true, true);
        dlg.show();
    }


    /// 进入trim清空编辑提示框
    public static void discardEditsTip(Context context, final onRemindSureClickListener listener) {
        boolean isDarkTheme = VideoBeautyPlugin.themeMode != 0;
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_discard_edits_tips, null);
        layout.setSelected(isDarkTheme);
        final TextView title = (TextView) layout.findViewById(R.id.remind_title);
        title.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final Button cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        final Button discardBtn = (Button) layout.findViewById(R.id.btn_sure);
        title.setText(context.getString(R.string.discard_edits_tip_title));
        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onSureClick();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        setCustomerDialogAttributes(context, dlg, layout, Gravity.CENTER, true, true);
        dlg.show();
    }


    /// 有标题有文本的提示框
    public static void remindTitleAndContentCenter(Context context, String titlestr, String content, final onRemindSureClickListener listener) {
        boolean isDarkTheme = VideoBeautyPlugin.themeMode != 0;
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_remind_title_content, null);
        layout.setSelected(isDarkTheme);
        final TextView title = (TextView) layout.findViewById(R.id.remind_title);
        title.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final TextView remindContent = (TextView) layout.findViewById(R.id.remind_content);
        remindContent.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final Button cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        final Button discardBtn = (Button) layout.findViewById(R.id.btn_sure);
        title.setText(titlestr);
        if(TextUtils.isEmpty(content)){
            remindContent.setVisibility(View.GONE);
        }else{
            remindContent.setText(content);
        }

        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onSureClick();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        setCustomerDialogNoAnimationAttributes(context, dlg, layout, Gravity.CENTER, true, true);
        dlg.show();
    }


//    public static void recordUndoClip(Context context, final onRecordUndoClickListener listener) {
//        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
//        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_undo_record, null);
//        final Button keepBtn = layout.findViewById(R.id.btn_keep);
//        final Button discardBtn = layout.findViewById(R.id.btn_discard);
//        discardBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dlg.dismiss();
//                listener.onDiscardVideoClick();
//            }
//        });
//        keepBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dlg.dismiss();
//            }
//        });
//        setCustomerDialogNoAnimationAttributes(context, dlg, layout, Gravity.CENTER, true, true);
//        dlg.show();
//    }

    /**
     * 关闭提示dialog
     *
     * @param context
     * @param listener
     */
    public static void closeTipDialog(Context context, String tipTitle, final onDiscardClickListener listener) {
        boolean isDarkTheme = VideoBeautyPlugin.themeMode != 0;
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_undo_record, null);
        layout.setSelected(isDarkTheme);
        TextView tipTitleText = layout.findViewById(R.id.record_undo_title);
        tipTitleText.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        tipTitleText.setText(tipTitle);
        final Button keepBtn = layout.findViewById(R.id.btn_keep);
        final Button discardBtn = layout.findViewById(R.id.btn_discard);
        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onDiscardClick();
            }
        });
        keepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        setCustomerDialogNoAnimationAttributes(context, dlg, layout, Gravity.CENTER, true, true);
        dlg.show();
    }


    public static void recordClose(Context context, final onRecordCloseClickListener listener) {
        boolean isDarkTheme = VideoBeautyPlugin.themeMode != 0;
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_record_close, null);
        layout.setSelected(isDarkTheme);
        final TextView tvTitle = layout.findViewById(R.id.record_title);
        final TextView tvContent = layout.findViewById(R.id.record_title_describe);
        tvTitle.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        tvContent.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final Button discardVideoBtn = layout.findViewById(R.id.btn_discard_video);
        final Button startOverBtn = layout.findViewById(R.id.btn_start_over);
        final Button saveAsDraftBtn = layout.findViewById(R.id.btn_save_as_draft);
        final Button cancelBtn = layout.findViewById(R.id.btn_cancel);

        discardVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (listener != null) {
                    listener.onDiscardVideoClick();
                }
            }
        });
        startOverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (listener != null) {
                    listener.onStartOverClick();
                }
            }
        });
        saveAsDraftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (listener != null) {
                    listener.onSaveAsDraftClick();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        setCustomerDialogNoAnimationAttributes(context, dlg, layout, Gravity.CENTER, true, true);
        dlg.show();
    }

    public static void recordRecover(Context context, final onRecordRecoverClickListener listener) {
        boolean isDarkTheme = VideoBeautyPlugin.themeMode != 0;
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_record_recover, null);
        layout.setSelected(isDarkTheme);
        final TextView tvTitle = layout.findViewById(R.id.record_title);
        final TextView tvContent = layout.findViewById(R.id.record_title_describe);
        tvTitle.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        tvContent.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final Button continueBtn = layout.findViewById(R.id.btn_continue);
        final Button startNewBtn = layout.findViewById(R.id.btn_start_new);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (listener != null) {
                    listener.onContinue();
                }
            }
        });
        startNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (listener != null) {
                    listener.onStartNewClick();
                }
            }
        });
        setCustomerDialogNoAnimationAttributes(context, dlg, layout, Gravity.CENTER, false, false);
        dlg.show();
    }


    public static void permissionDialog(Context context, String titleStr, String content, final onRemindSureClickListener listener, boolean isDarkTheme) {
        final Dialog dlg = new Dialog(context, R.style.CustomDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_permission_tips, null);
        layout.setSelected(isDarkTheme);
        final TextView tvTitle = (TextView) layout.findViewById(R.id.permission_title);
        tvTitle.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final TextView tvContent = (TextView) layout.findViewById(R.id.permission_content);
        tvContent.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_color_black));
        final Button cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        cancelBtn.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.lsq_color_white) : context.getResources().getColor(R.color.lsq_light_blue));
        final Button settingBtn = (Button) layout.findViewById(R.id.btn_sure);
        settingBtn.setTextColor(isDarkTheme ? context.getResources().getColor(R.color.color_1589FE) : context.getResources().getColor(R.color.lsq_light_blue));
        tvTitle.setText(titleStr);
        tvContent.setText(content);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                listener.onSureClick();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        setCustomerDialogAttributes(context, dlg, layout, Gravity.CENTER, true, true);
        dlg.show();
    }


    public interface onRemindSureClickListener {
        void onSureClick();
    }

    public interface onRecordCloseClickListener {
        void onDiscardVideoClick();

        void onStartOverClick();

        void onSaveAsDraftClick();
    }

    public interface onDiscardClickListener {
        void onDiscardClick();
    }

    public interface onRecordRecoverClickListener {
        void onContinue();

        void onStartNewClick();
    }

}

