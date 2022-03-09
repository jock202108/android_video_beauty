package org.lasque.twsdkvideo.video_beauty;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.lasque.twsdkvideo.video_beauty.effectcamera.MovieRecordFullScreenActivity;
import org.lasque.twsdkvideo.video_beauty.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.start).setOnClickListener(v -> {
      if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions())) {
        Intent intent = new Intent(this, MovieRecordFullScreenActivity.class);

        startActivity(intent);
        overridePendingTransition(R.anim.push_bottom_in, 0);

      } else {
        PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
      }

    });

  }

  private String[] getRequiredPermissions() {
    String[] permissions = new String[]{
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.CAMERA,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.READ_PHONE_STATE,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_WIFI_STATE
    };

    return permissions;
  }
}
