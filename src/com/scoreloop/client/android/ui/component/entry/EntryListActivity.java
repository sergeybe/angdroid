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

package com.scoreloop.client.android.ui.component.entry;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.*;
import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class EntryListActivity extends ComponentListActivity<BaseListItem> {

	class EntryListAdapter extends BaseListAdapter<BaseListItem> {

		public EntryListAdapter(final Context context) {
			super(context);
			final Resources res = context.getResources();
			final Configuration configuration = getConfiguration();

			add(new CaptionListItem(context, null, getGame().getName()));
			leaderboardsItem = new EntryListItem(EntryListActivity.this, res.getDrawable(R.drawable.sl_icon_leaderboards), context
					.getString(R.string.sl_leaderboards), null);
			add(leaderboardsItem);
			if (configuration.isFeatureEnabled(Configuration.Feature.ACHIEVEMENT)) {
				achievementsItem = new EntryListItem(EntryListActivity.this, res.getDrawable(R.drawable.sl_icon_achievements), context
						.getString(R.string.sl_achievements), null);
				add(achievementsItem);
			}
			if (configuration.isFeatureEnabled(Configuration.Feature.CHALLENGE)) {
				challengesItem = new EntryListItem(EntryListActivity.this, res.getDrawable(R.drawable.sl_icon_challenges), context
						.getString(R.string.sl_challenges), null);
				add(challengesItem);
			}
			if (configuration.isFeatureEnabled(Configuration.Feature.NEWS)) {
				newsItem = new EntryListItem(EntryListActivity.this, res.getDrawable(R.drawable.sl_icon_news_closed), context
						.getString(R.string.sl_news), null);
				add(newsItem);
			}
		}
	}

	private EntryListItem	achievementsItem;
	private EntryListItem	challengesItem;
	private EntryListItem	leaderboardsItem;
	private EntryListItem	newsItem;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new EntryListAdapter(this));

		addObservedKeys(
				ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_ACHIEVEMENTS), 
				ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NUMBER_CHALLENGES_WON), 
				ValueStore.concatenateKeys(Constant.USER_VALUES, Constant.NEWS_NUMBER_UNREAD_ITEMS));
	}

	@Override
	public void onListItemClick(final BaseListItem item) {
		final Factory factory = getFactory();
		if (item == leaderboardsItem) {
			display(factory.createScoreScreenDescription(getGame(), null, null));
		} else if (item == challengesItem) {
			display(factory.createChallengeScreenDescription(getUser()));
		} else if (item == achievementsItem) {
			display(factory.createAchievementScreenDescription(getUser()));
		} else if (item == newsItem) {
			display(factory.createNewsScreenDescription());
		}
	}

	@Override
	public void onValueChanged(final ValueStore valueStore, final String key, final Object oldValue, final Object newValue) {
		if (isValueChangedFor(key, Constant.NUMBER_ACHIEVEMENTS, oldValue, newValue)) {
			achievementsItem.setSubTitle(StringFormatter.getAchievementsSubTitle(this, getUserValues(), false));
			getBaseListAdapter().notifyDataSetChanged();
		} else if (isValueChangedFor(key, Constant.NUMBER_CHALLENGES_WON, oldValue, newValue)) {
			challengesItem.setSubTitle(StringFormatter.getChallengesSubTitle(this, getUserValues()));
			getBaseListAdapter().notifyDataSetChanged();
		} else if (isValueChangedFor(key, Constant.NEWS_NUMBER_UNREAD_ITEMS, oldValue, newValue)) {
			newsItem.setSubTitle(StringFormatter.getNewsSubTitle(this, getUserValues()));
			newsItem.setDrawable(StringFormatter.getNewsDrawable(this, getUserValues(), false));
			getBaseListAdapter().notifyDataSetChanged();
		}
	}

	@Override
	public void onValueSetDirty(final ValueStore valueStore, final String key) {
		final Configuration configuration = getConfiguration();

		if (configuration.isFeatureEnabled(Configuration.Feature.ACHIEVEMENT) && key.equals(Constant.NUMBER_ACHIEVEMENTS)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
		if (configuration.isFeatureEnabled(Configuration.Feature.CHALLENGE) && key.equals(Constant.NUMBER_CHALLENGES_WON)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_DIRTY, null);
		}
		if (configuration.isFeatureEnabled(Configuration.Feature.NEWS) && key.equals(Constant.NEWS_NUMBER_UNREAD_ITEMS)) {
			getUserValues().retrieveValue(key, ValueStore.RetrievalMode.NOT_OLDER_THAN, Constant.NEWS_FEED_REFRESH_TIME);
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        hideFooter();
        if (!PackageManager.isScoreloopAppInstalled(this)) {
            showFooter(new StandardListItem<Void>(this, getResources().getDrawable(R.drawable.sl_icon_scoreloop),
                    getString(R.string.sl_slapp_title), getString(R.string.sl_slapp_subtitle), null));
        }
    }

    @Override
    protected void onFooterItemClick(final BaseListItem footerItem) {
        hideFooter();
        PackageManager.installScoreloopApp(this);
    }


}
