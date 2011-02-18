package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import android.util.Log;
	
public class NativeWrapper {
	// Load native library
	static {
		System.loadLibrary(Plugins.LoaderLib);
	}

	private TermView term = null;
	private Handler	handler = null;
	private StateManager state = null;

	private String display_lock = "lock";

	// Call native methods from library
	native void gameStart(String pluginPath, int argc, String[] argv);
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
		int key = state.keyBuffer.get(v);

		/* useful when debugging borg autostart
		if (key != 0) {
			try{
				Thread.sleep(1000);
			}catch(Exception ex){}
		}
		else {
			refresh();
		}
		*/
		return key;
	}

	//this is called from native thread just before exiting
	public void onGameExit() {
		handler.sendEmptyMessage(AngbandDialog.Action.OnGameExit.ordinal());
	}

	public boolean onGameStart() {
		synchronized (display_lock) {
			return term.onGameStart();
		}
	}

	public void increaseFontSize() {
		synchronized (display_lock) {
			term.increaseFontSize();
			resize();
		}
	}

	public void decreaseFontSize() {
		synchronized (display_lock) {
			term.decreaseFontSize();
			resize();
		}
	}

	public void flushinp() {
		state.keyBuffer.clear();
	}

	public void fatal(String msg) {
		synchronized (display_lock) {
			state.fatalMessage = msg;
			state.fatalError = true;
			handler.sendMessage(handler.obtainMessage(AngbandDialog.Action.GameFatalAlert.ordinal(),0,0,msg));
		}
	}

	public void warn(String msg) {
		synchronized (display_lock) {
			state.warnMessage = msg;
			state.warnError = true;
			handler.sendMessage(handler.obtainMessage(AngbandDialog.Action.GameWarnAlert.ordinal(),0,0,msg));
		}
	}

	public void resize() {
		synchronized (display_lock) {
			term.onGameStart();
			refresh(state.stdscr, true);
		}
	}

	public void refresh() {
		synchronized (display_lock) {
			refresh(state.stdscr, false);
		}
	}

	private void refresh(TermWindow w, boolean resize) {
		synchronized(display_lock) {
			for(int r = 0; r<w.rows; r++) {
				for(int c = 0; c<w.cols; c++) {
					TermWindow.TermPoint p = w.buffer[r][c];
					if (p.isDirty || resize) {
						term.drawPoint(r, c, p.Char, p.Color);
						p.isDirty = false;
					}
				}
			}

			term.postInvalidate();
		
			if (resize)
				term.onScroll(null,null,0,0);  // sanitize scroll position
		}
	}

	public void addnstr(final int n, final byte[] cp) {
		synchronized (display_lock) {
			state.stdscr.addnstr(n, cp);
		}
	}

	public int mvinch(final int r, final int c) {
		synchronized (display_lock) {
			return state.stdscr.mvinch(r, c);
		}
	}

	public int attrget(final int r, final int c) {
		synchronized (display_lock) {
			return state.stdscr.attrget(r, c);
		}
	}

	public void attrset(final int a) {
		synchronized (display_lock) {
			state.stdscr.attrset(a);
		}
	}

	public void hline(final byte c, final int n) {
		synchronized (display_lock) {
			state.stdscr.hline((char)c, n);
		}
	}

	public void clear() {
		synchronized (display_lock) {
			state.stdscr.clear();
		}
	}

	public void clrtoeol() {
		synchronized (display_lock) {
			state.stdscr.clrtoeol();
		}
	}

	public void clrtobot() {
		synchronized (display_lock) {
			state.stdscr.clrtobot();
		}
	}

	public void noise() {
		synchronized (display_lock) {
			if (term != null) term.noise();
		}
	}

	public void move(final int y, final int x) {
		synchronized (display_lock) {
			state.stdscr.move(y,x);
		}		
	}

	public void curs_set(final int v) {
		if (v == 1) {
			state.stdscr.cursor_visible = true;
		} else if (v == 0) {
			state.stdscr.cursor_visible = false;
		}
	}

	public void touchwin (final int w) {
		synchronized (display_lock) {
			state.getWin(w).touch();
		}		
	}

	public int newwin (final int rows, final int cols, 
						final int begin_y, final int begin_x) {
		synchronized (display_lock) {
			if (state.termwins.size()>1)
				return 1; //hack!
			else {
				TermWindow w = new TermWindow(rows,cols,begin_y,begin_x);
				int key = state.termwins.size();
				state.termwins.put(key,w);
				return key;
			}
		}		
	}

	public void overwrite (final int wsrc, final int wdst) {
		synchronized (display_lock) {
			state.getWin(wdst).overwrite(state.getWin(wsrc));
		}		
	}

	int getcury() {
		return state.stdscr.getcury();
	}

	int getcurx() {
		return state.stdscr.getcurx();
	}
}
