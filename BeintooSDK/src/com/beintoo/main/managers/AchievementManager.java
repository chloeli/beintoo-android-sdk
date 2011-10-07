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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.beintoo.R;
import com.beintoo.activities.mission.MissionAchievementMessage;
import com.beintoo.activities.mission.MissionDialog;
import com.beintoo.beintoosdk.BeintooAchievements;
import com.beintoo.beintoosdkutility.Current;
import com.beintoo.beintoosdkutility.DeviceId;
import com.beintoo.beintoosdkutility.PreferencesHandler;
import com.beintoo.beintoosdkutility.SerialExecutor;
import com.beintoo.main.Beintoo.BAchievementListener;
import com.beintoo.main.Beintoo.BMissionListener;
import com.beintoo.wrappers.Mission;
import com.beintoo.wrappers.Player;
import com.beintoo.wrappers.PlayerAchievement;
import com.beintoo.wrappers.PlayerAchievementContainer;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

public class AchievementManager {
	
	private static Context currentContext;
	private static AtomicLong LAST_OPERATION = new AtomicLong(0);
	private long OPERATION_TIMEOUT = 5000;
	
	public AchievementManager (Context ctx){
		currentContext = ctx;
	}
	
	public void submitAchievementScore(final String achievement, final Float percentage, final Float value, final Boolean increment,
			final boolean showNotification, final int gravity, final BAchievementListener listener){
		SerialExecutor executor = SerialExecutor.getInstance();	
		executor.execute(new Runnable(){     					
    		public void run(){	
    			synchronized (LAST_OPERATION){
					try {
						
						if(System.currentTimeMillis() < LAST_OPERATION.get() + OPERATION_TIMEOUT)
							return;						
						final Player p = Current.getCurrentPlayer(currentContext);
						BeintooAchievements ba = new BeintooAchievements();
						
						boolean localUnlocked = PreferencesHandler.getBool("achievements"+p.getGuid(), achievement, currentContext);
						String deviceUUID = null;
						
						// IF THE USER HAS ALREADY UNLOCKED THIS ACHIEVEMENT ON THIS DEVICE DON'T DO ANYTHING
						if(!localUnlocked){
							// IF THE USER DOESN'T UNLOCKED THE ACHIEVEMENT ON THIS DEVICE 
							// CHECK IF THE ACHIEVEMENT WAS PREVIOUS UNLOCKED ON ANOTHER DEVICE
							List<PlayerAchievement> prevachievements = ba.getPlayerAchievements(p.getGuid());
							boolean previousUnlocked = false;					
							for(PlayerAchievement pa : prevachievements){
								if(pa.getAchievement().getId().equals(achievement)){
									if(pa.getStatus().equals("UNLOCKED")){
										previousUnlocked = true;
										
										// ADD TO THIS DEVICE A PREVIOUS UNLOCKED ACHIEVEMENT FROM ANOTHER DEVICE
										PreferencesHandler.saveBool("achievements"+p.getGuid(), pa.getAchievement().getId(), true, currentContext);
									}
								}  
							}   
							 
							// IF THE ACHIEVEMENT WAS NOT PREVIOUS UNLOCKED UNLOCK IT
							if(!previousUnlocked){
								deviceUUID = DeviceId.getUniqueDeviceId(currentContext);
								PlayerAchievementContainer achievements = ba.submitPlayerAchievement(p.getGuid(), 
										deviceUUID, achievement, percentage, value, increment);
								StringBuilder message = new StringBuilder("");
								StringBuilder achivementsName = new StringBuilder("");
								boolean hasUnlocked = false;
								boolean isMissionCompleted = false;
								
								Mission completedMission = null;
								int count = 0;
								
								if(achievements.getMission() != null){
									isMissionCompleted = achievements.getMission().getStatus().equals("OVER") ? true : false;
									completedMission = achievements.getMission();
								}
								
								for(PlayerAchievement pa : achievements.getPlayerAchievements()){
									if(pa.getStatus().equals("UNLOCKED")){ 
										hasUnlocked = true; 
										
										// CHECK IF IT'S A MISSION ACHIEVEMENT
										if(achievements.getMission() != null){										
											achivementsName.append(pa.getAchievement().getName());
											achivementsName.append(" ");
											message.append(currentContext.getString(R.string.achievementmission));
											
											boolean appTargetFound = false;
											for(PlayerAchievement psearch : achievements.getMission().getSponsoredAchievements()){
												if(psearch.getAchievement().getId().equals(achievement)){
													if(achievements.getMission().getPlayerAchievements().get(0).getAchievement().getApp() != null){
														message.append(achievements.getMission().getPlayerAchievements().get(0).getAchievement().getApp().getName());
														appTargetFound = true;
													}
													break;
												}												
											}
											
											if(achievements.getMission().getSponsoredAchievements().size() > 0)
												if(!appTargetFound && achievements.getMission().getSponsoredAchievements().get(0).getAchievement().getApp() != null) 
													message.append(achievements.getMission().getSponsoredAchievements().get(0).getAchievement().getApp().getName());
											
										}else{											
											achivementsName.append(pa.getAchievement().getName());
											achivementsName.append(" ");
										}
											
										// SAVE LOCALLY
										PreferencesHandler.saveBool("achievements"+p.getGuid(), pa.getAchievement().getId(), true, currentContext);

										count++;
									} 
								}		

								if(!isMissionCompleted && hasUnlocked && showNotification){
									final Bundle msg = new Bundle();
									msg.putString("name", achivementsName.toString());
									msg.putString("message", message.toString());
									UIHandler.post(new Runnable(){
										@Override
										public void run() {
											MissionAchievementMessage.showMessage(currentContext, msg, gravity);											
										}										
									});
								}else if(isMissionCompleted){
									// OPEN THE MISSION COMPLETED DIALOG
									final Mission mm = completedMission;
									final String devID = deviceUUID;
									UIHandler.post(new Runnable(){
										@Override
										public void run() {
											MissionDialog m = new MissionDialog(currentContext, mm, MissionDialog.MISSION_COMPLETED, p, devID);
											m.show();											
										}										
									});
								}
								
								if(listener != null){
									listener.onComplete(achievements.getPlayerAchievements());
									if(hasUnlocked){								
										listener.onAchievementUnlocked(achievements.getPlayerAchievements());
									}
								}	
							}
						}
						
						LAST_OPERATION.set(System.currentTimeMillis());
					}catch(Exception e){
						if(listener != null) listener.onBeintooError(e);
						e.printStackTrace();
					}
				}
    		}
		});
	} 
	
