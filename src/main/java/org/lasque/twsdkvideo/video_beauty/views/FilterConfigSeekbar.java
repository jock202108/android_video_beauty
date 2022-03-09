/** 
 * TuSDKLiveDemo
 * FilterConfigSeekbar.java
 *
 * @author 		Yanlin
 * @Date 		2016-4-15 上午10:36:28
 * @Copyright 	(c) 2016 tw. All rights reserved.
 *
 */
package org.lasque.twsdkvideo.video_beauty.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lasque.tusdk.core.TuSdkContext;
import org.lasque.tusdk.core.seles.SelesParameters.FilterArg;
import org.lasque.tusdk.core.seles.SelesParameters.FilterParameterInterface;
import org.lasque.tusdk.core.seles.sources.SelesOutInput;
import org.lasque.tusdk.core.view.TuSdkRelativeLayout;
import org.lasque.tusdk.impl.view.widget.TuSeekBar;
import org.lasque.tusdk.impl.view.widget.TuSeekBar.TuSeekBarDelegate;
import org.lasque.twsdkvideo.video_beauty.R;

import java.math.BigDecimal;

/**
 * 滤镜配置拖动栏
 * 
 * @author Yanlin
 */
public class FilterConfigSeekbar extends TuSdkRelativeLayout
{
	/**
	 * 滤镜配置拖动栏委托
	 * 
	 * @author Clear
	 */
	public interface FilterConfigSeekbarDelegate
	{
		/**
		 * 配置数据改变
		 * 
		 * @param seekbar
		 *            滤镜配置拖动栏
		 * @param arg
		 *            滤镜参数
		 */
		void onSeekbarDataChanged(FilterConfigSeekbar seekbar, FilterArg arg);
	}

	/**
	 * 布局ID
	 * 
	 * @return
	 */
	public static int getLayoutId()
	{
		//return R.layout.filter_config_seekbar;
		return TuSdkContext.getLayoutResId("filter_config_seekbar");
	}

	public FilterConfigSeekbar(Context context)
	{
		super(context);
	}

	public FilterConfigSeekbar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public FilterConfigSeekbar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	// 百分比控制条
	private TuSeekBarPressure mSeekbar;
	// 标题视图
	private TextView mTitleView;
	// 计数视图
	private TextView mNumberView;
	// 滤镜对象
    private FilterParameterInterface mFilter;
	// 滤镜配置参数
	private FilterArg mFilterArg;
	// 滤镜配置拖动栏委托
	private FilterConfigSeekbarDelegate mDelegate;

	/**
	 * 调节栏值
	 */
	private TextView mConfigValueView;

	/** 配置参数 */
	private ConfigViewParams.ConfigViewArg mConfigViewArg;

	// 前缀
	private String mPrefix = "lsq_beauty_";
	/**
	 * 滤镜强度值
	 */
	private TextView mFilterValueView;
	/**
	 * 百分比控制条
	 * 
	 * @return the mSeekbar
	 */
	public TuSeekBarPressure getSeekbar()
	{
		if (mSeekbar == null)
		{
			mSeekbar = this.getViewById("lsq_seekView");
			if (mSeekbar != null)
			{
				mSeekbar.setDelegate(mTuSeekBarDelegate);
			}
		}
		return mSeekbar;
	}

	/**
	 * 百分比控制条委托
	 */
	private TuSeekBarDelegate mTuSeekBarDelegate = new TuSeekBarDelegate()
	{
		/**
		 * 进度改变
		 * 
		 * @param seekBar
		 *            百分比控制条
		 * @param progress
		 *            进度百分比
		 */
		public void onTuSeekBarChanged(TuSeekBar seekBar, float progress)
		{
			onSeekbarDataChanged(progress);
		}
	};

	/**
	 * 百分比控制条数据改变
	 * 
	 * @param progress
	 */
	private void onSeekbarDataChanged(float progress)
	{
		this.setProgress(mFilterArg.getKey(),progress);

		if(this.getConfigValueView()!=null)
		{
			this.getConfigValueView().setText((int)(progress*100)+"");
		}

		if (mDelegate != null)
		{
			mDelegate.onSeekbarDataChanged(this, mFilterArg);
		}
	}

	/**
	 * 标题视图
	 * 
	 * @return the mTitleView
	 */
	public final TextView getTitleView()
	{
		if (mTitleView == null)
		{
			mTitleView = this.getViewById("lsq_titleView");
		}
		return mTitleView;
	}
	/**
	 * 滤镜强度值
	 * 
	 * @return the mFilterValueView
	 */
	public final TextView getFilterValueView()
	{
		if (mFilterValueView == null)
		{
			mFilterValueView = this.getViewById("lsq_filterValueView");
		}
		return mFilterValueView;
	}

	/**
	 * 计数视图
	 * 
	 * @return the mNumberView
	 */
	public final TextView getNumberView()
	{
		if (mNumberView == null)
		{
			mNumberView = this.getViewById("lsq_numberView");
		}
		return mNumberView;
	}

