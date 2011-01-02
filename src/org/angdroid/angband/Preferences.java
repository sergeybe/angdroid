package org.angdroid.angband;

import java.io.File;
import java.lang.reflect.Array;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.os.Environment;
import android.content.res.Resources;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.app.AlertDialog;
import android.util.Log;

final public class Preferences {

	public static final int rows = 24;
	public static final int cols = 80;

	static final String NAME = "angband";

	static final String KEY_VIBRATE = "angband.vibrate";
	static final String KEY_FULLSCREEN = "angband.fullscreen";
	static final String KEY_ORIENTATION = "angband.orientation";

	static final String KEY_ENABLEVOLKEYFONTSIZE = "angband.enablevolkeyfontsize";
	static final String KEY_ENABLETOUCH = "angband.enabletouch";
	static final String KEY_PORTRAITKB = "angband.portraitkb";
	static final String KEY_LANDSCAPEKB = "angband.landscapekb";
	static final String KEY_PORTRAITFONTSIZE = "angband.portraitfontsize";
	static final String KEY_LANDSCAPEFONTSIZE = "angband.landscapefontsize";
	static final String KEY_ALWAYSRUN = "angband.alwaysrun";

	static final String KEY_GAMEPLUGIN = "angband.gameplugin";
	static final String KEY_SKIPWELCOME = "angband.skipwelcome";
	static final String KEY_AUTOSTARTBORG = "angband.autostartborg";

	static final String KEY_PROFILES = "angband.profiles";
	static final String KEY_ACTIVEPROFILE = "angband.activeprofile";

	static final String KEY_INSTALLEDVERSION = "angband.installedversion";

	public enum Plugin {
		Angband(0), Angband306(1), ToME(2), Sangband(3), NPP(4);

		private int id;

