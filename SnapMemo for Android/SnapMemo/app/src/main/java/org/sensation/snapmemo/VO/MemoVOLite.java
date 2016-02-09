package org.sensation.snapmemo.VO;

/**
 * Created by Alan on 2016/2/8.
 */
public class MemoVOLite {
    String topic;
    String date;
    String content;

    public MemoVOLite(String topic, String date, String content) {
        this.topic = topic;
        this.date = date;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
