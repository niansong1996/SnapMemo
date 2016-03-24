package org.sensation.snapmemo.server.PO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class UserPO {
	private int userID;
	private String userName;
	private String password;
	private String groupID;
	private String logoLocation;
	private String signature;
	private Collection<MemoPO> memos;

	public UserPO(){}
	public UserPO(String userName, String password) {
		this.memos = new ArrayList<MemoPO>();
		this.userName = userName;
		this.password = password;
		this.groupID = "no_group";
		this.logoLocation = "/alidata/SnapMemoData/userLogo/defaultLogo_"+((int)(Math.random()*5)+1)+".jpg";
//		this.logoLocation = "logo.jpg";
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
		return this.toStringID();
	}
	public void setID(String iD) {
		userID = Integer.parseInt(iD);
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
	private String toStringID(){
		String tmp = this.userID+"";
		while(tmp.length()!=6){
			tmp = "0"+tmp;
		}
		return tmp;
	}
	@Override
	public boolean equals(Object o){
		UserPO source = (UserPO) o;
		if(source.getID().equals(this.getID()))
			return true;
		return false;
	}


}
