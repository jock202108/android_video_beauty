
package org.lasque.twsdkvideo.video_beauty.effectcamera.base

import android.content.Context
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter<I,H : RecyclerView.ViewHolder>(itemList : MutableList<I>,context : Context) : RecyclerView.Adapter<H>() {

    protected var mItemList : MutableList<I> = itemList
    protected var mContext : Context = context
    protected var mOnItemClickListener : OnItemClickListener<I,H>? = null
    protected var mCurrentPos = -1

    protected abstract fun onChildCreateViewHolder(parent : ViewGroup,viewType : Int) : H
    protected abstract fun onChildBindViewHolder(holder : H,position : Int,item : I)

    override fun getItemCount(): Int {
        return mItemList.size
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        onChildBindViewHolder(holder,position,mItemList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        return onChildCreateViewHolder(parent, viewType)
    }

    open fun getCurrentPosition() : Int{
        return mCurrentPos
    }

    open fun setCurrentPosition(pos : Int){
        val lastPos = mCurrentPos
        notifyItemChanged(lastPos)
        mCurrentPos = pos
        if (mCurrentPos != -1){
            notifyItemChanged(mCurrentPos)
        }
    }

    open fun setOnItemClickListener(onItemClickListener: OnItemClickListener<I, H>) {
        this.mOnItemClickListener = onItemClickListener
    }


}