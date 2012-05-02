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

import com.scoreloop.client.android.core.model.Entity;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.component.post.PostOverlayActivity;

/**
 * The PostScoreOverlayActivity displays a dialog overlay that
 * enables the user to post the score to a social network.
 * The activity should be called:
 * - after gameplay has ended,
 * - after the score has been submitted to Scoreloop.
 *
 * Basic Usage:
 * -# Ensure that the ScoreloopManagerSingleton has been
 *    properly intialized.
 * -# Start the PostOverlayActivity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 *
 * \sa @link scoreloopui-integratescores Score Submission Integration Guide@endlink
 */
public class PostScoreOverlayActivity extends PostOverlayActivity {

	@Override
	protected Entity getMessageTarget() {
		final ScoreloopManagerSingleton.Impl impl = ScoreloopManagerSingleton.getImpl();
		Entity target = impl.getLastSubmittedChallenge();
		if ((target == null) || (target.getIdentifier() == null)) {
			target = impl.getLastSubmittedScore();
		}
		return target;
	}

	@Override
	protected String getPostText() {
		final Entity target = getMessageTarget();
		if (target instanceof Score) {
			final Configuration configuration = ScoreloopManagerSingleton.getImpl().getConfiguration();
			return "Score: " + StringFormatter.formatSocialNetworkPostScore((Score) target, configuration);
		} else {
			return "Challenge";
		}
	}
}
