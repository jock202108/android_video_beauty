package org.lasque.twsdkvideo.video_beauty.editor.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.decoder.TuSDKAudioDecoderTaskManager;
import org.lasque.tusdk.core.decoder.TuSDKVideoInfo;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.modules.view.widget.sticker.StickerGroup;
import org.lasque.tusdk.modules.view.widget.sticker.StickerLocalPackage;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerAudioEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaStickerEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.views.CompoundConfigView;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewParams;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewSeekBar;
import org.lasque.twsdkvideo.video_beauty.views.MvRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lasque.tusdk.core.TuSdkContext.getString;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeAudio;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSticker;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeStickerAudio;


public class EditorMVComponent extends EditorComponent {
    /**
     * ???????????????
     **/
    private float mMasterVolume;
    /**
     * ??????????????????
     **/
    private float mOtherVolume;

    /**
     * BottomView
     */
    private View mBottomView;
    /**
     * Mv
     */
    private RecyclerView mMvRecyclerView;
    /**
     * Mv?????????
     */
    private MvRecyclerAdapter mMvRecyclerAdapter;
    /**
     * ?????????
     */
//    private LineView mTimeLineView;
    private TuSdkMovieScrollPlayLineView mPlayLineView;
    /**
     * ??????????????????
     */
    private ImageView ivMvPlayBtn;

    /**
     * MV????????????
     */
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> mMusicMap = new HashMap<Integer, Integer>();
    /**
     * ?????????????????????
     */
    private CompoundConfigView mVoiceVolumeConfigView;
    /**
     * MV??????????????????
     **/
    private final int mMinSelectTimeUs = 1 * 1000000;
    private boolean isSelect = false;

    /**
     * ??????????????????
     *
     * @param editorController
     */
    public EditorMVComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.MV;

