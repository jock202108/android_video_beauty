package org.lasque.twsdkvideo.video_beauty.editor.component.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkLinearLayoutManager;
import org.lasque.tusdk.video.editor.TuSdkMediaRepeatTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaReversalTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaSlowTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorEffectComponent;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;
import org.lasque.twsdkvideo.video_beauty.views.TimeRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class TimeEffectFragment extends EffectFragment {
    /**
     * 视频编辑器
     **/
    private TuSdkMovieEditor mMovieEditor;
    /**
     * 正在使用的时间特效
     **/
    private TuSdkMediaTimeEffect mCurrentEffectData;
    /**
     * 备忘时间特效
     **/
    private TuSdkMediaTimeEffect mMementoEffectData;

    /**
     * 当前时间特效的视图
     **/
    private View mTimeView;
    /**
     * 时间特效列表
     **/
    private RecyclerView mTimeRecycle;
    /**
     * 时间特效列表适配器
     **/
    private TimeRecyclerAdapter mTimeAdapter;
    /**
     * 播放进度控件
     **/
    private TuSdkMovieScrollPlayLineView mLineView;
    /**
     * 封面图片列表
     **/
    private List<Bitmap> mBitmapList;
    /**
     * 播放按钮
     **/
//        private ImageView mPlayBtn;
    /**
     * 当前正在使用的时间特效下标
     **/
    private int mCurrentIndex;
    /**
     * 当前备忘上一个时间特效的下标
     **/
    private int mMementoIndex;
    /**
     * 当前视图的输出总时长
     **/
    private long mVideoOutputTotalTimeUs;
    /**
     * 当前时间特效是否正在改变(包括时间范围)
     **/
    private boolean isTimeChanged;

    /**
     * 反复和慢动作最长持续时间
     **/
    private long mMaxEffectTimeUs = 3 * 1000000;
    /**
     * 反复和慢动作最短持续时间
     **/
    private long mMinEffectTimeUs = 1 * 1000000;
    boolean isOnSelect = false;
//    private View rollback_btn;

    /**
     * 是否在快速切换中
     **/
    boolean isFastSwitch = false;

    /**
     * 默认特效持续时间
     **/
    private static long mEffectDurationUs = 2 * 1000000;
    private MovieEditorController movieEditorController;

    public void editorPlayClick(boolean isPause) {
        isOnSelect = false;
        if (isPause) {
            if (isTimeChanged) {
                if (mCurrentEffectData != null) {
                    mMovieEditor.getEditorPlayer().setTimeEffect(mCurrentEffectData);
                }
                isTimeChanged = false;
            }
            // mMovieEditor.getEditorPlayer().startPreview();
        } else {
            //  mMovieEditor.getEditorPlayer().pausePreview();
        }

    }

    public TimeEffectFragment(TuSdkMovieEditor movieEditor, List<Bitmap> bitmapList, MovieEditorController movieEditorController) {
        super(movieEditor);
        this.movieEditorController = movieEditorController;
        this.mMovieEditor = movieEditor;
        this.mBitmapList = bitmapList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTimeView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_time, null);
        mTimeRecycle = mTimeView.findViewById(R.id.lsq_editor_effect_time_list);
//        rollback_btn = mTimeView.findViewById(R.id.rollback_btn);
//        rollback_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mTimeAdapter.setCurrentPosition(-1);
//                rollback_btn.setVisibility(View.INVISIBLE);
//                mLineView.setTimeEffectType(0);
//                mLineView.setShowSelectBar(false);
//                mMovieEditor.getEditorPlayer().clearTimeEffect();
//                mMovieEditor.getEditorPlayer().setTimeEffect(null);
//                mCurrentEffectData = null;
//            }
//        });
        mTimeRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mTimeAdapter = new TimeRecyclerAdapter(getActivity());
        mTimeAdapter.setTimeList(Arrays.asList(Constants.TIME_EFFECT_CODES));
        mTimeRecycle.setAdapter(mTimeAdapter);
        mVideoOutputTotalTimeUs = mMovieEditor.getEditorPlayer().getOutputTotalTimeUS();
        mTimeAdapter.setItemCilckListener(new TimeRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (TuSdkViewHelper.isFastDoubleClick()) return;

                if (position > 0) {
                    int otherEffectNum = movieEditorController.getEffectComponent().getmMagicFragment().getmDataList().size() + movieEditorController.getEffectComponent().getmScreenFragment().getmDataList().size();
                    if (otherEffectNum >= 8) {
                        ToastUtils.showRedToast(getActivity(), getActivity().getResources().getString(R.string.exceed_effects_tip));
                        return;
                    }
                }
                
                if (isFastSwitch) return;
                isFastSwitch = true;
                setTimeEffect(position);
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFastSwitch = false;
                    }
                }, 500);
            }

            private void setTimeEffect(int position) {
                // rollback_btn.setVisibility(View.VISIBLE);
                mTimeAdapter.setCurrentPosition(position);
                mCurrentIndex = position;
                isTimeChanged = true;

                if (!getEditorPlayer().isPause())
                    getEditorPlayer().pausePreview();
                if (position == 0) {
                    mLineView.seekTo(0);
                    mLineView.setTimeEffectType(0);
                    mLineView.setShowSelectBar(false);
                    mMovieEditor.getEditorPlayer().clearTimeEffect();
                    mMovieEditor.getEditorPlayer().setTimeEffect(null);
                    mCurrentEffectData = null;
                } else if (position == 1) {
                    mLineView.seekTo(0);
                    //反复
                    mLineView.setTimeEffectType(0);
                    mLineView.setOutlineType(1);
                    mLineView.setShowSelectBar(true);
                    mLineView.setMaxWidth(mMaxEffectTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                    mLineView.setMinWidth(mMinEffectTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                    TuSdkMediaRepeatTimeEffect repeatTimeEffect = new TuSdkMediaRepeatTimeEffect();
                    TuSdkTimeRange applyTime = getApplyTimeRang();
                    repeatTimeEffect.setTimeRange(applyTime.getStartTimeUS(), applyTime.getEndTimeUS());
                    repeatTimeEffect.setRepeatCount(2);
                    getEditorPlayer().setTimeEffect(repeatTimeEffect);
                    mCurrentEffectData = repeatTimeEffect;
                    getEditorPlayer().seekOutputTimeUs(0);
                } else if (position == 2) {
                    //慢动作
                    mLineView.setTimeEffectType(0);
                    mLineView.setOutlineType(1);
                    mLineView.setShowSelectBar(true);
                    mLineView.setMaxWidth(mMaxEffectTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                    mLineView.setMinWidth(mMinEffectTimeUs / (float) getEditorPlayer().getTotalTimeUs());
                    TuSdkMediaSlowTimeEffect slowTimeEffect = new TuSdkMediaSlowTimeEffect();
                    TuSdkTimeRange applyTime = getApplyTimeRang();
                    slowTimeEffect.setTimeRange(applyTime.getStartTimeUS(), applyTime.getEndTimeUS());
                    slowTimeEffect.setSpeed(0.5f);
                    getEditorPlayer().setTimeEffect(slowTimeEffect);
                    mCurrentEffectData = slowTimeEffect;
                    getEditorPlayer().seekOutputTimeUs(0);

                } else if (position == 3) {
                    //时光倒流
                    mLineView.setOutlineType(2);
                    mLineView.setLeftBarPosition(0);
                    mLineView.setRightBarPosition(1);
                    mLineView.setTimeEffectType(1);
                    mLineView.seekTo(1f);
                    mLineView.setMaxWidth(1);
                    mLineView.setMinWidth(1);
                    TuSdkMediaReversalTimeEffect reversalTimeEffect = new TuSdkMediaReversalTimeEffect();
                    getEditorPlayer().setTimeEffect(reversalTimeEffect);
                    getEditorPlayer().seekOutputTimeUs(getEditorPlayer().getOutputTotalTimeUS());
                    mLineView.setShowSelectBar(true);
                    mCurrentEffectData = reversalTimeEffect;
                }
                setPlayState(1);
            }
        });

        mLineView = mTimeView.findViewById(R.id.lsq_editor_time_play_range);
        mLineView.setOutlineType(1);
        mLineView.setType(1);
        mLineView.setShowSelectBar(false);
        mLineView.setOnProgressChangedListener(mOnScrollingPlayListener);
        mLineView.setOnPlayPointerChangeListener(onPlayProgressChangeListener);
        mLineView.setMaxWidth(mMaxEffectTimeUs / (float) getEditorPlayer().getTotalTimeUs());
        mLineView.setMinWidth(mMinEffectTimeUs / (float) getEditorPlayer().getTotalTimeUs());
        if (mBitmapList != null) {
            for (Bitmap bp : mBitmapList)
                mLineView.addBitmap(bp);
        }
        mLineView.setSelectRangeChangedListener(new TuSdkRangeSelectionBar.OnSelectRangeChangedListener() {
            @Override
            public void onSelectRangeChanged(float leftPercent, float rightPerchent, int type) {
                if (!getEditorPlayer().isPause()) {
                    getEditorPlayer().pausePreview();
                    getEditorPlayer().seekOutputTimeUs(0);
                }

                if (mCurrentEffectData == null) return;
                isTimeChanged = true;
                if (type == 0) {
                    mCurrentEffectData.getTimeRange().setStartTimeUs((long) (getEditorPlayer().getInputTotalTimeUs() * leftPercent));
                } else {
                    mCurrentEffectData.getTimeRange().setEndTimeUs((long) (getEditorPlayer().getInputTotalTimeUs() * rightPerchent));
                }

            }
        });
        mLineView.setExceedCriticalValueListener(new TuSdkRangeSelectionBar.OnExceedCriticalValueListener() {
            @Override
            public void onMaxValueExceed() {
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Integer maxTime = (int) (mMaxEffectTimeUs / 1000000);
                        String tips = String.format(getString(R.string.lsq_max_time_effect_tips), maxTime.toString());
                        TuSdk.messageHub().showToast(getContext(), tips);
                    }
                }, 100);
            }

            @Override
            public void onMinValueExceed() {
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Integer minTime = (int) (mMinEffectTimeUs / 1000000);
                        String tips = String.format(getString(R.string.lsq_min_time_effect_tips), minTime.toString());
                        TuSdk.messageHub().showToast(getContext(), tips);
                    }
                }, 100);
            }
        });

