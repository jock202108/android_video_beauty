package org.lasque.twsdkvideo.video_beauty.editor;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.lasque.tusdk.core.TuSdk;
import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.media.codec.extend.TuSdkMediaTimeSlice;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.struct.TuSdkMediaDataSource;
import org.lasque.tusdk.core.struct.TuSdkSize;
import org.lasque.tusdk.core.utils.ColorUtils;
import org.lasque.tusdk.core.utils.ThreadHelper;
import org.lasque.tusdk.core.utils.image.BitmapHelper;
import org.lasque.tusdk.impl.components.widget.sticker.StickerDynamicItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerImageItemView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerTextItemView;
import org.lasque.tusdk.modules.view.widget.sticker.StickerData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerDynamicData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerImageData;
import org.lasque.tusdk.modules.view.widget.sticker.StickerItemViewInterface;
import org.lasque.tusdk.modules.view.widget.sticker.StickerText;
import org.lasque.tusdk.modules.view.widget.sticker.StickerTextData;
import org.lasque.tusdk.video.editor.TuSdkTimeRange;
import org.lasque.twsdkvideo.video_beauty.ScreenAdapterActivity;
import org.lasque.twsdkvideo.video_beauty.VideoBeautyPlugin;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorHomeComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorMusicComponent;
import org.lasque.twsdkvideo.video_beauty.editor.component.EditorVoiceoverComponent;
import org.lasque.twsdkvideo.video_beauty.event.BackEvent;
import org.lasque.twsdkvideo.video_beauty.event.SelectSoundEvent;
import org.lasque.twsdkvideo.video_beauty.event.StickerEvent;
import org.lasque.twsdkvideo.video_beauty.utils.AppColorUtils;
import org.lasque.twsdkvideo.video_beauty.utils.BitmapUtils;
import org.lasque.twsdkvideo.video_beauty.utils.DialogHelper;
import org.lasque.twsdkvideo.video_beauty.utils.SoftInputUtil;
import org.lasque.twsdkvideo.video_beauty.utils.SoftKeyboardStateWatcher;
import org.lasque.twsdkvideo.video_beauty.utils.Utils;
import org.lasque.twsdkvideo.video_beauty.views.TuSdkTextView;
import org.lasque.tusdk.impl.components.widget.sticker.StickerView;
import org.lasque.twsdkvideo.video_beauty.R;
import org.lasque.twsdkvideo.video_beauty.album.AlbumUtils;
import org.lasque.twsdkvideo.video_beauty.constant.AppConstants;
import org.lasque.twsdkvideo.video_beauty.data.AddTextRecordBean;
import org.lasque.twsdkvideo.video_beauty.data.TextStyleBean;
import org.lasque.twsdkvideo.video_beauty.data.TextStyleDetailBean;
import org.lasque.twsdkvideo.video_beauty.views.VideoContent;
import org.lasque.twsdkvideo.video_beauty.views.adapters.OnItemClickListener;
import org.lasque.twsdkvideo.video_beauty.views.adapters.OnStyleDetailItemClickListener;
import org.lasque.twsdkvideo.video_beauty.views.adapters.TextStyleAdapter;
import org.lasque.twsdkvideo.video_beauty.views.adapters.TextStyleDetailAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor.TuSdkMovieEditorOptions.TuSdkMediaPictureEffectReferTimelineType.TuSdkMediaEffectReferInputTimelineType;

/**
 * 编辑页面
 *
 * @since v3.0.0
 */
public  class MovieEditorActivity extends ScreenAdapterActivity {




    public   static class MyHandler extends Handler {
        WeakReference<Activity> mActivityReference;

        public MyHandler(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityReference.get();
            if (activity != null) {
           //     mImageView.setImageBitmap(mBitmap);
            }
        }
    }






    /**
     * 顶部ViewContent
     **/
    private FrameLayout mHeaderView;
    /**
     * 底部ViewContent
     **/
    private FrameLayout mBottomView;

    private ViewGroup mMusiclayout;


    private ViewGroup lsqTitle;
    private ArrayList<String> stickerIds ;


    /**
     * 播放器Content
     **/
    private VideoContent mVideoContent;
    /**
     * 编辑控制器
     **/
    private MovieEditorController mEditorController;
    /**
     * 文字贴纸操作视图
     **/
    private StickerView mTextStickerView;
    /**
     * 魔法效果操作视图
     **/
    private RelativeLayout mMagicContent;
    /**
     * 视频路径
     **/
    public String mVideoPath;
    /**
     * 是否直接编辑
     **/
//    boolean isDirectEdit = false;

    /**
     * 是否是上传视频(从相册上传视频到编辑页的)
     */
   public boolean isTrim = false;

    public boolean isAlbum;//是从相册跳转过来的



    /**
     * 是否从草稿箱进入
     */
   public boolean isFromDraft = false;
    /**
     * 需要直接编辑的时间区间
     **/
    ArrayList<TuSdkMediaTimeSlice> mTimeSlice;

    private int editorDisplayWidth, editorDisplayHeight;
    private int videoWidth;
    private int videoHeight;
    private float mCurrentSpeed = 1f;
    private float mCurrentLeftPercent = 0f;
    private float mCurrentRightPercent = 1.0f;
    private long mTotalDurationUs = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_movie_editor_full_screen);
        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        mEditorController.getMovieEditor().getEditorPlayer().startPreview();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mVideoPath = getIntent().getStringExtra("videoPath");
        if (getIntent().hasExtra("isFromDraft")) {
            isFromDraft = getIntent().getBooleanExtra("isFromDraft", true);
        }

        if (getIntent().hasExtra("stickerIds")) {
            stickerIds = getIntent().getStringArrayListExtra("stickerIds");
        }


        if (getIntent().hasExtra("isTrim")) {
            isTrim = getIntent().getBooleanExtra("isTrim", false);
        }
//        if (getIntent().hasExtra("isDirectEdit")) {
//            isDirectEdit = getIntent().getBooleanExtra("isDirectEdit", false);
//            if (isDirectEdit) {
//                mTimeSlice = (ArrayList<TuSdkMediaTimeSlice>) getIntent().getSerializableExtra("timeRange");
//            }
//        }
        mHeaderView = findViewById(R.id.lsq_editor_header);
        mMusiclayout = findViewById(R.id.lsq_music_layout);
        mBottomView = findViewById(R.id.lsq_editor_bottom);
        lsqTitle = findViewById(R.id.lsq_title);
        RelativeLayout.LayoutParams lsqTitleLayoutParams = (RelativeLayout.LayoutParams) lsqTitle.getLayoutParams();
        lsqTitleLayoutParams.setMargins(0, VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(12), 0, 0);
        mVideoContent = findViewById(R.id.lsq_editor_content);
        mTextStickerView = findViewById(R.id.lsq_stickerView);
        mTextStickerView.setVisibility(View.VISIBLE);
        mTextStickerView.changeOrUpdateStickerType(StickerView.StickerType.Normal);
        mTextStickerView.setDelegate(mStickerDelegate);
        mTextStickerView.setDefaultStrokeColor(Color.TRANSPARENT);
        mMagicContent = findViewById(R.id.lsq_magic_content);
        videoWidth = getIntent().getIntExtra("videoWidth", 0);
        videoHeight = getIntent().getIntExtra("videoHeight", 0);
        mCurrentSpeed = getIntent().getFloatExtra("currentSpeed", 1);
        mCurrentLeftPercent = getIntent().getFloatExtra("currentLeftPercent", 0);
        mCurrentRightPercent = getIntent().getFloatExtra("currentRightPercent", 1);
        mTotalDurationUs = getIntent().getLongExtra("totalDurationUs", 0);
        isAlbum =  getIntent().getStringExtra("router")!=null;
      //  double ratio = (double) videoWidth / videoHeight;

      //  editorDisplayWidth = (int) VideoBeautyPlugin.screenWidth;
      //  editorDisplayHeight = (int) ((float)VideoBeautyPlugin.screenWidth / ratio);

        editorDisplayWidth = TuSdkContext.getScreenSize().width;
        editorDisplayHeight = TuSdkContext.getScreenSize().height;


     //   mVideoContent.setWidth((int) VideoBeautyPlugin.screenWidth);
        // 如果
