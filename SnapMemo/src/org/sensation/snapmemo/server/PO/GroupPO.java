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
	public void deleteUser(UserPO user){
		this.users.remove(user);
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
		//TODO
		return false;
	}
}
