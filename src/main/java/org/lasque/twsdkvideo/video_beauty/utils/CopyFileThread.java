package org.lasque.twsdkvideo.video_beauty.utils;
 
import android.util.Log;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
 
 
public class CopyFileThread extends Thread {
    private File sourceFile;
    private File targetFile;
 
    public CopyFileThread(String sourceFilePath, String targetFilePath) {
        this.sourceFile = new File(sourceFilePath);
        this.targetFile = new File(targetFilePath);
    }
 
    public void run() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(targetFile);
            byte[] b = new byte[1024];
            int a;
            long len = sourceFile.length();
            double temp = 0;
            DecimalFormat df = new DecimalFormat("##.##%");
            double progress = 0;
            while ((a = fis.read(b)) != -1) {
                fos.write(b, 0, a);
                temp += a;
                if (temp / len - progress > 0.01) {
                    progress = temp / len;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fis != null;
                fis.close();
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
