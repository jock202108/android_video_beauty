
package org.lasque.twsdkvideo.video_beauty.effectcamera.audio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.audio_list_item.view.*
import org.lasque.twsdkvideo.video_beauty.R
import org.lasque.twsdkvideo.video_beauty.effectcamera.base.BaseAdapter


class AudioAdapter(itemList: MutableList<AudioItem>, context: Context) :
    BaseAdapter<AudioItem, AudioAdapter.AudioItemViewHolder>(itemList, context) {

    class AudioItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stateIcon = itemView.lsq_audio_list_state
        val name = itemView.lsq_audio_list_name
        val duration = itemView.lsq_audio_item_duration
        val playOrCommit = itemView.lsq_audio_list_commit
    }

    interface OnAudioItemClickListener{
        fun onItemPlay(item:AudioItem)

        fun onItemSelect(item: AudioItem)
    }

    public var audioItemClickListener : OnAudioItemClickListener? = null

    override fun onChildCreateViewHolder(parent: ViewGroup, viewType: Int): AudioItemViewHolder {
        return AudioItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.audio_list_item,parent,false))
    }

    override fun onChildBindViewHolder(
        holder: AudioItemViewHolder,
        position: Int,
        item: AudioItem
    ) {
        holder.name.setText(item.name)
        holder.duration.setText(item.getDuration())
        holder.itemView.setOnClickListener {
            if (position > 0){
                setCurrentPosition(position)
                audioItemClickListener?.onItemPlay(item)
            }
        }
        holder.playOrCommit.setOnClickListener {
            audioItemClickListener?.onItemSelect(item)
        }
        if (mCurrentPos == position){
            Glide.with(mContext).load(R.drawable.music_ic_record2).into(holder.stateIcon)
        } else {
            Glide.with(mContext).load(R.drawable.music_ic_record1).into(holder.stateIcon)
        }
        holder.playOrCommit.setBackgroundResource(R.drawable.audio_commit_background)
        holder.playOrCommit.setTextColor(mContext.getColor(R.color.lsq_clip_title_color))
        holder.playOrCommit.setText("使用")
    }
}