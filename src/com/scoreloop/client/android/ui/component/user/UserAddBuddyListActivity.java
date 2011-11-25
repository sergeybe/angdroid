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

package com.scoreloop.client.android.ui.component.user;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.SocialProviderController;
import com.scoreloop.client.android.core.controller.SocialProviderControllerObserver;
import com.scoreloop.client.android.core.controller.UsersController;
import com.scoreloop.client.android.core.model.SocialProvider;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.agent.ManageBuddiesTask;
import com.scoreloop.client.android.ui.component.agent.ManageBuddiesTask.ManageBuddiesContinuation;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class UserAddBuddyListActivity extends ComponentListActivity<BaseListItem> implements SocialProviderControllerObserver {

	private class LoginDialog extends Dialog implements OnClickListener {
		private EditText	loginEdit;
		private Button		okButton;

		LoginDialog(final Context context, final int style) {
			super(context, style);
		}

		public void onClick(final View v) {
			if (v.getId() == R.id.button_ok) {
				dismiss();
				handleDialogClick(loginEdit.getText().toString().trim());
			}
		}

		@Override
		protected void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.sl_dialog_login);
			setTitle(getResources().getString(R.string.sl_scoreloop_username));
			loginEdit = (EditText) findViewById(R.id.edit_login);
			okButton = (Button) findViewById(R.id.button_ok);
			okButton.setOnClickListener(this);
		}
	}

    private final Object		_addressBookTarget	= new Object();
	private final Object		_loginTarget		= new Object();

	private UsersController		usersSearchController;

	private void handleDialogClick(final String login) {
		showSpinnerFor(usersSearchController);
		usersSearchController.setSearchOperator(UsersController.LoginSearchOperator.EXACT_MATCH);
		usersSearchController.searchByLogin(login);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new BaseListAdapter<BaseListItem>(this));

		usersSearchController = new UsersController(getRequestControllerObserver());
		usersSearchController.setSearchesGlobal(true);

		final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
		final Resources res = getResources();
		final Configuration configuration = getConfiguration();

		adapter.clear();
		adapter.add(new CaptionListItem(this, null, getString(R.string.sl_add_friends)));
		adapter.add(new UserAddBuddyListItem(this, res.getDrawable(R.drawable.sl_icon_facebook), getString(R.string.sl_facebook),
				SocialProvider.getSocialProviderForIdentifier(SocialProvider.FACEBOOK_IDENTIFIER)));
		adapter.add(new UserAddBuddyListItem(this, res.getDrawable(R.drawable.sl_icon_twitter), getString(R.string.sl_twitter),
				SocialProvider.getSocialProviderForIdentifier(SocialProvider.TWITTER_IDENTIFIER)));
		adapter.add(new UserAddBuddyListItem(this, res.getDrawable(R.drawable.sl_icon_myspace), getString(R.string.sl_myspace),
				SocialProvider.getSocialProviderForIdentifier(SocialProvider.MYSPACE_IDENTIFIER)));
		if (configuration.isFeatureEnabled(Configuration.Feature.ADDRESS_BOOK)) {
			adapter.add(new UserAddBuddyListItem(this, res.getDrawable(R.drawable.sl_icon_addressbook), getString(R.string.sl_addressbook),
					_addressBookTarget));
		}
		adapter.add(new UserAddBuddyListItem(this, res.getDrawable(R.drawable.sl_icon_scoreloop),
				getString(R.string.sl_scoreloop_username), _loginTarget));
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
            case Constant.DIALOG_ADD_FRIEND_LOGIN:
                final LoginDialog loginDialog = new LoginDialog(this, R.style.sl_dialog);
                loginDialog.setOnDismissListener(this);
                return loginDialog;
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		final Object target = ((UserAddBuddyListItem) item).getTarget();
		if (target == _addressBookTarget) {
			showSpinnerFor(usersSearchController);
			usersSearchController.searchByLocalAddressBook();
		} else if (target == _loginTarget) {
			showDialogSafe(Constant.DIALOG_ADD_FRIEND_LOGIN, true);
		} else {
			final SocialProvider socialProvider = (SocialProvider) target;
			if (socialProvider.isUserConnected(getSessionUser())) {
				showSpinnerFor(usersSearchController);
				usersSearchController.searchBySocialProvider(socialProvider);
			} else {
				final SocialProviderController socialProviderController = SocialProviderController.getSocialProviderController(null, this,
						socialProvider);
				socialProviderController.connect(this);
			}
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController requestController) {
		if (requestController == usersSearchController) {
			if (usersSearchController.isOverLimit()) {
				if (usersSearchController.isMaxUserCount()) {
					showToast(getResources().getString(R.string.sl_found_too_many_users));
				} else {
					showToast(String.format(getResources().getString(R.string.sl_format_found_many_users),
							usersSearchController.getCountOfUsers()));
				}
				return;
			}

			final List<User> users = usersSearchController.getUsers();
			if (users.isEmpty()) {
				showToast(getResources().getString(R.string.sl_found_no_user));
			} else {
				showSpinner();
				ManageBuddiesTask.addBuddies(this, users, getSessionUserValues(), new ManageBuddiesContinuation() {
					public void withAddedOrRemovedBuddies(final int addedBuddies) {
						hideSpinner();
						if (!isPaused()) {
							switch (addedBuddies) {
							case 0:
								showToast(getResources().getString(R.string.sl_found_no_user));
								break;
							case 1:
								showToast(getResources().getString(R.string.sl_format_one_friend_added));
								break;
							default:
								showToast(String.format(getResources().getString(R.string.sl_format_friends_added), addedBuddies));
								break;
							}
						}
					}
				});
			}
		}
	}

	public void socialProviderControllerDidCancel(final SocialProviderController controller) {
	}

	public void socialProviderControllerDidEnterInvalidCredentials(final SocialProviderController controller) {
		showDialogSafe(Constant.DIALOG_ERROR_NETWORK);
	}

	public void socialProviderControllerDidFail(final SocialProviderController controller, final Throwable error) {
		showDialogSafe(Constant.DIALOG_ERROR_NETWORK);
	}

	public void socialProviderControllerDidSucceed(final SocialProviderController socialProviderController) {
		final SocialProvider socialProvider = socialProviderController.getSocialProvider();
		if (socialProvider.isUserConnected(getSessionUser())) {
			showSpinnerFor(usersSearchController);
			usersSearchController.searchBySocialProvider(socialProvider);
		}
	}
}
