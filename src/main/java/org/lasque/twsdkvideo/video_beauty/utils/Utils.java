package org.lasque.twsdkvideo.video_beauty.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorSaverImpl;
import org.lasque.tusdk.core.sticker.StickerPositionInfo;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.impl.components.widget.sticker.StickerDynamicItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerFactory;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaLiveStickerEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerImageEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaTextEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.data.CustomAudioRenderEntry;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

public class Utils {
    public static List<Long> imageStickerIds = new ArrayList<>();
    private static int imageStickerStartId = 100;


    public static void addImageSticker(Bitmap bitmap, TuSdkEditorPlayer editorPlayer, StickerView stickerView, Context context) {
        StickerImageData imageData = new StickerImageData();
        imageData.setImage(bitmap);
        imageData.height = TuSdkContext.px2dip((float) VideoBeautyPlugin.screenWidth / 2);
        imageData.width = TuSdkContext.px2dip((float) VideoBeautyPlugin.screenWidth / 2);
        //设置贴纸时长
        imageData.starTimeUs = 0;
        imageData.stopTimeUs = editorPlayer.getInputTotalTimeUs();
        imageData.stickerId = imageStickerStartId + imageStickerIds.size();
        imageStickerIds.add(imageData.stickerId);
        //时间间隔为2s
//                    imageData.starTimeUs = 0;
//                    if (imageData.starTimeUs + defaultDurationUs > mEditorController.getMovieEditor().getEditorPlayer().getInputTotalTimeUs()) {
//                        imageData.stopTimeUs = mEditorController.getMovieEditor().getEditorPlayer().getOutputTotalTimeUS();
//                    } else {
//                        imageData.stopTimeUs = imageData.starTimeUs + defaultDurationUs;
//                    }
        //  stickerView.appendSticker(imageData);
//        try {
////           addGifSticker(stickerView,editorPlayer.getInputTotalTimeUs(),context);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void addGifSticker(StickerView stickerView, long stopTimeUs, MovieEditorController.GifValue gifValue, Context context) throws IOException {
        StickerData stickerData = new StickerData();
        stickerData.stickerType = 3;
//        ArrayList<String> pngPaths = new ArrayList<>();
//        GifDrawable gifDrawable = new GifDrawable("/storage/emulated/0/DCIM/Camera/可爱的小狗_13_爱给网_aigei_com.gif");//代表android中assert的gif文件名
//        int totalCount = gifDrawable.getNumberOfFrames();
//        for(int i=0;i<totalCount;i++){
//            pngPaths.add(saveBitmap(gifDrawable.seekToFrameAndGet(i),context));
//        }
        StickerPositionInfo stickerPositionInfo = new StickerPositionInfo();
        stickerPositionInfo.resourceList = gifValue.pngFilePathsFromGifUrl;
        stickerPositionInfo.stickerWidth = TuSdkContext.px2dip((float) VideoBeautyPlugin.screenWidth / 2);
        stickerPositionInfo.stickerHeight = TuSdkContext.px2dip((float) VideoBeautyPlugin.screenWidth / 2);
        stickerPositionInfo.loopMode = 1;
        stickerPositionInfo.renderType = 1;
        stickerPositionInfo.frameInterval = gifValue.duration;
        stickerPositionInfo.loopStartIndex = 0;
        stickerData.positionInfo = stickerPositionInfo;
        StickerDynamicData stickerDynamicData = new StickerDynamicData(stickerData);
        stickerDynamicData.starTimeUs = 0;
        stickerDynamicData.stopTimeUs = stopTimeUs;
        stickerView.appendSticker(stickerDynamicData);


    }

