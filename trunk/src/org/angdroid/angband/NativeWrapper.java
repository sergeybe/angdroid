package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
	
public class NativeWrapper implements Runnable {
	// Load native library
	static {
		System.loadLibrary("loader");
	}

	private TermView term = null;
	private Handler	handler = null;
	private StateManager state = null;

	/* game thread state */	
	private Thread thread = null;
	private boolean game_thread_running = false;
	private boolean game_fully_initialized = false;
	private String game_thread_lock = "lock";
	private String display_lock = "lock";
	private boolean signal_game_exit = false;
	private boolean game_restart = false;

	private int quit_key_seq = 0;
	private String running_plugin = null;
	private boolean plugin_change = false;

	/* keyboard state */
	private Queue<Integer> keybuffer = new LinkedList<Integer>();
	private boolean wait = false;

	/* cached prefs */
	private boolean always_run = true;

	// Call native methods from library
	native void gameStart(String pluginPath, int argc, String[] argv);
	native int gameQueryRedraw(int x1, int y1, int x2, int y2);
	native int gameQueryInt(int argc, String[] argv);
	native String gameQueryString(int argc, String[] argv);

	public void link(TermView aterm, Handler ahandler, StateManager astate) {
		synchronized (display_lock) {
			handler = ahandler;
			term = aterm;
			always_run = Preferences.getAlwaysRun();
			state = astate;
		}
	}

	int getch(final int v) {

		Integer key = null;

		//Log.d("Angband", "getch");
		if (!game_fully_initialized)
			Log.d("Angband","game is fully initialized");

		game_fully_initialized = true;

		synchronized (keybuffer) {

			key = getSpecialKey();
			if (key >= 0) return key;

			//peek before wait -- fix issue #3 keybuffer loss
			if (keybuffer.peek() != null) {
				key = keybuffer.poll();
				//Log.w("Angband", "process key = " + key);
				return key;
			}		

			if (v == 1) {
				// Wait key press
				try {
		        	//Log.d("Angband", "Wait keypress BEFORE");
					wait = true;
					//keybuffer.clear(); //not necessary
					keybuffer.wait();
					wait = false;
					//Log.d("Angband", "Wait keypress AFTER");
				} catch (Exception e) {
					Log.d("Angband", "The getch() wait exception" + e);
				}
			}

			// return key after wait, if there is one
			if (keybuffer.peek() != null) {
				key = keybuffer.poll();
				//Log.w("Angband", "process key = " + key);
				return key;
			}		
		}

		return 0;
	}

	public void addDirectionToKeyBuffer(int key) {
		boolean rogueLike = (AngbandActivity.xb.gameQueryInt(1,new String[]{"rl"})==1);

		if (rogueLike) {
			switch(key) {
			case '1': key = 'b'; break;
			case '2': key = 'j'; break;
			case '3': key = 'n'; break;
			case '4': key = 'h'; break;
			case '5': key = ' '; break;
			case '6': key = 'l'; break;
			case '7': key = 'y'; break;
			case '8': key = 'k'; break;
			case '9': key = 'u'; break;
			}
		}

		if (always_run && wait) {
			synchronized (keybuffer) {
				if (rogueLike) {
					key = Character.toUpperCase(key);
				}
				else {
					keybuffer.offer(46); // '.' command
				}
				keybuffer.offer(key);

				if (wait) {
					//Log.d("Angband", "Wake up!!!");
					keybuffer.notify();
				}
			}
		} else {
			addToKeyBuffer(key);
		}
	}

	public void addToKeyBuffer(int key) {
		//Log.d("Angband", "addKeyToBuffer:"+key);
		synchronized (keybuffer) {
			keybuffer.offer(key);
			if (wait) {
				//Log.d("Angband", "Wake up!!!");
				keybuffer.notify();
			}
		}
	}

	public void clearKeyBuffer() {
		synchronized (keybuffer) {
			keybuffer.clear();
		}
	}

