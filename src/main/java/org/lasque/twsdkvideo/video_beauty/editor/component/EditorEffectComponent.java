package org.lasque.twsdkvideo.video_beauty.editor.component;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayerImpl;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.recyclerview.TuSdkLinearLayoutManager;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaParticleEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaRepeatTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaReversalTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaSceneEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaSlowTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaTimeEffect;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorController;
import org.lasque.twsdkvideo.video_beauty.editor.component.fragment.EffectFragment;
import org.lasque.twsdkvideo.video_beauty.editor.component.fragment.ParticleEffectFragment;
import org.lasque.twsdkvideo.video_beauty.editor.component.fragment.SceneEffectFragment;
import org.lasque.twsdkvideo.video_beauty.editor.component.fragment.TimeEffectFragment;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewParams;
import org.lasque.twsdkvideo.video_beauty.views.ConfigViewSeekBar;
import org.lasque.twsdkvideo.video_beauty.views.EffectComponetAdapter;
import org.lasque.twsdkvideo.video_beauty.views.MagicRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.SceneRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.TabPagerIndicator;
import org.lasque.twsdkvideo.video_beauty.views.TimeRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.editor.TuSdkMovieScrollPlayLineView;
import org.lasque.twsdkvideo.video_beauty.views.editor.color.ColorView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollContent;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkMovieScrollView;
import org.lasque.twsdkvideo.video_beauty.views.editor.playview.TuSdkRangeSelectionBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.lasque.twsdkvideo.video_beauty.utils.Constants.EDITORFILTERS;
import static org.lasque.twsdkvideo.video_beauty.utils.Constants.PARTICLE_CODES;


public class EditorEffectComponent extends EditorComponent {

    /**
     * ???????????????????????????
     **/
    private View mBottomView;

    /**
     * ??????????????? ViewPager
     **/
    private ViewPager mViewPager;
    /**
     * ?????????
     **/
    private TabPagerIndicator mIndicator;
    /**
     * ?????? ViewPager ?????????
     **/
    private EffectComponetAdapter mAdapter;
    /**
     * ????????????
     **/
    private View mBackBtn;
    /**
     * ????????????
     **/
    private View mNextBtn;
    /**
     * ?????????????????? (??????  ??????)
     **/
    private ParticleConfigView mMagicConfig;

    /**
     * ???????????? Fragment
     **/
    private SceneEffectFragment mScreenFragment;
    /**
     * ???????????? Fragment
     **/
    private TimeEffectFragment mTimeFragment;
    /**
     * ???????????? Fragment
     **/
    private ParticleEffectFragment mMagicFragment;

    /**
     * ?????? Frgament ??????
     **/
    private List<EffectFragment> mFragmentList;
    /**
     * ????????????????????????
     **/
    private List<Bitmap> mBitmapList = new ArrayList<>();

    /**
     * ????????????????????????
     **/
    private static long mEffectDurationUs = 2 * 1000000;


    /**
     * ????????????????????????
     *
     * @since V3.0.0
     */
    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (getEditorController().getActivity().getMagicContent() == null) return;
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getEditorController().getActivity().getMagicContent().getLayoutParams();
                    layoutParams.width = previewSize.width;
                    layoutParams.height = previewSize.height;
                    layoutParams.leftMargin = (getEditorController().getVideoContentView().getWidth() - layoutParams.width) / 2;
                    layoutParams.topMargin = (getEditorController().getVideoContentView().getHeight() - layoutParams.height) / 2;
                    getEditorController().getActivity().getMagicContent().setLayoutParams(layoutParams);
                }
            });

        }
    };


    /**
     * ????????????????????????
     *
     * @return
     */
    public EditorEffectComponent setHeadAction() {

        mNextBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_save);
        mBackBtn = getEditorController().getActivity().getTitleView().findViewById(R.id.lsq_back1);

        if (mNextBtn != null) {
            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save();
                }
            });
            mBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hasEffect();

                }
            });
        }
        return this;
    }

    /**
     * ??????????????????
     **/
    private TuSdkEditorPlayer.TuSdkProgressListener mProgressLisntener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {
            if (mScreenFragment != null) mScreenFragment.setPlayState(state);
            if (mScreenFragment != null) mScreenFragment.onPlayerStateChanged(state);
            if (mTimeFragment != null) mTimeFragment.setPlayState(state);
            if (mMagicFragment != null) mMagicFragment.setPlayState(state);
            if (mMagicFragment != null) mMagicFragment.onPlayerStateChanged(state);

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            Log.e("sdfdfsfdfsffsf","onProgress"+percentage);
            if (isAnimationStaring) return;
            if (mScreenFragment != null) mScreenFragment.moveToPercent(percentage, playbackTimeUs);
            if (mMagicFragment != null) mMagicFragment.moveToPercent(percentage, playbackTimeUs);
            if (mTimeFragment != null) mTimeFragment.moveToPercent(percentage);

        }
    };


    /**
     * ??????????????????
     *
     * @param editorController
     */
    public EditorEffectComponent(MovieEditorController editorController) {
        super(editorController);
        mComponentType = EditorComponentType.Effect;
        getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
    }

    @Override
    public void attach() {
        getEditorController().getBottomView().addView(getBottomView());
        getEditorPlayer().pausePreview();
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);

        if (mScreenFragment != null) mScreenFragment.attach();
        if (mTimeFragment != null) mTimeFragment.attach();
        if (mMagicFragment != null) mMagicFragment.attach();
        if (mViewPager != null) mViewPager.setCurrentItem(0);

