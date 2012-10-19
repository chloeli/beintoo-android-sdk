package com.beintoo.beintoosdk;

import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.main.Beintoo;
import com.beintoo.wrappers.Vgood;
import com.google.beintoogson.Gson;

public class BeintooDisplay {
	String apiPreUrl = null;
	
	public BeintooDisplay() {
		if(!BeintooSdkParams.internalSandbox)
			apiPreUrl = BeintooSdkParams.displayUrl;
		else
			apiPreUrl = BeintooSdkParams.displaySandboxUrl;
	}
	
	public Vgood getAd(String guid, String deviceUUID, String imei, String macaddress, String carrier,
			String codeID, String latitude, String longitude, String radius, String developerUserGuid) {
		
		String apiUrl = apiPreUrl+"display/serve/?wrapInJson=true";
		  
		if(latitude != null) apiUrl = apiUrl + "&latitude="+latitude;
		if(longitude != null) apiUrl = apiUrl + "&longitude="+longitude;
		if(radius != null) apiUrl = apiUrl + "&radius="+radius;
		if(developerUserGuid != null) apiUrl = apiUrl + "&developer_user_guid="+developerUserGuid;
		
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		if(guid != null){
			header.getKey().add("guid");
			header.getValue().add(guid);
		}		
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}		
		if(deviceUUID != null){
			header.getKey().add("deviceUUID");
			header.getValue().add(deviceUUID);			
		}
		if(imei != null){
			header.getKey().add("imei");
			header.getValue().add(imei);
		}
		if(macaddress != null){
			header.getKey().add("macaddress");
			header.getValue().add(macaddress);			
		}
		if(carrier != null){
			header.getKey().add("carrier");
			header.getValue().add(carrier);			
		}
				
		try {
		// ADD THE USER AGENT
			String userAgent = Beintoo.userAgent;
			if(userAgent != null){
				header.getKey().add("User-Agent");
				header.getValue().add(userAgent);
			}
		}catch (Exception e){e.printStackTrace();}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
				
		Vgood vgoods = new Gson().fromJson(json, Vgood.class);
		
		return vgoods;
	}
}