    public static String saveBitmap(Bitmap bm, Context context) {
        File sdDir = Environment.getExternalStorageDirectory();
        //   File sdDir = context.getFilesDir();
        String tmpFile = sdDir.toString() + "/DCIM/" + System.currentTimeMillis() + ".png";
        File f = new File(tmpFile);
        if (f.exists())
            return null;
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return f.getPath();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    protected static TuSdkMediaStickerImageEffectData createTileEffectData(Bitmap bitmap, float stickerWidth, float stickerHeight, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs, float ratio) {
        TuSdkMediaStickerImageEffectData mediaStickerImageEffectData = new TuSdkMediaStickerImageEffectData(bitmap, stickerWidth, stickerHeight, offsetX, offsetY, rotation, ratio);
        mediaStickerImageEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
        return mediaStickerImageEffectData;
    }


    //1.循环list中所有的元素然后删除
    public static ArrayList removeDuplicate(ArrayList list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(i).equals(list.get(j)))
                    list.remove(j);
            }
        }

        return list;
    }

    public static ArrayList removeEmpty(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (TextUtils.isEmpty(list.get(i))) {
                list.remove(i);
                break;
            }
        }
        return list;
    }


    /**
     * @param bitmap        图片
     * @param stickerWidth  归一化后 图片的显示宽度
     * @param stickerHeight 归一化后 图片的显示高度
     * @param offsetX       归一化后 x轴相对左上角偏移量
     * @param offsetY       归一化后 y轴相对左上角偏移量
     * @param rotation      旋转的角度
     * @param startTimeUs   特效开始的时间
     * @param stopTimeUs    特效结束的时间
     * @return
     */
    protected static TuSdkMediaTextEffectData createTextMediaEffectData(Bitmap bitmap, float stickerWidth, float stickerHeight, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs, float ratio) {
        TuSdkMediaTextEffectData mediaTextEffectData = new TuSdkMediaTextEffectData(bitmap, stickerWidth, stickerHeight, offsetX, offsetY, rotation, ratio);
        mediaTextEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
        return mediaTextEffectData;
    }

    public static void stickerHandleCompleted(MovieEditorActivity activity, TuSdkSize outPutSize, TuSdkSize currentPreviewSize, TuSdkSize renderSize, MovieEditorController movieEditorController) {
        // 贴图集合类
        ArrayList<TuSdkMediaEffectData> datas = new ArrayList<TuSdkMediaEffectData>();
        // 屏幕的宽高
        float renderWidth = renderSize.width;
        float renderHeight = renderSize.height;
        // 视频原始的宽高
        float outPutWidth = outPutSize.width;
        float outPutHeight = outPutSize.height;
        // 预览的宽高
        float previewWidth = currentPreviewSize.width;
        float previewHeight = currentPreviewSize.height;
//        Log.e("hh","renderWidth-----"+renderWidth);
//        Log.e("hh","renderHeight-----"+renderHeight);
//        Log.e("hh","outPutWidth-----"+outPutWidth);
//        Log.e("hh","outPutHeight-----"+outPutHeight);
//        Log.e("hh","previewWidth-----"+previewWidth);
//        Log.e("hh","previewHeight-----"+previewHeight);
        //  movieEditorController.getActivity().getTextStickerView().setVisibility(View.INVISIBLE);
        movieEditorController.getActivity().getTextStickerView().setItemViewEnable(false);
        for (StickerItemViewInterface stickerItem : activity.getImageStickerView().getStickerItems()) {
            if (stickerItem instanceof StickerImageItemView) {

                StickerImageItemView stickerItemView = ((StickerImageItemView) stickerItem);
                //生成图片前重置一些视图
                stickerItemView.resetRotation();
                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_transparent), 0);
                TuSdkSize sclaSize = stickerItemView.getRenderScaledSize();
                //生成相应的图片
                Bitmap textBitmap = stickerItemView.getStickerData().getImage();
                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_white), 2);
                StickerView stickerView = activity.getImageStickerView();
                int[] parentLocaiont = new int[2];
                stickerView.getLocationInWindow(parentLocaiont);
                //获取计算相应的位置
                int[] locaiont = new int[2];
                /** 当SDKVersion >= 27 需要使用 getLocationInWindow() 方法 不然会产生极大的误差 小于27时 getLocationInWindow() 与 getLocationOnScreen()方法返回值相同*/
                stickerItemView.getImageView().getLocationInWindow(locaiont);
                int pointX = locaiont[0] - parentLocaiont[0];
                int pointY = (int) (locaiont[1] - parentLocaiont[1]);
                /** 归一化计算 */
                float offsetX = pointX / renderWidth;
                float offsetY = pointY / renderHeight;
                float stickerWidth = (float) sclaSize.width / renderWidth;
                float stickerHeight = (float) sclaSize.height / renderHeight;
                float degree = stickerItemView.getResult(null).degree;
                float ratio = sclaSize.maxMinRatio();
                //设置初始化的时间
                long starTimeUs = ((StickerImageData) stickerItemView.getSticker()).starTimeUs;
                long stopTimeUs = ((StickerImageData) stickerItemView.getSticker()).stopTimeUs;
                //创建特效对象并且应用
                TuSdkMediaStickerImageEffectData stickerImageEffectData = createTileEffectData(textBitmap, stickerWidth, stickerHeight, offsetX, offsetY, degree, starTimeUs, stopTimeUs, ratio);
                // movieEditorController.getMovieEditor().getEditorEffector().addMediaEffectData(stickerImageEffectData);
                datas.add(stickerImageEffectData);
