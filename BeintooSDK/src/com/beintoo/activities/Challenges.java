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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Challenge;
import com.beintoo.wrappers.Player;
import com.google.gson.Gson;

public class Challenges extends Dialog implements OnClickListener{
	static Dialog current;
	Challenge [] challenge;
	final int PENDING = 0;
	final int ACCEPTED = 1;
	final int ENDED = 2;
	int CURRENT_SECTION = 0;
	public Challenges(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.challenges);
		current = this;
		try{
			challenge = new Gson().fromJson(PreferencesHandler.getString("challenge", getContext()), Challenge[].class);
			if(challenge.length > 0)
				loadChallenges(PENDING);
			else { // NO CHALLENGE REQUEST
				TextView noChallenge = new TextView(getContext());
				noChallenge.setText(getContext().getString(R.string.challNoPending));
				noChallenge.setTextColor(Color.GRAY);
				noChallenge.setPadding(15,0,0,0);
				TableLayout table = (TableLayout) findViewById(R.id.table);
				table.addView(noChallenge);
			}
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx);}
		
		
		BeButton b = new BeButton(ctx);
		final Button pending = (Button) findViewById(R.id.pendingchall);		
		pending.setBackgroundDrawable(b.setPressedBg(R.drawable.pending, R.drawable.pending_h, R.drawable.pending_h));
		pending.setBackgroundResource(R.drawable.pending_h);
		pending.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				pending.setBackgroundResource(R.drawable.pending_h);
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
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
						}
						dialog.dismiss();
					}
				}).start();
			}
		});
		
		
		final Button accepted = (Button) findViewById(R.id.acceptedchall);		
		accepted.setBackgroundDrawable(b.setPressedBg(R.drawable.ongoing, R.drawable.ongoing_h, R.drawable.ongoing_h));
		accepted.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				accepted.setBackgroundResource(R.drawable.ongoing_h);
				
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooUser newuser = new BeintooUser();            				
							// GET THE CURRENT LOGGED PLAYER
							Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
							challenge = newuser.challengeShow(p.getUser().getId(), "STARTED");				
							UIhandler.sendEmptyMessage(ACCEPTED);
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            			dialog.dismiss();
            		}
				}).start();	
			}
		});
		
		final Button ended = (Button) findViewById(R.id.endedchall);		
		ended.setBackgroundDrawable(b.setPressedBg(R.drawable.ended, R.drawable.ended_h, R.drawable.ended_h));
		ended.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				resetButtons();
				ended.setBackgroundResource(R.drawable.ended_h);
				
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooUser newuser = new BeintooUser();            				
							// GET THE CURRENT LOGGED PLAYER
							Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", getContext()));
							challenge = newuser.challengeShow(p.getUser().getId(), "ENDED");				
							UIhandler.sendEmptyMessage(ENDED);
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            			dialog.dismiss();
            		}
				}).start();
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
	
	public void loadChallenges(int section){
		CURRENT_SECTION = section;
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.setColumnStretchable(1, true);
		table.removeAllViews();
		Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);
			
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
	
	    for (int i = 0; i < challenge.length; i++){
	    	final LoaderImageView image;
	    	String nick;
	    	String contest;
	    	
    		if(p.getUser().getId().equals(challenge[i].getPlayerFrom().getUser().getId())){
    			//image = new LoaderImageView(getContext(), challenge[i].getPlayerTo().getUser().getUsersmallimg());
    			image = new LoaderImageView(getContext(),challenge[i].getPlayerTo().getUser().getUserimg(),70,70);
    			nick = getContext().getString(R.string.challFromTo)+challenge[i].getPlayerTo().getUser().getNickname();
    			contest = challenge[i].getContest().getName();
    		}else{
    			//image = new LoaderImageView(getContext(), challenge[i].getPlayerFrom().getUser().getUsersmallimg());
    			image = new LoaderImageView(getContext(),challenge[i].getPlayerFrom().getUser().getUserimg(),70,70);
    			nick = getContext().getString(R.string.challFrom)+challenge[i].getPlayerFrom().getUser().getNickname()+
    			getContext().getString(R.string.challYou);
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
			if(i%2 == 0) row.setBackgroundColor(Color.parseColor("#d8eaef"));
			View spacer = createSpacer(getContext(),0,2);
			spacer.setId(-100);
			rowList.add(spacer);
	    	count++;
	    }
	
	    for (View row : rowList) {
	
		      table.addView(row);
		}
	}
	
	
	public TableRow createRow(View image, String name, String contest, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding(10, 10, 10, 10);		  
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  row.addView(image);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  
		  // NICKNAME TEXTVIEW
		  TextView nameView = new TextView(activity);		  
		  if(contest.length() > 26)
			  nameView.setText(contest.substring(0, 23)+"...");
		  else
			  nameView.setText(contest);
		  
		  nameView.setPadding(0, 10, 10, 10);
		  nameView.setTextColor(Color.parseColor("#83be56"));
		  nameView.setTypeface(null,Typeface.BOLD);
		  nameView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  // CONTEST NAME TEXTVIEW
		  TextView contestView = new TextView(activity);		
		  contestView.setText(name);		  
		  contestView.setPadding(0, 10, 10, 10);
		  contestView.setTextColor(Color.parseColor("#787A77"));
		  contestView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
		  	
		  main.addView(nameView);
		  main.addView(contestView);
		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
	
		  return row;
	}
	
	private static View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);

		 // spacer.setPadding(50,50,50,50);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color != 0)
			  spacer.setBackgroundColor(Color.LTGRAY);

		  return spacer;
	}

	public void onClick(View v) {
		if(v.getId()!=-100){ // REMOVE THE SPACER
			if(CURRENT_SECTION == PENDING)
				this.respondDialog(v);
			else if(CURRENT_SECTION == ACCEPTED){
				ChallengeOverview challengeShow = new ChallengeOverview(getContext(),challenge[v.getId()], ACCEPTED);
				challengeShow.show();
			}else if(CURRENT_SECTION == ENDED){
				ChallengeOverview challengeShow = new ChallengeOverview(getContext(),challenge[v.getId()], ENDED);
				challengeShow.show();
			}
		}	
	}
	
	public void respondDialog (final View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(getContext().getString(R.string.challAccept)+challenge[v.getId()].getPrice().intValue()+" BeDollars?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		 
		        	   respondChallenge(challenge[v.getId()].getPlayerTo().getUser().getId(),
		        			   challenge[v.getId()].getPlayerFrom().getUser().getId(),"ACCEPT",
		        			   challenge[v.getId()].getContest().getCodeID());      	   
		        	   dialog.dismiss();
		        	   MessageDisplayer.showMessage(getContext(), getContext().getString(R.string.challAccepted));
		        	   
		        	   TableLayout table = (TableLayout) v.getParent();
		        	   table.removeView(v);
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   respondChallenge(challenge[v.getId()].getPlayerTo().getUser().getId(),
		        			   challenge[v.getId()].getPlayerFrom().getUser().getId(),"REFUSE",
		        			   challenge[v.getId()].getContest().getCodeID());
		        	   dialog.dismiss();
		        	   MessageDisplayer.showMessage(getContext(), getContext().getString(R.string.challRefused));

		        	   TableLayout table = (TableLayout) v.getParent();
		        	   table.removeView(v);
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void respondChallenge (final String userExtFrom, final String userExtTo, final String action, final String codeID){
		new Thread(new Runnable(){      
    		public void run(){
    			try{             				
    				BeintooUser user = new BeintooUser();
    				user.challenge(userExtFrom, userExtTo, action, codeID); 
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    			
    		}
		}).start();
	}
	
	public String getStatus (String status){
		if(status.equals("TO_BE_ACCEPTED"))
			return "Pending";
		else if(status.equals("STARTED"))
			return "Ongoing";
		else if(status.equals("ENDED"))
			return "Over";
		else
			return "";
	}
	
	public void loadEmptySection (int section){
		TextView noChallenge = new TextView(getContext());
		if(section == PENDING)
			noChallenge.setText(getContext().getString(R.string.challNoPendingC));
		else if(section == ACCEPTED)
			noChallenge.setText(getContext().getString(R.string.challNoAccepted));
		else if(section == ENDED)
			noChallenge.setText(getContext().getString(R.string.challNoEnded));
		
		noChallenge.setTextColor(Color.GRAY);
		noChallenge.setPadding(15,0,0,0);
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();
		table.addView(noChallenge);
	}
	
	public void resetButtons(){
		findViewById(R.id.pendingchall).setBackgroundResource(R.drawable.pending);
		findViewById(R.id.acceptedchall).setBackgroundResource(R.drawable.ongoing);
		findViewById(R.id.endedchall).setBackgroundResource(R.drawable.ended);	
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
			  }
			  super.handleMessage(msg);
		  }
	};
}
