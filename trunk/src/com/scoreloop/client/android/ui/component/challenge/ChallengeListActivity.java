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

package com.scoreloop.client.android.ui.component.challenge;

import java.util.List;

import android.os.Bundle;

import com.scoreloop.client.android.core.controller.ChallengesController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.Challenge;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.EmptyListItem;
import com.scoreloop.client.android.ui.component.base.ExpandableListItem;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class ChallengeListActivity extends ComponentListActivity<BaseListItem> {

	private List<User>				_buddies;
	private ExpandableListItem		_expandableHistoryListItem;
	private ExpandableListItem		_expandableOpenListItem;
	private List<Challenge>			_history;
	private ChallengesController	_historyController;
	private List<Challenge>			_open;
	private ChallengesController	_openController;
	private boolean					_showPrize;
	private boolean					_showSeeMoreHistory	= true;
	private boolean					_showSeeMoreOpen	= true;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new BaseListAdapter<BaseListItem>(this));

		_openController = new ChallengesController(getRequestControllerObserver());
		_historyController = new ChallengesController(getRequestControllerObserver());

		addObservedKeys(ValueStore.concatenateKeys(Constant.SESSION_USER_VALUES, Constant.USER_BUDDIES));
	}

	@Override
	public void onListItemClick(final BaseListItem item) {

		final Factory factory = getFactory();
		if (item == _expandableHistoryListItem) {
			_showSeeMoreHistory = false;
			updateListIfReady();
		} else if (item == _expandableOpenListItem) {
			_showSeeMoreOpen = false;
			updateListIfReady();
		} else if (item.getType() == Constant.LIST_ITEM_TYPE_CHALLENGE_OPEN) {
			display(factory.createChallengeAcceptScreenDescription(((ChallengeOpenListItem) item).getTarget()));
		} else if (item.getType() == Constant.LIST_ITEM_TYPE_CHALLENGE_NEW) {
			display(factory.createChallengeCreateScreenDescription(((ChallengeCreateListItem) item).getTarget(), null));
		} else if (item.getType() == Constant.LIST_ITEM_TYPE_CHALLENGE_HISTORY) {
			_showPrize = !_showPrize;
			final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
			for (int i = 0; i < adapter.getCount(); i++) {
				final BaseListItem it = adapter.getItem(i);
				if (it.getType() == Constant.LIST_ITEM_TYPE_CHALLENGE_HISTORY) {
					((ChallengeHistoryListItem) it).setShowPrize(_showPrize);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRefresh(final int flags) {
		showSpinnerFor(_openController);
		_openController.loadOpenChallenges();

		showSpinnerFor(_historyController);
		_historyController.loadChallengeHistory();
	}

	@Override
	public void onStart() {
		super.onStart();

		setNeedsRefresh();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (newValue instanceof List<?>) {
			_buddies = (List<User>) newValue;
			updateListIfReady();
		}
	}

	@Override
	public void onValueSetDirty(final ValueStore valueStore, final String key) {
		getSessionUserValues().retrieveValue(Constant.USER_BUDDIES, ValueStore.RetrievalMode.NOT_DIRTY, null);
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		if (aRequestController == _historyController) {
			_history = _historyController.getChallenges();
		} else if (aRequestController == _openController) {
			_open = _openController.getChallenges();
		}
		updateListIfReady();
	}

	private void updateListIfReady() {
		if ((_open == null) || (_history == null) || (_buddies == null)) {
			return;
		}

		final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
		adapter.clear();

		adapter.add(new CaptionListItem(ChallengeListActivity.this, null, getString(R.string.sl_open_challenges)));
		if (_open.size() > 0) {
			int i = 0;
			for (final Challenge challenge : _open) {
				if (_showSeeMoreOpen && (++i > ExpandableListItem.COLLAPSED_LIMIT)) {
					_expandableOpenListItem = new ExpandableListItem(this);
					adapter.add(_expandableOpenListItem);
					break;
				}
				adapter.add(new ChallengeOpenListItem(this, challenge));
			}
		} else {
			adapter.add(new EmptyListItem(this, getResources().getString(R.string.sl_no_open_challenges)));
		}

		adapter.add(new CaptionListItem(ChallengeListActivity.this, null, getString(R.string.sl_challenges_history)));
		if (_history.size() > 0) {
			int i = 0;
			boolean addedItem = false;
			for (final Challenge challenge : _history) {
				if (challenge.isComplete() || challenge.isOpen() || challenge.isAssigned() || challenge.isRejected()
						|| challenge.isAccepted()) {
					addedItem = true;
					if (_showSeeMoreHistory && (++i > ExpandableListItem.COLLAPSED_LIMIT)) {
						_expandableHistoryListItem = new ExpandableListItem(this);
						adapter.add(_expandableHistoryListItem);
						break;
					}
					adapter.add(new ChallengeHistoryListItem(this, challenge, _showPrize));
				}
			}
			if (!addedItem) {
				adapter.add(new EmptyListItem(this, getResources().getString(R.string.sl_no_history_challenges)));
			}
		} else {
			adapter.add(new EmptyListItem(this, getResources().getString(R.string.sl_no_history_challenges)));
		}

		adapter.add(new CaptionListItem(ChallengeListActivity.this, null, getString(R.string.sl_new_challenge)));

		adapter.add(new ChallengeCreateListItem(this, null));
		for (final User user : _buddies) {
			adapter.add(new ChallengeCreateListItem(this, user));
		}
	}
}
