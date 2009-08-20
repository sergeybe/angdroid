/*
 * File: TermView.java
 * Purpose: Terminal-base view for Android application
 *
 * Copyright (c) 2009 David Barr, Sergey Belinsky
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

package org.angdroid.angband2;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

	static final int ARROW_DOWN = 0x8A;
	static final int ARROW_LEFT = 0x8B;
	static final int ARROW_RIGHT = 0x8C;
	static final int ARROW_UP = 0x8D;

	private boolean ctrl_mod = false;
	private boolean wait = false;

	Queue<Integer> keybuffer = new LinkedList<Integer>();

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
	0xFFFF00FF,/* TERM_VIOLET */
	0xFFFFFF00,/* TERM_YELLOW */
	0xFFFF4040,/* TERM_L_RED */
	0xFF00FF00,/* TERM_L_GREEN */
	0xFF00FFFF,/* TERM_L_BLUE */
	0xFFC08040 /* TERM_L_UMBER */
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

	private int char_height = 12;
	private int char_width = 6;

	private Vibrator vibrator;
	private boolean vibrate;

	private Thread thread;

	boolean game_running = false;
	private boolean cursor_visible;

	// Load native library
	static {
		System.loadLibrary("angband");
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
		tf = Typeface.createFromAsset(getResources().getAssets(), "6x12.ttf");
		fore = new Paint();
		fore.setTypeface(tf);
		fore.setTextSize(12);
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

		thread = new Thread(this);

		setFocusableInTouchMode(true);
	}

	protected void onDraw(Canvas canvas) {
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
		int x = cur_x * char_width;
		int y = (cur_y + 1) * char_height;

		if (cursor_visible) {
			canvas.drawRect(x - 1, y - char_height + 2, x + char_width - 1,
					y + 2, cursor);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("Angband", "onMeasure");
		setMeasuredDimension(480, 320);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Log.d("Angband", "onSizeChanged");
		super.onSizeChanged(w, h, oldw, oldh);

		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

		canvas = new Canvas(bitmap);

		game_running = true;

		thread.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int key = 0;

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
			if (!ctrl_mod) {
				ctrl_mod = !ctrl_mod;
				return true;
			}
			ctrl_mod = false;
		case KeyEvent.KEYCODE_BACK:
			key = '`'; // escape key on double-dpad_center
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
		}
		if (key == 0) {
			// key = event.getDisplayLabel();
			key = event.getUnicodeChar();
			if (key <= 127) {
				if (key >= 'a' && key <= 'z' && ctrl_mod) {
					key = key - 'a' + 1;
					ctrl_mod = false;
				}
			}
		}

		if (key == 0) {
			return super.onKeyDown(keyCode, event);
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
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			int x = (int) event.getX();
			int y = (int) event.getY();

			int r, c;
			c = (x * 3) / getWidth();
			r = (y * 3) / getHeight();

			addToKeyBuffer((2 - r) * 3 + c + '1');

			return true;
		}
		return false;
	}

	public void addToKeyBuffer(int key) {
		synchronized (keybuffer) {

			Log.v("Angband", "key = " + key);
			keybuffer.offer(key);

			if (wait) {
				Log.d("Angband", "Wake up!!!");
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

		if (v == 1 && game_running) {
			// Wait key press
			try {
				Log.d("Angband", "Wait keypress BEFORE");
				synchronized (keybuffer) {
					wait = true;
					keybuffer.clear();
					keybuffer.wait();
					wait = false;
				}
				Log.d("Angband", "Wait keypress AFTER");
			} catch (Exception e) {
				Log.d("Angband", "The getch() wait exception" + e);
			}
		}

		if (keybuffer.peek() != null) {
			key = keybuffer.poll();
			Log.w("Angband", "Key=" + key);
			return key;
		}

		return 0;
	}

	public void refresh() {
		postInvalidate();
	}

	public void clear() {
		if (canvas != null) {
			canvas.drawPaint(back);
		}
	}

	public void noise() {
		if (vibrate) {
			vibrator.vibrate(50);
		}
	}

	public int text(final int x, final int y, final int n, final byte a,
			final byte[] cp) {

		move(x, y);

		fore.setColor(colors[a]);

		for (int i = 0; i < n; i++) {
			if (cp[i] > 19 && cp[i] < 128) {
				putchar((char) cp[i]);
			}
		}

		return 0;
	}

	public void wipe(final int row, final int col, final int n) {
		synchronized (bitmap) {
			float x = col * char_width;
			float y = (row + 1) * char_height;
			canvas.drawRect(x, y - char_height + 2, x + char_width * n, y + 2,
					back);
		}
	}

	public void move(final int col, final int row) {
		synchronized (bitmap) {
			this.col = col;
			this.row = row;
		}
	}

	public void putchar(final char c) {
		synchronized (bitmap) {

			float x = col * char_width;
			float y = (row + 1) * char_height;
			String str = c + "";

			canvas
					.drawRect(x, y - char_height + 2, x + char_width, y + 2,
							back);
			canvas.drawText(str, x, y, fore);

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
		Log.d("Angband", "setCursor() x =" + x + " y=" + y);
		synchronized (bitmap) {
			this.cur_x = x;
			this.cur_y = y;
		}
	}

	public void setCursorVisible(final int v) {
		if (v == 1) {
			cursor_visible = true;
		} else if (v == 0) {
			cursor_visible = false;
		}
	}

	// Call native method from library
	native void initGame();

	native void playGame();

	native void finishGame();

	public void onResume() {
		game_running = true;
	}

	public void onPause() {

		game_running = false;

		synchronized (keybuffer) {

			keybuffer.clear();
			keybuffer.offer(-1);

			if (wait) {
				keybuffer.notify();
			}
		}
	}

	public void run() {
		initGame();
		playGame();
		finishGame();
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public boolean getVibrate() {
		return vibrate;
	}
}