package org.sensation.snapmemo.VO;

import android.graphics.Bitmap;

/**
 * Created by Alan on 2016/2/8.
 */
public class UserVO {

    String userID;
    String userName;
    String educationInfo;
    String condition;
    Bitmap userLogo;

    public UserVO(String userID, String userName, String educationInfo, String condition, Bitmap userLogo) {
        this.userID = userID;
        this.userName = userName;
        this.educationInfo = educationInfo;
        this.condition = condition;
        this.userLogo = userLogo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEducationInfo() {
        return educationInfo;
    }

    public void setEducationInfo(String educationInfo) {
        this.educationInfo = educationInfo;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Bitmap getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(Bitmap userLogo) {
        this.userLogo = userLogo;
    }

}
