package org.lasque.twsdkvideo.video_beauty.data;

import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioRenderEntry;

public class CustomAudioRenderEntry {
    private TuSDKAudioRenderEntry tuSDKAudioRenderEntry;
    private String color;
    private boolean isReversing;


    public TuSDKAudioRenderEntry getTuSDKAudioRenderEntry() {
        return tuSDKAudioRenderEntry;
    }

    public void setTuSDKAudioRenderEntry(TuSDKAudioRenderEntry tuSDKAudioRenderEntry) {
        this.tuSDKAudioRenderEntry = tuSDKAudioRenderEntry;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public boolean isReversing() {
        return isReversing;
    }

    public void setReversing(boolean reversing) {
        isReversing = reversing;
    }
}
