package com.alan.sphare.logic.httpBl;

import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.httpservice.JSONHandlerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alan on 2016/1/23.
 */
public class JSONHandler implements JSONHandlerService {
    @Override
    public GroupVO getGroupInfo(String JSONGroupInfo) {
        try {
            //将传入的JSON格式的字符串篡位JSONObject进行操作
            JSONObject jsonObject = new JSONObject(JSONGroupInfo);
            //获得userID的数组
            JSONArray userIDList = jsonObject.getJSONArray("userID");
            JSONArray date,freeTime;

            for(int i=0;i<userIDList.length();i++){

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
