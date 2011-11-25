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

package com.scoreloop.client.android.ui.component.market;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.scoreloop.client.android.core.controller.GamesController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.Game;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentHeaderActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.TrackerEvents;
import com.scoreloop.client.android.ui.framework.ValueStore;
import com.scoreloop.client.android.ui.framework.ValueStore.Observer;
import com.scoreloop.client.android.ui.util.ImageDownloader;

public class MarketHeaderActivity extends ComponentHeaderActivity implements OnClickListener, Observer {

	private GamesController	_gamesController;

	@Override
	public void onClick(final View view) {
		final Game featuredGame = getScreenValues().getValue(Constant.FEATURED_GAME);
		if (featuredGame != null) {
			getTracker().trackEvent(TrackerEvents.CAT_NAVI, TrackerEvents.NAVI_HEADER_GAME_FEATURED, featuredGame.getName(), 0);
			display(getFactory().createGameDetailScreenDescription(featuredGame));
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.sl_header_market);
		setTitle(getString(R.string.sl_market));
		setSubTitle(getString(R.string.sl_market_description));
		getImageView().setImageResource(R.drawable.sl_header_icon_market);
		addObservedKeys(Constant.FEATURED_GAME, Constant.FEATURED_GAME_NAME, Constant.FEATURED_GAME_IMAGE_URL,
				Constant.FEATURED_GAME_PUBLISHER);
		_gamesController = new GamesController(getRequestControllerObserver());
		_gamesController.setRangeLength(1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		_gamesController.loadRangeForFeatured();
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (oldValue != newValue) {
			if (key.equals(Constant.FEATURED_GAME_IMAGE_URL)) {
				ImageDownloader.downloadImage((String) newValue, getResources().getDrawable(R.drawable.sl_header_icon_market),
						getImageView(), null);
			} else if (key.equals(Constant.FEATURED_GAME_NAME)) {
				setTitle((String) newValue);
			} else if (key.equals(Constant.FEATURED_GAME_PUBLISHER)) {
				setSubTitle((String) newValue);
			}
		}
	}

	@Override
	public void onValueSetDirty(final ValueStore valueStore, final String key) {
		if (Constant.FEATURED_GAME.equals(key)) {
			getScreenValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		} else if (Constant.FEATURED_GAME_IMAGE_URL.equals(key)) {
			getScreenValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		} else if (Constant.FEATURED_GAME_NAME.equals(key)) {
			getScreenValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		} else if (Constant.FEATURED_GAME_PUBLISHER.equals(key)) {
			getScreenValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		final List<Game> featuredGames = _gamesController.getGames();
		if (featuredGames.size() > 0) {
			final ValueStore store = getScreenValues();
			final Game game = featuredGames.get(0);
			store.putValue(Constant.FEATURED_GAME, game);
			store.putValue(Constant.FEATURED_GAME_NAME, game.getName());
			store.putValue(Constant.FEATURED_GAME_PUBLISHER, game.getPublisherName());
			store.putValue(Constant.FEATURED_GAME_IMAGE_URL, game.getImageUrl());
			showControlIcon(R.drawable.sl_button_arrow);
		}
	}

	private void showControlIcon(final int resId) {
		final ImageView icon = (ImageView) findViewById(R.id.sl_control_icon);
		icon.setImageResource(resId);
		findViewById(R.id.sl_header_layout).setOnClickListener(this);
	}
}
