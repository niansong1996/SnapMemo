package org.sensation.snapmemo.VO;

import android.graphics.Bitmap;

/**
 * Created by Alan on 2016/2/8.
 */
public class UserVO {

    String userID;
    String userName;
    String signature;
    Bitmap userLogo;

    public UserVO(String userID, String userName, String signature, Bitmap userLogo) {
        this.userID = userID;
        this.userName = userName;
        this.signature = signature;
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


    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Bitmap getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(Bitmap userLogo) {
        this.userLogo = userLogo;
    }

}