	public void getMission(final BMissionListener listener, final boolean alwaysShow){
		SerialExecutor executor = SerialExecutor.getInstance();	
		executor.execute(new Runnable(){     					
    		public void run(){	
    			synchronized (LAST_OPERATION){
					try {						
						if(System.currentTimeMillis() < LAST_OPERATION.get() + OPERATION_TIMEOUT)
							return;
						
						final long lastShow = PreferencesHandler.getLong("lastmission", currentContext);						
						if(System.currentTimeMillis() < lastShow + 86400000  && !alwaysShow) //24 HOURS
							return;
						
						final String deviceUUID = DeviceId.getUniqueDeviceId(currentContext);
						final Player p = Current.getCurrentPlayer(currentContext);
						
						Long currentTime = System.currentTimeMillis();
	    				Location pLoc = LocationMManager.getSavedPlayerLocation(currentContext);
	    				Mission m = null;
	    				
	    				BeintooAchievements ba = new BeintooAchievements();
	    	        	if(pLoc != null){
	    	        		if((currentTime - pLoc.getTime()) <= 900000){ // TEST 20000 (20 seconds)
	    	        			// GET A MISSION WITH COORDINATES
	    	        			m = ba.getMission(deviceUUID, Double.toString(pLoc.getLatitude()), 
	    	        					Double.toString(pLoc.getLongitude()), Double.toString(pLoc.getAccuracy()));
	    	        		}else {
	    	        			// GET A MISSION WITHOUT COORDINATES
	    	        			m = ba.getMission(deviceUUID, null, null, null);
			    				LocationMManager.savePlayerLocation(currentContext);
	    	        		}
	    	        	}else{
	    	        		m = ba.getMission(deviceUUID, null, null, null);
		    				LocationMManager.savePlayerLocation(currentContext);
	    	        	}
	    	        	
	    	        	final Mission mission = m;
						UIHandler.post(new Runnable(){
							@Override
							public void run() {
								if(mission.getId() != null){
									MissionDialog md = new MissionDialog(currentContext, mission, MissionDialog.MISSION_STATUS, p, deviceUUID);
									md.show();
								}
							}							 
						});	
						
						PreferencesHandler.saveLong("lastmission", System.currentTimeMillis(), currentContext);
						
						if(listener != null) listener.onComplete();
					}catch(Exception e){
						if(listener != null) listener.onError(e);
						e.printStackTrace();						
					}
    			}
    		}
		});
	}
	
	Handler UIHandler = new Handler();
}
