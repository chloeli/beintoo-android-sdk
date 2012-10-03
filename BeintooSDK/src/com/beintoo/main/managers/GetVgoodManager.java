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
package com.beintoo.main.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.location.Location;
import android.widget.LinearLayout;

import com.beintoo.beintoosdk.BeintooDisplay;
import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkutility.ApiCallException;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.SerialExecutor;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BDisplayAdListener;
import com.beintoo.main.Beintoo.BEligibleVgoodListener;
import com.beintoo.main.Beintoo.BGetVgoodListener;
import com.beintoo.main.Beintoo.BIsRewardCoveredListener;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.Vgood;
import com.beintoo.wrappers.VgoodChooseOne;
import com.beintoo.wrappers.VgoodCovered;

public class GetVgoodManager {
	private static AtomicLong LAST_OPERATION = new AtomicLong(0);
	private static AtomicLong LAST_OPERATION_ELIGIBLE = new AtomicLong(0);
	private long OPERATION_TIMEOUT = 5000;
	
	private Context currentContext;
	
	public GetVgoodManager (Context ctx) {
		currentContext = ctx;		
	}
	
	public void GetVgood(final String codeID, final boolean isMultiple, final LinearLayout container, final int notificationType, final BGetVgoodListener listener){
		try {			
			SerialExecutor executor = SerialExecutor.getInstance();	
    		executor.execute(new Runnable(){     					
	    		public void run(){
	    			try{			
	    				final Player currentPlayer = Current.getCurrentPlayer(currentContext);	    				
	    				if(currentPlayer.getGuid() == null) return;
	    				
	    				Long currentTime = System.currentTimeMillis();
	    				Location pLoc = LocationMManager.getSavedPlayerLocation(currentContext);
	    	        	if(pLoc != null){
	    	        		if((currentTime - pLoc.getTime()) <= 900000){ // TEST 20000 (20 seconds)
	    	        			// GET A VGOOD WITH COORDINATES
						    	getVgoodHelper(codeID, isMultiple,container,notificationType,pLoc,currentPlayer, listener);
	    	        		}else {
	    	        			// GET A VGOOD WITHOUT COORDINATES
			    				getVgoodHelper(codeID, isMultiple,container,notificationType,null,currentPlayer, listener);
			    				LocationMManager.savePlayerLocation(currentContext);
	    	        		}
	    	        	}else{
	    	        		getVgoodHelper(codeID, isMultiple,container,notificationType,null,currentPlayer, listener);
		    				LocationMManager.savePlayerLocation(currentContext);
	    	        	}
	    			}catch(Exception e){e.printStackTrace();}
	    		}
    		});
		}catch (Exception e){
			e.printStackTrace();
		} 
	}
	
