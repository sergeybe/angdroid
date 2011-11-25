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

public enum PagingDirection {
	PAGE_TO_NEXT(2), PAGE_TO_OWN(3), PAGE_TO_PREV(1), PAGE_TO_RECENT(4), PAGE_TO_TOP(0);

	private int	_flag;

	private PagingDirection(final int shift) {
		_flag = 1 << shift;
	}

	int combine(final int flags) {
		return flags | _flag;
	}

	boolean isPresentIn(final int flags) {
		return (flags & _flag) != 0;
	}
}
