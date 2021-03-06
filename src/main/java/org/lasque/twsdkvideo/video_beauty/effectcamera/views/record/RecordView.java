package org.lasque.twsdkvideo.video_beauty.effectcamera.views.record;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.tusdk.pulse.Config;
import com.tusdk.pulse.audio.processors.AudioPitchProcessor;
import com.tusdk.pulse.filter.Filter;
import com.tusdk.pulse.filter.filters.TusdkBeautFaceV2Filter;
import com.tusdk.pulse.filter.filters.TusdkFacePlasticFilter;
import com.tusdk.pulse.filter.filters.TusdkImageFilter;
import com.tusdk.pulse.filter.filters.TusdkReshapeFilter;

import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.model.PropsItemMonster;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.model.PropsItemSticker;
import org.lasque.twsdkvideo.video_beauty.tubeautysetting.AudioConvert;
import org.lasque.twsdkvideo.video_beauty.tubeautysetting.Beauty;
import org.lasque.tusdkpulse.core.TuSdk;
import org.lasque.tusdkpulse.core.TuSdkContext;
import org.lasque.tusdkpulse.core.TuSdkResult;
import org.lasque.tusdkpulse.core.seles.SelesParameters;
import org.lasque.tusdkpulse.core.seles.tusdk.FilterGroup;
import org.lasque.tusdkpulse.core.seles.tusdk.FilterLocalPackage;
import org.lasque.tusdkpulse.core.seles.tusdk.FilterOption;
import org.lasque.tusdkpulse.core.struct.TuSdkSize;
import org.lasque.tusdkpulse.core.utils.TLog;
import org.lasque.tusdkpulse.core.utils.ThreadHelper;
import org.lasque.tusdkpulse.core.utils.hardware.CameraConfigs;
import org.lasque.tusdkpulse.core.utils.image.AlbumHelper;
import org.lasque.tusdkpulse.core.utils.image.RatioType;
import org.lasque.tusdkpulse.core.utils.sqllite.ImageSqlHelper;
import org.lasque.tusdkpulse.core.view.TuSdkViewHelper;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.effectcamera.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.BeautyPlasticRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.BeautyRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.FilterConfigSeekbar;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.FilterRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.HorizontalProgressBar;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.ParamsConfigView;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.TabPagerIndicator;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.CosmeticPanelController;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.CosmeticTypes;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.cosmetic.panel.BasePanel;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.newFilterUI.FilterFragment;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.newFilterUI.FilterViewPagerAdapter;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.PropsItemMonsterPageFragment;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.PropsItemPageFragment;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.PropsItemPagerAdapter;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.StickerPropsItemPageFragment;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.model.PropsItem;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.model.PropsItemCategory;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.model.PropsItemMonsterCategory;
import org.lasque.twsdkvideo.video_beauty.effectcamera.views.props.model.PropsItemStickerCategory;
import org.lasque.tusdkpulse.cx.hardware.camera.TuCamera;
import org.lasque.tusdkpulse.cx.hardware.utils.TuCameraAspectRatio;
import org.lasque.tusdkpulse.impl.view.widget.RegionDefaultHandler;
import org.lasque.tusdkpulse.impl.view.widget.RegionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zuojindong on 2018/6/20.
 */

public class RecordView extends RelativeLayout {

    public final static String DEFAULT_FILTER_CODE = "default_filter_code";
    public final static String DEFAULT_FILTER_GROUP = "default_filter_group";

    private final static int mCropIndex = 20;

    public final static HashMap<SelesParameters.FilterModel, Integer> mFilterMap = new HashMap<SelesParameters.FilterModel, Integer>();

    public final static int DOUBLE_VIEW_INDEX = 50;

    static {
        mFilterMap.put(SelesParameters.FilterModel.Reshape, 13);
        mFilterMap.put(SelesParameters.FilterModel.CosmeticFace, 14);
        mFilterMap.put(SelesParameters.FilterModel.MonsterFace, 15);
        mFilterMap.put(SelesParameters.FilterModel.PlasticFace, 16);
        mFilterMap.put(SelesParameters.FilterModel.StickerFace, 17);
        mFilterMap.put(SelesParameters.FilterModel.SkinFace, 18);
        mFilterMap.put(SelesParameters.FilterModel.Filter, 19);


    }

    public enum RecordState {
        Recording, Paused, RecordCompleted, RecordTimeOut, Saving, SaveCompleted;
    }

    public enum DoubleViewMode {
        None, ViewInView, TopBottom, LeftRight;
    }

    /**
     * ??????????????????
     */
    public interface RecordType {
        // ??????
        int CAPTURE = 0;
        // ????????????
        int SHORT_CLICK_RECORD = 2;
        // ???????????????
        int SHORT_CLICK_RECORDING = 4;
        //??????
        int DOUBLE_VIEW_RECORD = 5;
    }

    /**
     * ????????????????????????
     */
    public interface TuSDKMovieRecordDelegate {
        /**
         * ??????????????????
         */
        boolean startRecording();

        /**
         * ??????????????????
         *
         * @return
         */
        boolean isRecording();

        /**
         * ??????????????????
         */
        void pauseRecording();

        /**
         * ??????????????????
         */
        boolean stopRecording();

        /**
         * ??????????????????
         */
        void finishRecordActivity();

        void changedAudioEffect(AudioConvert.AudioPitchType type);

        void changedSpeed(double speed);

        void changedRatio(TuSdkSize ratio);

        void changedRect(RectF rectF);

        int getFragmentSize();

        void popFragment();

        void selectVideo();

        void updateDoubleViewMode(DoubleViewMode mode);

        void selectAudio();

        void updateMicState(boolean isOpen);

        void changeRenderWidth(double width);
    }

    public void setDelegate(TuSDKMovieRecordDelegate delegate) {
        mDelegate = delegate;
    }

    public TuSDKMovieRecordDelegate getDelegate() {
        return mDelegate;
    }

    private Context mContext;
    /**
     * ????????????????????????
     */
    private TuSDKMovieRecordDelegate mDelegate;
    /**
     * ???????????????Bitmap
     */
    private Bitmap mCaptureBitmap;

    private TuSdkResult mCurrentResult;

    private SharedPreferences mFilterValueMap;

    private TuCamera mCamera;

    private Beauty mBeautyManager;

    /******************************* FilterPipe ********************************/

    private DoubleViewMode mCurrentDoubleViewMode = DoubleViewMode.LeftRight;

    /******************************* View ********************************/
    private TuSdkVideoFocusTouchView mFocusTouchView;

    /**
     * ????????????
     */
    private LinearLayout mTopBar;
    /**
     * ????????????
     */
    private TextView mCloseButton;
    /**
     * ?????????????????????
     */
    private TextView mSwitchButton;
    /**
     * ????????????
     */
    private TextView mBeautyButton;
    /**
     * ????????????
     */
    private TextView mSpeedButton;
    /**
     * ????????????
     */
    private TextView mMoreButton;

    /**
     * ????????????
     */
    private LinearLayout mSmartBeautyTabLayout;
    private RecyclerView mBeautyRecyclerView;

    /**
     * ????????????
     **/
    private HorizontalProgressBar mRecordProgress;
    /**
     * ??????????????????????????????
     */
    private RelativeLayout interuptLayout;
    /**
     * ????????????
     */
    private TextView mRollBackButton;

    /**
     * ????????????????????????
     */
    private LinearLayout mBottomBarLayout;
    /**
     * ????????????
     */
    private ImageView mRecordButton;
    /**
     * ??????????????????
     **/
    private TextView mConfirmButton;
    /**
     * ??????
     */
    private TextView mStickerWrapButton;
    /**
     * ??????
     */
    private TextView mFilterButton;

    /**
     * ????????????????????????
     */
    private ViewGroup mSpeedModeBar;
    /**
     * ????????????????????????
     */
    private boolean isSpeedChecked = false;

    /**
     * ??????????????????
     */
    private RelativeLayout mRecordModeBarLayout;
    /**
     * ????????????
     */
    private TextView mShootButton;
    /**
     * ????????????
     */
    private TextView mClickButton;

//    private TextView mDoubleViewButton;

    /**
     * ??????????????????
     */
    private LinearLayout mMoreConfigLayout;
    /**
     * ??????????????????
     */
    private TextView mFocusOpen;
    private TextView mFocusClose;
    /**
     * ???????????????
     */
    private TextView mLightingOpen;
    private TextView mLightingClose;
    /**
     * Radio??????
     */
    private ImageView mRadioFull;
    private ImageView mRadio3_4;
    private ImageView mRadio1_1;
    /**
     * ??????
     */
    private RelativeLayout mChangeAudioLayout;
    private RadioGroup mChangeAudioGroup;

    private RelativeLayout mSimultaneouslyLayer;
    private TextView mTopBottomMode;
    private TextView mLeftRightMode;
    private TextView mViewInViewMode;

    private TextView mRender720;
    private TextView mRender1080;

    private boolean canChangeLayer = true;


    // ???????????? ??????+?????????

    /**
     * ????????????
     */
    private LinearLayout mPropsItemLayout;
    /**
     * ????????????
     */
    private ImageView mPropsItemCancel;
    /**
     * ?????? Layout
     */
    private ViewPager2 mPropsItemViewPager;
    /**
     * ??????  PropsItemPagerAdapter
     */
    private PropsItemPagerAdapter<PropsItemPageFragment> mPropsItemPagerAdapter;

    private TabPagerIndicator mPropsItemTabPagerIndicator;
    /**
     * ??????????????????
     */
    private List<PropsItemCategory> mPropsItemCategories = new ArrayList<>();

    //????????????
    private SeekBar mExposureSeekbar;


    /**
     * ??????????????????
     **/
    private ImageView mPreViewImageView;
    /**
     * ??????????????????
     **/
    private TextView mBackButton;
    /**
     * ????????????
     **/
    private TextView mSaveImageButton;

    private LinearLayout mSelectAudio;
    private TextView mAudioName;

    private TextView mMicOpen;
    private TextView mMicClose;

    private boolean isBeautyClose = false;


    private int mCameraMaxEV = 0;

    private int mCameraMinEV = 0;

    private int mCurrentCameraEV = 0;


    private ViewPager2 mFilterViewPager;
    private TabPagerIndicator mFilterTabIndicator;
    private FilterViewPagerAdapter mFilterViewPagerAdapter;
    private ImageView mFilterReset;
    private boolean isFilterReset = false;

    private List<FilterFragment> mFilterFragments;

    private List<FilterGroup> mFilterGroups;

    public RecordView(Context context) {
        super(context);
    }

    public RecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected int getLayoutId() {
        return R.layout.record_view_new;
    }

    protected void init(Context context) {
        LayoutInflater.from(context).inflate(getLayoutId(), this,
                true);

        // TopLayout
        mTopBar = findViewById(R.id.lsq_topBar);
        mCloseButton = findViewById(R.id.lsq_closeButton);
        mSwitchButton = findViewById(R.id.lsq_switchButton);
        mBeautyButton = findViewById(R.id.lsq_beautyButton);
        mSpeedButton = findViewById(R.id.lsq_speedButton);
        mMoreButton = findViewById(R.id.lsq_moreButton);

        mCloseButton.setOnClickListener(onClickListener);
        mSwitchButton.setOnClickListener(onClickListener);
        mBeautyButton.setOnClickListener(onClickListener);
        mSpeedButton.setOnClickListener(onClickListener);
        mMoreButton.setOnClickListener(onClickListener);

        // more_config_layout
        mMoreConfigLayout = findViewById(R.id.lsq_more_config_layout);
        // ????????????
        mFocusOpen = findViewById(R.id.lsq_focus_open);
        mFocusClose = findViewById(R.id.lsq_focus_close);
        mFocusOpen.setOnClickListener(onClickListener);
        mFocusClose.setOnClickListener(onClickListener);
        // ?????????
        mLightingOpen = findViewById(R.id.lsq_lighting_open);
        mLightingClose = findViewById(R.id.lsq_lighting_close);
        mLightingOpen.setOnClickListener(onClickListener);
        mLightingClose.setOnClickListener(onClickListener);
        // ??????
        mRadioFull = findViewById(R.id.lsq_radio_full);
        mRadio3_4 = findViewById(R.id.lsq_radio_3_4);
        mRadio1_1 = findViewById(R.id.lsq_radio_1_1);
        mRadioFull.setOnClickListener(onClickListener);
        mRadio3_4.setOnClickListener(onClickListener);
        mRadio1_1.setOnClickListener(onClickListener);
        // ??????
        mChangeAudioLayout = findViewById(R.id.lsq_audio_layout);
        mChangeAudioGroup = findViewById(R.id.lsq_audio_group);
        mChangeAudioGroup.setOnCheckedChangeListener(mAudioOnCheckedChangeListener);

        mSimultaneouslyLayer = findViewById(R.id.lsq_simultaneously_layer);
        mTopBottomMode = findViewById(R.id.lsq_top_bottom);
        mLeftRightMode = findViewById(R.id.lsq_left_right);
        mViewInViewMode = findViewById(R.id.lsq_view_in_view);
        mTopBottomMode.setOnClickListener(mOnSimultaneouslyModeChanged);
        mLeftRightMode.setOnClickListener(mOnSimultaneouslyModeChanged);
        mViewInViewMode.setOnClickListener(mOnSimultaneouslyModeChanged);

        mMicOpen = findViewById(R.id.lsq_mic_open);
        mMicOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelegate != null) mDelegate.updateMicState(true);

                mMicOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mMicClose.setTextColor(getResources().getColor(R.color.lsq_color_white));
            }
        });
        mMicClose = findViewById(R.id.lsq_mic_close);
        mMicClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelegate != null) mDelegate.updateMicState(false);

                mMicClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mMicOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
            }
        });

        // ????????????????????????
        mBottomBarLayout = findViewById(R.id.lsq_button_wrap_layout);
        // ????????????
        mStickerWrapButton = findViewById(R.id.lsq_stickerWrap);
        mStickerWrapButton.setOnClickListener(onClickListener);
        // ????????????
        mFilterButton = findViewById(R.id.lsq_tab_filter_btn);
        mFilterButton.setOnClickListener(onClickListener);
        // ????????????
        mConfirmButton = findViewById(R.id.lsq_confirmWrap);
        mConfirmButton.setOnClickListener(onClickListener);
        // ????????????
        mRecordButton = findViewById(R.id.lsq_recordButton);
        mRecordButton.setOnTouchListener(onTouchListener);

        // ??????????????????
        mRecordModeBarLayout = findViewById(R.id.lsq_record_mode_bar_layout);
       // mRecordModeBarLayout.setOnTouchListener(onModeBarTouchListener);

        // ???????????????
        mRecordProgress = findViewById(R.id.lsq_record_progressbar);
        Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
        LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
        minTimeLayoutParams.leftMargin = (int) (((float) Constants.MIN_RECORDING_TIME * TuSdkContext.getScreenSize().width) / Constants.MAX_RECORDING_TIME)
                - TuSdkContext.dip2px(minTimeButton.getWidth());
        // ????????????????????????????????????????????????
        interuptLayout = (RelativeLayout) findViewById(R.id.interuptLayout);
        // ????????????
        mRollBackButton = (TextView) findViewById(R.id.lsq_backWrap);
        mRollBackButton.setOnClickListener(onClickListener);

        // ????????????
        mShootButton = findViewById(R.id.lsq_shootButton);
        mClickButton = findViewById(R.id.lsq_clickButton);
     //   mDoubleViewButton = findViewById(R.id.lsq_double_view_Button);
