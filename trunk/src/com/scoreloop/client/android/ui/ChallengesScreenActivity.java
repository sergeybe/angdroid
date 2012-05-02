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

import android.os.Bundle;

import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.framework.ScreenActivity;

/**
 * The ChallengesScreenActivity presents the interface for 
 * Scoreloop challenges. It lists challenges that are available 
 * for the user to play, as well as challenges that the user
 * has completed. From this screen the user can start a new challenge and
 * may challenge a buddy, (a "direct" challenge), or an anonymous user,
 * (an "open" challenge). 
 *
 * For the ChallengesScreenActivity to be available the scoreloop.properties
 * file must be configured correctly. 
 * <table><tr><td>
 * @code
 * ui.feature.challenge = true
 * @endcode
 * </td></tr></table>
 * 
 * Basic Usage:
 * -# Ensure that the scoreloop.properties file has been properly configured.
 * -# Make sure that the ScoreloopManagerSingleton has been properly 
 * initialized.
 * -# Start the ChallengesScreenActivity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 *
 * \sa @link scoreloopui-integratechallenges Challenges Integration Guide@endlink
 */
public class ChallengesScreenActivity extends ScreenActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Configuration configuration = ScoreloopManagerSingleton.getImpl().getConfiguration();
		if (!configuration.isFeatureEnabled(Configuration.Feature.CHALLENGE)) {
			finish();
			return;
		}
		final Factory factory = ScoreloopManagerSingleton.getImpl().getFactory();
		display(factory.createChallengeScreenDescription(null), savedInstanceState);
	}
}
