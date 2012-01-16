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
import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.User;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
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
	private final int UPDATE_UNREAD_MSG = 5;
	private final int OPEN_ACHIEVEMENTS = 6;
	private final int OPEN_FORUM = 7;
	private BeButton button;
	private User u;
	private Dialog current;
	private double ratio;	
	
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
		RelativeLayout welcome = (RelativeLayout) findViewById(R.id.welcome);
		welcome.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*30),BDrawableGradient.GRAY_GRADIENT));
		
		u = Current.getCurrentUser(ctx);
		
		// SET USER ROW IF IS USER
		if(u != null && Beintoo.isLogged(ctx))
			showUserInfoRow();
		else{
			DialogStack.addToDialogStack(this);
			LinearLayout signup = (LinearLayout) findViewById(R.id.signupPlayer);
			signup.addView(SignupLayouts.signupRow(current.getContext(),"home"));
			signup.setVisibility(View.VISIBLE);
		}
		// SET ROWS BACKGROUND, CLICKLISTENER AND CHECK IF THE DEVELOPER WANTS TO REMOVE SOME FEATURES
		setFeatureToUse();		
	}
	
	public void showUserInfoRow(){
		findViewById(R.id.userinfo).setVisibility(View.VISIBLE);
		try {
			Context context = current.getContext();			
			// set the nickname
			TextView nickname = (TextView) findViewById(R.id.nickname);
			nickname.setText(u.getNickname());
			TextView bedollars = (TextView) findViewById(R.id.bedollars);
			bedollars.setText(u.getBedollars().intValue() + " BeDollars");
			LinearLayout unread = (LinearLayout) findViewById(R.id.messages);
			int unreadcount = u.getUnreadMessages();			
			if(unreadcount > 0){
				findViewById(R.id.usermsg).setVisibility(View.VISIBLE);
				TextView unreadtxt = (TextView) findViewById(R.id.unmessage);
				unreadtxt.setText(String.format(context.getString(R.string.messagenotification), unreadcount));				
				unread.setBackgroundDrawable(button.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				unread.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						MessagesList ml = new MessagesList(getContext());
				        ml.show();	
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
					
			setRowBg(row1,0);
			setRowBg(row2,1);
			setRowBg(row3,2);
			setRowBg(row4,3);
			setRowBg(row5,4);
			setRowBg(row6,5);
			
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
				if(u == null){		
					AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
				    animation.setDuration(1);
				    animation.setFillEnabled(true);
				    animation.setFillAfter(true);
					row4.setAnimation(animation);
				}
				row4.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {
						if(u != null){
							UIhandler.sendEmptyMessage(OPEN_CHALLENGE);
						}else{
							SignupLayouts.signupNowDialog(current.getContext(), Beintoo.FEATURE_CHALLENGES);
						}
					}
				});
			}
			
			// ACHIEVEMENTS
			if(row5 != null){			    
				row5.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {								
		            	UIhandler.sendEmptyMessage(OPEN_ACHIEVEMENTS);								            	
					}
				});
			}
			
			// Tips&forum
			if(row6 != null){	
				if(u == null){		
					AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
				    animation.setDuration(1);
				    animation.setFillEnabled(true);
				    animation.setFillAfter(true);
					row6.setAnimation(animation);
				}
				row6.setOnClickListener(new TableRow.OnClickListener(){
					public void onClick(View v) {	
						if(u != null){
							UIhandler.sendEmptyMessage(OPEN_FORUM);
						}else{
							SignupLayouts.signupNowDialog(current.getContext(), Beintoo.FEATURE_FORUMS);
						}
					}
				});
			}
			
			
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
				if(!f.contains("wallet")){
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
				if(!f.contains("challenges")){
					tl.removeView((LinearLayout)findViewById(R.id.fourthWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.fourthGrayline));
					tl.removeView((TableRow) findViewById(R.id.fourthRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.fourthRow), count);
					count++;
				}
				if(!f.contains("achievements")){
					tl.removeView((LinearLayout)findViewById(R.id.fifthWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.fifthGrayline));
					tl.removeView((TableRow) findViewById(R.id.fifthRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.fifthRow), count);
					count++;
				}
				if(!f.contains("forums")){
					tl.removeView((LinearLayout)findViewById(R.id.sixthWhiteline));
					tl.removeView((LinearLayout)findViewById(R.id.sixthGrayline));
					tl.removeView((TableRow) findViewById(R.id.sixthRow));				
				}else{
					setRowBg((TableRow) findViewById(R.id.sixthRow), count);
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
	
	/*
	 * Used in the MessageRead.java to update users unread messages
	 */
	public void updateMessage (int count){
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt("count",count);
		msg.setData(b);
		msg.what = UPDATE_UNREAD_MSG;
		UIhandler.sendMessage(msg);
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
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(userProfile);
				  userProfile.show();
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
			  }else if(msg.what == UPDATE_UNREAD_MSG){
				  TextView unreadtxt = (TextView) findViewById(R.id.unmessage);
				  unreadtxt.setText(String.format(current.getContext().getString(R.string.messagenotification), msg.getData().getInt("count")));
			  }else if(msg.what == OPEN_ACHIEVEMENTS){			  
				  UserAchievements achievements = new UserAchievements(getContext());
				  Beintoo.currentDialog = achievements;
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(achievements);
				  achievements.show();
			  }else if(msg.what == OPEN_FORUM){		
				  if(u != null){					  
					  StringBuilder sb = new StringBuilder("http://appsforum.beintoo.com/?apikey=");
				      sb.append(DeveloperConfiguration.apiKey);
				      sb.append("&userExt=");
				      sb.append(u.getId());
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
