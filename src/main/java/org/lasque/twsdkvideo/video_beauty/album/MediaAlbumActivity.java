/**
 * TuSDK
 * twsdkvideo3
 * MediaAlbumActivity.java
 *
 * @author H.ys
 * @Date 2019/6/3 15:23
 * @Copyright (c) 2019 tw. All rights reserved.
 */

package org.lasque.twsdkvideo.video_beauty.album;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ContextUtils;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.twsdkvideo.video_beauty.MediaAlbumAdapter;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.ScreenAdapterActivity;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.utils.AnimationUtils;
import org.lasque.twsdkvideo.video_beauty.utils.DarkModeUtils;
import org.lasque.twsdkvideo.video_beauty.utils.PermissionUtils;
import org.lasque.twsdkvideo.video_beauty.views.MediaImageInfoIndexRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.MediaInfoIndexRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.TabPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class MediaAlbumActivity extends ScreenAdapterActivity {

    private TabPagerIndicator mMediaTabPagerIndicator;

    private ViewPager mMediaViewPager;

    private MediaAlbumAdapter mMediaAlbumAdapter;

    private MovieAlbumFragment mMovieAlbumFragment;

    private ImageAlbumFragment mImageAlbumFragment;

    private LinearLayout linearLayoutIndexAll;

    private RecyclerView recyclerViewIndexContent;

    int enterType;

    /* 确定按钮 */
    protected TextView mConfirmButton;
    /* 返回按钮 */
    protected TextView mBackButton;

    private View.OnClickListener mConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Fragment currentFragment = mMediaAlbumAdapter.getFragmentList().get(mMediaViewPager.getCurrentItem());
            if (currentFragment instanceof ImageAlbumFragment) {
                mImageAlbumFragment.getNextStepClickListener().onClick(v);
            } else if (currentFragment instanceof MovieAlbumFragment) {
                mMovieAlbumFragment.getNextStepClickListener().onClick(v);
            }
        }
    };

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
        };

        return permissions;
    }

    /**
     * 处理用户的许可结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handleRequestPermissionsResult(requestCode, permissions, grantResults, this, mGrantedResultDelgate);
    }

    protected PermissionUtils.GrantedResultDelgate mGrantedResultDelgate = new PermissionUtils.GrantedResultDelgate() {
        @Override
        public void onPermissionGrantedResult(boolean permissionGranted) {
            if (permissionGranted) {
                setContentView(R.layout.media_all_album_activity);
                initViews();
            } else {
                String msg = TuSdkContext.getString("lsq_album_no_access", ContextUtils.getAppName(MediaAlbumActivity.this));

                TuSdkViewHelper.alert(permissionAlertDelegate, MediaAlbumActivity.this, TuSdkContext.getString("lsq_album_alert_title"),
                        msg, TuSdkContext.getString("lsq_button_close"), TuSdkContext.getString("lsq_button_setting")
                );
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
                    Uri.fromParts("package", MediaAlbumActivity.this.getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        @Override
        public void onAlertCancel(AlertDialog dialog) {
            finish();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (PermissionUtils.hasRequiredPermissions(this, getRequiredPermissions())) {
            setContentView(R.layout.media_all_album_activity);
            initViews();
        } else {
            PermissionUtils.requestRequiredPermissions(this, getRequiredPermissions());
        }
    }

    private void initViews() {
        //目录文件夹
        linearLayoutIndexAll = findViewById(R.id.lsq_all_index_ll);
        RelativeLayout topLayout = findViewById(R.id.lsq_topBar);
        final TextView indexTitleTv = findViewById(R.id.lsq_all_index);
        ImageView indexArrow = findViewById(R.id.lsq_all_arrow);
        View line = findViewById(R.id.lsq_view);
        topLayout.setBackgroundColor(DarkModeUtils.getColor(this,R.color.lsq_color_white,R.color.color_121921));
        indexTitleTv.setTextColor(DarkModeUtils.getColor(this,R.color.color_121921,R.color.lsq_color_white));
        indexArrow.setImageResource(DarkModeUtils.getImageResource(this,R.drawable.ic_all_album_arrow,R.drawable.ic_all_album_arrow_dark));
        line.setBackgroundColor(DarkModeUtils.getColor(this,R.color.color_DFE1EA,R.color.color_4E536F));
       /* mConfirmButton = (TextView) findViewById(R.id.lsq_next);
        mConfirmButton.setText(R.string.lsq_next);
        mConfirmButton.setOnClickListener(mConfirmClickListener);*/

        mBackButton = (TextView) findViewById(R.id.lsq_back);
        mBackButton.setOnClickListener(new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                finish();
            }
        });

        //mMediaTabPagerIndicator = findViewById(R.id.lsq_media_album_tab);


        linearLayoutIndexAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewIndexContent != null && indexArrow != null) {
                    if (recyclerViewIndexContent.getVisibility() != View.VISIBLE) {
                        recyclerViewIndexContent.setVisibility(View.VISIBLE);

                        //AnimationUtils.expand(recyclerViewIndexContent);
                        AnimationUtils.openFolder(recyclerViewIndexContent, TuSdkContext.dip2px(50));

                        RotateAnimation rotateAnimation = AnimationUtils.getRotateAnimation(0, 180f, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f, 500, null);
                        rotateAnimation.setFillAfter(true);

                        indexArrow.startAnimation(rotateAnimation);
                    } else {
                        //recyclerViewIndexContent.setVisibility(View.INVISIBLE);

                        //AnimationUtils.collapse(recyclerViewIndexContent);
                        AnimationUtils.closeFolder(recyclerViewIndexContent, TuSdkContext.dip2px(50));

                        RotateAnimation rotateAnimation = AnimationUtils.getRotateAnimation(180, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f, 500, null);
                        rotateAnimation.setFillAfter(true);
                        indexArrow.startAnimation(rotateAnimation);
                    }
                }
            }
        });



        //目录文件夹内容
        recyclerViewIndexContent = findViewById(R.id.lsq_media_album_index_content);
        recyclerViewIndexContent.setBackgroundColor(DarkModeUtils.getColor(this,R.color.lsq_color_white,R.color.color_121921));
        recyclerViewIndexContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mMediaViewPager = findViewById(R.id.lsq_media_view_pager);
        enterType = getIntent().getIntExtra("enterType", AppConstants.SELECT_VIDEO);
        mMovieAlbumFragment = new MovieAlbumFragment();
        mMovieAlbumFragment.setOnMovieInfoIndexListener(new MovieAlbumFragment.OnMediaInfoIndexListener() {
            @Override
            public void onDataMediaInfoIndex(List<String> listIndex, Hashtable<String, List<MovieInfo>> hashtable) {
                if (listIndex != null && !listIndex.isEmpty())
                    indexTitleTv.setText(listIndex.get(0));

                MediaInfoIndexRecyclerAdapter mediaInfoIndexRecyclerAdapter = new MediaInfoIndexRecyclerAdapter();
                mediaInfoIndexRecyclerAdapter.setData(listIndex, hashtable);
                mediaInfoIndexRecyclerAdapter.setItemClickListener(new MediaInfoIndexRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String indexTitle) {
                        indexTitleTv.setText(indexTitle);
                        mMovieAlbumFragment.setVideoAlbumAdapterData(indexTitle);

                        if (recyclerViewIndexContent != null && indexArrow != null) {
                            if (recyclerViewIndexContent.getVisibility() != View.VISIBLE) {
                                recyclerViewIndexContent.setVisibility(View.VISIBLE);
                                RotateAnimation rotateAnimation = AnimationUtils.getRotateAnimation(0, 180f, Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f, 500, null);
                                rotateAnimation.setFillAfter(true);
                                indexArrow.startAnimation(rotateAnimation);
                            } else {
                                //recyclerViewIndexContent.setVisibility(View.INVISIBLE);
                                //AnimationUtils.collapse(recyclerViewIndexContent);
                                AnimationUtils.closeFolder(recyclerViewIndexContent, TuSdkContext.dip2px(50));
                                
                                RotateAnimation rotateAnimation = AnimationUtils.getRotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f, 500, null);
                                rotateAnimation.setFillAfter(true);
                                indexArrow.startAnimation(rotateAnimation);
                            }
                        }
                    }
                });

                recyclerViewIndexContent.setAdapter(mediaInfoIndexRecyclerAdapter);
                recyclerViewIndexContent.addItemDecoration(new DividerItemDecoration(
                        MediaAlbumActivity.this, DividerItemDecoration.HORIZONTAL));
            }
        });
        mImageAlbumFragment = new ImageAlbumFragment();
        mImageAlbumFragment.setOnImangeInfoIndexListener(new ImageAlbumFragment.OnMediaInfoIndexListener() {
            @Override
            public void onDataMediaInfoIndex(List<String> listIndex, Hashtable<String, List<ImageSqlInfo>> hashtable) {
                indexTitleTv.setText(listIndex.get(0));

                MediaImageInfoIndexRecyclerAdapter mediaImageInfoIndexRecyclerAdapter = new MediaImageInfoIndexRecyclerAdapter(MediaAlbumActivity.this);
                mediaImageInfoIndexRecyclerAdapter.setData(listIndex, hashtable);
                mediaImageInfoIndexRecyclerAdapter.setItemClickListener(new MediaImageInfoIndexRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String indexTitle) {
                        indexTitleTv.setText(indexTitle);
                        mImageAlbumFragment.setVideoAlbumAdapterData(indexTitle);

                        if (recyclerViewIndexContent != null && indexArrow != null) {
                            if (recyclerViewIndexContent.getVisibility() != View.VISIBLE) {
                                recyclerViewIndexContent.setVisibility(View.VISIBLE);
                                RotateAnimation rotateAnimation = AnimationUtils.getRotateAnimation(0, 180f, Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f, 500, null);
                                rotateAnimation.setFillAfter(true);
                                indexArrow.startAnimation(rotateAnimation);
                            } else {
                                //recyclerViewIndexContent.setVisibility(View.INVISIBLE);
                                AnimationUtils.collapse(recyclerViewIndexContent);
                                RotateAnimation rotateAnimation = AnimationUtils.getRotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f, 500, null);
                                rotateAnimation.setFillAfter(true);
                                indexArrow.startAnimation(rotateAnimation);
                            }
                        }
                    }
                });

                recyclerViewIndexContent.setAdapter(mediaImageInfoIndexRecyclerAdapter);
                recyclerViewIndexContent.addItemDecoration(new DividerItemDecoration(
                        MediaAlbumActivity.this, DividerItemDecoration.HORIZONTAL));
            }
        });

        List<Fragment> fragments = new ArrayList<>();
        if (enterType == AppConstants.SELECT_VIDEO) {
            fragments.add(mMovieAlbumFragment);
        } else {
            fragments.add(mImageAlbumFragment);
        }


        mMediaAlbumAdapter = new MediaAlbumAdapter(getSupportFragmentManager(), fragments);
        mMediaViewPager.setAdapter(mMediaAlbumAdapter);
        mMediaViewPager.setOffscreenPageLimit(1);
        /*mMediaTabPagerIndicator.setViewPager(mMediaViewPager, 0);
        mMediaTabPagerIndicator.setTabItems(Arrays.asList(""));*/
    }

    public void setEnable(boolean enable) {
        //mMediaTabPagerIndicator.setEnabled(enable);
        mMediaViewPager.setEnabled(enable);
        mConfirmButton.setEnabled(enable);
        mBackButton.setEnabled(enable);
        mImageAlbumFragment.setIsEnable(enable);
        mMovieAlbumFragment.setIsEnable(enable);

    }
}
