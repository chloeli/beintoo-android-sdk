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
import android.widget.Button;
import android.widget.ImageButton;
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
		current = this;
		Beintoo.homeDialog = current;
		// set the nickname
		TextView nickname = (TextView) findViewById(R.id.nickname);
		nickname.setText(getContext().getString(R.string.homeWelcome)+getCurrentPlayerNickname());
		
		// CHECK IF THE DEVELOPER WANTS TO REMOVE SOME FEATURES
		setFeatureToUse();
		
		BeButton b = new BeButton(ctx);
		
		Button profilebt = (Button) findViewById(R.id.profilebt);		
		if(profilebt != null){
			profilebt.setBackgroundDrawable(b.setPressedBg(R.drawable.profilebt, R.drawable.profilebt_h, R.drawable.profilebt_h));			    
			profilebt.setOnClickListener(new Button.OnClickListener(){
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
		
		Button leaderbt = (Button) findViewById(R.id.leaderboardbt);
		if(leaderbt != null){
			leaderbt.setBackgroundDrawable(b.setPressedBg(R.drawable.leaderbt, R.drawable.leaderbt_h, R.drawable.leaderbt_h));			    
			leaderbt.setOnClickListener(new ImageButton.OnClickListener(){
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
		
		Button walletbt = (Button) findViewById(R.id.walletbt);
		if(walletbt != null){
			walletbt.setBackgroundDrawable(b.setPressedBg(R.drawable.wallet, R.drawable.wallet_h, R.drawable.wallet_h));			    
			walletbt.setOnClickListener(new ImageButton.OnClickListener(){
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
		
		Button challengesbt = (Button) findViewById(R.id.challengesbt);
		if(challengesbt != null){
			challengesbt.setBackgroundDrawable(b.setPressedBg(R.drawable.challenges, R.drawable.challenges_h, R.drawable.challenges_h));			    
			challengesbt.setOnClickListener(new ImageButton.OnClickListener(){
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
		
		Button close = (Button) findViewById(R.id.close);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));
	    close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();
			}
		});
		
	}
	
	public void setFeatureToUse(){
		String[] features = Beintoo.usedFeatures;
		
		if(features != null){
			
			HashSet<String> f = new HashSet<String>(Arrays.asList(features));
			TableRow row1 = (TableRow) findViewById(R.id.firstRow);
			TableRow row2 = (TableRow) findViewById(R.id.secondRow);
			System.out.println(":feat:"+ features+" containprofile: "+f.contains("profile")
					+" contain wallet: "+f.contains("wallet"));
			/*
			 * REMOVE FEATURES THAT ARE NOT IN THE features ARRAY SETTED BY THE DEVELOPER
			 */
			if(!f.contains("profile")){
				Button profile = (Button) findViewById(R.id.profilebt);
				row1.removeView(profile);
				System.out.println(":feat:dentro "+ features);
			}
			if(!f.contains("wallet")){
				Button wallet = (Button) findViewById(R.id.walletbt);
				row2.removeView(wallet);
			}
			if(!f.contains("leaderboard")){
				Button leaderboard = (Button) findViewById(R.id.leaderboardbt);
				row1.removeView(leaderboard);
			}
			if(!f.contains("challenges")){
				Button challenges = (Button) findViewById(R.id.challengesbt);
				row2.removeView(challenges);
			}
				
		}
	}
	
	/**
	 * Returns the nickname of the current logged in player
	 * @return
	 */
	public String getCurrentPlayerNickname () {
		Gson gson = new Gson();
		Player player = gson.fromJson(getCurrentPlayer(), Player.class);
		return player.getUser().getNickname();		
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
