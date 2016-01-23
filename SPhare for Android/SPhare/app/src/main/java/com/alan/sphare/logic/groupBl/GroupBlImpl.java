package com.alan.sphare.logic.groupBl;

import com.alan.sphare.logic.httpBl.HttpHandler;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.UserVO;
import com.alan.sphare.model.logicservice.GroupBlService;

import java.util.ArrayList;

/**
 * Created by Alan on 2016/1/21.
 */
public class GroupBlImpl implements GroupBlService {

    /**
     * 依赖的处理http传输的接口逻辑
     */
    HttpHandler httpHandler;

    TimeTableVO[] timeTable;

    public GroupBlImpl() {
        httpHandler = new HttpHandler();
    }

    @Override
    public TimeTableVO[] getTimeTable(String groupID) {

        //通过http逻辑方法获得小组信息，并转换为TimeTable列表返回
        GroupVO group = httpHandler.getGroupInfo(groupID);
        ArrayList<UserVO> userList = group.getUserList();
        timeTable = new TimeTableVO[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            timeTable[i] = userList.get(i).getTimeTable();
        }

        return timeTable;
    }

    @Override
    public boolean setFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {

        return httpHandler.setFreeTime(freeDateTimeVO, groupID);
    }

    /**
     * 后台线程处理的网络工作
     */
    class BackgroundTask implements Runnable {
        static final int SET = 1, GET = 2;
        int mode;
        String groupID;
        FreeDateTimeVO freeDateTimeVO;

        /**
         * GET的Http请求方法
         * @param groupID
         */
        public BackgroundTask(String groupID) {
            this.groupID = groupID;
            mode = GET;
        }

        /**
         * SET的Http请求方法
         * @param freeDateTimeVO
         * @param groupID
         */
        public BackgroundTask(FreeDateTimeVO freeDateTimeVO, String groupID) {
            this.freeDateTimeVO = freeDateTimeVO;
            this.groupID = groupID;
            mode = SET;
        }

        @Override
        public void run() {
            if (mode == GET) {

            } else if (mode == SET) {

            }
        }
    }
}
