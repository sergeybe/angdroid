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

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import org.angdroid.angband.R;

public class ScreenActivity extends ActivityGroup implements ScreenActivityProtocol, OnClickListener {

	private static final String	REGION_BODY					= "body";
	private static final String	REGION_HEADER				= "header";
	private static final String	STACK_ENTRY_REFERENCE_KEY	= "stackEntryReference";

	private BaseActivity		_bodyActivity;

	public ScreenActivity() {
		super(false); // passing false here indicates, that we want to have more than one resumed activity per activity-group
	}

	@Override
	public void cleanOutSubactivities() {
		getLocalActivityManager().removeAllActivities();
	}

	protected void display(final ScreenDescription description, final Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return; // just return as handling of this case is already done in ScreenActivity.onCreate()
		}
		ScreenManagerSingleton.get().displayInScreen(description, this, true);
	}

	protected void displayPreviousDescription() {
		ScreenManagerSingleton.get().displayPreviousDescription(false);
	}

	protected void finishDisplay() {
		ScreenManagerSingleton.get().finishDisplay();
	}

	@Override
	public void finishFromChild(final Activity child) {
		final Intent childIntent = child.getIntent();
		if (childIntent.hasExtra(BaseActivity.CHILD_RESULT_CODE)) {
			setResult(childIntent.getIntExtra(BaseActivity.CHILD_RESULT_CODE, 0), childIntent);
			finishDisplay();
		} else {
			super.finishFromChild(child);
		}
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	private Activity getBodyActivity() {
		if (_bodyActivity != null) {
			return _bodyActivity;
		}
		return getLocalActivityManager().getActivity(REGION_BODY);
	}

	@Override
	public Activity getCurrentActivity() {
		if (_bodyActivity != null) {
			return _bodyActivity;
		}
		return super.getCurrentActivity();
	}

	@Override
	public boolean isNavigationAllowed(final NavigationIntent navigationIntent) {

		// NOTE: here and for onCreate/PrepareOptionsMenu: implement a visitor pattern instead to go over all activities.
		boolean navigationAllowed = true;
		Activity activity = getLocalActivityManager().getActivity(REGION_HEADER);
		if ((activity != null) && (activity instanceof BaseActivity)) {
			final BaseActivity headerActivity = (BaseActivity) activity;
			navigationAllowed &= headerActivity.isNavigationAllowed(navigationIntent);
		}
		activity = getBodyActivity();
		if (activity instanceof BaseActivity) {
			final BaseActivity bodyActivity = (BaseActivity) activity;
			navigationAllowed &= bodyActivity.isNavigationAllowed(navigationIntent);
		} else if (activity instanceof TabsActivity) {
			final TabsActivity tabActivity = (TabsActivity) activity;
			navigationAllowed &= tabActivity.isNavigationAllowed(navigationIntent);
		}
		return navigationAllowed;
	}

	@Override
	public void onClick(final View view) {
		if (view == findViewById(R.id.sl_status_close_button)) {
			onStatusCloseClick(view);
		} else if (view == findViewById(R.id.sl_shortcuts)) {
			onShortcutClick((ShortcutView) view);
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sl_screen);

		final ImageView closeButton = (ImageView) findViewById(R.id.sl_status_close_button);
		if (closeButton != null) {
			closeButton.setEnabled(true);
			closeButton.setOnClickListener(this);
		}

		final ShortcutView shortcutView = (ShortcutView) findViewById(R.id.sl_shortcuts);
		shortcutView.setOnSegmentClickListener(this);

		if (savedInstanceState == null) {
			ScreenManagerSingleton.get().displayStoredDescriptionInScreen(this);
		} else {
			final int stackEntryReference = savedInstanceState.getInt(STACK_ENTRY_REFERENCE_KEY);
			final int currentStackEntryReference = ScreenManagerSingleton.get().getCurrentStackEntryReference();
			if (stackEntryReference != currentStackEntryReference) {
				// this sould never happen
				Log.w("ScoreloopUI.Framework", String.format(
						"onCreate with savedInstanceState: contains wrong stackEntryReference %s and current stack depth is %s",
						stackEntryReference, currentStackEntryReference));
				ScreenManagerSingleton.get().finishDisplay();
				finish();
			} else {
				ScreenManagerSingleton.get().displayReferencedStackEntryInScreen(stackEntryReference, this);

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// this is called from body activity
		boolean result = super.onCreateOptionsMenu(menu);
		final Activity bodyActivity = getLocalActivityManager().getActivity(REGION_BODY);
		if ((bodyActivity != null) && (bodyActivity instanceof OptionsMenuForActivityGroup)) {
			result |= ((OptionsMenuForActivityGroup) bodyActivity).onCreateOptionsMenuForActivityGroup(menu);
		}
		final Activity headerActivity = getLocalActivityManager().getActivity(REGION_HEADER);
		if ((headerActivity != null) && (headerActivity instanceof OptionsMenuForActivityGroup)) {
			result |= ((OptionsMenuForActivityGroup) headerActivity).onCreateOptionsMenuForActivityGroup(menu);
		}
		return result;
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
			displayPreviousDescription();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// this is called from body activity
		boolean consumed = super.onOptionsItemSelected(item);
		if (!consumed) {
			final Activity activity = getLocalActivityManager().getActivity(REGION_HEADER);
			if ((activity != null) && (activity instanceof OptionsMenuForActivityGroup)) {
				consumed = ((OptionsMenuForActivityGroup) activity).onOptionsItemSelectedForActivityGroup(item);
			}
		}
		if (!consumed) {
			final Activity activity = getLocalActivityManager().getActivity(REGION_BODY);
			if ((activity != null) && (activity instanceof OptionsMenuForActivityGroup)) {
				consumed = ((OptionsMenuForActivityGroup) activity).onOptionsItemSelectedForActivityGroup(item);
			}
		}
		return consumed;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		// this is called from body activity
		boolean result = super.onPrepareOptionsMenu(menu);
		final Activity headerActivity = getLocalActivityManager().getActivity(REGION_HEADER);
		if ((headerActivity != null) && (headerActivity instanceof OptionsMenuForActivityGroup)) {
			result |= ((OptionsMenuForActivityGroup) headerActivity).onPrepareOptionsMenuForActivityGroup(menu);
		}
		final Activity bodyActivity = getLocalActivityManager().getActivity(REGION_BODY);
		if ((bodyActivity != null) && (bodyActivity instanceof OptionsMenuForActivityGroup)) {
			result |= ((OptionsMenuForActivityGroup) bodyActivity).onPrepareOptionsMenuForActivityGroup(menu);
		}
		ScreenManagerSingleton.get().onWillShowOptionsMenu();
		return result;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (_bodyActivity != null) {
			_bodyActivity.onResume();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {

		// if body has configChanges set, then store it here and re-apply in startBody
		final Activity activity = getBodyActivity();
		if ((activity != null) && (activity.onRetainNonConfigurationInstance() != null)) {
			return activity;
		}
		return null;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		final int stackEntryReference = ScreenManagerSingleton.get().getCurrentStackEntryReference();
		outState.putInt(STACK_ENTRY_REFERENCE_KEY, stackEntryReference);
	}

	private void onShortcutClick(final ShortcutView shortcutView) {
		final int selection = shortcutView.getSelectedSegment();
		final NavigationIntent navigationIntent = new NavigationIntent(NavigationIntent.Type.SHORTCUT, new Runnable() {
			@Override
			public void run() {
				final ScreenDescription screenDescription = ScreenManagerSingleton.get().getCurrentDescription();
				final ScreenDescription.ShortcutObserver observer = screenDescription.getShortcutObserver();
				if (observer == null) {
					return;
				}
				if (selection != -1) {
					final ShortcutDescription shortcutDescription = screenDescription.getShortcutDescriptions().get(selection);
					observer.onShortcut(shortcutDescription.getTextId());
				}
			}
		});
		if (!isNavigationAllowed(navigationIntent)) {
			shortcutView.switchToSegment(shortcutView.oldSelectedSegment);
		} else {
			navigationIntent.execute();
		}
	}

	protected void onStatusCloseClick(final View view) {
		finishDisplay();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (_bodyActivity != null) {
			_bodyActivity.onStop();
		}
	}

	@Override
	public void setShortcuts(final ScreenDescription description) {
		final ShortcutView shortcutView = (ShortcutView) findViewById(R.id.sl_shortcuts);
		shortcutView.setDescriptions(this, description.getShortcutDescriptions());

		final int index = description.getShortcutSelectionIndex();
		shortcutView.switchToSegment(index);
	}

	@Override
	public void startBody(final ActivityDescription description, final int anim) {
		final Activity activity = (Activity) getLastNonConfigurationInstance();
		if ((activity != null) && (activity instanceof BaseActivity)) {
			final View paneView = activity.getWindow().getDecorView();
			final ViewParent parent = paneView.getParent();
			if ((parent != null) && (parent instanceof ViewGroup)) {
				final ViewGroup viewGroup = (ViewGroup) parent;
				viewGroup.removeView(paneView);
			}
			final ViewGroup region = (ViewGroup) findViewById(R.id.sl_body);
			region.addView(paneView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			_bodyActivity = (BaseActivity) activity;
			return;
		}
		ActivityHelper.startLocalActivity(this, description.getIntent(), REGION_BODY, R.id.sl_body, anim);
		_bodyActivity = null;
	}

	@Override
	public void startEmptyBody() {
		final ViewGroup region = (ViewGroup) findViewById(R.id.sl_body);
		region.removeAllViews();
	}

	@Override
	public void startEmptyHeader() {
		final ViewGroup region = (ViewGroup) findViewById(R.id.sl_header);
		region.removeAllViews();
	}

	@Override
	public void startHeader(final ActivityDescription description, final int anim) {
		ActivityHelper.startLocalActivity(this, description.getIntent(), REGION_HEADER, R.id.sl_header, anim);
	}

	@Override
	public void startNewScreen() {
		startActivity(new Intent(this, ScreenActivity.class));
	}

	@Override
	public void startTabBody(final ScreenDescription description, final int anim) {
		final Intent intent = new Intent(this, TabsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // we want TabsActivity to receive onNewIntent calls but otherwise reuse them
		ActivityHelper.startLocalActivity(this, intent, REGION_BODY, R.id.sl_body, anim);
		_bodyActivity = null;
	}

}
