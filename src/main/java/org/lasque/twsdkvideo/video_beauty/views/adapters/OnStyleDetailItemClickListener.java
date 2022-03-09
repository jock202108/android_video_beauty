package org.lasque.twsdkvideo.video_beauty.views.adapters;


import org.lasque.twsdkvideo.video_beauty.data.TextStyleDetailBean;

import java.util.List;

public interface OnStyleDetailItemClickListener {
    void onItemClick(List<TextStyleDetailBean> datas,int position,TextStyleDetailBean bean);
    
}
