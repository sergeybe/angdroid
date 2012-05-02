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

import android.os.Bundle;

import com.scoreloop.client.android.core.controller.ChallengeController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Money;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.agent.UserDetailsAgent;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.component.challenge.ChallengeControlsListItem.OnControlObserver;
import com.scoreloop.client.android.ui.framework.BaseDialog;
import com.scoreloop.client.android.ui.framework.BaseDialog.OnActionListener;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class ChallengeCreateListActivity extends ChallengeActionListActivity implements RequestControllerObserver, OnActionListener,
		OnControlObserver {

	private ChallengeControlsListItem		_challengeControlsListItem;
	private ChallengeParticipantsListItem	_challengeParticipantsListItem;
	private ChallengeSettingsEditListItem	_challengeStakeAndModeEditListItem;
	private User							_contestant;
	private ValueStore						_opponentValueStore;

	@Override
	CaptionListItem getCaptionListItem() {
		return new CaptionListItem(ChallengeCreateListActivity.this, null, getString(R.string.sl_new_challenge));
	}

	@Override
	ChallengeControlsListItem getChallengeControlsListItem() {
		_challengeControlsListItem = new ChallengeControlsListItem(this, null, this);
		return _challengeControlsListItem;
	}

	@Override
	ChallengeParticipantsListItem getChallengeParticipantsListItem() {
		if (_challengeParticipantsListItem == null) {
			_challengeParticipantsListItem = new ChallengeParticipantsListItem(this, getUser(), _contestant);
		}
		return _challengeParticipantsListItem;
	}

	@Override
	ChallengeSettingsListItem getChallengeStakeAndModeListItem() {
		_challengeStakeAndModeEditListItem = new ChallengeSettingsEditListItem(this);
		return _challengeStakeAndModeEditListItem;
	}

	@Override
	public void onAction(final BaseDialog dialog, final int actionId) {
		dialog.dismiss();
		displayPrevious();
	}

	@Override
	public void onControl1() {
		if (challengeGamePlayAllowed()) {
			final Money stake = _challengeStakeAndModeEditListItem.getStake();
			if (stake != null) {
				final ChallengeController challengeController = new ChallengeController(this);
				challengeController.createChallenge(stake, _contestant);

				final Integer mode = _challengeStakeAndModeEditListItem.getMode();
				if (mode != null) {
					challengeController.getChallenge().setMode(mode);
				}

				finishDisplay();
				getManager().startGamePlay(mode, challengeController.getChallenge());
			}
		}
	}

	@Override
	public void onControl2() {
		throw new IllegalStateException("this should not happen - a button has been clicked that isn't there");
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addObservedKeys(ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_CHALLENGES_WON));

		_contestant = getActivityArguments().getValue(Constant.CONTESTANT, null);

		if (_contestant != null) {
			_opponentValueStore = new ValueStore();
			_opponentValueStore.putValue(Constant.USER, _contestant);
			_opponentValueStore.addObserver(Constant.NUMBER_CHALLENGES_WON, this);
			_opponentValueStore.addValueSources(new UserDetailsAgent(this));
			setNeedsRefresh();
		}

		initAdapter();
	}

	@Override
	public void onRefresh(final int flags) {
		if (_contestant != null) {
			_opponentValueStore.retrieveValue(Constant.NUMBER_CHALLENGES_WON, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (valueStore == _opponentValueStore) {
			if (isValueChangedFor(key, Constant.NUMBER_CHALLENGES_WON, oldValue, newValue)) {
				getChallengeParticipantsListItem().setContestantStats(StringFormatter.getChallengesSubTitle(this, _opponentValueStore));
				getBaseListAdapter().notifyDataSetChanged();
			}
		} else {
			if (isValueChangedFor(key, Constant.NUMBER_CHALLENGES_WON, oldValue, newValue)) {
				getChallengeParticipantsListItem().setContenderStats(StringFormatter.getChallengesSubTitle(this, valueStore));
				getBaseListAdapter().notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onValueSetDirty(final ValueStore valueStore, final String key) {
		if (valueStore == _opponentValueStore) {
			_opponentValueStore.retrieveValue(Constant.NUMBER_CHALLENGES_WON, ValueStore.RetrievalMode.NOT_DIRTY, null);
		} else if (Constant.NUMBER_CHALLENGES_WON.equals(key)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
	}
}