//        mShootButton.setOnTouchListener(onModeBarTouchListener);
//        mClickButton.setOnTouchListener(onModeBarTouchListener);
   //     mDoubleViewButton.setOnTouchListener(onModeBarTouchListener);

        // PreviewLayout
        mBackButton = findViewById(R.id.lsq_backButton);
        mBackButton.setOnClickListener(onClickListener);
        mSaveImageButton = findViewById(R.id.lsq_saveImageButton);
        mSaveImageButton.setOnClickListener(onClickListener);
        mPreViewImageView = findViewById(R.id.lsq_cameraPreviewImageView);
        mPreViewImageView.setOnClickListener(onClickListener);

        mSelectAudio = findViewById(R.id.lsq_select_audio);
        mSelectAudio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDelegate != null) {
                    mDelegate.selectAudio();
                }
            }
        });

        mAudioName = findViewById(R.id.lsq_audio_name);

        // ???????????????
        mSpeedModeBar = findViewById(R.id.lsq_movie_speed_bar);
        int childCount = mSpeedModeBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mSpeedModeBar.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectSpeedMode(Double.parseDouble((String) view.getTag()));
                }
            });
        }

        // ??????Bar
        mSmartBeautyTabLayout = findViewById(R.id.lsq_smart_beauty_layout);
        setBeautyLayout(false);
        mBeautyRecyclerView = findViewById(R.id.lsq_beauty_recyclerView);
        mBeautyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // ????????????
        mBeautyRecyclerAdapter = new BeautyRecyclerAdapter(getContext());
        mBeautyRecyclerAdapter.setOnSkinItemClickListener(beautyItemClickListener);
        // ?????????
        mBeautyPlasticRecyclerAdapter = new BeautyPlasticRecyclerAdapter(getContext(), mBeautyPlastics);
        mBeautyPlasticRecyclerAdapter.setOnBeautyPlasticItemClickListener(beautyPlasticItemClickListener);

        // ??????
        mController = new CosmeticPanelController(getContext());
        initCosmeticView();

        // ????????????
        mFilterConfigView = findViewById(R.id.lsq_filter_config_view_new);
        mFilterConfigView.setSeekBarDelegate(mFilterConfigViewSeekBarDelegate);
        // ???????????????
        mBeautyPlasticsConfigView = findViewById(R.id.lsq_beauty_plastics_config_view);
        mBeautyPlasticsConfigView.setPrefix("lsq_beauty_");
        mBeautyPlasticsConfigView.setSeekBarDelegate(mBeautyPlasticConfigViewSeekBarDelegate);


        //??????????????????
        mExposureSeekbar = findViewById(R.id.lsq_exposure_compensation_seek);
        mExposureSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (!fromUser) return;
                mCurrentCameraEV = progress - mCameraMaxEV;
                mCamera.cameraParams().setExposureCompensation(mCurrentCameraEV);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRender720 = findViewById(R.id.lsq_render_720);
        mRender720.setOnClickListener(mRenderSizeChanged);
        mRender1080 = findViewById(R.id.lsq_render_1080);
        mRender1080.setOnClickListener(mRenderSizeChanged);

        mFilterValueMap = getContext().getSharedPreferences("TUTUFilter", Context.MODE_PRIVATE);

        initFilterRecyclerView();
        initStickerLayout();

    }

    public DoubleViewMode getCurrentDoubleViewMode() {
        return mCurrentDoubleViewMode;
    }

    private String mCurrentFilterCode = "";

    public TuSdkSize mCurrentRatio;

    /**
     */
    public void initFilterPipe(Beauty beauty) {
        mBeautyManager = beauty;
        mController.initCosmetic(beauty);

//        Future<Boolean> res = mRenderPool.submit(new Callable<Boolean>() {
//            @Override
//            public Boolean call() throws Exception {
//                mRatioFilter = new Filter(mFP.getContext(), AspectRatioFilter.TYPE_NAME);
//                boolean ret = mFP.addFilter(mCropIndex, mRatioFilter);
//                TuSdkSize size = TuSdkContext.getScreenSize();
//                TLog.e("screen size %s",size.toString());
//                mRatioProperty.widthRatio = size.width;
//                mRatioProperty.heightRatio = size.height;
//                mCurrentRatio = TuSdkSize.create(size);
//                mRatioFilter.setProperty(AspectRatioFilter.PROP_PARAM,mRatioProperty.makeProperty());
//                return ret;
//            }
//        });
//
//        try {
//            res.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        initPlastic();

        switchConfigSkin(Constants.SkinMode.SkinMoist);
    }


    public void initFilterGroupsViews(FragmentManager fragmentManager, Lifecycle lifecycle, List<FilterGroup> filterGroups) {
        mFilterGroups = filterGroups;
        mFilterReset = findViewById(R.id.lsq_filter_reset);
        mFilterReset.setOnClickListener(new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                mBeautyManager.setFilter("");
                mFilterFragments.get(mFilterTabIndicator.getCurrentPosition()).removeFilter();
                mFilterConfigView.setVisibility(View.GONE);
                mFilterViewPagerAdapter.notifyDataSetChanged();
                mCurrentFilterCode = "";
                mFilterValueMap.edit().remove(DEFAULT_FILTER_CODE).apply();
                mFilterValueMap.edit().remove(DEFAULT_FILTER_GROUP).apply();
                isFilterReset = true;

            }
        });

        mFilterTabIndicator = findViewById(R.id.lsq_filter_tabIndicator);

        mFilterViewPager = findViewById(R.id.lsq_filter_view_pager);
        mFilterViewPager.requestDisallowInterceptTouchEvent(true);
        List<String> tabTitles = new ArrayList<>();
        List<FilterFragment> fragments = new ArrayList<>();
        for (FilterGroup group : mFilterGroups) {
            if (group == null){
                continue;
            }
            FilterFragment fragment = FilterFragment.newInstance(group);
            if (group.groupId == 252) {
                fragment.setOnFilterItemClickListener(new FilterFragment.OnFilterItemClickListener() {
                    @Override
                    public void onFilterItemClick(String code, int position) {
                        mCurrentFilterCode = code;
                        mCurrentPosition = position;
                        //????????????
                        changeVideoComicEffectCode(mCurrentFilterCode);
                    }
                });
            } else {
                fragment.setOnFilterItemClickListener(new FilterFragment.OnFilterItemClickListener() {
                    @Override
                    public void onFilterItemClick(String code, int position) {
                        if (TextUtils.equals(mCurrentFilterCode, code)) {
                            mFilterConfigView.setVisibility(mFilterConfigView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                        } else {
                            mCurrentFilterCode = code;
                            mCurrentPosition = position;
                            //????????????
                            changeVideoFilterCode(mCurrentFilterCode);
                        }
                    }
                });
            }

            fragments.add(fragment);
            tabTitles.add(group.getName());
        }
        mFilterFragments = fragments;
        mFilterViewPagerAdapter = new FilterViewPagerAdapter(fragmentManager, lifecycle, fragments);
        mFilterViewPager.setAdapter(mFilterViewPagerAdapter);
        mFilterTabIndicator.setViewPager(mFilterViewPager, 0);
        mFilterTabIndicator.setDefaultVisibleCounts(tabTitles.size());
        mFilterTabIndicator.setTabItems(tabTitles);


    }

    /**
     * ???????????????
     */
    public void initRecordProgress() {
        mRecordProgress.clearProgressList();
        interuptLayout.removeAllViews();
        if (mBottomBarLayout.getVisibility() == VISIBLE)
            setViewHideOrVisible(true);
    }

    /**
     * ????????????????????????
     *
     * @param camera
     */
    public void setUpCamera(Context context, TuCamera camera) {
        this.mContext = context;
        this.mCamera = camera;

        getFocusTouchView();
    }

    public void setExposure() {
        mCameraMaxEV = mCamera.cameraParams().getMaxExposureCompensation();
        mCameraMinEV = mCamera.cameraParams().getMinExposureCompensation();
        mExposureSeekbar.setMax(mCameraMaxEV + Math.abs(mCameraMinEV));
        mExposureSeekbar.setProgress(mCameraMaxEV);
    }

    /**
     * ????????????
     */
    private OnTouchListener onTouchListener = new OnTouchListener() {
        /**
         * @param v
         * @param event
         * @return
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getDelegate() == null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (TuSdkViewHelper.isFastDoubleClick()) return false;
                    return true;
                case MotionEvent.ACTION_UP:
                    // ????????????
                    if (mRecordMode == RecordType.CAPTURE) {
                        //todo ??????
                        mCamera.shotPhoto();
                    }
                    // ????????????
                    else if (mRecordMode == RecordType.SHORT_CLICK_RECORD || mRecordMode == RecordType.DOUBLE_VIEW_RECORD) {
                        // ???????????????
                        if (getDelegate().isRecording()) {
                            getDelegate().pauseRecording();
                            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
                        } else {
                            //todo ??????
                            setViewHideOrVisible(false);
                            if (getDelegate().startRecording()) {
                                updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
                            }
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
    };

    /**
     * ????????????
     */
    RadioGroup.OnCheckedChangeListener mAudioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.lsq_audio_normal) {
                getDelegate().changedAudioEffect(AudioConvert.AudioPitchType.NORMAL);
                // ??????
            } else if (checkedId == R.id.lsq_audio_monster) {
                getDelegate().changedAudioEffect(AudioConvert.AudioPitchType.MONSTER);
                // ??????
            } else if (checkedId == R.id.lsq_audio_uncle) {
                getDelegate().changedAudioEffect(AudioConvert.AudioPitchType.UNCLE);
                // ??????
            } else if (checkedId == R.id.lsq_audio_girl) {
                getDelegate().changedAudioEffect(AudioConvert.AudioPitchType.GIRL);
                // ??????
            } else if (checkedId == R.id.lsq_audio_lolita) {
                getDelegate().changedAudioEffect(AudioConvert.AudioPitchType.LOLITA);
                // ??????
            }
        }
    };

    /**
     * ????????????????????????
     */
    private ViewPropertyAnimatorListener mViewPropertyAnimatorListener = new ViewPropertyAnimatorListener() {

        @Override
        public void onAnimationCancel(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
            ViewCompat.animate(mPropsItemLayout).setListener(null);
            ViewCompat.animate(mFilterContent).setListener(null);
        }

        @Override
        public void onAnimationStart(View view) {
        }
    };

    /******************************** ?????? ********************************************/
    /**
     * ??????????????????
     */
    private static final int DEFAULT_POSITION = 1;
    /**
     * ????????????
     */
    private RelativeLayout mFilterContent;
    /**
     * ??????????????????
     */
    protected ParamsConfigView mFilterConfigView;
    /**
     * ????????????
     */
    private RecyclerView mFilterRecyclerView;
    /**
     * ????????????Adapter
     */
    private FilterRecyclerAdapter mFilterAdapter;
    /**
     * ????????????
     */
    private RecyclerView mComicsFilterRecyclerView;
    /**
     * ????????????Adapter
     */
    private FilterRecyclerAdapter mComicsFilterAdapter;
    /**
     * ???????????????????????????
     */
    private int mCurrentPosition = DEFAULT_POSITION;
    /**
     * ???????????????????????????
     */
    private int mComicsCurrentPosition = 0;
    /**
     * ????????????
     */
    private TextView mFilterNameTextView;
    /**
     * ????????????????????????
     */
    private boolean isComicsFilterChecked = false;

    /**
     * ???????????????
     */
    private void initFilterRecyclerView() {
        mFilterNameTextView = findViewById(R.id.lsq_filter_name);
        mFilterContent = findViewById(R.id.lsq_filter_content);
        /** ??????????????????????????? ?????????????????????????????????  ???????????? ?????????*/
        mFilterContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setFilterContentVisible(false);

    }

    /**
     * ??????????????????
     */
    private void showFilterLayout() {
        // ??????????????????????????????
        ViewCompat.setTranslationY(mFilterContent,
                mFilterContent.getHeight());
        ViewCompat.animate(mFilterContent).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);

        setFilterContentVisible(true);

        // ????????????????????????
        if (mCurrentPosition > 0 && mFilterConfigView != null) {
            mFilterConfigView.invalidate();
        }
    }

    /**
     * ???????????????
     */
    private ParamsConfigView.FilterConfigViewSeekBarDelegate mFilterConfigViewSeekBarDelegate = new ParamsConfigView.FilterConfigViewSeekBarDelegate() {
        @Override
        public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, SelesParameters.FilterArg arg) {
            float progress = seekbar.getSeekbar().getProgress();
            mFilterValueMap.edit().putFloat(mCurrentFilterCode, progress).apply();
        }
    };

    private void setDefaultFilter() {
        mFilterViewPager.setCurrentItem(0);
        int filterViewPagerPos = 0;
        mFilterGroups = Constants.getCameraFilters(true);
        String defaultCode = mFilterValueMap.getString(DEFAULT_FILTER_CODE, "");
        if (TextUtils.isEmpty(defaultCode)) {
            return;
        }
        long defaultFilterGroupId = mFilterValueMap.getLong(DEFAULT_FILTER_GROUP, -1);
        List<FilterOption> defaultFilters = mFilterGroups.get(0).filters;
        FilterGroup defaultGroup = mFilterGroups.get(0);
        if (defaultFilterGroupId != -1) {
            for (FilterGroup group : mFilterGroups) {
                if (group.groupId == defaultFilterGroupId) {
                    defaultFilters = group.filters;
                    defaultGroup = group;
                    filterViewPagerPos = mFilterGroups.indexOf(group);
                    break;
                }
            }
        }
        for (int i = 0; i < defaultFilters.size(); i++) {
            if (defaultFilters.get(i).code.equals(defaultCode)) {
                mCurrentPosition = i;
                break;
            }
        }
        mCurrentFilterCode = defaultCode;
        mFilterViewPager.setCurrentItem(filterViewPagerPos, false);
        final FilterGroup finalDefaultGroup = defaultGroup;
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (finalDefaultGroup.groupId == 252) {
                    changeVideoComicEffectCode(mCurrentFilterCode);
                } else {
                    changeVideoFilterCode(mCurrentFilterCode);
                }
            }
        }, 1000);
    }

    /**
     * ???????????????????????????
     */
    private FilterRecyclerAdapter.ItemClickListener mFilterItemClickListener = new FilterRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mFilterConfigView.setVisibility((position == 0) ? INVISIBLE :
                    ((mCurrentPosition == position) ? (mFilterConfigView.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE)
                            : INVISIBLE));
            if (mCurrentPosition == position) return;
            mCurrentPosition = position;
            changeVideoFilterCode(mFilterAdapter.getFilterList().get(position));
        }
    };

    /**
     * ?????????????????????????????????
     */
    private FilterRecyclerAdapter.ItemClickListener mComicsFilterItemClickListener = new FilterRecyclerAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            mComicsCurrentPosition = position;
            changeVideoComicEffectCode(mComicsFilterAdapter.getFilterList().get(position));
        }
    };

    /**
     * ????????????
     *
     * @param code
     */
    protected void changeVideoFilterCode(final String code) {
        isFilterReset = false;
        SelesParameters selesParameters = new SelesParameters(code, SelesParameters.FilterModel.Filter);

        List<FilterOption> options = FilterLocalPackage.shared().getFilters(Arrays.asList(code));
        if (options.size() > 0) {
            FilterOption option = options.get(0);
            for (String arg : option.args.keySet()) {
                selesParameters.appendFloatArg(arg, Float.parseFloat(option.args.get(arg)));
            }
        }
        FilterOption option = options.get(0);
        double value = Double.parseDouble(option.args.get("mixied"));

        mBeautyManager.setFilter(code);
        mBeautyManager.setFilterStrength((float) value);
        selesParameters.setListener(new SelesParameters.SelesParametersListener() {
            @Override
            public void onUpdateParameters(SelesParameters.FilterModel model, String code, SelesParameters.FilterArg arg) {
                mBeautyManager.setFilterStrength(arg.getPrecentValue());
            }
        });
        mFilterConfigView.setFilterArgs(selesParameters.getArgs());

        mFilterValueMap.edit().putString(DEFAULT_FILTER_CODE, code).apply();
        mFilterValueMap.edit().putLong(DEFAULT_FILTER_GROUP, mFilterGroups.get(mFilterViewPager.getCurrentItem()).groupId).apply();
        if (mFilterTabIndicator.getCurrentPosition() != -1) {
            for (int i = 0; i < mFilterFragments.size(); i++) {
                if (i == mFilterTabIndicator.getCurrentPosition()) {
                    mFilterFragments.get(i).setCurrentPosition(mCurrentPosition);
                } else {
                    mFilterFragments.get(i).setCurrentPosition(-1);
                }
            }
        }
        // ???????????????
        showHitTitle(TuSdkContext.getString("lsq_filter_" + code));
    }


    /**
     * ??????????????????
     *
     * @param title
     */
    private void showHitTitle(String title) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFilterNameTextView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFilterNameTextView.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFilterNameTextView.setText(title);
        mFilterNameTextView.setAnimation(alphaAnimation);
        alphaAnimation.setDuration(2000);
        alphaAnimation.start();
    }

    /**
     * TouchView????????????
     */
    private TuSdkVideoFocusTouchViewBase.GestureListener gestureListener = new TuSdkVideoFocusTouchViewBase.GestureListener() {
        @Override
        public void onLeftGesture() {
            // ??????????????????????????????
            if (mSmartBeautyTabLayout.getVisibility() == VISIBLE) return;

            FilterGroup current = mFilterGroups.get(mFilterTabIndicator.getCurrentPosition());
            final String filterCode;
            if (mCurrentPosition == current.filters.size() - 1) {
                int targetViewPagerPos = mFilterTabIndicator.getCurrentPosition() + 1 == mFilterFragments.size() ? 0 : mFilterTabIndicator.getCurrentPosition() + 1;
                current = mFilterGroups.get(targetViewPagerPos);
                mFilterViewPager.setCurrentItem(targetViewPagerPos);
                mCurrentPosition = 0;
            } else {
                ++mCurrentPosition;
            }
            filterCode = current.filters.get(mCurrentPosition).code;
            mCurrentFilterCode = filterCode;
            final FilterGroup finalCurrent = current;
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (finalCurrent.groupId == 252) {
                        changeVideoComicEffectCode(filterCode);
                    } else {
                        changeVideoFilterCode(filterCode);
                    }
                }
            }, 100);


        }

        @Override
        public void onRightGesture() {
            // ??????????????????????????????
            if (mSmartBeautyTabLayout.getVisibility() == VISIBLE) return;

            FilterGroup current = mFilterGroups.get(mFilterTabIndicator.getCurrentPosition());
            final String filterCode;
            if (mCurrentPosition == 0) {
                int targetViewPagerPos = mFilterTabIndicator.getCurrentPosition() - 1 == -1 ? mFilterFragments.size() - 1 : mFilterTabIndicator.getCurrentPosition() - 1;
                current = mFilterGroups.get(targetViewPagerPos);
                mFilterViewPager.setCurrentItem(targetViewPagerPos);
                mCurrentPosition = current.filters.size() - 1;
            } else {
                --mCurrentPosition;
            }
            filterCode = current.filters.get(mCurrentPosition).code;
            mCurrentFilterCode = filterCode;
            final FilterGroup finalCurrent = current;
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (finalCurrent.groupId == 252) {
                        changeVideoComicEffectCode(filterCode);
                    } else {
                        changeVideoFilterCode(filterCode);
                    }
                }
            }, 100);
        }

        @Override
        public void onClick() {
            if (!isRecording()) {
                mExposureSeekbar.setProgress(mCameraMaxEV);
                setFilterContentVisible(false);
                setBeautyViewVisible(false);
                setBottomViewVisible(true);
                setStickerVisible(false);
                mMoreConfigLayout.setVisibility(GONE);
                setTextButtonDrawableTop(mMoreButton, R.drawable.video_nav_ic_more);
                mPropsItemViewPager.getAdapter().notifyDataSetChanged();
                getFocusTouchView().isShowFoucusView(true);
            }
        }
    };

    private RegionHandler mRegionHandle = new RegionDefaultHandler();

    private TuSdkVideoFocusTouchView getFocusTouchView() {
        if (mFocusTouchView == null) {
            mFocusTouchView = findViewById(R.id.lsq_focus_touch_view);
            mFocusTouchView.setCamera(mCamera);

            mFocusTouchView.setRegionHandler(mRegionHandle);
            mFocusTouchView.setGestureListener(gestureListener);
        }
        return mFocusTouchView;
    }

    public void setDisplaySize(int width, int height) {
        TLog.e("Surface Size %s || %s", width, height);
    }

    private boolean isRecording() {
        return false;
    }

    public void setWrapSize(TuSdkSize wrapSize) {
        mRegionHandle.setWrapSize(wrapSize);
    }

    /********************** ?????? ****************************/

    /**
     * ??????????????????
     *
     * @param code
     */
    protected void changeVideoComicEffectCode(final String code) {
        isFilterReset = false;
        mBeautyManager.setFilter(code);
        mFilterValueMap.edit().putString(DEFAULT_FILTER_CODE, code).apply();
        mFilterValueMap.edit().putLong(DEFAULT_FILTER_GROUP, mFilterGroups.get(mFilterViewPager.getCurrentItem()).groupId).apply();
        if (mFilterTabIndicator.getCurrentPosition() != -1) {
            for (int i = 0; i < mFilterFragments.size(); i++) {
                if (i == mFilterTabIndicator.getCurrentPosition()) {
                    mFilterFragments.get(i).setCurrentPosition(mCurrentPosition);
                } else {
                    mFilterFragments.get(i).setCurrentPosition(-1);
                }
            }
        }
        mFilterConfigView.setVisibility(View.GONE);
        // ???????????????
        showHitTitle(TuSdkContext.getString("lsq_filter_" + code));
    }

    /******************************* ?????? **************************/
    /**
     * ???????????????
     */
    private void initStickerLayout() {
        mPropsItemViewPager = findViewById(R.id.lsq_viewPager);
        mPropsItemTabPagerIndicator = findViewById(R.id.lsq_TabIndicator);

        mPropsItemCancel = findViewById(R.id.lsq_cancel_button);
        mPropsItemCancel.setOnClickListener(onClickListener);

        // ????????????
        mPropsItemLayout = findViewById(R.id.lsq_sticker_layout);
        setStickerVisible(false);
    }

    /**
     * ??????????????????
     *
     * @param isVisible ????????????
     */
    private void setStickerVisible(boolean isVisible) {
        mPropsItemLayout.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }

    /**
     * ??????????????????
     */
    private void showStickerLayout() {
        setStickerVisible(true);
        // ??????????????????????????????
        ViewCompat.setTranslationY(mPropsItemLayout,
                mPropsItemLayout.getHeight());
        ViewCompat.animate(mPropsItemLayout).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
    }

    /**
     * ?????????????????????????????????
     */
    private StickerPropsItemPageFragment.StickerItemDelegate mStickerPropsItemDelegate = new StickerPropsItemPageFragment.StickerItemDelegate() {
        /**
         * ????????????
         * @param propsItem
         */

        private boolean removeRes = false;

        private boolean selectRes = false;

        @Override
        public void removePropsItem(PropsItem propsItem) {
            if (propsItemUsed(propsItem)) {
                mBeautyManager.setDynamicSticker(0);
            }
        }

        private long mCurrentGroupId = 0l;


        @Override
        public void didSelectPropsItem(PropsItem propsItem) {
            mPropsItemPagerAdapter.notifyAllPageData();
            mBeautyManager.setDynamicSticker(((PropsItemSticker) propsItem).getStickerGroup().groupId);
            mCurrentGroupId = ((PropsItemSticker) propsItem).getStickerGroup().groupId;
        }

        /**
         * ?????????????????????????????????
         *
         * @param propsItem ??????
         * @return
         */
        @Override
        public boolean propsItemUsed(PropsItem propsItem) {
            if (mBeautyManager == null || !mBeautyManager.hasDynamicSticker())
                return false;
            long groupId = ((PropsItemSticker) propsItem).getStickerGroup().groupId;

            TLog.e("[Debug] current click sticker id %s current sticker id %s",groupId,mCurrentGroupId);
            return mCurrentGroupId == groupId;
        }
    };

    /**
     * ???????????????????????????
     */
    private PropsItemPageFragment.ItemDelegate mPropsItemDelegate = new PropsItemPageFragment.ItemDelegate() {

        private String mCurrentMonsterFaceCode = "";

        @Override
        public void didSelectPropsItem(PropsItem propsItem) {
            hideBeautyBarLayout();
            mBeautyPlasticRecyclerAdapter.clearSelect();

            mCurrentMonsterFaceCode = ((PropsItemMonster) propsItem).getMonsterCode();

            mBeautyManager.setMonsterFace(mCurrentMonsterFaceCode);
            TLog.e("current monster code %s",mCurrentMonsterFaceCode);

            mPropsItemPagerAdapter.notifyAllPageData();
        }

        /**
         * ?????????????????????????????????
         *
         * @param propsItem ??????
         * @return
         */
        @Override
        public boolean propsItemUsed(PropsItem propsItem) {
            if (mBeautyManager == null || !mBeautyManager.hasMonsterFace())
                return false;
            boolean res = mCurrentMonsterFaceCode.equals(((PropsItemMonster) propsItem).getMonsterCode());
            return res;
        }


    };

    /**
     * ?????????????????????
     */
    public void init(final FragmentManager fm, final Lifecycle lifecycle) {

        // ??????????????????????????????
        mPropsItemCategories.addAll(PropsItemStickerCategory.allCategories());

        // ???????????????????????????
        mPropsItemCategories.addAll(PropsItemMonsterCategory.allCategories());

        mPropsItemPagerAdapter = new PropsItemPagerAdapter(fm, lifecycle, new PropsItemPagerAdapter.DataSource() {
            @Override
            public Fragment frament(int pageIndex) {

                PropsItemCategory category = mPropsItemCategories.get(pageIndex);

                switch (category.getMediaEffectType()) {
                    case StickerFace: {
                        StickerPropsItemPageFragment fragment = new StickerPropsItemPageFragment(pageIndex, mPropsItemCategories.get(pageIndex).getItems());
                        fragment.setItemDelegate(mStickerPropsItemDelegate);
                        return fragment;
                    }
                    default: {
                        PropsItemMonsterPageFragment fragment = new PropsItemMonsterPageFragment(pageIndex, mPropsItemCategories.get(pageIndex).getItems());
                        fragment.setItemDelegate(mPropsItemDelegate);
                        return fragment;
                    }
                }

            }

            @Override
            public int pageCount() {
                return mPropsItemCategories.size();
            }
        });

        mPropsItemViewPager.setAdapter(mPropsItemPagerAdapter);

        mPropsItemTabPagerIndicator.setViewPager(mPropsItemViewPager, 0);
        mPropsItemTabPagerIndicator.setDefaultVisibleCounts(mPropsItemCategories.size());


        List<String> itemTitles = new ArrayList<>();
        for (PropsItemCategory category : mPropsItemCategories)
            itemTitles.add(category.getName());


        mPropsItemTabPagerIndicator.setTabItems(itemTitles);

//        initFilterGroupsViews(fm,lifecycle);
    }

    /*********************************** ?????? ********************* */

    private RelativeLayout mCosmeticList;

    private LinearLayout mLipstick, mBlush, mEyebrow, mEyeshadow, mEyeliner, mEyelash, mFacial, mCosmeticClear;
    private RelativeLayout mLipstickPanel, mBlushPanel, mEyebrowPanel, mEyeshadowPanel, mEyelinerPanel, mEyelashPanel, mFacialPanel;

    private CosmeticPanelController mController;

    private CosmeticTypes.Types mCurrentType;

    private HashSet<CosmeticTypes.Types> types = new HashSet<>();

    private int lastPos = -1;

    private BasePanel.OnPanelClickListener panelClickListener = new BasePanel.OnPanelClickListener() {
        @Override
        public void onClear(CosmeticTypes.Types type) {
            int viewID = -1;
            switch (type) {
                case Lipstick:
                    viewID = R.id.lsq_lipstick_add;
                    break;
                case Blush:
                    viewID = R.id.lsq_blush_add;
                    break;
                case Eyebrow:
                    viewID = R.id.lsq_eyebrow_add;
                    break;
                case Eyeshadow:
                    viewID = R.id.lsq_eyeshadow_add;
                    break;
                case Eyeliner:
                    viewID = R.id.lsq_eyeliner_add;
                    break;
                case Eyelash:
                    viewID = R.id.lsq_eyelash_add;
                    break;
                case Facial:
                    viewID = R.id.lsq_facial_add;
                    break;
            }
            findViewById(viewID).setVisibility(View.GONE);
            mBeautyPlasticsConfigView.setVisibility(View.GONE);
            types.remove(type);
        }

        @Override
        public void onClose(CosmeticTypes.Types type) {
            switch (type) {
                case Lipstick:
                    mLipstickPanel.setVisibility(View.GONE);
                    break;
                case Blush:
                    mBlushPanel.setVisibility(View.GONE);
                    break;
                case Eyebrow:
                    mEyebrowPanel.setVisibility(View.GONE);
                    break;
                case Eyeshadow:
                    mEyeshadowPanel.setVisibility(View.GONE);
                    break;
                case Eyeliner:
                    mEyelinerPanel.setVisibility(View.GONE);
                    break;
                case Eyelash:
                    mEyelashPanel.setVisibility(View.GONE);
                    break;
                case Facial:
                    mFacialPanel.setVisibility(View.GONE);
                    break;
            }
            mCurrentType = null;
            mBeautyPlasticsConfigView.setVisibility(View.GONE);
            mPreButton.setVisibility(View.VISIBLE);
            mCosmeticScroll.scrollTo(lastPos, 0);
        }

        @Override
        public void onClick(CosmeticTypes.Types type) {
            int viewID = -1;
            switch (type) {
                case Lipstick:
                    viewID = R.id.lsq_lipstick_add;
                    break;
                case Blush:
                    viewID = R.id.lsq_blush_add;
                    break;
                case Eyebrow:
                    viewID = R.id.lsq_eyebrow_add;
                    break;
                case Eyeshadow:
                    viewID = R.id.lsq_eyeshadow_add;
                    break;
                case Eyeliner:
                    viewID = R.id.lsq_eyeliner_add;
                    break;
                case Eyelash:
                    viewID = R.id.lsq_eyelash_add;
                    break;
                case Facial:
                    viewID = R.id.lsq_facial_add;
                    break;
            }
            findViewById(viewID).setVisibility(View.VISIBLE);
            mBeautyPlasticsConfigView.setVisibility(View.VISIBLE);
            types.add(type);
        }
    };

    private View mPreButton;

    private OnClickListener mCosmeticClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            lastPos = mCosmeticScroll.getScrollX();
            if (v.getId() != R.id.lsq_cosmetic_item_clear)
                if (mPreButton != null) mPreButton.setVisibility(View.VISIBLE);
            int id = v.getId();
            if (id == R.id.lsq_cosmetic_item_clear) {
                clearCosmetic();
            } else if (id == R.id.lsq_cosmetic_item_lipstick) {
                mCurrentType = CosmeticTypes.Types.Lipstick;
                mLipstick.setVisibility(View.GONE);
                mPreButton = mLipstick;
                mLipstickPanel.setVisibility(mLipstickPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mBlushPanel.setVisibility(View.GONE);
                mEyebrowPanel.setVisibility(View.GONE);
                mEyeshadowPanel.setVisibility(View.GONE);
                mEyelinerPanel.setVisibility(View.GONE);
                mEyelashPanel.setVisibility(View.GONE);
                mFacialPanel.setVisibility(View.GONE);
                if (mLipstickPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_lipstick_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("lipAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mLipstickPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);
                }
            } else if (id == R.id.lsq_cosmetic_item_blush) {
                mCurrentType = CosmeticTypes.Types.Blush;
                mBlush.setVisibility(View.GONE);
                mPreButton = mBlush;
                mLipstickPanel.setVisibility(View.GONE);
                mBlushPanel.setVisibility(mBlushPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEyebrowPanel.setVisibility(View.GONE);
                mEyeshadowPanel.setVisibility(View.GONE);
                mEyelinerPanel.setVisibility(View.GONE);
                mEyelashPanel.setVisibility(View.GONE);
                mFacialPanel.setVisibility(View.GONE);
                if (mBlushPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_blush_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("blushAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mBlushPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);
                }
            } else if (id == R.id.lsq_cosmetic_item_eyebrow) {
                mCurrentType = CosmeticTypes.Types.Eyebrow;
                mEyebrow.setVisibility(View.GONE);
                mPreButton = mEyebrow;
                mLipstickPanel.setVisibility(View.GONE);
                mBlushPanel.setVisibility(View.GONE);
                mEyebrowPanel.setVisibility(mEyebrowPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEyeshadowPanel.setVisibility(View.GONE);
                mEyelinerPanel.setVisibility(View.GONE);
                mEyelashPanel.setVisibility(View.GONE);
                mFacialPanel.setVisibility(View.GONE);
                if (mEyebrowPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyebrow_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyebrowAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mEyebrowPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);
                }
            } else if (id == R.id.lsq_cosmetic_item_eyeshadow) {
                mCurrentType = CosmeticTypes.Types.Eyeshadow;
                mEyeshadow.setVisibility(View.GONE);
                mPreButton = mEyeshadow;
                mLipstickPanel.setVisibility(View.GONE);
                mBlushPanel.setVisibility(View.GONE);
                mEyebrowPanel.setVisibility(View.GONE);
                mEyeshadowPanel.setVisibility(mEyeshadowPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEyelinerPanel.setVisibility(View.GONE);
                mEyelashPanel.setVisibility(View.GONE);
                mFacialPanel.setVisibility(View.GONE);
                if (mEyeshadowPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyeshadow_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyeshadowAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mEyeshadowPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);
                }
            } else if (id == R.id.lsq_cosmetic_item_eyeliner) {
                mCurrentType = CosmeticTypes.Types.Eyeliner;
                mEyeliner.setVisibility(View.GONE);
                mPreButton = mEyeliner;
                mLipstickPanel.setVisibility(View.GONE);
                mBlushPanel.setVisibility(View.GONE);
                mEyebrowPanel.setVisibility(View.GONE);
                mEyeshadowPanel.setVisibility(View.GONE);
                mEyelinerPanel.setVisibility(mEyelinerPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEyelashPanel.setVisibility(View.GONE);
                mFacialPanel.setVisibility(View.GONE);
                if (mEyelinerPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyeliner_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyelineAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mEyelinerPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);
                }
            } else if (id == R.id.lsq_cosmetic_item_eyelash) {
                mCurrentType = CosmeticTypes.Types.Eyelash;
                mEyelash.setVisibility(View.GONE);
                mPreButton = mEyelash;
                mLipstickPanel.setVisibility(View.GONE);
                mBlushPanel.setVisibility(View.GONE);
                mEyebrowPanel.setVisibility(View.GONE);
                mEyeshadowPanel.setVisibility(View.GONE);
                mEyelinerPanel.setVisibility(View.GONE);
                mEyelashPanel.setVisibility(mEyelashPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mFacialPanel.setVisibility(View.GONE);
                if (mEyelashPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_eyelash_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyelashAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mEyelashPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);

                }
            } else if (id == R.id.lsq_cosmetic_item_facial) {
                mCurrentType = CosmeticTypes.Types.Facial;
                mFacial.setVisibility(View.GONE);
                mPreButton = mFacial;
                mLipstickPanel.setVisibility(View.GONE);
                mBlushPanel.setVisibility(View.GONE);
                mEyebrowPanel.setVisibility(View.GONE);
                mEyeshadowPanel.setVisibility(View.GONE);
                mEyelinerPanel.setVisibility(View.GONE);
                mEyelashPanel.setVisibility(View.GONE);
                mFacialPanel.setVisibility(mFacialPanel.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                if (mFacialPanel.getVisibility() == View.VISIBLE) {
                    mBeautyPlasticsConfigView.setVisibility(findViewById(R.id.lsq_facial_add).getVisibility());
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("facialAlpha")));
                    findViewById(R.id.list_panel).addOnLayoutChangeListener(new OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            mCosmeticScroll.scrollTo(mFacialPanel.getLeft(), 0);
                            findViewById(R.id.list_panel).removeOnLayoutChangeListener(this);
                        }
                    });
                } else {
                    mBeautyPlasticsConfigView.setVisibility(View.GONE);

                }
            }
        }
    };

    private boolean isFirstShow = true;

    private void clearCosmetic() {
        if (types.size() == 0) return;
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        adBuilder.setTitle(R.string.lsq_text_cosmetic_type);
        adBuilder.setMessage(R.string.lsq_clear_beauty_cosmetic_hit);
        adBuilder.setNegativeButton(R.string.lsq_audioRecording_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.setPositiveButton(R.string.lsq_audioRecording_next, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mController.clearAllCosmetic();
                types.removeAll(Arrays.asList(CosmeticTypes.Types.values()));
            }
        });
        adBuilder.show();
    }

    private HorizontalScrollView mCosmeticScroll;

    private void initCosmeticView() {
        mController.setPanelClickListener(panelClickListener);

        mCosmeticList = findViewById(R.id.lsq_cosmetic_list);

        mCosmeticClear = findViewById(R.id.lsq_cosmetic_item_clear);
        mCosmeticClear.setOnClickListener(mCosmeticClick);

        mLipstick = findViewById(R.id.lsq_cosmetic_item_lipstick);
        mLipstick.setOnClickListener(mCosmeticClick);
        mLipstickPanel = findViewById(R.id.lsq_lipstick_panel);
        mLipstickPanel.addView(mController.getLipstickPanel().getPanel());

        mBlush = findViewById(R.id.lsq_cosmetic_item_blush);
        mBlush.setOnClickListener(mCosmeticClick);
        mBlushPanel = findViewById(R.id.lsq_blush_panel);
        mBlushPanel.addView(mController.getBlushPanel().getPanel());

        mEyebrow = findViewById(R.id.lsq_cosmetic_item_eyebrow);
        mEyebrow.setOnClickListener(mCosmeticClick);
        mEyebrowPanel = findViewById(R.id.lsq_eyebrow_panel);
        mEyebrowPanel.addView(mController.getEyebrowPanel().getPanel());

        mEyeshadow = findViewById(R.id.lsq_cosmetic_item_eyeshadow);
        mEyeshadow.setOnClickListener(mCosmeticClick);
        mEyeshadowPanel = findViewById(R.id.lsq_eyeshadow_panel);
        mEyeshadowPanel.addView(mController.getEyeshadowPanel().getPanel());

        mEyeliner = findViewById(R.id.lsq_cosmetic_item_eyeliner);
        mEyeliner.setOnClickListener(mCosmeticClick);
        mEyelinerPanel = findViewById(R.id.lsq_eyeliner_panel);
        mEyelinerPanel.addView(mController.getEyelinerPanel().getPanel());

        mEyelash = findViewById(R.id.lsq_cosmetic_item_eyelash);
        mEyelash.setOnClickListener(mCosmeticClick);
        mEyelashPanel = findViewById(R.id.lsq_eyelash_panel);
        mEyelashPanel.addView(mController.getEyelashPanel().getPanel());

        mFacial = findViewById(R.id.lsq_cosmetic_item_facial);
        mFacial.setOnClickListener(mCosmeticClick);
        mFacialPanel = findViewById(R.id.lsq_facial_panel);
        mFacialPanel.addView(mController.getFacialPanel().getPanel());


        mCosmeticScroll = findViewById(R.id.lsq_cosmetic_scroll_view);
    }

    /*********************************** ????????? ********************/
    /**
     * ???????????????????????????
     */
    private boolean isBeautyChecked = true;

    private boolean isCosmeticChecked = false;
    /**
     * ???????????????
     */
    private BeautyRecyclerAdapter mBeautyRecyclerAdapter;
    /**
     * ??????????????????
     */
    private BeautyPlasticRecyclerAdapter mBeautyPlasticRecyclerAdapter;


    /**
     * ??????????????????
     */
    private ParamsConfigView mBeautyPlasticsConfigView;
    /**
     * ??????????????????  Float ????????????
     */
    private HashMap<String, Float> mDefaultBeautyPercentParams = new HashMap<String, Float>() {
        {
            put("eyeSize", 0.3f);
            put("chinSize", 0.5f);
            put("cheekNarrow", 0.0f);
            put("smallFace", 0.0f);
            put("noseSize", 0.2f);
            put("noseHeight", 0.0f);
            put("mouthWidth", 0.0f);
            put("lips", 0.0f);
            put("philterum", 0.0f);
            put("archEyebrow", 0.0f);
            put("browPosition", 0.0f);
            put("jawSize", 0.0f);
            put("cheekLowBoneNarrow", 0.0f);
            put("eyeAngle", 0.0f);
            put("eyeInnerConer", 0.0f);
            put("eyeOuterConer", 0.0f);
            put("eyeDis", 0.0f);
            put("eyeHeight", 0.0f);
            put("forehead", 0.0f);
            put("cheekBoneNarrow", 0.0f);

            put("eyelidAlpha", 0.0f);
            put("eyemazingAlpha", 0.0f);

            put("whitenTeethAlpha", 0.0f);
            put("eyeDetailAlpha", 0.0f);
            put("removePouchAlpha", 0.0f);
            put("removeWrinklesAlpha", 0.0f);

        }
    };

    private List<String> mReshapePlastics = new ArrayList() {
        {
            add("eyelidAlpha");
            add("eyemazingAlpha");

            add("whitenTeethAlpha");
            add("eyeDetailAlpha");
            add("removePouchAlpha");
            add("removeWrinklesAlpha");
        }
    };

    /**
     * ???????????????
     */
    private List<String> mBeautyPlastics = new ArrayList() {
        {
            add("reset");
            add("eyeSize");
            add("chinSize");
            add("cheekNarrow");
            add("smallFace");
            add("noseSize");
            add("noseHeight");
            add("mouthWidth");
            add("lips");
            add("philterum");
            add("archEyebrow");
            add("browPosition");
            add("jawSize");
            add("cheekLowBoneNarrow");
            add("eyeAngle");
            add("eyeInnerConer");
            add("eyeOuterConer");
            add("eyeDis");
            add("eyeHeight");
            add("forehead");
            add("cheekBoneNarrow");

            add("eyelidAlpha");
            add("eyemazingAlpha");

            add("whitenTeethAlpha");
            add("eyeDetailAlpha");
            add("removePouchAlpha");
            add("removeWrinklesAlpha");
        }
    };

    /**
     * ???????????????
     */
    private ParamsConfigView.FilterConfigViewSeekBarDelegate mBeautyPlasticConfigViewSeekBarDelegate =
            new ParamsConfigView.FilterConfigViewSeekBarDelegate() {
                @Override
                public void onSeekbarDataChanged(FilterConfigSeekbar seekbar, SelesParameters.FilterArg arg) {
//                    if (isBeautyChecked)
//                        submitSkinParamter(arg.getKey(), seekbar.getSeekbar().getProgress());
//                    else
//                        submitPlasticFaceParamter(arg.getKey(), seekbar.getSeekbar().getProgress());
                }
            };

    /**
     * ??????Item????????????
     */
    BeautyRecyclerAdapter.OnBeautyItemClickListener beautyItemClickListener =
            new BeautyRecyclerAdapter.OnBeautyItemClickListener() {
                @Override
                public void onChangeSkin(View v, String key, Constants.SkinMode skinMode) {
                    mBeautyPlasticsConfigView.setVisibility(VISIBLE);
                    if (skinMode != mCurrentSkinMode)
                        switchConfigSkin(skinMode);

                    SelesParameters.FilterArg filterArg = mSkinParameters.getFilterArg(key);
                    mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(filterArg));
                }

                @Override
                public void onClear() {
                    hideBeautyBarLayout();

                    mBeautyManager.setBeautyStyle(Beauty.BeautySkinMode.None);
                    mCurrentSkinMode = null;
                    isBeautyClose = true;
                }
            };

    /**
     * ?????????Item????????????
     */
    BeautyPlasticRecyclerAdapter.OnBeautyPlasticItemClickListener beautyPlasticItemClickListener = new BeautyPlasticRecyclerAdapter.OnBeautyPlasticItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            mBeautyPlasticsConfigView.setVisibility(VISIBLE);
            switchBeautyPlasticConfig(position);
        }

        @Override
        public void onClear() {

            hideBeautyBarLayout();

            AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            adBuilder.setTitle(R.string.lsq_text_beauty_type);
            adBuilder.setMessage(R.string.lsq_clear_beauty_plastic_hit);
            adBuilder.setNegativeButton(R.string.lsq_audioRecording_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            adBuilder.setPositiveButton(R.string.lsq_audioRecording_next, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (String key : mDefaultBeautyPercentParams.keySet()) {
                        TLog.e("key -- %s", mDefaultBeautyPercentParams.get(key));
                        submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
                    }
                    dialog.dismiss();
                }
            });
            adBuilder.show();
        }
    };

    /**
     * ???????????????????????????
     */
    private void hideBeautyBarLayout() {
        mBeautyPlasticsConfigView.setVisibility(GONE);

    }

    /**
     * ????????????????????????Tab
     *
     * @param view
     */
    private void switchBeautyConfigTab(View view) {
        int id = view.getId();// ??????
        if (id == R.id.lsq_beauty_tab) {
            isBeautyChecked = true;
            ((TextView) findViewById(R.id.lsq_beauty_tab)).setTextColor(getResources().getColor(R.color.lsq_color_white));
            ((TextView) findViewById(R.id.lsq_beauty_plastic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
            ((TextView) findViewById(R.id.lsq_cosmetic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
            findViewById(R.id.lsq_beauty_tab_line).setBackgroundResource(R.color.lsq_color_white);
            findViewById(R.id.lsq_beauty_plastic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
            findViewById(R.id.lsq_cosmetic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
            mCosmeticList.setVisibility(View.GONE);
            mBeautyRecyclerView.setVisibility(View.VISIBLE);
            mBeautyRecyclerView.setAdapter(mBeautyRecyclerAdapter);
            hideBeautyBarLayout();
            // ?????????
        } else if (id == R.id.lsq_beauty_plastic_tab) {
            isBeautyChecked = false;
            ((TextView) findViewById(R.id.lsq_beauty_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
            ((TextView) findViewById(R.id.lsq_beauty_plastic_tab)).setTextColor(getResources().getColor(R.color.lsq_color_white));
            ((TextView) findViewById(R.id.lsq_cosmetic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));

            findViewById(R.id.lsq_beauty_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
            findViewById(R.id.lsq_beauty_plastic_tab_line).setBackgroundResource(R.color.lsq_color_white);
            findViewById(R.id.lsq_cosmetic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
            mCosmeticList.setVisibility(View.GONE);
            mBeautyRecyclerView.setVisibility(View.VISIBLE);
            mBeautyRecyclerView.setAdapter(mBeautyPlasticRecyclerAdapter);
            mBeautyRecyclerView.scrollToPosition(mBeautyPlasticRecyclerAdapter.getCurrentPos() - 1);
            int currentPos = mBeautyPlasticRecyclerAdapter.getCurrentPos();
            if (currentPos != -1) {
                switchBeautyPlasticConfig(currentPos);
            } else {
                hideBeautyBarLayout();
            }
            //??????
        } else if (id == R.id.lsq_cosmetic_tab) {
            isBeautyChecked = false;
            isCosmeticChecked = true;
            ((TextView) findViewById(R.id.lsq_cosmetic_tab)).setTextColor(getResources().getColor(R.color.lsq_color_white));
            findViewById(R.id.lsq_cosmetic_tab_line).setBackgroundResource(R.color.lsq_color_white);

            ((TextView) findViewById(R.id.lsq_beauty_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
            ((TextView) findViewById(R.id.lsq_beauty_plastic_tab)).setTextColor(getResources().getColor(R.color.lsq_alpha_white_66));
            findViewById(R.id.lsq_beauty_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);
            findViewById(R.id.lsq_beauty_plastic_tab_line).setBackgroundResource(R.color.lsq_alpha_white_00);

            mCosmeticList.setVisibility(View.VISIBLE);
            mBeautyRecyclerView.setVisibility(View.GONE);
            if (mCurrentType != null) {
                mBeautyPlasticsConfigView.setVisibility(View.VISIBLE);
                switch (mCurrentType) {
                    case Lipstick:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("lipAlpha")));
                        break;
                    case Blush:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("blushAlpha")));
                        break;
                    case Eyebrow:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyebrowAlpha")));
                        break;
                    case Eyeshadow:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyeshadowAlpha")));
                        break;
                    case Eyeliner:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyelineAlpha")));
                        break;
                    case Eyelash:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("eyelashAlpha")));
                        break;
                    case Facial:
                        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(mController.getEffect().getFilterArg("facialAlpha")));
                        break;
                }
            } else {
                hideBeautyBarLayout();
            }
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param isVisible true??????false??????
     */
    private void setBeautyViewVisible(boolean isVisible) {

        if (isVisible) {
            setBeautyLayout(true);
            setTextButtonDrawableTop(mBeautyButton, R.drawable.video_nav_ic_beauty_selected);

            TextView lsq_beauty_tab = findViewById(R.id.lsq_beauty_tab);
            TextView lsq_beauty_shape_tab = findViewById(R.id.lsq_beauty_plastic_tab);
            TextView lsq_cosmetic_tab = findViewById(R.id.lsq_cosmetic_tab);

            lsq_beauty_tab.setTag(0);
            lsq_beauty_shape_tab.setTag(1);

            lsq_beauty_tab.setOnClickListener(onClickListener);
            lsq_beauty_shape_tab.setOnClickListener(onClickListener);
            lsq_cosmetic_tab.setOnClickListener(onClickListener);

            if (isCosmeticChecked) {
                switchBeautyConfigTab(lsq_cosmetic_tab);
            } else {
                switchBeautyConfigTab(isBeautyChecked ? lsq_beauty_tab : lsq_beauty_shape_tab);
            }
        } else {
            setBeautyLayout(false);
            setTextButtonDrawableTop(mBeautyButton, R.drawable.video_nav_ic_beauty);
        }
    }

    /**
     * ??????????????????
     *
     * @param isVisible ????????????
     */
    private void setBeautyLayout(boolean isVisible) {
        mSmartBeautyTabLayout.setVisibility(isVisible ? VISIBLE : GONE);
    }

    private SelesParameters mSkinParameters;

    private Constants.SkinMode mCurrentSkinMode;

    /**
     * ????????????????????????
     *
     * @param skinMode true ??????(??????)?????? false ????????????
     */
    private void switchConfigSkin(Constants.SkinMode skinMode) {

        SelesParameters selesParameters = new SelesParameters();
        selesParameters.appendFloatArg("whitening", 0.3f);
        selesParameters.appendFloatArg("smoothing", 0.8f);

        switch (skinMode) {
            case SkinNatural:
                selesParameters.appendFloatArg("ruddy", 0.4f);

                mBeautyManager.setBeautyStyle(Beauty.BeautySkinMode.SkinNatural);
                mBeautyManager.setSmoothLevel(0.8f);
                mBeautyManager.setWhiteningLevel(0.3f);
                mBeautyManager.setRuddyLevel(0.4f);
                break;
            case SkinMoist:
                selesParameters.appendFloatArg("ruddy", 0.4f);

                mBeautyManager.setBeautyStyle(Beauty.BeautySkinMode.SkinMoist);
                mBeautyManager.setSmoothLevel(0.8f);
                mBeautyManager.setWhiteningLevel(0.3f);
                mBeautyManager.setRuddyLevel(0.4f);
                break;
            case Beauty:
                selesParameters.appendFloatArg("sharpen", 0.6f);

                mBeautyManager.setBeautyStyle(Beauty.BeautySkinMode.Beauty);

                mBeautyManager.setSmoothLevel(0.8f);
                mBeautyManager.setWhiteningLevel(0.3f);
                mBeautyManager.setSharpenLevel(0.6f);
                break;
        }

        selesParameters.setListener(new SelesParameters.SelesParametersListener() {
            @Override
            public void onUpdateParameters(SelesParameters.FilterModel model, String code, SelesParameters.FilterArg arg) {

                String key = arg.getKey();
                double progress = arg.getPrecentValue();
                switch (key) {
                    case "whitening":
                        mBeautyManager.setWhiteningLevel((float) progress);
                        break;
                    case "smoothing":
                        mBeautyManager.setSmoothLevel((float) progress);
                        break;
                    case "ruddy":
                        mBeautyManager.setRuddyLevel((float) progress);
                        break;
                    case "sharpen":
                        mBeautyManager.setSharpenLevel((float) progress);
                        break;
                }
            }
        });
        mSkinParameters = selesParameters;
        mCurrentSkinMode = skinMode;

        // ???????????????
        showHitTitle(TuSdkContext.getString(getSkinModeTitle(skinMode)));

        isBeautyClose = false;
    }

    private String getSkinModeTitle(Constants.SkinMode skinMode) {
        switch (skinMode) {
            case SkinNatural:
                return "lsq_beauty_skin_precision";
            case SkinMoist:
                return "lsq_beauty_skin_extreme";
            case Beauty:
                return "lsq_beauty_skin_beauty";
        }
        return "";
    }

    private SelesParameters mPlasticParameter;

    /**
     * ?????????????????????
     *
     * @param position
     */
    private void switchBeautyPlasticConfig(int position) {
        mPropsItemPagerAdapter.notifyAllPageData();



        if (mBeautyManager.hasPlastic()) {
            initPlastic();
        }

        SelesParameters.FilterArg filterArg = mPlasticParameter.getFilterArg(mBeautyPlastics.get(position));
        mBeautyPlasticsConfigView.setFilterArgs(Arrays.asList(filterArg));


    }

    private void initPlastic() {
        SelesParameters parameters = new SelesParameters();
        for (String key : mDefaultBeautyPercentParams.keySet()) {
            float value = mDefaultBeautyPercentParams.get(key);
            if (mReshapePlastics.contains(key)) {
                parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                switch (key) {
                    case "eyelidAlpha":
                        mBeautyManager.setEyelidLevel(value);
                        break;
                    case "eyemazingAlpha":
                        mBeautyManager.setEyemazingLevel(value);
                        break;
                    case "whitenTeethAlpha":
                        mBeautyManager.setWhitenTeethLevel(value);
                        break;
                    case "eyeDetailAlpha":
                        mBeautyManager.setEyeDetailLevel(value);
                        break;
                    case "removePouchAlpha":
                        mBeautyManager.setRemovePouchLevel(value);
                        break;
                    case "removeWrinklesAlpha":
                        mBeautyManager.setRemoveWrinklesLevel(value);
                        break;
                }
            } else {
                switch (key) {
                    case "eyeSize":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setEyeEnlargeLevel(value);
                        break;
                    case "chinSize":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setCheekThinLevel(value);
                        break;
                    case "cheekNarrow":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setCheekNarrowLevel(value);
                        break;
                    case "smallFace":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setFaceSmallLevel(value);
                        break;
                    case "noseSize":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setNoseWidthLevel(value);
                        break;
                    case "noseHeight":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setNoseHeightLevel(value);
                        break;
                    case "mouthWidth":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setMouthWidthLevel(value);
                        break;
                    case "lips":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setLipsThicknessLevel(value);
                        break;
                    case "philterum":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setPhilterumThicknessLevel(value);
                        break;
                    case "archEyebrow":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setBrowThicknessLevel(value);
                        break;
                    case "browPosition":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setBrowHeightLevel(value);
                        break;
                    case "jawSize":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setChinThicknessLevel(value);
                        break;
                    case "cheekLowBoneNarrow":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setCheekLowBoneNarrowLevel(value);
                        break;
                    case "eyeAngle":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setEyeAngleLevel(value);
                        break;
                    case "eyeInnerConer":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setEyeInnerConerLevel(value);
                        break;
                    case "eyeOuterConer":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setEyeOuterConerLevel(value);
                        break;
                    case "eyeDis":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setEyeDistanceLevel(value);
                        break;
                    case "eyeHeight":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setEyeHeightLevel(value);
                        break;
                    case "forehead":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key), -1, 1);
                        mBeautyManager.setForeheadHeightLevel(value);
                        break;
                    case "cheekBoneNarrow":
                        parameters.appendFloatArg(key, mDefaultBeautyPercentParams.get(key));
                        mBeautyManager.setCheekBoneNarrowLevel(value);
                        break;

                }
            }
        }
        parameters.setListener(new SelesParameters.SelesParametersListener() {
            @Override
            public void onUpdateParameters(SelesParameters.FilterModel model, String code, SelesParameters.FilterArg arg) {
                float value = arg.getValue();
                String key = arg.getKey();
                submitPlastic(value, key);
            }
        });

        mPlasticParameter = parameters;

    }

    private void submitPlastic(float value, String key) {
        switch (key) {
            case "eyeSize":
                mBeautyManager.setEyeEnlargeLevel(value);
                break;
            case "chinSize":
                mBeautyManager.setCheekThinLevel(value);
                break;
            case "cheekNarrow":
                mBeautyManager.setCheekNarrowLevel(value);
                break;
            case "smallFace":
                mBeautyManager.setFaceSmallLevel(value);
                break;
            case "noseSize":
                mBeautyManager.setNoseWidthLevel(value);
                break;
            case "noseHeight":
                mBeautyManager.setNoseHeightLevel(value);
                break;
            case "mouthWidth":
                mBeautyManager.setMouthWidthLevel(value);
                break;
            case "lips":
                mBeautyManager.setLipsThicknessLevel(value);
                break;
            case "philterum":
                mBeautyManager.setPhilterumThicknessLevel(value);
                break;
            case "archEyebrow":
                mBeautyManager.setBrowThicknessLevel(value);
                break;
            case "browPosition":
                mBeautyManager.setBrowHeightLevel(value);
                break;
            case "jawSize":
                mBeautyManager.setChinThicknessLevel(value);
                break;
            case "cheekLowBoneNarrow":
                mBeautyManager.setCheekLowBoneNarrowLevel(value);
                break;
            case "eyeAngle":
                mBeautyManager.setEyeAngleLevel(value);
                break;
            case "eyeInnerConer":
                mBeautyManager.setEyeInnerConerLevel(value);
                break;
            case "eyeOuterConer":
                mBeautyManager.setEyeOuterConerLevel(value);
                break;
            case "eyeDis":
                mBeautyManager.setEyeDistanceLevel(value);
                break;
            case "eyeHeight":
                mBeautyManager.setEyeHeightLevel(value);
                break;
            case "forehead":
                mBeautyManager.setForeheadHeightLevel(value);
                break;
            case "cheekBoneNarrow":
                mBeautyManager.setCheekBoneNarrowLevel(value);
                break;
            case "eyelidAlpha":
                mBeautyManager.setEyelidLevel(value);
                break;
            case "eyemazingAlpha":
                mBeautyManager.setEyemazingLevel(value);
                break;
            case "whitenTeethAlpha":
                mBeautyManager.setWhitenTeethLevel(value);
                break;
            case "eyeDetailAlpha":
                mBeautyManager.setEyeDetailLevel(value);
                break;
            case "removePouchAlpha":
                mBeautyManager.setRemovePouchLevel(value);
                break;
            case "removeWrinklesAlpha":
                mBeautyManager.setRemoveWrinklesLevel(value);
                break;

        }
        mPlasticParameter.getFilterArg(key).setValue((float) value);
    }

    /**
     * ???????????????
     *
     * @param key
     * @param progress
     */
    private void submitPlasticFaceParamter(String key, float progress) {
        submitPlastic(progress, key);
    }


    /******************************** ?????? ************************/
    /**
     * ????????????????????????
     *
     * @param isShow true??????false??????
     */
    private void updatePreviewImageLayoutStatus(boolean isShow) {
        ThreadHelper.post(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.lsq_preview_image_layout).setVisibility(isShow ? VISIBLE : GONE);
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param
     */
    public void presentPreviewLayout(TuSdkResult result) {
        if (result.image != null) {
            mCurrentResult = result;
            mCaptureBitmap = result.image;
            updatePreviewImageLayoutStatus(true);
            mPreViewImageView.setImageBitmap(result.image);
            // ????????????
            mCamera.pausePreview();
        }
    }

    /**
     * ??????????????????
     */
    public void saveResource() {
        updatePreviewImageLayoutStatus(false);
        File file = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            file = AlbumHelper.getAlbumFileAndroidQ();
        } else {
            file = AlbumHelper.getAlbumFile();
        }
        ImageSqlHelper.saveJpgToAblum(mContext, mCaptureBitmap, 80, file, mCurrentResult.metadata);
        refreshFile(file);
        destroyBitmap();
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, getStringFromResource("lsq_image_save_ok"), Toast.LENGTH_SHORT).show();
            }
        });

        mCamera.resumePreview();
    }

    /**
     * ????????????
     *
     * @param file
     */
    public void refreshFile(File file) {
        if (file == null) {
            TLog.e("refreshFile file == null");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mContext.sendBroadcast(intent);
    }

    /**
     * ??????????????????
     */
    public void deleteResource() {
        updatePreviewImageLayoutStatus(false);
        destroyBitmap();
        mCamera.resumePreview();
    }

    /**
     * ??????????????????
     */
    private void destroyBitmap() {
        if (mCaptureBitmap == null) return;

        if (!mCaptureBitmap.isRecycled())
            mCaptureBitmap.recycle();

        mCaptureBitmap = null;
    }

    /********************************** ???????????? ************************/
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();// ??????
            if (id == R.id.lsq_closeButton) {
                if (getDelegate() != null) getDelegate().finishRecordActivity();
                // ???????????????
            } else if (id == R.id.lsq_switchButton) {
                mCamera.rotateCamera();
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                if (mCamera.getFacing() == CameraConfigs.CameraFacing.Front) {
                    mCamera.cameraFocus().setFocus(new PointF(0.5f, 0.5f), null);
                }
                // ??????????????????????????????
            } else if (id == R.id.lsq_beautyButton) {
                setFilterContentVisible(false);
                setBottomViewVisible(mSmartBeautyTabLayout.getVisibility() == VISIBLE);
                setBeautyViewVisible(mSmartBeautyTabLayout.getVisibility() == GONE);
                setStickerVisible(false);
                setSpeedViewVisible(false);
                getFocusTouchView().isShowFoucusView(false);
                // ??????
            } else if (id == R.id.lsq_speedButton) {
                setFilterContentVisible(false);
                setBottomViewVisible(true);
                setStickerVisible(false);
                setBeautyViewVisible(false);
                setSpeedViewVisible(mSpeedModeBar.getVisibility() == GONE);
                // ????????????
            } else if (id == R.id.lsq_moreButton) {
                mMoreConfigLayout.setVisibility(mMoreConfigLayout.getVisibility() == VISIBLE ? GONE : VISIBLE);
                setTextButtonDrawableTop(mMoreButton, mMoreConfigLayout.getVisibility() == VISIBLE ? R.drawable.video_nav_ic_more_selected : R.drawable.video_nav_ic_more);
                // ??????????????????
            } else if (id == R.id.lsq_focus_open) {
                mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mFocusClose.setTextColor(getResources().getColor(R.color.lsq_color_white));

                mCamera.cameraFocus().setDisableContinueFocus(false);
                // ??????????????????
            } else if (id == R.id.lsq_focus_close) {
                mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mFocusClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));

                mCamera.cameraFocus().setDisableContinueFocus(true);
                // ???????????????
            } else if (id == R.id.lsq_lighting_open) {
                updateFlashMode(CameraConfigs.CameraFlash.Torch);
                // ???????????????
            } else if (id == R.id.lsq_lighting_close) {
                updateFlashMode(CameraConfigs.CameraFlash.Off);
                // ??????
            } else if (id == R.id.lsq_beauty_tab) {
                isCosmeticChecked = false;
                switchBeautyConfigTab(v);
                // ?????????
            } else if (id == R.id.lsq_beauty_plastic_tab) {
                isCosmeticChecked = false;
                switchBeautyConfigTab(v);
            } else if (id == R.id.lsq_cosmetic_tab) {
                isCosmeticChecked = true;
                switchBeautyConfigTab(v);
                // ??????
            } else if (id == R.id.lsq_tab_filter_btn) {
                setBeautyViewVisible(false);
                setBottomViewVisible(false);
                setSpeedViewVisible(false);
                setStickerVisible(false);
                showFilterLayout();
                getFocusTouchView().isShowFoucusView(false);
                // ??????
            } else if (id == R.id.lsq_stickerWrap) {
                setFilterContentVisible(false);
                setBeautyViewVisible(false);
                setSpeedViewVisible(false);
                setBottomViewVisible(false);
                showStickerLayout();
                getFocusTouchView().isShowFoucusView(false);
                // ??????
            } else if (id == R.id.lsq_radio_1_1) {
                updateCameraRatio(RatioType.ratio_1_1);
            } else if (id == R.id.lsq_radio_3_4) {
                updateCameraRatio(RatioType.ratio_3_4);
            } else if (id == R.id.lsq_radio_full) {
                updateCameraRatio(RatioType.ratio_orgin);
                // ????????????
            } else if (id == R.id.lsq_backWrap) {// ???????????????????????????????????????
                if (getDelegate().getFragmentSize() > 0) {
                    getDelegate().popFragment();
                    mRecordProgress.removePreSegment();

                    if (interuptLayout.getChildCount() != 0) {
                        interuptLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);

                            }
                        });
                    }
                    // ???????????????????????????????????????
                    if (getDelegate().getFragmentSize() == 0) {
                        interuptLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                interuptLayout.removeAllViews();
                            }
                        });

                        updateRecordButtonResource(mRecordMode);
                        setViewHideOrVisible(true);
                        return;
                    }
                }
                // ??????????????????
                setViewHideOrVisible(true);
                // ??????????????????
            } else if (id == R.id.lsq_confirmWrap) {//                    if (mCamera.getMovieDuration() < Constants.MIN_RECORDING_TIME) {
//                        String msg = getStringFromResource("min_recordTime") + Constants.MIN_RECORDING_TIME + "s";
//                        TuSdk.messageHub().showToast(mContext, msg);
//                        return;
//                    }
                // ????????????????????????????????????
                if (mDelegate.stopRecording()) {
                    initRecordProgress();
                    setViewHideOrVisible(true);
                }
                // ????????????
            } else if (id == R.id.lsq_backButton) {
                deleteResource();
                // ????????????
            } else if (id == R.id.lsq_saveImageButton) {
                saveResource();
                // ????????????
            } else if (id == R.id.lsq_cancel_button) {
                mBeautyManager.setDynamicSticker(0);
                mBeautyManager.setMonsterFace("");
                mPropsItemPagerAdapter.notifyAllPageData();
            }
        }
    };


    private OnClickListener mOnSimultaneouslyModeChanged = new OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            if (mDelegate == null) return;
            if (!canChangeLayer) return;
            int id = v.getId();
            if (id == R.id.lsq_top_bottom) {
                mCurrentDoubleViewMode = DoubleViewMode.TopBottom;
                mTopBottomMode.setTextColor(getContext().getColor(R.color.lsq_widget_speedbar_button_bg));
                mLeftRightMode.setTextColor(getContext().getColor(R.color.lsq_color_white));
                mViewInViewMode.setTextColor(getContext().getColor(R.color.lsq_color_white));
            } else if (id == R.id.lsq_left_right) {
                mCurrentDoubleViewMode = DoubleViewMode.LeftRight;
                mTopBottomMode.setTextColor(getContext().getColor(R.color.lsq_color_white));
                mLeftRightMode.setTextColor(getContext().getColor(R.color.lsq_widget_speedbar_button_bg));
                mViewInViewMode.setTextColor(getContext().getColor(R.color.lsq_color_white));
            } else if (id == R.id.lsq_view_in_view) {
                mCurrentDoubleViewMode = DoubleViewMode.ViewInView;
                mTopBottomMode.setTextColor(getContext().getColor(R.color.lsq_color_white));
                mLeftRightMode.setTextColor(getContext().getColor(R.color.lsq_color_white));
                mViewInViewMode.setTextColor(getContext().getColor(R.color.lsq_widget_speedbar_button_bg));
            }
            mDelegate.updateDoubleViewMode(mCurrentDoubleViewMode);
        }
    };

    private OnClickListener mRenderSizeChanged = new OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            if (mDelegate == null) return;
            int id = v.getId();
            if (id == R.id.lsq_render_720) {
                mRender720.setTextColor(getContext().getColor(R.color.lsq_widget_speedbar_button_bg));
                mRender1080.setTextColor(getContext().getColor(R.color.lsq_color_white));
                mDelegate.changeRenderWidth(720);
            } else if (id == R.id.lsq_render_1080) {
                mRender720.setTextColor(getContext().getColor(R.color.lsq_color_white));
                mRender1080.setTextColor(getContext().getColor(R.color.lsq_widget_speedbar_button_bg));
                mDelegate.changeRenderWidth(1080);
            }
        }
    };

    /**
     * ?????????????????????
     *
     * @param cameraFlash
     */
    public void updateFlashMode(CameraConfigs.CameraFlash cameraFlash) {
        if (mCamera.getFacing() == CameraConfigs.CameraFacing.Front) return;
        switch (cameraFlash) {
            case Off:
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));

                mCamera.cameraParams().setFlashMode(cameraFlash);
                break;
            case Torch:
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_color_white));

                mCamera.cameraParams().setFlashMode(cameraFlash);
                break;
        }
    }

    /**
     * ??????????????????
     *
     * @param type
     */
    public void updateCameraRatio(int type) {
        // ?????????????????????????????????
        if (mDelegate.getFragmentSize() > 0) return;
        switch (type) {
            case RatioType.ratio_1_1:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square_selected);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full);
                switchCameraRatio(new Point(1, 1));
                break;
            case RatioType.ratio_3_4:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4_selected);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full);
                switchCameraRatio(new Point(9,  16));
                break;
            case RatioType.ratio_orgin:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full_selected);
                TuSdkSize orgin = mRegionHandle.getWrapSize();
                TuCameraAspectRatio ratio = TuCameraAspectRatio.of(orgin.width, orgin.height);
                switchCameraRatio(new Point(ratio.getX(), ratio.getY()));
                break;
        }
    }

    public void onDoubleView(){
        mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
        mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
        mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full_selected);
    }

    /**
     * ?????????????????? ????????????????????????
     *
     * @param
     */
    private void switchCameraRatio(Point ratio) {
        if (mCamera == null) return;
        TLog.e("current ratio %s", ratio.toString());

        mRegionHandle.setOffsetTopPercent(getPreviewOffsetTopPercent(ratio.x, ratio.y));

        mRegionHandle.changeWithRatio(((float) ratio.x) / ratio.y, new RegionHandler.RegionChangerListener() {
            @Override
            public void onRegionChanged(RectF rectPercent) {
                mDelegate.changedRect(rectPercent);
            }
        });
//
        mDelegate.changedRatio(TuSdkSize.create(ratio.x, ratio.y));


        // ????????????????????????????????? ????????? changeRegionRatio ????????????
//        mCamera.getRegionHandler().setOffsetTopPercent(getPreviewOffsetTopPercent(type));
//        mCamera.changeRegionRatio(RatioType.ratio(type));
//        mCamera.setRegionRatio(RatioType.ratio(type));

        // ??????????????????
//        mCamera.getVideoEncoderSetting().videoSize = TuSdkSize.create((int) (mCamera.getCameraPreviewSize().width * RatioType.ratio(type)), mCamera.getCameraPreviewSize().width);

    }

    /**
     * ???????????? Ratio ?????????????????????????????????????????????-1 ???????????? ???????????????0-1???
     *
     * @param
     * @return
     */
    protected float getPreviewOffsetTopPercent(int x, int y) {
        if (x == 1 && y == 1) return 0.1f;
        // ??????
        return 0.f;
    }

    /************************ ?????????????????? **************************/
    /**
     * ????????????????????????
     */
    private ValueAnimator valueAnimator;
    /**
     * ??????????????????
     */
    private int mRecordMode = RecordType.SHORT_CLICK_RECORD;

    private float mPosX, mCurPosX;
    private static final int FLING_MIN_DISTANCE = 20;// ??????????????????

