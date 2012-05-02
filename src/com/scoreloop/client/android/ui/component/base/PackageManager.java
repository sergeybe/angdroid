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

package com.scoreloop.client.android.ui.component.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;

public class PackageManager {

	private static final String[]	SCORELOOP_APP_PACKAGE_NAMES	= { "com.scoreloop.android.slapp" };

	private static void download(final Context context, final String downloadUrl) {
		if (downloadUrl != null) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)));
		}
	}

	public static void installScoreloopApp(final Context context) {
		download(context, ScoreloopManagerSingleton.get().getSession().getScoreloopAppDownloadUrl());
	}

	public static void installGame(final Context context, final Game game) {
		download(context, game.getDownloadUrl());
	}

	private static Intent getLaunchIntentForPackage(final Context context, final String[] packageNames) {
		final android.content.pm.PackageManager pm = context.getPackageManager();
		for (final String packageName : packageNames) {
			try {
				if (pm.getPackageInfo(packageName, 0) != null) {
					return pm.getLaunchIntentForPackage(packageName);
				}
			} catch (final NameNotFoundException e) {
			}
		}
		return null;
	}

	public static void launchGame(final Context context, final Game game) {
		final Intent intent = getLaunchIntentForPackage(context, game.getPackageNames());
		if (intent != null) {
			context.startActivity(intent);
		}
	}

	public static boolean isScoreloopAppInstalled(final Context context) {
		return getLaunchIntentForPackage(context, SCORELOOP_APP_PACKAGE_NAMES) != null;
	}

	public static boolean isGameInstalled(final Context context, final Game game) {
		return getLaunchIntentForPackage(context, game.getPackageNames()) != null;
	}

}
