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

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.core.model.Money;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.util.ImageDownloader;

public class GameItemListItem extends BaseListItem {

	private final boolean	_isPending;

	private final GameItem	_target;
	protected Drawable		buttonDrawable;
	private String			_subTitle;
	private String			_priceText;
	private String			_priceTextSmall;

	public GameItemListItem(final ComponentActivity activity, final GameItem target, final boolean isPending) {
		super(activity, null, null);
		_target = target;
		_isPending = isPending;

		if (target.isCoinPack()) {
			setDrawable(activity.getResources().getDrawable(R.drawable.sl_button_add_coins));
		} else {
			setDrawable(activity.getResources().getDrawable(R.drawable.sl_icon_market));
		}

		setTitle(target.getName());
		setSubTitle(target.getDescription());

		int buttonDrawableId = R.drawable.sl_button_buy;

		if (_isPending) {
			setPriceText("");
			setPriceTextSmall(R.string.sl_pending_payment);
			buttonDrawableId = R.drawable.sl_button_buy_disabled;
		} else if ((target.getPurchaseDate() != null) && !target.isCollectable()) {
			setPriceText("");
			setPriceTextSmall(R.string.sl_purchased_item);
			buttonDrawableId = R.drawable.sl_button_buy_disabled;
		} else if (target.isFree()) {
			setPriceTextSmall("");
			setPriceText(R.string.sl_free_item);
		} else {
			final List<Money> lowestPrices = target.getLowestPrices();
			if (lowestPrices.size() > 0) {
				final Money preferedLowestPrice = Money.getPreferred(lowestPrices, Locale.getDefault(), "USD");
				final String lowestPricesString = StringFormatter.formatMoney(preferedLowestPrice, activity.getConfiguration());
				setPriceTextSmall(R.string.sl_price_from);
				setPriceText(lowestPricesString);
			} else {
				setPriceTextSmall("");
				setPriceText(R.string.sl_purchasable_item);
			}
		}

		buttonDrawable = activity.getResources().getDrawable(buttonDrawableId);
	}

	protected GameItem getTarget() {
		return _target;
	}

	public String getPriceTextSmall() {
		return _priceTextSmall;
	}

	public void setPriceTextSmall(final String subTitle3) {
		this._priceTextSmall = subTitle3;
	}

	public void setPriceTextSmall(final int resId) {
		setPriceTextSmall(getContext().getResources().getString(resId));
	}

	public void setSubTitle(final String subTitle) {
		this._subTitle = subTitle;
	}

	public void setSubTitle(final int resId) {
		setSubTitle(getContext().getResources().getString(resId));
	}

	public String getSubTitle() {
		return _subTitle;
	}

	public String getPriceText() {
		return _priceText;
	}

	public void setPriceText(final String subTitle2) {
		this._priceText = subTitle2;
	}

	public void setPriceText(final int resId) {
		setPriceText(getContext().getResources().getString(resId));
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_grid_item_game_item, null);
		}
		final String imageUrl = getImageUrl();
		final ImageView icon = (ImageView) view.findViewById(R.id.sl_icon);
		if (imageUrl != null) {
			final Drawable drawable = getDrawableLoading();
			ImageDownloader.downloadImage(imageUrl, drawable, icon, getDrawableLoadingError());
		} else {
			final Drawable drawable = getDrawable();
			if (drawable != null) {
				icon.setImageDrawable(drawable);
			}
		}
		((TextView) view.findViewById(R.id.sl_title)).setText(getTitle());

		final TextView subTitle = (TextView) view.findViewById(R.id.sl_subtitle);
		if (subTitle != null) {
			subTitle.setText(getSubTitle());
		}
		final TextView priceText = (TextView) view.findViewById(R.id.sl_price);
		if (priceText != null) {
			priceText.setVisibility(getPriceText().length() > 0 ? View.VISIBLE : View.GONE);
			priceText.setText(getPriceText());
		}
		final TextView priceTextSmall = (TextView) view.findViewById(R.id.sl_price_header);
		if (priceTextSmall != null) {
			priceTextSmall.setVisibility(getPriceTextSmall().length() > 0 ? View.VISIBLE : View.GONE);
			priceTextSmall.setText(getPriceTextSmall());
		}

		view.findViewById(R.id.sl_buy_button).setBackgroundDrawable(buttonDrawable);
		return view;
	}

	protected String getImageUrl() {
		if (getTarget().isCoinPack()) {
			return null;
		}
		final String imageKey = getTarget().getDefaultImageKey();
		return imageKey != null ? getTarget().getImageUrlForKey(imageKey) : null;
	}

	@Override
	public Drawable getDrawable() {
		if (!getTarget().isCoinPack()) {
			return super.getDrawable();
		}

		// TODO: proper logic to select a coin pack icon
		// or remove all icons but one.
		int drawableID = R.drawable.sl_icon_coins1;
		if (getTarget().getCoinPackValue().getAmountInUnits().compareTo(new BigDecimal(10)) > 0) {
			drawableID = R.drawable.sl_icon_coins2;
		}
		return getContext().getResources().getDrawable(drawableID);
	}

	@Override
	public boolean isEnabled() {
		if (_isPending) {
			return false;
		}
		// enabled if game-item does not have an ownership or is collectible
		// (i.e can be bought multiple times)
		return (!getTarget().isPurchased()) || getTarget().isCollectable();
	}

	protected Drawable getDrawableLoading() {
		return getContext().getResources().getDrawable(R.drawable.sl_icon_games_loading);
	}

	protected Drawable getDrawableLoadingError() {
		return null;
	}

	@Override
	public int getType() {
		return 1;
	}
}
