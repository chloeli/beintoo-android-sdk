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
import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.LoaderImageView;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.google.gson.Gson;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class UserSelection extends Dialog implements OnClickListener{
	User[] users;
	final Dialog current;
	public UserSelection(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.userselection);
		current = this;
		
		TableLayout table = (TableLayout) findViewById(R.id.usersTableLayout);
		  //LayoutUtils.Layout.WidthFill_HeightFill.applyViewGroupParams(table);
	
		  // set which column is expandable/can grow
		table.setColumnStretchable(1, true);
	
		  // apply layout animation
		 // AnimUtils.setLayoutAnim_slideupfrombottom(table, this);
	
		final ArrayList<View> rowList = new ArrayList<View>();
		//rowList.add();
		
		String currentUsersList = PreferencesHandler.getString("deviceUsersList", getContext());
		Gson gson = new Gson();
		users = gson.fromJson(currentUsersList, User[].class);
		
		for(int i = 0; i< users.length; i++){
			if(users[i] != null){
				//final LoaderImageView image = new LoaderImageView(getContext(), users[i].getUsersmallimg());
				final LoaderImageView image = new LoaderImageView(getContext(), users[i].getUserimg(),70,70);
				TableRow row = createRow(image, users[i].getNickname(), getContext());
				row.setId(i);
				rowList.add(row);
				View spacer = createSpacer(getContext(),0,5);
				spacer.setId(-100);
				rowList.add(spacer);				
			}
		}
		
		table.addView(createSpacer(getContext(),0,10));
		
		for (View row : rowList) {
	      row.setPadding(0, 0, 0, 0);	      
	      //row.setBackgroundColor(Color.argb(200, 51, 51, 51));
	      if(row.getId() != -100) // IF IS NOT A SPACER IT'S CLICKABLE
	    	  row.setOnClickListener(this);
	      table.addView(row);
	    }
		
		table.addView(createSpacer(getContext(),0,10));
		
		// ADD THE NEW PLAYER BUTTON
	    Button newplayer = (Button) findViewById(R.id.anotheracc);
	    BeButton b = new BeButton(ctx);
	    newplayer.setBackgroundDrawable(b.setPressedBg(R.drawable.useanother, R.drawable.useanother_h, R.drawable.useanother_h));		
	    /*newplayer.setBackgroundResource(R.drawable.useanother);
	    newplayer.setLayoutParams(new LinearLayout.LayoutParams(
		          LinearLayout.LayoutParams.WRAP_CONTENT,
		          LinearLayout.LayoutParams.WRAP_CONTENT
		));
	    newplayer.setGravity(Gravity.CENTER);
	   // newplayer.setPadding(0, 0, 0, 0);

	    TableRow.LayoutParams params = new TableRow.LayoutParams();  
	    params.span = 2; 	 */   
	    //newplayer.setLayoutParams(params);	    
	    newplayer.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				UserLogin userLogin = new UserLogin(getContext());
				userLogin.show();	
				current.dismiss();
				//finish();
			}
        });
	    
	    Button close = (Button) findViewById(R.id.close);
	    close.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				current.dismiss();

			}
        });
	    /*TableRow row = new TableRow(getContext());
	    row.addView(newplayer);	 //, params
	    row.setBackgroundColor(Color.WHITE);
	    row.setGravity(Gravity.CENTER);
	    table.addView(row);*/
		    
	}
    
	public static TableRow createRow(View image, String txt, Context activity) {
		  TableRow row = new TableRow(activity);
		
		 /* ImageView icon = (ImageView) image;
		  icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
		  icon.setMaxHeight(30);
		  icon.setMaxWidth(30);
		 // icon.setImageResource(image);
		  icon.setPadding(0, 0, 0, 0);
		  
		  */
		  TextView text = new TextView(activity);
		  text.setText(txt);
		  text.setPadding(10, 0, 0, 0);
		  text.setTextColor(Color.parseColor("#83be56"));
		  text.setBackgroundColor(Color.WHITE);
		  text.setTypeface(null,Typeface.BOLD);
		  
		  image.setPadding(8, 0, 0, 0);
		  image.setBackgroundColor(Color.WHITE);
		  		  
		  ((LinearLayout) image).setGravity(Gravity.LEFT);
		  
		  row.addView(image);
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
		final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", "Login...",true);
		new Thread(new Runnable(){      
    		public void run(){
    			try{   
					Gson gson = new Gson();
					
					String userExt = users[selectedRow].getId();
					BeintooPlayer player = new BeintooPlayer();
					Player newPlayer = player.playerLogin(userExt,null,null,DeviceId.getUniqueDeviceId(getContext()),null, null);
					String jsonPlayer = gson.toJson(newPlayer);
					
					// SAVE THE CURRENT PLAYER
					PreferencesHandler.saveString("currentPlayer", jsonPlayer, getContext());
					// SET THAT THE PLAYER IS LOGGED IN BEINTOO
					PreferencesHandler.saveBool("isLogged", true, getContext());
					
					UIhandler.sendEmptyMessage(1);
					
    			}catch(Exception e){}	
        			dialog.dismiss();
        		}
			}).start();		
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			BeintooHome beintooHome = new BeintooHome(getContext());
			beintooHome.show();					          	
			current.dismiss();
	        super.handleMessage(msg);
			  //do something in the user interface to display data from message
		  }
	};
}
