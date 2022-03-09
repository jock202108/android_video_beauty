package org.lasque.twsdkvideo.video_beauty.event;

public class CollectEvent {
    public final String content;
    public final  boolean isCollect;

    public static CollectEvent getInstance(String content,boolean isCollect) {
        return new CollectEvent(content,isCollect);
    }

    public CollectEvent(String content, boolean isCollect) {
        this.content = content;
        this.isCollect = isCollect;
    }
}
