package org.lasque.twsdkvideo.video_beauty.editor.component.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkLinearLayoutManager;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaRepeatTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaReversalTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaSlowTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorEffectComponent;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;
import org.lasque.twsdkvideo.video_beauty.views.MagicRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.SceneRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.TimeRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 场景特效Fragment
 */

public  class SceneEffectFragment extends EffectFragment {
    /**
     * 判断为按压的最短触摸时间
     **/
    private static final int MIN_PRESS_DURATION_MILLIS = 150;
    /**
     * 视频编辑器
     **/
    private TuSdkMovieEditor mMovieEditor;
    /**
     * 当前正在应用的场景特效数据
     **/
    private TuSdkMediaSceneEffectData mediaSceneEffectData;
    /**
     * 当前应用的场景数据列表
     **/
    private LinkedList<TuSdkMediaSceneEffectData> mDataList;
    /**
     * 当前应用的场景数据备忘列表
     **/
    private LinkedList<TuSdkMediaSceneEffectData> mMementoList;
    private Handler mHandler = new Handler();
    /**
     * 场景特效Framgent的视图
     **/
    private View mSceneView;
    /**
     * 当前场景特效组件的播放按钮
     **/
//        private View mPlayBtn;
    /**
     * 场景特效列表
     **/
    private RecyclerView mSceneRecycle;
    /**
     * 场景特效列表适配器
     **/
    private SceneRecyclerAdapter mSceneAdapter;
    /**
     * 播放控件
     **/
    private TuSdkMovieScrollPlayLineView mLineView;

    private View rollbackBtn;

    /**
     * 视频封面Bitmap列表
     **/
    private List<Bitmap> mBitmapList;
    /**
     * 当前正在应用的场景特效Code
     **/
    public volatile String mSceneCode;
    /**
     * 当前应用场景特效的开始时间
     **/
    public long mStartTimeUs;
    /**
     * 当前是否允许绘制特效色块
     **/
    public boolean mDrawColorState = false;
    /**
     * 当前组件是否被选择
     **/
    boolean isOnSelect = false;
    private boolean isContinue = true;

    private float prePercent = 0;
    private MovieEditorController movieEditorController;
    private  SceneRecyclerAdapter.SceneViewHolder curSceneViewHolder;
    boolean isTouching = false;
    public void editorPlayClick(boolean isPause) {
        isOnSelect = false;
        if (isPause) {
            isContinue = true;
        }
    }
    public void onPageScrolled(){
        if(curSceneViewHolder!=null){
            onReleaseSceneEffect(curSceneViewHolder);
        }
    }

    private void onReleaseSceneEffect(SceneRecyclerAdapter.SceneViewHolder sceneViewHolder) {
        //手抬起来之后
        isTouching = false;
        mHandler.removeMessages(0);
        sceneViewHolder.effectStroke.setSelected(false);
        mMovieEditor.getEditorPlayer().pausePreview();
        //  sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
    }

    private SceneRecyclerAdapter.OnItemTouchListener mOnItemTouchListener = new SceneRecyclerAdapter.OnItemTouchListener() {
        /** 当前触摸的持续时间 **/
        long duration = 0;


        /**
         * @param event
         * @param position
         * @param sceneViewHolder
         */
        @Override
        public void onItemTouch(MotionEvent event, final int position, final SceneRecyclerAdapter.SceneViewHolder sceneViewHolder) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isTouching){
                                return;
                            }
                            if(sceneViewHolder.itemView.getParent() != null){
                                sceneViewHolder.itemView.getParent().requestDisallowInterceptTouchEvent(true);
                            }

