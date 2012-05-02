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

package com.scoreloop.client.android.ui.component.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.framework.BaseDialog;

public class ErrorDialog extends BaseDialog {

	private String	_title;
	private String	_text;

	public ErrorDialog(final Context context) {
		super(context);
		setCancelable(true);
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_error;
	}

	@Override
	public void onClick(final View v) {
		if (v.getId() == R.id.sl_button_ok) {
			dismiss();
		}
	}

	public void setTitle(final String title) {
		this._title = title;
		updateUi();
	}

	@Override
	public void setText(final String text) {
		this._text = text;
		updateUi();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Button okButton = (Button) findViewById(R.id.sl_button_ok);
		okButton.setOnClickListener(this);

		updateUi();
	}

	private void updateUi() {
		final TextView tvTitle = (TextView) findViewById(R.id.sl_title);
		tvTitle.setText(_title);
		final TextView tvErrorMessage = (TextView) findViewById(R.id.sl_error_message);
		tvErrorMessage.setText(_text);
	}
}
