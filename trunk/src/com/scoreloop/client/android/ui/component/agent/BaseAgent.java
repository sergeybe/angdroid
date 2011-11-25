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

package com.scoreloop.client.android.ui.component.agent;

import java.util.Collections;
import java.util.Set;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.ui.framework.ValueStore;
import com.scoreloop.client.android.ui.framework.ValueStore.ValueSource;

public abstract class BaseAgent implements ValueSource, RequestControllerObserver {

	private boolean			_isRetrieving;
	private final String[]	_keys;
	private ValueStore		_valueStore;

	protected BaseAgent(final String... keys) {
		_keys = keys;
	}

	public boolean isRetrieving() {
		return _isRetrieving;
	}

	protected abstract void onFinishRetrieve(RequestController aRequestController, ValueStore valueStore);

	protected abstract void onStartRetrieve(ValueStore valueStore);

	protected void putValue(final String key, final Object value) {
		_valueStore.putValue(key, value);
	}

	public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
		_isRetrieving = false;
	}

	public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
		_isRetrieving = false;
		onFinishRetrieve(aRequestController, _valueStore);
	}

	public void retrieve(final ValueStore valueStore) {
		_isRetrieving = true;
		_valueStore = valueStore;
		onStartRetrieve(valueStore);
	}

	public void supportedKeys(final Set<String> keys) {
		Collections.addAll(keys, _keys);
	}

}