//        if (isAlbum || isTrim) {//58是底部菜单lsq_editor_component_home_bottom的高度
//           // mVideoContent.setHeight((int) VideoBeautyPlugin.screenHeight);
//            //播放键
////            FrameLayout.LayoutParams layoutParams1  = (FrameLayout.LayoutParams) playbtn.getLayoutParams();
//////            if(videoWidth>videoHeight){
//////
//////            }
////            layoutParams1.height = (int) VideoBeautyPlugin.screenHeight-TuSdkContext.dip2px(48);
////            playbtn.setLayoutParams(layoutParams1);
//
//        } else {
            mVideoContent.setHeight(editorDisplayHeight);
//        }


        if (!new File(mVideoPath).exists()) {
            TuSdk.messageHub().showToast(this, R.string.lsq_not_file);
            return;
        }
        initAddTextView();
        //初始化视频编辑器
        initEditorController();
        mEditorController.getMovieEditor().getEditorPlayer().addProgressListener(mPlayProgressListener);


//        if(videoWidth > videoHeight){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mEditorController.getMovieEditor().getEditorPlayer().setOutputRatio(9f / 16f,false);
//                }
//            },700);
//        }

    }


    public void initEditorController() {
        TuSdkMovieEditor.TuSdkMovieEditorOptions defaultOptions = TuSdkMovieEditor.TuSdkMovieEditorOptions.defaultOptions();
        defaultOptions.setVideoDataSource(new TuSdkMediaDataSource(mVideoPath))
                .setIncludeAudioInVideo(true) // 设置是否保存原音
                .setClearAudioDecodeCacheInfoOnDestory(false)// 设置MovieEditor销毁时是否自动清除缓存音频解码信息
                .setPictureEffectReferTimelineType(TuSdkMediaEffectReferInputTimelineType);//设置时间线模式
        //  defaultOptions.saveToAlbum = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q?false:true;//保存为私有目录
        defaultOptions.saveToAlbum = false;

        // todo 不起作用
       // defaultOptions.setCutTimeRange(TuSdkTimeRange.makeTimeUsRange((long) mCurrentLeftPercent * mTotalDurationUs, (long) (mCurrentRightPercent * mTotalDurationUs)));
        // 正常输出视频的宽高
        defaultOptions.outputSize = TuSdkSize.create(editorDisplayWidth, editorDisplayHeight);
//        if (isDirectEdit) {
//            //时间轴编辑
//            mEditorController = new MovieEditorController(videoWidth, videoHeight, mCurrentSpeed, mCurrentLeftPercent, mCurrentRightPercent, isAlbum, this, mVideoContent, mTimeSlice, defaultOptions);
//        } else {
//         }
        //编辑
        mEditorController = new MovieEditorController(videoWidth, videoHeight, mCurrentSpeed, mCurrentLeftPercent, mCurrentRightPercent, isAlbum, this, mVideoContent, defaultOptions);

        mEditorController.getMovieEditor().getEditorPlayer().addPreviewSizeChangeListener(mOnDisplayChangeListener);
    }

    /**
     * 获取HeadView
     *
     * @return
     */
    public ViewGroup getHeaderView() {
        return mHeaderView;
    }


    public ViewGroup getMusicLayout() {
        return mMusiclayout;
    }

    /**
     * 获取BottomView
     *
     * @return
     */
    public ViewGroup getBottomView() {
        return mBottomView;
    }

    public ViewGroup getTitleView() {
        return lsqTitle;
    }


    /**
     * 获取文字控件
     *
     * @return
     */
    public StickerView getTextStickerView() {
        return mTextStickerView;
    }

    /**
     * 图片贴纸控件
     **/
    public StickerView getImageStickerView() {
        return mTextStickerView;
    }

    /**
     * 获取魔法效果
     *
     * @return
     */
    public RelativeLayout getMagicContent() {
        return mMagicContent;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mEditorController != null)
            mEditorController.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();



        //清空重置相关数据
        getImageStickerView().removeAllSticker();
        //清空录音
        EditorVoiceoverComponent.mMementoVoiceList.clear();
        //清空背景音乐
        EditorMusicComponent.backgroundMusicSelected = null;


        Utils.imageStickerIds.clear();
        EventBus.getDefault().unregister(this);

        if (mEditorController != null)
            mEditorController.onDestroy();

        if (mEditorController != null) {
            for (Bitmap bitmap : mEditorController.getThumbBitmapList())
                BitmapHelper.recycled(bitmap);

            mEditorController.getThumbBitmapList().clear();
        }
    }

    /****************添加文字ui************************/

    /**
     * ---------关于添加文字视图------------
     **/
    public RelativeLayout mRlAddText;
    public LinearLayout mAddTextBottomView;
    public TextView mDoneText;
    public RecyclerView mRvTextStyle;
    public RecyclerView mRvTextStyleDetail;
    public TuSdkTextView mTvEffectsText;
    public TextStyleAdapter mTextStyleAdapter;
    public TextStyleDetailAdapter mTextStyleDetailAdapter;
    public RelativeLayout mRlProgress;
    public TextView mTvProgressName;
    public SeekBar mSeekBar;
    public LinearLayout mDeleteLayout;
    public ImageView mIcDelete;
    public RelativeLayout mRlPop;
    public boolean isEditSticker = false;
    public RelativeLayout mRlRest;


    // 主样式的数据源
    public ArrayList<TextStyleBean> mTextStyleBeans = new ArrayList<TextStyleBean>();

    // 子style的数据源
    public ArrayList<TextStyleDetailBean> mFontBeans = new ArrayList<TextStyleDetailBean>();
    public ArrayList<TextStyleDetailBean> mStyleBeans = new ArrayList<TextStyleDetailBean>();
    public ArrayList<TextStyleDetailBean> mColorBeans = new ArrayList<TextStyleDetailBean>();
    public ArrayList<TextStyleDetailBean> mStrokeBeans = new ArrayList<TextStyleDetailBean>();
    public ArrayList<TextStyleDetailBean> mBackgroundBeans = new ArrayList<TextStyleDetailBean>();
    public ArrayList<TextStyleDetailBean> mAlignmentBeans = new ArrayList<TextStyleDetailBean>();
    public ArrayList<TextStyleDetailBean> mDirectionBeans = new ArrayList<TextStyleDetailBean>();

    // 贴纸记录集合
    public ArrayList<AddTextRecordBean> mAddTextRecordBeans = new ArrayList<AddTextRecordBean>();

    // 当前贴纸记录类
    AddTextRecordBean addTextRecordBean = new AddTextRecordBean();

    // 第一次滑动的PointF;
    PointF firstPointF;

    // 当前编辑的StickerTextItemView
    StickerTextItemView mCurrentEditStickerTextItemView;

    // 是否显示软键盘
    boolean isSoftKeyboard = false;

    private void initAddTextView() {
        mAddTextBottomView = findViewById(R.id.mBottomView);
        mRlAddText = findViewById(R.id.include_add_text);
        mRvTextStyleDetail = findViewById(R.id.rv_style_detail);
        mDoneText = findViewById(R.id.done_addtext);
        RelativeLayout.LayoutParams mDoneTextLayoutParams = (RelativeLayout.LayoutParams) mDoneText.getLayoutParams();
        mDoneTextLayoutParams.setMargins(0, VideoBeautyPlugin.statusBarHeight + TuSdkContext.dip2px(12), TuSdkContext.dip2px(18), 0);
        mRvTextStyle = findViewById(R.id.rv_add_text);
        mTvEffectsText = findViewById(R.id.mTvEffectsText);
        mTvEffectsText.setSelected(true);
        mRlProgress = findViewById(R.id.rl_progress);
        mTvProgressName = findViewById(R.id.progress_name);
        mSeekBar = findViewById(R.id.seekbar);
        mDeleteLayout = findViewById(R.id.include_delete_layout);
        mIcDelete = findViewById(R.id.ic_delete);
        mRlPop = findViewById(R.id.mRlPop);
        mRlRest = findViewById(R.id.rl_rest);
        initKeyBordWatcher();
        initBeans();
        initTextStyleRv();
        initTextStyleDetailRv();
        initListener();
    }

    /**
     * 初始化数据源
     */
    private void initBeans() {
        // getResources().getString(R.string.style),
        AppConstants.textStringNames = Arrays.asList(getResources().getString(R.string.font), getResources().getString(R.string.color), getResources().getString(R.string.stroke), getResources().getString(R.string.background), getResources().getString(R.string.alignment), getResources().getString(R.string.direction));
        mTextStyleBeans.clear();
        mFontBeans.clear();
        mStyleBeans.clear();
        mColorBeans.clear();
        mStrokeBeans.clear();
        mBackgroundBeans.clear();
        mAlignmentBeans.clear();
        mDirectionBeans.clear();
        for (int i = 0; i < AppConstants.textStringNames.size(); i++) {
            mTextStyleBeans.add(new TextStyleBean(AppConstants.textStringNames.get(i), i == 0));

        }
        for (int i = 0; i < AppConstants.unSelectFontIcons.size(); i++) {
            TextStyleDetailBean bean = new TextStyleDetailBean();
            bean.setSelect(i == 0 ? true : false);
            bean.setParentStyleKind(AppConstants.textStringNames.get(0));
            bean.setUnSelectImageId(AppConstants.unSelectFontIcons.get(i));
            bean.setSelectImageId(AppConstants.selectFontIcons.get(i));
            bean.setFontName(AppConstants.fontName.get(i));
            bean.setChildStyleName(i + "");
            mFontBeans.add(bean);
        }

//        for (int i = 0; i < AppConstants.unSelectStyleIcons.size(); i++) {
//            TextStyleDetailBean bean = new TextStyleDetailBean();
//            bean.setSelect(false);
//            bean.setParentStyleKind(AppConstants.textStringNames.get(1));
//            bean.setUnSelectImageId(AppConstants.unSelectStyleIcons.get(i));
//            bean.setSelectImageId(AppConstants.selectStyleIcons.get(i));
//            bean.setChildStyleName(i + "");
//            mStyleBeans.add(bean);
//        }

        for (int i = 0; i < AppConstants.colors.size(); i++) {
            TextStyleDetailBean bean = new TextStyleDetailBean();
            bean.setSelect(false);
            bean.setParentStyleKind(AppConstants.textStringNames.get(1));
            bean.setChildStyleName(i + "");
            bean.setColor(getResources().getColor(AppConstants.colors.get(i)));
            mColorBeans.add(bean);
        }

        for (int i = 0; i < AppConstants.colors.size(); i++) {
            TextStyleDetailBean bean = new TextStyleDetailBean();
            bean.setSelect(false);
            bean.setParentStyleKind(AppConstants.textStringNames.get(2));
            bean.setChildStyleName(i + "");
            bean.setColor(getResources().getColor(AppConstants.colors.get(i)));
            mStrokeBeans.add(bean);
        }

        for (int i = 0; i < AppConstants.colors.size(); i++) {
            TextStyleDetailBean bean = new TextStyleDetailBean();
            bean.setSelect(false);
            bean.setParentStyleKind(AppConstants.textStringNames.get(3));
            bean.setChildStyleName(i + "");
            bean.setColor(getResources().getColor(AppConstants.colors.get(i)));
            mBackgroundBeans.add(bean);
        }

        for (int i = 0; i < AppConstants.unSelectAlignmentIcons.size(); i++) {
            TextStyleDetailBean bean = new TextStyleDetailBean();
            bean.setSelect(false);
            bean.setParentStyleKind(AppConstants.textStringNames.get(4));
            bean.setUnSelectImageId(AppConstants.unSelectAlignmentIcons.get(i));
            bean.setSelectImageId(AppConstants.selectAlignmentIcons.get(i));
            bean.setChildStyleName(i + "");
            mAlignmentBeans.add(bean);
        }
        for (int i = 0; i < AppConstants.unSelectDirectionIcons.size(); i++) {
            TextStyleDetailBean bean = new TextStyleDetailBean();
            bean.setSelect(false);
            bean.setParentStyleKind(AppConstants.textStringNames.get(5));
            bean.setUnSelectImageId(AppConstants.unSelectDirectionIcons.get(i));
            bean.setSelectImageId(AppConstants.selectDirectionIcons.get(i));
            bean.setChildStyleName(i + "");
            mDirectionBeans.add(bean);
        }
    }

    /**
     * 初始化监听
     */
    private void initListener() {


        //view加载完成时回调
        mTvEffectsText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTvEffectsText.requestFocus();
            }
        });


        mRlAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoftKeyboard) {
                    SoftInputUtil.hideSoftInput(MovieEditorActivity.this);
                } else {
                    closeOverlay();
                }

            }
        });

        mDoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoftKeyboard) {
                    SoftInputUtil.hideSoftInput(MovieEditorActivity.this);
                } else {
                    closeOverlay();
                }

            }
        });

        mTextStyleAdapter.setListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mTextStyleAdapter.setCurrentPosition(position);
                textStyleDetail(mTextStyleBeans.get(position).getStyleName());
                setSeekProgress(position);
            }

            @Override
            public void onSelectedSound(int position) {

            }
        });

        mTextStyleDetailAdapter.setListener(new OnStyleDetailItemClickListener() {
            @Override
            public void onItemClick(List<TextStyleDetailBean> data, int position, TextStyleDetailBean bean) {
                setTextAttribute(data, bean, position);
            }

        });

        /**
         * 进度条监听
         */
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (addTextRecordBean.getStyleName().equals(getResources().getString(R.string.stroke))) {
                    addTextRecordBean.setmCurrentStrokeProgress(progress);
                    setProgressTextStroke(progress);
                } else if (addTextRecordBean.getStyleName().equals(getResources().getString(R.string.background))) {
                    addTextRecordBean.setmCurrentBackgroundProgress(progress);
                    setProgressTextBackGround(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mRlRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restColor();
            }
        });

    }

    /**
     * 初始化软键盘监听
     */
    private void initKeyBordWatcher() {
        SoftKeyboardStateWatcher helper = new SoftKeyboardStateWatcher(getWindow().getDecorView(), this);
        helper.addSoftKeyboardStateListener(new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                isSoftKeyboard = true;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAddTextBottomView.getLayoutParams();
                layoutParams.bottomMargin = keyboardHeightInPx;
                mAddTextBottomView.setLayoutParams(layoutParams);
            }

            @Override
            public void onSoftKeyboardClosed() {
                closeOverlay();
            }
        });
    }

    /**
     * 主样式RecyclerView
     */
    private void initTextStyleRv() {
        mRvTextStyle.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mTextStyleAdapter = new TextStyleAdapter(this);
        mRvTextStyle.setAdapter(mTextStyleAdapter);
        mTextStyleAdapter.setDate(mTextStyleBeans);
    }

    /**
     * 子样式RecyclerView
     */
    private void initTextStyleDetailRv() {
        mRvTextStyleDetail.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        mTextStyleDetailAdapter = new TextStyleDetailAdapter(this);
        mRvTextStyleDetail.setAdapter(mTextStyleDetailAdapter);
        mTextStyleDetailAdapter.setDate(mFontBeans);
    }

    /**
     * 设置进度条
     */
    public void setSeekProgress(int position) {
        if (mTextStyleBeans.get(position).getStyleName().equals(getResources().getString(R.string.stroke))) {
            mRlProgress.setVisibility(View.VISIBLE);
            mTvProgressName.setText(getResources().getString(R.string.width));

        } else if (mTextStyleBeans.get(position).getStyleName().equals(getResources().getString(R.string.background))) {
            mRlProgress.setVisibility(View.VISIBLE);
            mTvProgressName.setText(getResources().getString(R.string.opacity));
        } else {
            mRlProgress.setVisibility(GONE);
        }
    }


    /************* 业务层逻辑 *************************/


    /**
     * 打开蒙层
     */
    public void openOverlay(boolean isEdit, long stickerId) {
        isEditSticker = isEdit;
        if (mEditorController.getCurrentComponent() instanceof EditorHomeComponent) {
            mEditorController.getCurrentComponent().getHeaderView().setVisibility(View.INVISIBLE);
        }
        // 编辑
        if (isEdit) {
            restTuSdkTextView();
            editSticker(stickerId);
        }
        // 重新添加
        else {
            restTuSdkTextView();
            mCurrentEditStickerTextItemView = null;
        }
    }

    /**
     * 第一次进入或者重新添加字体
     */
    private void restTuSdkTextView() {
        addTextRecordBean = new AddTextRecordBean();
        mRlAddText.setVisibility(View.VISIBLE);
        initBeans();
        mTextStyleAdapter.setDate(mTextStyleBeans);
        mTextStyleDetailAdapter.setDate(mFontBeans);
        setSeekProgress(0);
        mTvEffectsText.setUnderlineText(false);
        mTvEffectsText.setText("");
        mTvEffectsText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTvEffectsText.setTextColor(Color.WHITE);
        mTvEffectsText.setTextStrokeColor(Color.TRANSPARENT);
        mTvEffectsText.setTextStrokeWidth(0);
        mTvEffectsText.setBackgroundColor(Color.TRANSPARENT);
        mTvEffectsText.setGravity(Gravity.CENTER);
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                SoftInputUtil.showSoftInputNoFocus(MovieEditorActivity.this);
            }
        }, 200);
    }


    /**
     * 点击编辑贴纸文字进入逻辑
     */
    private void editSticker(long stickerId) {
        for (int i = 0; i < mAddTextRecordBeans.size(); i++) {
            AddTextRecordBean bean = mAddTextRecordBeans.get(i);
            if (bean.getId() == stickerId) {
                editStickerData(bean);
                break;
            }
        }
    }

    /**
     * 编辑贴纸数据源
     */
    private void editStickerData(AddTextRecordBean bean) {
        addTextRecordBean = bean;
        mRlAddText.setVisibility(View.VISIBLE);
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                SoftInputUtil.showSoftInput(MovieEditorActivity.this);
            }
        }, 500);

        String content = bean.getContent();
        int fontPosition = bean.getFontPosition();
        boolean isItalic = bean.isItalic();
        boolean isUnderline = bean.isUnderline();
        boolean isBold = bean.isBold();
        int textColor = bean.getTextColor();
        int strokeColor = bean.getStrokeColor();
        int strokeWidth = bean.getStrokeWidth();
        int backGroundColor = bean.getBackGroundColor();
        float alphaProgressFloat = bean.getBackGroundColorAlphaProgress() / 100f;
        int alignmentPosition = bean.getAlignmentPosition();
        int directionPosition = bean.getDirectionPosition();
      String fontName =  AppConstants.fontName.get(fontPosition);
        if (fontName.isEmpty()) {
            mTvEffectsText.setTypeface(Typeface.DEFAULT);
        } else {
            Typeface type = Typeface.createFromAsset(getAssets(), fontName);
            mTvEffectsText.setTypeface(type);
        }
     //   mTvEffectsText.setUnderlineText(isUnderline);
        mTvEffectsText.setText(content);
