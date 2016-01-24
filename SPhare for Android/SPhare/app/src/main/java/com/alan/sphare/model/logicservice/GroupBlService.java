package com.alan.sphare.model.logicservice;

import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.TimeTableVO;

/**
 * Created by Alan on 2016/1/21.
 */
public interface GroupBlService {

    /**
     * 根据groupID来获得组内所有人的timetable
     *
     * @param groupID
     * @return
     */
    public TimeTableVO[] getTimeTable(String groupID);

    /**
     * 添加空余时间
     *
     * @param freeDateTimeVO
     * @param groupID
     * @return
     */
    public boolean addFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID);

    /**
     * 删除空余时间
     *
     * @param freeDateTimeVO
     * @param groupID
     * @return
     */
    public boolean deleteFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID);


}
