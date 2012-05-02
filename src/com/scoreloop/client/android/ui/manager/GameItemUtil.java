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

package com.scoreloop.client.android.ui.manager;

import com.scoreloop.client.android.core.controller.GameItemController;
import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.model.Continuation;
import com.scoreloop.client.android.core.model.GameItem;
import com.scoreloop.client.android.core.model.Session;

public class GameItemUtil {
	public static void withGameItemForIdentifier(final Session session, final String gameItemIdentifier,
			final Continuation<GameItem> continuation) {
		final GameItemController controller = new GameItemController(session, new RequestControllerObserver() {
			@Override
			public void requestControllerDidFail(final RequestController aRequestController, final Exception anException) {
				continuation.withValue(null, anException);
			}

			@Override
			public void requestControllerDidReceiveResponse(final RequestController aRequestController) {
				continuation.withValue(((GameItemController) aRequestController).getGameItem(), null);
			}
		});
		controller.setCachedResponseUsed(false);
		controller.setGameItem(session.getEntityFactory().createEntity(GameItem.ENTITY_NAME, gameItemIdentifier));
		controller.loadGameItem();
	}
}
