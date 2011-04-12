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


import java.util.Arrays;
import java.util.HashSet;

import com.beintoo.R;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.google.gson.Gson;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BeintooHome extends Dialog {
	private final int OPEN_PROFILE = 1;
	private final int OPEN_LEADERBOARD = 2;
	private final int OPEN_WALLET = 3;
	private final int OPEN_CHALLENGE = 4;
	Dialog current;
	public BeintooHome(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.homeb);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		current = this;
		Beintoo.homeDialog = current;
		
		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		LinearLayout beintooBar = (LinearLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		
		// SETTING UP ROWS GRADIENT
		
		RelativeLayout welcome = (RelativeLayout) findViewById(R.id.welcome);
		welcome.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*30),BDrawableGradient.GRAY_GRADIENT));
		
		TableRow row1 = (TableRow) findViewById(R.id.firstRow);
		TableRow row2 = (TableRow) findViewById(R.id.secondRow);
		TableRow row3 = (TableRow) findViewById(R.id.thirdRow);
		TableRow row4 = (TableRow) findViewById(R.id.fourthRow);
		
		BeButton b = new BeButton(getContext());
		row1.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		
		row2.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		
		row3.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		
		row4.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		
		try {
			// set the nickname
			TextView nickname = (TextView) findViewById(R.id.nickname);
			nickname.setText(getCurrentUser().getNickname());
			TextView bedollars = (TextView) findViewById(R.id.bedollars);
			bedollars.setText(getCurrentUser().getBedollars().intValue() + " BeDollars");
			// CHECK IF THE DEVELOPER WANTS TO REMOVE SOME FEATURES
			setFeatureToUse();
		}catch (Exception e){e.printStackTrace();}
		
		// PROFILE	
		if(row1 != null){			    
			row1.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {		
					UIhandler.sendEmptyMessage(OPEN_PROFILE);
				}
			});
		}
		
		// LEADERBOARD
		if(row2 != null){			    
			row2.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {		
					UIhandler.sendEmptyMessage(OPEN_LEADERBOARD);
				}
			});
		}
		
		// WALLET
		if(row3 != null){			    
			row3.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {	
					UIhandler.sendEmptyMessage(OPEN_WALLET);
				}
			});
		}
		
		// CHALLENGES
		if(row4 != null){			    
			row4.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {								
	            	UIhandler.sendEmptyMessage(OPEN_CHALLENGE);								            	
				}
			});
		}
	}
	
	public void setFeatureToUse(){
		String[] features = Beintoo.usedFeatures;
		
		if(features != null){			
			HashSet<String> f = new HashSet<String>(Arrays.asList(features));
			TableLayout tl = (TableLayout)findViewById(R.id.myTableLayout);			
			/*
			 * REMOVE FEATURES THAT ARE NOT IN THE features ARRAY SETTED BY THE DEVELOPER
			 */
			if(!f.contains("profile")){
				tl.removeView((LinearLayout)findViewById(R.id.firstWhiteline));
				tl.removeView((LinearLayout)findViewById(R.id.firstGrayline));
				tl.removeView((TableRow) findViewById(R.id.firstRow));
			}
			if(!f.contains("wallet")){
				tl.removeView((LinearLayout)findViewById(R.id.secondWhiteline));
				tl.removeView((LinearLayout)findViewById(R.id.secondGrayline));
				tl.removeView((TableRow) findViewById(R.id.secondRow));
			}
			if(!f.contains("leaderboard")){
				tl.removeView((LinearLayout)findViewById(R.id.thirdWhiteline));
				tl.removeView((LinearLayout)findViewById(R.id.thirdGrayline));
				tl.removeView((TableRow) findViewById(R.id.thirdRow));
			}
			if(!f.contains("challenges")){
				tl.removeView((LinearLayout)findViewById(R.id.fourthWhiteline));
				tl.removeView((LinearLayout)findViewById(R.id.fourthGrayline));
				tl.removeView((TableRow) findViewById(R.id.fourthRow));
			}				
		}
	}
	
	/**
	 * Returns the nickname of the current logged in player
	 * @return
	 */
	public User getCurrentUser () {
		Gson gson = new Gson();
		try {
			Player player = gson.fromJson(getCurrentPlayer(), Player.class);
			return player.getUser();		
		}catch (Exception e){e.printStackTrace();}
		
		return new User();		
	}
	
	/**
	 *  Returns the current logged in Player in json
	 */
	public String getCurrentPlayer () {		
		return PreferencesHandler.getString("currentPlayer", getContext());		
	}
	
	
	/**
	 * This Handler open this listed Dialogs:
	 * 	Profile
	 * 	Leaderboard
	 * 	Challenges
	 * 	Wallet
	 */
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  
			  if(msg.what == OPEN_PROFILE){
				  UserProfile userProfile = new UserProfile(getContext());
				  Beintoo.currentDialog = userProfile;
				  userProfile.show();
			  }else if(msg.what == OPEN_LEADERBOARD){
				  
				  LeaderBoardContest leaderboard = new LeaderBoardContest(getContext());
				  Beintoo.currentDialog = leaderboard;
				  leaderboard.show();
			  }else if(msg.what == OPEN_WALLET){			  
				  Wallet wallet = new Wallet(getContext());
				  Beintoo.currentDialog = wallet;
				  wallet.show();
			  }else if(msg.what == OPEN_CHALLENGE){			  
				  Challenges challenges = new Challenges(getContext());
				  Beintoo.currentDialog = challenges;
				  challenges.show();
			  }
			  
			  super.handleMessage(msg);
		  }
	};
}
