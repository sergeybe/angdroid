package org.angdroid.angband;

import android.util.Log;
import android.view.KeyEvent;

import org.angdroid.angband.KeyMapper.KeyAction;

public class KeyMap {

	protected String pref_key = "";
	protected char character;
	protected KeyAction key_action;

	protected int key_code = 0;
	protected boolean alt_mod = false;
	protected boolean char_mod = false;

	public KeyMap(String pref_key, char character) {
		this.pref_key = pref_key;
		this.character = character;
		this.key_action = KeyAction.CharacterKey;
		loadFromPref();
	}

	public KeyMap(String pref_key, KeyAction action) {
		this.pref_key = pref_key;
		this.key_action = action;
		loadFromPref();
	}

	public void loadFromPref() {
		String pref_val = Preferences.getString(pref_key);
		if (pref_val != null && pref_val.length()>0) {
			alt_mod = false;
			char_mod = false;
			if (pref_val.startsWith("C")) {
				key_code = pref_val.charAt(1);
				char_mod = true;
			}
			else {
				alt_mod = pref_val.startsWith("0");
				key_code = Integer.parseInt(pref_val);
			}
		}
	}

	public void assign(int key_code, boolean alt_mod, boolean char_mod) {
		this.key_code = key_code;
		this.alt_mod = alt_mod;
		this.char_mod = char_mod;
	}

	public void clear() {
		this.key_code = 0;
		this.alt_mod = false;
		this.char_mod = false;
	}

	public String getPrefValue() {
		return stringValue(key_code, alt_mod,char_mod);
	}
	public String getPrefKey() {
		return pref_key;
	}

	public boolean isAssigned() {
		return key_code != 0;
	}

	public KeyAction getKeyAction() {
		return key_action;
	}

	public char getCharacter() {
		return character;
	}

	public static String stringValue(int key_code, boolean alt_mod, boolean char_mod) {
		if (key_code == 0) 
			return "";
		else if (char_mod) 
			return "C"+(char)key_code;
		else if (key_code == KeyEvent.KEYCODE_ALT_LEFT || key_code == KeyEvent.KEYCODE_ALT_RIGHT)
			return ""+key_code;
		else
			return (alt_mod?"0":"")+key_code;
	}
}