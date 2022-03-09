package org.lasque.twsdkvideo.video_beauty.event;

public class MusicListEvent {
    public final String content;

    public static MusicListEvent getInstance(String content) {
        return new MusicListEvent(content);
    }

    public MusicListEvent(String content) {
        this.content = content;
    }
}
