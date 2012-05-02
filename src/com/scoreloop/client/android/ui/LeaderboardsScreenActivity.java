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

package com.scoreloop.client.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.framework.ScreenActivity;

/**
 * The LeaderboardsScreenActivity displays the leaderboards
 * for the game. Leaderboards are sorted lists of the best
 * scores submitted by players of the game. The user
 * can switch between:
 * - the global leaderboard, (showing
 *   scores submitted by all users), 
 * - a leaderboard consisting of the user's buddies 
 *   and the user,
 * - the 24 hour leaderboard, (consisting of the best
 *   scores submitted in the previous 24 hours).
 * 
 * By default the LeaderboardsScreenActivity starts with the 
 * global leaderboard and the first mode in the game, (mode 0). 
 * This can be overridden by setting the
 * extra data LEADERBOARD and/or GAME in the intent.
 * 
 * -# Ensure that the ScoreloopManagerSingleton has been correctly
 *    intialized.
 * -# Set the LEADERBOARD and/or MODE properties to override
 *    the leaderboard defaults, if required.
 * -# Start the LeaderboardsScreenActivity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 *
 * \sa @link scoreloopui-integrateleaderboards Leaderboards Integration Guide@endlink
 */
public class LeaderboardsScreenActivity extends ScreenActivity {

	/**
	* A key identifying a leaderboard. This is used 
	* in the construction of new Android
	* <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a> objects.
	 */
	public static final String	LEADERBOARD			= "leaderboard";

	/**
	* This code is used to identify the leaderboard
	* based on the best scores submitted in the previous 24 hours.
	 */
	public static final int		LEADERBOARD_24h		= 2;

	/**
	* This code is used to identify the leaderboard
	* based on scores submitted by the user's friends. 
	 */
	public static final int		LEADERBOARD_FRIENDS	= 1;

	/**
	* This code is used to identify the leaderboard
	* based on all scores submitted by all users.
	 */
	public static final int		LEADERBOARD_GLOBAL	= 0;

	/**
	* This code is used to identify the local (offline) leaderboard.
	* Only scores submitted to this local leadearboard will be show.
	 */
	public static final int		LEADERBOARD_LOCAL	= Constant.LEADERBOARD_LOCAL;

	/**
	* A key identifying a mode. This is used in the 
	* construction of new Android
	* <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a> objects.
	 */
	public static final String	MODE				= "mode";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();

		Integer mode = null;
		if (intent.hasExtra(MODE)) {
			mode = intent.getIntExtra(MODE, 0);
			final Session session = ScoreloopManagerSingleton.getImpl().getSession();
			final Game game = session.getGame();
			final Integer minMode = game.getMinMode();
			final Integer maxMode = game.getMaxMode();
			if ((minMode != null) && (maxMode != null) && ((mode < minMode) || (mode >= maxMode))) {
				Log.e(Constant.LOG_TAG, "mode extra parameter on LeaderboardsScreenActivity is out of range [" + minMode + "," + maxMode
						+ "[");
				finish();
				return;
			}
		}

		Integer leaderboard = null;
		if (intent.hasExtra(LEADERBOARD)) {
			leaderboard = intent.getIntExtra(LEADERBOARD, 0);
			if ((leaderboard < LEADERBOARD_GLOBAL) || (leaderboard > LEADERBOARD_LOCAL)) {
				Log.e(Constant.LOG_TAG, "leaderboard extra parameter on LeaderboardsScreenActivity is invalid");
				finish();
				return;
			}
		}

		final Factory factory = ScoreloopManagerSingleton.getImpl().getFactory();
		display(factory.createScoreScreenDescription(null, mode, leaderboard), savedInstanceState);
	}
}
