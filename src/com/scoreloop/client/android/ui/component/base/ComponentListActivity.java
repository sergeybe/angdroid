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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListAdapter.OnListItemClickListener;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.ScreenDescription;

public abstract class ComponentListActivity<T extends BaseListItem> extends ComponentActivity implements OnClickListener,
		OnItemClickListener, OnListItemClickListener<T> {

	private BaseListItem	_footerItem;
	protected boolean		_isVisibleOptionsMenuAccountSettings	= true;

	public void setVisibleOptionsMenuAccountSettings(boolean isVisibleOptionsMenuProfileSettings) {
		this._isVisibleOptionsMenuAccountSettings = isVisibleOptionsMenuProfileSettings;
	}

	@SuppressWarnings("unchecked")
	public BaseListAdapter<T> getBaseListAdapter() {
		return (BaseListAdapter<T>) getListAdapter();
	}

	public ListAdapter getListAdapter() {
		return getListView().getAdapter();
	}

	public ListView getListView() {
		return (ListView) findViewById(R.id.sl_list);
	}

	public void hideFooter() {
		final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.sl_footer);
		if (viewGroup != null) {
			_footerItem = null;
			viewGroup.removeAllViews();
		}
	}

	public void onClick(final View view) {
		if (_footerItem != null) {
			onFooterItemClick(_footerItem);
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_list_view, true);

		getListView().setFocusable(true);
		getListView().setOnItemClickListener(this);
	}

	protected void onFooterItemClick(final BaseListItem footerItem) {
		// intentionally empty - should be overridden in subclass
	}

	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final BaseListAdapter<T> baseListAdapter = getBaseListAdapter();
        final T item = baseListAdapter.getItem(position);
        if (item.isEnabled()) {
            baseListAdapter.onItemClick(parent, view, position, id);
        }
	}

	public void onListItemClick(final T item) {
		// intentionally empty - should be overridden in subclass
	}

	public void setListAdapter(final ListAdapter adapter) {
		getListView().setAdapter(adapter);
		getBaseListAdapter().setOnListItemClickListener(this);
	}

	public void showFooter(final BaseListItem footerItem) {
		final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.sl_footer);
		if (viewGroup != null) {
			final View footerView = footerItem.getView(null, null);
			if (footerView != null) {
				_footerItem = footerItem;
				viewGroup.addView(footerView);
				if (footerItem.isEnabled()) {
					footerView.setOnClickListener(this);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenuForActivityGroup(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sl_options_menu, menu);
		return super.onCreateOptionsMenuForActivityGroup(menu);
	}

	@Override
	public boolean onPrepareOptionsMenuForActivityGroup(Menu menu) {
		final MenuItem item = menu.findItem(R.id.sl_item_account_settings);
		if (item != null) {
			item.setVisible(_isVisibleOptionsMenuAccountSettings);
		}
		return super.onPrepareOptionsMenuForActivityGroup(menu);
	}

	@Override
	public boolean onOptionsItemSelectedForActivityGroup(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sl_item_account_settings) {
            getTracker().trackEvent(TrackerEvents.CAT_NAVI, TrackerEvents.NAVI_OM_ACCOUNT_SETTINGS, null, 0);
            final ScreenDescription profileSettingsScreenDescription = getFactory()
                    .createProfileSettingsScreenDescription(getSessionUser());
            display(profileSettingsScreenDescription);
            return true;
        }
		return super.onOptionsItemSelectedForActivityGroup(item);
	}

}
