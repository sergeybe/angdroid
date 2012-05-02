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

package com.scoreloop.client.android.ui.manager;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.scoreloop.client.android.core.controller.ChallengeController;
import com.scoreloop.client.android.core.controller.GameItemController;
import com.scoreloop.client.android.core.controller.PendingPaymentProcessor;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerException;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.ScoreController;
import com.scoreloop.client.android.core.controller.ScoresController;
import com.scoreloop.client.android.core.controller.TermsOfServiceController;
import com.scoreloop.client.android.core.controller.TermsOfServiceControllerObserver;
import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.core.model.AwardList;
import com.scoreloop.client.android.core.model.Challenge;
import com.scoreloop.client.android.core.model.Client;
import com.scoreloop.client.android.core.model.ClientObserver;
import com.scoreloop.client.android.core.model.Continuation;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.core.model.Payment;
import com.scoreloop.client.android.core.model.PaymentMethod;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.ScoreSubmitException;
import com.scoreloop.client.android.core.model.SearchList;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.TermsOfService;
import com.scoreloop.client.android.core.model.User;
import com.scoreloop.client.android.ui.OnCanStartGamePlayObserver;
import com.scoreloop.client.android.ui.OnPaymentChangedObserver;
import com.scoreloop.client.android.ui.OnScoreSubmitObserver;
import com.scoreloop.client.android.ui.OnStartGamePlayRequestObserver;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.component.achievement.AchievementHeaderActivity;
import com.scoreloop.client.android.ui.component.achievement.AchievementListActivity;
import com.scoreloop.client.android.ui.component.achievement.AchievementsEngine;
import com.scoreloop.client.android.ui.component.agent.BaseAgent;
import com.scoreloop.client.android.ui.component.agent.NewsAgent;
import com.scoreloop.client.android.ui.component.agent.NumberAchievementsAgent;
import com.scoreloop.client.android.ui.component.agent.UserAgent;
import com.scoreloop.client.android.ui.component.agent.UserBuddiesAgent;
import com.scoreloop.client.android.ui.component.agent.UserDetailsAgent;
import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Configuration.Feature;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.component.base.Manager;
import com.scoreloop.client.android.ui.component.base.Tracker;
import com.scoreloop.client.android.ui.component.challenge.ChallengeAcceptListActivity;
import com.scoreloop.client.android.ui.component.challenge.ChallengeCreateListActivity;
import com.scoreloop.client.android.ui.component.challenge.ChallengeHeaderActivity;
import com.scoreloop.client.android.ui.component.challenge.ChallengeListActivity;
import com.scoreloop.client.android.ui.component.entry.EntryListActivity;
import com.scoreloop.client.android.ui.component.game.GameDetailHeaderActivity;
import com.scoreloop.client.android.ui.component.game.GameDetailListActivity;
import com.scoreloop.client.android.ui.component.game.GameListActivity;
import com.scoreloop.client.android.ui.component.market.MarketHeaderActivity;
import com.scoreloop.client.android.ui.component.market.MarketListActivity;
import com.scoreloop.client.android.ui.component.news.NewsHeaderActivity;
import com.scoreloop.client.android.ui.component.news.NewsListActivity;
import com.scoreloop.client.android.ui.component.payment.AbstractCheckoutListActivity;
import com.scoreloop.client.android.ui.component.payment.GameItemGridActivity;
import com.scoreloop.client.android.ui.component.payment.GameItemHeaderActivity;
import com.scoreloop.client.android.ui.component.payment.PaymentConstant;
import com.scoreloop.client.android.ui.component.payment.PaymentHeaderActivity;
import com.scoreloop.client.android.ui.component.payment.PaymentMethodListActivity;
import com.scoreloop.client.android.ui.component.payment.PriceListActivity;
import com.scoreloop.client.android.ui.component.profile.ProfileSettingsListActivity;
import com.scoreloop.client.android.ui.component.profile.ProfileSettingsPictureListActivity;
import com.scoreloop.client.android.ui.component.score.ScoreHeaderActivity;
import com.scoreloop.client.android.ui.component.score.ScoreListActivity;
import com.scoreloop.client.android.ui.component.user.UserAddBuddyListActivity;
import com.scoreloop.client.android.ui.component.user.UserDetailListActivity;
import com.scoreloop.client.android.ui.component.user.UserHeaderActivity;
import com.scoreloop.client.android.ui.component.user.UserListActivity;
import com.scoreloop.client.android.ui.framework.BaseActivity;
import com.scoreloop.client.android.ui.framework.ScreenDescription;
import com.scoreloop.client.android.ui.framework.ScreenManager;
import com.scoreloop.client.android.ui.framework.ScreenManagerSingleton;
import com.scoreloop.client.android.ui.framework.StandardScreenManager;
import com.scoreloop.client.android.ui.framework.ValueStore;
import com.scoreloop.client.android.ui.framework.ValueStore.ValueSource;
import com.scoreloop.client.android.ui.framework.ValueStore.ValueSourceFactory;
import com.scoreloop.client.android.ui.manager.Checker.CheckerRun;
import com.scoreloop.client.android.ui.util.LocalImageStorage;

