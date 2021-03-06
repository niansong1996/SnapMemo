package org.sensation.snapmemo.VO;

/**
 * Created by Alan on 2016/2/8.
 */
public class MemoVOLite {

    String memoID;
    String topic;
    String time;
    String content;

    public MemoVOLite(String memoID, String topic, String time, String content) {
        this.memoID = memoID;
        this.topic = topic;
        this.time = time;
        this.content = content;
    }

    public String getMemoID() {
        return memoID;
    }

    public void setMemoID(String memoID) {
        this.memoID = memoID;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
