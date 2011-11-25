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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerException;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.TrackerEvents;
import com.scoreloop.client.android.ui.framework.BaseDialog;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.OkCancelDialog;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class ProfileSettingsListActivity extends ComponentListActivity<BaseListItem> implements RequestControllerObserver,
		DialogInterface.OnDismissListener {

    private static final String STATE_RESTOREEMAIL = "restoreEmail";
    private static final String STATE_ERRORTITLE = "errorTitle";
    private static final String STATE_ERRORMESSAGE = "errorMessage";
    private static final String STATE_HINT = "hint";
    private static final String STATE_LASTREQUESTTYPE = "lastRequestType";
    private static final String STATE_LASTUPDATEERROR = "lastUpdateError";

    class UserProfileListAdapter extends BaseListAdapter<BaseListItem> {

		public UserProfileListAdapter(final Context context) {
			super(context);
			// screen contains static main list items
			add(new CaptionListItem(context, null, getString(R.string.sl_account_settings)));
			add(_changePictureItem);
			add(_changeUsernameItem);
			if (getSessionUser().getEmailAddress() != null) {
				add(_changeEmailItem);
			}
			add(_mergeAccountItem);
		}
	}

	private ProfileListItem	_changeEmailItem;
	private ProfileListItem	_mergeAccountItem;
	private ProfileListItem	_changePictureItem;
	private ProfileListItem	_changeUsernameItem;

	private UserController	_userController;

	private String			_restoreEmail;
	private String			_errorTitle;
	private String			_errorMessage;
    private String          _hint;
    private RequestType	_lastRequestType;
    private boolean		_lastUpdateError	= true;

	enum RequestType {
		USERNAME(Constant.DIALOG_PROFILE_CHANGE_USERNAME), EMAIL(Constant.DIALOG_PROFILE_CHANGE_EMAIL), USERNAME_EMAIL(
				Constant.DIALOG_PROFILE_FIRST_TIME), MERGE_ACCOUNTS(Constant.DIALOG_PROFILE_MERGE_ACCOUNTS);

		private final int	dialogId;

		RequestType(int dialogId) {
			this.dialogId = dialogId;
		}

		public int getDialogId() {
			return dialogId;
		}
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_RESTOREEMAIL, _restoreEmail);
        outState.putString(STATE_ERRORTITLE, _errorTitle);
        outState.putString(STATE_ERRORMESSAGE, _errorMessage);
        outState.putString(STATE_HINT, _hint);
        if (_lastRequestType != null) {
            outState.putString(STATE_LASTREQUESTTYPE, _lastRequestType.toString());
        }
        outState.putBoolean(STATE_LASTUPDATEERROR, _lastUpdateError);
    }

    private void saveUserState() {
		_restoreEmail = getSessionUser().getEmailAddress();
	}

	private void restoreUserState() {
		getSessionUser().setEmailAddress(_restoreEmail);
	}

	public void setLastRequestType(RequestType lastRequestType) {
		this._lastRequestType = lastRequestType;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		default:
			return super.onCreateDialog(id);
		case Constant.DIALOG_PROFILE_CHANGE_USERNAME:
			return getChangeUsernameDialog();
		case Constant.DIALOG_PROFILE_CHANGE_EMAIL:
			return getChangeEmailDialog();
		case Constant.DIALOG_PROFILE_MERGE_ACCOUNTS:
			return getMergeAccountDialog();
		case Constant.DIALOG_PROFILE_FIRST_TIME:
			return getFirstTimeDialog();
		case Constant.DIALOG_PROFILE_MSG:
			return getMsgDialog();
		}
	}

	private Dialog getMsgDialog() {
		final ErrorDialog dialog = new ErrorDialog(this);
		dialog.setOnDismissListener(this);
		return dialog;
	}

	private Dialog getChangeEmailDialog() {
		final FieldEditDialog dialog = new FieldEditDialog(this, getString(R.string.sl_change_email), getString(R.string.sl_current),
				getString(R.string.sl_new), null);
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			public void onAction(BaseDialog dialog, int actionId) {
				if (actionId == FieldEditDialog.BUTTON_OK) {
					FieldEditDialog dlg = (FieldEditDialog) dialog;
					String newEmail = dlg.getEditText().trim();
					if (!isValidEmailFormat(newEmail)) {
                        _hint = getString(R.string.sl_please_email_valid);
                        dlg.setHint(_hint);
						return;
					} else {
						updateUser(newEmail, null, RequestType.EMAIL);
					}
				}
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	private Dialog getMergeAccountDialog() {
		final FieldEditDialog dialog = new FieldEditDialog(this, getString(R.string.sl_merge_account_title), null, null,
				getString(R.string.sl_merge_account_description));
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			public void onAction(BaseDialog dialog, int actionId) {
				if (actionId == FieldEditDialog.BUTTON_OK) {
					FieldEditDialog dlg = (FieldEditDialog) dialog;
					String newEmail = dlg.getEditText().trim();
					if (!isValidEmailFormat(newEmail)) {
                        _hint = getString(R.string.sl_please_email_valid);
						dlg.setHint(_hint);
						return;
					} else if (getSessionUser().getEmailAddress() != null && getSessionUser().getEmailAddress().equalsIgnoreCase(newEmail)) {
                        _hint = getString(R.string.sl_merge_account_email_current);
						dlg.setHint(_hint);
						return;
                    } else {
						updateUser(newEmail, null, RequestType.MERGE_ACCOUNTS);
					}
				}
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	private Dialog getChangeUsernameDialog() {
		final FieldEditDialog dialog = new FieldEditDialog(this, getString(R.string.sl_change_username), getString(R.string.sl_current),
				getString(R.string.sl_new), null);
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			@Override
			public void onAction(BaseDialog dialog, int actionId) {
				dialog.dismiss();
				if (actionId == FieldEditDialog.BUTTON_OK) {
					String newUsername = ((FieldEditDialog) dialog).getEditText().trim();
					updateUser(null, newUsername, RequestType.USERNAME);
				}
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case Constant.DIALOG_PROFILE_FIRST_TIME:
            FirstTimeDialog firstTimeDialog = (FirstTimeDialog) dialog;
            firstTimeDialog.setHint(_hint);
			break;
		case Constant.DIALOG_PROFILE_CHANGE_USERNAME:
			FieldEditDialog changeUsernameDialog = (FieldEditDialog) dialog;
			changeUsernameDialog.setCurrentText(getSessionUser().getLogin());
            changeUsernameDialog.setHint(_hint);
			// reuse edited username on submit failure
			if (!(_lastUpdateError && RequestType.USERNAME.equals(_lastRequestType))) {
				changeUsernameDialog.setEditText(null);
			}
			break;
		case Constant.DIALOG_PROFILE_CHANGE_EMAIL:
			FieldEditDialog changeEmailDialog = (FieldEditDialog) dialog;
			changeEmailDialog.setHint(_hint);
			changeEmailDialog.setCurrentText(getSessionUser().getEmailAddress());
			// reuse edited email address on submit failure
			if (!(_lastUpdateError && RequestType.EMAIL.equals(_lastRequestType))) {
				changeEmailDialog.setEditText(null);
			}
			break;
		case Constant.DIALOG_PROFILE_MERGE_ACCOUNTS:
			FieldEditDialog mergeAccountsDialog = (FieldEditDialog) dialog;
			mergeAccountsDialog.setHint(_hint);
			// reuse edited email address on submit failure
			if (!(_lastUpdateError && RequestType.MERGE_ACCOUNTS.equals(_lastRequestType))) {
				mergeAccountsDialog.setEditText(null);
			}
			break;
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}
		if (dialog instanceof ErrorDialog) {
			final ErrorDialog errorDialog = (ErrorDialog) dialog;
			errorDialog.setText(_errorMessage);
			errorDialog.setTitle(_errorTitle);
		} else if (dialog instanceof OkCancelDialog) {
			final OkCancelDialog okCancelDialog = (OkCancelDialog) dialog;
			okCancelDialog.setText(_errorMessage);
			okCancelDialog.setTitle(_errorTitle);
		}
	}

	private Dialog getFirstTimeDialog() {
		final FirstTimeDialog dialog = new FirstTimeDialog(this, getSessionUser().getLogin());
		dialog.setOnActionListener(new BaseDialog.OnActionListener() {
			public void onAction(BaseDialog dialog, int actionId) {
				FirstTimeDialog dlg = (FirstTimeDialog) dialog;
				if (actionId == FirstTimeDialog.BUTTON_OK) {
					String newEmail = dlg.getEmailText().trim();
					String newUsername = dlg.getUsernameText().trim();
					if (!isValidEmailFormat(newEmail)) {
						dlg.setHint(getString(R.string.sl_please_email_address));
						return;
					} else {
						updateUser(newEmail, newUsername, RequestType.USERNAME_EMAIL);
					}
				}
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(this);
		return dialog;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
        _restoreEmail = savedInstanceState.getString(STATE_RESTOREEMAIL);
        _errorTitle = savedInstanceState.getString(STATE_ERRORTITLE);
        _errorMessage = savedInstanceState.getString(STATE_ERRORMESSAGE);
        _hint = savedInstanceState.getString(STATE_HINT);
        if (savedInstanceState.containsKey(STATE_LASTREQUESTTYPE)) {
            _lastRequestType = RequestType.valueOf(savedInstanceState.getString(STATE_LASTREQUESTTYPE));
        }
        if (savedInstanceState.containsKey(STATE_LASTUPDATEERROR)) {
            _lastUpdateError = savedInstanceState.getBoolean(STATE_LASTUPDATEERROR);
        }
        }

		super.onCreate(savedInstanceState);
		User user = getSessionUser();
		    _userController = new UserController(this);
		_changePictureItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_change_picture),
				getString(R.string.sl_change_picture), getString(R.string.sl_change_picture_details));
		_changeUsernameItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_change_username),
				getString(R.string.sl_change_username), user.getLogin());
		_changeEmailItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_change_email),
				getString(R.string.sl_change_email), user.getEmailAddress());
		_mergeAccountItem = new ProfileListItem(this, getResources().getDrawable(R.drawable.sl_icon_merge_account),
				getString(R.string.sl_merge_account_title), getString(R.string.sl_merge_account_subtitle));
		if (user.getLogin() == null || user.getEmailAddress() == null) {
			showSpinnerFor(_userController);
			_userController.loadUser();
		} else {
			setListAdapter(new UserProfileListAdapter(this));
		}
		setVisibleOptionsMenuAccountSettings(false);
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
        _hint = null;
		if (item == _changeUsernameItem) {
			if (getSessionUser().getEmailAddress() == null) {
				showDialogSafe(Constant.DIALOG_PROFILE_FIRST_TIME, true);
			} else {
				showDialogSafe(Constant.DIALOG_PROFILE_CHANGE_USERNAME, true);
			}
		} else if (item == _changePictureItem) {
			display(getFactory().createProfileSettingsPictureScreenDescription(getSessionUser()));
		} else if (item == _changeEmailItem) {
			showDialogSafe(Constant.DIALOG_PROFILE_CHANGE_EMAIL, true);
		} else if (item == _mergeAccountItem) {
			showDialogSafe(Constant.DIALOG_PROFILE_MERGE_ACCOUNTS, true);
		}
	}

	@Override
	public void onRefresh(final int flags) {
		_changeEmailItem.setSubTitle(getSessionUser().getEmailAddress());
		_changeUsernameItem.setSubTitle(getSessionUser().getLogin());
		if (getBaseListAdapter() != null) {
			getBaseListAdapter().notifyDataSetChanged();
		}
		getManager().persistSessionUserName();
	}

	@Override
	protected void requestControllerDidFailSafe(RequestController requestController, Exception exception) {
        hideSpinnerFor(requestController);
		if (exception instanceof RequestControllerException
				&& ((RequestControllerException) exception).hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_EMAIL_TAKEN)
				&& RequestType.MERGE_ACCOUNTS.equals(_lastRequestType)) {
			// for merge accounts this is the expected success
            _errorTitle = getString(R.string.sl_merge_account_success_title);
            _errorMessage = getString(R.string.sl_merge_account_success);
            showDialogSafe(Constant.DIALOG_PROFILE_MSG, true);
            _lastUpdateError = false;
            getTracker().trackEvent(TrackerEvents.CAT_REQUEST, getActionSettings(_lastRequestType), TrackerEvents.LABEL_SUCCESS, 0);
		} else {
			_lastUpdateError = true;
			int errorCode = 0;
			if (exception instanceof RequestControllerException) {
				RequestControllerException requestException = (RequestControllerException) exception;
                // errors first, then warnings
				if (requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_EMAIL_TAKEN)) {
                    _errorTitle = getString(R.string.sl_error_message_email_already_taken_title);
                    _errorMessage = getString(R.string.sl_error_message_email_already_taken);
                    showDialogSafe(Constant.DIALOG_PROFILE_MSG, true);
					errorCode = RequestControllerException.DETAIL_USER_UPDATE_REQUEST_EMAIL_TAKEN;
				} else if (requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_INVALID_EMAIL)) {
					_hint = getString(R.string.sl_error_message_invalid_email);
					errorCode = RequestControllerException.DETAIL_USER_UPDATE_REQUEST_INVALID_EMAIL;
                    showDialogSafe(_lastRequestType.getDialogId(), true);
				} else if (requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_INVALID_USERNAME)
						| requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_USERNAME_TAKEN)
						| requestException.hasDetail(RequestControllerException.DETAIL_USER_UPDATE_REQUEST_USERNAME_TOO_SHORT)) {
					_hint = getString(R.string.sl_error_message_username_already_taken);
					errorCode = RequestControllerException.DETAIL_USER_UPDATE_REQUEST_USERNAME_TAKEN;
                    showDialogSafe(_lastRequestType.getDialogId(), true);
				} else {
                    super.requestControllerDidFailSafe(requestController, exception);
				}
			} else {
				super.requestControllerDidFailSafe(requestController, exception);
			}
			getTracker().trackEvent(TrackerEvents.CAT_REQUEST, getActionSettings(_lastRequestType), TrackerEvents.LABEL_ERROR, errorCode);
		}
        restoreUserState();
	}

	private String getActionSettings(RequestType requestType) {
		if (RequestType.EMAIL.equals(requestType)) {
			return TrackerEvents.REQ_CHANGE_EMAIL;
		} else if (RequestType.USERNAME.equals(requestType)) {
			return TrackerEvents.REQ_CHANGE_USERNAME;
        } else if (RequestType.USERNAME_EMAIL.equals(requestType)) {
            return TrackerEvents.REQ_CHANGE_USERNAME_FIRSTTIME;
        } else if (RequestType.MERGE_ACCOUNTS.equals(requestType)) {
            return TrackerEvents.REQ_MERGE_ACCOUNT;
		}
		return null;
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController controller) {
        final ValueStore store = getUserValues();
        store.putValue(Constant.USER_NAME, getSessionUser().getDisplayName());
        store.putValue(Constant.USER_IMAGE_URL, getSessionUser().getImageUrl());
        setListAdapter(new UserProfileListAdapter(this));
        hideSpinnerFor(controller);
        setNeedsRefresh();
		if (RequestType.MERGE_ACCOUNTS.equals(_lastRequestType)) {
			// for merge accounts this is an error response
			_hint = getString(R.string.sl_merge_account_not_found);
            _lastUpdateError = true;
            showDialogSafe(Constant.DIALOG_PROFILE_MERGE_ACCOUNTS, true);
            getTracker().trackEvent(TrackerEvents.CAT_REQUEST, getActionSettings(_lastRequestType), TrackerEvents.LABEL_ERROR, 0);
		} else if (_lastRequestType != null) {
                _lastUpdateError = false;
                getTracker().trackEvent(TrackerEvents.CAT_REQUEST, getActionSettings(_lastRequestType), TrackerEvents.LABEL_SUCCESS, 0);
		}
	}

	private boolean isValidEmailFormat(String email) {
		Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void updateUser(String newEmail, String newUsername, final RequestType requestType) {
		saveUserState();
		final User user = getSessionUser();
		if (newEmail != null) {
			user.setEmailAddress(newEmail);
		}
		if (newUsername != null) {
			user.setLogin(newUsername);
		}
		setLastRequestType(requestType);
		getHandler().post(new Runnable() {
			public void run() {
				showSpinnerFor(_userController);
				_userController.setUser(user);
				_userController.submitUser();
			}
		});
	}

}
