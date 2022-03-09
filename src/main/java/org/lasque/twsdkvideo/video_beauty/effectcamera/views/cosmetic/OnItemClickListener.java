package org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic;

import androidx.recyclerview.widget.RecyclerView;


public interface OnItemClickListener<I,H extends RecyclerView.ViewHolder> {
    void onItemClick(int pos, H holder, I item);
}
