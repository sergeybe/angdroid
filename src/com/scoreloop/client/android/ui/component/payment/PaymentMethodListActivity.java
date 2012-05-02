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

import java.util.List;
import java.util.Locale;

import android.os.Bundle;

import com.scoreloop.client.android.core.controller.GameItemController;
import com.scoreloop.client.android.core.controller.PaymentMethodsController;
import com.scoreloop.client.android.core.controller.PaymentProviderController;
import com.scoreloop.client.android.core.controller.PendingPaymentProcessor;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.Entity;
import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.core.model.Money;
import com.scoreloop.client.android.core.model.PaymentMethod;
import com.scoreloop.client.android.core.model.PaymentProvider;
import com.scoreloop.client.android.core.model.Price;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.ValueStore;

class FreeListItem extends StandardListItem<Void> {
	public FreeListItem(final ComponentActivity activity) {
		super(activity, null, activity.getString(R.string.sl_payment_get_it), activity.getString(R.string.sl_payment_its_free), null);
	}
}

public class PaymentMethodListActivity extends AbstractCheckoutListActivity {

	private static final String			FALLBACK_CURRENCY_CODE			= "USD";
	private static final int			MODE_LOAD_GAME_ITEM				= 0;
	private static final int			MODE_LOAD_PAYMENT_METHODS		= 1;
	private static final int			MODE_SUBMIT_GAME_ITEM_OWNERSHIP	= 2;

	private FreeListItem				_freeListItem;
	private GameItemController			_gameItemController;
	private int							_mode;
	private PaymentMethodsController	_paymentMethodsController;

	private FreeListItem getFreeListItem() {
		if (_freeListItem == null) {
			_freeListItem = new FreeListItem(this);
		}
		return _freeListItem;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new BaseListAdapter<BaseListItem>(this));

		// prepare controllers with game-item-entity
		final Entity gameItemEntity = getGameItemEntity();

		_gameItemController = new GameItemController(getRequestControllerObserver());
		_gameItemController.setCachedResponseUsed(false);
		_gameItemController.setGameItem(gameItemEntity);

