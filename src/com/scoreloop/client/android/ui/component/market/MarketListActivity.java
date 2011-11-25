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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class MarketListActivity extends ComponentListActivity<BaseListItem> implements ValueStore.Observer {

	class SocialMarketListAdapter extends BaseListAdapter<BaseListItem> {

		public SocialMarketListAdapter(final Context context) {
			super(context);
			add(new CaptionListItem(context, null, context.getString(R.string.sl_market)));
			add(_newGamesItem);
			add(_popularGamesItem);
			add(_buddiesGamesItem);
			add(new CaptionListItem(context, null, context.getString(R.string.sl_playing)));
			add(_myGamesItem);
		}
	}

	private MarketListItem	_buddiesGamesItem;
	private MarketListItem	_myGamesItem;
	private MarketListItem	_newGamesItem;
	private MarketListItem	_popularGamesItem;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Resources res = getResources();
		_myGamesItem = new MarketListItem(this, res.getDrawable(R.drawable.sl_icon_games), getString(R.string.sl_my_games),
				getString(R.string.sl_games_subtitle));
		_myGamesItem.setCounter(getSessionUser().getGamesCounter());
		_popularGamesItem = new MarketListItem(this, res.getDrawable(R.drawable.sl_icon_market), getString(R.string.sl_popular_games),
				getString(R.string.sl_popular_games_subtitle));
		_newGamesItem = new MarketListItem(this, res.getDrawable(R.drawable.sl_icon_market), getString(R.string.sl_new_games),
				getString(R.string.sl_new_games_subtitle));
		_buddiesGamesItem = new MarketListItem(this, res.getDrawable(R.drawable.sl_icon_market), getString(R.string.sl_friends_games),
				getString(R.string.sl_friends_games_subtitle));
		setListAdapter(new SocialMarketListAdapter(this));
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		final Factory factory = getFactory();
		final User user = getUser();
		if (item == _myGamesItem) {
			display(factory.createGameScreenDescription(user, Constant.GAME_MODE_USER));
		} else if (item == _popularGamesItem) {
			display(factory.createGameScreenDescription(user, Constant.GAME_MODE_POPULAR));
		} else if (item == _newGamesItem) {
			display(factory.createGameScreenDescription(user, Constant.GAME_MODE_NEW));
		} else if (item == _buddiesGamesItem) {
			display(factory.createGameScreenDescription(user, Constant.GAME_MODE_BUDDIES));
		}
	}

}
