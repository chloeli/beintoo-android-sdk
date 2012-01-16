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

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.ImageManager;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Challenge;
import com.beintoo.wrappers.Player;

public class Challenges extends Dialog implements OnClickListener{
	static Dialog current;
	private Context context;
	Challenge [] challenge;
	public final static int PENDING = 0;
	public final static int ACCEPTED = 1;
	public final static int ENDED = 2;
	private final int CONNECTION_ERROR = 3;
	private final ImageManager imageManager;
	
	int CURRENT_SECTION = 0;
	final double ratio;
	
	public Challenges(Context ctx, boolean dontLoadOnStart) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.challenges);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		context = ctx;
		current = this;
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.challenges);

		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		imageManager = new ImageManager(context);
		
		try{
			showLoading();
			if(!dontLoadOnStart)
				startLoading();
		}catch (Exception e){e.printStackTrace();}
		
		
		final BeButton b = new BeButton(ctx);
		
		// PENDING CHALLENGES BUTTON
		final Button pending = (Button) findViewById(R.id.pendingchall);		
		pending.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));	
		pending.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				pending.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooUser newuser = new BeintooUser();            				
							// GET THE CURRENT LOGGED PLAYER
							Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
							challenge = newuser.challengeShow(p.getUser().getId(), "TO_BE_ACCEPTED");				
							UIhandler.sendEmptyMessage(PENDING);
						}catch (Exception e){
							e.printStackTrace();
							manageConnectionException();
						}
					}
				}).start();
			}
		});
		
		// ACCEPTED CHALLENGES BUTTON
		final Button accepted = (Button) findViewById(R.id.acceptedchall);		
		accepted.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		accepted.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				accepted.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooUser newuser = new BeintooUser();            				
							// GET THE CURRENT LOGGED IN PLAYER
							Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
							challenge = newuser.challengeShow(p.getUser().getId(), "STARTED");				
							UIhandler.sendEmptyMessage(ACCEPTED);
            			}catch (Exception e){
            				e.printStackTrace();
            				manageConnectionException();
            			}
            		}
				}).start();	
			}
		});
		
		
		// ENDED CHALLENGES BUTTON
		final Button ended = (Button) findViewById(R.id.endedchall);		
		ended.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		ended.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				ended.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				
				//final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooUser newuser = new BeintooUser();            				
							// GET THE CURRENT LOGGED IN PLAYER
							Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
							challenge = newuser.challengeShow(p.getUser().getId(), "ENDED");				
							UIhandler.sendEmptyMessage(ENDED);
            			}catch (Exception e){
            				e.printStackTrace();
            				manageConnectionException();
            			}
            			//dialog.dismiss();
            		}
				}).start();
			}
		});				
	}
	
	public Challenges(Context ctx) {
		this(ctx, false);
	}
	
	public void startLoading (){
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
					BeintooUser newuser = new BeintooUser();            				
					// GET THE CURRENT LOGGED PLAYER
					Player p = Current.getCurrentPlayer(context);
					challenge = newuser.challengeShow(p.getUser().getId(), "TO_BE_ACCEPTED");
					UIhandler.sendEmptyMessage(PENDING);
					
    			}catch (Exception e){
    				e.printStackTrace();
    				manageConnectionException();
    			}
    		}
		}).start();
	}
	
	private void loadChallenges(int section){
		CURRENT_SECTION = section;
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.setColumnStretchable(1, true);
		table.removeAllViews();
		Player p = Current.getCurrentPlayer(context);
			
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();		
		
	    for (int i = 0; i < challenge.length; i++){
	    	String nick;
	    	String contest;	    	
	    	ImageView image = new ImageView(context);
	    	
    		if(p.getUser().getId().equals(challenge[i].getPlayerFrom().getUser().getId())){
    			image.setTag(challenge[i].getPlayerTo().getUser().getUserimg());    			
    			imageManager.displayImage(challenge[i].getPlayerTo().getUser().getUserimg(), context, image);    			
    			nick = getContext().getString(R.string.challFromTo)+"<font color=\"#000000\">"+challenge[i].getPlayerTo().getUser().getNickname()+"</font>";
    			contest = challenge[i].getContest().getName();
    		}else{
    			image.setTag(challenge[i].getPlayerFrom().getUser().getUserimg());
    			imageManager.displayImage(challenge[i].getPlayerFrom().getUser().getUserimg(), context, image);    			
    			nick = "<font color=\"#000000\">"+challenge[i].getPlayerFrom().getUser().getNickname()+"</font>" + getContext().getString(R.string.challFromUser);
    			contest = challenge[i].getContest().getName();    			
    		}
    		
    		TableRow row = createRow(image, nick,contest, table.getContext());
    		    		
    		if(section == PENDING){
	    		if(!p.getUser().getId().equals(challenge[i].getPlayerFrom().getUser().getId())){
	    			row.setOnClickListener(this);
	    		}
    		}else{ row.setOnClickListener(this); }
    		
			row.setId(count);
			rowList.add(row);
			
			BeButton b = new BeButton(getContext());
			if(i % 2 == 0)
	    		row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.LIGHT_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			else
				row.setBackgroundDrawable(b.setPressedBackg(
			    		new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			
			View spacer = createSpacer(getContext(),1,1);
			spacer.setId(-100);
			rowList.add(spacer);
			View spacer2 = createSpacer(getContext(),2,1);
			spacer2.setId(-100);
			rowList.add(spacer2);
			count++;
	    }
	
	    for (View row : rowList) {
	
		      table.addView(row);
		}
	}
	
	
	private TableRow createRow(ImageView image, String name, String contest, Context activity) {
		  TableRow row = new TableRow(activity);
		  
		  int size = (int) (50 * context.getResources().getDisplayMetrics().density + 0.5f);
		  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
		  params.rightMargin = (int)(ratio * 10);
		  params.leftMargin = (int)(ratio * 10);
		  image.setLayoutParams(params);
		  params.gravity = Gravity.CENTER;
	    	
		  LinearLayout rowContainer = new LinearLayout(row.getContext());
		  
		  rowContainer.setOrientation(LinearLayout.HORIZONTAL);
		  rowContainer.addView(image);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  
		  // NICKNAME TEXTVIEW
		  TextView contestName = new TextView(activity);		  
		  if(contest.length() > 26)
			  contestName.setText(contest.substring(0, 23)+"...");
		  else
			  contestName.setText(contest);
		  
		  contestName.setPadding(0, 0, 0, 0);
		  contestName.setTextColor(Color.parseColor("#545859"));
		  contestName.setTextSize(16);
		  contestName.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  // NICKNAME FROM TO TEXTVIEW
		  TextView from = new TextView(activity);		
		  from.setText(Html.fromHtml(name));		  
		  from.setPadding(0, 0, 0, 0);
		  from.setTextColor(Color.parseColor("#787A77"));
		  from.setTextSize(14);
		  from.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  	
		  main.addView(contestName);
		  main.addView(from);	
		  rowContainer.addView(main, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int)(ratio * 70)));
		  
		  row.addView(rowContainer);

		  return row;
	}
	
	private static View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color == 1)
			  spacer.setBackgroundColor(Color.parseColor("#8F9193"));
		  else if(color == 2)
			  spacer.setBackgroundColor(Color.WHITE);

		  return spacer;
	}

	public void onClick(View v) {
		if(v.getId()!=-100){ // REMOVE THE SPACER
			final int challengeType;			
			try {
				if(challenge[v.getId()].getType().equals("CHALLENGE"))
					challengeType = ChallengeOverview.CHALLENGE_48_HOURS;
				else if(challenge[v.getId()].getActor().getGuid().equals(challenge[v.getId()].getPlayerFrom().getGuid()))
					challengeType = ChallengeOverview.DIRECTION_SOURCE;
				else
					challengeType = ChallengeOverview.DIRECTION_DEST;
				
				if(CURRENT_SECTION == PENDING){				 
					ChallengeOverview challengeShow = new ChallengeOverview(getContext(),challenge[v.getId()], v, challengeType, PENDING);
					challengeShow.show();
				}else if(CURRENT_SECTION == ACCEPTED){
					ChallengeOverview challengeShow = new ChallengeOverview(getContext(),challenge[v.getId()], v, challengeType, ACCEPTED);
					challengeShow.show();
				}else if(CURRENT_SECTION == ENDED){
					ChallengeOverview challengeShow = new ChallengeOverview(getContext(),challenge[v.getId()], v, challengeType, ENDED);
					challengeShow.show();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}	
	}
	
	private void loadEmptySection (int section){
		TextView noChallenge = new TextView(getContext());
		if(section == PENDING)
			noChallenge.setText(getContext().getString(R.string.challNoPendingC));
		else if(section == ACCEPTED)
			noChallenge.setText(getContext().getString(R.string.challNoAccepted));
		else if(section == ENDED)
			noChallenge.setText(getContext().getString(R.string.challNoEnded));
		
		noChallenge.setTextColor(Color.GRAY);
		noChallenge.setPadding(15,15,0,0);
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();
		table.addView(noChallenge);
	}
	
	private void resetButtons(){
		BeButton b = new BeButton(getContext());
		findViewById(R.id.pendingchall).setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		findViewById(R.id.acceptedchall).setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		findViewById(R.id.endedchall).setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));	
		
		
	}
	
	private void showLoading (){
		ProgressBar pb = new ProgressBar(getContext());
		pb.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.progress));
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int)(100*ratio), 0, 0);		
		TableRow row = new TableRow(getContext());
		row.setLayoutParams(params);
		row.setGravity(Gravity.CENTER);
		row.addView(pb);
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();
		table.addView(row);
	}
	
	private void manageConnectionException (){
		UIhandler.sendEmptyMessage(3);		
	}
	
	public void openSectionFromOutside(int section){
		if(section == PENDING){
			final Button pending = (Button) findViewById(R.id.pendingchall);
			pending.performClick();
		}else if(section == ACCEPTED){
			final Button accepted = (Button) findViewById(R.id.acceptedchall);
			accepted.performClick();
		}else if(section == ENDED){
			final Button ended = (Button) findViewById(R.id.endedchall);
			ended.performClick();
		}
	}
	
	@Override
	public void onBackPressed() {		
		// STOPPING IMAGE MANAGER THREADS
		if(imageManager != null){			
			imageManager.interrupThread();
		}
		super.onBackPressed();
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  if(msg.what == PENDING){
				  if(challenge.length > 0)
						loadChallenges(PENDING);
				  else { // NO CHALLENGE REQUEST
						loadEmptySection(PENDING);
				  }
			  }else if(msg.what == ACCEPTED){
				  if(challenge.length > 0)
						loadChallenges(ACCEPTED);
				  else { // NO CHALLENGE REQUEST
						loadEmptySection(ACCEPTED);
				  }
			  }else if (msg.what == ENDED){
				  if(challenge.length > 0)
						loadChallenges(ENDED);
				  else { // NO CHALLENGE REQUEST
						loadEmptySection(ENDED);
				  }
			  }else if(msg.what == CONNECTION_ERROR){
				  TableLayout table = (TableLayout) findViewById(R.id.table);
				  table.removeAllViews();
				  ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , current.getContext(), null);
			  }
			  super.handleMessage(msg);
		  }
	};
}
