package org.lasque.twsdkvideo.video_beauty.data;

public class TextStyleBean {
    String styleName;
    boolean isSelect;

    public TextStyleBean(String styleName, boolean isSelect) {
        this.styleName = styleName;
        this.isSelect = isSelect;
    }

    public TextStyleBean() {
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
