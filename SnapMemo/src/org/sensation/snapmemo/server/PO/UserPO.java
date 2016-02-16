package org.sensation.snapmemo.server.PO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class UserPO {
	private String userID;
	private String userName;
	private String password;
	private String groupID;
	private String logoLocation;
	private String signature;
	private Collection<MemoPO> memos;
	
	public UserPO(){}
	public UserPO(String iD, String userName, String password) {
		this.userID = iD;
		this.memos = new ArrayList<MemoPO>();
		this.userName = userName;
		this.password = password;
		this.groupID = "no_group";
		this.logoLocation = "/home/SnapMemoData/userLogo/"+this.userID+".png";
		this.signature = "Take a SNAP and everything gets easier";
	}
	public Iterator<MemoPO> getMemos(){
		return this.memos.iterator();
	}
	public void addMemo(MemoPO memo){
		this.memos.add(memo);
	}
	public void deleteMemo(MemoPO memo){
		this.memos.remove(memo);
	}
	public String getID() {
		return userID;
	}
	public void setID(String iD) {
		userID = iD;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGroupID() {
		return groupID;
	}
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	public String getLogoLocation() {
		return logoLocation;
	}
	public void setLogoLocation(String logoLocation) {
		this.logoLocation = logoLocation;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	@Override
	public boolean equals(Object o){
		UserPO source = (UserPO) o;
		if(source.getID().equals(this.getID()))
			return true;
		return false;
	}
	
	
}
