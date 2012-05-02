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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.angdroid.angband.R;

import com.scoreloop.client.android.core.model.Challenge;

/**
 * You can use this activity to show the result of a game play to the player. Start this activity after receiving an @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.onScoreSubmit() onScoreSubmit() @endlink callback.
 * 
 * Ensure that you have properly initialized the ScoreloopManagerSingleton,
 * before starting this activity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 */
public class ShowResultOverlayActivity extends Activity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_result);

		final ScoreloopManagerSingleton.Impl impl = ScoreloopManagerSingleton.getImpl();

		final int lastStatus = impl.getLastSubmitStatus();

		String text = "";
		switch (lastStatus) {
		case OnScoreSubmitObserver.STATUS_SUCCESS_SCORE:
			text = getResources().getString(R.string.sl_status_success_score);
			break;
		case OnScoreSubmitObserver.STATUS_SUCCESS_LOCAL_SCORE:
			text = getResources().getString(R.string.sl_status_success_local_score);
			break;
		case OnScoreSubmitObserver.STATUS_SUCCESS_CHALLENGE:
			final Challenge challenge = impl.getLastSubmittedChallenge();
			if (challenge.isOpen() || challenge.isAssigned()) {
				text = getResources().getString(R.string.sl_status_success_challenge_created);
			} else if (challenge.isComplete()) {
				if (ScoreloopManagerSingleton.getImpl().getSession().getUser().equals(challenge.getWinner())) {
					text = getResources().getString(R.string.sl_status_success_challenge_won);
				} else {
					text = getResources().getString(R.string.sl_status_success_challenge_lost);
				}
			}
			break;
		case OnScoreSubmitObserver.STATUS_ERROR_NETWORK:
			text = getResources().getString(R.string.sl_status_error_network);
			break;
		case OnScoreSubmitObserver.STATUS_ERROR_BALANCE:
			text = getResources().getString(R.string.sl_status_error_balance);
			break;
		default:
			throw new IllegalStateException(
					"this should not happen - make sure to start ShowResultOverlayActivity only after onScoreSubmit() was called");
		}

		final TextView textView = (TextView) findViewById(R.id.sl_text);
		textView.setText(text);

		final Button button = (Button) findViewById(R.id.sl_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				finish();
			}
		});
	}
}
