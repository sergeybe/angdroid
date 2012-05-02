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

import android.app.Activity;

import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.payment.PaymentConstant;

/**
 * The OnPaymentChangedObserver receives notifications about payment process state changes.
 * \sa GameItemsScreenActivity
 */
public interface OnPaymentChangedObserver {

	/**
	 * Flag indicating that toast notification should be shown.
	 */
	public static final int	FLAG_TOAST_SHOW				= Constant.PAYMENT_TOAST_SHOW;
	
	/**
	 * Flag indicating that Scoreloop UI should return from it's current activity.
	 */
	public static final int	FLAG_UI_LEAVE				= Constant.PAYMENT_UI_LEAVE;

	/**
	 * Payment failed because the item has been already purchased.
	 */
	public static final int	RESULT_ALREADY_PURCHASED	= PaymentConstant.RESULT_ALREADY_PURCHASED;

	/**
	 * Payment failed because the id of an item was invalid.
	 */
	public static final int	RESULT_INVALID_GAME_ITEM	= PaymentConstant.RESULT_INVALID_GAME_ITEM;
	
	/**
	 * Payment failed because an item doesn't have any payment methods available.
	 */
	public static final int	RESULT_NO_PAYMENT_METHODS	= PaymentConstant.RESULT_NO_PAYMENT_METHODS;
	
	/**
	 * Payment has been successful.
	 */
	public static final int	RESULT_PAYMENT_BOOKED		= Activity.RESULT_OK;
	
	/**
	 * Payment has been canceled by the user.
	 */
	public static final int	RESULT_PAYMENT_CANCELED		= Activity.RESULT_CANCELED;
	
	/**
	 * Payment failed because of an internal error.
	 */
	public static final int	RESULT_PAYMENT_FAILED		= PaymentConstant.RESULT_PAYMENT_FAILED;
	
	/**
	 * Payment is in progress.
	 */
	public static final int	RESULT_PAYMENT_PENDING		= PaymentConstant.RESULT_PAYMENT_PENDING;

	/**
	 * This methods allows to handle different payment states.
	 *
	 * @param gameItem the game item being purchased
	 * @param resultCode (see RESULT_xxx) 
	 * @param wasPendingPayment 
	 * @return flags indicating whether to show a toast message and/or to leave the scoreloop ui (see FLAG_xxx)
	 */
	int onPaymentChanged(GameItem gameItem, int resultCode, boolean wasPendingPayment);
}
