package org.angdroid.variants;

import java.util.zip.ZipInputStream;
import java.io.InputStream;

final public class Plugins {
	public enum Plugin {
		ToME(0), Sangband(1), Steamband(2);
		//NPP(?);

		private int id;

		private Plugin(int id) {
			this.id = id;
		}
		public int getId() {
			return id;
		}
		public static Plugin convert(int value) {
			return Plugin.class.getEnumConstants()[value];
		}
	}

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~0~0";
	public static String LoaderLib ="loader-variants";

	public static String getFilesDir(Plugin p) {
		switch (p) {
		case Sangband: return "sang";
		case Steamband: return "steam";
		default: return p.toString().toLowerCase();
		}
	}

	public static int getKeyDown(Plugin p) {
		switch (p) {
		default: return '2';
		}
	}

	public static int getKeyUp(Plugin p) {
		switch (p) {
		default: return '8';
		}
	}

	public static int getKeyLeft(Plugin p) {
		switch (p) {
		default: return '4';
		}
	}

	public static int getKeyRight(Plugin p) {
		switch (p) {
		default: return '6';
		}
	}

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;

		if (plugin == Plugin.ToME.getId())
			is = Preferences.getResources().openRawResource(R.raw.ziptome);
		else if (plugin == Plugin.Sangband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipsang);
		else if (plugin == Plugin.Steamband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipsteam);
		//else if (plugin == Plugin.NPP.getId())
		//	is = Preferences.getResources().openRawResource(R.raw.zipnpp);

		return new ZipInputStream(is);
	}

	public static String getUpgradePath(Plugin p) {
		switch (p) {
		default: return "";
		}		
	}

	public static String getStartBorgSequence() {
		return "";
	}

}
