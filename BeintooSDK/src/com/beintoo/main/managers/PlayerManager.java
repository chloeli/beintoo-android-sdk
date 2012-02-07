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

import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;

import com.beintoo.R;
import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.beintoosdkutility.SerialExecutor;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BPlayerLoginListener;
import com.beintoo.main.Beintoo.BScoreListener;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerScore;

public class PlayerManager {
	
	private static Context currentContext;
	private long OPERATION_TIMEOUT = 600000;
	
	public PlayerManager (Context ctx){
		currentContext = ctx;
	}
	
	public void playerLogin(final Context ctx, final String guid, final BPlayerLoginListener listener){
		SerialExecutor executor = SerialExecutor.getInstance();
		executor.execute(new Runnable(){     					
    		public void run(){
    			synchronized (Beintoo.LAST_LOGIN){
	    			try{	    				
	    				String currentPlayer = Current.getCurrentPlayerJson(ctx);
	    				Player loggedUser = null;
	    				if(currentPlayer != null)
	    					loggedUser = Current.getCurrentPlayer(ctx);
	    				
	    				BeintooPlayer player = new BeintooPlayer();
	    				Player loginPlayer; 

	    				if(System.currentTimeMillis() > Beintoo.LAST_LOGIN.get() + OPERATION_TIMEOUT){	
	    					Beintoo.LAST_LOGIN.set(System.currentTimeMillis());
	    					
	    					String language = null;
	    					try {
	    						language = Locale.getDefault().getLanguage();
	    					}catch (Exception e){}
	    					
	    					if(guid != null){
	    						loginPlayer = player.playerLogin(null,guid,null,
		        						DeviceId.getUniqueDeviceId(ctx),language, null);
	    					}else if(loggedUser == null){ // FIRST EXECUTION
		    					loginPlayer = player.playerLogin(null,null,null,
		    						DeviceId.getUniqueDeviceId(ctx),language, null);
		    				}else if(loggedUser.getUser() == null){ // A PLAYER EXISTS BUT NOT A USER
		    					loginPlayer = player.playerLogin(null,loggedUser.getGuid(),null,
		        						DeviceId.getUniqueDeviceId(ctx),language, null);
		    				}else{ // PLAYER HAS A REGISTERED USER
		    					loginPlayer = player.playerLogin(loggedUser.getUser().getId(),null,null,
		        						DeviceId.getUniqueDeviceId(ctx),language, null);
		    				} 
		    				
		    				if(loginPlayer.getGuid()!= null){
		    					Current.setCurrentPlayer(ctx, loginPlayer);
		        				DebugUtility.showLog("After playerLogin "+Current.getCurrentPlayerJson(ctx));        				
		    				}  
	    				}else {
	    					loginPlayer = loggedUser;    					
	    				}
	    				
	    				try {
		    				if(loginPlayer.getUser()!=null){
		        				Message msg = new Message();
		        				Bundle b = new Bundle();
		        				Integer unread = null;
		        				if(loginPlayer.getUnreadNotification() != null)
		        					unread = loginPlayer.getUnreadNotification();        				
		        				String message = ctx.getString(R.string.homeWelcome)+loginPlayer.getUser().getNickname();
		        				if(unread > 0) {
		        					if(unread == 1)
		        						message = message +"\n"+String.format(ctx.getString(R.string.messagenotification), unread);
		        					else
		        						message = message +"\n"+String.format(ctx.getString(R.string.messagenotificationp), unread);
		        				}
		    					b.putString("Message", message);
		    					b.putInt("Gravity", Gravity.BOTTOM);
		    					msg.setData(b);
		    					msg.what = Beintoo.LOGIN_MESSAGE;
		    					Beintoo.UIhandler.sendMessage(msg);
		    				}
	    				}catch (Exception e){e.printStackTrace(); Beintoo.LAST_LOGIN = new AtomicLong(0);}
	    				
	    				if(listener != null) listener.onComplete(loginPlayer);
	    				
	    				LocationMManager.savePlayerLocation(currentContext);
	    				if(Beintoo.userAgent == null){
	    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.UPDATE_USER_AGENT);
	    				}
	    			}catch (Exception e){
	    				e.printStackTrace();
	    				if(listener != null) listener.onError();
	    				Beintoo.LAST_LOGIN = new AtomicLong(0);
	    			}
	    		}    			
    		}    		
		});	
	}
	
	public void logout(Context ctx){
		try {			
			PreferencesHandler.saveBool("isLogged", false, ctx);
			Beintoo.LAST_LOGIN = new AtomicLong(0);
			String guid = Current.getCurrentPlayer(ctx).getGuid();
			String key = guid+":count";
			PreferencesHandler.clearPref(key, ctx);
			PreferencesHandler.clearPref("currentPlayer", ctx);
			PreferencesHandler.clearPref("SubmitScoresCount", ctx);
		}catch(Exception e){ e.printStackTrace(); }
	}
	
	public PlayerScore getPlayerScore(final Context ctx, final String codeID){
		try {
			PlayerScore p = null;
			BeintooPlayer player = new BeintooPlayer();
			String guid = Current.getCurrentPlayer(ctx).getGuid();
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
	
	public void getPlayerScoreAsync(final Context ctx, final String codeID, final BScoreListener listener){
		SerialExecutor executor = SerialExecutor.getInstance();
		executor.execute(new Runnable(){     					
    		public void run(){	
				try {
					PlayerScore p = null;
					BeintooPlayer player = new BeintooPlayer();
					String guid = Current.getCurrentPlayer(ctx).getGuid();
					if(codeID != null)
						p = player.getScoreForContest(guid, codeID);
					else
						p = player.getScore(guid);
					
					listener.onComplete(p);
					  
				} catch (Exception e) {						
					listener.onBeintooError(e);
					e.printStackTrace();
				}				
    		}
		});
	}
}
