package com.alan.sphare.logic.groupBl.stub;

import com.alan.sphare.model.Tool.Date;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.Tool.Time;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.TimeVO;
import com.alan.sphare.model.logicservice.GroupBlService;

import java.util.HashMap;

/**
 * Created by Alan on 2016/1/21.
 */
public class GroupBlImpl_stub implements GroupBlService {
    @Override
    public TimeTableVO[] getTimeTable(String groupID) {

        TimeVO[] timeVOList;
        Time time1, time2, time3, time4;
        Date date;
        HashMap<Date, TimeVO[]> hashMap;

        TimeTableVO timeTableVO1, timeTableVO2, timeTableVO3;

        //Alan 2016.1.17的空余时间安排
        timeVOList = new TimeVO[2];
        time1 = new Time(12, 30);
        time2 = new Time(21, 0);
        time3 = new Time(22, 30);
        time4 = new Time(24, 0);
        timeVOList[0] = new TimeVO(time1, time2);
        timeVOList[1] = new TimeVO(time3, time4);
        date = new Date(2016, 1, 17);
        hashMap = new HashMap<Date, TimeVO[]>();
        hashMap.put(date, timeVOList);
        timeTableVO1 = new TimeTableVO("Alan", hashMap);

        //Anthony 2016.1.17的空余时间安排
        timeVOList = new TimeVO[2];
        time1 = new Time(8, 30);
        time2 = new Time(12, 0);
        time3 = new Time(20, 30);
        time4 = new Time(24, 0);
        timeVOList[0] = new TimeVO(time1, time2);
        timeVOList[1] = new TimeVO(time3, time4);
        date = new Date(2016, 1, 17);
        hashMap = new HashMap<Date, TimeVO[]>();
        hashMap.put(date, timeVOList);
        timeTableVO2 = new TimeTableVO("Anthony", hashMap);

        //Sissel 2016.1.17的空余时间安排
        timeVOList = new TimeVO[2];
        time1 = new Time(12, 30);
        time2 = new Time(14, 0);
        time3 = new Time(18, 30);
        time4 = new Time(24, 0);
        timeVOList[0] = new TimeVO(time1, time2);
        timeVOList[1] = new TimeVO(time3, time4);
        date = new Date(2016, 1, 17);
        hashMap = new HashMap<Date, TimeVO[]>();
        hashMap.put(date, timeVOList);
        timeTableVO3 = new TimeTableVO("Sissel", hashMap);


        TimeTableVO[] timeTableVOs = {timeTableVO1, timeTableVO2, timeTableVO3};


        return timeTableVOs;
    }

    @Override
    public boolean setFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {
        return false;
    }
}
