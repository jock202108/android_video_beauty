package org.lasque.twsdkvideo.video_beauty.album;

import android.content.Context;
import android.content.Intent;

import com.tusdk.pulse.filter.TuSDKFilter;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.NativeLibraryHelper;
import org.lasque.tusdkpulse.core.TuSdk;
import org.lasque.twsdkvideo.video_beauty.R;


import java.io.File;

import dalvik.system.PathClassLoader;

/**
 * 相册工具类
 */


public class AlbumUtils {
    /**
     * 打开相册选取视频
     */
    public static void openVideoAlbum(String intentClassName, int selectMax) {
        Context context = TuSdkContext.context();
        if (context == null) return;
        Intent intent = new Intent(context, MovieAlbumActivity.class);
        intent.putExtra("cutClassName", intentClassName);
        intent.putExtra("selectMax", selectMax);
        // 修复个别机型上使用ApplicationContext启动Activity崩溃问题
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void init(Context context,String md5,String specialKey) {
//        TuSdk.enableDebugLog(true);
//        TuSdk.setResourcePackageClazz(org.lasque.twsdkvideo.video_beauty.R.class);
//        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
//        NativeLibraryHelper.shared().mapLibrary(NativeLibraryHelper.NativeLibType.LIB_FACE, pathClassLoader.findLibrary("twsdk-face"));
//        NativeLibraryHelper.shared().mapLibrary(NativeLibraryHelper.NativeLibType.LIB_VIDEO, pathClassLoader.findLibrary("twsdk-video"));
//        NativeLibraryHelper.shared().mapLibrary(NativeLibraryHelper.NativeLibType.LIB_CORE, pathClassLoader.findLibrary("twsdk-library"));
//  //      TuSdk.init(context, "a5087fafcd5543f6-02-kij4t1", null, "46a7ce5f73a34eda6488ecab4458960c");//224899b463251e93-02-kij4t1
//        TuSdk.init(context, specialKey, null, md5);//224899b463251e93-02-kij4t1



          md5 = "19685ec22a2ff5e35da37541e77afa84";
           specialKey = "0eaaf1065aafd0c7-04-ewdjn1";

        TuSDKFilter.register();
        org.lasque.tusdkpulse.core.TuSdk.setResourcePackageClazz(R.class);
        TuSdk.init(context, specialKey);
    }

    public static void openMediaAlbum(String intentClassName, int selectMax) {
        Context context = TuSdkContext.context();
        if (context == null) return;
        Intent intent = new Intent(context, MediaAlbumActivity.class);
        intent.putExtra("cutClassName", intentClassName);
        intent.putExtra("selectMax", selectMax);
        // 修复个别机型上使用ApplicationContext启动Activity崩溃问题
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void openMediaAlbum1(String intentClassName, int selectMax) {
        Context context = TuSdkContext.context();
        if (context == null) return;

        Intent intent = new Intent(context, MediaAlbumActivity.class);
        intent.putExtra("cutClassName", intentClassName);
        intent.putExtra("selectMax", selectMax);
        // 修复个别机型上使用ApplicationContext启动Activity崩溃问题
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openPictureAlbum(String intentClassName, int selectMax, int enterType) {
        Context context = TuSdkContext.context();
        if (context == null) return;

        Intent intent = new Intent(context, MediaAlbumActivity.class);
        intent.putExtra("cutClassName", intentClassName);
        intent.putExtra("selectMax", selectMax);
        intent.putExtra("enterType", enterType);
        // 修复个别机型上使用ApplicationContext启动Activity崩溃问题
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
