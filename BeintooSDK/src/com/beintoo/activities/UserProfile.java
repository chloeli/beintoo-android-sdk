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
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerScore;
import com.google.beintoogson.Gson;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserProfile extends Dialog {
	Dialog current;
	double ratio;
	private Player currentPlayer;
	
	private int currentSection;
	
	private final int LOAD_PROFILE = 1;
	private final int SET_NEWMSG_BUTTON = 2;
	
	public static final int CURRENT_USER_PROFILE = 1; 
	public static final int FRIEND_USER_PROFILE = 2;
	
	public UserProfile(final Context ctx) {
		super(ctx, R.style.ThemeBeintoo);				
		current = this;
		currentSection = CURRENT_USER_PROFILE;
		setupMainLayoutGradients();
		showLoading();
		setupCurrentUserProfileLayout();		
		startLoading(null, CURRENT_USER_PROFILE);			
	}
	
	public UserProfile(final Context ctx, final String userExt) {
		super(ctx, R.style.ThemeBeintoo);				
		current = this;
		currentSection = FRIEND_USER_PROFILE;
		setupMainLayoutGradients();		
		showLoading();
		setupFriendProfileLayout();
		startLoading(userExt, FRIEND_USER_PROFILE);
	}
	
	private void setupMainLayoutGradients (){
		setContentView(R.layout.profileb);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ratio = (current.getContext().getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		// SETTING UP PICTURE AND LEVEL CONTENT GRADIENT
		pixels = ratio * 104;
		LinearLayout textlayout = (LinearLayout) findViewById(R.id.textlayout);
		textlayout.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.GRAY_GRADIENT));
	}
	
	private void setupCurrentUserProfileLayout (){
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.profile);
		LinearLayout gc = (LinearLayout) findViewById(R.id.goodcontent);
		gc.setVisibility(LinearLayout.INVISIBLE);
		
		currentPlayer = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);	
		
		Button logout = (Button) findViewById(R.id.logout);
	    BeButton b = new BeButton(current.getContext());
	    logout.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
	    logout.setBackgroundDrawable(
	    		b.setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));			    	 
		logout.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				Beintoo.logout(getContext());
				Beintoo.homeDialog.dismiss();
				current.dismiss();												
			}
		});
		
		Button detach = (Button) findViewById(R.id.detach);
		detach.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
		detach.setBackgroundDrawable(
	    		b.setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));			    	 
		detach.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				BeintooUser usr = new BeintooUser();
				try {													
					usr.detachUserFromDevice(DeviceId.getUniqueDeviceId(current.getContext()), currentPlayer.getUser().getId());
					Beintoo.logout(getContext());	
					Beintoo.homeDialog.dismiss();
					current.dismiss();
				}catch (Exception e){e.printStackTrace();}	
			}
		});
		
		
		/*
		 * SETUP TOOLBAR BUTTONS 
		 */
		
		ImageButton friends = (ImageButton) findViewById(R.id.friendsbt);
		friends.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				Friends mf = new Friends(getContext(), null, Friends.MAIN_FRIENDS_MENU, R.style.ThemeBeintoo);
		        mf.show();										
			}
		});
		
		ImageButton messages = (ImageButton) findViewById(R.id.messagesbt);
		messages.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				MessagesList ml = new MessagesList(getContext());
		        ml.show();											
			}
		});
		
		ImageButton balancebt = (ImageButton) findViewById(R.id.balancebt);
		balancebt.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				UserBalance ub = new UserBalance(getContext());
		        ub.show();											
			}
		});
				
		try {
			TextView unread = (TextView) findViewById(R.id.msgunread);
			unread.setText(Integer.toString(currentPlayer.getUser().getUnreadMessages()));			
		}catch(Exception e){e.printStackTrace();}
		
		LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar);
		toolbar.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT));
		
	}
	
	private void setupFriendProfileLayout (){
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		Button logout = (Button) findViewById(R.id.logout);
		Button detach = (Button) findViewById(R.id.detach);
		LinearLayout gc = (LinearLayout) findViewById(R.id.goodcontent);
		ImageView mail = (ImageView) findViewById(R.id.messagesbt);
		
		t.setText(R.string.profile);				
		logout.setVisibility(LinearLayout.GONE);
		detach.setVisibility(LinearLayout.GONE);
		gc.setVisibility(LinearLayout.INVISIBLE);
		mail.setVisibility(LinearLayout.GONE); 	
		
		LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar);
		toolbar.setVisibility(LinearLayout.GONE);
	}
	
	private void startLoading (final String userExt, final int ws) {
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
					BeintooPlayer bPlayer = new BeintooPlayer();
					if(ws == CURRENT_USER_PROFILE){
						Player currentSaved = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));				
						currentPlayer = bPlayer.getPlayer(currentSaved.getGuid());
					}else if(ws == FRIEND_USER_PROFILE){
						currentPlayer = bPlayer.getPlayerByUser(userExt, null);
						Bundle b = new Bundle();
						b.putString("nickname", currentPlayer.getUser().getNickname());
						b.putString("userExt", currentPlayer.getUser().getId());
						Message msg = new Message();
						msg.setData(b);
						msg.what = SET_NEWMSG_BUTTON;
						UIhandler.sendMessage(msg);
					}
					
					UIhandler.sendEmptyMessage(LOAD_PROFILE);
					
    			}catch (Exception e){}			
    		}
		}).start();
	}
	
	private void loadData (){
		// SETTING UP TEXTVIEWS AND PROFILE IMAGE
		LoaderImageView profilepict = (LoaderImageView) findViewById(R.id.profilepict);		
		TextView nickname = (TextView) findViewById(R.id.nickname);
		TextView level = (TextView) findViewById(R.id.level);
		TextView dollars = (TextView) findViewById(R.id.bedollars);
		TextView bescore = (TextView) findViewById(R.id.salary);
		LinearLayout contests = (LinearLayout) findViewById(R.id.contests);
		profilepict.setImageDrawable(currentPlayer.getUser().getUserimg());
		nickname.setText(currentPlayer.getUser().getNickname());
		level.setText(getContext().getString(R.string.profileLevel)+fromIntToLevel(currentPlayer.getUser().getLevel()));
		dollars.setText("Bedollars: "+currentPlayer.getUser().getBedollars());
		bescore.setText("Bescore: "+currentPlayer.getUser().getBescore());
		
		Map<String, PlayerScore> playerScore = currentPlayer.getPlayerScore();
		
		if(playerScore != null) { // THE USER HAS SCORES FOR THE APP
			Iterator<?> it = playerScore.entrySet().iterator();
		    while (it.hasNext()) {			    	
		        @SuppressWarnings("unchecked")
				Map.Entry<String, PlayerScore> pairs = (Map.Entry<String, PlayerScore>) it.next();
		        if(pairs.getValue().getContest().isPublic()){
			        PlayerScore pscore = pairs.getValue();
			        TextView contestName = new TextView(getContext());				        
			        
			        contestName.setText(pscore.getContest().getName());
			        contestName.setPadding(12,5,0,5);
			        contestName.setTextColor(Color.BLACK);	 
			        contestName.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.GRAY_GRADIENT));
			        
			        LinearLayout grayLine = new LinearLayout(getContext());
			        grayLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1));
			        grayLine.setBackgroundColor(Color.parseColor("#8F9193"));
			        LinearLayout grayLine2 = new LinearLayout(getContext());
			        grayLine2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1));
			        grayLine2.setBackgroundColor(Color.parseColor("#8F9193"));
			        LinearLayout grayLine3 = new LinearLayout(getContext());
			        grayLine3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1));
			        grayLine3.setBackgroundColor(Color.parseColor("#8F9193"));
			        LinearLayout grayLine4 = new LinearLayout(getContext());
			        grayLine4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1));
			        grayLine4.setBackgroundColor(Color.parseColor("#8F9193"));
			        				        				        
			        TextView totalScore = new TextView(getContext());
			        TextView lastScore = new TextView(getContext());
			        TextView bestScore = new TextView(getContext());
			        totalScore.setPadding(15,5,0,5);
			        totalScore.setText(getContext().getString(R.string.profileTotalScore)+pscore.getBalance());
			        totalScore.setTextSize(12);
			        totalScore.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*22),BDrawableGradient.LIGHT_GRAY_GRADIENT));
			        totalScore.setTextColor(Color.parseColor("#545859"));
			        
			        lastScore.setPadding(15,5,0,5);
			        lastScore.setText(getContext().getString(R.string.profileLastScore)+pscore.getLastscore());
			        lastScore.setTextSize(12);
			        lastScore.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*22),BDrawableGradient.LIGHT_GRAY_GRADIENT));
			        lastScore.setTextColor(Color.parseColor("#545859"));
			        
			        bestScore.setPadding(15,5,0,5);
			        bestScore.setText(getContext().getString(R.string.profileBestScore)+pscore.getBestscore());
			        bestScore.setTextSize(12);
			        bestScore.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*22),BDrawableGradient.LIGHT_GRAY_GRADIENT));
			        bestScore.setTextColor(Color.parseColor("#545859"));
			        				        
			        contests.addView(contestName);	
			        contests.addView(grayLine);
			        contests.addView(totalScore);
			        contests.addView(grayLine2);
			        contests.addView(lastScore);
			        contests.addView(grayLine3);
			        contests.addView(bestScore);
			        contests.addView(grayLine4);
		        }
		    }
		}else { // NO SCORES FOR THE APP
			if(currentSection == CURRENT_USER_PROFILE){
				TextView noScores = new TextView(getContext());
				noScores.setText(getContext().getString(R.string.profileNoScores));
				noScores.setPadding(5,10,0,2);
				noScores.setTextColor(Color.BLACK);
				contests.addView(noScores);	
			}
		} 
		
		LinearLayout mc = (LinearLayout) findViewById(R.id.maincontainer);
		LinearLayout l = (LinearLayout) findViewById(R.id.loading);
		mc.removeView(l);		
		LinearLayout gc = (LinearLayout) findViewById(R.id.goodcontent);
		gc.setVisibility(LinearLayout.VISIBLE);
	}
	
	private void showLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 50, 0, 0);				
		LinearLayout mc = (LinearLayout) findViewById(R.id.loading);
		mc.setLayoutParams(params);
		mc.addView(pb);
	}
	
	// USED FOR FRIEND PROFILE NEW MESSAGE BUTTON
	private void setMsgButton(final String extId, final String nickname){
		Button newMessage = (Button) findViewById(R.id.logout);
	    BeButton b = new BeButton(current.getContext());
	    newMessage.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
	    newMessage.setVisibility(LinearLayout.VISIBLE);
	    newMessage.setText(current.getContext().getString(R.string.messagesend));
	    newMessage.setBackgroundDrawable(
	    		b.setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));			    	 
	    newMessage.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				MessagesWrite mw = new MessagesWrite(current.getContext());
				mw.setToExtId(extId);
				mw.setToNickname(nickname);
				mw.show();												
			}
		});
	}
	
	
	public String fromIntToLevel (int level) {
		if(level == 1) return "Novice";
		if(level == 2) return "Learner";
		if(level == 3) return "Passionate";
		if(level == 4) return "Winner";
		
		return "Novice";
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  if(msg.what == LOAD_PROFILE){
				try {
					loadData();
				}catch(Exception e){e.printStackTrace();}
			  }else if(msg.what == SET_NEWMSG_BUTTON){
				Bundle b = msg.getData();
				try{
					setMsgButton(b.getString("userExt"),b.getString("nickname"));
					TextView t = (TextView)findViewById(R.id.dialogTitle);
					t.setText(String.format(current.getContext().getString(R.string.profileOf), b.getString("nickname")));
			  	}catch(Exception e){e.printStackTrace();}
			  }
			  
			  super.handleMessage(msg);
		  }
	};
}
