package common;

import java.util.ArrayList;

import javax.persistence.Id;

public class GroupPO {
	String groupID;
	ArrayList<UserPO> userID;
	public GroupPO(){};
	public GroupPO(String id,ArrayList<UserPO> users){
		this.groupID = id;
		this.userID = users;
	}
	@Id
	public String getGroupID() {
		return groupID;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public ArrayList<UserPO> getUserID() {
		return userID;
	}
	public void setUserID(ArrayList<UserPO> userID) {
		this.userID = userID;
	}
	
}