//        if (isItalic && isBold) {
//            mTvEffectsText.setTypeface(mTvEffectsText.getTypeface(), Typeface.BOLD_ITALIC);
//        } else if (isBold) {
//            mTvEffectsText.setTypeface(mTvEffectsText.getTypeface(), Typeface.BOLD);
//        } else if (isItalic) {
//            mTvEffectsText.setTypeface(mTvEffectsText.getTypeface(), Typeface.ITALIC);
//        } else {
//            mTvEffectsText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//        }
        if (textColor != -1) {
            mTvEffectsText.setTextColor(textColor);
        }
        if (strokeColor != -1) {
            mTvEffectsText.setTextStrokeColor(strokeColor);
        }
        if (strokeWidth != -1) {
            mTvEffectsText.setTextStrokeWidth(strokeWidth);
        }
        if (backGroundColor != -1234567) {
            int retColor = ColorUtils.alphaEvaluator(alphaProgressFloat, backGroundColor);
            mTvEffectsText.setBackgroundColor(retColor);
        }
        if (alignmentPosition != -1) {
            if (alignmentPosition == 0) {
                mTvEffectsText.setGravity(Gravity.START);
            } else if (alignmentPosition == 1) {
                mTvEffectsText.setGravity(Gravity.CENTER);
            } else if (alignmentPosition == 2) {
                mTvEffectsText.setGravity(Gravity.END);
            }
        }

        moveCursorToEnd();

        /*------设置数据源----------*/
