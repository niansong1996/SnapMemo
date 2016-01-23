package com.alan.sphare.model.VO;

import java.util.ArrayList;

/**
 * Created by Alan on 2016/1/20.
 */
public class GroupVO {
    String groupID;
    ArrayList<UserVO> userList;

    public GroupVO(String groupID, ArrayList<UserVO> userList) {
        this.groupID = groupID;
        this.userList = userList;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public ArrayList<UserVO> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<UserVO> userList) {
        this.userList = userList;
    }
}