        getEditorAudioMixer().addTaskStateListener(mAudioDecoderTask);
        getVoiceVolumeConfigView();
    }

    //???????????????????????????
    private TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener mAudioDecoderTask = new TuSDKAudioDecoderTaskManager.TuSDKAudioDecoderTaskStateListener() {
        @Override
        public void onStateChanged(TuSDKAudioDecoderTaskManager.State state) {
            if (state == TuSDKAudioDecoderTaskManager.State.Complete) {
                // ??????????????????
                if(MovieEditorController.mCurrentComponent instanceof EditorMVComponent){
                    startPreview();
                }
              
            }
        }
    };

    @Override
    public void attach() {
        getEditorController().getVideoContentView().setClickable(false);
        // ??????????????????
        getEditorController().getBottomView().addView(getBottomView());

        // ???????????????????????????
        getEditorController().getPlayBtn().setVisibility(View.GONE);

        // ??????
        pausePreview();


        // ??????????????????
        getEditorController().getMovieEditor().getEditorPlayer().addProgressListener(mPlayerProgressListener);
        isSelect = true;
        // ??????????????????
        if (getEditorController().getMVEffectData() != null) {
            float leftPercent = Float.valueOf(getEditorController().getMVEffectData().getAtTimeRange().getStartTimeUS()) / Float.valueOf(getMovieEditor().getEditorPlayer().getTotalTimeUs());
            mPlayLineView.setLeftBarPosition(leftPercent);

            float rightPercent = Float.valueOf(getEditorController().getMVEffectData().getAtTimeRange().getEndTimeUS()) / Float.valueOf(getMovieEditor().getEditorPlayer().getTotalTimeUs());
            mPlayLineView.setRightBarPosition(rightPercent);

            mSelectEffectData = getEditorController().getMVEffectData();
            mMvRecyclerAdapter.setCurrentPosition(mMementoEffectIndex);
        } else {
            mMvRecyclerAdapter.setCurrentPosition(0);
        }

        mMasterVolume = getEditorController().getMasterVolume();
        mOtherVolume = mMementoOtherVolume;
        setSeekBarProgress(0, mMasterVolume);
        setSeekBarProgress(1, mOtherVolume);
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        // seek??????????????????
        getEditorPlayer().seekOutputTimeUs(0);
        if (getEditorPlayer().isReversing()) {
            mPlayLineView.seekTo(1f);
        } else {
            mPlayLineView.seekTo(0f);
        }
    }

    @Override
    public void detach() {
        pausePreview();
        isSelect = false;
        getEditorPlayer().removeProgressListener(mPlayerProgressListener);
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);
        getEditorController().getVideoContentView().setClickable(true);
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

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mPlayLineView.addBitmap(bitmap);
    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mPlayLineView.addFirstFrameBitmap(bitmap);
    }

    /**
     * ?????????BottomView
     *
     * @return
     */
    private View initBottomView() {
        if (mBottomView == null) {
            View bottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_mv_bottom, null);
            ImageButton ibCoverBack = bottomView.findViewById(R.id.lsq_mv_close);
            ImageButton ibCoverSure = bottomView.findViewById(R.id.lsq_mv_sure);
            ibCoverBack.setOnClickListener(onClickListener);
            ibCoverSure.setOnClickListener(onClickListener);
            mBottomView = bottomView;
            geMvPlayBtn();
            initMvRecyclerView();
            initTimeLineView();
        }
        return mBottomView;
    }

    private ImageView geMvPlayBtn() {
        if (ivMvPlayBtn == null) {
            ivMvPlayBtn = mBottomView.findViewById(R.id.lsq_mv_btn);
            ivMvPlayBtn.setOnClickListener(playOnClickListener);
        }
        return ivMvPlayBtn;
    }

    /**
     * ??????????????????
     */
    private void initTimeLineView() {
        if (mPlayLineView == null) {
            mPlayLineView = mBottomView.findViewById(R.id.lsq_mv_lineView);
            mPlayLineView.setType(1);
            if (getEditorPlayer().getOutputTotalTimeUS() > 0) {
                float minPercent = mMinSelectTimeUs / (float) getEditorPlayer().getOutputTotalTimeUS();
                mPlayLineView.setMinWidth(minPercent);
            }
            mPlayLineView.setSelectRangeChangedListener(new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
                @Override
                public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
                    updateMediaEffectsTimeRange();
                    getEditorPlayer().seekInputTimeUs((long) ((type == 0 ? leftPercent : rightPerchent) * getEditorPlayer().getInputTotalTimeUs()));
                }
            });

            mPlayLineView.setOnProgressChangedListener(new TuSdkMovieScrollView.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(float progress, boolean isTouching) {
                    if (!isTouching) return;
                    if (isTouching) {
                        getEditorPlayer().pausePreview();
                    }

                    if (getEditorPlayer().isPause()) {
                        long seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                        getEditorPlayer().seekInputTimeUs(seekUs);
                    }
                }

                @Override
                public void onCancelSeek() {

                }
            });
        }
    }

    /**
     * ?????????Mv
     */
    private void initMvRecyclerView() {
        mMvRecyclerView = mBottomView.findViewById(R.id.lsq_mv_recyclerView);
        mMvRecyclerView.setLayoutManager(new LinearLayoutManager(getEditorController().getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mMvRecyclerAdapter = new MvRecyclerAdapter();
        mMvRecyclerAdapter.setItemClickListener(mvItemClickListener);
        mMvRecyclerView.setAdapter(mMvRecyclerAdapter);
        mMvRecyclerAdapter.setMvModeList(getMvModeList());
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private CompoundConfigView getVoiceVolumeConfigView() {
        if (mVoiceVolumeConfigView == null) {
            mVoiceVolumeConfigView = (CompoundConfigView) getEditorController().getActivity().findViewById(R.id.lsq_voice_volume_config_view);
            mVoiceVolumeConfigView.setDelegate(mMvVolumeConfigSeekbarDelegate);
            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg(getString("originIntensity"), mMasterVolume);
            params.appendFloatArg(getString("dubbingIntensity"), mOtherVolume);
            mVoiceVolumeConfigView.setCompoundConfigView(params);
            mVoiceVolumeConfigView.showView(false);
        }

        return mVoiceVolumeConfigView;
    }

    /**
     * ??????Mv??????
     *
     * @return
     */
    private List<StickerGroup> getMvModeList() {
        /** ??????????????????Id **/
        mMusicMap.put(1420, R.raw.lsq_audio_cat);
        mMusicMap.put(1427, R.raw.lsq_audio_crow);
        mMusicMap.put(1432, R.raw.lsq_audio_tangyuan);
        mMusicMap.put(1446, R.raw.lsq_audio_children);
        mMusicMap.put(1470, R.raw.lsq_audio_oldmovie);
        mMusicMap.put(1469, R.raw.lsq_audio_relieve);

        List<StickerGroup> groups = new ArrayList<StickerGroup>();
        List<StickerGroup> smartStickerGroups = StickerLocalPackage.shared().getSmartStickerGroups(false);

        for (StickerGroup smartStickerGroup : smartStickerGroups) {
            if (mMusicMap.containsKey((int) smartStickerGroup.groupId))
                groups.add(smartStickerGroup);
        }

        groups.add(0, new StickerGroup());
        return groups;
    }

    /**
     * ?????????????????????????????????
     */
    private ConfigViewSeekBar.ConfigSeekbarDelegate mMvVolumeConfigSeekbarDelegate = new ConfigViewSeekBar.ConfigSeekbarDelegate() {

        @Override
        public void onSeekbarDataChanged(ConfigViewSeekBar seekbar, ConfigViewParams.ConfigViewArg arg) {
            if (arg.getKey().equals("originIntensity")) {
                getEditorController().getMovieEditor().getEditorMixer().setMasterAudioTrack(arg.getPercentValue());
                mMasterVolume = arg.getPercentValue();
            } else if (arg.getKey().equals("dubbingIntensity")) {
                getEditorController().getMovieEditor().getEditorMixer().setSecondAudioTrack(arg.getPercentValue());
                mOtherVolume = arg.getPercentValue();
            }
        }
    };

    /**
     * MV??????Item??????
     */
    private MvRecyclerAdapter.ItemClickListener mvItemClickListener = new MvRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(final int position) {
            isSelect = false;
            if (TuSdkViewHelper.isFastDoubleClick()) return;
            getVoiceVolumeConfigView().setVisibility((position == 0) ? View.GONE : View.VISIBLE);
            mPlayLineView.setShowSelectBar(position > 0);
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    // ?????? MV ??????
                    changeMvEffect(position, mMvRecyclerAdapter.getMvModeList().get(position));
                }
            });
        }
    };

    /**
     * ????????????
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getVoiceVolumeConfigView().setVisibility(View.GONE);
            int id = v.getId();
            if (id == R.id.lsq_mv_close) {
                if (mSelectEffectData != null) {
                    getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeAudio);
                    getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSticker);
                    mMvRecyclerAdapter.setCurrentPosition(0);
                    if (getEditorController().getMediaEffectData() != null) {
                        getEditorEffector().addMediaEffectData(getEditorController().getMediaEffectData());
                    } else {
                        mPlayLineView.setShowSelectBar(false);
                    }
                } else {
                    if (getEditorController().getMediaEffectData() != null) {
                        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeAudio);
                        getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSticker);
                        getEditorEffector().addMediaEffectData(getEditorController().getMediaEffectData());
                    }
                }

                mSelectIndex = -1;
                mSelectEffectData = null;
                getEditorController().onBackEvent();
            } else if (id == R.id.lsq_mv_sure) {
                if (mSelectEffectData != null) {
                    mMementoOtherVolume = mOtherVolume;
                } else {
                    mMementoOtherVolume = 0.5f;
                }

                mMementoEffectIndex = mSelectIndex;
                getEditorController().setMusicEffectData(null);
                getEditorController().setMVEffectData(mSelectEffectData);
                getEditorController().setMasterVolume(mMasterVolume);
                mSelectIndex = -1;
                mSelectEffectData = null;
                getEditorController().onBackEvent();
            }
        }
    };

    // ??????????????????
    private View.OnClickListener playOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isSelect = false;
            if (getEditorController().getMovieEditor().getEditorPlayer().isPause()) {
                startPreview();
            } else {
                pausePreview();
            }
        }
    };

    /**
     * ?????????????????????
     */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayerProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        /**
         *
         * @param state ??????: 0??????, 1??????
         */
        @Override
        public void onStateChanged(int state) {
            geMvPlayBtn().setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
        }

        /**
         *
         * @param playbackTimeUs ??????????????????
         * @param totalTimeUs    ?????????
         * @param percentage     ?????????
         */
        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            if (isSelect) return;
            mPlayLineView.seekTo(percentage);
        }
    };

    /**
     * ??????MV??????
     *
     * @param position
     * @param itemData
     */
    protected void changeMvEffect(int position, StickerGroup itemData) {
        if (position < 0 || mSelectIndex == position) return;

        mSelectIndex = position;


        if (position >= 0) {
            int groupId = (int) itemData.groupId;
            if (position == 0) {
                mSelectEffectData = null;
                getEditorController().getMovieEditor().getEditorEffector().removeMediaEffectsWithType(TuSdkMediaEffectDataTypeStickerAudio);
                getEditorController().getMovieEditor().getEditorMixer().clearAllAudioData();
            }
            if (mMusicMap != null && mMusicMap.containsKey(groupId)) {
                //????????????MV
                Uri uri = Uri.parse("android.resource://" + getEditorController().getActivity().getPackageName() + "/" + mMusicMap.get(groupId));
                TuSdkMediaStickerAudioEffectData stickerAudioEffectDat = new TuSdkMediaStickerAudioEffectData(new TuSdkMediaDataSource(getEditorController().getActivity(), uri), itemData);
                stickerAudioEffectDat.setAtTimeRange(TuSdkTimeRange.makeRange(0, Float.MAX_VALUE));
                stickerAudioEffectDat.getMediaAudioEffectData().getAudioEntry().setLooping(true);
                getEditorController().getMovieEditor().getEditorEffector().addMediaEffectData(stickerAudioEffectDat);
                mSelectEffectData = stickerAudioEffectDat;
            } else {
                //????????????MV
                TuSdkMediaStickerEffectData stickerEffectData = new TuSdkMediaStickerEffectData(itemData);
                stickerEffectData.setAtTimeRange(TuSdkTimeRange.makeRange(0, Float.MAX_VALUE));
                getEditorController().getMovieEditor().getEditorEffector().addMediaEffectData(stickerEffectData);
                mSelectEffectData = stickerEffectData;
            }
        }
    }

    /**
     * ??????????????????
     */
    private void startPreview() {
        //??????????????????
        if (getEditorController().getMovieEditor() == null) return;
        // ???????????????????????????
        updateMediaEffectsTimeRange();
        getEditorController().getMovieEditor().getEditorPlayer().startPreview();
        geMvPlayBtn().setImageBitmap(BitmapFactory.decodeResource(getEditorController().getActivity().getResources(), R.drawable.edit_ic_pause));

        // ???????????????????????????
        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    /**
     * ??????????????????
     */
    private void pausePreview() {
        //??????????????????
        if (getEditorController().getMovieEditor() == null) return;
        getEditorController().getMovieEditor().getEditorPlayer().pausePreview();
        geMvPlayBtn().setImageBitmap(BitmapFactory.decodeResource(getEditorController().getActivity().getResources(), R.drawable.edit_ic_play));
    }

    /**
     * ????????????????????????
     */
    private void updateMediaEffectsTimeRange() {
        TuSDKVideoInfo videoInfo = getEditorController().getMovieEditor().getEditorTransCoder().getOutputVideoInfo();
        if (videoInfo == null) return;

        long startTimeUs = (long) (mPlayLineView.getLeftBarPercent() * videoInfo.durationTimeUs);
        long endTimeUs = (long) (mPlayLineView.getRightBarPercent() * videoInfo.durationTimeUs);

        if (endTimeUs <= startTimeUs) return;

        TuSdkTimeRange timeRange = TuSdkTimeRange.makeTimeUsRange(startTimeUs, endTimeUs);
        if (mSelectEffectData != null && mSelectEffectData.getMediaEffectType().equals(TuSdkMediaEffectDataTypeStickerAudio)){
            mSelectEffectData.setAtTimeRange(timeRange);
            getEditorController().setMVEffectData(mSelectEffectData);
        }
        /** Mv????????????????????? ????????????????????? */
        List<TuSdkMediaEffectData> list = getEditorController().getMovieEditor().getEditorEffector().mediaEffectsWithType(TuSdkMediaEffectDataTypeSticker);

        if (list != null) {
            for (TuSdkMediaEffectData item : list) {
                    item.setAtTimeRange(timeRange);
            }
        }
    }

    private void setSeekBarProgress(int index, float progress) {
        mVoiceVolumeConfigView.getSeekBarList().get(index).setProgress(progress);
    }
}
