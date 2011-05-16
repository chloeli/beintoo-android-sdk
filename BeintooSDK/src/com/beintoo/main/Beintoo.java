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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


import com.beintoo.R;
import com.beintoo.activities.BeintooHome;
import com.beintoo.activities.tryBeintoo;
import com.beintoo.activities.tryDialog;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdk.DeveloperConfiguration;
import com.beintoo.beintoosdkutility.ApiCallException;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.ErrorDisplayer;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LocationManagerUtils;
import com.beintoo.beintoosdkutility.MessageDisplayer;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.vgood.VGoodGetList;
import com.beintoo.vgood.VgoodAcceptDialog;
import com.beintoo.vgood.VgoodBanner;
import com.beintoo.wrappers.LocalScores;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerAchievement;
import com.beintoo.wrappers.PlayerScore;
import com.beintoo.wrappers.Vgood;
import com.beintoo.wrappers.VgoodChooseOne;
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
import android.widget.LinearLayout;

 
public class Beintoo{
	static Context currentContext;
	
	private final static int GO_HOME = 1;
	private final static int GET_VGOOD_BANNER = 2;
	private final static int GET_VGOOD_ALERT = 7;
	private final static int GET_MULTI_VGOOD = 6;
	private final static int LOGIN_MESSAGE = 3;
	private final static int SUBMITSCORE_POPUP = 4;
	private final static int TRY_DIALOG_POPUP = 5;
	
	public static Dialog currentDialog = null;
	public static Dialog homeDialog = null;
	public static Vgood vgood = null;
	public static VgoodChooseOne vgoodlist = null;
	
	public static String[] usedFeatures = null; 
	public static String FEATURE_PROFILE = "profile";
	public static String FEATURE_LEADERBOARD = "leaderboard";
	public static String FEATURE_WALLET = "wallet";
	public static String FEATURE_CHALLENGES = "challenges";
	
	public static int VGOOD_NOTIFICATION_BANNER = 1; 
	public static int VGOOD_NOTIFICATION_ALERT = 2;
	private static LinearLayout vgood_container = null;
	
	public static boolean OPEN_DASHBOARD_AFTER_LOGIN = true;
	
