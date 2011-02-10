package org.angdroid.angband;

import java.util.zip.ZipInputStream;
import java.io.InputStream;
import android.os.Environment;

final public class Plugins {
	public enum Plugin {
		Angband(0), Angband306(1), FrogKnows(2), Moria(3), Rogue(4);
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

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~0~0|0~Borg~BORGSAVE~1~1";
	public static String LoaderLib ="loader-angband";

	public static String getFilesDir(Plugin p) {
		switch (p) {
		case Angband: return "angband320";
		case Angband306: return "306";
		default: return p.toString().toLowerCase();
		}
	}

	public static int getKeyDown(Plugin p) {
		switch (p) {
		case Angband: return 0x8A;
		case Rogue: return 'j';
		default: return '2';
		}
	}

	public static int getKeyUp(Plugin p) {
		switch (p) {
		case Angband: return 0x8D;
		case Rogue: return 'k';
		default: return '8';
		}
	}

	public static int getKeyLeft(Plugin p) {
		switch (p) {
		case Angband: return 0x8B;
		case Rogue: return 'h';
		default: return '4';
		}
	}

	public static int getKeyRight(Plugin p) {
		switch (p) {
		case Angband: return 0x8C;
		case Rogue: return 'l';
		default: return '6';
		}
	}

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;
		if (plugin == Plugin.Angband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipangband320);
		else if (plugin == Plugin.Angband306.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipangband306);
		else if (plugin == Plugin.FrogKnows.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipfrogknows);
		else if (plugin == Plugin.Moria.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipmoria);
		else if (plugin == Plugin.Rogue.getId())
			is = Preferences.getResources().openRawResource(R.raw.ziprogue);
		return new ZipInputStream(is);
	}

	public static String getUpgradePath(Plugin p) {
		switch (p) {
		case Angband: 
			return Environment.getExternalStorageDirectory()
				+ "/"
				+ "Android/data/org.angdroid.angband/files/lib/save";
		default: return "";
		}		
	}
}
