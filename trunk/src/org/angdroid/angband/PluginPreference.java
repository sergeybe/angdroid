package org.angdroid.angband;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class PluginPreference extends ListPreference 
{
	public PluginPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PluginPreference(Context context) {
        super(context);
    }

    @Override
    protected boolean persistString(String value) {
		if (value != null) {
			Preferences.getActiveProfile().setPlugin(Integer.parseInt(value));
			Preferences.saveProfiles();
			return true;
		}
		else
			return false;
	}

    @Override
    protected String getPersistedString(String defaultReturnValue) {
		int pl = Preferences.getActiveProfile().getPlugin();
		return String.valueOf(pl);
    }
}