//        mStyleBeans.get(0).setSelect(isItalic);
//        mStyleBeans.get(1).setSelect(isUnderline);
//        mStyleBeans.get(2).setSelect(isBold);

        for (int i = 0; i < mFontBeans.size(); i++) {
            TextStyleDetailBean fontBean = mFontBeans.get(i);
            fontBean.setSelect(false);
           if(i== fontPosition){
               fontBean.setSelect(true);
           }
        }

        for (int i = 0; i < mColorBeans.size(); i++) {
            TextStyleDetailBean colorBean = mColorBeans.get(i);
            colorBean.setSelect(false);
            if (colorBean.getColor() == textColor) {
                colorBean.setSelect(true);
            }
        }

        for (int i = 0; i < mStrokeBeans.size(); i++) {
            TextStyleDetailBean strokeBean = mStrokeBeans.get(i);
            strokeBean.setSelect(false);
            if (strokeBean.getColor() == strokeColor) {
                strokeBean.setSelect(true);
            }
        }

        for (int i = 0; i < mBackgroundBeans.size(); i++) {
            TextStyleDetailBean backgroundBean = mBackgroundBeans.get(i);
            backgroundBean.setSelect(false);
            if (backgroundBean.getColor() == strokeColor) {
                backgroundBean.setSelect(true);
            }
        }

        for (int i = 0; i < mAlignmentBeans.size(); i++) {
            mAlignmentBeans.get(i).setSelect(alignmentPosition == i);
        }

        for (int i = 0; i < mDirectionBeans.size(); i++) {
            mDirectionBeans.get(i).setSelect(directionPosition == i);
        }
        mTextStyleAdapter.setDate(mTextStyleBeans);
        mTextStyleDetailAdapter.setDate(mFontBeans);
        setSeekProgress(0);
    }


    /**
     * 关闭蒙层
     */
    private void closeOverlay() {
        mRlAddText.setVisibility(GONE);
        isEditSticker = false;
        isSoftKeyboard = false;
        if (mEditorController.getCurrentComponent() instanceof EditorHomeComponent) {
            mEditorController.getCurrentComponent().getHeaderView().setVisibility(View.VISIBLE);
        }
        //  mTextStickerView.setViewSize(mEditorController.getVideoContentView(), TuSdkContext.getScreenSize().width, TuSdkContext.getScreenSize().height);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mAddTextBottomView.getLayoutParams();
        layoutParams.bottomMargin = 0;
        mAddTextBottomView.setLayoutParams(layoutParams);
        /// 编辑文本
        if (mCurrentEditStickerTextItemView != null) {
            if (!mTvEffectsText.getText().toString().trim().isEmpty()) {
                addTextRecordBean.setContent(mTvEffectsText.getText().toString().trim());
                addTextRecordBean.setTypeface(mTvEffectsText.getTypeface());
                for (int i = 0; i < mAddTextRecordBeans.size(); i++) {
                    if (mAddTextRecordBeans.get(i).getId() == addTextRecordBean.getId()) {
                        mAddTextRecordBeans.set(i, addTextRecordBean);
                    }
                }
                upDateStickerTextData();
            }

        }
        /// 创建文本
        else {
            if (!mTvEffectsText.getText().toString().trim().isEmpty()) {
                addTextRecordBean.setContent(mTvEffectsText.getText().toString().trim());
                addTextRecordBean.setTypeface(mTvEffectsText.getTypeface());
                generateStickerTextData();
                mAddTextRecordBeans.add(addTextRecordBean);
            }
        }


        mCurrentEditStickerTextItemView = null;
    }

    /**
     * 创建贴纸数据类
     */
    private void generateStickerTextData() {
        StickerText stickerText = new StickerText();
        stickerText.content = addTextRecordBean.getContent();
        stickerText.textSize = 30f;
        if (addTextRecordBean.getTextColor() == -1) {
            stickerText.color = "#ffffff";
        } else {
            stickerText.color = AppColorUtils.getHexString(addTextRecordBean.getTextColor());
        }

        // 设置文字区域位置相对上边距百分比信息
        stickerText.rectTop = 0f;
        stickerText.rectLeft = 0f;
        stickerText.rectWidth = 1f;
        stickerText.rectHeight = 1f;
        stickerText.paddings = 0;
        ArrayList<StickerText> mStickerTexts = new ArrayList<>();
        mStickerTexts.add(stickerText);

        StickerTextData mStickerData = new StickerTextData();
        mStickerData.starTimeUs = 0L;
        mStickerData.stopTimeUs = mEditorController.getMovieEditor().getEditorPlayer().getInputTotalTimeUs();
        mStickerData.texts = mStickerTexts;
        mStickerData.stickerType = 2;
        mStickerData.stickerId = mAddTextRecordBeans.size();

        addTextRecordBean.setId(mAddTextRecordBeans.size());
        //  mEditorController.getMovieEditor().getEditorPlayer().pausePreview();
        mTextStickerView.appendSticker(mStickerData);
    }


    /**
     * 更新贴纸数据类
     */
    private void upDateStickerTextData() {
        setStickerData(mCurrentEditStickerTextItemView);
        mCurrentEditStickerTextItemView.setVisibility(View.VISIBLE);
    }


    /**
     * 点击主菜单显示
     *
     * @param parentStyle
     */
    private void textStyleDetail(String parentStyle) {
        addTextRecordBean.setStyleName(parentStyle);
        mRlRest.setVisibility(GONE);
        if (parentStyle.equals(getResources().getString(R.string.font))) {
            mTextStyleDetailAdapter.setDate(mFontBeans);
        } else if (parentStyle.equals(getResources().getString(R.string.style))) {
            mTextStyleDetailAdapter.setDate(mStyleBeans);
        } else if (parentStyle.equals(getResources().getString(R.string.color))) {
            mTextStyleDetailAdapter.setDate(mColorBeans);
            mRlRest.setVisibility(View.VISIBLE);
        } else if (parentStyle.equals(getResources().getString(R.string.stroke))) {
            mTextStyleDetailAdapter.setDate(mStrokeBeans);
            mRlRest.setVisibility(View.VISIBLE);
            mSeekBar.setProgress(addTextRecordBean.mCurrentStrokeProgress);
        } else if (parentStyle.equals(getResources().getString(R.string.background))) {
            mTextStyleDetailAdapter.setDate(mBackgroundBeans);
            mRlRest.setVisibility(View.VISIBLE);
            mSeekBar.setProgress(addTextRecordBean.mCurrentBackgroundProgress);
        } else if (parentStyle.equals(getResources().getString(R.string.alignment))) {
            mTextStyleDetailAdapter.setDate(mAlignmentBeans);
        } else if (parentStyle.equals(getResources().getString(R.string.direction))) {
            mTextStyleDetailAdapter.setDate(mDirectionBeans);
        }
    }


    /**
     * 点击子菜单
     */
    private void setTextAttribute(List<TextStyleDetailBean> data, TextStyleDetailBean textStyleDetailBean, int position) {
        String parentStyle = textStyleDetailBean.getParentStyleKind();
        if (parentStyle.equals(getResources().getString(R.string.font))) {
            setTextFont(position, textStyleDetailBean.getFontName());
        } else if (parentStyle.equals(getResources().getString(R.string.style))) {
            setTextStyle(data, position);
        } else if (parentStyle.equals(getResources().getString(R.string.color))) {
            setTextColor(textStyleDetailBean, position);
        } else if (parentStyle.equals(getResources().getString(R.string.stroke))) {
            setClickTextStroke(textStyleDetailBean, position);
        } else if (parentStyle.equals(getResources().getString(R.string.background))) {
            setClickTextBackGround(textStyleDetailBean, position);

        } else if (parentStyle.equals(getResources().getString(R.string.alignment))) {
            setTextAlignment(position);

        } else if (parentStyle.equals(getResources().getString(R.string.direction))) {
            setTextDirection(position);
        }
    }


    /**
     * 重置颜色
     */

    private void restColor() {
        String styleName = addTextRecordBean.getStyleName();
        if (styleName.equals(getResources().getString(R.string.color))) {
            restTextColor();

        } else if (styleName.equals(getResources().getString(R.string.background))) {
            restBackGround();

        } else if (styleName.equals(getResources().getString(R.string.stroke))) {
            restStroke();
        }
    }


    /**
     * 设置文字Font属性
     */
    private void setTextFont(int position, String fontName) {
        addTextRecordBean.setFontPosition(position);
        mTextStyleDetailAdapter.setCurrentPosition(position);
        if (fontName.isEmpty()) {
            mTvEffectsText.setTypeface(Typeface.DEFAULT);
        } else {
            Typeface type = Typeface.createFromAsset(getAssets(), fontName);
            mTvEffectsText.setTypeface(type);
        }

    }


    /**
     * 设置Style属性
     */
    private void setTextStyle(List<TextStyleDetailBean> data, int position) {
        mTextStyleDetailAdapter.setCurrentClickItem(position);
        addTextRecordBean.setItalic(data.get(0).isSelect());
        addTextRecordBean.setUnderline(data.get(1).isSelect());
        addTextRecordBean.setBold(data.get(2).isSelect());
        boolean isItalic = addTextRecordBean.isItalic();
        boolean isUnderline = addTextRecordBean.isUnderline();
        boolean isBold = addTextRecordBean.isBold();
        if (isItalic && isBold) {
            mTvEffectsText.setTypeface(mTvEffectsText.getTypeface(), Typeface.BOLD_ITALIC);
        } else if (isBold) {
            mTvEffectsText.setTypeface(mTvEffectsText.getTypeface(), Typeface.BOLD);
        } else if (isItalic) {
            mTvEffectsText.setTypeface(mTvEffectsText.getTypeface(), Typeface.ITALIC);
        } else {
            mTvEffectsText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        mTvEffectsText.setUnderlineText(isUnderline);
        mTvEffectsText.setText(mTvEffectsText.getText());
        moveCursorToEnd();
    }

    /**
     * 光标在文本最后
     */
    private void moveCursorToEnd() {
        mTvEffectsText.setSelection(mTvEffectsText.getText().length());
    }

    /**
     * 设置字体颜色
     */
    private void setTextColor(TextStyleDetailBean textStyleDetailBean, int position) {
        mTextStyleDetailAdapter.setCurrentPosition(position);
        addTextRecordBean.setTextColor(textStyleDetailBean.getColor());
        mTvEffectsText.setTextColor(textStyleDetailBean.getColor());
    }

    /**
     * 重置字体颜色
     */
    private void restTextColor() {
        mTextStyleDetailAdapter.setCurrentPosition(0);
        addTextRecordBean.setTextColor(Color.WHITE);
        mTvEffectsText.setTextColor(Color.WHITE);
    }


    /**----------设置外边框颜色开始-------------**/
    /**
     * 设置字体外边框
     */
    private void setClickTextStroke(TextStyleDetailBean textStyleDetailBean, int position) {
        mTextStyleDetailAdapter.setCurrentPosition(position);
        int strokeWidth = (int) (mSeekBar.getProgress() * addTextRecordBean.getmMaxStrokeWidth() / 100);
        setTextStrokeColorAndWidth(textStyleDetailBean.getColor(), strokeWidth);
    }

    /**
     * 设置进度条滚动字体外边框
     */
    private void setProgressTextStroke(int progress) {
        if (addTextRecordBean.getStrokeColor() != 0) {
            int strokeWidth = (int) (progress * addTextRecordBean.getmMaxStrokeWidth() / 100);
            setTextStrokeColorAndWidth(addTextRecordBean.getStrokeColor(), strokeWidth);
            ;
        }
    }

    /**
     * 最终设置的stoke颜色和宽度
     */
    private void setTextStrokeColorAndWidth(int color, int width) {
        addTextRecordBean.setStrokeWidth(width);
        addTextRecordBean.setStrokeColor(color);
        mTvEffectsText.setTextStrokeColor(color);
        mTvEffectsText.setTextStrokeWidth(width);
    }

    /**
     * 重置背景色
     */
    private void restStroke() {
        addTextRecordBean.setStrokeWidth(0);
        addTextRecordBean.setStrokeColor(Color.TRANSPARENT);
        mTextStyleDetailAdapter.setCurrentPosition(-1);
        mTvEffectsText.setTextStrokeColor(0);
        mTvEffectsText.setTextStrokeWidth(Color.TRANSPARENT);
    }
    /**----------设置外边框颜色结束-------------**/


    /**
     * -------------设置背景颜色开始------------
     **/

    private void setClickTextBackGround(TextStyleDetailBean textStyleDetailBean, int position) {
        mTextStyleDetailAdapter.setCurrentPosition(position);
        int color = textStyleDetailBean.getColor();
        setBackGroundAndAlpha(color, addTextRecordBean.mCurrentBackgroundProgress);
    }

    private void setProgressTextBackGround(int progress) {
        if (addTextRecordBean.getBackGroundColor() != 0) {
            setBackGroundAndAlpha(addTextRecordBean.getBackGroundColor(), progress);
        }
    }

    private void setBackGroundAndAlpha(int color, int progress) {

        if (color != -1234567) {
            float progressFloat = progress / 100f;
            int retColor = ColorUtils.alphaEvaluator(progressFloat, color);
            mTvEffectsText.setBackgroundColor(retColor);
        } else {
            mTvEffectsText.setBackgroundColor(Color.TRANSPARENT);
        }


        addTextRecordBean.setBackGroundColor(color);
        addTextRecordBean.setBackGroundColorAlphaProgress(progress);
    }

    /**
     * 重置背景色
     */
    private void restBackGround() {
        addTextRecordBean.setBackGroundColor(Color.TRANSPARENT);
        mTextStyleDetailAdapter.setCurrentPosition(-1);
        mTvEffectsText.setBackgroundColor(Color.TRANSPARENT);
    }
    /**-------------设置背景颜色结束------------**/


    /**
     * 设置对齐方式
     */
    private void setTextAlignment(int position) {
        addTextRecordBean.setAlignmentPosition(position);
        mTextStyleDetailAdapter.setCurrentPosition(position);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTvEffectsText.getLayoutParams();
        switch (position) {
            case 0:
                setAlignmentStart(layoutParams);
                break;
            case 1:
                setAlignmentCenter(layoutParams);
                break;
            case 2:
                setAlignmentEnd(layoutParams);
                break;
        }

    }

    private void setAlignmentStart(RelativeLayout.LayoutParams layoutParams) {
        layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        mTvEffectsText.setGravity(Gravity.START);
    }

    private void setAlignmentCenter(RelativeLayout.LayoutParams layoutParams) {
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mTvEffectsText.setGravity(Gravity.CENTER);
    }


    private void setAlignmentEnd(RelativeLayout.LayoutParams layoutParams) {
        layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        mTvEffectsText.setGravity(Gravity.END);
    }


    /**
     * 设置文本方向
     */
    private void setTextDirection(int position) {
        mTextStyleDetailAdapter.setCurrentPosition(position);
        addTextRecordBean.setDirectionPosition(position);
        String content = mTvEffectsText.getText().toString().trim();
        boolean isReverse = addTextRecordBean.isReverse();
        switch (position) {
            case 0:
                if (isReverse) {
                    mTvEffectsText.setText(reverseString(content));
                } else {
                    mTvEffectsText.setText(content);
                }
                mTvEffectsText.setSelection(content.length());
                addTextRecordBean.setReverse(false);

                break;
            case 1:
                if (isReverse) {
                    mTvEffectsText.setText(content);
                } else {
                    mTvEffectsText.setText(reverseString(content));
                }
                mTvEffectsText.setSelection(content.length());
                addTextRecordBean.setReverse(true);
                break;

        }

    }

    /**
     * 将字符串反转并返回
     */
    private String reverseString(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(text);
        stringBuilder.reverse();

        return stringBuilder.toString();
    }

    /**
     * 设置贴纸属性
     */
    private void setStickerData(StickerTextItemView stickerTextItemView) {
        org.lasque.tusdk.core.view.TuSdkTextView textView = stickerTextItemView.getTextView();
        String content = addTextRecordBean.getContent();
        boolean isItalic = addTextRecordBean.isItalic();
        boolean isUnderline = addTextRecordBean.isUnderline();
        boolean isBold = addTextRecordBean.isBold();
        int textColor = addTextRecordBean.getTextColor();
        int strokeColor = addTextRecordBean.getStrokeColor();
        int strokeWidth = addTextRecordBean.getStrokeWidth();
        int backGroundColor = addTextRecordBean.getBackGroundColor();
        float alphaProgressFloat = addTextRecordBean.getBackGroundColorAlphaProgress() / 100f;
        int alignmentPosition = addTextRecordBean.getAlignmentPosition();
        int directionPosition = addTextRecordBean.getDirectionPosition();
        stickerTextItemView.setUnderline(isUnderline);
        if (content != null || !content.equals("")) {
            textView.setText(content);
        }
//        if (isItalic && isBold) {
//            textView.setTypeface(textView.getTypeface(),Typeface.BOLD_ITALIC);
//        } else if (isBold) {
//            textView.setTypeface(textView.getTypeface(),Typeface.BOLD);
//        } else if (isItalic) {
//            textView.setTypeface(textView.getTypeface(),Typeface.ITALIC);
//        } else {
//            textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//        }
        if (addTextRecordBean.getTypeface() != null) {
            textView.setTypeface(addTextRecordBean.getTypeface());
        } else {
            textView.setTypeface(Typeface.DEFAULT);
        }


        if (textColor != -1) {
            textView.setTextColor(textColor);
        }
        if (strokeColor != -1) {
            textView.setTextStrokeColor(strokeColor);
        }
        if (strokeWidth != -1) {
            textView.setTextStrokeWidth(strokeWidth);
        }
        if (backGroundColor != -1234567) {
            if (backGroundColor == 0) {
                textView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                int retColor = ColorUtils.alphaEvaluator(alphaProgressFloat, backGroundColor);
                textView.setBackgroundColor(retColor);
            }

        }
        if (alignmentPosition != -1) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            if (alignmentPosition == 0) {
                setAlignmentStart(layoutParams);
                textView.setGravity(Gravity.START);
            } else if (alignmentPosition == 1) {
                setAlignmentCenter(layoutParams);
                textView.setGravity(Gravity.CENTER);
            } else if (alignmentPosition == 2) {
                setAlignmentEnd(layoutParams);
                textView.setGravity(Gravity.END);
            }
        }
    }


    /**
     * 选择文字贴纸
     */

    private void selectTextSticker(StickerTextItemView itemView, long stickerId) {
        Rect rect = new Rect();
        itemView.getGlobalVisibleRect(rect);
       float rotation =  itemView.getRotation();
       float px =  rect.left+(rect.right-rect.left)/2.0f;
       float py = rect.top+(rect.bottom-rect.top)/2.0f;
       RectF rectF =  new RectF(rect.left,rect.top,rect.right,rect.bottom);
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation,px,py);
        matrix.mapRect(rectF);
        int x = (int) (rectF.left+(rectF.right-rectF.left)/2);
        int y = (int)(rectF.top+(rectF.bottom-rectF.top)/2);
        showTextPopupWindow(itemView, stickerId, x, y);
        itemView.setStroke(Color.WHITE, 3);
        itemView.setSelected(true);

