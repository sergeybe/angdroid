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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public abstract class SegmentedView extends LinearLayout implements OnClickListener {

	private OnClickListener	onSegmentClickListener;
	protected int			selectedSegment		= -1;
	public int				oldSelectedSegment	= -1;

	public SegmentedView(final Context context, final AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	protected boolean allowsReselection() {
		return false; // default is false - override if other semantics needed
	}

	public int getSelectedSegment() {
		return selectedSegment;
	}

	public View getSelectedSegmentView() {
		return selectedSegment != -1 ? getChildAt(selectedSegment) : null;
	}

	@Override
	public void onClick(final View view) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			if (getChildAt(i) == view) {
				if ((i != selectedSegment) || allowsReselection()) {
					switchToSegment(i);
					if (onSegmentClickListener != null) {
						onSegmentClickListener.onClick(this);
					}
				}
				return;
			}
		}
	}

	protected void prepareSelection() {
		// intentionally empty
	}

	public void prepareUsage() {
		requestLayout();

		final int count = getChildCount();
		for (int i = 0; i < count; ++i) {
			getChildAt(i).setOnClickListener(this);
		}

		prepareSelection();
	}

	public void setOnSegmentClickListener(final OnClickListener listener) {
		onSegmentClickListener = listener;
	}

	protected void setSegment(final int segment) {
		if (selectedSegment != -1) {
			setSegmentEnabled(selectedSegment, false);
		}
		oldSelectedSegment = selectedSegment;
		selectedSegment = segment;
		if (selectedSegment != -1) {
			setSegmentEnabled(selectedSegment, true);
		}
	}

	protected abstract void setSegmentEnabled(int segment, boolean enabled);

	public void switchToSegment(final int segment) {
		setSegment(segment);
	}
}
