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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scoreloop.client.android.core.model.Game;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.component.base.Constant;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public class GameDetailListItem extends BaseListItem {

	private final Game	_game;

	public GameDetailListItem(final Context context, final Drawable drawable, final Game game) {
		super(context, drawable, game.getDescription());
		_game = game;
	}

	@Override
	public int getType() {
		return Constant.LIST_ITEM_TYPE_GAME_DETAIL;
	}

	@Override
	public View getView(View view, final ViewGroup parent) {
		if (view == null) {
			view = getLayoutInflater().inflate(R.layout.sl_list_item_game_detail, null);
		}
		final TextView detail = (TextView) view.findViewById(R.id.sl_list_item_game_detail_text);
		String description = _game.getDescription();
		if (description == null) {
			description = _game.getPublisherName();
		}
		if (description != null) {
			detail.setText(description.replace("\r", ""));
		}
		return view;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

}
