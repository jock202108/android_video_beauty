package org.lasque.twsdkvideo.video_beauty.data;

import org.lasque.tusdk.api.audio.preproc.mixer.TuSDKAudioRenderEntry;

public class BackgroundMusicSelected {
   private   TuSDKAudioRenderEntry curAudioRenderEntry;
   private   String musicId;


   public BackgroundMusicSelected(TuSDKAudioRenderEntry curAudioRenderEntry, String musicId) {
      this.curAudioRenderEntry = curAudioRenderEntry;
      this.musicId = musicId;
   }

   public TuSDKAudioRenderEntry getCurAudioRenderEntry() {
      return curAudioRenderEntry;
   }

   public String getMusicId() {
      return musicId;
   }
}