	public void run() {	    
		synchronized (game_thread_lock) {
			if (game_restart) {
				game_restart = false;
				try {
					// if restarting, pause for effect (and to let the
					// other game thread unlock its mutex!)
					Thread.sleep(400);
				} catch (Exception ex) {}
			}
			else {
			}
		}

		Log.d("Angband","gameStart");

		running_plugin = Preferences.getActivePluginName();
	    String pluginPath = Preferences.getActivityFilesDirectory()
			+"/../lib/lib"+Preferences.getActivePluginName()+".so";

		// wait for and validate install processing (if any);
		//Log.d("Angband","run.waiting for install");
		Installer.waitForInstall();

		if (state.installState != StateManager.InstallState.Success) {
			//Log.d("Angband","run.sending fatal message");
			handler.sendEmptyMessage(AngbandDialog.Action.InstallFatalAlert.ordinal());
			onExitGame();
			return;
		}

		/* game is not running, so start it up */
		gameStart(
				  pluginPath, 
				  2, 
				  new String[]{
					  Preferences.getAngbandFilesDirectory(),
					  Preferences.getActiveProfile().getSaveFile()
				  }
		);
	}

	//this is called from native thread just before exiting
	public void onExitGame() {
		boolean local_restart = false;
			
		synchronized (game_thread_lock) {
			Log.d("Angband","onExitGame()");
			game_thread_running = false;
			game_fully_initialized = false;

			// if game exited normally, restart!
			local_restart 
				= game_restart 
				= ((!signal_game_exit || plugin_change) 
				   	&& state.installState == StateManager.InstallState.Success 
					&& !state.fatalError);
		}

		if	(local_restart) startBand();
	}

	public void startBand() {
		boolean already_running = false;
		boolean already_initialized = false;

		plugin_change = false;

		// sanity checks: thread must not already be running
		// and we must have a valid canvas to draw upon.
		synchronized(game_thread_lock) {
			already_running = game_thread_running;
			already_initialized = game_fully_initialized;	  
		}

		// don't bother restarting here, we are going down.
		if (state.fatalError) return;

		if (already_running && already_initialized) {

			/* this is an onResume event */

			if (running_plugin != null && 
				running_plugin.compareTo(Preferences.getActivePluginName())!=0) {
			
				/* plugin has been changed */

				Log.d("Angband","startBand.plugin changed");
				plugin_change = true;
				stopBand();
			}
			else {
				Log.d("Angband","startBand.game initialized - redrawing");
				redraw();
			}			
			return; // done.
		}

		synchronized(game_thread_lock) {
			if (game_thread_running) {
				Log.d("Angband","startBand.game already running, not initialized");
				redraw();
				return;
			}

			Installer installer = new Installer(state, handler);
			installer.checkInstall();

			synchronized (display_lock) {
				term.onXbandStarting();
			}

			clearKeyBuffer();
			if (Preferences.getActiveProfile().getPlugin()
				== Preferences.Plugin.Angband306.getId()
				&& Preferences.getAutoStartBorg()) {
				addToKeyBuffer(32); //space
				addToKeyBuffer(26); //ctrl-v
				addToKeyBuffer(122); //v
			}
			else if (Preferences.getSkipWelcome()) {
				addToKeyBuffer(32); //space
			}

			quit_key_seq = 0;
			game_thread_running = true;
			signal_game_exit = false;
		}

		Log.d("Angband","startBand().starting loader thread");

		thread = new Thread(this);
		thread.start();
	}

	public void saveBand() {
		Log.d("Angband","saveBand()");
		synchronized (game_thread_lock) {
			if (!game_thread_running) {
				Log.d("Angband","saveBand().no game running");
				return;
			}
			if (thread == null)  {
				Log.d("Angband","saveBand().no thread");
				return;
			}
			if (state.installState != StateManager.InstallState.Success) {
				Log.d("Angband","saveBand().install not finished or not successful");
				return;
			}
		}

		synchronized (keybuffer) {
			keybuffer.clear();
			keybuffer.offer(-1);

			if (wait) {
				keybuffer.notify();
			}
		}	
	}

