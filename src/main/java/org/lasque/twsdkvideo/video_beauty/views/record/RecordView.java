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
     * 录制类型状态
     */
    public interface RecordType {
        // 拍摄
        int CAPTURE = 0;
        // 长按拍摄
        int LONG_CLICK_RECORD = 1;
        // 单击拍摄
        int SHORT_CLICK_RECORD = 2;
        // 长按录制中
        int LONG_CLICK_RECORDING = 3;
        // 短按录制中
        int SHORT_CLICK_RECORDING = 4;
    }

    /**
     * 录制视频动作委托
     */
    public interface TuSDKMovieRecordDelegate {
        /**
         * 开始录制视频
         */
        void startRecording();

        /**
         * 是否正在录制
         *
         * @return
         */
        boolean isRecording();

        /**
         * 暂停录制视频
         */
        void pauseRecording();

        /**
         * 停止录制视频
         */
        void stopRecording();

        /**
         * 关闭录制界面
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
     * 录制相机
     */
    protected TuSdkRecorderVideoCameraImpl mCamera;
    /**
     * 录制视频动作委托
     */
    private TuSDKMovieRecordDelegate mDelegate;
    /**
     * 拍照获得的Bitmap
     */
    private Bitmap mCaptureBitmap;

    private SharedPreferences mFilterValueMap;

    /**
     * 速度选项是否开启
     */
    private boolean isSpeedChecked = false;

    /**
     * 美颜是否关闭
     */
    private boolean isBeautyClose = true;

    /**
     * 闪光灯是否开启
     */
    private boolean isOpenFlash = false;

    /**
     * 是否点击透明按钮保存
     */
    private boolean isCheckTransparentButton = false;

    /**
     * 是否是返回页面
     */
    private boolean isBackFromEdit = false;

    /**
     * 音乐的本地地址
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
     * 默认选中滤镜
     */
    private static final int DEFAULT_POSITION = -1;
    /**
     * 滤镜视图
     */
    private RelativeLayout mFilterContent;
    /**
     * 参数调节视图
     */
    protected ParamsConfigView mFilterConfigView;
    /**
     * 滤镜列表
     */
    private RecyclerView mFilterRecyclerView;
    /**
     * 滤镜列表Adapter
     */
    private FilterRecyclerAdapter mFilterAdapter;
    /**
     * 滤镜列表
     */
    private RecyclerView mComicsFilterRecyclerView;
    /**
     * 滤镜列表Adapter
     */
    private FilterRecyclerAdapter mComicsFilterAdapter;
    /**
     * 用于记录上一次位置
     */
    private int mCurrentPosition = DEFAULT_POSITION;
    /**
     * 用于记录上一次位置
     */
    private int mComicsCurrentPosition = 0;
    /**
     * 滤镜名称
     */
    private TextView mFilterNameTextView;
    /**
     * 是否切换漫画滤镜
     */
    private boolean isComicsFilterChecked = false;

    /**
     * 当前录制的最大秒速
     */
    private int maxRecordTime = 15;

    /**
     * 当前选择时间的位置(60s:0,30:1,15:2)
     */
    private int currentTimeIndex = 2;

    /******************************* 顶部和右侧布局样式Start ********************************/

    /**
     * 顶部按键
     */
    private RelativeLayout mTopBar, mTopRecordProgress;

    private View selectEffect;
    private TextView effectName;
    private String stickerId = "";
    private ArrayList<String> stickerIds = new ArrayList<>();
    private ImageView effectImg;

    /**
     * 录制进度
     **/
    private HorizontalProgressBar mRecordProgress;

    /**
     * 打补丁，解决退回后台返回后的bug用的boolean
     **/
    private boolean isBackFromOnPause;

    /**
     * 录制的视频之间的断点
     */
    private RelativeLayout interuptLayout;

    /**
     * 顶部录制时间布局
     */
    private RelativeLayout mRecordTimeRe;

    /**
     * 顶部录制时间文本
     */
    private TextView mRecordTimeTv;

    /**
     * 关闭按键
     */
    private TuSdkTextButton mCloseButton;

    /**
     * 添加声音按钮
     */
    private LinearLayout addSoundLl;

    /**
     * 添加声音按钮的图片
     */
    private ImageView addSoundImg;

    /**
     * 声音名称
     */
    private TextView tvSoundName;

    /**
     * 右侧功能按钮布局
     */
    private LinearLayout mRightButtonLl;

    /**
     * 切换摄像头按键
     */
    private TuSdkTextButton mSwitchButton;

    /**
     * 美颜按键
     */
    private TuSdkTextButton mBeautyButton;

    /**
     * 速度按键
     */
    private TuSdkTextButton mSpeedButton;

    /**
     * 滤镜按钮
     */
    private TuSdkTextButton mFiltersButton;

    /**
     * 闪光灯按钮
     */
    private TuSdkTextButton mFlashOffButton;

    /**
     * 美颜设置
     */
    private LinearLayout mSmartBeautyTabLayout;

    /**
     * 美颜的列表
     */
    private RecyclerView mBeautyRecyclerView;

    /******************************* 顶部和右侧布局样式End********************************/


    /******************************* 底部布局样式End********************************/

    /**
     * 选择时间布局
     */
    private LinearLayout mSelectTimeLayout;

    /**
     * 60s布局
     */
    private Button _60sButton;

    /**
     * 30s布局
     */
    private Button _30sButton;

    /**
     * 15s布局
     */
    private Button _15sButton;

    /**
     * 底部功能按键视图
     */
    private LinearLayout mBottomBarLayout;

    /**
     * 回退按钮
     */
    private TuSdkTextButton mRollBackButton;


    /**
     * 录制按键
     */
    private ImageView mRecordButton;
    private LottieAnimationView mRecordPlayingButton;

    /**
     * 确认保存视频
     **/
    private TuSdkTextButton mConfirmButton;

    /**
     * 半透明确认保存视频
     **/
    private TuSdkTextButton mTranslucentConfirmWrapButton;
    ;

    /**
     * 贴纸
     */
    private View mStickerWrapButton;

    /**
     * 相册选取
     */
    private TuSdkTextButton mOpenAlbumButton;

    /**
     * 拍摄模式视图
     */
    private RelativeLayout mRecordModeBarLayout;
    /**
     * 拍照按键
     */
    private TuSdkTextButton mShootButton;
    /**
     * 长按录制
     */
    private TuSdkTextButton mLongButton;
    /**
     * 单击拍摄
     */
    private TuSdkTextButton mClickButton;

    private String mCurrentFilterCode = "";


    /******************************* 底部布局样式End********************************/


    /******************************* 其他布局样式Start********************************/

    /**
     * 视频速度模式视图
     */
    private ViewGroup mSpeedModeBar, mSpeedModeBarBg;

    /**
     * 视频时长最小有效按钮
     */
    private Button minTimeButton;

    /**
     * 更多设置视图
     */
    private LinearLayout mMoreConfigLayout;
    /**
     * 自动对焦开关
     */
    private TextView mFocusOpen;
    private TextView mFocusClose;
    /**
     * 闪关灯开关
     */
    private TextView mLightingOpen;
    private TextView mLightingClose;
    /**
     * Radio设置
     */
    private ImageView mRadioFull;
    private ImageView mRadio3_4;
    private ImageView mRadio1_1;
    /**
     * 变声
     */
    private RelativeLayout mChangeAudioLayout;
    private RadioGroup mChangeAudioGroup;


    // 道具布局 贴纸+哈哈镜

    /**
     * 道具布局
     */
    private LinearLayout mPropsItemLayout;
    /**
     * 取消道具
     */
    private ImageView mPropsItemCancel;
    /**
     * 道具 Layout
     */
    private ViewPager2 mPropsItemViewPager;
    /**
     * 道具  PropsItemPagerAdapter
     */
    private PropsItemPagerAdapter<PropsItemPageFragment> mPropsItemPagerAdapter;

    private TabPagerIndicator mPropsItemTabPagerIndicator;
    /**
     * 道具分类类别
     */
    private List<PropsItemCategory> mPropsItemCategories = new ArrayList<>();

    //曝光补偿
    private SeekBar mExposureSeekbar;


    /**
     * 图片预留视图
     **/
    private ImageView mPreViewImageView;
    /**
     * 返回拍照按钮
     **/
    private TuSdkTextButton mBackButton;
    /**
     * 保存按钮
     **/
    private TuSdkTextButton mSaveImageButton;

    /******************************* 其他布局样式End********************************/
    /***
     * 播放背景音乐
     */

    private Mp3Player mp3Player;

    //记录录像机状态，为弥补一些状态缺失导致的bug (complete = 1）
    private int recordState;

    /**
     * 当前的返回的音乐
     */
    private BackgroundMusicBean currentBackMusicBean;

    /**
     * 视频第一帧的bitmap
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

    /******************************* 初始化Start********************************/

    protected void init(Context context) {
        EventBus.getDefault().register(this);
        LayoutInflater.from(context).inflate(getLayoutId(), this,
                true);

        //音乐播放
        mp3Player = Mp3Player.getInstance();
        mp3Player.init(mAudioPlayerListener);
        // 顶部操作控件
        mTopBar = findViewById(R.id.lsq_topBar);
        selectEffect  = findViewById(R.id.select_effect);
        effectName  = findViewById(R.id.effect_name);
        effectImg  = findViewById(R.id.effect_img);
        mTopRecordProgress = findViewById(R.id.lsq_process_container);
        RelativeLayout.LayoutParams topRecordProgressLayoutParams = (LayoutParams) mTopRecordProgress.getLayoutParams();
        topRecordProgressLayoutParams.topMargin = VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(6);
        mTopRecordProgress.setLayoutParams(topRecordProgressLayoutParams);
        // 录制进度条
        mRecordProgress = findViewById(R.id.lsq_record_progressbar);
        // 3s最小位置显示
        minTimeButton = (Button) findViewById(R.id.lsq_minTimeBtn);

        // 用来记录录制断点布局
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
        // 右侧操作控件
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

        //选择录制时间区域
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

        // 底部功能按键视图
        mBottomBarLayout = findViewById(R.id.lsq_button_wrap_layout);
        // 特效按键
        mStickerWrapButton = findViewById(R.id.lsq_stickerWrap);
        effectsImg = findViewById(R.id.effects_img);
        mStickerWrapButton.setOnClickListener(onClickListener);


        // 相册选取按钮
        mOpenAlbumButton = findViewById(R.id.lsq_tab_upload);
        mOpenAlbumButton.setOnClickListener(onClickListener);
        // 保存视频
        mConfirmButton = findViewById(R.id.lsq_confirmWrap);
        mConfirmButton.setOnClickListener(onClickListener);

        // 透明的保存按钮
        mTranslucentConfirmWrapButton = findViewById(R.id.lsq_translucent_confirmWrap);
        RelativeLayout.LayoutParams translucentConfirmlayoutParams = (LayoutParams) mTranslucentConfirmWrapButton.getLayoutParams();
        int translucentConfirmRightMargin = (TuSdkContext.getDisplaySize().width - TuSdkContext.dip2px(24 + 24)) / 3 - TuSdkContext.dip2px((float) (32 + 30 + 36.17));
        translucentConfirmlayoutParams.rightMargin = translucentConfirmRightMargin;
        mTranslucentConfirmWrapButton.setLayoutParams(translucentConfirmlayoutParams);

        mTranslucentConfirmWrapButton.setOnClickListener(onClickListener);
        // 录制按钮
        mRecordButton = findViewById(R.id.lsq_recordButton);
        mRecordButton.setOnTouchListener(onTouchListener);

        mRecordPlayingButton = findViewById(R.id.lsq_recording);
        RelativeLayout.LayoutParams recordPlaylayoutParams = (LayoutParams) mRecordPlayingButton.getLayoutParams();
        //int marginBottom = - TuSdkContext.dip2px(50 + 31.65);
        //动态计算margin 保持和原有播放键高度一致 原有播放器icon高度 TuSdkContext.dip2px(76.174f) 录制中icon TuSdkContext.dip2px(92.174f)

        int marginBottom = TuSdkContext.dip2px(18f);
        recordPlaylayoutParams.bottomMargin = marginBottom;
        mRecordPlayingButton.setLayoutParams(recordPlaylayoutParams);
        mRecordPlayingButton.setOnTouchListener(onTouchListener);


        // 回退按钮
        mRollBackButton = (TuSdkTextButton) findViewById(R.id.lsq_backWrap);
        mRollBackButton.setOnClickListener(onClickListener);

        /*******************暂时用不到start********************/
        // more_config_layout
        mMoreConfigLayout = findViewById(R.id.lsq_more_config_layout);
        // 自动对焦
        mFocusOpen = findViewById(R.id.lsq_focus_open);
        mFocusClose = findViewById(R.id.lsq_focus_close);
        mFocusOpen.setOnClickListener(onClickListener);
        mFocusClose.setOnClickListener(onClickListener);
        // 闪光灯
        mLightingOpen = findViewById(R.id.lsq_lighting_open);
        mLightingClose = findViewById(R.id.lsq_lighting_close);
        mLightingOpen.setOnClickListener(onClickListener);
        mLightingClose.setOnClickListener(onClickListener);
        // 比例
        mRadioFull = findViewById(R.id.lsq_radio_full);
        mRadio3_4 = findViewById(R.id.lsq_radio_3_4);
        mRadio1_1 = findViewById(R.id.lsq_radio_1_1);
        mRadioFull.setOnClickListener(onClickListener);
        mRadio3_4.setOnClickListener(onClickListener);
        mRadio1_1.setOnClickListener(onClickListener);
        // 变声
        mChangeAudioLayout = findViewById(R.id.lsq_audio_layout);
        mChangeAudioGroup = findViewById(R.id.lsq_audio_group);
        mChangeAudioGroup.setOnCheckedChangeListener(mAudioOnCheckedChangeListener);

        // 模式切换视图
        mRecordModeBarLayout = findViewById(R.id.lsq_record_mode_bar_layout);
        mRecordModeBarLayout.setOnTouchListener(onModeBarTouchListener);


        /*******************暂时用不到end********************/


        // 模式切换
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

        // 速度控制条
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

        // 美颜Bar
        mSmartBeautyTabLayout = findViewById(R.id.lsq_smart_beauty_layout);
        setBeautyLayout(false);
        mBeautyRecyclerView = findViewById(R.id.lsq_beauty_recyclerView);
        mBeautyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // 美颜类型
        mBeautyRecyclerAdapter = new BeautyRecyclerAdapter(getContext());
        mBeautyRecyclerAdapter.setOnSkinItemClickListener(beautyItemClickListener);
        // 微整形
        mBeautyPlasticRecyclerAdapter = new BeautyPlasticRecyclerAdapter(getContext(), mBeautyPlastics);
        mBeautyPlasticRecyclerAdapter.setOnBeautyPlasticItemClickListener(beautyPlasticItemClickListener);

        // 美妆
        mController = new CosmeticPanelController(getContext());
        initCosmeticView();

        // 滤镜调节
        mFilterConfigView = findViewById(R.id.lsq_filter_config_view);
        mFilterConfigView.setSeekBarDelegate(mFilterConfigViewSeekBarDelegate);
        // 微整形调节
        mBeautyPlasticsConfigView = findViewById(R.id.lsq_beauty_plastics_config_view);
        mBeautyPlasticsConfigView.setPrefix("lsq_beauty_");
        mBeautyPlasticsConfigView.setSeekBarDelegate(mBeautyPlasticConfigViewSeekBarDelegate);

        //曝光补偿控制
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
        // 初始化设置点击录制
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
                //国际化获取FilterTypeItemList
                FilterFragment fragment = FilterFragment.newInstance(group, getFilterTypeItemList(i1));
                i1++;
                if (group.groupId == 252) {
                    fragment.setOnFilterItemClickListener(new FilterFragment.OnFilterItemClickListener() {
                        @Override
                        public void onFilterItemClick(String code, int position) {
                            mCurrentFilterCode = code;
                            mCurrentPosition = position;
                            //设置滤镜
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
                                //设置滤镜
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
     * 初始化进度
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
        //离开界面的时候初始化
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
        //点击停止录制
        //setRecordingViewVisible(false);
        //setRecordingViewVisible(false, 0);
        //updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
      //  pauseMusic(false);
        //离开界面的时候不改变/显示任何当前icon
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
        // 初始化设置点击录制
        //switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);

        //退到后台再回来
        if (isBackFromOnPause) {
            getDelegate().startRecording();
            isBackFromEdit = true;
            isBackFromOnPause = false;
        }
    }


    /******************************* 初始化End********************************/


    /*****************************各种回调Start******************************/
    /**
     * 特效数据应用、移除回调
     */
    private TuSdkRecorderVideoCamera.TuSdkMediaEffectChangeListener mMediaEffectChangeListener = new TuSdkRecorderVideoCamera.TuSdkMediaEffectChangeListener() {
        @Override
        public void didApplyingMediaEffect(final TuSdkMediaEffectData mediaEffectData) {
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    switch (mediaEffectData.getMediaEffectType()) {
                        case TuSdkMediaEffectDataTypeFilter: //滤镜效果
                            List<SelesParameters.FilterArg> filterArgs = new ArrayList<>();
                            SelesParameters.FilterArg filterArg = mediaEffectData.getFilterArg("mixied");// 获取效果参数
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
         * 移除特效后回调
         * @param mediaEffects
         */
        @Override
        public void didRemoveMediaEffect(List<TuSdkMediaEffectData> mediaEffects) {

        }
    };

    /**
     * 录制按键
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
                    // 点击拍照
                    if (mRecordMode == RecordType.CAPTURE) {
                        mCamera.captureImage();
                    }
                    // 长按录制
                    else if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                        getDelegate().pauseRecording();
                        updateRecordButtonResource(RecordType.LONG_CLICK_RECORD);
                    }
                    // 点击录制
                    else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
                        mRecordPlayingButton.setVisibility(VISIBLE);
                        mRecordButton.setVisibility(INVISIBLE);

                        // 当前状态是否录制中
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
                            //开始录制
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
        //点击停止录制
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
            //点击停止录制
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
     * 变声切换
     */
    RadioGroup.OnCheckedChangeListener mAudioOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.lsq_audio_normal) {// 正常
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Normal);
            } else if (checkedId == R.id.lsq_audio_monster) {// 怪兽
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Monster);
            } else if (checkedId == R.id.lsq_audio_uncle) {// 大叔
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Uncle);
            } else if (checkedId == R.id.lsq_audio_girl) {// 女生
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Girl);
            } else if (checkedId == R.id.lsq_audio_lolita) {// 萝莉
                mCamera.setSoundPitchType(TuSdkAudioPitchEngine.TuSdkSoundPitchType.Lolita);
            }
        }
    };

    /**
     * 属性动画监听事件
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
     * MP3播放器监听事件
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
     * 传递录制相机对象
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

    /*****************************各种回调End******************************/


    /******************************** 滤镜Start ********************************************/

    /**
     * 初始化滤镜
     */
    private void initFilterRecyclerView() {
        mFilterNameTextView = findViewById(R.id.lsq_filter_name);
        mFilterContent = findViewById(R.id.lsq_filter_content);
        /** 屏蔽在滤镜栏显示时 在滤镜栏上面的手势操作  如不需要 可删除*/
        mFilterContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setFilterContentVisible(false);

    }

    /**
     * 显示滤镜列表
     */
    private void showFilterLayout() {
        // 滤镜栏向上动画并显示
        ViewCompat.setTranslationY(mFilterContent,
                mFilterContent.getHeight());
        ViewCompat.animate(mFilterContent).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);

        setFilterContentVisible(true);

        // 设置滤镜参数调节
        if (mCurrentPosition > 0 && mFilterConfigView != null) {
            mFilterConfigView.invalidate();
        }
    }

    /**
     * 滤镜调节栏
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
     * 滤镜效果改变监听事件
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
//                        // 调用极致美颜
//                        switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty);
//                }
//            }, 500);
            ThreadHelper.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).size() == 0) {
                        // 添加一个默认微整形特效
//                        TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
//                        mCamera.addMediaEffectData(plasticFaceEffect);

//                        TuSdkMediaReshapeEffect effect = new TuSdkMediaReshapeEffect();
//                        mCamera.addMediaEffectData(effect);
//                        for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
//                            if (arg.equalsKey("eyeSize")) {// 大眼
//                                arg.setMaxValueFactor(0.85f);// 最大值限制
//                            }
//                            if (arg.equalsKey("chinSize")) {// 瘦脸
//                                arg.setMaxValueFactor(0.9f);// 最大值限制
//                            }
//                            if (arg.equalsKey("noseSize")) {// 瘦鼻
//                                arg.setMaxValueFactor(0.6f);// 最大值限制
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
            // 滤镜切换需要做延时
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
         * 拍照数据回调
         * @param bitmap 图片
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
     * 滤镜组列表点击事件
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
     * 漫画滤镜组列表点击事件
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
     * 切换滤镜
     *
     * @param code
     */
    protected void changeVideoFilterCode(final String code) {
        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter) != null && mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter).size() > 0 && mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypeFilter).get(0).getFilterWrap().getCode().equals(code))
            return;
        isFilterReset = false;
        TuSdkMediaFilterEffectData filterEffectData = new TuSdkMediaFilterEffectData(code);
        SelesParameters.FilterArg filterArg = filterEffectData.getFilterArg("mixied");// 效果
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
        // 滤镜名显示
        showHitTitle(TuSdkContext.getString("lsq_filter_" + code));
    }


    /**
     * 显示提示文字
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
     * TouchView滑动监听
     */
    private TuSdkVideoFocusTouchViewBase.GestureListener gestureListener = new TuSdkVideoFocusTouchViewBase.GestureListener() {
        @Override
        public void onLeftGesture() {
            // 美颜开启禁止滑动切换
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
            // 美颜开启禁止滑动切换
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
                //boolean isDisplayTimeLayout有变速的时候就不显示默认时间区域
                setSelectTimeLayoutHideOrVisible(!isSpeedChecked);

                //恢复右侧特效icon和关闭按钮
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
                //恢复flip状态
                isDisplaySingleFlipStatus(false);
                if(mPropsItemViewPager.getAdapter() != null){
                    mPropsItemViewPager.getAdapter().notifyDataSetChanged();
                }
                mCamera.getFocusTouchView().isShowFoucusView(true);
            }
        }
    };

    /******************************** 滤镜End ********************************************/


    /********************** 动漫 ****************************/

    /**
     * 切换漫画滤镜
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
        // 滤镜名显示
        showHitTitle(TuSdkContext.getString("lsq_filter_" + code));
    }

    /******************************* 特效Start **************************/
    /**
     * 初始化贴纸
     */
    private void initStickerLayout() {
        mPropsItemViewPager = findViewById(R.id.lsq_viewPager);
        mPropsItemTabPagerIndicator = findViewById(R.id.lsq_TabIndicator);

        mPropsItemCancel = findViewById(R.id.lsq_cancel_button);
        mPropsItemCancel.setOnClickListener(onClickListener);

        // 贴纸视图
        mPropsItemLayout = findViewById(R.id.lsq_sticker_layout);
        setStickerVisible(false);
    }

    /**
     * 设置贴纸视图
     *
     * @param isVisible 是否可见
     */
    private void setStickerVisible(boolean isVisible) {
        mPropsItemLayout.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }

    /**
     * 显示特效视图
     */
    private void showStickerLayout() {
        setStickerVisible(true);
        // 滤镜栏向上动画并显示
        ViewCompat.setTranslationY(mPropsItemLayout,
                mPropsItemLayout.getHeight());
        ViewCompat.animate(mPropsItemLayout).translationY(0).setDuration(200).setListener(mViewPropertyAnimatorListener);
    }

    /**
     * 选择贴纸道具物品后回调
     */
    private StickerPropsItemPageFragment.StickerItemDelegate mStickerPropsItemDelegate = new StickerPropsItemPageFragment.StickerItemDelegate() {
        /**
         * 移除道具
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
         * 当前道具是否正在被使用
         *
         * @param propsItem 道具
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
     * 选择道具物品后回调
     */
    private PropsItemPageFragment.ItemDelegate mPropsItemDelegate = new PropsItemPageFragment.ItemDelegate() {

        @Override
        public void didSelectPropsItem(PropsItem propsItem, CustomStickerGroup customStickerGroup) {
            mCamera.addMediaEffectData(propsItem.effect());
            mPropsItemPagerAdapter.notifyAllPageData();
        }

        /**
         * 当前道具是否正在被使用
         *
         * @param propsItem 道具
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
     * 设置贴纸适配器
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
        // 添加贴纸道具分类数据
        mPropsItemCategories.addAll(PropsItemStickerCategory.allCategories(getContext(),gVisionDynamicStickerBean));
        // 添加哈哈镜道具分类
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

    /******************************* 特效End **************************/


    /*********************************** 美妆Start ********************* */

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


    /*********************************** 微整形 ********************/
    /**
     * 美颜微整形是否选中
     */
    private boolean isBeautyChecked = true;

    private boolean isCosmeticChecked = false;
    /**
     * 美颜适配器
     */
    private BeautyRecyclerAdapter mBeautyRecyclerAdapter;
    /**
     * 微整形适配器
     */
    private BeautyPlasticRecyclerAdapter mBeautyPlasticRecyclerAdapter;


    /**
     * 微整形调节栏
     */
    private ParamsConfigView mBeautyPlasticsConfigView;
    /**
     * 微整形默认值  Float 为进度值
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
     * 微整形参数
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
     * 美型调节栏
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
     * 美颜Item点击事件
     */
    BeautyRecyclerAdapter.OnBeautyItemClickListener beautyItemClickListener =
            new BeautyRecyclerAdapter.OnBeautyItemClickListener() {
                @Override
                public void onChangeSkin(View v, String key, TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
                    mBeautyPlasticsConfigView.setVisibility(VISIBLE);
                    switchConfigSkin(skinMode);

                    // 获取key值并显示到调节栏
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
     * 微整形Item点击事件
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
     * 隐藏美颜参数调节栏
     */
    private void hideBeautyBarLayout() {
        mBeautyPlasticsConfigView.setVisibility(GONE);

    }

    /**
     * 切换美颜、微整形Tab
     *
     * @param view
     */
    private void switchBeautyConfigTab(View view) {
        int id = view.getId();// 美颜
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
            // 微整形
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
            //美妆
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
     * 设置美颜、微整形视图状态
     *
     * @param isVisible true显示false隐藏
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
     * 设置美颜视图
     *
     * @param isVisible 是否可见
     */
    private void setBeautyLayout(boolean isVisible) {
        mSmartBeautyTabLayout.setVisibility(isVisible ? VISIBLE : GONE);
    }

    /**
     * 切换美颜预设按键
     *
     * @param skinMode true 自然(精准)美颜 false 极致美颜
     */
    private void switchConfigSkin(TuSdkMediaSkinFaceEffect.SkinFaceType skinMode) {
        TuSdkMediaSkinFaceEffect skinFaceEffect = new TuSdkMediaSkinFaceEffect(skinMode);


        // 美白
        SelesParameters.FilterArg whiteningArgs = skinFaceEffect.getFilterArg("whitening");//whiten whitening
        whiteningArgs.setMaxValueFactor(1.0f);//设置最大值限制
        whiteningArgs.setDefaultPercent(0.3f);
        whiteningArgs.setPrecentValue(0.3f);//设置默认显示
        // 磨皮
        SelesParameters.FilterArg smoothingArgs = skinFaceEffect.getFilterArg("smoothing");//smooth smoothing
        smoothingArgs.setMaxValueFactor(1.0f);//设置最大值限制
        smoothingArgs.setDefaultPercent(0.8f);
        smoothingArgs.setPrecentValue(0.8f);//设置默认显示
        // 红润
        SelesParameters.FilterArg ruddyArgs = skinFaceEffect.getFilterArg(skinMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? "ruddy" : "sharpen");//sharpen ruddy
        ruddyArgs.setMaxValueFactor(1.0f);//设置最大值限制
        ruddyArgs.setDefaultPercent(skinMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? 0.2f : 0.2f);
        ruddyArgs.setPrecentValue(skinMode != TuSdkMediaSkinFaceEffect.SkinFaceType.Beauty ? 0.2f : 0.2f);


        // 锐化
        SelesParameters.FilterArg sharpenArgs = skinFaceEffect.getFilterArg("sharpen");//sharpen ruddy
        sharpenArgs.setMaxValueFactor(1.0f);//设置最大值限制
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
                // 滤镜名显示
                showHitTitle(TuSdkContext.getString(getSkinModeTitle(skinMode)));
            }
        }

        // 添加一个默认微整形特效
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
     * 应用美肤
     *
     * @param key
     * @param progress
     */
    private void submitSkinParamter(String key, float progress) {
        List<TuSdkMediaEffectData> filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeSkinFace);

        if (filterEffects.size() == 0) return;

        // 只能添加一个滤镜特效
        TuSdkMediaSkinFaceEffect filterEffect = (TuSdkMediaSkinFaceEffect) filterEffects.get(0);
        filterEffect.submitParameter(key, progress);
    }

    /**
     * 切换微整形类型
     *
     * @param position
     */
    private void switchBeautyPlasticConfig(int position) {
        if (mCamera.mediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace).size() == 0) {
            // 添加一个默认微整形特效
//            TuSdkMediaPlasticFaceEffect plasticFaceEffect = new TuSdkMediaPlasticFaceEffect();
//            mCamera.addMediaEffectData(plasticFaceEffect);
//
//            TuSdkMediaReshapeEffect effect = new TuSdkMediaReshapeEffect();
//            mCamera.addMediaEffectData(effect);
//            for (SelesParameters.FilterArg arg : plasticFaceEffect.getFilterArgs()) {
//                if (arg.equalsKey("eyeSize")) {// 大眼
//                    arg.setMaxValueFactor(0.85f);// 最大值限制
//                }
//                if (arg.equalsKey("chinSize")) {// 瘦脸
//                    arg.setMaxValueFactor(0.9f);// 最大值限制
//                }
//                if (arg.equalsKey("noseSize")) {// 瘦鼻
//                    arg.setMaxValueFactor(0.6f);// 最大值限制
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
     * 应用整形值
     *
     * @param key
     * @param progress
     */
    private void submitPlasticFaceParamter(String key, float progress) {
        List<TuSdkMediaEffectData> filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypePlasticFace);

        if (filterEffects.size() == 0) return;

        // 只能添加一个滤镜特效
//        TuSdkMediaPlasticFaceEffect filterEffect = (TuSdkMediaPlasticFaceEffect) filterEffects.get(0);
//        filterEffect.submitParameter(key, progress);

        filterEffects = mCamera.mediaEffectsWithType(TuSdkMediaEffectData.TuSdkMediaEffectDataType.TuSdkMediaEffectDataTypeReshape);
        if (filterEffects.size() == 0) return;
//
//        TuSdkMediaReshapeEffect effect = (TuSdkMediaReshapeEffect) filterEffects.get(0);
//        effect.submitParameter(key, progress);

    }


    /******************************** 拍照 ************************/
    /**
     * 更新拍照预览界面
     *
     * @param isShow true显示false隐藏
     */
    private void updatePreviewImageLayoutStatus(boolean isShow) {
        findViewById(R.id.lsq_preview_image_layout).setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 显示拍照视图
     *
     * @param bitmap
     */
    private void presentPreviewLayout(Bitmap bitmap) {
        if (bitmap != null) {
            mCaptureBitmap = bitmap;
            updatePreviewImageLayoutStatus(true);
            mPreViewImageView.setImageBitmap(bitmap);
            // 暂停相机
            mCamera.pauseCameraCapture();
        }
    }

    /**
     * 保存拍照资源
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
     * 刷新相册
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
     * 删除拍照资源
     */
    public void deleteResource() {
        updatePreviewImageLayoutStatus(false);
        destroyBitmap();
        mCamera.resumeCameraCapture();
    }

    /**
     * 销毁拍照图片
     */
    private void destroyBitmap() {
        if (mCaptureBitmap == null) return;

        if (!mCaptureBitmap.isRecycled())
            mCaptureBitmap.recycle();

        mCaptureBitmap = null;
    }

    /********************************** 点击事件 ************************/
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            // 关闭按钮
            if (id == R.id.lsq_closeButton) {

                hanldeCloseButton();

            }

            // 添加声音
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
            // 切换摄像头
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
            /// 拍照速度按钮
            else if (id == R.id.lsq_speedButton) {
                setFilterContentVisible(false);
                setBottomViewVisible(true);
                setStickerVisible(false);
                setBeautyViewVisible(false);
                setSpeedViewVisible(mSpeedModeBar.getVisibility() == GONE);

            }//速度界面其他空白区域
            else if (id == R.id.lsq_movie_speed_bar_rl) {
                setSpeedViewVisible(false);
            }
            // 美颜按钮显示美颜布局
            else if (id == R.id.lsq_beautyButton) {
                // todo 一键美颜
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
                    // 移除美颜
                    mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypeSkinFace);
                    // 移除微整形
                    mCamera.removeMediaEffectsWithType(TuSdkMediaEffectDataTypePlasticFace);
                    mBeautyButton.setSelected(false);
                    isBeautyClose = true;
                    //  ToastUtils.showToast(getContext(),getContext().getString(R.string.beauty_mode_off) );
                    ToastUtils.showCustomToast((Activity) getContext(), getContext().getString(R.string.beauty_mode_off));
                }
                //
                mCamera.getFocusTouchView().isShowFoucusView(false);

            }
            // 滤镜按钮
            else if (id == R.id.lsq_filtersButton) {
                setBeautyViewVisible(false);
                setBottomViewVisible(false);
                setSpeedViewVisible(false);
                setStickerVisible(false);
                showFilterLayout();
                //点了滤镜不显示
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
            // 闪光灯按钮
            else if (id == R.id.lsq_flash_off) {
                if (isOpenFlash) {
                    updateFlashMode(CameraConfigs.CameraFlash.Off);

                    isOpenFlash = false;
                } else {
                    updateFlashMode(CameraConfigs.CameraFlash.Torch);
                    isOpenFlash = true;
                }

            }
            // 自动对焦开启
            else if (id == R.id.lsq_focus_open) {
                mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));
                mFocusClose.setTextColor(getResources().getColor(R.color.lsq_color_white));

                mCamera.setDisableContinueFocus(false);
            }
            // 自动对焦关闭
            else if (id == R.id.lsq_focus_close) {
                mFocusOpen.setTextColor(getResources().getColor(R.color.lsq_color_white));
                mFocusClose.setTextColor(getResources().getColor(R.color.lsq_widget_speedbar_button_bg));

                mCamera.setDisableContinueFocus(true);

            }
            // 闪光灯开启
            else if (id == R.id.lsq_lighting_open) {
                updateFlashMode(CameraConfigs.CameraFlash.Torch);

            }
            // 闪光灯关闭
            else if (id == R.id.lsq_lighting_close) {
                updateFlashMode(CameraConfigs.CameraFlash.Off);
            }
            // 美颜弹窗中的美服
            else if (id == R.id.lsq_beauty_tab) {
                isCosmeticChecked = false;
                switchBeautyConfigTab(v);

            }
            // 美颜弹窗中的微整形
            else if (id == R.id.lsq_beauty_plastic_tab) {
                isCosmeticChecked = false;
                switchBeautyConfigTab(v);
            }
            // 美颜弹窗中的美妆
            else if (id == R.id.lsq_cosmetic_tab) {
                isCosmeticChecked = true;
                switchBeautyConfigTab(v);
                // 滤镜
            } else if (id == R.id.lsq_tab_upload) {
                AlbumUtils.openMediaAlbum1(MovieEditorActivity.class.getName(), Constants.MAX_EDITOR_SELECT_MUN);

            }
            // 贴纸特效
            else if (id == R.id.lsq_stickerWrap) {
                setFilterContentVisible(false);
                setBeautyViewVisible(false);
                setSpeedViewVisible(false);
                setBottomViewVisible(false);
                showStickerLayout();
                //只显示flip
                isDisplaySingleFlipStatus(true);
                mCamera.getFocusTouchView().isShowFoucusView(false);
                // 比例
            } else if (id == R.id.lsq_radio_1_1) {
                updateCameraRatio(RatioType.ratio_1_1);
            } else if (id == R.id.lsq_radio_3_4) {
                updateCameraRatio(RatioType.ratio_3_4);
            } else if (id == R.id.lsq_radio_full) {
                updateCameraRatio(RatioType.ratio_orgin);
                // 视频回退
            } else if (id == R.id.lsq_backWrap) {// 点击后退按钮删除上一条视频
                //点击之后弹框判断
                DialogHelper.closeTipDialog(mContext, getResources().getString(R.string.dialog_title_discard_last_clip), new DialogHelper.onDiscardClickListener() {
                    @Override
                    public void onDiscardClick() {
                        pauseMusic(false);

                        //分段删除
                        if (mCamera.getRecordingFragmentSize() > 0) {
                            if(stickerIds.size()>0){
                                stickerIds.remove(stickerIds.size()-1);
                            }
                            mCamera.popVideoFragment();
                            mRecordProgress.removePreSegment();

                            if (interuptLayout.getChildCount() != 0) {
                                if (recordState == 1) {
                                    //pause 和 complete都增加了断点标记
                                    interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
                                    recordState = -1;
                                }
                                interuptLayout.removeViewAt(interuptLayout.getChildCount() - 1);
                            }
                            // 删除最后一段，重置录制状态
                            if (mCamera.getRecordingFragmentSize() == 0) {

                                mCamera.cancelRecording();
                                setMinTimeButtonStatus(VISIBLE);
                            }
                        }
                        // 速度按钮和时长按钮重叠的bug是改变速度后 按退回键没有重置isSpeedChecked这个boolean,所以setViewHideOrVisible里面的判断无效，现阶段时间紧为了避免影响其他地方，单独设置isSpeedChecked
                        isSpeedChecked = false;
                        // 刷新按钮状态
                        setViewHideOrVisible(true);
                        //setSpeedViewVisible(false);再调一次setSpeedViewVisible(false);会让switchTimeLayout改变，最小化改变UI只改isSpeedChecked
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
                // 启动录制隐藏比例调节按钮
                mCamera.stopRecording();
                initRecordProgress();
                setViewHideOrVisible(true);
                // 拍摄时直接保存
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
                    // 启动录制隐藏比例调节按钮
                    mCamera.stopRecording();
                    mRecordPlayingButton.setVisibility(INVISIBLE);
                    mRecordPlayingButton.pauseAnimation();
                    mRecordButton.setVisibility(VISIBLE);
                    updateRecordButtonResource(RecordType.SHORT_CLICK_RECORD);
                    initRecordProgress();
                }
            }
            // 取消拍摄
            else if (id == R.id.lsq_backButton) {
                deleteResource();
                // 保存拍摄
            } else if (id == R.id.lsq_saveImageButton) {
                saveResource();
                // 取消贴纸
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
            // 选择60s录制
            else if (id == R.id.btn_60s) {
                switchTimeLayout(0);
                mCamera.setMaxRecordingTime(60);
                LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
                minTimeLayoutParams.leftMargin = ((interuptLayout != null ? interuptLayout.getWidth() : TuSdkContext.getScreenSize().width) * Constants.MIN_RECORDING_TIME / Constants.MAX_RECORDING_TIME_60) - minTimeButton.getWidth() + TuSdkContext.dip2px(16);
                updateRecordTime(_60sButton, 60);
            }
            // 选择30s录制
            else if (id == R.id.btn_30s) {
                switchTimeLayout(1);
                mCamera.setMaxRecordingTime(30);
                LayoutParams minTimeLayoutParams = (LayoutParams) minTimeButton.getLayoutParams();
                minTimeLayoutParams.leftMargin = ((interuptLayout != null ? interuptLayout.getWidth() : TuSdkContext.getScreenSize().width) * Constants.MIN_RECORDING_TIME / Constants.MAX_RECORDING_TIME_30) - minTimeButton.getWidth() + TuSdkContext.dip2px(16);
                updateRecordTime(_30sButton, 30);
            }
            // 选择15s录制
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
            //显示特效名
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
                    //TODO  需要删除素材?
                    if (getDelegate() != null) getDelegate().finishRecordActivity();
                }

                @Override
                public void onStartOverClick() {
                    //一次性删除所有片段
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

                        // 删除最后一段，重置录制状态
                        if (mCamera.getRecordingFragmentSize() == 0) {
                            mCamera.cancelRecording();
                        }
                        // 速度按钮和时长按钮重叠的bug是改变速度后 按退回键没有重置isSpeedChecked这个boolean,所以setViewHideOrVisible里面的判断无效，现阶段时间紧为了避免影响其他地方，单独设置isSpeedChecked
                        isSpeedChecked = false;
                        // 刷新按钮状态
                        setViewHideOrVisible(true);
                        updateViewOnMovieRecordProgressChanged(0, 0);
                        selectMusic(new SelectSoundEvent (null,0));
                    }
                }

                @Override
                public void onSaveAsDraftClick() {
                    //TODO 保存在哪里云盘？相册？
                    AppConstants.isSaveDraft = true;
                    //  AlbumUtils.openMediaAlbum(EDITOR_CLASS, Constants.MAX_EDITOR_SELECT_MUN);
                    if ((musicLocalPath != null && musicLocalPath != "")) {
                        pauseMusic(true);
                        AppConstants.musicLocalPath = "";
                        AppConstants.shootBackgroundMusicBean = null;
                    }
                    // 启动录制隐藏比例调节按钮
                    mCamera.stopRecording();
                    initRecordProgress();
                    setViewHideOrVisible(true);
                }
            });
        } else {
            if (getDelegate() != null) getDelegate().finishRecordActivity();
        }
    }

    /******************************新添加逻辑*********************************/

    /**
     * 把当前的背景音乐设置为静态变量
     */
    void setStaticBackgroundMusicBean() {
        AppConstants.shootBackgroundMusicBean = currentBackMusicBean;
        AppConstants.musicLocalPath = musicLocalPath;
    }


    /******************************显示隐藏*********************************/


    /**
     * 改变录制时间
     */
    public void updateRecordTime(Button button, int maxRecordTimes) {
        _60sButton.setSelected(false);
        _30sButton.setSelected(false);
        _15sButton.setSelected(false);
        maxRecordTime = maxRecordTimes;
        button.setSelected(true);
    }


    /**
     * 改变闪关灯状态
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
     * 更新相机比例
     *
     * @param type
     */
    private void updateCameraRatio(int type) {
        // 只要开始录制就不可切换
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
     * 改变屏幕比例 录制状态不可改变
     *
     * @param type 参数类型 RatioType
     */
    private void switchCameraRatio(int type) {
        if (mCamera == null || !mCamera.canChangeRatio()) return;

        // 设置预览区域顶部偏移量 必须在 changeRegionRatio 之前设置
        mCamera.getRegionHandler().setOffsetTopPercent(getPreviewOffsetTopPercent(type));
        mCamera.changeRegionRatio(RatioType.ratio(type));
        mCamera.setRegionRatio(RatioType.ratio(type));

        // 计算保存比例
        mCamera.getVideoEncoderSetting().videoSize = TuSdkSize.create((int) (mCamera.getCameraPreviewSize().width * RatioType.ratio(type)), mCamera.getCameraPreviewSize().width);

    }

    /**
     * 获取当前 Ratio 预览画面顶部偏移百分比（默认：-1 居中显示 取值范围：0-1）
     *
     * @param ratioType
     * @return
     */
    protected float getPreviewOffsetTopPercent(int ratioType) {
        if (ratioType == RatioType.ratio_1_1) return 0.1f;
        // 置顶
        return 0.f;
    }

    /************************ 录制模式切换 **************************/
    /**
     * 模式按键切换动画
     */
    private ValueAnimator valueAnimator;
    /**
     * 录制按键模式
     */
    private int mRecordMode = RecordType.LONG_CLICK_RECORD;

    private float mPosX, mCurPosX;
    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离

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
                    // 滑动效果处理
                    if (mCurPosX - mPosX > 0
                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
                        //向左滑动
                        if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.CAPTURE);
                        } else if (mRecordMode == RecordType.SHORT_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                        }
                        return false;
                    } else if (mCurPosX - mPosX < 0
                            && (Math.abs(mCurPosX - mPosX) > FLING_MIN_DISTANCE)) {
                        //向右滑动
                        if (mRecordMode == RecordType.CAPTURE) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                        } else if (mRecordMode == RecordType.LONG_CLICK_RECORD) {
                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
                        }
                        return false;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    // 点击效果处理
                    if (Math.abs(mCurPosX - mPosX) < FLING_MIN_DISTANCE || mCurPosX == 0) {
                        int id = v.getId();// 拍照模式
                        if (id == R.id.lsq_shootButton) {
                            switchCameraModeButton(RecordType.CAPTURE);
                            // 长按录制模式
                        } else if (id == R.id.lsq_longButton) {
                            switchCameraModeButton(RecordType.LONG_CLICK_RECORD);
                            // 点击录制模式
                        } else if (id == R.id.lsq_clickButton) {
                            switchCameraModeButton(RecordType.SHORT_CLICK_RECORD);
                        }
                        return false;
                    }
            }
            return false;
        }
    };
    //控件总长186dp, margin5 button52 起始margin24
    int lastMargin = (TuSdkContext.getScreenSize().width / 2 - TuSdkContext.dip2px(186 - 5 - 52 / 2 + 24));

    /**
     * 切换选择时间布局
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
     * 切换摄像模式按键
     *
     * @param index
     */
    private void switchCameraModeButton(int index) {
        if (valueAnimator != null && valueAnimator.isRunning() || mRecordMode == index) return;

        // 设置文字颜色
        mShootButton.setTextColor(index == 0 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mLongButton.setTextColor(index == 1 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));
        mClickButton.setTextColor(index == 2 ? getResources().getColor(R.color.lsq_color_white) : getResources().getColor(R.color.lsq_alpha_white_66));

        // 设置偏移位置
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

        // 切换动画
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

        // 录制按键背景
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
     * 获取底部拍摄模式按键宽度
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
     * 切换速率
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

        // 切换相机速率
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
     * 设置显示隐藏控件（速度按键）
     *
     * @param isVisible 是否可见 true显示false隐藏
     *                  回退按钮没有把这个值置成false,需要主动调方法
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

    /****************************** 视图控制 ****************************/

    /**
     * 设置按键图片
     *
     * @param textButton 按键
     * @param id         图片id
     */
    private void setTextButtonDrawableTop(TuSdkTextButton textButton, @DrawableRes int id) {
        Drawable top = getResources().getDrawable(id);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        textButton.setCompoundDrawables(null, top, null, null);
    }

    /**
     * 点击录制时视图变化
     */
    private void setRecordingViewVisible(boolean recording) {
        mRecordTimeRe.setVisibility(recording ? VISIBLE : GONE);
        mRightButtonLl.setVisibility(recording ? GONE : VISIBLE);
        if (mSelectTimeLayout.getVisibility() != (recording ? GONE : VISIBLE))
            mSelectTimeLayout.setVisibility(recording ? GONE : VISIBLE);

    }

    /**
     * 修icon bug 点击录制时视图变化
     */
    private void setRecordingViewVisible(boolean recording, int type) {
        mRecordTimeRe.setVisibility(recording ? VISIBLE : GONE);
        mRightButtonLl.setVisibility(recording ? GONE : VISIBLE);
        //if(mSelectTimeLayout.getVisibility() != (recording ? GONE : VISIBLE))
        mSelectTimeLayout.setVisibility(GONE);
    }

    /**
     * 底部控件是否可见 滤镜、美颜、贴纸切换时
     * ƒ
     *
     * @param isVisible 是否可见
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
     * 设置选择时间布局的隐藏和显示
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
     * 设置显示隐藏控件（录制、非录制状态下）
     *
     * @param isVisible 是否可见
     */
    private void setViewHideOrVisible(boolean isVisible) {

        int visibleState = isVisible ? VISIBLE : GONE;
        if (isCheckTransparentButton) {
            visibleState = GONE;
        }
        ;
        // 顶部区域(关闭按钮和添加音乐区域)
        mTopBar.setVisibility(visibleState);
        // 添加音乐按钮的状态
        addSoundImageState(isVisible);
        // 右侧功能按钮区域
        mRightButtonLl.setVisibility(visibleState);
        // 顶部录制数据
        mRecordTimeRe.setVisibility(isVisible ? GONE : VISIBLE);
        // 底部录制速度控制ui
        mSpeedModeBar.setVisibility(isVisible && isSpeedChecked ? visibleState : GONE);
        mSpeedModeBarBg.setVisibility(isVisible && isSpeedChecked ? visibleState : GONE);
        // 底部功能按钮区域(特效,视频上传,保存,撤回)
        mBottomBarLayout.setVisibility(visibleState);
        // mRecordModeBarLayout.setVisibility(visibleState);
        // 提交按钮
        mConfirmButton.setVisibility(GONE);
        // 返回上一级按钮

        setRollBackButton(GONE);


        //  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // TODO onPause的状态 会把mConfirmButton.setVisibility(visibleState); mRollBackButton.setVisibility(visibleState);都变成true,从而显示错误
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
     * 添加音乐按钮的状态改变
     *
     * @param isVisible
     */
    private void addSoundImageState(boolean isVisible) {
        if (isVisible) {
            // 如果有录制片段
            if (mCamera.getMovieDuration() > 0) {
                // 并且添加了音乐,图标选中,不能点击
                if (musicLocalPath != null && !musicLocalPath.equals("")) {
                    addSoundLl.setSelected(false);
                    addSoundImg.setSelected(false);
                } else {
                    //如果有录制片段在的时候,添加音乐按钮是不能点击的,图标未选中
                    addSoundLl.setSelected(false);
                    addSoundImg.setSelected(true);
                }
            }
            // 如果没有录制片段
            else {
                // 添加了音乐 图标选中,可点击
                if (musicLocalPath != null && !musicLocalPath.equals("")) {
                    addSoundLl.setSelected(true);
                    addSoundImg.setSelected(false);
                } else {
                    // 可点击,图标未选中,默认初始化状态
                    addSoundLl.setSelected(true);
                    addSoundImg.setSelected(true);
                }
            }

        }
    }


    /**
     * 改变录制按钮视图
     *
     * @param type
     */
    private void updateRecordButtonResource(int type) {
        switch (type) {
            // 拍照
            case RecordType.CAPTURE:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_shoot);
                mRecordButton.setImageResource(0);
                break;
            // 长按录制
            case RecordType.LONG_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_unpressed);
                mRecordButton.setImageResource(0);
                break;
            // 点击录制
            case RecordType.SHORT_CLICK_RECORD:
                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_click_record_unpressed);
                //   mRecordButton.setImageResource(R.drawable.video_ic_recording);
                mRecordPlayingButton.setVisibility(INVISIBLE);
                mRecordPlayingButton.pauseAnimation();
                mRecordButton.setVisibility(VISIBLE);

                break;
            // 长按录制中
            case RecordType.LONG_CLICK_RECORDING:
                mRecordPlayingButton.setVisibility(VISIBLE);
                mRecordPlayingButton.playAnimation();
                mRecordButton.setVisibility(INVISIBLE);

                mRecordButton.setBackgroundResource(R.drawable.tusdk_view_widget_record_pressed);
                mRecordButton.setImageResource(0);
                break;
            // 点击录制中
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
     * 设置滤镜视图
     */
    private void setFilterContentVisible(boolean isVisible) {
        mFilterContent.setVisibility(isVisible ? VISIBLE : INVISIBLE);
    }


    /********************************** 回调事件 ***********************/

    /**
     * 录制状态改变回调
     *
     * @param state     录制状态
     * @param recording 是否正在录制中
     */
    public void updateMovieRecordState(TuSdkRecorderVideoCamera.RecordState state, boolean recording) {
        if (state == TuSdkRecorderVideoCamera.RecordState.Recording) // 开始录制
        {
            if (mRecordMode == RecordType.LONG_CLICK_RECORD)
                updateRecordButtonResource(RecordType.LONG_CLICK_RECORDING);
            else
                updateRecordButtonResource(RecordType.SHORT_CLICK_RECORDING);
            setViewHideOrVisible(false);
            mMoreConfigLayout.setVisibility(GONE);

        } else if (state == TuSdkRecorderVideoCamera.RecordState.Paused) // 已暂停录制
        {
            if (mRecordProgress.getProgress() != 0) {
                addInteruptPoint((TuSdkContext.getDisplaySize().width - TuSdkContext.dip2px(36)) * mRecordProgress.getProgress());
            }
            mRecordProgress.pauseRecord();
            //自动录像达到最大时间进入pause，隐藏掉mTranslucentConfirmWrapButton
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
        } else if (state == TuSdkRecorderVideoCamera.RecordState.RecordCompleted) //录制完成弹出提示（续拍模式下录过程中超过最大时间时调用）
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
                //时间达到拍摄满的时长直接跳转到编辑页
                mCamera.stopRecording();
                initRecordProgress();
                setViewHideOrVisible(true);
            }
        } else if (state == TuSdkRecorderVideoCamera.RecordState.Saving) // 正在保存视频
        {
            String msg = getStringFromResource(R.string.new_movie_saving);
            TuSdk.messageHub().setStatus(mContext, msg);
        } else if (state == TuSdkRecorderVideoCamera.RecordState.SaveCompleted) {

            String msg = getStringFromResource(R.string.lsq_video_save_ok);
            TuSdk.messageHub().showToast(mContext, msg);

            updateRecordButtonResource(mRecordMode);
            //录制中点选勾会走到saving，不改变icon
            //setViewHideOrVisible(true);
        }
        backFormEdit();
    }

    /**
     * 页面从编辑返回的逻辑
     */
    void backFormEdit() {
        if (isBackFromEdit) {
            //一次性删除所有片段
            if (mCamera != null && mCamera.getRecordingFragmentSize() > 0) {
                if (mRecordProgress != null) {
                    int index = mCamera.getRecordingFragmentSize() - 1;
                    while (index >= 0) {
                        mCamera.popVideoFragment();
                        mRecordProgress.removePreSegment();
                        index--;
                    }
                }
                // 删除最后一段，重置录制状态
                if (mCamera.getRecordingFragmentSize() == 0) {
                    mCamera.cancelRecording();
                }

            }
            updateViewOnMovieRecordProgressChanged(0, 0);
            // 速度按钮和时长按钮重叠的bug是改变速度后 按退回键没有重置isSpeedChecked这个boolean,所以setViewHideOrVisible里面的判断无效，现阶段时间紧为了避免影响其他地方，单独设置isSpeedChecked
            isSpeedChecked = false;
            // 刷新按钮状态
            setViewHideOrVisible(true);
        }
        isBackFromEdit = false;
    }

    /**
     * 添加视频断点标记
     *
     * @param margingLeft
     */
    private void addInteruptPoint(float margingLeft) {
        // 添加断点标记 (需求暂时去掉）
        Button interuptBtn = new Button(mContext);
        LayoutParams lp = new LayoutParams(TuSdkContext.dip2px(2),
                LayoutParams.MATCH_PARENT);

        interuptBtn.setBackgroundColor(TuSdkContext.getColor("lsq_progress_interupt_color"));
        lp.setMargins((int) Math.ceil(margingLeft), 0, 0, 0);
        interuptBtn.setLayoutParams(lp);
        interuptLayout.addView(interuptBtn);
    }

    /**
     * 录制进度回调
     *
     * @param progress
     * @param durationTime
     */
    //TODO mTranslucentConfirmWrapButton 显示问题是否合乎逻辑
    public void updateViewOnMovieRecordProgressChanged(float progress, float durationTime) {
        if (durationTime > Constants.MIN_RECORDING_TIME) {
            mTranslucentConfirmWrapButton.setSelected(true);
            //大于1s消失小于1s显示
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
     * 录制错误时更新视图显示
     *
     * @param error
     * @param isRecording
     */
    public void updateViewOnMovieRecordFailed(TuSdkRecorderVideoCamera.RecordError error, boolean isRecording) {
        if (error == TuSdkRecorderVideoCamera.RecordError.MoreMaxDuration) // 超过最大时间 （超过最大时间是再次调用startRecording时会调用）
        {
            String msg = getStringFromResource(R.string.max_recordTime) + Constants.MAX_RECORDING_TIME + "s";
            TuSdk.messageHub().showToast(mContext, msg);

        } else if (error == TuSdkRecorderVideoCamera.RecordError.SaveFailed) // 视频保存失败
        {
            String msg = getStringFromResource(R.string.new_movie_error_saving);
            TuSdk.messageHub().showError(mContext, msg);
        } else if (error == TuSdkRecorderVideoCamera.RecordError.InvalidRecordingTime) {
            TuSdk.messageHub().showError(mContext, R.string.lsq_record_time_invalid);
        }
        setViewHideOrVisible(true);
    }

    /**
     * 录制完成时更新视图显示
     *
     * @param isRecording
     */
    public void updateViewOnMovieRecordComplete(boolean isRecording) {
        TuSdk.messageHub().dismissRightNow();
        String msg = getStringFromResource(R.string.new_movie_saved);
        TuSdk.messageHub().showSuccess(mContext, msg);

        // 录制完进度清零(正常录制模式)
        mRecordProgress.clearProgressList();
        setViewHideOrVisible(true);
    }

    /**
     * 获取字符串资源
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
     * 得到最近视频的第一帧作为缩略图
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
                    //音乐播放
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