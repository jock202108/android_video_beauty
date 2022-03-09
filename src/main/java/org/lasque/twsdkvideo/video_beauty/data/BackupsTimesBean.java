package org.lasque.twsdkvideo.video_beauty.data;

public class BackupsTimesBean {
    final long id;
    final  long starTimeUs;
    final  long stopTimeUs;

    public long getId() {
        return id;
    }

    public long getStarTimeUs() {
        return starTimeUs;
    }

    public long getStopTimeUs() {
        return stopTimeUs;
    }

    public BackupsTimesBean(long id, long starTimeUs, long stopTimeUs) {
        this.id = id;
        this.starTimeUs = starTimeUs;
        this.stopTimeUs = stopTimeUs;
    }
}