                            onPressSceneEffect(sceneViewHolder, position);
                        }
                    },150);

                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:

                    onReleaseSceneEffect(sceneViewHolder);
                    break;
            }
        }


        private void onPressSceneEffect(SceneRecyclerAdapter.SceneViewHolder sceneViewHolder, int position) {
             boolean isRepeat = false;
            for (int i = 0; i < mDataList.size(); i++) {
                if(mDataList.get(i).getEffectCode().equals(mSceneAdapter.getSceneCode(position))){
                    isRepeat = true;
                    break;
                }
            }

            if(mDataList.size()>=5&&!isRepeat){
                ToastUtils.showRedToast(getActivity(), getActivity().getResources().getString(R.string.exceed_move_effects_tip));
                return;
            }

            //获取场景的特效数量
            int  otherEffectNum  =  movieEditorController.getEffectComponent().getmMagicFragment().getmDataList().size();
            if(movieEditorController.getEffectComponent().getmTimeFragment().getmCurrentEffectData()!=null){
                otherEffectNum++;
            }

            if(mDataList.size()+otherEffectNum>=8){
                ToastUtils.showRedToast(getActivity(), getActivity().getResources().getString(R.string.exceed_effects_tip));
                return;
            }



            curSceneViewHolder = sceneViewHolder;
            sceneViewHolder.effectStroke.setSelected(true);
            TLog.e("outputTimeUs %s", getEditorPlayer().getOutputTotalTimeUS());

            /** 开始播放视频并预览已设置的特效 */
            if (getEditorPlayer().getCurrentOutputTimeUs() >= getEditorPlayer().getOutputTotalTimeUS()) {
               // sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            /** 倒序情况下特效添加到头则返回 **/
            if (getEditorPlayer().isReversing() && (0 == currentPercent)) {
             //   sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            if (mLineView.getCurrentPercent() == 1 && !getEditorPlayer().isReversing()) {
              //  sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            if (getEditorPlayer().getCurrentTimeUs() >= getEditorPlayer().getTotalTimeUs() && !getEditorPlayer().isReversing()) {
             //   sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_color_transparent"));
                return;
            }

            if (!mMovieEditor.getEditorPlayer().isPause()) {
                mMovieEditor.getEditorPlayer().pausePreview();
                return;
            }

            mSceneCode = mSceneAdapter.getSceneCode(position);


            long totalUs = getEditorPlayer().getTotalTimeUs();
            float percent = mLineView.getCurrentPercent();
            mStartTimeUs = (long) (totalUs * percent);
            mMovieEditor.getEditorPlayer().startPreview();
            mediaSceneEffectData = new TuSdkMediaSceneEffectData(mSceneCode);
            //设置ViewModel
            if (mMovieEditor.getEditorPlayer().isReversing()) {
                mediaSceneEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(0, mStartTimeUs));
            } else {
                mediaSceneEffectData.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs, Long.MAX_VALUE));
            }
            // 预览场景特效
            mMovieEditor.getEditorEffector().addMediaEffectData(mediaSceneEffectData);
            mLineView.endAddColorRect();
            mLineView.addColorRect(TuSdkContext.getColor("lsq_scence_effect_color_" + mSceneCode));
            mDataList.add(mediaSceneEffectData);
            isOnSelect = false;
       //     sceneViewHolder.mSelectLayout.setImageResource(TuSdkContext.getColorResId("lsq_scence_effect_color_" + mSceneCode));
            if (rollbackBtn.getVisibility() == View.INVISIBLE) {
                //  mSceneAdapter.setCanDeleted(true);
                rollbackBtn.setVisibility(View.VISIBLE);
                //  mSceneAdapter.notifyItemChanged(0);
            }
        }



    };


    public SceneEffectFragment(final TuSdkMovieEditor movieEditor, final List<Bitmap> bitmaps, MovieEditorController movieEditorController) {
        super(movieEditor);
        this.movieEditorController = movieEditorController;
        this.mMovieEditor = movieEditor;
        this.mBitmapList = bitmaps;
        mDataList = new LinkedList<>();
        mMementoList = new LinkedList<>();
        mSceneAdapter = new SceneRecyclerAdapter(movieEditorController.getActivity());
        mSceneAdapter.setOnItemTouchListener(mOnItemTouchListener);
//            mSceneAdapter.setItemCilckListener(new SceneRecyclerAdapter.ItemClickListener() {
//                @Override
//                public void onItemClick(int position) {
//                    if (position != 0 || mDataList.size() == 0 || mMovieEditor == null || !mSceneAdapter.isCanDeleted())
//                        return;
//
//
//                    mSceneAdapter.setCanDeleted(isCanDeleted);
//                    mSceneAdapter.notifyItemChanged(0);
//                }
//            });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSceneView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_scene, null);
        mSceneRecycle = mSceneView.findViewById(R.id.lsq_editor_effect_scene_list);
        mSceneRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mSceneRecycle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    onPageScrolled();
                }
                return false;
            }
        });

        mSceneRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onPageScrolled();
            }
        });

        mSceneRecycle.setAdapter(mSceneAdapter);
        mSceneAdapter.setSceneList(Arrays.asList(Constants.SCENE_EFFECT_CODES));

        mLineView = mSceneView.findViewById(R.id.lsq_editor_scene_play_range);
        mLineView.setType(0);
        mLineView.setOnProgressChangedListener(mOnScrollingPlayListener);
        mLineView.setOnPlayPointerChangeListener(onPlayProgressChangeListener);
        mLineView.setOnBackListener(new TuSdkMovieScrollView.OnColorGotoBackListener() {
            @Override
            public void onGotoBack(float percent) {
                prePercent = percent;
            }
        });
        if (mBitmapList != null) {
            for (Bitmap bp : mBitmapList)
                mLineView.addBitmap(bp);
        }

