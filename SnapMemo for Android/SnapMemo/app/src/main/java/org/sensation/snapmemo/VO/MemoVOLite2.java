package org.sensation.snapmemo.VO;

/**
 * Created by Alan on 2016/2/19.
 */
public class MemoVOLite2 {
    String topic;
    String time;
    String content;

    public MemoVOLite2(String topic, String time, String content) {
        this.topic = topic;
        this.time = time;
        this.content = content;
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
