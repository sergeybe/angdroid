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

package com.scoreloop.client.android.ui.component.base;

import java.util.Collections;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

import com.scoreloop.client.android.core.controller.AchievementsController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Client;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.ScoreFormatter;
import com.scoreloop.client.android.core.model.ScoreFormatter.ScoreFormatKey;
import com.scoreloop.client.android.core.model.Session;

public class Configuration {

	static class ConfigurationException extends IllegalStateException {
		private static final long	serialVersionUID	= 1L;

		ConfigurationException(final String message) {
			super(message);
			Log.e(Constant.LOG_TAG, "=====================================================================================");
			Log.e(Constant.LOG_TAG, "scoreloop.properties file verification error. Please resolve any issues first!");
			Log.e(Constant.LOG_TAG, message);
		}
	}

	public static enum Feature {
		ACHIEVEMENT("ui.feature.achievement", false), ADDRESS_BOOK("ui.feature.address_book", true), CHALLENGE("ui.feature.challenge",
				false), NEWS("ui.feature.news", false), PAYMENT("ui.feature.payment", false), PAYMENT_FORTUMO("payment.fortumo", false), PAYMENT_GOOGLEMARKET(
				"payment.googlemarket", false), PAYMENT_PAYPALX("payment.paypalx", false);

		private boolean	_isEnabled	= true;
		private String	_propertyName;

		Feature(final String propertyName, final boolean preset) {
			_propertyName = propertyName;
			_isEnabled = preset;
		}

		String getPropertyName() {
			return _propertyName;
		}

		boolean isEnabled() {
			return _isEnabled;
		}

		void setEnabled(final boolean value) {
			_isEnabled = value;
		}
	}

	private static final String		ACHIVEMENT_INITIAL_SYNC				= "ui.feature.achievement.forceSync";
	private static final String		FORMAT_SCORE_CHALLENGES				= "ui.format.score.challenges";
	private static final String		FORMAT_SCORE_LEADERBOARD			= "ui.format.score.leaderboard";
	private static final String		FORMAT_SCORE_SOCIAL_NETWORK_POST	= "ui.format.score.socialnetworkpost";

	private boolean					_achievementForceInitialSync		= true;
	private final ScoreFormatKey	_challengesScoreFormat;
	private final ScoreFormatKey	_leaderboardScoreFormat;
	private String[]				_modesNames;
	private final ScoreFormatKey	_socialNetworkPostScoreFormat;

	public Configuration(final Context context, final Session session) {
		final Properties properties = Client.getProperties(context);
		final Game game;

		// read and verify the feature flags
		final Feature features[] = Feature.values();
		for (int i = 0; i < features.length; ++i) {
			final Feature feature = features[i];
			final String property = feature.getPropertyName();

			final String value = properties.getProperty(property);
			if (value != null) {
				feature.setEnabled(verifyBooleanProperty(value.trim(), property));
			}
		}

		final String value = properties.getProperty(ACHIVEMENT_INITIAL_SYNC);
		if (value != null) {
			_achievementForceInitialSync = verifyBooleanProperty(value.trim(), ACHIVEMENT_INITIAL_SYNC);
		}

		_leaderboardScoreFormat = loadScoreFormatProperty(properties, FORMAT_SCORE_LEADERBOARD);
		_challengesScoreFormat = loadScoreFormatProperty(properties, FORMAT_SCORE_CHALLENGES);
		_socialNetworkPostScoreFormat = loadScoreFormatProperty(properties, FORMAT_SCORE_SOCIAL_NETWORK_POST);

		game = session.getGame();
		if ((game != null) && game.hasModes()) {
			final int minMode = game.getMinMode();
			final int modeCount = game.getModeCount();

			_modesNames = new String[modeCount];
			for (int i = minMode; i < (minMode + modeCount); i++) {
				_modesNames[i - minMode] = ScoreFormatter.getDefaultScoreFormatter()
						.formatScore(new Score(null, Collections.<String, Object> singletonMap(Game.CONTEXT_KEY_MODE, i)),
								ScoreFormatKey.ModeOnlyFormat);
			}
		} else {
			_modesNames = new String[0];
		}

		if (properties.containsKey("ui.format.score")) {
			throw new ConfigurationException("Property \"ui.format.score\" is no longer supported. Please use \"format.score\".");
		}

		if (properties.containsKey("ui.format.money")) {
			throw new ConfigurationException("Property \"ui.format.money\" is no longer supported. Please use \"format.money\".");
		}

		// check configuration
		verifyConfiguration(context, session);
	}

	public ScoreFormatKey getChallengesScoreFormat() {
		return _challengesScoreFormat;
	}

	public ScoreFormatKey getLeaderboardScoreFormat() {
		return _leaderboardScoreFormat;
	}

	public String[] getModesNames() {
		return _modesNames;
	}

	public ScoreFormatKey getSocialNetworkPostScoreFormat() {
		return _socialNetworkPostScoreFormat;
	}

	public boolean isAchievementForceInitialSync() {
		return _achievementForceInitialSync;
	}

	public boolean isFeatureEnabled(final Feature feature) {
		return feature.isEnabled();
	}

	private ScoreFormatKey loadScoreFormatProperty(final Properties properties, final String propertyName) {
		final String format = properties.getProperty(propertyName);
		ScoreFormatKey scoreFormatKey = null;

		if (format != null) {
			scoreFormatKey = ScoreFormatKey.parse(format);
			if (scoreFormatKey == null) {
				throw new ConfigurationException("invalid " + propertyName + " value (unrecognized format key): " + format);
			}
		}

		return scoreFormatKey;
	}

	private boolean verifyBooleanProperty(final String value, final String property) {
		if (value.equalsIgnoreCase("false")) {
			return false;
		} else if (value.equalsIgnoreCase("true")) {
			return true;
		} else {
			throw new ConfigurationException("property " + property + " must be either 'true' or 'false'");
		}
	}

	protected void verifyConfiguration(final Context context, final Session session) {

		// check if we have an achievements bundle if achievements are enabled
		if (isFeatureEnabled(Feature.ACHIEVEMENT)) {
			final AchievementsController controller = new AchievementsController(new RequestControllerObserver() {
				@Override
				public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
				}

				@Override
				public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
				}
			});
			if (controller.getAwardList() == null) {
				throw new ConfigurationException(
						"when you enable the achievement feature you also have to provide an SLAwards.bundle in the assets folder");
			}
		}

		// check that we have a valid modes resource if the game has modes
		final Game game = session.getGame();
		if ((game != null) && game.hasModes()) {
			final int modeCount = game.getModeCount();
			final int minMode = session.getGame().getMinMode();
			final String[] modesNames = ScoreFormatter.getDefaultScoreFormatter().getDefinedModesNames(minMode, modeCount);
			// verify modes from score formatter
			for (int i = 0; i < modesNames.length; i++) {
				if (modesNames[i] == null) {
					throw new ConfigurationException("no name configured for mode " + (minMode + i));
				}
			}
		}
	}
}
