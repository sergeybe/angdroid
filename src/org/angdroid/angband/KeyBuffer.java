package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
	
public class KeyBuffer {

	/* keyboard state */
	private Queue<Integer> keybuffer = new LinkedList<Integer>();
	private boolean wait = false;
	private int quit_key_seq = 0;
	private boolean signal_game_exit = false;
	private NativeWrapper nativew = null;

	public KeyBuffer(NativeWrapper n) {
		nativew = n;
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
}
