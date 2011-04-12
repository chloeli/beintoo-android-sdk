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

import com.beintoo.R;
import com.beintoo.activities.BeintooHome;
import com.beintoo.activities.VGoodGetDialog;
import com.beintoo.activities.tryBeintoo;
import com.beintoo.activities.tryDialog;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkutility.DebugUtility;
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
import android.view.Gravity;

 
public class Beintoo{
	static Context currentContext;
	
	private final static int GO_HOME = 1;
	private final static int GET_VGOOD = 2;
	private final static int LOGIN_MESSAGE = 3;
	private final static int SUBMITSCORE_POPUP = 4;
	private final static int TRY_DIALOG_POPUP = 5;
	
	public static Dialog currentDialog = null;
	public static Dialog homeDialog = null;
	public static Vgood vgood = null;
	
	public static String[] usedFeatures = null; 
	public static String FEATURE_PROFILE = "profile";
	public static String FEATURE_LEADERBOARD = "leaderboard";
	public static String FEATURE_WALLET = "wallet";
	public static String FEATURE_CHALLENGES = "challenges";
	
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
	 * and manage their goods, profile, leaderboard etc. 
	 * 
	 * @param ctx current Context
	 */
	public static void BeintooStart(final Context ctx){
		currentContext = ctx;
		
		try{
			final Gson gson = new Gson();
			boolean isLogged = PreferencesHandler.getBool("isLogged", ctx);
			String savedPlayer = PreferencesHandler.getString("currentPlayer", ctx);
			final Player currentPlayer;
			if(savedPlayer != null){
				currentPlayer = gson.fromJson(savedPlayer, Player.class);
			}else currentPlayer = null;
			
			// AVOID BAD LOGIN
			if(isLogged == true && (savedPlayer == null || savedPlayer == "")) logout(ctx);
			
			if(isLogged == true && currentPlayer.getUser() != null){
				final ProgressDialog  dialog = ProgressDialog.show(ctx, "", ctx.getString(R.string.loadingBeintoo),true);
				Thread t = new Thread(new Runnable(){     					
            		public void run(){ 
            			try{   	
							BeintooPlayer player = new BeintooPlayer();
							// DEBUG
							DebugUtility.showLog("current saved player: "+PreferencesHandler.getString("currentPlayer", ctx));											
							// LOGIN TO BEINTOO
							Player newPlayer = player.playerLogin(currentPlayer.getUser().getId(),null,null,DeviceId.getUniqueDeviceId(ctx),null, null);
							if(newPlayer.getUser().getId() != null){				
								PreferencesHandler.saveString("currentPlayer", gson.toJson(newPlayer), ctx);
								// GO HOME
								UIhandler.sendEmptyMessage(GO_HOME);
							}							
            			}catch (Exception e ){
            				dialog.dismiss();
            				e.printStackTrace();
            				ErrorDisplayer.showConnectionErrorOnThread(ErrorDisplayer.CONN_ERROR, ctx,e);
            			}
            			dialog.dismiss();
            		}
				});
				t.start();
		
			}else{
					tryBeintoo tryBe = new tryBeintoo(ctx);
					currentDialog = tryBe;
					tryBe.show();
			}
		}catch (Exception e ){e.printStackTrace(); ErrorDisplayer.showConnectionError(ErrorDisplayer.CONN_ERROR , ctx,e); logout(ctx);}
	}
	
	/**
	 * Show a dialog with the try Beintoo message. The dialog is show with an interval of days passed in daysInterval
	 * 
	 * @param ctx current Context
	 * @param daysInterval interval of days you want to show the dialog
	 */
	public static void tryBeintooDialog (Context ctx, int daysInterval){
		currentContext = ctx;
		  
		long lastDay = PreferencesHandler.getLong("lastTryDialog", ctx);
		long now = System.currentTimeMillis();
		long daysIntervalMillis = 86400000 * daysInterval; //60000   
		long nextPopup = lastDay + daysIntervalMillis;
		boolean isLogged = PreferencesHandler.getBool("isLogged", ctx);
		
		if((lastDay == 0 || now >= nextPopup) && !isLogged){
			UIhandler.sendEmptyMessage(TRY_DIALOG_POPUP);
			PreferencesHandler.saveLong("lastTryDialog", now, ctx);
		}
	} 
	
