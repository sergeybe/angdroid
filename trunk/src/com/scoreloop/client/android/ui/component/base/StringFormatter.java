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

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.scoreloop.client.android.core.addon.RSSItem;
import com.scoreloop.client.android.core.model.*;
import com.scoreloop.client.android.core.model.ScoreFormatter.ScoreFormatKey;
import org.angdroid.angband.R;
import com.scoreloop.client.android.ui.framework.ValueStore;

public class StringFormatter {

	private static final BigDecimal	CENTS_FOR_UNIT		= new BigDecimal(100);	// TODO: remove in 3.0 and use new method provided
	private static final int		ROUND_TO_PERCENT	= 5;

	public static String formatChallengesScore(final Score score, final Configuration configuration) {
		final ScoreFormatKey format = configuration.getChallengesScoreFormat();
		final ScoreFormatter scoreFormatter = configuration.getScoreFormatter();

		if (format == null) {
			return formatScore(score, configuration);
		}

		return scoreFormatter.formatScore(score, format);
	}

	public static String formatLeaderboardsScore(final Score score, final Configuration configuration) {
		final ScoreFormatKey format = configuration.getLeaderboardScoreFormat();
		final ScoreFormatter scoreFormatter = configuration.getScoreFormatter();

		if (format == null) {
			return formatScore(score, configuration);
		}

		return scoreFormatter.formatScore(score, format);
	}

	public static String formatMoney(final Money money, final Configuration configuration) {
		final BigDecimal cents = money.getAmount();
		final BigDecimal units = cents.divide(CENTS_FOR_UNIT);

		String currencyName = Money.getApplicationCurrencyNamePlural();
		if (units.equals(BigDecimal.ONE)) {
			currencyName = Money.getApplicationCurrencyNameSingular();
		}

		return String.format(configuration.getMoneyFormat(), units, currencyName);
	}

	public static String formatRanking(final Context context, final Ranking ranking, final Configuration configuration) {
		if (ranking == null) {
			return "";
		}
		final int total = ranking.getTotal();
		if (total == 0) {
			return "";
		}
		final int percent = ranking.getRank() * 100 / total;
		int roundedPercent = 0;
		if (percent == 0) {
			roundedPercent = 1;
		} else if (percent == 100) {
			roundedPercent = 100;
		} else {
			roundedPercent = (percent + ROUND_TO_PERCENT) / ROUND_TO_PERCENT * ROUND_TO_PERCENT;
		}
		return String.format(context.getString(R.string.sl_format_leaderboards_percent), roundedPercent);
	}

	public static String formatScore(final Score score, final Configuration configuration) {
		final String scoreResultFormat = configuration.getScoreResultFormat();
		if (scoreResultFormat != null) {
			// backward compatibility
			return String.format(scoreResultFormat, score.getResult());
		}
		final ScoreFormatter scoreFormatter = configuration.getScoreFormatter();
		return scoreFormatter.formatScore(score, ScoreFormatKey.ScoresOnlyFormat);
	}

	public static String formatSocialNetworkPostScore(final Score score, final Configuration configuration) {
		final ScoreFormatKey format = configuration.getSocialNetworkPostScoreFormat();
		final ScoreFormatter scoreFormatter = configuration.getScoreFormatter();

		if (format == null) {
			return formatScore(score, configuration);
		}

		return scoreFormatter.formatScore(score, format);
	}

	public static String getAchievementIncrementTitle(final Context context, final Achievement achievement,
			final Configuration configuration) {
		final Range range = achievement.getAward().getCounterRange();
		if (range.getLength() > 1) {
			final int percent = (achievement.getValue() - range.getLocation()) * 100 / range.getLength();
			return String.format(context.getString(R.string.sl_format_achievement_increment), percent);
		}
		return "";
	}

