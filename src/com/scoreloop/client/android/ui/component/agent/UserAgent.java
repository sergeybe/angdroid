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

package com.scoreloop.client.android.ui.component.agent;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.UserController;
import com.scoreloop.client.android.core.model.Entity;
import com.scoreloop.client.android.core.model.User;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class UserAgent extends BaseAgent {

	public static final String[]	SUPPORTED_KEYS	= { Constant.USER_NAME, Constant.USER_IMAGE_URL, Constant.USER_BALANCE,
		Constant.NUMBER_GAMES, Constant.NUMBER_BUDDIES, Constant.NUMBER_GLOBAL_ACHIEVEMENTS };

	private UserController			_userController;

	public UserAgent(final Delegate delegate) {
		super(delegate, SUPPORTED_KEYS);
	}

	@Override
	protected void onFinishRetrieve(final RequestController aRequestController, final ValueStore valueStore) {
		final User user = _userController.getUser();
		putValue(Constant.USER_NAME, user.getDisplayName());
		putValue(Constant.USER_IMAGE_URL, user.getImageUrl());
		putValue(Constant.USER_BALANCE, ScoreloopManagerSingleton.get().getSession().getBalance());
		putValue(Constant.NUMBER_GAMES, user.getGamesCounter());
		putValue(Constant.NUMBER_BUDDIES, user.getBuddiesCounter());
		putValue(Constant.NUMBER_GLOBAL_ACHIEVEMENTS, user.getGlobalAchievementsCounter());
	}

	@Override
	protected void onStartRetrieve(final ValueStore valueStore) {
		_userController = new UserController(this);
		_userController.setCachedResponseUsed(false);
		_userController.setUser((Entity) valueStore.getValue(Constant.USER));
		_userController.loadUser();
	}

	@Override
	public void retrieve(final ValueStore valueStore) {

		// trigger remote retrieval
		super.retrieve(valueStore);

		// if we already have some data ready, put it immediately
		putValue(Constant.USER_NAME, _userController.getUser().getDisplayName());
	}

}
