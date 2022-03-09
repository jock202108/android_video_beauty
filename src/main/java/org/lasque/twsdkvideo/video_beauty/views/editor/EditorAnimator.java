package org.lasque.twsdkvideo.video_beauty.views.editor;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorHomeComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorTrimTimeComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorVoiceoverComponent;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/26 15:02
 * @Copright (c) 2018 tw. All rights reserved.
 * <p>
 * 视频编辑动画管理类
 */
public class EditorAnimator {
    private static final String TAG = "EditorAnimator";
    private float toX;
    private float fromY;
    private float pivotYValue;

    public interface OnAnimationEndListener {
        void onShowAnimationStartListener();

        void onShowAnimationEndListener();

        void onHideAnimationStartListener();

        void onHideAnimationEndListener();
    }

    //动画持续时间
    private static final int DURATION = 150;
    //播放器的content
    private VideoContent mVideoContent;
    private MovieEditorController mEditorController;
    private EditorComponent.EditorComponentType mComponentEnum;
    private OnAnimationEndListener animationEndListener;
    private boolean horizontalScreen = false;
    //底部组件展示动画
    private ObjectAnimator mBottomShowAnimator = new ObjectAnimator();
    private ObjectAnimator mBottomHideAnimator = new ObjectAnimator();
    private AnimatorSet animator = new AnimatorSet();
//    private int oriVideoContentHeight;
//    private  int  oriVideoContentWidth;
    private int differenceHeight;


    public EditorAnimator(MovieEditorController editorController, VideoContent videoContent, boolean horizontalScreen, boolean isAlbum) {
        this.horizontalScreen = horizontalScreen;
        this.mVideoContent = videoContent;


//        videoContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
////                if(isAlbum){
////                    differenceHeight = 0;
////
////                }else{
////                    differenceHeight = (int) (VideoBeautyPlugin.screenHeight -  mVideoContent.getHeight());
////
////                }
//                differenceHeight = 0;
//
////                oriVideoContentHeight = videoContent.getHeight();
////                oriVideoContentWidth = videoContent.getWidth();
//                videoContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });

        this.mEditorController = editorController;
    }

//    public void setCurrentPreviewSize( TuSdkSize mCurrentPreviewSize){
//      //  differenceHeight = (int) ((VideoBeautyPlugin.screenHeight -  1920)/2);
//        oriVideoContentHeight = mCurrentPreviewSize.height;
//        Log.e("sdfsdfdsfdsfdfdsf","VideoBeautyPlugin.screenHeight="+VideoBeautyPlugin.screenHeight+"。。。。。。oriVideoContentHeight="+oriVideoContentHeight);
//    }

    public void setAnimationEndListener(OnAnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }

