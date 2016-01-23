package com.alan.sphare.model.PO;

import com.alan.sphare.model.Tool.Date;
import com.alan.sphare.model.Tool.Time;

import java.io.Serializable;

/**
 * Created by Alan on 2016/1/23.
 */
public class FreeTimePO implements Serializable {
    String groupID;
    String userID;
    Date date;
    Time startTime;
    Time endTime;

    public FreeTimePO(String groupID, String userID, Date date, Time startTime, Time endTime) {
        this.groupID = groupID;
        this.userID = userID;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
