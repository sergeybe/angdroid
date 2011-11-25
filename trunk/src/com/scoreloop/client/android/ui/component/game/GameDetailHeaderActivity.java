/*
 * In derogation of the Scoreloop SDK - License Agreement concluded between
 * Licensor and Licensee, as defined therein, the following conditions shall
 * apply for the source code contained below, whereas apart from that the
 * Scoreloop SDK - License Agreement shall remain unaffected.
 * 
 * Copyright: Scoreloop AG, Germany (Licensor)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.scoreloop.client.android.ui.component.game;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.scoreloop.client.android.core.model.Game;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentHeaderActivity;
import com.scoreloop.client.android.ui.component.base.PackageManager;
import com.scoreloop.client.android.ui.component.base.TrackerEvents;
import com.scoreloop.client.android.ui.util.ImageDownloader;

public class GameDetailHeaderActivity extends ComponentHeaderActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.sl_header_game);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setNeedsRefresh();
	}

	@Override
	public void onRefresh(final int flags) {
		final Button controlButton = (Button) findViewById(R.id.sl_control_button);
		final Game game = getGame();
		ImageDownloader.downloadImage(game.getImageUrl(), getResources().getDrawable(R.drawable.sl_icon_games_loading), getImageView(), null);
		setTitle(game.getName());
		setSubTitle(game.getPublisherName());
		if (game.getPackageNames() != null) {
			if (PackageManager.isGameInstalled(this, game)) {
				controlButton.setText(getString(R.string.sl_launch));
				controlButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						getTracker().trackEvent(TrackerEvents.CAT_NAVI, TrackerEvents.NAVI_HEADER_GAME_LAUNCH, game.getName(), 0);
						PackageManager.launchGame(GameDetailHeaderActivity.this, game);
					}
				});
			} else {
				controlButton.setText(getString(R.string.sl_get));
				controlButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						getTracker().trackEvent(TrackerEvents.CAT_NAVI, TrackerEvents.NAVI_HEADER_GAME_GET, game.getName(), 0);
						PackageManager.installGame(GameDetailHeaderActivity.this, game);
					}
				});
			}
			controlButton.setVisibility(View.VISIBLE);
		} else {
			controlButton.setVisibility(View.GONE);
		}
	}

}
