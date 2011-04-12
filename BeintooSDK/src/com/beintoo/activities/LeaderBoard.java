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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.ApiCallException;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
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
	final double ratio;
	final int SHOW_MESSAGE = 1;
	final int LOAD_CONTEST = 2;
	public LeaderBoard(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);
		setContentView(R.layout.leaderboard);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		current = this;
		currentContext = ctx;
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);	
		
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
		
		LinearLayout tip = (LinearLayout) findViewById(R.id.tip);
		tip.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio*27),BDrawableGradient.LIGHT_GRAY_GRADIENT));
		
		usersExts = new ArrayList<String>();
		usersNicks = new ArrayList<String>();
		try{
			showLoading();
			startLoading();
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx,e);}
		
	}
	
	public void startLoading (){
		new Thread(new Runnable(){      
    		public void run(){
    			try{   
    				Thread.sleep(100); // GIVE THE TIME TO SHOW THE DIALOG
    				UIhandler.sendEmptyMessage(LOAD_CONTEST); 
    			}catch(Exception e){e.printStackTrace();}
    		}
		}).start();	
	}
	
	public void loadLeadersByContestTable(){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);				
		
		Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", getContext()), Player.class);
		
		Iterator<?> it = deserializeLeaderboard().entrySet().iterator();		
		int selectedContest = Integer.parseInt(PreferencesHandler.getString("selectedContest", currentContext));
		// SET TITLE
		TextView contestName = (TextView)findViewById(R.id.dialogTitle);

		int count = 0;
		final ArrayList<View> rowList = new ArrayList<View>();
		
	    while (it.hasNext()) {	    	
	    	@SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<EntryCouplePlayer>> pairs = (Map.Entry<String, ArrayList<EntryCouplePlayer>>) it.next();
	    	
	    	List<EntryCouplePlayer> arr = pairs.getValue();
	    	if(selectedContest == count){
	    		contestName.setText(arr.get(0).getEntry().getPlayerScore().get(pairs.getKey()).getContest().getName());
	    		codeID = pairs.getKey();
		    	for(int i = 0; i<arr.size(); i++){
		    		//final LoaderImageView image = new LoaderImageView(getContext(), arr.get(i).getObj().getUser().getUsersmallimg());
		    		final LoaderImageView image = new LoaderImageView(getContext(),arr.get(i).getObj().getUser().getUserimg(),(int)(ratio*70),(int)(ratio*70));
		    			
		    		TableRow row = createRow(image, arr.get(i).getObj().getUser().getNickname(),
		    				arr.get(i).getVal().toString(), getContext(), i);
					row.setId(i);
					rowList.add(row);
					
					if(!p.getUser().getId().equals(arr.get(i).getObj().getUser().getId()))			    		
		    			row.setOnClickListener(this);
					
					View spacer = createSpacer(getContext(),1,1);
					spacer.setId(-100);
					rowList.add(spacer);
					View spacer2 = createSpacer(getContext(),2,1);
					spacer2.setId(-100);
					rowList.add(spacer2);
					
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
					
					// ADD THE USER EXT TO THE USERS ARRAY
					usersExts.add(arr.get(i).getObj().getUser().getId());
					usersNicks.add(arr.get(i).getObj().getUser().getNickname());
		    	}	 
	    	}
	    	count++;
	    }
	    
	    table.removeAllViews();
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
	
	public TableRow createRow(View image, String nick,String score, Context activity, int pposition) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  		  
		  // PICTURE
		  image.setPadding((int)(ratio*10), 0, 10, 0);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  LinearLayout foto = new LinearLayout(row.getContext());
		  foto.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  foto.setOrientation(LinearLayout.HORIZONTAL);
		  foto.setGravity(Gravity.CENTER);
		  foto.addView(image);
		  
		  row.addView(foto,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio*90))); //TableRow.LayoutParams.WRAP_CONTENT
		 
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  
		  TextView nickname = new TextView(activity);
		  nickname.setText(nick);
		  nickname.setPadding(0, 0, 0, 0);
		  nickname.setTextColor(Color.parseColor("#545859"));
		  nickname.setMaxLines(2);
		  nickname.setTextSize(16);
		  nickname.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  		    
		  TextView scoreView = new TextView(activity);		
		  scoreView.setText(activity.getString(R.string.leadScore)+score);	
		  scoreView.setPadding(0, 0, 0, 0);
		  scoreView.setTextColor(Color.parseColor("#787A77"));
		  scoreView.setTextSize(14);
		  scoreView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));		  
		  
		  main.addView(nickname);
		  main.addView(scoreView);
		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
		  
		  // USER POSITION
		  LinearLayout position = new LinearLayout(row.getContext());
		  position.setGravity(Gravity.CENTER);
		  position.setBackgroundColor(Color.argb(150, 255, 255, 255));
		  
		  TextView positionNumber = new TextView(activity);
		  positionNumber.setText(String.format("%02d", pposition+1));
		  positionNumber.setPadding(0, 0, 0, 0);
		  positionNumber.setTextColor(Color.LTGRAY);
		  positionNumber.setTextSize(16);
		  
		  position.addView(positionNumber);
		  TableRow.LayoutParams params = new TableRow.LayoutParams((int)(ratio*35),
				  (int)(ratio*35));
		  params.setMargins(0, 0, 18, 0);
		  row.addView(position,params);
		  
		  
		  return row;
	}
	
	private View createSpacer(Context activity, int color, int height) {
		  View spacer = new View(activity);
		  spacer.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,height));
		  if(color == 1)
			  spacer.setBackgroundColor(Color.parseColor("#8F9193"));
		  else if(color == 2)
			  spacer.setBackgroundColor(Color.WHITE);

		  return spacer;
	}	
	
	public void sendChallenge  (final String userExtFrom, final String userExtTo, final String action, final int id){		
		new Thread(new Runnable(){      
    		public void run(){
    			try{             				
    				BeintooUser user = new BeintooUser();
    				user.challenge(userExtFrom, userExtTo, action, codeID);
    				// SHOW THE NOTIFICATION
    				Bundle b = new Bundle();
    				b.putString("msg",getContext().getString(R.string.challSent)+usersNicks.get(id));
    				Message msg = new Message();
    				msg.what = SHOW_MESSAGE;
    				msg.setData(b);
    				UIhandler.sendMessage(msg); 
    			}catch (ApiCallException e){
    				// SHOW THE NOTIFICATION
    				Bundle b = new Bundle();
    				b.putString("msg",e.getMessage());
    				Message msg = new Message();
    				msg.what = SHOW_MESSAGE;
    				msg.setData(b);
    				UIhandler.sendMessage(msg); 
    			}catch (Exception e){
    				e.printStackTrace();
    			}
    			
    		}
		}).start();		
	}
	
	// CALLED WHEN A ROW IS CLICKED
	public void onClick(final View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(getContext().getString(R.string.challSend))
		       .setCancelable(false)
		       .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {		 
		        	   Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", v.getContext()), Player.class);		       			
		       			sendChallenge(p.getUser().getId(),
		       				usersExts.get(v.getId()),"INVITE", v.getId());  
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
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
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  switch (msg.what) {				  
	            case SHOW_MESSAGE: // GO HOME ON START BEINTOO (GO_HOME)
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("msg"), Gravity.BOTTOM);
	            break;
	            case LOAD_CONTEST:
	            	loadLeadersByContestTable();
	            break;
		    }
	        super.handleMessage(msg);
		  }
	};
}
