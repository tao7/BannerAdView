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

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 用来定义广告类型，所有广告都必须包含的方法。
 * 
 */
public abstract class AdEntity {
	protected Context mContext;

	public AdEntity(Context context) {
		this.mContext = context;
	}

	final Context getContext() {
		return mContext;
	}

	/**
	 * 用来得到广告展示的图片，返回的图片会在{@link BannerAdView}中进行展示。
	 * 
	 * @return 广告图片
	 */
	public abstract Bitmap getAdBitmap();

	/**
	 * 返回广告的标题
	 * 
	 * @return 广告标题
	 */
	public String getAdTitle() {
		return "";
	}

	/**
	 * 返回广告的编号，用来唯一确定一条广告。
	 * <p>
	 * 编号可以用在形如统计广告点击次数的地方。
	 * 
	 * @return 广告id编号
	 */
	public String getAdId() {
		return null;
	}

	/**
	 * 当{@link BannerAdView}中的广告被点击时，被点击的广告的本方法会被回调，用来执行特定的任务。
	 */
	public void responseClick() {

	}
}
