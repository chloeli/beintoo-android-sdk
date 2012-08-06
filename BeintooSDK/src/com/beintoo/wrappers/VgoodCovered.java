package com.beintoo.wrappers;

public class VgoodCovered {
	private boolean isCovered;
	private boolean specialAvailable;
	
	public VgoodCovered(boolean isCovered) {
		this.isCovered = isCovered;
	}
	public boolean isCovered() {
		return isCovered;
	}
	public void setCovered(boolean isCovered) {
		this.isCovered = isCovered;
	}
	public boolean isSpecialAvailable() {
		return specialAvailable;
	}
	public void setSpecialAvailable(boolean specialAvailable) {
		this.specialAvailable = specialAvailable;
	}			
}