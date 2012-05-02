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

import com.scoreloop.client.android.core.model.GameItem;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentHeaderActivity;
import com.scoreloop.client.android.ui.framework.ValueStore;
import com.scoreloop.client.android.ui.framework.ValueStore.RetrievalMode;
import com.scoreloop.client.android.ui.util.ImageDownloader;

public class PaymentHeaderActivity extends ComponentHeaderActivity {

	private static final int	DRAWABLE_ID_GAME_ITEM	= R.drawable.sl_header_icon_shop;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.sl_header_default);

		// fill UI with initial data
		setCaption(getGame().getName());
		setTitle(getResources().getString(R.string.sl_purchase));
		getImageView().setImageDrawable(getResources().getDrawable(DRAWABLE_ID_GAME_ITEM));

		addObservedKeys(PaymentConstant.GAME_ITEM);
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		updateUI();
	}

	@Override
	public void onValueSetDirty(final ValueStore valueStore, final String key) {
		if (PaymentConstant.GAME_ITEM.equals(key)) {
			getScreenValues().retrieveValue(key, RetrievalMode.NOT_DIRTY, null);
		}
	}

	private void updateUI() {
		final GameItem gameItem = getScreenValues().getValue(PaymentConstant.GAME_ITEM, null);
		if (gameItem == null) {
			return;
		}

		// set sub-title
		setSubTitle(gameItem.getName());

		// set icon
		final String imageKey = gameItem.getDefaultImageKey();
		if (imageKey != null) {
			final String imageUrl = gameItem.getImageUrlForKey(imageKey);
			ImageDownloader.downloadImage(imageUrl, getResources().getDrawable(DRAWABLE_ID_GAME_ITEM), getImageView(), null);
		} else if (gameItem.isCoinPack()) {
			getImageView().setImageDrawable(getResources().getDrawable(R.drawable.sl_header_icon_add_coins));
		}
	}
}
