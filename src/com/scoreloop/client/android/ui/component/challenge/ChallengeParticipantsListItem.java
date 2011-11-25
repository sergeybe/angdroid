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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.util.ImageDownloader;

class ChallengeParticipantsListItem extends BaseListItem {

	private final User	_contender;
	private String		_contenderStats;
	private final User	_contestant;
	private String		_contestantStats;

	public ChallengeParticipantsListItem(final Context context, final User contender, final User contestant) {
		super(context, null, null);
		_contender = contender;
		_contestant = contestant;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_PARTICIPANTS;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_challenge_participants, null);
		}
		prepareView(view);
		return view;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	protected void prepareView(final View view) {
		final ImageView contenderIconView = (ImageView) view.findViewById(R.id.contender_icon);
		final String contenderImageUrl = _contender.getImageUrl();
		if (contenderImageUrl != null) {
			Drawable drawable = getDrawableLoading();
			ImageDownloader.downloadImage(contenderImageUrl, drawable, contenderIconView, null);
		}

		final TextView contenderNameView = (TextView) view.findViewById(R.id.contender_name);
		contenderNameView.setText(_contender.getDisplayName());

		final TextView contenderStatsView = (TextView) view.findViewById(R.id.contender_stats);
		contenderStatsView.setText(_contenderStats);

		final ImageView contestantIconView = (ImageView) view.findViewById(R.id.contestant_icon);
		if (_contestant != null) {
			final String contestantImageUrl = _contestant.getImageUrl();
			if (contestantImageUrl != null) {
				Drawable drawable = getDrawableLoading();
				ImageDownloader.downloadImage(contestantImageUrl, drawable, contestantIconView, null);
			}
		} else {
			contestantIconView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.sl_icon_challenge_anyone));
		}

		final TextView contestantNameView = (TextView) view.findViewById(R.id.contestant_name);
		final String contestantName = _contestant != null ? _contestant.getDisplayName() : getContext().getResources().getString(
				R.string.sl_anyone);
		contestantNameView.setText(contestantName);

		final TextView contestantStatsView = (TextView) view.findViewById(R.id.contestant_stats);
		contestantStatsView.setText(_contestantStats);
	}

	private Drawable getDrawableLoading() {
		return getContext().getResources().getDrawable(R.drawable.sl_icon_games_loading);
	}

	public void setContenderStats(final String contenderStats) {
		_contenderStats = contenderStats;
	}

	public void setContestantStats(final String contestantStats) {
		_contestantStats = contestantStats;
	}
}
