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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.IllegalFormatException;
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
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.ScoreFormatter.ScoreFormatKey;

public class Configuration {

	static class ConfigurationException extends IllegalStateException {
		private static final String	SCORELOOP_UI		= "ScoreloopUI";
		private static final long	serialVersionUID	= 1L;

		ConfigurationException(final String message) {
			super(message);
			Log.e(SCORELOOP_UI, "=====================================================================================");
			Log.e(SCORELOOP_UI, "scoreloop.properties file verification error. Please resolve any issues first!");
			Log.e(SCORELOOP_UI, message);
		}
	}

	public static enum Feature {
		ACHIEVEMENT("ui.feature.achievement", false), ADDRESS_BOOK("ui.feature.address_book", true), CHALLENGE("ui.feature.challenge",
				false), NEWS("ui.feature.news", false);

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

	private static final String	FORMAT_MONEY_KEY		= "ui.format.money";
	private static final String	FORMAT_SCORE_RESULT_KEY	= "ui.format.score.result";
	private static final String	FORMAT_SCORE_LEADERBOARD = "ui.format.score.leaderboard";
	private static final String	FORMAT_SCORE_CHALLENGES = "ui.format.score.challenges";
	private static final String	FORMAT_SCORE_SOCIAL_NETWORK_POST = "ui.format.score.socialnetworkpost";
	private static final String	FORMAT_SCORE_KEY		= "ui.format.score";
    private static final String	RES_MODES_NAME			= "ui.res.modes.name";
    private static final String	ACHIVEMENT_INITIAL_SYNC			= "ui.feature.achievement.forceSync";

	private int					_modesResId;
	// NOTE: see for formatting conventions: http://developer.android.com/reference/java/util/Formatter.html
	private String				_moneyFormat			= "%.2f %s";
	private String				_scoreResultFormat;
	private ScoreFormatKey		_leaderboardScoreFormat;
	private ScoreFormatKey		_challengesScoreFormat;
	private ScoreFormatKey		_socialNetworkPostScoreFormat;
	private ScoreFormatter		_scoreFormatter;
	private String[]			_modesNames;
	private boolean				_achievementForceInitialSync = true;

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

		// read the score formatting properties
		_scoreResultFormat = properties.getProperty(FORMAT_SCORE_RESULT_KEY);
		if (_scoreResultFormat != null) {
			_scoreResultFormat = _scoreResultFormat.trim();
		}
		_scoreFormatter = new ScoreFormatter(properties.getProperty(FORMAT_SCORE_KEY));
		_leaderboardScoreFormat = loadScoreFormatProperty(properties, FORMAT_SCORE_LEADERBOARD);
		_challengesScoreFormat = loadScoreFormatProperty(properties, FORMAT_SCORE_CHALLENGES);
		_socialNetworkPostScoreFormat = loadScoreFormatProperty(properties, FORMAT_SCORE_SOCIAL_NETWORK_POST);

		game = session.getGame();
		if (game != null && game.hasModes()) {
			int minMode = game.getMinMode();
			int modeCount = game.getModeCount();
			
			_modesNames = new String[modeCount];
			for (int i = minMode; i < minMode + modeCount; i++) {
	            _modesNames[i] = _scoreFormatter.formatScore(new Score(null, Collections.<String, Object>singletonMap(Game.CONTEXT_KEY_MODE, i)), ScoreFormatKey.ModeOnlyFormat);
			}
		} else {
			_modesNames = new String[0];
		}

		_moneyFormat = properties.getProperty(FORMAT_MONEY_KEY, _moneyFormat).trim();

		// read the modes property
		// this property is deprecated - modes should be defined as part of FORMAT_SCORE_KEY
		final String modesResName = properties.getProperty(RES_MODES_NAME);
		if (modesResName != null) {
			_modesResId = context.getResources().getIdentifier(modesResName.trim(), "array", context.getPackageName());
			Log.i("test", "Type: " + context.getResources().getResourceTypeName(_modesResId));
		}

		// read other properties here...

		// check configuration
		verifyConfiguration(context, session);
	}

	/**
	 * Just for backward compatibility. Modes should be configured in score formatter.
	 * @see {@link #getModesNames()}
	 */
	public int getModesResId() {
		return _modesResId;
	}

	public String[] getModesNames() {
		return _modesNames;
	}

	public String getMoneyFormat() {
		return _moneyFormat;
	}

    public boolean isAchievementForceInitialSync() {
        return _achievementForceInitialSync;
    }

    public ScoreFormatKey getLeaderboardScoreFormat() {
		return _leaderboardScoreFormat;
	}

	public ScoreFormatKey getChallengesScoreFormat() {
		return _challengesScoreFormat;
	}

	public ScoreFormatKey getSocialNetworkPostScoreFormat() {
		return _socialNetworkPostScoreFormat;
	}

	/**
     * used ScoreFormatter with key ui.format.score instead
     * just for backward compatibility
     */
	public String getScoreResultFormat() {
		return _scoreResultFormat;
	}

	public ScoreFormatter getScoreFormatter() {
		return _scoreFormatter;
	}

	public boolean isFeatureEnabled(final Feature feature) {
		return feature.isEnabled();
	}

	private ScoreFormatKey loadScoreFormatProperty(Properties properties, String propertyName) {
		String format = properties.getProperty(propertyName);
		ScoreFormatKey scoreFormatKey = null;

		if (format != null) {
			scoreFormatKey = ScoreFormatKey.parse(format);
			if (scoreFormatKey == null) {
				throw new ConfigurationException("invalid " + propertyName+ " value (unrecognized format key): " + format);
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
		if (Feature.ACHIEVEMENT._isEnabled) {
			final AchievementsController controller = new AchievementsController(new RequestControllerObserver() {
				public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
				}

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
		if (game != null && game.hasModes()) {
			final int modeCount = game.getModeCount();
			if (_modesResId == 0) {
				int minMode = session.getGame().getMinMode();
				String[] modesNames = _scoreFormatter.getDefinedModesNames(minMode, modeCount);
				// verify modes from score formatter
				for (int i = 0; i < modesNames.length; i++) {
					if (modesNames[i] == null) {
						throw new ConfigurationException("no name configured for mode " + (minMode + i) + " - check " + FORMAT_SCORE_KEY);
					}
				}
			} else {
				// kept for backward compatibility
				final String[] modeStrings = context.getResources().getStringArray(_modesResId);
				if ((modeStrings == null) || (modeStrings.length != modeCount)) {
					throw new ConfigurationException("your modes string array must have exactily " + modeCount + " entries!");
				}
			}
		}

		// check that the money and score formatters are ok
		try {
			String.format(_moneyFormat, BigDecimal.ONE, "$");
		} catch (final IllegalFormatException exception) {
			throw new ConfigurationException("invalid " + FORMAT_MONEY_KEY
					+ " value: must contain valid %f and %s specifiers in that order. " + exception.getLocalizedMessage());
		}
	}
}
