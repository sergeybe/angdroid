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

package com.scoreloop.client.android.ui.framework;

import android.content.Intent;

public class ActivityDescription {

	private static int			_instanceCounter		= 0;

	public static final String	EXTRA_IDENTIFIER		= "activityIdentifier";

	private final ValueStore	_arguments				= new ValueStore();
	private boolean				_enabledWantsClearTop	= true;
	private final String		_identifier;
	private final Intent		_intent;
	private final int			_tabId;
	private boolean				_wantsClearTop			= false;

	ActivityDescription(final int tabId, final Intent intent) {
		_identifier = "pane-" + ++_instanceCounter;
		_tabId = tabId;
		_intent = intent;
	}

	public ValueStore getArguments() {
		return _arguments;
	}

	public String getIdentifier() {
		return _identifier;
	}

	public Intent getIntent() {
		_intent.putExtra(EXTRA_IDENTIFIER, _identifier);
		if (_enabledWantsClearTop && _wantsClearTop) {
			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else {
			final int flags = _intent.getFlags();
			_intent.setFlags(flags & ~Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		return _intent;
	}

	public int getTabId() {
		return _tabId;
	}

	boolean hasIdentfier(final String paneId) {
		return _identifier.equals(paneId);
	}

	void setEnabledWantsClearTop(final boolean enabled) {
		_enabledWantsClearTop = enabled;
	}

	void setWantsClearTop(final boolean flag) {
		_wantsClearTop = flag;
	}
}
