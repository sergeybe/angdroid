package org.angdroid.variants;

import java.util.zip.ZipInputStream;
import java.util.Scanner;
import java.io.InputStream;

final public class Plugins {
	public enum Plugin {
		ToME(0), Sangband(1), Steamband(2), Sil(3), NPP(4);

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

	public static int getKeyEnter(Plugin p) {
		return '\r';
	}

	public static int getKeyTab(Plugin p) {
		return '\t';
	}

	public static int getKeyDelete(Plugin p) {
		return 0x7F;
	}

	public static int getKeyBackspace(Plugin p) {
		return '\b';
	}

	public static int getKeyEsc(Plugin p) {
		return 0x1B;
	}

	public static int getKeyQuitAndSave(Plugin p) {
		return 0x18;
	}

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;

		if (plugin == Plugin.ToME.getId())
			is = Preferences.getResources().openRawResource(R.raw.ziptome);
		else if (plugin == Plugin.Sangband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipsang);
		else if (plugin == Plugin.Steamband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipsteam);
		else if (plugin == Plugin.Sil.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipsil);
		else if (plugin == Plugin.NPP.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipnpp);

		return new ZipInputStream(is);
	}
	public static String getPluginCrc(int plugin) {
		InputStream is = null;
		if (plugin == Plugin.ToME.getId())
			is = Preferences.getResources().openRawResource(R.raw.crctome);
		else if (plugin == Plugin.Sangband.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcsang);
		else if (plugin == Plugin.Steamband.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcsteam);
		else if (plugin == Plugin.Sil.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcsil);
		else if (plugin == Plugin.NPP.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcnpp);
		return new Scanner(is).useDelimiter("\\A").next().trim();
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
