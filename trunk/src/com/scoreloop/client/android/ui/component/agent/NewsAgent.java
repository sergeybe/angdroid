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
import java.util.List;
import java.util.Set;

import com.scoreloop.client.android.core.addon.RSSFeed;
import com.scoreloop.client.android.core.addon.RSSItem;
import com.scoreloop.client.android.core.model.Continuation;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.ValueStore;
import com.scoreloop.client.android.ui.framework.ValueStore.ValueSource;

public class NewsAgent implements ValueSource {

	public static final String[]	SUPPORTED_KEYS	= { Constant.NEWS_NUMBER_UNREAD_ITEMS, Constant.NEWS_FEED };

	private RSSFeed					_feed;

	@Override
	public boolean isRetrieving() {
		return _feed != null ? _feed.getState() == RSSFeed.State.PENDING : false;
	}

	@Override
	public void retrieve(final ValueStore valueStore) {
		if (_feed == null) {
			_feed = new RSSFeed(null);
		}
		_feed.reloadOnNextRequest();
		_feed.requestAllItems(new Continuation<List<RSSItem>>() {
			@Override
			public void withValue(final List<RSSItem> feed, final Exception failure) {
				if (feed == null) {
					return;
				}

				valueStore.putValue(Constant.NEWS_FEED, feed);

				int numberUnread = 0;
				for (final RSSItem item : feed) {
					if (!item.hasPersistentReadFlag()) {
						++numberUnread;
					}
				}
				valueStore.putValue(Constant.NEWS_NUMBER_UNREAD_ITEMS, numberUnread);
			}
		}, false, null);
	}

	@Override
	public void supportedKeys(final Set<String> keys) {
		Collections.addAll(keys, SUPPORTED_KEYS);
	}
}
