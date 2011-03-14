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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.beintoosdkutility.GetParams;
import com.beintoo.beintoosdkutility.HeaderParams;

public class BeintooConnection {
	
	/**
	 * This is the main HTTREQUEST method
	 * 
	 * @param apiurl the url of the called api
	 * @param header header params 
	 * @param get get params (NOT USED)
	 * @return the json object returned by the api
	 */
	public String httpRequest(String apiurl,HeaderParams header,GetParams get, boolean isPost){
		// DEBUG
		DebugUtility.showLog(apiurl);
		URL url;
		//URLConnection postUrlConnection;
		try {
			String getParams = "";
			
			if(get != null)
				for(int i = 0; i<get.getKey().size(); i++){
					if(i==0)
						getParams = "?"+get.getKey().get(i)+"="+get.getValue().get(i);
					else
						getParams = "&"+get.getKey().get(i)+"="+get.getValue().get(i);
				}
			url = new URL(apiurl+getParams);
			HttpURLConnection postUrlConnection = (HttpURLConnection) url.openConnection();
			if(isPost)
				postUrlConnection.setRequestMethod("POST");
			postUrlConnection.setUseCaches(false);
			for(int i = 0; i<header.getKey().size(); i++){
				postUrlConnection.setRequestProperty(header.getKey().get(i),header.getValue().get(i));
			}
			
		
			InputStream postInputStream = postUrlConnection.getInputStream();
	
			BufferedReader postBufferedReader = new BufferedReader(new
			InputStreamReader(postInputStream),10*1024);
			String postline = null;
			String jsonString = "";
			
			while((postline = postBufferedReader.readLine()) != null) {
				DebugUtility.showLog(postline);
				jsonString = jsonString + postline;
			}
			
			return jsonString;
			
		} catch (IOException e) {			
			e.printStackTrace();
			return "{\"messageID\":0,\"message\":\"ERROR\",\"kind\":\"message\"}";
		}
	}
	
	public String httpRequest(String apiurl,HeaderParams header,GetParams get){
		return httpRequest(apiurl, header, get, false);
	}
	
	/**
	 * Check if is active the internet connection
	 * @param ctx current application Context
	 * @return a boolean that return the connection state
	 */
	public static boolean isOnline(Context ctx) {
	     ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	     if (connectivity != null) {
	        NetworkInfo[] info = connectivity.getAllNetworkInfo();
	        if (info != null) {
	           for (int i = 0; i < info.length; i++) {
	              if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	                 return true;
	              }
	           }
	        }
	     }
	     return false;
	}
}
