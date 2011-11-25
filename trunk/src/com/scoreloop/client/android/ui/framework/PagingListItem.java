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

package com.scoreloop.client.android.ui.framework;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.angdroid.nightly.R;

public class PagingListItem extends BaseListItem {

	private static Drawable getDrawable(final Context context, final PagingDirection pagingDirection) {
		final Resources resources = context.getResources();

		switch (pagingDirection) {
		case PAGE_TO_NEXT:
			return resources.getDrawable(R.drawable.sl_icon_next);
		case PAGE_TO_PREV:
			return resources.getDrawable(R.drawable.sl_icon_previous);
		case PAGE_TO_TOP:
			return resources.getDrawable(R.drawable.sl_icon_top);
		default:
			return null;
		}
	}

	public static String getTitle(final Context context, final PagingDirection pagingDirection) {
		switch (pagingDirection) {
		case PAGE_TO_NEXT:
			return context.getString(R.string.sl_next);
		case PAGE_TO_PREV:
			return context.getString(R.string.sl_previous);
		case PAGE_TO_TOP:
			return context.getString(R.string.sl_top);
		default:
			return null;
		}
	}

	private final PagingDirection	_pagingDirection;

	public PagingListItem(final Context context, final PagingDirection pagingDirection) {
		super(context, getDrawable(context, pagingDirection), getTitle(context, pagingDirection));
		_pagingDirection = pagingDirection;
	}

	public PagingDirection getPagingDirection() {
		return _pagingDirection;
	}

	@Override
	public int getType() {
		return 0; // paging items have the fixed type 0, component items thus have to start with 1
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_icon_title_small, null);
		}
		final ImageView icon = (ImageView) view.findViewById(R.id.sl_icon);
		icon.setImageDrawable(getDrawable());
		final TextView title = (TextView) view.findViewById(R.id.sl_title);
		title.setText(getTitle());
		
		return view;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
