package org.lasque.twsdkvideo.video_beauty.views.record;

import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeComic;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeCosmetic;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeFilter;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypePlasticFace;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeReshape;
import static org.lasque.tusdk.video.editor.TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSkinFace;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
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

import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.util.DelayedRunnable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.tusdk.api.audio.preproc.processor.TuSdkAudioPitchEngine;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.components.camera.TuSdkVideoFocusTouchViewBase;
import org.lasque.tusdk.core.seles.SelesParameters;
import org.lasque.tusdk.core.seles.tusdk.FilterGroup;
import org.lasque.tusdk.core.seles.tusdk.FilterOption;
import org.lasque.tusdk.core.seles.tusdk.FilterWrap;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.hardware.CameraConfigs;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCamera;
import org.lasque.tusdk.core.utils.hardware.TuSdkRecorderVideoCameraImpl;
import org.lasque.tusdk.core.utils.hardware.TuSdkStillCameraAdapter;
import org.lasque.tusdk.core.utils.image.AlbumHelper;
import org.lasque.tusdk.core.utils.image.RatioType;
import org.lasque.tusdk.core.utils.sqllite.ImageSqlHelper;
import org.lasque.tusdk.core.view.TuSdkViewHelper;
import org.lasque.tusdk.core.view.widget.button.TuSdkTextButton;
import org.lasque.tusdk.video.editor.TuSdkMediaComicEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaCosmeticEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaFilterEffectData;
import org.lasque.tusdk.video.editor.TuSdkMediaPlasticFaceEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaReshapeEffect;
import org.lasque.tusdk.video.editor.TuSdkMediaSkinFaceEffect;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.BackgroundMusicBean;
import org.lasque.twsdkvideo.video_beauty.data.CustomStickerGroup;
import org.lasque.twsdkvideo.video_beauty.data.GVisionDynamicStickerBean;
import org.lasque.twsdkvideo.video_beauty.data.SoundBean;
import org.lasque.twsdkvideo.video_beauty.editor.MovieEditorActivity;
import org.lasque.twsdkvideo.video_beauty.event.BackEvent;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.PublishActivity;
import org.lasque.twsdkvideo.video_beauty.utils.Constants;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.Mp3Player;
import org.lasque.twsdkvideo.video_beauty.utils.TextWidthUtils;
import org.lasque.twsdkvideo.video_beauty.utils.ToastUtils;
import org.lasque.twsdkvideo.video_beauty.views.BeautyPlasticRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.BeautyRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.FilterConfigSeekbar;
import org.lasque.twsdkvideo.video_beauty.views.FilterRecyclerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.HorizontalProgressBar;
import org.lasque.twsdkvideo.video_beauty.views.ParamsConfigView;
import org.lasque.twsdkvideo.video_beauty.views.TabPagerIndicator;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.CosmeticPanelController;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.CosmeticTypes;
import org.lasque.twsdkvideo.video_beauty.views.cosmetic.panel.BasePanel;
import org.lasque.twsdkvideo.video_beauty.views.fragments.BaseFullBottomSheetFragment;
import org.lasque.twsdkvideo.video_beauty.views.newFilterUI.FilterFragment;
import org.lasque.twsdkvideo.video_beauty.views.newFilterUI.FilterViewPagerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.props.PropsItemMonsterPageFragment;
import org.lasque.twsdkvideo.video_beauty.views.props.PropsItemPageFragment;
import org.lasque.twsdkvideo.video_beauty.views.props.PropsItemPagerAdapter;
import org.lasque.twsdkvideo.video_beauty.views.props.StickerPropsItemPageFragment;
import org.lasque.twsdkvideo.video_beauty.views.props.model.PropsItem;
import org.lasque.twsdkvideo.video_beauty.views.props.model.PropsItemCategory;
import org.lasque.twsdkvideo.video_beauty.views.props.model.PropsItemStickerCategory;

