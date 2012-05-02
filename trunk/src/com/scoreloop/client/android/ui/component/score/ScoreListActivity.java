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

package com.scoreloop.client.android.ui.component.score;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;

import com.scoreloop.client.android.core.controller.RankingController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.ScoresController;
import com.scoreloop.client.android.core.model.Ranking;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.SearchList;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.EmptyListItem;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.PagingDirection;
import com.scoreloop.client.android.ui.framework.PagingListAdapter;
import com.scoreloop.client.android.ui.framework.ValueStore;
import com.scoreloop.client.android.ui.framework.ValueStore.Observer;

public class ScoreListActivity extends ComponentListActivity<ScoreListItem> implements Observer,
PagingListAdapter.OnListItemClickListener<ScoreListItem> {

	private static final String	RECENT_TOP_RANK				= "recentTopRank";

	private int					_cachedVerticalCenterOffset	= -1;
	private int					_highlightedPosition;
	private PagingDirection		_pagingDirection;
	private RankingController	_rankingController;
	private ScoresController	_scoresController;
	private SearchList			_searchList;
	private BaseListItem		_submitLocalScoresListItem;

	@SuppressWarnings("unchecked")
	private PagingListAdapter<ScoreListItem> getPagingListAdapter() {
		final BaseListAdapter<?> baseAdapter = getBaseListAdapter();
		return (PagingListAdapter<ScoreListItem>) baseAdapter;
	}

	private int getVerticalCenterOffset() {
		if (_cachedVerticalCenterOffset == -1) {
			final ScoreListItem item = getPagingListAdapter().getContentItem(_highlightedPosition);
			final View itemView = item.getView(null, null);
			itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			final int listHeight = getListView().getHeight();
			final int itemHeight = itemView.getMeasuredHeight();
			_cachedVerticalCenterOffset = (listHeight - itemHeight) / 2;
		}
		return _cachedVerticalCenterOffset;
	}

	private boolean isHighlightedScore(final Score score) {
		if (_searchList == SearchList.getLocalScoreSearchList()) {
			return false;
		}
		final User user = score.getUser();
		return (user != null) && getSession().isOwnedByUser(user);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new PagingListAdapter<ScoreListItem>(this));

		_searchList = getActivityArguments().getValue(Constant.SEARCH_LIST, SearchList.getDefaultScoreSearchList());

		addObservedKeys(Constant.MODE);
		if (_searchList.equals(SearchList.getBuddiesScoreSearchList())) {
			addObservedKeys(Constant.NUMBER_BUDDIES);
		}

		_scoresController = new ScoresController(getRequestControllerObserver());
		_scoresController.setRangeLength(Constant
				.getOptimalRangeLength(getListView(), new ScoreListItem(this, new Score(0.0, null), false)));
		_scoresController.setSearchList(_searchList);

		if (showsLocalSearchList()) {
			_submitLocalScoresListItem = new ScoreSubmitLocalListItem(this);
		} else {
			_rankingController = new RankingController(getRequestControllerObserver());
			_rankingController.setSearchList(_searchList);
		}
	}

	@Override
	protected void onFooterItemClick(final BaseListItem footerItem) {
		if (footerItem == _submitLocalScoresListItem) {
			showSpinner();
			getManager().submitLocalScores(new Runnable() {
				@Override
				public void run() {
					// refresh the UI, this will hide the footer if necessary
					hideSpinner();
					setNeedsRefresh(PagingDirection.PAGE_TO_RECENT);
				}
			});
		} else if (footerItem.getType() == Constant.LIST_ITEM_TYPE_SCORE_HIGHLIGHTED) {
			setNeedsRefresh(PagingDirection.PAGE_TO_OWN);
		}
	}

	@Override
	public void onListItemClick(final ScoreListItem item) {
		Score score = item.getTarget();
		Double result = score.getResult();
		HashMap context = (HashMap)score.getContext();
		
		// Log.d("Angband", "result = " + score.getResult());
		// Log.d("Angband", "level = " + score.getLevel());
		
		String msg = "";
		msg += context.get("name") + ", a level ";
		msg += context.get("cur_lev") + " ";
		msg += context.get("race") + " ";
		msg += context.get("class") + " with ";
		msg += context.get("gold") + " gold, was killed on dungeon level ";
		msg += context.get("cur_dun") + " by ";
		msg += context.get("how_died") + ".";

		Activity a = this;
		while (a.getParent() != null)
			a = a.getParent();

		new AlertDialog.Builder(a)
			.setTitle("Result") 
			.setMessage(msg) 
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
		final Factory factory = getFactory();
		final User user = item.getTarget().getUser();

		if (getSession().isOwnedByUser(user)) {
			display(factory.createProfileSettingsScreenDescription(user));
		} else {
			display(factory.createUserDetailScreenDescription(user, true));
		}
	}
				}).show();
	}

	@Override
	public void onPagingListItemClick(final PagingDirection pagingDirection) {
		setNeedsRefresh(pagingDirection);
	}

	private void onRanking() {
		final PagingListAdapter<ScoreListItem> adapter = getPagingListAdapter();

		final Ranking ranking = _rankingController.getRanking();
		final Integer rank = ranking.getRank();

		// update the footer
		if (rank != null) {

			// if we have a highlighted position, update its ranking and refresh the list
			if (_highlightedPosition != -1) {
				final ScoreHighlightedListItem highlightedItem = (ScoreHighlightedListItem) adapter.getContentItem(_highlightedPosition);
				highlightedItem.setRanking(ranking);
				adapter.notifyDataSetChanged();
			}

			// otherwise show the score corresponding to the ranking in the footer
			else {
				final Score score = ranking.getScore();
				if (score != null) {
					showFooter(new ScoreHighlightedListItem(this, score, ranking));
				}
			}
		} else {
			// if no rank found, inform user via
			showFooter(new ScoreExcludedListItem(this));
		}

		// update the scrolling
		updateScrollPosition();
	}

	@Override
	public void onRefresh(final int flags) {
		showSpinnerFor(_scoresController);
		if (getSession().getGame().hasModes()) {
			_scoresController.setMode(getScreenValues().<Integer> getValue(Constant.MODE));
		} else {
			_scoresController.setMode(null);
		}

		switch (_pagingDirection) {
		case PAGE_TO_TOP:
			_scoresController.loadRangeAtRank(1);
			break;

		case PAGE_TO_RECENT:
			final Integer recentTopRank = getActivityArguments().getValue(RECENT_TOP_RANK, 1);
			_scoresController.loadRangeAtRank(recentTopRank);
			break;

		case PAGE_TO_PREV:
			_scoresController.loadPreviousRange();
			break;

		case PAGE_TO_NEXT:
			_scoresController.loadNextRange();
			break;

		case PAGE_TO_OWN:
			_scoresController.loadRangeForUser(getSessionUser());
			break;
		}
	}

	private void onScores() {
		final PagingListAdapter<ScoreListItem> adapter = getPagingListAdapter();
		adapter.clear();

		// fill adapter with scores
		final List<Score> scores = _scoresController.getScores();
		final int scoreCount = scores.size();
		final boolean clickable = !showsLocalSearchList();
		for (int i = 0; i < scoreCount; ++i) {
			final Score score = scores.get(i);
			if (isHighlightedScore(score)) {
				_highlightedPosition = i;
				adapter.add(new ScoreHighlightedListItem(this, score, null));
			} else {
				adapter.add(new ScoreListItem(this, score, clickable));
			}
		}
		Integer recentTopRank;
		if (scoreCount == 0) {
			adapter.add(new EmptyListItem(this, getString(R.string.sl_no_scores)));
			recentTopRank = 1;
		} else {
			recentTopRank = scores.get(0).getRank();
		}
		getActivityArguments().putValue(RECENT_TOP_RANK, recentTopRank);

		// fill adapter with paging navigators
		final boolean hasPreviousRange = _scoresController.hasPreviousRange();
		adapter.addPagingItems(hasPreviousRange, hasPreviousRange, _scoresController.hasNextRange());

		// load/gather additional data
		if (showsLocalSearchList()) {
			hideFooter();
			new ScoresController(new RequestControllerObserver() {
				@Override
				public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
				}

				@Override
				public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
					if (aRequestController instanceof ScoresController) {
						final ScoresController scoresController = (ScoresController) aRequestController;
						final List<Score> scoreList = scoresController.getScores();
						if (!scoreList.isEmpty()) {
							// update the footer
							showFooter(_submitLocalScoresListItem);
						}
					}
				}
			}).loadLocalScoresToSubmit();

			// update the scrolling
			updateScrollPosition();
		} else {
			// load the rank for the user
			final Integer mode = getGame().hasModes() ? (Integer) getScreenValues().getValue(Constant.MODE) : null;
			_rankingController.loadRankingForUserInGameMode(getUser(), mode);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		setNeedsRefresh(PagingDirection.PAGE_TO_RECENT);
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (isValueChangedFor(key, Constant.MODE, oldValue, newValue)) {
			if (oldValue == null) {
				// NOTE: this is a bit obscure but handles the case where the activity is recreated
				setNeedsRefresh(PagingDirection.PAGE_TO_RECENT);
			} else {
				setNeedsRefresh(PagingDirection.PAGE_TO_TOP);
			}
		} else if (isValueChangedFor(key, Constant.NUMBER_BUDDIES, oldValue, newValue)) {
			setNeedsRefresh(PagingDirection.PAGE_TO_TOP);
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		if (aRequestController == _scoresController) {
			onScores();
		} else if (aRequestController == _rankingController) {
			onRanking();
		}
	}

	private void setNeedsRefresh(final PagingDirection pagingDirection) {
		_highlightedPosition = -1;
		_pagingDirection = pagingDirection;
		hideFooter();
		setNeedsRefresh();
	}

	private boolean showsLocalSearchList() {
		return _searchList == SearchList.getLocalScoreSearchList();
	}

	private void updateScrollPosition() {

		// if _highlightedPosition is valid, scroll to that position, otherwise to top or bottom (depending on paging direction).
		getListView().post(new Runnable() {
			@Override
			public void run() {
				final PagingListAdapter<ScoreListItem> adapter = getPagingListAdapter();
				final ListView listView = getListView();

				if (_highlightedPosition != -1) {
					listView.setSelectionFromTop(_highlightedPosition + adapter.getFirstContentPosition(), getVerticalCenterOffset());
				} else {
					if (_pagingDirection == PagingDirection.PAGE_TO_TOP) {
						listView.setSelection(0);
					} else if (_pagingDirection == PagingDirection.PAGE_TO_NEXT) {
						listView.setSelection(adapter.getFirstContentPosition());
					} else if (_pagingDirection == PagingDirection.PAGE_TO_PREV) {
						listView.setSelectionFromTop(adapter.getLastContentPosition() + 1, listView.getHeight());
					}
				}
			}
		});
	}
}
