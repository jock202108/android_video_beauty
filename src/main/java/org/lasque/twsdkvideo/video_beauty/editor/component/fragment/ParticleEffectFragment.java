package org.lasque.twsdkvideo.video_beauty.editor.component.fragment;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.view.recyclerview.TuSdkLinearLayoutManager;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaParticleEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorEffectComponent;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewParams;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewSeekBar;
import org.lasque.twsdkvideo.video_beauty.views.MagicRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.SceneRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.color.ColorView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static org.lasque.twsdkvideo.video_beauty.utils.Constants.PARTICLE_CODES;

/**
 * 魔法特效
 */
public class ParticleEffectFragment extends EffectFragment {
    /**
     * 当前正在使用的魔法效果
     **/
    private volatile TuSdkMediaParticleEffectData mCurrentParticleEffectModel;
    private Handler mHandler = new MovieEditorActivity.MyHandler(getActivity());
    /**
     * 判断为按压的最短触摸时间
     **/
    private static final int MIN_PRESS_DURATION_MILLIS = 150;
    /**
     * 当前应用的魔法效果列表
     **/
    private LinkedList<TuSdkMediaParticleEffectData> mDataList;
    /**
     * 上次应用过后的魔法效果备忘列表
     **/
    private LinkedList<TuSdkMediaParticleEffectData> mMementoList;
    private  MagicRecyclerAdapter.MagicViewHolder curMagicViewHolder;
    /**
     * 魔法特效Framgent视图
     **/
    private View mParticleView;
    /**
     * 魔法特效列表
     **/
    private RecyclerView mParticleRecycle;
    /**
     * 魔法特效列表适配器
     **/
    private MagicRecyclerAdapter mParticleAdapter;
    /**
     * 播放进度视图
     **/
//        private LineView mLineView;
    private TuSdkMovieScrollPlayLineView mLineView;
    /**
     * 播放按钮
     **/
//        private ImageView mPlayBtn;
    /**
     * 魔法效果触摸视图
     **/
    private RelativeLayout mParticleContent;
    /**
     * 魔法效果设置视图 ( 大小、颜色 )
     **/
    public EditorEffectComponent.ParticleConfigView mParticleConfig;
    private boolean isContinue = true;
    /**
     * 当前使用的魔法效果下标
     **/
    private int mCurrentIndex;
    /**
     * 上次应用的魔法效果下标
     **/
    private int mMementoIndex;
    /**
     * 当前魔法效果的Code
     **/
    private String mCurrentParticleCode;
    private boolean mDrawColorState;
    /**
     * 应用特效的开始时间
     **/
    private long mStartTimeUs;
    /**
     * 视频封面列表
     **/
    private List<Bitmap> mBitmapList;
    private View rollback_btn;
    /**
     * 是否正在切换过程中
     **/
    boolean isFirstSelect = false;
    /**
     * 上一个进度
     **/
    private float prePercent;
    private MovieEditorController movieEditorController;

    public void editorPlayClick(boolean isPause) {
        isFirstSelect = false;
        if (isPause) {
            isContinue = true;
        }
    }


    public void cleanEffect(){
        mMementoList.clear();
        if(mLineView!=null){
            mLineView.clearAllColorRect();
        }

    }


    public boolean hasEffects(){
        return  mDataList!=null && mDataList.size() != 0;
    }



    public ParticleEffectFragment(TuSdkMovieEditor movieEditor, RelativeLayout magicContent, EditorEffectComponent.ParticleConfigView magicConfig, List<Bitmap> bitmaps, MovieEditorController movieEditorController) {
        super(movieEditor);
        this.mBitmapList = bitmaps;
        this.mParticleContent = magicContent;
        this.movieEditorController = movieEditorController;
        mDataList = new LinkedList<>();
        mMementoList = new LinkedList<>();
        mParticleAdapter = new MagicRecyclerAdapter(movieEditorController.getActivity());
        //  mParticleContent.setOnTouchListener(mOnParticleTouchListener);
        mParticleConfig = magicConfig;
        mParticleAdapter.setOnItemTouchListener(mOnItemTouchListener);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }


