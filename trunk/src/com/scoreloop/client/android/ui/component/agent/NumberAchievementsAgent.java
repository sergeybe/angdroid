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

import com.scoreloop.client.android.core.controller.AchievementsController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.User;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class NumberAchievementsAgent extends BaseAgent {

	public static final String[]	SUPPORTED_KEYS	= { Constant.NUMBER_ACHIEVEMENTS, Constant.NUMBER_AWARDS };

	private AchievementsController	_achievementsController;

	public NumberAchievementsAgent() {
		super(SUPPORTED_KEYS);
	}

	@Override
	protected void onFinishRetrieve(final RequestController aRequestController, final ValueStore valueStore) {
		putValue(Constant.NUMBER_AWARDS, _achievementsController.getAwardList().getAwards().size()); // must be first!
		putValue(Constant.NUMBER_ACHIEVEMENTS, _achievementsController.countAchievedAwards());
	}

	@Override
	protected void onStartRetrieve(final ValueStore valueStore) {
		if (_achievementsController == null) {
			_achievementsController = new AchievementsController(this);
		}
		_achievementsController.setUser(valueStore.<User> getValue(Constant.USER));
		_achievementsController.loadAchievements();
	}
}
