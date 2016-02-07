package org.sensation.snapmemo.tool;

import com.google.gson.Gson;

import org.sensation.snapmemo.VO.MemoVO;

/**
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
        MemoVO memoVO = gson.fromJson(JSONMemoVO, MemoVO.class);
        return memoVO;
    }
}
