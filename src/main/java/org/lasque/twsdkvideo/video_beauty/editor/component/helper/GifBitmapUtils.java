package org.lasque.twsdkvideo.video_beauty.editor.component.helper;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.os.Environment;
import android.util.Log;

import com.facebook.imageutils.BitmapUtil;
import com.giphy.sdk.ui.GPHContentType;

import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class GifBitmapUtils {

    static Context context;

    private static Bitmap getBitmap(String gifUrl){
        Bitmap bmp;
        try {
            URL url = new URL(gifUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                bmp = BitmapFactory.decodeStream(in);
            } finally {
                urlConnection.disconnect();
            }
        }
        catch(IllegalStateException ex){
            return null;
        }
        catch(IOException ex){
            return null;
        }

        return bmp;
    }

    private static String convertBitmapToFile(StickerView stickerView, GPHContentType gphContentType, Bitmap srcBitmap, String fName, int i) {
        if (srcBitmap == null) return null;

        try {
            String filename = fName;
            File f = new File(context.getCacheDir(), filename);
            f.createNewFile();
            Bitmap bitmap = srcBitmap;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int disPlayWidth = getDisPlayWidth(stickerView, gphContentType);
            int disPlayHeight = (int) ((bitmapHeight / (float) bitmapWidth) * disPlayWidth);

            if (i == 0) {//只改变第一帧的宽高也可以， （一点点优化，目前测试 慢的原因主要还是生成文件，转换bitmap 每帧消耗 3-6 毫秒 ）
                Bitmap disPlayBitmap = BitmapHelper.imageScale(bitmap, disPlayWidth, disPlayHeight);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                disPlayBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                srcBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            }


            if (f != null) return f.getAbsolutePath();
        } catch(IOException ex) {
            return null;
        }

        return null;
    }

    private static int getDisPlayWidth(StickerView stickerView ,GPHContentType gphContentType){
       int stickerViewWidth =  stickerView.getWidth();
        int width = stickerViewWidth/2;
        if(gphContentType == GPHContentType.recents || gphContentType == GPHContentType.gif){
            width = (stickerViewWidth-50)/2;

        }else if(gphContentType == GPHContentType.sticker ){
            width = (stickerViewWidth-250)/3;

        }else if(gphContentType == GPHContentType.emoji){
            width = (stickerViewWidth -350)/5;
        }
        else if(gphContentType == GPHContentType.text){
            width = (stickerViewWidth -300)/2;
        }
        return  width;
    }

    public static MovieEditorController.GifValue getPNGFilePathsFromGifUrl(StickerView stickerView,String gifUrl, Context ctx, GPHContentType gphContentType, DownloadProgress progressListener){
        context = ctx;
        MovieEditorController.GifValue gifValue=new MovieEditorController.GifValue();
        ArrayList<String>  list = new ArrayList<String>();
        try {
            URL url = new URL(gifUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                int contentLength = urlConnection.getContentLength();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                GifDrawable gifDrawable=new GifDrawable(readAllBytes(in,contentLength,progressListener));

                int totalCount = gifDrawable.getNumberOfFrames();
                gifValue.duration= (int) (gifDrawable.getDuration()/(float)totalCount);
                for(int i=0;i<totalCount;i++) {
                    gifDrawable.getFrameDuration(i);
                    gifValue.duration= Math.min(gifValue.duration, gifDrawable.getFrameDuration(i));
                    list.add(convertBitmapToFile(stickerView, gphContentType, gifDrawable.seekToFrameAndGet(i), "gif" + i + System.currentTimeMillis() + ".png", i));
                }


            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifValue.pngFilePathsFromGifUrl=list;
        return gifValue;
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

    public interface  DownloadProgress{
        void onDownloadProgress(float percent);
    }

    private static byte[] readAllBytes(InputStream inputStream,int length,DownloadProgress progressListener) throws IOException {
        final int bufLen = 10 * 0x400; // 10KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;
        int progress=0;
        float progressPercent=0;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
                    outputStream.write(buf, 0, readLen);
                    progress+=readLen;
                    progressPercent=(float) progress/(float)length;
                    progressListener.onDownloadProgress(progressPercent);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

}
