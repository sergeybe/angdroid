/*
 * File: TermView.java
 * Purpose: Terminal-base view for Android application
 *
 * Copyright (c) 2010 David Barr, Sergey Belinsky
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the "Angband licence":
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 */

package org.angdroid.angband;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class TermView extends View implements Runnable {

	static final int LEFT_BUTTON = 0x0200, MIDDLE_BUTTON = 0x201,
			RIGHT_BUTTON = 0x202, LEFT_DRAG = 0x203, LEFT_RELEASE = 0x206,
			CURSOR_UP = 0x209, CURSOR_DOWN = 0x20a, CURSOR_LEFT = 0x20b,
			CURSOR_RIGHT = 0x20c, MOD_CTRL = 0x1000, MOD_SHFT = 0x2000,
			MOD_NUM_KEYPAD = 0x4000;
	// MIDDLE_DRAG = 0x204, RIGHT_DRAG = 0x205,

	static int ARROW_DOWN = 0x8A;
	static int ARROW_LEFT = 0x8B;
	static int ARROW_RIGHT = 0x8C;
	static int ARROW_UP = 0x8D;

	private boolean ctrl_mod = false;
	private boolean shift_mod = false;
	private boolean alt_mod = false;
	private boolean shift_down = false;
	private boolean alt_down = false;
	private boolean ctrl_down = false;
	private boolean ctrl_key_pressed = false;
	private boolean shift_key_pressed = false;
	private boolean alt_key_pressed = false;

	private boolean wait = false;
	private int quit_key_seq = 0;

	private Queue<Integer> keybuffer = new LinkedList<Integer>();

	// Default curses colors
	final int colors[] = { /* */
	0xFF000000,/* TERM_DARK */
	0xFFFFFFFF,/* TERM_WHITE */
	0xFF808080,/* TERM_SLATE */
	0xFFFF8000,/* TERM_ORANGE */
	0xFFC00000,/* TERM_RED */
	0xFF008040,/* TERM_GREEN */
	0xFF0040FF,/* TERM_BLUE */
	0xFF804000,/* TERM_UMBER */
	0xFF606060,/* TERM_L_DARK */
	0xFFC0C0C0,/* TERM_L_WHITE */
	0xFFDD00FF,/* TERM_L_PURPLE */
	0xFFCCCC00,/* TERM_YELLOW */
	0xFFFF4040,/* TERM_L_RED */
	0xFF00FF00,/* TERM_L_GREEN */
	0xFF00FFFF,/* TERM_L_BLUE */
	0xFFC08040,/* TERM_L_UMBER */

	/*
		max colors has increased from 16 to 28
		as of rev 1862
		http://trac.rephial.org/changeset/1862/trunk/src/z-term.h

		todo: fine tune RGB values using a test display function
	*/

	0xFF9900AA,/* TERM_PURPLE */
	0xFFAA00CC,/* TERM_VIOLET */
	0xFF0099BB,/* TERM_TEAL */
	0xFFCCCC22,/* TERM_MUD */
	0xFFFFFF00,/* TERM_L_YELLOW */
	0xFFCC00DD,/* TERM_MAGENTA */
	0xFF00CCEE,/* TERM_L_TEAL */
	0xFFBB00DD,/* TERM_L_VIOLET */
	0xFFFF20FF,/* TERM_L_PINK */
	0xFFAAAA66,/* TERM_MUSTARD */
	0xFF101099,/* TERM_BLUE_SLATE */
	0xFF0010FF /* TERM_DEEP_L_BLUE */
	};

	Typeface tf;
	Bitmap bitmap;
	Canvas canvas;
	Paint fore;
	Paint back;
	Paint cursor;

	int row = 0;
	int col = 0;
	int width = 80;
	int height = 24;

	int cur_x = 0;
	int cur_y = 0;

	private int screen_height = 320;
	private int screen_width = 480;
	private int char_height = 12;
	private int char_width = 6;
	private int font_text_size = 12;
	private int font_width_scrunch = 0;
	private int font_height_scrunch = 0;

	private Vibrator vibrator;
	private boolean vibrate;
	private boolean always_run = true;
	private boolean cursor_visible;

	private static boolean game_thread_running = false;
	private static String game_thread_lock = "lock";

	private Thread thread = null;
	boolean signal_game_exit = false;
	boolean game_restart = false;

	// Load native library
	static {
		System.loadLibrary("loader");
	}

	public TermView(Context context) {
		super(context);
		initTermView(context);
	}

	public TermView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTermView(context);
	}

	protected void initTermView(Context context) {
		fore = new Paint();
		fore.setTextAlign(Paint.Align.LEFT);
		fore.setColor(colors[1]);

		back = new Paint();
		back.setColor(colors[0]);

		cursor = new Paint();
		cursor.setColor(Color.GREEN);
		cursor.setStyle(Paint.Style.STROKE);
		cursor.setStrokeWidth(0);

		vibrator = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		setFocusableInTouchMode(true);
	}

	protected void onDraw(Canvas canvas) {
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
		int x = cur_x * (char_width) + font_width_scrunch;
		int y = (cur_y + 1) * char_height;

		// due to font "scrunch", cursor is sometimes a bit too big
		int cl = Math.max(x-1,0);
		int cr = Math.min(x+char_width-1,screen_width-1);
		int ct = Math.max(y-char_height+2,0);
		int cb = Math.min(y+2,screen_height-1);

		if (cursor_visible) {
			canvas.drawRect(cl, ct, cr, cb, cursor);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		screen_width = getMeasuredWidth();
		screen_height = getMeasuredHeight();
		setMeasuredDimension(screen_width,screen_height);

		char_height = screen_height/height;
		char_width = screen_width/width;

		if (tf == null) {
			if (char_width <= 6) {
				char_height -= 1;
				font_height_scrunch = 0;
				font_width_scrunch = 0;
				tf = Typeface.createFromAsset(getResources().getAssets(), "6x12.ttf");
			}
			else {
				font_height_scrunch = char_height/10;
				font_width_scrunch = char_width/10;
				tf = Typeface.createFromAsset(getResources().getAssets(), "VeraMoBd.ttf"); 
			}
		}

		font_text_size = char_height-font_height_scrunch;
		fore.setTypeface(tf);

		fore.setTextSize(font_text_size);
		//fore.setAntiAlias(false);  // these don't seem to have any effect, why?
		//fore.setDither(false);
		//fore.setSubpixelText(false);

		//Log.d("Angband", "onMeasure "+screen_width+","+screen_height
		//	+","+char_height+","+char_width+","+font_text_size
		//	+","+font_height_scrunch);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// fix for blank splash/title screen at startup:
		// for some reason we occasionally get a portrait orientation
		// size change which creates a new canvas by mistake.
		if (h > w) {
			Log.d("Angband", "onSizeChanged.rejected");
			return; // ignore portrait until we actually implement it!
		}
		else {
			Log.d("Angband", "onSizeChanged");
		}
		 
		synchronized (game_thread_lock) {
			if (game_thread_running) return;

			bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
			canvas = new Canvas(bitmap);

			/*
			canvas.setDrawFilter(new PaintFlagsDrawFilter(
				Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG,0
			)); // this seems to have no effect, why?
			*/
		}

		startAngband();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int key = 0;

		//Log.d("Angband", "onKeyDown("+keyCode+","+event+")");
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			key = ARROW_UP;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			key = ARROW_DOWN;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			key = ARROW_LEFT;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			key = ARROW_RIGHT;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case 97: // emoticon key on Samsung Epic 4G (todo move to Preference)
			if (event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			ctrl_mod = true;
			ctrl_down = true;
			ctrl_key_pressed = false;
			return true;
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
			alt_mod = true;
			alt_down = true;
			alt_key_pressed = false;
			key = -1;
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			if (event.getRepeatCount()>0) return true; // ignore repeat from modifiers
			shift_mod = true;
			shift_down = true;
			shift_key_pressed = false;
			key = -1;
			break;
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

		if (key <= 0) {
			return super.onKeyDown(keyCode, event);
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

		addToKeyBuffer(key);

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Log.d("Angband", "onKeyUp("+keyCode+","+event+")");
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
			ctrl_down = false;
			ctrl_mod = !ctrl_key_pressed; // turn off mod only if used at least once

			// I think the overloaded control key + menu feature is annoying
			// todo: move to preference
			if(!ctrl_key_pressed) {
				addToKeyBuffer('\r');
			}
			break;
		case 97: // emoticon key on Samsung Epic 4G (todo move to Preference)
		    	ctrl_down = false;
			ctrl_mod = !ctrl_key_pressed; // turn off mod only if used at least once
			break;
		case KeyEvent.KEYCODE_ALT_LEFT:
		case KeyEvent.KEYCODE_ALT_RIGHT:
			alt_down = false;
			alt_mod = !alt_key_pressed; // turn off mod only if used at least once
			break;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			shift_down = false;
			shift_mod = !shift_key_pressed; // turn off mod only if used at least once
			break;
		}
		return super.onKeyUp(keyCode, event);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!Preferences.getEnableTouch()) return false;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			int x = (int) event.getX();
			int y = (int) event.getY();

			int r, c;
			c = (x * 3) / getWidth();
			r = (y * 3) / getHeight();
			
			boolean rogueLike = (gameQueryInt(1,new String[]{"isRogueLikeEnabled"})==1);
			int key = (2 - r) * 3 + c + '1';

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
			return true;
		}
		return false;
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

	int getch(final int v) {

		Integer key = null;

		//Log.d("Angband", "getch");
		synchronized (keybuffer) {

			if (signal_game_exit) {
				//Log.d("Angband", "getch.exit game sequence");
				switch((quit_key_seq++)%4) {
				case 0: return 24; // Esc
				case 1: return 0; 
				case 2: return 96; // Ctrl-X (Quit)
				case 3: return 0; 
				}
			}

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

	public void refresh() {
		synchronized (game_thread_lock) {
			postInvalidate();
		}
	}

	public void clear() {
		synchronized (game_thread_lock) {
			if (canvas != null) {
				canvas.drawPaint(back);
			}
		}
	}

	public void noise() {
		synchronized (game_thread_lock) {
			if (vibrate) {
				vibrator.vibrate(50);
			}
		}
	}

	public int text(final int x, final int y, final int n, final byte a,
			final byte[] cp) {

		move(x, y);

		/* handy for debugging
		StringBuffer result = new StringBuffer();
		for (int i=0; i < n; i++) {
			result.append(new String(new byte[] {cp[i]}));
		}
		Log.d("Angband","text"+result.toString());
		*/

		fore.setColor(colors[a]);

		for (int i = 0; i < n; i++) {
			if (cp[i] > 19 && cp[i] < 128) {
				putchar((char) cp[i]);
			}
		}

		return 0;
	}

	public void wipe(final int row, final int col, final int n) {
		synchronized (game_thread_lock) {
			float x = col * char_width;
			float y = (row + 1) * char_height;
			canvas.drawRect(
				x, 
				y - char_height + 2, 
				x + (char_width) * n + font_width_scrunch, 
				y + 2,
				back
			);
		}
	}

	public void move(final int col, final int row) {
		synchronized (game_thread_lock) {
			this.col = col;
			this.row = row;
		}
	}

	public void putchar(final char c) {
		wipe(row, col, 1);
		synchronized (game_thread_lock) {

			float x = col * (char_width);
			float y = (row + 1) * char_height;
			String str = c + "";

			canvas.drawText (
				str,
				x, 
				y-font_height_scrunch, 
				fore
			);

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

	public void setCursorXY(final int x, final int y) {
		//Log.d("Angband", "setCursor() x = " + x + ", y = " + y);
		synchronized (game_thread_lock) {
			this.cur_x = x;
			this.cur_y = y;
		}
	}

	public void setCursorVisible(final int v) {
		synchronized (game_thread_lock) {
			if (v == 1) {
				cursor_visible = true;
			} else if (v == 0) {
				cursor_visible = false;
			}
		}
	}

	public void onResume() {
		always_run = Preferences.getAlwaysRun();
		vibrate = Preferences.getVibrate();

		Log.d("Angband","Termview.onResume()");
		startAngband();
	}

	public void onPause() {
		Log.d("Angband","Termview.onPause()");
		// this is the only guaranteed safe place to save state 
		// according to SDK docs
		quitAngband();
	}

	public void startAngband() {
		//Log.d("Angband","startAngband()");

		// sanity checks: thread must not already be running
		// and we must have a valid canvas to draw upon.
		synchronized(game_thread_lock) {
			if (game_thread_running || canvas == null) return;

			game_thread_running = true;
			signal_game_exit = false;
			quit_key_seq = 0;
		}

		Log.d("Angband","startAngband().reallyStarting");

		clearKeyBuffer();

		if (Preferences.getActivePluginName().compareTo("angband")==0) {
			ARROW_DOWN = 0x8A;
			ARROW_LEFT = 0x8B;
			ARROW_RIGHT = 0x8C;
			ARROW_UP = 0x8D;
		}
		else {
			ARROW_DOWN = '2';
			ARROW_LEFT = '4';
			ARROW_RIGHT = '6';
			ARROW_UP = '8';
		}

		thread = new Thread(this);
		thread.start();
	}

	public void quitAngband() {
		// signal keybuffer to send quit command to angband 
		// (this is when the user chooses quit or the app is pausing)

		Log.d("Angband","quitAngband()");

		synchronized (game_thread_lock) {
			if (!game_thread_running || thread == null) 
				return;
		}

		signal_game_exit = true;

		Log.d("Angband","waiting on thread.join()");

		synchronized (keybuffer) {
			keybuffer.notify();
		}

		try {
			thread.join();
		} catch (Exception e) {
			Log.d("Angband",e.toString());
		}

		Log.d("Angband","after waiting for thread.join()");
	}

	// Call native methods from library
	native void gameStart(String pluginPath, int argc, String[] argv);
	native int gameQueryInt(int argc, String[] argv);
	native String gameQueryString(int argc, String[] argv);

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
		}

	    String pluginPath = Preferences.getActivityFilesDirectory()
			+"/../lib/lib"+Preferences.getActivePluginName()+".so";
		Log.d("Angband","gameStart");
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

			// if game exited normally, restart!
			local_restart = game_restart = !signal_game_exit;
		}

		if	(local_restart) startAngband();
	}
}
