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

package com.scoreloop.client.android.ui.framework;

import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.angdroid.nightly.R;

public class TabsActivity extends ActivityGroup implements TabsActivityProtocol, OnClickListener, OptionsMenuForActivityGroup {

	private ScreenDescription	_screenDescription;

	@Override
	public boolean onCreateOptionsMenuForActivityGroup(Menu menu) {
		final int index = _screenDescription.getSelectedBodyIndex();
		final Activity bodyActivity = getLocalActivityManager().getActivity(getTabActivityIdentifier(index));
		if ((bodyActivity != null) && (bodyActivity instanceof OptionsMenuForActivityGroup)) {
			return ((OptionsMenuForActivityGroup) bodyActivity).onCreateOptionsMenuForActivityGroup(menu);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenuForActivityGroup(Menu menu) {
		final int index = _screenDescription.getSelectedBodyIndex();
		final Activity bodyActivity = getLocalActivityManager().getActivity(getTabActivityIdentifier(index));
		if ((bodyActivity != null) && (bodyActivity instanceof OptionsMenuForActivityGroup)) {
			return ((OptionsMenuForActivityGroup) bodyActivity).onPrepareOptionsMenuForActivityGroup(menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelectedForActivityGroup(MenuItem item) {
		final int index = _screenDescription.getSelectedBodyIndex();
		final Activity bodyActivity = getLocalActivityManager().getActivity(getTabActivityIdentifier(index));
		if ((bodyActivity != null) && (bodyActivity instanceof OptionsMenuForActivityGroup)) {
			return ((OptionsMenuForActivityGroup) bodyActivity).onOptionsItemSelectedForActivityGroup(item);
		}
		return false;
	}

	public boolean isNavigationAllowed(NavigationIntent navigationIntent) {
		final Activity activity = getLocalActivityManager().getCurrentActivity();
		if (activity instanceof BaseActivity) {
			final BaseActivity baseActivity = (BaseActivity) activity;
			return baseActivity.isNavigationAllowed(navigationIntent);
		}
		return true;
	}

	public void onClick(final View view) {
		final TabView tabView = (TabView) view;
		final int selectedTab = tabView.getSelectedSegment();
		startTab(selectedTab);
		ScreenManagerSingleton.get().onShowedTab(_screenDescription);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_tabs);
		ScreenManagerSingleton.get().displayStoredDescriptionInTabs(this);
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		final TabView tabView = (TabView) findViewById(R.id.sl_tabs_segments);
		tabView.removeAllViews();
		ScreenManagerSingleton.get().displayStoredDescriptionInTabs(this);
	}

	public void startDescription(final ScreenDescription description) {
		_screenDescription = description;

		for (final ActivityDescription bodyDescription : description.getBodyDescriptions()) {
			bodyDescription.setEnabledWantsClearTop(true);
		}

		// configure segments
		final TabView tabView = (TabView) findViewById(R.id.sl_tabs_segments);
		tabView.setOnSegmentClickListener(this);

		final List<ActivityDescription> descriptions = description.getBodyDescriptions();
		for (int i = 0; i < descriptions.size(); i++) {
			final ActivityDescription bodyDescription = descriptions.get(i);
			final TextView tv = (TextView) getLayoutInflater().inflate(R.layout.sl_tab_caption, null);
			final int height = (int) getResources().getDimension(R.dimen.sl_clickable_height);
			final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1);
			tv.setLayoutParams(lp);
			tv.setText(bodyDescription.getTabId());
			tabView.addView(tv);
		}
		tabView.prepareUsage();

		// start selected body
		startTab(description.getSelectedBodyIndex());
	}

	private void startTab(final int index) {
		final TabView tabView = (TabView) findViewById(R.id.sl_tabs_segments);
		tabView.switchToSegment(index);

		// remember selected tab for when we navigate to another model and come back later on
		_screenDescription.setSelectedBodyIndex(index);

		final ActivityDescription bodyDescription = _screenDescription.getBodyDescriptions().get(index);
		ActivityHelper.startLocalActivity(this, bodyDescription.getIntent(), getTabActivityIdentifier(index), R.id.sl_tabs_body,
				ActivityHelper.ANIM_NONE);
		bodyDescription.setEnabledWantsClearTop(false);
	}

	private String getTabActivityIdentifier(int index) {
		return "tab-" + index;
	}
}
