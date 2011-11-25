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

package com.scoreloop.client.android.ui.component.game;

import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.UsersController;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.CaptionListItem;
import com.scoreloop.client.android.ui.component.base.ComponentListActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.EmptyListItem;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class GameDetailListActivity extends ComponentListActivity<BaseListItem> implements RequestControllerObserver {

	class GameDetailListAdapter extends BaseListAdapter<BaseListItem> {

		public GameDetailListAdapter(final Context context) {
			super(context);
			// game detail screen contains static main list items
			add(new CaptionListItem(context, null, context.getString(R.string.sl_details)));
			add(new GameDetailListItem(context, null, getGame()));
		}
	}

	protected UsersController	_usersController;

	protected void addUsers(final BaseListAdapter<BaseListItem> adapter, final List<User> users) {
		if (users.size() == 0) {
			adapter.add(new EmptyListItem(this, getString(R.string.sl_no_friends_playing)));
		} else {
			for (final User user : users) {
				adapter.add(new GameDetailUserListItem(this, user));
			}
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new GameDetailListAdapter(this));
		_usersController = new UsersController(this);
		_usersController.loadBuddies(getUser(), getGame());
	}

	@Override
	public void onRefresh(int flags) {
		super.onRefresh(flags);
		getBaseListAdapter().notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		if (item.getType() == Constant.LIST_ITEM_TYPE_GAME_DETAIL_USER) {
			display(getFactory().createUserDetailScreenDescription(((GameDetailUserListItem) item).getTarget(), null));
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController controller) {
		if (controller == _usersController) {
			final List<User> users = _usersController.getUsers();
			final BaseListAdapter<BaseListItem> adapter = getBaseListAdapter();
			adapter.add(new CaptionListItem(this, null, String.format(getString(R.string.sl_format_friends_playing), getGame().getName())));
			addUsers(adapter, users);
		}
	}
}
