package org.sensation.snapmemo.server.PO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.sensation.snapmemo.server.Utility.UtilityTools;

public class MemoPO {
	private String memoID;
	private String topic;
	//times is annotated as yyyy-mm-dd HH:mm:ss
	private Calendar time;
	private String content;
	public MemoPO(){}
	public MemoPO(String userID, String topic, String time,String content){
		SimpleDateFormat myFmt=new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		this.memoID = userID + myFmt.format(new Date());
		this.topic = topic;
		this.time = UtilityTools.String2Cal(time);
		this.content = content;
	}
	public String getMemoID() {
		return memoID;
	}

	public void setMemoID(String memoId) {
		this.memoID = memoId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Calendar getTime() {
		return time;
	}
	public void setTime(Calendar time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public boolean equals(Object o){
		//TODO
		return false;
	}
}
