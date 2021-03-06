package org.lasque.twsdkvideo.video_beauty.editor.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerDynamicItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerImageEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.event.StickerEvent;
import org.lasque.twsdkvideo.video_beauty.utils.BitmapUtils;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.views.TileRecycleAdapter;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

import static android.view.View.GONE;

import java.io.File;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2019/3/4 11:10
 * @Copright (c) 2019 tw. All rights reserved.
 * <p>
 * ????????????
 */
public class EditorStickerComponent extends EditorComponent {
    /**
     * ????????????
     **/
    private View mBottomView;
    /**
     * ?????????????????????
     **/
    private StickerView mStickerView;
    /**
     * ????????????
     **/
    private View mBackBtn;
    /**
     * ?????????
     **/
    private View mNextBtn;
    /**
     * RecycleView
     **/
    private RecyclerView mTileRecycle;
    /**
     * ?????????Adapter
     **/
    private TileRecycleAdapter mTileAdapter;
    /**
     * ???????????????
     **/
    private TuSdkMovieScrollPlayLineView mLineView;
    /**
     * ????????????
     **/
    private ImageView mPlayBtn;
    /**
     * ??????????????????
     **/
    private long defaultDurationUs = 1 * 1000000;
    /**
     * ??????????????????
     **/
    private int minSelectTimeUs = 1 * 1000000;
    /**
     * ?????????????????????
     **/
    private StickerImageData mCurrentSticker;
    /**
     * ??????????????????
     **/
    private TuSdkMovieColorRectView mCurrentColorRectView;
    /**
     * ??????????????????
     **/
    private EditorStickerImageBackups mStickerImageBackups;
    /**
     * ???????????????
     **/
    private TextView mSelectPhoto;

//    private TuSdkSize mCurrentPreviewSize = null;


    public EditorStickerComponent setHeadAction() {
        if (mNextBtn != null) {
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ????????????
                    mStickerImageBackups.onApplyEffect();
                    // ??????????????????
                    handleCompleted();
                    getEditorController().onBackEvent();
                }
            });
            mBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mStickerImageBackups.onBackEffect();
                    getEditorController().onBackEvent();
                }
            });
        }
        return this;
    }


    /**
     * ????????????????????????
     *
     * @since V3.0.0
     */
