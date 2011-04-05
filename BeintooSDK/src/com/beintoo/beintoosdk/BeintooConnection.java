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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.beintoo.beintoosdkutility.DebugUtility;
import com.beintoo.beintoosdkutility.PostParams;
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
	public String httpRequest(String apiurl,HeaderParams header,PostParams post, boolean isPost){

		// DEBUG		
		DebugUtility.showLog(apiurl);
		
		URL url;
		
		try {
			String postParams = "";
			
			if(post != null)
				for(int i = 0; i<post.getKey().size(); i++){
					if(i==0)
						postParams = postParams + post.getKey().get(i)+"="+URLEncoder.encode(post.getValue().get(i), "UTF-8");
					else
						postParams = postParams + "&"+post.getKey().get(i)+"="+URLEncoder.encode(post.getValue().get(i), "UTF-8");
				}
			
			url = new URL(apiurl);
			
			System.setProperty("http.keepAlive", "false");
			
			HttpsURLConnection postUrlConnection = (HttpsURLConnection) url.openConnection();
			
			postUrlConnection.setUseCaches(false);
			postUrlConnection.setDoOutput(true);
			postUrlConnection.setDoInput(true);

			for(int i = 0; i<header.getKey().size(); i++){
				postUrlConnection.setRequestProperty(header.getKey().get(i),header.getValue().get(i));
			}
			
			if(isPost){
				postUrlConnection.setRequestMethod("POST");
				postUrlConnection.setRequestProperty("Content-Length", "" + 
			               Integer.toString(postParams.getBytes().length));
				DataOutputStream wr = new DataOutputStream (
						postUrlConnection.getOutputStream ());
			      wr.writeBytes (postParams);
			      wr.flush ();
			      wr.close ();				
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
	
	public String httpRequest(String apiurl,HeaderParams header,PostParams get){
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
