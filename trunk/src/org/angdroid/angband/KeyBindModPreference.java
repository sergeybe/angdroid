package org.angdroid.angband;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.AttributeSet;
import android.content.DialogInterface;

public class KeyBindModPreference
	extends KeyBindPreference implements DialogInterface.OnClickListener {

	public KeyBindModPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean handleModifier(int keyCode) {
		return false;
	}
}
