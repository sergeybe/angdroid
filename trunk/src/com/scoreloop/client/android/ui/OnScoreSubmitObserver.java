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

/**
 * The OnScoreSubmitObserver receives notifications and callbacks
 * from the server after a score has been submitted to Scoreloop. 
 * The observer must be correctly set in the 
 * @link #com.scoreloop.client.android.ui.ScoreloopManager ScoreloopManager
 * @endlink class before a score submission is made. 
 *
 * Basic Usage:
 * -# Implement the onScoreSubmit(final int, final Exception) method.
 * -# Set the observer using ScoreloopManager.setOnScoreSubmitObserver(OnScoreSubmitObserver).
 *
 */
public interface OnScoreSubmitObserver {

	/**
	 * This code is returned via the 
	 * OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)
	 * in cases where a score failed to be submitted to Scoreloop
	 * as part of a challenge due to the user's balance being too 
	 * low to cover the balance stake.
	 */
	public static final int	STATUS_ERROR_BALANCE		= 5;

	/**
	 * This code is returned via the 
	 * OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)
	 * callback
	 * in cases where a score failed to be submitted to Scoreloop
	 * due to a network error.
	 */
	public static final int	STATUS_ERROR_NETWORK		= 4;

	/**
	 * This code is returned via the 
	 * OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)
	 * callback in cases where a score was successfully submitted as 
	 * part of a challenge.
	 */
	public static final int	STATUS_SUCCESS_CHALLENGE	= 3;

	/**
	 * This code is returned via the
	 * OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)
	 * in cases where a score is saved to the local (offline) leaderboard only.
	 */
	public static final int	STATUS_SUCCESS_LOCAL_SCORE	= 2;

	/**
	 * This code is returned via the 
	 * OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)
	 * callback in cases where a score was successfully submitted.
	 */
	public static final int	STATUS_SUCCESS_SCORE		= 1;

	/**
	 * \internal
	 */
	public static final int	STATUS_UNDEFINED			= 0;

	/**
	 * This method is called after a score
	 * has been submitted to the server. 
	 * It will normally be implemented within 
	 * the gameplay. In the case of a successful response:
	 * - the developer should display a toast notification if required;
	 * ScoreloopUI does not display a message by default.
	 * - the developer may wish to open an activity
	 * to post the score to Facebook or Twitter,
	 * see @link scoreloopui-integratescores Scores and Leaderboards@endlink for more details..
	 *
	 * In the case of an unsuccessful response ScoreloopUI will 
	 * display an error notification.
	 * 
	 * @param status The status code, can be either 
	 * @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.STATUS_SUCCESS_SCORE STATUS_SUCCESS_SCORE @endlink, 
	 * @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.STATUS_SUCCESS_CHALLENGE STATUS_SUCCESS_CHALLENGE @endlink, 
	 * @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.STATUS_SUCCESS_LOCAL_SCORE STATUS_SUCCESS_LOCAL_SCORE @endlink, 
	 * @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.STATUS_ERROR_NETWORK STATUS_ERROR_NETWORK @endlink, or 
	 * @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.STATUS_ERROR_BALANCE STATUS_ERROR_BALANCE @endlink. 
	 * @param error <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Exception.html">java.lang.Exception</a> If the submission was unsuccessful this object will hold the reason for the error.
	 */
	void onScoreSubmit(final int status, final Exception error);

}
