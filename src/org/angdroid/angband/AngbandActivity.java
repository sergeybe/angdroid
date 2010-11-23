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
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.ComponentName;
import android.widget.LinearLayout;
import android.content.pm.ActivityInfo;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;

import com.flurry.android.FlurryAgent;

public class AngbandActivity extends Activity {

	public static NativeWrapper xb = null;
	private static String progress_lock = "lock";

	private LinearLayout screenLayout = null; 
	private TermView term = null;

	protected static final int CONTEXTMENU_FITWIDTH_ITEM = 0;
	protected static final int CONTEXTMENU_FITHEIGHT_ITEM = 1;
	protected static final int CONTEXTMENU_VKEY_ITEM = 2;

	protected Handler handler = null;
	protected ProgressDialog progressDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("Angband", "onCreate");		

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

		final AngbandActivity aa = this;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Log.d("Angband","handleMessage: "+msg.what);		
				switch (msg.what) {
				case 10: // display progress
					aa.showProgress((String)msg.obj);
					break;
				case 20: // dismiss progress
					aa.dismissProgress();
					break;
				case 30: // fatal error
					aa.showFatalAlert();
					break;
				case 40: // display context menu
					aa.openContextMenu();
					break;
				}
			}
		};

		if (xb == null) {
			xb = new NativeWrapper();
		}
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
		Log.d("Angband","finish");
		xb.stopBand();
		super.finish();
	}

	protected void rebuildViews() {
		synchronized (progress_lock) {
			//Log.d("Angband","rebuildViews");
			dismissProgress();

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
			term.setFocusable(false);
			registerForContextMenu(term);
			xb.link(term, handler);

			screenLayout.setOrientation(LinearLayout.VERTICAL);
			screenLayout.addView(term);

			Boolean kb = false;
			if(Preferences.isScreenPortraitOrientation())
				kb = Preferences.getPortraitKeyboard();
			else		
				kb = Preferences.getLandscapeKeyboard();

			if (kb) {
				AngbandKeyboard virtualKeyboard = new AngbandKeyboard(this);
				screenLayout.addView(virtualKeyboard.virtualKeyboardView);
			}

			setContentView(screenLayout);

			if (xb.installResult == -1) // install in progress
				showProgress("Installing files...");
			if (xb.installResult > 0) // install fatal error
				showFatalAlert();

			term.invalidate();
		}
	}

	public void openContextMenu() {
		super.openContextMenu(term);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Quick Settings");
		menu.add(0, CONTEXTMENU_FITWIDTH_ITEM, 0, "Fit Width"); 
		menu.add(0, CONTEXTMENU_FITHEIGHT_ITEM, 0, "Fit Height"); 
		menu.add(0, CONTEXTMENU_VKEY_ITEM, 0, "Toggle Keyboard"); 
	}

	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
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
			if(Preferences.isScreenPortraitOrientation())
				Preferences.setPortraitKeyboard(!Preferences.getPortraitKeyboard());
			else		
				Preferences.setLandscapeKeyboard(!Preferences.getLandscapeKeyboard());
			rebuildViews();
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
		return term.onKeyDown(keyCode,event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return term.onKeyUp(keyCode,event);
	}

	public Handler getHandler() {
		return handler;
	}

	public void showProgress(String msg) {		
		synchronized (progress_lock) {
			Log.d("Angband", "showProgress");		
			progressDialog = ProgressDialog.show(this, "Angband", msg, true);
		}
	}
	public void dismissProgress() {
		synchronized (progress_lock) {
			Log.d("Angband", "dismissProgress");		
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	public void showFatalAlert() {
		//Log.d("Angband","showFatalAlert");		
		String errMsg = "Error: an unknown error occurred, cannot continue.";
		switch(xb.installResult) {
		case 1:
			errMsg = "Error: external storage card not found, cannot continue.";
			break;
		case 2:
			errMsg = "Error: failed to write and verify files to external storage, cannot continue.";
			break;
		}
		fatalAlert(errMsg);
	}

	public int fatalAlert(String msg) {
		//Log.d("Angband","fatalAlert");		
		//dismissProgress();
		new AlertDialog.Builder(this) 
			.setTitle("Angband") 
			.setMessage(msg) 
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {xb.installResult=-2; finish();}
			}
		).show();
		return 0;
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
