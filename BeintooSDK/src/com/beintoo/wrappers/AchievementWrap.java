package com.beintoo.wrappers;

public class AchievementWrap {
	String id;
    String name;
    String description;
    String imageURL;
    App app;
    Double bedollars;
    Boolean isSecret;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public App getApp() {
		return app;
	}
	public void setApp(App app) {
		this.app = app;
	}
	public Double getBedollars() {
		return bedollars;
	}
	public void setBedollars(Double bedollars) {
		this.bedollars = bedollars;
	}
	public Boolean getIsSecret() {
		return isSecret;
	}
	public void setIsSecret(Boolean isSecret) {
		this.isSecret = isSecret;
	}
}
