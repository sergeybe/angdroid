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

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.Challenge;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

class ChallengeSettingsListItem extends BaseListItem {

	private final Challenge	_challenge;

	public ChallengeSettingsListItem(final ComponentActivity context, final Challenge challenge) {
		super(context, null, null);
		_challenge = challenge;
	}

	protected ComponentActivity getComponentActivity() {
		return (ComponentActivity) getContext();
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_STAKE_AND_MODE;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_challenge_settings, null);
		}
		prepareView(view);
		return view;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	protected void prepareView(final View view) {
		final TextView stake = (TextView) view.findViewById(R.id.stake);
		stake.setText(String.format(getContext().getResources().getString(R.string.sl_format_stake), StringFormatter.formatMoney(_challenge
				.getStake(), getComponentActivity().getConfiguration())));

		final TextView mode = (TextView) view.findViewById(R.id.mode);
		if (getComponentActivity().getGame().hasModes()) {
			mode.setVisibility(View.VISIBLE);
			mode.setText(getComponentActivity().getModeString(_challenge.getMode()));
		} else {
			mode.setVisibility(View.GONE);
		}
	}
}
