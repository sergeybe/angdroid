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

/**
 * The OnCanStartGamePlayObserver receives notifications
 * and callbacks before a Scoreloop challenge is
 * started or accepted, and returns a boolean value 
 * indicating whether the gameplay can start or not. 
 *
 * Before a challenge begins or is accepted there are three
 * possible scenarios:
 * - there is no gameplay session underway,
 * - there is an existing gameplay session,
 * - there is an existing challenge underway.
 * 
 * ScoreloopUI keeps a record of
 * any challenges that are underway and
 * handles that scenario internally. However to 
 * find out whether there is already an ordinary gameplay session
 * underway, ScoreloopUI must communicate with the 
 * game application. The onCanStartGamePlay() method
 * will be notified and should be implemented by the 
 * developer according to the type of policy they
 * wish to execute:
 * - if there is an existing gameplay session,
 * cancel it and replace it with the challenge,
 * - if there is an existing gameplay session,
 * retain it and dismiss the challenge.
 *
 * Note that the observer must be correctly set in the
 * ScoreloopManager if 
 * the game supports challenges.
 *
 * Basic Usage:
 * -# Implement the onCanStartGamePlay() method.
 * -# Set the observer by calling 
 * ScoreloopManager.setOnCanStartGamePlayObserver(OnCanStartGamePlayObserver).
 */
public interface OnCanStartGamePlayObserver {

	/**
	 * This method is called before the user starts or accepts 
	 * a challenge. It will normally be implemented
	 * in the game, (that is within the application itself). 
	 * @return @c true if the game is ready to start a challenge, otherwise @c false. 
	 */
	boolean onCanStartGamePlay();
}
