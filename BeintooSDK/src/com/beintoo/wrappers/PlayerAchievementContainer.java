package com.beintoo.wrappers;

import java.util.List;

public class PlayerAchievementContainer {
	List<PlayerAchievement> playerAchievements;
	Mission mission;
	 
	public List<PlayerAchievement> getPlayerAchievements() {
		return playerAchievements;
	}
	public void setPlayerAchievements(List<PlayerAchievement> playerAchievements) {
		this.playerAchievements = playerAchievements;
	}
	public Mission getMission() {
		return mission;
	}
	public void setMission(Mission mission) {
		this.mission = mission;
	} 
}
