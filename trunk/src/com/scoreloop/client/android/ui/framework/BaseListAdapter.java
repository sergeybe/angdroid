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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class BaseListAdapter<T extends BaseListItem> extends ArrayAdapter<T> {

	public static interface OnListItemClickListener<T extends BaseListItem> {
		public void onListItemClick(T item);
	}

	private static int	_viewTypeCount	= 1;

	public static void setViewTypeCount(final int count) {
		_viewTypeCount = count;
	}

	protected OnListItemClickListener<T>	_listItemClickListener;

	public BaseListAdapter(final Context context) {
		super(context, 0);
	}

	@Override
	public int getItemViewType(final int position) {
		return getItem(position).getType();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return getItem(position).getView(convertView, parent);
	}

	@Override
	public int getViewTypeCount() {
		return _viewTypeCount;
	}

	@Override
	public boolean isEnabled(final int position) {
		return getItem(position).isEnabled();
	}

	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (_listItemClickListener != null) {
			_listItemClickListener.onListItemClick(getItem(position));
		}
	}

	public void setOnListItemClickListener(final OnListItemClickListener<T> listener) {
		_listItemClickListener = listener;
	}
}
