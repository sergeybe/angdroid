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

package com.scoreloop.client.android.ui.component.market;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.StandardListItem;

public class MarketListItem extends StandardListItem<Void> {

	public static class MarketViewHolder extends StandardViewHolder {
		TextView	number;
	}

	private String	_imageUrl;
	private Integer	_counter;

	public MarketListItem(final ComponentActivity activity, final Drawable drawable, final String title, final String subTitle) {
		super(activity, drawable, title, subTitle, null);
	}

	public void setCounter(Integer counter) {
		_counter = counter;
	}

	@Override
	protected StandardViewHolder createViewHolder() {
		return new MarketViewHolder();
	}

	@Override
	protected void fillViewHolder(final View view, final StandardViewHolder holder) {
		super.fillViewHolder(view, holder);
		MarketViewHolder marketHolder = (MarketViewHolder)holder;
		marketHolder.number = (TextView) view.findViewById(R.id.sl_number);
	}

	@Override
	protected void updateViews(final StandardViewHolder holder) {
		super.updateViews(holder);
		MarketViewHolder marketHolder = (MarketViewHolder)holder;
		if(marketHolder.number != null) {
			if(_counter != null) {
				marketHolder.number.setText(_counter.toString());
			}
			else {
				marketHolder.number.setText(null);
			}
		}
	}

	@Override
	protected String getImageUrl() {
		return _imageUrl;
	}

	public void setImageUrl(final String imageUrl) {
		_imageUrl = imageUrl;
	}

	protected int getLayoutId() {
		return R.layout.sl_list_item_market;
	}
}
