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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.angdroid.angband.R;

public class OkCancelDialog extends BaseDialog {

	public static final int	BUTTON_CANCEL	= 1;
	public static final int	BUTTON_OK		= 0;

	private String			title;

	public OkCancelDialog(final Context context) {
		super(context);
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_ok_cancel;
	}

	public void setTitle(final String title) {
		this.title = title;
		refresh();
	}

	@Override
	public void onClick(final View view) {
		if (_listener == null) {
			return;
		}
		if (view.getId() == R.id.sl_button_ok) {
			_listener.onAction(this, BUTTON_OK);
		} else if (view.getId() == R.id.sl_button_cancel) {
			_listener.onAction(this, BUTTON_CANCEL);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Button okButton = (Button) findViewById(R.id.sl_button_ok);
		okButton.setOnClickListener(this);

		final Button cancelButton = (Button) findViewById(R.id.sl_button_cancel);
		cancelButton.setOnClickListener(this);

		refresh();
	}

	private void refresh() {
		final TextView tvTitle = (TextView) findViewById(R.id.sl_title);
		if (title != null) {
			tvTitle.setText(title);
			tvTitle.setVisibility(View.VISIBLE);
		} else {
			tvTitle.setVisibility(View.INVISIBLE);
		}
	}
}
