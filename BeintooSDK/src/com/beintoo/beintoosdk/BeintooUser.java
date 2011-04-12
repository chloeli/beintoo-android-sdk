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
package com.beintoo.beintoosdk;

import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.wrappers.Challenge;
import com.beintoo.wrappers.Message;
import com.beintoo.wrappers.User;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class BeintooUser {
	String apiPreUrl = null;
	
	public BeintooUser() {
		if(!BeintooSdkParams.useSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}
	
	/**
	 * Returns an array of users from the same deviceUDID
	 * @param deviceUDID the requested device unique identifier
	 * @return a json array of users
	 */
	
	public User[] getUsersByDeviceUDID (String deviceUDID) {
		
		String apiUrl = apiPreUrl+"user/bydeviceUDID/"+deviceUDID;
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
	
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		Gson gson = new Gson();
		
		User[] users  = gson.fromJson(json, User[].class);
		
		return users;
	}
	
	/**
	 * Return a user by email and password
	 * @param email user email
	 * @param password user password
	 * @return json object of the requested user
	 */
	public User getUsersByEmail (String email, String password) {
		
		String apiUrl = apiPreUrl+"user/byemail";
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("email");
		header.getValue().add(email);
		header.getKey().add("password");
		header.getValue().add(password);

 		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		Gson gson = new Gson();
		User user = null;
		try { 
			user = gson.fromJson(json, User.class);
		} catch (final JsonParseException e) {  
			user = null;
		}
		 
		return user;
	}
	
	/**
	 * Returns challenges of the requested user 
	 * @param userExt usr_ext of the request user 
	 * @param status (path parameter) one of TO_BE_ACCEPTED, STARTED, ENDED. 
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @return
	 */
	public Challenge[] challengeShow (String userExt, String status, String codeID){
		String apiUrl = apiPreUrl+"user/challenge/show/"+userExt+"/"+status;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		return new Gson().fromJson(json, Challenge[].class);		
	}
	
	public Challenge[] challengeShow (String userExt, String status){
		return challengeShow(userExt, status, null);
	}
	
	/**
	 * Manage challenges, used for accept, invite and refuse challenges
	 * @param userExtFrom (path parameter) the unique beintoo id of registered user
	 * @param userExtTo (path parameter) the unique beintoo id of registered user
	 * @param action action (path parameter) one of INVITE, ACCEPT, REFUSE
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @return a json object of request challenges
	 * @throws Exception 
	 */
	public Challenge challenge (String userExtFrom, String userExtTo, String action, String codeID){
		String apiUrl = apiPreUrl+"user/challenge/"+userExtFrom+"/"+action+"/"+userExtTo;
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		Gson gson = new Gson();
		
		Challenge challenge = gson.fromJson(json, Challenge.class);
		
		/*if (challenge.getStatus() == null){
			Message msg = gson.fromJson(json, Message.class);
			if(msg.getMessageID() == -15)
				throw new Exception("-15");
		}*/
		
		return challenge;	
	}
	
	public Challenge challenge (String userExtFrom, String userExtTo, String action){
		return challenge (userExtFrom, userExtTo, action, null);
	}
	
	
	/**
	 * Get friends of a given user
	 *  
	 * @param userExt requested user
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature
	 * @return an array of Users
	 */
	public User[] getUserFriends(String userExt, String codeID){
		String apiUrl = apiPreUrl+"user/friend/"+userExt;
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		Gson gson = new Gson();
		
		User [] user = gson.fromJson(json, User[].class);
		
		return user;		
	}
	
	public Message detachUserFromDevice (String deviceID,String userID){
		String apiUrl = apiPreUrl+"user/removeUDID/"+deviceID+"/"+userID;
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		Gson gson = new Gson();
		
		Message msg = gson.fromJson(json, Message.class);
		
		return msg;		
	}

}
