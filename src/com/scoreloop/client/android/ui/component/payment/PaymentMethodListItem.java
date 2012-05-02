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

import com.scoreloop.client.android.core.controller.PaymentProviderController;
import com.scoreloop.client.android.core.model.PaymentMethod;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.StandardListItem;

public class PaymentMethodListItem extends StandardListItem<PaymentMethod> {

	private boolean						_isEnabled;
	private PaymentProviderController	_paymentProviderController;

	public PaymentMethodListItem(final ComponentActivity activity, final PaymentMethod target, final String priceInfo) {
		super(activity, activity.getResources().getDrawable(R.drawable.sl_icon_shop), target.getPaymentProvider().getName(), priceInfo,
				target);
	}

	@Override
	protected String getImageUrl() {
		return getTarget().getPaymentProvider().getImageUrl();
	}

	public PaymentProviderController getPaymentProviderController() {
		return _paymentProviderController;
	}

	@Override
	public boolean isEnabled() {
		return _isEnabled;
	}

	public void setEnabled(final boolean flag) {
		_isEnabled = flag;
	}

	public void setPaymentProviderController(final PaymentProviderController controller) {
		_paymentProviderController = controller;
	}
}
