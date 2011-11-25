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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.angdroid.angband.R;

public class ShortcutView extends SegmentedView {

	private List<ShortcutDescription>	_shortcutDescriptions	= null;

	public ShortcutView(final Context context, final AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public void setDescriptions(final Activity activity, final List<ShortcutDescription> shortcutDescriptions) {

		// performance improvement in case descriptions have not changed
		if ((_shortcutDescriptions != null) && _shortcutDescriptions.equals(shortcutDescriptions)) {
			_shortcutDescriptions = shortcutDescriptions;
			return;
		}

		removeAllViews();

		_shortcutDescriptions = shortcutDescriptions;
		Display display = activity.getWindowManager().getDefaultDisplay(); 
		for (final ShortcutDescription shortcutDescription : shortcutDescriptions) {
			final ViewGroup viewGroup = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.sl_tab_shortcut, null);
			int margin = (int)getResources().getDimension(R.dimen.sl_margin_shortcut);
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			LayoutParams lp = null;
			int rotation = display.getOrientation();
			if (metrics.widthPixels > metrics.heightPixels || rotation == Surface.ROTATION_270 || rotation == Surface.ROTATION_90) {
				lp = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 0, 1);
				lp.gravity = Gravity.CENTER;
				lp.leftMargin = margin;
				lp.rightMargin = margin;
			}
			else {
				lp = new LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
				lp.gravity = Gravity.CENTER;
				lp.bottomMargin = margin;
				lp.topMargin = margin;
			}
			viewGroup.setLayoutParams(lp);
			viewGroup.setId(shortcutDescription.getTextId());
			((ImageView) viewGroup.findViewById(R.id.sl_image_tab_view)).setImageResource(shortcutDescription.getImageId());
			addView(viewGroup);
		}

		prepareUsage();
	}

	@Override
	protected void setSegmentEnabled(final int segment, final boolean enabled) {
		final View view = getChildAt(segment);
		final ShortcutDescription shortcutDescription = _shortcutDescriptions.get(segment);

		if (enabled) {
			((ImageView) view.findViewById(R.id.sl_image_tab_view)).setImageResource(shortcutDescription.getActiveImageId());
			view.setBackgroundResource(R.drawable.sl_shortcut_highlight);
		} else {
			((ImageView) view.findViewById(R.id.sl_image_tab_view)).setImageResource(shortcutDescription.getImageId());
			view.setBackgroundDrawable(null);
		}
	}
}