	public void stopBand() {
		// signal keybuffer to send quit command to angband 
		// (this is when the user chooses quit or the app is pausing)

		Log.d("Angband","StopBand()");

		synchronized (game_thread_lock) {
			if (!game_thread_running) {
				Log.d("Angband","StopBand().no game running");
				return;
			}
			if (thread == null)  {
				Log.d("Angband","StopBand().no thread");
				return;
			}
		}

		signalGameExit();
	}

	public void redraw() {
		synchronized (display_lock) {
			term.onXbandStarting();
			term.redraw(state.charBuffer, state.colorBuffer);
		}
	}

	private void signalGameExit() {
		signal_game_exit = true;

		term.onXbandStopping();

		synchronized (keybuffer) {
			keybuffer.notify();
		}

		Log.d("Angband","signalGameExit.waiting on thread.join()");

		try {
			thread.join();
		} catch (Exception e) {
			Log.d("Angband",e.toString());
		}

		Log.d("Angband","signalGameExit.after waiting for thread.join()");
	}

	public int getSpecialKey() {
		if (signal_game_exit) {
			//Log.d("Angband", "getch.exit game sequence");
			switch((quit_key_seq++)%4) {
			case 0: return 24; // Esc
			case 1: return 0; 
			case 2: return 96; // Ctrl-X (Quit)
			case 3: return 0; 
			}
		}
		return -1;
	}

	public void fatalError(String msg) {
		state.fatalMessage = msg;
		state.fatalError = true;
		handler.sendMessage(handler.obtainMessage(AngbandDialog.Action.GameFatalAlert.ordinal(),0,0,msg));
	}

	public void warnError(String msg) {
		state.warnMessage = msg;
		state.warnError = true;
		handler.sendMessage(handler.obtainMessage(AngbandDialog.Action.GameWarnAlert.ordinal(),0,0,msg));
	}

	public int text(final int x, final int y, final int n, final byte a,
					final byte[] cp) {
		synchronized (display_lock) {
			byte c;
			int col = x;
			int row = y;
			for (int i = 0; i < n; i++) {
				c = cp[i];
				if (c > 19 && c < 128) {
					state.charBuffer[col][row] = (char)c;
					state.colorBuffer[col][row] = a;
					col++;
					if (col >= 80) {
						row++;
						col = 0;
					}
					if (row >= 24) {
						row = 23;
					}
				}
			}

			if (term != null) return term.text(x,y,n,a,cp);
			else return 0;
		}
	}
	public void wipe(final int row, final int col, final int n) {
		synchronized (display_lock) {
			state.charBuffer[col][row] = '\0';
			state.colorBuffer[col][row] = 0;

			if (term != null) term.wipe(row,col,n);
		}
	}
	public void clear() {
		synchronized (display_lock) {
			for(int r=0;r<Preferences.rows;r++) {
				for(int c=0;c<Preferences.cols;c++) {
					state.charBuffer[c][r] = '\0';
					state.colorBuffer[c][r] = 0;
				}
			}

			if (term != null) term.clear();
		}
	}
	public void noise() {
		synchronized (display_lock) {
			if (term != null) term.noise();
		}
	}
	public void refresh() {
		synchronized (display_lock) {
			if (term != null) term.refresh();
		}
	}
	public void move(final int col, final int row) {
		synchronized (display_lock) {
			if (term != null) term.move(col,row);
		}
	}
	public void setCursorXY(final int x, final int y) {
		//Log.d("Angband", "setCursor() x = " + x + ", y = " + y);
		state.cur_x = x;
		state.cur_y = y;
	}
	public void setCursorVisible(final int v) {
		if (v == 1) {
			state.cursor_visible = true;
		} else if (v == 0) {
			state.cursor_visible = false;
		}
	}
	public void postInvalidate() {
		synchronized (display_lock) {
			if (term != null) term.postInvalidate();
		}
	}
}
