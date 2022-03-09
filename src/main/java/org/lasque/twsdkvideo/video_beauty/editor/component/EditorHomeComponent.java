package org.lasque.twsdkvideo.video_beauty.editor.component;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import org.greenrobot.eventbus.EventBus;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkImageView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.ItemBtnData;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.event.BackEvent;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.SpUtils;
import org.lasque.twsdkvideo.video_beauty.utils.TextWidthUtils;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;
import org.lasque.twsdkvideo.video_beauty.views.MusicRecyclerAdapter;
import org.quanqi.circularprogress.CircularProgressView;

import java.util.ArrayList;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 14:14
 * @Copright (c) 2018 tw. All rights reserved.
 * <p>
 * 编辑 主页组件
 */
public class EditorHomeComponent extends EditorComponent {
    private static final String TAG = "HomeComponent";
    /**
     * 头部视图
     **/
    private View mHeaderView;
    /**
     * 底部视图
     **/
    private View mBottomView;

    /**
     * 是否启用控件
     **/
    private boolean isEnable = true;

    public interface OnItemClickListener {
        void onClick(TabType tabType);
    }


    public enum TabType {
        FilterTab,
        MVTab,
        MusicTab,
        TextTab,
        EffectTab,
        Sticker,
        Trim,
        TrimTime,
        TrimMusic,
        TransitionsEffect,
        DynamicStickers,
        Voiceover
    }

    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorHomeComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Home;
    }

    @Override
    public void attach() {
        getEditorController().getHeaderView().addView(getHeaderView());
        getEditorController().getBottomView().addView(getBottomView());
        if (getMovieEditor().getEditorPlayer().isPause()) {
            getMovieEditor().getEditorPlayer().startPreview();
        }
    }

    @Override
    public void detach() {

    }


    @Override
    public View getHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = initHeadView();
        }
        return mHeaderView;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {

    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {

    }


    public void showHeaderView(){
        mHeaderView.setVisibility(View.VISIBLE);
    }
    public void hideHeaderView(){
        mHeaderView.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置是否启用控件
     *
     * @param isEnable
     */
    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
//        if (mEditorTabBar == null) return;
//        mEditorTabBar.setEnable(isEnable);
    }

    private TextView lsqSoundName;
    private ImageView imageAddSound;

    /**
     * 初始化headView
     *
     * @return View
     */
    private View initHeadView() {
        if (mHeaderView == null) {
            View headView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_navigation, null);
            mHeaderView = headView;
            lsqSoundName = mHeaderView.findViewById(R.id.lsq_sound_name);
            lsqSoundName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            lsqSoundName.setMarqueeRepeatLimit(-1);
            imageAddSound = mHeaderView.findViewById(R.id.add_music_img);
            if(AppConstants.shootBackgroundMusicBean != null){

                LinearLayout.LayoutParams layoutParams = ( LinearLayout.LayoutParams ) lsqSoundName.getLayoutParams();
                layoutParams.width = (int) TextWidthUtils.getTextWith(getEditorController().getActivity(),lsqSoundName,AppConstants.shootBackgroundMusicBean.getTitle()+getEditorController().getActivity().getResources().getString(R.string.tv_blank))-2;
                layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.START;
                lsqSoundName.setText(AppConstants.shootBackgroundMusicBean.getTitle()+getEditorController().getActivity().getResources().getString(R.string.tv_blank));
                imageAddSound.setSelected(false);
            }else {
                imageAddSound.setSelected(true);
            }
            ImageView lsqBack = mHeaderView.findViewById(R.id.lsq_back);
            lsqBack.setOnClickListener(mOnClickListener);
            LinearLayout trimLl =  mHeaderView.findViewById(R.id.lsq_item_trim_layout);
            //如果是从trim页面跳转过来的
            if(getEditorController().getActivity().isTrim){
                trimLl.setVisibility(View.GONE);
            }else {
                trimLl.setVisibility(View.VISIBLE);
            }
            mHeaderView.findViewById(R.id.lsq_item_text_layout).setOnClickListener(mOnClickListener);
            mHeaderView.findViewById(R.id.lsq_item_sticker_layout).setOnClickListener(mOnClickListener);
            mHeaderView.findViewById(R.id.lsq_item_filters_layout).setOnClickListener(mOnClickListener);
            mHeaderView.findViewById(R.id.lsq_item_effects_layout).setOnClickListener(mOnClickListener);
            trimLl.setOnClickListener(mOnClickListener);
            mHeaderView.findViewById(R.id.lsq_item_voiceover_layout).setOnClickListener(mOnClickListener);
            mHeaderView.findViewById(R.id.lsq_add_sound).setOnClickListener(mOnClickListener);

            ConstraintLayout.LayoutParams lsqBackLayoutParams = (ConstraintLayout.LayoutParams) lsqBack.getLayoutParams();
            lsqBackLayoutParams.topMargin = TuSdkContext.dip2px(28) + VideoBeautyPlugin.statusBarHeight;
            lsqBack.setLayoutParams(lsqBackLayoutParams);




        }
        return mHeaderView;
    }


    public void setSoundName(String soundName) {

        if (!soundName.equals( getEditorController().getActivity().getResources().getString(R.string.add_sound))) {
            LinearLayout.LayoutParams layoutParams = ( LinearLayout.LayoutParams ) lsqSoundName.getLayoutParams();
            layoutParams.width = (int) TextWidthUtils.getTextWith(getEditorController().getActivity(),lsqSoundName,soundName+getEditorController().getActivity().getResources().getString(R.string.tv_blank))-2;
            layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.START;
            imageAddSound.setSelected(false);
            lsqSoundName.setText(soundName+ getEditorController().getActivity().getResources().getString(R.string.tv_blank));
        }else {
            LinearLayout.LayoutParams layoutParams = ( LinearLayout.LayoutParams ) lsqSoundName.getLayoutParams();
            layoutParams.width = (int) TextWidthUtils.getTextWith(getEditorController().getActivity(),lsqSoundName,getEditorController().getActivity().getResources().getString(R.string.add_sound));
            imageAddSound.setSelected(true);
            lsqSoundName.setText(soundName);
        }

    }

    private OnItemClickListener itemClickListener;

    public void setItemOnClickListener(OnItemClickListener onClickListener) {
        this.itemClickListener = onClickListener;
    }


    /**
     * 初始化BottomView
     *
     * @return View
     */
    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_home_bottom, null);
            bottomView.findViewById(R.id.lsq_next).setOnClickListener(mOnClickListener);
            mBottomView = bottomView;
        }
        return mBottomView;
    }

    private void backDeal() {
        AppConstants.EDIT_TYPE = 2;
        EventBus.getDefault().post(new BackEvent());
        EventBus.getDefault().post(new SelectSoundEvent(null, AppConstants.ENTER_STATE));
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                getEditorController().getActivity().finish();
            }
        },1000);

    }

    public void backAction() {
        DialogHelper.remindTitleAndContentCenter(getEditorController().getActivity(), getEditorController().getActivity().getResources().getString(R.string.dialog_title_discard_edits_tips), getEditorController().getActivity().getResources().getString(R.string.dialog_content_discard_entire_clip_tips), new DialogHelper.onRemindSureClickListener() {
            @Override
            public void onSureClick() {
                backDeal();
            }
        });
    }


    /**
     * 点击事件回调
     **/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lsq_back) {
               backAction();
            } else if (id == R.id.lsq_next) {
                if (getEditorController().isSaving()) return;

                AppConstants.EDIT_TYPE = 1;
                getEditorController().saveVideo();
                new SpUtils(mBottomView.getContext()).remove(SpUtils.recoverKey);
            } else if (id == R.id.lsq_item_text_layout) {
//               if(itemClickListener!=null){
//                   itemClickListener.onClick(TabType.TextTab);
//               }
                StickerView stickerView =  getEditorController().getActivity().getTextStickerView();
                int count = stickerView.getStickerItems().size();
                if(count <5){
                    getEditorController().getActivity().openOverlay(false, -1);
                }else {
                    ToastUtils.showRedToast(getEditorController().getActivity(), getEditorController().getActivity().getString(R.string.toast_stickers_limit));
                }

            } else if (id == R.id.lsq_item_sticker_layout) {
                StickerView stickerView =  getEditorController().getActivity().getTextStickerView();
                int count = stickerView.getStickerItems().size();
                if(count <5){
                    if (itemClickListener != null) {
                        itemClickListener.onClick(TabType.Sticker);
                    }
                }else {
                    ToastUtils.showRedToast(getEditorController().getActivity(), getEditorController().getActivity().getString(R.string.toast_stickers_limit));
                }
            } else if (id == R.id.lsq_item_filters_layout) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(TabType.FilterTab);
                }
            } else if (id == R.id.lsq_item_effects_layout) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(TabType.EffectTab);
                }
            } else if (id == R.id.lsq_item_trim_layout) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(TabType.TrimTime);
                }
            } else if (id == R.id.lsq_item_voiceover_layout) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(TabType.Voiceover);
                }
            } else if (id == R.id.lsq_add_sound) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(TabType.MusicTab);
                }
            }


        }
    };
}