//        getEditorController().getVideoContentView().setClickable(false);
//        getEditorController().getPlayBtn().setVisibility(View.GONE);
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();

        if (mScreenFragment != null) mScreenFragment.onAnimationStart();
        if (mTimeFragment != null) mTimeFragment.onAnimationStart();
        if (mMagicFragment != null) mMagicFragment.onAnimationStart();

    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mScreenFragment != null) mScreenFragment.onAnimationEnd();
        if (mTimeFragment != null) mTimeFragment.onAnimationEnd();
        if (mMagicFragment != null) mMagicFragment.onAnimationEnd();
    }

    @Override
    public void detach() {
        if (mScreenFragment != null) mScreenFragment.detach();
        if (mTimeFragment != null) mTimeFragment.detach();
        if (mMagicFragment != null) mMagicFragment.detach();
        getEditorPlayer().pausePreview();
        getEditorPlayer().seekTimeUs(0);
        getEditorController().getVideoContentView().setClickable(true);
        getEditorController().getPlayBtn().setVisibility(View.VISIBLE);

    }

    @Override
    public View getHeaderView() {
        return null;
    }

    @Override
    public View getBottomView() {
        if (mBottomView == null) {
            initBottomView();
        }
        return mBottomView;
    }

    @Override
    public void addCoverBitmap(Bitmap bitmap) {
        getBottomView();
        mBitmapList.add(bitmap);
        if (mScreenFragment != null) mScreenFragment.addCoverBitmap(bitmap);
        if (mTimeFragment != null) mTimeFragment.addCoverBitmap(bitmap);
        if (mMagicFragment != null) mMagicFragment.addCoverBitmap(bitmap);
    }

    @Override
    public void addFirstFrameCoverBitmap(Bitmap bitmap) {
        getBottomView();
        if (mScreenFragment != null) mScreenFragment.addFirstFrameCoverBitmap(bitmap);
        if (mTimeFragment != null) mTimeFragment.addFirstFrameCoverBitmap(bitmap);
        if (mMagicFragment != null) mMagicFragment.addFirstFrameCoverBitmap(bitmap);

    }


    /**
     * ?????????BottomView
     **/
    private void initBottomView() {
        mBottomView = LayoutInflater.from(getEditorController().getActivity()).inflate(R.layout.lsq_editor_component_effect_bottom, null);
        mViewPager = findViewById(R.id.lsq_editor_effect_content);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mMagicFragment!=null&&motionEvent.getAction()==MotionEvent.ACTION_UP){
                    mMagicFragment.onPageScrolled();
                }
                return false;
            }
        });
        // ibFilterBack.setOnClickListener(onClickListener);


//        mBackBtn = findViewById(R.id.lsq_effect_close);
//        mNextBtn = findViewById(R.id.lsq_effect_sure);


        mIndicator = findViewById(R.id.lsq_effect_indicator);

