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
import java.util.List;
import java.util.Map;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooApp;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Challenge;
import com.beintoo.wrappers.EntryCouplePlayer;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.Vgood;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
		
		LinearLayout welcome = (LinearLayout) findViewById(R.id.welcome);
		welcome.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*25),BDrawableGradient.GRAY_GRADIENT));
		
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
			nickname.setText(getContext().getString(R.string.homeWelcome)+getCurrentPlayerNickname());			
			// CHECK IF THE DEVELOPER WANTS TO REMOVE SOME FEATURES
			setFeatureToUse();
		}catch (Exception e){e.printStackTrace();}
		
		//Button profilebt = (Button) findViewById(R.id.profilebt);		
		if(row1 != null){			    
			row1.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {				
					final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
					new Thread(new Runnable(){      
	            		public void run(){
	            			try{ 
								BeintooPlayer bPlayer = new BeintooPlayer();
								Player currentSaved = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));				
								Player requestedPlayer = bPlayer.getPlayer(currentSaved.getGuid());				
								PreferencesHandler.saveString("currentPlayer", JSONconverter.playerToJson(requestedPlayer), getContext());
								
								UIhandler.sendEmptyMessage(OPEN_PROFILE);
	            			}catch (Exception e){
	            			}
	            			dialog.dismiss();
	            		}
					}).start();
				}
			});
		}
		
		if(row2 != null){			    
			row2.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {				
					final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
					new Thread(new Runnable(){      
	            		public void run(){
	            			try{ 
								BeintooApp app = new BeintooApp();							
								Map<String, List<EntryCouplePlayer>> leader = app.TopScore(null, 0);
								Gson gson = new Gson();
								String jsonLeaderboard = gson.toJson(leader);
								
								PreferencesHandler.saveString("leaderboard", jsonLeaderboard, getContext());
								UIhandler.sendEmptyMessage(OPEN_LEADERBOARD);							
	            			}catch (Exception e){
	            				e.printStackTrace();
	            			}
	            			dialog.dismiss();
	            		}
					}).start();
				}
			});
		}
		
		if(row3 != null){			    
			row3.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {				
					final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
					new Thread(new Runnable(){      
	            		public void run(){
	            			try{             				
	            				BeintooVgood newvgood = new BeintooVgood();            				
	            				// GET THE CURRENT LOGGED PLAYER
	            				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
	            				Vgood [] vgood = newvgood.showByUser(p.getUser().getId(), null);
	            				PreferencesHandler.saveString("wallet", new Gson().toJson(vgood), getContext());
	            				UIhandler.sendEmptyMessage(OPEN_WALLET);							
	            			}catch (Exception e){
	            				e.printStackTrace();
	            			}
	            			dialog.dismiss();
	            		}
					}).start();
				}
			});
		}
		
		if(row4 != null){			    
			row4.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {				
					final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
					new Thread(new Runnable(){      
	            		public void run(){
	            			try{             				
	            				BeintooUser newuser = new BeintooUser();            				
	            				// GET THE CURRENT LOGGED PLAYER
	            				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
	            				Challenge[] challenge = newuser.challengeShow(p.getUser().getId(), "TO_BE_ACCEPTED");
	            				
	            				PreferencesHandler.saveString("challenge", new Gson().toJson(challenge), getContext());
	            				UIhandler.sendEmptyMessage(OPEN_CHALLENGE);							
	            			}catch (Exception e){
	            				e.printStackTrace();
	            			}
	            			dialog.dismiss();
	            		}
					}).start();
				}
			});
		}
	}
	
	public void setFeatureToUse(){
		String[] features = Beintoo.usedFeatures;
		
		if(features != null){
			
			HashSet<String> f = new HashSet<String>(Arrays.asList(features));
			TableLayout tl = (TableLayout)findViewById(R.id.myTableLayout);
			TableRow row1 = (TableRow) findViewById(R.id.firstRow);
			TableRow row2 = (TableRow) findViewById(R.id.secondRow);
			TableRow row3 = (TableRow) findViewById(R.id.thirdRow);
			TableRow row4 = (TableRow) findViewById(R.id.fourthRow);
			
			/*
			 * REMOVE FEATURES THAT ARE NOT IN THE features ARRAY SETTED BY THE DEVELOPER
			 */
			if(!f.contains("profile")){				
				tl.removeView(row1);
			}
			if(!f.contains("wallet")){
				tl.removeView(row2);
			}
			if(!f.contains("leaderboard")){
				tl.removeView(row3);
			}
			if(!f.contains("challenges")){
				tl.removeView(row4);
			}
				
		}
	}
	
	/**
	 * Returns the nickname of the current logged in player
	 * @return
	 */
	public String getCurrentPlayerNickname () {
		Gson gson = new Gson();
		try {
			Player player = gson.fromJson(getCurrentPlayer(), Player.class);
			return player.getUser().getNickname();		
		}catch (Exception e){e.printStackTrace();}
		
		return "";
		
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
