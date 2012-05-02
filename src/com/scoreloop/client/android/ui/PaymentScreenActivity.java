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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.Factory;
import com.scoreloop.client.android.ui.component.payment.PaymentConstant;
import com.scoreloop.client.android.ui.framework.ScreenActivity;

/**
 */
public class PaymentScreenActivity extends ScreenActivity {

	/**
	 * 
	 */
	public static final String	GAME_ITEM_ID				= PaymentConstant.GAME_ITEM_ID;

	/**
	 * 
	 */
	public static final String	PAYMENT_EXPLICIT_CURRENCY	= PaymentConstant.PAYMENT_EXPLICIT_CURRENCY;

	/**
	 * 
	 */
	public static final int		RESULT_ALREADY_PURCHASED	= PaymentConstant.RESULT_ALREADY_PURCHASED;

	/**
	 * 
	 */
	public static final int		RESULT_INVALID_GAME_ITEM	= PaymentConstant.RESULT_INVALID_GAME_ITEM;

	/**
	 * 
	 */
	public static final int		RESULT_NO_PAYMENT_METHODS	= PaymentConstant.RESULT_NO_PAYMENT_METHODS;

	/**
	 * 
	 */
	public static final int		RESULT_PAYMENT_FAILED		= PaymentConstant.RESULT_PAYMENT_FAILED;

	/**
	 * 
	 */
	public static final int		RESULT_PAYMENT_PENDING		= PaymentConstant.RESULT_PAYMENT_PENDING;

	/**
	 * 
	 */
	public static final int		VIEW_FLAGS_SHOW_ALL_PRICES	= PaymentConstant.VIEW_FLAGS_SHOW_ALL_PRICES;

	/**
	 * 
	 */
	public static final int		VIEW_FLAGS_SHOW_TOASTS		= PaymentConstant.VIEW_FLAGS_SHOW_TOAST;

	/**
	 * 
	 */
	public static final String	VIEW_FLAGS					= PaymentConstant.VIEW_FLAGS;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Configuration configuration = ScoreloopManagerSingleton.getImpl().getConfiguration();
		if (!configuration.isFeatureEnabled(Configuration.Feature.PAYMENT)) {
			finish();
			return;
		}

		final Intent intent = getIntent();

		final String gameItemId = intent.getStringExtra(GAME_ITEM_ID);
		if ((gameItemId == null) || (gameItemId.length() == 0)) {
			Log.e(Constant.LOG_TAG, "missing extra parameter gameItemId");
			finish();
		}
		final String explicitCurrency = intent.getStringExtra(PAYMENT_EXPLICIT_CURRENCY);
		if ((explicitCurrency != null) && (explicitCurrency.length() != 3)) {
			Log.e(Constant.LOG_TAG, "strange explicit currency: " + explicitCurrency);
			finish();
		}
		final int viewFlags = PaymentConstant.VIEW_FLAGS_STAND_ALONE | intent.getIntExtra(VIEW_FLAGS, 0);

		final Factory factory = ScoreloopManagerSingleton.getImpl().getFactory();
		display(factory.createPaymentMethodsScreenDescription(gameItemId, explicitCurrency, viewFlags), savedInstanceState);
	}
}
