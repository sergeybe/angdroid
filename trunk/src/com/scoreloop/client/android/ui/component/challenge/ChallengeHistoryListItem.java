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

import java.math.BigDecimal;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.Challenge;
import com.scoreloop.client.android.core.model.Money;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StringFormatter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class ChallengeHistoryListItem extends BaseListItem {

	static class ViewHolder {

		TextView		contenderName;
		TextView		contenderScore;
		TextView		contestantName;
		TextView		contestantScore;
		ImageView		icon;
		TextView		prize;
		LinearLayout	scores;
	}

	private final Challenge	_challenge;
	private boolean			_showPrize;

	public ChallengeHistoryListItem(final ComponentActivity componentActivity, final Challenge challenge, final boolean showPrize) {
		super(componentActivity, null, null);
		_challenge = challenge;
		_showPrize = showPrize;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_CHALLENGE_HISTORY;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		ViewHolder holder;

		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_challenge_history, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) view.findViewById(R.id.sl_icon);
			holder.contenderName = (TextView) view.findViewById(R.id.sl_contender_name);
			holder.contenderScore = (TextView) view.findViewById(R.id.sl_contender_score);
			holder.contestantName = (TextView) view.findViewById(R.id.sl_contestant_name);
			holder.contestantScore = (TextView) view.findViewById(R.id.sl_contestant_score);
			holder.scores = (LinearLayout) view.findViewById(R.id.sl_scores);
			holder.prize = (TextView) view.findViewById(R.id.sl_prize);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		prepareView(holder);

		return view;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	private void fillView(final ViewHolder holder, final Drawable drawable, final String contestantName, final String contestantScore,
			final String prize) {
		holder.icon.setImageDrawable(drawable != null ? drawable : getContext().getResources().getDrawable(R.drawable.sl_icon_challenges));
		holder.contenderName.setText(_challenge.getContender().getDisplayName());
		holder.contenderScore
				.setText(StringFormatter.formatChallengesScore(_challenge.getContenderScore(), getComponentActivity().getConfiguration()));
		holder.contestantName.setText(contestantName != null ? contestantName : _challenge.getContestant().getDisplayName());
		holder.contestantScore.setText(contestantScore != null ? contestantScore : getContext().getResources().getString(
				R.string.sl_pending));
		holder.prize.setText(prize != null ? prize : "-"
				+ StringFormatter.formatMoney(_challenge.getStake(), getComponentActivity().getConfiguration()));

		if (_showPrize) {
			holder.prize.setVisibility(View.VISIBLE);
			holder.scores.setVisibility(View.GONE);
		} else {
			holder.prize.setVisibility(View.GONE);
			holder.scores.setVisibility(View.VISIBLE);
		}
	}

	private ComponentActivity getComponentActivity() {
		return (ComponentActivity) getContext();
	}

	protected void prepareView(final ViewHolder holder) {
		if (_challenge.isComplete()) {
			Drawable drawable;
			BigDecimal prize = BigDecimal.ZERO;
			String sign = "";
			prize = prize.subtract(_challenge.getStake().getAmount());

			if (getComponentActivity().getSession().isOwnedByUser(_challenge.getWinner())) {
				drawable = getContext().getResources().getDrawable(R.drawable.sl_icon_challenge_won);
				prize = prize.add(_challenge.getPrize().getAmount());
				sign = "+";
			} else {
				drawable = getContext().getResources().getDrawable(R.drawable.sl_icon_challenge_lost);
			}

			fillView(holder, drawable, null,
					StringFormatter.formatChallengesScore(_challenge.getContestantScore(), getComponentActivity().getConfiguration()), sign
							+ StringFormatter.formatMoney(new Money(prize), getComponentActivity().getConfiguration()));

		} else if (_challenge.isOpen()) {
			fillView(holder, null, getContext().getResources().getString(R.string.sl_anyone),
					getContext().getResources().getString(R.string.sl_pending), null);

		} else if (_challenge.isAssigned()) {
			fillView(holder, null, null, getContext().getResources().getString(R.string.sl_pending), null);

		} else if (_challenge.isRejected()) {
			fillView(holder, null, null, getContext().getResources().getString(R.string.sl_rejected),
					StringFormatter.formatMoney(new Money(BigDecimal.ZERO), getComponentActivity().getConfiguration()));
		} else if (_challenge.isAccepted()) {
			fillView(holder, null, null, getContext().getResources().getString(R.string.sl_pending), null);
		}
	}

	void setShowPrize(final boolean showPrize) {
		_showPrize = showPrize;
	}
}
