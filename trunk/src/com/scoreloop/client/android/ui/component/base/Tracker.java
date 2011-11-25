package com.scoreloop.client.android.ui.component.base;

import com.scoreloop.client.android.ui.framework.ValueStore;

public interface Tracker {

	void trackPageView(String activityClassName, ValueStore arguments);

	void trackEvent(String category, String action, String label, int value);

}
