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

import android.os.Bundle;

import com.scoreloop.client.android.core.controller.PaymentProviderController;
import com.scoreloop.client.android.core.model.PaymentMethod;
import com.scoreloop.client.android.core.model.Price;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class PriceListActivity extends AbstractCheckoutListActivity {

	private PaymentMethod getPaymentMethod() {
		return getActivityArguments().getValue(PaymentConstant.PAYMENT_METHOD);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new BaseListAdapter<BaseListItem>(this));
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		if (item instanceof PriceListItem) {
			final PriceListItem priceListItem = (PriceListItem) item;
			final PaymentMethod paymentMethod = getPaymentMethod();
			final PaymentProviderController paymentProviderController = PaymentProviderController.getPaymentProviderController(this,
					paymentMethod.getPaymentProvider(), getSession());

			startCheckout();
			paymentProviderController.checkout(this, getGameItem(), priceListItem.getTarget());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		updateUI();
	}

	@Override
	public void paymentControllerDidCancel(final PaymentProviderController paymentProviderController) {
		super.paymentControllerDidCancel(paymentProviderController);
		displayPrevious();
	}

	@Override
	public void paymentControllerDidFail(final PaymentProviderController paymentProviderController, final Exception error) {
		super.paymentControllerDidFail(paymentProviderController, error);
		displayPrevious();
	}

	@Override
	protected void stepOut() {

		// step out two times (one time ourself, one time for the payment-methods-activity)
		displayPrevious(true);
		displayPrevious(true);
	}

	private void updateUI() {
		final PaymentMethod paymentMethod = getPaymentMethod();

		// clear adapter
		final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
		adapter.clear();

		// add payment-method-info
		adapter.add(new CaptionListItem(this, null, getResources().getString(R.string.sl_payment_method)));
		adapter.add(new PaymentMethodListItem(this, paymentMethod, null));

		// add prices
		adapter.add(new CaptionListItem(this, null, getResources().getString(R.string.sl_prices)));
		for (final Price price : paymentMethod.getPrices()) {
			adapter.add(new PriceListItem(this, price));
		}
	}
}
