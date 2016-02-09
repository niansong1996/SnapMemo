package org.sensation.snapmemo.server.PO;

import java.util.Collection;

public class GroupPO {
	private String ID;
	private String groupName;
	private Collection<UserPO> users;
	public GroupPO(String iD, String groupName) {
		ID = iD;
		this.groupName = groupName;
	}
	public void addUser(UserPO user){
		this.users.add(user);
	}
	public void modifyUser(UserPO user){
		this.deleteUser(user.getID());
		this.addUser(user);
	}
	public void deleteUser(String userID){
		for(UserPO user : users)
			if(user.getID().equals(userID))
				users.remove(user);
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@Override
	public boolean equals(Object o){
		GroupPO source = (GroupPO) o;
		if(this.getID().equals(source.getID()))
			return true;
		return false;
	}
}