	/**
	 * 调节栏强度值
	 *
	 * @return the mConfigValueView
	 */
	public final TextView getConfigValueView()
	{
		if (mConfigValueView == null)
		{
			mConfigValueView = this.getViewById("lsq_filter_configValueView");
		}
		return mConfigValueView;
	}

	/**
	 * 滤镜配置拖动栏委托
	 * 
	 * @return the mDelegate
	 */
	public FilterConfigSeekbarDelegate getDelegate()
	{
		return mDelegate;
	}

	/**
	 * 滤镜配置拖动栏委托
	 * 
	 * @param mDelegate
	 *            the mDelegate to set
	 */
	public void setDelegate(FilterConfigSeekbarDelegate mDelegate)
	{
		this.mDelegate = mDelegate;
	}

	/**
	 * 设置调节栏配置参数（代码逻辑参考）
	 * -
	 * @param arg
	 */
	public void setConfigViewArg(ConfigViewParams.ConfigViewArg arg)
	{
		mConfigViewArg = arg;
		if (mConfigViewArg == null) return;

		TuSeekBar seekBar = this.getSeekbar();
		if (seekBar == null) return;
		seekBar.setProgress(arg.getPercentValue());

		if (this.getTitleView() != null)
		{
			this.getTitleView().setText(
					TuSdkContext.getString("lsq_congfigview_set_" + arg.getKey()));
		}
		if(this.getConfigValueView()!=null)
		{
			this.getConfigValueView().setText((int)(arg.getPercentValue()*100)+"");
		}
		this.setProgress(arg.getPercentValue());
	}

	/**
	 * 设置滤镜配置参数
	 * 
	 * @param arg
	 */
	public void setFilterArg(FilterArg arg)
	{
		mFilterArg = arg;
		if (mFilterArg == null) return;

		TuSeekBarPressure seekBar = this.getSeekbar();
		if (seekBar == null) return;
		seekBar.setDragViewBackgroundResourceId(R.drawable.tusdk_view_widget_seekbar_drag);
		//seekBar.setBottomViewBackgroundResourceId(R.drawable.tusdk_view_widget_seekbar_bottom_bg);
		seekBar.setProgress(arg.getPrecentValue());
		seekBar.setSecondProgress(arg.getDefaultValue());

		if (this.getTitleView() != null)
		{
			this.getTitleView().setText(
					TuSdkContext.getString("lsq_beauty_" + arg.getKey()));
		}
		if(this.getConfigValueView()!=null)
		{
			this.getConfigValueView().setText((int)(arg.getPrecentValue()*100)+"");
		}
		this.setProgress(arg.getKey(),arg.getPrecentValue());
		
	}


	/**
	 * 设置百分比信息
	 *
	 * @param progress
	 */
	public void setProgress(float progress)
	{
		if (mConfigViewArg != null)
		{
			mConfigViewArg.setPercentValue(progress);
		}
		getSeekbar().setProgress(progress);

		if(this.getConfigValueView()!=null)
		{
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) getConfigValueView().getLayoutParams();
			//打开之后设置progress点的位置
			if(mSeekbar.getWidth() != 0){
				layoutParams.leftMargin =(int)Math.floor((progress*(getSeekbar().getWidth()-50*progress)));
			}else {
				layoutParams.leftMargin =(int)Math.floor((progress*(TuSdkContext.getScreenSize().width-TuSdkContext.dip2px(36))));
			}


			this.getConfigValueView().setText((int)(progress*100)+"");
		}
	}

	/**
	 * 设置百分比信息
	 *
	 * @param key
	 * @param progress
	 */
	private void setProgress(String key,float progress)
	{
		if (mFilterArg != null)
		{
			mFilterArg.setPrecentValue(progress);
		}

		if (this.getNumberView() != null)
		{
			this.getNumberView().setText(
					String.format("%02d", (int) (progress * 100)));
		}

		this.setProgress(progress);

		if(this.getFilterValueView()!=null)
		{
			switch (key) {
				// 以下为改变显示进度
				case "mouthWidth":
				case "archEyebrow":
				case "jawSize":
				case "eyeAngle":
				case "eyeDis":
				case "lips":
				case "browPosition":
				case "forehead":
				case "eyeHeight":
				case "philterum":
					progress = progress - 0.5f;
					break;
			}
			BigDecimal bigDecimal = new BigDecimal(progress);
			progress = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			this.getFilterValueView().setText(String.valueOf((int) (progress * 100) + "%"));
		}
	}

	/**
	 * 重置参数
	 */
	public void reset()
	{
		if (mFilterArg == null) return;
		mFilterArg.reset();
		this.setFilterArg(mFilterArg);
	}

    /**
     * 设置滤镜
     *
     * @param filter
     */
    public void setSelesFilter(SelesOutInput filter)
    {
        if (filter == null || !(filter instanceof FilterParameterInterface))
            return;
        
       this.mFilter = (FilterParameterInterface) filter;
       
       mFilter.submitParameter();
    }

	public String getPrefix(){
		return mPrefix;
	}

	public void setPrefix(String prefix){
		this.mPrefix = prefix;
	}
}
