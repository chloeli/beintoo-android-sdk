package com.beintoo.main.managers;

import com.beintoo.beintoosdk.BeintooPlayer;
import com.beintoo.beintoosdk.BeintooUser;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.beintoosdkutility.SerialExecutor;
import com.beintoo.main.Beintoo.BCreateUserListener;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;

import android.content.Context;

public class UserManager {
	private static Context currentContext;
	
	public UserManager (Context ctx){
		currentContext = ctx;
	}
	
	public void createUser(final String email, final String nickname, final String password,
			final String name, final String address, final String country, final Boolean sendGreetingsEmail, final String imageUrl, final BCreateUserListener callback){
		SerialExecutor executor = SerialExecutor.getInstance();
		executor.execute(new Runnable(){     					
    		public void run(){
    			
	    			try{	    				
	    				Player player = Current.getCurrentPlayer(currentContext);
	    				if(player == null){
	    					BeintooPlayer bp = new BeintooPlayer();
	    					player = bp.playerLogin(null, null, null, DeviceId.getUniqueDeviceId(currentContext), null, null);
	    					BeintooUser bu = new BeintooUser();	    					
	    					User u = bu.setOrUpdateUser("set", player.getGuid(), null, email, nickname, password, name, address, country, null, sendGreetingsEmail, imageUrl, null);
	    					if(u != null){
	    						player = Current.attachUserToPlayerAndSetCurrentPlayer(currentContext, u, player);
	    						PreferencesHandler.saveBool("isLogged", true, currentContext);
	    						
	    						if(callback != null)
	    							callback.onComplete(player);
	    					}
	    				}else if(player.getUser() == null){
	    					BeintooUser bu = new BeintooUser();
	    					User u = bu.setOrUpdateUser("set", player.getGuid(), null, email, nickname, password, name, address, country, null, sendGreetingsEmail, imageUrl, null);
	    					if(u != null){
	    						player = Current.attachUserToPlayerAndSetCurrentPlayer(currentContext, u, player);
	    						PreferencesHandler.saveBool("isLogged", true, currentContext);
	    						
	    						if(callback != null)
	    							callback.onComplete(player);
	    					}
	    				}	    				
	    			}catch (Exception e){
	    				if(callback != null)
	    					callback.onError();
	    				e.printStackTrace();
	    			}
	    		}    				
		});	
	}
	
}
