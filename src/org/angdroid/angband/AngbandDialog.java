package org.angdroid.angband;

import android.os.Message;
import android.content.DialogInterface;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

public class AngbandDialog {
	private GameActivity activity;
	private StateManager state;
	private ProgressDialog progressDialog = null;

	public enum Action {
			OpenContextMenu
			,GameFatalAlert
			,GameWarnAlert
			,StartGame
			,OnGameExit
			,Toast
			,ToggleKeyboard;

		public static Action convert(int value)
		{
			return Action.class.getEnumConstants()[value];
		}
    };

	AngbandDialog(GameActivity a, StateManager s) {
		activity = a;
		state = s;
	}

	public void HandleMessage(Message msg) {
		//Log.d("Angband","handleMessage: "+msg.what);		

		switch (Action.convert(msg.what)) {
		case OpenContextMenu: // display context menu
			activity.openContextMenu();
			break;
		case ToggleKeyboard: 
			activity.toggleKeyboard();
			break;
		case GameFatalAlert: // fatal error from angband (native side)
			fatalAlert(state.getFatalError());
			break;
		case GameWarnAlert: // warning from angband (native side)
			warnAlert(state.getWarnError());
			break;
		case StartGame: // start angband
			state.gameThread.send(GameThread.Request.StartGame);
			break;
		case OnGameExit: // angband is exiting
			state.gameThread.send(GameThread.Request.OnGameExit);
			break;
		case Toast: 
			Toast.makeText(activity, (String)msg.obj, Toast.LENGTH_SHORT).show();			
			break;
		}
	}

	public void restoreDialog() {
		if (state.fatalError) 
			fatalAlert(state.getFatalError());
		else if (state.warnError) 
			warnAlert(state.getWarnError());
	}

	public int fatalAlert(String msg) {
		new AlertDialog.Builder(activity) 
			.setTitle("Angband") 
			.setMessage(msg) 
			.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						state.fatalMessage=""; 
						state.fatalError=false; 
						activity.finish();
					}
			}
		).show();
		return 0;
	}

	public int warnAlert(String msg) {
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