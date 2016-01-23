package com.alan.sphare.logic.httpBl;

import com.alan.sphare.model.Tool.Date;
import com.alan.sphare.model.Tool.Time;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.VO.TimeVO;
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
            //将传入的JSON格式的字符串转为JSONObject进行操作
            JSONObject jsonObject = new JSONObject(JSONGroupInfo);
            //用户的JSON类
            JSONObject user;
            //freeTime的JSON类
            JSONObject freeTime;
            //获得userID的数组
            JSONArray userIDList = jsonObject.getJSONArray("userID");
            //自由时间数组
            JSONArray freeTimeArray, timePeriod;
            //获得
            for (int i = 0; i < userIDList.length(); i++) {
                user = userIDList.getJSONObject(i);
                freeTimeArray = user.getJSONArray("freeTime");
                for(int j=0;j<freeTimeArray.length();j++){
                    freeTime = freeTimeArray.getJSONObject(j);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getFreeTimeJSON(FreeDateTimeVO freeDateTimeVO, String groupID) {
        String freeTimeJSONString;
        //总对象
        JSONObject freeTimeJSON = new JSONObject();
        //用户的空余时间对象
        JSONObject freeTime = new JSONObject();

        try {
            //设置总对象属性
            freeTimeJSON.put("userID", freeDateTimeVO.getUserID());
            freeTimeJSON.put("groupID", groupID);
            freeTimeJSON.put("freeTime", freeTime);

            //设置空余时间对象属性
            Date date = freeDateTimeVO.getDate();
            freeTime.put("date", date.getYear() + "-" + date.getMonth() + "-" + date.getDay());
            TimeVO timePeriod = freeDateTimeVO.getFreeDateTime()[0];
            Time startTime = timePeriod.getStartTime(),
                    endTime = timePeriod.getEndTime();
            freeTime.put("timePeriod", amplify(startTime.hour) + ":" + amplify(startTime.minute)
                    + "-" + amplify(endTime.hour) + ":" + amplify(endTime.minute));

            //生成返回字符串
            freeTimeJSONString = freeTimeJSON.toString();

            return freeTimeJSONString;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 补充1位数字，在前面添加0，并以String类型返回
     *
     * @param num
     * @return
     */
    private String amplify(int num) {
        if (num >= 0 && num < 10) {
            return "0" + String.valueOf(num);
        } else {
            return String.valueOf(num);
        }
    }
}