//               mPlayBtn = mSceneView.findViewById(R.id.lsq_editor_scene_play);
//             mPlayBtn.setOnClickListener(mOnClickListener);
        rollbackBtn = mSceneView.findViewById(R.id.rollback_btn);

        rollbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDataList.size() == 0 || mMovieEditor == null)
                    return;
                isContinue = false;
                setPlayState(1);
                mLineView.endAddColorRect();
                mLineView.deletedColorRect();
                mMovieEditor.getEditorPlayer().pausePreview();
                TuSdkMediaEffectData mediaEffectData = mDataList.removeLast();
                mMovieEditor.getEditorEffector().removeMediaEffectData(mediaEffectData);
                if (mMovieEditor.getEditorPlayer().isReversing()) {
                    mLineView.seekTo((mediaEffectData.getAtTimeRange().getEndTimeUS()) / (float) getEditorPlayer().getTotalTimeUs());
                    mMovieEditor.getEditorPlayer().seekInputTimeUs(mediaEffectData.getAtTimeRange().getEndTimeUS());
                } else {
                    mLineView.seekTo((mediaEffectData.getAtTimeRange().getStartTimeUS()) / (float) getEditorPlayer().getTotalTimeUs());
                    mMovieEditor.getEditorPlayer().seekInputTimeUs(mediaEffectData.getAtTimeRange().getStartTimeUS());
                }
                boolean isCanDeleted = mDataList.size() > 0;
                if (isCanDeleted) {
                    rollbackBtn.setVisibility(View.VISIBLE);
                } else {
                    rollbackBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        mLineView.setCursorMinPercent(movieEditorController.getmCurrentLeftPercent());
        mLineView.setCursorMaxPercent(movieEditorController.getmCurrentRightPercent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLineView.seekTo(getEditorPlayer().isReversing() ? movieEditorController.getmCurrentRightPercent() : movieEditorController.getmCurrentLeftPercent());
            }
        },100);


        return mSceneView;
    }

    public void addCoverBitmap(Bitmap bitmap) {
        if (mLineView != null)
            mLineView.addBitmap(bitmap);
    }

    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        if (mLineView != null)
            mLineView.addFirstFrameBitmap(bitmap);
    }

