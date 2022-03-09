package org.lasque.twsdkvideo.video_beauty.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.IOException;

/**
 * 音频播放控制类
 */
public class Mp3Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {
    /**
     * 状态-未初始化
     */
    public static final int STATE_UNINITIALIZED = 0;
    /**
     * 状态-初始化完毕
     */
    public static final int STATE_INITIALIZED = 1;
    /**
     * 状态-准备完毕
     */
    public static final int STATE_PREPARED = 2;
    /**
     * 状态-播放
     */
    public static final int STATE_PLAYING = 3;
    /**
     * 状态-暂停
     */
    public static final int STATE_PAUSE = 4;
    /**
     * 状态-停止
     */
    public static final int STATE_STOP = 5;

    public int mState;
    private int mSampleTime;

    private static Mp3Player mInstance;
    public MediaPlayer mMediaPlayer;
    private AudioPlayerListener mAudioPlayerListener;

    private final Handler mHandler;
    private Runnable mUndatePlayPositionRunnable = new Runnable() {

        @Override
        public void run() {
            undatePosition();
        }
    };
    public String path;

    public void setmAudioPlayerListener(AudioPlayerListener mAudioPlayerListener) {
        this.mAudioPlayerListener = mAudioPlayerListener;
    }

    private Mp3Player() {
        super();
        mState = STATE_UNINITIALIZED;
        mSampleTime = 100;

        if (Looper.myLooper() == null) {
            Looper.prepare();
            mHandler = new Handler();
            Looper.loop();
        } else {
            mHandler = new Handler();
        }
    }

    public static Mp3Player getInstance() {
        mInstance = new Mp3Player();
        return mInstance;
    }

    /**
     * 初始化MediaPlayer
     */
    public void init(AudioPlayerListener listener) {
        if (mState == STATE_UNINITIALIZED) {
            if (listener == null) {
                throw new RuntimeException("AudioPlayerListener not null");
            }
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mState = STATE_INITIALIZED;
        }
        this.mAudioPlayerListener = listener;
    }


    public boolean isPlaying(){
        if (mMediaPlayer!=null){
            return mMediaPlayer.isPlaying();
        }else {
            return false;
        }
    }

    /**
     * 设置音频源（异步）
     *
     * @param path
     * @return 返回Duration
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws IOException
     */
    public boolean setDataSource(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        this.path = path;
        if (mState == STATE_UNINITIALIZED) {
            throw new RuntimeException("设置音频源之前请进行初始化");
        }
        if (mState == STATE_PAUSE || mState == STATE_PLAYING) {
            stop();
        }
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepareAsync();
        return true;
    }


    public String getPath() {
        return path;
    }

    /**
     * 设置音频源（异步）
     *
     * @param fileDescriptor
     * @return 返回Duration
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws IllegalStateException
     * @throws IOException
     */
    public boolean setDataSource(AssetFileDescriptor fileDescriptor) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {

        if (mState == STATE_UNINITIALIZED) {
            throw new RuntimeException("设置音频源之前请进行初始化");
        }
        if (mState == STATE_PAUSE || mState == STATE_PLAYING) {
            stop();
        }
        if (fileDescriptor == null) {
            return false;
        }
        mMediaPlayer.reset();
        mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                fileDescriptor.getLength());
        mMediaPlayer.prepareAsync();
        return true;
    }

    public void play() throws IllegalStateException, IOException {
        if (mState == STATE_UNINITIALIZED) {
            throw new RuntimeException("播放前请进行初始化");
        }
        if (mState == STATE_INITIALIZED) {
            throw new RuntimeException("播放前请设置音频源");
        }
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mState = STATE_PLAYING;
            undatePosition();
        }
    }

    public void pause() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mState = STATE_PAUSE;
            }
        } catch (Exception e) {

        }
    }

    public void stop() {
        if (mMediaPlayer != null && (mMediaPlayer.isPlaying() || mState == STATE_PAUSE)) {
            mMediaPlayer.stop();
            mState = STATE_STOP;
        }
    }

    public void release() {
        mState = STATE_UNINITIALIZED;
        stop();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
        mAudioPlayerListener = null;
        mInstance = null;
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(msec);
        }
    }

    /**
     * 获取当前播放位置
     *
     * @return
     */
    public int getCurrentPostion() {
        return mMediaPlayer == null ? -1 : mMediaPlayer.getCurrentPosition();
    }

    /**
     * 获取总时间
     *
     * @return
     */
    public int getDuration() {

        return mMediaPlayer == null ? -1 : mMediaPlayer.getDuration();
    }

    /**
     * 获取当前状态
     *
     * @return
     */
    public int getState() {
        return mState;
    }

    /**
     * 更新进度
     */
    private void undatePosition() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mAudioPlayerListener.onUpdateCurrentPosition(mMediaPlayer.getCurrentPosition());
            mHandler.postDelayed(mUndatePlayPositionRunnable, mSampleTime);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mAudioPlayerListener.onBufferingUpdate(mp, percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = STATE_PREPARED;
        mAudioPlayerListener.onPrepared();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mState = STATE_STOP;
        mAudioPlayerListener.onCompletion();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mAudioPlayerListener.onError(mp, what, extra);
        return true;
    }

    public void reset() {
        if (mMediaPlayer!=null){
            mMediaPlayer.reset();
        }
    }

    public interface AudioPlayerListener {
        /**
         * AudioPlayer准备完成时回调
         */
        void onPrepared();

        /**
         * AudioPlayer播放完成时回调
         */
        void onCompletion();

        /**
         * AudioPlayer播放期间每个设置的取样时间间隔回调一次
         *
         * @param position 当前播放位置
         */
        void onUpdateCurrentPosition(int position);

        /**
         * 缓存进度回调
         *
         * @param mp
         * @param percent
         */
        void onBufferingUpdate(MediaPlayer mp, int percent);

        /**
         * Called to indicate an error.
         *
         * @param mp
         * @param what
         * @param extra
         */
        void onError(MediaPlayer mp, int what, int extra);

    }
}