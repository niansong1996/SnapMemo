package org.sensation.snapmemo.VO;

/**
 * 接收从服务器获得的用户签名
 * Created by Alan on 2016/2/13.
 */
public class UserVOLite {
    String signature;

    public UserVOLite(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


}