//        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (v.getId() == R.id.lsq_editor_scene_play) {
//                    isOnSelect = false;
//                    if (mMovieEditor.getEditorPlayer().isPause()) {
//                        mMovieEditor.getEditorPlayer().startPreview();
//                        isContinue = true;
//                    } else
//                        mMovieEditor.getEditorPlayer().pausePreview();
//                }
//            }
//        };

    /**
     * 设置播放状态
     *
     * @param state 0 播放  1 暂停
     * @since V3.0.0
     */
    public void setPlayState(int state) {
        if (MovieEditorController.mCurrentComponent instanceof EditorEffectComponent) {
            if (state == 0) {
                movieEditorController.getPlayBtn().setVisibility(View.GONE);
            } else {

                movieEditorController.getPlayBtn().setVisibility(View.VISIBLE);
            }
        }
        //    if (mPlayBtn == null) return;
        //   mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(state == 0 ? R.drawable.edit_ic_pause : R.drawable.edit_ic_play));
    }

    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {
            currentPercent = progress;
            if (!isTouching) return;
            if (isTouching)
                getEditorPlayer().pausePreview();
            long current = (long) (progress * getEditorPlayer().getTotalTimeUs());

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
            //纠偏
            float progress_1 = getEditorPlayer().getCurrentTimeUs() / (float) getEditorPlayer().getTotalTimeUs();
            mLineView.seekTo(progress_1);
        }
    };

    private TuSdkMovieScrollContent.OnPlayProgressChangeListener onPlayProgressChangeListener = new TuSdkMovieScrollContent.OnPlayProgressChangeListener() {
        @Override
        public void onProgressChange(float progress) {
            currentPercent = progress;
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


    @Override
    public void attach() {
        super.attach();
        //恢复之前的情况
        if (mMementoList.size() == 0) {
            if (rollbackBtn != null) {
                rollbackBtn.setVisibility(View.INVISIBLE);
            }
            //   mSceneAdapter.notifyItemChanged(0);
        } else {
            if (rollbackBtn != null) {
                rollbackBtn.setVisibility(View.VISIBLE);
            }
            //    mSceneAdapter.notifyItemChanged(0);
        }

        for (TuSdkMediaSceneEffectData item : mMementoList) {
            recoveryEffect(item);
        }
        if (mLineView != null) {
            //            mLineView.seekTo(getEditorPlayer().isReversing() ? 1 : 0);
            mLineView.seekTo(getEditorPlayer().isReversing() ? movieEditorController.getmCurrentRightPercent() : movieEditorController.getmCurrentLeftPercent());
            mLineView.setTimeEffectType(getEditorPlayer().isReversing() ? 1 : 0);
            mLineView.setCursorMinPercent(movieEditorController.getmCurrentLeftPercent());
            mLineView.setCursorMaxPercent(movieEditorController.getmCurrentRightPercent());
            mLineView.setPercent(movieEditorController.getmCurrentLeftPercent());
            //todo seekOutputTimeUs seekInputTimeUs ??


        }
        setPlayState(1);
    }


    @Override
    public void onSelected() {
        super.onSelected();
        isOnSelect = true;
        boolean isReverse = mMovieEditor.getEditorPlayer().isReversing();
        mMovieEditor.getEditorPlayer().pausePreview();
        mMovieEditor.getEditorPlayer().seekOutputTimeUs(0);
//        mLineView.seekTo(isReverse ? 1f : 0f);
        mLineView.seekTo(getEditorPlayer().isReversing() ? movieEditorController.getmCurrentRightPercent() : movieEditorController.getmCurrentLeftPercent());

        if (mLineView != null)
            mLineView.setTimeEffectType(mMovieEditor.getEditorPlayer().isReversing() ? 1 : 0);
        //   mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));

    }

    /**
     * 恢复之前应用的特效
     **/
    private void recoveryEffect(TuSdkMediaSceneEffectData mediaEffectData) {
        String sceneCode = mediaEffectData.getEffectCode();
        float startPercent = mediaEffectData.getAtTimeRange().getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs();
        float endPercent = mediaEffectData.getAtTimeRange().getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs();
        mLineView.recoverColorRect(TuSdkContext.getColor("lsq_scence_effect_color_" + sceneCode), startPercent, endPercent);
        mDataList.add(mediaEffectData);
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void back() {
        super.back();
        for (TuSdkMediaEffectData item : mDataList) {
            mMovieEditor.getEditorEffector().removeMediaEffectData(item);
            mLineView.deletedColorRect();
        }

        for (TuSdkMediaEffectData item : mMementoList) {
            mMovieEditor.getEditorEffector().addMediaEffectData(item);
        }

        mDataList.clear();
    }

    @Override
    public void next() {
        super.next();
        mMementoList.clear();
        mMementoList.addAll(mDataList);
        mDataList.clear();
        mLineView.clearAllColorRect();
    }

    public void cleanEffect(){
        mMementoList.clear();
        if(mLineView!=null){
            mLineView.clearAllColorRect();
        }

    }

    public boolean hasEffects(){
        return  mDataList!= null && mDataList.size()!= 0;
    }


    /**
     * 同步播放器的播放状态
     **/
    public void onPlayerStateChanged(int state) {

        if (mediaSceneEffectData != null) {
            //暂停
            if (state == 1) {
                if (mediaSceneEffectData != null) {
                    TuSdkTimeRange timeRange = mediaSceneEffectData.getAtTimeRange();
                    if (mMovieEditor.getEditorPlayer().isReversing()) {
                        timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
                    } else {
                        timeRange.setEndTimeUs(getEditorPlayer().getCurrentTimeUs());
//                        mLineView.seekTo(mMovieEditor.getEditorPlayer().getCurrentInputTimeUs() / (float) getEditorPlayer().getTotalTimeUs());
//                            if (prePercent < 1 &&  timeRange.getEndTimeUS() < prePercent * getEditorPlayer().getTotalTimeUs()) {
//                                timeRange.setEndTimeUs((long) (prePercent * getEditorPlayer().getTotalTimeUs()));
//                            }
                    }
                    mediaSceneEffectData.setAtTimeRange(timeRange);
                }
                mDrawColorState = false;
                mLineView.endAddColorRect();
                mediaSceneEffectData = null;

            } else {
                mDrawColorState = true;
                isContinue = false;
            }
        }


    }

    /**
     * 更新进度
     **/
    private boolean isPreState = true;
    private float currentPercent;

    /**
     * 移动进度到播放视图
     **/
    public void moveToPercent(float percentage, long playbackTimeUs) {
        currentPercent = percentage;
        if (mLineView != null) {
            if (isOnSelect) return;
            mLineView.seekTo(percentage);
            if (mDrawColorState) {
                isPreState = mDrawColorState;
            } else {
                if (isPreState) {
                    isPreState = false;
                    return;
                }
            }

        }
    }

    public LinkedList<TuSdkMediaSceneEffectData> getmDataList() {
        return mDataList;
    }
}
