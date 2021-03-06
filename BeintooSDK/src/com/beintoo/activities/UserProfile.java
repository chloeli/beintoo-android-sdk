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
import com.beintoo.activities.alliances.UserAlliance;
import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerScore;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
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
	private Dialog current;
	private Context context;
	private double ratio;
	private Player currentPlayer;
	
	private int currentSection;
	
	// HANDLERS
	private final int LOAD_PROFILE = 1;
	private final int SET_NEWMSG_BUTTON = 2;
	private static final int CONNECTION_ERROR = 3;
	
	// SECTIONS
	public static final int CURRENT_USER_PROFILE = 1; 
	public static final int FRIEND_USER_PROFILE = 2;
		
	private ImageManager imageManager;
	
	public UserProfile(final Context ctx) {
		super(ctx, R.style.ThemeBeintoo);				
		current = this;
		context = ctx;
		currentSection = CURRENT_USER_PROFILE;
		setupMainLayoutGradients();
		showLoading();
		setupCurrentUserProfileLayout();		
		startLoading(null, CURRENT_USER_PROFILE);		
		imageManager = new ImageManager(context);
	}
	
	public UserProfile(final Context ctx, final String userExt) {
		super(ctx, R.style.ThemeBeintoo);				
		current = this;
		context = ctx;
		currentSection = FRIEND_USER_PROFILE;
		setupMainLayoutGradients();		
		showLoading();
		setupFriendProfileLayout();
		startLoading(userExt, FRIEND_USER_PROFILE);
		imageManager = new ImageManager(context);
	}
	
	private void setupMainLayoutGradients (){
		setContentView(R.layout.profileb);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
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
		
		currentPlayer = Current.getCurrentPlayer(context);	
		
		Button logout = (Button) findViewById(R.id.logout);
	    BeButton b = new BeButton(context);
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
					usr.detachUserFromDevice(DeviceId.getUniqueDeviceId(context), currentPlayer.getUser().getId());
					Beintoo.logout(getContext());	
					Beintoo.homeDialog.dismiss();
					current.dismiss();
				}catch (Exception e){e.printStackTrace();}	
			}
		});
				
		if(currentPlayer != null && currentPlayer.getUser() == null){ // REMOVE LOGOUT AND DETACH FROM DEVICE BUTTON AND ADD SIGNUP NOW
			findViewById(R.id.bottombuttons).setVisibility(View.GONE);
			findViewById(R.id.userinfo).setVisibility(View.GONE);
			findViewById(R.id.playersignup).setVisibility(View.VISIBLE);
			Button bt = (Button) findViewById(R.id.signupbt);
			bt.setText(Html.fromHtml(context.getString(R.string.signupnow)));
			bt.setBackgroundDrawable(
		    		b.setPressedBackg(
				    		new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_BUTTON_GRADIENT),
							new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
							new BDrawableGradient(0,(int) (ratio*30),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));
			bt.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v) {
					SignupLayouts.goSignupOrUserSelection(context);
				}
			});
		}
		
		setupToolbar();		
	}
	
	private void setupToolbar(){
		LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar);		
		toolbar.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT));
		
		final boolean isUser;
		if(currentPlayer != null && currentPlayer.getUser() != null)
			isUser = true;
		else
			isUser = false;
		
		ImageButton friends = (ImageButton) findViewById(R.id.friendsbt);
		friends.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				if(isUser){					
					Friends mf = new Friends(getContext(), null, Friends.MAIN_FRIENDS_MENU, R.style.ThemeBeintoo);
				    mf.show();	
				}else{
					SignupLayouts.signupNowDialog(context, Beintoo.FEATURE_PROFILE);
				}				
			}
		});
		
		ImageButton balancebt = (ImageButton) findViewById(R.id.balancebt);
		balancebt.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				if(isUser){
					UserBalance ub = new UserBalance(getContext());
		        	ub.show();					
				}else{
					SignupLayouts.signupNowDialog(context, Beintoo.FEATURE_PROFILE);
				}
			} 
		});
		
		ImageButton alliancebt = (ImageButton) findViewById(R.id.alliancebt);
		alliancebt.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				if(isUser){
					UserAlliance ua = new UserAlliance(getContext(),UserAlliance.ENTRY_VIEW);
		        	ua.show();	
				}else{
					SignupLayouts.signupNowDialog(context, Beintoo.FEATURE_PROFILE);
				}
			} 
		});
				
		ImageButton messages = (ImageButton) findViewById(R.id.messagesbt);
		messages.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				if(isUser){
					MessagesList ml = new MessagesList(getContext());
			        ml.show();
				}else{
					SignupLayouts.signupNowDialog(context, Beintoo.FEATURE_PROFILE);
				}														
			}
		});
		
		ImageButton settings = (ImageButton) findViewById(R.id.settingsbt);
		settings.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v) {
				if(isUser){
					openProfileSettings();
				}else{
					SignupLayouts.signupNowDialog(context, Beintoo.FEATURE_PROFILE);
				}														
			}
		});
		
		try{
			if(isUser){
				TextView unread = (TextView) findViewById(R.id.msgunread);
				unread.setText(Integer.toString(currentPlayer.getUser().getUnreadMessages()));
			}
		}catch(Exception e){e.printStackTrace();}	
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
						Current.setCurrentPlayer(getContext(), currentPlayer);
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
					
    			}catch (Exception e){manageConnectionException();}			
    		}
		}).start();
	}
	
	private void loadData (){
		// SETTING UP TEXTVIEWS AND PROFILE IMAGE
		ImageView profilepict = (ImageView) findViewById(R.id.profilepict);		
		TextView nickname = (TextView) findViewById(R.id.nickname);
		TextView alliance = (TextView) findViewById(R.id.alliance);
		TextView level = (TextView) findViewById(R.id.level);
		TextView dollars = (TextView) findViewById(R.id.bedollars);
		TextView bescore = (TextView) findViewById(R.id.salary);
		LinearLayout contestsContainer = (LinearLayout) findViewById(R.id.contests);
		if(currentPlayer.getUser() != null){			
			profilepict.setTag(currentPlayer.getUser().getUserimg());
			imageManager.displayImage(currentPlayer.getUser().getUserimg(), context, profilepict);			
			nickname.setText(currentPlayer.getUser().getNickname());
			level.setText(getContext().getString(R.string.profileLevel)+fromIntToLevel(currentPlayer.getUser().getLevel()));
			dollars.setText("Bedollars: "+currentPlayer.getUser().getBedollars());
			bescore.setText("Bescore: "+currentPlayer.getUser().getBescore());
		}else{
			nickname.setText("Player");
			level.setVisibility(View.INVISIBLE);
			dollars.setVisibility(View.INVISIBLE);
			profilepict.setImageResource(R.drawable.profbt);
		}
		if(currentPlayer.getAlliance() != null){
			alliance.setText(currentPlayer.getAlliance().getName());
		}else{
			alliance.setVisibility(View.GONE);
		}			
		
		Map<String, PlayerScore> playerScore = currentPlayer.getPlayerScore();
		
		if(playerScore != null) { // THE USER HAS SCORES FOR THE APP
			Iterator<?> it = playerScore.entrySet().iterator();
		    while (it.hasNext()) {			    	
		        @SuppressWarnings("unchecked")
				Map.Entry<String, PlayerScore> pairs = (Map.Entry<String, PlayerScore>) it.next();
		        if(pairs.getValue().getContest().isPublic()){
			        PlayerScore pscore = pairs.getValue();
			        
			        LinearLayout contestsRow = new LinearLayout(getContext());
			        LinearLayout contestsNameFeed = new LinearLayout(getContext());
			        LinearLayout contests = new LinearLayout(getContext());
			        LinearLayout grayLine = new LinearLayout(getContext());
			        LinearLayout grayLine2 = new LinearLayout(getContext());
			        
			        contestsRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			        contestsRow.setOrientation(LinearLayout.VERTICAL);
			        contestsNameFeed.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			        contestsNameFeed.setOrientation(LinearLayout.VERTICAL);		
			        contestsNameFeed.setPadding(12,5,0,5);
			        contestsNameFeed.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.GRAY_GRADIENT));			        
			        contests.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			        contests.setOrientation(LinearLayout.HORIZONTAL);		
			        contests.setPadding(12,5,0,5);
			        grayLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1));
			        grayLine.setBackgroundColor(Color.parseColor("#8F9193"));			        
			        grayLine2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,1));
			        grayLine2.setBackgroundColor(Color.parseColor("#8F9193"));
			        
			        
			        TextView contestName = new TextView(getContext());				        
			        TextView feedData = null;
			        TextView totalScore = new TextView(getContext());
			        TextView lastScore = new TextView(getContext());
			        TextView bestScore = new TextView(getContext());
			        
			        contestName.setText(pscore.getContest().getName());
			        contestName.setTextColor(Color.BLACK);	 			        
			        contestsNameFeed.addView(contestName);
			        
			        String feed = pscore.getContest().getFeed();			        
			        if(feed != null){
			        	feedData = new TextView(getContext());
			        	feedData.setTextColor(Color.parseColor("#6E6E6E"));	 			        					        
			        	feedData.setText(feed);
			        	contestsNameFeed.addView(feedData);
			        	contestsNameFeed.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*60),BDrawableGradient.GRAY_GRADIENT));
			        }
			        
			        //totalScore.setPadding(15,5,0,5);
			        totalScore.setText(getContext().getString(R.string.profileTotalScore)+pscore.getBalance());
			        totalScore.setTextSize(12);
			        totalScore.setTextColor(Color.parseColor("#545859"));
			        
			        lastScore.setPadding(10,0,0,0);
			        lastScore.setText(getContext().getString(R.string.profileLastScore)+pscore.getLastscore());
			        lastScore.setTextSize(12);
			        lastScore.setTextColor(Color.parseColor("#545859"));
			        
			        bestScore.setPadding(10,0,0,0);
			        bestScore.setText(getContext().getString(R.string.profileBestScore)+pscore.getBestscore());
			        bestScore.setTextSize(12);
			        bestScore.setTextColor(Color.parseColor("#545859"));
			        				        
			        
			        contestsRow.addView(contestsNameFeed);
			        contestsRow.addView(grayLine);
			        contests.addView(totalScore);			        
			        contests.addView(lastScore);
			        contests.addView(bestScore);
			        contestsRow.addView(contests);
			        contestsRow.addView(grayLine2);
			        
			        contestsContainer.addView(contestsRow);			        
		        }
		    }
		}else { // NO SCORES FOR THE APP
			if(currentSection == CURRENT_USER_PROFILE){
				TextView noScores = new TextView(getContext());
				noScores.setText(getContext().getString(R.string.profileNoScores));
				noScores.setPadding(5,10,0,2);
				noScores.setTextColor(Color.BLACK);
				contestsContainer.addView(noScores);	
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
	    BeButton b = new BeButton(context);
	    newMessage.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
	    newMessage.setVisibility(LinearLayout.VISIBLE);
	    newMessage.setText(context.getString(R.string.messagesend));
	    newMessage.setBackgroundDrawable(
	    		b.setPressedBackg(
			    		new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*50),BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));			    	 
	    newMessage.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				MessagesWrite mw = new MessagesWrite(context);
				mw.setToExtId(extId);
				mw.setToNickname(nickname);
				mw.show();												
			}
		});
	}
	
	private void openProfileSettings(){
		String webUrl = null;
        if(!BeintooSdkParams.internalSandbox){
        	webUrl = BeintooSdkParams.webUrl;
        }else{
        	webUrl = BeintooSdkParams.sandboxWebUrl;
        }
		Uri.Builder b = Uri.parse(webUrl+"nativeapp/settings.html").buildUpon();
        Player p = Current.getCurrentPlayer(getContext());
        if(p!=null && p.getUser() != null){
        	b.appendQueryParameter("extId", p.getUser().getId());
        	b.appendQueryParameter("guid", p.getGuid());
        }
        b.appendQueryParameter("apikey", DeveloperConfiguration.apiKey);
        
        BeintooBrowser bb = new BeintooBrowser(getContext(), b.build().toString());
        bb.show();
	}
	
	private void manageConnectionException (){
		UIhandler.sendEmptyMessage(3);		
	}
	
	public String fromIntToLevel (int level) {
		if(level == 1) return "Novice";
		if(level == 2) return "Learner";
		if(level == 3) return "Passionate";
		if(level == 4) return "Winner";
		if(level == 5) return "Master";
		
		return "Novice";
	}
	
	@Override
	public void onBackPressed() {		
		// STOPPING IMAGE MANAGER THREADS
		try {
			if(imageManager != null)
				imageManager.interrupThread();
		}catch (Exception e){
			e.printStackTrace();
		}		
		super.onBackPressed();
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
					t.setText(String.format(context.getString(R.string.profileOf), b.getString("nickname")));
			  	}catch(Exception e){e.printStackTrace();}
			  }else if(msg.what == CONNECTION_ERROR){
				  LinearLayout mc = (LinearLayout) findViewById(R.id.goodcontent);
				  LinearLayout loading = (LinearLayout) findViewById(R.id.loading);
				  mc.removeAllViews();
				  loading.removeAllViews();
				  ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , context, null);
			  }
			  
			  super.handleMessage(msg);
		  }
	};
}
