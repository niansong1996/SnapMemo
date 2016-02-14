package org.sensation.snapmemo.VO;

/**
 * Created by Alan on 2016/2/3.
 */
public class MemoVO {

    String memoID;
    String topic;
    String date;
    String day;
    String content;

    public MemoVO(String memoID, String topic, String date, String day, String content) {
        this.memoID = memoID;
        this.topic = topic;
        this.date = date;
        this.day = day;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MemoVOLite toMemoVOLite() {
        return new MemoVOLite(memoID, topic, date, content);
    }
}
