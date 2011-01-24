package org.angdroid.angband;

import android.util.Log;
import java.util.Formatter;

public class TermWindow {

	public int TERM_BLACK = 0xFF000000;

	public class TermPoint {
		public char Char = ' ';
		public int Color = TERM_BLACK;
		public boolean isDirty = false;
	}
	public TermPoint[][] buffer = null; 

	public boolean allDirty = false;
	public boolean cursor_visible;
	public int col = 0;
	public int row = 0;
	public int fcolor = TERM_BLACK;

	public int cols = 0;
	public int rows = 0;
	public int begin_y = 0;
	public int begin_x = 0;

	public TermWindow(int rows, int cols, int begin_y, int begin_x) {
		this.cols = cols;
		this.rows = rows;
		this.begin_y = begin_y;
		this.begin_x = begin_x;
		buffer = new TermPoint[rows][cols];
		for(int r=0;r<rows;r++) {
			for(int c=0;c<cols;c++) {
				buffer[r][c] = newPoint(null);
			}
		}
	}

	private TermPoint newPoint(TermPoint p) {
		if (p == null) 
			p = new TermPoint();
		else {
			p.isDirty = p.isDirty || p.Char != ' ' || p.Color != TERM_BLACK;
			p.Char = ' ';
			p.Color = TERM_BLACK;
		}
		return p;
	}

	public void clearPoint(int row, int col) {
		if (col>-1 && col<cols 
			&& row>-1 && row<rows) {
			TermPoint p = buffer[row][col];
			newPoint(p);
		}
		else {
			Log.d("Angband","TermWindow.clearPoint - point out of bounds: "+col+","+row);
		}
	}

	protected void attrset(int a) {
		fcolor = a;
	}

	public void clear() {
		//Log.d("Angband","TermWindow.clear");
		for(int r=0;r<rows;r++) {
			for(int c=0;c<cols;c++) {
				TermPoint p = buffer[r][c];
				newPoint(p);
			}
		}
	}

	public void clrtoeol() {
		//Log.d("Angband","TermWindow.clrtoeol ("+row+","+col+")");
		for(int c=col;c<cols;c++) {
			TermPoint p = buffer[row][c];
			newPoint(p);
		}
	}

	public void hline(char c, int n) {
		//Log.d("Angband","TermWindow.hline ("+row+","+col+") "+n);
		int x = Math.min(cols,n+col);
		for(int i=col;i<x;i++) {
			addch(c);
		}
	}

	public void move(int row, int col) {
		if (col>-1 && col<cols && row>-1 & row<rows) {
			this.col = col;
			this.row = row;
		}
	}

	public void addnstr(int n, byte[] cp) {
		byte c;
		
		//String foo = new String(cp);
		//Log.d("Angband","addnstr ("+row+","+col+") ["+foo+"]");

		for (int i = 0; i < n; i++) {
			c = cp[i];
			addch((char)c);
		}
	}

	public void addch(char c) {
		if (c > 19 && c < 128
			&& col>-1 && col<cols && row>-1 && row<rows) {
			TermPoint p = buffer[row][col];
			
			p.isDirty = p.isDirty || p.Char != c || p.Color != fcolor;
			p.Char = c;
			p.Color = fcolor;

			/*
			  Formatter fmt = new Formatter();
			  fmt.format("color: %x", fcolor);
			  Log.d("Angband","TermWindow.addch ("+row+","+col+") "+fmt+" '"+c+"'");
			*/
		}
		else {
			Log.d("Angband","TermWindow.addch - point out of bounds: "+col+","+row);
		}
	
		advance();
	}

	private void advance() {
		col++;
		if (col >= cols) {
			row++;
			col = 0;
		}
		if (row >= rows) {
			row = rows-1;
		}
	}

	public void mvaddch(int row, int col, char c) {
		move(row, col);
		addch(c);
	}

}