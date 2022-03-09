package org.lasque.twsdkvideo.video_beauty.effectcamera.base

import androidx.recyclerview.widget.RecyclerView


interface OnItemClickListener<I,H : RecyclerView.ViewHolder> {
    fun onItemClick(pos: Int, holder: H, item: I)
}

interface OnItemDeleteClickListener<I,H : RecyclerView.ViewHolder>{
    fun onItemDelete(pos: Int, holder: H, item: I)
}