package org.angdroid.nightly;

import java.util.zip.ZipInputStream;
import java.util.Scanner;
import java.io.InputStream;

final public class Plugins {
	public enum Plugin {
		Nightly(0);

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
	public static String LoaderLib ="loader-nightly";

	public static String getFilesDir(Plugin p) {
		return p.toString().toLowerCase();
	}

	// these codes have to match the codes in extsrc/src/ui-event.h

	public static int getKeyDown(Plugin p) {
		return 0x80;
	}

	public static int getKeyUp(Plugin p) {
		return 0x83;
	}

	public static int getKeyLeft(Plugin p) {
		return 0x81;
	}

	public static int getKeyRight(Plugin p) {
		return 0x82;
	}

	public static int getKeyEnter(Plugin p) {
		return 0x9C;
	}

	public static int getKeyTab(Plugin p) {
		return 0x9D;
	}

	public static int getKeyDelete(Plugin p) {
		return 0x9E;
	}

	public static int getKeyBackspace(Plugin p) {
		return 0x9F;
	}

	public static int getKeyEsc(Plugin p) {
		return 0xE000;
	}

	public static int getKeyQuitAndSave(Plugin p) {
		return 0x18;
	}

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;

		if (plugin == Plugin.Nightly.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipnightly);

		return new ZipInputStream(is);
	}
	public static String getPluginCrc(int plugin) {
		InputStream is = null;
		if (plugin == Plugin.Nightly.getId())
			is = Preferences.getResources().openRawResource(R.raw.crcnightly);
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
