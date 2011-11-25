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

package com.scoreloop.client.android.ui.component.achievement;

import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.core.model.Range;
import org.angdroid.nightly.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StandardListItem;
import com.scoreloop.client.android.ui.component.base.StringFormatter;

public class AchievementListItem extends StandardListItem<Achievement> {

	public static class AchievementViewHolder extends StandardViewHolder {
		View		accessory;
		TextView	increment;
		ProgressBar	progress;
	}

	private final boolean	_belongsToSessionUser;

	public AchievementListItem(final ComponentActivity activity, final Achievement achievement, final boolean belongsToSessionUser) {
		super(activity, achievement);
		_belongsToSessionUser = belongsToSessionUser;

		setDrawable(new BitmapDrawable(achievement.getImage()));

		setTitle(achievement.getAward().getLocalizedTitle());

		setSubTitle(achievement.getAward().getLocalizedDescription());

		setSubTitle2(StringFormatter.getAchievementRewardTitle(activity, achievement, activity.getConfiguration()));
	}

	@Override
	protected StandardViewHolder createViewHolder() {
		return new AchievementViewHolder();
	}

	@Override
	protected void fillViewHolder(final View view, final StandardViewHolder holder) {
		super.fillViewHolder(view, holder);

		final AchievementViewHolder achievementViewHolder = (AchievementViewHolder) holder;
		achievementViewHolder.accessory = view.findViewById(R.id.sl_list_item_achievement_accessory);
		achievementViewHolder.progress = (ProgressBar) view.findViewById(R.id.sl_list_item_achievement_progress);
		achievementViewHolder.increment = (TextView) view.findViewById(R.id.sl_list_item_achievement_percent);
	}

	protected int getIconId() {
		return R.id.sl_list_item_achievement_icon;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.sl_list_item_achievement;
	}

	protected int getSubTitle2Id() {
		return R.id.sl_list_item_achievement_reward;
	}

	protected int getSubTitleId() {
		return R.id.sl_list_item_achievement_description;
	}

	@Override
	protected int getTitleId() {
		return R.id.sl_list_item_achievement_title;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_ACHIEVEMENT;
	}

	@Override
	public boolean isEnabled() {
		return _belongsToSessionUser && getTarget().isAchieved() && (getTarget().getIdentifier() != null);
	}

	@Override
	protected void updateViews(final StandardViewHolder holder) {
		super.updateViews(holder);

		final AchievementViewHolder achievementViewHolder = (AchievementViewHolder) holder;

		if (isEnabled()) {
			achievementViewHolder.accessory.setVisibility(View.VISIBLE);
			achievementViewHolder.subTitle2.setVisibility(View.GONE);
		}
		else {
			achievementViewHolder.accessory.setVisibility(View.INVISIBLE);
			achievementViewHolder.subTitle2.setVisibility(View.VISIBLE);			
		}

		final Range range = getTarget().getAward().getCounterRange();
		if (!getTarget().isAchieved() &&  range.getLength() > 1) {
			achievementViewHolder.progress.setVisibility(View.VISIBLE);
			achievementViewHolder.progress.setMax(range.getLength());
			achievementViewHolder.progress.setProgress(getTarget().getValue() - range.getLocation());

			achievementViewHolder.increment.setVisibility(View.VISIBLE);
			achievementViewHolder.increment.setText(StringFormatter.getAchievementIncrementTitle(getContext(), getTarget(),
					getComponentActivity().getConfiguration()));
		} else {
			achievementViewHolder.progress.setVisibility(View.GONE);
			achievementViewHolder.increment.setVisibility(View.GONE);
		}
	}
}
