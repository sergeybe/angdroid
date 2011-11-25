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

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.angdroid.nightly.R;

public abstract class BaseDialog extends Dialog implements View.OnClickListener {

	public static interface OnActionListener {
		void onAction(BaseDialog dialog, int actionId);
	}

	protected OnActionListener	_listener;
    private String				_text;
    private String				_okButtonText;
    private TextView			_textView;
    private Button  			_okButton;
	private Object				_target;

	protected BaseDialog(final Context context) {
		super(context, R.style.sl_dialog);
		setCancelable(false);
	}

	protected abstract int getContentViewLayoutId();

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getContentViewLayoutId());

		_textView = ((TextView) findViewById(R.id.sl_text));
        _okButton = (Button)findViewById(R.id.sl_button_ok);
		refresh();
	}

	public void setOnActionListener(final OnActionListener listener) {
		_listener = listener;
	}

	public void setText(final String text) {
		_text = text;
		refresh();
	}

    public void setOkButtonText(String okButtonText) {
        _okButtonText = okButtonText;
        refresh();
    }


	private void refresh() {
        if (_textView != null) {
            _textView.setText(_text);
        }
        if (_textView != null && _okButtonText != null) {
            _okButton.setText(_okButtonText);
        }
	}
	
	public void setTarget(Object target) {
		_target = target;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getTarget() {
		return (T)_target;		
	}
}
