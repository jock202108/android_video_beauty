package org.lasque.twsdkvideo.video_beauty.editor.component;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.lasque.tusdk.core.TuSdk;
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
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.data.BackupsTimesBean;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorGroupView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.rangeselect.TuSdkMovieColorRectView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

/**
 * droid-sdk-video
 *
 * @author MirsFang
 * @Date 2018/9/25 15:53
 * @Copright (c) 2018 tw. All rights reserved.
 * <p>
 * 文字组件
 */
public class EditorTextComponent extends EditorComponent {
    private static final String TAG = "EditorTextComponent";

    /**
     * 文字特效备忘管理
     **/
    private EditorTextBackups mTextBackups;

    private TuSdkSize mCurrentPreviewSize = null;

    /**
     * 文字的底部View
     */
    public EditorTextBottomView mBottomView;
    /**
     * 贴纸视图
     */
    private StickerView mStickerView;

    /**
     * 最小持续时间
     **/
    private int minSelectTimeUs = 1 * 1000000;


    /**
     * 备份进入的数据
     */
    private HashMap<Long, BackupsTimesBean> backupsTimesBeans = new HashMap<Long, BackupsTimesBean>();

    /**
     * trim时间长度的文字
     */
    private String mTrimTimeTips;

    /**
     * 当前选中的贴纸
     */
    private StickerItemViewInterface mCurrentStickerItemView;

    /**
     * 是否播放的时候显示光标
     */
    private boolean isNeedShowCursor = true;

