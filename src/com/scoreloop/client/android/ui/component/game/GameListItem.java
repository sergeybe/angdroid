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

package com.scoreloop.client.android.ui.component.game;

import android.graphics.drawable.Drawable;

import com.scoreloop.client.android.core.model.Game;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.ComponentActivity;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.component.base.StandardListItem;

public class GameListItem extends StandardListItem<Game> {

	public GameListItem(final ComponentActivity context, final Drawable drawable, final Game game) {
		super(context, drawable, game.getName(), game.getPublisherName(), game);
	}

	@Override
	protected String getImageUrl() {
		return getTarget().getImageUrl();
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_GAME;
	}

	@Override
	public Drawable getDrawable() {
		return getContext().getResources().getDrawable(R.drawable.sl_icon_games);
	}
}
