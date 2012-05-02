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

public class ShortcutDescription {

	private final int	_activeImageId;
	private final int	_imageId;
	private final int	_textId;

	public ShortcutDescription(final int textId, final int imageId, final int activeImageId) {
		_textId = textId;
		_imageId = imageId;
		_activeImageId = activeImageId;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ShortcutDescription other = (ShortcutDescription) obj;
		if (_activeImageId != other._activeImageId) {
			return false;
		}
		if (_imageId != other._imageId) {
			return false;
		}
		if (_textId != other._textId) {
			return false;
		}
		return true;
	}

	public int getActiveImageId() {
		return _activeImageId;
	}

	public int getImageId() {
		return _imageId;
	}

	public int getTextId() {
		return _textId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + _activeImageId;
		result = (prime * result) + _imageId;
		result = (prime * result) + _textId;
		return result;
	}
}
