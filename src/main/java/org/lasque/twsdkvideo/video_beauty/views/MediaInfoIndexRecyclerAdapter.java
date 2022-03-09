package org.lasque.twsdkvideo.video_beauty.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.album.MovieInfo;
import org.lasque.twsdkvideo.video_beauty.utils.DarkModeUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MediaInfoIndexRecyclerAdapter extends RecyclerView.Adapter<MediaInfoIndexRecyclerAdapter.MediaInfoIndexViewHolder> {

    private List<String> indexTitles;
    private Hashtable<String, List<MovieInfo>> htImg;
    private Context context;
    private int currentPosition;

    /** 点击监听 **/
    private OnItemClickListener mItemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(String indexTitle);
    }

    public void setData(List<String> indexTitles, Hashtable<String, List<MovieInfo>> htImg){
        this.indexTitles = indexTitles;
        this.htImg = htImg;
    }
    @NonNull
    @Override
    public MediaInfoIndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsq_media_info_index_recycler_item_view,parent, false);
        MediaInfoIndexViewHolder mediaInfoIndexViewHolder = new MediaInfoIndexViewHolder(view);
        return mediaInfoIndexViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaInfoIndexViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(indexTitles != null && indexTitles.size() > 0) {
            String indexTitleName = indexTitles.get(position);
            List<MovieInfo> mediaList = getMediaList(indexTitleName);
            MovieInfo movieInfo = mediaList.get(0);

            if(movieInfo != null) {
                String path;
                if (!TextUtils.isEmpty(path = movieInfo.getPath())) {
                    Glide.with(context).load(path).into(holder.mPreImageview);
                }
            }

            holder.mTitleCountView.setText(mediaList != null ? (mediaList.size() + "") : "");
            holder.mTitleView.setText(indexTitleName);
            holder.mTitleCountView.setTextColor(VideoBeautyPlugin.themeMode == 0? ContextCompat.getColor(context,R.color.color_6E7187): ContextCompat.getColor(context,R.color.color_787a8c));


            holder.mTitleView.setText(indexTitleName);
            holder.mTitleView.setTextColor(VideoBeautyPlugin.themeMode == 0? ContextCompat.getColor(context,R.color.lsq_color_black): ContextCompat.getColor(context,R.color.lsq_color_white));

            holder.line.setBackgroundColor(DarkModeUtils.getColor(context,R.color.color_E8E9EF,R.color.color_373A4E));
            holder.mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(indexTitles.get(position));
                        currentPosition = position;
                        notifyDataSetChanged();
                    }
                }
            });

            if(currentPosition != position) {
                holder.mChooseImageview.setVisibility(View.INVISIBLE);
            }else{
                holder.mChooseImageview.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return indexTitles != null ? indexTitles.size() : 0;
    }

    public List<MovieInfo> getMediaList(String dirName)
    {
        if(!TextUtils.isEmpty(dirName))
        {
            if(context.getResources().getString(R.string.record_local_album_all_index).equals(dirName))
            {
                List<MovieInfo> lt = new ArrayList<>();
                for(Map.Entry<String, List<MovieInfo>> entry : htImg.entrySet())
                {
                    List<MovieInfo> ltTmp = entry.getValue();
                    if(ltTmp != null && ltTmp.size() > 0)
                    {
                        lt.addAll(ltTmp);
                    }
                }
                return lt;
            }
            return htImg.get(dirName);
        }
        return null;
    }

    class MediaInfoIndexViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleView, mTitleCountView;
        public ImageView mPreImageview, mChooseImageview;
        public RelativeLayout mRelativeLayout;
        public  View line;

        public MediaInfoIndexViewHolder(View itemView) {
            super(itemView);
            mRelativeLayout = itemView.findViewById(R.id.lsq_rl_gallery);
            mTitleView = itemView.findViewById(R.id.lsq_item_title);
            mTitleCountView = itemView.findViewById(R.id.lsq_item_count);
            mPreImageview = itemView.findViewById(R.id.iv_preview);
            mChooseImageview = itemView.findViewById(R.id.iv_gallery_choose);
            line = itemView.findViewById(R.id.line);
        }
    }
}
