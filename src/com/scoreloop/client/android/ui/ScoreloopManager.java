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

package com.scoreloop.client.android.ui;

import java.util.List;
import java.util.Set;

import android.app.Activity;

import com.scoreloop.client.android.core.model.Achievement;
import com.scoreloop.client.android.core.model.AwardList;
import com.scoreloop.client.android.core.model.Continuation;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.Session;

/**
 * The ScoreloopManager is the general managing class for ScoreloopUI. A single instance of
 * ScoreloopManager is shared by all activities in the application. The ScoreloopManager
 * should be instantiated from within the <b>Android application</b> class. The shared
 * ScoreloopManager instance should persist for the lifecycle of the game. The
 * ScoreloopManagerSingleton class is used to intialize instances of ScoreloopManager.
 * 
 * Basic Usage:\n
 * -# Use the ScoreloopManagerSingleton class to initialize the ScoreloopManager.
 * -# Access the ScoreloopManager using ScoreloopManagerSingleton.get().
 * -# Invoke the desired ScoreloopManager method.
 *
 *  \sa ScoreloopManagerSingleton
 */
public interface ScoreloopManager {

	/**
	 * This method is used to "unlock" an award and to automatically set it
	 * as having been achieved by the session user. The method can be used
	 * to specify whether a toast message should be displayed and also
	 * whether the achieved award should be submitted to the Scoreloop
	 * servers straight away.
	 * 
	 * Note that loadAchievements() must first have
	 * been called before this method is invoked, otherwise a
	 * <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">
	 * java.lang.illegalStateException</a> will be thrown.
	 * 
	 * @param awardId a valid award identifier as specified on the developer site.
	 * @param showToast true if you want the ScoreloopUI show a toast when the award gets achieved.
	 * @param submitNow true if you want to submit the new achievement immediately. You might not want to do this during gameplay as it results in a communication being built up.
	 */
	void achieveAward(String awardId, boolean showToast, boolean submitNow);

	/**
	 * If the user rejected the Scoreloop Terms of Services you can use this method to ask the user again to make
	 * up his mind in order that the Scoreloop functionality gets enabled.
	 * 
	 * @param activity
	 * @param continuation
	 */
	void askUserToAcceptTermsOfService(Activity activity, Continuation<Boolean> continuation);

	/**
	 * This method returns the Achievement object
	 * for a given Award identifier. The award
	 * identifier is chosen by the developer and
	 * configured on https://developer.scoreloop.com.
	 * 
	 * Note that the loadAchievements() method
	 * must first be called, before this method is invoked
	 * otherwise a <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">java.lang.IllegalStateException</a> will be thrown.
	 *
	 * @param awardId An award identifier as specified by the developer on https://developer.scoreloop.com
	 * @return An Achievement object
	 */
	Achievement getAchievement(String awardId);

	/**
	 * This method Returns the list of all achievements
	 * that have been requested from the server by calling loadAchievements().
	 * 
	 * If this method is called before loadAchievements()
	 * then a <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">java.lang.IllegalStateException</a> will be thrown.
	 * @return <a href="http://download.oracle.com/javase/6/docs/api/java/util/List.html">
	 * java.util.List<Achievement></a> A list of Achievement objects.
	 */
	List<Achievement> getAchievements();

	/**
	 * Accessor to the award list of this game. To access individual Award objects in the list you should:
	 * - Call getAwardList().
	 * - Use the returned AwardList object to call AwardList.getAwards(). This returns a
	 * <a href="http://download.oracle.com/javase/6/docs/api/java/util/List.html">java.util.List\<Award\></a>, from
	 * which individual Award objects can be accessed.
	 * 
	 * @return An AwardList object
	 */
	AwardList getAwardList();

	/**
	 * Returns a URL to the game item's content via a callback.
	 * @param gameItemIdentifier an identifier to the game item
	 * @param continuation callback that returns requested URL
	 */
	void getGameItemDownloadUrl(String gameItemIdentifier, Continuation<String> continuation);

	/**
	 * Returns some information about the installed Scoreloop SDK.
	 * \return <a href="http://download.oracle.com/javase/6/docs/api/java/lang/String.html">java.lang.String</a>
	 */
	String getInfoString();

	/**
	 * Retrieves the mode names from configuration of ScoreFormatter
	 * The modes are formatted with ModeOnlyFormat
	 * For backward compatibility the property "ui.res.modes.name" from scoreloop.properties is evaluated
	 * @return The mode names
	 */
	String[] getModeNames();

	/**
	 * This method is used to check whether the list of achievements
	 * have been successfully requested from the server.
	 * @return true if the list of achievements were successfully loaded, otherwise @c false.
	 */
	boolean hasLoadedAchievements();

	/**
	 * Checks if there is a payment process in process for a game item.
	 *
	 * @param identifier a game identifier
	 * @return true if there is an ongoing payment process, false otherwise
	 */
	Boolean hasPendingPaymentForGameItemWithIdentifier(String identifier);

