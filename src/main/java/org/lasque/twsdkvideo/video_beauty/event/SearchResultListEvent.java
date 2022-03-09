package org.lasque.twsdkvideo.video_beauty.event;

public class SearchResultListEvent {
    public final String content;

    public static SearchResultListEvent getInstance(String content) {
        return new SearchResultListEvent(content);
    }

    public SearchResultListEvent(String content) {
        this.content = content;
    }
}
