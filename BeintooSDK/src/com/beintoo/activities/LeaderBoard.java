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
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.EntryCouplePlayer;
import com.beintoo.wrappers.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LeaderBoard extends Dialog implements OnClickListener{
	Dialog current;
	Context currentContext;
	ArrayList<String> usersExts;
	ArrayList<String> usersNicks;
	String codeID; // THE CODE ID OF THE SELECTED CONTEST
	public LeaderBoard(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);
		setContentView(R.layout.leaderboard);
		current = this;
		currentContext = ctx;
		usersExts = new ArrayList<String>();
		usersNicks = new ArrayList<String>();
		
		loadLeadersByContestTable(); 
			
		Button close = (Button) findViewById(R.id.close);
		BeButton b = new BeButton(ctx);
		close.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));
	    close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {												
				current.dismiss();
			}	
        });
	}
	
	public void loadLeadersByContestTable(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		table.removeAllViews();		
		
		Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);
		
		Iterator<?> it = deserializeLeaderboard().entrySet().iterator();		
		int selectedContest = Integer.parseInt(PreferencesHandler.getString("selectedContest", currentContext));
		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
	    while (it.hasNext()) {
	    	@SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<EntryCouplePlayer>> pairs = (Map.Entry<String, ArrayList<EntryCouplePlayer>>) it.next();
	    	
	    	List<EntryCouplePlayer> arr = pairs.getValue();
	    	if(selectedContest == count){
	    		codeID = pairs.getKey();
		    	for(int i = 0; i<arr.size(); i++){
		    		final LoaderImageView image = new LoaderImageView(getContext(), arr.get(i).getObj().getUser().getUsersmallimg());
		    		
		    		Button bt = new Button(getContext());
		    		bt.setId(i);
			  		bt.setOnClickListener(this);
			  		bt.setText("Challenge");
		    		if(p.getUser().getId().equals(arr.get(i).getObj().getUser().getId()))			    		
		    			bt.setVisibility(View.INVISIBLE);
		    		
			    		
		    		TableRow row = createRow(image, arr.get(i).getObj().getUser().getNickname(),
		    				arr.get(i).getVal().toString(), bt, getContext());
					row.setId(count);
					rowList.add(row);
					
					View spacer = createSpacer(getContext(),0,2);
					spacer.setId(-100);
					rowList.add(spacer);
					if(i%2 == 0) row.setBackgroundColor(Color.parseColor("#d8eaef"));
					
					// ADD THE USER EXT TO THE USERS ARRAY
					usersExts.add(arr.get(i).getObj().getUser().getId());
					usersNicks.add(arr.get(i).getObj().getUser().getNickname());
					
		    		System.out.println( arr.get(i).getObj().getUser().getNickname()+ " " + arr.get(i).getVal());
		    	}	 
	    	}
	    	count++;
	    }
	    
	    for (View row : rowList) {	      
		      table.addView(row);
		}
	}
	
	public Map<String, List<EntryCouplePlayer>> deserializeLeaderboard (){
		Type mapType = new TypeToken<Map<String, ArrayList<EntryCouplePlayer>>>() {}.getType();
		String jsonLeaderboard = PreferencesHandler.getString("leaderboard",currentContext);
		Map<String, List<EntryCouplePlayer>> Leaderboard = new Gson().fromJson(jsonLeaderboard, mapType);
		
		return Leaderboard;
	}
	
	public static TableRow createRow(View image, String nick,String score, Button challenge, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding(10, 10, 10, 10);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  row.addView(image);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  		 
		  TextView nickname = new TextView(activity);		  
		  nickname.setText(nick);
		  nickname.setPadding(0, 10, 10, 10);
		  nickname.setTextColor(Color.parseColor("#83be56"));

		  nickname.setTypeface(null,Typeface.BOLD);
		  nickname.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  TextView scoreView = new TextView(activity);		
		  scoreView.setText("Score: "+score);	
		  scoreView.setPadding(0, 10, 10, 10);
		  scoreView.setTextColor(Color.parseColor("#787A77"));
		  scoreView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  	
		  main.addView(nickname);
		  main.addView(scoreView);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		  
		  
		  row.addView(challenge);
		  
		  return row;
	}
	
	private static View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);

		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color != 0)
			  spacer.setBackgroundColor(Color.LTGRAY);

		  return spacer;
	}	
	
	public void sendChallenge  (final String userExtFrom, final String userExtTo, final String action){
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
	
	// CALLED ON CHALLENGE BUTTON CLICK
	public void onClick(View v) {
		Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", v.getContext()), Player.class);
		MessageDisplayer.showMessage(currentContext, "Challenge sent to "+usersNicks.get(v.getId()));
		sendChallenge(p.getUser().getId(),
				usersExts.get(v.getId()),"INVITE");  
	}
}
