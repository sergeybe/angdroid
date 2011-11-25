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

package com.scoreloop.client.android.ui.component.challenge;

import com.scoreloop.client.android.core.model.User;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StandardListItem;

public class ChallengeCreateListItem extends StandardListItem<User> {

	public ChallengeCreateListItem(final ComponentActivity activity, final User user) {
		super(activity, null, null, null, user);
		if (user == null) {
			setDrawable(activity.getResources().getDrawable(R.drawable.sl_icon_challenge_anyone));
			setTitle(activity.getResources().getString(R.string.sl_against_anyone));
		} else {
			setDrawable(activity.getResources().getDrawable(R.drawable.sl_icon_user));
			setTitle(user.getDisplayName());
		}
	}

	/**
	 * no subtitle
	 */
	@Override
	protected int getSubTitleId() {
		return 0;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_NEW;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.sl_list_item_user;
	}

	@Override
	protected String getImageUrl() {
		if (getTarget() != null) {
			return getTarget().getImageUrl();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
