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

package com.scoreloop.client.android.ui.component.user;

import android.app.Dialog;
import android.os.Bundle;

import com.scoreloop.client.android.core.controller.MessageController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.framework.BaseDialog;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.OkCancelDialog;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class UserDetailListActivity extends ComponentListActivity<BaseListItem> implements BaseDialog.OnActionListener {

	// if user-plays-game argument is true, then we display the Game section
	// if arg is null, we have to determine whether user plays game
	// all the above is only true if the game is the session game and the configurations are enabled

	protected static enum GameSectionDisplayOption {
		HIDE, RECOMMEND, SHOW, UNKNOWN
	}

	protected UserDetailListItem		_achievementsListItem;
	protected UserDetailListItem		_buddiesListItem;
	protected BaseListItem				_challengesListItem;
	protected GameSectionDisplayOption	_gameSectionDisplayOption	= GameSectionDisplayOption.UNKNOWN;
	protected UserDetailListItem		_gamesListItem;
	protected BaseListItem				_recommendListItem;

	protected UserDetailListItem getAchievementsListItem() {
		if (_achievementsListItem == null) {
			_achievementsListItem = new UserDetailListItem(this, getResources().getDrawable(R.drawable.sl_icon_achievements),
					getString(R.string.sl_achievements), StringFormatter.getAchievementsSubTitle(this, getUserValues(), false));
		}
		return _achievementsListItem;
	}

	protected UserDetailListItem getBuddiesListItem() {
		if (_buddiesListItem == null) {
			_buddiesListItem = new UserDetailListItem(this, getResources().getDrawable(R.drawable.sl_icon_friends),
					getString(R.string.sl_friends), StringFormatter.getBuddiesSubTitle(this, getUserValues()));
		}
		return _buddiesListItem;
	}

	protected BaseListItem getChallengesListItem() {
		if (_challengesListItem == null) {
			_challengesListItem = new StandardListItem<Void>(this, getResources().getDrawable(R.drawable.sl_icon_challenges),
					getString(R.string.sl_format_challenges_title), getString(R.string.sl_format_challenges_subtitle, getUser().getDisplayName()), null);
		}
		return _challengesListItem;
	}

	protected CaptionListItem getCommunityCaptionListItem() {
		return new CaptionListItem(this, null, getString(R.string.sl_community));
	}

	protected CaptionListItem getGameCaptionListItem() {
		return new CaptionListItem(this, null, getGame().getName());
	}

	protected UserDetailListItem getGamesListItem() {
		if (_gamesListItem == null) {
			_gamesListItem = new UserDetailListItem(this, getResources().getDrawable(R.drawable.sl_icon_games),
					getString(R.string.sl_games), StringFormatter.getGamesSubTitle(this, getUserValues()));
		}
		return _gamesListItem;
	}

	protected BaseListItem getRecommendListItem() {
		if (getGame() != null && _recommendListItem == null) {
			final User user = getUser();
			final Game game = getGame();

			final String title = String.format(getString(R.string.sl_format_recommend_title), game.getName());
			final String subtitle = String.format(getString(R.string.sl_format_recommend_subtitle), user.getDisplayName());

			_recommendListItem = new StandardListItem<Void>(this, getResources().getDrawable(R.drawable.sl_icon_recommend), title,
					subtitle, null);
		}
		return _recommendListItem;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new BaseListAdapter<BaseListItem>(this));

		addObservedKeys(ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_BUDDIES),
				ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_GAMES),
				ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_ACHIEVEMENTS));
	}

	@Override
	protected void onStart() {
		super.onStart();

		setNeedsRefresh();
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		final User user = getUser();
		final Factory factory = getFactory();

		if (item == getRecommendListItem()) {
			showDialogSafe(Constant.DIALOG_CONFIRMATION_RECOMMEND_GAME, true);
		} else if (item == getAchievementsListItem()) {
			display(factory.createAchievementScreenDescription(user));
		} else if (item == getChallengesListItem()) {
			display(factory.createChallengeCreateScreenDescription(user, null));
		} else if (item == getBuddiesListItem()) {
			display(factory.createUserScreenDescription(user));
		} else if (item == this.getGamesListItem()) {
			display(factory.createGameScreenDescription(user, Constant.GAME_MODE_USER));
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.DIALOG_CONFIRMATION_RECOMMEND_GAME:
			OkCancelDialog dialog = new OkCancelDialog(this);
			dialog.setOnActionListener(this);
			dialog.setOkButtonText(getResources().getString(R.string.sl_leave_accept_game_recommendation_ok));
			dialog.setCancelable(true);
			dialog.setOnDismissListener(this);
			return dialog;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case Constant.DIALOG_CONFIRMATION_RECOMMEND_GAME:
			final String msg = getResources().getString(R.string.sl_leave_accept_game_recommendation, getGame().getName(),
					getUser().getDisplayName());
			((OkCancelDialog) dialog).setText(msg);
			break;
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	@Override
	public void onAction(BaseDialog dialog, int actionId) {
		dialog.dismiss();
		if (actionId == OkCancelDialog.BUTTON_OK) {
			postRecommendation();
		}
	}

	@Override
	public void onRefresh(final int flags) {
		if (_gameSectionDisplayOption == GameSectionDisplayOption.UNKNOWN) {

			if (isSessionGame()) {
				final Boolean userPlaysGame = getActivityArguments().getValue(Constant.USER_PLAYS_SESSION_GAME);
				if (userPlaysGame == null) {
					_gameSectionDisplayOption = GameSectionDisplayOption.UNKNOWN;
				} else if (userPlaysGame) {
					_gameSectionDisplayOption = GameSectionDisplayOption.SHOW;
				} else {
					_gameSectionDisplayOption = GameSectionDisplayOption.RECOMMEND;
				}
			} else {
				_gameSectionDisplayOption = GameSectionDisplayOption.HIDE;
			}
			updateList();
		}
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (isValueChangedFor(key, Constant.NUMBER_BUDDIES, oldValue, newValue)) {
			getBuddiesListItem().setSubTitle(StringFormatter.getBuddiesSubTitle(this, getUserValues()));
			getBaseListAdapter().notifyDataSetChanged();
		} else if (isValueChangedFor(key, Constant.NUMBER_GAMES, oldValue, newValue)) {
			getGamesListItem().setSubTitle(StringFormatter.getGamesSubTitle(this, getUserValues()));
			getBaseListAdapter().notifyDataSetChanged();
		} else if (isValueChangedFor(key, Constant.NUMBER_ACHIEVEMENTS, oldValue, newValue)) {
			getAchievementsListItem().setSubTitle(StringFormatter.getAchievementsSubTitle(this, getUserValues(), false));
			getBaseListAdapter().notifyDataSetChanged();
		}
	}

	@Override
	public void onValueSetDirty(final ValueStore valueStore, final String key) {
		if (Constant.NUMBER_BUDDIES.equals(key)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
		if (Constant.NUMBER_GAMES.equals(key)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}

		final Configuration configuration = getConfiguration();
		if (configuration.isFeatureEnabled(Configuration.Feature.ACHIEVEMENT) && Constant.NUMBER_ACHIEVEMENTS.equals(key)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
	}

	protected void postRecommendation() {
		final MessageController controller = new MessageController(getRequestControllerObserver());

		controller.setTarget(getGame());
		controller.setMessageType(MessageController.TYPE_RECOMMENDATION);
		controller.addReceiverWithUsers(MessageController.RECEIVER_USER, getUser());

		if (controller.isSubmitAllowed()) {
			showSpinnerFor(controller);
			controller.submitMessage();
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		showToast(getString(R.string.sl_recommend_sent));
	}

	protected void updateList() {
		final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
		adapter.clear();

		final Configuration configuration = getConfiguration();

		switch (_gameSectionDisplayOption) {
		case SHOW:
			final boolean showAchievements = configuration.isFeatureEnabled(Configuration.Feature.ACHIEVEMENT);
			final boolean showChallenges = configuration.isFeatureEnabled(Configuration.Feature.CHALLENGE);

			if (showAchievements || showChallenges) {
				adapter.add(getGameCaptionListItem());
				if (showAchievements) {
					adapter.add(getAchievementsListItem());
				}
				if (showChallenges) {
					adapter.add(getChallengesListItem());
				}
			}
			break;

		case RECOMMEND:
			adapter.add(getGameCaptionListItem());
			adapter.add(getRecommendListItem());
			break;
		}

		adapter.add(getCommunityCaptionListItem());
		adapter.add(getBuddiesListItem());
		adapter.add(getGamesListItem());
	}
}
