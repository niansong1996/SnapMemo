package com.alan.sphare.logic.groupBl;

import com.alan.sphare.logic.httpBl.HttpHandler;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.UserVO;
import com.alan.sphare.model.httpservice.HttpHandlerService;
import com.alan.sphare.model.logicservice.GroupBlService;

import java.util.ArrayList;

/**
 * Created by Alan on 2016/1/21.
 */
public class GroupBlImpl implements GroupBlService {

    /**
     * 依赖的处理http传输的接口逻辑
     */
    HttpHandlerService httpHandler;

    public GroupBlImpl() {
        httpHandler = new HttpHandler();
    }

    @Override
    public TimeTableVO[] getTimeTable(String groupID) {

        //通过http逻辑方法获得小组信息，并转换为TimeTable列表返回
        GroupVO group = httpHandler.getGroupInfo(groupID);
        ArrayList<UserVO> userList = group.getUserList();
        TimeTableVO[] timeTable = new TimeTableVO[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            timeTable[i] = userList.get(i).getTimeTable();
        }

        return timeTable;
    }

    @Override
    public boolean setFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {

        return httpHandler.setFreeTime(freeDateTimeVO, groupID);
    }


}
