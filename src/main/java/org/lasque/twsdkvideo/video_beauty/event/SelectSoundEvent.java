package org.lasque.twsdkvideo.video_beauty.event;

import org.lasque.twsdkvideo.video_beauty.data.SoundBean;

public class SelectSoundEvent {
    public final SoundBean soundBean;
    //0:拍摄页  1:视频编辑页
    public final  int type;

    public SelectSoundEvent(SoundBean soundBean,int type) {
        this.soundBean = soundBean;
        this.type = type;
    }
}
