package org.lasque.twsdkvideo.video_beauty.data;



public class GVisionSoundBean {
    String musicId;
    String musicUrl;
    String musicTitle;

    public GVisionSoundBean(String musicId, String musicUrl, String musicTitle, String musicAuthor, int duration, boolean attentionState, String musicCoverUrl) {
        this.musicId = musicId;
        this.musicUrl = musicUrl;
        this.musicTitle = musicTitle;
        this.musicAuthor = musicAuthor;
        this.duration = duration;
        this.attentionState = attentionState;
        this.musicCoverUrl = musicCoverUrl;
    }

    String musicAuthor;
    long duration;
    boolean attentionState;
    String musicCoverUrl;

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
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

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isAttentionState() {
        return attentionState;
    }

    public void setAttentionState(boolean attentionState) {
        this.attentionState = attentionState;
    }

    public String getMusicCoverUrl() {
        return musicCoverUrl;
    }

    public void setMusicCoverUrl(String musicCoverUrl) {
        this.musicCoverUrl = musicCoverUrl;
    }


}
