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

package com.scoreloop.client.android.ui.component.user;

import android.graphics.drawable.Drawable;

import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StandardListItem;

public class UserAddBuddyListItem extends StandardListItem<Object> {

	public UserAddBuddyListItem(final ComponentActivity context, final Drawable drawable, final String title, final Object target) {
		super(context, drawable, title, null, target);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.sl_list_item_user;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_USER_ADD_BUDDY;
	}
}
