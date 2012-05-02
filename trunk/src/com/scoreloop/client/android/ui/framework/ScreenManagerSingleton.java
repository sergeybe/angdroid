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

public abstract class ScreenManagerSingleton {

	private static ScreenManager	_singleton;

	/**
	 * Destroys the ScreenManagerSingleton.<br>
	 * This method should be called from {@link android.app.Application#onTerminate()}
	 * @see android.app.Application#onTerminate()
	 */
	public static void destroy() {
		_singleton = null;
	}

	public static ScreenManager get() {
		if (_singleton == null) {
			throw new IllegalStateException("you have to init the screen-manager-singleton first");
		}
		return _singleton;
	}

	public static void init(final ScreenManager manager) {
		if (_singleton != null) {
			throw new IllegalStateException("ScreenManagerSingleton.init() can be called only once");
		}
		_singleton = manager;
	}

}
