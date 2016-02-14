package org.sensation.snapmemo.VO;

/**
 * Created by Alan on 2016/2/13.
 */
public class UserVOLite {
    String userName;
    String educationInfo;
    String signiture;

    public UserVOLite(String userName, String educationInfo, String condition) {
        this.userName = userName;
        this.educationInfo = educationInfo;
        this.signiture = condition;
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

    public String getSigniture() {
        return signiture;
    }

    public void setSigniture(String signiture) {
        this.signiture = signiture;
    }


}
