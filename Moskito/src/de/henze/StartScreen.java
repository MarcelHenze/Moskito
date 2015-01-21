package de.henze;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartScreen extends Activity  implements OnClickListener{
	
	Button playButton;
	EditText playerName;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);
		
        pref = getSharedPreferences("Game", 0);		 
    	editor = pref.edit();
		
		playButton = (Button) findViewById(R.id.playButton);
		playerName = (EditText) findViewById(R.id.playerName);
		playButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(!playerName.getText().toString().equals("")){
	    	Intent intent = new Intent(this, GameScreen.class);
	    	editor.putString("playerName", playerName.getText().toString().trim());
	    	editor.putInt("timer",60);
	    	editor.putInt("points",0);
	    	editor.putInt("goal",25);
	    	editor.putInt("level",1);
	    	editor.commit();
			finish();
	        startActivity(intent);
		}else{
			Toast popup = Toast.makeText(this,"Please enter your name", Toast.LENGTH_SHORT);  
			popup.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0); 
			popup.show();
		}
	}
}