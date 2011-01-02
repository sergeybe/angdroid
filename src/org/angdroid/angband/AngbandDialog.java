package org.angdroid.angband;

import android.os.Message;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.util.Log;

public class AngbandDialog {
	private AngbandActivity activity;
	private StateManager state;
	private ProgressDialog progressDialog = null;

	public enum Action {
		ShowProgress
			,DismissProgress
			,InstallFatalAlert
			,OpenContextMenu
			,GameFatalAlert
			,GameWarnAlert;

		public static Action convert(int value)
		{
			return Action.class.getEnumConstants()[value];
		}
    };

	AngbandDialog(AngbandActivity a, StateManager s) {
		activity = a;
		state = s;
	}

	public void HandleMessage(Message msg) {
		//Log.d("Angband","handleMessage: "+msg.what);		

		switch (Action.convert(msg.what)) {
		case ShowProgress: // display progress
			showProgress("Installing files...");
			break;
		case DismissProgress: // dismiss progress
			dismissProgress();
			break;
		case InstallFatalAlert: // fatal error during install/startup
			fatalAlert(state.getInstallError());
			break;
		case OpenContextMenu: // display context menu
			activity.openContextMenu();
			break;
		case GameFatalAlert: // fatal error from angband (native side)
			fatalAlert(state.getFatalError());
			break;
		case GameWarnAlert: // warning from angband (native side)
			warnAlert(state.getWarnError());
			break;
		}
	}

	public void restoreDialog() {
		if (state.installState == StateManager.InstallState.InProgress)
			showProgress("Installing files...");
		else if (StateManager.InstallState.isError(state.installState))
			fatalAlert(state.getInstallError());
		else if (state.fatalError) 
			fatalAlert(state.getFatalError());
		else if (state.warnError) 
			warnAlert(state.getWarnError());
	}

	public void showProgress(String msg) {		
		synchronized (state.progress_lock) {
			Log.d("Angband", "showProgress");		
			progressDialog = ProgressDialog.show(activity, "Angband", msg, true);
		}
	}
	public void dismissProgress() {
		synchronized (state.progress_lock) {
			Log.d("Angband", "dismissProgress");		
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

	public int fatalAlert(String msg) {
		//Log.d("Angband","fatalAlert");		
		//dismissProgress();
		new AlertDialog.Builder(activity) 
			.setTitle("Angband") 
			.setMessage(msg) 
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						state.installState=StateManager.InstallState.Unknown; 
						state.fatalMessage=""; 
						state.fatalError=false; 
						activity.finish();
					}
			}
		).show();
		return 0;
	}

	public int warnAlert(String msg) {
		//Log.d("Angband","warnAlert");		
		//dismissProgress();
		new AlertDialog.Builder(activity) 
			.setTitle("Angband") 
			.setMessage(msg) 
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						state.warnMessage=""; 
						state.warnError = false;
					}
			}
		).show();
		return 0;
	}

}