//        mBackBtn.setOnClickListener(mOnClickListener);
//        mNextBtn.setOnClickListener(mOnClickListener);

        mMagicConfig = new ParticleConfigView();

        mFragmentList = new ArrayList<>();
        mScreenFragment = new SceneEffectFragment(getEditorController().getMovieEditor(), mBitmapList, getEditorController());
        mTimeFragment = new TimeEffectFragment(getEditorController().getMovieEditor(), mBitmapList, getEditorController());

        mMagicFragment = new ParticleEffectFragment(getEditorController().getMovieEditor(), getEditorController().getActivity().getMagicContent(), mMagicConfig, mBitmapList, getEditorController());


        mFragmentList.add(mMagicFragment);
        mFragmentList.add(mScreenFragment);
        mFragmentList.add(mTimeFragment);


        mAdapter = new EffectComponetAdapter(getEditorController().getActivity().getSupportFragmentManager(), mFragmentList);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if(mMagicFragment!=null){
                    mMagicFragment.onPageScrolled();
                }
                if(mScreenFragment!=null){
                    mScreenFragment.onPageScrolled();
                }
            }

            @Override
            public void onPageSelected(int i) {
                if (mFragmentList.get(i) != mMagicFragment) {
                    mMagicFragment.mParticleConfig.setVisible(false);
                    mMagicFragment.clearSelect();
                }

                if (mFragmentList.get(i) != mTimeFragment) {
                    mTimeFragment.updataApplayTimeEffect();
                }

                mFragmentList.get(i).onSelected();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mIndicator.setViewPager(mViewPager, 0);
        mIndicator.setTabItems(Arrays.asList(
                getEditorController().getActivity().getResources().getString(R.string.lsq_visual),
                getEditorController().getActivity().getResources().getString(R.string.lsq_move),
                getEditorController().getActivity().getResources().getString(R.string.lsq_time)
        ));
        mViewPager.setCurrentItem(0);

        getEditorController().getMovieEditor().getEditorPlayer().addProgressListener(mProgressLisntener);
    }


    public void close() {
        mScreenFragment.back();
        mTimeFragment.back();
        mMagicFragment.back();
        getEditorPlayer().seekOutputTimeUs(0);
        getEditorController().onBackEvent();
    }

    public void save() {
        mScreenFragment.next();
        mTimeFragment.next();
        mMagicFragment.next();
        getEditorController().onBackEvent();
    }


    public void hasEffect() {
        if(mScreenFragment.hasEffects() ||mTimeFragment.hasEffects() ||  mMagicFragment.hasEffects()){
                DialogHelper.closeTipDialog(getEditorController().getActivity(), getEditorController().getActivity().getString(R.string.dialog_title_discard_edits_tips), new DialogHelper.onDiscardClickListener() {
                    @Override
                    public void onDiscardClick() {
                        close();
                    }
                });
        }else {
            close();
        }

    }



    public void cleanEffect() {
        if (mScreenFragment != null) mScreenFragment.cleanEffect();
        if (mTimeFragment != null) mTimeFragment.cleanEffect();
        if (mMagicFragment != null) mMagicFragment.cleanEffect();
    }


    /**
     * ??????????????????????????????
     **/
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.lsq_back1) {
                mScreenFragment.back();
                mTimeFragment.back();
                mMagicFragment.back();
                getEditorPlayer().seekOutputTimeUs(0);
                getEditorController().onBackEvent();
            } else if (id == R.id.lsq_save) {
                mScreenFragment.next();
                mTimeFragment.next();
                mMagicFragment.next();
                getEditorController().onBackEvent();
            }
        }
    };


    private <T extends View> T findViewById(@IdRes int id) {
        return mBottomView.findViewById(id);
    }

    public SceneEffectFragment getmScreenFragment() {
        return mScreenFragment;
    }

    public TimeEffectFragment getmTimeFragment() {
        return mTimeFragment;
    }

    public ParticleEffectFragment getmMagicFragment() {
        return mMagicFragment;
    }


    /**
     * ??????????????????
     **/
    public class ParticleConfigView {
        private LinearLayout mContent;
        private ConfigViewSeekBar mSizeSeekBar;
        private ColorView mColorSeekBar;

        public ParticleConfigView() {
            mContent = getEditorController().getActivity().findViewById(R.id.lsq_magic_config);
            mSizeSeekBar = getEditorController().getActivity().findViewById(R.id.lsq_magic_size_seekbar);
            ConfigViewParams params = new ConfigViewParams();
            params.appendFloatArg("size", 0f);
            mSizeSeekBar.setConfigViewArg(params.getArgs().get(0));

            mColorSeekBar = getEditorController().getActivity().findViewById(R.id.lsq_magic_color_seekView);
            mColorSeekBar.setCircleRadius(10);
        }

        public void setVisible(boolean visible) {
            mContent.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public boolean isVisible() {
            return mContent.getVisibility() == View.VISIBLE;
        }

        /**
         * ????????????
         **/
        public float getSize() {
            return mSizeSeekBar.getSeekbar().getProgress();
        }

        /**
         * ????????????
         **/
        public int getColor() {
            return mColorSeekBar.getSelectColor();
        }
    }


}
