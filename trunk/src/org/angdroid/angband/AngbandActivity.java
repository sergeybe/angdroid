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
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.os.Environment;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.ComponentName;
import android.content.res.Configuration;
import android.widget.LinearLayout;
import android.content.pm.ActivityInfo;

import com.flurry.android.FlurryAgent;

public class AngbandActivity extends Activity {

	public static NativeWrapper xb = null;

	private LinearLayout screenLayout = null; 
	private TermView term = null;
	private boolean kbVisible = false;

	protected static final int CONTEXTMENU_FITWIDTH_ITEM = 0;
	protected static final int CONTEXTMENU_FITHEIGHT_ITEM = 1;
	protected static final int CONTEXTMENU_VKEY_ITEM = 2;

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

		if (xb == null) {
			xb = new NativeWrapper();
		}

/* TODO: fix title bar problem on portrait (vkeyboard) startup
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (Preferences.getFullScreen()) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		else {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
*/
	}

	public void onStart() {
		super.onStart();

		startFlurry();

		rebuildViews();
	}

	public void onStop() {
		super.onStop();

		stopFlurry();
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
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void finish() {
		xb.stopBand();
		super.finish();
	}

	/* TODO: move native code handling out of TermView, then rebuildViews 
		on the fly, and re-link new TermView to native code.

	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		Configuration config = this.getResources().getConfiguration();		
		if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
			Log.d("Angband","onConfigurationChanged: Portrait");
		else
			Log.d("Angband","onConfigurationChanged: Landscape");
	}
	*/

	protected void rebuildViews() {
		Log.d("Angband", "rebuildViews");

	    kbVisible = Preferences.getEnableVirtualKeyboard();
		rebuildViews(kbVisible);
	}


	protected void rebuildViews(boolean kb) {
		Log.d("Angband", "rebuildViews");

	    int orient = Preferences.getOrientation();
		switch (orient) {
		case 0: // sensor
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			break;
		case 1: // portrait
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case 2: // landscape
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		}

 		if (screenLayout != null) screenLayout.removeAllViews();
		screenLayout = new LinearLayout(this);

		term = new TermView(this);
		term.setLayoutParams(
			new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
										  LayoutParams.WRAP_CONTENT, 
										  1.0f)
		);

		registerForContextMenu(term);

		screenLayout.setOrientation(LinearLayout.VERTICAL);
		screenLayout.addView(term);

		if (kb) {
			AngbandKeyboard virtualKeyboard = new AngbandKeyboard(this.term);
			virtualKeyboard.virtualKeyboardView.setLayoutParams(
				new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
											  LayoutParams.WRAP_CONTENT, 
											  0)
			);
			screenLayout.addView(virtualKeyboard.virtualKeyboardView);
		}

		setContentView(screenLayout);

		xb.linkTermView(term);
	}

	@Override
	public void openContextMenu(View view) {
		Log.d("Angband", "openContextMenu");		
		super.openContextMenu(view);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		Log.d("Angband", "onCreateContextMenu");
		menu.setHeaderTitle("Quick Settings");
		menu.add(0, CONTEXTMENU_FITWIDTH_ITEM, 0, "Fit Width"); 
		menu.add(0, CONTEXTMENU_FITHEIGHT_ITEM, 0, "Fit Height"); 
		menu.add(0, CONTEXTMENU_VKEY_ITEM, 0, "Toggle Keyboard"); 
	}

	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
		Log.d("Angband", "onContextItemSelected");		
		switch (aItem.getItemId()) {
		case CONTEXTMENU_FITWIDTH_ITEM:
			term.autoSizeFontByWidth(0);
			xb.redraw();
			return true; 
		case CONTEXTMENU_FITHEIGHT_ITEM:
			term.autoSizeFontByHeight(0);
			xb.redraw();
			return true; 
		case CONTEXTMENU_VKEY_ITEM:
			kbVisible = !kbVisible;
			rebuildViews(kbVisible);
			term.onResume();
			//xb.redraw();
			return true; 
		}
		return false;
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
			// for some reason after toggling keyboard via context menu,
			// term does not receive the BACK onKeyDown anymore.  This
			// patches up that problem
			return term.onKeyDown(keyCode,event);
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
			else if (plugin == Preferences.Plugin.Sangband.getId())
				is = getResources().openRawResource(R.raw.zipsangband);
			else if (plugin == Preferences.Plugin.NPP.getId())
				is = getResources().openRawResource(R.raw.zipnpp);
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


	private void startFlurry() {
		// test key
		FlurryAgent.onStartSession(this, "382WWKEB1V2HZN1UJYBP");

		// release key
		//FlurryAgent.onStartSession(this, "GFZUMCZCJ2J9WNYI8XAV");
	}

	private void stopFlurry() {
		FlurryAgent.onEndSession(this);
	}

}
