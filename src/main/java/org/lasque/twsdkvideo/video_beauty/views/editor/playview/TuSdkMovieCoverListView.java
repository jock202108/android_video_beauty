package org.lasque.twsdkvideo.video_beauty.views.editor.playview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.lasque.tusdk.core.utils.TLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 视频封面列表控件
 * @author MirsFang
 */
public class TuSdkMovieCoverListView extends LinearLayout {

    private static final String TAG = "MovieCoverList";
    /** 图片的张数 (默认20张) **/
    private int mImageCount = 10;
    /** 当前图片的数量 **/
    private int mCurrentImageCount = 0;
    /** 每张图片的宽度 **/
    private int mImageWidth;
    /** 每张图片的高度 **/
    private int mImageHeight;
    /** 视图的宽度 **/
    private int mViewWidth;
    private int widthTemp = 0;
    private List<Bitmap> bitmaps = new ArrayList<>();
    List<Bitmap> firstFrameBitmaps = new ArrayList<>();
    public TuSdkMovieCoverListView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public TuSdkMovieCoverListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = Math.max(getWidth(),MeasureSpec.getSize(widthMeasureSpec));
        int viewHeight =MeasureSpec.getSize(heightMeasureSpec);


        //计算图片显示的宽高
        mViewWidth = viewWidth - getPaddingLeft() - getPaddingRight();
        if(mViewWidth > 0)widthTemp = mViewWidth;
        mImageWidth = mViewWidth/mImageCount ;
        mImageHeight = viewHeight;

        for (int i = 0; i < getChildCount(); i++) {
            ImageView imageView = (ImageView) getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
            if(i == getChildCount() - 1){
                int diffWidth = mViewWidth - mImageCount * mImageWidth;
                if( diffWidth > 0){
                    layoutParams.width = mImageWidth + diffWidth;
                }else {
                    layoutParams.width = mImageWidth;
                }
            }else {
              layoutParams.width = mImageWidth;
            }
            layoutParams.height = mImageHeight;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    /** 设置图片数量  **/
    public void setBitmapCount(int imageCount){
        this.mImageCount = imageCount;
    }

    /** 添加图片 **/
    public synchronized void addBitmap(Bitmap bitmap){
        if(mCurrentImageCount >= mImageCount )return;
        if(bitmap.isRecycled()){
            TLog.w("%s bitmap %s isRecycled",TAG,mCurrentImageCount);
            return;
        }
        bitmaps.add(bitmap);
        CoverImageView imageView = (CoverImageView) getChildAt(mCurrentImageCount);
       if(imageView == null){
           CoverImageView  imageView1 = new CoverImageView(getContext());
           imageView1.setImageBitmap(bitmap);
           imageView1.setTag(mCurrentImageCount);
           mCurrentImageCount++;
           imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
           this.addView(imageView1,mImageWidth,mImageHeight);
       }else {
           imageView.setImageBitmap(bitmap);
           imageView.setTag(mCurrentImageCount);
           mCurrentImageCount++;
           imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
       }
    if(firstFrameBitmaps != null && !firstFrameBitmaps.isEmpty()){
        for (int i = 0; i < firstFrameBitmaps.size(); i++) {
            firstFrameBitmaps.get(i).recycle();
        }
        firstFrameBitmaps.clear();
    }


    }

    /**
     * 添加首帧封面图
     * @param bitmap
     */
    public synchronized void addFirstFrameBitmap(Bitmap bitmap){
       firstFrameBitmaps.clear();
        for (int i = 0; i < 10; i++) {
            firstFrameBitmaps.add(bitmap);
        }
        for (int i = 0; i < firstFrameBitmaps.size(); i++) {
            Bitmap b = firstFrameBitmaps.get(i);
            if(b.isRecycled()){
                TLog.w("%s bitmap %s isRecycled",TAG,mCurrentImageCount);
                return;
            }
            CoverImageView imageView = new CoverImageView(getContext());
            imageView.setImageBitmap(bitmap);
            imageView.setTag(mCurrentImageCount);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            this.addView(imageView,mImageWidth,mImageHeight);
        }
//        for (int i = 0; i < firstFrameBitmaps.size(); i++) {
//            firstFrameBitmaps.get(i).recycle();
//        }
    }

    /**
     * 获取图片的总长度
     * @return
     */
    public int getTotalWidth(){
        int temp = widthTemp;
        return temp;
    }

    public void release(){
        removeAllViews();
    }

    private class CoverImageView extends androidx.appcompat.widget.AppCompatImageView{
        private boolean isDrawed  = false;

        public CoverImageView(Context context) {
            super(context);
        }

        public CoverImageView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public CoverImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            try {
                super.onDraw(canvas);
            }catch (Exception e){
                TLog.w("CoverImageView is error : %s",getTag());
                setImageBitmap(bitmaps.get((Integer) getTag()));
            }

        }
    }

}

