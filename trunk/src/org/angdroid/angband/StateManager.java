package org.angdroid.angband;

public class StateManager {
	/* screen state */
	public char[][] charBuffer = null; 
	public byte[][] colorBuffer = null; 
	public boolean cursor_visible;
	public int cur_x = 0;
	public int cur_y = 0;

	/* alert dialog state */
	public boolean fatalError = false;
	public boolean warnError = false;
	public String fatalMessage = "";
	public String warnMessage = "";

	/* progress dialog state */
	public static String progress_lock = "lock";

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
		colorBuffer = new byte[Preferences.cols][Preferences.rows];
		charBuffer = new char[Preferences.cols][Preferences.rows];
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
}
