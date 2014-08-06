package cn.changwentao.ad;

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

import java.net.MalformedURLException;
import java.net.URL;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 */
public class WebImageView extends ImageView {
	public WebImageView(Context context) {
		super(context);
	}

	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void showImageFromUrl(String url) throws MalformedURLException {
		URL localUrl = new URL(url);
		showImageFromUrl(localUrl);
	}

	public void showImageFromUrl(URL url) {

	}
}