    public void onPageScrolled(){
        if(curMagicViewHolder!=null){
            onReleaseMagicEffect(curMagicViewHolder);
        }
    }

    private void onReleaseMagicEffect(MagicRecyclerAdapter.MagicViewHolder magicViewHolder) {
        isTouching = false;
        mHandler.removeCallbacksAndMessages(null);
        magicViewHolder.effectStroke.setSelected(false);
        // 取消预览魔法特效
        if (mCurrentParticleEffectModel == null) {
            return;
        }

        TuSdkTimeRange timeRange = mCurrentParticleEffectModel.getAtTimeRange();

        if (getEditorPlayer().isReversing()) {
            timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
        } else {
            timeRange.setEndTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
        }
        mCurrentParticleEffectModel.setAtTimeRange(timeRange);

        mCurrentParticleEffectModel = null;
        getEditorPlayer().pausePreview();
        mDrawColorState = false;
        mLineView.endAddColorRect();
    }
    boolean isTouching = false;
    private MagicRecyclerAdapter.OnItemTouchListener mOnItemTouchListener = new MagicRecyclerAdapter.OnItemTouchListener() {
        /** 当前触摸的持续时间 **/


        /**
         * @param event
         * @param position
         * @param
         */
        @Override
        public void onItemTouch(MotionEvent event, final int position, final MagicRecyclerAdapter.MagicViewHolder magicViewHolder) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isTouching){
                                return;
                            }
                            magicViewHolder.itemView.getParent().requestDisallowInterceptTouchEvent(true);
                            onPressMagicEffect(magicViewHolder, position);
                        }
                    },150);



                    break;
                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP:
                    Log.e("sfsfsfsfsfsfdsfsfsd","ACTION_UP");
                    isFirstSelect=true;
                    isTouching = false;
                    onReleaseMagicEffect(magicViewHolder);

                    break;
            }
        }





        private void onPressMagicEffect(MagicRecyclerAdapter.MagicViewHolder magicViewHolder, int positon) {
            boolean isRepeat = false;
            for (int i = 0; i < mDataList.size(); i++) {
                if(mDataList.get(i).getParticleCode().equals(mParticleAdapter.getParticleCode(positon))){
                    isRepeat = true;
                    break;
                }
            }


            if(mDataList.size()>=5&&!isRepeat){
                ToastUtils.showRedToast(getActivity(), getActivity().getResources().getString(R.string.exceed_visual_effects_tip));
                return;
            }
            //获取场景的特效数量
            int  otherEffectNum  =  movieEditorController.getEffectComponent().getmScreenFragment().getmDataList().size();
            if(movieEditorController.getEffectComponent().getmTimeFragment().getmCurrentEffectData()!=null){
                otherEffectNum++;
            }
            if(mDataList.size()+otherEffectNum>=8){
                ToastUtils.showRedToast(getActivity(), getActivity().getResources().getString(R.string.exceed_effects_tip));
                return;
            }




            isTouching = true;
            curMagicViewHolder = magicViewHolder;
            magicViewHolder.effectStroke.setSelected(true);
            isFirstSelect=false;
            /** 开始播放视频并预览已设置的特效 */
            if (getEditorPlayer().getCurrentOutputTimeUs() >= getEditorPlayer().getOutputTotalTimeUS() && getEditorPlayer().getOutputTotalTimeUS() > 0) {
                return;
            }

            /** 倒序情况下特效添加到头则返回 **/
            if (getEditorPlayer().isReversing() && (0 == currentPercent)) {
                return;
            }
            if (mLineView.getCurrentPercent() == 1 && !getEditorPlayer().isReversing() || getEditorPlayer().getCurrentTimeUs() == getEditorPlayer().getTotalTimeUs() && !getEditorPlayer().isReversing()) {
                return;
            }

            if (getEditorPlayer().getCurrentTimeUs() >= getEditorPlayer().getTotalTimeUs() && !getEditorPlayer().isReversing()) {
                return;
            }
            TLog.d("Current Time Us %s preTimeUs %s Current Percent %s", getEditorPlayer().getCurrentTimeUs(), mPreTimeUs, mLineView.getCurrentPercent());
            if (mPreTimeUs == getEditorPlayer().getCurrentTimeUs() && mPreTimeUs != 0) {
                mOutCurrentFrameCount++;
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                float currentPercent = Float.valueOf(decimalFormat.format(mLineView.getCurrentPercent()));
                if (mOutCurrentFrameCount > 4 || currentPercent >= 0.99) {
                    return;
                }
            } else {
                mOutCurrentFrameCount = 0;
            }
            mPreTimeUs = getEditorPlayer().getCurrentTimeUs();


            PointF pointF = null;
            if (positon == 0 || positon == 4 || positon == 5) {
                pointF = getConvertedPoint(TuSdkContext.getScreenSize().width / 2f, (VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(44)));
            } else if (positon == 1 || positon == 2 || positon == 3 || positon == 3 || positon == 6) {
         //      float sdfdsfsf = VideoBeautyPlugin.screenHeight - movieEditorController.getCurrentComponent().getBottomView().getMeasuredHeight()-TuSdkContext.dip2px(44);
           //     pointF = new PointF(TuSdkContext.getScreenSize().width / 2f, getConvertedPoint(movieEditorController.getVideoContentView().getWidth() / 2, sdfdsfsf).y);
                final int[] locations = new int[2];
                LinearLayout linearLayout =   (LinearLayout)mLineView.getParent();
                linearLayout.getLocationOnScreen(locations);
          //      pointF = getConvertedPoint(TuSdkContext.getScreenSize().width / 2f,sdfdsfsf );
                pointF = getConvertedPoint(TuSdkContext.getScreenSize().width / 2f, locations[1]-TuSdkContext.dip2px(144));
            //    pointF = new PointF(TuSdkContext.getScreenSize().width / 2f,(VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(44)));

          //      pointF = getConvertedPoint(TuSdkContext.getScreenSize().width / 2f, sdfdsfsf);


               // pointF = new PointF(TuSdkContext.getScreenSize().width / 2f, getConvertedPoint(movieEditorController.getVideoContentView().getWidth() / 2, sdfdsfsf).y);


            }

            isContinue = false;
            mParticleConfig.setVisible(false);
            mLineView.endAddColorRect();

            String particleCode = mParticleAdapter.getParticleCode(positon);
            // 构建魔法特效
            mCurrentParticleEffectModel = new TuSdkMediaParticleEffectData(particleCode);
            mCurrentParticleEffectModel.setSize(mParticleConfig.getSize());
            mCurrentParticleEffectModel.setColor(mParticleConfig.getColor());
            mCurrentParticleEffectModel.putPoint(getEditorPlayer().getCurrentTimeUs(), pointF);
            // 预览魔法特效
            getEditorEffector().addMediaEffectData(mCurrentParticleEffectModel);
            mDataList.addLast(mCurrentParticleEffectModel);
            mLineView.addColorRect(TuSdkContext.getColor("lsq_margic_effect_color_" + particleCode));
            long totalUs = getEditorPlayer().getTotalTimeUs();
            float percent = mLineView.getCurrentPercent();
            getEditorPlayer().startPreview();

            mStartTimeUs = (long) (totalUs * percent);
            if (getEditorPlayer().isReversing()) {
                TLog.d("setAtTimeRange mStartTimeUs = " + mStartTimeUs + " isReversing totalUs = " + totalUs + " percent = " + percent + "inputTotalTimeUs = " + getEditorPlayer().getInputTotalTimeUs() + " outputTotalTimeUs = " + getEditorPlayer().getOutputTotalTimeUS());
                mCurrentParticleEffectModel.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(0, mStartTimeUs));
            } else {
                TLog.d("setAtTimeRange mStartTimeUs = " + mStartTimeUs + " notReversing");
                mCurrentParticleEffectModel.setAtTimeRange(TuSdkTimeRange.makeTimeUsRange(mStartTimeUs, Long.MAX_VALUE));
            }

            mDrawColorState = true;
            mCurrentParticleEffectModel.getFilterWrap().updateParticleEmitPosition(pointF);

            if (mDataList.size() > 0) {
                rollback_btn.setVisibility(View.VISIBLE);
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mParticleView = inflater.inflate(R.layout.lsq_editor_component_effect_bottom_particle, null);
        mParticleRecycle = mParticleView.findViewById(R.id.lsq_editor_effect_time_list);
        rollback_btn = mParticleView.findViewById(R.id.rollback_btn);
        rollback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        mParticleRecycle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    onPageScrolled();
                }
                return false;
            }
        });

        mParticleRecycle.setLayoutManager(new TuSdkLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mParticleAdapter.setMagicList(Arrays.asList(PARTICLE_CODES));
        mParticleRecycle.setAdapter(mParticleAdapter);
        mParticleAdapter.setItemCilckListener(new MagicRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position, MagicRecyclerAdapter.MagicViewHolder MagicViewHolder) {
                dealClick(position, false);

            }
        });
        mParticleRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onPageScrolled();
            }
        });

        mLineView = mParticleView.findViewById(R.id.lsq_editor_time_play_range);
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
            if (mBitmapList != null) {
                for (Bitmap bp : mBitmapList)
                    mLineView.addBitmap(bp);
            }
        }
        mLineView.setCursorMinPercent(movieEditorController.getmCurrentLeftPercent());
        mLineView.setCursorMaxPercent(movieEditorController.getmCurrentRightPercent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLineView.seekTo(getEditorPlayer().isReversing() ? movieEditorController.getmCurrentRightPercent() : movieEditorController.getmCurrentLeftPercent());
            }
        },100);


