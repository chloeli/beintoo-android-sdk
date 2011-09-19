package com.beintoo.wrappers;

import java.util.List;

public class Mission {
	String id;
	String status;
	String creationdate;
    List<PlayerAchievement> playerAchievements;
    List<PlayerAchievement> sponsoredAchievements;
    Vgood vgood;
	Double bedollars;
	boolean isNew;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreationdate() {
		return creationdate;
	}
	public void setCreationdate(String creationdate) {
		this.creationdate = creationdate;
	}
	public List<PlayerAchievement> getPlayerAchievements() {
		return playerAchievements;
	}
	public void setPlayerAchievements(List<PlayerAchievement> playerAchievements) {
		this.playerAchievements = playerAchievements;
	}
	public List<PlayerAchievement> getSponsoredAchievements() {
		return sponsoredAchievements;
	}
	public void setSponsoredAchievements(
			List<PlayerAchievement> sponsoredAchievements) {
		this.sponsoredAchievements = sponsoredAchievements;
	}
	public Vgood getVgood() {
		return vgood;
	}
	public void setVgood(Vgood vgood) {
		this.vgood = vgood;
	}
	public Double getBedollars() {
		return bedollars;
	}
	public void setBedollars(Double bedollars) {
		this.bedollars = bedollars;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
}
