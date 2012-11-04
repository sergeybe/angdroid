package org.angdroid.angband;

import android.app.Activity;

import org.angdroid.angband.ActivityKeys;

import com.flurry.android.FlurryAgent;

public class StatPublisher
{
	public static void start(Activity a)
	{
		FlurryAgent.onStartSession(a, ActivityKeys.FlurryKey);
	}

	public static void stop(Activity a)
	{
		FlurryAgent.onEndSession(a);	
	}
}