	/**
	 * Use this method to increment the value of an award by one. If the achieving value is reached, a toast will be shown (optionally) and
	 * the award will be uploaded to Scoreloop (also optionally).
	 * 
	 * Note that loadAchievements() must first have
	 * been called before this method is invoked, otherwise a
	 * <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">
	 * java.lang.illegalStateException</a> will be thrown.
	 * 
	 * @param awardId a valid award identifier as specified on the developer site.
	 * @param showToast true if you want the ScoreloopUI show a toast when the award gets achieved.
	 * @param submitNow true if you want to submit the new achievement immediately. You might not want to do this during gameplay as it results in a communication being built up.
	 * @return true if the award was achieved by incrementing the value.
	 */
	boolean incrementAward(String awardId, boolean showToast, boolean submitNow);

	/**
	 * This method is used to query whether an award with a given id
	 * has been achieved or not. The award identifier is defined
	 * by the developer and configured on https://developer.scoreloop.com.
	 * Note that loadAchievements() must have first been called
	 * before this method can be invoked, otherwise a
	 * <a href="http://download.oracle.com/javase/6/docs/api/java/lang/IllegalStateException.html">
	 * java.lang.IllegalStateException</a> will be thrown.
	 * @param awardId The identifier for the award specified on the developer web site.
	 * @return true if the award has been achieved, otherwise @c false.
	 */
	boolean isAwardAchieved(String awardId);

	/**
	 * This method loads a list of Scoreloop
	 * achievement objects from the server.
	 * Once this method
	 * has been called (and after a successful repsonse has been
	 * received from the server) the following methods can be used:
	 * - hasLoadedAchievements(), (to check whether the load
	 * operation was successful),
	 * - getAchievements(), (to access the returned list),
	 * - getAchievement(String), (to access a single named achievement),
	 * - isAwardAchieved(String), (to check whether a specific award has been achieved),
	 * - achieveAward(String, boolean, boolean).
	 * 
	 * @param continuation A continuation that gets called when
	 * the loading of achievements completes with or without errors.
	 * This can be  be @c null.
	 *
	 * Normally, a Scoreloop server request is made on the first call to this method after an installation of the game.
	 * By setting the ui.feature.achievement.forceSync property in scoreloop.properties to false, loadAchievements() will not make this initial server request
	 * which has pros and cons. As a pro, you can use the achievements without network access (you still
	 * have to call loadAchievements() though but this will return the achievements from the local store only.
	 * As a cons, if the game was deleted and reinstalled, the returned
	 * achievements will not know about previous achievements before submitAchievement() is called.
	 */
	void loadAchievements(Continuation<Boolean> continuation);

	/**
	 * This method is used to submit a score to Scoreloop.
	 * It should be called once the game play
	 * activity has ended. This method will automatically
	 * check whether a challenge is underway and, if so, submit
	 * the score as part of the challenge. The method will also
	 * automatically detect whether the score should be submitted
	 * on behalf of the challenge contender or the challenge contestant.
	 *
	 * If no challenge is currently underway, the score will be submitted
	 * to Scoreloop on behalf of the session user in the standard way.
	 *
	 * After submitting the score, @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.onScoreSubmit(final int, final Exception) OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)@endlink will be called.
	 * 
	 * This method always tries to submit the score to the remote Scoreloop servers. If this will not be possible due to connection problems,
	 * the score will be stored in the local (offline) leaderboards list instead. You might then submit the best score of the local leaderboard
	 * some time later.
	 *
	 * \sa @link scoreloopui-integratescores Submitting Scores to Scoreloop@endlink
	 *
	 * @param scoreResult A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Double.html">java.lang.Double</a> object representing the
	 * score result obtained by the user in the game.
	 * @param mode A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Integer.html">java.lang.Integer</a> representing the mode at which the score was obtained. If the game does not support modes, pass @c null here instead.
	 */
	void onGamePlayEnded(Double scoreResult, Integer mode);

