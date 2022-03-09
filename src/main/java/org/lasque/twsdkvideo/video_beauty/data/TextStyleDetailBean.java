package org.lasque.twsdkvideo.video_beauty.data;

public class TextStyleDetailBean {
    // 样式种类
    String parentStyleKind;
    // 样式名称
    String childStyleName;
    // 为选中的图片id
    int unSelectImageId;
    // 选中的图片id
    int selectImageId;
    // 是否选中
    boolean isSelect;
    // 颜色名称
    int color;
    //字体样式名称
    String fontName;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getParentStyleKind() {
        return parentStyleKind;
    }

    public void setParentStyleKind(String parentStyleKind) {
        this.parentStyleKind = parentStyleKind;
    }

    public String getChildStyleName() {
        return childStyleName;
    }

    public void setChildStyleName(String childStyleName) {
        this.childStyleName = childStyleName;
    }

    public int getUnSelectImageId() {
        return unSelectImageId;
    }

    public void setUnSelectImageId(int unSelectImageId) {
        this.unSelectImageId = unSelectImageId;
    }

    public int getSelectImageId() {
        return selectImageId;
    }

    public void setSelectImageId(int selectImageId) {
        this.selectImageId = selectImageId;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }




    public TextStyleDetailBean() {
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
