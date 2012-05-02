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

public interface ScreenManager {

	public static interface Delegate {

		public enum Direction {
			FORWARD, BACKWARD, NONE
		}

		void screenManagerWillShowScreenDescription(ScreenDescription screenDescription, Direction direction);

		boolean screenManagerWantsNewScreen(ScreenManager screenManager, ScreenDescription description,
				ScreenDescription referenceDescription);

		void screenManagerWillEnterFramework(ScreenManager manager);

		void screenManagerDidLeaveFramework(ScreenManager manager);

		void screenManagerWillShowOptionsMenu();
	}

	void display(ScreenDescription description);

	void displayInScreen(ScreenDescription description, ScreenActivityProtocol screenActivity, boolean wantsEmptyStack);

	void displayPreviousDescription(boolean force);

	void displayReferencedStackEntryInScreen(int stackEntryReference, ScreenActivityProtocol newScreenActivity);

	void displayStoredDescriptionInScreen(ScreenActivityProtocol screenActivity);

	void displayStoredDescriptionInTabs(TabsActivityProtocol tabs);

	void displayWithEmptyStack(ScreenDescription description);

	void finishDisplay();

	ActivityDescription getActivityDescription(String id);

	ScreenDescription getCurrentDescription();

	int getCurrentStackEntryReference();

	void setDelegate(Delegate delegate);

	void onWillShowOptionsMenu();

	void onShowedTab(ScreenDescription screenDescription);

}
