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

package com.scoreloop.client.android.ui.component.base;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.angdroid.angband.R;

public abstract class ComponentHeaderActivity extends ComponentActivity implements OnClickListener {

	private TextView	_caption;

	protected ImageView getImageView() {
		return (ImageView) findViewById(R.id.sl_header_image);
	}

	public void onClick(final View view) {
		// intentionally empty - override in subclass
	}

	public void onCreate(final Bundle savedInstanceState, final int layout_id) {
		super.onCreate(savedInstanceState);
		setContentView(layout_id);
	}

	@Override
	protected void onSpinnerShow(final boolean show) {
		// overridden here as we don't show spinners for header activities - at least not the standard one
	}

	protected void setCaption(final String captionText) {
		if (_caption == null) {
			final Display display = getWindowManager().getDefaultDisplay();
			int orientation = display.getOrientation();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			if (metrics.widthPixels > metrics.heightPixels || orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
				_caption = (TextView) findViewById(R.id.sl_header_caption_land);
			} else {
				_caption = (TextView) findViewById(R.id.sl_header_caption);
			}
		}
		if (_caption != null) {
			_caption.setText(captionText);
		}
	}

	protected void setSubTitle(final String subTitle) {
		final TextView textView = (TextView) findViewById(R.id.sl_header_subtitle);
		if (textView != null) {
			textView.setText(subTitle);
		}
	}

	protected void setTitle(final String title) {
		final TextView textView = (TextView) findViewById(R.id.sl_header_title);
		textView.setText(title);
	}

}
