/*
 * File: AngbandActivity.java
 * Purpose: Generic ui functions in Android application
 *
 * Copyright (c) 2010 David Barr, Sergey Belinsky
 * 
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the "Angband licence":
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 */

package org.angdroid.angband;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.os.Environment;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.ComponentName;

import com.flurry.android.FlurryAgent;

public class AngbandActivity extends Activity {

	private static TermView term = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String version = "unknown";
		try {
			ComponentName comp = new ComponentName(this, AngbandActivity.class);
			PackageInfo pinfo = this.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			version = pinfo.versionName;
		} catch (Exception e) {}

	    Preferences.init ( 
			getFilesDir(),
			getResources(), 
			getSharedPreferences(Preferences.NAME, MODE_PRIVATE),
			version
		);

		for(int i = 0; i < Preferences.getInstalledPlugins().length; i++) {
			extractAngbandResources(Preferences.getInstalledPlugins()[i]);
		}

		setContentView(R.layout.main);

		// it seems that the activity is restarted sometimes
		// without a pause notification.  I can reproduce it when
		// I answer a phone call, hang up, then press back key.
		// In this case, make sure we get out of the old session!
		if (term != null) term.onPause();

		term = (TermView) findViewById(R.id.term);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(getApplication());
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent;
		switch (item.getNumericShortcut()) {
		case '1':
			intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			break;
		case '2':
			intent = new Intent(this, ProfilesActivity.class);
			startActivity(intent);
			break;
		case '3':
			intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			break;
		case '4':
			term.onPause();
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onResume() {
		//Log.d("Angband", "onResume");
		super.onResume();

		if (Preferences.getFullScreen()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		term.onResume();
	}

	@Override
	protected void onPause() {
		//Log.d("Angband", "onPause");
		super.onPause();
		term.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Dirty hack for BACK key
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	void extractAngbandResources(int plugin) {

		try {
			File f = new File(Preferences.getAngbandFilesDirectory(plugin));
			f.mkdirs();
			String abs_path = f.getAbsolutePath();

			File cookie = new File(abs_path + "/installed-" + Preferences.getVersion()); 
			// drop a cookie to indicate we've extracted the files
			if (!cookie.createNewFile()) {
				return; 
			}

			InputStream is = null;
			if (plugin == Preferences.Plugin.Angband.getId())
				is = getResources().openRawResource(R.raw.zipangband);
			else if (plugin == Preferences.Plugin.Angband306.getId())
				is = getResources().openRawResource(R.raw.zipangband306);
			/*
			  else if (plugin == Preferences.Plugin.ToME.getId())
			  is = getResources().openRawResource(R.raw.ziptome);
			  }
			*/

			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze;

			while ((ze = zis.getNextEntry()) != null) {
				String ze_name = ze.getName();
				Log.v("Angband", "extracting " + ze_name);

				String filename = abs_path + "/" + ze_name;
				File myfile = new File(filename);

				if (ze.isDirectory()) {
					myfile.mkdirs();
					continue;
				}

				byte contents[] = new byte[(int) ze.getSize()];

				FileOutputStream fos = new FileOutputStream(myfile);
				int remaining = (int) ze.getSize();

				while (remaining > 0) {
					int readlen = zis.read(contents, 0, remaining);
					fos.write(contents, 0, readlen);
					remaining -= readlen;
				}

				fos.close();
				zis.closeEntry();
			}
			zis.close();
		} catch (Exception e) {
			Log.v("Angband", "error extracting files: " + e);
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		//Log.d("Angband", "delete old file: "+dir.getAbsolutePath());
		return dir.delete();
	}


	public void onStart() {
		super.onStart();
		// test key
		FlurryAgent.onStartSession(this, "382WWKEB1V2HZN1UJYBP");

		// release key
		//FlurryAgent.onStartSession(this, "GFZUMCZCJ2J9WNYI8XAV");

		// your code
	}

	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
		// your code
	}
}
