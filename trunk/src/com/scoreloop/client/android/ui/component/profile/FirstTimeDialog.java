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
import android.widget.EditText;
import android.widget.TextView;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.framework.BaseDialog;

public class FirstTimeDialog extends BaseDialog {

	public static final int	BUTTON_OK		= 0;
	public static final int	BUTTON_CANCEL	= 1;

	private final String	_currentUsername;
	private EditText		_username;
	private EditText		_email;
	private TextView		_hint;

	public FirstTimeDialog(final Context context, final String currentUsername) {
		super(context);
		setCancelable(true);
		_currentUsername = currentUsername;
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_profile_edit_initial;
	}

	@Override
	public void onClick(final View v) {
		if (_listener != null) {
			final int viewId = v.getId();
			if (viewId == R.id.sl_button_ok) {
				_listener.onAction(this, BUTTON_OK);

			} else if (viewId == R.id.sl_button_cancel) {
				_listener.onAction(this, BUTTON_CANCEL);
			}
		}
	}

	public String getUsernameText() {
		return _username.getText().toString();
	}

	public String getEmailText() {
		return _email.getText().toString();
	}

	public void setHint(final String text) {
		_hint.setText(text);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Button okButton = (Button) findViewById(R.id.sl_button_ok);
		okButton.setOnClickListener(this);
		final Button cancelButton = (Button) findViewById(R.id.sl_button_cancel);
		cancelButton.setOnClickListener(this);
		final TextView tvCurrentUsername = (TextView) findViewById(R.id.sl_user_profile_edit_initial_current_text);
		tvCurrentUsername.setText(_currentUsername);
		_username = (EditText) findViewById(R.id.sl_user_profile_edit_initial_username_text);
		_email = (EditText) findViewById(R.id.sl_user_profile_edit_initial_email_text);
		_hint = (TextView) findViewById(R.id.sl_dialog_hint);
	}
}
