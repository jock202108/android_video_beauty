package org.lasque.twsdkvideo.video_beauty.event;

/// 模糊音搜索
public class DimEvent {
    public final String content;

    public static DimEvent getInstance(String content) {
        return new DimEvent(content);
    }

    public DimEvent(String content) {
        this.content = content;
    }
}
