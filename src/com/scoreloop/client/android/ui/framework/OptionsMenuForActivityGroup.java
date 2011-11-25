package com.scoreloop.client.android.ui.framework;

import android.view.Menu;
import android.view.MenuItem;

public interface OptionsMenuForActivityGroup {
	boolean onCreateOptionsMenuForActivityGroup(Menu menu);

	boolean onPrepareOptionsMenuForActivityGroup(Menu menu);

	boolean onOptionsItemSelectedForActivityGroup(MenuItem item);
}
