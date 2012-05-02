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
 * The AchievementsScreenActivity displays the awards that may 
 * be achieved in the game. It also shows whether the user has
 * achieved them. Awards that have been achieved are clickable. Clicking
 * an achieved award opens a dialog that enables the user to post
 * a message about the award to a social network. Awards must be configured on https://developer.scoreloop.com and the
 * awards bundle generated and added to your project. 
 * 
 * For the AchievementsScreenActivity to be available the scoreloop.properties
 * file must be configured correctly:
 * <table><tr><td>
 * @code
 *  ui.feature.achievement = true
 * @endcode
 * </td></tr></table>
 * 
 * Basic Usage:
 * -# Configure the scoreloop.properties file correctly.
 * -# Ensure that the ScoreloopManagerSingleton has been properly initialized.
 * -# Start the AchievementsScreenActivity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 *
 * \sa @link scoreloopui-integrateawards Awards and Achievements Integration Guide@endlink
 */
public class AchievementsScreenActivity extends ScreenActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Configuration configuration = ScoreloopManagerSingleton.getImpl().getConfiguration();
		if (!configuration.isFeatureEnabled(Configuration.Feature.ACHIEVEMENT)) {
			finish();
			return;
		}
		final Factory factory = ScoreloopManagerSingleton.getImpl().getFactory();
		display(factory.createAchievementScreenDescription(null), savedInstanceState);
	}
}
