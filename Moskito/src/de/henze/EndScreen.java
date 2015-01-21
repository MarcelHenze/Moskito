package de.henze;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EndScreen extends Activity implements OnClickListener {

	TextView success, score;
	int timer, points, goal, level;
	String playerName, game, URL;
	Button retryButton, showOnlineHighscore;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	TextView highscoreTitle, highscore1, highscore2, highscore3;
	MediaPlayer win, lose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_endscreen);

		pref = getSharedPreferences("Game", 0);
		editor = pref.edit();

		timer = pref.getInt("timer", 60);
		points = pref.getInt("points", 0);
		goal = pref.getInt("goal", 25);
		level = pref.getInt("level", 1);
		playerName = pref.getString("playerName", "");
		game = "Game";

		win = MediaPlayer.create(this, R.raw.win);
		lose = MediaPlayer.create(this, R.raw.lose);

		retryButton = (Button) findViewById(R.id.retry);
		retryButton.setOnClickListener(this);

		showOnlineHighscore = (Button) findViewById(R.id.highscoreButton);
		showOnlineHighscore.setText("Highscore senden");
		showOnlineHighscore.setOnClickListener(this);

		success = (TextView) findViewById(R.id.endView);
		score = (TextView) findViewById(R.id.pointsDisplay);

		highscoreTitle = (TextView) findViewById(R.id.highscoreTitle);
		highscore1 = (TextView) findViewById(R.id.highscore1);
		highscore2 = (TextView) findViewById(R.id.highscore2);
		highscore3 = (TextView) findViewById(R.id.highscore3);
		highscoreTitle.setText("Highscores:");

		if (points > pref.getInt("HIGHSCORE1", 0)) {
			editor.putInt("HIGHSCORE3", pref.getInt("HIGHSCORE2", 0));
			editor.putInt("HIGHSCORE2", pref.getInt("HIGHSCORE1", 0));
			editor.putInt("HIGHSCORE1", points);
			editor.putString("PLAYER1", playerName);
			editor.commit();
		} else if (points > pref.getInt("HIGHSCORE2", 0)) {
			editor.putInt("HIGHSCORE3", pref.getInt("HIGHSCORE2", 0));
			editor.putInt("HIGHSCORE2", points);
			editor.putString("PLAYER2", playerName);
			editor.commit();
		} else if (points > pref.getInt("HIGHSCORE3", 0)) {
			editor.putInt("HIGHSCORE3", points);
			editor.putString("PLAYER3", playerName);
			editor.commit();
		}

		if (pref.contains("HIGHSCORE1") && pref.getInt("HIGHSCORE1", 0) > 0) {
			highscore1.setText("#1: " + pref.getString("PLAYER1", "")
					+ " with " + pref.getInt("HIGHSCORE1", 0) + " points.");
			highscore1.setVisibility(View.VISIBLE);
		}
		if (pref.contains("HIGHSCORE2") && pref.getInt("HIGHSCORE2", 0) > 0) {
			highscore2.setText("#2: " + pref.getString("PLAYER2", "")
					+ " with " + pref.getInt("HIGHSCORE2", 0) + " points.");
			highscore2.setVisibility(View.VISIBLE);
		}
		if (pref.contains("HIGHSCORE3") && pref.getInt("HIGHSCORE3", 0) > 0) {
			highscore3.setText("#3: " + pref.getString("PLAYER3", "")
					+ " with " + pref.getInt("HIGHSCORE3", 0) + " points.");
			highscore3.setVisibility(View.VISIBLE);
		}

		if (points >= goal) {
			success.setText("You Won!");
			score.setText("You scored " + points + " points in " + (60 - timer)
					+ "s.");
			retryButton.setText("Next level");
			win.start();
		} else {
			success.setText("You Lose!");
			score.setText("You scored " + points + " points.");
			retryButton.setText("Try again");
			lose.start();
		}
		new MyAsyncTask().execute();
	}

	@Override
	public void onClick(View v) {
		if (v == retryButton) {
			Intent intent = new Intent(this, GameScreen.class);
			editor.putInt("timer", 60);
			editor.putInt("points", 0);
			if (points >= goal) {
				editor.putInt("goal", (int) (goal * 1.2));
				editor.putInt("level", level + 1);
			} else {
				editor.putInt("goal", 25);
				editor.putInt("level", 1);
			}
			win.stop();
			lose.stop();
			editor.commit();
			finish();
			startActivity(intent);
		} else if (v == showOnlineHighscore) {
			onlineAnzeigen();
		}
	}

	public void onlineAnzeigen() {
		URL = "http://moskitohenze.appspot.com/moskitoserver";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
	}

	private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData();
			return null;
		}

		public void postData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httppost = new HttpGet(
					"http://moskitohenze.appspot.com/moskitoserver?game="+game+"&name="+playerName+"&score="+points+"&level="+level);
			try {
				// Execute HTTP Post Request
				httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		win.stop();
		lose.stop();
	}
}
