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

public class FieldEditDialog extends BaseDialog {

	public static final int	BUTTON_OK		= 0;
	public static final int	BUTTON_CANCEL	= 1;

	private final String	_title;
	private final String	description;
	private final String	_currentLabel;
	private String			_currentText;
	private final String	_newLabel;
	private String			_newText;
	private String			_hint;
	private EditText		_tfEditText;
	private TextView		_tfHint;
	private TextView		_tfCurrentText;

	public FieldEditDialog(final Context context, final String title, final String currentLabel, final String newLabel,
			final String description) {
		super(context);
		setCancelable(true);
		_title = title;
		_currentLabel = currentLabel;
		_newLabel = newLabel;
		this.description = description;
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_profile_edit;
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

	public void setCurrentText(final String currentText) {
		this._currentText = currentText;
		refresh();
	}

	public void setEditText(final String editText) {
		_newText = editText;
		refresh();
	}

	public void setHint(final String text) {
		_hint = text;
		refresh();
	}

	public String getEditText() {
		_newText = _tfEditText.getText().toString();
		return _tfEditText.getText().toString();
	}

	private void refresh() {
		if (_tfEditText != null) {
			_tfEditText.setText(_newText);
		}
		if (_tfCurrentText != null) {
			_tfCurrentText.setText(_currentText);
		}
		if (_tfHint != null) {
			_tfHint.setVisibility(View.VISIBLE);
			_tfHint.setText(_hint);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Button okButton = (Button) findViewById(R.id.sl_button_ok);
		okButton.setOnClickListener(this);
		final Button cancelButton = (Button) findViewById(R.id.sl_button_cancel);
		cancelButton.setOnClickListener(this);
		final TextView tvTitle = (TextView) findViewById(R.id.sl_title);
		tvTitle.setText(_title);
		final TextView tvDescription = (TextView) findViewById(R.id.sl_description);
		if (description != null) {
			tvDescription.setText(description);
		} else {
			tvDescription.setVisibility(View.INVISIBLE);
		}
		final TextView tvCurrentLabel = (TextView) findViewById(R.id.sl_user_profile_edit_current_label);
		_tfCurrentText = (TextView) findViewById(R.id.sl_user_profile_edit_current_text);
		if (_currentLabel != null) {
			tvCurrentLabel.setText(_currentLabel);
		} else {
			tvCurrentLabel.setVisibility(View.INVISIBLE);
			_tfCurrentText.setVisibility(View.INVISIBLE);
		}
		final TextView tvNewLabel = (TextView) findViewById(R.id.sl_user_profile_edit_new_label);
		if (_newLabel != null) {
			tvNewLabel.setText(_newLabel);
		} else {
			tvNewLabel.setVisibility(View.INVISIBLE);
		}
		_tfEditText = (EditText) findViewById(R.id.sl_user_profile_edit_new_text);
		_tfHint = (TextView) findViewById(R.id.sl_dialog_hint);
		refresh();
	}
}