//        int[] location = new int[2];
//        itemView.getLocationOnScreen(location);
//        itemView.setStroke(Color.WHITE, 3);
//        itemView.setSelected(true);
//        showTextPopupWindow(itemView, stickerId, location[0], location[1]);
    }

    /**
     * 选择图片贴纸
     */
    private void selectImageSticker(StickerImageItemView itemView, long stickerId) {
//        Rect rect = new Rect();
//        itemView.getGlobalVisibleRect(rect);
//        int x = rect.left;
//        int y = rect.top;
        int[] location = new int[2];
        itemView.getLocationOnScreen(location);
        itemView.setStroke(Color.WHITE, 3);
        itemView.setSelected(true);
        showImagePopupWindow(itemView, stickerId,  location[0], location[1]);
    }

    /**
     * 选择图片贴纸
     */
    private void selectGifImageSticker(StickerDynamicItemView itemView, long stickerId) {
         Rect rect = new Rect();
        itemView.getGlobalVisibleRect(rect);
       float rotation =  itemView.getRotation();
       float px =  rect.left+(rect.right-rect.left)/2.0f;
       float py = rect.top+(rect.bottom-rect.top)/2.0f;
       RectF rectF =  new RectF(rect.left,rect.top,rect.right,rect.bottom);
        Matrix matrix = new Matrix();
        matrix.setRotate(rotation,px,py);
        matrix.mapRect(rectF);
        int x = (int) (rectF.left+(rectF.right-rectF.left)/2);
        int y = (int)(rectF.top+(rectF.bottom-rectF.top)/2);
        showGifPopupWindow(itemView, stickerId, x, y);
        itemView.setStroke(Color.WHITE, 3);
        itemView.setSelected(true);
//        int[] location = new int[2];
//        itemView.getLocationOnScreen(location);
//        itemView.setStroke(Color.WHITE, 3);
//        itemView.setSelected(true);
//        showGifPopupWindow(itemView, stickerId, x, y);
    }


    /**
     * 文字贴纸移动逻辑
     */
    private void moveTextSticker(StickerTextItemView itemView, PointF pointF) {
        mDeleteLayout.setVisibility(View.VISIBLE);
        itemView.setStroke(Color.TRANSPARENT, 3);
        itemView.setSelected(true);
        int left = mDeleteLayout.getLeft();
        int right = mDeleteLayout.getRight();
        int top = mDeleteLayout.getTop();
        int bottom = mDeleteLayout.getBottom();
        int pointX = (int) pointF.x;
        int pointY = (int) pointF.y;
        if (left <= pointX && pointX <= right && pointY >= top && pointY <= bottom) {
            mDeleteLayout.setSelected(true);
            mIcDelete.setSelected(true);
        } else {
            mDeleteLayout.setSelected(false);
            mIcDelete.setSelected(false);
        }
    }

    /**
     * 图片贴纸移动逻辑
     */
    private void moveImageSticker(StickerImageItemView itemView, PointF pointF) {
        mDeleteLayout.setVisibility(View.VISIBLE);
        itemView.setStroke(Color.TRANSPARENT, 3);
        itemView.setSelected(true);
        int left = mDeleteLayout.getLeft();
        int right = mDeleteLayout.getRight();
        int top = mDeleteLayout.getTop();
        int bottom = mDeleteLayout.getBottom();
        int pointX = (int) pointF.x;
        int pointY = (int) pointF.y;
        if (left <= pointX && pointX <= right && pointY >= top && pointY <= bottom) {
            mDeleteLayout.setSelected(true);
            mIcDelete.setSelected(true);
        } else {
            mDeleteLayout.setSelected(false);
            mIcDelete.setSelected(false);
        }
    }

    /**
     * 图片贴纸移动逻辑
     */
    private void moveGifSticker(StickerDynamicItemView itemView, PointF pointF) {
        mDeleteLayout.setVisibility(View.VISIBLE);
        itemView.setStroke(Color.TRANSPARENT, 3);
        itemView.setSelected(true);
        int left = mDeleteLayout.getLeft();
        int right = mDeleteLayout.getRight();
        int top = mDeleteLayout.getTop();
        int bottom = mDeleteLayout.getBottom();
        int pointX = (int) pointF.x;
        int pointY = (int) pointF.y;
        if (left <= pointX && pointX <= right && pointY >= top && pointY <= bottom) {
            mDeleteLayout.setSelected(true);
            mIcDelete.setSelected(true);
        } else {
            mDeleteLayout.setSelected(false);
            mIcDelete.setSelected(false);
        }
    }


    /**
     * 删除文字贴纸逻辑
     */
    private void deleteTextSticker(StickerTextItemView itemView, PointF pointF, StickerData stickerData) {
        int left = mDeleteLayout.getLeft();
        int right = mDeleteLayout.getRight();
        int top = mDeleteLayout.getTop();
        int bottom = mDeleteLayout.getBottom();
        int pointX = (int) pointF.x;
        int pointY = (int) pointF.y;
        mDeleteLayout.setVisibility(GONE);
        mDeleteLayout.setSelected(false);
        mIcDelete.setSelected(false);
        if (left <= pointX && pointX <= right && pointY >= top && pointY <= bottom) {
            itemView.closeSticker();
            for (int i = 0; i < mAddTextRecordBeans.size(); i++) {
                if (mAddTextRecordBeans.get(i).getId() == stickerData.stickerId) {
                    mAddTextRecordBeans.remove(i);
                }
            }
        }
    }

    /**
     * 删除图片贴纸逻辑
     */
    private void deleteImageSticker(StickerImageItemView itemView, PointF pointF, StickerData stickerData) {
        int left = mDeleteLayout.getLeft();
        int right = mDeleteLayout.getRight();
        int top = mDeleteLayout.getTop();
        int bottom = mDeleteLayout.getBottom();
        int pointX = (int) pointF.x;
        int pointY = (int) pointF.y;
        mDeleteLayout.setVisibility(GONE);
        mDeleteLayout.setSelected(false);
        mIcDelete.setSelected(false);
        if (left <= pointX && pointX <= right && pointY >= top && pointY <= bottom) {
            itemView.closeSticker();
        }
    }

    /**
     * 删除图片贴纸逻辑
     */
    private void deleteGifImageSticker(StickerDynamicItemView itemView, PointF pointF) {
        int left = mDeleteLayout.getLeft();
        int right = mDeleteLayout.getRight();
        int top = mDeleteLayout.getTop();
        int bottom = mDeleteLayout.getBottom();
        int pointX = (int) pointF.x;
        int pointY = (int) pointF.y;
        mDeleteLayout.setVisibility(GONE);
        mDeleteLayout.setSelected(false);
        mIcDelete.setSelected(false);
        if (left <= pointX && pointX <= right && pointY >= top && pointY <= bottom) {
            itemView.closeSticker();
        }
    }

    // http://www.bubuko.com/infodetail-322326.html
    private void showTextPopupWindow(StickerTextItemView itemView, long stickerId, int x, int y) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.text_pop_window, null);


        final PopupWindow popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight =  contentView.getMeasuredHeight();
        popupWindow.setFocusable(true);

        popupWindow.setTouchable(true);
        // 设置按钮的点击事件
        FrameLayout tvSetDuration = contentView.findViewById(R.id.ll_set_duration);
        FrameLayout tvEdit = contentView.findViewById(R.id.ll_edit);
        tvSetDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置时长
                popupWindow.dismiss();
                itemView.setStroke(Color.TRANSPARENT, 3);
                itemView.setSelected(true);
                mEditorController.goTextComponent();
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 编辑状态
                popupWindow.dismiss();
                mCurrentEditStickerTextItemView = itemView;
                itemView.setVisibility(View.INVISIBLE);
                openOverlay(true, stickerId);
            }
        });


        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(
//                R.drawable.selectmenu_bg_downward));

        // 设置好参数之后再show
        // popupWindow.showAsDropDown(view,0,-TuSdkContext.dip2px(155),Gravity.TOP);
       // popupWindow.showAtLocation(mRlPop, Gravity.NO_GRAVITY, (x+itemView.getWidth()/2)-popupWidth/2, y-popupHeight+itemView.getHeight()/4-10);
        popupWindow.showAtLocation(mRlPop, Gravity.NO_GRAVITY, x-popupWidth/2, (int)(y-popupHeight*1.3));
    }


    private void showImagePopupWindow(StickerImageItemView itemView, long stickerId, int x, int y) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.image_pop_window, null);
        // 设置按钮的点击事件
        LinearLayout llSetDuration = contentView.findViewById(R.id.ll_image_pop);

        final PopupWindow popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight =  contentView.getMeasuredHeight();

        popupWindow.setFocusable(true);

        popupWindow.setTouchable(true);
        llSetDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置时长
                popupWindow.dismiss();
                itemView.setStroke(Color.TRANSPARENT, 3);
                itemView.setSelected(true);
                mEditorController.goTextComponent();
            }
        });

        popupWindow.showAtLocation(mRlPop, Gravity.NO_GRAVITY, (x+itemView.getWidth()/2)-popupWidth/2, y-popupHeight);
    }

    private void showGifPopupWindow(StickerDynamicItemView itemView, long stickerId, int x, int y) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.image_pop_window, null);
        // 设置按钮的点击事件
        LinearLayout llSetDuration = contentView.findViewById(R.id.ll_image_pop);

        final PopupWindow popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight =  contentView.getMeasuredHeight();
        popupWindow.setFocusable(true);

        popupWindow.setTouchable(true);
        llSetDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置时长
                popupWindow.dismiss();
                itemView.setStroke(Color.TRANSPARENT, 3);
                itemView.setSelected(true);
                mEditorController.goTextComponent();
            }
        });

        popupWindow.showAtLocation(mRlPop, Gravity.NO_GRAVITY, x-popupWidth/2, (int) (y-itemView.getHeight()/2-popupHeight/1.5));
    }


    public StickerView.StickerViewDelegate mStickerDelegate = new StickerView.StickerViewDelegate() {
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

        }

        @Override
        public void onStickerItemViewSelected(StickerItemViewInterface stickerItemViewInterface, StickerDynamicData stickerDynamicData, String s, boolean b) {

        }

        @Override
        public void onStickerItemViewReleased(StickerItemViewInterface stickerItemViewInterface, PointF pointF) {

            getHeaderView().setVisibility(View.VISIBLE);

            if (stickerItemViewInterface != null) {
                if (stickerItemViewInterface instanceof StickerTextItemView) {
                    StickerTextItemView itemView = (StickerTextItemView) stickerItemViewInterface;
                    if (pointF != null) {
                        if (firstPointF != null) {
                            if (Math.abs(pointF.x - firstPointF.x) <= 20 && Math.abs(pointF.y - firstPointF.y) < 20) {
                                selectTextSticker(itemView, itemView.getSticker().stickerId);
                            }
                        } else {
                            selectTextSticker(itemView, itemView.getSticker().stickerId);
                        }

                        StickerData stickerData = itemView.getSticker();
                        deleteTextSticker(itemView, pointF, stickerData);
                    }
                } else if (stickerItemViewInterface instanceof StickerImageItemView) {
                    StickerImageItemView itemView = (StickerImageItemView) stickerItemViewInterface;
                    if (pointF != null) {
                        if (firstPointF != null) {
                            if (Math.abs(pointF.x - firstPointF.x) <= 20 && Math.abs(pointF.y - firstPointF.y) < 20) {
                                selectImageSticker(itemView, itemView.getSticker().stickerId);
                            }
                        } else {
                            selectImageSticker(itemView, itemView.getSticker().stickerId);
                        }
                        StickerData stickerData = itemView.getSticker();
                        deleteImageSticker(itemView, pointF, stickerData);
                    }
                } else if (stickerItemViewInterface instanceof StickerDynamicItemView) {
                    StickerDynamicItemView itemView = (StickerDynamicItemView) stickerItemViewInterface;
                    if (pointF != null) {
                        if (firstPointF != null) {
                            if (Math.abs(pointF.x - firstPointF.x) <= 20 && Math.abs(pointF.y - firstPointF.y) < 20) {
                                selectGifImageSticker(itemView, itemView.getCurrentStickerGroup().getStickerData().stickerId);
                            }
                        } else {
                            selectGifImageSticker(itemView, itemView.getCurrentStickerGroup().getStickerData().stickerId);
                        }
                        deleteGifImageSticker(itemView, pointF);
                    }
                }
            }

            firstPointF = null;
        }


        @Override
        public void onCancelAllStickerSelected() {
        }

        @Override
        public void onStickerCountChanged(StickerData stickerData, final StickerItemViewInterface stickerItemViewInterface, int operation, int count) {
            if (stickerItemViewInterface != null) {
                if (operation == 1) {
                    if (stickerItemViewInterface instanceof StickerTextItemView) {
                        StickerTextItemView itemView = (StickerTextItemView) stickerItemViewInterface;
                        setStickerData(itemView);
                    }
                }

            }
        }


        @Override
        public void onStickerCountChanged(StickerDynamicData stickerData, StickerItemViewInterface stickerItemViewInterface, int operation, int count) {

        }

        @Override
        public void onStickerItemViewMove(StickerItemViewInterface stickerItemViewInterface, Rect rect, PointF pointF) {
            getHeaderView().setVisibility(GONE);
            if (firstPointF == null) {
                firstPointF = pointF;
            }
            if (stickerItemViewInterface != null) {
                if (stickerItemViewInterface instanceof StickerTextItemView) {
                    StickerTextItemView itemView = (StickerTextItemView) stickerItemViewInterface;
                    int height = itemView.getHeight();
                    moveTextSticker(itemView, pointF);
                } else if (stickerItemViewInterface instanceof StickerImageItemView) {
                    StickerImageItemView itemView = (StickerImageItemView) stickerItemViewInterface;


                    moveImageSticker(itemView, pointF);
                } else if (stickerItemViewInterface instanceof StickerDynamicItemView) {
                    StickerDynamicItemView itemView = (StickerDynamicItemView) stickerItemViewInterface;


                    moveGifSticker(itemView, pointF);
                }

            }
        }
    };


    private TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener mOnDisplayChangeListener = new TuSdkEditorPlayer.TuSdkPreviewSizeChangeListener() {
        @Override
        public void onPreviewSizeChanged(final TuSdkSize previewSize) {
            if (mTextStickerView == null) return;
            // mCurrentPreviewSize = TuSdkSize.create(previewSize.width, previewSize.height);
            ThreadHelper.post(new Runnable() {
                @Override
                public void run() {
                    mTextStickerView.setVisibility(View.VISIBLE);
                    //   mTextStickerView.changeOrUpdateStickerType(StickerView.StickerType.Text);
                    mTextStickerView.resize(previewSize, mEditorController.getVideoContentView());
                }
            });

        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectPicture(StickerEvent event) {
        mEditorController.getMovieEditor().getEditorPlayer().startPreview();
        addSticker(event.getImageSqlInfo().path);
    }

    private void addSticker(String path) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Utils.addImageSticker(BitmapUtils.decodeUri(mEditorController.getActivity(), uri, 200, 200), mEditorController.getMovieEditor().getEditorPlayer(), getImageStickerView(), mEditorController.getActivity());
    }

    /**
     * 播放状态和进度回调 (播放器的)
     */
    private TuSdkEditorPlayer.TuSdkProgressListener mPlayProgressListener = new TuSdkEditorPlayer.TuSdkProgressListener() {
        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onProgress(long playbackTimeUs, long totalTimeUs, float percentage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (StickerItemViewInterface itemViewInterface : mTextStickerView.getStickerItems()) {
                        if (itemViewInterface instanceof StickerImageItemView) {
                            StickerImageItemView itemView = (StickerImageItemView) itemViewInterface;
                            StickerImageData textData = (StickerImageData) itemView.getSticker();
                            if (textData.isContains(playbackTimeUs)) {
                                itemView.setVisibility(View.VISIBLE);
                            } else {
                                itemView.setVisibility(GONE);
                            }
                        } else if (itemViewInterface instanceof StickerTextItemView) {
                            StickerTextItemView itemView = (StickerTextItemView) itemViewInterface;
                            StickerTextData imageData = (StickerTextData) itemView.getSticker();
                            if (imageData.isContains(playbackTimeUs)) {
                                if (!isEditSticker) {
                                    itemView.setVisibility(View.VISIBLE);
                                }

                            } else {
                                itemView.setVisibility(GONE);
                            }
                        } else if (itemViewInterface instanceof StickerDynamicItemView) {
                            StickerDynamicItemView itemView = ((StickerDynamicItemView) itemViewInterface);
                            StickerDynamicData dynamicData = itemView.getCurrentStickerGroup();
                            itemView.updateStickers(System.currentTimeMillis());
                            if (dynamicData.isContains(playbackTimeUs)) {
                                itemView.setVisibility(View.VISIBLE);
                            } else {
                                itemView.setVisibility(GONE);
                            }
                        }
                    }


                }
            });
        }
    };


    /**
     * 返回键
     */
    @Override
    public void onBackPressed() {
        if(mEditorController.isAlbum){
            finish();
        }else{
            if (mEditorController.getCurrentComponent() instanceof EditorVoiceoverComponent) {
                ((EditorVoiceoverComponent) mEditorController.getCurrentComponent()).backBtnTip();
            } else {
                if(isTrim){
                    DialogHelper.remindTitleAndContentCenter(this, this.getResources().getString(R.string.dialog_title_discard_edits_tips), "", new DialogHelper.onRemindSureClickListener() {
                        @Override
                        public void onSureClick() {
                            backDeal();
                        }
                    });
                }else{
                    DialogHelper.remindTitleAndContentCenter(this, this.getResources().getString(R.string.dialog_title_discard_edits_tips), this.getResources().getString(R.string.dialog_content_discard_entire_clip_tips), new DialogHelper.onRemindSureClickListener() {
                        @Override
                        public void onSureClick() {
                            backDeal();
                        }
                    });
                }


            }
        }

    }

    private void backDeal() {
        AppConstants.EDIT_TYPE = 2;
        EventBus.getDefault().post(new BackEvent());
        EventBus.getDefault().post(new SelectSoundEvent(null, AppConstants.ENTER_STATE));
        ThreadHelper.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000);

    }

    public ArrayList<String> getStickerIds() {
        return stickerIds;
    }
}



