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

public class AngbandActivity extends Activity {

	private TermView term;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		extractAngbandResources();

		setContentView(R.layout.main);
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
			intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			break;
		case '3':
			term.onPause();
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onResume() {
		Log.d("Angband", "onResume");
		super.onResume();

		SharedPreferences pref = getSharedPreferences(Preferences.NAME,
				MODE_PRIVATE);

		if (pref.getBoolean(Preferences.KEY_FULLSCREEN, true)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		term.setVibrate(pref.getBoolean(Preferences.KEY_VIBRATE, false));

		term.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("Angband", "onPause");
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

	void extractAngbandResources() {
		File f = new File(getAngbandFilesDirectory());
		f.mkdirs();
		String abs_path = f.getAbsolutePath();

		InputStream is = getResources().openRawResource(R.raw.zip);
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry ze;
		try {
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

				if (!myfile.createNewFile()) {
					Log.v("Angband",
							"file exists. not extracting any more files.");
					break;
				}

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

			// copy version 0.15 save file to sdcard
			File oldsave = new File(getFilesDir().getAbsolutePath() +
						"/lib/save/PLAYER");
			//Log.v("Angband","old save path = " + oldsave.getAbsolutePath());
			if (oldsave.exists()) {
			    Log.v("Angband", "0.15 save files exists; copying");
			    File newsave = new File(getAngbandFilesDirectory() +
					       "/save/PLAYER");
			    FileReader in = new FileReader(oldsave);
			    FileWriter out = new FileWriter(newsave,false);
			    int c;

			    while ((c = in.read()) != -1)
				out.write(c);

			    in.close();
			    out.close();
			    oldsave.delete();
			}
		} catch (Exception e) {
			Log.v("Angband", "error extracting files: " + e);
		}
	}

	public static String  getAngbandFilesDirectory() {
		return 
			Environment.getExternalStorageDirectory()
			+ "/"
			+ "Android/data/org.angdroid.angband/files";
	}
}