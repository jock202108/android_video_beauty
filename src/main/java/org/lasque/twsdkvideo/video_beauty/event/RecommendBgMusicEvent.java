package org.lasque.twsdkvideo.video_beauty.event;

public class RecommendBgMusicEvent {
    public final String content;

    public static RecommendBgMusicEvent getInstance(String content) {
        return new RecommendBgMusicEvent(content);
    }

    public RecommendBgMusicEvent(String content) {
        this.content = content;
    }
}
