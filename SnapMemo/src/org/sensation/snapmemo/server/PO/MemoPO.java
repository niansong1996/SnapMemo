package org.sensation.snapmemo.server.PO;

import java.util.Calendar;

public class MemoPO {
	private String theme;
	//times is annotated as yyyy-mm-dd HH:mm:ss
	private String startTime;
	private String endTime;
	private String description;
	
	public MemoPO(String theme, String startTime, String endTime, String description) {
		this.theme = theme;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}
	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o){
		//TODO
		return false;
	}
	public static void main(String[] args){
		Calendar cal = Calendar.getInstance();
	}
}
