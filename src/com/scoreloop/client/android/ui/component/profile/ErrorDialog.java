package com.scoreloop.client.android.ui.component.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.framework.BaseDialog;

public class ErrorDialog extends BaseDialog {

	private String _title;
    private String _text;

	public ErrorDialog(final Context context)
	{
		super(context);
        setCancelable(true);
	}

	@Override
	protected int getContentViewLayoutId() {
		return R.layout.sl_dialog_error;
	}

	public void onClick(final View v) {
		if (v.getId() == R.id.sl_button_ok) {
			dismiss();
		}
	}

    public void setTitle(String title) {
        this._title = title;
        updateUi();
    }

    public void setText(String text) {
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
        TextView tvTitle = (TextView) findViewById(R.id.sl_title);
        tvTitle.setText(_title);
        TextView tvErrorMessage = (TextView) findViewById(R.id.sl_error_message);
        tvErrorMessage.setText(_text);
    }
}
