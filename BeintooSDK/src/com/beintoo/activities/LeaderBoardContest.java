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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooApp;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.EntryCouplePlayer;
import com.beintoo.wrappers.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LeaderBoardContest extends Dialog implements OnClickListener{
	Dialog current;
	Context currentContext;
	
	public LeaderBoardContest(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);
		setContentView(R.layout.contestselection);
		current = this;
		currentContext = ctx;
		try {
			loadContestTable();
		}catch(Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx);}	
	 
		BeButton b = new BeButton(ctx);
		Button general = (Button) findViewById(R.id.generaleader);				
		general.setBackgroundDrawable(b.setPressedBg(R.drawable.all, R.drawable.all_h, R.drawable.all_h));
		general.setOnClickListener(new Button.OnClickListener(){
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
							UIhandler.sendEmptyMessage(0);
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            			dialog.dismiss();
            		}
				}).start();
			}
        });
		
		Button friends = (Button) findViewById(R.id.friendsleader);
		friends.setBackgroundDrawable(b.setPressedBg(R.drawable.friends, R.drawable.friends_h, R.drawable.friends_h));
		friends.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 
							BeintooApp app = new BeintooApp();				
							Player player = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", currentContext));
							Map<String, List<EntryCouplePlayer>> leader = app.TopScoreByUserExt(null, 0, player.getUser().getId());
							Gson gson = new Gson();
							String jsonLeaderboard = gson.toJson(leader);							
							PreferencesHandler.saveString("leaderboard", jsonLeaderboard, getContext());
							UIhandler.sendEmptyMessage(0); 
            			}catch (Exception e){
            				e.printStackTrace();
            			}
            			dialog.dismiss();
            		}
				}).start();
			}
        });
		
		Button close = (Button) findViewById(R.id.close);
		close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {												
				current.dismiss();
			}	
        });
	}
	
	public void loadContestTable(){
		
		TableLayout table = (TableLayout) findViewById(R.id.tablecontest);
		table.setColumnStretchable(1, true);
		table.removeAllViews();
		
		final ArrayList<View> rowList = new ArrayList<View>();
		Iterator<?> it = deserializeLeaderboard().entrySet().iterator();
		int count = 0;
	    while (it.hasNext()) {
	    	@SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<EntryCouplePlayer>> pairs = (Map.Entry<String, ArrayList<EntryCouplePlayer>>) it.next();
	    	List<EntryCouplePlayer> arr = pairs.getValue();	
	    	
	    	if(arr.get(0).getEntry().getPlayerScore().get(pairs.getKey()).getContest().isPublic() == true){
		    	TableRow row = createRow(arr.get(0).getEntry().getPlayerScore().get(pairs.getKey()).getContest().getName(), getContext());
		    	//TableRow row = createRow(pairs.getKey(),getContext());
		    	if(count % 2 == 0)
		    		row.setBackgroundColor(Color.parseColor("#d8eaef"));
		    	else
		    		row.setBackgroundColor(Color.parseColor("#ecf3f5"));
		    	
		    	TableLayout.LayoutParams tableRowParams=
	    		  new TableLayout.LayoutParams
	    		  (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
	    		tableRowParams.setMargins(10, 0, 10, 0);
	    		row.setLayoutParams(tableRowParams);
		    	row.setId(count);
				rowList.add(row);
				View spacer = createSpacer(getContext(),0,5);
				spacer.setId(-100);
				rowList.add(spacer);
		    	count++;	
	    	}
	    }
	    
	    for (View row : rowList) {
		      row.setPadding(0, 0, 0, 0);	      
		      //row.setBackgroundColor(Color.argb(200, 51, 51, 51));
		      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
		    	  row.setOnClickListener(this);
		      table.addView(row);
		}
	}
	
	
	
	public Map<String, List<EntryCouplePlayer>> deserializeLeaderboard (){
		Type mapType = new TypeToken<Map<String, ArrayList<EntryCouplePlayer>>>() {}.getType();
		String jsonLeaderboard = PreferencesHandler.getString("leaderboard",currentContext);
		Map<String, List<EntryCouplePlayer>> Leaderboard = new Gson().fromJson(jsonLeaderboard, mapType);
		
		return Leaderboard;
	}
	
	
	public static TableRow createRow(String txt, Context activity) {
		  TableRow row = new TableRow(activity);
		
		  TextView text = new TextView(activity);
		  text.setText(txt);
		  text.setPadding(10, 10, 10, 10);
		  text.setTextColor(Color.parseColor("#545859"));
		  text.setTypeface(null,Typeface.BOLD);
		  
		  row.addView(text);
		   
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
	
	
	// CALLED WHEN THE USER SELECT A USER IN THE TABLE
	public void onClick(View v) {
		final int selectedRow = v.getId();
		final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading),true);
		new Thread(new Runnable(){      
    		public void run(){
    			try{     				
    				PreferencesHandler.saveString("selectedContest", ""+selectedRow, currentContext);
    				UIhandler.sendEmptyMessage(1);
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    			dialog.dismiss();
    		}
		}).start();		
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  if(msg.what == 0)
				  loadContestTable();
			  else if(msg.what == 1){
				  LeaderBoard leaderboard = new LeaderBoard(currentContext);
				  leaderboard.show();
			  }
			  super.handleMessage(msg);
		  }
	};
	
}
