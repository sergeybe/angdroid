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

import android.os.Bundle;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.ui.framework.ValueStore;

/**
 * \internal
 * This interface describes the main entry points for Component-Activity subclasses.
 */
public interface ComponentActivityHooks {

	/**
	 * Construct controllers and observe values from the stores.
	 */
	void onCreate(Bundle savedInstanceState);

	/**
	 * When you indicated a refresh via setNeesRefresh(), this entry-point gets called while the activity is resumed.
	 * Trigger Scoreloop controller requests here.
	 */
	void onRefresh(int flags);

	/**
	 * Called for every observed key when onResume() is called and, while being resumed,  
	 * when an observed value in a value-store got retrieved or was changed.
	 * 
	 * Either update your UI or make some controller requests (directly or indirectly via setNeedsRefresh()).
	 * setNeesRefresh() has the advantage, that several successive {@link #onValueChanged(ValueStore, String, Object, Object)}
	 * calls will only lead to one call of {@link #onRefresh(int)}.
	 * @param valueStore a value store
	 */
	void onValueChanged(ValueStore valueStore, String key, Object oldValue, Object newValue);

	/**
	 * Called for every observed key when onResume() is called and, while being resumed,  
	 * when someone has marked an observed value as dirty.
	 * 
	 * You will probably want to re-retrieve your observed values from the store and handle updates on
	 * them in {@link #onValueChanged(ValueStore, String, Object, Object)}
	 * @param valueStore a value store
	 */
	void onValueSetDirty(ValueStore valueStore, String key);

	/**
	 * When a scoreloop controller returns, this method is called which ensures, that the activity is not paused but
	 * in the resumed state.
	 * If a controller response is received outside the resumed state, the setNeedsRefresh() flag is set again 
	 * automatically. 
	 */
	void requestControllerDidReceiveResponseSafe(RequestController aRequestController);
}
