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

import android.content.Context;
import android.util.AttributeSet;

import org.angdroid.angband.R;

public class TabView extends SegmentedView {


	private final int	res2[][]	= new int[][] {
			{ R.drawable.sl_tab_active, R.drawable.sl_tab_default_left_border },
			{ R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_active } };

	private final int	res3[][]	= new int[][] {
			{ R.drawable.sl_tab_active, R.drawable.sl_tab_default_left_border, R.drawable.sl_tab_default_left_border },
			{ R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_active, R.drawable.sl_tab_default_left_border },
			{ R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_active } };

	private final int	res4[][]	= new int[][] {
			{ R.drawable.sl_tab_active, R.drawable.sl_tab_default_left_border, R.drawable.sl_tab_default_left_border, R.drawable.sl_tab_default_left_border },
			{ R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_active, R.drawable.sl_tab_default_left_border, R.drawable.sl_tab_default_left_border },
			{ R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_active, R.drawable.sl_tab_default_left_border },
			{ R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_default_right_border, R.drawable.sl_tab_active } };

	public TabView(final Context context, final AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	@Override
	protected void prepareSelection() {
		if (getChildCount() != 0) {
			switchToSegment(0);
		}
	}

	@Override
	protected void setSegmentEnabled(final int segment, final boolean enabled) {
		// this needs to be true, otherwise we won't receive onClick on TextView's
		getChildAt(segment).setEnabled(true);
	}

	@Override
	public void switchToSegment(final int segment) {
		if (segment == selectedSegment) {
			return;
		}
		updateHighlight(segment);
		setSegment(segment);
	}

	private void updateHighlight(final int segment) {
		int res[][] = null;
		final int count = getChildCount();
		switch (count) {
		default: throw new IllegalStateException("unsupported number of tabs");
		case 2: res = res2; break;
		case 3: res = res3; break;
		case 4: res = res4; break;
		}
		for (int i = 0; i < count; i++) {
			getChildAt(i).setBackgroundResource(res[segment][i]);
		}
	}
}
