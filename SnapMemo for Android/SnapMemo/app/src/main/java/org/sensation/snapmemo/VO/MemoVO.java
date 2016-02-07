package org.sensation.snapmemo.VO;

/**
 * Created by Alan on 2016/2/3.
 */
public class MemoVO {
    String topic;
    String date;
    String day;
    String content;

    public MemoVO(String topic, String date, String day, String content) {
        this.topic = topic;
        this.date = date;
        this.day = day;
        this.content = content;
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
}
