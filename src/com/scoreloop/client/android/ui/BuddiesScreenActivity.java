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

import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.framework.ScreenActivity;

/**
 * The BuddiesScreenActivity lists other Scoreloop players that the
 * user has designated as friends or "buddies". From the buddies-screen
 * the user can inspect individual buddies and their status. 
 *
 * Basic Usage:
 * -# Ensure the buddies feature is correctly configured in the 
 *    scoreloop.properties file, (it is enabled by default).
 * -# Make sure the ScoreloopManagerSingleton has been properly
 *    intialized. 
 * -# Start the BuddiesScreenActivity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 *
 * \sa @link scoreloopui-integratebuddies Buddies Integration Guide@endlink
 */
public class BuddiesScreenActivity extends ScreenActivity {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Factory factory = ScoreloopManagerSingleton.getImpl().getFactory();
		display(factory.createUserScreenDescription(null), savedInstanceState);
	}
}
