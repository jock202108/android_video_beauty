package org.lasque.twsdkvideo.video_beauty.data;

public class ItemBtnData {
    private int image;
    private int text;


    public ItemBtnData(int image, int text) {
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getText() {
        return text;
    }

    public void setText(int text) {
        this.text = text;
    }
}
