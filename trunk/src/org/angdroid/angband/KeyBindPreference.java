package org.angdroid.angband;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.preference.DialogPreference;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.view.KeyEvent;

public class KeyBindPreference
	extends DialogPreference implements DialogInterface.OnClickListener {

	Context context;
	boolean alt_mod = false;
    int key_code = 0;

	public KeyBindPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setDialogTitle("Press a hardware key...");
		setPositiveButtonText("Clear");
		setNegativeButtonText("Cancel");
	}
	@Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		super.onPrepareDialogBuilder(builder);    

		alt_mod = false;
		key_code = 0;

		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

					if (event.getAction() != KeyEvent.ACTION_DOWN
						|| event.getRepeatCount()>0) 
						return false;

					switch(keyCode) {
					case KeyEvent.KEYCODE_ALT_LEFT:
					case KeyEvent.KEYCODE_ALT_RIGHT:
						alt_mod = !alt_mod;
						return false;
					default:
						if (event.isAltPressed()) alt_mod = true;
						key_code = keyCode;

						SharedPreferences.Editor ed = getSharedPreferences().edit();
						String val = (alt_mod ? "0": "")+key_code;
						ed.putString(getKey(), val);
						ed.commit();

						dialog.dismiss();
						return true;
					}
				}
	    });
	}

	public String getDescription() {
		SharedPreferences settings =  getSharedPreferences(); 
		String val = settings.getString(getKey(),"");

		String desc = "<none>";
		
		if (val != null && val.length()>0) {
		
			alt_mod = val.startsWith("0");
			key_code = Integer.parseInt(val);

			if (key_code != 0) {
				desc = "";
				if (alt_mod) desc = "Alt+";
				desc += "Key "+key_code;
			}
		}

		return desc;
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == -1) { //Clear
			SharedPreferences.Editor ed = getSharedPreferences().edit();
			ed.putString(getKey(), "");
			ed.commit();
		}
		else {
			//Toast.makeText(context, "Cancel was clicked", Toast.LENGTH_SHORT).show();
		}
	} 
}
