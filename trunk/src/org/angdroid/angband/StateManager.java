package org.angdroid.angband;

import android.os.Handler;
import java.util.Map;
import java.util.HashMap;

public class StateManager {
	/* screen state */
	public Map<Integer,TermWindow> termwins = null;
	public TermWindow stdscr = null;

	/* alert dialog state */
	public boolean fatalError = false;
	public boolean warnError = false;
	public String fatalMessage = "";
	public String warnMessage = "";
	public Plugins.Plugin currentPlugin;

	/* progress dialog state */
	public static String progress_lock = "lock";

	/* keybuffer */
	public KeyBuffer keyBuffer = null;

	/* native angband library interface */
	public NativeWrapper nativew = null;

	/* game thread */
	public GameThread gameThread = null;

	/* installer state */
	public enum InstallState {
		Unknown
			,MediaNotReady
			,InProgress
			,Success
			,Failure;
		public static InstallState convert(int value)
		{
			return InstallState.class.getEnumConstants()[value];
		}
		public static boolean isError(InstallState s) {
			return (s == MediaNotReady || s == Failure);
		}
    };
	public static InstallState installState = InstallState.Unknown;

	StateManager() {
		termwins = new HashMap<Integer,TermWindow>();
		stdscr = new TermWindow(0,0,0,0);
		termwins.put(0,stdscr);

		nativew = new NativeWrapper(this);
		gameThread = new GameThread(this, nativew);
	}

	public void link(TermView t, Handler h) {
		nativew.link(t, h);
		gameThread.link(h);
		if (keyBuffer != null)
			keyBuffer.link(h);
	}

	public TermWindow getWin(int handle) {
		if (termwins.containsKey(handle))
			return termwins.get(handle);
		else
			return null;
	}

	public String getInstallError() {
		String errMsg = "Error: an unknown error occurred, cannot continue.";
		switch(installState) {
		case MediaNotReady:
			errMsg = "Error: external storage card not found, cannot continue.";
			break;
		case Failure:
			errMsg = "Error: failed to write and verify files to external storage, cannot continue.";
			break;
		}
		return errMsg;
	}

	public String getFatalError() {
		return "Angband quit with the following error: "+fatalMessage;
	}

	public String getWarnError() {
		return "Angband sent the following warning: "+warnMessage;
	}

	public int getKeyUp() {
		return Plugins.getKeyUp(currentPlugin);
	}
	public int getKeyDown() {
	    return Plugins.getKeyDown(currentPlugin);
	}
	public int getKeyLeft() {
		return Plugins.getKeyLeft(currentPlugin);
	}
	public int getKeyRight() {
		return Plugins.getKeyRight(currentPlugin);
	}
}
