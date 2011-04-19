package com.beintoo.beintoosdk;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.beintoosdkutility.PostParams;
import com.beintoo.wrappers.Message;
import com.beintoo.wrappers.UsersMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BeintooMessages {
	final public static String READ = "READ";
	final public static String UNREAD = "UNREAD";
	
	String apiPreUrl = null;
	
	public BeintooMessages() {
		if(!BeintooSdkParams.useSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}
	
	public Message sendMessage(String from, String to, String text){
		String apiUrl = apiPreUrl+"message";
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		PostParams post = new PostParams();
		
		if(text != null && from != null && to != null){
			post.getKey().add("from");
			post.getValue().add(from);
			post.getKey().add("to");
			post.getValue().add(to);
			post.getKey().add("text");
			post.getValue().add(text);
		}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, post,true);		 
		
		return new Gson().fromJson(json, Message.class);		
	}
	
	public List<UsersMessage> getUserMessages(String to, String status){
		String apiUrl = apiPreUrl+"message";
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);		
		header.getKey().add("to");
		header.getValue().add(to);
		header.getKey().add("status");
		header.getValue().add(status);
		
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);

		List<UsersMessage> messages = new ArrayList<UsersMessage>();
		Type type = new TypeToken<List<UsersMessage>>() {}.getType();        
		messages = new Gson().fromJson(json, type);
		
		return messages;
	}
	
	public UsersMessage markAsRead (String messageID, String status){
		String apiUrl = apiPreUrl+"message/"+messageID;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		PostParams post = new PostParams();
		post.getKey().add("status");
		post.getValue().add(status);		
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, post,true);		 
		
		return new Gson().fromJson(json, UsersMessage.class);	
	}
	
	public UsersMessage markAsArchived (String messageID, String status){
		String apiUrl = apiPreUrl+"message/"+messageID;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		PostParams post = new PostParams();
		post.getKey().add("archived");
		post.getValue().add(status);		
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, post,true);		 
		
		return new Gson().fromJson(json, UsersMessage.class);	
	}
	
}
