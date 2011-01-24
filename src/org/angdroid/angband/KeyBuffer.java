package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import android.util.Log;
	
public class KeyBuffer {

	/* keyboard state */
	private Queue<Integer> keybuffer = new LinkedList<Integer>();
	private boolean wait = false;
	private int quit_key_seq = 0;
	private boolean signal_game_exit = false;
	private NativeWrapper nativew = null;
	private StateManager state = null;

	static final int LEFT_BUTTON = 0x0200, MIDDLE_BUTTON = 0x201,
			RIGHT_BUTTON = 0x202, LEFT_DRAG = 0x203, LEFT_RELEASE = 0x206,
			CURSOR_UP = 0x209, CURSOR_DOWN = 0x20a, CURSOR_LEFT = 0x20b,
			CURSOR_RIGHT = 0x20c, MOD_CTRL = 0x1000, MOD_SHFT = 0x2000,
			MOD_NUM_KEYPAD = 0x4000;

	private boolean ctrl_mod = false;
	private boolean shift_mod = false;
	private boolean alt_mod = false;
	private boolean shift_down = false;
	private boolean alt_down = false;
	private boolean ctrl_down = false;
	private boolean ctrl_key_pressed = false;
	private boolean ctrl_key_overload = false;
	private boolean shift_key_pressed = false;
	private boolean alt_key_pressed = false;

	public KeyBuffer(StateManager state) {
		this.state = state;
		nativew = state.nativew;
		clear();
		if (Preferences.getAutoStartBorg()) {
			add(32); //space
			add(26); //ctrl-v
			add(122); //v
		}
		else if (Preferences.getSkipWelcome()) {
			add(32); //space
		}
		quit_key_seq = 0;
	}

	public void add(int key) {
		//Log.d("Angband", "KebBuffer.add:"+key);
		synchronized (keybuffer) {
			keybuffer.offer(key);
			wakeUp();
		}
	}

	public void addDirection(int key) {
		boolean rogueLike = (nativew.gameQueryInt(1,new String[]{"rl"})==1);
		boolean alwaysRun = Preferences.getAlwaysRun();

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

		if (alwaysRun && wait) {
			synchronized (keybuffer) {
				if (rogueLike) {
					key = Character.toUpperCase(key);
				}
				else {
					keybuffer.offer(46); // '.' command
				}
				keybuffer.offer(key);
				wakeUp();
			}
		} else {
			add(key);
		}
	}

	public void clear() {
		synchronized (keybuffer) {
			keybuffer.clear();
		}
	}

	public int get(int v) {
		int key = 0;
		synchronized (keybuffer) {

			int check = getSpecialKey();
			if (check >= 0) {
				key = check;
				// we have a key, so we're done.
			}
			else if (keybuffer.peek() != null) {
				//peek before wait -- fix issue #3 keybuffer loss
				key = keybuffer.poll();
				//Log.w("Angband", "process key = " + key);
			}		
			else if (v == 1) {
				// Wait for key press
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

				// return key after wait, if there is one
				if (keybuffer.peek() != null) {
					key = keybuffer.poll();
					//Log.w("Angband", "process key = " + key);
				}		
			}
		}
		return key;
	}

	public void signalSave() {
		//Log.d("Angband", "signalSave");
		synchronized (keybuffer) {
			keybuffer.clear();
			keybuffer.offer(-1);
			wakeUp();
		}	
	}

	public void wakeUp() {
		synchronized (keybuffer) {
			if (wait) {
				keybuffer.notify();
			}
		}
	}

	public void signalGameExit() {
		signal_game_exit = true;
		wakeUp();
	}

	public boolean getSignalGameExit() {
		return signal_game_exit;
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int key = 0;

		//Log.d("Angband", "onKeyDown("+keyCode+","+event+")");
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			key = state.getKeyUp();
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			key = state.getKeyDown();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			key = state.getKeyLeft();
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			key = state.getKeyRight();
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case 97: // emoticon key on Samsung Epic 4G (todo move to Preference)
			if (event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			ctrl_mod = !ctrl_mod;
			ctrl_key_pressed = !ctrl_mod; // double tap, turn off mod
			ctrl_down = true;
			if (ctrl_key_overload) key = '\r';
			else return true;
			break;
		case KeyEvent.KEYCODE_BACK:
			key = '`'; // escape key on back button
			break;
		case KeyEvent.KEYCODE_ENTER:
			key = '\r';
			break;
		case KeyEvent.KEYCODE_FOCUS:
		case KeyEvent.KEYCODE_SPACE:
			key = ' ';
			break;
		case KeyEvent.KEYCODE_DEL:
			key = '\b';
			break;
		case KeyEvent.KEYCODE_ALT_LEFT:
		case KeyEvent.KEYCODE_ALT_RIGHT:
			if (event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			alt_mod = !alt_mod;
			alt_key_pressed = !alt_mod; // double tap, turn off mod
			alt_down = true;
			key = -1;
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			if (event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			shift_mod = !shift_mod;
			shift_key_pressed = !shift_mod; // double tap, turn off mod
			shift_down = true;
			key = -1;
			break;

		/* todo: move this font-size mapping to prefs */
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (Preferences.getVolumeKeyFontSizing()) {
				nativew.increaseFontSize();
			}
			return true;
			//break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (Preferences.getVolumeKeyFontSizing()) {
				nativew.decreaseFontSize();
			}
			return true;
			//break;
		}

		if (key == 0) {
			int meta=0;
			if(alt_mod) {
				meta |= KeyEvent.META_ALT_ON;
				meta |= KeyEvent.META_ALT_LEFT_ON;
				alt_mod = alt_down; // if held down, mod is still active
			}
			if(shift_mod) {
				meta |= KeyEvent.META_SHIFT_ON;
				meta |= KeyEvent.META_SHIFT_LEFT_ON;
				shift_mod = shift_down; // if held down, mod is still active
			}
			key = event.getUnicodeChar(meta);
			if (key <= 127) {
				if (key >= 'a' && key <= 'z') {
				    if (ctrl_mod) {
					key = key - 'a' + 1;
				        ctrl_mod = ctrl_down; // if held down, mod is still active
				    }
				}
			}
		}

		ctrl_key_overload = false;

		if (key <= 0) {
			return nativew.onKeyDown(keyCode, event);
		}
		else {
			alt_key_pressed = alt_down;
			ctrl_key_pressed = ctrl_down;
			shift_key_pressed = shift_down;
		}

		if (event.isShiftPressed()) {
			key |= MOD_SHFT;
		}
		if (event.isAltPressed()) {
			key |= MOD_CTRL;
		}

		add(key);
		return true; 
		// two \r's in a row force pop up context menu
		// there may be other Android behaviors like this,
		// and I think its best to stop them here if we've 
		// already handled the key.
		// return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Log.d("Angband", "onKeyUp("+keyCode+","+event+")");
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case 97: // emoticon key on Samsung Epic 4G (todo move to Preference)
			ctrl_down = false;
			ctrl_mod = !ctrl_key_pressed; // turn off mod only if used at least once
			ctrl_key_overload = ctrl_mod;
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			shift_down = false;
			shift_mod = !shift_key_pressed; // turn off mod only if used at least once
			break;
		case KeyEvent.KEYCODE_ALT_LEFT:		
		case KeyEvent.KEYCODE_ALT_RIGHT:		
			alt_down = false;		
			alt_mod = !alt_key_pressed; // turn off mod only if used at least once		
			break;
		}
		return nativew.onKeyUp(keyCode, event);
	}
}
