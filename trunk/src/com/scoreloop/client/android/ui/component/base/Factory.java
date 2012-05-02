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

package com.scoreloop.client.android.ui.component.base;

import java.util.List;

import com.scoreloop.client.android.core.model.Challenge;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.core.model.PaymentMethod;
import com.scoreloop.client.android.core.model.User;
import com.scoreloop.client.android.ui.framework.ScreenDescription;

public interface Factory {

	ScreenDescription createAchievementScreenDescription(User user);

	ScreenDescription createChallengeAcceptScreenDescription(Challenge challenge);

	ScreenDescription createChallengeCreateScreenDescription(User user, Integer mode);

	ScreenDescription createChallengePaymentScreenDescription();

	ScreenDescription createChallengeScreenDescription(User user);

	ScreenDescription createEntryScreenDescription();

	ScreenDescription createGameDetailScreenDescription(Game game);

	ScreenDescription createGameItemsScreenDescription(int mode, String paymentCurrency, List<String> tags, int viewFlags);

	ScreenDescription createGameScreenDescription(User user, int mode);

	ScreenDescription createMarketScreenDescription(User user);

	ScreenDescription createNewsScreenDescription();

	ScreenDescription createPaymentMethodsScreenDescription(String gameItemId, String paymentCurrency, int viewFlags);

	ScreenDescription createPricesScreenDescription(GameItem gameItem, PaymentMethod paymentMethod, int viewFlags);

	ScreenDescription createProfileSettingsPictureScreenDescription(User user);

	ScreenDescription createProfileSettingsScreenDescription(User user);

	ScreenDescription createScoreScreenDescription(Game game, Integer mode, Integer leaderboard);

	ScreenDescription createUserAddBuddyScreenDescription();

	ScreenDescription createUserDetailScreenDescription(User user, Boolean playsSessionGame);

	ScreenDescription createUserScreenDescription(User user);
}
