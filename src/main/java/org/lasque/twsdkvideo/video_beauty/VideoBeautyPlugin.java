package org.lasque.twsdkvideo.video_beauty;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.utils.NativeLibraryHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.data.GVisionDynamicStickerBean;
import org.lasque.twsdkvideo.video_beauty.data.GVisionSoundBean;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.event.CollectEvent;
import org.lasque.twsdkvideo.video_beauty.event.DimEvent;
import org.lasque.twsdkvideo.video_beauty.event.MusicListEvent;
import org.lasque.twsdkvideo.video_beauty.event.RecommendBgMusicEvent;
import org.lasque.twsdkvideo.video_beauty.event.SearchResultListEvent;
import org.lasque.twsdkvideo.video_beauty.effectcamera.MovieRecordFullScreenActivity;
import org.lasque.twsdkvideo.video_beauty.utils.AppManager;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.LanguageUtil;
import org.lasque.twsdkvideo.video_beauty.utils.PermissionUtils;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * VideoBeautyPlugin
 */
public class VideoBeautyPlugin  {

    public final static int kGetSoundList = 0;
    public final static int kSearchSound = 1;
    public final static int kAddSoundToFavorite = 2;
    public final static int kRemoveSoundFromFavorite = 3;
    public final static int kGetDimSoundLlist = 4; //模糊音
    public final static int kGetRecommendBgMusicList = 5;
    public final static int kGetEmojiList = 6;
    public final static  int kGetDynamicStickerList = 7;
    public final static  int kGetShootDraft = 8;


    //状态栏高度
    public static int statusBarHeight = 0;
    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static int themeMode = 0;
    public static int navigationBarHeight = 0;
    public static String bgMusicId = "";


    Context context;
    Handler mHandler = new Handler(Looper.myLooper());
    private MainReceiver mMainReceiver;

    private int mRequestCode = -1;
    private Activity activity;
    private String originalVideoPath;

