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
import android.graphics.BitmapFactory;

/**
 * 空白广告，点击广告图片后什么也不做，仅仅展示一张图片。
 * <p>
 * // TODO 修改为从网络加载
 * 
 */
public class EmptyAd extends AdEntity {
	// TODO 修改为从网络加载图片，暂时测试用
	private static final int DEFUALT_IMAGE = R.drawable.advert00; // 图片来源于网络

	public EmptyAd(Context context) {
		super(context);
	}

	@Override
	public Bitmap getAdBitmap() {
		return BitmapFactory.decodeResource(mContext.getResources(),
				DEFUALT_IMAGE);
	}

}
