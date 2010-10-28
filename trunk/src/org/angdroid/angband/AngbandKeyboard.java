package org.angdroid.angband;

import android.content.Context;
import android.app.Activity;
import android.view.LayoutInflater;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.util.Log;

public class AngbandKeyboard implements OnKeyboardActionListener
{
	KeyboardView virtualKeyboardView;
	Keyboard virtualKeyboardQwerty;
	Keyboard virtualKeyboardSymbols;
	Keyboard virtualKeyboardSymbolsShift;

	TermView term;

	AngbandKeyboard(TermView term)
	{
		this.term = term;
		virtualKeyboardQwerty = new Keyboard(term.getContext(), R.xml.keyboard_qwerty);
		virtualKeyboardSymbols = new Keyboard(term.getContext(), R.xml.keyboard_sym);
		virtualKeyboardSymbolsShift = new Keyboard(term.getContext(), R.xml.keyboard_symshift);
		LayoutInflater inflater = LayoutInflater.from(term.getContext());
		virtualKeyboardView = (KeyboardView)inflater.inflate(
				R.layout.input, null);
		virtualKeyboardView.setKeyboard(virtualKeyboardQwerty);
		virtualKeyboardView.setOnKeyboardActionListener(this);
	}

	private void handleShift()
	{
		Keyboard currentKeyboard = virtualKeyboardView.getKeyboard();
//		checkToggleCapsLock();
//		virtualKeyboardView.setShifted(mCapsLock || !mInputView.isShifted());

		if(currentKeyboard == virtualKeyboardQwerty)
		{
			// checkToggleCapsLock();
			virtualKeyboardView.setShifted(/*capslock*/ !virtualKeyboardView.isShifted());
		}
	}
	
	public void onKey(int primaryCode, int[] keyCodes)
	{
		char c = 0;
		if(primaryCode == Keyboard.KEYCODE_DELETE)
		{
			c = 8;
		}
		else if(primaryCode == Keyboard.KEYCODE_SHIFT)
		{
			handleShift();
		}
		else if(primaryCode == Keyboard.KEYCODE_ALT)
		{
			Keyboard current = virtualKeyboardView.getKeyboard();
			if(current == virtualKeyboardSymbolsShift)
			{
				virtualKeyboardView.setKeyboard(virtualKeyboardQwerty);
			}
			else
			{
				//virtualKeyboardSymbols.setShifted(true);
				virtualKeyboardView.setKeyboard(virtualKeyboardSymbolsShift);
				//virtualKeyboardSymbolsShift.setShifted(true);
			}
		}
		else if(primaryCode == Keyboard.KEYCODE_MODE_CHANGE)
		{
			Keyboard current = virtualKeyboardView.getKeyboard();
			if(current == virtualKeyboardSymbols)
			{
				current = virtualKeyboardQwerty;
			}
			else
			{
				current = virtualKeyboardSymbols;
			}
			virtualKeyboardView.setKeyboard(current);
			if(current == virtualKeyboardSymbols)
			{
				current.setShifted(false);
			}
		}
       	else
		{
			c = (char)primaryCode;			
			if(virtualKeyboardView.getKeyboard() == virtualKeyboardQwerty && virtualKeyboardView.isShifted())
			{
				c = Character.toUpperCase(c);
				virtualKeyboardView.setShifted(false);
			}
		}
		if(c != 0)
		{
			term.addToKeyBuffer(c);
		}
	}
	
	public void onPress(int primaryCode)
	{
	}
	public void onRelease(int primaryCode)
	{
	}
	public void onText(CharSequence text)
	{
	}
	public void swipeDown()
	{
	}
	public void swipeLeft()
	{
	}
	public void swipeRight()
	{
	}
	public void swipeUp()
	{
	}
}
