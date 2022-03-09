package org.lasque.twsdkvideo.video_beauty.event;

import org.lasque.tusdk.core.utils.sqllite.ImageSqlInfo;

public class StickerEvent {
    ImageSqlInfo imageSqlInfo;
    public ImageSqlInfo getImageSqlInfo() {
        return imageSqlInfo;
    }

    public void setImageSqlInfo(ImageSqlInfo imageSqlInfo) {
        this.imageSqlInfo = imageSqlInfo;
    }
}
