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

import com.scoreloop.client.android.core.controller.AchievementController;
import com.scoreloop.client.android.core.controller.AchievementsController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.core.model.Continuation;

public class AchievementsEngine implements RequestControllerObserver {

	private AchievementController		_achievementController;
	private AchievementsController		_achievementsController;
	private boolean						_isLoading;
	private boolean						_isSubmitting;
	private List<Continuation<Boolean>>	_loadContinuations		= new ArrayList<Continuation<Boolean>>();
	private List<Continuation<Boolean>>	_submitContinuations	= new ArrayList<Continuation<Boolean>>();

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

	public void checkHadInitialSync(final Continuation<Boolean> contination) {
		getAchievementsController().checkHadInitialSync(contination);
	}

	public boolean hasLoadedAchievements() {
		return getAchievementsController().getAchievements().size() > 0;
	}

	private void invokeLoadContinuations(final Boolean success, final Exception error) {
		final List<Continuation<Boolean>> continuations = _loadContinuations;
		_loadContinuations = new ArrayList<Continuation<Boolean>>();
		for (final Continuation<Boolean> continuation : continuations) {
			continuation.withValue(success, error);
		}
	}

	private void invokeSubmitContinuations(final Boolean success, final Exception error) {
		final List<Continuation<Boolean>> continuations = _submitContinuations;
		_submitContinuations = new ArrayList<Continuation<Boolean>>();
		for (final Continuation<Boolean> continuation : continuations) {
			continuation.withValue(success, error);
		}
	}

	public void loadAchievements(final boolean forceInitialSync, final Continuation<Boolean> continuation) {
		checkHadInitialSync(new Continuation<Boolean>() {
			@Override
			public void withValue(final Boolean hadInitialSync, final Exception error) {
				if (!hasLoadedAchievements() || (forceInitialSync && !hadInitialSync)) {
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
						continuation.withValue(true, null);
					}
				}
			}
		});

	}

	@Override
	public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
		if (aRequestController == _achievementsController) {
			_isLoading = false;
			invokeLoadContinuations(false, anException);
		} else if (aRequestController == _achievementController) {
			_isSubmitting = false;
			invokeSubmitContinuations(false, anException);
		}
	}

	@Override
	public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
		if (aRequestController == _achievementsController) {
			_isLoading = false;
			invokeLoadContinuations(true, null);
		} else if (aRequestController == _achievementController) {
			_isSubmitting = false;
			submitNextAchievement();
		}
	}

	public void submitAchievements(final boolean forceInitialSync, final Continuation<Boolean> continuation) {
		checkHadInitialSync(new Continuation<Boolean>() {
			@Override
			public void withValue(final Boolean hadInitialSync, final Exception error) {
				if (!hasLoadedAchievements() || (!hadInitialSync && forceInitialSync)) {
					loadAchievements(forceInitialSync, new Continuation<Boolean>() {
						@Override
						public void withValue(final Boolean success, final Exception error) {
							if (hasLoadedAchievements()) {
								submitAchievements(forceInitialSync, continuation);
							} else {
								// in case we had problems loading the achievements, we just run the submit continuations
								if (continuation != null) {
									_submitContinuations.add(continuation);
								}
								invokeSubmitContinuations(success, error);
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
		});
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
		invokeSubmitContinuations(true, null);
	}
}
