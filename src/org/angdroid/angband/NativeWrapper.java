package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
	
public class NativeWrapper {
	// Load native library
	static {
		System.loadLibrary("loader");
	}

	private TermView term = null;
	private Handler	handler = null;
	private StateManager state = null;

	private String display_lock = "lock";

	// Call native methods from library
	native void gameStart(String pluginPath, int argc, String[] argv);
	native int gameQueryRedraw(int x1, int y1, int x2, int y2);
	native int gameQueryInt(int argc, String[] argv);
	native String gameQueryString(int argc, String[] argv);

	public NativeWrapper(StateManager s) {
		state = s;
	}

	public void link(TermView t, Handler h) {
		synchronized (display_lock) {
			term = t;
			handler = h;
		}
	}

	int getch(final int v) {
		state.gameThread.setFullyInitialized();
		return state.keyBuffer.get(v);
	}

	//this is called from native thread just before exiting
	public void onExitGame() {
		//Log.d("Angband","NativeWrapper.onExitGame.sendEmptyMessage.OnGameExiting");
		handler.sendEmptyMessage(AngbandDialog.Action.OnGameExiting.ordinal());
	}

	public void redraw() {
		synchronized (display_lock) {
			term.onXbandStarting();
			term.redraw(state.charBuffer, state.colorBuffer);
		}
	}

	public void onGameStarting() {
		synchronized (display_lock) {
			term.onXbandStarting();
		}
	}

	public void onGameStopping() {
		term.onXbandStopping();
	}

	public void clearKeyBuffer() {
		state.keyBuffer.clear();
	}

	public void fatalError(String msg) {
		synchronized (display_lock) {
			state.fatalMessage = msg;
			state.fatalError = true;
			handler.sendMessage(handler.obtainMessage(AngbandDialog.Action.GameFatalAlert.ordinal(),0,0,msg));
		}
	}

	public void warnError(String msg) {
		synchronized (display_lock) {
			state.warnMessage = msg;
			state.warnError = true;
			handler.sendMessage(handler.obtainMessage(AngbandDialog.Action.GameWarnAlert.ordinal(),0,0,msg));
		}
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
