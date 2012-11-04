package org.angdroid.angband;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.util.Log;

import com.scoreloop.client.android.ui.*;
import com.scoreloop.client.android.core.model.Score;

public class ScorePublisher implements OnScoreSubmitObserver {

	public Activity activity;
	public ScorePublisher(Activity a)
	{
		activity = a;
		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(this);
	}

	public static void Init(android.app.Application app)
	{
		// replace "whatever_your_game_secret_is" with your own game secret
		ScoreloopManagerSingleton.init(app, "%SL_SECRET%");
	}

	public static void Destroy()
	{
		ScoreloopManagerSingleton.destroy();
	}

	public void ShowLeaderboards()
	{
		Intent intent = new Intent(activity, LeaderboardsScreenActivity.class);
		intent.putExtra(LeaderboardsScreenActivity.LEADERBOARD,
						LeaderboardsScreenActivity.LEADERBOARD_LOCAL);
		activity.startActivity(intent);
	}

	public void ShowEntry()
	{
		Intent intent = new Intent(activity, EntryScreenActivity.class);
		activity.startActivity(intent);
	}


	public void Publish(ScoreContainer curScore)
	{
		String versionName = "unknown";
		try {
			versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0 ).versionName;
		} catch(PackageManager.NameNotFoundException e) {
			Log.d("Angband","error getting version" + e);
		}

		Score sc = new Score(new Double(curScore.score), curScore.map);
		sc.setLevel(curScore.level);

		HashMap ctx = (HashMap)sc.getContext();
		ctx.put("version", versionName);

		// Iterator itr = ctx.keySet().iterator();
		// while(itr.hasNext()) {
		//     String key = (String)itr.next();
		//     Log.d("Angband", key + " = \"" + ctx.get(key) +
		// 	  "\"");
		// }
			
		sc.setContext(ctx);
		ScoreloopManagerSingleton.get().onGamePlayEnded(sc, false);
	}

    //The class implements OnScoreSubmitObserver and so must implement this callback
	@Override
	public void onScoreSubmit(final int status, final Exception error) {		
		//Calls the ShowResultOverlayActivity. Make sure you have modified the
		//AndroidManifest.xml to reference this overlay class.
		activity.startActivity(new Intent(activity, ShowResultOverlayActivity.class));
	}
}