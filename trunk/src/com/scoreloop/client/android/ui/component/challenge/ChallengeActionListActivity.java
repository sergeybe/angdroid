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

import android.app.Dialog;
import android.os.Bundle;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseDialog.OnActionListener;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.TextButtonDialog;

public abstract class ChallengeActionListActivity extends ComponentListActivity<BaseListItem> implements OnActionListener {

	abstract CaptionListItem getCaptionListItem();

	abstract ChallengeControlsListItem getChallengeControlsListItem();

	abstract ChallengeParticipantsListItem getChallengeParticipantsListItem();

	abstract ChallengeSettingsListItem getChallengeStakeAndModeListItem();

	void initAdapter() {
		final BaseListAdapter<BaseListItem> adapter = new BaseListAdapter<BaseListItem>(this);
		adapter.add(getCaptionListItem());
		adapter.add(getChallengeParticipantsListItem());
		adapter.add(getChallengeStakeAndModeListItem());
		adapter.add(getChallengeControlsListItem());
		setListAdapter(adapter);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.DIALOG_CHALLENGE_ONGOING:
			TextButtonDialog dialogOngoing = new TextButtonDialog(this);
			dialogOngoing.setText(getResources().getString(R.string.sl_error_message_challenge_ongoing));
			dialogOngoing.setOnActionListener(this);
			return dialogOngoing;
		case Constant.DIALOG_CHALLENGE_GAME_NOT_READY:
			TextButtonDialog dialogGameNotReady = new TextButtonDialog(this);
			dialogGameNotReady.setText(getResources().getString(R.string.sl_error_message_challenge_game_not_ready));
			dialogGameNotReady.setOnActionListener(this);
			return dialogGameNotReady;
		default:
			return super.onCreateDialog(id);
		}
	}

	protected boolean challengeGamePlayAllowed() {
		if (getManager().isChallengeOngoing()) {
			showDialogSafe(Constant.DIALOG_CHALLENGE_ONGOING);
			return false;
		} else if (!getManager().canStartGamePlay()) {
			showDialogSafe(Constant.DIALOG_CHALLENGE_GAME_NOT_READY);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
