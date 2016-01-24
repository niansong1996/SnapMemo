package com.alan.sphare.model.httpservice;

import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;

/**
 * Created by Alan on 2016/1/22.
 */
public interface HttpHandlerService {

    /**
     * 根据小组ID获得小组信息
     *
     * @param groupID
     * @return
     */
    public GroupVO getGroupInfo(String groupID);

    /**
     * 用户添加自己的空余时间
     *
     * @param freeDateTimeVO
     * @return
     */
    public boolean addFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID);

    /**
     * 用户删除自己的空余时间
     *
     * @param freeDateTimeVO
     * @param groupID
     * @return
     */
    public boolean deleteFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID);
}
