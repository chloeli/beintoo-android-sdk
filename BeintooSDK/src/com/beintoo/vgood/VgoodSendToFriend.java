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
package com.beintoo.vgood;

import java.util.ArrayList;


import com.beintoo.R;

import com.beintoo.activities.Wallet;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkui.BeintooBrowser;
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.main.managers.LocationMManager;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.beintoo.wrappers.Vgood;

import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class VgoodSendToFriend extends Dialog implements OnClickListener{
	public static final int OPEN_FRIENDS_FROM_VGOOD = 1;
	public static final int OPEN_FRIENDS_FROM_WALLET = 2;
	public static final int OPEN_FRIENDS_FROM_GPS = 2;
	public static final int OPEN_FRIENDS_FROM_MARKETPLACE = 3;
	
	public Dialog previous;
	public Dialog backPrevious;
	public String vgoodID;
	public View rowToRemove;
	
	private Dialog current;
	private ArrayList<String> usersExts;
	private ArrayList<String> usersNicks;	
	private User [] friends;
	private double ratio;
	private int calledFrom;
	private Vgood vgood;
	
	public VgoodSendToFriend(Context context, int calledFrom, User[] userData, Vgood vgood){
		super(context, R.style.ThemeBeintoo);
		initializeView(context, calledFrom, userData, vgood);		
	}
	
	public VgoodSendToFriend(Context context, int calledFrom, User[] userData) {
		super(context);
		initializeView(context, calledFrom, userData, null);	
	}
	
	private void initializeView(Context context, int calledFrom, User[] u, Vgood vgood){		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friendlist);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.current = this;		
		this.friends = u;
		this.vgood = vgood;
		this.calledFrom = calledFrom;
		
		// GETTING DENSITY PIXELS RATIO
		ratio = (context.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);						
		// SET UP LAYOUTS
		double pixels = ratio * 40;
		RelativeLayout beintooBar = (RelativeLayout) findViewById(R.id.beintoobarsmall);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.LIGHT_GRAY_GRADIENT));
		
		usersExts = new ArrayList<String>();
		usersNicks = new ArrayList<String>();
		
		try {
			if(friends.length > 0)
				loadFriendsTable(calledFrom);
			else { // NO VGOODS SHOW A MESSAGE
				TextView noGoods = new TextView(getContext());
				noGoods.setText(getContext().getString(R.string.friendsNo));
				noGoods.setTextColor(Color.GRAY);
				noGoods.setPadding(20,0,0,0);						
				TableLayout table = (TableLayout) findViewById(R.id.table);
				table.addView(noGoods);
			}
			
		}catch (Exception e){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , context,e);}
	}
	
	public void loadFriendsTable(int calledFrom){
		TableLayout table = (TableLayout) findViewById(R.id.table);
		//table.setColumnStretchable(1, true);
		table.removeAllViews();		
				
		final ArrayList<View> rowList = new ArrayList<View>();
		
    	for(int i = 0; i<friends.length; i++){    		
    		//final LoaderImageView image = new LoaderImageView(getContext(), getUsersmallimg());
    		final LoaderImageView image = new LoaderImageView(getContext(),friends[i].getUserimg(),(int)(ratio * 70),(int)(ratio *70));
       		
    		TableRow row = createRow(image, friends[i].getNickname(),
    				friends[i].getName(), getContext());
			row.setId(i);
			rowList.add(row);
			
			// IF WE CALLED THE DIALOG FROM SEND AS A GIFT WE NEED THE
			// ROW CLICKABLE TO SEND THE VGOOD AS A GIFT
			if(calledFrom == OPEN_FRIENDS_FROM_VGOOD || calledFrom == OPEN_FRIENDS_FROM_WALLET || calledFrom == OPEN_FRIENDS_FROM_MARKETPLACE)
				row.setOnClickListener(this);
			
			View spacer = createSpacer(getContext(),1,1);
			spacer.setId(-100);
			rowList.add(spacer);
			View spacer2 = createSpacer(getContext(),2,1);
			spacer2.setId(-100);
			rowList.add(spacer2);
			
			row.setBackgroundDrawable(new BDrawableGradient(0,(int)(ratio * 90),BDrawableGradient.LIGHT_GRAY_GRADIENT));
	
			
			// ADD THE USER EXT TO THE USERS ARRAY
			usersExts.add(friends[i].getId());
			usersNicks.add(friends[i].getNickname());
    	}	 
	    
	    for (View row : rowList) {	      
		      table.addView(row);
		}
	}
	
	public TableRow createRow(View image, String nick,String name, Context activity) {
		  TableRow row = new TableRow(activity);
		  row.setGravity(Gravity.CENTER);
		  
		  image.setPadding(15, 4, 10, 4);
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  LinearLayout foto = new LinearLayout(row.getContext());
		  foto.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  foto.setOrientation(LinearLayout.VERTICAL);
		  
		  foto.addView(image);
		  row.addView(foto,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT)); //
		 
		  
		 // row.addView(image);
		  
		  LinearLayout main = new LinearLayout(row.getContext());
		  main.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  main.setOrientation(LinearLayout.VERTICAL);
		  main.setGravity(Gravity.CENTER_VERTICAL);
		  
		  TextView nickname = new TextView(activity);		  
		  nickname.setText(nick);
		  nickname.setPadding(5, 0, 0, 0);
		  nickname.setTextColor(Color.parseColor("#545859"));
		  nickname.setMaxLines(1);
		  nickname.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  
		  TextView scoreView = new TextView(activity);		
		  scoreView.setText(name);	
		  scoreView.setPadding(5, 0, 0, 0);
		  scoreView.setTextColor(Color.parseColor("#787A77"));
		  scoreView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		  	
		  main.addView(nickname);
		  main.addView(scoreView);		  
		  row.addView(main,new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,(int)(ratio * 90))); //
		 // row.addView(img);
		  
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
	
	// CALLED WHEN THE USER SELECT A FRIEND TO SEND A GIFT
	public void onClick(View v) {
		  confirmDialog(v.getId());
	}
	
	private void confirmDialog (final int selected) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage(getContext().getString(R.string.friendSure)+usersNicks.get(selected)+"?")
		       .setCancelable(false)
		       .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   current.dismiss();		
		        	   if(backPrevious != null)
		        		   backPrevious.dismiss();
		        	    
		        	   	if(calledFrom == OPEN_FRIENDS_FROM_WALLET){		        		   
			        	   try {
			        		   ((Wallet)previous).removeSentAsAGiftItem();
			        	   }catch(Exception e){
			        		   e.printStackTrace();
			        	   }
		        	   	}else if(calledFrom == OPEN_FRIENDS_FROM_MARKETPLACE){
		        	   		handleOpenSendAsAGiftInBrowser(usersExts.get(selected));
		        	   		if(previous != null)
		        	   			previous.dismiss();
		        	   		return;
		           		}else{
		        		   previous.dismiss();
		           		}
		        	   	
		        	   new Thread(new Runnable(){      
				    	public void run(){
			    			try{     
			    				Player p = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer",getContext()));
			    				String userFrom = p.getUser().getId();			    				
			    				String userTo = usersExts.get(selected);			    				
			    				BeintooVgood sendVgood = new BeintooVgood();
			    				com.beintoo.wrappers.Message msg = sendVgood.sendAsAGift(vgoodID, userFrom, userTo, null);
			    				if(!msg.getKind().equals("error")){			    								    				
			    					UIhandler.sendEmptyMessage(selected);
			    				}
			    			}catch (Exception e){
			    				e.printStackTrace();
			    			}
				    	}
					   }).start();		
		           }
		       })
		       .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void doneDialog (int i) {
		MessageDisplayer.showMessage(getContext(), getContext().getString(R.string.friendSent)+usersNicks.get(i),Gravity.BOTTOM);
	}
	
	public void handleOpenSendAsAGiftInBrowser(String friendExtId){
		if(vgood == null) return;
		current.dismiss();
		Uri.Builder url = Uri.parse(vgood.getGetRealURL()).buildUpon();
		Player p = Current.getCurrentPlayer(getContext());
		if(p!=null) url.appendQueryParameter("guid", p.getGuid());
		
		if(Beintoo.virtualCurrencyData != null)
			url.appendQueryParameter("developer_user_guid", Beintoo.getVirtualCurrencyDevUserId());		
		if(friendExtId != null)
			url.appendQueryParameter("friend_user_ext", friendExtId);
		
		if(!vgood.isOpenInBrowser()){
			BeintooBrowser bb = new BeintooBrowser(current.getContext(), url.toString());
			bb.show();
		}else{
			Location loc = LocationMManager.getSavedPlayerLocation(getContext());
			if(loc != null){
				Double latitude = loc.getLatitude();
				Double longitude = loc.getLongitude();
				Float accuracy = loc.getAccuracy();
				url.appendQueryParameter("lat", latitude.toString());
				url.appendQueryParameter("lon", longitude.toString());
				url.appendQueryParameter("acc", accuracy.toString());
			}
						
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url.toString()));
			current.getContext().startActivity(browserIntent);
		}		
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {			  
			  doneDialog(msg.what);			  
			  super.handleMessage(msg);
		  }
	};
	
}
