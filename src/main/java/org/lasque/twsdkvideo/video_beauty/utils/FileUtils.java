package org.lasque.twsdkvideo.video_beauty.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import org.lasque.tusdk.core.common.TuSDKMediaUtils;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {
    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) {
        FileInputStream fis = null;
        try {
            long size = 0;
            if (file.exists()) {

                fis = new FileInputStream(file);
                size = fis.available();
            }
            return size;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean fileIsUsable(Context context, File file) {
       long totalSize =  new SpUtils(context).getLong(file.getAbsolutePath(),0);
        if(totalSize != 0){
            long currentSize = file.length();
            return totalSize == currentSize;
        }
      return  false;

    }

    /**
     * 获取音频文件的总时长大小
     *
     * @param filePath 音频文件路径
     * @return 返回时长大小
     */
    public static long getAudioFileVoiceTime(String filePath) {
        long mediaPlayerDuration = 0L;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
        } finally {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        }

        return mediaPlayerDuration;
    }

}
