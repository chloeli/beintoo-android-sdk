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

import com.beintoo.wrappers.Message;
import com.beintoo.wrappers.Vgood;
import com.google.gson.Gson;

public class BeintooVgood {
	String apiPreUrl = null;
	
	public BeintooVgood() {
		if(!BeintooSdkParams.useSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}

	/**
	 * Retrieve a vgood for the requested user
	 * 
	 * @param guid user guid
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @param privategood a bool to set if the vgood has to be private
	 * @return a json object with vgood data
	 */
	public Vgood getVgood(String guid, String codeID,String latitude, String longitude, String radius, boolean privategood) {
		
		String apiUrl = apiPreUrl+"vgood/get/byguid/"+guid+"/?";
		
		if(privategood == true) apiUrl = apiUrl + "private=true"; else apiUrl = apiUrl + "private=false";  
		if(latitude != null) apiUrl = apiUrl + "&latitude="+latitude;
		if(longitude != null) apiUrl = apiUrl + "&longitude="+longitude;
		if(radius != null) apiUrl = apiUrl + "&radius="+radius;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		header.getKey().add("guid");
		header.getValue().add(guid);
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		Gson gson = new Gson();
		Vgood vgood = new Vgood();
		vgood = gson.fromJson(json, Vgood.class);
		
		return vgood;
	}
	
	/**
	 * Returns an array with all the vgoods earned by the user
	 * 
	 * @param userExt usr_ext unique identifier
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @return a Vgood object array
	 */
	public Vgood [] showByUser(String userExt, String codeID){
		String apiUrl = apiPreUrl+"vgood/show/byuser/"+userExt;
		
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
		
		Vgood[] vgood = gson.fromJson(json, Vgood[].class);
		
		return vgood;
		
	}
	
	/**
	 * Send a Vgood as a gift from a user to another user
	 * 
	 * @param vgoodExt the vgood id 
	 * @param userExt the user id of the gift sender
	 * @param userExt_to the user id of the gift sender
	 * @param codeID (optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @return a Message object with the status of the sendAsAgift operation
	 */
	public Message sendAsAGift (String vgoodExt, String userExt, String userExt_to, String codeID){
			
		String apiUrl = apiPreUrl+"vgood/sendasgift/"+vgoodExt+"/"+userExt+"/"+userExt_to;
	
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
		
		Message msg = gson.fromJson(json, Message.class);
		
		return msg;	
	}

}