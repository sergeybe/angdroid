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

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.scoreloop.client.android.ui.framework.BaseListAdapter;
import com.scoreloop.client.android.ui.framework.BaseListItem;

public abstract class Constant {

	public static final String	LOG_TAG									= "ScoreloopUI";

	public static final String	ACHIEVEMENTS_ENGINE						= "achievementsEngine";
	public static final String	NAVIGATION_INTENT						= "navigationIntent";
	public static final String	NAVIGATION_DIALOG_CONTINUATION			= "navigationDialogContinuation";
	public static final String	NAVIGATION_ALLOWED						= "navigationAllowed";
	public static final String	CHALLENGE								= "challenge";
	public static final String	CONFIGURATION							= "configuration";
	public static final String	CONTESTANT								= "contestant";
	public static final String	FACTORY									= "factory";
	public static final String	FEATURED_GAME							= "featuredGame";
	public static final String	FEATURED_GAME_IMAGE_URL					= "featuredGameImageUrl";
	public static final String	FEATURED_GAME_NAME						= "featuredGameName";
	public static final String	FEATURED_GAME_PUBLISHER					= "featuredGamePublisher";
	public static final String	GAME									= "game";
	public static final String	GAME_HEADER_CONTROL						= "gameHeaderControl";
	public static final String	GAME_IMAGE_URL							= "gameImageUrl";
	public static final String	GAME_LIST_MODE							= "gameListMode";
	public static final int		GAME_MODE_BUDDIES						= 3;
	public static final int		GAME_MODE_LAST							= 4;
	public static final int		GAME_MODE_NEW							= 2;
	public static final int		GAME_MODE_POPULAR						= 1;
	public static final int		GAME_MODE_USER							= 0;
	public static final String	GAME_NAME								= "gameName";
	public static final String	GAME_PUBLISHER							= "gamePublisher";
	public static final String	GAME_VALUES								= "gameValues";

	public static final String	IMAGE_URL_BUDDIES_GAMES					= "imageUrlBuddiesGames";
	public static final String	IMAGE_URL_NEW_GAMES						= "imageUrlNewGames";
	public static final String	IMAGE_URL_USER_GAMES					= "imageUrlUserGames";
	public static final String	IMAGE_URL_POPULAR_GAMES					= "imageUrlPopularGames";

	/* start: dialog ids ares used by google analytics --- PLEASE DO NOT CHANGE --- */
	public static final int		DIALOG_ERROR_NETWORK					= 0;
	public static final int		DIALOG_CHALLENGE_ERROR_BALANCE			= 1;
	public static final int		DIALOG_CHALLENGE_ERROR_ACCEPT			= 2;
	public static final int		DIALOG_CHALLENGE_ERROR_REJECT			= 3;
	public static final int		DIALOG_CHALLENGE_LEAVE_ACCEPT			= 4;
	public static final int		DIALOG_CHALLENGE_ONGOING				= 5;
	public static final int		DIALOG_CHALLENGE_GAME_NOT_READY			= 6;
	public static final int		DIALOG_CHALLENGE_LEAVE_PAYMENT			= 7;
	public static final int		DIALOG_CONFIRMATION_MATCH_BUDDIES		= 10;
	public static final int		DIALOG_CONFIRMATION_RECOMMEND_GAME		= 11;
	public static final int		DIALOG_PROFILE_CHANGE_USERNAME			= 12;
	public static final int		DIALOG_PROFILE_CHANGE_EMAIL				= 13;
	public static final int		DIALOG_PROFILE_FIRST_TIME				= 14;
	public static final int		DIALOG_PROFILE_MSG						= 15;
	public static final int		DIALOG_PROFILE_MERGE_ACCOUNTS			= 17;
	public static final int		DIALOG_GAME_MODE						= 18;
	public static final int		DIALOG_ADD_FRIEND_LOGIN					= 19;
	/* end: dialog ids ares used by google analytics --- PLEASE DO NOT CHANGE --- */

	public static final int		LEADERBOARD_LOCAL						= 3;
	public static final String	IS_LOCAL_LEADEARBOARD					= "isLocalLeadearboard";

