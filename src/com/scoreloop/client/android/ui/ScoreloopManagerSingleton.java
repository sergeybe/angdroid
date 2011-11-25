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

package com.scoreloop.client.android.ui;

import android.content.Context;

/**
 * The ScoreloopManagerSingleton class is used to create and retrieve
 * the singleton instance of ScoreloopManager that is shared by all 
 * ScoreloopUI activities. 
 * 
 * Basic usage:
 * -# Create an instance of ScoreloopManager using init(final Context).
 * -# Access the instance by calling get().
 */
public class ScoreloopManagerSingleton {

	private static ScoreloopManager	_singleton;

	/**
	 * This method returns the ScoreloopManager instance 
	 * shared by all ScoreloopUI activities.
	 * @return ScoreloopManager The manager shared by all application activities. 
	 */
	public static ScoreloopManager get() {
		if (_singleton == null) {
			throw new IllegalStateException("ScoreloopManagerSingleton.init() must be called first");
		}
		return _singleton;
	}

	/**
	 * The initializer for ScoreloopUI. This method should:
	 * - be called from within the Android application class,
	 * - only be called once during the lifecycle of the game. 
	 * 
	 * @param context An  <a href="http://developer.android.com/reference/android/content/Context.html">android.content.Context</a> object; The Android
	 * application context. 
	 * @param gameSecret Your game's secret, which can be retrieved from https://developer.scoreloop.com/
	 */
	public static ScoreloopManager init(final Context context, String gameSecret) {
		if (_singleton != null) {
			throw new IllegalStateException("ScoreloopManagerSingleton.init() can be called only once");
		}
		_singleton = new StandardScoreloopManager(context, gameSecret);
		return _singleton;
	}

	/**
	 * The initializer for ScoreloopUI. This method should:
	 * - be called when using a custom ScoreloopManager class,
	 * - only be called once during the lifecycle of the game. 
	 * 
	 * @param manager A custom ScoreloopManager object. 
	 * 
	 */
	public static ScoreloopManager init(final ScoreloopManager manager) {
		if (_singleton != null) {
			throw new IllegalStateException("ScoreloopManagerSingleton.init() can be called only once");
		}
		_singleton = manager;
		return _singleton;
	}

    /**
     * Destroys the SingletonManager for ScoreloopUI.<br/>
     * This method should be called from <a href="http://developer.android.com/reference/android/app/Application.html#onTerminate%28%29">  android.app.Application#onTerminate()</a>
     */
    public static void destroy() {
        if (_singleton != null) {
            _singleton.destroy();
        }
        _singleton = null;
    }

}
