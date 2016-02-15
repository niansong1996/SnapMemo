package org.sensation.snapmemo.tool;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.MemoVOLite;
import org.sensation.snapmemo.VO.UserVOLite;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * JSON工具类
 * Created by Alan on 2016/2/5.
 */
public class JSONHandler {

    /**
     * 利用GSON将传回的JSON字符串直接解析称MemoVO类
     *
     * @param JSONMemoVO
     * @return
     */
    public static MemoVO getMemoVO(String JSONMemoVO) {
        Gson gson = new Gson();
        MemoVOLite memoVOLite = gson.fromJson(JSONMemoVO, MemoVOLite.class);
        String date = memoVOLite.getDate();
        String day = getDay(date);
        MemoVO memoVO = new MemoVO(memoVOLite.getMemoID(), memoVOLite.getTopic(),
                memoVOLite.getDate(), day, memoVOLite.getContent());

        return memoVO;
    }

    /**
     * JSONString转成ArrayList
     *
     * @param resultJSONString
     * @return
     */
    public static ArrayList<MemoVO> getMemoList(String resultJSONString) {
        JSONArray memo;
        ArrayList<MemoVO> memoVOList = new ArrayList<MemoVO>();
        try {
            JSONObject memoList = new JSONObject(resultJSONString);
            memo = memoList.getJSONArray("memo");

            for (int i = 0; i < memo.length(); i++) {
                MemoVO memoVO = getMemoVO(memo.getString(i));
                memoVOList.add(memoVO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return memoVOList;
    }

//    /**
//     * ArrayList转JSONString
//     * @param memoVOList
//     * @return
//     */
//    public static String getMemoListJSON(ArrayList<MemoVO> memoVOList){
//        Gson gson = new Gson();
//        JSONObject memo = new JSONObject();
//        JSONArray memoList = new JSONArray();
//
//    }

    /**
     * 生成memoVO的JSON语句
     *
     * @param memoVOLite
     * @return memoVOLite的JSON String
     */
    public static String getMemoJSON(MemoVOLite memoVOLite) {
        Gson gson = new Gson();
        String memoJSON;
        TypeAdapter<MemoVOLite> typeAdapter = gson.getAdapter(MemoVOLite.class);
        try {
            memoJSON = typeAdapter.toJson(memoVOLite);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return memoJSON;
    }

    /**
     * @param memoID
     * @return memoID生成的JSON String
     */
    public static String getMemoIDJSON(String memoID) {
        JSONObject memoIDJSON = new JSONObject();
        try {
            memoIDJSON.put("memoID", memoID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return memoIDJSON.toString();
    }

    /**
     * 生成登录的JSON字符串
     *
     * @param userName
     * @param password
     * @return
     */
    public static String getSignInJSON(String userName, String password) {
        JSONObject signinJSON = new JSONObject();
        try {
            signinJSON.put("userName", userName);
            signinJSON.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return signinJSON.toString();
    }

    /**
     * 获得根据yyyy-MM-dd格式传入的日期所对应的星期几信息
     *
     * @param date
     * @return
     */
    private static String getDay(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        int dayOfWeekNum = 0;
        String dayOfWeek = "未获得";

        try {
            c.setTime(format.parse(date));

            dayOfWeekNum = c.get(Calendar.DAY_OF_WEEK);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (dayOfWeekNum) {
            case 1:
                dayOfWeek = "周日";
                break;
            case 2:
                dayOfWeek = "周一";
                break;
            case 3:
                dayOfWeek = "周二";
                break;
            case 4:
                dayOfWeek = "周三";
                break;
            case 5:
                dayOfWeek = "周四";
                break;
            case 6:
                dayOfWeek = "周五";
                break;
            case 7:
                dayOfWeek = "周六";
                break;
        }

        return dayOfWeek;
    }

    /**
     * 转换userIDJSONString为userID
     *
     * @param userIDJSON
     * @return
     */
    public static String getUserID(String userIDJSON) {
        try {
            JSONObject userJSON = new JSONObject(userIDJSON);
            return userJSON.get("userID").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 转换userIDString为userIDJSONString
     *
     * @param userID
     * @return
     */
    public static String getUserIDJSON(String userID) {
        JSONObject userIDJSON = new JSONObject();
        try {
            userIDJSON.put("userID", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userIDJSON.toString();
    }

    /**
     * 转换userVOLiteJSON为userVOLite
     *
     * @param userInfoJSON
     * @return
     */
    public static UserVOLite getUserInfo(String userInfoJSON) {
        Gson gson = new Gson();
        UserVOLite userVOLite = gson.fromJson(userInfoJSON, UserVOLite.class);
        return userVOLite;
    }

}
