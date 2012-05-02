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

package com.scoreloop.client.android.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.component.payment.PaymentConstant;
import com.scoreloop.client.android.ui.framework.ScreenActivity;

/**
 * The GameItemsScreenActivity displays game items that can be purchased by the user.
 * From the game items screen the user can purchase or obtain (if it is free) a game item.
 * If an item can be purchased the user have a possibility to select one of payment methods
 * configured for the game item.
 * 
 * The payment process can be monitored using OnPaymentChangedObserver.
 * 
 * Basic usage:
 * For the GameItemsScreenActivity to be available the scoreloop.properties
 * file must be configured correctly. Different payment methods supported by the application 
 * can also be configured in the scoreloop.properties. Take a look at the @link scoreloopui-integratepayments Payments Integration Guide@endlink for more details.
 * 
 * Basic Usage:
 * -# Configure the scoreloop.properties file correctly.
 * -# Ensure that the ScoreloopManagerSingleton has been properly initialized.
 * -# Start the GameItemsScreenActivity using an <a href="http://developer.android.com/reference/android/content/Intent.html">android.content.Intent</a>.
 * -# Implement OnPaymentChangedObserver.OnPaymentChanged handling at least result code RESULT_PAYMENT_BOOKED. The implementation should initiate provision of the game item.
 * If a game item is a downloadable item hosted on Scoreloop server, the url to the item can obtained using ScoreloopManager.getGameItemDownloadUrl method.
 * 
 * \sa OnPaymentChangedObserver
 * \sa ScoreloopManager.setOnCanStartGamePlayObserver
 * \sa ScoreloopManager.getGameItemDownloadUrl
 */
public class GameItemsScreenActivity extends ScreenActivity {

	/**
	 * \internal
	 */
	public static final String	MODE							= PaymentConstant.GAME_ITEMS_MODE;

	/**
	 * \internal
	 */
	public static final int		MODE_COIN_PACK					= PaymentConstant.GAME_ITEMS_MODE_COIN_PACK;

	/**
	 * \internal
	 */
	public static final int		MODE_GAME_ITEM					= PaymentConstant.GAME_ITEMS_MODE_GAME_ITEM;

	/**
	 * \internal
	 */
	public static final String	PAYMENT_EXPLICIT_CURRENCY		= PaymentConstant.PAYMENT_EXPLICIT_CURRENCY;

	/**
	 * \internal
	 */
	public static final String	TAGS							= PaymentConstant.TAGS;

	/**
	 * \internal
	 */
	public static final int		VIEW_FLAGS_HIDE_PURCHASED_ITEMS	= PaymentConstant.VIEW_FLAGS_HIDE_PURCHASED_ITEMS;

	/**
	 * \internal
	 */
	public static final int		VIEW_FLAGS_SHOW_ALL_PRICES		= PaymentConstant.VIEW_FLAGS_SHOW_ALL_PRICES;

	/**
	 * \internal
	 */
	public static final String	VIEW_FLAGS						= PaymentConstant.VIEW_FLAGS;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Configuration configuration = ScoreloopManagerSingleton.getImpl().getConfiguration();
		if (!configuration.isFeatureEnabled(Configuration.Feature.PAYMENT)) {
			finish();
			return;
		}

		final Intent intent = getIntent();

		final int mode = intent.getIntExtra(MODE, MODE_GAME_ITEM);
		if (!((mode == MODE_COIN_PACK) || (mode == MODE_GAME_ITEM))) {
			Log.e(Constant.LOG_TAG, "invalid mode extra");
			finish();
		}
		final String explicitCurrency = intent.getStringExtra(PAYMENT_EXPLICIT_CURRENCY);
		if ((explicitCurrency != null) && (explicitCurrency.length() != 3)) {
			Log.e(Constant.LOG_TAG, "strange explicit currency: " + explicitCurrency);
			finish();
		}
		final String tagsArray[] = intent.getStringArrayExtra(TAGS);
		List<String> tags = null;
		if (tagsArray != null) {
			tags = new ArrayList<String>();
			Collections.addAll(tags, tagsArray);
		}
		final int viewFlags = intent.getIntExtra(VIEW_FLAGS, 0);

		final Factory factory = ScoreloopManagerSingleton.getImpl().getFactory();
		display(factory.createGameItemsScreenDescription(mode, explicitCurrency, tags, viewFlags), savedInstanceState);
	}
}