	/**
	 * 
	 * This method is used to submit a score object to Scoreloop
	 * and should be used when the score to be submitted has multiple components, such as
	 * the:
	 * - result (mandatory),
	 * - minor result, (optional),
	 * - level, (optional),
	 * - mode, (optional).
	 *
	 * This method should be called once the game play
	 * activity has ended. This method will automatically
	 * check whether a challenge is underway and, if so, submit
	 * the score as part of the challenge. The method will also
	 * automatically detect whether the score should be submitted
	 * on behalf of the challenge contender or the challenge contestant.
	 *
	 * If no challenge is currently underway, the score will be submitted
	 * to Scoreloop on behalf of the session user in the standard way.
	 *
	 * After submitting the score, @link com.scoreloop.client.android.ui.OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)OnScoreSubmitObserver.onScoreSubmit(final int, final Exception)@endlink will be called.
	 *
	 * This method allows you to specify whether the score should be submitted to the local (offline) leaderboard only. You might then submit
	 * the best score of the local leaderboard some time later. Pass null or false if you want the score to be submitted to the Scoreloop servers.
	 * Note, that scores which are part of a challenge are always submitted remotely.
	 * 
	 * \sa @link scoreloopui-integratescores Submitting Scores to Scoreloop@endlink for details about how to
	 * create complex Score objects.
	 *
	 * @param score A Score object representing the score result obtained by the user in the game.
	 * @param submitLocallyOnly A Boolean which when true will indicate that the score should be submitted to the local (offline) leaderaboard list only.
	 * Pass null or false to send a score to the Scoreloop servers.
	 */
	void onGamePlayEnded(Score score, Boolean submitLocallyOnly);

	/**
	 * Use this method to prevent that the Terms of Service dialog is presented at a place where you don't want it to come up.
	 * Note, that in this case no Scoreloop functionality will be available until you pass true to another call of this method again.
	 * 
	 * @param ask false, to prevent the Terms of Service dialog to be shown.
	 */
	void setAllowToAskUserToAcceptTermsOfService(boolean ask);

	/**
	 * This method correctly sets an OnCanStartGamePlayObserver
	 * in ScoreloopManager. This observer must be set if the challenges feature has been
	 * enabled in the game.
	 * 
	 * @param observer A valid observer. Pass @c null to remove
	 * the observer.
	 * \sa OnCanStartGamePlayObserver
	 */
	void setOnCanStartGamePlayObserver(OnCanStartGamePlayObserver observer);

	/**
	 * This method sets an OnPaymentChangedObserver in ScoreloopManager.
	 * Set this observer to get informed about processed payment changes.
	 *
	 * @param observer A valid observer. Pass @c null to remove
	 * the observer
	 * \sa OnPaymentChangedObserver
	 */
	void setOnPaymentChangedObserver(OnPaymentChangedObserver observer);

	/**
	 * This method correctly sets an OnScoreSubmitObserver in
	 * ScoreloopManager.
	 * Set this observer to get informed about score submissions,
	 * (if the submission to the server succeeded or failed).
	 *
	 * @param observer A valid observer. Pass @c null to remove
	 * the observer.
	 * \sa OnScoreSubmitObserver
	 */
	void setOnScoreSubmitObserver(OnScoreSubmitObserver observer);

	/**
	 * This method correctly sets an OnStartGamePlayRequestObserver
	 * in ScoreloopManager.
	 * This observer must be set if the challenges feature has been
	 * enabled in the game.
	 * 
	 * @param observer a valid observer. Pass @c null to remove the observer.
	 */
	void setOnStartGamePlayRequestObserver(OnStartGamePlayRequestObserver observer);

	/**
	 * This method displays a "welcome back" toast showing
	 * the user's Scoreloop display name.
	 * If no display name exists for the user, then no toast is shown.
	 * The display name is set after the ScoreloopUI is finished with an
	 * authenticated user.
	 * 
	 * @param delay A time interval in milliseconds after which the toast should be shown. To show the toast immediately, pass zero here.
	 */
	void showWelcomeBackToast(long delay);

	/**
	 * This method is used to submit achievements to the Scoreloop server.
	 * This method will implicitly call the  loadAchievements()
	 * method if it has not already been called.
	 * 
	 * @param continuation A continuation that will be invoked when all achievements have been submitted.
	 * This value can be @c null.
	 */
	void submitAchievements(Continuation<Boolean> continuation);

	/**
	 * @param continuation A <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Runnable.html">java.lang.Runnable</a> that will be invoked
	 * when the local scores hav been submitted to to the Scoreloop servers. Note that only the currently best local score of every mode which has not yet been
	 * submitted will be uploaded to the server.
	 * This value can be @c null.
	 */
	void submitLocalScores(Runnable continuation);

	/**
	 * If the user rejected the Scoreloop Terms of Service, you should disabled all button in your game which open Scoreloop related
	 * Activities or functionality.
	 * 
	 * @param notification If you pass a notification continuation here, it will be called when new versions of the Terms of Service
	 * are rejected. Use @c null to unregister the notification.
	 * @return
	 */
	boolean userRejectedTermsOfService(Continuation<Boolean> notification);

	/**
	 * Checks if a game item has been already purchased.
	 *
	 * @param gameItemIdentifier a game item identfier
	 * @param continuation callback that gets the result value
	 */
	void wasGameItemPurchasedBefore(String gameItemIdentifier, Continuation<Boolean> continuation);

	/**
	 * @return An immutable set of strings identifying the enabled and suppported payment providers
	 */
	Set<String> getSupportedPaymentProviderKinds();

	/**
	 * 
	 * @return The current Session
	 */
	Session getSession();

}
