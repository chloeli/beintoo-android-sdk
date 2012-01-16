package com.beintoo.wrappers;

public class Notification {
    private String id;
    private Player source;
    private String type;
    private String status;
    private String creationdate;
    private String localizedMessage;
    private String extId;
    private String extIdRef;
    private String url;
    private String image_url;
    
    public Notification(){}
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Player getSource() {
		return source;
	}
	public void setSource(Player source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getLocalizedMessage() {
		return localizedMessage;
	}
	public void setLocalizedMessage(String localizedMessage) {
		this.localizedMessage = localizedMessage;
	}
	public String getExtId() {
		return extId;
	}
	public void setExtId(String extId) {
		this.extId = extId;
	}
	public String getExtIdRef() {
		return extIdRef;
	}
	public void setExtIdRef(String extIdRef) {
		this.extIdRef = extIdRef;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
}
