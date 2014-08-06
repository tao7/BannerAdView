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

import java.util.List;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
 * 广告牌视图，可以自动循环播放也可以手指左右滑动浏览。
 * 
 */
public class BannerAdView extends RelativeLayout implements
		ViewPager.OnPageChangeListener, View.OnClickListener {

	/**
	 * 默认自动切换广告的间隔时间秒数
	 */
	private static final int DEFAULT_INTERVAL = 5 * 1000;

	/**
	 * 默认的广告牌长宽比（高度除以宽度）
	 */
	private static final float DEFAULT_PAPER_RATIO = 0.4f;

	private int mFlipInterval = DEFAULT_INTERVAL;

	/**
	 * 显示广告的ViewPager
	 */
	private ViewPager mPager;

	/**
	 * 广告页面位置指示器
	 */
	private AdIndicator mIndicator;

	private OnPageChangeListener mOnPageChangeListener;

	/**
	 * 广告被点击时的回调函数
	 */
	private OnItemClickListener mOnItemClickListener;

	/**
	 * 是否自动开始循环切换广告
	 */
	private boolean mAutoStart = true;

	private boolean mRunning = false;
	private boolean mStarted = false;
	private boolean mVisible = false;
	private boolean mUserPresent = true;

	// 临时记录一下Measure的高度大小
	private int tmpHeightMeasureSpec;

	/**
	 * 显示的广告列表
	 */
	private List<AdEntity> mAdList;

	/**
	 * 处理锁屏和解锁事件，如果锁屏则暂停自动播放，反之毅然。
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mUserPresent = false;
				updateRunning();
			} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				mUserPresent = true;
				updateRunning();
			}
		}
	};

	public BannerAdView(Context context) {
		super(context);
		init();
	}

	public BannerAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BannerAdView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (isInEditMode()) {
			return;
		}

		LayoutInflater.from(getContext()).inflate(R.layout.banner_layout, this,
				true);
		mPager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (AdIndicator) findViewById(R.id.indicator);
		mPager.setOnPageChangeListener(this);
	}

	/**
	 * 设置广告列表，并从列表中第一张开始显示。
	 * 
	 * @param list
	 *            广告列表
	 */
	public void setAdList(List<AdEntity> list) {
		mAdList = list;
		mPager.setAdapter(new BillboardPagerAdapter());
		mIndicator.notifyPageCountChanged(mPager.getCurrentItem(), mPager
				.getAdapter().getCount());

	}

	/**
	 * 播放下一张广告，如果已经到了列表末尾，则回到头部重新播放。
	 */
	public void showNext() {
		if (hasAdapter()) {
			int item = (mPager.getCurrentItem() + 1)
					% mPager.getAdapter().getCount();
			mPager.setCurrentItem(item);
		}
	}

	/**
	 * 播放上一张广告，如果已经到了列表开头，则切换到列表末尾。
	 */
	public void showPrevious() {
		if (hasAdapter()) {
			int item = (mPager.getCurrentItem() - 1 + mPager.getAdapter()
					.getCount()) % mPager.getAdapter().getCount();
			mPager.setCurrentItem(item);
		}
	}

	/**
	 * mPager是否有设定Adapter
	 * 
	 * @return 有真无假
	 */
	private boolean hasAdapter() {
		return mPager.getAdapter() != null;
	}

	/**
	 * 得到当前正在显示的广告的坐标位置
	 * 
	 * @return 当前广告位置
	 */
	public int getCurrentItem() {
		return mPager.getCurrentItem();
	}

	/**
	 * 设置页面状态变化时的回调。请参见{@link OnPageChangeListener}。
	 * 
	 * @param listener
	 *            设定的回调
	 */
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	/**
	 * 自动切换到下一张广告所要等待的时间间隔（毫秒）
	 * 
	 * @param milliseconds
	 *            时间间隔
	 */
	public void setFlipInterval(int milliseconds) {
		mFlipInterval = milliseconds;
	}

	/**
	 * 开始循环播放广告，最终是否播放还与其他几个因素有关。
	 */
	public void startFlipping() {
		mStarted = true;
		updateRunning();
	}

	/**
	 * 停止循环播放广告。
	 */
	public void stopFlipping() {
		mStarted = false;
		updateRunning();
	}

	/**
	 * 如果返回真则广告正在播放中，当然还和其他几个因素有关。
	 * 
	 * @return 广告是否在播放
	 */
	public boolean isFlipping() {
		return mStarted;
	}

	/**
	 * 设置当视图{@link #onAttachedToWindow}的时候是否自动调用{@link #startFlipping()}
	 * 方法来自动开始循环播放广告。
	 * 
	 * @param autoStart
	 *            是否自动开始播放
	 */
	public void setAutoStart(boolean autoStart) {
		mAutoStart = autoStart;
	}

	/**
	 * 如果当View的{@link #onAttachedToWindow}被调用的时候会自动调用{@link #startFlipping()}
	 * 则返回true。
	 * 
	 * @return 是否自动播放
	 */
	public boolean isAutoStart() {
		return mAutoStart;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		getContext().registerReceiver(mReceiver, filter, null, mHandler);

		if (mAutoStart) {
			// 自动开始循环切换广告
			startFlipping();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		mVisible = false;
		getContext().unregisterReceiver(mReceiver);
		updateRunning();
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);

		// 当前视图是否可见，除了View.VISIBLE其他都当不可见处理。
		mVisible = visibility == VISIBLE;
		updateRunning();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		tmpHeightMeasureSpec = heightMeasureSpec;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != 0) {
			int specMode = MeasureSpec.getMode(tmpHeightMeasureSpec);
			int specSize = MeasureSpec.getSize(tmpHeightMeasureSpec);

			if (specMode == MeasureSpec.EXACTLY) {
				return;
			}

			float expectHight = getWidth() * DEFAULT_PAPER_RATIO;

			if (specMode == MeasureSpec.AT_MOST) {
				expectHight = Math.min(expectHight, specSize);
			}

			int finalHight = (int) Math.ceil(expectHight);

			if (finalHight != h) {
				ViewGroup.LayoutParams params = getLayoutParams();
				params.height = finalHight;
				setLayoutParams(params);
			}
		}
	}

	/**
	 * 设置当广告被点击时的回调
	 * 
	 * @param listener
	 *            回调
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	/**
	 * 当子页面被点击时回调的接口定义。
	 */
	public interface OnItemClickListener {
		/**
		 * 当子页面被点击时调用
		 * 
		 * @param position
		 *            被点击的页面的索引值
		 */
		public void onItemClick(int position);
	}

	@Override
	public void onClick(View v) {
		AdEntity adEntity = mAdList.get(mPager.getCurrentItem());
		adEntity.responseClick();
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(mPager.getCurrentItem());
		}
	}

	private final int FLIP_MSG = 1;

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FLIP_MSG) {
				if (mRunning) {
					showNext();
					msg = obtainMessage(FLIP_MSG);
					sendMessageDelayed(msg, mFlipInterval);
				}
			}
		}
	};

	/**
	 * 用来启动或者停止发送自动切换广告的{@link Message}。
	 */
	private void updateRunning() {
		boolean running = mVisible && mStarted && mUserPresent;
		if (running != mRunning) {
			if (running) {
				Message msg = mHandler.obtainMessage(FLIP_MSG);
				mHandler.sendMessageDelayed(msg, mFlipInterval);
			} else {
				mHandler.removeMessages(FLIP_MSG);
			}
			mRunning = running;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		mIndicator.onPageScrollStateChanged(state);
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrollStateChanged(state);
		}
		switch (state) {
		case ViewPager.SCROLL_STATE_DRAGGING:
			stopFlipping();
			break;
		default:
			startFlipping();
			break;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		mIndicator.onPageScrolled(position, positionOffset,
				positionOffsetPixels);
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		mIndicator.onPageSelected(position);
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onPageSelected(position);
		}
	}

	private class BillboardPagerAdapter extends PagerAdapter {
		// TODO 图片很多、很大时候需要改为循环利用对象，类似ListView。并且对大图压缩处理。
		private SparseArray<ImageView> imageViews = new SparseArray<ImageView>();

		@Override
		public int getCount() {
			return mAdList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView localImageView = imageViews.get(position);

			if (localImageView == null) {
				localImageView = new ImageView(BannerAdView.this.getContext());
				localImageView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				localImageView.setImageBitmap(mAdList.get(position)
						.getAdBitmap());
				localImageView.setScaleType(ScaleType.FIT_XY);
				localImageView.setOnClickListener(BannerAdView.this);
				imageViews.put(position, localImageView);
			}

			container.addView(localImageView);

			return localImageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(imageViews.get(position));
		}
	}
}
