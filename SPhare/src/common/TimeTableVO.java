package com.alan.sphare.model.VO;

/**
 * Created by Alan on 2016/1/19.
 */
public class TimeTableVO {
    String userID;
    FreeTimeVO userFreeTime;

    public TimeTableVO(String userID, FreeTimeVO userFreeTime) {
        this.userID = userID;
        this.userFreeTime = userFreeTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public FreeTimeVO getUserFreeTime() {
        return userFreeTime;
    }

    public void setUserFreeTime(FreeTimeVO userFreeTime) {
        this.userFreeTime = userFreeTime;
    }
}
