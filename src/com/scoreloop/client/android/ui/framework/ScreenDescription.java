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

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

public class ScreenDescription {

	public static interface ShortcutObserver {
		void onShortcut(int textId);
	}

	private final List<ActivityDescription>	_bodyDescriptions		= new ArrayList<ActivityDescription>();
	private ActivityDescription				_headerDescription;
	private final ValueStore				_screenValues			= new ValueStore();
	private int								_selectedBodyIndex		= 0;
	private final List<ShortcutDescription>	_shortcutDescriptions	= new ArrayList<ShortcutDescription>();
	private ShortcutObserver				_shortcutObserver;
	private int								_shortcutSelectionId	= 0;

	public ActivityDescription addBodyDescription(final int tabId, final Intent intent) {
		final ActivityDescription description = new ActivityDescription(tabId, intent);
		_bodyDescriptions.add(description);
		return description;
	}

	public void addShortcutDescription(final int textId, final int imageId, final int activeImageId) {
		_shortcutDescriptions.add(new ShortcutDescription(textId, imageId, activeImageId));
	}

	public void addShortcutObserver(final ShortcutObserver observer) {
		_shortcutObserver = observer;
	}

	ActivityDescription getActivityDescription(final String id) {
		if ((_headerDescription != null) && _headerDescription.hasIdentfier(id)) {
			return _headerDescription;
		}
		for (final ActivityDescription bodyDescription : _bodyDescriptions) {
			if (bodyDescription.hasIdentfier(id)) {
				return bodyDescription;
			}
		}
		return null;
	}

	public List<ActivityDescription> getBodyDescriptions() {
		return _bodyDescriptions;
	}

	public ActivityDescription getHeaderDescription() {
		return _headerDescription;
	}

	public ValueStore getScreenValues() {
		return _screenValues;
	}

	public int getSelectedBodyIndex() {
		return _selectedBodyIndex;
	}

	public List<ShortcutDescription> getShortcutDescriptions() {
		return _shortcutDescriptions;
	}

	public ShortcutObserver getShortcutObserver() {
		return _shortcutObserver;
	}

	public int getShortcutSelectionId() {
		return _shortcutSelectionId;
	}

	public int getShortcutSelectionIndex() {
		if (_shortcutSelectionId != 0) {
			final int count = _shortcutDescriptions.size();
			for (int i = 0; i < count; ++i) {
				if (_shortcutSelectionId == _shortcutDescriptions.get(i).getTextId()) {
					return i;
				}
			}
		}
		return -1;
	}

	public ActivityDescription setBodyDescription(final Intent intent) {
		return addBodyDescription(0, intent);
	}

	public ActivityDescription setHeaderDescription(final Intent intent) {
		_headerDescription = new ActivityDescription(0, intent);
		return _headerDescription;
	}

	public void setSelectedBodyIndex(final int index) {
		_selectedBodyIndex = index;
	}

	public void setShortcutSelectionId(final int selectionId) {
		_shortcutSelectionId = selectionId;
	}
}