import java.io.File;
import java.io.IOException;
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
    public static final String EDITOR_CLASS = MovieEditorActivity.class.getName();
    private ImageView effectsImg;

    public String getStickerId() {
        return stickerId;
    }
    public ArrayList<String> getStickerIds() {
        return stickerIds;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }

    /**
     * ??????????????????
     */
    public interface RecordType {
        // ??????
        int CAPTURE = 0;
        // ????????????
        int LONG_CLICK_RECORD = 1;
        // ????????????
        int SHORT_CLICK_RECORD = 2;
        // ???????????????
        int LONG_CLICK_RECORDING = 3;
        // ???????????????
        int SHORT_CLICK_RECORDING = 4;
    }

    /**
     * ????????????????????????
     */
    public interface TuSDKMovieRecordDelegate {
        /**
         * ??????????????????
         */
        void startRecording();

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
        void stopRecording();

        /**
         * ??????????????????
         */
        void finishRecordActivity();
    }

    public void setDelegate(TuSDKMovieRecordDelegate delegate) {
        mDelegate = delegate;
    }

    public TuSDKMovieRecordDelegate getDelegate() {
        return mDelegate;
    }

    private Context mContext;
    /**
     * ????????????
     */
    protected TuSdkRecorderVideoCameraImpl mCamera;
    /**
     * ????????????????????????
     */
    private TuSDKMovieRecordDelegate mDelegate;
    /**
     * ???????????????Bitmap
     */
    private Bitmap mCaptureBitmap;

    private SharedPreferences mFilterValueMap;

    /**
     * ????????????????????????
     */
    private boolean isSpeedChecked = false;

    /**
     * ??????????????????
     */
    private boolean isBeautyClose = true;

    /**
     * ?????????????????????
     */
    private boolean isOpenFlash = false;

    /**
     * ??????????????????????????????
     */
    private boolean isCheckTransparentButton = false;

    /**
     * ?????????????????????
     */
    private boolean isBackFromEdit = false;

    /**
     * ?????????????????????
     */
    private String musicLocalPath;


    private int mCameraMaxEV = 0;

    private int mCameraMinEV = 0;

    private int mCurrentCameraEV = 80;


    private ViewPager2 mFilterViewPager;
    private TabPagerIndicator mFilterTabIndicator;
    private FilterViewPagerAdapter mFilterViewPagerAdapter;
    private ImageView mFilterReset;
    private boolean isFilterReset = false;

    private List<FilterFragment> mFilterFragments;

    private List<FilterGroup> mFilterGroups;

    /**
     * ??????????????????
     */
    private static final int DEFAULT_POSITION = -1;
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
     * ???????????????????????????
     */
    private int maxRecordTime = 15;

    /**
     * ???????????????????????????(60s:0,30:1,15:2)
     */
    private int currentTimeIndex = 2;

    /******************************* ???????????????????????????Start ********************************/

    /**
     * ????????????
     */
    private RelativeLayout mTopBar, mTopRecordProgress;

    private View selectEffect;
    private TextView effectName;
    private String stickerId = "";
    private ArrayList<String> stickerIds = new ArrayList<>();
    private ImageView effectImg;

    /**
     * ????????????
     **/
    private HorizontalProgressBar mRecordProgress;

    /**
     * ??????????????????????????????????????????bug??????boolean
     **/
    private boolean isBackFromOnPause;

    /**
     * ??????????????????????????????
     */
    private RelativeLayout interuptLayout;

    /**
     * ????????????????????????
     */
    private RelativeLayout mRecordTimeRe;

    /**
     * ????????????????????????
     */
    private TextView mRecordTimeTv;

    /**
     * ????????????
     */
    private TuSdkTextButton mCloseButton;

    /**
     * ??????????????????
     */
    private LinearLayout addSoundLl;

    /**
     * ???????????????????????????
     */
    private ImageView addSoundImg;

    /**
     * ????????????
     */
    private TextView tvSoundName;

    /**
     * ????????????????????????
     */
    private LinearLayout mRightButtonLl;

    /**
     * ?????????????????????
     */
    private TuSdkTextButton mSwitchButton;

    /**
     * ????????????
     */
    private TuSdkTextButton mBeautyButton;

    /**
     * ????????????
     */
    private TuSdkTextButton mSpeedButton;

    /**
     * ????????????
     */
    private TuSdkTextButton mFiltersButton;

    /**
     * ???????????????
     */
    private TuSdkTextButton mFlashOffButton;

    /**
     * ????????????
     */
    private LinearLayout mSmartBeautyTabLayout;

    /**
     * ???????????????
     */
    private RecyclerView mBeautyRecyclerView;

    /******************************* ???????????????????????????End********************************/


    /******************************* ??????????????????End********************************/

    /**
     * ??????????????????
     */
    private LinearLayout mSelectTimeLayout;

    /**
     * 60s??????
     */
    private Button _60sButton;

    /**
     * 30s??????
     */
    private Button _30sButton;

    /**
     * 15s??????
     */
    private Button _15sButton;

    /**
     * ????????????????????????
     */
    private LinearLayout mBottomBarLayout;

    /**
     * ????????????
     */
    private TuSdkTextButton mRollBackButton;


    /**
     * ????????????
     */
    private ImageView mRecordButton;
    private LottieAnimationView mRecordPlayingButton;

    /**
     * ??????????????????
     **/
    private TuSdkTextButton mConfirmButton;

    /**
     * ???????????????????????????
     **/
    private TuSdkTextButton mTranslucentConfirmWrapButton;
    ;

    /**
     * ??????
     */
    private View mStickerWrapButton;

    /**
     * ????????????
     */
    private TuSdkTextButton mOpenAlbumButton;

    /**
     * ??????????????????
     */
    private RelativeLayout mRecordModeBarLayout;
    /**
     * ????????????
     */
    private TuSdkTextButton mShootButton;
    /**
     * ????????????
     */
    private TuSdkTextButton mLongButton;
    /**
     * ????????????
     */
    private TuSdkTextButton mClickButton;

    private String mCurrentFilterCode = "";


    /******************************* ??????????????????End********************************/


    /******************************* ??????????????????Start********************************/

    /**
     * ????????????????????????
     */
    private ViewGroup mSpeedModeBar, mSpeedModeBarBg;

    /**
     * ??????????????????????????????
     */
    private Button minTimeButton;

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
    private TuSdkTextButton mBackButton;
    /**
     * ????????????
     **/
    private TuSdkTextButton mSaveImageButton;

    /******************************* ??????????????????End********************************/
    /***
     * ??????????????????
     */

    private Mp3Player mp3Player;

    //????????????????????????????????????????????????????????????bug (complete = 1???
    private int recordState;

    /**
     * ????????????????????????
     */
    private BackgroundMusicBean currentBackMusicBean;

    /**
     * ??????????????????bitmap
     */
   private Bitmap bitmap = null;

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
        return R.layout.record_view;
    }

    /******************************* ?????????Start********************************/

    protected void init(Context context) {
        EventBus.getDefault().register(this);
        LayoutInflater.from(context).inflate(getLayoutId(), this,
                true);

        //????????????
        mp3Player = Mp3Player.getInstance();
        mp3Player.init(mAudioPlayerListener);
        // ??????????????????
        mTopBar = findViewById(R.id.lsq_topBar);
        selectEffect  = findViewById(R.id.select_effect);
        effectName  = findViewById(R.id.effect_name);
        effectImg  = findViewById(R.id.effect_img);
        mTopRecordProgress = findViewById(R.id.lsq_process_container);
        RelativeLayout.LayoutParams topRecordProgressLayoutParams = (LayoutParams) mTopRecordProgress.getLayoutParams();
        topRecordProgressLayoutParams.topMargin = VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(6);
        mTopRecordProgress.setLayoutParams(topRecordProgressLayoutParams);
        // ???????????????
        mRecordProgress = findViewById(R.id.lsq_record_progressbar);
        // 3s??????????????????
        minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);

        // ??????????????????????????????
        interuptLayout = (RelativeLayout) findViewById(R.id.interuptLayout);

        LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
        minTimeLayoutParams.leftMargin = ((TuSdkContext.getScreenSize().width - TuSdkContext.dip2px(18) * 2) * Constants.MIN_RECORDING_TIME / Constants.MAX_RECORDING_TIME) + TuSdkContext.dip2px(16 - 3);//- minTimeButton.getWidth()

        mRightButtonLl = findViewById(R.id.ll_right_button);
        mRecordTimeRe = findViewById(R.id.lsq_record_time);
        mRecordTimeTv = findViewById(R.id.tv_record_time);
        mCloseButton = findViewById(R.id.lsq_closeButton);
        addSoundLl = findViewById(R.id.lsq_add_sound);
        addSoundLl.setSelected(true);
        addSoundImg = findViewById(R.id.add_music_img);
        addSoundImg.setSelected(true);
        tvSoundName = findViewById(R.id.lsq_sound_name);
        // ??????????????????
        mSwitchButton = findViewById(R.id.lsq_switchButton);
        mBeautyButton = findViewById(R.id.lsq_beautyButton);
        mSpeedButton = findViewById(R.id.lsq_speedButton);
        mFiltersButton = findViewById(R.id.lsq_filtersButton);
        mFlashOffButton = findViewById(R.id.lsq_flash_off);

        mCloseButton.setOnClickListener(onClickListener);
        mSwitchButton.setOnClickListener(onClickListener);
        mBeautyButton.setOnClickListener(onClickListener);
        mSpeedButton.setOnClickListener(onClickListener);
        mFiltersButton.setOnClickListener(onClickListener);
        mFlashOffButton.setOnClickListener(onClickListener);
        addSoundLl.setOnClickListener(onClickListener);

        //????????????????????????
        mSelectTimeLayout = findViewById(R.id.lsq_select_record_time);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSelectTimeLayout.getLayoutParams();
        if (layoutParams != null)
            layoutParams.leftMargin = (TuSdkContext.getScreenSize().width / 2 - TuSdkContext.dip2px(186 - 5 - 52 / 2 + 24));
        mSelectTimeLayout.setLayoutParams(layoutParams);

        _60sButton = findViewById(R.id.btn_60s);
        _30sButton = findViewById(R.id.btn_30s);
        _15sButton = findViewById(R.id.btn_15s);
        _60sButton.setOnClickListener(onClickListener);
        _30sButton.setOnClickListener(onClickListener);
        _15sButton.setOnClickListener(onClickListener);
        _15sButton.setSelected(true);

        // ????????????????????????
        mBottomBarLayout = findViewById(R.id.lsq_button_wrap_layout);
        // ????????????
        mStickerWrapButton = findViewById(R.id.lsq_stickerWrap);
        effectsImg = findViewById(R.id.effects_img);
        mStickerWrapButton.setOnClickListener(onClickListener);


        // ??????????????????
        mOpenAlbumButton = findViewById(R.id.lsq_tab_upload);
        mOpenAlbumButton.setOnClickListener(onClickListener);
        // ????????????
        mConfirmButton = findViewById(R.id.lsq_confirmWrap);
        mConfirmButton.setOnClickListener(onClickListener);

        // ?????????????????????
        mTranslucentConfirmWrapButton = findViewById(R.id.lsq_translucent_confirmWrap);
        RelativeLayout.LayoutParams translucentConfirmlayoutParams = (LayoutParams) mTranslucentConfirmWrapButton.getLayoutParams();
        int translucentConfirmRightMargin = (TuSdkContext.getDisplaySize().width - TuSdkContext.dip2px(24 + 24)) / 3 - TuSdkContext.dip2px((float) (32 + 30 + 36.17));
        translucentConfirmlayoutParams.rightMargin = translucentConfirmRightMargin;
        mTranslucentConfirmWrapButton.setLayoutParams(translucentConfirmlayoutParams);

        mTranslucentConfirmWrapButton.setOnClickListener(onClickListener);
        // ????????????
        mRecordButton = findViewById(R.id.lsq_recordButton);
        mRecordButton.setOnTouchListener(onTouchListener);

        mRecordPlayingButton = findViewById(R.id.lsq_recording);
        RelativeLayout.LayoutParams recordPlaylayoutParams = (LayoutParams) mRecordPlayingButton.getLayoutParams();
        //int marginBottom = - TuSdkContext.dip2px(50 + 31.65);
        //????????????margin ???????????????????????????????????? ???????????????icon?????? TuSdkContext.dip2px(76.174f) ?????????icon TuSdkContext.dip2px(92.174f)

        int marginBottom = TuSdkContext.dip2px(18f);
        recordPlaylayoutParams.bottomMargin = marginBottom;
        mRecordPlayingButton.setLayoutParams(recordPlaylayoutParams);
        mRecordPlayingButton.setOnTouchListener(onTouchListener);


        // ????????????
        mRollBackButton = (TuSdkTextButton) findViewById(R.id.lsq_backWrap);
        mRollBackButton.setOnClickListener(onClickListener);

        /*******************???????????????start********************/
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

        // ??????????????????
        mRecordModeBarLayout = findViewById(R.id.lsq_record_mode_bar_layout);
        mRecordModeBarLayout.setOnTouchListener(onModeBarTouchListener);


        /*******************???????????????end********************/


        // ????????????
        mShootButton = findViewById(R.id.lsq_shootButton);
        mLongButton = findViewById(R.id.lsq_longButton);
        mClickButton = findViewById(R.id.lsq_clickButton);
        mShootButton.setOnTouchListener(onModeBarTouchListener);
        mLongButton.setOnTouchListener(onModeBarTouchListener);
        mClickButton.setOnTouchListener(onModeBarTouchListener);


        // PreviewLayout
        mBackButton = findViewById(R.id.lsq_backButton);
        mBackButton.setOnClickListener(onClickListener);
        mSaveImageButton = findViewById(R.id.lsq_saveImageButton);
        mSaveImageButton.setOnClickListener(onClickListener);
        mPreViewImageView = findViewById(R.id.lsq_cameraPreviewImageView);
        mPreViewImageView.setOnClickListener(onClickListener);

        // ???????????????
        mSpeedModeBar = findViewById(R.id.lsq_movie_speed_bar);
        mSpeedModeBarBg = findViewById(R.id.lsq_movie_speed_bar_rl);
        mSpeedModeBarBg.setOnClickListener(onClickListener);
        int childCount = mSpeedModeBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mSpeedModeBar.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectSpeedMode(Integer.parseInt((String) view.getTag()));
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
        mFilterConfigView = findViewById(R.id.lsq_filter_config_view);
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
                if (mCamera == null) return;
                mCurrentCameraEV = progress - mCameraMaxEV;
                mCamera.setExposureCompensation(mCurrentCameraEV);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (mCamera != null) {
            mCamera.setExposureCompensation(mCurrentCameraEV);
        }

        mFilterValueMap = getContext().getSharedPreferences("TUTUFilter", Context.MODE_PRIVATE);

        initFilterRecyclerView();
        initStickerLayout();
        // ???????????????????????????
        switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);

    }

    public void resetFilter() {
        mCurrentFilterCode = "";
        mFilterValueMap.edit().remove(DEFAULT_FILTER_CODE).apply();
        mFilterValueMap.edit().remove(DEFAULT_FILTER_GROUP).apply();
    }

    public void initFilterGroupsViews(FragmentManager fragmentManager, Lifecycle lifecycle, List<FilterGroup> filterGroups) {
        mFilterGroups = filterGroups;
        mFilterReset = findViewById(R.id.lsq_filter_reset);
        mFilterReset.setOnClickListener(new TuSdkViewHelper.OnSafeClickListener() {
            @Override
            public void onSafeClick(View view) {



                mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeFilter);
                mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeComic);
                mFilterFragments.get(mFilterTabIndicator.getCurrentPosition()).removeFilter();
                mFilterConfigView.setVisibility(View.GONE);
                mFilterViewPagerAdapter.notifyDataSetChanged();
                resetFilter();
                isFilterReset = true;
            }
        });


        mFilterTabIndicator = findViewById(R.id.lsq_filter_tabIndicator);

        mFilterViewPager = findViewById(R.id.lsq_filter_view_pager);
        mFilterViewPager.requestDisallowInterceptTouchEvent(true);
        List<String> tabTitles = new ArrayList<>();
        List<FilterFragment> fragments = new ArrayList<>();
        int i1 = 0;
        for (FilterGroup group : mFilterGroups) {
            if (group != null) {
                //???????????????FilterTypeItemList
                FilterFragment fragment = FilterFragment.newInstance(group, getFilterTypeItemList(i1));
                i1++;
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

        }
        AppConstants.textStringFilterNames = Arrays.asList(getResources().getString(R.string.lsq_filter_group_Portrait_Video), getResources().getString(R.string.lsq_filter_group_Food_Video), getResources().getString(R.string.lsq_filter_group_Scenery_Video), getResources().getString(R.string.lsq_filter_group_Sharp_Video));
        mFilterFragments = fragments;
        mFilterViewPagerAdapter = new FilterViewPagerAdapter(fragmentManager, lifecycle, fragments);
        mFilterViewPager.setAdapter(mFilterViewPagerAdapter);
        mFilterTabIndicator.setViewPager(mFilterViewPager, 0);
        mFilterTabIndicator.setDefaultVisibleCounts(tabTitles.size());
        //mFilterTabIndicator.setTabItems(tabTitles);
        mFilterTabIndicator.setTabItems(AppConstants.textStringFilterNames);


    }

    private ArrayList getFilterTypeItemList(int index) {
        switch (index) {
            case 0:
                return new ArrayList<>(AppConstants.textStringFilterTypeItemNamesFirst);
            case 1:
                return new ArrayList<>(AppConstants.textStringFilterTypeItemNamesSecond);
            case 2:
                return new ArrayList<>(AppConstants.textStringFilterTypeItemNamesThird);
            case 3:
                return new ArrayList<>(AppConstants.textStringFilterTypeItemNamesFourth);
            default:
                break;
        }
        return null;
    }

    /**
     * ???????????????
     */
    public void initRecordProgress() {
        /*mRecordProgress.clearProgressList();
        interuptLayout.removeAllViews();
        if (mBottomBarLayout.getVisibility() == VISIBLE);
            setViewHideOrVisible(true);*/
    }

    public void initOnPauseSavedStatus() {
        if (mRecordProgress != null) {
            mRecordProgress.pauseRecord();
        }
        mRecordProgress.clearProgressList();
        interuptLayout.removeAllViews();
        //if (mBottomBarLayout.getVisibility() == VISIBLE)
        mTranslucentConfirmWrapButton.setVisibility(GONE);
        getDelegate().pauseRecording();
        pauseMusic(false);
        //??????????????????????????????
        setViewHideOrVisible(true);
        isBackFromOnPause = true;
    }

    public void initOnPauseRecordProgress() {
//        if (mRecordProgress != null) {
//            mRecordProgress.pauseRecord();
//        }
        /*mRecordProgress.clearProgressList();
        interuptLayout.removeAllViews();*/
        //if (mBottomBarLayout.getVisibility() == VISIBLE)
        //mTranslucentConfirmWrapButton.setVisibility(GONE);
        //   getDelegate().pauseRecording();
        //??????????????????
        //setRecordingViewVisible(false);
        //setRecordingViewVisible(false, 0);
        //updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
      //  pauseMusic(false);
        //??????????????????????????????/??????????????????icon
        //setViewHideOrVisible(true);
        if (getDelegate().isRecording()) {
            stopRecordExtracted();
        }
        isBackFromOnPause = true;
    }

    public void initOnResumeRecordProgress() {
        /*mRecordProgress.clearProgressList();
        interuptLayout.removeAllViews();*/
        //if (mBottomBarLayout.getVisibility() == VISIBLE)
        //setViewHideOrVisible(true);
        // ???????????????????????????
        //switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);

        //?????????????????????
        if (isBackFromOnPause) {
            getDelegate().startRecording();
            isBackFromEdit = true;
            isBackFromOnPause = false;
        }
    }


    /******************************* ?????????End********************************/


    /*****************************????????????Start******************************/
    /**
     * ?????????????????????????????????
     */
    private TuSdkRecorderVideoCamera.TuSdkMediaEffectChangeListener mMediaEffectChangeListener = new TuSdkRecorderVideoCamera.TuSdkMediaEffectChangeListener() {
        @Override
        public void didApplyingMediaEffect(final TuSdkMediaEffectData mediaEffectData) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    switch (mediaEffectData.getMediaEffectType()) {
                        case TuSdkMediaEffectDataTypeFilter: //????????????
                            List<SelesParameters.FilterArg> filterArgs = new ArrayList<>();
                            SelesParameters.FilterArg filterArg = mediaEffectData.getFilterArg("mixied");// ??????????????????
                            if (filterArg != null) filterArgs.add(filterArg);
                            SelesParameters.FilterArg saturationFilterArg = mediaEffectData.getFilterArg("saturation");
                            if (saturationFilterArg != null) filterArgs.add(saturationFilterArg);
                            mFilterConfigView.setFilterArgs(mediaEffectData, filterArgs);
                            break;

                    }
                }
            });
        }

        /**
         * ?????????????????????
         * @param mediaEffects
         */
        @Override
        public void didRemoveMediaEffect(List<TuSdkMediaEffectData> mediaEffects) {

        }
    };

    /**
     * ????????????
     */
    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (getDelegate() == null) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (TuSdkViewHelper.isFastDoubleClick()) return false;
                    if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                        setViewHideOrVisible(false);
                        getDelegate().startRecording();
                        updateRecordButtonResource(RecordType.LONG_CLICK_RECORDING);

                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    // ????????????
                    if (mRecordMode == RecordType.CAPTURE) {
                        mCamera.captureImage();
                    }
                    // ????????????
                    else if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                        getDelegate().pauseRecording();
                        updateRecordButtonResource(RecordType.LONG_CLICK_RECORD);
                    }
                    // ????????????
                    else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
                        mRecordPlayingButton.setVisibility(VISIBLE);
                        mRecordButton.setVisibility(INVISIBLE);

                        // ???????????????????????????
                        if (getDelegate().isRecording()) {
                            stopRecordExtracted();
                        } else {
                            mTranslucentConfirmWrapButton.setVisibility(VISIBLE);
                            if (mCamera.getMovieDuration() >= Constants.MAX_RECORDING_TIME) {
                                String msg = getStringFromResource(R.string.max_recordTime) + Constants.MAX_RECORDING_TIME + "s";
                                TuSdk.messageHub().showToast(mContext, msg);
                                return false;
                            }

                            setViewHideOrVisible(false);
                            setRecordingViewVisible(true);
                            //????????????
                            getDelegate().startRecording();
                            stickerIds.add(stickerId);
                            mRecordPlayingButton.setVisibility(VISIBLE);
                            mRecordPlayingButton.playAnimation();
                            mRecordButton.setVisibility(INVISIBLE);
                            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
                            playMusic();
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }
    };

    private void stopRecordExtracted() {
        mTranslucentConfirmWrapButton.setVisibility(GONE);
        getDelegate().pauseRecording();
        //??????????????????
        //setRecordingViewVisible(false);
        setRecordingViewVisible(false, 0);
        mRecordPlayingButton.setVisibility(INVISIBLE);
        mRecordPlayingButton.pauseAnimation();
        mRecordButton.setVisibility(VISIBLE);
        updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
        pauseMusic(false);
    }

    public void recordReStartCheck(boolean isFromPause) {
        if (isFromPause) {
            mTranslucentConfirmWrapButton.setVisibility(GONE);
            //  getDelegate().pauseRecording();
            //??????????????????
            //setRecordingViewVisible(false);
            setRecordingViewVisible(false, 0);
            updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
            pauseMusic(false);
        }
    }


    public void onStop(){
        if(getDelegate().isRecording()){
            stopRecordExtracted();
        }
    }
    public void onStart(){


    }

    public void recordPauseCheck() {
        mTranslucentConfirmWrapButton.setVisibility(VISIBLE);
        if (mCamera.getMovieDuration() >= Constants.MAX_RECORDING_TIME) {
            String msg = getStringFromResource(R.string.max_recordTime) + Constants.MAX_RECORDING_TIME + "s";
            TuSdk.messageHub().showToast(mContext, msg);
            //return false;
        }
        setViewHideOrVisible(false);
        setRecordingViewVisible(true);
        //getDelegate().startRecording();
        updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
        //playMusic();
    }


    /**
     * ????????????
     */
    RadioGroup.OnCheckedChangeListener mAudioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.lsq_audio_normal) {// ??????
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal);
            } else if (checkedId == R.id.lsq_audio_monster) {// ??????
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Monster);
            } else if (checkedId == R.id.lsq_audio_uncle) {// ??????
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Uncle);
            } else if (checkedId == R.id.lsq_audio_girl) {// ??????
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl);
            } else if (checkedId == R.id.lsq_audio_lolita) {// ??????
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Lolita);
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

    /**
     * MP3?????????????????????
     */
    private Mp3Player.AudioPlayerListener mAudioPlayerListener = new Mp3Player.AudioPlayerListener() {

        @Override
        public void onPrepared() {

        }

        @Override
        public void onCompletion() {

        }

        @Override
        public void onUpdateCurrentPosition(int position) {

        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }

        @Override
        public void onError(MediaPlayer mp, int what, int extra) {

        }
    };

    /**
     * ????????????????????????
     *
     * @param camera
     */
    public void setUpCamera(Context context, TuSdkRecorderVideoCameraImpl camera) {
        this.mContext = context;
        this.mCamera = camera;

        mCamera.setCameraListener(mVideoCameraLinstener);
        mCamera.setMediaEffectChangeListener(mMediaEffectChangeListener);
        mCamera.getFocusTouchView().setGestureListener(gestureListener);
        ThreadHelper.runThread(new Runnable() {
            @Override
            public void run() {
                getFirstFrame();
            }
        });
    }

    /*****************************????????????End******************************/


    /******************************** ??????Start ********************************************/

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
            if (mCurrentFilter != null) {
                mCurrentFilter.submitParameter(arg.getKey(), progress);
            }
        }
    };

    /**
     * ??????????????????????????????
     */
    protected TuSdkRecorderVideoCamera.TuSdkCameraListener mVideoCameraLinstener = new TuSdkRecorderVideoCamera.TuSdkCameraListener() {
        @Override
        public void onFilterChanged(FilterWrap selesOutInput) {
        }

        @Override
        public void onVideoCameraStateChanged(TuSdkStillCameraAdapter.CameraState newState) {
            if (newState.equals(TuSdkStillCameraAdapter.CameraState.StateUnknow)) return;

//            ThreadHelper.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (!isBeautyClose)
//                        // ??????????????????
//                        switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty);
//                }
//            }, 500);
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).size() == 0) {
                        // ?????????????????????????????????
//                        TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
//                        mCamera.addMediaEffectData(plasticFaceEffect);

//                        TuSdkMediaReshapeEffect effect = new TuSdkMediaReshapeEffect();
//                        mCamera.addMediaEffectData(effect);
//                        for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
//                            if (arg.equalsKey("eyeSize")) {// ??????
//                                arg.setMaxValueFactor(0.85f);// ???????????????
//                            }
//                            if (arg.equalsKey("chinSize")) {// ??????
//                                arg.setMaxValueFactor(0.9f);// ???????????????
//                            }
//                            if (arg.equalsKey("noseSize")) {// ??????
//                                arg.setMaxValueFactor(0.6f);// ???????????????
//                            }
//                        }
                        for (String key : mDefaultBeautyPercentParams.keySet()) {
                            TLog.e("key -- %s", mDefaultBeautyPercentParams.get(key));
                            submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
//                            if (plasticFaceEffect.getFilterArg(key) != null) {
//                                plasticFaceEffect.getFilterArg(key).setDefaultPercent(mDefaultBeautyPercentParams.get(key));
//                            } else
//                                if (effect.getFilterArg(key) != null) {
//                                effect.getFilterArg(key).setDefaultPercent(mDefaultBeautyPercentParams.get(key));
//                            }
                        }
                    }
                }
            }, 700);
            // ???????????????????????????
            if (!isFilterReset) {
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setDefaultFilter();
                    }
                }, 200);

            }
            if (newState.equals(TuSdkStillCameraAdapter.CameraState.StatePreview) && mCameraMaxEV == 0) {
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCameraMaxEV = mCamera.getMaxExposureCompensation();
                        mCameraMinEV = mCamera.getMinExposureCompensation();
                        mExposureSeekbar.setMax(mCameraMaxEV + Math.abs(mCameraMinEV));
                        mExposureSeekbar.setProgress(mCameraMaxEV);
                    }
                }, 1000);
            }


        }

        /**
         * ??????????????????
         * @param bitmap ??????
         */
        @Override
        public void onVideoCameraScreenShot(Bitmap bitmap) {
            presentPreviewLayout(bitmap);
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

    private TuSdkMediaEffectData mCurrentFilter = null;

    /**
     * ????????????
     *
     * @param code
     */
    protected void changeVideoFilterCode(final String code) {
        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter) != null && mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter).size() > 0 && mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter).get(0).getFilterWrap().getCode().equals(code))
            return;
        isFilterReset = false;
        TuSdkMediaFilterEffectData filterEffectData = new TuSdkMediaFilterEffectData(code);
        SelesParameters.FilterArg filterArg = filterEffectData.getFilterArg("mixied");// ??????
        mCamera.addMediaEffectData(filterEffectData);
        mFilterValueMap.edit().putString(DEFAULT_FILTER_CODE, code).apply();
        mFilterValueMap.edit().putLong(DEFAULT_FILTER_GROUP, mFilterGroups.get(mFilterViewPager.getCurrentItem()).groupId).apply();
        if (filterArg != null) {
            Float value = mFilterValueMap.getFloat(code, -1f) == -1f ? 0.75f : mFilterValueMap.getFloat(code, -1f);
            filterArg.setPrecentValue(value);
            filterEffectData.submitParameter(filterArg.getKey(), filterArg.getPrecentValue());
        }
        mCurrentFilter = filterEffectData;
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
                mFilterNameTextView.setVisibility(GONE);
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
                if(mCurrentPosition > current.filters.size() - 1){
                    mCurrentPosition = current.filters.size() - 1;
                }
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
                if(mCurrentPosition<0){
                    mCurrentPosition = 0;
                }
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
            if (!mCamera.isRecording()) {
                mExposureSeekbar.setProgress(mCameraMaxEV);
                setFilterContentVisible(false);
                setBeautyViewVisible(false);
                setBottomViewVisible(true);
                setStickerVisible(false);
                mMoreConfigLayout.setVisibility(GONE);
                //boolean isDisplayTimeLayout????????????????????????????????????????????????
                setSelectTimeLayoutHideOrVisible(!isSpeedChecked);

                //??????????????????icon???????????????
                if (mRightButtonLl != null && mRightButtonLl.getVisibility() != VISIBLE) {
                    mRightButtonLl.setVisibility(VISIBLE);
                }
                if (mCloseButton != null && mCloseButton.getVisibility() != VISIBLE) {
                    mCloseButton.setVisibility(VISIBLE);
                }
                if (mTopBar != null && mTopRecordProgress != null) {
                    mTopBar.setVisibility(VISIBLE);
                    mTopRecordProgress.setVisibility(VISIBLE);
                }
                //??????flip??????
                isDisplaySingleFlipStatus(false);
                if(mPropsItemViewPager.getAdapter() != null){
                    mPropsItemViewPager.getAdapter().notifyDataSetChanged();
                }
                mCamera.getFocusTouchView().isShowFoucusView(true);
            }
        }
    };

    /******************************** ??????End ********************************************/


    /********************** ?????? ****************************/

    /**
     * ??????????????????
     *
     * @param code
     */
    protected void changeVideoComicEffectCode(final String code) {
        isFilterReset = false;
        TuSdkMediaComicEffectData effectData = new TuSdkMediaComicEffectData(code);
        mCamera.addMediaEffectData(effectData);
        mFilterValueMap.edit().putString(DEFAULT_FILTER_CODE, code).apply();
        mFilterValueMap.edit().putLong(DEFAULT_FILTER_GROUP, mFilterGroups.get(mFilterViewPager.getCurrentItem()).groupId).apply();
        mCurrentFilter = effectData;
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

    /******************************* ??????Start **************************/
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
        @Override
        public void removePropsItem(PropsItem propsItem) {
            if (propsItemUsed(propsItem))
                mCamera.removeMediaEffectsWithType(mPropsItemCategories.get(mPropsItemViewPager.getCurrentItem()).getMediaEffectType());
        }



        @Override
        public void didSelectPropsItem(PropsItem propsItem, CustomStickerGroup customStickerGroup) {

            Glide.with(mContext).load(customStickerGroup.getPreviewName())
                    .placeholder(R.drawable.ic_effects)
                    .error(R.drawable.ic_effects)
                    .into(effectsImg);
            Glide.with(mContext).load(customStickerGroup.getPreviewName())
                    .placeholder(R.drawable.ic_effects)
                    .error(R.drawable.ic_effects)
                    .into(effectImg);
            effectName.setText(customStickerGroup.name);
            stickerId = customStickerGroup.getStickerId();
            selectEffect.setVisibility(VISIBLE);

            mCamera.addMediaEffectData(propsItem.effect());
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
            if (propsItem.effect() == null) return false;
            List<TuSdkMediaEffectData> mediaEffectDataList = mCamera.mediaEffectsWithType(propsItem.effect().getMediaEffectType());

            if (mediaEffectDataList == null || mediaEffectDataList.size() == 0) return false;

            return mediaEffectDataList.contains(propsItem.effect());
        }
    };

    /**
     * ???????????????????????????
     */
    private PropsItemPageFragment.ItemDelegate mPropsItemDelegate = new PropsItemPageFragment.ItemDelegate() {

        @Override
        public void didSelectPropsItem(PropsItem propsItem, CustomStickerGroup customStickerGroup) {
            mCamera.addMediaEffectData(propsItem.effect());
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
            if (propsItem.effect() == null) return false;
            List<TuSdkMediaEffectData> mediaEffectDataList = mCamera.mediaEffectsWithType(propsItem.effect().getMediaEffectType());

            if (mediaEffectDataList == null || mediaEffectDataList.size() == 0) return false;

            return mediaEffectDataList.contains(propsItem.effect());
        }
    };

    private  GVisionDynamicStickerBean gVisionDynamicStickerBean;
    /**
     * ?????????????????????
     */
    public void init(final FragmentManager fm, final Lifecycle lifecycle, GVisionDynamicStickerBean gVisionDynamicStickerBean) {
        this.gVisionDynamicStickerBean = gVisionDynamicStickerBean;
        if(gVisionDynamicStickerBean.getCategories()!=null&&gVisionDynamicStickerBean.getCategories().size()>0&&gVisionDynamicStickerBean.getCategories().get(0).getStickers()!=null&&gVisionDynamicStickerBean.getCategories().get(0).getStickers().size()>0){
            Glide.with(mContext)
                    .load(gVisionDynamicStickerBean.getCategories().get(0).getStickers().get(0).getPreviewImage())
                    .placeholder(R.drawable.ic_effects)
                    .error(R.drawable.ic_effects)
                    .into(effectsImg);
        }
        // ??????????????????????????????
        mPropsItemCategories.addAll(PropsItemStickerCategory.allCategories(getContext(),gVisionDynamicStickerBean));
        // ???????????????????????????
        //  mPropsItemCategories.addAll(PropsItemMonsterCategory.allCategories());

        mPropsItemPagerAdapter = new PropsItemPagerAdapter(fm, lifecycle, new PropsItemPagerAdapter.DataSource() {
            @Override
            public Fragment frament(int pageIndex) {

                PropsItemCategory category = mPropsItemCategories.get(pageIndex);

                switch (category.getMediaEffectType()) {
                    case TuSdkMediaEffectDataTypeSticker: {
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

    /******************************* ??????End **************************/


    /*********************************** ??????Start ********************* */

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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("lipAlpha")));
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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("blushAlpha")));
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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyebrowAlpha")));
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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyeshadowAlpha")));
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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelineAlpha")));
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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelashAlpha")));
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
                    mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("facialAlpha")));
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
            put("mouthWidth", 0.5f);
            put("lips", 0.5f);
            put("philterum", 0.5f);
            put("archEyebrow", 0.5f);
            put("browPosition", 0.5f);
            put("jawSize", 0.5f);
            put("cheekLowBoneNarrow", 0.0f);
            put("eyeAngle", 0.5f);
            put("eyeInnerConer", 0.0f);
            put("eyeOuterConer", 0.0f);
            put("eyeDis", 0.5f);
            put("eyeHeight", 0.5f);
            put("forehead", 0.5f);
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
                    if (isBeautyChecked)
                        submitSkinParamter(arg.getKey(), seekbar.getSeekbar().getProgress());
                    else
                        submitPlasticFaceParamter(arg.getKey(), seekbar.getSeekbar().getProgress());
                }
            };

    /**
     * ??????Item????????????
     */
    BeautyRecyclerAdapter.OnBeautyItemClickListener beautyItemClickListener =
            new BeautyRecyclerAdapter.OnBeautyItemClickListener() {
                @Override
                public void onChangeSkin(View v, String key, TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
                    mBeautyPlasticsConfigView.setVisibility(VISIBLE);
                    switchConfigSkin(skinMode);

                    // ??????key????????????????????????
                    TuSdkMediaEffectData mediaEffectData = mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace).get(0);
                    SelesParameters.FilterArg filterArg = mediaEffectData.getFilterArg(key);
                    mBeautyPlasticsConfigView.setFilterArgs(mediaEffectData, Arrays.asList(filterArg));
                }

                @Override
                public void onClear() {
                    hideBeautyBarLayout();

                    mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace);
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
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("lipAlpha")));
                        break;
                    case Blush:
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("blushAlpha")));
                        break;
                    case Eyebrow:
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyebrowAlpha")));
                        break;
                    case Eyeshadow:
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyeshadowAlpha")));
                        break;
                    case Eyeliner:
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelineAlpha")));
                        break;
                    case Eyelash:
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("eyelashAlpha")));
                        break;
                    case Facial:
                        mBeautyPlasticsConfigView.setFilterArgs(mController.getEffect(), Arrays.asList(mController.getEffect().getFilterArg("facialAlpha")));
                        break;
                }
            } else {
                hideBeautyBarLayout();
            }


            if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeCosmetic).size() == 0) {
                TuSdkMediaCosmeticEffectData data = mController.getEffect();
                mCamera.addMediaEffectData(data);
                for (String key : CosmeticPanelController.mDefaultCosmeticPercentParams.keySet()) {
                    data.submitParameter(key, CosmeticPanelController.mDefaultCosmeticPercentParams.get(key));
                    data.getFilterArg(key).setDefaultPercent(CosmeticPanelController.mDefaultCosmeticPercentParams.get(key));
                }
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
            // setTextButtonDrawableTop(mBeautyButton, R.drawable.video_nav_ic_beauty_selected);

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
            //   setTextButtonDrawableTop(mBeautyButton, R.drawable.video_nav_ic_beauty);
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

    /**
     * ????????????????????????
     *
     * @param skinMode true ??????(??????)?????? false ????????????
     */
    private void switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
        TuSdkMediaSkinFaceEffect skinFaceEffect = new TuSdkMediaSkinFaceEffect(skinMode);


        // ??????
        SelesParameters.FilterArg whiteningArgs = skinFaceEffect.getFilterArg("whitening");//whiten whitening
        whiteningArgs.setMaxValueFactor(1.0f);//?????????????????????
        whiteningArgs.setDefaultPercent(0.3f);
        whiteningArgs.setPrecentValue(0.3f);//??????????????????
        // ??????
        SelesParameters.FilterArg smoothingArgs = skinFaceEffect.getFilterArg("smoothing");//smooth smoothing
        smoothingArgs.setMaxValueFactor(1.0f);//?????????????????????
        smoothingArgs.setDefaultPercent(0.8f);
        smoothingArgs.setPrecentValue(0.8f);//??????????????????
        // ??????
        SelesParameters.FilterArg ruddyArgs = skinFaceEffect.getFilterArg(skinMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? "ruddy" : "sharpen");//sharpen ruddy
        ruddyArgs.setMaxValueFactor(1.0f);//?????????????????????
        ruddyArgs.setDefaultPercent(skinMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? 0.2f : 0.2f);
        ruddyArgs.setPrecentValue(skinMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? 0.2f : 0.2f);


        // ??????
        SelesParameters.FilterArg sharpenArgs = skinFaceEffect.getFilterArg("sharpen");//sharpen ruddy
        sharpenArgs.setMaxValueFactor(1.0f);//?????????????????????
        sharpenArgs.setDefaultPercent( 0.2f);
        sharpenArgs.setPrecentValue(0.2f);


        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace) == null ||
                mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace).size() == 0) {

            mCamera.addMediaEffectData(skinFaceEffect);
        } else {
            TuSdkMediaSkinFaceEffect oldSkinFaceEffect = (TuSdkMediaSkinFaceEffect) mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace).get(0);
            mCamera.addMediaEffectData(skinFaceEffect);

            for (SelesParameters.FilterArg filterArg : oldSkinFaceEffect.getFilterArgs()) {
                SelesParameters.FilterArg arg = skinFaceEffect.getFilterArg(filterArg.getKey());
                if (arg != null)
                    arg.setPrecentValue(filterArg.getPrecentValue());
            }

            skinFaceEffect.submitParameters();

            if (!oldSkinFaceEffect.getFilterWrap().equals(skinFaceEffect.getFilterWrap())) {
                // ???????????????
                showHitTitle(TuSdkContext.getString(getSkinModeTitle(skinMode)));
            }
        }

        // ?????????????????????????????????
        TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
        mCamera.addMediaEffectData(plasticFaceEffect);

        for (String key : mDefaultBeautyPercentParams.keySet()) {
            if (key.equals("eyeSize")) {
                submitPlasticFaceParamter(key, 0.4f);
            } else if (key.equals("smallFace")) {
                submitPlasticFaceParamter(key, 0.2f);
            } else {
                submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
            }

        }
        isBeautyClose = false;
    }

    private String getSkinModeTitle(TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
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

    /**
     * ????????????
     *
     * @param key
     * @param progress
     */
    private void submitSkinParamter(String key, float progress) {
        List<TuSdkMediaEffectData> filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSkinFace);

        if (filterEffects.size() == 0) return;

        // ??????????????????????????????
        TuSdkMediaSkinFaceEffect filterEffect = (TuSdkMediaSkinFaceEffect) filterEffects.get(0);
        filterEffect.submitParameter(key, progress);
    }

    /**
     * ?????????????????????
     *
     * @param position
     */
    private void switchBeautyPlasticConfig(int position) {
        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).size() == 0) {
            // ?????????????????????????????????
//            TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
//            mCamera.addMediaEffectData(plasticFaceEffect);
//
//            TuSdkMediaReshapeEffect effect = new TuSdkMediaReshapeEffect();
//            mCamera.addMediaEffectData(effect);
//            for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
//                if (arg.equalsKey("eyeSize")) {// ??????
//                    arg.setMaxValueFactor(0.85f);// ???????????????
//                }
//                if (arg.equalsKey("chinSize")) {// ??????
//                    arg.setMaxValueFactor(0.9f);// ???????????????
//                }
//                if (arg.equalsKey("noseSize")) {// ??????
//                    arg.setMaxValueFactor(0.6f);// ???????????????
//                }
//
//            }
            for (String key : mDefaultBeautyPercentParams.keySet()) {
                TLog.e("key -- %s", mDefaultBeautyPercentParams.get(key));
                submitPlasticFaceParamter(key, mDefaultBeautyPercentParams.get(key));
            }

        }

        if (mReshapePlastics.contains(mBeautyPlastics.get(position))) {
            TuSdkMediaEffectData effectData = mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeReshape).get(0);
            SelesParameters.FilterArg filterArg = effectData.getFilterArg(mBeautyPlastics.get(position));
            mBeautyPlasticsConfigView.setFilterArgs(null, Arrays.asList(filterArg));

        } else {
            TuSdkMediaEffectData effectData = mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).get(0);
            SelesParameters.FilterArg filterArg = effectData.getFilterArg(mBeautyPlastics.get(position));