	public static final int		LIST_ITEM_TYPE_ACHIEVEMENT				= 1;
	public static final int		LIST_ITEM_TYPE_CAPTION					= 2;
	public static final int		LIST_ITEM_TYPE_CHALLENGE_CONTROLS		= 3;
	public static final int		LIST_ITEM_TYPE_CHALLENGE_HISTORY		= 4;
	public static final int		LIST_ITEM_TYPE_CHALLENGE_NEW			= 5;
	public static final int		LIST_ITEM_TYPE_CHALLENGE_OPEN			= 6;
	public static final int		LIST_ITEM_TYPE_CHALLENGE_PARTICIPANTS	= 7;
	public static final int		LIST_ITEM_TYPE_CHALLENGE_STAKE_AND_MODE	= 9;
	public static final int		LIST_ITEM_TYPE_ENTRY					= 10;
	public static final int		LIST_ITEM_TYPE_EXPANDABLE				= 11;
	public static final int		LIST_ITEM_TYPE_GAME						= 12;
	public static final int		LIST_ITEM_TYPE_GAME_DETAIL				= 13;
	public static final int		LIST_ITEM_TYPE_GAME_DETAIL_USER			= 14;
	public static final int		LIST_ITEM_TYPE_NEWS						= 15;
	public static final int		LIST_ITEM_TYPE_EMPTY					= 16;
	public static final int		LIST_ITEM_TYPE_PAGING					= 0;								// must be 0!
	public static final int		LIST_ITEM_TYPE_PROFILE					= 17;
	public static final int		LIST_ITEM_TYPE_PROFILE_PICTURE			= 18;
	public static final int		LIST_ITEM_TYPE_SCORE					= 19;
	public static final int		LIST_ITEM_TYPE_SCORE_EXCLUDED			= 20;
	public static final int		LIST_ITEM_TYPE_SCORE_HIGHLIGHTED		= 21;
	public static final int		LIST_ITEM_TYPE_SCORE_SUBMIT_LOCAL		= 22;
	public static final int		LIST_ITEM_TYPE_STANDARD					= 23;
	public static final int		LIST_ITEM_TYPE_USER						= 24;
	public static final int		LIST_ITEM_TYPE_USER_ADD_BUDDIES			= 25;
	public static final int		LIST_ITEM_TYPE_USER_ADD_BUDDY			= 26;
	public static final int		LIST_ITEM_TYPE_USER_DETAIL				= 27;
	public static final int		LIST_ITEM_TYPE_USER_FIND_MATCH			= 28;
	public static final int		LIST_ITEM_TYPE_X_COUNT					= 29;

	public static final String	MANAGER									= "manager";
	public static final long	MARKET_REFRESH_TIME						= 300 * 1000;						// 300 secods in milliseconds
	public static final String	MODE									= "mode";							// should be Integer
	public static final String	CAPTION_VISIBLE							= "captionVisible";				// Boolean
	public static final String	NEWS_FEED								= "newsFeed";
	public static final long	NEWS_FEED_REFRESH_TIME					= 30 * 1000;						// 30 seconds in milliseconds
	public static final String	NEWS_NUMBER_UNREAD_ITEMS				= "newsNumberUnreadItems";
	public static final String	NUMBER_ACHIEVEMENTS						= "numberAchievements";			// Integer
	public static final String	NUMBER_AWARDS							= "numberAwards";					// Integer
	public static final String	NUMBER_BUDDIES							= "numberBuddies";					// Integer
	public static final String	NUMBER_CHALLENGES_PLAYED				= "numberChallengesPlayed";		// Integer
	public static final String	NUMBER_CHALLENGES_WON					= "numberChallengesWon";			// Integer
	public static final String	NUMBER_GAMES							= "numberGames";					// Integer
	public static final String	NUMBER_GLOBAL_ACHIEVEMENTS				= "numberGlobalAchievements";		// Integer
	public static final String	SEARCH_LIST								= "searchList";
	public static final String	SESSION_GAME_VALUES						= "sessionGameValues";
	public static final String	SESSION_USER_VALUES						= "sessionUserValues";
	public static final String	TRACKER									= "tracker";
	public static final String	USER									= "user";
	public static final String	USER_BALANCE							= "userBalance";
	public static final String	USER_BUDDIES							= "userBuddies";					// List of User
	public static final String	USER_IMAGE_URL							= "userImageUrl";
	public static final String	USER_NAME								= "userName";
	public static final String	USER_PLAYS_SESSION_GAME					= "userPlaysSessionGame";			// Boolean:null=undefined,true=plays
	public static final String	USER_VALUES								= "userValues";

	private static final int	MIN_RANGE_LENGTH						= 10;

	public static final String	GAME_ITEM_ID							= "gameItemId";

	public static final int		PAYMENT_UI_LEAVE						= 0x01;
	public static final int		PAYMENT_TOAST_SHOW						= 0x10;

	public static void setup() {
		BaseListAdapter.setViewTypeCount(LIST_ITEM_TYPE_X_COUNT);
	}

	public static int getOptimalRangeLength(final View listView, final BaseListItem item) {
		final View itemView = item.getView(null, null);
		itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int listHeight = listView.getHeight();
		if (listHeight == 0) {
			// fallback code if listView was not layed out yet - which is the norm in onCreate but also in onStart :-(
			final Display display = ((WindowManager) listView.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			listHeight = display.getHeight();
		}
		final int itemHeight = itemView.getMeasuredHeight();
		return Math.max(MIN_RANGE_LENGTH, (listHeight / itemHeight) + 1);
	}
}
