package org.lasque.twsdkvideo.video_beauty.data;

import android.graphics.Typeface;

import org.lasque.tusdk.modules.view.widget.sticker.StickerText;

public class AddTextRecordBean {

    /// id
    private long id;
    
    /// 字体位置
    private int fontPosition;

    
    /// 当前输入的文字
    private String content;

    /// 是否处于反向状态
    private boolean isReverse=false;


    /// 当前点击的属性名称
    private  String styleName;
    
    /// 是否斜体
    private boolean isItalic = false;
    /// 是否加粗
    private boolean isBold = false;
    /// 是否有下划线
    private boolean isUnderline =false;

    /// 当前的字体颜色
    private int textColor =-1;
    
    /// 描边宽度
    private int strokeWidth=-1;
    
    /// 描边颜色 
    private int strokeColor=-1;

    /// 背景颜色
    private int backGroundColor=-1234567;

    /// 背景颜色透明度百分比
    private int backGroundColorAlphaProgress =-1;

    /// 最大的描边宽度
    private int mMaxStrokeWidth =10;

    /// 当前的描边progress
    public int mCurrentStrokeProgress =30;

    /// 当前的背景progress
    public int mCurrentBackgroundProgress =100;

    /// 当前的Aligment
    public int alignmentPosition =-1;

    /// 当前的Direction
    public int directionPosition =-1;
    private Typeface typeface;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public int getFontPosition() {
        return fontPosition;
    }

    public void setFontPosition(int fontPosition) {
        this.fontPosition = fontPosition;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAlignmentPosition() {
        return alignmentPosition;
    }

    public void setAlignmentPosition(int position) {
        this.alignmentPosition = position;
    }

    public int getDirectionPosition() {
        return directionPosition;
    }

    public void setDirectionPosition(int position) {
        this.directionPosition = position;
    }


    public int getmMaxStrokeWidth() {
        return mMaxStrokeWidth;
    }

    public void setmMaxStrokeWidth(int mMaxStrokeWidth) {
        this.mMaxStrokeWidth = mMaxStrokeWidth;
    }

    public int getmCurrentStrokeProgress() {
        return mCurrentStrokeProgress;
    }

    public void setmCurrentStrokeProgress(int mCurrentStrokeProgress) {
        this.mCurrentStrokeProgress = mCurrentStrokeProgress;
    }

    public int getmCurrentBackgroundProgress() {
        return mCurrentBackgroundProgress;
    }

    public void setmCurrentBackgroundProgress(int mCurrentBackgroundProgress) {
        this.mCurrentBackgroundProgress = mCurrentBackgroundProgress;
    }

  

    public int getBackGroundColorAlphaProgress() {
        return backGroundColorAlphaProgress;
    }

    public void setBackGroundColorAlphaProgress(int backGroundColorAlphaProgress) {
        this.backGroundColorAlphaProgress = backGroundColorAlphaProgress;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }
    
    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
    

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }
}
