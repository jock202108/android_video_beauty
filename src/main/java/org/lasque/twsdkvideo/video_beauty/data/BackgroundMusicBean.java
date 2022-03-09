package org.lasque.twsdkvideo.video_beauty.data;

import org.lasque.twsdkvideo.video_beauty.utils.TimeUtils;

// 编辑视频背景音乐实体类
public class BackgroundMusicBean {
    String title;
    String localPath;
    String imageUrl;
    String soundUrl;
    boolean isDownLoading =false;
    boolean isFromSearch;
    boolean isSelect = false;
    String musicAuthor;
    long duration;
    String musicId;
    boolean trimFlisShow;
    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

   

    public boolean isDownLoading() {
        return isDownLoading;
    }

    public void setDownLoading(boolean downLoading) {
        isDownLoading = downLoading;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFromSearch() {
        return isFromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        isFromSearch = fromSearch;
    }


    public BackgroundMusicBean() {
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public long getDuration() {
        return duration;
    }
    public String getDurationFormatStr() {
        return TimeUtils.duration(duration);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