	private static AtomicLong LAST_LOGIN = new AtomicLong(0);
	
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
	public static void BeintooStart(final Context ctx, boolean goToDashboard){
		currentContext = ctx;
		OPEN_DASHBOARD_AFTER_LOGIN = goToDashboard;
		
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
							Player newPlayer = player.getPlayer(currentPlayer.getGuid());
							if(newPlayer.getUser().getId() != null){				
								PreferencesHandler.saveString("currentPlayer", gson.toJson(newPlayer), ctx);
								// GO HOME
								UIhandler.sendEmptyMessage(GO_HOME);
							}							
            			}catch (Exception e ){
            				dialog.dismiss();
            				e.printStackTrace();
            				ErrorDisplayer.showConnectionErrorOnThread(ErrorDisplayer.CONN_ERROR, ctx,e);
            				logout(ctx);
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
	 * @param isMultiple if true retrieve a list of vgood in wich the player can choose a virtual good
	 */	
	public static void GetVgood(final Context ctx, final boolean isMultiple, final LinearLayout container, final int notificationType){
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
							    	if(!isMultiple) { // ASSIGN A SINGLE VGOOD TO THE PLAYER
			    				    	vgood = vgoodHand.getVgood(currentPlayer.getGuid(), null, Double.toString(l.getLatitude()), 
			    								Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), false);
			    				    	// ADD THE SINGLE VGOOD TO THE LIST
			    				    	List<Vgood> list = new ArrayList<Vgood>();
			    				    	list.add(vgood);
			    				    	VgoodChooseOne v = new VgoodChooseOne(list);	    				    	
			    				    	vgoodlist = v;
								    	
			    				    	if(vgood.getName() != null){
			    				    		if(notificationType == VGOOD_NOTIFICATION_BANNER){
			    				    			vgood_container = container;
			    				    			UIhandler.sendEmptyMessage(GET_VGOOD_BANNER);
			    				    		}else{
			    				    			UIhandler.sendEmptyMessage(GET_VGOOD_ALERT);
			    				    		}
			    				    	}
							    	}else { // ASSIGN A LIST OF VGOOD TO THE PLAYER
							    		vgoodlist = vgoodHand.getVgoodList(currentPlayer.getGuid(), null, Double.toString(l.getLatitude()), 
			    								Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), false);    						 
			    				    	if(vgoodlist != null){
			    				    		// CHECK IF THERE IS MORE THAN ONE VGOOD AVAILABLE
		    				    			if(notificationType == VGOOD_NOTIFICATION_BANNER){
		    				    				vgood_container = container;
			    				    			UIhandler.sendEmptyMessage(GET_VGOOD_BANNER);
		    				    			}else{
			    				    			UIhandler.sendEmptyMessage(GET_VGOOD_ALERT);
		    				    			}
			    				    	}
							    	}
							    	saveLocationHelper (location); 
		    				    	Looper.myLooper().quit(); //QUIT THE THREAD
							    }
								public void onProviderDisabled(String provider) {}
				
								public void onProviderEnabled(String provider) {}
				
								public void onStatusChanged(String provider,int status, Bundle extras) {  }
		    				}; 
							locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); //NETWORK_PROVIDER
							Looper.loop();
		    			}catch(Exception e){e.printStackTrace();}	
		    		}		
	    		}).start();
	    	}else {
	    		new Thread(new Runnable(){     					
		    		public void run(){
		    			try{			 
		            		// GET A VGOOD WITHOUT COORDINATES
		    				if(!isMultiple) { // ASSIGN A SINGLE VGOOD TO THE PLAYER
						    	vgood = vgoodHand.getVgood(currentPlayer.getGuid(), null, null, 
										null, null, false);   		
						    	// ADD THE SINGLE VGOOD TO THE LIST
						    	List<Vgood> l = new ArrayList<Vgood>();
						    	l.add(vgood);
	    				    	VgoodChooseOne v = new VgoodChooseOne(l);	    				    	
	    				    	vgoodlist = v;
						    	if(vgood.getName() != null){
	    				    		if(notificationType == VGOOD_NOTIFICATION_BANNER){
	    				    			vgood_container = container;
	    				    			UIhandler.sendEmptyMessage(GET_VGOOD_BANNER);
	    				    		}else{
	    				    			UIhandler.sendEmptyMessage(GET_VGOOD_ALERT);
	    				    		}
	    				    	}
		    				} else { // ASSIGN A LIST OF VGOOD TO THE PLAYER		    					
		    					vgoodlist = vgoodHand.getVgoodList(currentPlayer.getGuid(), null, null, 
										null, null, false);   				    		    					
			            		if(vgoodlist != null){
			            			// CHECK IF THERE IS MORE THAN ONE VGOOD AVAILABLE
			            			if(vgoodlist != null){
		    				    		// CHECK IF THERE IS MORE THAN ONE VGOOD AVAILABLE
	    				    			if(notificationType == VGOOD_NOTIFICATION_BANNER){
	    				    				vgood_container = container;
		    				    			UIhandler.sendEmptyMessage(GET_VGOOD_BANNER);
	    				    			}else{
		    				    			UIhandler.sendEmptyMessage(GET_VGOOD_ALERT);
	    				    			}
		    				    	}
						    	}
		    				}
		    			}catch(Exception e){e.printStackTrace();}
		    		}
				}).start();			
	    	}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void GetVgood(final Context ctx) {
		GetVgood(ctx, false, null, VGOOD_NOTIFICATION_ALERT);
	}
	
	/**
	 * Check if the user is eligible for a new virtual good
	 * 
	 * @param ctx
	 * @param el
	 */
	public static void isEligibleForVgood(final Context ctx, final BEligibleVgoodListener el){
		currentContext = ctx;
		
		try {
			final Player p = JSONconverter.playerJsonToObject((PreferencesHandler.getString("currentPlayer", currentContext)));
			final BeintooVgood vgooddispatcher = new BeintooVgood();
			
			if(p.getGuid() == null) return;
			
    		new Thread(new Runnable(){     					
	    		public void run(){
	    			try{	    				
	    				Location location = getSavedPlayerLocation();
	    				 
	    				if(location != null){
	    					com.beintoo.wrappers.Message msg = null;
	    					try {
	    						msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, Double.toString(location.getLatitude()), 
	    						Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()));
	    						
	    						checkVgoodEligibility(msg.getMessageID(), p,el);
	    					}catch (ApiCallException e){
	    						checkVgoodEligibility(e.getId(), p,el);
	    					}
	    				}else{
	    					final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
	    			    	if(LocationManagerUtils.isProviderSupported("network", locationManager) &&
	    			    			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){	    			    		
	    			    		Looper.prepare();
	    			    		final LocationListener locationListener = new LocationListener() {
	    			    			public void onLocationChanged(Location location) {
	    			    				locationManager.removeUpdates(this);
	    			    				com.beintoo.wrappers.Message msg = null;
	    			    				try {
	    			    					msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, Double.toString(location.getLatitude()), 
				    						Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()));
	    			    					
	    			    					checkVgoodEligibility(msg.getMessageID(), p,el);
	    			    				}catch(ApiCallException e){
	    			    					checkVgoodEligibility(e.getId(), p,el);
	    			    				}
	    			    				Looper.myLooper().quit();
	    			    			} 
	    			    			public void onProviderDisabled(String provider) {}
			
	    			    			public void onProviderEnabled(String provider) {}
			
	    			    			public void onStatusChanged(String provider,int status, Bundle extras) {}
	    			    		};   
	    			    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, Looper.myLooper());
	    			    		Looper.loop();
	    			    	}else{ // LOCATION NOT AVAILABLE
	    			    		com.beintoo.wrappers.Message msg = null;
	    			    		try {
	    			    			msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, null, 
			    						null, null);
	    			    			checkVgoodEligibility(msg.getMessageID(), p,el);
	    			    		}catch (ApiCallException e){
	    			    			checkVgoodEligibility(e.getId(), p,el);
	    			    		} 
	    			    	}	
	    				}
	    			}catch(Exception e){e.printStackTrace();}
	    		}
			}).start();			
		}catch (Exception e){
			e.printStackTrace(); 
		}
	}
	
	private static void checkVgoodEligibility(Integer msgId, Player p, BEligibleVgoodListener el){
		el.onComplete(p);
		if(msgId == 0){
			el.vgoodAvailable(p);	    						 
		}else if(msgId == -11){
			el.isOverQuota(p);
		}else if(msgId == -10 || msgId == -21){
			el.nothingToDispatch(p);
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
    			synchronized (LAST_LOGIN){
	    			try{
	    				Gson gson = new Gson();
	    				String currentPlayer = PreferencesHandler.getString("currentPlayer", ctx);
	    				Player loggedUser = null;
	    				if(currentPlayer != null)
	    					loggedUser = gson.fromJson(currentPlayer, Player.class);
	    				
	    				BeintooPlayer player = new BeintooPlayer();
	    				Player loginPlayer; 

	    				if(System.currentTimeMillis() > LAST_LOGIN.get() + 600000){	
	    					LAST_LOGIN.set(System.currentTimeMillis());
	    					
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
		        				DebugUtility.showLog("After playerLogin "+gson.toJson(loginPlayer));        				
		    				}  
	    				}else {
	    					loginPlayer = loggedUser;    					
	    				}
	    				
	    				if(loginPlayer.getUser()!=null){
	        				Message msg = new Message();
	        				Bundle b = new Bundle();
	        				int unread = loginPlayer.getUser().getUnreadMessages();        				
	        				String message = ctx.getString(R.string.homeWelcome)+loginPlayer.getUser().getNickname();
	        				if(unread > 0) message = message +"\n"+String.format(ctx.getString(R.string.messagenotification), unread);
	    					b.putString("Message", message);
	    					b.putInt("Gravity", Gravity.BOTTOM);
	    					msg.setData(b);
	    					msg.what = LOGIN_MESSAGE;
	    					UIhandler.sendMessage(msg);
	    				}
	    				
	    				savePlayerLocation(currentContext);
	    				
	    			}catch (Exception e){
	    				e.printStackTrace();
	    			}
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
	public static void submitScoreWithVgoodCheck (final Context ctx, int score, int treshold, boolean isMultiple,
			LinearLayout container, int notificationType){
		currentContext = ctx;
		try{
			Player currentPlayer = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", ctx), Player.class);
			String key = currentPlayer.getGuid()+":count";
			
			int currentTempScore = PreferencesHandler.getInt(key, ctx);
			currentTempScore+=score;
			
			submitScore(ctx, score, null, true, Gravity.BOTTOM);
			
			if(currentTempScore >= treshold) { // THE USER REACHED THE DEVELOPER TRESHOLD SEND VGOOD AND SAVE THE REST
				PreferencesHandler.saveInt(key, currentTempScore-treshold, ctx);
				GetVgood(ctx,isMultiple,container,notificationType);
			}else{
				PreferencesHandler.saveInt(key, currentTempScore, ctx);
			}
		
		}catch(Exception e){e.printStackTrace(); } 
	}
	
	public static void submitScoreWithVgoodCheck (final Context ctx, int score, int treshold){
		submitScoreWithVgoodCheck (ctx, score, treshold, true, null, VGOOD_NOTIFICATION_ALERT);
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
	private static void submitScoreHelper (final Context ctx, final int lastScore, final Integer balance, final String codeID, 
			final boolean showNotification,final Location location, final int gravity){
		new Thread(new Runnable(){     					
    		public void run(){	
				try {
	    			final Message msg = new Message();
					Bundle b = new Bundle();
					if(balance != null)
						b.putString("Message", String.format(ctx.getString(R.string.earnedScore), lastScore)+String.format(ctx.getString(R.string.earnedBalance), balance)); 
					else
						b.putString("Message", String.format(ctx.getString(R.string.earnedScore), lastScore));
					
					b.putInt("Gravity", gravity);
					
					msg.setData(b);
					msg.what = SUBMITSCORE_POPUP;
					
					
					/* check if there is a previously submitted score that could not be submitted because of
					 connection error */										
					final BeintooPlayer player = new BeintooPlayer();
					String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
					final Player p = JSONconverter.playerJsonToObject(jsonPlayer);
					com.beintoo.wrappers.Message result;
					if(location != null){
						result = player.submitScore(p.getGuid(), codeID, null, lastScore, balance,Double.toString(location.getLatitude()), 
								Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()), null);
					}else{
						result = player.submitScore(p.getGuid(), codeID, null, lastScore, balance, null, null, null, null);
					}
					 
					if(!result.getMessage().equals("OK")){ // SAVE THE SCORE LOCALLY
						LocalScores.saveLocalScore(ctx,lastScore,codeID);
					}else{		
						// THERE ARE SOME SCORES TO SUBMIT
						if(PreferencesHandler.getString("localScores", ctx) != null){
							String jsonScore = PreferencesHandler.getString("localScores", ctx); 
							PreferencesHandler.clearPref("localScores", ctx);
							player.submitSavedScores(p.getGuid(), null, null, jsonScore, null, null, null, null);							
						}
					}
					if(showNotification)
						UIhandler.sendMessage(msg);
				
				}catch (Exception e){e.printStackTrace();}
    		}	
    	}).start();				
	}
	

	/**
	 * Submit user score check the user location and then call submitScoreHelper wich make the api call in a thread
	 * 
	 * @param ctx the current Context
	 * @param lastScore the score to submit
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @param showNotification if true it will show a notification to the user when receive a submit score 
	 */	
	public static void submitScore(final Context ctx, final int lastScore, final String codeID, final boolean showNotification, int gravity){
		currentContext = ctx;
		String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
		final Player p = JSONconverter.playerJsonToObject(jsonPlayer);		
		if(p != null){			    			
			try{
				Long currentTime = System.currentTimeMillis();
				Location pLoc = getSavedPlayerLocation();
	        	if(pLoc != null){
	        		if((currentTime - pLoc.getTime()) <= 900000){ // TEST 20000 (20 seconds)
	        			submitScoreHelper(ctx,lastScore,null,codeID,showNotification,pLoc, gravity);
	        		}else {
	        			submitScoreHelper(ctx,lastScore,null,codeID,showNotification,null, gravity);
	        		}
	        	}else { // LOCATION IS DISABLED OR FIRST TIME EXECUTION
	        		submitScoreHelper(ctx,lastScore,null,codeID,showNotification,null, gravity);
	        	}	
			}catch(Exception e){ 
				e.printStackTrace();    				
			}		    			
		}
	}
	
	public static void submitScore(Context ctx, int lastScore, boolean showNotification, int gravity){
		submitScore(ctx, lastScore, null, showNotification, gravity);
	}
	
	public static void submitScore(Context ctx, int lastScore, boolean showNotification){
		submitScore(ctx, lastScore, null, showNotification, Gravity.BOTTOM);
	}
		
	public static void submitScore(Context ctx, int lastScore, String codeID, boolean showNotification){
		submitScore(ctx, lastScore, codeID, showNotification, Gravity.BOTTOM);
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
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static PlayerScore getPlayerScore(final Context ctx){
		return getPlayerScore(ctx,null);
	}
	
	/** 
	 * Get the current player score with a callback
	 * 
	 * @param ctx
	 * @param codeID
	 * @return
	 */
	public static void getPlayerScoreAsync(final Context ctx, final String codeID, final BScoreListener listener){
		new Thread(new Runnable(){     					
    		public void run(){	
				try {
					PlayerScore p = null;
					BeintooPlayer player = new BeintooPlayer();
					String guid = JSONconverter.playerJsonToObject(PreferencesHandler.getString("currentPlayer", ctx)).getGuid();
					if(codeID != null)
						p = player.getScoreForContest(guid, codeID);
					else
						p = player.getScore(guid);
					
					listener.onComplete(p);
					
				} catch (Exception e) {
					e.printStackTrace();
					listener.onBeintooError(e);
				}				
    		}
		}).start();
	}
		 
	/**
	 * Se which features to use in your app
	 * @param features an array of features avalaible features are: profile, leaderboard, wallet, challenge 
	 */
	public static void setFeaturesToUse(String[] features){
		usedFeatures = features;
	}
	public static void featureToUse(String[] features){
		setFeaturesToUse(features);
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
	            case GET_VGOOD_BANNER: // GET A VGOOD (GET_VGOOD)
					VgoodBanner vb = new VgoodBanner(currentContext,vgood_container, vgoodlist);
	            	vb.show();
	            break;
	            case GET_VGOOD_ALERT: // GET A VGOOD (GET_VGOOD)
	            	VgoodAcceptDialog d = new VgoodAcceptDialog(currentContext, vgoodlist);
	  			  	d.show();
	            break;
	            case GET_MULTI_VGOOD:
	            	VGoodGetList getVgoodList = new VGoodGetList(currentContext, vgoodlist);
		    		currentDialog = getVgoodList;
		    		getVgoodList.show();
	            break;
	            case LOGIN_MESSAGE:	            	
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("Message"), msg.getData().getInt("Gravity"));
	            break;
	            case SUBMITSCORE_POPUP:
	            	MessageDisplayer.showMessage(currentContext, msg.getData().getString("Message"), msg.getData().getInt("Gravity"));
	            break;
	            case TRY_DIALOG_POPUP:
	            	tryDialog t = new tryDialog(currentContext);
	            	t.open();
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
						    	saveLocationHelper (location);
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
	    				}catch(Exception e){e.printStackTrace();}
	        		}	
	        	}).start();		
    		}
		}catch(Exception e){ 
			e.printStackTrace(); 				
		}		    
	}
	
	private static void saveLocationHelper (Location location){
		PreferencesHandler.saveString("playerLatitude",Double.toString(location.getLatitude()), currentContext);
    	PreferencesHandler.saveString("playerLongitude", Double.toString(location.getLongitude()), currentContext);
    	PreferencesHandler.saveString("playerAccuracy", Float.toString(location.getAccuracy()), currentContext);
    	PreferencesHandler.saveString("playerLastTimeLocation", Long.toString(location.getTime()), currentContext);    	
    	DebugUtility.showLog("SAVED PLAYER LOCATION: "+location);
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
	
	 public static interface BScoreListener {
		 public void onComplete(PlayerScore p);
		 public void onBeintooError(Exception e);
	 }
	 
	 public static interface BAchievementListener {
		 public void onComplete(List<PlayerAchievement> a);
		 public void onBeintooError(Exception e);
	 }
	 
	 public static interface BEligibleVgoodListener {
		 public void onComplete(Player p);
		 public void vgoodAvailable(Player p);
		 public void isOverQuota(Player p);
		 public void nothingToDispatch(Player p);
		 public void onError();
	 }
}