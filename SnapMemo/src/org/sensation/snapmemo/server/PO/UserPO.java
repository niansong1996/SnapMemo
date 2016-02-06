package org.sensation.snapmemo.server.PO;

import java.util.Collection;

public class UserPO {
	private String ID;
	private String userName;
	private String password;
	private Collection<MemoPO> memos;
	
	public UserPO(String iD, String userName, String password) {
		ID = iD;
		this.userName = userName;
		this.password = password;
	}
	public void addMemo(MemoPO memo){
		this.memos.add(memo);
	}
	public void deleteMemo(MemoPO memo){
		this.memos.remove(memo);
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
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
	@Override
	public boolean equals(Object o){
		//TODO
		return false;
	}
	
}
