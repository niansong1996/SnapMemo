package org.sensation.snapmemo.server.Utility;

public class IntStringWrapper {
	private int code;
	private String info;
	private byte[] img;
	public IntStringWrapper(int code, String info) {
		this.code = code;
		this.info = info;
	}
	public IntStringWrapper(int code, byte[] img) {
		this.code = code;
		this.info = "Is a pic";
		this.img = img;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public byte[] getImg() {
		return img;
	}
	public void setImg(byte[] img) {
		this.img = img;
	}
	
}
