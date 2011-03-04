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

import com.beintoo.beintoosdkutility.BeintooSdkParams;
import com.beintoo.beintoosdkutility.HeaderParams;
import com.beintoo.wrappers.EntryCouplePlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BeintooApp {
	String apiPreUrl = null;
	
	public BeintooApp() {
		if(!BeintooSdkParams.useSandbox)
			apiPreUrl = BeintooSdkParams.apiUrl;
		else
			apiPreUrl = BeintooSdkParams.sandboxUrl;
	}
	
	/**
	 * @return Returns a map the most delivered vgood with you application and the number those 
	 * have been erogated. This call can be very useful because you have visibility on the 
	 * vgoods which are generating more revenues for you. You could decide for example to 
	 * provide a functional advantage to users who converted one of these goods, 
	 * for example giving him more energy in case of a game.
	 * 
	 * @param codeID (Optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 */
	
	public void TopVGood(String codeID){
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
	
	public void TopVGood(){
		TopVGood(null);
	}
	
	/**
	 * @return Returns a map with your players and the score of those.
	 * max is set to 50, default 20
	 * @param codeID (Optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @param rows the maximum number of results (max is set to 50, default 20).
	 */
	public Map<String, List<EntryCouplePlayer>> TopScore(String codeID, int rows){
		String apiUrl;
		if(rows != 0)
			apiUrl = apiPreUrl+"app/topscore?rows="+rows;
		else
			apiUrl = apiPreUrl+"app/topscore";
		
		//Set the auth request header
		HeaderParams header = new HeaderParams();
		header.getKey().add("apikey");
		header.getValue().add(DeveloperConfiguration.apiKey);
		if(codeID != null){
			header.getKey().add("codeID");
			header.getValue().add(codeID);
		}
		BeintooConnection conn = new BeintooConnection();
		String json = conn.httpRequest(apiUrl, header, null);
		System.out.println("json "+json);
		Gson gson = new Gson();
		
        Type mapType = new TypeToken<Map<String, List<EntryCouplePlayer>>>() {
        }.getType();
        Map<String, List<EntryCouplePlayer>> leaders = gson.fromJson(json,mapType);
		
		return leaders;
	}
	
	public void TopScore(String codeID){
		TopScore(codeID, 0);
	}
	
	public void TopScore(int rows){
		TopScore(null,rows);
	}
	
	/**
	 * Returns a map with your provided user and its friends and the score of those.
	 * @param codeID (Optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @param rows (Optional) the maximum number of results (max is set to 50, default 20).
	 * @param userExt the unique beintoo id of registered user.
	 */
	public Map<String, List<EntryCouplePlayer>> TopScoreByUserExt(String codeID, int rows, String userExt){
		String apiUrl;
		if(rows != 0)
			apiUrl = apiPreUrl+"app/topscore/"+userExt+"?rows="+rows;
		else
			apiUrl = apiPreUrl+"app/topscore/"+userExt;
		
		System.out.println(apiUrl);
		//Set the auth request header
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
		
        Type mapType = new TypeToken<Map<String, List<EntryCouplePlayer>>>() {
        }.getType();
        Map<String, List<EntryCouplePlayer>> leaders = gson.fromJson(json,mapType);
		
        return leaders;
	}
	/**
	 * Returns a map with your provided user and its friends and the score of those.
	 * Returns the default number of rows (20)
	 * @param codeID (Optional) a string that represents the position in your code. We will use it to indentify different api calls of the same nature.
	 * @param userExt the unique beintoo id of registered user.
	 * @see #TopScoreByUserExt(String codeID, int rows, String userExt)
	 */
	public void TopScoreByUserExt(String codeID, String userExt){
		TopScoreByUserExt(codeID, 0,userExt);
	}
	
	/**
	 * Returns a map with your provided user and its friends and the score of those.
	 * Returns the default codeID
	 * @param rows (Optional) the maximum number of results (max is set to 50, default 20).
	 * @param userExt the unique beintoo id of registered user.
	 * @see #TopScoreByUserExt(String codeID, int rows, String userExt)
	 */
	public void TopScoreByUserExt(int rows, String userExt){
		TopScoreByUserExt(null,rows,userExt);
	}
	
	/**
	 * Returns a map with your provided user and its friends and the score of those.
	 * Without any params, rows are default to 20 e codeID is default
	 * @param userExt the unique beintoo id of registered user.
	 * @see #TopScoreByUserExt(String codeID, int rows, String userExt)
	 */
	public void TopScoreByUserExt(String userExt){
		TopScoreByUserExt(null,0,userExt);
	}
	
	
}