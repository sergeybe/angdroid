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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.GestureDetector.OnGestureListener;

public class TermView extends View implements OnGestureListener {

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
	private boolean ctrl_key_overload = false;
	private boolean shift_key_pressed = false;
	private boolean alt_key_pressed = false;
	private boolean new_instance = true;

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

	Typeface tfStd;
	Typeface tfTiny;
	Bitmap bitmap;
	Canvas canvas;
	Paint fore;
	Paint back;
	Paint cursor;

	int row = 0;
	int col = 0;
	int rows = 24;
	int cols = 80;

	public int canvas_width = 0;
	public int canvas_height = 0;

	private int char_height = 0;
	private int char_width = 0;
	private int font_text_size = 0;

	private Vibrator vibrator;
	private boolean vibrate;

	private GestureDetector gesture;

	private boolean pausing = false;
	private boolean resuming = false;
	private AngbandActivity aa = null;

	public TermView(Context context) {
		super(context);
		initTermView(context);
		aa = (AngbandActivity)context;
	}

	public TermView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTermView(context);
		aa = (AngbandActivity)context;
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
		gesture = new GestureDetector(context, this);
	}

	protected void onDraw(Canvas canvas) {
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, null);

			int x = AngbandActivity.xb.cur_x * (char_width);
			int y = (AngbandActivity.xb.cur_y + 1) * char_height;

			// due to font "scrunch", cursor is sometimes a bit too big
			int cl = Math.max(x,0);
			int cr = Math.min(x+char_width,canvas_width-1);
			int ct = Math.max(y-char_height,0);
			int cb = Math.min(y,canvas_height-1);

			if (AngbandActivity.xb.cursor_visible) {
				canvas.drawRect(cl, ct, cr, cb, cursor);
			}
		}
	}

	public void computeCanvasSize()
	{
		canvas_width = cols*char_width;
	    canvas_height = rows*char_height;
	}

	public void autoSizeFontByHeight(int maxHeight) {
		if (maxHeight == 0) maxHeight = getMeasuredHeight();
		setFontFace(0, maxHeight);

		// HACK -- keep 480x320 fullscreen as-is
		if (maxHeight==320) {
			setFontSizeLegacy();
		}
		else {
			font_text_size = 6;
			do {
				font_text_size += 1;
				setFontSize(font_text_size);
			} while (char_height*rows <= maxHeight);
		
			font_text_size -= 1;
			setFontSize(font_text_size);
		}
		Log.d("Angband","autoSizeFontHeight "+font_text_size);
	}

	public void autoSizeFontByWidth(int maxWidth) {
		if (maxWidth == 0) maxWidth = getMeasuredWidth();
		setFontFace(maxWidth, 0);

		// HACK -- keep 480x320 fullscreen as-is
		if (maxWidth==480) {
			setFontSizeLegacy();
		}
		else {
			font_text_size = 6;
			do {
				font_text_size += 1;
				setFontSize(font_text_size);		
			} while (char_width*cols <= maxWidth);

			font_text_size -= 1;
			setFontSize(font_text_size);
		}
		Log.d("Angband","autoSizeFontWidth "+font_text_size+","+maxWidth);
	}

	private void setFontSizeLegacy() {
		font_text_size = 12;
		char_height = 12;
		char_width = 6;
		setFontSize(font_text_size);
	}

	private void setFontFace(int maxWidth, int maxHeight) {
		if ( (maxWidth >0 && maxWidth<=480) || (maxHeight>0 && maxHeight<=320) ) {
			tfTiny = Typeface.createFromAsset(getResources().getAssets(), "6x12.ttf");
			fore.setTypeface(tfTiny);
		}
		else {
			tfStd = Typeface.createFromAsset(getResources().getAssets(), "VeraMoBd.ttf"); 
			fore.setTypeface(tfStd);
		}		
	}

	private void setFontSize(int size) {
		if (size>12) {
			if (tfStd == null) {
				tfStd = Typeface.createFromAsset(getResources().getAssets(), "VeraMoBd.ttf"); 
			}
			fore.setTypeface(tfStd);
		}
		else {	
			if (tfTiny == null) {
				tfTiny = Typeface.createFromAsset(getResources().getAssets(), "6x12.ttf"); 
			}
			fore.setTypeface(tfTiny);
		}

		if (size < 6) size = 6;
		else if (size > 48) size = 48;

		font_text_size = size;

		fore.setTextSize(font_text_size);		
		Preferences.setDefaultFontSize(font_text_size);
 
		char_height = (int)Math.ceil(fore.getFontSpacing()); 
		char_width = (int)fore.measureText("X", 0, 1);	
		Log.d("Angband","setSizeFont "+font_text_size);
	}

	@Override
	protected void onMeasure(int widthmeasurespec, int heightmeasurespec)
	{
		Log.d("Angband", "onMeasure");

		int height = MeasureSpec.getSize(heightmeasurespec);
		int width = MeasureSpec.getSize(widthmeasurespec);

		setFontSize(Preferences.getDefaultFontSize());  // todo: move to prefs

		fore.setTextAlign(Paint.Align.LEFT);


		int minheight = getSuggestedMinimumHeight();
		int minwidth = getSuggestedMinimumWidth();

		// int width=0, height=0;
		/*

		if (width < minwidth)
		{
			width = minwidth;
		}
		if (height < minheight)
		{
			height = minheight;
		}

		int modex = MeasureSpec.getMode(widthmeasurespec);
		int modey = MeasureSpec.getMode(heightmeasurespec);
		if(modex == MeasureSpec.AT_MOST)
		{
			width = Math.min(MeasureSpec.getSize(widthmeasurespec), width);
		}
		else if(modex == MeasureSpec.EXACTLY)
		{
			width = MeasureSpec.getSize(widthmeasurespec);
		}
		if(modey == MeasureSpec.AT_MOST)
		{
			height = Math.min(MeasureSpec.getSize(heightmeasurespec), height);
		}
		else if(modey == MeasureSpec.EXACTLY)
		{
			height = MeasureSpec.getSize(heightmeasurespec);
		}
		*/
		Log.d("Angband","onMeasure "+canvas_width+","+canvas_height+";"+width+","+height);

		setMeasuredDimension(width, height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		return gesture.onTouchEvent(me);
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
	{	   
		int newscrollx = this.getScrollX() + (int)distanceX;
		int newscrolly = this.getScrollY() + (int)distanceY;
	
		if(newscrollx < 0) 
			newscrollx = 0;
		if(newscrolly < 0) 
			newscrolly = 0;
		if(newscrollx >= canvas_width - getWidth())
			newscrollx = canvas_width - getWidth() + 1;
		if(newscrolly >= canvas_height - getHeight())
		 	newscrolly = canvas_height - getHeight() + 1;

		if (canvas_width <= getWidth()) newscrollx = 0; //this.getScrollX();
		if (canvas_height <= getHeight()) newscrolly = 0; //this.getScrollY();		

		scrollTo(newscrollx, newscrolly);

		return true;
	}
	public boolean onDown(MotionEvent e)
	{
		return true;
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		return true;
	}
	public void onLongPress(MotionEvent e)
	{
		Log.d("Angband", "onLongPress");		
		aa.openContextMenu(this);
		
	}
	public void onShowPress(MotionEvent e)
	{
	}
	public boolean onSingleTapUp(MotionEvent event)
	{
		if (!Preferences.getEnableTouch()) return false;

		int x = (int) event.getX();
		int y = (int) event.getY();

		int r, c;
		c = (x * 3) / getWidth();
		r = (y * 3) / getHeight();

		int key = (2 - r) * 3 + c + '1';

		AngbandActivity.xb.addDirectionToKeyBuffer(key);
			
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("Angband", "onSizeChanged");

		if (pausing) {
			pausing = false;
		}
		else if (resuming || new_instance) {
			resuming = false;
			new_instance = false;
			AngbandActivity.xb.startBand();
		}
  	}

	/* Xband interface */
	public boolean onXbandStarting() {

		computeCanvasSize();

		// sanity 
		if (canvas_width == 0 || canvas_height == 0) return false;

		Log.d("Angband","createBitmap "+canvas_width+","+canvas_height);
		bitmap = Bitmap.createBitmap(canvas_width, canvas_height, Bitmap.Config.RGB_565);
		canvas = new Canvas(bitmap);		
		/*
		  canvas.setDrawFilter(new PaintFlagsDrawFilter(
		  Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG,0
		  )); // this seems to have no effect, why?
		*/		

		if (Preferences.getActiveProfile().getPlugin()
			== Preferences.Plugin.Angband.getId()) {
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

		return true;
	}

	public void onXbandStopping() {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int key = 0;

		Log.d("Angband", "onKeyDown("+keyCode+","+event+")");
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
				setFontSize(font_text_size+1);
				//computeCanvasSize();
				AngbandActivity.xb.redraw();
			}
			return true;
			//break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (Preferences.getVolumeKeyFontSizing()) {
				setFontSize(font_text_size-1);
				//computeCanvasSize();
				AngbandActivity.xb.redraw();
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

		AngbandActivity.xb.addToKeyBuffer(key);
		return true; 
		// two \r's in a row force pop up context menu
		// there may be other Android behaviors like this,
		// and I think its best to stop them here if we've 
		// already handled the key.
		// return super.onKeyDown(keyCode, event);
	}

	@Override
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
		return super.onKeyUp(keyCode, event);
	}

	public void refresh() {
		postInvalidate();
		onScroll(null,null,0,0);  // sanitize scroll position
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

		/* handy for debugging
		StringBuffer result = new StringBuffer();
		for (int i=0; i < n; i++) {
			result.append(new String(new byte[] {cp[i]}));
		}
		Log.d("Angband","text"+result.toString());
		*/

		fore.setColor(colors[a]);

		byte c;
		for (int i = 0; i < n; i++) {
			c = cp[i];
			if (c > 19 && c < 128) {
				wipe(row, col, 1);

				AngbandActivity.xb.cachePoint(col, row, c, a);
		
				putchar((char)c);
			}
		}

		return 0;
	}

	public void wipe(final int row, final int col, final int n) {
		float x = col * char_width;
		float y = row * char_height;

		if (canvas == null ) return;

		canvas.drawRect(
						x, 
						y, 
						x + char_width * n, 
						y + char_height,
						back
						);
	}

	public void move(final int col, final int row) {
		this.col = col;
		this.row = row;
	}

	private void putchar(final char c) {

		float x = col * char_width;
		float y = (row + 1) * char_height;
		  
		String str = c + "";

		if (canvas == null ) return;

		canvas.drawText (
						 str,
						 x, 
						 y - fore.descent(), 
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

	public void redraw(char[][] charmap, byte[][] colormap) {
		Log.d("Angband","Termview.redraw()");
		int row_save = row;
		int col_save = col;

		if (canvas != null) {
			Log.d("Angband","Termview.really redrawing");
			
			canvas.drawPaint(back);
			for(int r = 0; r < rows; r++) {
				for(int c=0; c < cols; c++) {
					fore.setColor(colors[colormap[c][r]]);
					move(c, r);

					char ch = charmap[c][r]; 
					if(ch != '\0') putchar(ch);
				}
			}		

			row = row_save;
			col = col_save;
			refresh();
		}
	}

	public void onResume() {
		vibrate = Preferences.getVibrate();
		pausing = false;
		resuming = true;
		Log.d("Angband","Termview.onResume()");
	}

	public void onPause() {
		Log.d("Angband","Termview.onPause()");
		// this is the only guaranteed safe place to save state 
		// according to SDK docs
		pausing = true;
		resuming = false;
		AngbandActivity.xb.saveBand();
	}
}
