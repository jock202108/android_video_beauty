package org.lasque.twsdkvideo.video_beauty.tubeautysetting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.SizeF;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.core.util.Pair;

import com.tusdk.pulse.filter.Image;

import org.lasque.tusdkpulse.core.struct.TuSdkSize;
import org.lasque.tusdkpulse.core.utils.TLog;
import org.lasque.tusdkpulse.core.utils.ThreadHelper;
import org.lasque.tusdkpulse.core.utils.image.ImageOrientation;
import org.lasque.twsdkvideo.video_beauty.utils.TextureRender;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

import ai.deepar.ar.ARErrorType;
import ai.deepar.ar.AREventListener;
import ai.deepar.ar.DeepAR;
import ai.deepar.ar.DeepARImageFormat;


public class PipeMediator implements ImageConvert.ProcessProperty {

  private static final String TAG = "PipeMediator";

  private static PipeMediator INSTANCE = null;

  private DeepAR deepAR;

  public static PipeMediator getInstance() {
    if (INSTANCE == null) {
      synchronized (PipeMediator.class) {
        if (INSTANCE == null) {
          INSTANCE = new PipeMediator();
        }
      }
    }
    return INSTANCE;
  }


  private PipeMediator() {
    mRenderPipe = new org.lasque.twsdkvideo.video_beauty.tubeautysetting.RenderPipe();
    mRenderPipe.initRenderPipe();

    mImageConvert = new ImageConvert(mRenderPipe);

  }

  /**
   * 输入纹理尺寸
   */
  private TuSdkSize mInputSize;

  private ByteBuffer buffer;
  private ByteBuffer buffer2;

  /**
   * 相机纹理输入方向
   */
  private ImageOrientation mCameraInputOrientation;

  /**
   * RenderPipe
   */
  private RenderPipe mRenderPipe;

  /**
   * 渲染宽度,默认值为720
   */
  private int mRenderWidth = 720;

  /**
   * 渲染比例
   */
  private Pair<Double, Double> mAspect;

  /**
   * 当前渲染区域
   */
  private RectF mCurrentPreviewRect;

  /**
   * 方向传感器
   */
  private SensorHelper mSensorHelper;

  /**
   * 渲染管理器
   */
  private ImageConvert mImageConvert;

  private TextureRender mOESRender;

  /**
   * 美颜参数管理器
   */
  private BeautyManager mBeautyManager;

  /**
   * 预览渲染管理器
   */
  private org.lasque.twsdkvideo.video_beauty.tubeautysetting.PreviewManager mPreviewManager;

  private boolean isReady = false;

  /**
   * --------------------------- Record Manager -----------------------------------
   */

  private org.lasque.twsdkvideo.video_beauty.tubeautysetting.RecordManager mRecordManager = new org.lasque.twsdkvideo.video_beauty.tubeautysetting.RecordManager();

  private long mRecordingStart = -1;

  private double mVideoStretch = 1.0;

  private double mAudioStretch = 1.0;

  private Thread mAudioConvertReceiverThread;

  private Thread mAudioWritingThread;

  private AudioConvert mAudioConvert;

  private boolean isNeedMixer = false;

  private boolean isNeedAudioPrecess = false;

  private String mBGMPath = "";

  private Long mAudioStartPos = 0L;

  private AudioConvert.AudioPitchType mCurrentAudioPitch = AudioConvert.AudioPitchType.NORMAL;

  private LinkedBlockingQueue<AudioConvert.AudioItem> mOutputQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

