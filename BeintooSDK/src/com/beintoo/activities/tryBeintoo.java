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

import com.beintoo.R;

import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.User;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class tryBeintoo extends Dialog {
	private static final int GO_REG = 1;
	private static final int USER_SEL = 2;
	public tryBeintoo(Context ctx) {
		super(ctx, R.style.ThemeBeintooOn);
		setContentView(R.layout.trybeintoo);
		final Dialog current = this;
		
		BeButton b = new BeButton(ctx);
		Button bt = (Button) findViewById(R.id.trybt);
		bt.setBackgroundDrawable(b.setPressedBg(R.drawable.facebook, R.drawable.facebook_h, R.drawable.facebook_h));
		bt.setBackgroundDrawable(b.setPressedBg(R.drawable.trybutton, R.drawable.trybutton_h, R.drawable.trybutton_h));
	    bt.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", "Loading Beintoo...",true);
				final BeintooUser usr = new BeintooUser(); 
				
				Thread t = new Thread(new Runnable(){      
            		public void run(){
            			User[] arr = null;
            			try{             				
            				arr = usr.getUsersByDeviceUDID(DeviceId.getUniqueDeviceId(getContext()));
            				if(arr.length == 0) { // Loading the registration form 
            					Message msg = new Message();
            			        msg.what = GO_REG;
            			        UIhandler.sendMessage(msg);
            				}else if(arr.length > 0){ // check the existing users
            					Gson gson = new Gson();
            					String jsonUsers = gson.toJson(arr);
            					// SAVE THE CURRENT USERS BY DEVICE UDID
            					PreferencesHandler.saveString("deviceUsersList", jsonUsers, getContext());
            					
            					Message msg = new Message();
            			        msg.what = 2;
            			        UIhandler.sendMessage(msg);            					
            				} 
            			}catch(Exception e){}
            			dialog.dismiss();
            			current.dismiss();
            			// CHECK IF THE USER WAS CONNECTED (arr != null)
            			if(!checkConnection(arr)) ErrorDisplayer.showConnectionErrorOnThread("Connection error.\nPlease check your Internet connection.", getContext());
            		}
        		});
        		t.start();
			}
        });
	    
	    
	    Button nothanksbt = (Button) findViewById(R.id.nothanksbt);
	    nothanksbt.setBackgroundDrawable(b.setPressedBg(R.drawable.nothanksbt, R.drawable.nothanksbt_h, R.drawable.nothanksbt_h));
	    nothanksbt.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {  
				current.dismiss();
			}
	    });	
	    
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  switch (msg.what) {
	            case GO_REG:
	            	UserLogin userLogin = new UserLogin(getContext());
	            	Beintoo.currentDialog = userLogin;
	            	userLogin.show();
	            break;
	            case USER_SEL:
					UserSelection userSelection = new UserSelection(getContext());      
					Beintoo.currentDialog = userSelection;
					userSelection.show();
	            break;
	            	
		    }
	        super.handleMessage(msg);
		  }
	};
	
	// IF THE USERS ARRAY IS NULL WE HAD CONNECTION PROBLEMS 
	public boolean checkConnection (User[] arr){
		if(arr != null)
			return true;
		else{ 
			return false;
		}
	}
}
