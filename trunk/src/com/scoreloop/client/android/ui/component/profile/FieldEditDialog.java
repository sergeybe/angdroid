package com.scoreloop.client.android.ui.component.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.angdroid.nightly.R;
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

	public void onClick(final View v) {
		if (_listener != null) {
            int viewId = v.getId();
            if (viewId == R.id.sl_button_ok) {
                _listener.onAction(this, BUTTON_OK);

            } else if (viewId == R.id.sl_button_cancel) {
                _listener.onAction(this, BUTTON_CANCEL);
            }
		}
	}

	public void setCurrentText(String currentText) {
		this._currentText = currentText;
		refresh();
	}

	public void setEditText(String editText) {
		_newText = editText;
		refresh();
	}

	public void setHint(String text) {
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
			_tfHint.setVisibility(TextView.VISIBLE);
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
			tvDescription.setVisibility(TextView.INVISIBLE);
		}
		final TextView tvCurrentLabel = (TextView) findViewById(R.id.sl_user_profile_edit_current_label);
		_tfCurrentText = (TextView) findViewById(R.id.sl_user_profile_edit_current_text);
		if (_currentLabel != null) {
			tvCurrentLabel.setText(_currentLabel);
		} else {
			tvCurrentLabel.setVisibility(TextView.INVISIBLE);
			_tfCurrentText.setVisibility(TextView.INVISIBLE);
		}
		final TextView tvNewLabel = (TextView) findViewById(R.id.sl_user_profile_edit_new_label);
		if (_newLabel != null) {
			tvNewLabel.setText(_newLabel);
		} else {
			tvNewLabel.setVisibility(TextView.INVISIBLE);
		}
		_tfEditText = (EditText) findViewById(R.id.sl_user_profile_edit_new_text);
		_tfHint = (TextView) findViewById(R.id.sl_dialog_hint);
		refresh();
	}
}
