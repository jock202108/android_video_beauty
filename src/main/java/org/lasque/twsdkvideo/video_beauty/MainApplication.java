package org.lasque.twsdkvideo.video_beauty;

import android.app.Application;

import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;

public class MainApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    String md5 = "46a7ce5f73a34eda6488ecab4458960c";
    String specialKey = "a5087fafcd5543f6-02-kij4t1";
    AlbumUtils.init(this, md5, specialKey);

    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      VideoBeautyPlugin.statusBarHeight = getResources().getDimensionPixelSize(resourceId);
    }
  }
}