		_paymentMethodsController = new PaymentMethodsController(getRequestControllerObserver());
		_paymentMethodsController.setCurrency(getActivityArguments().<String> getValue(PaymentConstant.PAYMENT_EXPLICIT_CURRENCY));
		_paymentMethodsController.setGameItem(gameItemEntity);
	}

	private Entity getGameItemEntity() {
		return getSession().getEntityFactory().createEntity(GameItem.ENTITY_NAME,
				getActivityArguments().<String> getValue(PaymentConstant.GAME_ITEM_ID));
	}

	private void onGameItem() {
		final GameItem gameItem = _gameItemController.getGameItem();

		// if game-item was purchased already, (report this back and) finish activity
		if ((gameItem.getPurchaseDate() != null) && !gameItem.isCollectable()) {
			finishWithResult(PaymentConstant.RESULT_ALREADY_PURCHASED);
			return;
		}

		// set details entry
		final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
		adapter.clear();
		adapter.add(new CaptionListItem(this, null, getString(R.string.sl_details)));
		adapter.add(new GameItemDetailListItem(this, null, gameItem));

		// set screen values to propagate game-item info to the header
		final ValueStore store = getScreenValues();
		store.putValue(PaymentConstant.GAME_ITEM, gameItem);

		if (gameItem.isFree()) {
			// add a button to get the free item (set the ownership of it)
			adapter.add(new CaptionListItem(this, null, getString(R.string.sl_actions)));
			adapter.add(getFreeListItem());
		} else {
			// load the payment methods for the non-free game-item
			setNeedsRefresh(MODE_LOAD_PAYMENT_METHODS, RefreshMode.SET);
		}
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		final GameItem gameItem = getGameItem();

		if (item == _freeListItem) {
			setNeedsRefresh(MODE_SUBMIT_GAME_ITEM_OWNERSHIP, RefreshMode.SET);
		} else if (item instanceof PaymentMethodListItem) {
			final PaymentMethodListItem methodItem = (PaymentMethodListItem) item;
			final PaymentMethod paymentMethod = methodItem.getTarget();

			final List<Price> prices = paymentMethod.getPrices();
			if (showAllPrices() && (prices.size() > 1)) {

				// we have more than one price (i.e. more than one currency) so let the user decide which one to take
				display(getFactory().createPricesScreenDescription(gameItem, paymentMethod,
						getActivityArguments().<Integer> getValue(PaymentConstant.VIEW_FLAGS, 0)));
			} else {
				startCheckout();

				// retrieve controller from item (if stored there after prepare call) or create it here
				PaymentProviderController paymentProviderController = methodItem.getPaymentProviderController();
				if (paymentProviderController == null) {
					paymentProviderController = PaymentProviderController.getPaymentProviderController(this,
							paymentMethod.getPaymentProvider(), getSession());
				}

				// invoke controller to do checkout
				final Price preferedPrice = Money.getPreferred(prices, Locale.getDefault(), FALLBACK_CURRENCY_CODE);
				paymentProviderController.checkout(this, gameItem, preferedPrice);
			}
		}
	}

	private void onPaymentMethods() {

		// add caption
		final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
		adapter.add(new CaptionListItem(this, null, getString(R.string.sl_payment_methods)));

		// fill adapter with supported payment methods
		int paymentMethodCount = 0;
		PaymentMethodListItem item = null;
		for (final PaymentMethod paymentMethod : _paymentMethodsController.getPaymentMethods()) {
			++paymentMethodCount;
			final PaymentProvider paymentProvider = paymentMethod.getPaymentProvider();

			// if payment provider indicates that the controller needs a prepare call, create the controller and prepare it
			PaymentProviderController paymentProviderController = null;
			if (paymentProvider.controllerSupportsPrepare()) {
				paymentProviderController = PaymentProviderController.getPaymentProviderController(this, paymentProvider, getSession());
				paymentProviderController.prepare();
			}

			// add payment-method-item
			String pricesString = null;
			if (showAllPrices()) {
				pricesString = StringFormatter.formatPriceList(this, paymentMethod.getPrices(), getConfiguration());
			} else {
				final Price preferedPrice = Money.getPreferred(paymentMethod.getPrices(), Locale.getDefault(), FALLBACK_CURRENCY_CODE);
				pricesString = StringFormatter.formatPrice(this, preferedPrice, getConfiguration());
			}
			item = new PaymentMethodListItem(this, paymentMethod, pricesString);
			item.setEnabled(true);

			// store controller (which might be null) in the item to spare repeated construction later on
			item.setPaymentProviderController(paymentProviderController);
			adapter.add(item);
		}

		// if no supported payment methods, (report back and) finish activity
		if (paymentMethodCount == 0) {
			finishWithResult(PaymentConstant.RESULT_NO_PAYMENT_METHODS);
		}
	}

	@Override
	public void onRefresh(final int flags) {
		_mode = flags;
		if (flags == MODE_LOAD_GAME_ITEM) {
			showSpinnerFor(_gameItemController);
			_gameItemController.loadGameItem();
		} else if (flags == MODE_SUBMIT_GAME_ITEM_OWNERSHIP) {
			startCheckout();
			_gameItemController.submitOwnership();
		} else if (flags == MODE_LOAD_PAYMENT_METHODS) {
			showSpinnerFor(_paymentMethodsController);
			_paymentMethodsController.loadPaymentMethods();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// if game-item has a pending payment, (report this back and) finish activity
		if (PendingPaymentProcessor.getInstance(getSession()).hasPendingPaymentForGameItem(getGameItemEntity().getIdentifier())) {
			finishWithResult(PaymentConstant.RESULT_PAYMENT_PENDING);
			return;
		}

		setNeedsRefresh(MODE_LOAD_GAME_ITEM, RefreshMode.SET);
	}

	private void onSubmitOwnershipFailed() {
		stopCheckout();
		finishWithResult(PaymentConstant.RESULT_ALREADY_PURCHASED);
	}

	private void onSubmitOwnershipSucceeded() {
		stopCheckout();
		finishWithResult(RESULT_OK);
	}

	@Override
	protected void requestControllerDidFailSafe(final RequestController aRequestController, final Exception anException) {
		if (_mode == MODE_LOAD_GAME_ITEM) {
			finishWithResult(PaymentConstant.RESULT_INVALID_GAME_ITEM);
		} else if (_mode == MODE_SUBMIT_GAME_ITEM_OWNERSHIP) {
			onSubmitOwnershipFailed();
		} else if (_mode == MODE_LOAD_PAYMENT_METHODS) {
			super.requestControllerDidFailSafe(aRequestController, anException);
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		if (_mode == MODE_LOAD_GAME_ITEM) {
			onGameItem();
		} else if (_mode == MODE_SUBMIT_GAME_ITEM_OWNERSHIP) {
			onSubmitOwnershipSucceeded();
		} else if (_mode == MODE_LOAD_PAYMENT_METHODS) {
			onPaymentMethods();
		}
	}

	private boolean showAllPrices() {
		final Integer viewFlags = getActivityArguments().getValue(PaymentConstant.VIEW_FLAGS, 0);
		return (viewFlags & PaymentConstant.VIEW_FLAGS_SHOW_ALL_PRICES) != 0;
	}

	@Override
	protected void stepOut() {
		displayPrevious(true);
	}

}