//    private OnTouchListener onModeBarTouchListener = new OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    mPosX = event.getX();
//                    mCurPosX = 0;
//                    return true;
//                case MotionEvent.ACTION_MOVE:
//                    mCurPosX = event.getX();
//                    // ??????????????????
//                    if (mCurPosX - mPosX > 0
//                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
//                        //????????????
//                        if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
//                            switchCameraModeButton(RecordType.CAPTURE);
//                        } else if (mRecordMode == RecordType.DOUBLE_VIEW_RECORD) {
//                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
//                        }
//                        return false;
//                    } else if (mCurPosX - mPosX < 0
//                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
//                        //????????????
//                        if (mRecordMode == RecordType.CAPTURE) {
//                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
//                        } else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
//                            switchCameraModeButton(RecordType.DOUBLE_VIEW_RECORD);
//                        }
//                        return false;
//                    }
//                    return true;
//                case MotionEvent.ACTION_UP:
//                    // ??????????????????
//                    if (Math.abs(mCurPosX - mPosX) < FLING_MIN_DISTANCE || mCurPosX == 0) {
//                        int id = v.getId();// ????????????
//                        if (id == R.id.lsq_shootButton) {
//                            switchCameraModeButton(RecordType.CAPTURE);
//                            // ??????????????????
//                        } else if (id == R.id.lsq_clickButton) {
//                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
//                        }
////                        else if (id == R.id.lsq_double_view_Button) {
////                            switchCameraModeButton(RecordType.DOUBLE_VIEW_RECORD);
////                        }
//                        return false;
//                    }
//            }
//            return false;
//        }
//    };

    /**
     * ????????????????????????
     *
     * @param index
     */
    public void switchCameraModeButton(int index) {
        if (valueAnimator != null && valueAnimator.isRunning() || mRecordMode == index) return;

        // ??????????????????
        mShootButton.setTextColor(index == 0 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mClickButton.setTextColor(index == 2 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        //mDoubleViewButton.setTextColor(index == 5 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));

        // ??????????????????
        final float[] Xs = getModeButtonWidth();

        float offSet = 0;
        if (mRecordMode == 0 && index == 2)
            offSet = -(Xs[1] - Xs[0]) / 2 - (Xs[2] - Xs[1]) / 2;
        else if (mRecordMode == 0 && index == 5)
            offSet = -(Xs[1] - Xs[0]) / 2 - (Xs[3] - Xs[2]) / 2 - (Xs[2] - Xs[1]);
        else if (mRecordMode == 2 && index == 0)
            offSet = (Xs[1] - Xs[0]) / 2 + (Xs[2] - Xs[1]) / 2;
        else if (mRecordMode == 2 && index == 5)
            offSet = -(Xs[2] - Xs[1]) / 2  - (Xs[3] - Xs[2]) / 2;
        else if (mRecordMode == 5 && index == 0)
            offSet = (Xs[1] - Xs[0]) / 2 + (Xs[2] - Xs[1]) + (Xs[3] - Xs[2]) / 2;
        else if (mRecordMode == 5 && index == 2)
            offSet = (Xs[2] - Xs[1]) / 2 + (Xs[3] - Xs[2]) / 2;

        // ????????????
        valueAnimator = ValueAnimator.ofFloat(0, offSet);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offSet = (float) animation.getAnimatedValue();
                mShootButton.setX(Xs[0] + offSet);
                mClickButton.setX(Xs[1] + offSet);
               // mDoubleViewButton.setX(Xs[2] + offSet);
            }
        });
        valueAnimator.start();

        // ??????????????????
        if (index == RecordType.CAPTURE) {
            mSpeedButton.setVisibility(GONE);
            mSpeedModeBar.setVisibility(GONE);
            mChangeAudioLayout.setVisibility(GONE);
            mSimultaneouslyLayer.setVisibility(GONE);

            findViewById(R.id.lsq_camera_radio_layer).setVisibility(View.VISIBLE);
        } else if (index == RecordType.SHORT_CLICK_RECORD) {
            mSpeedButton.setVisibility(VISIBLE);
            mSpeedModeBar.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mChangeAudioLayout.setVisibility(VISIBLE);
            mSimultaneouslyLayer.setVisibility(GONE);

            findViewById(R.id.lsq_camera_radio_layer).setVisibility(View.VISIBLE);
        } else if (index == RecordType.DOUBLE_VIEW_RECORD) {
            mSpeedButton.setVisibility(VISIBLE);
            mSpeedModeBar.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mChangeAudioLayout.setVisibility(VISIBLE);
            mSimultaneouslyLayer.setVisibility(VISIBLE);

            if (mDelegate != null)
                mDelegate.selectVideo();

            findViewById(R.id.lsq_camera_radio_layer).setVisibility(View.GONE);
        }
        if (index != RecordType.DOUBLE_VIEW_RECORD) {
            if (mDelegate != null) mDelegate.updateDoubleViewMode(DoubleViewMode.None);
            mSelectAudio.setVisibility(View.VISIBLE);
        } else if ( index == RecordType.DOUBLE_VIEW_RECORD){
            mSelectAudio.setVisibility(View.GONE);
        }
        updateRecordButtonResource(index);
        mRecordMode = index;
    }

    /**
     * ????????????????????????????????????
     */
    private float[] getModeButtonWidth() {
        float[] Xs = new float[4];
        Xs[0] = mShootButton.getX();
        Xs[1] = mClickButton.getX();
        Xs[2] = 0;
        Xs[3] =0;
        return Xs;
    }

    /**
     * ????????????
     *
     * @param selectedSpeedMode
     */
    private void selectSpeedMode(double selectedSpeedMode) {
        int childCount = mSpeedModeBar.getChildCount();

        for (int i = 0; i < childCount; i++) {
            Button btn = (Button) mSpeedModeBar.getChildAt(i);
            double speedMode = Double.parseDouble((String) btn.getTag());

            if (selectedSpeedMode == speedMode) {
                btn.setBackgroundResource(R.drawable.tusdk_view_widget_speed_button_bg);
            } else {
                btn.setBackgroundResource(0);
            }
        }

        getDelegate().changedSpeed(selectedSpeedMode);

//        // ??????????????????
//        TuSdkRecorderVideoCamera.SpeedMode speedMode = TuSdkRecorderVideoCamera.SpeedMode.values()[selectedSpeedMode];
//        mCamera.setSpeedMode(speedMode);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param isVisible ???????????? true??????false??????
     */
    private void setSpeedViewVisible(boolean isVisible) {
        isSpeedChecked = isVisible;
        if (isVisible) {
            setTextButtonDrawableTop(mSpeedButton, R.drawable.video_nav_ic_speed_selected);
            mSpeedModeBar.setVisibility(VISIBLE);
        } else {
            setTextButtonDrawableTop(mSpeedButton, R.drawable.video_nav_ic_speed);
            mSpeedModeBar.setVisibility(GONE);
        }
    }

    /****************************** ???????????? ****************************/

    /**
     * ??????????????????
     *
     * @param textButton ??????
     * @param id         ??????id
     */
    private void setTextButtonDrawableTop(TextView textButton, @DrawableRes int id) {
        Drawable top = getResources().getDrawable(id);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        textButton.setCompoundDrawables(null, top, null, null);
    }

    /**
     * ???????????????????????? ?????????????????????????????????
     *
     * @param isVisible ????????????
     */
    private void setBottomViewVisible(boolean isVisible) {
        mBottomBarLayout.setVisibility(isVisible ? VISIBLE : GONE);
        mRecordButton.setVisibility(isVisible ? VISIBLE : GONE);
        mRecordModeBarLayout.setVisibility(isVisible && mDelegate.getFragmentSize() <= 0 ? VISIBLE : GONE);
        mRollBackButton.setVisibility(isVisible && mDelegate.getFragmentSize() > 0 ? VISIBLE : GONE);
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param isVisible ????????????
     */
    private void setViewHideOrVisible(boolean isVisible) {
        int visibleState = isVisible ? VISIBLE : GONE;

        mTopBar.setVisibility(visibleState);
        mSpeedModeBar.setVisibility(isVisible && isSpeedChecked ? visibleState : GONE);
        mBottomBarLayout.setVisibility(visibleState);
        mRecordModeBarLayout.setVisibility(visibleState);
        mConfirmButton.setVisibility(GONE);
        mRollBackButton.setVisibility(GONE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);

        if (mDelegate.getFragmentSize() > 0) {
            layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            mConfirmButton.setVisibility(visibleState);
            mRollBackButton.setVisibility(visibleState);
            mRecordModeBarLayout.setVisibility(GONE);
        }
        mFilterButton.setLayoutParams(layoutParams);
    }

    /**
     * ????????????????????????
     *
     * @param type
     */
    private void updateRecordButtonResource(int type) {
        switch (type) {
            case RecordType.CAPTURE:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_shoot);
                mRecordButton.setImageResource(0);
                break;
            case RecordType.SHORT_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                mRecordButton.setImageResource(R.drawable.video_ic_recording);
                break;
            case RecordType.SHORT_CLICK_RECORDING:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                mRecordButton.setImageResource(R.drawable.video_ic_recording);
                break;
            case RecordType.DOUBLE_VIEW_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                mRecordButton.setImageResource(R.drawable.video_ic_recording);
                break;

        }
    }

    /**
     * ??????????????????
     *
     * @param isVisible ????????????
     */
    private void setFilterContentVisible(boolean isVisible) {
        mFilterContent.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }


    /********************************** ???????????? ***********************/

    /**
     * ????????????????????????
     *
     * @param
     * @param recording ?????????????????????
     */
    public void updateMovieRecordState(RecordState state, boolean recording) {

        if (state == RecordState.Recording) // ????????????
        {
            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
            setViewHideOrVisible(false);
            mMoreConfigLayout.setVisibility(GONE);
            setTextButtonDrawableTop(mMoreButton, false ? R.drawable.video_nav_ic_more_selected : R.drawable.video_nav_ic_more);
            mSelectAudio.setVisibility(View.GONE);
            mSimultaneouslyLayer.setVisibility(View.VISIBLE);
            canChangeLayer = false;

            switch (mCurrentDoubleViewMode){
                case None:
                    break;
                case ViewInView:
                    mTopBottomMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_gray));
                    mLeftRightMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_gray));
                    mViewInViewMode.setTextColor(getContext().getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                    break;
                case TopBottom:
                    mTopBottomMode.setTextColor(getContext().getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                    mLeftRightMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_gray));
                    mViewInViewMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_gray));
                    break;
                case LeftRight:
                    mTopBottomMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_gray));
                    mLeftRightMode.setTextColor(getContext().getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                    mViewInViewMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_gray));
                    break;
            }



        } else if (state == RecordState.Paused) // ???????????????
        {
            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint(TuSdkContext.getDisplaySize().width * mRecordProgress.getProgress());
            }
            mRecordProgress.pauseRecord();
            setViewHideOrVisible(true);
            updateRecordButtonResource(mRecordMode);
        } else if (state == RecordState.RecordCompleted) //????????????????????????????????????????????????????????????????????????????????????
        {
            getDelegate().pauseRecording();
            String msg = getStringFromResource("lsq_record_completed");
            post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            });


            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint(TuSdkContext.getDisplaySize().width * 0.999f);
            }
            updateRecordButtonResource(mRecordMode);
            setViewHideOrVisible(true);

        } else if (state == RecordState.Saving) // ??????????????????
        {
            String msg = getStringFromResource("new_movie_saving");
            post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            });

        } else if (state == RecordState.SaveCompleted) {
            String msg = getStringFromResource("lsq_video_save_ok");
            post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    if (mRecordMode != RecordType.DOUBLE_VIEW_RECORD){
                        mSelectAudio.setVisibility(View.VISIBLE);
                    } else if (mRecordMode == RecordType.DOUBLE_VIEW_RECORD){
                        mSimultaneouslyLayer.setVisibility(View.VISIBLE);
                        canChangeLayer = true;
                        switch (mCurrentDoubleViewMode){
                            case None:
                                break;
                            case ViewInView:
                                mTopBottomMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                                mLeftRightMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                                mViewInViewMode.setTextColor(getContext().getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                                break;
                            case TopBottom:
                                mTopBottomMode.setTextColor(getContext().getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                                mLeftRightMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                                mViewInViewMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                                break;
                            case LeftRight:
                                mTopBottomMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                                mLeftRightMode.setTextColor(getContext().getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                                mViewInViewMode.setTextColor(getContext().getResources().getColor(R.color.lsq_color_white));
                                break;
                        }
                    }
                }
            });


            updateRecordButtonResource(mRecordMode);
            setViewHideOrVisible(true);
        } else if (state == RecordState.RecordTimeOut) {
            String msg = getStringFromResource("lsq_max_audio_record_time");

            post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            });


            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
            setViewHideOrVisible(true);
        }
    }

    /**
     * ????????????????????????
     *
     * @param margingLeft
     */
    private void addInteruptPoint(float margingLeft) {
        // ??????????????????
        View interuptBtn = new View(mContext);
        LayoutParams lp = new LayoutParams(2,
                LayoutParams.MATCH_PARENT);

        interuptBtn.setBackgroundColor(TuSdkContext.getColor("lsq_progress_interupt_color"));
        lp.setMargins((int) Math.ceil(margingLeft), 0, 0, 0);
        interuptBtn.setLayoutParams(lp);
        interuptLayout.addView(interuptBtn);
    }

    /**
     * ??????????????????
     *
     * @param progress
     * @param durationTime
     */
    public void updateViewOnMovieRecordProgressChanged(float progress, float durationTime) {
        TLog.e("progress -- %s durationTime -- %s", progress, durationTime);
        mRecordProgress.setProgress(progress);
    }

    /**
     * ?????????????????????????????????
     *
     * @param
     * @param isRecording
     */
    public void updateViewOnMovieRecordFailed(/*TuSdkRecorderVideoCamera.RecordError error,*/ boolean isRecording) {
//        if (error == TuSdkRecorderVideoCamera.RecordError.MoreMaxDuration) // ?????????????????? ????????????????????????????????????startRecording???????????????
//        {
//            String msg = getStringFromResource("max_recordTime") + Constants.MAX_RECORDING_TIME + "s";
//            TuSdk.messageHub().showToast(mContext, msg);
//
//        } else if (error == TuSdkRecorderVideoCamera.RecordError.SaveFailed) // ??????????????????
//        {
//            String msg = getStringFromResource("new_movie_error_saving");
//            TuSdk.messageHub().showError(mContext, msg);
//        } else if (error == TuSdkRecorderVideoCamera.RecordError.InvalidRecordingTime) {
//            TuSdk.messageHub().showError(mContext, R.string.lsq_record_time_invalid);
//        }
//        setViewHideOrVisible(true);
    }

    /**
     * ?????????????????????????????????
     *
     * @param isRecording
     */
    public void updateViewOnMovieRecordComplete(boolean isRecording) {
        TuSdk.messageHub().dismissRightNow();
        String msg = getStringFromResource("new_movie_saved");
        TuSdk.messageHub().showSuccess(mContext, msg);

        // ?????????????????????(??????????????????)
        mRecordProgress.clearProgressList();
        setViewHideOrVisible(true);
    }

    /**
     * ?????????????????????
     *
     * @param fieldName
     * @return
     */
    protected String getStringFromResource(String fieldName) {
        int stringID = this.getResources().getIdentifier(fieldName, "string",
                this.mContext.getPackageName());

        return getResources().getString(stringID);
    }


    public void onResume() {

    }

    public TuSdkSize getWrapSize() {
        return mRegionHandle.getWrapSize();
    }

    public void updateAudioNameState(int visibility){
        mSelectAudio.setVisibility(visibility);
    }

    public void setAudioName(String name) {
        mAudioName.setText(name);
    }

    public void updateMinPosition(float leftPercent){
        Button minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);
        LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
        minTimeLayoutParams.leftMargin = (int) (TuSdkContext.getScreenSize().width * leftPercent)
                - TuSdkContext.dip2px(minTimeButton.getWidth());
        minTimeButton.setLayoutParams(minTimeLayoutParams);
    }
}
