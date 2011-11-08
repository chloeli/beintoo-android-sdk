package com.beintoo.beintoosdkutility;

import android.content.Context;

import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.User;
import com.google.beintoogson.Gson;

public class Current {
	public static Player getCurrentPlayer(Context context){
		Player p = new Gson().fromJson(PreferencesHandler.getString("currentPlayer", context), Player.class);		
		return p;
	}
	
	public static String getCurrentPlayerJson(Context context){
		return PreferencesHandler.getString("currentPlayer", context);
	}
	
	public static User getCurrentUser(Context context){
		Player p = Current.getCurrentPlayer(context);
		if(p != null)
			return p.getUser();
		else 
			return null;
	}
	
	public static void setCurrentPlayer(Context context, Player p){
		PreferencesHandler.saveString("currentPlayer", new Gson().toJson(p), context);
	}
}