  private Runnable mAudioConvertReceiverRunnable = new Runnable() {
    @Override
    public void run() {
      while (!ThreadHelper.isInterrupted()) {
        try {
          AudioConvert.AudioItem item = mAudioConvert.receiveAudioData();
          if (item == null) continue;
          if (item.length > 0) {
            long currentAudioTime = System.currentTimeMillis() - mRecordingStart;
            mOutputQueue.put(item);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  };

  private Runnable mAudioWriterRunnable = new Runnable() {
    @Override
    public void run() {
      while (!ThreadHelper.isInterrupted()) {
        AudioConvert.AudioItem item = mOutputQueue.poll();
        if (item != null) {
          TLog.e("[Debug] current audio info %s", item);
          mRecordManager.sendAudio(item.buffer, item.buffer.length, item.time);
        }
      }
    }
  };

  /**
   * @return 是否可用
   */
  public boolean isReady() {
    return isReady;
  }

  /**
   * @param renderWidth 渲染画面宽度 默认值为720
   */
  public void setRenderWidth(int renderWidth) {
    mRenderWidth = renderWidth;
    if (mImageConvert.isReady()) {
      mImageConvert.setRenderWidth(renderWidth);
    }
  }

  /**
   * @param context Context 对象
   * @param parent  渲染View父布局
   * @param aspect  默认渲染尺寸比例
   * @return Init结果 不为0时 说明参数存在错误
   */
  public Pair<Boolean, Integer> requestInit(Context context, ViewGroup parent, SizeF aspect) {
    if (isReady) {
      return new Pair<>(false, -1);
    }





    mSensorHelper = new SensorHelper(context);

    mAspect = new Pair<>(((double) aspect.getWidth()), ((double) aspect.getHeight()));
    mImageConvert.setInputSize(mInputSize.width, mInputSize.height, mCameraInputOrientation);
    mImageConvert.setRenderWidth(mRenderWidth);
    mImageConvert.setAspect(aspect.getWidth(), aspect.getHeight());
    mImageConvert.setProcessProperty(this);



    Pair<Boolean, Integer> result = mImageConvert.requestInit();
    if (!result.first) {
      return result;
    }




    mRenderPipe.getRenderPool().runSync(new Runnable() {
      @Override
      public void run() {
        deepAR = new DeepAR(context);
        deepAR.setLicenseKey("1ca3595bced748fd983b84e613d4fc93ff3cc1adf60581fe5bc31f4ef139dbcf5954a9e61ae8e0f8");
        deepAR.initialize(context, new AREventListener() {
          @Override
          public void screenshotTaken(Bitmap bitmap) {

          }

          @Override
          public void videoRecordingStarted() {

          }

          @Override
          public void videoRecordingFinished() {

          }

          @Override
          public void videoRecordingFailed() {

          }

          @Override
          public void videoRecordingPrepared() {

          }

          @Override
          public void shutdownFinished() {

          }

          @Override
          public void initialized() {
            deepAR.switchEffect("mask", getFilterPath("aviators"));
          }

          @Override
          public void faceVisibilityChanged(boolean b) {

          }

          @Override
          public void imageVisibilityChanged(String s, boolean b) {

          }

          @Override
          public void frameAvailable(android.media.Image image) {
            onFrameAvailable(image);
          }

          @Override
          public void error(ARErrorType arErrorType, String s) {

          }

          @Override
          public void effectSwitched(String s) {

          }

          private String getFilterPath(String filterName) {
            if (filterName.equals("none")) {
              return null;
            }
            return "file:///android_asset/" + filterName;
          }
        });
        deepAR.setOffscreenRendering(mImageConvert.getRenderSize().width,mImageConvert.getRenderSize().height);


        mOESRender = new TextureRender(false);
        mOESRender.create(mImageConvert.getRenderSize().width,mImageConvert.getRenderSize().height,true);
      }
    });

    mAudioConvert = new AudioConvert();

    mBeautyManager = new org.lasque.twsdkvideo.video_beauty.tubeautysetting.BeautyManager();
    mBeautyManager.requestInit(mRenderPipe);

    mPreviewManager = new org.lasque.twsdkvideo.video_beauty.tubeautysetting.PreviewManager();
    result = mPreviewManager.requestInit(context, parent);
    if (!result.first) {
      return result;
    }

    isReady = true;

    return new Pair<Boolean, Integer>(true, 0);
  }

  /**
   * @param aspect 更新渲染画面比例
   */
  public void updateAspect(SizeF aspect) {
    if (!isReady) return;
    mAspect = new Pair<>(((double) aspect.getWidth()), ((double) aspect.getHeight()));
    mImageConvert.updateAspect(aspect.getWidth(), aspect.getHeight());
  }

  /**
   * @param rectF 实际渲染区域 默认计算方式为fitin
   */
  public void changedRect(@Nullable RectF rectF) {
    mCurrentPreviewRect = rectF;
  }

  /**
   * @return SurfaceTexture Android系统纹理包装类 OES类型 可直接设置到Camera中
   */
  public SurfaceTexture getSurfaceTexture() {
    return mImageConvert.getSurfaceTexture();
  }
  /**
   * SurfaceTexture 回调中调用此方法 通知管线进行渲染
   */
  public Image onFrameAvailable() {
    if (!isReady) return null;
//    if (deepAR == null) return null;
    //1. 通过ImageConvert 将Android默认的OES纹理转换为可处理的Texture2D纹理
    Image in = mImageConvert.onFrameAvailable();
//    if (deepAR != null) {
//      buffer.put(in.getBuffer(Image.Format.YUV_420_888));
//      buffer.position(0);
//      deepAR.receiveFrame(buffer, 720, 1517,
//        180, mCameraInputOrientation.isMirrored(), DeepARImageFormat.YUV_420_888, 0);
//    }


    if (deepAR != null) {
//      deepAR.useSingleThreadedMode(true);

//      buffer.position(0);
//      buffer.put(in.getBuffer(Image.Format.NV21));
//      deepAR.receiveFrame(buffer, in.GetWidth(), in.GetHeight(),
//              180, false, DeepARImageFormat.YUV_NV21, 0);

      Image out = mBeautyManager.processFrame(in);

      mRenderPipe.getRenderPool().runSync(new Runnable() {
        @Override
        public void run() {
          mOESRender.drawFrame(out.getGLTexture(),out.GetWidth(),out.GetHeight());
          deepAR.receiveFrameExternalTexture(out.GetWidth(),out.GetHeight(),180,false,mOESRender.getTextureID());

        }
      });


    }


    return in;
  }

  public void onFrameAvailable(android.media.Image image) {
    if (!isReady || image == null || image.getPlanes().length < 1) {
      return;
    }
    final android.media.Image.Plane[] planes = image.getPlanes();
    final Buffer buffer = planes[0].getBuffer().rewind();
    int pixelStride = planes[0].getPixelStride();
    int rowStride = planes[0].getRowStride();
    int rowPadding = rowStride - pixelStride * image.getWidth();
    Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
    bitmap.copyPixelsFromBuffer(buffer);
    image.close();

    long inputPos = System.currentTimeMillis();
    Image in = new Image(bitmap, inputPos);
    //2. 通过BeautyManager进行美颜处理
    Image out = in;
    //3. 将处理后的Image显示到View上
    if (mCurrentPreviewRect == null) {
      mPreviewManager.updateImage(out);
    } else {
      mPreviewManager.updateImage(out, mCurrentPreviewRect);
    }
    //4. 需要录制的情况下 将处理后的Image对象送入RecordManager进行文件输出
    if (mRecordManager != null) {
      long recordPos = System.currentTimeMillis();
      mRecordManager.sendImage(out, recordPos);
    }
    out.release();
  }

  /**
   * 开始录制视频
   *
   * @param outputPath   视频输出绝对路径
   * @param width        视频输出宽度
   * @param height       视频输出高度
   * @param watermark    水印图片
   * @param watermarkPos 水印位置 // 0 : tl, 1 : tr, 2 : bl, 3 : br
   */
  public void startRecord(String outputPath, int width, int height, @Nullable Bitmap watermark, int watermarkPos, org.lasque.twsdkvideo.video_beauty.tubeautysetting.RecordManager.RecordListener listener) {
    mRecordManager.setRecordListener(listener);
    mRecordManager.newExporter(outputPath, width, height, 2, 44100, watermark, watermarkPos, mRenderPipe.getContext());

    if (isNeedMixer || isNeedAudioPrecess) {
      mAudioConvert.requestInit(2, 44100);
      mAudioConvert.updateAudioPitch(mCurrentAudioPitch);
      mAudioConvert.updateAudioStretch(mAudioStretch);
      if (isNeedMixer)
        mAudioConvert.initAudioMixer(mBGMPath, mAudioStartPos);
      mAudioConvertReceiverThread = ThreadHelper.runThread(mAudioConvertReceiverRunnable);
      mAudioConvert.startAudioConvert();
    }

    mRecordManager.startExporter();

    mAudioWritingThread = ThreadHelper.runThread(mAudioWriterRunnable);
  }

  /**
   * 暂停录制
   */
  public void pauseRecord() {
    mRecordManager.pauseExporter();

    if (isNeedMixer || isNeedAudioPrecess) {
      mAudioConvert.stopAudioConvert();
      mAudioConvertReceiverThread.interrupt();
    }

    mAudioWritingThread.interrupt();
  }

  /**
   * 停止录制
   */
  public void stopRecord() {
    mRecordManager.stopExporter();
  }

  /**
   * @param speed 录制速率 0.1~2.0
   */
  public void setRecordSpeed(double speed) {
    mAudioStretch = speed;
    if (mAudioStretch == 2.0) {
      mVideoStretch = 0.5;
    } else if (mAudioStretch == 1.5) {
      mVideoStretch = 0.75;
    } else if (mAudioStretch == 0.75) {
      mVideoStretch = 1.5;
    } else if (mAudioStretch == 0.5) {
      mVideoStretch = 2.0;
    } else {
      mVideoStretch = mAudioStretch;
    }

    mRecordManager.updateStretch(mAudioStretch);
  }

  /**
   * @return 删除当前最后一段录制片段
   */
  public org.lasque.twsdkvideo.video_beauty.tubeautysetting.RecordManager.VideoFragmentItem popLastFragment() {
    return mRecordManager.popFragment();
  }

  /**
   * @return 获取当前录制长度
   */
  public long getCurrentRecordDuration() {
    return mRecordManager.getCurrentRecordDuration();
  }

  /**
   * @return 当前录制片段数量
   */
  public int getCurrentRecordFragmentSize() {
    return mRecordManager.getFragmentSize();
  }

  /**
   * @param isPlay 是否开始播放合拍片段
   */
  public void setJoinerPlay(boolean isPlay) {
    if (!hasJoiner()) return;
    mBeautyManager.setJoinerPlayerState(isPlay);
    if (isPlay) {
      mAudioConvert.startAudioPlayer();
    } else {
      mAudioConvert.stopAudioPlayer();
    }

  }

  public void setBGMPlay(boolean isPlay) {
    if (TextUtils.isEmpty(mBGMPath)) return;
    if (isPlay) {
      mAudioConvert.startAudioPlayer();
    } else {
      mAudioConvert.stopAudioPlayer();
    }
  }


  /**
   * @param buffer 音频PCM数据
   * @param size   数据长度
   * @param ts     时间戳
   * @return
   */
  public boolean sendAudioBuffer(byte[] buffer, int size, long ts) {
    AudioConvert.AudioItem audioItem = new AudioConvert.AudioItem(buffer, size, ts);

    TLog.e("[Debug] %s send audio buffer isNeedAudioPrecess %s isNeedMixer %s", TAG, isNeedAudioPrecess, isNeedMixer);

    if (!isNeedAudioPrecess && !isNeedMixer) {
      TLog.e("[Debug] is has audio process %s is has mixer %s", isNeedAudioPrecess, isNeedMixer);
      try {
        mOutputQueue.put(audioItem);
      } catch (InterruptedException e) {
        e.printStackTrace();
        return false;
      }
    } else {
      mAudioConvert.sendAudioData(audioItem);
    }

    return true;
  }


  @Override
  public double getAngle() {
    return mSensorHelper.getDeviceAngle();
  }

  @Override
  public boolean getEnableMarkSence() {
    return mBeautyManager.checkEnableMarkSence();
  }

  /**
   * @return 获取美颜属性管理类
   */
  public org.lasque.twsdkvideo.video_beauty.tubeautysetting.BeautyManager getBeautyManager() {
    return mBeautyManager;
  }

  public RenderPipe getRenderPipe(){
    return mRenderPipe;
  }

  /**
   * 设置视频画面输入大小
   *
   * @param width  宽度
   * @param height 高度
   */
  public void setInputSize(int width, int height) {
    mInputSize = new TuSdkSize(width, height);
    buffer = ByteBuffer.allocateDirect(width * height * 3);
    buffer.order(ByteOrder.nativeOrder());
    buffer.position(0);

    buffer2 = ByteBuffer.allocateDirect(width * height * 3);
    buffer2.order(ByteOrder.nativeOrder());
    buffer2.position(0);
  }

  /**
   * 设置视频画面输入方向
   *
   * @param rotation 角度
   * @param isMirror 是否镜像
   */
  public void setInputRotation(int rotation, boolean isMirror) {
    mCameraInputOrientation = ImageOrientation.getValue(rotation, isMirror);
  }

  /**
   * @param pitchType 变声类型
   */
  public void setSoundPitchType(AudioConvert.AudioPitchType pitchType) {
    mCurrentAudioPitch = pitchType;
    if (pitchType != AudioConvert.AudioPitchType.NORMAL)
      isNeedAudioPrecess = true;
    else
      isNeedAudioPrecess = false;
  }

  /**
   * @param path 背景音乐路径
   */
  public void setBGM(String path, Long audioStartPos) {
    mBGMPath = path;
    mAudioStartPos = audioStartPos;
    isNeedAudioPrecess = true;
    isNeedMixer = true;
  }

  /**
   * @param videoPath  合拍视频路径
   * @param cameraRect 合拍中相机渲染区域
   * @param videoRect  合拍中视频渲染区域
   */
  public void setJoiner(String videoPath, String audioPath, RectF cameraRect, RectF videoRect, Long audioStartPos) {
    mBGMPath = audioPath;
    mAudioStartPos = audioStartPos;
    mBeautyManager.updateVideoStretch(mVideoStretch);
    mBeautyManager.setRenderSize(mImageConvert.getRenderSize());
    mBeautyManager.setJoiner(videoRect, cameraRect, videoPath);
    isNeedAudioPrecess = true;
    isNeedMixer = true;
  }


  /**
   * 删除合拍
   */
  public void deleteJoiner() {
    mBGMPath = "";
    mAudioConvert.resetAudioMixer();
    mBeautyManager.deleteJoiner();
    isNeedMixer = false;
  }

  /**
   * 合拍画面跳转
   *
   * @param ts 时间戳
   * @return
   */
  public boolean joinerSeek(long ts) {
    return mBeautyManager.joinerSeek(ts);
  }

  /**
   * @return 当前是否在合拍状态
   */
  public boolean hasJoiner() {
    return mBeautyManager.hasJoiner();
  }

  public boolean hasBGM() {
    if (hasJoiner()) return false;
    return !TextUtils.isEmpty(mBGMPath);
  }

  /**
   * @param color 预览区域背景颜色
   */
  public void setPreviewBackgroundColor(int color) {
    mPreviewManager.setBackgroundColor(color);
  }

  /**
   * 处理相机拍照输出的图片
   *
   * @param input 单张图片
   * @return 处理后的图片
   */
  public Bitmap processBitmap(Bitmap input) {
    Image res = mBeautyManager.processFrame(new Image(input, System.currentTimeMillis()));
    return res.toBitmap();
  }

  /**
   * 释放
   */
  public void release() {
    if (!isReady) return;
    mImageConvert.release();
    mBeautyManager.release();
    mRenderPipe.release();

    deepAR.setAREventListener(null);
    deepAR.release();
    deepAR = null;

    isReady = false;
  }
}
