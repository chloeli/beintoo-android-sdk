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
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.Looper;

import android.widget.LinearLayout;

import com.beintoo.beintoosdk.BeintooVgood;
import com.beintoo.beintoosdkutility.ApiCallException;
import com.beintoo.beintoosdkutility.JSONconverter;
import com.beintoo.beintoosdkutility.LocationManagerUtils;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.main.Beintoo;
import com.beintoo.main.Beintoo.BEligibleVgoodListener;
import com.beintoo.main.Beintoo.BGetVgoodListener;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.Vgood;
import com.beintoo.wrappers.VgoodChooseOne;

public class GetVgoodManager {
	
	private static Context currentContext;
	
	private static AtomicLong LAST_OPERATION = new AtomicLong(0);
	private static AtomicLong LAST_OPERATION_ELIGIBLE = new AtomicLong(0);
	private long OPERATION_TIMEOUT = 5000;
	
	public GetVgoodManager (Context ctx) {
		currentContext = ctx;		
	}
	
	public void GetVgood(final Context ctx, final String codeID, final boolean isMultiple, final LinearLayout container, final int notificationType, final BGetVgoodListener listener){
		try {
			final Player currentPlayer = JSONconverter.playerJsonToObject((PreferencesHandler.getString("currentPlayer", currentContext)));
			
			if(currentPlayer.getGuid() == null) return;

			final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
	    	if(LocationManagerUtils.isProviderSupported("network", locationManager) &&
	    			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    		new Thread(new Runnable(){     					
		    		public void run(){
		    			try{	
				    		Looper.prepare();
				    		final Timer t = new Timer();
							final LocationListener locationListener = new LocationListener() {
							    public void onLocationChanged(Location location) {
							    	locationManager.removeUpdates(this);
							    	t.cancel();
							    	final Location l = location;							    					    				
		    						// GET A VGOOD WITH COORDINATES
							    	getVgoodHelper(codeID, isMultiple,container,notificationType,l,currentPlayer, listener);
							    	LocationMManager.saveLocationHelper (ctx, location); 					    	
		    				    	Looper.myLooper().quit(); //QUIT THE THREAD
							    }
								public void onProviderDisabled(String provider) {}
				
								public void onProviderEnabled(String provider) {}
				
								public void onStatusChanged(String provider,int status, Bundle extras) {}
		    				}; 
							locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener); //NETWORK_PROVIDER
							LocationMManager.timeoutLocationManager(locationManager, locationListener, Looper.myLooper(),t); // START A TIMEOUT TIMER
							Looper.loop();
		    			}catch(Exception e){e.printStackTrace();}	
		    		}		
	    		}).start();
	    	}else {
	    		new Thread(new Runnable(){     					
		    		public void run(){
		    			try{			 
		            		// GET A VGOOD WITHOUT COORDINATES
		    				getVgoodHelper(codeID, isMultiple,container,notificationType,null,currentPlayer, listener);
		    			}catch(Exception e){e.printStackTrace();}
		    		}
				}).start();			
	    	}
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
				boolean isBanner = false;
				if(!isMultiple) { // ASSIGN A SINGLE VGOOD TO THE PLAYER
					if(l != null){
						Beintoo.vgood = vgoodHand.getVgood(currentPlayer.getGuid(), codeID, Double.toString(l.getLatitude()), 
							Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), false);
					}else {
						Beintoo.vgood = vgoodHand.getVgood(currentPlayer.getGuid(), codeID, null, 
								null,null, false);
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
			    				isBanner = true;
			    				if(Beintoo.vgood.getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER);
			    				else // BEINTOO RECOMMENDATION WITH HTML CONTENT
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER_HTML);
			    			}
			    		}else{
			    			
			    			if(!Beintoo.vgood.isBanner()){ // NORMAL VGOOD REWARD
			    				Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_ALERT);
			    			}else{ // BEINTOO RECOMMENDATION
			    				isBanner = true;
			    				if(Beintoo.vgood.getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT);
			    				else // BEINTOO RECOMMENDATION WITH HTML CONTENT
			    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT_HTML);
			    			}
			    		}
			    	}
		    	}else { // ASSIGN A LIST OF VGOOD TO THE PLAYER
		    		if(l != null){
		    			Beintoo.vgoodlist = vgoodHand.getVgoodList(currentPlayer.getGuid(), codeID, Double.toString(l.getLatitude()), 
							Double.toString(l.getLongitude()), Double.toString(l.getAccuracy()), false);
		    		}else {
		    			Beintoo.vgoodlist = vgoodHand.getVgoodList(currentPlayer.getGuid(), codeID, null, 
		    					null,null, false);
		    		}
			    	if(Beintoo.vgoodlist != null){
			    		// CHECK IF THERE IS MORE THAN ONE VGOOD AVAILABLE
		    			if(notificationType == Beintoo.VGOOD_NOTIFICATION_BANNER){
		    				Beintoo.vgood_container = container;
		    				if(!Beintoo.vgoodlist.getVgoods().get(0).isBanner()){ // NORMAL VGOOD REWARD
		    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_BANNER);
		    				}else{ // BEINTOO RECOMMENDATION
		    					isBanner = true;
		    					if(Beintoo.vgoodlist.getVgoods().get(0).getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER);
		    					else // BEINTOO RECOMMENDATION WITH HTML CONTENT
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_BANNER_HTML);
		    				}
		    			}else{
		    				if(!Beintoo.vgoodlist.getVgoods().get(0).isBanner()){
		    					Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_VGOOD_ALERT);
		    				}else{ // BEINTOO RECOMMENDATION
		    					isBanner = true;
		    					if(Beintoo.vgoodlist.getVgoods().get(0).getContentType() == null) // BEINTOO RECOMMENDATION WITH IMAGE
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT);
		    					else // BEINTOO RECOMMENDATION WITH HTML CONTENT
		    						Beintoo.UIhandler.sendEmptyMessage(Beintoo.GET_RECOMM_ALERT_HTML);
		    				}
		    			}
			    	}
		    	}
				
				if(listener != null && !isBanner)
					listener.onComplete(Beintoo.vgoodlist);
				
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
	
	public void isEligibleForVgood(final Context ctx, final BEligibleVgoodListener el){
		
    		new Thread(new Runnable(){     					
	    		public void run(){    			   
    				synchronized (LAST_OPERATION_ELIGIBLE){
    					try {
    						
						if(System.currentTimeMillis() < LAST_OPERATION_ELIGIBLE.get() + OPERATION_TIMEOUT)
							return;
						
						final Player p = JSONconverter.playerJsonToObject((PreferencesHandler.getString("currentPlayer", currentContext)));
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
	    					final LocationManager locationManager = (LocationManager) currentContext.getSystemService(Context.LOCATION_SERVICE);
	    			    	if(LocationManagerUtils.isProviderSupported("network", locationManager) &&
	    			    			locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){	    			    		
	    			    		Looper.prepare();
	    			    		final Timer t = new Timer();
	    			    		final LocationListener locationListener = new LocationListener() {
	    			    			public void onLocationChanged(Location location) {
	    			    				locationManager.removeUpdates(this);
	    			    				t.cancel();
	    			    				com.beintoo.wrappers.Message msg = null;
	    			    				try {
	    			    					msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, Double.toString(location.getLatitude()), 
				    						Double.toString(location.getLongitude()), Double.toString(location.getAccuracy()));
	    			    					
	    			    					checkVgoodEligibility(msg, p,el);
	    			    				}catch(ApiCallException e){
	    			    					msg = new com.beintoo.wrappers.Message();
	    		    						msg.setMessage(e.getError());
	    		    						msg.setMessageID(e.getId());
	    		    						checkVgoodEligibility(msg, p,el);
	    			    				}
	    			    				Looper.myLooper().quit();
	    			    			} 
	    			    			public void onProviderDisabled(String provider) {}
			
	    			    			public void onProviderEnabled(String provider) {}
			
	    			    			public void onStatusChanged(String provider,int status, Bundle extras) {}
	    			    		};   
	    			    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, Looper.myLooper());
	    			    		LocationMManager.timeoutLocationManager(locationManager, locationListener, Looper.myLooper(),t); // START A TIMEOUT TIMER
	    			    		Looper.loop();
	    			    	}else{ // LOCATION NOT AVAILABLE
	    			    		com.beintoo.wrappers.Message msg = null;
	    			    		try {
	    			    			msg = vgooddispatcher.isEligibleForVgood(p.getGuid(), null, null, 
			    						null, null);
	    			    			checkVgoodEligibility(msg, p,el);
	    			    		}catch (ApiCallException e){
	    			    			msg = new com.beintoo.wrappers.Message();
		    						msg.setMessage(e.getError());
		    						msg.setMessageID(e.getId());
		    						checkVgoodEligibility(msg, p,el);
	    			    		} 
	    			    	}	
	    				}
	    				
	    				LAST_OPERATION_ELIGIBLE.set(System.currentTimeMillis());
	    				
	    			}catch(Exception e){e.printStackTrace();el.onError();}
    				}
	    		}
			}).start();		    		
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
