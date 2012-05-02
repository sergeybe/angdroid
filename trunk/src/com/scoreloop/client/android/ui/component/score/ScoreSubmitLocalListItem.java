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

package com.scoreloop.client.android.ui.component.score;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class ScoreSubmitLocalListItem extends BaseListItem {

	public ScoreSubmitLocalListItem(final Context context) {
		super(context, null, null);
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_SCORE_SUBMIT_LOCAL;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_score_submit_local, null);
		}
		prepareView(view);
		return view;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	private void prepareView(final View view) {
		view.setEnabled(false);
		final Button button = (Button) view.findViewById(R.id.sl_submit_local_score_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				view.performClick();
			}
		});
	}

}
