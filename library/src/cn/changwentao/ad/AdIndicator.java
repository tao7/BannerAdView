/*
 * Copyright (C) 2014 Chang Wentao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.changwentao.ad;

import android.support.v4.view.ViewPager;

/**
 * AdIndicator指示器用来指示广告牌（{@link BannerAdView}）广告的总数量和当前显示的广告。
 * 
 */
public interface AdIndicator {

	/**
	 * 当前页面滚动的时候本方法会被调用，无论是包含动画的自动滚动还是用户主动的触摸滑动。
	 * 
	 * @param position
	 *            正显示的第一个页面位置，如果positionOffset>0下一个显示的页面将是position+1。
	 * @param positionOffset
	 *            [0, 1)之间的值用来表明position处页面的偏移。
	 * @param positionOffsetPixels
	 *            position处页面偏移的像素值
	 */
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels);

	/**
	 * 当新的页面被选中的时候本方法会被调用。
	 * 
	 * @param position
	 *            新选中页面的位置。
	 */
	public void onPageSelected(int position);

	/**
	 * 当滚动的状态改变的时候被调用。
	 * 
	 * @param state
	 *            滚动状态。
	 * @see ViewPager#SCROLL_STATE_IDLE
	 * @see ViewPager#SCROLL_STATE_DRAGGING
	 * @see ViewPager#SCROLL_STATE_SETTLING
	 */
	public void onPageScrollStateChanged(int state);

	/**
	 * 当页面的数量发生变化时，本方法会被调用。同时当前选中的页面位置也可能发生变化。子类可以在这里更新指示器的数量。
	 * 
	 * @param current
	 *            当前选中的页面位置。
	 * @param PageCount
	 *            页面的总数量。
	 */
	public void notifyPageCountChanged(int current, int pageCount);
}
