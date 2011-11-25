package org.angdroid.angband;
import com.scoreloop.client.android.ui.*;

public class AngbandApp extends android.app.Application {
    @Override public void onCreate() {
	super.onCreate();
	// replace "whatever_your_game_secret_is" with your own game secret
	ScoreloopManagerSingleton.init(this, "%SL_SECRET%");
    }
 
    @Override public void onTerminate() {
	super.onTerminate();
	ScoreloopManagerSingleton.destroy();
    }
}
