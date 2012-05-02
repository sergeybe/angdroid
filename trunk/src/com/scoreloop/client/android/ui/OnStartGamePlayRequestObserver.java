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
 * The OnStartGamePlayRequestObserver receives notifications
 * and callbacks from the server when a Scoreloop challenge is
 * started or accepted. 
 * 
 * The observer must be correctly set in the ScoreloopManager if 
 * the game supports challenges.
 *
 * Basic Usage:
 * -# Implement the onStartGamePlayRequest(Integer) method.
 * -# Set the observer by calling 
 * ScoreloopManager.setOnStartGamePlayRequestObserver(OnStartGamePlayRequestObserver).
 */
public interface OnStartGamePlayRequestObserver {

	/**
	 * This method is called when the user starts or accepts 
	 * a challenge. It will normally be implemented
	 * in the game, (that is within the application itself). 
	 *
	 * @param mode <a href="http://download.oracle.com/javase/6/docs/api/java/lang/Integer.html">java.lang.Integer</a> corresponding to the challenge mode. This can be @c null. 
	 */
	void onStartGamePlayRequest(Integer mode);
}
