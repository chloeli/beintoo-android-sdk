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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
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
	public Challenges(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.challenges);
		current = this;
		
		challenge = new Gson().fromJson(PreferencesHandler.getString("challenge", getContext()), Challenge[].class);
		if(challenge.length > 0)
			loadChallenges();
		else { // NO CHALLENGE REQUEST
			System.out.println("no req");
			TableRow r = new TableRow(getContext());
			TextView noChallenge = new TextView(getContext());
			noChallenge.setText("You don't have any challenge request");
			noChallenge.setTextColor(Color.GRAY);
			noChallenge.setPadding(15,15,0,0);
			
			r.addView(noChallenge, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));			
			TableLayout table = (TableLayout) findViewById(R.id.table);
			table.addView(r);
		}
		
		
		Button close = (Button) findViewById(R.id.close);
		BeButton b = new BeButton(ctx);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));
	    close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();				
			}
		});
	}
	
	public void loadChallenges(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.setColumnStretchable(1, true);
		//table.removeAllViews();
		Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);
			
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
	
	    for (int i = 0; i < challenge.length; i++){
	    	final LoaderImageView image;
	    	String nick;
	    	String contest;
	    	
    		if(p.getUser().getId().equals(challenge[i].getPlayerFrom().getUser().getId())){
    			image = new LoaderImageView(getContext(), challenge[i].getPlayerTo().getUser().getUsersmallimg());
    			nick = "From you to "+challenge[i].getPlayerTo().getUser().getNickname();
    			contest = challenge[i].getContest().getName() +" ("+this.getStatus(challenge[i].getStatus())+")";
    		}else{
    			image = new LoaderImageView(getContext(), challenge[i].getPlayerFrom().getUser().getUsersmallimg());
    			nick = "From "+challenge[i].getPlayerFrom().getUser().getNickname()+" to you";
    			contest = challenge[i].getContest().getName()+" ("+this.getStatus(challenge[i].getStatus())+")";
    			
    		}
    		
    		TableRow row = createRow(image, nick,contest, table.getContext());
    		if(!p.getUser().getId().equals(challenge[i].getPlayerFrom().getUser().getId())){
    			row.setOnClickListener(this);
    		}
			row.setId(count);
			rowList.add(row);
			if(i%2 == 0) row.setBackgroundColor(Color.parseColor("#d8eaef"));
			View spacer = createSpacer(getContext(),0,10);
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
		  
		  
		 /* bt.setText("Respond");
		  //bt.setPadding(10,10,10,10);		 
		  bt.setGravity(Gravity.CENTER);
		  
		  
		  //row.addView(image);
		  //row.addView(nameView);
		  row.addView(bt);*/
		  
		   
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
		if(v.getId()!=-100)
			this.respondDialog(v);
	}
	
	public void respondDialog (final View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage("Do you want to accept this challenge?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		 
		        	   respondChallenge(challenge[v.getId()].getPlayerTo().getUser().getId(),
		        			   challenge[v.getId()].getPlayerFrom().getUser().getId(),"ACCEPT",
		        			   challenge[v.getId()].getContest().getCodeID());      	   
		        	   dialog.dismiss();
		        	   MessageDisplayer.showMessage(getContext(), "Challenge accepted");
		        	   
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
		        	   MessageDisplayer.showMessage(getContext(), "Challenge refused");

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
}
