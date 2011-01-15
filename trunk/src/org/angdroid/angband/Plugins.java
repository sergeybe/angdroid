package org.angdroid.angband;

import java.util.zip.ZipInputStream;
import java.io.InputStream;

final public class Plugins {
	public enum Plugin {
		Angband(0), Angband306(1);
		/*
		ToME(0), Sangband(1), NPP(2),
			Angband(3), Angband306(4);
		*/
		private int id;

		private Plugin(int id) {
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~0~0|0~Borg~BORGSAVE~1~1";

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;
		if (plugin == Plugin.Angband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipangband);
		else if (plugin == Plugin.Angband306.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipangband306);

		/*
		else if (plugin == Plugin.ToME.getId())
			is = resources.openRawResource(R.raw.ziptome);
		else if (plugin == Plugin.Sangband.getId())
			is = resources.openRawResource(R.raw.zipsang);
		else if (plugin == Plugin.NPP.getId())
			is = resources.openRawResource(R.raw.zipnpp);
		*/
		return new ZipInputStream(is);
	}
}
