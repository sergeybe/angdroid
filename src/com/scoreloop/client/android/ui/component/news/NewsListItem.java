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

package com.scoreloop.client.android.ui.component.news;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoreloop.client.android.core.addon.RSSItem;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.util.ImageDownloader;

public class NewsListItem extends BaseListItem {

	private final RSSItem	_item;

	public NewsListItem(final Context context, final RSSItem item) {
		super(context, null, item.getTitle());
		_item = item;
	}

	public RSSItem getItem() {
		return _item;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_NEWS;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_news, null);
		}

		final ImageView icon = (ImageView) view.findViewById(R.id.sl_list_item_news_icon);
		final int resId = _item.hasPersistentReadFlag() ? R.drawable.sl_icon_news_opened : R.drawable.sl_icon_news_closed;
		icon.setImageResource(resId);
		final String imageUrl = _item.getImageUrlString();
		if (imageUrl != null) {
			ImageDownloader.downloadImage(imageUrl, getContext().getResources().getDrawable(resId), icon, null);
		}

		final TextView title = (TextView) view.findViewById(R.id.sl_list_item_news_title);
		title.setText(getTitle());

		final TextView descripiton = (TextView) view.findViewById(R.id.sl_list_item_news_description);
		descripiton.setText(_item.getDescription());

		final View accessory = view.findViewById(R.id.sl_list_item_news_accessory);
		accessory.setVisibility(isEnabled() ? View.VISIBLE : View.INVISIBLE);

		return view;
	}

	@Override
	public boolean isEnabled() {
		return _item.getLinkUrlString() != null;
	}
}
