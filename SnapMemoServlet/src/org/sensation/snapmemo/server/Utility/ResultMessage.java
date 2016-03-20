package org.sensation.snapmemo.server.Utility;

public class ResultMessage {
	private boolean success;
	private String msg;
	public ResultMessage(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
