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

import java.util.List;

import android.os.Bundle;
import android.widget.ListView;

import com.scoreloop.client.android.core.controller.GamesController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Game;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.EmptyListItem;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.PagingDirection;
import com.scoreloop.client.android.ui.framework.PagingListAdapter;

public class GameListActivity extends ComponentListActivity<GameListItem> implements RequestControllerObserver,
		PagingListAdapter.OnListItemClickListener<GameListItem> {

	private GamesController	_gamesController;
	private int				_mode;
	private PagingDirection	_pagingDirection;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new PagingListAdapter<BaseListItem>(this, 1));
		_mode = getActivityArguments().<Integer> getValue(Constant.MODE);

		final int optimalRangeLength = Constant.getOptimalRangeLength(getListView(), new StandardListItem<GameListItem>(this, null,
				"title", "subtitle", null));

		_gamesController = new GamesController(this);
		_gamesController.setRangeLength(optimalRangeLength);
		if (_mode == Constant.GAME_MODE_BUDDIES) {
			_gamesController.setLoadsDevicesPlatformOnly(false);
		}
		setNeedsRefresh(PagingDirection.PAGE_TO_TOP);
	}

	private void setNeedsRefresh(final PagingDirection pagingDirection) {
		_pagingDirection = pagingDirection;
		setNeedsRefresh();
	}

	@SuppressWarnings("unchecked")
	private PagingListAdapter<GameListItem> getPagingListAdapter() {
		final BaseListAdapter<?> baseAdapter = getBaseListAdapter();
		return (PagingListAdapter<GameListItem>) baseAdapter;
	}

	private void onGames(final List<Game> games) {
		final PagingListAdapter<GameListItem> adapter = getPagingListAdapter();
		adapter.clear();

		// add caption
		int id = 0;
		switch (_mode) {
		case Constant.GAME_MODE_USER:
			id = isSessionUser() ? R.string.sl_my_games : R.string.sl_games;
			break;
		case Constant.GAME_MODE_POPULAR:
			id = R.string.sl_popular_games;
			break;
		case Constant.GAME_MODE_NEW:
			id = R.string.sl_new_games;
			break;
		case Constant.GAME_MODE_BUDDIES:
			id = R.string.sl_friends_games;
			break;
		default:
			id = R.string.sl_games;
			break;
		}
		adapter.add(new CaptionListItem(this, null, getString(id)));

		// fill adapter with games
		for (final Game game : games) {
			adapter.add(new GameListItem(this, getResources().getDrawable(R.drawable.sl_icon_games_loading), game));
		}
		if (games.size() == 0) {
			adapter.add(new EmptyListItem(this, getString(R.string.sl_no_games)));
		} else {
			// fill adapter with paging navigators
			final boolean showTop = _gamesController.hasPreviousRange();
			adapter.addPagingItems(showTop, showTop, _gamesController.hasNextRange());

			// scroll to top or bottom (depending on paging direction).
			// for setSelection() to have any effect, according to a google developer, it should be called through a view.post()
			final ListView listView = getListView();
			listView.post(new Runnable() {
				public void run() {
					if (_pagingDirection == PagingDirection.PAGE_TO_TOP) {
						listView.setSelection(0);
					} else if (_pagingDirection == PagingDirection.PAGE_TO_NEXT) {
						listView.setSelection(adapter.getFirstContentPosition());
					} else if (_pagingDirection == PagingDirection.PAGE_TO_PREV) {
						listView.setSelectionFromTop(adapter.getLastContentPosition() + 1, listView.getHeight());
					}
				}
			});
		}
	}

	@Override
	public void onListItemClick(final GameListItem item) {
		if (item.getType() == Constant.LIST_ITEM_TYPE_GAME) {
			display(getFactory().createGameDetailScreenDescription(item.getTarget()));
		}
	}

	@Override
	public void onRefresh(final int flags) {
		showSpinnerFor(_gamesController);

		switch (_pagingDirection) {
		case PAGE_TO_TOP:
			switch (_mode) {
			case Constant.GAME_MODE_USER:
				onRefreshUser();
				break;
			case Constant.GAME_MODE_POPULAR:
				onRefreshPopular();
				break;
			case Constant.GAME_MODE_NEW:
				onRefreshNew();
				break;
			case Constant.GAME_MODE_BUDDIES:
				onRefreshBuddies();
				break;
			}
			break;

		case PAGE_TO_PREV:
			_gamesController.loadPreviousRange();
			break;

		case PAGE_TO_NEXT:
			_gamesController.loadNextRange();
			break;
		}

	}

	private void onRefreshBuddies() {
		showSpinnerFor(_gamesController);
		_gamesController.loadRangeForBuddies();
	}

	private void onRefreshNew() {
		showSpinnerFor(_gamesController);
		_gamesController.loadRangeForNew();
	}

	private void onRefreshPopular() {
		showSpinnerFor(_gamesController);
		_gamesController.loadRangeForPopular();
	}

	private void onRefreshUser() {
		showSpinnerFor(_gamesController);
		_gamesController.loadRangeForUser(getUser());
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		if (aRequestController == _gamesController) {
			onGames(_gamesController.getGames());
		}
	}

	@Override
	public void onPagingListItemClick(final PagingDirection pagingDirection) {
		setNeedsRefresh(pagingDirection);
	}

}
