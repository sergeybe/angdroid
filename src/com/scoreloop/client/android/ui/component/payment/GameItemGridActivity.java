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

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.scoreloop.client.android.core.controller.GameItemsController;
import com.scoreloop.client.android.core.controller.PendingPaymentProcessor;
import com.scoreloop.client.android.core.controller.PendingPaymentProcessor.Observer;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.core.model.Payment;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListAdapter.OnListItemClickListener;

public class GameItemGridActivity extends ComponentActivity implements Observer, OnItemClickListener,
		OnListItemClickListener<GameItemListItem> {

	private GameItemsController	_gameItemsController;
	private GridView			grid;

	private String getExplicitCurrency() {
		return getActivityArguments().getValue(PaymentConstant.PAYMENT_EXPLICIT_CURRENCY);
	}

	public ListAdapter getListAdapter() {
		return grid.getAdapter();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_shelf_view, true);

		grid = (GridView) findViewById(R.id.sl_grid);
		setListAdapter(new BaseListAdapter<GameItemListItem>(this));

		grid.setFocusable(true);
		grid.setOnItemClickListener(this);

		_gameItemsController = new GameItemsController(getRequestControllerObserver());

		// we always want to retrieve the ownership from the server and not use cached (stale) data here.
		_gameItemsController.setCachedResponseUsed(false);
		final List<String> tags = getActivityArguments().getValue(PaymentConstant.TAGS);
		if (tags != null) {
			_gameItemsController.setTags(tags);
		}
		_gameItemsController.setCurrency(getExplicitCurrency());
	}

	private void setListAdapter(final BaseListAdapter<GameItemListItem> adapter) {
		grid.setAdapter(adapter);
		getBaseListAdapter().setOnListItemClickListener(this);
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		final BaseListAdapter<GameItemListItem> baseListAdapter = getBaseListAdapter();
		final GameItemListItem item = baseListAdapter.getItem(position);
		if (item.isEnabled()) {
			baseListAdapter.onItemClick(parent, view, position, id);
		}
	}

	@SuppressWarnings("unchecked")
	public BaseListAdapter<GameItemListItem> getBaseListAdapter() {
		return (BaseListAdapter<GameItemListItem>) getListAdapter();
	}

	private void onGameItems() {
		final BaseListAdapter<GameItemListItem> adapter = getBaseListAdapter();
		adapter.clear();

		final boolean hidePurchasedItems = (getActivityArguments().<Integer> getValue(PaymentConstant.VIEW_FLAGS) & PaymentConstant.VIEW_FLAGS_HIDE_PURCHASED_ITEMS) != 0;
		for (final GameItem gameItem : _gameItemsController.getGameItems()) {
			if (hidePurchasedItems && (gameItem.isPurchased()) && !gameItem.isCollectable()) {
				continue;
			}
			final boolean hasPendingPayment = PendingPaymentProcessor.getInstance(getSession()).hasPendingPaymentForGameItem(
					gameItem.getIdentifier());
			adapter.add(new GameItemListItem(this, gameItem, hasPendingPayment));
		}
	}

	@Override
	public void onListItemClick(final GameItemListItem item) {
		final GameItem gameItem = item.getTarget();
		final int viewFlags = getActivityArguments().<Integer> getValue(PaymentConstant.VIEW_FLAGS);
		display(getFactory().createPaymentMethodsScreenDescription(gameItem.getIdentifier(), getExplicitCurrency(), viewFlags));
	}

	@Override
	protected void onPause() {
		super.onPause();
		PendingPaymentProcessor.getInstance(getSession()).removeObserver(this);
	}

	@Override
	public void onRefresh(final int flags) {
		showSpinnerFor(_gameItemsController);

		final Integer mode = getActivityArguments().getValue(PaymentConstant.GAME_ITEMS_MODE);
		if (mode == PaymentConstant.GAME_ITEMS_MODE_GAME_ITEM) {
			_gameItemsController.loadGameItems();
		} else if (mode == PaymentConstant.GAME_ITEMS_MODE_COIN_PACK) {
			_gameItemsController.loadCoinPacks();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		PendingPaymentProcessor.getInstance(getSession()).addObserver(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setNeedsRefresh();
	}

	@Override
	public void pendingPaymentProcessorDidProcessPayment(final PendingPaymentProcessor processor, final Payment payment) {
		final String gameItemIdentifier = payment.getGameItemIdentifier();

		// if we display a game-item matching the argument here, refresh the game-items list
		// so that the pending status gets updated etc.
		for (final GameItem gameItem : _gameItemsController.getGameItems()) {
			if (gameItem.getIdentifier().equals(gameItemIdentifier)) {
				setNeedsRefresh();
			}
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		if (aRequestController == _gameItemsController) {
			onGameItems();
		}
	}
}
