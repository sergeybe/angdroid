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

package com.scoreloop.client.android.ui.component.payment;

import android.app.Activity;

public class PaymentConstant {

	public static final String	GAME_ITEM						= "gameItem";
	public static final String	GAME_ITEM_ID					= "gameItemId";
	public static final String	GAME_ITEMS_MODE					= "gameItemsMode";
	public static final int		GAME_ITEMS_MODE_COIN_PACK		= 1;
	public static final int		GAME_ITEMS_MODE_GAME_ITEM		= 0;
	public static final String	PAYMENT_EXPLICIT_CURRENCY		= "paymentExplicitCurrency";
	public static final String	PAYMENT_METHOD					= "paymentMethod";
	public static final int		RESULT_ALREADY_PURCHASED		= Activity.RESULT_FIRST_USER;
	public static final int		RESULT_INVALID_GAME_ITEM		= Activity.RESULT_FIRST_USER + 1;
	public static final int		RESULT_NO_PAYMENT_METHODS		= Activity.RESULT_FIRST_USER + 2;
	public static final int		RESULT_PAYMENT_FAILED			= Activity.RESULT_FIRST_USER + 3;
	public static final int		RESULT_PAYMENT_PENDING			= Activity.RESULT_FIRST_USER + 4;
	public static final String	TAGS							= "tags";
	public static final String	VIEW_FLAGS						= "viewFlags";
	public static final int		VIEW_FLAGS_HIDE_PURCHASED_ITEMS	= 0x01;
	public static final int		VIEW_FLAGS_INTERNAL_USAGE		= 0x02;
	public static final int		VIEW_FLAGS_SHOW_ALL_PRICES		= 0x04;
	public static final int		VIEW_FLAGS_SHOW_TOAST			= 0x08;
	public static final int		VIEW_FLAGS_STAND_ALONE			= 0x10;
}
