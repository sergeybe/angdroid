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

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.scoreloop.client.android.core.controller.RequestCancelledException;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.TermsOfServiceException;
import com.scoreloop.client.android.core.model.Game;
import com.scoreloop.client.android.core.model.Session;
import com.scoreloop.client.android.core.model.TermsOfService.Status;
import com.scoreloop.client.android.core.model.User;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.component.agent.BaseAgent;
import com.scoreloop.client.android.ui.framework.BaseActivity;
import com.scoreloop.client.android.ui.framework.ValueStore;

public abstract class ComponentActivity extends BaseActivity implements ComponentActivityHooks, BaseAgent.Delegate {

	private class StandardRequestControllerObserver implements RequestControllerObserver {

		@Override
		public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
			ComponentActivity.this.requestControllerDidFail(aRequestController, anException);
		}

		@Override
		public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
			ComponentActivity.this.requestControllerDidReceiveResponse(aRequestController);
		}
	}

	public static boolean isValueChangedFor(final String aKey, final String theKey, final Object oldValue, final Object newValue) {
		return aKey.equals(theKey) && ((newValue != null) && !newValue.equals(oldValue));
	}

	private RequestControllerObserver	_requestControllerObserver;

	private Dialog createErrorDialog(final int resId) {
		final Dialog dialog = new Dialog(getTopParent());
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		final View view = getLayoutInflater().inflate(R.layout.sl_dialog_custom, null);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		((TextView) view.findViewById(R.id.message)).setText(getString(resId));
		dialog.setOnDismissListener(this);
		return dialog;
	}

	public Configuration getConfiguration() {
		return getScreenValues().getValue(Constant.CONFIGURATION);
	}

	public Factory getFactory() {
		return getScreenValues().getValue(Constant.FACTORY);
	}

	public Game getGame() {
		final ValueStore gameValues = getGameValues();
		if (gameValues != null) {
			return getGameValues().getValue(Constant.GAME);
		}
		return null;
	}

	public ValueStore getGameValues() {
		return getScreenValues().getValue(Constant.GAME_VALUES);
	}

	public Manager getManager() {
		return getScreenValues().getValue(Constant.MANAGER);
	}

	public int getModeForPosition(final int position) {
		final Game game = getGame();
		return position + (game.hasModes() ? game.getMinMode() : 0);
	}

	public String getModeString(final int mode) {
		if (!getGame().hasModes()) {
			return ""; // return empty string if we don't have modes
		}
		return getConfiguration().getModesNames()[getPositionForMode(mode)];
	}

	public int getPositionForMode(final int mode) {
		final Game game = getGame();
		return game.hasModes() ? mode - game.getMinMode() : -1;
	}

	protected RequestControllerObserver getRequestControllerObserver() {
		if (_requestControllerObserver == null) {
			_requestControllerObserver = new StandardRequestControllerObserver();
		}
		return _requestControllerObserver;
	}

	public Session getSession() {
		return ScoreloopManagerSingleton.get().getSession();
	}

	public ValueStore getSessionGameValues() {
		return getScreenValues().getValue(Constant.SESSION_GAME_VALUES);
	}

	public User getSessionUser() {
		return getSession().getUser();
	}

	public ValueStore getSessionUserValues() {
		return getScreenValues().getValue(Constant.SESSION_USER_VALUES);
	}

	public Tracker getTracker() {
		return getScreenValues().getValue(Constant.TRACKER);
	}

	public User getUser() {
		return getUserValues().getValue(Constant.USER);
	}

	public ValueStore getUserValues() {
		return getScreenValues().getValue(Constant.USER_VALUES);
	}

	public boolean isSessionGame() {
		final Game game = getGame();
		return game != null ? game.equals(getSession().getGame()) : false;
	}

	public boolean isSessionUser() {
		final User user = getUser();
		if (getSession().getUsersTermsOfService().getStatus() != Status.ACCEPTED) {
			return false;
		}
		return user != null ? getSession().isOwnedByUser(user) : false;
	}

	@Override
	public void onAgentDidFail(final BaseAgent agent, final RequestController controller, final Exception error) {
		if (error instanceof TermsOfServiceException) {
			finishDisplay();
			return;
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case Constant.DIALOG_ERROR_NETWORK:
			return createErrorDialog(R.string.sl_error_message_network);
		default:
			return super.onCreateDialog(id);
		}
	}

	public final void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
		if (anException instanceof RequestCancelledException) {
			return;
		}

		if (anException instanceof TermsOfServiceException) {
			finishDisplay();
			return;
		}

		hideSpinnerFor(aRequestController);
		if (!isPaused()) {
			requestControllerDidFailSafe(aRequestController, anException);
		}
	}

	protected void requestControllerDidFailSafe(final RequestController aRequestController, final Exception anException) {
		showDialogForExceptionSafe(anException);
	}

	public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
		hideSpinnerFor(aRequestController);
		if (!isPaused()) {
			requestControllerDidReceiveResponseSafe(aRequestController);
		} else {
			setNeedsRefresh();
		}
	}

	@Override
	public void requestControllerDidReceiveResponseSafe(final RequestController aRequestController) {
		// intentionally empty - override in subclass
	}

	protected void showDialogForExceptionSafe(final Exception exception) {
		// override this in subclasses to take argument into account
		showDialogSafe(Constant.DIALOG_ERROR_NETWORK, true);
	}

	@Override
	protected void showDialogSafe(final int res, final boolean saveDialogState) {
		super.showDialogSafe(res, saveDialogState);
		if (!isPaused()) {
			getTracker().trackEvent(TrackerEvents.CAT_NAVI, String.format(TrackerEvents.NAVI_DIALOG, res), null, 0);
		}
	}
}
