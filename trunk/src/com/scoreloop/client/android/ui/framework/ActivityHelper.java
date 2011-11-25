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

package com.scoreloop.client.android.ui.framework;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ViewSwitcher;

import org.angdroid.angband.R;

public abstract class ActivityHelper {

	public static final int	ANIM_NEXT		= 1;
	public static final int	ANIM_NONE		= 0;
	public static final int	ANIM_PREVIOUS	= 2;

	public static void startLocalActivity(final ActivityGroup activityGroup, final Intent intent, final String identifier,
			final int regionId, final int anim) {
		final LocalActivityManager activityManager = activityGroup.getLocalActivityManager();
		final View paneView = activityManager.startActivity(identifier, intent).getDecorView();

		final ViewParent parent = paneView.getParent();
		if ((parent != null) && (parent instanceof ViewGroup)) {
			throw new IllegalStateException("should not happen - currently we don't recycle activities");
		}

		final ViewGroup region = (ViewGroup) activityGroup.findViewById(regionId);
		if ((anim != ANIM_NONE) && (region instanceof ViewSwitcher)) {
			final ViewSwitcher animator = (ViewSwitcher) region;

			if (anim == ANIM_NEXT) {
				animator.setInAnimation(activityGroup, R.anim.sl_next_in);
				animator.setOutAnimation(activityGroup, R.anim.sl_next_out);
			} else {
				animator.setInAnimation(activityGroup, R.anim.sl_previous_in);
				animator.setOutAnimation(activityGroup, R.anim.sl_previous_out);
			}

			final int numChilds = animator.getChildCount();
			if (numChilds == 0) {
				animator.addView(paneView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
			} else if (numChilds == 1) {
				animator.addView(paneView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
				animator.showNext();
			} else {
				animator.removeViewAt(0);
				animator.addView(paneView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
				animator.showNext();
			}
		} else {
			region.removeAllViews();
			region.addView(paneView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		}
	}
}
