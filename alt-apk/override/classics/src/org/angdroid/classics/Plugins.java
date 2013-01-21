package org.angdroid.classics;

import java.util.zip.ZipInputStream;
import java.util.Scanner;
import java.io.InputStream;

final public class Plugins {
	public enum Plugin {
		Rogue(0), Larn(1), Moria(2);

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
	public static String LoaderLib ="loader-classics";

	public static String getFilesDir(Plugin p) {
		switch (p) {
		case Rogue:
			return "rogue2";
		default: 
			return p.toString().toLowerCase();
		}
	}

	public static int getKeyDown(Plugin p) {
		switch (p) {
		case Rogue: return 'j';
		case Larn: return 'j';
		default: return '2';
		}
	}

	public static int getKeyUp(Plugin p) {
		switch (p) {
		case Rogue: return 'k';
		case Larn: return 'k';
		default: return '8';
		}
	}

	public static int getKeyLeft(Plugin p) {
		switch (p) {
		case Rogue: return 'h';
		case Larn: return 'h';
		default: return '4';
		}
	}

	public static int getKeyRight(Plugin p) {
		switch (p) {
		case Rogue: return 'l';
		case Larn: return 'l';
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

		if (plugin == Plugin.Rogue.getId())
			is = Preferences.getResources().openRawResource(R.raw.ziprogue);
		else if (plugin == Plugin.Larn.getId())
			is = Preferences.getResources().openRawResource(R.raw.ziplarn);
		else if (plugin == Plugin.Moria.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipmoria);
		return new ZipInputStream(is);
	}
	public static String getPluginCrc(int plugin) {
		InputStream is = null;
		if (plugin == Plugin.Rogue.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcrogue);
		else if (plugin == Plugin.Larn.getId())
			is = Preferences.getResources().openRawResource(R.raw.crclarn);
		else if (plugin == Plugin.Moria.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcmoria);
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
