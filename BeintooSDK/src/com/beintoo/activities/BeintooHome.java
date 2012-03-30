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
import com.beintoo.activities.leaderboard.LeaderboardContest;
import com.beintoo.activities.marketplace.Marketplace;
import com.beintoo.activities.notifications.NotificationList;
import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BMissionListener;
import com.beintoo.wrappers.Player;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BeintooHome extends Dialog {
	private final int OPEN_PROFILE = 1;
	private final int OPEN_LEADERBOARD = 2;
	private final int OPEN_MARKETPLACE = 8;
	private final int OPEN_WALLET = 3;
	private final int OPEN_CHALLENGE = 4;	
	private final int OPEN_ACHIEVEMENTS = 6;
	private final int OPEN_FORUM = 7;
	private BeButton button;
	private Player player;
	private Dialog current;
	private double ratio;
	private boolean isFirstLoading = true;	
	
	public BeintooHome(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.homeb);		
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        current = this;
		Beintoo.homeDialog = current;
		// GETTING DENSITY PIXELS RATIO		
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		LinearLayout beintooBar = (LinearLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
			
		// SETTING UP GRADIENTS		
		button = new BeButton(ctx);		
		findViewById(R.id.welcome).setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*30),BDrawableGradient.GRAY_GRADIENT));
		findViewById(R.id.notificationalert).setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*25),BDrawableGradient.NOTIFICATION_BAR_ALERT_CIRCLE));
		
		player = Current.getCurrentPlayer(ctx);
		
		// SET USER ROW IF IS USER
		if(player != null && player.getUser() != null && Beintoo.isLogged(ctx))
			showUserInfoRow();
		else{
			DialogStack.addToDialogStack(this);
			LinearLayout signup = (LinearLayout) findViewById(R.id.signupPlayer);
			signup.addView(SignupLayouts.signupRow(current.getContext(),"home"));
			signup.setVisibility(View.VISIBLE);
		}
		
		setMissionStatusButton();
		
		// SET ROWS BACKGROUND, CLICKLISTENER AND CHECK IF THE DEVELOPER WANTS TO REMOVE SOME FEATURES
		setFeatureToUse();		
	}
	
	public void showUserInfoRow(){
		findViewById(R.id.userinfo).setVisibility(View.VISIBLE);
		try {						
			// set the nickname
			TextView nickname = (TextView) findViewById(R.id.nickname);
			nickname.setText(player.getUser().getNickname());
			TextView bedollars = (TextView) findViewById(R.id.bedollars);
			bedollars.setText(player.getUser().getBedollars().intValue() + " BeDollars");
		}catch (Exception e){e.printStackTrace();}
	}
	
	public void setMissionStatusButton(){
		try {
			if(player.hasMission()){
				Context context = current.getContext();
				LinearLayout unread = (LinearLayout) findViewById(R.id.messages);			
				findViewById(R.id.missionstatus).setVisibility(View.VISIBLE);
				TextView unreadtxt = (TextView) findViewById(R.id.unmissionstatus);
				unreadtxt.setText(context.getString(R.string.unmissionstatus));				
				unread.setBackgroundDrawable(button.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				unread.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {							
						final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
						dialog.setCancelable(true);						
						Beintoo.getMission(current.getContext(), true, new BMissionListener(){
							@Override
							public void onComplete() {
								dialog.dismiss();
							}
							@Override
							public void onError(Exception e) {
								dialog.dismiss();   
							}							
						});								
					}					
				});
			}
		}catch (Exception e){e.printStackTrace();}
	}
	
	public void setFeatureToUse(){
		try {
			TableRow row1 = (TableRow) findViewById(R.id.firstRow);
			TableRow row2 = (TableRow) findViewById(R.id.secondRow);
			TableRow row3 = (TableRow) findViewById(R.id.thirdRow);
			TableRow row4 = (TableRow) findViewById(R.id.fourthRow);
			TableRow row5 = (TableRow) findViewById(R.id.fifthRow);
			TableRow row6 = (TableRow) findViewById(R.id.sixthRow);
			TableRow row7 = (TableRow) findViewById(R.id.sevenRow);
					
			setRowBg(row1,0);
			setRowBg(row2,1);
			setRowBg(row3,2);
			setRowBg(row4,3);
			setRowBg(row5,4);
			setRowBg(row6,5);
			setRowBg(row7,6);
			
			// PROFILE	
			if(row1 != null){		
				row1.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {		
						UIhandler.sendEmptyMessage(OPEN_PROFILE);
					}
				});
			}
			
			// MARKETPLACE
			if(row2 != null){			    
				row2.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {		 
						UIhandler.sendEmptyMessage(OPEN_MARKETPLACE);
					}
				});
			}
			
			// LEADERBOARD
			if(row3 != null){			    
				row3.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {		
						UIhandler.sendEmptyMessage(OPEN_LEADERBOARD);
					}
				});
			}
			
			// WALLET
			if(row4 != null){			    
				row4.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {	
						UIhandler.sendEmptyMessage(OPEN_WALLET);
					}
				});
			}
			
			// CHALLENGES
			if(row5 != null){			    
				if(player.getUser() == null){		
					AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
				    animation.setDuration(1);
				    animation.setFillEnabled(true);
				    animation.setFillAfter(true);
					row5.setAnimation(animation);
				}
				row5.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {
						if(player.getUser() != null){
							UIhandler.sendEmptyMessage(OPEN_CHALLENGE);
						}else{
							SignupLayouts.signupNowDialog(current.getContext(), Beintoo.FEATURE_CHALLENGES);
						}
					}
				});
			}
			
			// ACHIEVEMENTS
			if(row6 != null){			    
				row6.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {								
		            	UIhandler.sendEmptyMessage(OPEN_ACHIEVEMENTS);								            	
					}
				});
			}
			
			// Tips&forum
			if(row7 != null){	
				if(player.getUser() == null){		
					AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
				    animation.setDuration(1);
				    animation.setFillEnabled(true);
				    animation.setFillAfter(true);
					row7.setAnimation(animation);
				}
				row7.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {	
						if(player.getUser() != null){
							UIhandler.sendEmptyMessage(OPEN_FORUM);
						}else{
							SignupLayouts.signupNowDialog(current.getContext(), Beintoo.FEATURE_FORUMS);
						}
					}
				});
			}
			
			LinearLayout notifications = (LinearLayout) findViewById(R.id.notifications);
			notifications.setOnClickListener(new TableRow.OnClickListener(){
				public void onClick(View v) {	
					NotificationList nl = new NotificationList(current.getContext());
					nl.show();					
				}
			});
			TextView notificationsNumber = (TextView) findViewById(R.id.notnumber);
			if(player != null && player.getUnreadNotification() != null) 
				notificationsNumber.setText(player.getUnreadNotification().toString());
			notifications.setBackgroundDrawable(button.setPressedBackg(
					new BDrawableGradient(0,(int) (ratio*36),BDrawableGradient.NOTIFICATION_BAR_GRADIENT),
					new BDrawableGradient(0,(int) (ratio*36),BDrawableGradient.GRAY_ROLL_BUTTON_GRADIENT),
					new BDrawableGradient(0,(int) (ratio*36),BDrawableGradient.GRAY_ROLL_BUTTON_GRADIENT)));
			findViewById(R.id.notificationcircle).setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*25),BDrawableGradient.NOTIFICATION_BAR_CIRCLE_NUMBER));
			
			String[] features = Beintoo.usedFeatures;		
			if(features != null){			
				HashSet<String> f = new HashSet<String>(Arrays.asList(features));
				TableLayout tl = (TableLayout)findViewById(R.id.myTableLayout);			
				/*
				 * REMOVE FEATURES THAT ARE NOT IN THE features ARRAY SETTED BY THE DEVELOPER
				 */
				int count = 0;
				if(!f.contains("profile")){
					tl.removeView((LinearLayout)findViewById(R.id.firstWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.firstGrayline));
					tl.removeView((TableRow) findViewById(R.id.firstRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.firstRow), count);
					count++;
				}
				if(!f.contains("marketplace")){
					tl.removeView((LinearLayout)findViewById(R.id.secondWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.secondGrayline));
					tl.removeView((TableRow) findViewById(R.id.secondRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.secondRow), count);
					count++;
				}
				if(!f.contains("leaderboard")){
					tl.removeView((LinearLayout)findViewById(R.id.thirdWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.thirdGrayline));
					tl.removeView((TableRow) findViewById(R.id.thirdRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.thirdRow), count);
					count++;
				}
				if(!f.contains("wallet")){
					tl.removeView((LinearLayout)findViewById(R.id.fourthGrayline));
					tl.removeView((LinearLayout)findViewById(R.id.fourthWhiteline));
					tl.removeView((TableRow) findViewById(R.id.fourthRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.fourthRow), count);
					count++;
				}
				if(!f.contains("challenges")){
					tl.removeView((LinearLayout)findViewById(R.id.fifthWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.fifthGrayline));
					tl.removeView((TableRow) findViewById(R.id.fifthRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.fifthRow), count);
					count++;
				}
				if(!f.contains("achievements")){
					tl.removeView((LinearLayout)findViewById(R.id.sixthWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.sixthGrayline));
					tl.removeView((TableRow) findViewById(R.id.sixthRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.sixthRow), count);
					count++;
				}
				if(!f.contains("forums")){
					tl.removeView((LinearLayout)findViewById(R.id.sevenWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.sevenGrayline));
					tl.removeView((TableRow) findViewById(R.id.sevenRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.sevenRow), count);
					count++;
				}
			}			
		}catch (Exception e){e.printStackTrace();}
	}
		
	public void setRowBg(TableRow row, int pos){	
		if(row != null){			
			if(pos % 2 == 0)
				row.setBackgroundDrawable(button.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else	
				row.setBackgroundDrawable(button.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*80),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		}
	}
	
	private void updateNotifications(){		
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				BeintooPlayer bp = new BeintooPlayer();
    				final Player newPlayer = bp.getPlayer(Current.getCurrentPlayer(getContext()).getGuid());													
					Current.setCurrentPlayer(getContext(), newPlayer);
					player = newPlayer;
					UIhandler.post(new Runnable(){
						@Override
						public void run() {
							try {
								TextView notificationsNumber = (TextView) findViewById(R.id.notnumber);
								if(player != null && newPlayer.getUnreadNotification() != null)
									notificationsNumber.setText(newPlayer.getUnreadNotification().toString());
								findViewById(R.id.notificationcircle).setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*25),BDrawableGradient.NOTIFICATION_BAR_CIRCLE_NUMBER));
							}catch(Exception e){
								e.printStackTrace();
							}
						}						
					});
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}
		}).start();					
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {		
		super.onWindowFocusChanged(hasFocus);		
		if(hasFocus && !isFirstLoading){
			updateNotifications();
		}
		isFirstLoading = false;
	}

	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  
			  if(msg.what == OPEN_PROFILE){
				  UserProfile userProfile = new UserProfile(getContext());
				  Beintoo.currentDialog = userProfile;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(userProfile);
				  userProfile.show();
			  }else if(msg.what == OPEN_MARKETPLACE){			  
				  /*Marketplace markeplace = new Marketplace(getContext());
				  Beintoo.currentDialog = markeplace;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(markeplace);
				  markeplace.show();*/
				  	Marketplace mp = new Marketplace(getContext(), player);				  	
					Beintoo.currentDialog = mp;
					  if(Beintoo.dialogStack != null)
						  DialogStack.addToDialogStack(mp);
					mp.show();
			  }else if(msg.what == OPEN_LEADERBOARD){
				  LeaderboardContest leaderboard = new LeaderboardContest(getContext());
				  Beintoo.currentDialog = leaderboard;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(leaderboard);
				  leaderboard.show();
			  }else if(msg.what == OPEN_WALLET){	
				  Wallet wallet = new Wallet(getContext());
				  Beintoo.currentDialog = wallet;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(wallet);
				  wallet.show();
			  }else if(msg.what == OPEN_CHALLENGE){			  
				  Challenges challenges = new Challenges(getContext());
				  Beintoo.currentDialog = challenges;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(challenges);
				  challenges.show();
			  }else if(msg.what == OPEN_ACHIEVEMENTS){			  
				  UserAchievements achievements = new UserAchievements(getContext());
				  Beintoo.currentDialog = achievements;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(achievements);
				  achievements.show();
			  }else if(msg.what == OPEN_FORUM){		
				  if(player.getUser() != null){					  
					  StringBuilder sb = new StringBuilder("http://appsforum.beintoo.com/?apikey=");
				      sb.append(DeveloperConfiguration.apiKey);
				      sb.append("&userExt=");
				      sb.append(player.getUser().getId());
				      sb.append("#main"); 
					  Forums f = new Forums(getContext(),sb.toString());				  
					  Beintoo.currentDialog = f;
					  if(Beintoo.dialogStack != null)
						  DialogStack.addToDialogStack(f);
					  f.show();
				  }
			  }
			  			  
			  super.handleMessage(msg);
		  }
	};
}