public class StandardScoreloopManager implements ScoreloopManagerSingleton.Impl, Manager, Factory, ScreenDescription.ShortcutObserver,
		ScreenManager.Delegate, ValueSourceFactory, Tracker, PendingPaymentProcessor.Observer, ClientObserver, BaseAgent.Delegate {

	private static final String	PREFERENCES_ENTRY_USER_IMAGE_URL	= "userImageUrl";
	private static final String	PREFERENCES_ENTRY_USER_NAME			= "userName";
	private static final String	PREFERENCES_NAME					= "com.scoreloop.ui.login";

	private static boolean containsKey(final String[] keys, final String key) {
		for (final String string : keys) {
			if (string.equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

	private AchievementsEngine				_achievementsEngine;
	private boolean							_askUserForTermsAndConditions	= true;
	private ValueStore						_cachedUserValueStore;
	private Client							_client;
	private Configuration					_configuration;
	private Context							_context;
	private final Handler					_handler						= new Handler();
	private Challenge						_lastSubmittedChallenge;
	private Score							_lastSubmittedScore;
	private int								_lastSubmitStatus;
	private OnCanStartGamePlayObserver		_onCanStartGamePlayObserver;
	private OnPaymentChangedObserver		_onPaymentChangedObserver;
	private OnScoreSubmitObserver			_onScoreSubmitObserver;
	private OnStartGamePlayRequestObserver	_onStartGamePlayRequestObserver;
	private Continuation<Boolean>			_rejectedTermsOfServiceNotification;
	private ValueStore						_sessionGameValueStore;
	private ValueStore						_sessionUserValueStore;
	private List<Runnable>					_submitLocalScoresContinuations	= new LinkedList<Runnable>();

	@Override
	public void achieveAward(final String awardId, final boolean showToast, final boolean submitNow) {
		final Achievement achievement = getAchievement(awardId); // checks made here
		if (achievement.isAchieved()) {
			return;
		}
		achievement.setAchieved();
		if (showToast) {
			showToastForAchievement(achievement);
		}
		if (submitNow) {
			submitAchievements(null);
		}
	}

	@Override
	public void askUserToAcceptTermsOfService(final Activity activity, final Continuation<Boolean> continuation) {
		final TermsOfServiceController controller = new TermsOfServiceController(getSession(), new TermsOfServiceControllerObserver() {
			@Override
			public void termsOfServiceControllerDidFinish(final TermsOfServiceController controller, final Boolean accepted) {
				if (continuation != null) {
					continuation.withValue(accepted, null);
				}
			}
		});
		controller.query(activity);
	}

	@Override
	public boolean canStartGamePlay() {
		if (_onCanStartGamePlayObserver == null) {
			throw new IllegalStateException(
					"trying to check if gameplay can be started, but the callback is not set - did you call ScoreloopManagerSingleton.get().setOnCanStartGamePlayObserver(...)?");
		}
		return _onCanStartGamePlayObserver.onCanStartGamePlay();
	}

	private void checkHasAwards() {
		if (!_configuration.isFeatureEnabled(Feature.ACHIEVEMENT)) {
			throw new IllegalStateException("you have to set 'ui.feature.achievement = true' in the scoreloop.properties first!");
		}
	}

	private void checkHasLoadedAchievements() {
		if (!hasLoadedAchievements()) {
			throw new IllegalStateException("you have to load the achievements first!");
		}
	}

	private void checkHasPaymentEnabled() {
		if (!_configuration.isFeatureEnabled(Feature.PAYMENT)) {
			throw new IllegalStateException("you have to set 'ui.feature.payment = true' in the scoreloop.properties first!");
		}
	}

	@Override
	public void clientDidAskUserForTermsOfService(final Client client, final Boolean accepted) {
		if ((accepted == null) || !accepted) {
			if (_rejectedTermsOfServiceNotification != null) {
				_rejectedTermsOfServiceNotification.withValue(accepted, null);
			}
		}
	}

	@Override
	public boolean clientShouldAskUserForTermsOfService(final Client client) {
		return _askUserForTermsAndConditions;
	}

	@Override
	public ScreenDescription createAchievementScreenDescription(final User user) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description.setHeaderDescription(new Intent(getContext(), AchievementHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), AchievementListActivity.class)).getArguments()
				.putValue(Constant.ACHIEVEMENTS_ENGINE, getAchievementsEngine());
		return description;
	}

	@Override
	public ScreenDescription createChallengeAcceptScreenDescription(final Challenge challenge) {
		final ScreenDescription description = createScreenDescription(null, null, true);
		description.setHeaderDescription(new Intent(getContext(), ChallengeHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), ChallengeAcceptListActivity.class)).getArguments()
				.putValue(Constant.CHALLENGE, challenge);

		return description;
	}

	@Override
	public ScreenDescription createChallengeCreateScreenDescription(final User user, final Integer mode) {
		final ScreenDescription description = createScreenDescription(null, null, true);
		description.setHeaderDescription(new Intent(getContext(), ChallengeHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), ChallengeCreateListActivity.class)).getArguments()
				.putValue(Constant.CONTESTANT, user);

		return description;
	}

	@Override
	public ScreenDescription createChallengePaymentScreenDescription() {
		return createGameItemsScreenDescription(PaymentConstant.GAME_ITEMS_MODE_COIN_PACK, null, null,
				PaymentConstant.VIEW_FLAGS_SHOW_TOAST | PaymentConstant.VIEW_FLAGS_INTERNAL_USAGE);
	}

	@Override
	public ScreenDescription createChallengeScreenDescription(final User user) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description.setHeaderDescription(new Intent(getContext(), ChallengeHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), ChallengeListActivity.class));
		return description;
	}

	@Override
	public ScreenDescription createEntryScreenDescription() {
		final ScreenDescription description = createScreenDescription(null, null, true);
		description.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class)).getArguments()
				.putValue(Constant.MODE, UserHeaderActivity.ControlMode.PROFILE);
		description.setBodyDescription(new Intent(getContext(), EntryListActivity.class));
		description.setShortcutSelectionId(R.string.sl_home);
		return description;
	}

	@Override
	public ScreenDescription createGameDetailScreenDescription(final Game game) {
		final ScreenDescription description = createScreenDescription(null, game, true);
		description.setHeaderDescription(new Intent(getContext(), GameDetailHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), GameDetailListActivity.class));
		return description;
	}

	@Override
	public ScreenDescription createGameItemsScreenDescription(final int mode, final String explicitCurrency, final List<String> tags,
			final int viewFlags) {
		final ScreenDescription description = createScreenDescription(null, null, true);
		description.setHeaderDescription(new Intent(getContext(), GameItemHeaderActivity.class)).getArguments()
				.putValue(PaymentConstant.GAME_ITEMS_MODE, mode);
		final ValueStore bodyArguments = description.setBodyDescription(new Intent(getContext(), GameItemGridActivity.class))
				.getArguments();
		bodyArguments.putValue(PaymentConstant.GAME_ITEMS_MODE, mode);
		bodyArguments.putValue(PaymentConstant.PAYMENT_EXPLICIT_CURRENCY, explicitCurrency);
		bodyArguments.putValue(PaymentConstant.TAGS, tags);
		bodyArguments.putValue(PaymentConstant.VIEW_FLAGS, viewFlags);
		return description;
	}

	@Override
	public ScreenDescription createGameScreenDescription(final User user, final int mode) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		if (mode == Constant.GAME_MODE_USER) {
			description.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class)).getArguments()
					.putValue(Constant.MODE, UserHeaderActivity.ControlMode.BUDDY);
		} else {
			description.setHeaderDescription(new Intent(getContext(), MarketHeaderActivity.class));
		}
		description.setBodyDescription(new Intent(getContext(), GameListActivity.class)).getArguments().putValue(Constant.MODE, mode);
		return description;
	}

	@Override
	public ScreenDescription createMarketScreenDescription(final User user) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description.setHeaderDescription(new Intent(getContext(), MarketHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), MarketListActivity.class));
		description.setShortcutSelectionId(R.string.sl_market);
		return description;
	}

	@Override
	public ScreenDescription createNewsScreenDescription() {
		final ScreenDescription description = createScreenDescription(null, null, true);
		description.setHeaderDescription(new Intent(getContext(), NewsHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), NewsListActivity.class));
		return description;
	}

	@Override
	public ScreenDescription createPaymentMethodsScreenDescription(final String gameItemId, final String explicitCurrency,
			final int viewFlags) {
		final ScreenDescription description = createScreenDescription(null, null, false);
		description.setHeaderDescription(new Intent(getContext(), PaymentHeaderActivity.class));
		final ValueStore bodyArguments = description.setBodyDescription(new Intent(getContext(), PaymentMethodListActivity.class))
				.getArguments();
		bodyArguments.putValue(PaymentConstant.GAME_ITEM_ID, gameItemId);
		bodyArguments.putValue(PaymentConstant.PAYMENT_EXPLICIT_CURRENCY, explicitCurrency);
		bodyArguments.putValue(PaymentConstant.VIEW_FLAGS, viewFlags);
		return description;
	}

	@Override
	public ScreenDescription createPricesScreenDescription(final GameItem gameItem, final PaymentMethod paymentMethod, final int viewFlags) {
		final ScreenDescription description = createScreenDescription(null, null, false);
		description.getScreenValues().putValue(PaymentConstant.GAME_ITEM, gameItem);
		description.setHeaderDescription(new Intent(getContext(), PaymentHeaderActivity.class));
		final ValueStore bodyArguments = description.setBodyDescription(new Intent(getContext(), PriceListActivity.class)).getArguments();
		bodyArguments.putValue(PaymentConstant.PAYMENT_METHOD, paymentMethod);
		bodyArguments.putValue(PaymentConstant.VIEW_FLAGS, viewFlags);
		return description;
	}

	@Override
	public ScreenDescription createProfileSettingsPictureScreenDescription(final User user) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), ProfileSettingsPictureListActivity.class));
		return description;
	}

	@Override
	public ScreenDescription createProfileSettingsScreenDescription(final User user) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class));
		description.setBodyDescription(new Intent(getContext(), ProfileSettingsListActivity.class));
		return description;
	}

	@Override
	public ScreenDescription createScoreScreenDescription(final Game game, final Integer mode, final Integer leaderboard) {
		final ScreenDescription description = createScreenDescription(null, game, true);
		description.getScreenValues().putValue(Constant.MODE, mode != null ? mode : ensureGame(game).getMinMode());

		final Boolean isLocalLeaderboard = (leaderboard != null) && (leaderboard == Constant.LEADERBOARD_LOCAL);
		description.setHeaderDescription(new Intent(getContext(), ScoreHeaderActivity.class)).getArguments()
				.putValue(Constant.IS_LOCAL_LEADEARBOARD, isLocalLeaderboard);

		if (isLocalLeaderboard) {
			description.setBodyDescription(new Intent(getContext(), ScoreListActivity.class)).getArguments()
					.putValue(Constant.SEARCH_LIST, SearchList.getLocalScoreSearchList());
		} else {
			description.addBodyDescription(R.string.sl_global, new Intent(getContext(), ScoreListActivity.class)).getArguments()
					.putValue(Constant.SEARCH_LIST, SearchList.getGlobalScoreSearchList());
			description.addBodyDescription(R.string.sl_friends, new Intent(getContext(), ScoreListActivity.class)).getArguments()
					.putValue(Constant.SEARCH_LIST, SearchList.getBuddiesScoreSearchList());
			description.addBodyDescription(R.string.sl_twentyfour, new Intent(getContext(), ScoreListActivity.class)).getArguments()
					.putValue(Constant.SEARCH_LIST, SearchList.getTwentyFourHourScoreSearchList());
			if (leaderboard != null) {
				description.setSelectedBodyIndex(leaderboard);
			}
		}

		return description;
	}

	private ScreenDescription createScreenDescription(final User user, final Game game, final boolean addStandardShortcuts) {
		final ScreenDescription description = new ScreenDescription();

		final ValueStore screenValues = description.getScreenValues();
		screenValues.putValue(Constant.USER_VALUES, getUserValues(ensureUser(user)));
		screenValues.putValue(Constant.GAME_VALUES, getGameValues(ensureGame(game)));
		screenValues.putValue(Constant.SESSION_USER_VALUES, getSessionUserValues());
		screenValues.putValue(Constant.SESSION_GAME_VALUES, getSessionGameValues());
		screenValues.putValue(Constant.MANAGER, this);
		screenValues.putValue(Constant.FACTORY, this);
		screenValues.putValue(Constant.TRACKER, this);
		screenValues.putValue(Constant.CONFIGURATION, _configuration);

		if (addStandardShortcuts) {
			description.addShortcutObserver(this);
			description.addShortcutDescription(R.string.sl_home, R.drawable.sl_shortcut_home_default, R.drawable.sl_shortcut_home_active);
			description.addShortcutDescription(R.string.sl_friends, R.drawable.sl_shortcut_friends_default,
					R.drawable.sl_shortcut_friends_active);
			description.addShortcutDescription(R.string.sl_market, R.drawable.sl_shortcut_market_default,
					R.drawable.sl_shortcut_market_active);
		}
		return description;
	}

	@Override
	public ScreenDescription createUserAddBuddyScreenDescription() {
		final ScreenDescription description = createScreenDescription(null, null, true);
		description.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class)).getArguments()
				.putValue(Constant.MODE, UserHeaderActivity.ControlMode.BLANK);
		description.setBodyDescription(new Intent(getContext(), UserAddBuddyListActivity.class));
		return description;
	}

	@Override
	public ScreenDescription createUserDetailScreenDescription(final User user, final Boolean playsSessionGame) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class)).getArguments()
				.putValue(Constant.MODE, UserHeaderActivity.ControlMode.BUDDY);
		description.setBodyDescription(new Intent(getContext(), UserDetailListActivity.class)).getArguments()
				.putValue(Constant.USER_PLAYS_SESSION_GAME, playsSessionGame);
		return description;
	}

	@Override
	public ScreenDescription createUserScreenDescription(final User user) {
		final ScreenDescription description = createScreenDescription(user, null, true);
		description
				.setHeaderDescription(new Intent(getContext(), UserHeaderActivity.class))
				.getArguments()
				.putValue(
						Constant.MODE,
						getSession().isOwnedByUser(ensureUser(user)) ? UserHeaderActivity.ControlMode.BLANK
								: UserHeaderActivity.ControlMode.BUDDY);
		description.setBodyDescription(new Intent(getContext(), UserListActivity.class));
		if (getSession().isOwnedByUser(ensureUser(user))) {
			description.setShortcutSelectionId(R.string.sl_friends);
		}
		return description;
	}

	@Override
	public void destroy() {
		ScreenManagerSingleton.destroy();

		_client.destroy();
		_client = null;
	}

	private void displayWithEmptyStack(final ScreenDescription description) {
		ScreenManagerSingleton.get().displayWithEmptyStack(description);
	}

	private Game ensureGame(final Game game) {
		return game != null ? game : getSession().getGame();
	}

	private User ensureUser(final User user) {
		return user != null ? user : getSession().getUser();
	}

	@Override
	public Achievement getAchievement(final String awardId) {
		checkHasAwards();
		checkHasLoadedAchievements();
		return _achievementsEngine.getAchievementsController().getAchievementForAwardIdentifier(awardId);
	}

	@Override
	public List<Achievement> getAchievements() {
		checkHasAwards();
		checkHasLoadedAchievements();
		return _achievementsEngine.getAchievementsController().getAchievements();
	}

	private AchievementsEngine getAchievementsEngine() {
		if (_achievementsEngine == null) {
			_achievementsEngine = new AchievementsEngine();
		}
		return _achievementsEngine;
	}

	@Override
	public AwardList getAwardList() {
		checkHasAwards();
		return getAchievementsEngine().getAchievementsController().getAwardList();
	}

	@Override
	public Configuration getConfiguration() {
		return _configuration;
	}

	private Context getContext() {
		return _context;
	}

	@Override
	public Factory getFactory() {
		return this;
	}

	@Override
	public void getGameItemDownloadUrl(final String gameItemIdentifier, final Continuation<String> continuation) {
		if ((gameItemIdentifier == null) || (continuation == null)) {
			throw new IllegalArgumentException("arguments must not be null");
		}
		checkHasPaymentEnabled();

		final GameItemController controller = new GameItemController(getSession(), new RequestControllerObserver() {
			@Override
			public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
				continuation.withValue(null, anException);
			}

			@Override
			public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
				final GameItemController gameItemController = (GameItemController) aRequestController;
				continuation.withValue(gameItemController.getGameItem().getDownloadUrl(), null);
			}
		});
		controller.setGameItem(getSession().getEntityFactory().createEntity(gameItemIdentifier, GameItem.ENTITY_NAME));
		controller.loadGameItemDownloadUrl();
	}

	private ValueStore getGameValues(final Game game) {
		if (game.equals(getSession().getGame())) {
			return getSessionGameValues();
		}
		final ValueStore valueStore = new ValueStore();
		valueStore.setValueSourceFactroy(this);
		valueStore.putValue(Constant.GAME, game);

		return valueStore;
	}

	@Override
	public String getInfoString() {
		return _client.getInfoString();
	}

	@Override
	public Challenge getLastSubmittedChallenge() {
		return _lastSubmittedChallenge;
	}

	@Override
	public Score getLastSubmittedScore() {
		return _lastSubmittedScore;
	}

	@Override
	public int getLastSubmitStatus() {
		return _lastSubmitStatus;
	}

	@Override
	public String[] getModeNames() {
		return _configuration.getModesNames();
	}

	private String getPersistedUserImageUrl() {
		final SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES_NAME, 0);
		return preferences.getString(PREFERENCES_ENTRY_USER_IMAGE_URL, null);
	}

	private String getPersistedUserName() {
		final SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES_NAME, 0);
		return preferences.getString(PREFERENCES_ENTRY_USER_NAME, null);
	}

	@Override
	public Session getSession() {
		return _client.getSession();
	}

	private ValueStore getSessionGameValues() {
		if (_sessionGameValueStore == null) {
			_sessionGameValueStore = new ValueStore();
			_sessionGameValueStore.setValueSourceFactroy(this);
			_sessionGameValueStore.putValue(Constant.GAME, getSession().getGame());
		}
		return _sessionGameValueStore;
	}

	private ValueStore getSessionUserValues() {
		if (_sessionUserValueStore == null) {
			_sessionUserValueStore = new ValueStore();
			_sessionUserValueStore.setValueSourceFactroy(this);
			_sessionUserValueStore.putValue(Constant.USER, getSession().getUser());
		}
		return _sessionUserValueStore;
	}

	private ValueStore getUserValues(final User user) {
		if (getSession().isOwnedByUser(user)) {
			return getSessionUserValues();
		}

		// look in the cache first
		if ((_cachedUserValueStore != null) && _cachedUserValueStore.getValue(Constant.USER).equals(user)) {
			return _cachedUserValueStore;
		}

		// not found, so create new value store and cache that one
		final ValueStore valueStore = new ValueStore();
		valueStore.setValueSourceFactroy(this);
		valueStore.putValue(Constant.USER, user);
		_cachedUserValueStore = valueStore;

		return valueStore;
	}

	@Override
	public ValueSource getValueSourceForKeyInStore(final String key, final ValueStore valueStore) {
		if (containsKey(UserAgent.SUPPORTED_KEYS, key)) {
			return new UserAgent(this);
		} else if (containsKey(UserDetailsAgent.SUPPORTED_KEYS, key)) {
			return new UserDetailsAgent(this);
		} else if (containsKey(NumberAchievementsAgent.SUPPORTED_KEYS, key)) {
			return new NumberAchievementsAgent(this);
		} else if (containsKey(NewsAgent.SUPPORTED_KEYS, key)) {
			return new NewsAgent();
		} else if (containsKey(UserBuddiesAgent.SUPPORTED_KEYS, key)) {
			return new UserBuddiesAgent(this);
		}
		return null;
	}

	@Override
	public boolean hasLoadedAchievements() {
		checkHasAwards();
		return getAchievementsEngine().hasLoadedAchievements();
	}

	@Override
	public Boolean hasPendingPaymentForGameItemWithIdentifier(final String identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}
		checkHasPaymentEnabled();

		return PendingPaymentProcessor.getInstance(getSession()).hasPendingPaymentForGameItem(identifier);
	}

	@Override
	public boolean incrementAward(final String awardId, final boolean showToast, final boolean submitNow) {
		final Achievement achievement = getAchievement(awardId); // checks made here
		if (achievement.isAchieved()) {
			return false;
		}
		achievement.incrementValue();
		if (!achievement.needsSubmit()) {
			return false;
		}
		if (showToast) {
			showToastForAchievement(achievement);
		}
		if (submitNow) {
			submitAchievements(null);
		}
		return true;
	}

	@Override
	public void init(final Context context, final String gameSecret) {
		_context = context;
		_client = new Client(_context, gameSecret, this);

		_configuration = new Configuration(_context, getSession());
		verifyManifest();

		final ScreenManager screenManager = new StandardScreenManager();
		screenManager.setDelegate(this);
		ScreenManagerSingleton.init(screenManager);

		Constant.setup();

		if (getConfiguration().isFeatureEnabled(Feature.PAYMENT)) {
			final PendingPaymentProcessor processor = PendingPaymentProcessor.getInstance(getSession());
			processor.addObserver(this);
		}
		_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				LocalImageStorage.get().tryPurge(context);
			}
		}, 1000 * 30);
	}

	@Override
	public boolean isAwardAchieved(final String awardId) {
		final Achievement achievement = getAchievement(awardId); // checks made here
		return achievement.isAchieved();
	}

	@Override
	public boolean isChallengeOngoing() {
		return getSession().getChallenge() != null;
	}

	@Override
	public void loadAchievements(final Continuation<Boolean> continuation) {
		checkHasAwards();
		getAchievementsEngine().loadAchievements(getConfiguration().isAchievementForceInitialSync(), continuation);
	}

	@Override
	public void onAgentDidFail(final BaseAgent agent, final RequestController controller, final Exception error) {
		// activity should show "it's a bit quiet in here"
	}

	@Override
	public void onGamePlayEnded(final Double scoreResult, final Integer mode) {
		final Score score = new Score(scoreResult, null);
		score.setMode(mode);
		onGamePlayEnded(score, null);
	}

	@Override
	public void onGamePlayEnded(final Score score, final Boolean submitLocallyOnly) {
		final Game game = getSession().getGame();
		// NOTE: discuss introduction of Game.isValidMode(Integer mode)
		if (game.hasModes()) {
			if (!score.hasMode()) {
				throw new IllegalArgumentException("the game has modes, but no mode was passed");
			}
			final Integer mode = score.getMode();
			final int minMode = game.getMinMode();
			final int maxMode = game.getMaxMode();
			if ((mode < minMode) || (mode >= maxMode)) {
				throw new IllegalArgumentException("mode out of range [" + minMode + "," + maxMode + "[");
			}
		}

		if (!game.hasModes() && score.hasMode()) {
			throw new IllegalArgumentException("the game has no modes, but a mode was passed");
		}

		_lastSubmitStatus = OnScoreSubmitObserver.STATUS_UNDEFINED;
		_lastSubmittedScore = null;
		_lastSubmittedChallenge = null;

		if (isChallengeOngoing()) {
			if (!_configuration.isFeatureEnabled(Configuration.Feature.CHALLENGE)) {
				throw new IllegalStateException(
						"we're in challenge mode, but the challenge feature is not enabled in the scoreloop.properties");
			}

			final Challenge challenge = getSession().getChallenge();
			if (challenge.isCreated()) {
				challenge.setContenderScore(score);
			}

			if (challenge.isAccepted()) {
				challenge.setContestantScore(score);
			}

			final ChallengeController challengeController = new ChallengeController(new ChallengeRequestControllerObserver(score));
			challengeController.setChallenge(challenge);
			challengeController.submitChallenge();
		} else {
			final ScoreController scoreController = new ScoreController(new ScoreRequestControllerObserver());
			scoreController.setShouldSubmitScoreLocally((submitLocallyOnly != null) && submitLocallyOnly);
			scoreController.submitScore(score);
		}
	}

	@Override
	public void onShortcut(final int textId) {
		if (textId == R.string.sl_home) {
			displayWithEmptyStack(createEntryScreenDescription());
		} else if (textId == R.string.sl_friends) {
			displayWithEmptyStack(createUserScreenDescription(null));
		} else if (textId == R.string.sl_market) {
			displayWithEmptyStack(createMarketScreenDescription(null));
		}
	}

	@Override
	public int paymentFinished(final GameItem gameItem, final int code) {

		// game-item was payed regularly (not-pending), so inform observer and ask it for a
		// show-toast intent
		if (_onPaymentChangedObserver != null) {
			return _onPaymentChangedObserver.onPaymentChanged(gameItem, code, false);
		}
		return OnPaymentChangedObserver.FLAG_TOAST_SHOW;
	}

	@Override
	public void pendingPaymentProcessorDidProcessPayment(final PendingPaymentProcessor processor, final Payment payment) {
		GameItemUtil.withGameItemForIdentifier(getSession(), payment.getGameItemIdentifier(), new Continuation<GameItem>() {
			@Override
			public void withValue(final GameItem gameItem, final Exception error) {
				if (gameItem == null) {
					Log.d(Constant.LOG_TAG, "can't retrieve game-item: " + error);
					return;
				}
				int resultCode = 0;
				switch (payment.getState()) {
				case BOOKED:
					resultCode = OnPaymentChangedObserver.RESULT_PAYMENT_BOOKED;
					break;
				case CANCELED:
					resultCode = OnPaymentChangedObserver.RESULT_PAYMENT_CANCELED;
					break;
				case FAILED:
					resultCode = OnPaymentChangedObserver.RESULT_PAYMENT_FAILED;
					break;
				}

				// if there is a payment changed observer, ask him what to show, otherwise show toast
				int flags;
				if (_onPaymentChangedObserver != null) {
					flags = _onPaymentChangedObserver.onPaymentChanged(gameItem, resultCode, true);
				} else {
					flags = OnPaymentChangedObserver.FLAG_TOAST_SHOW;
				}
				if ((flags & OnPaymentChangedObserver.FLAG_TOAST_SHOW) != 0) {
					AbstractCheckoutListActivity.showGameItemToast(_context, gameItem, resultCode, null);
				}
			}
		});
	}

	@Override
	public void persistSessionUserName() {
		final User sessionUser = getSession().getUser();
		if (sessionUser.isAuthenticated()) {
			final SharedPreferences.Editor preferences = getContext().getSharedPreferences(PREFERENCES_NAME, 0).edit();
			preferences.putString(PREFERENCES_ENTRY_USER_NAME, sessionUser.getDisplayName());
			preferences.putString(PREFERENCES_ENTRY_USER_IMAGE_URL, sessionUser.getImageUrl());
			preferences.commit();
		}
	}

	private class ScoreRequestControllerObserver implements RequestControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
			if (anException instanceof ScoreSubmitException) {
				_lastSubmitStatus = OnScoreSubmitObserver.STATUS_SUCCESS_LOCAL_SCORE;
			} else {
				_lastSubmitStatus = OnScoreSubmitObserver.STATUS_ERROR_NETWORK;
			}
			if (_onScoreSubmitObserver != null) {
				_onScoreSubmitObserver.onScoreSubmit(_lastSubmitStatus, anException);
			}
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
			if (aRequestController instanceof ScoreController) {
				final ScoreController scoreController = (ScoreController) aRequestController;
				_lastSubmittedScore = scoreController.getScore();
				if (scoreController.shouldSubmitScoreLocally()) {
					_lastSubmitStatus = OnScoreSubmitObserver.STATUS_SUCCESS_LOCAL_SCORE;
				} else {
					_lastSubmitStatus = OnScoreSubmitObserver.STATUS_SUCCESS_SCORE;
				}
				if (_onScoreSubmitObserver != null) {
					_onScoreSubmitObserver.onScoreSubmit(_lastSubmitStatus, null);
				}
			}

		}
	}

	private class ChallengeRequestControllerObserver implements RequestControllerObserver {
		private final Score	score;

		private ChallengeRequestControllerObserver(final Score score) {
			this.score = score;
		}

		@Override
		public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
			if ((anException instanceof RequestControllerException)
					&& ((RequestControllerException) anException).hasDetail(RequestControllerException.CHALLENGE_INSUFFICIENT_BALANCE)) {
				_lastSubmitStatus = OnScoreSubmitObserver.STATUS_ERROR_BALANCE;
			} else {
				_lastSubmitStatus = OnScoreSubmitObserver.STATUS_ERROR_NETWORK;
			}
			if (_onScoreSubmitObserver != null) {
				_onScoreSubmitObserver.onScoreSubmit(_lastSubmitStatus, anException);
			}
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
			if (aRequestController instanceof ChallengeController) {
				final ChallengeController challengeController = (ChallengeController) aRequestController;
				_lastSubmitStatus = OnScoreSubmitObserver.STATUS_SUCCESS_CHALLENGE;
				_lastSubmittedScore = score;
				_lastSubmittedChallenge = challengeController.getChallenge();
				if (_onScoreSubmitObserver != null) {
					_onScoreSubmitObserver.onScoreSubmit(_lastSubmitStatus, null);
				}
			}
		}
	}

	private void runSubmitLocalScoresContinuations() {
		final List<Runnable> continuations = _submitLocalScoresContinuations;
		_submitLocalScoresContinuations = new LinkedList<Runnable>();

		for (final Runnable continuation : continuations) {
			continuation.run();
		}
	}

	@Override
	public void screenManagerDidLeaveFramework(final ScreenManager manager) {
		_sessionUserValueStore = null;
		_sessionGameValueStore = null;
		_cachedUserValueStore = null;
		persistSessionUserName();
	}

	@Override
	public boolean screenManagerWantsNewScreen(final ScreenManager screenManager, final ScreenDescription description,
			final ScreenDescription referenceDescription) {

		// we want a new screen, when the user or game of the description has changed

		final ValueStore screenValues = description.getScreenValues();
		final ValueStore referenceScreenValues = referenceDescription.getScreenValues();

		final String userPath = ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.USER);
		final User user = screenValues.getValue(userPath);
		final User referenceUser = referenceScreenValues.getValue(userPath);
		if (!user.equals(referenceUser)) {
			return true;
		}

		final String gamePath = ValueStore.concatenateKeys(Constant.GAME_VALUES, Constant.GAME);
		final Game game = screenValues.getValue(gamePath);
		final Game referenceGame = referenceScreenValues.getValue(gamePath);
		return !game.equals(referenceGame);
	}

	@Override
	public void screenManagerWillEnterFramework(final ScreenManager manager) {
		final String userName = getPersistedUserName();
		if (userName != null) {
			getSession().getUser().setLogin(userName);
		}
		final String userImageUrl = getPersistedUserImageUrl();
		if (userImageUrl != null) {
			if (getSessionUserValues().getValue(Constant.USER_IMAGE_URL) == null) {
				getSessionUserValues().putValue(Constant.USER_IMAGE_URL, userImageUrl);
			}
		}
	}

	@Override
	public void screenManagerWillShowOptionsMenu() {
		// to nothing (may track event)
	}

	@Override
	public void screenManagerWillShowScreenDescription(final ScreenDescription screenDescription, final Direction direction) {
		// do nothing
	}

	@Override
	public void setAllowToAskUserToAcceptTermsOfService(final boolean ask) {
		_askUserForTermsAndConditions = ask;
	}

	@Override
	public void setOnCanStartGamePlayObserver(final OnCanStartGamePlayObserver observer) {
		_onCanStartGamePlayObserver = observer;
	}

	@Override
	public void setOnPaymentChangedObserver(final OnPaymentChangedObserver observer) {
		checkHasPaymentEnabled();
		_onPaymentChangedObserver = observer;
	}

	@Override
	public void setOnScoreSubmitObserver(final OnScoreSubmitObserver observer) {
		_onScoreSubmitObserver = observer;
	}

	@Override
	public void setOnStartGamePlayRequestObserver(final OnStartGamePlayRequestObserver observer) {
		_onStartGamePlayRequestObserver = observer;
	}

	private void showToastForAchievement(final Achievement achievement) {
		final String text = String.format(getContext().getString(R.string.sl_format_unlocked), achievement.getAward().getLocalizedTitle());
		BaseActivity.showToast(getContext(), text);
	}

	@Override
	public void showWelcomeBackToast(final long delay) {
		if (delay < 0) {
			throw new IllegalArgumentException("delay to showWelcomeBackToast must be zero or postive");
		}
		_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				final String userName = getPersistedUserName();
				if (userName != null) {
					final String message = String.format(getContext().getString(R.string.sl_format_welcome_back), userName);
					BaseActivity.showToast(getContext(), message, null, Toast.LENGTH_LONG);
				}
			}
		}, delay);
	}

	@Override
	public void startGamePlay(final Integer mode, final Challenge challenge) {
		if (_onStartGamePlayRequestObserver == null) {
			throw new IllegalStateException(
					"trying to start gameplay, but the callback is not set - did you call ScoreloopManagerSingleton.get().setOnStartGamePlayRequestObserver(...)?");
		}
		// challenge is not used, we always use the challenge from the session
		_onStartGamePlayRequestObserver.onStartGamePlayRequest(mode);
	}

	@Override
	public void submitAchievements(final Continuation<Boolean> continuation) {
		checkHasAwards();
		getAchievementsEngine().submitAchievements(getConfiguration().isAchievementForceInitialSync(), continuation);
	}

	@Override
	public void submitLocalScores(final Runnable continuation) {

		// if we are submitting local scores already, just add continuation to list of continuations
		if (_submitLocalScoresContinuations.size() > 0) {
			if (continuation != null) {
				_submitLocalScoresContinuations.add(continuation);
			}
			return;
		}

		new ScoresController(new RequestControllerObserver() {
			@Override
			public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
			}

			@Override
			public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
				if (aRequestController instanceof ScoresController) {
					final ScoresController scoresController = (ScoresController) aRequestController;

					// get list of local scores to submit
					final List<Score> scoresToSubmit = scoresController.getScores();

					// add continuation to list of continuations
					if (continuation != null) {
						_submitLocalScoresContinuations.add(continuation);
					}

					// nothing to submit, so run continuations
					if (scoresToSubmit.size() == 0) {
						runSubmitLocalScoresContinuations();
						return;
					}

					// iterate on scores to submit
					final ListIterator<Score> scoresIterator = scoresToSubmit.listIterator();
					final ScoreController scoreController = new ScoreController(getSession(), new RequestControllerObserver() {
						@Override
						public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {

							// ignore the error for now and continue with next score
							Log.w("ScoreloopUI", "failed to submit localScore: " + anException);
							submitNextScore((ScoreController) aRequestController);
						}

						@Override
						public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
							submitNextScore((ScoreController) aRequestController);
						}

						private void submitNextScore(final ScoreController controller) {
							if (scoresIterator.hasNext()) {
								controller.submitScore(scoresIterator.next());
							} else {
								runSubmitLocalScoresContinuations();
							}
						}
					});
					scoreController.submitScore(scoresIterator.next());

				}
			}
		}).loadLocalScoresToSubmit();

	}

	@Override
	public void trackEvent(final String category, final String action, final String label, final int value) {
		// no tracking
	}

	@Override
	public void trackPageView(final String activityClassName, final ValueStore arguments) {
		// no tracking
	}

	@Override
	public boolean userRejectedTermsOfService(final Continuation<Boolean> notification) {
		_rejectedTermsOfServiceNotification = notification;
		return getSession().getUsersTermsOfService().getStatus() == TermsOfService.Status.REJECTED;
	}

	private void verifyManifest() {
		final Checker checker = new Checker(getContext(), getConfiguration());

		final CheckerRun required = checker.createActivityRun();
		required.add("com.scoreloop.client.android.core.ui.ProxyActivity", null, "configChanges", ActivityInfo.CONFIG_KEYBOARD
				| ActivityInfo.CONFIG_KEYBOARD_HIDDEN | ActivityInfo.CONFIG_ORIENTATION, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");

		required.add("com.scoreloop.client.android.ui.framework.ScreenActivity", null, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		required.add("com.scoreloop.client.android.ui.framework.TabsActivity");
		required.add("com.scoreloop.client.android.ui.component.market.MarketHeaderActivity");
		required.add("com.scoreloop.client.android.ui.component.market.MarketListActivity");
		required.add("com.scoreloop.client.android.ui.component.entry.EntryListActivity");
		required.add("com.scoreloop.client.android.ui.component.post.PostOverlayActivity", null, "theme", R.style.sl_dialog);
		required.add("com.scoreloop.client.android.ui.component.score.ScoreHeaderActivity");
		required.add("com.scoreloop.client.android.ui.component.score.ScoreListActivity");
		required.add("com.scoreloop.client.android.ui.component.user.UserAddBuddyListActivity");
		required.add("com.scoreloop.client.android.ui.component.user.UserHeaderActivity");
		required.add("com.scoreloop.client.android.ui.component.user.UserDetailListActivity");
		required.add("com.scoreloop.client.android.ui.component.user.UserListActivity");
		required.add("com.scoreloop.client.android.ui.component.game.GameDetailHeaderActivity");
		required.add("com.scoreloop.client.android.ui.component.game.GameDetailListActivity");
		required.add("com.scoreloop.client.android.ui.component.game.GameListActivity");
		required.add("com.scoreloop.client.android.ui.component.achievement.AchievementHeaderActivity", Feature.ACHIEVEMENT);
		required.add("com.scoreloop.client.android.ui.component.achievement.AchievementListActivity", Feature.ACHIEVEMENT);
		required.add("com.scoreloop.client.android.ui.component.news.NewsHeaderActivity", Feature.NEWS);
		required.add("com.scoreloop.client.android.ui.component.news.NewsListActivity", Feature.NEWS);
		required.add("com.scoreloop.client.android.ui.component.challenge.ChallengeHeaderActivity", Feature.CHALLENGE);
		required.add("com.scoreloop.client.android.ui.component.challenge.ChallengeListActivity", Feature.CHALLENGE);
		required.add("com.scoreloop.client.android.ui.component.challenge.ChallengeAcceptListActivity", Feature.CHALLENGE);
		required.add("com.scoreloop.client.android.ui.component.challenge.ChallengeCreateListActivity", Feature.CHALLENGE);
		required.add("com.scoreloop.client.android.ui.component.profile.ProfileSettingsListActivity");
		required.add("com.scoreloop.client.android.ui.component.profile.ProfileSettingsPictureListActivity");
		required.add("com.scoreloop.client.android.ui.component.payment.PaymentHeaderActivity", Feature.PAYMENT);
		required.add("com.scoreloop.client.android.ui.component.payment.GameItemHeaderActivity", Feature.PAYMENT);
		required.add("com.scoreloop.client.android.ui.component.payment.GameItemListActivity", Feature.PAYMENT);
		required.add("com.scoreloop.client.android.ui.component.payment.PaymentMethodListActivity", Feature.PAYMENT, "configChanges",
				ActivityInfo.CONFIG_KEYBOARD | ActivityInfo.CONFIG_KEYBOARD_HIDDEN | ActivityInfo.CONFIG_ORIENTATION);
		required.add("com.scoreloop.client.android.ui.component.payment.PriceListActivity", Feature.PAYMENT, "configChanges",
				ActivityInfo.CONFIG_KEYBOARD | ActivityInfo.CONFIG_KEYBOARD_HIDDEN | ActivityInfo.CONFIG_ORIENTATION);

		// payment provider specific stuff
		required.add("com.paypal.android.MEP.PayPalActivity", Feature.PAYMENT_PAYPALX, "configChanges", ActivityInfo.CONFIG_KEYBOARD_HIDDEN
				| ActivityInfo.CONFIG_ORIENTATION, "theme", "@android:style/Theme.Translucent.NoTitleBar");
		required.add("com.fortumo.android.FortumoActivity", Feature.PAYMENT_FORTUMO, "theme", "@android:style/Theme.Translucent.NoTitleBar");

		required.reportRequired();

		final CheckerRun optional = checker.createActivityRun();
		optional.add("com.scoreloop.client.android.ui.EntryScreenActivity", null, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.PostScoreOverlayActivity", null, R.style.sl_dialog);
		optional.add("com.scoreloop.client.android.ui.ShowResultOverlayActivity", null, R.style.sl_dialog);
		optional.add("com.scoreloop.client.android.ui.BuddiesScreenActivity", null, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.LeaderboardsScreenActivity", null, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.ChallengesScreenActivity", Feature.CHALLENGE, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.AchievementsScreenActivity", Feature.ACHIEVEMENT, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.SocialMarketScreenActivity", null, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.ProfileScreenActivity", null, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.PaymentScreenActivity", Feature.PAYMENT, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.add("com.scoreloop.client.android.ui.GameItemsScreenActivity", Feature.PAYMENT, "theme",
				"@android:style/Theme.Black.NoTitleBar.Fullscreen");
		optional.reportOptional();

		// verify permissions
		// Note: READ_PHONE_STATE and INTERNET are checked in ScoreloopCores
		final CheckerRun permission = checker.createUsesPermissionRun();
		permission.add("android.permission.READ_CONTACTS", Feature.ADDRESS_BOOK);
		permission.reportRequired();

		required.check();
		optional.check();
		permission.check();
	}

	@Override
	public void wasGameItemPurchasedBefore(final String gameItemIdentifier, final Continuation<Boolean> continuation) {
		if ((gameItemIdentifier == null) || (gameItemIdentifier.length() == 0)) {
			throw new IllegalArgumentException("invalid gameItemIdentifier");
		}
		if (continuation == null) {
			throw new IllegalArgumentException("continuation must not be null");
		}
		checkHasPaymentEnabled();

		GameItemUtil.withGameItemForIdentifier(getSession(), gameItemIdentifier, new Continuation<GameItem>() {
			@Override
			public void withValue(final GameItem gameItem, final Exception error) {
				if (gameItem != null) {
					continuation.withValue(gameItem.isPurchased(), null);
				} else {
					continuation.withValue(null, error);
				}
			}
		});
	}

	@Override
	public Set<String> getSupportedPaymentProviderKinds() {
		return _client.getSupportedPaymentProviderKinds();
	}

}
