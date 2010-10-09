package org.angdroid.angband;

import java.io.File;

import android.os.Environment;
import android.content.res.Resources;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.util.Log;

final public class Preferences {

	static final String NAME = "angband";
	static final String KEY_PROFILES = "angband.profiles";
	static final String KEY_ACTIVEPROFILE = "angband.activeprofile";
	static final String KEY_VIBRATE = "angband.vibrate";
	static final String KEY_FULLSCREEN = "angband.fullscreen";
	static final String KEY_GAMEPLUGIN = "angband.gameplugin";
	static final String KEY_ALWAYSRUN = "angband.alwaysrun";

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~false";

	private static String activityFilesPath;
	private static SharedPreferences pref;
	private static String[] gamePluginValues;
	private static ProfileList profiles;

	Preferences() {}

	public static void init(File filesDir, Resources resources, SharedPreferences sharedPrefs) {
		activityFilesPath = filesDir.getAbsolutePath();
		pref = sharedPrefs;
		gamePluginValues = resources.getStringArray(R.array.gamePluginValues);
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
			//Log.d("Angband", "loading profiles");
			String s = pref.getString(Preferences.KEY_PROFILES, DEFAULT_PROFILE);
			profiles = ProfileList.deserialize(s);
			if (s.compareTo(DEFAULT_PROFILE)==0) saveProfiles();
		}
		return profiles;
	}

	public static void saveProfiles() {
		// low-level save
		// assumes validation has already occurred in activity

		//Log.d("Angband", "saving profiles");

		// generate Ids if necessary
		ProfileList pl = getProfiles();
		for(int ix = 0; ix < pl.size(); ix++) {
			if (pl.get(ix).id == 0) {
				pl.get(ix).id = pl.getNextId();
			}
		}

		SharedPreferences.Editor ed = pref.edit();
		ed.putString(Preferences.KEY_PROFILES, profiles.serialize());
		ed.commit();
	}

	public static Profile getActiveProfile() {
		ProfileList pl = getProfiles();
		int id = pref.getInt(Preferences.KEY_ACTIVEPROFILE, 0);
		Profile p = pl.findById(id);
		if (p == null) {
			p = pl.get(0);
			setActiveProfile(p);
		}
		return p;
	}

	public static void setActiveProfile(Profile p) {
		ProfileList pl = getProfiles();
		SharedPreferences.Editor ed = pref.edit();
		ed.putInt(Preferences.KEY_ACTIVEPROFILE, p.id);
		ed.commit();
	}

	public static boolean saveFileExists(String filename) {
		for(int i = 0; i < getInstalledPlugins().length; i++) {
			File f = new File(
				getAngbandFilesDirectory(getInstalledPlugins()[i]) 
				+ "/save/" 
				+ filename
			);
			if (f.exists()) return true;
		}
		return false;
	}

	public static String generateSaveFilename() {
		ProfileList pl = getProfiles();
		String saveFile = null;
		for(int i = 2; i<100; i++) {
			saveFile = "PLAYER"+i;
			if (pl.findBySaveFile(saveFile,0) == null
				&& !saveFileExists(saveFile)) 
				break;
		}
		return saveFile;
	}		

	public static int alert(Context ctx, String title, String msg) {
		new AlertDialog.Builder(ctx) 
			.setTitle(title) 
			.setMessage(msg) 
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {}
			}
		).show();
		return 0;
	}
}
