package org.angdroid.angband;

import java.io.File;
import java.lang.reflect.Array;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;

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

	private static String activityFilesPath;
	private static SharedPreferences pref;
	private static int[] gamePlugins;
	private static String[] gamePluginNames;
	private static ProfileList profiles;
	private static String version;
	private static int fontSize = 17;
	private static Resources resources;
	
	public enum KeyAction
	{
		None,
			AltKey,
			CtrlKey,
			EnterKey,
			EscKey,
			Period,
			ShiftKey,
			Space,
			VirtualKeyboard,
			ZoomIn,
			ZoomOut,
			ForwardToSystem;

		public static KeyAction convert(int value)
		{
			return KeyAction.class.getEnumConstants()[value];
		}

		public static KeyAction convert(String value)
		{
			return KeyAction.valueOf(value);
		}
	};
	private static KeyAction menuButtonAction;
	private static KeyAction searchButtonAction;
	private static KeyAction backButtonAction;
	private static KeyAction cameraButtonAction;
	private static KeyAction dpadButtonAction;
	private static KeyAction volumeDownButtonAction;
	private static KeyAction volumeUpButtonAction;
	private static KeyAction emoticonKeyAction;
	private static KeyAction leftShiftKeyAction;
	private static KeyAction rightShiftKeyAction;
	private static KeyAction leftAltKeyAction;
	private static KeyAction rightAltKeyAction;
	private static KeyAction ctrlDoubleTapAction;
	private static KeyAction centerScreenTapAction;

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

		initKeyBinding();
	}

	public static void initKeyBinding() {
		searchButtonAction = KeyAction.convert(pref.getString("angband.searchbutton", "ShiftKey"));
		menuButtonAction = KeyAction.convert(pref.getString("angband.menubutton", "ForwardToSystem"));
		backButtonAction = KeyAction.convert(pref.getString("angband.backbutton", "EscKey"));
		cameraButtonAction = KeyAction.convert(pref.getString("angband.camerabutton", "VirtualKeyboard"));
		dpadButtonAction = KeyAction.convert(pref.getString("angband.dpadbutton", "CtrlKey"));
		volumeDownButtonAction = KeyAction.convert(pref.getString("angband.volumedownbutton", "ZoomOut"));
		volumeUpButtonAction = KeyAction.convert(pref.getString("angband.volumeupbutton", "ZoomIn"));

		emoticonKeyAction = KeyAction.convert(pref.getString("angband.emoticonkey", "CtrlKey"));
		leftAltKeyAction = KeyAction.convert(pref.getString("angband.leftaltkey", "AltKey"));
		rightAltKeyAction = KeyAction.convert(pref.getString("angband.rightaltkey", "AltKey"));
		leftShiftKeyAction = KeyAction.convert(pref.getString("angband.leftshiftkey", "ShiftKey"));
		rightShiftKeyAction = KeyAction.convert(pref.getString("angband.rightshiftkey", "ShiftKey"));

		centerScreenTapAction = KeyAction.convert(pref.getString("angband.centerscreentap", "Space"));

		//todo: implement more generic hardware key double tap handling
		ctrlDoubleTapAction = KeyAction.convert(pref.getString("angband.ctrldoubletap", "EnterKey"));
	}

	public static String getVersion() {
		return version;
	}

	public static Resources getResources() {
		return resources;
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
			+ Plugins.getFilesDir(Plugins.Plugin.convert(pluginId));
	}

	public static String getAngbandFilesDirectory() {
		String dir = Plugins.getFilesDir(getActivePlugin());
		return 
			Environment.getExternalStorageDirectory()
			+ "/"
			+ "Android/data/org.angdroid.angband/files/lib"
			+ dir;
	}

	public static String getActivityFilesDirectory() {
		return activityFilesPath;
	}

	public static String getActivePluginName() {
		return getPluginName(getActivePlugin().getId());
	}

	public static Plugins.Plugin getActivePlugin() {
		int activePlugin;
		int prefPlugin = getActiveProfile().getPlugin();
		activePlugin = gamePlugins[0];
		for(int i = 0; i < gamePlugins.length; i++) {
			if (prefPlugin == gamePlugins[i])
				activePlugin = gamePlugins[i];
		}

		return Plugins.Plugin.convert(activePlugin);
	}

	public static ProfileList getProfiles() {
		if (profiles == null) {
			//Log.d("Angband", "loading profiles");
			String s = pref.getString(Preferences.KEY_PROFILES, "");
			if (s.length() == 0) {
				profiles = ProfileList.deserialize(Plugins.DEFAULT_PROFILE);
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

	public static KeyAction getSearchButtonAction() {
		return searchButtonAction;
	}
	public static KeyAction getMenuButtonAction() {
		return menuButtonAction;
	}
	public static KeyAction getBackButtonAction() {
		return backButtonAction;
	}
	public static KeyAction getCameraButtonAction() {
		return cameraButtonAction;
	}
	public static KeyAction getDpadButtonAction() {
		return dpadButtonAction;
	}
	public static KeyAction getVolumeDownButtonAction() {
		return volumeDownButtonAction;
	}
	public static KeyAction getVolumeUpButtonAction() {
		return volumeUpButtonAction;
	}
	public static KeyAction getEmoticonKeyAction() {
		return emoticonKeyAction;
	}
	public static KeyAction getLeftAltKeyAction() {
		return leftAltKeyAction;
	}
	public static KeyAction getRightAltKeyAction() {
		return rightAltKeyAction;
	}
	public static KeyAction getLeftShiftKeyAction() {
		return leftShiftKeyAction;
	}
	public static KeyAction getRightShiftKeyAction() {
		return rightShiftKeyAction;
	}
	public static KeyAction getCtrlDoubleTapAction() {
		return ctrlDoubleTapAction;
	}
	public static KeyAction getCenterScreenTapAction() {
		return centerScreenTapAction;
	}
}
