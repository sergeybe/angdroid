package org.angdroid.variants;

import java.util.zip.ZipInputStream;
import java.io.InputStream;

final public class Plugins {
	public enum Plugin {
		ToME(0), Sangband(1);
		//NPP(2);

		private int id;

		private Plugin(int id) {
			this.id = id;
		}
		public int getId() {
			return id;
		}
	}

	static final String DEFAULT_PROFILE = "0~Default~PLAYER~0~0";

	public static ZipInputStream getPluginZip(int plugin) {
		InputStream is = null;

		if (plugin == Plugin.ToME.getId())
			is = Preferences.getResources().openRawResource(R.raw.ziptome);
		else if (plugin == Plugin.Sangband.getId())
			is = Preferences.getResources().openRawResource(R.raw.zipsang);
		//else if (plugin == Plugin.NPP.getId())
		//	is = Preferences.getResources().openRawResource(R.raw.zipnpp);

		return new ZipInputStream(is);
	}
}
