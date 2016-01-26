package com.alan.sphare.logic.httpBl;

import com.alan.sphare.model.tool.Date;
import com.alan.sphare.model.tool.Time;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.VO.TimeTableVO;
import com.alan.sphare.model.VO.TimeVO;
import com.alan.sphare.model.VO.UserVO;
import com.alan.sphare.model.httpservice.JSONHandlerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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

            //自由时间数组
            JSONArray userIDList, freeTimeArray, timePeriod;

            //构成GroupVO的小组ID
            String groupID = jsonObject.getString("groupID");

            //构成GroupVO的用户列表
            ArrayList<UserVO> userList = new ArrayList<UserVO>();

            //获得userID的数组
            userIDList = jsonObject.getJSONArray("userID");

            //遍历用户列表
            for (int i = 0; i < userIDList.length(); i++) {
                user = userIDList.getJSONObject(i);
                //获得userID
                String userID = user.getString("name");

                //获得freeTime数组
                freeTimeArray = user.getJSONArray("freeTime");

                //日期-时间段的哈希时间表
                HashMap<Date, TimeVO[]> timeTable = new HashMap<Date, TimeVO[]>();

                for (int j = 0; j < freeTimeArray.length(); j++) {
                    //获得freeTime对象
                    freeTime = freeTimeArray.getJSONObject(j);

                    //获得date日期
                    String stringDate = freeTime.getString("date");
                    int year = Integer.parseInt(stringDate.split("-")[0]),
                            month = Integer.parseInt(stringDate.split("-")[1]),
                            day = Integer.parseInt(stringDate.split("-")[2]);

                    //当前日期
                    Date date = new Date(year, month, day);

                    //获得timePeriod数组
                    timePeriod = freeTime.getJSONArray("timePeriod");
                    TimeVO[] timeVOList = new TimeVO[timePeriod.length()];

                    //遍历timePeriod数组
                    for (int k = 0; k < timePeriod.length(); k++) {
                        String stringStartTime = timePeriod.getString(k).split("-")[0],
                                stringEndTime = timePeriod.getString(k).split("-")[1];
                        Time startTime = new Time(Integer.parseInt(stringStartTime.split(":")[0]),
                                Integer.parseInt(stringStartTime.split(":")[1])),
                                endTime = new Time(Integer.parseInt(stringEndTime.split(":")[0]),
                                        Integer.parseInt(stringEndTime.split(":")[1]));
                        timeVOList[k] = new TimeVO(startTime, endTime);
                    }
                    timeTable.put(date, timeVOList);
                }
                TimeTableVO timeTableVO = new TimeTableVO(userID, timeTable);
                UserVO userVO = new UserVO(userID, null, groupID, timeTableVO);
                userList.add(userVO);
            }

            GroupVO groupVO = new GroupVO(groupID, userList);

            return groupVO;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
            freeTime.put("timePeriod", Time.amplify(startTime.hour) + ":" + Time.amplify(startTime.minute)
                    + "-" + Time.amplify(endTime.hour) + ":" + Time.amplify(endTime.minute));

            //生成返回字符串
            freeTimeJSONString = freeTimeJSON.toString();

            return freeTimeJSONString;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
