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

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.scoreloop.client.android.core.controller.PaymentProviderController;
import com.scoreloop.client.android.core.controller.PaymentProviderControllerObserver;
import com.scoreloop.client.android.core.model.GameItem;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseActivity;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.NavigationIntent;
import com.scoreloop.client.android.ui.util.ImageDownloader;

abstract public class AbstractCheckoutListActivity extends ComponentListActivity<BaseListItem> implements PaymentProviderControllerObserver {

	/**
	 * 
	 * @param context
	 * @param gameItem
	 * @param resultCode
	 * @param error may be null
	 */
	public static void showGameItemToast(final Context context, final GameItem gameItem, final int resultCode, final Exception error) {
		String message = null;
		final String itemName = gameItem != null ? gameItem.getName() : context.getString(R.string.sl_unknown_game_item);
		String errorMessage = (error != null) ? error.getLocalizedMessage() : "";
		switch (resultCode) {
		case RESULT_OK:
			message = String.format(context.getString(R.string.sl_format_payment_booked), itemName);
			break;
		case PaymentConstant.RESULT_ALREADY_PURCHASED:
			errorMessage = context.getString(R.string.sl_payment_result_already_purchased);
			// fall through
		case PaymentConstant.RESULT_INVALID_GAME_ITEM:
			errorMessage = context.getString(R.string.sl_payment_result_invalid_item);
			// fall through
		case PaymentConstant.RESULT_NO_PAYMENT_METHODS:
			errorMessage = context.getString(R.string.sl_payment_result_no_payment_methods);
			// fall through
		case PaymentConstant.RESULT_PAYMENT_FAILED:
			if (errorMessage.length() == 0) {
				message = String.format(context.getString(R.string.sl_format_payment_failed), itemName);
			} else {
				message = String.format(context.getString(R.string.sl_format_payment_failed_msg), itemName, errorMessage);
			}
			break;
		case RESULT_CANCELED:
			message = String.format(context.getString(R.string.sl_format_payment_canceled), itemName);
			break;
		case PaymentConstant.RESULT_PAYMENT_PENDING:
			message = String.format(context.getString(R.string.sl_format_payment_pending), itemName);
			;
			break;
		}
		if (message == null) {
			return; // nothing to show
		}

		// format game-item and show toast
		final Toast toast = BaseActivity.showToast(context, message, null, Toast.LENGTH_LONG);
		final ImageView toastIcon = (ImageView) toast.getView().findViewById(R.id.icon);
		if(!gameItem.isCoinPack()) {
			final Drawable loading = toast.getView().getResources().getDrawable(R.drawable.sl_icon_games_loading);
			ImageDownloader.downloadImage(gameItem.getDefaultImageUrl(), loading, toastIcon, null);
		}
		else {
			toastIcon.setImageDrawable(toastIcon.getResources().getDrawable(R.drawable.sl_icon_coins2));
		}
	}

	private boolean	_doesCheckout;

	protected void finishWithResult(final int code, final Exception error) {
		final Integer viewFlagsObj = getActivityArguments().<Integer> getValue(PaymentConstant.VIEW_FLAGS);
		final int viewFlags = viewFlagsObj != null ? viewFlagsObj : 0;
		final boolean showToast = (viewFlags & PaymentConstant.VIEW_FLAGS_SHOW_TOAST) != 0;

		if ((viewFlags & PaymentConstant.VIEW_FLAGS_STAND_ALONE) != 0) {
			if (showToast) {
				showGameItemToast(this, getGameItem(), code, error);
			}

			// finish display unless canceled
			if (code != RESULT_CANCELED) {
				finishDisplayWithResult(code);
			}
		} else if ((viewFlags & PaymentConstant.VIEW_FLAGS_INTERNAL_USAGE) != 0) {
			if (showToast) {
				showGameItemToast(this, getGameItem(), code, error);
			}
			stepOut();
		} else {
			// ask game what to do
			final int flags = getManager().paymentFinished(getGameItem(), code);

			// game developer want's to show a dialog, so go for it
			if ((flags & Constant.PAYMENT_TOAST_SHOW) != 0) {
				showGameItemToast(this, getGameItem(), code, error);
			}

			// now, transition according to developers intent
			if ((flags & Constant.PAYMENT_UI_LEAVE) != 0) {
				finishDisplay();
			} else {
				stepOut();
			}
		}
	}

	protected void finishWithResult(final int code) {
		finishWithResult(code, null);
	}

	protected GameItem getGameItem() {
		return getScreenValues().getValue(PaymentConstant.GAME_ITEM);
	}

	@Override
	protected boolean isNavigationAllowed(final NavigationIntent navigationIntent) {
		return !_doesCheckout;
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return true;
	}

	@Override
	public void paymentControllerDidCancel(final PaymentProviderController paymentProviderController) {
		Log.d("ScoreloopUI", "AbstractCheckoutListActivity.paymentControllerDidCancel");
		stopCheckout();
		finishWithResult(RESULT_CANCELED);
	}

	@Override
	public void paymentControllerDidFail(final PaymentProviderController paymentProviderController, final Exception error) {
		Log.e("ScoreloopUI", "AbstractCheckoutListActivity.paymentControllerDidFail");
		error.printStackTrace();
		stopCheckout();
		finishWithResult(PaymentConstant.RESULT_PAYMENT_FAILED, error);
	}

	@Override
	public void paymentControllerDidFinishWithPendingPayment(final PaymentProviderController paymentProviderController) {
		Log.d("ScoreloopUI", "AbstractCheckoutListActivity.paymentControllerDidFinishWithPendingPayment");
		stopCheckout();
		finishWithResult(PaymentConstant.RESULT_PAYMENT_PENDING);
	}

	@Override
	public void paymentControllerDidSucceed(final PaymentProviderController paymentProviderController) {
		Log.d("ScoreloopUI", "AbstractCheckoutListActivity.paymentControllerDidSucceed");
		stopCheckout();
		finishWithResult(RESULT_OK);
	}

	protected void startCheckout() {
		_doesCheckout = true;
		showSpinner();
	}

	protected abstract void stepOut();

	protected void stopCheckout() {
		if (_doesCheckout) {
			hideSpinner();
		}
		_doesCheckout = false;
	}
}
