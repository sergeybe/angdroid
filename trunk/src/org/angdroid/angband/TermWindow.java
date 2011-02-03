package org.angdroid.angband;

import android.util.Log;
import java.util.Formatter;

public class TermWindow {

	public int TERM_BLACK = 0xFF000000;
	public int TERM_WHITE = 0xFFFFFFFF;

	public class TermPoint {
		public char Char = ' ';
		public int Color = TERM_WHITE;
		public boolean isDirty = false;
	}
	public TermPoint[][] buffer = null; 

	public boolean allDirty = false;
	public boolean cursor_visible;
	public int col = 0;
	public int row = 0;
	public int fcolor = TERM_WHITE;

	public int cols = 0;
	public int rows = 0;
	public int begin_y = 0;
	public int begin_x = 0;

	public TermWindow(int rows, int cols, int begin_y, int begin_x) {
		if (cols == 0) this.cols = Preferences.cols;
		else this.cols = cols;
		if (rows == 0) this.rows = Preferences.rows;
		else this.rows = rows;
		this.begin_y = begin_y;
		this.begin_x = begin_x;
		buffer = new TermPoint[this.rows][this.cols];
		for(int r=0;r<this.rows;r++) {
			for(int c=0;c<this.cols;c++) {
				buffer[r][c] = newPoint(null);
			}
		}
	}

	private TermPoint newPoint(TermPoint p) {
		if (p == null) 
			p = new TermPoint();
		else {
			p.isDirty = p.isDirty || p.Char != ' ' || p.Color != TERM_WHITE;
			p.Char = ' ';
			p.Color = TERM_WHITE;
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
		//Log.d("Angband","TermWindow.clear start "+rows+","+cols);
		for(int r=0;r<rows;r++) {
			for(int c=0;c<cols;c++) {
				//Log.d("Angband","TermWindow.clear clearPoint "+r+","+c);
				TermPoint p = buffer[r][c];
				newPoint(p);
			}
		}
		//Log.d("Angband","TermWindow.clear end");
	}

	public void clrtoeol() {
		//Log.d("Angband","TermWindow.clrtoeol ("+row+","+col+")");
		for(int c=col;c<cols;c++) {
			TermPoint p = buffer[row][c];
			newPoint(p);
		}
	}

	public void clrtobot() {
		//Log.d("Angband","TermWindow.clrtobot ("+row+","+col+")");
		for(int r=row;r<rows;r++) {
			for(int c=col;c<cols;c++) {
				TermPoint p = buffer[r][c];
				newPoint(p);
			}
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

	public int getcury() {
		return row;
	}

	public int getcurx() {
		return col;
	}

	public void overwrite(TermWindow w) {
		row = 0;
		col = 0;
		int max_rows = Math.min(rows,w.rows);
		int max_cols = Math.min(cols,w.cols);
		for(int r=0;r<max_rows;r++) {
			for(int c=0;c<max_cols;c++) {
				TermPoint p1 = w.buffer[r][c];
				TermPoint p2 = buffer[r][c];
				p2.Char = p1.Char;
				p2.Color = p1.Color;
				p2.isDirty = true;
			}
		}
	}

	public void touch() {
		for(int r=0;r<rows;r++) {
			for(int c=0;c<cols;c++) {
				TermPoint p = buffer[r][c];
				p.isDirty = true;
			}
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
		/*
		Formatter fmt = new Formatter();
		fmt.format("color: %x", fcolor);
		Log.d("Angband","TermWindow.addch ("+row+","+col+") "+fmt+" '"+c+"'");
		*/

		if (col>-1 && col<cols && row>-1 && row<rows) {
			if (c > 19 && c < 128) {
				TermPoint p = buffer[row][col];
			
				p.isDirty = p.isDirty || p.Char != c || p.Color != fcolor;
				p.Char = c;
				p.Color = fcolor;
				advance();
			}
			else if (c == 9) {  // recurse to expand that tab
				//Log.d("Angband","TermWindow.addch - tab expand");				
				int ss = col % 8;
				if (ss==0) ss=8;
				for(int i=0;i<ss;i++) addch(' ');
			}
			else {
				//Log.d("Angband","TermWindow.addch - invalid character: "+(int)c);
				advance();
			}
		}
		else {
			Log.d("Angband","TermWindow.addch - point out of bounds: "+col+","+row);
		}	
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