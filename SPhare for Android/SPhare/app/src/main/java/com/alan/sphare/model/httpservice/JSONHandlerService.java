package com.alan.sphare.model.httpservice;

import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;

/**
 * Created by Alan on 2016/1/23.
 */
public interface JSONHandlerService {

    /**
     * 将JSONString转为GroupVO信息返回
     *
     * @param JSONGroupInfo
     * @return
     */
    public GroupVO getGroupInfo(String JSONGroupInfo);

    /**
     * 将FreeDateTimeVO转为JSON返回
     *
     * @param freeDateTimeVO
     * @return
     */
    public String getFreeTimeJSON(FreeDateTimeVO freeDateTimeVO, String groupID);
}