//                EditorStickerImageBackups.StickerImageBackupEntity backupEntity = mStickerImageBackups.findTextBackupEntityByMemo(stickerItemView);
//                if (backupEntity != null)
//                    backupEntity.stickerImageMediaEffectData = stickerImageEffectData;

                // stickerItemView.setVisibility(GONE);
            } else if (stickerItem instanceof StickerTextItemView) {

                StickerTextItemView stickerItemView = ((StickerTextItemView) stickerItem);

                //生成图片前重置一些视图
                stickerItemView.resetRotation();
                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_transparent), 0);

                //生成相应的图片
                Bitmap textBitmap = StickerFactory.createBitmapFromView(stickerItemView.getTextView(), 0);

                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_white), 2);

                StickerView stickerView = activity.getImageStickerView();
                int[] parentLocaiont = new int[2];
                stickerView.getLocationInWindow(parentLocaiont);
                //获取计算相应的位置
                int[] locaiont = new int[2];
                /** 当SDKVersion >= 27 需要使用 getLocationInWindow() 方法 不然会产生极大的误差 小于27时 getLocationInWindow() 与 getLocationOnScreen()方法返回值相同*/
                stickerItemView.getTextView().getLocationInWindow(locaiont);
                int pointX = locaiont[0] - parentLocaiont[0];
                int pointY = (int) (locaiont[1] - parentLocaiont[1]);
//                Log.e("hh","文字x----"+locaiont[0]);
//                Log.e("hh","文字y----"+locaiont[1]);
//                Log.e("hh","文字pointX----"+pointX);
//                Log.e("hh","文字pointY----"+pointY);
//                Log.e("hh","文字parentLocaiontX----"+parentLocaiont[0]);
//                Log.e("hh","文字parentLocaiontY----"+parentLocaiont[1]);
                TuSdkSize canvasSize = TuSdkSize.create(textBitmap);
                /** 归一化计算 */
                float offsetX = pointX / previewWidth;
                float offsetY = pointY / previewHeight;


                float stickerWidth = (float) canvasSize.width / previewWidth;
                float stickerHeight = (float) canvasSize.height / previewHeight;
                float degree = stickerItemView.getResult(null).degree;
                float ratio = (float) canvasSize.width / (float) canvasSize.height;


                long starTimeUs = ((StickerTextData) stickerItemView.getSticker()).starTimeUs;
                long stopTimeUs = ((StickerTextData) stickerItemView.getSticker()).stopTimeUs;
                TuSdkMediaTextEffectData textEffectData = createTextMediaEffectData(textBitmap, stickerWidth, stickerHeight, offsetX, offsetY, degree, starTimeUs, stopTimeUs, ratio);
                // movieEditorController.getMovieEditor().getEditorEffector().addMediaEffectData(textEffectData);
                datas.add(textEffectData);
