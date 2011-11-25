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

import com.scoreloop.client.android.core.controller.ChallengeController;
import com.scoreloop.client.android.core.controller.ChallengeControllerObserver;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.Challenge;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.agent.UserDetailsAgent;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.component.challenge.ChallengeControlsListItem.OnControlObserver;
import com.scoreloop.client.android.ui.framework.BaseDialog;
import com.scoreloop.client.android.ui.framework.BaseDialog.OnActionListener;
import com.scoreloop.client.android.ui.framework.NavigationIntent;
import com.scoreloop.client.android.ui.framework.OkCancelDialog;
import com.scoreloop.client.android.ui.framework.TextButtonDialog;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class ChallengeAcceptListActivity extends ChallengeActionListActivity implements ChallengeControllerObserver, OnActionListener,
		OnControlObserver {

	private Challenge						_challenge;
	private ChallengeParticipantsListItem	_challengeParticipantsListItem;
	private boolean							_isNavigationAllowed;
	private OkCancelDialog					_navigationDialog;
	private NavigationIntent				_navigationIntent;
	private Runnable						_navigationDialogContinuation;
	private ValueStore						_opponentValueStore;

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.DIALOG_CHALLENGE_ERROR_ACCEPT:
			final TextButtonDialog dialogAccept = new TextButtonDialog(this);
			dialogAccept.setText(getResources().getString(R.string.sl_error_message_challenge_accept));
			dialogAccept.setOnActionListener(this);
			dialogAccept.setOnDismissListener(this);
			return dialogAccept;
		case Constant.DIALOG_CHALLENGE_ERROR_REJECT:
			final TextButtonDialog dialogReject = new TextButtonDialog(this);
			dialogReject.setText(getResources().getString(R.string.sl_error_message_challenge_reject));
			dialogReject.setOnActionListener(this);
			dialogReject.setOnDismissListener(this);
			return dialogReject;
		case Constant.DIALOG_CHALLENGE_ERROR_BALANCE:
			final TextButtonDialog dialogBalance = new TextButtonDialog(this);
			dialogBalance.setText(getResources().getString(R.string.sl_error_message_challenge_balance));
			dialogBalance.setOnActionListener(this);
			dialogBalance.setOnDismissListener(this);
			return dialogBalance;
		case Constant.DIALOG_CHALLENGE_LEAVE_ACCEPT:
			OkCancelDialog navigationDialog = new OkCancelDialog(this);
			navigationDialog.setText(getResources().getString(R.string.sl_leave_accept_challenge));
			navigationDialog.setOnActionListener(this);
			navigationDialog.setOnDismissListener(this);
			return navigationDialog;
		default:
			return super.onCreateDialog(id);
		}
	}

	public void challengeControllerDidFailOnInsufficientBalance(final ChallengeController challengeController) {
		_isNavigationAllowed = true;
		showDialogSafe(Constant.DIALOG_CHALLENGE_ERROR_BALANCE, true);
	}

	public void challengeControllerDidFailToAcceptChallenge(final ChallengeController challengeController) {
		_isNavigationAllowed = true;
		showDialogSafe(Constant.DIALOG_CHALLENGE_ERROR_ACCEPT, true);
	}

	public void challengeControllerDidFailToRejectChallenge(final ChallengeController challengeController) {
		_isNavigationAllowed = true;
		showDialogSafe(Constant.DIALOG_CHALLENGE_ERROR_REJECT, true);
	}

	private void doAfterNavigationDialog(final Runnable continuation) {
		if (_navigationDialog == null) {
			continuation.run();
		} else {
			_navigationDialogContinuation = continuation;
		}
	}

	@Override
	CaptionListItem getCaptionListItem() {
		return new CaptionListItem(ChallengeAcceptListActivity.this, null, getString(R.string.sl_accept_challenge));
	}

	@Override
	ChallengeControlsListItem getChallengeControlsListItem() {
		return new ChallengeControlsListItem(this, _challenge, this);
	}

	@Override
	ChallengeParticipantsListItem getChallengeParticipantsListItem() {
		if (_challengeParticipantsListItem == null) {
			_challengeParticipantsListItem = new ChallengeParticipantsListItem(this, _challenge.getContender(), getUser());
		}
		return _challengeParticipantsListItem;
	}

	@Override
	ChallengeSettingsListItem getChallengeStakeAndModeListItem() {
		return new ChallengeSettingsListItem(this, _challenge);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case Constant.DIALOG_CHALLENGE_LEAVE_ACCEPT:
			final OkCancelDialog okCancelDialog = (OkCancelDialog) dialog;
			okCancelDialog.setTarget(_navigationIntent);
			_navigationDialog = okCancelDialog;
			break;

		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected boolean isNavigationAllowed(final NavigationIntent navigationIntent) {
		if (!_isNavigationAllowed) {
			_navigationIntent = navigationIntent;
			showDialogSafe(Constant.DIALOG_CHALLENGE_LEAVE_ACCEPT, true);
			return false;
		}
		return true;
	}

	public void onAction(final BaseDialog dialog, final int action) {
		if (dialog == _navigationDialog) {
			_navigationDialog = null;
			if (action == OkCancelDialog.BUTTON_OK) {
				dialog.dismiss();
				dialog.<NavigationIntent> getTarget().execute();
			} else {
				dialog.dismiss();
				onNavigationDialogFinished();
			}
		} else {
			dialog.dismiss();
			doAfterNavigationDialog(new Runnable() {
				public void run() {
					displayPrevious();
				}
			});
		}
	}

	public void onControl1() {
		if (challengeGamePlayAllowed()) {
			_isNavigationAllowed = false;
			_challenge.setContestant(getUser());
			final ChallengeController challengeController = new ChallengeController(this);
			showSpinnerFor(challengeController);
			challengeController.setChallenge(_challenge);
			challengeController.acceptChallenge();
		}
	}

	public void onControl2() {
		_challenge.setContestant(getUser());
		final ChallengeController challengeController = new ChallengeController(this);
		showSpinnerFor(challengeController);
		challengeController.setChallenge(_challenge);
		challengeController.rejectChallenge();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		// used in prepare dialog (must be restored before super.onCreate()
		_navigationIntent = getActivityArguments().<NavigationIntent> getValue(Constant.NAVIGATION_INTENT, _navigationIntent);
		_navigationDialogContinuation = getActivityArguments().<Runnable> getValue(Constant.NAVIGATION_DIALOG_CONTINUATION);
		Boolean navigationAllowed = getActivityArguments().<Boolean> getValue(Constant.NAVIGATION_ALLOWED, Boolean.TRUE);
		if (navigationAllowed != null) {
			_isNavigationAllowed = navigationAllowed;
		}

		super.onCreate(savedInstanceState);

		addObservedKeys(ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_CHALLENGES_WON));

		_challenge = getActivityArguments().getValue(Constant.CHALLENGE, null); // assert challenge != null

		_opponentValueStore = new ValueStore();
		_opponentValueStore.putValue(Constant.USER, _challenge.getContender());
		_opponentValueStore.addObserver(Constant.NUMBER_CHALLENGES_WON, this);
		_opponentValueStore.addValueSources(new UserDetailsAgent());
		setNeedsRefresh();

		initAdapter();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getActivityArguments().putValue(Constant.NAVIGATION_INTENT, _navigationIntent);
		getActivityArguments().putValue(Constant.NAVIGATION_DIALOG_CONTINUATION, _navigationDialogContinuation);
		getActivityArguments().putValue(Constant.NAVIGATION_ALLOWED, _isNavigationAllowed);
	}

	private void onNavigationDialogFinished() {
		if (_navigationDialogContinuation != null) {
			final Runnable continuation = _navigationDialogContinuation;
			_navigationDialogContinuation = null;
			continuation.run();
		}
	}

	@Override
	public void onRefresh(final int flags) {
		_opponentValueStore.retrieveValue(Constant.NUMBER_CHALLENGES_WON, ValueStore.RetrievalMode.NOT_DIRTY, null);
	}

	@Override
	public void onResume() {
		super.onResume();

		_isNavigationAllowed = true;
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (valueStore == _opponentValueStore) {
			if (isValueChangedFor(key, Constant.NUMBER_CHALLENGES_WON, oldValue, newValue)) {
				getChallengeParticipantsListItem().setContenderStats(StringFormatter.getChallengesSubTitle(this, _opponentValueStore));
				getBaseListAdapter().notifyDataSetChanged();
			}

		} else {
			if (isValueChangedFor(key, Constant.NUMBER_CHALLENGES_WON, oldValue, newValue)) {
				getChallengeParticipantsListItem().setContestantStats(StringFormatter.getChallengesSubTitle(this, valueStore));
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

	@Override
	protected void requestControllerDidFailSafe(final RequestController aRequestController, final Exception anException) {
		_isNavigationAllowed = true;
		showDialogForExceptionSafe(anException);
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		_isNavigationAllowed = true;
		final Challenge challenge = ((ChallengeController) aRequestController).getChallenge();
		if (challenge.isAccepted()) {
			doAfterNavigationDialog(new Runnable() {
				public void run() {
					startChallenge();
				}
			});
		} else if (challenge.isRejected()) {
			displayPrevious();
		} else {
			throw new IllegalStateException("this should not happen - illegal state of the accepted/rejected challenge");
		}
	}

	private void startChallenge() {
		finishDisplay();
		getManager().startGamePlay(_challenge.getMode(), _challenge);
	}
}
