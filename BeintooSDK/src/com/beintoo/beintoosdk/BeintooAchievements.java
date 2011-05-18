package com.beintoo.beintoosdk;

import java.lang.reflect.Type;
import java.util.List;

import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.beintoosdkutility.PostParams;
import com.beintoo.wrappers.AchievementWrap;
import com.beintoo.wrappers.PlayerAchievement;
import com.google.beintoogson.Gson;
import com.google.beintoogson.reflect.TypeToken;

public class BeintooAchievements {
	String apiPreUrl = null;
	
	public BeintooAchievements() {
		if(!BeintooSdkParams.useSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}
	
	/**
	 * Returns the list of available achievements for the current app
	 * 
	 * @return a list of AchievementWrap
	 */
	public List<AchievementWrap> getAppAchievements(){
		String apiUrl = apiPreUrl+"achievement";
				
		//Set the auth request header
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		Gson gson = new Gson();
		
        Type mapType = new TypeToken<List<AchievementWrap>>() {
        }.getType();
        List<AchievementWrap> achievements = gson.fromJson(json,mapType);
		
        return achievements;
	}
	
	/**
	 * Returns the list of available achievements for the user
	 * 
	 * @return a list of AchievementWrap
	 */
	public List<PlayerAchievement> getUserAchievements(String userExt){
		String apiUrl = apiPreUrl+"achievement/";
				
		//Set the auth request header
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("userExt");
		header.getValue().add(userExt);
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		Gson gson = new Gson();
		
        Type mapType = new TypeToken<List<PlayerAchievement>>() {
        }.getType();
        List<PlayerAchievement> achievements = gson.fromJson(json,mapType);
		 
        return achievements;
	} 
	
	/**
	 * Submit a percentage or value of the achievement for a user
	 * 
	 * @param userExt the unique userID
	 * @param achievement the achievement id
	 * @param percentage (optional) percentage of the game or level
	 * @param value (optional) number of objectives conquered
	 * @return a PlayerAchievement object
	 */
	public List<PlayerAchievement> submitUserAchievement(String userExt, String achievement, Float percentage, Float value){
		String apiUrl = apiPreUrl+"achievement/"+achievement;
				
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("userExt");
		header.getValue().add(userExt);
		
		PostParams post = new PostParams();
		
		if(percentage != null){
			post.getKey().add("percentage");
			post.getValue().add(Float.toString(percentage));	
		}
		
		if(value != null){
			post.getKey().add("value");
			post.getValue().add(Float.toString(value));	
		}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, post, true);
		
		Gson gson = new Gson();
		
		Type mapType = new TypeToken<List<PlayerAchievement>>() {
        }.getType();
        List<PlayerAchievement> achievements = gson.fromJson(json,mapType);
		
        return achievements;
	}
}
