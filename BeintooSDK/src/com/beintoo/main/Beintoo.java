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
package com.beintoo.main;

import com.beintoo.activities.BeintooHome;
import com.beintoo.activities.VGoodGetDialog;
import com.beintoo.activities.tryBeintoo;
import com.beintoo.beintoosdk.BeintooConnection;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LocationManagerUtils;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerScore;
import com.beintoo.wrappers.Vgood;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
 
public class Beintoo{
	static Context currentContext;
	private final static int GO_HOME = 1;
	private final static int GET_VGOOD = 2;
	private final static int LOGIN_MESSAGE = 3;
	private final static int SUBMITSCORE_POPUP = 4;
	
	public static Dialog currentDialog = null;
	public static Dialog homeDialog = null;
	
	/**
	 * Set the developer apikey
	 * 
	 * @param apikey your apikey
	 */
	public static void setApiKey(String apikey){
		DeveloperConfiguration.apiKey = apikey;
	}
	
	/** 
	 * Starts the Beintoo Main app where the user can login into Beintoo
	 * and manage their goods, profile, leaderboard ecc. 
	 * 
	 * @param ctx current context
	 */
	public static void BeintooStart(final Context ctx){
		currentContext = ctx;
		final Gson gson = new Gson();
		boolean isLogged = PreferencesHandler.getBool("isLogged", ctx);
		String savedPlayer = PreferencesHandler.getString("currentPlayer", ctx);
		final Player currentPlayer = gson.fromJson(savedPlayer, Player.class);
		System.out.println(currentPlayer);
		
		// AVOID BAD LOGIN
		if(isLogged == true && (savedPlayer == null || savedPlayer == "")) logout(ctx);
			
		if(BeintooConnection.isOnline(ctx)){ // CHECK THE CONNECTION
				try{
					if(isLogged == true && currentPlayer.getUser() != null){
						final ProgressDialog  dialog = ProgressDialog.show(ctx, "", "Loading Beintoo...",true);
						Thread t = new Thread(new Runnable(){     					
		            		public void run(){ 
		            			try{   	
									BeintooPlayer player = new BeintooPlayer();
									System.out.println("current saved player: "+PreferencesHandler.getString("currentPlayer", ctx));				
									// LOGIN TO BEINTOO
									Player newPlayer = player.playerLogin(currentPlayer.getUser().getId(),null,DeviceId.getUniqueDeviceId(ctx),null, null);
									// GO HOME
									if(newPlayer.getUser().getId() != null){				
										PreferencesHandler.saveString("currentPlayer", gson.toJson(newPlayer), ctx);
										UIhandler.sendEmptyMessage(GO_HOME);
									}
		            			}catch (Exception e ){
		            			}
		            			dialog.dismiss();
		            		}
						});
						t.start();
				
					}else{
							tryBeintoo tryBe = new tryBeintoo(ctx);
							currentDialog = tryBe;
							tryBe.show();
							//Intent myIntent = new Intent(currentContext, BeintooActivity.class).putExtra("currentDialog", 1);
							//currentContext.startActivity(myIntent);
					}
				}catch (Exception e ){e.printStackTrace();}
		}else ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx);
		
	}
	
	/**
	 * Send a Vgood to the user it runs in background on a thread 
	 * because it retrieves the user location and do a playerlogin
	 * 
	 * @param ctx current context
	 */
	
	public static void GetVgood(final Context ctx){
		currentContext = ctx;	
		new Thread(new Runnable(){     					
    		public void run(){
    			try{
    				UIhandler.sendEmptyMessage(GET_VGOOD);
    			}catch (Exception e){
    			}	
    		}
		}).start();	
	}
	
	/**
	 * Do a player login and save the player data to sharedPreferences 
	 * 
	 * @param ctx current context
	 */
	
	public static void playerLogin(final Context ctx){
		currentContext = ctx;	
		new Thread(new Runnable(){     					
    		public void run(){
    			try{
    				Gson gson = new Gson();
    				String currentPlayer = PreferencesHandler.getString("currentPlayer", ctx);
    				Player loggedUser = gson.fromJson(currentPlayer, Player.class);
    				BeintooPlayer player = new BeintooPlayer();
    				Player loginPlayer; 

    				if(loggedUser == null){ // FIRST EXECUTION
    					loginPlayer = player.playerLogin(null,null,null,
    						DeviceId.getUniqueDeviceId(currentContext),null, null);
    				}else if(loggedUser.getUser() == null){ // A PLAYER EXISTS BUT NOT A USER
    					loginPlayer = player.playerLogin(null,loggedUser.getGuid(),null,
        						DeviceId.getUniqueDeviceId(currentContext),null, null);
    				}else{ // PLAYER HAS A REGISTERED USER
    					loginPlayer = player.playerLogin(loggedUser.getUser().getId(),null,
        						DeviceId.getUniqueDeviceId(currentContext),null, null);
    				} 
    				PreferencesHandler.saveString("currentPlayer", gson.toJson(loginPlayer), ctx);
    				System.out.println("Dopo playerLogin "+gson.toJson(loginPlayer));
    				
    				if(loginPlayer.getUser()!=null){
        				Message msg = new Message();
        				Bundle b = new Bundle();
    					b.putString("Message", "Welcome back "+loginPlayer.getUser().getNickname());    				
    					msg.setData(b);
    					msg.what = LOGIN_MESSAGE;
    					UIhandler.sendMessage(msg);
    				}
    			}catch (Exception e){
    			}	
    		}
		}).start();	
	}
	
	
	/**
	 * Submit user score 
	 * 
	 * @param lastScore the score to submit
	 * @param balance the total user score balance (used in performance apps)
	 * @param codeID the codeID a string that represents the position in your code.
	 * @param ctx the current context
	 */
	public static void submitScore(Context ctx, final int lastScore, final int balance, final String codeID, final boolean showNotification){
		final BeintooPlayer player = new BeintooPlayer();
		String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
		final Player p = JSONconverter.playerJsonToObject(jsonPlayer);
		
		if(p != null){
			new Thread(new Runnable(){     					
	    		public void run(){
	    			Looper.prepare();
	    			try{
	    				final Message msg = new Message();
	    				Bundle b = new Bundle();
	    				if(balance != -1)
	    					b.putString("Message", "You earned "+lastScore+" points\nYour balance is: "+balance);
	    				else
	    					b.putString("Message", "You earned "+lastScore+" points");
	    				
	    				msg.setData(b);
	    				msg.what = SUBMITSCORE_POPUP;
	    				
			        	final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
			        	if(LocationManagerUtils.isProviderSupported("network", locationManager)){
							final LocationListener locationListener = new LocationListener() {
							    public void onLocationChanged(Location location) {		 
									// SUBMIT SCORE WITH COORDS
							    	player.submitScore(p.getGuid(), codeID, null, lastScore, balance,Double.toString(location.getLatitude()), 
											Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()), null);
							    	
					    			// REMOVE NETWORK UPDATE - NO MORE NEEDED
					    			locationManager.removeUpdates(this);
					    			if(showNotification)
					    				UIhandler.sendMessage(msg);
							    }
								public void onProviderDisabled(String provider) {}
			
								public void onProviderEnabled(String provider) {}
			
								public void onStatusChanged(String provider,int status, Bundle extras) { System.out.println("status"+extras); }
							};   
							locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); //
			        	}else {
			        		player.submitScore(p.getGuid(), codeID, null, lastScore, balance, null, null, null, null);
			        		if(showNotification)
			        			UIhandler.sendMessage(msg);
			        	}
			
	    			}catch(Exception e){ e.printStackTrace(); }	    			
				Looper.loop();
	    		}
			}).start();
			
		}
	}
	
	public static void submitScore(Context ctx, int lastScore, boolean showNotification){
		submitScore(ctx, lastScore, -1, null, showNotification);
	}
	
	public static void submitScore(Context ctx, int lastScore, int balance, boolean showNotification){
		submitScore(ctx, lastScore, balance, null, showNotification);
	}
	
	public static void submitScore(Context ctx, int lastScore, String codeID, boolean showNotification){
		submitScore(ctx, lastScore, -1, codeID, showNotification);
	}
	
	
	/**
	 * Get the current player score
	 * 
	 * @param ctx
	 * @param codeID
	 * @return
	 */
	public static PlayerScore getPlayerScore(final Context ctx, final String codeID){
		try {
			PlayerScore p = null;
			BeintooPlayer player = new BeintooPlayer();
			String guid = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", ctx)).getGuid();
			if(codeID != null)
				p = player.getScoreForContest(guid, codeID);
			else
				p = player.getScore(guid);
			
			return p;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static PlayerScore getPlayerScore(final Context ctx){
		return getPlayerScore(ctx,null);
	}
	
	/*
	 * Handler for managing user interfaces in a Thread
	 */
	static Handler UIhandler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) {
			  switch (msg.what) {	
			  
	            case GO_HOME: // GO HOME ON START BEINTOO (GO_HOME)
	            	BeintooHome beintooHome = new BeintooHome(currentContext);
	            	currentDialog = beintooHome;
					beintooHome.show();
	            	//Intent myIntent = new Intent(currentContext, BeintooActivity.class).putExtra("currentDialog", 2);
	            	//currentContext.startActivity(myIntent);
	            break;
	            case GET_VGOOD: // GET A VGOOD (GET_VGOOD)	  
	            	
            		final Player currentPlayer = JSONconverter.playerJsonToObject((PreferencesHandler.getString("currentPlayer", currentContext)));
					final BeintooVgood vgoodHand = new BeintooVgood();
					
	            	final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
	            	if(LocationManagerUtils.isProviderSupported("network", locationManager)){
	    				final LocationListener locationListener = new LocationListener() {
	    				    public void onLocationChanged(Location location) {	
	    				    	System.out.println("location changed");
	    				    	locationManager.removeUpdates(this);
	    						// GET A VGOOD WITH COORDINATES
	    				    	Vgood vgood = vgoodHand.getVgood(currentPlayer.getGuid(), null, Double.toString(location.getLatitude()), 
	    								Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()), false);    						
	    				    	
	    				    	if(vgood.getName() != null){
	    				    		VGoodGetDialog getVgood = new VGoodGetDialog(currentContext, vgood);
	    				    		currentDialog = getVgood;
	    		    				getVgood.show();
	    				    	}
	    				    }
							public void onProviderDisabled(String provider) {}
	
							public void onProviderEnabled(String provider) {}
	
							public void onStatusChanged(String provider,int status, Bundle extras) {  }
	    				};   
	    				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); //NETWORK_PROVIDER
	            	}else {
	            		// GET A VGOOD WITH COORDINATES
				    	Vgood vgood = vgoodHand.getVgood(currentPlayer.getGuid(), null, null, 
								null, null, false);   
				    	
	            		if(vgood.getName() != null){
				    		VGoodGetDialog getVgood = new VGoodGetDialog(currentContext, vgood);
				    		currentDialog = getVgood;
		    				getVgood.show();
				    	}
	            	}
	            	
	            	//Location location = (Location) msg.getData().getParcelable("location");
	            	//System.out.println("Location dall'Handler "+location);	            	
	            break;
	            case LOGIN_MESSAGE:	            	
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("Message"));
	            break;
	            case SUBMITSCORE_POPUP:
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("Message"));
	            break;
		    }
	        super.handleMessage(msg);
		  }
	};
	
	public static void logout(Context ctx){
		PreferencesHandler.clearPref("currentPlayer", ctx);
		PreferencesHandler.saveBool("isLogged", false, ctx);
	}
	
	public static void clearBeintoo (){
		if(currentDialog != null)
			currentDialog.dismiss();
	} 
	
	public static void realodBeintoo (){
		if(currentDialog != null)
			currentDialog.show();
	}
}
