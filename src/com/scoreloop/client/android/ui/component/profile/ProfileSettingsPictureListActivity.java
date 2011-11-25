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

import java.io.FileNotFoundException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.scoreloop.client.android.core.addon.AndroidImage;
import com.scoreloop.client.android.core.controller.*;
import com.scoreloop.client.android.core.model.*;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class ProfileSettingsPictureListActivity extends ComponentListActivity<BaseListItem> implements RequestControllerObserver,
		SocialProviderControllerObserver {

	class PictureListAdapter extends BaseListAdapter<BaseListItem> {
		public PictureListAdapter(final Context context) {
			super(context);
			// screen contains static profile list items
			add(new CaptionListItem(context, null, getString(R.string.sl_change_picture)));
			add(_deviceLibraryItem);
			add(_facebookItem);
			add(_twitterItem);
			add(_myspaceItem);
			add(_setDefaultItem);
		}
	}

	private static final int		PICK_PICTURE	= 0x1;

	private Runnable				_continuation;
	private ProfilePictureListItem	_deviceLibraryItem;
	private ProfilePictureListItem	_facebookItem;
	private ProfilePictureListItem	_myspaceItem;
	private ProfilePictureListItem	_setDefaultItem;
	private ProfilePictureListItem	_twitterItem;
	private User					_user;
	private UserController			_userController;

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (data != null && data.getData() != null && !data.getData().toString().trim().equals("")) {
			getHandler().post(new Runnable() {
				public void run() {
					final Uri localImageUri = data.getData();

					startSubmitPicture(localImageUri);
				}
			});
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Resources res = getResources();
		_deviceLibraryItem = new ProfilePictureListItem(this, res.getDrawable(R.drawable.sl_icon_device),
				getString(R.string.sl_device_library));
		_facebookItem = new ProfilePictureListItem(this, res.getDrawable(R.drawable.sl_icon_facebook), getString(R.string.sl_facebook));
		_twitterItem = new ProfilePictureListItem(this, res.getDrawable(R.drawable.sl_icon_twitter), getString(R.string.sl_twitter));
		_myspaceItem = new ProfilePictureListItem(this, res.getDrawable(R.drawable.sl_icon_myspace), getString(R.string.sl_myspace));
		_setDefaultItem = new ProfilePictureListItem(this, res.getDrawable(R.drawable.sl_icon_user), getString(R.string.sl_set_default));
		setListAdapter(new PictureListAdapter(this));
		_user = getSessionUser();
		_userController = new UserController(this);
		_userController.setUser(_user);
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		if (item == _deviceLibraryItem) {
			pickDeviceLibraryPicture();
		} else if (item == _facebookItem) {
			withConnectedProvider(SocialProvider.FACEBOOK_IDENTIFIER, new Runnable() {
				public void run() {
					pickFacebookPicture();
				}
			});
		} else if (item == _twitterItem) {
			withConnectedProvider(SocialProvider.TWITTER_IDENTIFIER, new Runnable() {
				public void run() {
					pickTwitterPicture();
				}
			});
		} else if (item == _myspaceItem) {
			withConnectedProvider(SocialProvider.MYSPACE_IDENTIFIER, new Runnable() {
				public void run() {
					pickMyspacePicture();
				}
			});
		} else if (item == _setDefaultItem) {
			pickDefaultPicture();
		}
	}

	private void pickDefaultPicture() {
		_user.setImageSource(ImageSource.IMAGE_SOURCE_DEFAULT);
		_user.setImageMimeType(null);
		_user.setImageData(null);
		showSpinnerFor(_userController);
		_userController.submitUser();
	}

	private void pickDeviceLibraryPicture() {
		final Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.putExtra("windowTitle", getString(R.string.sl_choose_photo));
		try {
			startActivityForResult(intent, PICK_PICTURE);
		} catch (final Exception e) {
		}
	}

	private void pickFacebookPicture() {
		_user.setImageSource(SocialProvider.getSocialProviderForIdentifier(SocialProvider.FACEBOOK_IDENTIFIER));
		_user.setImageMimeType(null);
		_user.setImageData(null);
		showSpinnerFor(_userController);
		_userController.submitUser();
	}

	private void pickMyspacePicture() {
		_user.setImageSource(SocialProvider.getSocialProviderForIdentifier(SocialProvider.MYSPACE_IDENTIFIER));
		_user.setImageMimeType(null);
		_user.setImageData(null);
		showSpinnerFor(_userController);
		_userController.submitUser();
	}

	private void pickTwitterPicture() {
		_user.setImageSource(SocialProvider.getSocialProviderForIdentifier(SocialProvider.TWITTER_IDENTIFIER));
		_user.setImageMimeType(null);
		_user.setImageData(null);
		showSpinnerFor(_userController);
		_userController.submitUser();
	}

	@Override
	protected void requestControllerDidFailSafe(RequestController aRequestController, Exception anException) {
		super.requestControllerDidFailSafe(aRequestController, anException);
		getUserValues().putValue(Constant.USER_IMAGE_URL, _user.getImageUrl());
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController controller) {
		getUserValues().putValue(Constant.USER_IMAGE_URL, _user.getImageUrl());
		hideSpinnerFor(controller);
	}

	public void socialProviderControllerDidCancel(final SocialProviderController controller) {
		hideSpinnerFor(controller);
	}

	public void socialProviderControllerDidEnterInvalidCredentials(final SocialProviderController controller) {
		socialProviderControllerDidFail(controller, new RuntimeException("Invalid Credentials"));
	}

	public void socialProviderControllerDidFail(final SocialProviderController controller, final Throwable error) {
		hideSpinnerFor(controller);
		showToast(String.format(getString(R.string.sl_format_connect_failed), controller.getSocialProvider().getName()));
	}

	public void socialProviderControllerDidSucceed(final SocialProviderController controller) {
		hideSpinnerFor(controller);
		if (!isPaused() && (_continuation != null)) {
			_continuation.run();
		}
	}

	private void startSubmitPicture(final Uri localImageUri) {
		showSpinnerFor(_userController);

		final AsyncTask<Void, Void, Image> loadImageTask = new AsyncTask<Void, Void, Image>() {
			@Override
			protected Image doInBackground(Void... params) {
				try {
					return new AndroidImage(localImageUri, getContentResolver());
				} catch (final FileNotFoundException e1) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Image image) {
				if (image == null) {
					hideSpinnerFor(_userController);
				} else {
					_user.assignImage(image, new Continuation<Boolean>() {
						@Override
						public void withValue(final Boolean result, final Exception error) {
							if (!result) {
								hideSpinnerFor(_userController);
								return;
							}
							_userController.submitUser();
						}
					});
				}
			}
		};
		// noinspection unchecked
		loadImageTask.execute();
	}

	private void withConnectedProvider(final String socialProviderIdentifier, final Runnable runnable) {
		final SocialProvider socialProvider = SocialProvider.getSocialProviderForIdentifier(socialProviderIdentifier);
		if (socialProvider.isUserConnected(getSessionUser())) {
			runnable.run();
		} else {
			final SocialProviderController socialProviderController = SocialProviderController.getSocialProviderController(getSession(),
					this, socialProvider);
			_continuation = runnable;
			showSpinnerFor(socialProviderController);
			socialProviderController.connect(this);
		}
	}
}