//            mPlayBtn = mParticleView.findViewById(R.id.lsq_editor_time_play);
//            mPlayBtn.setOnClickListener(mOnClickListener);

        return mParticleView;
    }

    private void cancel() {
        if (mDataList.size() > 0) {
            isContinue = false;
            setPlayState(1);
            mLineView.endAddColorRect();
            mLineView.deletedColorRect();
            getEditorPlayer().pausePreview();
            TuSdkMediaParticleEffectData effectData = mDataList.removeLast();
            getEditorEffector().removeMediaEffectData(effectData);
            if (mDataList.size() == 0) {
                rollback_btn.setVisibility(View.INVISIBLE);
            }
//                        getEditorPlayer().seekOutputTimeUs(effectData.getAtTimeRange().getStartTimeUS());
            if (mMovieEditor.getEditorPlayer().isReversing()) {
                mMovieEditor.getEditorPlayer().seekInputTimeUs(effectData.getAtTimeRange().getEndTimeUS());
                mLineView.seekTo(effectData.getAtTimeRange().getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
            } else {
                mMovieEditor.getEditorPlayer().seekInputTimeUs(effectData.getAtTimeRange().getStartTimeUS());
                mLineView.seekTo(effectData.getAtTimeRange().getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs());
            }

            boolean isCanDeleted = mDataList.size() > 0;
            mParticleAdapter.setCanDeleted(isCanDeleted);
            mParticleAdapter.notifyDataSetChanged();
            return;
        }
    }

    private void dealClick(int position, boolean isClick) {
        if (mCurrentIndex != position)
            mParticleConfig.setVisible(false);

        if (isClick) {
            mCurrentIndex = -1;
        } else {
            mCurrentIndex = position;
        }
        if (isClick) {
            mCurrentParticleCode = "None";
        } else {
            mCurrentParticleCode = PARTICLE_CODES[position];

        }
        mParticleAdapter.setCurrentPosition(mCurrentIndex);
        if (mCurrentIndex == -1) {
            rollback_btn.setVisibility(View.INVISIBLE);
        }
//            if(position==0&&mDataList.size() == 0&&isClick){
//          //      rollback_btn.setVisibility(View.INVISIBLE);
//            }
        if (!isClick || mDataList.size() == 0) {
            return;
        }
        ;
        TuSdkMediaParticleEffectData effectData = mDataList.removeLast();
        getEditorEffector().removeMediaEffectData(effectData);
        if (mDataList.size() != 0) return;
        mParticleAdapter.setCanDeleted(false);
        mParticleAdapter.notifyDataSetChanged();

    }

    public void addCoverBitmap(Bitmap bitmap) {
        if (mLineView == null) return;
        mLineView.addBitmap(bitmap);
    }

    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        if (mLineView != null)
            mLineView.addFirstFrameBitmap(bitmap);
    }

    @Override
    public void attach() {
        super.attach();
        //恢复之前的情况
        if (mMementoList.size() == 0) {
            mParticleAdapter.setCanDeleted(false);
            mParticleAdapter.notifyItemChanged(0);
        } else {
            mParticleAdapter.setCanDeleted(true);
            mParticleAdapter.notifyItemChanged(0);
        }

        for (TuSdkMediaParticleEffectData item : mMementoList) {
            recoveryEffect(item);
        }
//            mParticleAdapter.setCurrentPosition(mMementoIndex);
        //   mParticleContent.setOnTouchListener(mOnParticleTouchListener);
//        getEditorPlayer().seekOutputTimeUs(0);
        getEditorPlayer().seekOutputTimeUs((long) (getEditorPlayer().getInputTotalTimeUs()*movieEditorController.getmCurrentLeftPercent()));



        if (mLineView != null) {
            //            mLineView.seekTo(getEditorPlayer().isReversing() ? 1 : 0);
            mLineView.seekTo(getEditorPlayer().isReversing() ? movieEditorController.getmCurrentRightPercent() : movieEditorController.getmCurrentLeftPercent());
            mLineView.setTimeEffectType(getEditorPlayer().isReversing() ? 1 : 0);
            mLineView.setCursorMinPercent(movieEditorController.getmCurrentLeftPercent());
            mLineView.setCursorMaxPercent(movieEditorController.getmCurrentRightPercent());
            mLineView.setPercent(movieEditorController.getmCurrentLeftPercent());
            //todo seekOutputTimeUs seekInputTimeUs ??


        }
    }


    @Override
    public void onSelected() {
        super.onSelected();
        isFirstSelect = true;
        boolean isReverse = getEditorPlayer().isReversing();
        mLineView.endAddColorRect();
        getEditorPlayer().pausePreview();
        getEditorPlayer().seekOutputTimeUs((long) (movieEditorController.getmCurrentLeftPercent()*getEditorPlayer().getTotalTimeUs()));
//        mLineView.seekTo(isReverse ? 1f : 0f);
        mLineView.seekTo(getEditorPlayer().isReversing() ? movieEditorController.getmCurrentRightPercent() : movieEditorController.getmCurrentLeftPercent());

        if (mLineView != null)
            mLineView.setTimeEffectType(getEditorPlayer().isReversing() ? 1 : 0);
        //   mPlayBtn.setImageDrawable(TuSdkContext.getDrawable(R.drawable.edit_ic_play));
        //   mParticleContent.setOnTouchListener(mOnParticleTouchListener);
    }

    /**
     * 恢复之前应用的特效
     **/
    private void recoveryEffect(TuSdkMediaParticleEffectData mediaEffectData) {
        mDataList.add(mediaEffectData);
        String screenCode = mediaEffectData.getParticleCode();
        float startPercent = mediaEffectData.getAtTimeRange().getStartTimeUS() / (float) getEditorPlayer().getTotalTimeUs();
        float endPercent = mediaEffectData.getAtTimeRange().getEndTimeUS() / (float) getEditorPlayer().getTotalTimeUs();
        mLineView.recoverColorRect(TuSdkContext.getColorResId("lsq_margic_effect_color_" + screenCode), startPercent, endPercent);
    }

    public void clearSelect() {
        mParticleContent.setOnTouchListener(null);
    }

    @Override
    public void detach() {
        super.detach();
        mParticleAdapter.setCurrentPosition(0);
        mParticleContent.setOnTouchListener(null);
        mParticleConfig.setVisible(false);
        mCurrentParticleCode = null;
    }

    @Override
    public void back() {
        super.back();
        for (TuSdkMediaEffectData item : mDataList) {
            getEditorEffector().removeMediaEffectData(item);
            mLineView.deletedColorRect();
        }

        for (TuSdkMediaEffectData item : mMementoList) {
            getEditorEffector().addMediaEffectData(item);
        }

        mDataList.clear();
        mParticleConfig.setVisible(false);
    }

    @Override
    public void next() {
        super.next();

        mMementoList.clear();
        mMementoList.addAll(mDataList);
        mMementoIndex = mCurrentIndex;
        mDataList.clear();
        mParticleConfig.setVisible(false);
        mLineView.clearAllColorRect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mParticleConfig.setVisible(false);
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
//

    }

    /**
     * 播放空间滚动时的回调
     **/
    private TuSdkMovieScrollView.OnProgressChangedListener mOnScrollingPlayListener = new TuSdkMovieScrollView.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(float progress, boolean isTouching) {
            currentPercent = progress;
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

    private long mPreTimeUs = 0l;

    private int mOutCurrentFrameCount = 0;



    /**
     * 点击坐标系和绘制动画坐标系不同，需要转换坐标
     *
     * @return
     */
    public PointF getConvertedPoint(float x, float y) {
        // 获取视频大小
        TuSdkSize videoSize;
        if (((TuSdkEditorPlayerImpl) getEditorPlayer()).getOutputSize() != null) {
            videoSize = ((TuSdkEditorPlayerImpl) getEditorPlayer()).getOutputSize();
        } else {
            videoSize = TuSdkSize.create(mMovieEditor.getEditorTransCoder().getOutputVideoInfo().width, mMovieEditor.getEditorTransCoder().getOutputVideoInfo().height);
        }


        TuSdkSize previewSize = new TuSdkSize((int) (mParticleContent.getMeasuredWidth()), (int) (mParticleContent.getMeasuredHeight() * 0.6));
        TuSdkSize screenSize = previewSize;

        RectF previewRectF = new RectF(0, (screenSize.height - previewSize.height) / (float) 2,
                previewSize.width, (screenSize.height + previewSize.height) / (float) 2);

        if (!previewRectF.contains(x, y))
            return new PointF(-1, -1);

        // 将基于屏幕的坐标转换成基于预览区域的坐标
        y -= previewRectF.top;

        float videoX, videoY;
        PointF convertedPoint;
        if (previewSize.width > previewSize.height) {
            videoX = x / (float) previewSize.width * videoSize.width;
            videoY = y / (float) previewSize.height * videoSize.height;
            convertedPoint = new PointF(videoX, videoSize.minSide() - videoY);
        } else {
            videoX = x / (float) previewSize.width * videoSize.minSide();
            videoY = y / (float) previewSize.height * videoSize.maxSide();
            convertedPoint = new PointF(videoX, videoSize.maxSide() - videoY);
        }

        return convertedPoint;
    }

    private float currentPercent;

    /**
     * 更新进度
     **/
    public void moveToPercent(float percentage, long playbackTimeUs) {
        if (mLineView == null) return;
        currentPercent = percentage;
        if (isFirstSelect) return;
        mLineView.seekTo(percentage);
    }

    public void onPlayerStateChanged(int state) {
        if (mCurrentParticleEffectModel != null) {
            //暂停
            if (state == 1) {
                if (mCurrentParticleEffectModel != null) {
                    TuSdkTimeRange timeRange = mCurrentParticleEffectModel.getAtTimeRange();
                    if (mMovieEditor.getEditorPlayer().isReversing()) {
                        timeRange.setStartTimeUs((long) (mLineView.getCurrentPercent() * getEditorPlayer().getTotalTimeUs()));
                    } else {
                        timeRange.setEndTimeUs(getEditorPlayer().getCurrentTimeUs());
//                        mLineView.seekTo(mMovieEditor.getEditorPlayer().getCurrentTimeUs() / (float) getEditorPlayer().getTotalTimeUs());
                        if (timeRange.getEndTimeUS() < prePercent * getEditorPlayer().getTotalTimeUs()) {
                            timeRange.setEndTimeUs((long) (prePercent * getEditorPlayer().getTotalTimeUs()));
                        }
                    }
                    mCurrentParticleEffectModel.setAtTimeRange(timeRange);
                }
                mDrawColorState = false;
                mLineView.endAddColorRect();
                mCurrentParticleEffectModel = null;

            } else {
                mDrawColorState = true;
                isContinue = false;
            }
        }
    }

    public LinkedList<TuSdkMediaParticleEffectData> getmDataList() {
        return mDataList;
    }
}

