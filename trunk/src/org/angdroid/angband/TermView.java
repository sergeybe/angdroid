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
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
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

	Typeface tfStd;
	Typeface tfTiny;
	Bitmap bitmap;
	Canvas canvas;
	Paint fore;
	Paint back;
	Paint cursor;

	//	int row = 0;
	//  int col = 0;

	public int canvas_width = 0;
	public int canvas_height = 0;

	private int char_height = 0;
	private int char_width = 0;
	private int font_text_size = 0;

	private Vibrator vibrator;
	private boolean vibrate;
	private Handler handler = null;
	private StateManager state = null;

	private GestureDetector gesture;

	public TermView(Context context) {
		super(context);
		initTermView(context);
		handler = ((AngbandActivity)context).getHandler();
		state = ((AngbandActivity)context).getStateManager();
	}

	public TermView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTermView(context);
		handler = ((AngbandActivity)context).getHandler();
		state = ((AngbandActivity)context).getStateManager();
	}

	protected void initTermView(Context context) {
		fore = new Paint();
		fore.setTextAlign(Paint.Align.LEFT);
		setForeColor(Color.WHITE);

		back = new Paint();
		back.setColor(Color.BLACK);

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

			int x = state.stdscr.col * (char_width);
			int y = (state.stdscr.row + 1) * char_height;

			// due to font "scrunch", cursor is sometimes a bit too big
			int cl = Math.max(x,0);
			int cr = Math.min(x+char_width,canvas_width-1);
			int ct = Math.max(y-char_height,0);
			int cb = Math.min(y,canvas_height-1);

			if (state.stdscr.cursor_visible) {
				canvas.drawRect(cl, ct, cr, cb, cursor);
			}
		}
	}

	public void computeCanvasSize() {
		canvas_width = Preferences.cols*char_width;
	    canvas_height = Preferences.rows*char_height;
	}

	protected void setForeColor(int a) {
		fore.setColor(a);			
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
				setFontSize(font_text_size, false);
			} while (char_height*Preferences.rows <= maxHeight);
		
			font_text_size -= 1;
			setFontSize(font_text_size);
		}
		//Log.d("Angband","autoSizeFontHeight "+font_text_size);
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
				setFontSize(font_text_size, false);		
			} while (char_width*Preferences.cols <= maxWidth);

			font_text_size -= 1;
			setFontSize(font_text_size);
		}
		//Log.d("Angband","autoSizeFontWidth "+font_text_size+","+maxWidth);
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

	public void increaseFontSize() {
		setFontSize(font_text_size+1);
	}

	public void decreaseFontSize() {
		setFontSize(font_text_size-1);
	}

	private void setFontSize(int size) {
		setFontSize(size,true);
	}
	private void setFontSize(int size, boolean persist) {
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
		
		if (persist) {
			if(Preferences.isScreenPortraitOrientation())
				Preferences.setPortraitFontSize(font_text_size);
			else
				Preferences.setLandscapeFontSize(font_text_size);			
		}
 
		char_height = (int)Math.ceil(fore.getFontSpacing()); 
		char_width = (int)fore.measureText("X", 0, 1);	
		//Log.d("Angband","setSizeFont "+font_text_size);
	}

	@Override
	protected void onMeasure(int widthmeasurespec, int heightmeasurespec) {
		int height = MeasureSpec.getSize(heightmeasurespec);
		int width = MeasureSpec.getSize(widthmeasurespec);

		int fs = 0;
		if(Preferences.isScreenPortraitOrientation())
			fs = Preferences.getPortraitFontSize();
		else
			fs = Preferences.getLandscapeFontSize();

		if(fs == 0) 
			autoSizeFontByWidth(width);
		else
			setFontSize(fs, false);  

		fore.setTextAlign(Paint.Align.LEFT);

		int minheight = getSuggestedMinimumHeight();
		int minwidth = getSuggestedMinimumWidth();

		setMeasuredDimension(width, height);
		//Log.d("Angband","onMeasure "+canvas_width+","+canvas_height+";"+width+","+height);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gesture.onTouchEvent(me);
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {	   
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
	public boolean onDown(MotionEvent e) {
		return true;
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}
	public void onLongPress(MotionEvent e) {
		handler.sendEmptyMessage(AngbandDialog.Action.OpenContextMenu.ordinal());
	}
	public void onShowPress(MotionEvent e) {
	}
	public boolean onSingleTapUp(MotionEvent event) {
		if (!Preferences.getEnableTouch()) return false;

		int x = (int) event.getX();
		int y = (int) event.getY();

		int r, c;
		c = (x * 3) / getWidth();
		r = (y * 3) / getHeight();

		int key = (2 - r) * 3 + c + '1';

		state.keyBuffer.addDirection(key);
			
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//Log.d("Angband", "onSizeChanged");
		super.onSizeChanged(w, h, oldw, oldh);
		handler.sendEmptyMessage(AngbandDialog.Action.StartGame.ordinal());
  	}

	public boolean onGameStart() {

		computeCanvasSize();

		// sanity 
		if (canvas_width == 0 || canvas_height == 0) return false;

		//Log.d("Angband","createBitmap "+canvas_width+","+canvas_height);
		bitmap = Bitmap.createBitmap(canvas_width, canvas_height, Bitmap.Config.RGB_565);
		canvas = new Canvas(bitmap);		
		/*
		  canvas.setDrawFilter(new PaintFlagsDrawFilter(
		  Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG,0
		  )); // this seems to have no effect, why?
		*/		

		state.currentPlugin = Plugins.Plugin.convert(Preferences.getActiveProfile().getPlugin());
   		return true;
	}

	public void drawPoint(int r, int c, char ch, int color) {
		float x = c * char_width;
		float y = r * char_height;

		if (canvas == null) {
			// OnSizeChanged has not been called yet
			//Log.d("Angband","null canvas in drawPoint");
			return;
		}

		canvas.drawRect(
						x, 
						y, 
						x + char_width, 
						y + char_height,
						back
						);					

		if (ch != ' ') {
			String str = ch + "";

			setForeColor(color);

			canvas.drawText (
							 str,
							 x, 
							 y + char_height - fore.descent(), 
							 fore
							 );
		}
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

	public void onResume() {
		//Log.d("Angband","Termview.onResume()");
		vibrate = Preferences.getVibrate();
	}

	public void onPause() {
		//Log.d("Angband","Termview.onPause()");
		// this is the only guaranteed safe place to save state 
		// according to SDK docs
		state.gameThread.send(GameThread.Request.SaveGame);
	}
}