	private void getVgoodHelper (String codeID, boolean isMultiple,final LinearLayout container, 
			final int notificationType, Location l, Player currentPlayer, final BGetVgoodListener listener){
		synchronized (LAST_OPERATION){
			try {
				Beintoo.gvl = listener;
				
				if(System.currentTimeMillis() < LAST_OPERATION.get() + OPERATION_TIMEOUT)
					return;
				
				final BeintooVgood vgoodHand = new BeintooVgood();
				//boolean isBanner = false;
				if(!isMultiple) { // ASSIGN A SINGLE VGOOD TO THE PLAYER
					if(l != null){				
						VgoodChooseOne v = vgoodHand.getVgoodList(currentPlayer.getGuid(),
								DeviceId.getUniqueDeviceId(currentContext),
								DeviceId.getImei(currentContext), 
								DeviceId.getMACAddress(currentContext),
								1, codeID, Double.toString(l.getLatitude()), 
								Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), 
								false);
						
						if(v.getVgoods() != null && v.getVgoods().get(0) != null)
							Beintoo.vgood = v.getVgoods().get(0);
					}else {			
						VgoodChooseOne v = vgoodHand.getVgoodList(currentPlayer.getGuid(), 
								DeviceId.getUniqueDeviceId(currentContext),
								DeviceId.getImei(currentContext), 
								DeviceId.getMACAddress(currentContext), 1,
								codeID, null, null, null, false);
						
						if(v.getVgoods() != null && v.getVgoods().get(0) != null)
							Beintoo.vgood = v.getVgoods().get(0);
					}
					
			    	// ADD THE SINGLE VGOOD TO THE LIST
			    	List<Vgood> list = new ArrayList<Vgood>();
			    	list.add(Beintoo.vgood);
			    	VgoodChooseOne v = new VgoodChooseOne(list);	    				    	
			    	Beintoo.vgoodlist = v;
			    	
			    	if(Beintoo.vgood.getName() != null){
			    		if(notificationType == Beintoo.VGOOD_NOTIFICATION_BANNER){
			    			Beintoo.vgood_container = container;
			    			if(!Beintoo.vgood.isBanner()){ // NORMAL VGOOD REWARD
			    				Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_BANNER);
			    			}else{ // BEINTOO RECOMMENDATION
			    				//isBanner = true;
			    				if(Beintoo.vgood.getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER);
			    				else // BEINTOO RECOMMENDATION WITH HTML CONTENT
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER_HTML);
			    			}
			    		}else{
			    			
			    			if(!Beintoo.vgood.isBanner()){ // NORMAL VGOOD REWARD
			    				Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_ALERT);
			    			}else{ // BEINTOO RECOMMENDATION
			    				//isBanner = true;
			    				if(Beintoo.vgood.getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT);
			    				else // BEINTOO RECOMMENDATION WITH HTML CONTENT
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT_HTML);
			    			}
			    		}
			    	}
		    	}else { // ASSIGN A LIST OF VGOOD TO THE PLAYER
		    		if(l != null){
		    			Beintoo.vgoodlist = vgoodHand.getVgoodList(currentPlayer.getGuid(), 
		    					DeviceId.getUniqueDeviceId(currentContext),
		    					DeviceId.getImei(currentContext), 
		    					DeviceId.getMACAddress(currentContext),
		    					3, codeID, Double.toString(l.getLatitude()), 
		    					Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), false);
		    		}else {
		    			Beintoo.vgoodlist = vgoodHand.getVgoodList(currentPlayer.getGuid(), 
		    					DeviceId.getUniqueDeviceId(currentContext),
		    					DeviceId.getImei(currentContext), 
		    					DeviceId.getMACAddress(currentContext),
		    					3, codeID, null, null,null, false);
		    		}
			    	if(Beintoo.vgoodlist != null){
			    		// CHECK IF THERE IS MORE THAN ONE VGOOD AVAILABLE
		    			if(notificationType == Beintoo.VGOOD_NOTIFICATION_BANNER){
		    				Beintoo.vgood_container = container;
		    				if(!Beintoo.vgoodlist.getVgoods().get(0).isBanner()){ // NORMAL VGOOD REWARD
		    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_BANNER);
		    				}else{ // BEINTOO RECOMMENDATION
		    					//isBanner = true;
		    					if(Beintoo.vgoodlist.getVgoods().get(0).getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER);
		    					else // BEINTOO RECOMMENDATION WITH HTML CONTENT
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER_HTML);
		    				}
		    			}else{
		    				if(!Beintoo.vgoodlist.getVgoods().get(0).isBanner()){
		    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_ALERT);
		    				}else{ // BEINTOO RECOMMENDATION
		    					//isBanner = true;
		    					if(Beintoo.vgoodlist.getVgoods().get(0).getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT);
		    					else // BEINTOO RECOMMENDATION WITH HTML CONTENT
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT_HTML);
		    				}
		    			}
			    	}
		    	}
				
				/*if(listener != null)
					listener.onComplete(Beintoo.vgoodlist);*/
				
				LAST_OPERATION.set(System.currentTimeMillis());
				
			}catch(ApiCallException e){	
				try {
					if(e.getId() == -11){
						if(listener != null)
							listener.isOverQuota();
					}else if(e.getId() == -10 || e.getId() == -21){
						if(listener != null)
							listener.nothingToDispatch();
					}else{
						if(listener != null)
							listener.onError();
					}					 
				}catch (Exception ex){ if(listener!= null) listener.onError(); }
			}
		}			
	}
	
	public void isVgoodCovered(final BIsRewardCoveredListener listener){
		try {			
			SerialExecutor executor = SerialExecutor.getInstance();	
    		executor.execute(new Runnable(){     					
	    		public void run(){
	    			try{			
	    				final Player currentPlayer = Current.getCurrentPlayer(currentContext);	    				
	    				if(currentPlayer.getGuid() == null) return;
	    				
	    				Long currentTime = System.currentTimeMillis();
	    				Location pLoc = LocationMManager.getSavedPlayerLocation(currentContext);
	    	        	if(pLoc != null){
	    	        		if((currentTime - pLoc.getTime()) <= 900000){ // TEST 20000 (20 seconds)
	    	        			// GET A VGOOD WITH COORDINATES
	    	        			isVgoodCoveredHelper(pLoc, listener);
	    	        		}else {
	    	        			// GET A VGOOD WITHOUT COORDINATES
	    	        			isVgoodCoveredHelper(null, listener);
			    				LocationMManager.savePlayerLocation(currentContext);
	    	        		}
	    	        	}else{
	    	        		isVgoodCoveredHelper(null, listener);
		    				LocationMManager.savePlayerLocation(currentContext);
	    	        	}
	    			}catch(Exception e){e.printStackTrace();}
	    		}
    		});
		}catch (Exception e){
			e.printStackTrace();
		} 
	}
	
	private void isVgoodCoveredHelper(final Location location, final BIsRewardCoveredListener listener){
		try {
			VgoodCovered msg = null;
			BeintooVgood bv = new BeintooVgood();		
			if(location != null){
				msg = bv.isVgoodCovered(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()),
						Double.toString(location.getAccuracy()));
			}else{
				msg = bv.isVgoodCovered(null, null, null);
			}
			
			if(msg != null){
				if(msg.isCovered())
					listener.onCovered();				
				if(msg.isSpecialAvailable())
					listener.onSpecialCampaignCovered();
			}
		}catch(Exception e){
			e.printStackTrace();
			listener.onError();
		}
	}
	
	
	public void requestAd(final String developerUserGuid, final String codeID, final boolean display, final BDisplayAdListener listener){
		SerialExecutor executor = SerialExecutor.getInstance();	
		executor.execute(new Runnable(){     					
    		public void run(){
    			try{    				
    				final Player currentPlayer = Current.getCurrentPlayer(currentContext);
    				String guid = null;
    				if(currentPlayer != null)
    					guid = currentPlayer.getGuid();
					Long currentTime = System.currentTimeMillis();
					Location l = LocationMManager.getSavedPlayerLocation(currentContext);
		        	if(l != null){
		        		if((currentTime - l.getTime()) <= 900000){ // TEST 20000 (20 seconds)
		        			getAdHelper(developerUserGuid, codeID, guid, l, display, listener);
		        		}else {	        		
		        			getAdHelper(developerUserGuid, codeID, guid, null, display, listener);
		    				LocationMManager.savePlayerLocation(currentContext);
		        		}
		        	}else{
		        		getAdHelper(developerUserGuid, codeID, guid, null, display, listener);
		        		LocationMManager.savePlayerLocation(currentContext);
		        	}
    			}catch(Exception e){
    				e.printStackTrace();
    			}
    		}
    	});
	}
	
	private void getAdHelper(final String developerUserGuid, final String codeID, final String guid, final Location l, 
			final boolean displayAd, final BDisplayAdListener listener){
		synchronized (LAST_OPERATION){
			try {					
				if(System.currentTimeMillis() < LAST_OPERATION.get() + OPERATION_TIMEOUT)
					return;
				
				final BeintooDisplay display = new BeintooDisplay();
				
				 // ASSIGN A LIST OF VGOOD TO THE PLAYER
	    		if(l != null){ 	    			
	    			Beintoo.ad = display.getAd(guid, 
	    					DeviceId.getUniqueDeviceId(currentContext),
	    					DeviceId.getImei(currentContext), 
	    					DeviceId.getMACAddress(currentContext),
	    					DeviceId.getNetworkOperator(currentContext),
	    					codeID, Double.toString(l.getLatitude()), 
	    					Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), developerUserGuid);
	    		}else {
	    			Beintoo.ad = display.getAd(guid, 
	    					DeviceId.getUniqueDeviceId(currentContext),
	    					DeviceId.getImei(currentContext), 
	    					DeviceId.getMACAddress(currentContext),
	    					DeviceId.getNetworkOperator(currentContext),
	    					codeID, null, null,null, developerUserGuid);
	    		}
	    		
	    		List<Vgood> list = new ArrayList<Vgood>();
		    	list.add(Beintoo.ad);
		    	VgoodChooseOne v = new VgoodChooseOne(list);	    				    	
		    	Beintoo.adlist = v;
	    		
		    	if(Beintoo.ad != null){		    		
		    		if(displayAd)
		    			Beintoo.UIhandler.sendEmptyMessage(Beintoo.REQUEST_AND_DISPLAY_AD);
		    		else
		    			Beintoo.UIhandler.sendEmptyMessage(Beintoo.REQUEST_AD);
		    	}else{
		    		if(listener != null)
						listener.onNoAd();
		    	}			    
				LAST_OPERATION.set(System.currentTimeMillis());			
			}catch(Exception e){
				if(listener != null)
					listener.onError();
				e.printStackTrace();
			}
		}	
	}
	
	public void isEligibleForVgood(final Context ctx, final BEligibleVgoodListener el){
		SerialExecutor executor = SerialExecutor.getInstance();	
		executor.execute(new Runnable(){     					
	    		public void run(){    			   
    				synchronized (LAST_OPERATION_ELIGIBLE){
    					try {
    						
						if(System.currentTimeMillis() < LAST_OPERATION_ELIGIBLE.get() + OPERATION_TIMEOUT)
							return;
						
						final Player p = Current.getCurrentPlayer(ctx);
	    				final BeintooVgood vgooddispatcher = new BeintooVgood();
	    				
	    				if(p.getGuid() == null) return;
	    				
	    				Location location = LocationMManager.getSavedPlayerLocation(ctx);
	    				Long currentTime = System.currentTimeMillis();
	    				if(location != null && (currentTime - location.getTime()) < 900000){ // SAVED AND UP TO DATE LOCATION
	    					com.beintoo.wrappers.Message msg = null;
	    					try {
	    						msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, Double.toString(location.getLatitude()), 
	    						Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()));
	    						
	    						checkVgoodEligibility(msg, p,el);
	    					}catch (ApiCallException e){
	    						msg = new com.beintoo.wrappers.Message();
	    						msg.setMessage(e.getError());
	    						msg.setMessageID(e.getId());
	    						checkVgoodEligibility(msg, p,el);
	    					}
	    				}else{ // NO LOCATION AVAILABLE
    			    		com.beintoo.wrappers.Message msg = null;
    			    		try {
    			    			msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, null, 
		    						null, null);
    			    			checkVgoodEligibility(msg, p,el);
    			    			
    			    			LocationMManager.savePlayerLocation(ctx);
    			    		}catch (ApiCallException e){
    			    			msg = new com.beintoo.wrappers.Message();
	    						msg.setMessage(e.getError());
	    						msg.setMessageID(e.getId());
	    						checkVgoodEligibility(msg, p,el);
    			    		} 
    			    	}	
    				
	    				
	    				LAST_OPERATION_ELIGIBLE.set(System.currentTimeMillis());
	    				
	    			}catch(Exception e){e.printStackTrace();el.onError();}
    				}
	    		}
			});		    		
	}
	
	private void checkVgoodEligibility(com.beintoo.wrappers.Message msg, Player p, BEligibleVgoodListener el){
		el.onComplete(msg, p);
		if(msg.getMessageID() == 0){
			el.vgoodAvailable(p);	    						 
		}else if(msg.getMessageID() == -11){
			el.isOverQuota(p);
		}else if(msg.getMessageID() == -10 || msg.getMessageID() == -21){
			el.nothingToDispatch(p);
		}else{
			el.onError();
		}
	}
}