    private Handler mHandler = new MovieEditorActivity.MyHandler(getEditorController().getActivity()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //加载缩略图
                if (msg.what == 0) {
                    isNeedShowCursor = true;
                }
            }
        }
    };



    /**
     * 显示区域改变回调
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (mStickerView == null) return;
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mStickerView.resize(previewSize, getEditorController().getVideoContentView());
                }
            });

        }
    };

    /**
     * 文字贴纸视图回调
     */
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
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerData stickerData, String s, boolean b) {
            if (stickerData != null) {
                mCurrentStickerItemView = stickerItemViewInterface;
                mBottomView.mLineView.setShowSelectBar(true);
                setAlpha(0.5f);
                if (stickerItemViewInterface instanceof StickerTextItemView) {
                    ((StickerTextItemView) stickerItemViewInterface).setAlpha(1f);
                    ((StickerTextItemView) stickerItemViewInterface).setStroke(Color.WHITE, 3);
                    ((StickerTextItemView) stickerItemViewInterface).setSelected(true);
                    /// 设置左边Bar的位置
                    mBottomView.mLineView.setLeftBarPosition(((StickerTextData) stickerData).starTimeUs / (float) getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                    /// 设置右边Bar的位置
                    mBottomView.mLineView.setRightBarPosition(Math.min(1f,((StickerTextData) stickerData).stopTimeUs / (float) getMovieEditor().getEditorPlayer().getInputTotalTimeUs()));
                    double time = (double) (((StickerTextData) stickerData).stopTimeUs - ((StickerTextData) stickerData).starTimeUs) / 1000 / 1000.0;
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(1);
                    mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), (mCurrentStickerItemView instanceof StickerTextItemView) ? getEditorController().getActivity().getString(R.string.tip_text) : getEditorController().getActivity().getString(R.string.tip_stickers)) + nf.format(time) + "s";
                    mBottomView.mBottomText.setText(mTrimTimeTips);

                } else if (stickerItemViewInterface instanceof StickerImageItemView) {
                    ((StickerImageItemView) stickerItemViewInterface).setAlpha(1f);
                    ((StickerImageItemView) stickerItemViewInterface).setStroke(Color.WHITE, 3);
                    ((StickerImageItemView) stickerItemViewInterface).setSelected(true);
                    /// 设置左边Bar的位置
                    mBottomView.mLineView.setLeftBarPosition(((StickerImageData) stickerData).starTimeUs / (float) getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                    /// 设置右边Bar的位置
                    mBottomView.mLineView.setRightBarPosition(Math.min(1f,((StickerImageData) stickerData).stopTimeUs / (float) getMovieEditor().getEditorPlayer().getInputTotalTimeUs()));
                    double time = (double) (((StickerImageData) stickerData).stopTimeUs - ((StickerImageData) stickerData).starTimeUs) / 1000 / 1000.0;
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(1);
                    mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), (mCurrentStickerItemView instanceof StickerTextItemView) ? getEditorController().getActivity().getString(R.string.tip_text) : getEditorController().getActivity().getString(R.string.tip_stickers)) + nf.format(time) + "s";
                    mBottomView.mBottomText.setText(mTrimTimeTips);
                }
            }

        }

        @Override
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerDynamicData stickerDynamicData, String s, boolean b) {
            if (stickerDynamicData != null) {
                mCurrentStickerItemView = stickerItemViewInterface;
                mBottomView.mLineView.setShowSelectBar(true);
                setAlpha(0.5f);
                if (stickerItemViewInterface instanceof StickerDynamicItemView) {
                    StickerDynamicItemView stickerItemViewInterface1 = (StickerDynamicItemView) stickerItemViewInterface;
                    stickerItemViewInterface1.setAlpha(1f);
                    ((StickerDynamicItemView) stickerItemViewInterface1).setStroke(Color.WHITE, 3);
                    ((StickerDynamicItemView) stickerItemViewInterface1).setSelected(true);
                    /// 设置左边Bar的位置
                    mBottomView.mLineView.setLeftBarPosition(stickerItemViewInterface1.getCurrentStickerGroup().starTimeUs / (float) getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                    /// 设置右边Bar的位置
                    mBottomView.mLineView.setRightBarPosition(Math.min(1f,stickerItemViewInterface1.getCurrentStickerGroup().stopTimeUs / (float) getMovieEditor().getEditorPlayer().getInputTotalTimeUs()));
                    double time = (double) (((StickerDynamicData) stickerDynamicData).stopTimeUs - ((StickerDynamicData) stickerDynamicData).starTimeUs) / 1000 / 1000.0;
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    nf.setMaximumFractionDigits(1);
                    mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), (mCurrentStickerItemView instanceof StickerTextItemView) ? getEditorController().getActivity().getString(R.string.tip_text) : getEditorController().getActivity().getString(R.string.tip_stickers)) + nf.format(time) + "s";
                    mBottomView.mBottomText.setText(mTrimTimeTips);
                }
            }
        }

        @Override
        public void onStickerItemViewReleased(StickerItemViewInterface stickerItemViewInterface, PointF pointF) {

        }

        @Override
        public void onCancelAllStickerSelected() {
            // if (mBottomView != null) mBottomView.mLineView.setShowSelectBar(false);
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, final StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

        }

        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

        }

        @Override
        public void onStickerItemViewMove(StickerItemViewInterface stickerItemViewInterface, Rect rect, PointF pointF) {

        }
    };


    /**
     * 播放状态和进度回调 (播放器的)
     */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mBottomView == null) return;
            if(getEditorController().getCurrentComponent() instanceof EditorTextComponent){
                mBottomView.setPlayState(state);
            }

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (mBottomView == null || isAnimationStaring) return;
            if (isNeedShowCursor) {
                mBottomView.mLineView.setPercent(percentage);
            }

        }
    };


    /**
     * 创建当前组件
     *
     * @param editorController
     */
    public EditorTextComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Text;
        mStickerView = editorController.getActivity().getTextStickerView();
        getBottomView();
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
        getEditorPlayer().addProgressListener(mPlayProgressListener);

    }

    @Override
    public void attach() {
        getEditorController().getActivity().getTextStickerView().setVisibility(View.VISIBLE);
        getEditorController().getBottomView().removeAllViews();
        getEditorController().getBottomView().addView(getBottomView());

        // 计算最小宽度
        float minPercent = minSelectTimeUs / (float) getEditorPlayer().getInputTotalTimeUs();
        mBottomView.mLineView.setMinWidth(minPercent);
        // 暂停
        getEditorPlayer().pausePreview();
        getEditorController().getVideoContentView().setClickable(false);
        getEditorController().getPlayBtn().setVisibility(View.GONE);

        mStickerView.changeOrUpdateStickerType(StickerView.StickerType.Normal);
        mStickerView.setDelegate(mStickerDelegate);
        mBottomView.mLineView.setShowSelectBar(true);
        mBottomView.setPlayState(1);
        backupsTimes();
        setAlpha(0.5f);
        setGlobalListener();
    }

    private  boolean isFirst = true;

    private void setGlobalListener() {
        mBottomView.mLineView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getFirstSelectItemView();
                ViewTreeObserver.OnGlobalLayoutListener listener = this;
                mBottomView.mLineView.post(new Runnable() {
                    @Override
                    public void run() {
                        mBottomView.mLineView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                    }
                });
            }
        });
    }


    /**
     * 得到第一次进入的数据
     */
    private void getFirstSelectItemView() {
        StickerItemViewInterface stickerItemViewInterface = mStickerView.getCurrentItemViewSelected();
//        if(stickerItemViewInterface == null){
//            stickerItemViewInterface = mStickerView.getStickerItems().get(0);
//        }
        if (stickerItemViewInterface != null) {
            mCurrentStickerItemView = stickerItemViewInterface;
            if (mCurrentStickerItemView instanceof StickerTextItemView) {
                StickerTextItemView stickerTextItemView = ((StickerTextItemView) mCurrentStickerItemView);
                StickerTextData stickerTextData = (StickerTextData) mCurrentStickerItemView.getStickerData();
                long startTimeUs = stickerTextData.starTimeUs;
                long stopTimeUs = stickerTextData.stopTimeUs;
                float start = startTimeUs / (float) getEditorController().getMovieEditor().getEditorPlayer().getInputTotalTimeUs();
                float stop = (stopTimeUs / (float) getEditorController().getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                mBottomView.mLineView.setLeftBarPosition(start);
                mBottomView.mLineView.setRightBarPosition(Math.min(1f,stop));
                stickerTextItemView.setAlpha(1f);
                stickerTextItemView.setStroke(Color.WHITE, 3);
                stickerTextItemView.setSelected(true);

                double time = (double) (stickerTextData.stopTimeUs - stickerTextData.starTimeUs) / 1000 / 1000.0;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), getEditorController().getActivity().getString(R.string.tip_text)) + nf.format(time) + "s";
                mBottomView.mBottomText.setText(mTrimTimeTips);

            } else if (mCurrentStickerItemView instanceof StickerImageItemView) {
                StickerImageItemView stickerTextItemView = ((StickerImageItemView) mCurrentStickerItemView);
                StickerImageData stickerTextData = (StickerImageData) mCurrentStickerItemView.getStickerData();
                long startTimeUs = stickerTextData.starTimeUs;
                long stopTimeUs = stickerTextData.stopTimeUs;
                float start = startTimeUs / (float) getEditorController().getMovieEditor().getEditorPlayer().getInputTotalTimeUs();
                float stop = (stopTimeUs / (float) getEditorController().getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                mBottomView.mLineView.setLeftBarPosition(start);
                mBottomView.mLineView.setRightBarPosition(Math.min(1f,stop));
                stickerTextItemView.setAlpha(1f);
                stickerTextItemView.setStroke(Color.WHITE, 3);
                stickerTextItemView.setSelected(true);

                double time = (double) (stickerTextData.stopTimeUs - stickerTextData.starTimeUs) / 1000 / 1000.0;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), getEditorController().getActivity().getString(R.string.tip_stickers))+ nf.format(time) + "s";
                mBottomView.mBottomText.setText(mTrimTimeTips);
            } else if (mCurrentStickerItemView instanceof StickerDynamicItemView) {
                StickerDynamicItemView stickerTextItemView = ((StickerDynamicItemView) mCurrentStickerItemView);
                StickerDynamicData stickerTextData = ((StickerDynamicItemView) mCurrentStickerItemView).getCurrentStickerGroup();
                long startTimeUs = stickerTextData.starTimeUs;
                long stopTimeUs = stickerTextData.stopTimeUs;
                float start = startTimeUs / (float) getEditorController().getMovieEditor().getEditorPlayer().getInputTotalTimeUs();
                float stop = (stopTimeUs / (float) getEditorController().getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                mBottomView.mLineView.setLeftBarPosition(start);

                mBottomView.mLineView.setRightBarPosition(Math.min(1f,stop));
                stickerTextItemView.setAlpha(1f);
                stickerTextItemView.setStroke(Color.WHITE, 3);
                stickerTextItemView.setSelected(true);

                double time = (double) (stickerTextData.stopTimeUs - stickerTextData.starTimeUs) / 1000 / 1000.0;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), getEditorController().getActivity().getString(R.string.tip_stickers)) + nf.format(time) + "s";
                mBottomView.mBottomText.setText(mTrimTimeTips);
            }
        }
    }


    /**
     * 设置背景为透明
     */
    private void setAlpha(float alpha) {
        List<StickerItemViewInterface> stickerItemViewInterfaces = mStickerView.getStickerItems();
        for (int i = 0; i < stickerItemViewInterfaces.size(); i++) {
            StickerItemViewInterface stickerItemViewInterface = stickerItemViewInterfaces.get(i);
            if (stickerItemViewInterface instanceof StickerTextItemView) {
                ((StickerTextItemView) stickerItemViewInterface).setAlpha(alpha);
                ((StickerTextItemView) stickerItemViewInterface).setStroke(Color.TRANSPARENT, 3);
                ((StickerTextItemView) stickerItemViewInterface).setSelected(false);

            } else if (stickerItemViewInterface instanceof StickerImageItemView) {
                ((StickerImageItemView) stickerItemViewInterface).setAlpha(alpha);
                ((StickerImageItemView) stickerItemViewInterface).setStroke(Color.TRANSPARENT, 3);
                ((StickerImageItemView) stickerItemViewInterface).setSelected(false);
            } else if (stickerItemViewInterface instanceof StickerDynamicItemView) {
                ((StickerDynamicItemView) stickerItemViewInterface).setAlpha(alpha);
                ((StickerDynamicItemView) stickerItemViewInterface).setStroke(Color.TRANSPARENT, 3);
                ((StickerDynamicItemView) stickerItemViewInterface).setSelected(false);
            }
        }
    }


    /**
     * 备份进入的时候的数据源
     */
    private void backupsTimes() {
        backupsTimesBeans.clear();
        List<StickerItemViewInterface> stickerItemViewInterfaces = mStickerView.getStickerItems();
        for (int i = 0; i < stickerItemViewInterfaces.size(); i++) {
            StickerItemViewInterface stickerItemViewInterface = stickerItemViewInterfaces.get(i);
            StickerData stickerData = stickerItemViewInterface.getStickerData();
            if (stickerItemViewInterface instanceof StickerTextItemView) {
                backupsTimesBeans.put(stickerData.stickerId, new BackupsTimesBean(stickerData.stickerId, ((StickerTextData) stickerData).starTimeUs, ((StickerTextData) stickerData).stopTimeUs));

            } else if (stickerItemViewInterface instanceof StickerImageItemView) {
                backupsTimesBeans.put(stickerData.stickerId, new BackupsTimesBean(stickerData.stickerId, ((StickerImageData) stickerData).starTimeUs, ((StickerImageData) stickerData).stopTimeUs));

            } else if (stickerItemViewInterface instanceof StickerDynamicItemView) {
                StickerDynamicItemView stickerItemViewInterface1 = (StickerDynamicItemView) stickerItemViewInterface;

                backupsTimesBeans.put(stickerItemViewInterface1.getCurrentStickerGroup().getStickerData().stickerId,
                        new BackupsTimesBean(stickerItemViewInterface1.getCurrentStickerGroup().getStickerData().stickerId,
                                stickerItemViewInterface1.getCurrentStickerGroup().starTimeUs,
                                stickerItemViewInterface1.getCurrentStickerGroup().stopTimeUs));

            }
//            if (stickerData instanceof StickerTextData) {
//                backupsTimesBeans.put(stickerData.stickerId, new BackupsTimesBean(stickerData.stickerId, ((StickerTextData) stickerData).starTimeUs, ((StickerTextData) stickerData).stopTimeUs));
//            } else if (stickerData instanceof StickerImageData) {
//                backupsTimesBeans.put(stickerData.stickerId, new BackupsTimesBean(stickerData.stickerId, ((StickerImageData) stickerData).starTimeUs, ((StickerImageData) stickerData).stopTimeUs));
//            }
        }
    }

    public EditorTextBackups getTextBackups() {
        return mTextBackups;
    }


    /**
     * 设置顶部点击事件
     *
     * @return
     */
    public EditorTextComponent setHeadAction() {
        if (mBottomView != null) {
            mBottomView.mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomView.lsqSave();
                }
            });
            mBottomView.mBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomView.close();
                }
            });
        }
        return this;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (getEditorPlayer().isReversing()) {
            mBottomView.mLineView.setPercent(1f);
        } else {
            mBottomView.mLineView.setPercent(0f);
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        getEditorPlayer().seekOutputTimeUs(0);
        if (getEditorPlayer().isReversing()) {
            mBottomView.mLineView.setPercent(1f);
        } else {
            mBottomView.mLineView.setPercent(0f);
        }
    }

    public void backUpDatas() {
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeText);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediEffectDataTypeStickerImage);
        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeDynamicSticker);

        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTextBackups == null) return;
                mTextBackups.onComponentAttach();
            }
        }, 50);

    }

    @Override
    public void detach() {
        mBottomView.setPlayState(1);
        getEditorPlayer().seekTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
        getEditorController().getVideoContentView().setClickable(true);
        if (mTextBackups != null) {
            mTextBackups.onComponentDetach();
        }
        getEditorController().getActivity().getTextStickerView().setVisibility(View.VISIBLE);
        mStickerView.changeOrUpdateStickerType(StickerView.StickerType.Normal);
        mStickerView.setDelegate(getEditorController().getActivity().mStickerDelegate);
        setAlpha(1f);
    }


    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null)
            mBottomView = new EditorTextBottomView();
        return mBottomView.mBottomView;
    }


    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        if (mBottomView != null)
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mBottomView.getLineView().addBitmap(bitmap);
                }
            });


    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        getBottomView();
        if (mBottomView != null)
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mBottomView.getLineView().addFirstFrameBitmap(bitmap);
                }
            });
    }


    //底部View
    class EditorTextBottomView {
        public View mBottomView;

        //返回按钮
        private View mBackBtn;
        //确定按钮
        public View mNextBtn;
        //播放|暂停 按钮
        private ImageView mPlayBtn;
        //标题按钮

        //进度以及区间选择View
        private TuSdkMovieScrollContent mLineView;

        // 底部文字
        private TextView mBottomText;


        public EditorTextBottomView() {
            mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_text_bottom, null);
            initView();
        }


        private void initView() {
            mBackBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_back1);
            mNextBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_save);
            mPlayBtn = mBottomView.findViewById(R.id.lsq_editor_text_play);
            mPlayBtn.setOnClickListener(mOnClickListener);
            mBottomText = mBottomView.findViewById(R.id.bottom_text);
            double time = getMovieEditor().getEditorPlayer().getInputTotalTimeUs() / 1000 / 1000.0;
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(1);
            mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), (mCurrentStickerItemView instanceof StickerTextItemView) ? getEditorController().getActivity().getString(R.string.tip_text) : getEditorController().getActivity().getString(R.string.tip_stickers)) + nf.format(time) + "s";
            mBottomText.setText(mTrimTimeTips);
            mLineView = mBottomView.findViewById(R.id.lsq_editor_text_play_range);
            mLineView.setType(1);
            mLineView.setOutlineType(1);
            mLineView.setShowSelectBar(true);
            mLineView.setNeedShowCursor(true);
            mLineView.layout(mLineView.getLeft(), mLineView.getTop(), mLineView.getRight(), mLineView.getBottom());
            mLineView.setProgressChangeListener(onPlayProgressChangeListener);
            mLineView.setSelectRangeChangedListener(mOnSelectTimeChangeListener);
            mLineView.setOnTouchSelectBarListener(mOnTouchSelectBarlistener);
            mLineView.setOnSelectColorRectListener(mSelectColorListener);
            mLineView.setExceedCriticalValueListener(mOnExceedValueListener);

        }

        //获取底部View
        private View getBottomView() {
            return mBottomView;
        }


        public void close() {
            getEditorController().onBackEvent();
            restData();
        }

        private void restData() {
            List<StickerItemViewInterface> stickerItemViewInterfaces = mStickerView.getStickerItems();
            for (int i = 0; i < stickerItemViewInterfaces.size(); i++) {
                StickerItemViewInterface stickerItemViewInterface = stickerItemViewInterfaces.get(i);
                StickerData stickerData = stickerItemViewInterface.getStickerData();
                // 文本
                if (stickerItemViewInterface instanceof StickerTextItemView) {
                    BackupsTimesBean backupsTimesBean = backupsTimesBeans.get(stickerData.stickerId);
                    ((StickerTextData) stickerData).starTimeUs = backupsTimesBean.getStarTimeUs();
                    ((StickerTextData) stickerData).stopTimeUs = backupsTimesBean.getStopTimeUs();
                } else if (stickerItemViewInterface instanceof StickerImageItemView) {
                    BackupsTimesBean backupsTimesBean = backupsTimesBeans.get(stickerData.stickerId);
                    ((StickerImageData) stickerData).starTimeUs = backupsTimesBean.getStarTimeUs();
                    ((StickerImageData) stickerData).stopTimeUs = backupsTimesBean.getStopTimeUs();
                } else if (stickerItemViewInterface instanceof StickerDynamicItemView) {
                    StickerDynamicData currentStickerGroup = ((StickerDynamicItemView) stickerItemViewInterface).getCurrentStickerGroup();
                    BackupsTimesBean backupsTimesBean = backupsTimesBeans.get(currentStickerGroup.getStickerData().stickerId);
                    currentStickerGroup.starTimeUs = backupsTimesBean.getStarTimeUs();
                    currentStickerGroup.stopTimeUs = backupsTimesBean.getStopTimeUs();
                }
            }
        }

        public void lsqSave() {
            getEditorController().onBackEvent();
        }


        //View的点击事件
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.lsq_editor_text_play) {
                    if (getEditorPlayer().isPause())
                        getEditorPlayer().startPreview();
                    else
                        getEditorPlayer().pausePreview();
                }
            }
        };


        public TuSdkMovieScrollContent getLineView() {
            return mLineView;
        }

        /**
         * 设置播放状态
         *
         * @param state 0 播放  1 暂停
         * @since V3.0.0
         */
        public void setPlayState(int state) {
            mLineView.setShowSelectBar(true);
            if (state == 1) {
                mBottomText.setText(mTrimTimeTips);
                getEditorPlayer().pausePreview();
            } else {
                mBottomText.setText(R.string.text_trim_tip);
                if (mBottomView != null) mLineView.setShowSelectBar(false);
                getEditorPlayer().startPreview();
            }

            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
        }


        /**
         * 两边的Bar移动监听
         */
        private TuSdkRangeSelectionBar.OnSelectRangeChangedListener mOnSelectTimeChangeListener = new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
            @Override
            public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
                isNeedShowCursor = false;
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessageDelayed(0, 200);
                if (type == 0) {
                    mLineView.setPercent(leftPercent);
                } else {
                    mLineView.setPercent(rightPerchent);
                }
                double time = (rightPerchent - leftPercent) * getMovieEditor().getEditorPlayer().getInputTotalTimeUs() / 1000 / 1000.0;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                mTrimTimeTips = String.format(getEditorController().getActivity().getString(R.string.text_trim_select_time), (mCurrentStickerItemView instanceof StickerTextItemView) ? getEditorController().getActivity().getString(R.string.tip_text) : getEditorController().getActivity().getString(R.string.tip_text)) + nf.format(time) + "s";
                mBottomText.setText(mTrimTimeTips);
                if (mCurrentStickerItemView instanceof StickerTextItemView) {
                    StickerTextItemView stickerTextItemView;
                    if (mCurrentStickerItemView != null) {
                        stickerTextItemView = (StickerTextItemView) mCurrentStickerItemView;
                    } else {
                        stickerTextItemView = (StickerTextItemView) mStickerView.getCurrentItemViewSelected();
                    }

                    if (stickerTextItemView != null) {
                        StickerTextData stickerTextData = (StickerTextData) stickerTextItemView.getSticker();
                        if (type == 0) {
                            stickerTextData.starTimeUs = (long) (leftPercent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                        } else if (type == 1) {
                            stickerTextData.stopTimeUs = (long) (rightPerchent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                        }
                    }

                } else if (mCurrentStickerItemView instanceof StickerImageItemView) {
                    StickerImageItemView stickerImageItemView;
                    if (mCurrentStickerItemView != null) {
                        stickerImageItemView = (StickerImageItemView) mCurrentStickerItemView;
                    } else {
                        stickerImageItemView = (StickerImageItemView) mStickerView.getCurrentItemViewSelected();
                    }
                    if (stickerImageItemView != null) {
                        StickerImageData stickerImageData = (StickerImageData) stickerImageItemView.getSticker();
                        if (type == 0) {
                            stickerImageData.starTimeUs = (long) (leftPercent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                        } else if (type == 1) {
                            stickerImageData.stopTimeUs = (long) (rightPerchent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                        }
                    }

                } else if (mCurrentStickerItemView instanceof StickerDynamicItemView) {
                    StickerDynamicItemView StickerItemViewInterface = (StickerDynamicItemView) mCurrentStickerItemView;
                    if (StickerItemViewInterface != null) {
                        StickerDynamicData stickerImageData = StickerItemViewInterface.getCurrentStickerGroup();
                        if (type == 0) {
                            stickerImageData.starTimeUs = (long) (leftPercent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                        } else if (type == 1) {
                            stickerImageData.stopTimeUs = (long) (rightPerchent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs());
                        }
                    }
                }

            }
        };

        /**
         * 播放指针 位置改变监听
         */
        TuSdkMovieScrollContent.OnPlayProgressChangeListener onPlayProgressChangeListener = new TuSdkMovieScrollContent.OnPlayProgressChangeListener() {
            @Override
            public void onProgressChange(float percent) {
                if (!getEditorPlayer().isPause()) {
                    getEditorPlayer().pausePreview();
                }
                getEditorPlayer().seekTimeUs((long) (percent * getMovieEditor().getEditorPlayer().getInputTotalTimeUs()));
            }
        };


        /**
         * 点击左右Bar的监听
         */
        private TuSdkRangeSelectionBar.OnTouchSelectBarListener mOnTouchSelectBarlistener = new TuSdkRangeSelectionBar.OnTouchSelectBarListener() {
            @Override
            public void onTouchBar(float leftPercent, float rightPerchent, int type) {
                if (type == 0) {
                    mLineView.setPercent(leftPercent);
                } else if (type == 1) {
                    mLineView.setPercent(rightPerchent);
                }
            }
        };

        /**
         * 点击色块
         */
        private TuSdkMovieColorGroupView.OnSelectColorRectListener mSelectColorListener = new TuSdkMovieColorGroupView.OnSelectColorRectListener() {
            @Override
            public void onSelectColorRect(final TuSdkMovieColorRectView rectView) {
            }
        };


        /**
         * 最大值最小值判断 (Line)
         **/
        private TuSdkRangeSelectionBar.OnExceedCriticalValueListener mOnExceedValueListener = new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
            @Override
            public void onMaxValueExceed() {
                TuSdk.messageHub().showToast(getBottomView().getContext(), R.string.lsq_max_time_effect);
            }

            @Override
            public void onMinValueExceed() {
                TuSdk.messageHub().showToast(getBottomView().getContext(), R.string.lsq_min_time_effect);
            }
        };
    }


}