    /**
     * 显示Component
     */
    public void showComponent() {
        if (mEditorController == null) {
            TLog.e("%s EditorController is null !!!", TAG);
            return;
        }
        final int bottomHeight = getBottomHeight();
        if (mBottomHideAnimator.isRunning()) mBottomHideAnimator.end();

        if (mEditorController.getCurrentComponent() instanceof EditorHomeComponent || mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Filter || mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Music || mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Sticker) {
            mEditorController.getTitleView().setVisibility(View.GONE);
        } else {
            mEditorController.getTitleView().setVisibility(View.VISIBLE);
        }
        mBottomShowAnimator.setIntValues(bottomHeight, 0);
        mBottomShowAnimator.setDuration(mEditorController.getCurrentComponent().getComponentEnum() != EditorComponent.EditorComponentType.Home ? DURATION : 0);
//        mBottomShowAnimator.setInterpolator(new DecelerateInterpolator());
        mBottomShowAnimator.addListener(mShowAnimatorListener);
        mBottomShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (int) animation.getAnimatedValue();
                mEditorController.getBottomView().setTranslationY(value);
            }
        });
        mBottomShowAnimator.start();

        //滤镜不缩放视频
        if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Filter) {
           // mVideoContent.setHeight(oriVideoContentHeight);
            return;
            //时间裁剪不缩放视频
        }
        if (horizontalScreen && mEditorController.getCurrentComponent().getComponentEnum() != EditorComponent.EditorComponentType.Text) {
        //    mVideoContent.setHeight(oriVideoContentHeight);
            return;
        } else if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Music) {
           // mVideoContent.setHeight(oriVideoContentHeight);
            return;
        } else if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Sticker) {
          //  mVideoContent.setHeight(oriVideoContentHeight);
            return;
        }
        if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Home ) {
            return;
        }
        if (mEditorController.getCurrentComponent().getComponentEnum() != EditorComponent.EditorComponentType.Home) {
            Log.e("sdfsdfsfsfsfsffdssd","........"+bottomHeight);
            float totalSpacing = (VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(44)) + bottomHeight;

            float width = TuSdkContext.getScreenSize().width;
            float height = TuSdkContext.getScreenSize().height-VideoBeautyPlugin.navigationBarHeight;

            fromY = 1- totalSpacing/height;
            pivotYValue = (VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(44))/totalSpacing;


            toX = ((height-totalSpacing)*width/height)/width;
            /** 设置缩放动画 */
            ObjectAnimator scaleXObjectAnimator      =  ObjectAnimator.ofFloat(mVideoContent,"scaleX",1f,toX);
            mVideoContent.setPivotX(width/2);
            scaleXObjectAnimator.setDuration(DURATION);
            ObjectAnimator scaleYObjectAnimator      =  ObjectAnimator.ofFloat(mVideoContent,"scaleY",1f,fromY);
            mVideoContent.setPivotY(height*pivotYValue);
            scaleYObjectAnimator.setDuration(DURATION);
            /**
             * 设置播放按钮
             */
            if(!horizontalScreen){
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mEditorController.getPlayBtn().getLayoutParams();
                layoutParams.setMargins(0,(int)((mVideoContent.getHeight()*fromY)/2+(VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(44))-(mEditorController.getPlayBtn().getHeight()/2)-TuSdkContext.dip2px(22)),0,0);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                mEditorController.getPlayBtn().setLayoutParams(layoutParams);
            }
            animator.playTogether(scaleXObjectAnimator,scaleYObjectAnimator);
            animator.start();



        }
    }


    /**
     * 隐藏Component
     */
    public void hideComponent() {
        //滤镜不缩放视频
        if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Filter) {
           // mVideoContent.setHeight(oriVideoContentHeight);
            mEditorController.switchComponent(mComponentEnum);
            if (animationEndListener != null) animationEndListener.onHideAnimationEndListener();
            showComponent();
            return;
            //时间裁剪不缩放视频
        } else if (horizontalScreen && mEditorController.getCurrentComponent().getComponentEnum() != EditorComponent.EditorComponentType.Text) {
          //  mVideoContent.setHeight(oriVideoContentHeight);
            mEditorController.switchComponent(mComponentEnum);
            if (animationEndListener != null) animationEndListener.onHideAnimationEndListener();
            showComponent();
            return;
        } else if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Music) {
          //  mVideoContent.setHeight(oriVideoContentHeight);
            mEditorController.switchComponent(mComponentEnum);
            if (animationEndListener != null) animationEndListener.onHideAnimationEndListener();
            showComponent();
            return;
        } else if (mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Sticker) {
          //  mVideoContent.setHeight(oriVideoContentHeight);
            mEditorController.switchComponent(mComponentEnum);
            if (animationEndListener != null) animationEndListener.onHideAnimationEndListener();
            showComponent();
            return;
        }
        final int bottomHeight = getBottomHeight();
        if (mBottomShowAnimator.isRunning()) mBottomShowAnimator.end();

        mBottomHideAnimator.setIntValues(0, bottomHeight);
        mBottomHideAnimator.setDuration(mEditorController.getCurrentComponent().getComponentEnum() != EditorComponent.EditorComponentType.Home ? DURATION : 0);
        mBottomHideAnimator.addListener(mHideAnimatorListener);
        mBottomHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mEditorController.getBottomView().setTranslationY(value);
            }
        });
        mBottomHideAnimator.start();
        if(mEditorController.getCurrentComponent().getComponentEnum() == EditorComponent.EditorComponentType.Home){
            return;
        }
        ObjectAnimator scaleXObjectAnimator      =  ObjectAnimator.ofFloat(mVideoContent,"scaleX",toX,1f);
        float width = TuSdkContext.getScreenSize().width;
        float height = TuSdkContext.getScreenSize().height;


        mVideoContent.setPivotX(width/2);
        scaleXObjectAnimator.setDuration(DURATION);
        ObjectAnimator scaleYObjectAnimator      =  ObjectAnimator.ofFloat(mVideoContent,"scaleY",fromY,1f);
        mVideoContent.setPivotY(height*pivotYValue);
        scaleYObjectAnimator.setDuration(DURATION);
        animator.playTogether(scaleXObjectAnimator,scaleYObjectAnimator);
        animator.start();

       if(!horizontalScreen){
           FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mEditorController.getPlayBtn().getLayoutParams();
           layoutParams.setMargins(0,(int)((mVideoContent.getHeight()*fromY)/2+(VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(44))-(mEditorController.getPlayBtn().getHeight()/2)),0,mVideoContent.getBottom());
           layoutParams.gravity = Gravity.CENTER;
           mEditorController.getPlayBtn().setLayoutParams(layoutParams);
       }


    }


    private int getBottomHeight() {
        if (mEditorController == null || mEditorController.getCurrentComponent() == null || mEditorController.getCurrentComponent().getBottomView() == null)
            return 0;
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mEditorController.getCurrentComponent().getBottomView().measure(w, h);


        return mEditorController.getCurrentComponent().getBottomView().getMeasuredHeight()-differenceHeight;
    }

    //动画切换
    public void animatorSwitchComponent(EditorComponent.EditorComponentType componentEnum) {
        mComponentEnum = componentEnum;
        hideComponent();
    }

    //进入动画监听
    private Animator.AnimatorListener mShowAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (animationEndListener != null) animationEndListener.onShowAnimationStartListener();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBottomShowAnimator.removeAllListeners();
            if (animationEndListener != null) animationEndListener.onShowAnimationEndListener();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };


    //隐藏动画监听
    private Animator.AnimatorListener mHideAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            if (animationEndListener != null) animationEndListener.onHideAnimationStartListener();
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mBottomHideAnimator.removeAllListeners();
            mEditorController.switchComponent(mComponentEnum);
            if (animationEndListener != null) animationEndListener.onHideAnimationEndListener();
            showComponent();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

}
