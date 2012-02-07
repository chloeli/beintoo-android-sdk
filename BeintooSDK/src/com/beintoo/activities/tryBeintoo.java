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
import com.beintoo.beintoosdkutility.BDrawableGradient;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.User;
import com.google.beintoogson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class tryBeintoo extends Dialog {
	private static final int GO_REG = 1;
	private static final int USER_SEL = 2;
	private boolean isLandscape = false;
	public tryBeintoo(Context ctx) {
		super(ctx, R.style.ThemeBeintooOn);		
		final Dialog current = this;
		
		// BUTTONS HEIGHT USED FOR GRADIENT COLOR
		int nothanksButtonHeight = 50;
		int trynowButtonHeight = 64;
		
		if(ctx.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.trybeintooland);	
			isLandscape = true;
			nothanksButtonHeight = 50;
        	trynowButtonHeight = 50;
        }else if(ctx.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
        	setContentView(R.layout.trybeintoo);
        }else if(ctx.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_SQUARE){
        	setContentView(R.layout.trybeintoo);
        }else if(ctx.getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_UNDEFINED) {
        	setContentView(R.layout.trybeintoo);
        }
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		Button trynowbt = (Button) findViewById(R.id.trybt);
		Button nothanksbt = (Button) findViewById(R.id.nothanksbt);
		
		// SETUP REWARD TEMPLATE
		if(Beintoo.TRY_BEINTOO_TEMPLATE == Beintoo.TRY_BEINTOO_REWARD){
			findViewById(R.id.default_template).setVisibility(View.GONE);
			findViewById(R.id.reward_template).setVisibility(View.VISIBLE);
			String sponsortext = ctx.getResources().getString(R.string.sponsor);
			if(Beintoo.virtualCurrencyData != null){
				sponsortext = ctx.getResources().getString(R.string.sponsorvirtualcurrency);
			}			
			TextView sponsor = (TextView) findViewById(R.id.textSponsor);
			sponsor.setText(Html.fromHtml(sponsortext));
			trynowbt = (Button) findViewById(R.id.trybtrew);
			nothanksbt = (Button) findViewById(R.id.nothanksbtrew);
			if(isLandscape){
				nothanksButtonHeight = 40;
				trynowButtonHeight = 40;
			}else{
				nothanksButtonHeight = 35;
			}
		}
	
		// GETTING DENSITY PIXELS RATIO
		double ratio = (ctx.getApplicationContext().getResources().getDisplayMetrics().densityDpi / 160d);		
				
		// SET UP LAYOUTS
		double pixels = ratio * 47;
		LinearLayout beintooBar = (LinearLayout) findViewById(R.id.beintoobar);
		beintooBar.setBackgroundDrawable(new BDrawableGradient(0,(int)pixels,BDrawableGradient.BAR_GRADIENT));
				
		pixels = ratio * trynowButtonHeight;
		
		BeButton b = new BeButton(ctx);		
		trynowbt.setShadowLayer(0.1f, 0, -2.0f, Color.BLACK);
		trynowbt.setBackgroundDrawable(b.setPressedBackg(new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_BUTTON_GRADIENT),
				new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT),
				new BDrawableGradient(0,(int) pixels,BDrawableGradient.BLU_ROLL_BUTTON_GRADIENT)));		
		trynowbt.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loadingBeintoo),true);
				dialog.setCancelable(true);
				final BeintooUser usr = new BeintooUser(); 
				
				Thread t = new Thread(new Runnable(){      
            		public void run(){
            			User[] arr = null;
            			try{             			
            				String deviceID = DeviceId.getUniqueDeviceId(getContext());            				
            				if(deviceID != null){ // CHECK IF WE ARE ON FROYO AND WITHOUT SIM (BUG)
	            				arr = usr.getUsersByDeviceUDID(deviceID);
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
            				}else {
            					UIhandler.sendEmptyMessage(GO_REG);            					
            				}
            			}catch(Exception e){ ErrorDisplayer.showConnectionErrorOnThread("Connection error.\nPlease check your Internet connection.", getContext(),null); }
            			dialog.dismiss();
            			current.dismiss();	 
            		}
        		});
        		t.start();
			}
        });
	    	    
	    pixels = ratio  * nothanksButtonHeight;
	    nothanksbt.setShadowLayer(0.1f, 0, -1.0f, Color.BLACK);
	    nothanksbt.setBackgroundDrawable(b.setPressedBackg(new BDrawableGradient(0,(int) pixels,BDrawableGradient.BAR_GRADIENT),
				new BDrawableGradient(0,(int) pixels,BDrawableGradient.GRAY_ROLL_BUTTON_GRADIENT),
				new BDrawableGradient(0,(int) pixels,BDrawableGradient.GRAY_ROLL_BUTTON_GRADIENT)));		
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
	            	UserRegistration userReg = new UserRegistration(getContext());
	            	Beintoo.currentDialog = userReg;
	            	userReg.show();
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
}
