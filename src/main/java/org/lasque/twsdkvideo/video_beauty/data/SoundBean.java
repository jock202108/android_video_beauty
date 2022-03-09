package org.lasque.twsdkvideo.video_beauty.data;

import org.lasque.twsdkvideo.video_beauty.utils.TimeUtils;

public class SoundBean {
    String soundsId;
    String soundPic;
    String soundTitle;
    String soundContent;
    long duration;
    String localPath;
    String soundUrl;
    boolean isCollect;
    boolean isPlaying;
    boolean isLoading;
    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }



    public SoundBean() {
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setSoundsId(String soundsId) {
        this.soundsId = soundsId;
    }

    public void setSoundPic(String soundPic) {
        this.soundPic = soundPic;
    }

    public void setSoundTitle(String soundTitle) {
        this.soundTitle = soundTitle;
    }

    public void setSoundContent(String soundContent) {
        this.soundContent = soundContent;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getSoundsId() {
        return soundsId;
    }

    public String getSoundPic() {
        return soundPic;
    }

    public String getSoundTitle() {
        return soundTitle;
    }

    public String getSoundContent() {
        return soundContent;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationFormatStr() {
        return TimeUtils.duration(duration);
    }

    public boolean isCollect() {
        return isCollect;
    }

    public boolean isPlaying() {
        return isPlaying;
    }


    public SoundBean(String soundsId, String soundUrl, String soundPic, String soundTitle, String soundContent, long duration, boolean isCollect, boolean isPlaying,boolean isLoading) {
        this.soundsId = soundsId;
        this.soundPic = soundPic;
        this.soundTitle = soundTitle;
        this.soundContent = soundContent;
        this.duration = duration;
        this.isCollect = isCollect;
        this.isPlaying = isPlaying;
        this.soundUrl = soundUrl;
        this.isLoading = isLoading;
    }


}
