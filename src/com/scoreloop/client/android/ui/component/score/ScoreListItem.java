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

import android.graphics.drawable.Drawable;

import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.component.base.StringFormatter;

public class ScoreListItem extends StandardListItem<Score> {
	
	private boolean _isEnabled;

	public ScoreListItem(final ComponentActivity activity, final Score score, final boolean isEnabled) {
		super(activity, null, StringFormatter.getScoreTitle(activity, score), StringFormatter.formatLeaderboardsScore(score, activity
				.getConfiguration()), score);
		_isEnabled = isEnabled;
	}

	@Override
	protected String getImageUrl() {
		User user = getTarget().getUser();
		if (user == null) {
			// if the score comes from the rank, it will not have a user object, so use the session user
			user = Session.getCurrentSession().getUser();
		}
		return user.getImageUrl();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.sl_list_item_score;
	}

	@Override
	protected int getSubTitleId() {
		return R.id.sl_list_item_score_result;
	}

	@Override
	protected int getTitleId() {
		return R.id.sl_list_item_score_title;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_SCORE;
	}

	@Override
	public Drawable getDrawable() {
		return getContext().getResources().getDrawable(R.drawable.sl_icon_user);
	}
	
	@Override
	public boolean isEnabled() {
		return _isEnabled;
	}
}