//                EditorTextBackups.TextBackupEntity backupEntity = mTextBackups.findTextBackupEntity(stickerItemView);
//                if (backupEntity != null)
//                    backupEntity.textMediaEffectData = textEffectData;
                // stickerItemView.setVisibility(GONE);
            } else if (stickerItem instanceof StickerDynamicItemView) {

                StickerDynamicItemView stickerItemView = ((StickerDynamicItemView) stickerItem);
                float scale = stickerItemView.getCurrentScale();
                TuSdkSize scaleSize = stickerItemView.getRenderScaledSize();
                float degree = stickerItemView.getCurrentDegree();
                stickerItemView.resetRotation();
                StickerView stickerView = movieEditorController.getActivity().getImageStickerView();
                int[] locaiont = new int[2];
                /** 当SDKVersion >= 27 需要使用 getLocationInWindow() 方法 不然会产生极大的误差 小于27时 getLocationInWindow() 与 getLocationOnScreen()方法返回值相同*/
                stickerItemView.getRenderView().getLocationInWindow(locaiont);
                int[] parentLocaiont = new int[2];
                stickerView.getLocationInWindow(parentLocaiont);
                float pointX = locaiont[0] - parentLocaiont[0];
                float pointY = (locaiont[1] - parentLocaiont[1]);
//                Log.e("hh","贴纸x----"+locaiont[0]);
//                Log.e("hh","贴纸y----"+locaiont[1]);
//                Log.e("hh","贴纸pointX----"+pointX);
//                Log.e("hh","贴纸pointY----"+pointY);
//                Log.e("hh","贴纸parentLocaiontX----"+parentLocaiont[0]);
//                Log.e("hh","贴纸parentLocaiontY----"+parentLocaiont[1]);
                StickerDynamicData dynamicData = stickerItemView.getCurrentStickerGroup();
                dynamicData.getStickerData().stickerType = 3;
                //进行归一化操作
                StickerPositionInfo info = dynamicData.getStickerData().positionInfo;
                info.offsetX = pointX / (float) previewWidth;
                info.offsetY = pointY / (float) previewHeight;
                info.stickerWidth = scaleSize.width / (float) renderWidth;
                info.stickerHeight = scaleSize.height / (float) renderHeight;
                info.scale = scale;
                info.rotation = degree;
                info.posType = StickerPositionInfo.StickerPositionType.StickerPosDynamic.getValue();
                dynamicData.getStickerData().positionInfo = info;
                long starTimeUs = dynamicData.starTimeUs;
                long stopTimeUs = dynamicData.stopTimeUs;
                TuSdkMediaLiveStickerEffectData effectData = new TuSdkMediaLiveStickerEffectData(dynamicData);
                effectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(starTimeUs, stopTimeUs));
                // movieEditorController.getMovieEditor().getEditorEffector().addMediaEffectData(effectData);
                datas.add(effectData);
//                EditorStickerImageBackups.DynamicStickerBackupEntity backupEntity = mStickerImageBackups.findDynamicStickerBackupEntityByMemo(stickerItemView);
//                if (backupEntity != null)
//                    backupEntity.effectData = effectData;

            }
        }
        // 往保存器里添加特效,不添加在stickerView上
        ((TuSdkEditorSaverImpl) movieEditorController.getMovieEditor().getEditorSaver()).setMediaDataList(datas);

        //清空重置相关数据
        activity.getImageStickerView().cancelAllStickerSelected();
        //   activity.getImageStickerView().removeAllSticker();
    }


    public static String getFileName(String urlname) {
        int start = urlname.lastIndexOf("/");
        int end = urlname.length();
        if (start != -1 && end != -1) {
            return urlname.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /**
     * 横屏可通过 widthPixels - widthPixels2 > 0 来判断底部导航栏是否存在
     *
     * @param windowManager
     * @return true表示有虚拟导航栏 false没有虚拟导航栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isNavigationBarShow(WindowManager windowManager) {
        Display defaultDisplay = windowManager.getDefaultDisplay();
        //获取屏幕高度
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        //宽度
        int widthPixels = outMetrics.widthPixels;


        //获取内容高度
        DisplayMetrics outMetrics2 = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics2);
        int heightPixels2 = outMetrics2.heightPixels;
        //宽度
        int widthPixels2 = outMetrics2.widthPixels;

        return heightPixels - heightPixels2 > 0 || widthPixels - widthPixels2 > 0;
    }


}
