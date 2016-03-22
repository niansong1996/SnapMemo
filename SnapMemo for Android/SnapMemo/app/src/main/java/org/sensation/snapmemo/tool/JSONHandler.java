package org.sensation.snapmemo.tool;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sensation.snapmemo.VO.MemoDisplayVO;
import org.sensation.snapmemo.VO.MemoTransVO;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.MemoVOLite;
import org.sensation.snapmemo.VO.MemoVOLite2;
import org.sensation.snapmemo.VO.UserVOLite;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JSON工具类
 * Created by Alan on 2016/2/5.
 */
public class JSONHandler {

    /**
     * 将图片OCR传回的信息解析为MemoDisplayVO
     *
     * @param JSONMemoVO
     * @return
     */
    public static MemoDisplayVO getMemoDisplayVO(String JSONMemoVO) {
        Gson gson = new Gson();
        MemoVOLite2 memoVOLite2 = gson.fromJson(JSONMemoVO, MemoVOLite2.class);
        String day = TimeTool.getDay(memoVOLite2.getTime());

        MemoDisplayVO memoDisplayVO = new MemoDisplayVO(memoVOLite2.getTopic(), memoVOLite2.getTime(),
                day, memoVOLite2.getContent());
        return memoDisplayVO;
    }

    /**
     * 利用GSON将传回的JSON字符串直接解析称MemoVO类
     *
     * @param JSONMemoVO
     * @return
     */
    public static MemoVO getMemoVO(String JSONMemoVO) {
        Gson gson = new Gson();
        MemoVOLite memoVOLite = gson.fromJson(JSONMemoVO, MemoVOLite.class);
        String date = memoVOLite.getTime();
        String day = TimeTool.getDay(date);
        MemoVO memoVO = new MemoVO(memoVOLite.getMemoID(), memoVOLite.getTopic(),
                memoVOLite.getTime(), day, memoVOLite.getContent());

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

    /**
     * ArrayList转JSONString
     *
     * @param memoVOList
     * @return
     */
    public static String getMemoListJSON(ArrayList<MemoVO> memoVOList) {
        JSONObject memo = new JSONObject();
        JSONArray memoList = new JSONArray();
        try {
            for (MemoVO memoVO : memoVOList) {
                memoList.put(new JSONObject(getMemoJSON(memoVO.toMemoVOLite())));
            }
            memo.accumulate("memo", memoList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return memo.toString();
    }

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
     * 生成MemoTransVO的JSON语句
     *
     * @param memoTransVO
     * @return
     */
    public static String getMemoTransJSON(MemoTransVO memoTransVO) {
        Gson gson = new Gson();
        String memoTransJSON;
        TypeAdapter<MemoTransVO> typeAdapter = gson.getAdapter(MemoTransVO.class);
        try {
            memoTransJSON = typeAdapter.toJson(memoTransVO);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return memoTransJSON;
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
     * @param memoIDJSON
     * @return 返回memoID
     */
    public static String getMemoID(String memoIDJSON) {
        String memoID = null;
        try {
            JSONObject memoJSON = new JSONObject(memoIDJSON);
            memoID = memoJSON.getString("memoID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return memoID;
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
