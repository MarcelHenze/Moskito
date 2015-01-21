package de.henze;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameScreen extends Activity implements Runnable,OnClickListener {
	int timer,points,goal,level;
	TextView timerView,pointsView,levelView;
	Handler handler;
	ProgressBar timeBar,pointsBar;
	FrameLayout frameLayout;
    MediaPlayer bgm,clicked;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamescreen);
        
        timerView = (TextView) findViewById(R.id.timerDisplay);
        timeBar = (ProgressBar) findViewById(R.id.timeProgress);
        pointsView = (TextView) findViewById(R.id.pointsDisplay);
        pointsBar = (ProgressBar) findViewById(R.id.pointsProgress);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        levelView = (TextView) findViewById(R.id.levelDisplay);
        pref = getSharedPreferences("Game", 0);		 
    	editor = pref.edit();
        
    	timer = pref.getInt("timer",60);
		points = pref.getInt("points",0);
		goal = pref.getInt("goal",25);
		level = pref.getInt("level",1);
        
        bgm = MediaPlayer.create(this, R.raw.bgm);
        
        timerView.setBackgroundColor(Color.GRAY);
        timerView.setText("Zeit: "+timer);
        pointsView.setBackgroundColor(Color.GRAY);
        pointsView.setText("Punkte: 0");
        levelView.setBackgroundColor(Color.GRAY);
        levelView.setText("Level: "+level);
        
        bgm.setLooping(true);
        bgm.start();
        
        frameLayout.post(new Runnable() {
            @Override
            public void run() {
            	createMuecke();
            }
        });
        
        timeBar.setMax(timer);
        timeBar.setProgress(timer);
        pointsBar.setMax(goal);
        pointsBar.setProgress(0);
        
        timeBar.getProgressDrawable().setColorFilter(Color.rgb(255,0,0), Mode.MULTIPLY);
        pointsBar.getProgressDrawable().setColorFilter(Color.rgb(100,145,237), Mode.MULTIPLY);
        
        handler = new Handler();
        handler.postDelayed(this, 1000);
        
    }

	@Override
	public void run() {
		if(timer > 0){
			timer--;
			timerView.setText("Zeit: "+timer);
			timeBar.setProgress(timer);
			handler.postDelayed(this, 1000);
			if(points >= goal)gameEnd();
		} else gameEnd();
	}


	@Override
	public void onClick(View v) {
		clicked = MediaPlayer.create(this, R.raw.clicked);
		clicked.start();
		frameLayout.removeViewInLayout(v);
		points++;
		pointsView.setText("Punkte: "+points+"");
		pointsBar.setProgress(points);
		createMuecke();
	}
	
	public void createMuecke(){
		ImageView muecke = new ImageView(this);
        muecke.setImageResource(R.drawable.muecke_no);
        muecke.setOnClickListener(this);
        muecke.setRotation(new Random().nextInt((360 - 0) + 1));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameLayout.addView(muecke,params);
        params.leftMargin = (new Random()).nextInt(frameLayout.getWidth()-50);
        params.topMargin = (new Random()).nextInt(frameLayout.getHeight()-50);
	}
	
	public void gameEnd(){
		handler.removeCallbacks(this);
		bgm.stop();
		Intent intent = new Intent(this, EndScreen.class);
		editor.putInt("timer", timer);
		editor.putInt("points", points);
		editor.putInt("goal", goal);
		editor.putInt("level", level);
		editor.commit();
		finish();
		startActivity(intent);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		bgm.pause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		bgm.start();
	}
}