	public static String getAchievementRewardTitle(final Context context, final Achievement achievement, final Configuration configuration) {
		final Money reward = achievement.getAward().getRewardMoney();
		if ((reward != null) && reward.hasAmount()) {
			return StringFormatter.formatMoney(reward, configuration);
		}
		return "";
	}

	public static String getAchievementsSubTitle(final Context context, final ValueStore userValues, final boolean extendedText) {
		final Integer numAchieved = userValues.<Integer> getValue(Constant.NUMBER_ACHIEVEMENTS);
		final Integer numTotal = userValues.<Integer> getValue(Constant.NUMBER_AWARDS);
		if ((numAchieved != null) && (numTotal != null)) {
			final int resId = extendedText ? R.string.sl_format_achievements_extended : R.string.sl_format_achievements;
			return String.format(context.getString(resId), numAchieved, numTotal);
		}
		return "";
	}

	public static String getBalanceSubTitle(final Context context, final ValueStore userValues, final Configuration configuration) {
		final Money balance = userValues.<Money> getValue(Constant.USER_BALANCE);
		if (balance != null) {
			return String.format(context.getString(R.string.sl_format_balance), formatMoney(balance, configuration));
		}
		return "";
	}

	public static String getBuddiesSubTitle(final Context context, final ValueStore userValues) {
		final Integer count = userValues.<Integer> getValue(Constant.NUMBER_BUDDIES);
		if (count != null) {
			return String.format(context.getString(R.string.sl_format_number_friends), count);
		}
		return "";
	}

	public static String getChallengesSubTitle(final Context context, final ValueStore userValues) {
		final Integer numWon = userValues.<Integer> getValue(Constant.NUMBER_CHALLENGES_WON);
		final Integer numTotal = userValues.<Integer> getValue(Constant.NUMBER_CHALLENGES_PLAYED);
		if ((numWon != null) && (numTotal != null)) {
			return String.format(context.getString(R.string.sl_format_challenge_stats), numWon, numTotal);
		}
		return "";
	}

	public static String getGamesSubTitle(final Context context, final ValueStore userValues) {
		final Integer count = userValues.<Integer> getValue(Constant.NUMBER_GAMES);
		if (count != null) {
			return String.format(context.getString(R.string.sl_format_number_games), count);
		}
		return "";
	}

	public static Drawable getNewsDrawable(final Context context, final ValueStore userValues, final boolean large) {
		final List<RSSItem> feed = userValues.getValue(Constant.NEWS_FEED);
		final Integer count = userValues.getValue(Constant.NEWS_NUMBER_UNREAD_ITEMS);
		int id = 0;
		if ((feed == null) || (count == null) || (feed.size() == 0) || (count > 0)) {
			id = large ? R.drawable.sl_header_icon_news_closed : R.drawable.sl_icon_news_closed;
		} else {
			id = large ? R.drawable.sl_header_icon_news_opened : R.drawable.sl_icon_news_opened;
		}
		return context.getResources().getDrawable(id);
	}

	public static String getNewsSubTitle(final Context context, final ValueStore userValues) {
		final Integer count = userValues.<Integer> getValue(Constant.NEWS_NUMBER_UNREAD_ITEMS);
		if (count != null) {
			if (count == 0) {
				final List<RSSItem> feed = userValues.getValue(Constant.NEWS_FEED);
				if ((feed == null) || (feed.size() == 0)) {
					return context.getString(R.string.sl_no_news);
				}
				return context.getString(R.string.sl_no_news_items);
			} else if (count == 1) {
				return context.getString(R.string.sl_one_news_item);
			} else {
				return String.format(context.getString(R.string.sl_format_new_news_items), count);
			}
		}
		return "";
	}

	public static String getScoreTitle(final Context context, final Score score) {
		final Integer rank = score.getRank();
		User user = score.getUser();
		if (user == null) {
			// if the score comes from the rank, it will not have a user object, so use the session user
			user = Session.getCurrentSession().getUser();
		}
		return String.format(context.getString(R.string.sl_format_score_title), rank, user.getDisplayName());
	}
}