	/** 
	 * Send a Vgood to the user it runs in background on a thread. 
	 * It retrieves the user location used to find a Vgood in nearby 
	 * 
	 * @param ctx current Context
	 */	
	public static void GetVgood(final Context ctx){
		currentContext = ctx;	
		
		try {
			final Player currentPlayer = JSONconverter.playerJsonToObject((PreferencesHandler.getString("currentPlayer", currentContext)));
			final BeintooVgood vgoodHand = new BeintooVgood();
			
			if(currentPlayer.getGuid() == null) return;
	    	
			final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
	    	if(LocationManagerUtils.isProviderSupported("network", locationManager) &&
	    			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    		new Thread(new Runnable(){     					
		    		public void run(){
		    			try{	
				    		Looper.prepare();
							final LocationListener locationListener = new LocationListener() {
							    public void onLocationChanged(Location location) {
							    	locationManager.removeUpdates(this);
							    	final Location l = location;
							    					    				
		    						// GET A VGOOD WITH COORDINATES
		    				    	vgood = vgoodHand.getVgood(currentPlayer.getGuid(), null, Double.toString(l.getLatitude()), 
		    								Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), false);    						 
		    				    	if(vgood.getName() != null){
		    				    		UIhandler.sendEmptyMessage(GET_VGOOD);
		    				    	}
		    				    	Looper.myLooper().quit(); //QUIT THE THREAD
							    }
								public void onProviderDisabled(String provider) {}
				
								public void onProviderEnabled(String provider) {}
				
								public void onStatusChanged(String provider,int status, Bundle extras) {  }
		    				}; 
							locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); //NETWORK_PROVIDER
							Looper.loop();
		    			}catch(Exception e){e.printStackTrace(); ErrorDisplayer.externalReport(e);}	
		    		}		
	    		}).start();
	    	}else {
	    		new Thread(new Runnable(){     					
		    		public void run(){
		    			try{			 
		            		// GET A VGOOD WITHOUT COORDINATES
					    	vgood = vgoodHand.getVgood(currentPlayer.getGuid(), null, null, 
									null, null, false);   				    	
		            		if(vgood.getName() != null){
		            			UIhandler.sendEmptyMessage(GET_VGOOD);
					    	}
		    			}catch(Exception e){e.printStackTrace(); ErrorDisplayer.externalReport(e);}
		    		}
				}).start();			
	    	}
		}catch (Exception e){
			e.printStackTrace(); ErrorDisplayer.externalReport(e);
		}
    			
	}
	
	
	/**
	 * Do a player login and save player data 
	 * 
	 * @param ctx current Context
	 */	
	public static void playerLogin(final Context ctx){
		currentContext = ctx;	
		new Thread(new Runnable(){     					
    		public void run(){
    			try{
    				Gson gson = new Gson();
    				String currentPlayer = PreferencesHandler.getString("currentPlayer", ctx);
    				Player loggedUser = null;
    				if(currentPlayer != null)
    					loggedUser = gson.fromJson(currentPlayer, Player.class);
    				
    				BeintooPlayer player = new BeintooPlayer();
    				Player loginPlayer; 

    				if(loggedUser == null){ // FIRST EXECUTION
    					loginPlayer = player.playerLogin(null,null,null,
    						DeviceId.getUniqueDeviceId(currentContext),null, null);
    				}else if(loggedUser.getUser() == null){ // A PLAYER EXISTS BUT NOT A USER
    					loginPlayer = player.playerLogin(null,loggedUser.getGuid(),null,
        						DeviceId.getUniqueDeviceId(currentContext),null, null);
    				}else{ // PLAYER HAS A REGISTERED USER
    					loginPlayer = player.playerLogin(loggedUser.getUser().getId(),null,null,
        						DeviceId.getUniqueDeviceId(currentContext),null, null);
    				} 
    				
    				if(loginPlayer.getGuid()!= null){
    					PreferencesHandler.saveString("currentPlayer", gson.toJson(loginPlayer), ctx);
    					// DEBUG  
        				DebugUtility.showLog("After playerLogin "+gson.toJson(loginPlayer));        				
    				} 
    				
    				if(loginPlayer.getUser()!=null){
        				Message msg = new Message();
        				Bundle b = new Bundle();
    					b.putString("Message", ctx.getString(R.string.homeWelcome)+loginPlayer.getUser().getNickname());
    					b.putInt("Gravity", Gravity.BOTTOM);
    					msg.setData(b);
    					msg.what = LOGIN_MESSAGE;
    					UIhandler.sendMessage(msg);
    				}
    				
    				savePlayerLocation(currentContext);
    				
    			}catch (Exception e){
    				e.printStackTrace(); logout(ctx); ErrorDisplayer.externalReport(e);
    			}	
    		}
		}).start();	
	}
	
	/**
	 * Submit a score with a treshold and check if the user reach that treshold. If yes automatically assign a vgood by
	 * calling getVGood
	 * 
	 * @param ctx current Context
	 * @param score score to submit
	 * @param treshold the score treshold for a new vgood
	 */
	public static void submitScoreWithVgoodCheck (final Context ctx, int score, int treshold){
		currentContext = ctx;
		try{
			Player currentPlayer = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", ctx), Player.class);
			String key = currentPlayer.getGuid()+":count";
			
			int currentTempScore = PreferencesHandler.getInt(key, ctx);
			currentTempScore+=score;
			
			submitScore(ctx, score, -1, null, true, Gravity.BOTTOM);
			
			if(currentTempScore >= treshold) { // THE USER REACHED THE DEVELOPER TRESHOLD SEND VGOOD AND SAVE THE REST
				PreferencesHandler.saveInt(key, currentTempScore-treshold, ctx);
				GetVgood(ctx);
			}else{
				PreferencesHandler.saveInt(key, currentTempScore, ctx);
			}
		
		}catch(Exception e){e.printStackTrace(); ErrorDisplayer.externalReport(e);}
	}
	
	
	/**
	 * Submit player score and save the score in case of submit error. Then send it in the next submit score 
	 * 
	 * @param ctx current context
	 * @param lastScore player score to submit
	 * @param balance player balance
	 * @param codeID codeID
	 * @param showNotification show a toast notification to the player with the score
	 * @param location player Location
	 */
	private static void submitScoreHelper (final Context ctx, final int lastScore, final int balance, final String codeID, 
			final boolean showNotification,final Location location, final int gravity){
		new Thread(new Runnable(){     					
    		public void run(){	
				try {
	    			final Message msg = new Message();
					Bundle b = new Bundle();
					if(balance != -1)
						b.putString("Message", String.format(ctx.getString(R.string.earnedScore), lastScore)+String.format(ctx.getString(R.string.earnedBalance), balance)); 
					else
						b.putString("Message", String.format(ctx.getString(R.string.earnedScore), lastScore));
					
					b.putInt("Gravity", gravity);
					
					msg.setData(b);
					msg.what = SUBMITSCORE_POPUP;
					
					
					/* check if there is a previously submitted score that could not be submitted because of
					 connection error */					
					final int errorScore = PreferencesHandler.getInt("errorScore", ctx);
					
					final BeintooPlayer player = new BeintooPlayer();
					String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
					final Player p = JSONconverter.playerJsonToObject(jsonPlayer);
					com.beintoo.wrappers.Message result;
					if(location != null){
						result = player.submitScore(p.getGuid(), codeID, null, lastScore+errorScore, balance,Double.toString(location.getLatitude()), 
								Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()), null);
					}else{
						result = player.submitScore(p.getGuid(), codeID, null, lastScore+errorScore, balance, null, null, null, null);
					}
					
					if(!result.getMessage().equals("OK")){
						int errorTScore = PreferencesHandler.getInt("errorScore", ctx);
						errorTScore += lastScore;
						PreferencesHandler.saveInt("errorScore", errorTScore, ctx);
					}else									
						PreferencesHandler.clearPref("errorScore", ctx);
					
					if(showNotification)
						UIhandler.sendMessage(msg);
				
				}catch (Exception e){e.printStackTrace(); ErrorDisplayer.externalReport(e);}
    		}	
    	}).start();				
	}
	

	/**
	 * Submit user score check the user location and then call submitScoreHelper wich make the api call in a thread
	 * 
	 * @param ctx the current Context
	 * @param lastScore the score to submit
	 * @param balance the total user score balance (used in performance apps)
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @param showNotification if true it will show a notification to the user when receive a submit score 
	 */	
	public static void submitScore(final Context ctx, final int lastScore, final int balance, final String codeID, final boolean showNotification, int gravity){
		currentContext = ctx;
		String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
		final Player p = JSONconverter.playerJsonToObject(jsonPlayer);		
		if(p != null){			    			
			try{
				Long currentTime = System.currentTimeMillis();
				Location pLoc = getSavedPlayerLocation();
	        	if(pLoc != null){
	        		if((currentTime - pLoc.getTime()) <= 900000){ // TEST 20000 (20 seconds)
	        			submitScoreHelper(ctx,lastScore,balance,codeID,showNotification,pLoc, gravity);
	        		}else {
	        			submitScoreHelper(ctx,lastScore,balance,codeID,showNotification,null, gravity);
	        		}
	        	}else { // LOCATION IS DISABLED OR FIRST TIME EXECUTION
	        		submitScoreHelper(ctx,lastScore,balance,codeID,showNotification,null, gravity);
	        	}	
			}catch(Exception e){ 
				e.printStackTrace(); ErrorDisplayer.externalReport(e);	    				
			}		    			
		}
	}
	
	public static void submitScore(Context ctx, int lastScore, boolean showNotification, int gravity){
		submitScore(ctx, lastScore, -1, null, showNotification, gravity);
	}
	
	public static void submitScore(Context ctx, int lastScore, boolean showNotification){
		submitScore(ctx, lastScore, -1, null, showNotification, Gravity.BOTTOM);
	}
	
	public static void submitScore(Context ctx, int lastScore, int balance, boolean showNotification){
		submitScore(ctx, lastScore, balance, null, showNotification, Gravity.BOTTOM);
	}
	
	public static void submitScore(Context ctx, int lastScore, String codeID, boolean showNotification){
		submitScore(ctx, lastScore, -1, codeID, showNotification, Gravity.BOTTOM);
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
			e.printStackTrace(); ErrorDisplayer.externalReport(e);
		}
		
		return null;
	}
	
	public static PlayerScore getPlayerScore(final Context ctx){
		return getPlayerScore(ctx,null);
	}
	
	/**
	 * Se which features to use in your app
	 * @param features an array of features avalaible features are: profile, leaderboard, wallet, challenge 
	 */
	public static void featureToUse(String[] features){
		usedFeatures = features;
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
	            break;
	            case GET_VGOOD: // GET A VGOOD (GET_VGOOD)
	            	VGoodGetDialog getVgood = new VGoodGetDialog(currentContext, vgood);
		    		currentDialog = getVgood;
    				getVgood.show();	            	
	            break;
	            case LOGIN_MESSAGE:	            	
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("Message"), msg.getData().getInt("Gravity"));
	            break;
	            case SUBMITSCORE_POPUP:
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("Message"), msg.getData().getInt("Gravity"));
	            break;
	            case TRY_DIALOG_POPUP:
	            	tryDialog t = new tryDialog(currentContext);
	            	t.show();
	            break;
		    }
	        super.handleMessage(msg);
		  }
	};
	
	public static void savePlayerLocation(final Context ctx){
		currentContext = ctx;
		try {
			final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
	    	if(LocationManagerUtils.isProviderSupported("network", locationManager) &&
	    			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    		new Thread(new Runnable(){     					
	        		public void run(){	
	    				try {
	        			Looper.prepare();
						final LocationListener locationListener = new LocationListener() {
						    public void onLocationChanged(Location location) {
						    	// SAVE PLAYER LOCATION
						    	PreferencesHandler.saveString("playerLatitude",Double.toString(location.getLatitude()), currentContext);
						    	PreferencesHandler.saveString("playerLongitude", Double.toString(location.getLongitude()), currentContext);
						    	PreferencesHandler.saveString("playerAccuracy", Float.toString(location.getAccuracy()), currentContext);
						    	PreferencesHandler.saveString("playerLastTimeLocation", Long.toString(location.getTime()), currentContext);
								DebugUtility.showLog("PLAYER LOCATION: "+location);
						    	locationManager.removeUpdates(this);
						    	// QUIT THE THREAD
						    	Looper.myLooper().quit();
						    }
							public void onProviderDisabled(String provider) {}
		
							public void onProviderEnabled(String provider) {}
		
							public void onStatusChanged(String provider,int status, Bundle extras) {}
						};   
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, Looper.myLooper());
						Looper.loop();
	    				}catch(Exception e){e.printStackTrace(); ErrorDisplayer.externalReport(e);}
	        		}	
	        	}).start();		
    		}
		}catch(Exception e){ 
			e.printStackTrace(); ErrorDisplayer.externalReport(e);	     				
		}		    
	}
	
	private static Location getSavedPlayerLocation(){
		try {
			Double latitude = Double.parseDouble(PreferencesHandler.getString("playerLatitude", currentContext));
			Double longitude = Double.parseDouble(PreferencesHandler.getString("playerLongitude", currentContext));
			Float accuracy = Float.parseFloat(PreferencesHandler.getString("playerAccuracy", currentContext));
			Long lastTimeLocation = Long.parseLong(PreferencesHandler.getString("playerLastTimeLocation", currentContext));
			
			Location pLoc = new Location("network");
			pLoc.setLatitude(latitude);
			pLoc.setLongitude(longitude);
			pLoc.setAccuracy(accuracy);
			pLoc.setTime(lastTimeLocation);
			// CHECK THE LAST TIME POSITION IF > 15 min UPDATE
			Long currentTime = System.currentTimeMillis();
			if((currentTime - pLoc.getTime()) > 900000){ // TEST 20000 (20 seconds)
				savePlayerLocation(currentContext);
			}
			
			return pLoc;
		
		}catch (Exception e){
			return null;
		}
		
	} 
	
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