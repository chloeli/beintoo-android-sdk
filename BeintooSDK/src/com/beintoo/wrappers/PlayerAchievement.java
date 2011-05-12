package com.beintoo.wrappers;

public class PlayerAchievement {
	AchievementWrap achievement;
    Float percentage;
    Float score;
    String unlockDate;
    String status;
    
	public AchievementWrap getAchievement() {
		return achievement;
	}
	public void setAchievement(AchievementWrap achievement) {
		this.achievement = achievement;
	}
	public Float getPercentage() {
		return percentage;
	}
	public void setPercentage(Float percentage) {
		this.percentage = percentage;
	}
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	public String getUnlockDate() {
		return unlockDate;
	}
	public void setUnlockDate(String unlockDate) {
		this.unlockDate = unlockDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
