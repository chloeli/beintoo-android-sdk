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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import android.net.Uri;

import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.PostParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.wrappers.Contest;
import com.beintoo.wrappers.LeaderboardContainer;
import com.google.beintoogson.Gson;
import com.google.beintoogson.reflect.TypeToken;

public class BeintooApp {
	String apiPreUrl = null;
	
	public BeintooApp() {
		if(!BeintooSdkParams.internalSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}
	
	/**
	 * @return Returns a map of the most delivered vgood with you application and the number those 
	 * have been erogated. This call can be very useful because you have visibility on the 
	 * vgoods which are generating more revenues for you. You could decide for example to 
	 * provide a functional advantage to users who converted one of these goods, 
	 * for example giving him more energy in case of a game.
	 * 
	 * @param codeID (Optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 */
	
	public void topVGood(String codeID){
		@SuppressWarnings("unused")
		String apiUrl = apiPreUrl+"app/topvgood";
		//Set the auth request header
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
	} 
	
	public void topVGood(){
		topVGood(null);
	}
		
	/**
	 * Returns the leaderboard for the current app and the position of the current user 
	 * 
	 * @param userExt
	 * @param codeID
	 * @param start
	 * @param rows
	 * @return
	 */
	public Map<String, LeaderboardContainer> getLeaderboard(String userExt, String kind, String codeID, Integer start, Integer rows){
		
		Uri.Builder apiUrl = Uri.parse(apiPreUrl+"app/leaderboard/").buildUpon();
		
		if(rows != null && start != null){
			apiUrl.appendQueryParameter("start", start.toString());
			apiUrl.appendQueryParameter("rows", rows.toString());
		}
		
		if(kind != null){
			apiUrl.appendQueryParameter("kind", kind);
		}
		
		//Set the auth request header
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);		
		
		if(userExt != null){
			header.getKey().add("userExt");
			header.getValue().add(userExt);
		}
		
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl.build().toString(), header, null);
		
		Gson gson = new Gson();
		
        Type mapType = new TypeToken<Map<String, LeaderboardContainer>>() {
        }.getType();
        Map<String, LeaderboardContainer> leaders = gson.fromJson(json,mapType);
		
        return leaders;
	}
	
	/**
	 * Returns the contests for the current app
	 * 
	 * @param codeID the contest id
	 * @param onlypublic if true retunrs only public contests
	 * @param userExt the userExt of the current user, if provided returns only the contests of its friends
	 * @return a list of contests
	 */
	public List<Contest> getContests(String codeID, boolean onlypublic, String userExt){
		String apiUrl = apiPreUrl+"app/contest/show/";
		if(onlypublic == false)
			apiUrl = apiUrl+"?onlyPublic=false";
		
		//Set the auth request header
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		if(userExt != null){
			header.getKey().add("userExt");
			header.getValue().add(userExt);			
		}
		
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		
		Gson gson = new Gson();
		
        Type mapType = new TypeToken<List<Contest>>() {}.getType();
        List<Contest> contests = gson.fromJson(json,mapType);
		
        return contests;
	}
	
	public List<Contest> getContests(){
		return getContests(null, true, null);
	}
	
	/**
	 * 
	 * 
	 * @param codeID
	 * @param level
	 * @param text
	 */
	public void ErrorReporting (String codeID, String level, String text){
		String apiUrl = apiPreUrl+"app/logging/";
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		
		PostParams post = new PostParams();
		
		post.getKey().add("sdk");
		post.getValue().add("android");
		
		if(codeID != null){
			post.getKey().add("codeID");
			post.getValue().add(codeID);
		}
		
		if(level != null){
			post.getKey().add("level");
			post.getValue().add(level);
		}
		
		if(text != null){
			post.getKey().add("text");
			post.getValue().add(text);
		}
		
		BeintooConnection conn = new BeintooConnection();
		conn.httpRequest(apiUrl, header, post,true);
	}
	
	
}
