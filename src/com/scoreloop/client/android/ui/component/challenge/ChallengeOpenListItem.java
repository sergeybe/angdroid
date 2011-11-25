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

import com.scoreloop.client.android.core.model.Challenge;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.component.base.StringFormatter;

public class ChallengeOpenListItem extends StandardListItem<Challenge> {

	public ChallengeOpenListItem(final ComponentActivity context, final Challenge challenge) {
		super(context, challenge);
		setDrawable(context.getResources().getDrawable(R.drawable.sl_icon_user));
		setTitle(challenge.getContender().getDisplayName());
		setSubTitle(context.getModeString(challenge.getMode()));
		setSubTitle2(StringFormatter.formatMoney(challenge.getStake(), getComponentActivity().getConfiguration()));
	}

    @Override
	protected String getImageUrl() {
		return getTarget().getContender().getImageUrl();
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_OPEN;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.sl_list_item_challenge_open;
	}

	@Override
	protected int getSubTitle2Id() {
		return R.id.sl_subtitle2;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
