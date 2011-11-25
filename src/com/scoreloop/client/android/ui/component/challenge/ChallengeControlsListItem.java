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
import android.view.View.OnClickListener;
import android.widget.Button;

import com.scoreloop.client.android.core.model.Challenge;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListItem;

class ChallengeControlsListItem extends BaseListItem {
	
	interface OnControlObserver {
		
		void onControl1();

		void onControl2();
	}

	private final Challenge	_challenge;
	private boolean			_controlsEnabled;
	private OnControlObserver _onControlObserver;

	public ChallengeControlsListItem(final ComponentActivity context, final Challenge challenge, OnControlObserver observer) {
		super(context, null, null);
		_challenge = challenge; // challenge == null ? createChallenge : acceptChallenge
		_controlsEnabled = true;
		_onControlObserver = observer;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_CONTROLS;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_challenge_controls, null);
		}
		prepareView(view);
		return view;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	protected void prepareView(final View view) {
		final Button control1 = (Button) view.findViewById(R.id.control1);
		control1.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				if (_controlsEnabled) {
					_controlsEnabled = false;
					_onControlObserver.onControl1();
				}
			}
		});

		final Button control2 = (Button) view.findViewById(R.id.control2);
		control2.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				if (_controlsEnabled) {
					_controlsEnabled = false;
					_onControlObserver.onControl2();
				}
			}
		});

		if (_challenge == null) {
			control1.setText(getContext().getResources().getString(R.string.sl_create_challenge));
			control2.setVisibility(View.GONE);
		} else {
			control1.setText(getContext().getResources().getString(R.string.sl_accept_start_challenge));
			if (_challenge.isAssigned()) {
				control2.setText(getContext().getResources().getString(R.string.sl_reject_challenge));
				control2.setVisibility(View.VISIBLE);
			} else {
				control2.setVisibility(View.GONE);
			}
		}
	}
}
