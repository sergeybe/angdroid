/*
 * In derogation of the Scoreloop SDK - License Agreement concluded between
 * Licensor and Licensee, as defined therein, the following conditions shall
 * apply for the source code contained below, whereas apart from that the
 * Scoreloop SDK - License Agreement shall remain unaffected.
 * 
 * Copyright: Scoreloop AG, Germany (Licensor)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.scoreloop.client.android.ui.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Configuration.Feature;
import com.scoreloop.client.android.ui.component.base.Constant;

public class Checker {

	private class CheckerException extends RuntimeException {
		private static final long	serialVersionUID	= 1L;

		private CheckerException() {
			super(("Manifest Verification Error! See logcat output!"));
		}
	}

	public class CheckerRun {

		private int							_counter;
		private final Map<String, Object>	_infos;
		private final String				_kind;
		private final List<String>			_missing	= new ArrayList<String>();
		private boolean						_shouldBail;

		private CheckerRun(final String kind, final Map<String, Object> infos) {
			_kind = kind;
			_infos = infos;
		}

		public void add(final String name) {
			add(name, null);
		}

		public void add(final String name, final Feature feature, final Object... keyValuePairs) {
			if ((feature != null) && !_configuration.isFeatureEnabled(feature)) {
				return;
			}
			++_counter;
			if (!_infos.containsKey(name)) {
				_missing.add(format(name, keyValuePairs));
			}
			for (int i = 0; i < (keyValuePairs.length - 1); i += 2) {
				final String key = (String) keyValuePairs[i];
				final Object expectedValue = keyValuePairs[i + 1];

				@SuppressWarnings("unchecked")
				final Map<String, Object> details = (Map<String, Object>) _infos.get(name);
				if (details != null) {
					final Object actualValue = details.get(key);
					if (actualValue != null) {
						if (key.equals("configChanges")) { // add other keys for which the comparison has to be special cased
							final int expectedFlags = (Integer) expectedValue;
							final int actualFlags = (Integer) actualValue;
							if ((actualFlags & expectedFlags) == expectedFlags) {
								continue;
							}
						} else if (key.equals("theme") && (expectedValue instanceof String)) {
							final int expectedInt = _context.getResources().getIdentifier((String) expectedValue, null, null);
							if (actualValue.equals(expectedInt)) {
								continue;
							}
						} else if (actualValue.equals(expectedValue)) {
							continue;
						}
					}
				}
				_missing.add(format(name, keyValuePairs));
				return;
			}
		}

		public void check() {
			if (_shouldBail) {
				throw new CheckerException();
			}
		}

		private String format(final String name, final Object[] keyValuePairs) {
			final StringBuilder buffer = new StringBuilder();
			buffer.append("android:name=\"");
			buffer.append(name);
			buffer.append("\"");
			for (int i = 0; i < (keyValuePairs.length - 1); i += 2) {
				final String key = (String) keyValuePairs[i];
				final Object value = keyValuePairs[i + 1];
				buffer.append(" android:");
				buffer.append(key);
				buffer.append("=\"");
				if (key.equals("theme") && (value instanceof Integer)) { // add other keys for which the value should be formatted as
					// resource-name
					buffer.append(_context.getResources().getResourceName((Integer) value));
				} else if (key.equals("configChanges")) {
					final int flags = (Integer) value;
					final int configFlags[] = { ActivityInfo.CONFIG_FONT_SCALE, ActivityInfo.CONFIG_KEYBOARD,
							ActivityInfo.CONFIG_KEYBOARD_HIDDEN, ActivityInfo.CONFIG_LOCALE, ActivityInfo.CONFIG_MCC,
							ActivityInfo.CONFIG_MNC, ActivityInfo.CONFIG_NAVIGATION, ActivityInfo.CONFIG_ORIENTATION,
							ActivityInfo.CONFIG_SCREEN_LAYOUT, ActivityInfo.CONFIG_TOUCHSCREEN };
					final String configNames[] = { "fontScale", "keyboard", "keyboardHidden", "locale", "mcc", "mnc", "navigation",
							"orientation", "layout", "touchscreen" };
					boolean hasFlag = false;
					for (int j = 0; j < configFlags.length; ++j) {
						if ((flags & configFlags[j]) != 0) {
							if (hasFlag) {
								buffer.append('|');
							}
							buffer.append(configNames[j]);
							hasFlag = true;
						}
					}
				} else if (key.equals("screenOrientation")) {
					final String orientationNames[] = { "landscape", "portrait", "user", "behind", "sensor", "nosensor", "sensorLandscape",
							"sensorPortait", "reverseLandscape", "reversePortait", "fullSensor" };
					buffer.append(orientationNames[(Integer) value]);
				} else {
					buffer.append(value.toString());
				}
				buffer.append("\"");
			}
			return buffer.toString();
		}

		private void informDeveloper(final String detail) {
			Log.e(Constant.LOG_TAG, "=====================================================================================");
			Log.e(Constant.LOG_TAG, "Manifest file verification error. Please resolve any issues first!");
			Log.e(Constant.LOG_TAG, detail);
			for (final String entry : _missing) {
				Log.e(Constant.LOG_TAG, "<" + _kind + " " + entry + "/>");
			}
		}

		public void reportOptional() {
			if (_counter == _missing.size()) {
				informDeveloper("At least one of following entries is mssing in your AndroidManifest.xml file:");
				_shouldBail = true;
			}
		}

		public void reportRequired() {
			if (_missing.size() > 0) {
				informDeveloper("All the following entries are missing in your AndroidManifest.xml file:");
				_shouldBail = true;
			}
		}
	}

	private Map<String, Object>	_activityInfo;
	private final Configuration	_configuration;
	private final Context		_context;
	private final PackageInfo	_packageInfo;

	private Map<String, Object>	_permissionInfo;

	public Checker(final Context context, final Configuration configuration) {
		_context = context;
		_configuration = configuration;

		final PackageManager packageManager = _context.getPackageManager();
		try {
			_packageInfo = packageManager.getPackageInfo(_context.getPackageName(), PackageManager.GET_ACTIVITIES
					| PackageManager.GET_PERMISSIONS);
		} catch (final NameNotFoundException e) {
			throw new CheckerException();
		}
	}

	public CheckerRun createActivityRun() {
		return new CheckerRun("activity", getActivityInfo());
	}

	public CheckerRun createUsesPermissionRun() {
		return new CheckerRun("uses-permission", getPermissionInfo());
	}

	private Map<String, Object> getActivityInfo() {
		if (_activityInfo == null) {
			_activityInfo = new HashMap<String, Object>();
			for (final ActivityInfo info : _packageInfo.activities) {
				final Map<String, Object> details = new HashMap<String, Object>();
				if (info.theme != 0) {
					details.put("theme", info.theme);
				}
				if (info.configChanges != 0) {
					details.put("configChanges", info.configChanges);
				}
				if (info.screenOrientation != -1) {
					details.put("screenOrientation", info.screenOrientation);
				}
				if (info.nonLocalizedLabel != null) {
					details.put("label", info.nonLocalizedLabel);
				}
				_activityInfo.put(info.name, details.isEmpty() ? null : details);
			}
		}
		return _activityInfo;
	}

	private Map<String, Object> getPermissionInfo() {
		if (_permissionInfo == null) {
			_permissionInfo = new HashMap<String, Object>();
			final String[] permissions = _packageInfo.requestedPermissions;
			if (permissions != null) {
				for (final String name : permissions) {
					_permissionInfo.put(name, null);
				}
			}
		}
		return _permissionInfo;
	}
}
