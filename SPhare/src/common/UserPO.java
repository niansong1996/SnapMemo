package common;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Id;

public class UserPO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2623408003982814356L;
	String name;
	ArrayList<FreeTimePO> freeTime;
	public UserPO(){}
	public UserPO(String name,ArrayList<FreeTimePO> time){
		this.name = name;
		this.freeTime = time;
	}
	@Id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<FreeTimePO> getFreeTime() {
		return freeTime;
	}
	public void setFreeTime(ArrayList<FreeTimePO> freeTime) {
		this.freeTime = freeTime;
	}
	
}
