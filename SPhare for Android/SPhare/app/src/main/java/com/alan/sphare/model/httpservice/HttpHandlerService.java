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
     * 用户设置自己的空余时间
     *
     * @param freeDateTimeVO
     * @return
     */
    public boolean setFreeTime(FreeDateTimeVO freeDateTimeVO,String groupID);
}
