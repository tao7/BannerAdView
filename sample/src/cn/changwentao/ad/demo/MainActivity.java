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

package cn.changwentao.ad.demo;

import java.util.ArrayList;
import java.util.List;
import cn.changwentao.ad.AdEntity;
import cn.changwentao.ad.EmptyAd;
import cn.changwentao.ad.ShowUrlAd;
import cn.changwentao.ad.ShowTostAd;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import cn.changwentao.ad.BannerAdView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		List<AdEntity> list = new ArrayList<AdEntity>();
		list.add(new EmptyAd(this));
		list.add(new ShowUrlAd(this));
		list.add(new ShowTostAd(this));

		((BannerAdView) findViewById(R.id.bannerAdView)).setAdList(list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_about) {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("关于");
			builder.setMessage("BannerAdView Sample");
			builder.setPositiveButton("确定", null).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
