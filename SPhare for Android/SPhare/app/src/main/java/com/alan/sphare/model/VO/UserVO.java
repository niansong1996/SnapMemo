package com.alan.sphare.model.VO;

/**
 * Created by Alan on 2016/1/20.
 */
public class UserVO {
    String userID;
    String password;
    String groupID;
    TimeTableVO timeTable;

    public UserVO(String userID, String password, String groupID, TimeTableVO timeTable) {
        this.userID = userID;
        this.password = password;
        this.groupID = groupID;
        this.timeTable = timeTable;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public TimeTableVO getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(TimeTableVO timeTable) {
        this.timeTable = timeTable;
    }
}
