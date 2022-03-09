package org.lasque.twsdkvideo.video_beauty.data;

import android.net.Uri;

import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;

import java.util.ArrayList;
import java.util.List;

public class ResultBean {
    String  videoPath;//视频路径
    ArrayList<String> stickerIds;
    String musicId;
    List<String> filterCodes = new ArrayList<String>();//滤镜
    List<String> musicPaths = new ArrayList<String>();//背景音乐路径
    List<String> effectCodes = new ArrayList<String>();//特效

    public ResultBean( String videoPath) {
        this.videoPath = videoPath;
    }

    public ResultBean(String videoPath, List<String> filterCodes) {
        this.videoPath = videoPath;
        this.filterCodes = filterCodes;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }


    public List<String> getFilterCodes() {
        return filterCodes;
    }

    public void setFilterCodes(List<String> filterCodes) {
        this.filterCodes = filterCodes;
    }

    public List<String> getMusicPaths() {
        return musicPaths;
    }

    public void setMusicPaths(List<String> musicPaths) {
        this.musicPaths = musicPaths;
    }

    public List<String> getEffectCodes() {
        return effectCodes;
    }

    public void setEffectCodes(List<String> effectCodes) {
        this.effectCodes = effectCodes;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }


    public ArrayList<String> getStickerIds() {
        return stickerIds;
    }

    public void setStickerIds(ArrayList<String> stickerIds) {
        this.stickerIds = stickerIds;
    }
}
