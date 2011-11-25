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

package com.scoreloop.client.android.ui.component.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class EmptyListItem extends BaseListItem {

	public EmptyListItem(final Context context, final String title) {
		super(context, null, title);
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_EMPTY;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_empty, null);
		}
		final TextView title = (TextView) view.findViewById(R.id.sl_title);
		title.setText(getTitle());
		return view;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}