//            mPlayBtn = mTimeView.findViewById(R.id.lsq_editor_time_play);
//            mPlayBtn.setOnClickListener(mOnClickListener);

        return mTimeView;
    }

    public void addCoverBitmap(Bitmap bitmap) {
        if (mLineView == null) return;
        mLineView.addBitmap(bitmap);
    }

    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        if (mLineView != null)
            mLineView.addFirstFrameBitmap(bitmap);
    }

    /**
     * 滚动时 播放位置的回调
     **/
    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {
            if (!isTouching) return;
            if (isTouching)
                getEditorPlayer().pausePreview();
            if (getEditorPlayer().isPause()) {
                long seekUs;
                if (getEditorPlayer().isReversing()) {
                    seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                    getEditorPlayer().seekOutputTimeUs(seekUs);
                } else {
                    seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                    getEditorPlayer().seekInputTimeUs(seekUs);
                }

            }
        }

        @Override
        public void onCancelSeek() {

        }
    };

    private TuSdkMovieScrollContent.OnPlayProgressChangeListener onPlayProgressChangeListener = new TuSdkMovieScrollContent.OnPlayProgressChangeListener() {
        @Override
        public void onProgressChange(float progress) {
//                if (!isTouching) return;
//                if (isTouching) getEditorPlayer().pausePreview();

            long seekUs;
            if (getEditorPlayer().isReversing()) {
                seekUs = (long) ((1 - progress) * getEditorPlayer().getInputTotalTimeUs());
                getEditorPlayer().seekOutputTimeUs(seekUs);
            } else {
                seekUs = (long) (getEditorPlayer().getInputTotalTimeUs() * progress);
                getEditorPlayer().seekInputTimeUs(seekUs);

            }
            mLineView.seekTo(progress);
        }
    };

