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
package com.beintoo.activities.leaderboard;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.activities.signupnow.SignupLayouts;
import com.beintoo.beintoosdk.BeintooApp;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DialogStack;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Contest;
import com.beintoo.wrappers.Player;

public class LeaderboardContest extends Dialog implements OnClickListener{
	private Dialog current;
	private Context currentContext;
	private final double ratio;
	private List<Contest> contests;
	private Player player = null;
	private String kind = null;
	private String selectedContest;
	
	public LeaderboardContest(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);
		setContentView(R.layout.contestselection);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		currentContext = ctx;
		
		// SET TITLE
		TextView t = (TextView)findViewById(R.id.dialogTitle);
		t.setText(R.string.leaderboard);

		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));

		try {
			player = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", currentContext));
		}catch (Exception e){ e.printStackTrace(); }
		
		try {			
			showLoading();
			startLoading();
		}catch(Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx,e);}	
	 
		final BeButton b = new BeButton(ctx);
		Button general = (Button) findViewById(R.id.generaleader);
		
		general.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
		general.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if(Beintoo.isLogged(currentContext)){
					// FRIEND FILTER
					findViewById(R.id.onlyf).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.closest).setVisibility(LinearLayout.VISIBLE);
					ImageButton friends = (ImageButton) findViewById(R.id.onlyfriends);
					ImageButton closestbt = (ImageButton) findViewById(R.id.closestbt);					
					friends.setImageResource(R.drawable.noselected);
					friends.setTag("notselected");					
					closestbt.setImageResource(R.drawable.noselected);
					closestbt.setTag("notselected");
				}
				resetButtons();
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooApp app = new BeintooApp();							
							kind = null;
							contests = app.getContests(null, true, null);
							UIhandler.sendEmptyMessage(0);
            			}catch (Exception e){
            				e.printStackTrace();
            				manageConnectionException();
            			}
            		}
				}).start();
			}
        });
		
		Button alliances = (Button) findViewById(R.id.friendsleader);
		alliances.setBackgroundDrawable(b.setPressedBackg(
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
				new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
		alliances.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {					
				// FRIEND FILTER
				findViewById(R.id.onlyf).setVisibility(LinearLayout.GONE);
				findViewById(R.id.closest).setVisibility(LinearLayout.GONE);
				resetButtons();
				v.setBackgroundDrawable(b.setPressedBackg(
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
						new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));				
				
				showLoading();
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
            				BeintooApp app = new BeintooApp();
            				contests = app.getContests(null, true, null);
            				kind = "ALLIANCES";
            				UIhandler.sendEmptyMessage(0);							
            			}catch (Exception e){
            				e.printStackTrace();
            				manageConnectionException();
            			}
            		}
				}).start();
			}
        });
		
		if(Beintoo.isLogged(ctx)){				
			findViewById(R.id.onlyf).setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));
			final LinearLayout friendsbutton = (LinearLayout) findViewById(R.id.friendsonly);
			friendsbutton.setBackgroundDrawable(b.setPressedBackg(
					new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
					new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
					new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			friendsbutton.setOnClickListener(new ImageButton.OnClickListener(){
				public void onClick(View v) {					
					// RESET THE OTHER FILTER BUTTON
					ImageButton closestbt = (ImageButton) findViewById(R.id.closestbt);
					closestbt.setImageResource(R.drawable.noselected);
					closestbt.setTag("notselected");

					final ImageButton friends = (ImageButton) findViewById(R.id.onlyfriends);
					if(friends.getTag().equals("notselected")){
						friends.setImageResource(R.drawable.selected);
						friends.setTag("selected");
						kind = "FRIENDS";																		
					}else if(friends.getTag().equals("selected")){
						friends.setImageResource(R.drawable.noselected);
						friends.setTag("notselected");
						kind = null;
					}
					  
					showLoading();
					new Thread(new Runnable(){      
	            		public void run(){
	            			try{ 
								BeintooApp app = new BeintooApp();		
								String userExt = null;
								if(kind != null && kind.equals("FRIENDS"))
									if(player != null && player.getUser() != null)
										userExt = player.getUser().getId();
								contests = app.getContests(null, true, userExt);							
								UIhandler.sendEmptyMessage(0); 
	            			}catch (Exception e){
	            				e.printStackTrace();
	            				manageConnectionException();
	            			}
	            		}
					}).start();
				}
	        });
			
			findViewById(R.id.closest).setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));
			final LinearLayout closest = (LinearLayout) findViewById(R.id.closest);
			closest.setBackgroundDrawable(b.setPressedBackg(
					new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
					new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT),
					new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
			closest.setOnClickListener(new Button.OnClickListener(){
				public void onClick(View v) {																	
					// RESET THE OTHER FILTER BUTTON
					ImageButton onlyfriends = (ImageButton) findViewById(R.id.onlyfriends);
					onlyfriends.setImageResource(R.drawable.noselected);
					onlyfriends.setTag("notselected");
					
					final ImageButton closestbt = (ImageButton) findViewById(R.id.closestbt);
					if(closestbt.getTag().equals("notselected")){
						closestbt.setImageResource(R.drawable.selected);
						closestbt.setTag("selected");
						kind = "CLOSEST";																		
					}else if(closestbt.getTag().equals("selected")){
						closestbt.setImageResource(R.drawable.noselected);
						closestbt.setTag("notselected");
						kind = null;
					}
					
					showLoading();
					new Thread(new Runnable(){      
	            		public void run(){
	            			try{ 
	            				BeintooApp app = new BeintooApp();
	            				contests = app.getContests(null, true, null);	            				
	            				UIhandler.sendEmptyMessage(0);							
	            			}catch (Exception e){
	            				e.printStackTrace();
	            				manageConnectionException();
	            			}
	            		}
					}).start();
				}
	        });
			
		}else { // IF THE USER IS NOT LOGGED HIDE ALL, FRIENDS, CLOSEST BUTTONS AND ADD SIGNUP ROW
			findViewById(R.id.onlyf).setVisibility(View.GONE);
			findViewById(R.id.closest).setVisibility(View.GONE);			
			LinearLayout signup = (LinearLayout) findViewById(R.id.signupPlayer);
			signup.addView(SignupLayouts.signupRow(ctx, Beintoo.FEATURE_LEADERBOARD));
			signup.setVisibility(View.VISIBLE);			
		}
	}
	
	public void startLoading (){
		new Thread(new Runnable(){      
    		public void run(){
    			try{ 
    				BeintooApp app = new BeintooApp();							    				
					contests = app.getContests(null, true, null);					
					UIhandler.sendEmptyMessage(0);
    			}catch (Exception e){
    				manageConnectionException();
    				e.printStackTrace();
    			}
    		}
		}).start();
	}
	
	public void loadContestTable(){
		try {
			TableLayout table = (TableLayout) findViewById(R.id.tablecontest);
			table.setColumnStretchable(1, true);
			table.removeAllViews();
			
			BeButton b = new BeButton(getContext());
			
			final ArrayList<View> rowList = new ArrayList<View>();
			
			 
		    for(int i = 0; i<contests.size(); i++){
				
		    	TableRow row = createRow(contests.get(i).getName(),getContext());
		    	if(i % 2 == 0)
		    		row.setBackgroundDrawable(b.setPressedBackg(
				    		new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.LIGHT_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				else
					row.setBackgroundDrawable(b.setPressedBackg(
				    		new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT),
							new BDrawableGradient(0,(int)(ratio * 35),BDrawableGradient.HIGH_GRAY_GRADIENT)));
				
		    	TableLayout.LayoutParams tableRowParams=
	    		  new TableLayout.LayoutParams
	    		  (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
	    		row.setLayoutParams(tableRowParams);
		    	row.setId(i);
				rowList.add(row);
				View spacer = createSpacer(getContext(),1,1);
				spacer.setId(-100);
				rowList.add(spacer);
				View spacer2 = createSpacer(getContext(),2,1);
				spacer2.setId(-100);
				rowList.add(spacer2);
		    }
		    
		    for (View row : rowList) {
			      row.setPadding(0, 0, 0, 0);	      
			      //row.setBackgroundColor(Color.argb(200, 51, 51, 51));
			      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
			    	  row.setOnClickListener(this);
			      table.addView(row);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	
	public TableRow createRow(String txt, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER_VERTICAL);
		  
		  TextView text = new TextView(activity);
		  text.setText(txt);
		  text.setPadding((int)(ratio*10), 0, 0, 0);
		  text.setTextColor(Color.parseColor("#545859"));
		  text.setTextSize(14);
		  text.setGravity(Gravity.CENTER_VERTICAL);
		  text.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				  RelativeLayout.LayoutParams.FILL_PARENT));
		  
		  RelativeLayout main = new RelativeLayout(row.getContext());
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				  RelativeLayout.LayoutParams.FILL_PARENT);
		  params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		  main.setLayoutParams(params);
		  
		  ImageView arrow = new ImageView(activity);
		  arrow.setImageResource(R.drawable.barrow);
		  arrow.setLayoutParams(params);
		  arrow.setPadding(0,0,(int)(ratio * 5),0);
		  
		  main.addView(arrow, params);
		  main.addView(text);
		  
		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 35)));
		  
		   
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
		
	public void resetButtons(){
		Button general = (Button) findViewById(R.id.generaleader);		
		general.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));	
		Button friends = (Button) findViewById(R.id.friendsleader);
		friends.setBackgroundDrawable(new BDrawableGradient(0,(int) (ratio*35),BDrawableGradient.LIGHT_GRAY_GRADIENT));		
	}
	
	// CALLED WHEN THE USER SELECT A CONTEST
	public void onClick(View v) {	
		selectedContest = contests.get(v.getId()).getCodeID();
    	UIhandler.sendEmptyMessage(1);    				
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
		TableLayout table = (TableLayout) findViewById(R.id.tablecontest);
		table.setColumnStretchable(0, false);
		table.removeAllViews();
		table.addView(row);
	}
	
	private void manageConnectionException (){
		UIhandler.sendEmptyMessage(3);		
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  if(msg.what == 0)
				  loadContestTable();
			  else if(msg.what == 1){
				  Leaderboard leaderboard = new Leaderboard(currentContext, kind, selectedContest);
				  if(Beintoo.dialogStack != null)
					  DialogStack.addToDialogStack(leaderboard);
				  leaderboard.show();
			  }else if(msg.what == 3){
				  TableLayout table = (TableLayout) findViewById(R.id.tablecontest);
				  table.removeAllViews();
				  ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , current.getContext(), null);
			  }
			  super.handleMessage(msg);
		  }
	};
	
}
