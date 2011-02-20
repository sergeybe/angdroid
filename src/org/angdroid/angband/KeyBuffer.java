package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import android.util.Log;
import android.os.Handler;
import android.os.Message;

import org.angdroid.angband.KeyMapper.KeyAction;
	
public class KeyBuffer {

	/* keyboard state */
	private Queue<Integer> keybuffer = new LinkedList<Integer>();
	private Queue<Integer> keymacro = new LinkedList<Integer>();
	private boolean wait = false;
	private int quit_key_seq = 0;
	private boolean signal_game_exit = false;
	private NativeWrapper nativew = null;
	private StateManager state = null;

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
	private boolean eat_shift = false;

	private Handler handler = null;

	public KeyBuffer(StateManager state) {
		this.state = state;
		nativew = state.nativew;
		clear();
		if (Preferences.getAutoStartBorg()) {
			String magic = Plugins.getStartBorgSequence();
			for(int i = 0; i<magic.length(); i++) {
				keymacro.offer((int)(magic.charAt(i)));
			}
		}
		else if (Preferences.getSkipWelcome()) {
			add(32); //space
		}
		quit_key_seq = 0;
	}

	public void link(Handler h) {
		handler = h;		
	}

	public void add(int key) {
		//Log.d("Angband", "KebBuffer.add:"+key);
		synchronized (keybuffer) {
			ctrl_key_overload = false;

			if (key <= 127) {
				if (key >= 'a' && key <= 'z') {
					if (ctrl_mod) {
						key = key - 'a' + 1;
						ctrl_mod = ctrl_down; // if held down, mod is still active
					}
					else if (shift_mod) {
						if (!eat_shift) key = key - 'a'  + 'A';
						shift_mod = shift_down; // if held down, mod is still active
					}
				}
			}

			eat_shift = false;

			alt_key_pressed = alt_down;
			ctrl_key_pressed = ctrl_down;
			shift_key_pressed = shift_down;

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
			//case '5': key = ' '; break; // now configurable below
			case '6': key = 'l'; break;
			case '7': key = 'y'; break;
			case '8': key = 'k'; break;
			case '9': key = 'u'; break;
			default: break;
			}
		}
		
		if (key == '5') { // center tap
			KeyAction act = Preferences.getKeyMapper().getCenterScreenTapAction();

			performActionKeyDown(act, 0, null);
			performActionKeyUp(act);
		}
		else { // directional tap
			if (alwaysRun && !ctrl_mod) { // let ctrl influence directionals, even with alwaysRun on
				if (shift_mod) {  // shift temporarily overrides always run
					eat_shift = true;
				}
				else if (rogueLike) {
					key = Character.toUpperCase(key);
				}
				else {
					add(46); // '.' command
				}
			}
		
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
				// running a macro?
				if (keymacro.peek() != null) {
					key = keymacro.poll();
				}
				else { // otherwise wait for key press
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

	private KeyMap getKeyMapFromKeyCode(int keyCode, KeyEvent event)
	{
		int meta=0;
		if(alt_mod) {
			meta |= KeyEvent.META_ALT_ON;
			meta |= KeyEvent.META_ALT_LEFT_ON;
			alt_mod = alt_down; // if held down, mod is still active
		}
		int ch = 0;
		boolean char_mod = false;
		if (event != null) {
			ch = event.getUnicodeChar(meta);
			char_mod = (ch > 32 && ch < 127);
		}
		int key_code = char_mod ? ch : keyCode;

		String keyAssign = KeyMap.stringValue(key_code, alt_mod, char_mod);		
		KeyMap map = Preferences.getKeyMapper().findKeyMapByAssign(keyAssign);
		return map;
	}

	private boolean performActionKeyDown(KeyAction act, int character, KeyEvent event) {
		boolean res = true;

		if (act == KeyAction.CtrlKey) {
			if (event != null && event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			ctrl_mod = !ctrl_mod;
			ctrl_key_pressed = !ctrl_mod; // double tap, turn off mod
			ctrl_down = true;
			if (ctrl_key_overload) {
				// ctrl double tap, translate into appropriate action
				act = Preferences.getKeyMapper().getCtrlDoubleTapAction();
			}
		}

   		switch(act){
		case CharacterKey:
			add(character);
			break;
		case EscKey:
			add('`');
			break;
		case Space:
			add(' ');
			break;
		case Period:
			add('.');
			break;
		case EnterKey:
			add('\r');
			break;
		case ArrowDownKey:
			add(state.getKeyDown());
			break;
		case ArrowUpKey:
			add(state.getKeyUp());
			break;
		case ArrowLeftKey:
			add(state.getKeyLeft());
			break;
		case ArrowRightKey:
			add(state.getKeyRight());
			break;
		case AltKey:
			if (event != null && event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			alt_mod = !alt_mod;
			alt_key_pressed = !alt_mod; // double tap, turn off mod
			alt_down = true;
			break;
		case ShiftKey:
			if (event != null && event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			shift_mod = !shift_mod;
			shift_key_pressed = !shift_mod; // double tap, turn off mod
			shift_down = true;
			break;
		case ZoomIn:
			nativew.increaseFontSize();
			break;
		case ZoomOut:
			nativew.decreaseFontSize();
			break;
		case CtrlKey:
			//handled above
			break;
		case VirtualKeyboard:
			// handled on keyup
			break;
		default:
			res = false; // let the OS handle the key
			break;
		}
		return res;
	}

	private boolean performActionKeyUp(KeyAction act) {
		boolean res = true; // handled the key

		switch(act){
		case AltKey:
			alt_down = false;		
			alt_mod = !alt_key_pressed; // turn off mod only if used at least once		
			break;
		case CtrlKey:
			ctrl_down = false;
			ctrl_mod = !ctrl_key_pressed; // turn off mod only if used at least once
			ctrl_key_overload = ctrl_mod;
			break;
		case ShiftKey:
			shift_down = false;
			shift_mod = !shift_key_pressed; // turn off mod only if used at least once
			break;
		case VirtualKeyboard:
			handler.sendEmptyMessage(AngbandDialog.Action.ToggleKeyboard.ordinal());
			break;

		// these are handled on keydown
		case ZoomIn:
		case ZoomOut:
		case None:
		case CharacterKey:
			break;

		default:
			res = false;  // let the OS handle the key
			break;
		}
		return res;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Log.d("Angband", "onKeyDown("+keyCode+","+event+")");

		KeyMap map = getKeyMapFromKeyCode(keyCode, event);
		if (map == null)
			return false;
		else 
			return performActionKeyDown(map.getKeyAction(), map.getCharacter(), event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Log.d("Angband", "onKeyUp("+keyCode+","+event+")");

		KeyMap map =  getKeyMapFromKeyCode(keyCode, event);
		if (map == null)
			return false;
		else 
			return performActionKeyUp(map.getKeyAction());
	}
}
