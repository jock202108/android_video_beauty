package org.lasque.twsdkvideo.video_beauty.views.editor

import android.content.Context
import android.graphics.*
import android.icu.util.TimeUnit
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import org.lasque.twsdkvideo.video_beauty.R
import org.lasque.twsdkvideo.video_beauty.utils.TimeUtils
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.floor
import kotlin.random.Random

/**
 * @date       2022/1/23
 * @des        音频裁剪控件
 */
class MusicTrimView : View {
    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }


    private var screenTime = 1000L//一屏所占的时长，视频长度
    private val groupLineCount = 10//每组音符线的数量
    private val screenLineGroupCount = 7//一屏音符组数量
    private var lineW = 10//音符线宽度
    private var minLineH = 50//音符线最短长度

    private var lineWDiff = 0f//音符之间宽度间距

    private var lineColor = Color.WHITE
    private var lineProgressColor = Color.WHITE

    private var musicTotalLength = 0L//音频总长度
    private var startProgress = 0L//音频起始播放位置
    private var currentProgress = 0L//当前音频播放进度
    private val musicLines = ArrayList<MusicLine>()
    private val layoutRect = Rect()
    private val scroller by lazy { Scroller(context, null, false) }

    var onStartProgressChangeListener: OnStartProgressChangeListener? = null

    interface OnStartProgressChangeListener {
        fun onChange(start: Long)
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL_AND_STROKE
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        setWillNotDraw(false)
        attrs?.apply {
            val obtain = context.obtainStyledAttributes(this, R.styleable.MusicCropView)
            lineColor = obtain.getColor(R.styleable.MusicCropView_mt_line_color, lineColor)
            lineProgressColor =
                obtain.getColor(R.styleable.MusicCropView_mt_line_progress_color, lineProgressColor)
            minLineH =
                obtain.getDimension(R.styleable.MusicCropView_mt_line_min_h, minLineH.toFloat())
                    .toInt()
            lineW =
                obtain.getDimension(R.styleable.MusicCropView_mt_line_w, lineW.toFloat()).toInt()
        }
    }

    private fun initMusicView() {
        musicLines.clear()
        val wSplitCount = (groupLineCount - 1) * screenLineGroupCount//宽度拆分个数
        lineWDiff = (measuredWidth - lineW).toFloat() / wSplitCount
        val randomRange = measuredHeight - minLineH
        //音符间隔的时间长度
        val lineDuration = screenTime.toFloat() / wSplitCount
        //计算总共需要多少根音符
        val lineCount = floor(musicTotalLength / lineDuration).toInt() + 1
        var preLine: MusicLine? = null
        val list = List(lineCount) {
            var position = (it * lineDuration).toLong()
            if (position > musicTotalLength) position = musicTotalLength
            val rectF = RectF()
            val randoms = (0..randomRange).random()

            val currentTop = randoms/2f
            rectF.top = currentTop
            rectF.bottom = measuredHeight - currentTop




            rectF.left = if (preLine == null) 0f else preLine!!.rect.left + lineWDiff
            rectF.right = rectF.left + lineW
            preLine = MusicLine(rectF, position)
            return@List preLine!!
        }
        musicLines.addAll(list)
        layoutRect.set(0, 0, list.last().rect.right.toInt(), measuredHeight)
        requestLayout()
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        musicLines.forEach {
            if (it.position <= currentProgress) {
                paint.color = lineProgressColor
            } else {
                paint.color = lineColor
            }
            canvas.drawRoundRect(it.rect, lineW.toFloat(), lineW.toFloat(), paint)
        }
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(layoutRect.left, layoutRect.top, layoutRect.right, layoutRect.bottom)
    }

    var lastX = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val diffX = event.rawX - lastX
                lastX = event.rawX
                if (layoutRect.left + diffX > 0) {
                    layoutRect.set(
                        0,
                        layoutRect.top,
                        musicLines.last().rect.right.toInt(),
                        layoutRect.bottom
                    )
                } else if (layoutRect.right + diffX < measuredWidth) {
                    layoutRect.set(
                        measuredWidth - width,
                        layoutRect.top,
                        measuredWidth,
                        layoutRect.bottom
                    )
                } else {
                    layoutRect.set(
                        layoutRect.left + diffX.toInt(),
                        layoutRect.top,
                        layoutRect.right + diffX.toInt(),
                        layoutRect.bottom
                    )
                }
                requestLayout()
            }
            MotionEvent.ACTION_UP -> {
                startProgress =
                    (musicTotalLength * (abs(layoutRect.left.toFloat()) / width)).toLong()
                onStartProgressChangeListener?.onChange(startProgress)
            }
        }
        return true
    }

    fun setDuration(musicDuration: Long, videoDuration: Long, previewProgress: Long) {
        musicTotalLength = musicDuration
        screenTime = videoDuration
        currentProgress = previewProgress
        startProgress = previewProgress

        initMusicView()
        layoutRect.set(
            layoutRect.left - (previewProgress.toFloat() / musicTotalLength * width).toInt(),
            layoutRect.top,
            layoutRect.right - (previewProgress.toFloat() / musicTotalLength * width).toInt(),
            layoutRect.bottom
        )
        onStartProgressChangeListener?.onChange(previewProgress)
    }

    fun setProgress(progress: Long) {
        currentProgress = progress + startProgress
        if (visibility == VISIBLE)
            invalidate()
    }


    class MusicLine(
        val rect: RectF, //音符形状和位置
        val position: Long//音符对应时间轴上的位置
    ) {
        override fun toString(): String {
            return "MusicLine(rectF=$rect, position=$position)"
        }
    }
}
