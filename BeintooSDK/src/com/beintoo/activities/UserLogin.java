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
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkui.BeButton;
import com.beintoo.beintoosdkui.BeintooFacebookLogin;
import com.beintoo.beintoosdkui.BeintooSignupBrowser;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class UserLogin extends Dialog{

	final Dialog current;
	private static final int GO_HOME = 1;
	private static final int SIGNUP_BROWSER = 2;
	
	public UserLogin(Context ctx) {
		super(ctx, R.style.ThemeBeintoo);		
		setContentView(R.layout.login);
		current = this;
		
		// USED TO DISMISS KEYBOARD
		final InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		
		/*
		 *  BUTTON CLICKED ON NEW USER
		 *  PLAYERLOGIN WITH NO GUID THE GUID WILL BE ASSIGNED FROM THE API
		 *  THEN CONNECT.HTML WITH THE ASSIGNE GUID
		 */
		Button newUser = (Button) findViewById(R.id.newuser);
		BeButton b = new BeButton(ctx);
		newUser.setBackgroundDrawable(b.setPressedBg(R.drawable.newuser, R.drawable.newuser_h, R.drawable.newuser_h));
		newUser.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", "Loading...",true);
				new Thread(new Runnable(){      
            		public void run(){
            			try{ 				
            				Gson gson = new Gson();
							BeintooPlayer player = new BeintooPlayer();							 
							// WE NEED A LOGIN FIRST WITH THE NEW GUID BEFORE CONNECT.HTML
							String currentPlayer = PreferencesHandler.getString("currentPlayer", getContext());
		    				Player loggedUser = gson.fromJson(currentPlayer, Player.class);
		    				Player newPlayer;
		    				
		    				DebugUtility.showLog("Logged player before new "+currentPlayer);
		    				
		    				if(loggedUser == null || loggedUser.getUser() != null) // GET A NEW RANDOM PLAYER
		    					newPlayer = player.playerLogin(null,null,DeviceId.getUniqueDeviceId(getContext()),null, null);
		    				else // USE THE CURRENT SAVE PLAYER 
		    					newPlayer = player.playerLogin(null,loggedUser.getGuid(),DeviceId.getUniqueDeviceId(getContext()),null, null, null);
		    				
							if(newPlayer.getGuid() != null){
								String signupUrl = "http://www.beintoo.com/connect.html?" +
								"apikey="+DeveloperConfiguration.apiKey+"&guid="+newPlayer.getGuid()+"" +
										"&display=touch&" +
								"redirect_uri=http://beintoostatic.s3.amazonaws.com/sdk/register_ok.html&logged_uri=http://beintoostatic.s3.amazonaws.com/sdk/already_logged.html";
								
								// DEBUG
								DebugUtility.showLog(signupUrl);
								
								PreferencesHandler.saveString("openUrl", signupUrl, getContext());				
								PreferencesHandler.saveString("guid", newPlayer.getGuid(), getContext());
								UIhandler.sendEmptyMessage(SIGNUP_BROWSER);
							}else{ // SHOW NETWORK ERROR
								ErrorDisplayer.showConnectionError("Connection error.\nPlease check your Internet connection.", getContext());
							}
            			}catch(Exception e){
            			}	
            			dialog.dismiss();
            		} 
				}).start();		
			}
        });
		
		Button go = (Button) findViewById(R.id.go);
		go.setBackgroundDrawable(b.setPressedBg(R.drawable.login, R.drawable.login_h, R.drawable.login_h));
		go.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {				
				final ProgressDialog  dialog = ProgressDialog.show(getContext(), "", "Login...",true);
				new Thread(new Runnable(){      
            		public void run(){
            			try{    
				
							EditText email = (EditText) findViewById(R.id.mail);
							EditText psw = (EditText) findViewById(R.id.password);
							
							BeintooUser user = new BeintooUser();
							User loggedUser = user.getUsersByEmail(email.getText().toString(), psw.getText().toString());
							
							// DEBUG
							DebugUtility.showLog("logged: "+loggedUser.getId());
							
							// IF THE USER O PASSWORD ARE WRONG SHOW AN ERROR
							if(loggedUser.getId() == null) {
								// DISMISS THE KEYBOARD
								imm.hideSoftInputFromWindow(email.getApplicationWindowToken(), 0);
					            imm.hideSoftInputFromWindow(psw.getApplicationWindowToken(), 0);
								dialog.dismiss(); // DISMISS LOGIN DIALOG
								ErrorDisplayer.showConnectionErrorOnThread("Wrong username or password", getContext());								
							}else {// PLAYERLOGIN AND THEN CLOSE THE LOGIN FORM AND GO HOME
								BeintooPlayer player = new BeintooPlayer();
								Player newPlayer = player.playerLogin(loggedUser.getId(),null,DeviceId.getUniqueDeviceId(getContext()),null, null);
								Gson gson = new Gson();					
								String jsonPlayer = gson.toJson(newPlayer);
								 
								// SAVE THE CURRENT PLAYER
								PreferencesHandler.saveString("currentPlayer", jsonPlayer, getContext());
								// SET THAT THE PLAYER IS LOGGED IN BEINTOO
								PreferencesHandler.saveBool("isLogged", true, getContext());
								
								// FINALLY GO USER HOME
								UIhandler.sendEmptyMessage(GO_HOME);	
								dialog.dismiss();
							}							
            			}catch(Exception e){
            			}	
            			
            		} 
				}).start();
				
			}	
        });
		
		Button fblogin = (Button) findViewById(R.id.fblogin);
		fblogin.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {								
				String loginUrl = "http://www.beintoo.com/connect.html?signup=facebook&apikey="
						+ DeveloperConfiguration.apiKey
						+ "&display=touch&redirect_uri=http://static.beintoo.com/sdk/register_ok.html&logged_uri=http://beintoostatic.s3.amazonaws.com/sdk/fblogin.html";				
				
				// DEBUG
				DebugUtility.showLog(loginUrl);
				
				PreferencesHandler.saveString("openUrl", loginUrl, getContext());				
				BeintooFacebookLogin fbLoginBrowser = new BeintooFacebookLogin(getContext());
				fbLoginBrowser.show();
				current.dismiss();
			}	
        });
		
		Button closeLogin = (Button) findViewById(R.id.close);
		closeLogin.setBackgroundDrawable(b.setPressedBg(R.drawable.close, R.drawable.close_h, R.drawable.close_h));
		closeLogin.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {												
				current.dismiss();
			}	
        });
	}
	
	Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  if(msg.what == GO_HOME){
				BeintooHome beintooHome = new BeintooHome(getContext());
			  	beintooHome.show();				
			  	current.dismiss();
			  }else  if(msg.what == SIGNUP_BROWSER){
				BeintooSignupBrowser signupBrowser = new BeintooSignupBrowser(getContext());
				signupBrowser.show();
				current.dismiss();
			  }
			  super.handleMessage(msg);
		  }
	};
}
