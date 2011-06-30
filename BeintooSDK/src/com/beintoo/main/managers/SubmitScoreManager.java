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
package com.beintoo.main.managers;

import java.util.concurrent.atomic.AtomicLong;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdkutility.ApiCallException;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BGetVgoodListener;
import com.beintoo.main.Beintoo.BSubmitScoreListener;
import com.beintoo.wrappers.LocalScores;
import com.beintoo.wrappers.Player;
import com.google.beintoogson.Gson;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.widget.LinearLayout;

public class SubmitScoreManager {
	
	private static AtomicLong LAST_OPERATION = new AtomicLong(0);
	private long OPERATION_TIMEOUT = 5000;
	
	public void submitScoreWithVgoodCheck (final Context ctx, int score, int threshold, String codeID, boolean isMultiple,
			LinearLayout container, int notificationType, final BSubmitScoreListener slistener, final BGetVgoodListener glistener){
		
		try{
			Player currentPlayer = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", ctx), Player.class);
			String key;
			if(codeID != null)
				key = currentPlayer.getGuid()+":count:"+codeID;
			else
				key = currentPlayer.getGuid()+":count";
			
			int currentTempScore = PreferencesHandler.getInt(key, ctx);
			currentTempScore+=score;
					
			if(currentTempScore >= threshold) { // THE USER REACHED THE DEVELOPER TRESHOLD SEND VGOOD AND SAVE THE REST
				PreferencesHandler.saveInt(key, currentTempScore-threshold, ctx);
				submitScore(ctx, score, codeID, true, Gravity.BOTTOM, slistener);
				
				GetVgoodManager gvm = new GetVgoodManager(ctx);
				gvm.GetVgood(ctx, codeID, isMultiple,container,notificationType, glistener);
			}else{
				PreferencesHandler.saveInt(key, currentTempScore, ctx);
				submitScore(ctx, score, codeID, true, Gravity.BOTTOM, slistener);				
			}
			
		}catch(Exception e){e.printStackTrace(); if(slistener != null) slistener.onBeintooError(e);} 
		
	}
	
	public void submitScore(final Context ctx, final int lastScore, final String codeID, final boolean showNotification, int gravity, final BSubmitScoreListener listener){
		String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
		final Player p = JSONconverter.playerJsonToObject(jsonPlayer);		
		if(p != null){			    			
			try{
				Long currentTime = System.currentTimeMillis();
				Location pLoc = LocationMManager.getSavedPlayerLocation(ctx);
	        	if(pLoc != null){
	        		if((currentTime - pLoc.getTime()) <= 900000){ // TEST 20000 (20 seconds)
	        			submitScoreHelper(ctx,lastScore,null,codeID,showNotification,pLoc, gravity, listener);
	        		}else {
	        			submitScoreHelper(ctx,lastScore,null,codeID,showNotification,null, gravity, listener);
	        		}
	        	}else { // LOCATION IS DISABLED OR FIRST TIME EXECUTION
	        		submitScoreHelper(ctx,lastScore,null,codeID,showNotification,null, gravity, listener);
	        	}	
			}catch(Exception e){ 
				e.printStackTrace(); 
			}		    			
		}
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
	private void submitScoreHelper (final Context ctx, final int lastScore, final Integer balance, final String codeID, 
			final boolean showNotification,final Location location, final int gravity, final BSubmitScoreListener listener){
			
		new Thread(new Runnable(){     					
    		public void run(){	
    			synchronized (LAST_OPERATION){
					try {
						
						if(System.currentTimeMillis() < LAST_OPERATION.get() + OPERATION_TIMEOUT)
							return;
						
		    			final Message msg = new Message();
						Bundle b = new Bundle();
						if(balance != null)
							b.putString("Message", String.format(ctx.getString(R.string.earnedScore), lastScore)+String.format(ctx.getString(R.string.earnedBalance), balance)); 
						else
							b.putString("Message", String.format(ctx.getString(R.string.earnedScore), lastScore));
						
						b.putInt("Gravity", gravity);
						
						msg.setData(b);
						msg.what = Beintoo.SUBMITSCORE_POPUP;
															
						final BeintooPlayer player = new BeintooPlayer();
						String jsonPlayer = PreferencesHandler.getString("currentPlayer", ctx);
						final Player p = JSONconverter.playerJsonToObject(jsonPlayer);
						com.beintoo.wrappers.Message result = null;
						try {
							if(location != null){
								result = player.submitScore(p.getGuid(), codeID, null, lastScore, balance,Double.toString(location.getLatitude()), 
										Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()), null);						
							}else{
								result = player.submitScore(p.getGuid(), codeID, null, lastScore, balance, null, null, null, null);
							}
						}catch(ApiCallException e){ if(listener!=null) listener.onBeintooError(e); }	
						
						if(result == null){ // SAVE THE SCORE LOCALLY
							LocalScores.saveLocalScore(ctx,lastScore,codeID);
						}else{		
							// THERE ARE SOME SCORES TO SUBMIT
							if(PreferencesHandler.getString("localScores", ctx) != null){
								try{
									String jsonScore = PreferencesHandler.getString("localScores", ctx); 
									PreferencesHandler.clearPref("localScores", ctx);
									player.submitSavedScores(p.getGuid(), null, null, jsonScore, null, null, null, null);
								}catch(Exception e){}
							}
						}
						if(showNotification)
							Beintoo.UIhandler.sendMessage(msg);
					
						if(listener != null)
							listener.onComplete();
						
						LAST_OPERATION.set(System.currentTimeMillis());
						
					}catch (Exception e){e.printStackTrace(); 
						if(listener != null)
							listener.onBeintooError(e);
					}
    			}
    		}	
    	}).start();				
	}
}
