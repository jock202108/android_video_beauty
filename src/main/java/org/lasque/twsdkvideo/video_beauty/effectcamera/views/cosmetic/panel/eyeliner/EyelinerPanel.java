package org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.eyeliner;

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

public class EyelinerPanel extends BasePanel {

    private CosmeticTypes.EyelinerType mCurrentType;
    private EyelinerAdapter mAdapter;


    public EyelinerPanel(CosmeticPanelController controller) {
        super(controller, CosmeticTypes.Types.Eyeliner);
    }

    @Override
    protected View createView() {
        View panel = LayoutInflater.from(mController.getContext()).inflate(R.layout.cosmetic_eyeliner_panel,null,false);
        final ImageView putAway = panel.findViewById(R.id.lsq_eyeliner_put_away);
        putAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPanelClickListener != null) onPanelClickListener.onClose(mType);
            }
        });

        final ImageView clearLips = panel.findViewById(R.id.lsq_eyeliner_null);
        clearLips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });

        mAdapter = new EyelinerAdapter(CosmeticPanelController.mEyelinerTypes,mController.getContext());
        mAdapter.setOnItemClickListener(new OnItemClickListener<CosmeticTypes.EyelinerType, EyelinerAdapter.EyelinerViewHolder>() {
            @Override
            public void onItemClick(int pos, EyelinerAdapter.EyelinerViewHolder holder, CosmeticTypes.EyelinerType item) {
                mCurrentType = item;
                mController.getBeautyManager().setEyelineEnable(true);
                mController.getBeautyManager().setEyelineStickerId(StickerLocalPackage.shared().getStickerGroup(mCurrentType.mGroupId).stickers.get(0).stickerId);
                mController.getBeautyManager().setEyelineOpacity(mController.getEffect().getFilterArg("eyelineAlpha").getPrecentValue());
                mAdapter.setCurrentPos(pos);
                if (onPanelClickListener != null) onPanelClickListener.onClick(mType);

            }
        });
        RecyclerView itemList = panel.findViewById(R.id.lsq_eyeliner_item_list);
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
        mController.getBeautyManager().setEyelineEnable(false);
        mAdapter.setCurrentPos(-1);
        if (onPanelClickListener != null) onPanelClickListener.onClear(mType);
    }
}
