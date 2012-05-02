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
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseListItem {

	private final Context			_context;
	private Drawable				_drawable;
	private final LayoutInflater	_layoutInflater;
	private String					_title;

	public BaseListItem(final Context context, final Drawable drawable, final String title) {
		_context = context;
		_drawable = drawable;
		_title = title;
		_layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	protected Context getContext() {
		return _context;
	}

	public Drawable getDrawable() {
		return _drawable;
	}

	protected LayoutInflater getLayoutInflater() {
		return _layoutInflater;
	}

	public void setTitle(final String title) {
		this._title = title;
	}

	public String getTitle() {
		return _title;
	}

	/**
	 * Returns the type of this list item, should be distinct for each specific list
	 * item type, otherwise androids view cache will lead to funny results.
	 * @return type of this list item.
	 */
	public abstract int getType();

	/**
	 * Returns actual view of this item for the ListView.
	 * @param view current view, if this is <em>null</em> create a new view,
	 *        otherwise you can just return this view for caching reasons
	 *        if it didn't change.
	 * @param parent the parent view group.
	 * @return view of this list item.
	 */
	public abstract View getView(View view, ViewGroup parent);

	/**
	 * Returns status of this item.
	 * @return <em>true</em> if this item is enabled, <em>false</em> otherwise.
	 */
	public abstract boolean isEnabled();

	public void setDrawable(final Drawable drawable) {
		_drawable = drawable;
	}
}