		private Plugin(int id) {
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~0~0|0~Borg~BORGSAVE~1~1";

	private static String activityFilesPath;
	private static SharedPreferences pref;
	private static int[] gamePlugins;
	private static String[] gamePluginNames;
	private static ProfileList profiles;
	private static String version;
	private static int fontSize = 17;
	private static Resources resources;

	Preferences() {}

	public static void init(File filesDir, Resources res, SharedPreferences sharedPrefs, String pversion) {
		activityFilesPath = filesDir.getAbsolutePath();
		pref = sharedPrefs;
		resources = res;

		String[] gamePluginsStr = resources.getStringArray(R.array.gamePlugins);
		gamePlugins = (int[])Array.newInstance(int.class, gamePluginsStr.length);
		for(int i = 0; i<gamePluginsStr.length; i++)
			gamePlugins[i] = Integer.parseInt(gamePluginsStr[i]);
		
		gamePluginNames = resources.getStringArray(R.array.gamePluginNames);
		version = pversion;
	}

	public static String getVersion() {
		return version;
	}

	public static String getInstalledVersion() {
		return pref.getString(Preferences.KEY_INSTALLEDVERSION, "");
	}
	public static void setInstalledVersion(String value) {
		SharedPreferences.Editor ed = pref.edit();
		ed.putString(Preferences.KEY_INSTALLEDVERSION, value);
		ed.commit();			
	}

	public static boolean getFullScreen() {
		return pref.getBoolean(Preferences.KEY_FULLSCREEN, true);
	}
	public static void setFullScreen(boolean value) {
		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(Preferences.KEY_FULLSCREEN, value);
		ed.commit();			
	}
	public static boolean isScreenPortraitOrientation() {
		Configuration config = resources.getConfiguration();		
		return (config.orientation == Configuration.ORIENTATION_PORTRAIT);
	}

	public static int getOrientation() {
		return Integer.parseInt(pref.getString(Preferences.KEY_ORIENTATION, "0"));
	}

	public static int getDefaultFontSize() {
		return fontSize;
	}
	public static int setDefaultFontSize(int value) {
		return fontSize = value;
	}

	public static boolean getVibrate() {
		return pref.getBoolean(Preferences.KEY_VIBRATE, false);
	}

	public static boolean getVolumeKeyFontSizing() {
		return pref.getBoolean(Preferences.KEY_ENABLEVOLKEYFONTSIZE, true);
	}

	public static boolean getPortraitKeyboard() {
		return pref.getBoolean(Preferences.KEY_PORTRAITKB, true);
	}
	public static void setPortraitKeyboard(boolean value) {
		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(Preferences.KEY_PORTRAITKB, value);
		ed.commit();			
	}
	public static boolean getLandscapeKeyboard() {
		return pref.getBoolean(Preferences.KEY_LANDSCAPEKB, false);
	}
	public static void setLandscapeKeyboard(boolean value) {
		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(Preferences.KEY_LANDSCAPEKB, value);
		ed.commit();			
	}

	public static int getPortraitFontSize() {
		return pref.getInt(Preferences.KEY_PORTRAITFONTSIZE, 0);
	}
	public static void setPortraitFontSize(int value) {
		SharedPreferences.Editor ed = pref.edit();
		ed.putInt(Preferences.KEY_PORTRAITFONTSIZE, value);
		ed.commit();			
	}
	public static int getLandscapeFontSize() {
		return pref.getInt(Preferences.KEY_LANDSCAPEFONTSIZE, 0);
	}
	public static void setLandscapeFontSize(int value) {
		SharedPreferences.Editor ed = pref.edit();
		ed.putInt(Preferences.KEY_LANDSCAPEFONTSIZE, value);
		ed.commit();			
	}

	public static boolean getEnableTouch() {
		return pref.getBoolean(Preferences.KEY_ENABLETOUCH, true);
	}

	public static boolean getAlwaysRun() {
		return pref.getBoolean(Preferences.KEY_ALWAYSRUN, true);
	}

	public static boolean getSkipWelcome() {
		return getActiveProfile().getSkipWelcome();
	}

	public static boolean getAutoStartBorg() {
		return getActiveProfile().getAutoStartBorg();
	}

	public static int[] getInstalledPlugins() {
		return gamePlugins;
	}

	public static String getPluginName(int pluginId) {
		if (pluginId > -1 && pluginId < gamePluginNames.length)
			return gamePluginNames[pluginId];
		else 
			return gamePluginNames[0];
	}

	public static String getAngbandFilesDirectory(int pluginId) {
		return 
			Environment.getExternalStorageDirectory()
			+ "/"
			+ "Android/data/org.angdroid.angband/files/lib"
			+ getPluginName(pluginId).replace("angband","");
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

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;
		if (plugin == Preferences.Plugin.Angband.getId())
			is = resources.openRawResource(R.raw.zipangband);
		else if (plugin == Preferences.Plugin.Angband306.getId())
			is = resources.openRawResource(R.raw.zipangband306);
		else if (plugin == Preferences.Plugin.ToME.getId())
			is = resources.openRawResource(R.raw.ziptome);
		else if (plugin == Preferences.Plugin.Sangband.getId())
			is = resources.openRawResource(R.raw.zipsang);
		/*
		else if (plugin == Preferences.Plugin.NPP.getId())
			is = resources.openRawResource(R.raw.zipnpp);
		*/
		return new ZipInputStream(is);
	}

	public static String getActivePluginName() {
		int activePlugin;
		int prefPlugin = getActiveProfile().getPlugin();
		activePlugin = gamePlugins[0];
		for(int i = 0; i < gamePlugins.length; i++) {
			if (prefPlugin == gamePlugins[i])
				activePlugin = gamePlugins[i];
		}

		return getPluginName(activePlugin);
	}

	public static ProfileList getProfiles() {
		if (profiles == null) {
			//Log.d("Angband", "loading profiles");
			String s = pref.getString(Preferences.KEY_PROFILES, "");
			if (s.length() == 0) {
				profiles = ProfileList.deserialize(DEFAULT_PROFILE);
				saveProfiles();

				// for some reason ProfileListPreference needs a persisted value to display
				// the very first time.
				// ...there is probably an override to get around this in ListPreference.
				SharedPreferences.Editor ed = pref.edit();
				ed.putString(Preferences.KEY_GAMEPLUGIN, String.valueOf(getActiveProfile().getPlugin()));
				ed.commit();			
			}
			else {
				profiles = ProfileList.deserialize(s);
			}
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
		for(int i = 2; i < 100; i++) {
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
