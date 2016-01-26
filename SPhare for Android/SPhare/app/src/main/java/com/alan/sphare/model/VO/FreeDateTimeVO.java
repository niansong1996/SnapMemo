package com.alan.sphare.model.VO;

import com.alan.sphare.model.tool.Date;

/**
 * Created by Alan on 2016/1/21.
 */
public class FreeDateTimeVO {
    String userID;
    Date date;
    TimeVO[] freeDateTime;

    public FreeDateTimeVO(String userID, Date date, TimeVO[] freeDateTime) {
        this.userID = userID;
        this.date = date;
        this.freeDateTime = freeDateTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public TimeVO[] getFreeDateTime() {
        return freeDateTime;
    }

    public void setFreeDateTime(TimeVO[] freeDateTime) {
        this.freeDateTime = freeDateTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