//        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.lsq_editor_time_play) {//应用时间特效并且备忘
//                    isOnSelect = false;
//                    if (mMovieEditor.getEditorPlayer().isPause()) {
//                        if (isTimeChanged) {
//                            if (mCurrentEffectData != null) {
//                                mMovieEditor.getEditorPlayer().setTimeEffect(mCurrentEffectData);
//                            }
//                            isTimeChanged = false;
//                        }
//                        mMovieEditor.getEditorPlayer().startPreview();
//                    } else {
//                        mMovieEditor.getEditorPlayer().pausePreview();
//                    }
//                }
//            }
//        };

    /**
     * 获取应用特效的时间区间
     *
     * @return 时间特效应用的时间区间
     */
    public TuSdkTimeRange getApplyTimeRang() {
        TuSdkTimeRange timeRange = new TuSdkTimeRange();
//            long starTimeUs = mMovieEditor.getEditorPlayer().getCurrentInputTimeUs();
        long starTimeUs = (long) (mLineView.getCurrentPercent() * getEditorPlayer().getInputTotalTimeUs());
        if (starTimeUs > (mMovieEditor.getEditorPlayer().getInputTotalTimeUs() - mEffectDurationUs)) {
            starTimeUs = mMovieEditor.getEditorPlayer().getInputTotalTimeUs() - mEffectDurationUs;
        }
        getEditorPlayer().seekInputTimeUs(0);
//            mLineView.seekTo(starTimeUs/(float)getEditorPlayer().getTotalTimeUs());
//            mLineView.seekTo(0f);
        timeRange.setStartTimeUs(starTimeUs);
        long endTimeUs = timeRange.getStartTimeUS() + mEffectDurationUs;
        if (endTimeUs > mVideoOutputTotalTimeUs) {
            endTimeUs = mVideoOutputTotalTimeUs;
        }
        timeRange.setEndTimeUs(endTimeUs);
        mLineView.setLeftBarPosition(timeRange.getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
        mLineView.setRightBarPosition(timeRange.getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
        return timeRange;
    }

    /**
     * 设置播放状态
     *
     * @param state 0 播放  1 暂停
     * @since V3.0.0
     */
    public void setPlayState(int state) {

//            if (mPlayBtn == null) return;
//            mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
    }

    @Override
    public void attach() {
        super.attach();
        mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
        if (mLineView != null)
            mLineView.seekTo(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
        if (mTimeAdapter != null)
            mTimeAdapter.setCurrentPosition(mMementoIndex);
        mCurrentEffectData = mMementoEffectData;
    }

    @Override
    public void onSelected() {
        super.onSelected();
        isOnSelect = true;
        boolean isReverse = mMovieEditor.getEditorPlayer().isReversing();
        mMovieEditor.getEditorPlayer().pausePreview();
        mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
        mLineView.seekTo(isReverse ? 1f : 0f);

        //  mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void back() {
        super.back();
        if (mMementoEffectData == null) {
            mLineView.setTimeEffectType(0);
            mMovieEditor.getEditorPlayer().clearTimeEffect();
            mMovieEditor.getEditorPlayer().setTimeEffect(null);
            mLineView.setShowSelectBar(false);
        } else {
            mMovieEditor.getEditorPlayer().setTimeEffect(mMementoEffectData);
            mTimeAdapter.setCurrentPosition(mMementoIndex);
            mLineView.setShowSelectBar(true);
            mLineView.setLeftBarPosition(mMementoEffectData.getTimeRange().getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
            mLineView.setRightBarPosition(mMementoEffectData.getTimeRange().getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
        }
        mCurrentEffectData = null;
        getEditorPlayer().seekTimeUs(0);
    }

    @Override
    public void next() {
        super.next();
        mMementoEffectData = mCurrentEffectData;
        mMementoIndex = mCurrentIndex;
        getEditorPlayer().setTimeEffect(mCurrentEffectData);
        mCurrentEffectData = null;
        getEditorPlayer().seekTimeUs(0);
    }

    public void cleanEffect() {
        if (mMementoEffectData != null && mLineView != null) {
            mMementoEffectData = null;
            mLineView.setTimeEffectType(0);
            mMovieEditor.getEditorPlayer().clearTimeEffect();
            mMovieEditor.getEditorPlayer().setTimeEffect(null);
            mLineView.setShowSelectBar(false);
        }

    }

    public boolean hasEffects() {
        return mCurrentEffectData != null;
    }

    /**
     * 播放器状态改变
     **/
    public void onStateChanged(int state) {
        if (state != 0) getEditorPlayer().setTimeEffect(mCurrentEffectData);
    }

    /**
     * 播放控件移动到指定的进度
     **/
    public void moveToPercent(float percent) {
        if (mLineView != null && !isOnSelect) {
            mLineView.seekTo(percent);
        }
    }

    public void updataApplayTimeEffect() {
        if (mCurrentEffectData == null) return;
        mMovieEditor.getEditorPlayer().setTimeEffect(mCurrentEffectData);
    }

    public void showTips(String tips) {
        Toast toast = Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public TuSdkMediaTimeEffect getmCurrentEffectData() {
        return mCurrentEffectData;
    }
}