package org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.eyeshadow;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.lasque.tusdkpulse.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.CosmeticPanelController;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.CosmeticTypes;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.OnItemClickListener;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.BasePanel;

import static org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.CosmeticTypes.Types.Eyeshadow;


public class EyeshadowPanel extends BasePanel {

    private CosmeticTypes.EyeshadowType mCurrentType;
    private EyeshadowAdapter mAdapter;

    public EyeshadowPanel(CosmeticPanelController controller) {
        super(controller, Eyeshadow);
    }

    @Override
    protected View createView() {
        View panel = LayoutInflater.from(mController.getContext()).inflate(R.layout.cosmetic_eyeshadow_panel,null,false);
        ImageView putAway = panel.findViewById(R.id.lsq_eyeshadow_put_away);
        putAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPanelClickListener != null) onPanelClickListener.onClose(mType);
            }
        });

        ImageView clear = panel.findViewById(R.id.lsq_eyeshadow_null);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        mAdapter = new EyeshadowAdapter(CosmeticPanelController.mEyeshadowTypes,mController.getContext());
        mAdapter.setOnItemClickListener(new OnItemClickListener<CosmeticTypes.EyeshadowType, EyeshadowAdapter.EyeshadowViewHolder>() {
            @Override
            public void onItemClick(int pos, EyeshadowAdapter.EyeshadowViewHolder holder, CosmeticTypes.EyeshadowType item) {
                mCurrentType = item;
                mController.getBeautyManager().setEyeshadowEnable(true);
                mController.getBeautyManager().setEyeshadowStickerId(StickerLocalPackage.shared().getStickerGroup(item.mGroupId).stickers.get(0).stickerId);
                mController.getBeautyManager().setEyeshadowOpacity(mController.getEffect().getFilterArg("eyeshadowAlpha").getPrecentValue());
                mAdapter.setCurrentPos(pos);
                if (onPanelClickListener != null) onPanelClickListener.onClick(mType);

            }
        });
        RecyclerView itemList = panel.findViewById(R.id.lsq_eyeshadow_item_list);
        LinearLayoutManager manager = new LinearLayoutManager(mController.getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemList.setLayoutManager(manager);
        itemList.setAdapter(mAdapter);
        itemList.setNestedScrollingEnabled(false);
        return panel;
    }

    @Override
    public void clear() {
        mCurrentType = null;
        mController.getBeautyManager().setEyeshadowEnable(false);
        mAdapter.setCurrentPos(-1);
        if (onPanelClickListener != null) onPanelClickListener.onClear(mType);
    }
}
