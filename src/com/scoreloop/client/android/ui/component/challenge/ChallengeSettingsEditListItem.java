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

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.Money;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StringFormatter;

class ChallengeSettingsEditListItem extends ChallengeSettingsListItem {

	private class ViewHolder {
		TextView	stakeText;
	}

	private final List<Money>	stakes	= ScoreloopManagerSingleton.get().getSession().getChallengeStakes();

	private int					_modePosition;
	private int					_stakePosition;

	public ChallengeSettingsEditListItem(final ComponentActivity context) {
		super(context, null);
	}

	Integer getMode() {
		if (getComponentActivity().getGame().hasModes()) {
			return getComponentActivity().getModeForPosition(_modePosition);
		} else {
			return null;
		}
	}

	Money getStake() {
		final Money balance = getComponentActivity().getUserValues().getValue(Constant.USER_BALANCE);
		if (balance == null) {
			return null;
		}

		final Money stake = stakes.get(_stakePosition);
		final boolean enoughBalance = stake.compareTo(balance) <= 0;
		return enoughBalance ? stake : null;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_STAKE_AND_MODE;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_challenge_settings_edit, null);
		}
		prepareView(view);
		return view;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	private void prepareModeSelector(final View view) {
		final Spinner modeSelector = (Spinner) view.findViewById(R.id.mode_selector);
		if (getComponentActivity().getGame().hasModes()) {
			modeSelector.setVisibility(View.VISIBLE);
			final ArrayAdapter<?> adapter = new ArrayAdapter<String>(getContext(), R.layout.sl_spinner_item, getComponentActivity()
					.getConfiguration().getModesNames());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			modeSelector.setAdapter(adapter);
			modeSelector.setSelection(_modePosition);
			modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
					_modePosition = position;
				}

				@Override
				public void onNothingSelected(final AdapterView<?> arg0) {
				}
			});
		} else {
			modeSelector.setVisibility(View.GONE);
		}
	}

	private void prepareStakeSelector(final View view) {
		final SeekBar stakeSelector = (SeekBar) view.findViewById(R.id.stake_selector);
		stakeSelector.setMax(stakes.size() - 1);
		stakeSelector.setProgress(_stakePosition);
		stakeSelector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				_stakePosition = progress;
				final ViewHolder holder = (ViewHolder) seekBar.getTag();
				updateStakeText(holder.stakeText);
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
			}
		});
		final ViewHolder holder = new ViewHolder();
		holder.stakeText = (TextView) view.findViewById(R.id.stake_text);
		stakeSelector.setTag(holder);
	}

	private void prepareStakeText(final View view) {
		final TextView stakeText = (TextView) view.findViewById(R.id.stake_text);
		updateStakeText(stakeText);
	}

	@Override
	protected void prepareView(final View view) {
		prepareStakeText(view);
		prepareModeSelector(view);
		prepareStakeSelector(view);
	}

	private void updateStakeText(final TextView stakeText) {
		final Money stake = getStake();
		stakeText.setText(stake != null ? StringFormatter.formatMoney(stake, getComponentActivity().getConfiguration()) : getContext()
				.getResources().getString(R.string.sl_balance_too_low));
	}
}
