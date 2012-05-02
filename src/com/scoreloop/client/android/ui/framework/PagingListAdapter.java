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
import android.widget.AdapterView;

public class PagingListAdapter<T extends BaseListItem> extends BaseListAdapter<BaseListItem> {

	public static interface OnListItemClickListener<T extends BaseListItem> extends BaseListAdapter.OnListItemClickListener<T> {
		void onPagingListItemClick(PagingDirection pagingDirection);
	}

	private final int		_listOffset;
	private PagingListItem	_nextPagingItem;
	private int				_pagingFlags	= 0;
	private PagingListItem	_prevPagingItem;

	private PagingListItem	_topPagingItem;

	public PagingListAdapter(final Context context) {
		super(context);
		_listOffset = 0;
	}

	public PagingListAdapter(final Context context, final int listOffset) {
		super(context);
		_listOffset = listOffset;
	}

	public void addPagingItems(final boolean showTop, final boolean showPrev, final boolean showNext) {
		_pagingFlags = 0;
		if (showTop) {
			insert(getTopPagingItem(), _listOffset);
			_pagingFlags = PagingDirection.PAGE_TO_TOP.combine(_pagingFlags);
		}
		if (showPrev) {
			insert(getPrevPagingItem(), showTop ? _listOffset + 1 : _listOffset);
			_pagingFlags = PagingDirection.PAGE_TO_PREV.combine(_pagingFlags);
		}
		if (showNext) {
			add(getNextPagingItem());
			_pagingFlags = PagingDirection.PAGE_TO_NEXT.combine(_pagingFlags);
		}
	}

	@SuppressWarnings("unchecked")
	public T getContentItem(final int position) {
		return (T) getItem(position + getFirstContentPosition());
	}

	public int getFirstContentPosition() {
		int offset = _listOffset;
		if (PagingDirection.PAGE_TO_TOP.isPresentIn(_pagingFlags)) {
			++offset;
		}
		if (PagingDirection.PAGE_TO_PREV.isPresentIn(_pagingFlags)) {
			++offset;
		}
		return offset;
	}

	public int getLastContentPosition() {
		int offset = getCount() - 1;
		if (PagingDirection.PAGE_TO_NEXT.isPresentIn(_pagingFlags)) {
			--offset;
		}
		return Math.max(0, offset);
	}

	private BaseListItem getNextPagingItem() {
		if (_nextPagingItem == null) {
			_nextPagingItem = new PagingListItem(getContext(), PagingDirection.PAGE_TO_NEXT);
		}
		return _nextPagingItem;
	}

	private BaseListItem getPrevPagingItem() {
		if (_prevPagingItem == null) {
			_prevPagingItem = new PagingListItem(getContext(), PagingDirection.PAGE_TO_PREV);
		}
		return _prevPagingItem;
	}

	private BaseListItem getTopPagingItem() {
		if (_topPagingItem == null) {
			_topPagingItem = new PagingListItem(getContext(), PagingDirection.PAGE_TO_TOP);
		}
		return _topPagingItem;
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		if (_listItemClickListener != null) {
			final BaseListItem item = getItem(position);

			if (item.getType() == 0) {
				final OnListItemClickListener<?> pagingListener = (OnListItemClickListener<?>) _listItemClickListener;
				final PagingListItem pagingItem = (PagingListItem) item;

				pagingListener.onPagingListItemClick(pagingItem.getPagingDirection());
			} else {
				_listItemClickListener.onListItemClick(item);
			}
		}
	}
}