//    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
//        /**
//         * @param previewSize ????????????????????????
//         */
//        @Override
//        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
//            if (getEditorController().getActivity().getImageStickerView() == null) return;
//            mCurrentPreviewSize = TuSdkSize.create(previewSize.width, previewSize.height);
//        }
//    };

    /**
     * ??????????????????
     **/
    private StickerView.StickerViewDelegate mStickerDelegate = new StickerView.StickerViewDelegate() {
        @Override
        public boolean canAppendSticker(StickerView view, StickerData sticker) {
            return true;
        }

        @Override
        public boolean canAppendSticker(StickerView view, StickerDynamicData sticker) {
            return true;
        }

        @Override
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerData stickerData, String s, boolean b)  {
            if (stickerData != null && stickerData instanceof StickerImageData) {
                mLineView.setShowSelectBar(true);
                mCurrentSticker = (StickerImageData) stickerData;
                mLineView.setLeftBarPosition(((StickerImageData) stickerData).starTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                mLineView.setRightBarPosition(((StickerImageData) stickerData).stopTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                mCurrentColorRectView = mStickerImageBackups.findColorRect(stickerData);
            }
        }

        @Override
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerDynamicData stickerDynamicData, String s, boolean b) {

        }

        @Override
        public void onStickerItemViewReleased(StickerItemViewInterface stickerItemViewInterface, PointF pointF) {

        }

        @Override
        public void onCancelAllStickerSelected() {
            mLineView.setShowSelectBar(false);
            mCurrentColorRectView = null;
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
            if (stickerItemViewInterface.getStickerType() == StickerView.StickerType.Text) return;
            /// 0:???????????? 1:????????????
            if (operation == 0) {
                mStickerImageBackups.removeBackupEntityWithSticker((StickerImageItemView) stickerItemViewInterface);
                mLineView.setShowSelectBar(false);
            } else {
                mLineView.setShowSelectBar(true);
                float startPercent = mLineView.getCurrentPercent();
                float endPercent = ((StickerImageData) stickerData).stopTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                TuSdkMovieColorRectView rectView = mLineView.recoverColorRect(R.color.lsq_scence_effect_color_EdgeMagic01, startPercent, endPercent);
                mCurrentColorRectView = rectView;
                mStickerImageBackups.addBackupEntity(EditorStickerImageBackups.createBackUpEntity(stickerData, (StickerImageItemView) stickerItemViewInterface, rectView));
            }
        }

        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

        }

        @Override
        public void onStickerItemViewMove(StickerItemViewInterface stickerItemViewInterface, Rect rect, PointF pointF) {

        }
    };


    /**
     * ????????????????????????????????????(rectView?????????????????????)
     */
    private TuSdkMovieColorGroupView.OnSelectColorRectListener mSelectColorListener = new TuSdkMovieColorGroupView.OnSelectColorRectListener() {
        @Override
        public void onSelectColorRect(final TuSdkMovieColorRectView rectView) {
            if (rectView == null) {
                mLineView.setShowSelectBar(false);
              //  mStickerView.cancelAllStickerSelected();
            }
            if (mStickerView.getStickerImageItems().size() == 0) return;
            final StickerImageData stickerData = (StickerImageData) mStickerImageBackups.findStickerData(rectView);
//                if(rectView == mCurrentColorRectView)return;

            if (stickerData != null && stickerData instanceof StickerImageData) {
                mLineView.setShowSelectBar(true);
                mCurrentSticker = stickerData;
                final float leftPercent = stickerData.starTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                float rightPercent = stickerData.stopTimeUs / (float) getEditorPlayer().getTotalTimeUs();
                mLineView.setLeftBarPosition(leftPercent);
                mLineView.setRightBarPosition(rightPercent);

                if (mCurrentColorRectView == rectView) return;

                mCurrentColorRectView = rectView;
                ThreadHelper.post(new Runnable() {
                    @Override
                    public void run() {
                        mLineView.seekTo(rectView.getStartPercent() + 0.002f);
                    }
                });
            }

            if (mStickerImageBackups.findStickerItem(rectView) != null) {
                mStickerView.onStickerItemViewSelected(mStickerImageBackups.findStickerItem(rectView));
                mStickerImageBackups.findStickerItem(rectView).setSelected(true);
            }
        }
    };


    /**
     * ???????????????????????????
     */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mBottomView == null) return;
            if(MovieEditorController.mCurrentComponent instanceof EditorStickerComponent){
                setPlayState(state);
            }

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (mBottomView == null || isAnimationStaring) return;

            if(MovieEditorController.mCurrentComponent instanceof EditorStickerComponent){
                mLineView.seekTo(percentage);
            }

        }
    };


    /**
     * ????????????????????????:0
     */
    private TuSdkRangeSelectionBar.OnSelectRangeChangedListener mOnSelectTimeChangeListener = new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
        @Override
        public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
            if (mCurrentSticker == null) return;
            if (type == 0) {
                mCurrentSticker.starTimeUs = (long) (leftPercent * getEditorPlayer().getTotalTimeUs());
            } else if (type == 1) {
                mCurrentSticker.stopTimeUs = (long) (rightPerchent * getEditorPlayer().getTotalTimeUs());
            }
            mLineView.changeColorRect(mCurrentColorRectView, leftPercent, rightPerchent);
        }
    };


    /**
     * ????????????????????????????????????:1
     */
    private TuSdkRangeSelectionBar.OnTouchSelectBarListener mOnTouchSelectBarlistener = new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
        @Override
        public void onTouchBar(float leftPercent, float rightPerchent, int type) {
            if (type == 0) {
                mLineView.seekTo(leftPercent);
            } else if (type == 1) {
                mLineView.seekTo(rightPerchent);
            }
        }
    };

    /**
     * ??????????????????:2
     */
    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayPositionListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {
            long playPositionTime = (long) (progress * getEditorPlayer().getTotalTimeUs());
            for (StickerItemViewInterface itemViewInterface : mStickerView.getStickerItems()) {
                if (itemViewInterface instanceof StickerImageItemView) {
                    StickerImageItemView itemView = (StickerImageItemView) itemViewInterface;
                    StickerImageData textData = (StickerImageData) itemView.getSticker();
                    if (textData.isContains(playPositionTime)) {
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(View.GONE);
                    }
                } else if (itemViewInterface instanceof StickerTextItemView) {
                    StickerTextItemView itemView = (StickerTextItemView) itemViewInterface;
                    StickerTextData imageData = (StickerTextData) itemView.getSticker();
                    if (imageData.isContains(playPositionTime)) {
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(GONE);
                    }
                } else if (itemViewInterface instanceof StickerDynamicItemView) {
                    StickerDynamicItemView itemView = ((StickerDynamicItemView) itemViewInterface);
                    StickerDynamicData dynamicData = itemView.getCurrentStickerGroup();
                    itemView.updateStickers(System.currentTimeMillis());
                    if (dynamicData.isContains(playPositionTime)) {
                        itemView.setVisibility(View.VISIBLE);
                    } else {
                        itemView.setVisibility(GONE);
                    }
                }
            }

            if (!isTouching) return;
            if (isTouching) {
                getEditorPlayer().pausePreview();
            }

            if (getEditorPlayer().isPause())
                getEditorPlayer().seekOutputTimeUs(playPositionTime);

        }

        @Override
        public void onCancelSeek() {

        }
    };

      private VideoContent mHolderView;

    /**
     * ??????????????????
     *
     * @param editorController
     */
    public EditorStickerComponent(MovieEditorController editorController,VideoContent mHolderView) {
        super(editorController);
        this.mHolderView = mHolderView;
        mComponentType = EditorComponentType.Sticker;
        mStickerView = getEditorController().getActivity().getImageStickerView();
//        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);

        mStickerImageBackups = new EditorStickerImageBackups(mStickerView, getEditorEffector(), editorController.getImageTextRankHelper());

    //    mStickerView.setDelegate(mStickerDelegate);
    }

    @Override
    public void attach() {
      //  EventBus.getDefault().register(this);
        // ????????????????????????
        getEditorController().getActivity().getTextStickerView().setVisibility(View.VISIBLE);
      //  getEditorController().getBottomView().addView(getBottomView());
        getEditorPlayer().pausePreview();
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);
        /// ??????????????????
     //   mStickerView.setDelegate(mStickerDelegate);
      //  mStickerView.changeOrUpdateStickerType(StickerView.StickerType.Image);
    }

    @Override
    public void detach() {
    //    EventBus.getDefault().unregister(this);
//        getEditorPlayer().seekOutputTimeUs(0);
//        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
//        getEditorController().getVideoContentView().setClickable(true);
//        getEditorController().getActivity().getTextStickerView().setVisibility(GONE);
//
//        mStickerImageBackups.onComponentDetach();
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        getEditorPlayer().seekOutputTimeUs(0);
        if (getEditorPlayer().isReversing()) {
            mLineView.seekTo(1f);
        } else {
            mLineView.seekTo(0f);
        }
    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            mBottomView = initBottomView();
        }
        return mBottomView;
    }

    public EditorStickerImageBackups getStickerImageBackups() {
        return mStickerImageBackups;
    }

    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_sticker_bottom, null);
            mBottomView = bottomView;


            mLineView = bottomView.findViewById(R.id.lsq_editor_sticker_play_range);

            mSelectPhoto = bottomView.findViewById(R.id.go_photo);
            mSelectPhoto.setOnClickListener(mOnClickListener);
            // ????????????????????????
            mLineView.setOnSelectColorRectListener(mSelectColorListener);
            // ????????????????????????
            mLineView.setSelectRangeChangedListener(mOnSelectTimeChangeListener);
            mLineView.setOnTouchSelectBarListener(mOnTouchSelectBarlistener);
            // ????????????????????????
            mLineView.setOnProgressChangedListener(mOnScrollingPlayPositionListener);
            float minPercent = minSelectTimeUs / (float) getEditorPlayer().getTotalTimeUs();
            mLineView.setMinWidth(minPercent);

            mLineView.setShowSelectBar(false);
            mLineView.setType(1);
            mStickerImageBackups.setLineView(mLineView);

            mPlayBtn = bottomView.findViewById(R.id.lsq_editor_sticker_play);
            mPlayBtn.setOnClickListener(mOnClickListener);
            mBackBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_back1);
            mNextBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_save);

            mTileRecycle = bottomView.findViewById(R.id.lsq_sticker_list_view);
            mTileRecycle.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mTileAdapter = new TileRecycleAdapter();
            mTileRecycle.setAdapter(mTileAdapter);
            mTileAdapter.setItemClickListener(new TileRecycleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int resId) {
                  //  getEditorPlayer().pausePreview();
//                       File file = new File("/storage/emulated/0/DCIM/Camera/20220114_195902.jpg");
//                   Uri uri =  Uri.fromFile(file);
//                   // Bitmap bitmap= BitmapUtils.decodeUri(getEditorController().getActivity(),uri,100,100);
//                  Bitmap bitmap =   BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/20220114_195902.jpg");
                    Bitmap bitmap = BitmapFactory.decodeResource(getEditorController().getActivity().getResources(), resId);
                    StickerImageData imageData = new StickerImageData();
                    imageData.setImage(bitmap);
                    imageData.height = TuSdkContext.px2dip(bitmap.getHeight());
                    imageData.width = TuSdkContext.px2dip(bitmap.getWidth());
                    imageData.starTimeUs = 0;
                    imageData.stopTimeUs = 2 * 1000000;

                    //???????????????2s
                    imageData.starTimeUs = (long) (mLineView.getCurrentPercent() * getEditorPlayer().getInputTotalTimeUs());
                    if (imageData.starTimeUs + defaultDurationUs > getEditorPlayer().getInputTotalTimeUs()) {
                        imageData.stopTimeUs = getEditorPlayer().getOutputTotalTimeUS();
                    } else {
                        imageData.stopTimeUs = imageData.starTimeUs + defaultDurationUs;
                    }

                    getEditorController().getActivity().getImageStickerView().appendSticker(imageData);
                }
            });
        }
        return mBottomView;
    }

    public void backUpDatas() {
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediEffectDataTypeStickerImage);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeText);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeDynamicSticker);
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mStickerImageBackups == null) return;
                mStickerImageBackups.onComponentAttach();
            }
        }, 50);

    }

    /**
     * ??????????????????
     *
     * @param state 0 ??????  1 ??????
     * @since V3.0.0
     */
    public void setPlayState(int state) {
        if (state == 1) {
            getEditorPlayer().pausePreview();
        } else {
          //  mStickerView.cancelAllStickerSelected();
            getEditorPlayer().startPreview();
        }

        mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == R.id.lsq_editor_sticker_play) {
                if (getEditorPlayer().isPause())
                    getEditorPlayer().startPreview();
                else
                    getEditorPlayer().pausePreview();
            } else if (id == R.id.go_photo) {
                // todo ??????????????????
                AlbumUtils.openPictureAlbum(MovieEditorActivity.class.getName(), Constants.MAX_EDITOR_SELECT_MUN, AppConstants.STICKER_ENTER);
            }
        }
    };







    /**
     * ????????????
     */
    private void startPreview() {
        getEditorPlayer().startPreview();
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mLineView.addBitmap(bitmap);
    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mLineView.addFirstFrameBitmap(bitmap);
    }

    /**
     * ??????????????????
     */
    protected void handleCompleted() {
        for (StickerItemViewInterface stickerItem : getEditorController().getActivity().getImageStickerView().getStickerItems()) {
            if (stickerItem instanceof StickerImageItemView) {
                float renderWidth = mHolderView.getWidth();
                float renderHeight = mHolderView.getHeight();

//                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStickerView.getLayoutParams();
//                float renderWidth = layoutParams.width - layoutParams.leftMargin;
//                float renderHeight = layoutParams.height - layoutParams.topMargin;
//
//
//                float renderWidth = mStickerView.getWidth() - mStickerView.getLeft();
//                float renderHeight = getEditorController().getVideoContentView().getMeasuredHeight() - getEditorController().getVideoContentView().getLeft();


                StickerImageItemView stickerItemView = ((StickerImageItemView) stickerItem);

                //?????????????????????????????????
                stickerItemView.resetRotation();
                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_transparent), 0);

                TuSdkSize sclaSize = stickerItemView.getRenderScaledSize();
                //?????????????????????
                Bitmap textBitmap = stickerItemView.getStickerData().getImage();

                stickerItemView.setStroke(TuSdkContext.getColor(R.color.lsq_color_white), 2);
                StickerView stickerView = getEditorController().getActivity().getImageStickerView();
                int[] parentLocaiont = new int[2];
                stickerView.getLocationInWindow(parentLocaiont);


                //???????????????????????????
                int[] locaiont = new int[2];
                /** ???SDKVersion >= 27 ???????????? getLocationInWindow() ?????? ?????????????????????????????? ??????27??? getLocationInWindow() ??? getLocationOnScreen()?????????????????????*/
                stickerItemView.getImageView().getLocationInWindow(locaiont);
                int pointX = locaiont[0] - parentLocaiont[0];
                int pointY = (int) (locaiont[1] - parentLocaiont[1]);

                /** ??????????????? */
                float offsetX = pointX / renderWidth;
                float offsetY = pointY / renderHeight;
                float stickerWidth = (float) sclaSize.width / renderWidth;
                float stickerHeight = (float) sclaSize.height / renderHeight;
                float degree = stickerItemView.getResult(null).degree;
                float ratio = sclaSize.maxMinRatio();

                //????????????????????????
                long starTimeUs = ((StickerImageData) stickerItemView.getSticker()).starTimeUs;
                long stopTimeUs = ((StickerImageData) stickerItemView.getSticker()).stopTimeUs;
                //??????????????????????????????
                TuSdkMediaStickerImageEffectData stickerImageEffectData = createTileEffectData(textBitmap, stickerWidth, stickerHeight, offsetX, offsetY, degree, starTimeUs, stopTimeUs, ratio);
                getEditorEffector().addMediaEffectData(stickerImageEffectData);

                EditorStickerImageBackups.StickerImageBackupEntity backupEntity = mStickerImageBackups.findTextBackupEntityByMemo(stickerItemView);
                if (backupEntity != null)
                    backupEntity.stickerImageMediaEffectData = stickerImageEffectData;

                stickerItemView.setVisibility(GONE);
            } else if (stickerItem instanceof StickerTextItemView) {
                StickerTextItemView stickerItemView = ((StickerTextItemView) stickerItem);
                EditorTextBackups.TextBackupEntity backupEntity = getEditorController().getTextComponent().getTextBackups().findTextBackupEntityToMemo(stickerItemView);
                if (backupEntity != null)
                    getEditorEffector().addMediaEffectData(backupEntity.textMediaEffectData);
                stickerItemView.setVisibility(GONE);
            } else if (stickerItem instanceof StickerDynamicItemView) {
                StickerDynamicItemView itemView = ((StickerDynamicItemView) stickerItem);
                EditorStickerImageBackups.DynamicStickerBackupEntity entity = getEditorController().getDynamicStickerComponent().getStickerImageBackups().findDynamicStickerBackupEntityByMemo(itemView);
                if (entity != null) {
                    getEditorEffector().addMediaEffectData(entity.effectData);
                }
                itemView.setVisibility(GONE);
            }
        }


        //????????????????????????
        getEditorController().getActivity().getImageStickerView().cancelAllStickerSelected();
        getEditorController().getActivity().getImageStickerView().removeAllSticker();
    }

    /**
     * ???????????????????????? TuSdkMediaEffectData
     *
     * @param bitmap      ??????
     * @param displaySize ?????????????????????
     * @param offsetX     ?????????????????????X????????????
     * @param offsetY     ?????????????????????Y????????????
     * @param rotation    ???????????????
     * @param startTimeUs ?????????????????????
     * @param stopTimeUs  ?????????????????????
     * @param stickerSize ??????StickerView??????????????????????????????
     * @return
     */
    @Deprecated
    protected TuSdkMediaStickerImageEffectData createTileEffectData(Bitmap bitmap, TuSdkSize displaySize, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs, TuSdkSize stickerSize) {
        TuSdkMediaStickerImageEffectData mediaTextEffectData = new TuSdkMediaStickerImageEffectData(bitmap, offsetX, offsetY, rotation, displaySize, stickerSize);
        mediaTextEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
        return mediaTextEffectData;
    }

    /**
     * @param bitmap        ??????
     * @param stickerWidth  ???????????? ?????????????????????
     * @param stickerHeight ???????????? ?????????????????????
     * @param offsetX       ???????????? x???????????????????????????
     * @param offsetY       ???????????? y???????????????????????????
     * @param rotation      ???????????????
     * @param startTimeUs   ?????????????????????
     * @param stopTimeUs    ?????????????????????
     * @return
     */
    protected TuSdkMediaStickerImageEffectData createTileEffectData(Bitmap bitmap, float stickerWidth, float stickerHeight, float offsetX, float offsetY, float rotation, long startTimeUs, long stopTimeUs, float ratio) {
        TuSdkMediaStickerImageEffectData mediaStickerImageEffectData = new TuSdkMediaStickerImageEffectData(bitmap, stickerWidth, stickerHeight, offsetX, offsetY, rotation, ratio);
        mediaStickerImageEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(startTimeUs, stopTimeUs));
        return mediaStickerImageEffectData;
    }

}
