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

package com.scoreloop.client.android.ui.component.achievement;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.scoreloop.client.android.core.controller.AchievementController;
import com.scoreloop.client.android.core.controller.AchievementsController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Achievement;

public class AchievementsEngine implements RequestControllerObserver {

	private AchievementController	_achievementController;
	private AchievementsController	_achievementsController;
	private boolean					_isLoading;
	private boolean					_isSubmitting;
	private List<Runnable>			_loadContinuations		= new ArrayList<Runnable>();
	private List<Runnable>			_submitContinuations	= new ArrayList<Runnable>();

	private AchievementController getAchievementController() {
		if (_achievementController == null) {
			_achievementController = new AchievementController(this);
		}
		return _achievementController;
	}

	public AchievementsController getAchievementsController() {
		if (_achievementsController == null) {
			_achievementsController = new AchievementsController(this);
		}
		return _achievementsController;
	}

	public boolean hadInitialSync() {
		return getAchievementsController().hadInitialSync();
	}

	public boolean hasLoadedAchievements() {
		return getAchievementsController().getAchievements().size() > 0;
	}

	private void invokeLoadContinuations() {
		final List<Runnable> continuations = _loadContinuations;
		_loadContinuations = new ArrayList<Runnable>();
		for (final Runnable continuation : continuations) {
			continuation.run();
		}
	}

	private void invokeSubmitContinuations() {
		final List<Runnable> continuations = _submitContinuations;
		_submitContinuations = new ArrayList<Runnable>();
		for (final Runnable continuation : continuations) {
			continuation.run();
		}
	}

	public void loadAchievements(final boolean forceInitialSync, final Runnable continuation) {
		if (!hasLoadedAchievements() || (forceInitialSync && !hadInitialSync())) {
			if (continuation != null) {
				_loadContinuations.add(continuation);
			}
			if (!_isLoading) {
				_isLoading = true;
				getAchievementsController().setForceInitialSync(forceInitialSync);
				getAchievementsController().loadAchievements();
			}
		} else {
			if (continuation != null) {
				continuation.run();
			}
		}
	}

	public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
		if (aRequestController == _achievementsController) {
			_isLoading = false;
			invokeLoadContinuations();
		} else if (aRequestController == _achievementController) {
			_isSubmitting = false;
			invokeSubmitContinuations();
		}
	}

	public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
		if (aRequestController == _achievementsController) {
			_isLoading = false;
			invokeLoadContinuations();
		} else if (aRequestController == _achievementController) {
			_isSubmitting = false;
			submitNextAchievement();
		}
	}

	public void submitAchievements(final boolean forceInitialSync, final Runnable continuation) {
		final AsyncTask<Void, Void, Boolean> loadHasInitialSyncTask = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				return hadInitialSync();
			}

			@Override
			protected void onPostExecute(Boolean hadInitialSync) {
				if (!hasLoadedAchievements() || (!hadInitialSync && forceInitialSync)) {
					loadAchievements(forceInitialSync, new Runnable() {
						public void run() {
							if (hasLoadedAchievements()) {
								submitAchievements(forceInitialSync, continuation);
							} else {
								// in case we had problems loading the achievements, we just run the submit continuations
								// NOTE: in the future change from Runnable to another interface which allows to pass the failure
								if (continuation != null) {
									_submitContinuations.add(continuation);
								}
								invokeSubmitContinuations();
							}
						}
					});
				} else {
					if (continuation != null) {
						_submitContinuations.add(continuation);
					}
					if (!_isSubmitting) {
						submitNextAchievement();
					}
				}
			}
		};
		// noinspection unchecked
		loadHasInitialSyncTask.execute();
	}

	private void submitNextAchievement() {
		for (final Achievement achievement : getAchievementsController().getAchievements()) {
			if (achievement.needsSubmit()) {
				getAchievementController().setAchievement(achievement);
				_isSubmitting = true;
				_achievementController.submitAchievement();
				return;
			}
		}

		// no achievement needs a submission, so invoke continuations
		invokeSubmitContinuations();
	}
}
