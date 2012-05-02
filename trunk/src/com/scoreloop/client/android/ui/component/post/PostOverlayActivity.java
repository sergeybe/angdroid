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

package com.scoreloop.client.android.ui.component.post;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.scoreloop.client.android.core.controller.MessageController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerException;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.SocialProviderController;
import com.scoreloop.client.android.core.controller.SocialProviderControllerObserver;
import com.scoreloop.client.android.core.model.Entity;
import com.scoreloop.client.android.core.model.SocialProvider;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.framework.BaseActivity;

public class PostOverlayActivity extends Activity implements RequestControllerObserver, OnCheckedChangeListener,
SocialProviderControllerObserver {

	private static Entity		_messageTarget			= null;

	private static final int	DIALOG_ERROR_NETWORK	= 100;
	private static final int	DIALOG_CONNECT_FAILED	= 101;
	private static final int	DIALOG_PROGRESS			= 102;

	public static void setMessageTarget(final Entity messageTarget) {
		_messageTarget = messageTarget;
	}

	private final Map<CheckBox, SocialProvider>	_checkboxToProviderMap	= new HashMap<CheckBox, SocialProvider>();
	private MessageController					_messageController;
	private EditText							_messageEditText;
	private Button								_noButton;
	private Button								_postButton;
	private final Map<SocialProvider, CheckBox>	_providerToCheckboxMap	= new HashMap<SocialProvider, CheckBox>();
	private final Handler						_handler				= new Handler();

	private void addCheckbox(final String socialProviderId, final int checkboxId) {
		final SocialProvider provider = SocialProvider.getSocialProviderForIdentifier(socialProviderId);
		final CheckBox checkBox = (CheckBox) findViewById(checkboxId);
		_checkboxToProviderMap.put(checkBox, provider);
		_providerToCheckboxMap.put(provider, checkBox);
		checkBox.setOnCheckedChangeListener(this);
	}

	private void blockUI(final boolean block) {
		if (block) {
			showDialog(DIALOG_PROGRESS);
		} else {
			try {
				dismissDialog(DIALOG_PROGRESS);
			} catch (final IllegalArgumentException e) {
				// developer reported case where dialog was not visible
				// ignore if dialog is not visible
			}
		}
		final boolean enabled = !block;
		_postButton.setEnabled(enabled);
		_noButton.setEnabled(enabled);
		_messageEditText.setEnabled(enabled);
		for (final CheckBox checkBox : _checkboxToProviderMap.keySet()) {
			checkBox.setEnabled(enabled);
		}
	}

	private Dialog createErrorDialog(final int messageResId) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(messageResId));
		final Dialog dialog = builder.create();
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	private Dialog createProgressDialog() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("progress");
		return dialog;
	}

	protected Entity getMessageTarget() {
		return _messageTarget;
	}

	protected String getPostText() {
		return "Achievement"; // needs to be overwritten in subclass
	}

	public void onBackPressed() {
		// intentionally empty
	}

	@Override
	public void onCheckedChanged(final CompoundButton button, final boolean isChecked) {
		if (isChecked) {
			final CheckBox checkBox = (CheckBox) button;
			final SocialProvider provider = _checkboxToProviderMap.get(checkBox);
			if (!provider.isUserConnected(ScoreloopManagerSingleton.get().getSession().getUser())) {
				blockUI(true);

				// make the UI more responsive by scheduling the connect in the next run-loop iteration
				_handler.post(new Runnable() {
					@Override
					public void run() {
						final SocialProviderController controller = SocialProviderController.getSocialProviderController(null,
								PostOverlayActivity.this, provider);
						controller.connect(PostOverlayActivity.this);
					}
				});
			}
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_post);

		final Entity messageTarget = getMessageTarget();
		if ((messageTarget == null) || (messageTarget.getIdentifier() == null)) {
			// post overlay is currently skipped in offline mode (no id for score)
			finish();
			return;
		}

		_messageController = new MessageController(this);

		final TextView textView = (TextView) findViewById(R.id.sl_post_text);
		final String format = getString(R.string.sl_format_post);
		textView.setText(String.format(format, getPostText()));

		_noButton = (Button) findViewById(R.id.cancel_button);
		_noButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});

		_messageEditText = (EditText) findViewById(R.id.message_edittext);

		_postButton = (Button) findViewById(R.id.ok_button);
		_postButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				_messageController.setTarget(getMessageTarget());

				for (final CheckBox checkBox : _checkboxToProviderMap.keySet()) {
					final SocialProvider provider = _checkboxToProviderMap.get(checkBox);
					if (checkBox.isChecked()) {
						_messageController.addReceiverWithUsers(provider);
					}
				}
				_messageController.setText(_messageEditText.getText().toString());

				if (_messageController.isSubmitAllowed()) {
					blockUI(true);
					_messageController.submitMessage();
				}
			}
		});

		addCheckbox(SocialProvider.FACEBOOK_IDENTIFIER, R.id.facebook_checkbox);
		addCheckbox(SocialProvider.TWITTER_IDENTIFIER, R.id.twitter_checkbox);
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case DIALOG_ERROR_NETWORK:
			return createErrorDialog(R.string.sl_error_message_network);
		case DIALOG_CONNECT_FAILED:
			return createErrorDialog(R.string.sl_error_message_connect_failed);
		case DIALOG_PROGRESS:
			return createProgressDialog();
		default:
			return null;
		}
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		return false;
	}

	@Override
	public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
		if (aRequestController == _messageController) {
			blockUI(false);
			for (final SocialProvider provider : SocialProvider.getSupportedProviders()) {
				updateProviderCheckbox(provider);
			}
			if (anException instanceof RequestControllerException) {
				final RequestControllerException requestControllerException = (RequestControllerException) anException;
				if (requestControllerException.getErrorCode() == RequestControllerException.CODE_SOCIAL_PROVIDER_DISCONNECTED) {
					showDialog(DIALOG_CONNECT_FAILED);
					return;
				}
			}
			showDialog(DIALOG_ERROR_NETWORK);
		}
	}

	@Override
	public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
		if (aRequestController == _messageController) {
			dismissDialog(DIALOG_PROGRESS);
			final String message = String.format(getResources().getString(R.string.sl_format_posted), getPostText());
			BaseActivity.showToast(this, message);
			finish();
		}
	}

	@Override
	public void socialProviderControllerDidCancel(final SocialProviderController controller) {
		blockUI(false);
		updateProviderCheckbox(controller.getSocialProvider());
	}

	@Override
	public void socialProviderControllerDidEnterInvalidCredentials(final SocialProviderController controller) {
		blockUI(false);
		updateProviderCheckbox(controller.getSocialProvider());
		showDialog(DIALOG_CONNECT_FAILED);
	}

	@Override
	public void socialProviderControllerDidFail(final SocialProviderController controller, final Throwable error) {
		blockUI(false);
		updateProviderCheckbox(controller.getSocialProvider());
		showDialog(DIALOG_ERROR_NETWORK);
	}

	@Override
	public void socialProviderControllerDidSucceed(final SocialProviderController controller) {
		blockUI(false);
		updateProviderCheckbox(controller.getSocialProvider());
	}

	private void updateProviderCheckbox(final SocialProvider provider) {
		if (!provider.isUserConnected(ScoreloopManagerSingleton.get().getSession().getUser())) {
			final CheckBox checkbox = _providerToCheckboxMap.get(provider);
			checkbox.setChecked(false);
		}
	}
}
