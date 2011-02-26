package org.angdroid.angband;

import android.os.Handler;
import android.os.Message;

import android.util.Log;
	
public class GameThread implements Runnable {

	public enum Request{
		StartGame
			,StopGame
			,SaveGame
			,OnGameExit;

		public static Request convert(int value)
		{
			return Request.class.getEnumConstants()[value];
		}
    };

	/* game thread state */	
	private Thread thread = null;
	private boolean game_thread_running = false;
	private boolean game_fully_initialized = false;
	private boolean game_restart = false;
	private String running_plugin = null;
	private String running_profile = null;
	private boolean plugin_change = false;
	private NativeWrapper nativew = null;
	private Handler handler = null;
	private StateManager state = null;
	
	public GameThread(StateManager s, NativeWrapper nw) {
		nativew = nw;
		state = s;
	}

	public void link(Handler h) {
		handler = h;		
	}

	public synchronized void send(Request rq) {
		switch (rq) {
		case StartGame:
			start();
			break;
		case StopGame:
			stop();
			break;
		case SaveGame:
			save();
			break;
		case OnGameExit:
			onGameExit();
			break;
		}
	}

	private void start() {
		plugin_change = false;

		// sanity checks: thread must not already be running
		// and we must have a valid canvas to draw upon.
		//			already_running = game_thread_running;
		//	already_initialized = game_fully_initialized;	  


		if (state.fatalError) {

			// don't bother restarting here, we are going down.
			Log.d("Angband","start.fatalError is set");
		}
		else if (game_thread_running) {

			/* this is an onResume event */
			if (game_fully_initialized &&
				running_plugin != null && 
				( running_plugin.compareTo(Preferences.getActivePluginName())!=0 ||
				  running_profile.compareTo(Preferences.getActiveProfile().getName())!=0 ) ) {
			
				/* plugin or profile has been changed */

				Log.d("Angband","start.plugin changed");
				plugin_change = true;
				stop();
			}
			else {
				//Log.d("Angband","startBand.redrawing");
				state.nativew.resize();
			}			
		}
		else {
			
			/* time to start angband */

			/* check install */
			Installer installer = new Installer(state, handler);
			installer.checkInstall();

			/* notify wrapper game is about to start */
			nativew.onGameStart();
			
 			/* initialize keyboard buffer */
			state.keyBuffer = new KeyBuffer(state);
			state.keyBuffer.link(handler);

			game_thread_running = true;

			//Log.d("Angband","startBand().starting loader thread");

			thread = new Thread(this);
			thread.start();
		}
	}

	private void stop() {
		// signal keybuffer to send quit command to angband 
		// (this is when the user chooses quit or the app is pausing)

		//Log.d("Angband","GameThread.Stop()");

		if (!game_thread_running) {
			//Log.d("Angband","stop().no game running");
			return;
		}
		if (thread == null)  {
			//Log.d("Angband","stop().no thread");
			return;
		}

		state.keyBuffer.signalGameExit();

		//Log.d("Angband","signalGameExit.waiting on thread.join()");

		try {
			thread.join();
		} catch (Exception e) {
			Log.d("Angband",e.toString());
		}

		//Log.d("Angband","signalGameExit.after waiting for thread.join()");
	}

	private void save() {
		//Log.d("Angband","saveBand()");

		if (!game_thread_running) {
			Log.d("Angband","save().no game running");
			return;
		}
		if (thread == null || state.keyBuffer == null)  {
			Log.d("Angband","save().no thread");
			return;
		}
		if (state.installState != StateManager.InstallState.Success) {
			Log.d("Angband","save().install not finished or not successful");
			return;
		}
	 
		state.keyBuffer.signalSave();
	}

	private void onGameExit() {
		boolean local_restart = false;
			
		Log.d("Angband","GameThread.onGameExit()");
		game_thread_running = false;
		game_fully_initialized = false;

		// if game exited normally, restart!
		local_restart 
			= game_restart 
			= ((!state.keyBuffer.getSignalGameExit() || plugin_change) 
			   && state.installState == StateManager.InstallState.Success 
			   && !state.fatalError);

		if	(local_restart) 
			handler.sendEmptyMessage(AngbandDialog.Action.StartGame.ordinal());
	}

	public void setFullyInitialized() {
		if (!game_fully_initialized) 
			Log.d("Angband","game is fully initialized");

		game_fully_initialized = true;		
	}

	public void run() {		
		if (game_restart) {
			game_restart = false;
			/* this hackery is no longer needed after
				serializing all access to GameThread 
				through the sync'd send() method and
			 	use of handlers to initiate async actions.  */
			/*
			try {
				// if restarting, pause for effect (and to let the
				// other game thread unlock its mutex!)
				Thread.sleep(400);
			} catch (Exception ex) {}
			*/
		}

		Log.d("Angband","GameThread.run");

		running_plugin = Preferences.getActivePluginName();
		running_profile = Preferences.getActiveProfile().getName();

	    String pluginPath = Preferences.getActivityFilesDirectory()
			+"/../lib/lib"+running_plugin+".so";

		// wait for and validate install processing (if any);
		//Log.d("Angband","run.waiting for install");
		Installer.waitForInstall();

		if (state.installState != StateManager.InstallState.Success) {
			//Log.d("Angband","installState bad sending fatal message");
			state.fatalError = true;
			handler.sendEmptyMessage(AngbandDialog.Action.InstallFatalAlert.ordinal());
			onGameExit();
			return;
		}

		/* game is not running, so start it up */
		nativew.gameStart(
				  pluginPath, 
				  2, 
				  new String[]{
					  Preferences.getAngbandFilesDirectory(),
					  Preferences.getActiveProfile().getSaveFile()
				  }
		);
	}
}
