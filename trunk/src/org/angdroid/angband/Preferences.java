package org.angdroid.angband;

import java.io.File;

import android.content.SharedPreferences;
import android.os.Environment;
import android.content.res.Resources;
import android.content.Context;
import android.util.Log;

final public class Preferences {

	static final String NAME = "angband";
	static final String KEY_PROFILES = "angband.profiles";
	static final String KEY_ACTIVEPROFILE = "angband.activeprofile";
	static final String KEY_PROFILEID = "angband.profileid";
	static final String KEY_VIBRATE = "angband.vibrate";
	static final String KEY_FULLSCREEN = "angband.fullscreen";
	static final String KEY_GAMEPLUGIN = "angband.gameplugin";
	static final String KEY_ALWAYSRUN = "angband.alwaysrun";

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~false";

	private static String activityFilesPath;
	private static SharedPreferences pref;
	private static String[] gamePluginValues;
	private static ProfileList profiles;
	private static Profile activeProfile;
	private static Resources res;

	Preferences() {}

	public static void init(File filesDir, Resources resources, SharedPreferences sharedPrefs) {
		activityFilesPath = filesDir.getAbsolutePath();
		res = resources;
		pref = sharedPrefs;
		gamePluginValues = res.getStringArray(R.array.gamePluginValues);
	}

	public static boolean getFullScreen() {
		return pref.getBoolean(Preferences.KEY_FULLSCREEN, true);
	}
	public static boolean getAlwaysRun() {
		return pref.getBoolean(Preferences.KEY_ALWAYSRUN, true);
	}
	public static boolean getVibrate() {
		return pref.getBoolean(Preferences.KEY_VIBRATE, false);
	}
	public static String[] getInstalledPlugins() {
		return gamePluginValues;
	}

	public static String getAngbandFilesDirectory(String pluginName) {
		return 
			Environment.getExternalStorageDirectory()
			+ "/"
			+ "Android/data/org.angdroid.angband/files/lib"
			+ pluginName.replace("angband","");
	}
	public static String getAngbandFilesDirectory() {
		return 
			Environment.getExternalStorageDirectory()
			+ "/"
			+ "Android/data/org.angdroid.angband/files/lib"
			+ getActivePluginName().replace("angband","");
	}
	public static String getActivityFilesDirectory() {
		return activityFilesPath;
	}
	public static String getActivePluginName() {
		String activePluginName;
		String prefPluginName = pref.getString(Preferences.KEY_GAMEPLUGIN, "");
		activePluginName = gamePluginValues[0];
		for(int i = 0; i < gamePluginValues.length; i++) {
			if (prefPluginName.compareTo(gamePluginValues[i])==0)
				activePluginName = gamePluginValues[i];
		}
		return activePluginName;
	}
	public static ProfileList getProfiles() {
		if (profiles == null) {
			String s = pref.getString(Preferences.KEY_PROFILES, DEFAULT_PROFILE);
			profiles = ProfileList.deserialize(s);
			if (s.compareTo(DEFAULT_PROFILE)==0) saveProfiles();
		}
		return profiles;
	}
	public static void saveProfiles() {
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(Preferences.KEY_PROFILES, profiles.serialize());
		ed.commit();
	}
	public static Profile getActiveProfile() {
		ProfileList pl = getProfiles();
		activeProfile = pl.get(0);
		int ix = pref.getInt(Preferences.KEY_ACTIVEPROFILE, 0);
		if (ix > -1 && ix < pl.size()) {
			activeProfile = pl.get(ix);
		}
		return activeProfile;
	}
	public static void setActiveProfile(Profile p) {
		ProfileList pl = getProfiles();
		int ix = pl.indexOf(p);
		SharedPreferences.Editor ed = pref.edit();
		ed.putInt(Preferences.KEY_ACTIVEPROFILE, ix);
		ed.commit();
	}
	public static int getNextProfileId() {
		int id = pref.getInt(Preferences.KEY_PROFILEID, 0)+1;
		SharedPreferences.Editor ed = pref.edit();
		ed.putInt(Preferences.KEY_PROFILEID, id);
		ed.commit();

		return id;
	}
}
