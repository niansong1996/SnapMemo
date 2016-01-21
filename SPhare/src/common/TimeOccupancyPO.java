package common;

import java.io.Serializable;

public class TimeOccupancyPO implements Serializable{

	private static final long serialVersionUID = 4008226459350446854L;
	public String userName;
	public int severness; //4 level from 0 - 3
	
	//Time format is tinyint
	public String startTime;
	public String endTime;
	public String day;
	public TimeOccupancyPO(String userName, int severness, String startTime, String endTime,String day) {
		this.userName = userName;
		this.severness = severness;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getSeverness() {
		return severness;
	}
	public void setSeverness(int severness) {
		this.severness = severness;
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
	
	
}
