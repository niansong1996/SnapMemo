package com.alan.sphare.model.VO;

import com.alan.sphare.model.tool.Date;

import java.util.HashMap;

/**
 * Created by Alan on 2016/1/19.
 */
public class TimeTableVO {
    String userID;
    HashMap<Date, TimeVO[]> userFreeTime;

    public TimeTableVO(String userID, HashMap<Date, TimeVO[]> userFreeTime) {
        this.userID = userID;
        this.userFreeTime = userFreeTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public HashMap<Date, TimeVO[]> getUserFreeTime() {
        return userFreeTime;
    }

    public void setUserFreeTime(HashMap<Date, TimeVO[]> userFreeTime) {
        this.userFreeTime = userFreeTime;
    }
}
