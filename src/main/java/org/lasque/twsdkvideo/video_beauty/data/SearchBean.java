package org.lasque.twsdkvideo.video_beauty.data;

public class SearchBean {

    String content;
    boolean isHistory;
    public SearchBean(String content, boolean isHistory) {
        this.content = content;
        this.isHistory = isHistory;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }


}