//        TLog.e("filterArg -- %s",filterArg.getPrecentValue());

            mBeautyPlasticsConfigView.setFilterArgs(null, Arrays.asList(filterArg));
        }

    }

    /**
     * ???????????????
     *
     * @param key
     * @param progress
     */
    private void submitPlasticFaceParamter(String key, float progress) {
        List<TuSdkMediaEffectData> filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypePlasticFace);

        if (filterEffects.size() == 0) return;

        // ??????????????????????????????
//        TuSdkMediaPlasticFaceEffect filterEffect = (TuSdkMediaPlasticFaceEffect) filterEffects.get(0);
//        filterEffect.submitParameter(key, progress);

        filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeReshape);
        if (filterEffects.size() == 0) return;
//
//        TuSdkMediaReshapeEffect effect = (TuSdkMediaReshapeEffect) filterEffects.get(0);
//        effect.submitParameter(key, progress);

    }


    /******************************** ?????? ************************/
    /**
     * ????????????????????????
     *
     * @param isShow true??????false??????
     */
    private void updatePreviewImageLayoutStatus(boolean isShow) {
        findViewById(R.id.lsq_preview_image_layout).setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * ??????????????????
     *
     * @param bitmap
     */
    private void presentPreviewLayout(Bitmap bitmap) {
        if (bitmap != null) {
            mCaptureBitmap = bitmap;
            updatePreviewImageLayoutStatus(true);
            mPreViewImageView.setImageBitmap(bitmap);
            // ????????????
            mCamera.pauseCameraCapture();
        }
    }

    /**
     * ??????????????????
     */
    public void saveResource() {
        updatePreviewImageLayoutStatus(false);
        File flie = AlbumHelper.getAlbumFile();
        ImageSqlHelper.saveJpgToAblum(mContext, mCaptureBitmap, 0, flie);
        refreshFile(flie);
        destroyBitmap();
        TuSdk.messageHub().showToast(mContext, R.string.lsq_image_save_ok);
        mCamera.resumeCameraCapture();
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
        mCamera.resumeCameraCapture();
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
            int id = v.getId();
            // ????????????
            if (id == R.id.lsq_closeButton) {

                hanldeCloseButton();

            }

            // ????????????
            else if (id == R.id.lsq_add_sound) {
                if (addSoundLl.isSelected()) {
                    pauseMusic(true);
                    String musicName = tvSoundName.getText().toString();
                    if (musicName.equals(getContext().getString(R.string.add_sound))) {
                        musicName = "";
                    }
                    FragmentActivity activity = (FragmentActivity) mContext;
                    BaseFullBottomSheetFragment.getInstance(0, musicName).show(activity.getSupportFragmentManager(), "dialog");
                }
            }
            // ???????????????
            else if (id == R.id.lsq_switchButton) {
                mCamera.rotateCamera();

                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                ThreadHelper.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCamera.isFrontFacingCameraPresent()) {
                            isOpenFlash = false;
                            mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                            setTextButtonDrawableTop(mFlashOffButton, R.drawable.ic_shadow_flash_off);
                            mCamera.setFlashMode(CameraConfigs.CameraFlash.Off);
                            mCamera.setFocusMode(CameraConfigs.CameraAutoFocus.Auto, new PointF(0.5f, 0.5f));
                        }

                    }
                },500
               );


            }
            /// ??????????????????
            else if (id == R.id.lsq_speedButton) {
                setFilterContentVisible(false);
                setBottomViewVisible(true);
                setStickerVisible(false);
                setBeautyViewVisible(false);
                setSpeedViewVisible(mSpeedModeBar.getVisibility() == GONE);

            }//??????????????????????????????
            else if (id == R.id.lsq_movie_speed_bar_rl) {
                setSpeedViewVisible(false);
            }
            // ??????????????????????????????
            else if (id == R.id.lsq_beautyButton) {
                // todo ????????????
//                setFilterContentVisible(false);
//                setBottomViewVisible(mSmartBeautyTabLayout.getVisibility() == VISIBLE);
//                setBeautyViewVisible(mSmartBeautyTabLayout.getVisibility() == GONE);
//                setStickerVisible(false);
//                setSpeedViewVisible(false);
                if (isBeautyClose) {
                    switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty);
                    mBeautyButton.setSelected(true);
                    isBeautyClose = false;
                    // ToastUtils.showToast(getContext(),getContext().getString(R.string.beauty_mode_on) );
                    ToastUtils.showCustomToast((Activity) getContext(), getContext().getString(R.string.beauty_mode_on));

                } else {
                    // ????????????
                    mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace);
                    // ???????????????
                    mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace);
                    mBeautyButton.setSelected(false);
                    isBeautyClose = true;
                    //  ToastUtils.showToast(getContext(),getContext().getString(R.string.beauty_mode_off) );
                    ToastUtils.showCustomToast((Activity) getContext(), getContext().getString(R.string.beauty_mode_off));
                }
                //
                mCamera.getFocusTouchView().isShowFoucusView(false);

            }
            // ????????????
            else if (id == R.id.lsq_filtersButton) {
                setBeautyViewVisible(false);
                setBottomViewVisible(false);
                setSpeedViewVisible(false);
                setStickerVisible(false);
                showFilterLayout();
                //?????????????????????
                if (mRightButtonLl != null && mRightButtonLl.getVisibility() == VISIBLE) {
                    mRightButtonLl.setVisibility(INVISIBLE);
                }
                if (mCloseButton != null && mCloseButton.getVisibility() == VISIBLE) {
                    mCloseButton.setVisibility(INVISIBLE);
                }
                if (mTopBar != null && mTopRecordProgress != null) {
                    mTopBar.setVisibility(INVISIBLE);
                    mTopRecordProgress.setVisibility(INVISIBLE);
                }
                mCamera.getFocusTouchView().isShowFoucusView(false);
            }
            // ???????????????
            else if (id == R.id.lsq_flash_off) {
                if (isOpenFlash) {
                    updateFlashMode(CameraConfigs.CameraFlash.Off);

                    isOpenFlash = false;
                } else {
                    updateFlashMode(CameraConfigs.CameraFlash.Torch);
                    isOpenFlash = true;
                }

            }
            // ??????????????????
            else if (id == R.id.lsq_focus_open) {
                mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mFocusClose.setTextColor(getResources().getColor(R.color.lsq_color_white));

                mCamera.setDisableContinueFocus(false);
            }
            // ??????????????????
            else if (id == R.id.lsq_focus_close) {
                mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mFocusClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));

                mCamera.setDisableContinueFocus(true);

            }
            // ???????????????
            else if (id == R.id.lsq_lighting_open) {
                updateFlashMode(CameraConfigs.CameraFlash.Torch);

            }
            // ???????????????
            else if (id == R.id.lsq_lighting_close) {
                updateFlashMode(CameraConfigs.CameraFlash.Off);
            }
            // ????????????????????????
            else if (id == R.id.lsq_beauty_tab) {
                isCosmeticChecked = false;
                switchBeautyConfigTab(v);

            }
            // ???????????????????????????
            else if (id == R.id.lsq_beauty_plastic_tab) {
                isCosmeticChecked = false;
                switchBeautyConfigTab(v);
            }
            // ????????????????????????
            else if (id == R.id.lsq_cosmetic_tab) {
                isCosmeticChecked = true;
                switchBeautyConfigTab(v);
                // ??????
            } else if (id == R.id.lsq_tab_upload) {
                AlbumUtils.openMediaAlbum1(MovieEditorActivity.class.getName(), Constants.MAX_EDITOR_SELECT_MUN);

            }
            // ????????????
            else if (id == R.id.lsq_stickerWrap) {
                setFilterContentVisible(false);
                setBeautyViewVisible(false);
                setSpeedViewVisible(false);
                setBottomViewVisible(false);
                showStickerLayout();
                //?????????flip
                isDisplaySingleFlipStatus(true);
                mCamera.getFocusTouchView().isShowFoucusView(false);
                // ??????
            } else if (id == R.id.lsq_radio_1_1) {
                updateCameraRatio(RatioType.ratio_1_1);
            } else if (id == R.id.lsq_radio_3_4) {
                updateCameraRatio(RatioType.ratio_3_4);
            } else if (id == R.id.lsq_radio_full) {
                updateCameraRatio(RatioType.ratio_orgin);
                // ????????????
            } else if (id == R.id.lsq_backWrap) {// ???????????????????????????????????????
                //????????????????????????
                DialogHelper.closeTipDialog(mContext, getResources().getString(R.string.dialog_title_discard_last_clip), new DialogHelper.onDiscardClickListener() {
                    @Override
                    public void onDiscardClick() {
                        pauseMusic(false);

                        //????????????
                        if (mCamera.getRecordingFragmentSize() > 0) {
                            if(stickerIds.size()>0){
                                stickerIds.remove(stickerIds.size()-1);
                            }
                            mCamera.popVideoFragment();
                            mRecordProgress.removePreSegment();

                            if (interuptLayout.getChildCount() != 0) {
                                if (recordState == 1) {
                                    //pause ??? complete????????????????????????
                                    interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
                                    recordState = -1;
                                }
                                interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
                            }
                            // ???????????????????????????????????????
                            if (mCamera.getRecordingFragmentSize() == 0) {

                                mCamera.cancelRecording();
                                setMinTimeButtonStatus(VISIBLE);
                            }
                        }
                        // ????????????????????????????????????bug?????????????????? ????????????????????????isSpeedChecked??????boolean,??????setViewHideOrVisible???????????????????????????????????????????????????????????????????????????????????????isSpeedChecked
                        isSpeedChecked = false;
                        // ??????????????????
                        setViewHideOrVisible(true);
                        //setSpeedViewVisible(false);????????????setSpeedViewVisible(false);??????switchTimeLayout????????????????????????UI??????isSpeedChecked
                    }
                });
            } else if (id == R.id.lsq_confirmWrap) {
                if (mCamera.getMovieDuration() < Constants.MIN_RECORDING_TIME) {
                    String msg = getStringFromResource(R.string.min_recordTime) + Constants.MIN_RECORDING_TIME + "s";
                    TuSdk.messageHub().showToast(mContext, msg);

                    ToastUtils.showRedToast((Activity) mContext, getContext().getString(R.string.min_record_tips));
                    return;
                }

                //  AlbumUtils.openMediaAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
                if ((musicLocalPath != null && musicLocalPath != "")) {
                    pauseMusic(false);
                    setStaticBackgroundMusicBean();
                }
                // ????????????????????????????????????
                mCamera.stopRecording();
                initRecordProgress();
                setViewHideOrVisible(true);
                // ?????????????????????
            } else if (id == R.id.lsq_translucent_confirmWrap) {
                if (mTranslucentConfirmWrapButton.isSelected()) {
                    isCheckTransparentButton = true;
                    if (mCamera.getMovieDuration() < Constants.MIN_RECORDING_TIME) {
                        String msg = getStringFromResource(R.string.min_recordTime) + Constants.MIN_RECORDING_TIME + "s";
                        TuSdk.messageHub().showToast(mContext, msg);
                        return;
                    }
                    //  AlbumUtils.openMediaAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
                    if (musicLocalPath != null && musicLocalPath != "") {
                        pauseMusic(false);
                        setStaticBackgroundMusicBean();
                    }
                    // ????????????????????????????????????
                    mCamera.stopRecording();
                    mRecordPlayingButton.setVisibility(INVISIBLE);
                    mRecordPlayingButton.pauseAnimation();
                    mRecordButton.setVisibility(VISIBLE);
                    updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
                    initRecordProgress();
                }
            }
            // ????????????
            else if (id == R.id.lsq_backButton) {
                deleteResource();
                // ????????????
            } else if (id == R.id.lsq_saveImageButton) {
                saveResource();
                // ????????????
            } else if (id == R.id.lsq_cancel_button) {
                if(gVisionDynamicStickerBean.getCategories()!=null&&gVisionDynamicStickerBean.getCategories().size()>0&&gVisionDynamicStickerBean.getCategories().get(0).getStickers()!=null&&gVisionDynamicStickerBean.getCategories().get(0).getStickers().size()>0){
                    Glide.with(mContext).load(gVisionDynamicStickerBean.getCategories().get(0).getStickers().get(0).getPreviewImage())
                            .placeholder(R.drawable.ic_effects)
                            .error(R.drawable.ic_effects)
                            .into(effectsImg);
                    effectName.setText("");
                    stickerId = "";
                    selectEffect.setVisibility(INVISIBLE);
                }


                mCamera.removeMediaEffectsWithType(mPropsItemCategories.get(mPropsItemViewPager.getCurrentItem()).getMediaEffectType());
                mPropsItemPagerAdapter.notifyAllPageData();
            }
            // ??????60s??????
            else if (id == R.id.btn_60s) {
                switchTimeLayout(0);
                mCamera.setMaxRecordingTime(60);
                LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
                minTimeLayoutParams.leftMargin = ((interuptLayout != null ? interuptLayout.getWidth() : TuSdkContext.getScreenSize().width) * Constants.MIN_RECORDING_TIME / Constants.MAX_RECORDING_TIME_60) - minTimeButton.getWidth() + TuSdkContext.dip2px(16);
                updateRecordTime(_60sButton, 60);
            }
            // ??????30s??????
            else if (id == R.id.btn_30s) {
                switchTimeLayout(1);
                mCamera.setMaxRecordingTime(30);
                LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
                minTimeLayoutParams.leftMargin = ((interuptLayout != null ? interuptLayout.getWidth() : TuSdkContext.getScreenSize().width) * Constants.MIN_RECORDING_TIME / Constants.MAX_RECORDING_TIME_30) - minTimeButton.getWidth() + TuSdkContext.dip2px(16);
                updateRecordTime(_30sButton, 30);
            }
            // ??????15s??????
            else if (id == R.id.btn_15s) {
                switchTimeLayout(2);
                mCamera.setMaxRecordingTime(15);
                LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
                minTimeLayoutParams.leftMargin = ((interuptLayout != null ? interuptLayout.getWidth() : TuSdkContext.getScreenSize().width) * Constants.MIN_RECORDING_TIME / Constants.MAX_RECORDING_TIME) - minTimeButton.getWidth() + TuSdkContext.dip2px(16);
                updateRecordTime(_15sButton, 15);
            }
        }
    };

    private void isDisplaySingleFlipStatus(boolean isDisplayFlip) {
        int visibleStatus = isDisplayFlip ? INVISIBLE : VISIBLE;
        if (mBeautyButton != null) {
            mBeautyButton.setVisibility(visibleStatus);
        }
        if (mSpeedButton != null) {
            mSpeedButton.setVisibility(visibleStatus);
        }
        if (mFiltersButton != null) {
            mFiltersButton.setVisibility(visibleStatus);
        }
        if (mFlashOffButton != null) {
            mFlashOffButton.setVisibility(visibleStatus);
        }
        if (mTopBar != null) {
            mTopBar.setVisibility(visibleStatus);
        }
        if (selectEffect != null) {
            //???????????????
            if(isDisplayFlip){
                selectEffect.setVisibility(!TextUtils.isEmpty(effectName.getText().toString()) ? VISIBLE : INVISIBLE);
            }else{
                selectEffect.setVisibility(INVISIBLE);
            }
        }



    }

    public void setMinTimeButtonStatus(int visible) {
        if (minTimeButton != null) minTimeButton.setVisibility(visible);
    }

    public void hanldeCloseButton() {
        if (mCamera.getRecordingFragmentSize() > 0) {
            DialogHelper.recordClose(mContext, new DialogHelper.onRecordCloseClickListener() {
                @Override
                public void onDiscardVideoClick() {
                    finishMusic();
                    //TODO  ???????????????????
                    if (getDelegate() != null) getDelegate().finishRecordActivity();
                }

                @Override
                public void onStartOverClick() {
                    //???????????????????????????
                    if (mCamera != null && mCamera.getRecordingFragmentSize() > 0) {
                        if (mRecordProgress != null) {
                            int index = mCamera.getRecordingFragmentSize() - 1;
                            while (index >= 0) {
                                mCamera.popVideoFragment();
                                mRecordProgress.removePreSegment();
                                index--;
                            }
                        }

                        if (interuptLayout.getChildCount() != 0) {
                            interuptLayout.removeAllViews();
                        }

                        // ???????????????????????????????????????
                        if (mCamera.getRecordingFragmentSize() == 0) {
                            mCamera.cancelRecording();
                        }
                        // ????????????????????????????????????bug?????????????????? ????????????????????????isSpeedChecked??????boolean,??????setViewHideOrVisible???????????????????????????????????????????????????????????????????????????????????????isSpeedChecked
                        isSpeedChecked = false;
                        // ??????????????????
                        setViewHideOrVisible(true);
                        updateViewOnMovieRecordProgressChanged(0, 0);
                        selectMusic(new SelectSoundEvent (null,0));
                    }
                }

                @Override
                public void onSaveAsDraftClick() {
                    //TODO ?????????????????????????????????
                    AppConstants.isSaveDraft = true;
                    //  AlbumUtils.openMediaAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
                    if ((musicLocalPath != null && musicLocalPath != "")) {
                        pauseMusic(true);
                        AppConstants.musicLocalPath = "";
                        AppConstants.shootBackgroundMusicBean = null;
                    }
                    // ????????????????????????????????????
                    mCamera.stopRecording();
                    initRecordProgress();
                    setViewHideOrVisible(true);
                }
            });
        } else {
            if (getDelegate() != null) getDelegate().finishRecordActivity();
        }
    }

    /******************************???????????????*********************************/

    /**
     * ?????????????????????????????????????????????
     */
    void setStaticBackgroundMusicBean() {
        AppConstants.shootBackgroundMusicBean = currentBackMusicBean;
        AppConstants.musicLocalPath = musicLocalPath;
    }


    /******************************????????????*********************************/


    /**
     * ??????????????????
     */
    public void updateRecordTime(Button button, int maxRecordTimes) {
        _60sButton.setSelected(false);
        _30sButton.setSelected(false);
        _15sButton.setSelected(false);
        maxRecordTime = maxRecordTimes;
        button.setSelected(true);
    }


    /**
     * ?????????????????????
     *
     * @param cameraFlash
     */
    public void updateFlashMode(CameraConfigs.CameraFlash cameraFlash) {
        if (mCamera.isFrontFacingCameraPresent()) return;
        switch (cameraFlash) {
            case Off:
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                setTextButtonDrawableTop(mFlashOffButton, R.drawable.ic_shadow_flash_off);
                mCamera.setFlashMode(cameraFlash);
                break;
            case Torch:
                mLightingOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mLightingClose.setTextColor(getResources().getColor(R.color.lsq_color_white));
                setTextButtonDrawableTop(mFlashOffButton, R.drawable.ic_shadow_flash_on);
                mCamera.setFlashMode(cameraFlash);
                break;
        }
    }

    /**
     * ??????????????????
     *
     * @param type
     */
    private void updateCameraRatio(int type) {
        // ?????????????????????????????????
        if (mCamera.getRecordingFragmentSize() > 0) return;
        switch (type) {
            case RatioType.ratio_1_1:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square_selected);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full);
                switchCameraRatio(RatioType.ratio_1_1);
                break;
            case RatioType.ratio_3_4:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4_selected);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full);
                switchCameraRatio(RatioType.ratio_3_4);
                break;
            case RatioType.ratio_orgin:
                mRadio1_1.setImageResource(R.drawable.lsq_video_popup_ic_scale_square);
                mRadio3_4.setImageResource(R.drawable.lsq_video_popup_ic_scale_3_4);
                mRadioFull.setImageResource(R.drawable.lsq_video_popup_ic_scale_full_selected);
                switchCameraRatio(RatioType.ratio_orgin);
                break;
        }
    }

    /**
     * ?????????????????? ????????????????????????
     *
     * @param type ???????????? RatioType
     */
    private void switchCameraRatio(int type) {
        if (mCamera == null || !mCamera.canChangeRatio()) return;

        // ????????????????????????????????? ????????? changeRegionRatio ????????????
        mCamera.getRegionHandler().setOffsetTopPercent(getPreviewOffsetTopPercent(type));
        mCamera.changeRegionRatio(RatioType.ratio(type));
        mCamera.setRegionRatio(RatioType.ratio(type));

        // ??????????????????
        mCamera.getVideoEncoderSetting().videoSize = TuSdkSize.create((int) (mCamera.getCameraPreviewSize().width * RatioType.ratio(type)), mCamera.getCameraPreviewSize().width);

    }

    /**
     * ???????????? Ratio ?????????????????????????????????????????????-1 ???????????? ???????????????0-1???
     *
     * @param ratioType
     * @return
     */
    protected float getPreviewOffsetTopPercent(int ratioType) {
        if (ratioType == RatioType.ratio_1_1) return 0.1f;
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
    private int mRecordMode = RecordType.LONG_CLICK_RECORD;

    private float mPosX, mCurPosX;
    private static final int FLING_MIN_DISTANCE = 20;// ??????????????????

    private OnTouchListener onModeBarTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPosX = event.getX();
                    mCurPosX = 0;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    mCurPosX = event.getX();
                    // ??????????????????
                    if (mCurPosX - mPosX > 0
                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
                        //????????????
                        if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.CAPTURE);
                        } else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                        }
                        return false;
                    } else if (mCurPosX - mPosX < 0
                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
                        //????????????
                        if (mRecordMode == RecordType.CAPTURE) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                        } else if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
                        }
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    // ??????????????????
                    if (Math.abs(mCurPosX - mPosX) < FLING_MIN_DISTANCE || mCurPosX == 0) {
                        int id = v.getId();// ????????????
                        if (id == R.id.lsq_shootButton) {
                            switchCameraModeButton(RecordType.CAPTURE);
                            // ??????????????????
                        } else if (id == R.id.lsq_longButton) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                            // ??????????????????
                        } else if (id == R.id.lsq_clickButton) {
                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
                        }
                        return false;
                    }
            }
            return false;
        }
    };
    //????????????186dp, margin5 button52 ??????margin24
    int lastMargin = (TuSdkContext.getScreenSize().width / 2 - TuSdkContext.dip2px(186 - 5 - 52 / 2 + 24));

    /**
     * ????????????????????????
     */
    private void switchTimeLayout(int index) {
        int margin;

        if (index == 0) {
            margin = (TuSdkContext.getScreenSize().width / 2 - TuSdkContext.dip2px(24 + 5 + 52 / 2));//380;
        } else if (index == 1) {
            margin = (TuSdkContext.getScreenSize().width / 2 - TuSdkContext.dip2px(186 - 5 - 52 - 5 - 5 - 52 / 2 + 24));//230;
        } else {
            margin = (TuSdkContext.getScreenSize().width / 2 - TuSdkContext.dip2px(186 - 5 - 52 / 2 + 24));//60;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSelectTimeLayout.getLayoutParams();
        final ValueAnimator animator = ValueAnimator.ofInt(lastMargin, margin);
        animator.setDuration(500);
        animator.start();
        lastMargin = margin;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animator.getAnimatedValue();
                params.leftMargin = value;
                mSelectTimeLayout.setLayoutParams(params);

            }
        });

    }


    /**
     * ????????????????????????
     *
     * @param index
     */
    private void switchCameraModeButton(int index) {
        if (valueAnimator != null && valueAnimator.isRunning() || mRecordMode == index) return;

        // ??????????????????
        mShootButton.setTextColor(index == 0 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mLongButton.setTextColor(index == 1 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mClickButton.setTextColor(index == 2 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));

        // ??????????????????
        final float[] Xs = getModeButtonWidth();

        float offSet = 0;
        if (mRecordMode == 0 && index == 1)
            offSet = -(Xs[1] - Xs[0]) / 2 - (Xs[2] - Xs[1]) / 2;
        else if (mRecordMode == 0 && index == 2)
            offSet = -(Xs[1] - Xs[0]) / 2 - (Xs[3] - Xs[2]) / 2 - (Xs[2] - Xs[1]);
        else if (mRecordMode == 1 && index == 0)
            offSet = (Xs[1] - Xs[0]) / 2 + (Xs[2] - Xs[1]) / 2;
        else if (mRecordMode == 1 && index == 2)
            offSet = -(Xs[2] - Xs[1]) / 2 - (Xs[3] - Xs[2]) / 2;
        else if (mRecordMode == 2 && index == 0)
            offSet = (Xs[1] - Xs[0]) / 2 + (Xs[2] - Xs[1]) + (Xs[3] - Xs[2]) / 2;
        else if (mRecordMode == 2 && index == 1)
            offSet = (Xs[2] - Xs[1]) / 2 + (Xs[3] - Xs[2]) / 2;

        // ????????????
        valueAnimator = ValueAnimator.ofFloat(0, offSet);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float offSet = (float) animation.getAnimatedValue();
                mShootButton.setX(Xs[0] + offSet);
                mLongButton.setX(Xs[1] + offSet);
                mClickButton.setX(Xs[2] + offSet);
            }
        });
        valueAnimator.start();

        // ??????????????????
        if (index == RecordType.CAPTURE) {
            mSpeedButton.setVisibility(GONE);
            mSpeedModeBar.setVisibility(GONE);
            mSpeedModeBarBg.setVisibility(GONE);
            mChangeAudioLayout.setVisibility(GONE);
        } else if (index == RecordType.LONG_CLICK_RECORD) {
            mSpeedButton.setVisibility(VISIBLE);
            mSpeedModeBar.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mSpeedModeBarBg.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mChangeAudioLayout.setVisibility(VISIBLE);
        } else if (index == RecordType.SHORT_CLICK_RECORD) {
            mSpeedButton.setVisibility(VISIBLE);
            mSpeedModeBar.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mSpeedModeBarBg.setVisibility(isSpeedChecked ? VISIBLE : GONE);
            mChangeAudioLayout.setVisibility(VISIBLE);
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
        Xs[1] = mLongButton.getX();
        Xs[2] = mClickButton.getX();
        Xs[3] = mClickButton.getX() + mClickButton.getWidth();
        return Xs;
    }

    /**
     * ????????????
     *
     * @param selectedSpeedMode
     */
    private void selectSpeedMode(int selectedSpeedMode) {
        int childCount = mSpeedModeBar.getChildCount();

        for (int i = 0; i < childCount; i++) {
            Button btn = (Button) mSpeedModeBar.getChildAt(i);
            int speedMode = Integer.parseInt((String) btn.getTag());

            if (selectedSpeedMode == speedMode) {
                btn.setBackgroundResource(R.drawable.tusdk_view_widget_speed_button_bg);
                btn.setTextColor(Color.BLACK);
            } else {
                btn.setBackgroundResource(0);
                btn.setTextColor(Color.WHITE);
            }
        }

        // ??????????????????
        TuSdkRecorderVideoCamera.SpeedMode speedMode = TuSdkRecorderVideoCamera.SpeedMode.values()[selectedSpeedMode];
        mCamera.setSpeedMode(speedMode);
        changeSpeedIcon(selectedSpeedMode);
        isSpeedChecked = false;
        mSpeedModeBar.setVisibility(GONE);
        mSpeedModeBarBg.setVisibility(GONE);
        setSelectTimeLayoutHideOrVisible(true);
    }

    private void changeSpeedIcon(int selectedSpeedMode) {
        switch (selectedSpeedMode) {
            case 0:
                setTextButtonDrawableTop(mSpeedButton, R.drawable.ic_shadow_speed);
                break;
            case 1:
                setTextButtonDrawableTop(mSpeedButton, R.drawable.ic_shadow_speed_double);
                break;
            case 2:
                setTextButtonDrawableTop(mSpeedButton, R.drawable.ic_shadow_speed_triple);
                break;
            case 3:
                setTextButtonDrawableTop(mSpeedButton, R.drawable.ic_shadow_speed_half);
                break;
            case 4:
                setTextButtonDrawableTop(mSpeedButton, R.drawable.ic_shadow_speed_one_third);
                break;
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param isVisible ???????????? true??????false??????
     *                  ????????????????????????????????????false,?????????????????????
     */
    private void setSpeedViewVisible(boolean isVisible) {
        isSpeedChecked = isVisible;
        if (isVisible) {
            mSpeedModeBar.setVisibility(VISIBLE);
            mSpeedModeBarBg.setVisibility(VISIBLE);
            setSelectTimeLayoutHideOrVisible(false);
        } else {
            mSpeedModeBar.setVisibility(GONE);
            mSpeedModeBarBg.setVisibility(GONE);
            setSelectTimeLayoutHideOrVisible(false);
//            if(mCamera.getRecordingFragmentSize() == 0){
//                se tSelectTimeLayoutHideOrVisible(true);
//            }else {
//                setSelectTimeLayoutHideOrVisible(false);
//            }
        }
    }

    /****************************** ???????????? ****************************/

    /**
     * ??????????????????
     *
     * @param textButton ??????
     * @param id         ??????id
     */
    private void setTextButtonDrawableTop(TuSdkTextButton textButton, @DrawableRes int id) {
        Drawable top = getResources().getDrawable(id);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        textButton.setCompoundDrawables(null, top, null, null);
    }

    /**
     * ???????????????????????????
     */
    private void setRecordingViewVisible(boolean recording) {
        mRecordTimeRe.setVisibility(recording ? VISIBLE : GONE);
        mRightButtonLl.setVisibility(recording ? GONE : VISIBLE);
        if (mSelectTimeLayout.getVisibility() != (recording ? GONE : VISIBLE))
            mSelectTimeLayout.setVisibility(recording ? GONE : VISIBLE);

    }

    /**
     * ???icon bug ???????????????????????????
     */
    private void setRecordingViewVisible(boolean recording, int type) {
        mRecordTimeRe.setVisibility(recording ? VISIBLE : GONE);
        mRightButtonLl.setVisibility(recording ? GONE : VISIBLE);
        //if(mSelectTimeLayout.getVisibility() != (recording ? GONE : VISIBLE))
        mSelectTimeLayout.setVisibility(GONE);
    }

    /**
     * ???????????????????????? ?????????????????????????????????
     * ??
     *
     * @param isVisible ????????????
     */
    private void setBottomViewVisible(boolean isVisible) {
        setSelectTimeLayoutHideOrVisible(isVisible);
        mBottomBarLayout.setVisibility(isVisible ? VISIBLE : GONE);
        mRecordButton.setVisibility(isVisible ? VISIBLE : GONE);
//        mRecordModeBarLayout.setVisibility(isVisible && mCamera.getRecordingFragmentSize() <= 0 ? VISIBLE : GONE);
        mRecordModeBarLayout.setVisibility(GONE);
        setRollBackButton(isVisible && mCamera.getRecordingFragmentSize() > 0 ? VISIBLE : GONE);
    }


    /**
     * ??????????????????????????????????????????
     */
    private void setSelectTimeLayoutHideOrVisible(boolean isVisible) {
        int state = isVisible ? VISIBLE : GONE;
        if (mCamera.getRecordingFragmentSize() > 0) {
            if (!(mSelectTimeLayout.getVisibility() == (GONE)))
                mSelectTimeLayout.setVisibility(GONE);
        } else {
            mSelectTimeLayout.setVisibility(state);
        }
    }


    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param isVisible ????????????
     */
    private void setViewHideOrVisible(boolean isVisible) {

        int visibleState = isVisible ? VISIBLE : GONE;
        if (isCheckTransparentButton) {
            visibleState = GONE;
        }
        ;
        // ????????????(?????????????????????????????????)
        mTopBar.setVisibility(visibleState);
        // ???????????????????????????
        addSoundImageState(isVisible);
        // ????????????????????????
        mRightButtonLl.setVisibility(visibleState);
        // ??????????????????
        mRecordTimeRe.setVisibility(isVisible ? GONE : VISIBLE);
        // ????????????????????????ui
        mSpeedModeBar.setVisibility(isVisible && isSpeedChecked ? visibleState : GONE);
        mSpeedModeBarBg.setVisibility(isVisible && isSpeedChecked ? visibleState : GONE);
        // ????????????????????????(??????,????????????,??????,??????)
        mBottomBarLayout.setVisibility(visibleState);
        // mRecordModeBarLayout.setVisibility(visibleState);
        // ????????????
        mConfirmButton.setVisibility(GONE);
        // ?????????????????????

        setRollBackButton(GONE);


        //  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // TODO onPause????????? ??????mConfirmButton.setVisibility(visibleState); mRollBackButton.setVisibility(visibleState);?????????true,??????????????????
        if (mCamera.getRecordingFragmentSize() > 0) {
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            if (mConfirmButton.getVisibility() != visibleState)
                mConfirmButton.setVisibility(visibleState);
            if (mRollBackButton.getVisibility() != visibleState)
                setRollBackButton(visibleState);
            mRecordModeBarLayout.setVisibility(GONE);
            mOpenAlbumButton.setVisibility(GONE);
            setSelectTimeLayoutHideOrVisible(false);
        } else {
            mOpenAlbumButton.setVisibility(VISIBLE);
            setSelectTimeLayoutHideOrVisible(isVisible);
        }
        mOpenAlbumButton.setLayoutParams(layoutParams);

        if (isCheckTransparentButton) {
            setSelectTimeLayoutHideOrVisible(false);
        }
        isCheckTransparentButton = false;
    }


    public void setRollBackButton(int visibility) {
        mRollBackButton.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            addSoundLl.setAlpha(0.5f);
            addSoundLl.setSelected(false);
            addSoundImageState(true);
        } else {
            addSoundLl.setAlpha(1f);
            addSoundLl.setSelected(true);
            addSoundImageState(false);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param isVisible
     */
    private void addSoundImageState(boolean isVisible) {
        if (isVisible) {
            // ?????????????????????
            if (mCamera.getMovieDuration() > 0) {
                // ?????????????????????,????????????,????????????
                if (musicLocalPath != null && !musicLocalPath.equals("")) {
                    addSoundLl.setSelected(false);
                    addSoundImg.setSelected(false);
                } else {
                    //?????????????????????????????????,????????????????????????????????????,???????????????
                    addSoundLl.setSelected(false);
                    addSoundImg.setSelected(true);
                }
            }
            // ????????????????????????
            else {
                // ??????????????? ????????????,?????????
                if (musicLocalPath != null && !musicLocalPath.equals("")) {
                    addSoundLl.setSelected(true);
                    addSoundImg.setSelected(false);
                } else {
                    // ?????????,???????????????,?????????????????????
                    addSoundLl.setSelected(true);
                    addSoundImg.setSelected(true);
                }
            }

        }
    }


    /**
     * ????????????????????????
     *
     * @param type
     */
    private void updateRecordButtonResource(int type) {
        switch (type) {
            // ??????
            case RecordType.CAPTURE:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_shoot);
                mRecordButton.setImageResource(0);
                break;
            // ????????????
            case RecordType.LONG_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                mRecordButton.setImageResource(0);
                break;
            // ????????????
            case RecordType.SHORT_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_click_record_unpressed);
                //   mRecordButton.setImageResource(R.drawable.video_ic_recording);
                mRecordPlayingButton.setVisibility(INVISIBLE);
                mRecordPlayingButton.pauseAnimation();
                mRecordButton.setVisibility(VISIBLE);

                break;
            // ???????????????
            case RecordType.LONG_CLICK_RECORDING:
                mRecordPlayingButton.setVisibility(VISIBLE);
                mRecordPlayingButton.playAnimation();
                mRecordButton.setVisibility(INVISIBLE);

                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                mRecordButton.setImageResource(0);
                break;
            // ???????????????
            case RecordType.SHORT_CLICK_RECORDING:
                mRecordPlayingButton.setVisibility(VISIBLE);
                mRecordPlayingButton.playAnimation();
                mRecordButton.setVisibility(INVISIBLE);

                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                //  mRecordButton.setImageResource(R.drawable.video_ic_recording);
                break;

        }
    }


    /**
     * ??????????????????
     */
    private void setFilterContentVisible(boolean isVisible) {
        mFilterContent.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }


    /********************************** ???????????? ***********************/

    /**
     * ????????????????????????
     *
     * @param state     ????????????
     * @param recording ?????????????????????
     */
    public void updateMovieRecordState(TuSdkRecorderVideoCamera.RecordState state, boolean recording) {
        if (state == TuSdkRecorderVideoCamera.RecordState.Recording) // ????????????
        {
            if (mRecordMode == RecordType.LONG_CLICK_RECORD)
                updateRecordButtonResource(RecordType.LONG_CLICK_RECORDING);
            else
                updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
            setViewHideOrVisible(false);
            mMoreConfigLayout.setVisibility(GONE);

        } else if (state == TuSdkRecorderVideoCamera.RecordState.Paused) // ???????????????
        {
            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint((TuSdkContext.getDisplaySize().width - TuSdkContext.dip2px(36)) * mRecordProgress.getProgress());
            }
            mRecordProgress.pauseRecord();
            //????????????????????????????????????pause????????????mTranslucentConfirmWrapButton
            if (mTranslucentConfirmWrapButton != null) {
                mTranslucentConfirmWrapButton.setVisibility(GONE);
            }
            setViewHideOrVisible(true);
            updateRecordButtonResource(mRecordMode);
            if (mCamera.getMovieDuration() < Constants.MIN_RECORDING_TIME) {
                mConfirmButton.setSelected(false);
            } else {
                mConfirmButton.setSelected(true);
            }
        } else if (state == TuSdkRecorderVideoCamera.RecordState.RecordCompleted) //????????????????????????????????????????????????????????????????????????????????????
        {
            recordState = 1;
            String msg = this.getResources().getString(R.string.lsq_record_completed);
            getStringFromResource(R.string.lsq_record_completed);
            TuSdk.messageHub().showToast(mContext, msg);

            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint((TuSdkContext.getDisplaySize().width - TuSdkContext.dip2px(36)) * mRecordProgress.getProgress());
            }
            updateRecordButtonResource(mRecordMode);
            setViewHideOrVisible(true);

            if (mCamera.getMovieDuration() < Constants.MIN_RECORDING_TIME) {
                mConfirmButton.setSelected(false);
            } else if ((mCamera.getMovieDuration() >= Constants.MIN_RECORDING_TIME) && (mCamera.getMovieDuration() < mCamera.getMaxRecordingTime())) {
                mConfirmButton.setSelected(true);
            } else {
                //??????????????????????????????????????????????????????
                mCamera.stopRecording();
                initRecordProgress();
                setViewHideOrVisible(true);
            }
        } else if (state == TuSdkRecorderVideoCamera.RecordState.Saving) // ??????????????????
        {
            String msg = getStringFromResource(R.string.new_movie_saving);
            TuSdk.messageHub().setStatus(mContext, msg);
        } else if (state == TuSdkRecorderVideoCamera.RecordState.SaveCompleted) {

            String msg = getStringFromResource(R.string.lsq_video_save_ok);
            TuSdk.messageHub().showToast(mContext, msg);

            updateRecordButtonResource(mRecordMode);
            //???????????????????????????saving????????????icon
            //setViewHideOrVisible(true);
        }
        backFormEdit();
    }

    /**
     * ??????????????????????????????
     */
    void backFormEdit() {
        if (isBackFromEdit) {
            //???????????????????????????
            if (mCamera != null && mCamera.getRecordingFragmentSize() > 0) {
                if (mRecordProgress != null) {
                    int index = mCamera.getRecordingFragmentSize() - 1;
                    while (index >= 0) {
                        mCamera.popVideoFragment();
                        mRecordProgress.removePreSegment();
                        index--;
                    }
                }
                // ???????????????????????????????????????
                if (mCamera.getRecordingFragmentSize() == 0) {
                    mCamera.cancelRecording();
                }

            }
            updateViewOnMovieRecordProgressChanged(0, 0);
            // ????????????????????????????????????bug?????????????????? ????????????????????????isSpeedChecked??????boolean,??????setViewHideOrVisible???????????????????????????????????????????????????????????????????????????????????????isSpeedChecked
            isSpeedChecked = false;
            // ??????????????????
            setViewHideOrVisible(true);
        }
        isBackFromEdit = false;
    }

    /**
     * ????????????????????????
     *
     * @param margingLeft
     */
    private void addInteruptPoint(float margingLeft) {
        // ?????????????????? (?????????????????????
        Button interuptBtn = new Button(mContext);
        LayoutParams lp = new LayoutParams(TuSdkContext.dip2px(2),
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
    //TODO mTranslucentConfirmWrapButton ??????????????????????????????
    public void updateViewOnMovieRecordProgressChanged(float progress, float durationTime) {
        if (durationTime > Constants.MIN_RECORDING_TIME) {
            mTranslucentConfirmWrapButton.setSelected(true);
            //??????1s????????????1s??????
            setMinTimeButtonStatus(View.INVISIBLE);
        } else {
            mTranslucentConfirmWrapButton.setSelected(false);
            setMinTimeButtonStatus(View.VISIBLE);
        }
        TLog.e("progress -- %s durationTime -- %s", progress, durationTime);
        mRecordProgress.setProgress(progress);
        int time = Math.round(durationTime);
        String timeStr;
        if (time < 10) {
            timeStr = "0" + time;
        } else {
            timeStr = String.valueOf(time);
        }
        mRecordTimeTv.setText("0:" + timeStr);
    }

    /**
     * ?????????????????????????????????
     *
     * @param error
     * @param isRecording
     */
    public void updateViewOnMovieRecordFailed(TuSdkRecorderVideoCamera.RecordError error, boolean isRecording) {
        if (error == TuSdkRecorderVideoCamera.RecordError.MoreMaxDuration) // ?????????????????? ????????????????????????????????????startRecording???????????????
        {
            String msg = getStringFromResource(R.string.max_recordTime) + Constants.MAX_RECORDING_TIME + "s";
            TuSdk.messageHub().showToast(mContext, msg);

        } else if (error == TuSdkRecorderVideoCamera.RecordError.SaveFailed) // ??????????????????
        {
            String msg = getStringFromResource(R.string.new_movie_error_saving);
            TuSdk.messageHub().showError(mContext, msg);
        } else if (error == TuSdkRecorderVideoCamera.RecordError.InvalidRecordingTime) {
            TuSdk.messageHub().showError(mContext, R.string.lsq_record_time_invalid);
        }
        setViewHideOrVisible(true);
    }

    /**
     * ?????????????????????????????????
     *
     * @param isRecording
     */
    public void updateViewOnMovieRecordComplete(boolean isRecording) {
        TuSdk.messageHub().dismissRightNow();
        String msg = getStringFromResource(R.string.new_movie_saved);
        TuSdk.messageHub().showSuccess(mContext, msg);

        // ?????????????????????(??????????????????)
        mRecordProgress.clearProgressList();
        setViewHideOrVisible(true);
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    protected String getStringFromResource(int StringId) {
        return getResources().getString(StringId);
    }


    public void onResume() {

    }


    void finishMusic() {
        AppConstants.musicLocalPath = "";
        if (mp3Player != null) {
            mp3Player.release();
            mp3Player = null;
        }
    }

    void pauseMusic(boolean isClean) {
        if (mp3Player != null && mp3Player.getState() == Mp3Player.STATE_PLAYING) {
            mp3Player.pause();
            if (isClean) {
                mp3Player = null;
            }

        }
    }

    void playMusic() {
        if (musicLocalPath != null && !musicLocalPath.equals("")) {
            try {
                if (mp3Player != null) {
                    if (mp3Player.getState() == Mp3Player.STATE_PREPARED) {
                        mp3Player.play();
                    } else if (mp3Player.getState() == Mp3Player.STATE_PAUSE) {
                        mp3Player.play();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    void  getFirstFrame(){
        Drawable drawable = null;
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, "date_added desc");
        ContentResolver cr = mContext.getContentResolver();
        if (cursor == null) {
            return;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            @SuppressLint("Range") long thumbNailsId = cursor.getLong(cursor.getColumnIndex("_ID"));
             bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, thumbNailsId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

          if(bitmap != null){
              drawable = new BitmapDrawable(bitmap);
              drawable.setBounds(0, 0, TuSdkContext.dip2px(28f),TuSdkContext.dip2px(28f));
              break;
          }
        }
        cursor.close();
        if(drawable != null){
            Drawable finalDrawable = drawable;
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOpenAlbumButton.setCompoundDrawables(null, finalDrawable, null, null);
                }
            }); 
        }
       
    }
    
    public void recycleBitmap(){
        if(bitmap != null&& !bitmap.isRecycled()){
            bitmap.recycle();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void selectMusic(SelectSoundEvent selectSoundEvent) {
        SoundBean soundBean = selectSoundEvent.soundBean;
        if (soundBean == null) {
            currentBackMusicBean = null;
            AppConstants.shootBackgroundMusicBean = null;
            tvSoundName.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvSoundName.getLayoutParams();
            layoutParams.width = (int) TextWidthUtils.getTextWith(getContext(), tvSoundName, getContext().getString(R.string.add_sound));
            layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.START;
            tvSoundName.setText(getContext().getString(R.string.add_sound));
            addSoundImg.setSelected(true);
            addSoundLl.setSelected(true);
            musicLocalPath = "";
        } else {
            musicLocalPath = soundBean.getLocalPath();
            BackgroundMusicBean bean = new BackgroundMusicBean();
            bean.setFromSearch(true);
            bean.setTitle(soundBean.getSoundTitle());
            bean.setImageUrl(soundBean.getSoundPic());
            bean.setSoundUrl(soundBean.getSoundUrl());
            bean.setDuration(soundBean.getDuration());
            bean.setMusicAuthor(soundBean.getSoundContent());
            bean.setLocalPath(musicLocalPath);
            bean.setSelect(true);
            bean.setMusicId(soundBean.getSoundsId());
            currentBackMusicBean = bean;

            if (selectSoundEvent.type == 0) {
                tvSoundName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvSoundName.setMarqueeRepeatLimit(-1);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvSoundName.getLayoutParams();
                layoutParams.width = (int) TextWidthUtils.getTextWith(getContext(), tvSoundName, soundBean.getSoundTitle()+getContext().getResources().getString(R.string.tv_blank)) - 2;
                layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.START;
                tvSoundName.setText(soundBean.getSoundTitle()+getContext().getResources().getString(R.string.tv_blank));
                addSoundImg.setSelected(false);
                if (mp3Player == null) {
                    //????????????
                    mp3Player = Mp3Player.getInstance();
                    mp3Player.init(mAudioPlayerListener);
                } else {
                    mp3Player.reset();
                }
                try {
                    if (mp3Player != null) {
                        mp3Player.setDataSource(soundBean.getLocalPath());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void goBack(BackEvent backEvent) {
        // getDelegate().startRecording();
        //   isBackFromEdit = true;
    }
}