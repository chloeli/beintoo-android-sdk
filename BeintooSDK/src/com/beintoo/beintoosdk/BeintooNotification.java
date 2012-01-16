package com.beintoo.beintoosdk;

import java.lang.reflect.Type;
import java.util.List;

import android.net.Uri;

import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.beintoosdkutility.PostParams;
import com.beintoo.wrappers.Message;
import com.beintoo.wrappers.Notification;
import com.google.beintoogson.Gson;
import com.google.beintoogson.reflect.TypeToken;

public class BeintooNotification {
	String apiPreUrl = null;
	
	public BeintooNotification() {
		if(!BeintooSdkParams.internalSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}
	
	public List<Notification> getNotifications(String guid, Integer start, Integer rows){
		Uri.Builder apiUrl = Uri.parse(apiPreUrl+"notification/").buildUpon();
		
		if(rows != null && start != null){
			apiUrl.appendQueryParameter("start", start.toString());
			apiUrl.appendQueryParameter("rows", rows.toString());
		}
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("guid");
		header.getValue().add(guid);
				
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl.toString(), header, null);		 
		
		Gson gson = new Gson();
		
        Type mapType = new TypeToken<List<Notification>>() {}.getType();
        List<Notification> notifications = gson.fromJson(json,mapType);
		
		return notifications; 		
	}
	
	public List<Notification> getNotifications(String guid){
		return getNotifications(guid, null, null);
	}
	
	public Message markAsRead(String guid, String notificationId){
		String apiUrl = apiPreUrl+"notification/"+notificationId;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("guid");
		header.getValue().add(guid);
		
		PostParams post = new PostParams();
		post.getKey().add("status");
		post.getValue().add("READ");
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, post,true);		 
		
		Gson gson = new Gson();		        
		Message msg = gson.fromJson(json, Message.class);
		
		return msg; 		
	}
	
	public Message markAllAsRead(String guid, String lastNotificationId){
		String apiUrl = apiPreUrl+"notification/upto/"+lastNotificationId;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("guid");
		header.getValue().add(guid);
		
		PostParams post = new PostParams();
		post.getKey().add("status");
		post.getValue().add("READ");
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, post,true);		 
		
		Gson gson = new Gson();		        
		Message msg = gson.fromJson(json, Message.class);
		
		return msg; 		
	}
}
