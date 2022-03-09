
package org.lasque.twsdkvideo.video_beauty.effectcamera.views.record;

import android.graphics.PointF;

/**
 * 聚焦区域视图接口
 * 
 * @author Clear
 */
public interface TuFocusRangeViewInterface
{
	/**
	 * 设置显示位置
	 * 
	 * @param lastPoint
	 */
	public void setPosition(PointF lastPoint);

	/**
	 * 设置聚焦状态
	 * 
	 * @param success
	 */
	public void setFoucsState(boolean success);
}
