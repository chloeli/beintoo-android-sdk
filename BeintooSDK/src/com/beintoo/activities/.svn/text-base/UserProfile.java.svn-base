/*******************************************************************************
 * Copyright 2011 Beintoo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.beintoo.activities;

import java.util.Iterator;
import java.util.Map;

import com.beintoo.R;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerScore;
import com.google.gson.Gson;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserProfile extends Dialog {
	Dialog current;
	@SuppressWarnings("unchecked")
	public UserProfile(final Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.profileb);
		current = this;

		// SETTING UP TEXTVIEWS AND PROFILE IMAGE
		LoaderImageView profilepict = (LoaderImageView) findViewById(R.id.profilepict);		
		TextView nickname = (TextView) findViewById(R.id.nickname);
		TextView level = (TextView) findViewById(R.id.level);
		TextView dollars = (TextView) findViewById(R.id.bedollars);
		TextView bescore = (TextView) findViewById(R.id.salary);
		LinearLayout contests = (LinearLayout) findViewById(R.id.contests);
		
		try {		
			Player currentPlayer;
			currentPlayer = getCurrentPlayer ();
			
			profilepict.setImageDrawable(currentPlayer.getUser().getUserimg());
			nickname.setText(currentPlayer.getUser().getNickname());
			level.setText("Level: "+fromIntToLevel(currentPlayer.getUser().getLevel()));
			dollars.setText("Bedollars: "+currentPlayer.getUser().getBedollars());
			bescore.setText("Bescore: "+currentPlayer.getUser().getBescore());
			
			
			Map<String, PlayerScore> playerScore = currentPlayer.getPlayerScore();
			
			if(playerScore != null) { // THE USER HAS SCORES FOR THE APP
				Iterator<?> it = playerScore.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String, PlayerScore> pairs = (Map.Entry<String, PlayerScore>) it.next();
			        PlayerScore pscore = pairs.getValue();
			        TextView contestName = new TextView(getContext());
			        TextView contestData = new TextView(getContext());	        
			        contestName.setText(pscore.getContest().getName());
			        contestName.setPadding(0,10,0,2);
			        contestName.setTextColor(Color.BLACK);	  
			        contestData.setPadding(10,5,0,5);
			        contestData.setText("Total score: "+pscore.getBalance()+"\nBestscore: "+pscore.getBestscore()+" Lastscore: "+pscore.getLastscore());
			        contestData.setTextSize(12);
			        contestData.setBackgroundColor(Color.parseColor("#d4e8ed"));
			        contestData.setTextColor(Color.parseColor("#545859"));
			        contests.addView(contestName);	        
			        contests.addView(contestData);
			    }
			}else { // NO SCORES FOR THE APP
				TextView noScores = new TextView(getContext());
				noScores.setText("You don't have any scores for this app");
				noScores.setPadding(5,10,0,2);
				noScores.setTextColor(Color.BLACK);
				contests.addView(noScores);	
			}
		}catch (Exception e) {e.printStackTrace();}
	   
	    Button logout = (Button) findViewById(R.id.logout);
	    BeButton b = new BeButton(ctx);
	    logout.setBackgroundDrawable(b.setPressedBg(R.drawable.logoutbt, R.drawable.logoutbt_h, R.drawable.logoutbt_h));			    	 
		logout.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				PreferencesHandler.saveBool("isLogged", false, getContext());
				PreferencesHandler.saveString("currentPlayer", null, getContext());
				Beintoo.homeDialog.dismiss();
				current.dismiss();												
			}
		});
	    
	    Button close = (Button) findViewById(R.id.close);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));	    
	    close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();
			}
		});
		
	}
	
	
	public Player getCurrentPlayer () {
		Gson gson = new Gson();		
		return gson.fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);		
	}
	
	public String fromIntToLevel (int level) {
		if(level == 1) return "Novice";
		if(level == 2) return "Learner";
		if(level == 3) return "Passionate";
		if(level == 4) return "Winner";
		
		return "Novice";
	}
}