    private void handleRecordButton() {

        mRequestCode = 1;

        if (PermissionUtils.hasRequiredPermissions(activity, getRequiredPermissions())) {
            Intent intent = new Intent(activity, MovieRecordFullScreenActivity.class);

            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_bottom_in, 0);
        } else {
            PermissionUtils.requestRequiredPermissions(activity, getRequiredPermissions());
        }

    }

    private void editVideo(){
        if (!new File(originalVideoPath).exists()) {
            Toast.makeText(context,context.getResources().getString(R.string.lsq_not_file),Toast.LENGTH_SHORT).show();
            return;
        }
        mRequestCode = 2;
        if (PermissionUtils.hasRequiredPermissions(activity, getRequiredPermissions())) {
            Intent intent = new Intent(activity, MovieEditorActivity.class);
            intent.putExtra("videoPath", originalVideoPath);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("videoWidth", screenWidth);
            intent.putExtra("videoHeight", screenHeight);
            intent.putExtra("isFromDraft",true);
            activity. startActivity(intent);
        } else {
            PermissionUtils.requestRequiredPermissions(activity, getRequiredPermissions());
        }

    }

    /**
     * 组件运行需要的权限列表
     *
     * @return 列表数组
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected String[] getRequiredPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
        };

        return permissions;
    }

    /**
     * 授予权限的结果，在对话结束后调用
     *
     * @param permissionGranted
     * true or false, 用户是否授予相应权限
     */
    protected PermissionUtils.GrantedResultDelgate mGrantedResultDelgate = new PermissionUtils.GrantedResultDelgate() {
        @Override
        public void onPermissionGrantedResult(boolean permissionGranted) {
            if (permissionGranted) {
                if (mRequestCode == 1) {
                    Intent intent = new Intent(activity, MovieRecordFullScreenActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.push_bottom_in, 0);
                }else if (mRequestCode == 2) {
                    Intent intent = new Intent(activity, MovieEditorActivity.class);
                    intent.putExtra("videoPath", originalVideoPath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("videoWidth", screenWidth);
                    intent.putExtra("videoHeight", screenHeight);
                    activity. startActivity(intent);

                }
            } else {
//                String msg = TuSdkContext.getString("lsq_camera_no_access", ContextUtils.getAppName(activity));
//
//                TuSdkViewHelper.alert(permissionAlertDelegate, activity, TuSdkContext.getString("lsq_camera_alert_title"),
//                        msg, TuSdkContext.getString("lsq_button_close"), TuSdkContext.getString("lsq_button_setting")
//                );

                DialogHelper.permissionDialog(activity,activity.getResources().getString(R.string.camera_permission_title),activity.getResources().getString(R.string.camera_permission_content),new DialogHelper.onRemindSureClickListener(){
                    @Override
                    public void onSureClick() {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", activity.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                },themeMode != 0);
            }
        }
    };

    /**
     * 权限警告提示框点击事件回调
     */
    protected TuSdkViewHelper.AlertDelegate permissionAlertDelegate = new TuSdkViewHelper.AlertDelegate() {
        @Override
        public void onAlertConfirm(AlertDialog dialog) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", activity.getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }

        @Override
        public void onAlertCancel(AlertDialog dialog) {

        }
    };


    public class MainReceiver extends BroadcastReceiver {
        public MainReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    // 处理广播发过来的数据
    private Map<String, Object> handleBroadcastData(Intent intent) {
        Map<String, Object> resultMap = new HashMap<>();
        int type = intent.getIntExtra("type", -1);
        if (type != -1) {
            switch (type) {
                // 歌曲列表
                case 0:
                    int pageNum = intent.getIntExtra("pageNum", 1);
                    int state = intent.getIntExtra("state", 0);
                    resultMap.put("pageNum", pageNum);
                    resultMap.put("state", state);
                    resultMap.put("type", type);
                    return resultMap;
                // 搜索结果列表
                case 1:
                    String keyword = intent.getStringExtra("keyword");
                    int pageNum1 = intent.getIntExtra("pageNum", 1);
                    resultMap.put("keyword", keyword);
                    resultMap.put("type", type);
                    resultMap.put("pageNum", pageNum1);
                    return resultMap;

                // 收藏某个歌曲
                // 取消收藏某个歌曲
                case 2:
                case 3:
                    String collectMusicId = intent.getStringExtra("musicId");
                    resultMap.put("musicId", collectMusicId);
                    resultMap.put("type", type);
                    return resultMap;
                // 模糊音搜索
                case 4:
                    String searchStr = intent.getStringExtra("keyword");
                    int pageNum2 = intent.getIntExtra("pageNum", 1);
                    resultMap.put("keyword", searchStr);
                    resultMap.put("type", type);
                    resultMap.put("pageNum", pageNum2);
                    return resultMap;

                // 推荐背景音乐
                case 5:
                    int pageNum3 = intent.getIntExtra("pageNum", 1);
                    resultMap.put("type", type);
                    resultMap.put("pageNum", pageNum3);
                    return resultMap;

                    // 贴纸数据
                case 7:
                    resultMap.put("type", type);
                    return resultMap;
                // 拍摄页存草稿
                case 8:
                    String videoUrl = intent.getStringExtra("videoUrl");
                    resultMap.put("videoUrl", videoUrl);
                    resultMap.put("type", type);
                    AppManager.getInstance().finishAllActivity();
                    return resultMap;
                default:
                    return resultMap;
            }
        }
        return resultMap;
    }

    // 处理返回的数据
    private void handleResultData(Map<String, Object> map) {

        int type = (Integer) map.get("type");
        String content = (String) map.get("content");
        if (type != -1) {
            switch (type) {
                case kGetSoundList:
                    EventBus.getDefault().post(new MusicListEvent(content));
                    break;
                case kSearchSound:
                    EventBus.getDefault().post(new SearchResultListEvent(content));
                    break;
                case kAddSoundToFavorite:
                    EventBus.getDefault().post(new CollectEvent(content, true));
                    break;
                case kRemoveSoundFromFavorite:
                    EventBus.getDefault().post(new CollectEvent(content, false));
                    break;
                case kGetDimSoundLlist:
                    EventBus.getDefault().post(new DimEvent(content));
                    break;
                case kGetRecommendBgMusicList:
                    EventBus.getDefault().post(new RecommendBgMusicEvent(content));
                    break;
                case kGetDynamicStickerList:
                    Gson gson = new Gson();
                    GVisionDynamicStickerBean gVisionDynamicStickerBean = gson.fromJson(content, new TypeToken<GVisionDynamicStickerBean>() {
                    }.getType());
                    EventBus.getDefault().post(gVisionDynamicStickerBean);

                    break;
                default:
                    break;
            }
        }

    }
}

