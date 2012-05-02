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

import com.scoreloop.client.android.core.model.Challenge;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.ui.component.base.Configuration;
import com.scoreloop.client.android.ui.component.base.Factory;

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

	public static interface Impl extends ScoreloopManager {
		void destroy();

		Configuration getConfiguration();

		Factory getFactory();

		Challenge getLastSubmittedChallenge();

		Score getLastSubmittedScore();

		int getLastSubmitStatus();

		void init(Context context, String gameSecret);
	}

	private static Impl	_singleton;

	/**
	 * Destroys the SingletonManager for ScoreloopUI.<br/>
	 * This method should be called from <a href="http://developer.android.com/reference/android/app/Application.html#onTerminate%28%29"> android.app.Application#onTerminate()</a>
	 * to clean up in the case of running in an emulator. onTerminate() is not called on devices though.
	 */
	public static void destroy() {
		if (_singleton != null) {
			_singleton.destroy();
			_singleton = null;
		}
	}

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

	static Impl getImpl() {
		return (Impl) get();
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
	public static ScoreloopManager init(final Context context, final String gameSecret) {
		if (_singleton != null) {
			throw new IllegalStateException("ScoreloopManagerSingleton.init() can be called only once");
		}
		try {
			// NOTE: using class-for-name here to prevent the dependency to the higher level manager package!
			// _singleton = new StandardScoreloopManager();
			_singleton = (Impl) Class.forName("com.scoreloop.client.android.ui.manager.StandardScoreloopManager").newInstance();
			_singleton.init(context, gameSecret);
		} catch (final Exception e) {
			throw new IllegalStateException("ScoreloopManagerSingleton.init() failed to instantiate ScoreloopManager implementation: "
					+ e.getLocalizedMessage(), e);
		}

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
	public static ScoreloopManager init(final Impl manager) {
		if (_singleton != null) {
			throw new IllegalStateException("ScoreloopManagerSingleton.init() can be called only once");
		}
		_singleton = manager;
		return _singleton;
	}
}
