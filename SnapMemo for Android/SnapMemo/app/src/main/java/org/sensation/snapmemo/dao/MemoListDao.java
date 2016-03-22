package org.sensation.snapmemo.dao;

import org.sensation.snapmemo.tool.IOTool;
import org.sensation.snapmemo.tool.JSONHandler;
import org.sensation.snapmemo.VO.MemoVO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Memo列表的DAO
 */
public class MemoListDao {

    /**
     * 在同步完成之后或默认用户，保存本地数据
     *
     * @param newLocalMemoList 需要保存的memo列表
     * @param userName         用户名
     * @return 保存是否成功
     */
    public static boolean saveLocalMemoList(ArrayList<MemoVO> newLocalMemoList, String userName) {
        File localMemoFile;
        FileOutputStream out = null;
        BufferedWriter writer = null;

        localMemoFile = IOTool.createFile(IOTool.DEFAULT_SAVING_DIR +
                "/" + userName, IOTool.LOCAL_DATA);
        try {
            out = new FileOutputStream(localMemoFile);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            for (MemoVO newLocalMemo : newLocalMemoList) {
                String tempMemoString = JSONHandler.getMemoJSON(newLocalMemo.toMemoVOLite());
                writer.write(tempMemoString);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 默认用户保存本地数据
     *
     * @param newLocalMemoList 需要保存的memo列表
     * @return 保存是否成功
     */
    public static boolean saveLocalMemoList(ArrayList<MemoVO> newLocalMemoList) {
        return saveLocalMemoList(newLocalMemoList, IOTool.DEFAULT_USER_NAME);
    }

    /**
     * 登录失败或是网络连接失败时加载本地数据
     *
     * @param userName 用户名
     * @return MemoVOList
     */
    public static ArrayList<MemoVO> getLocalMemoList(String userName) {
        FileInputStream in = null;
        BufferedReader reader = null;
        String temp;
        StringBuilder localMemoInfo = new StringBuilder();

        try {
            in = new FileInputStream(new File(IOTool.DEFAULT_SAVING_DIR +
                    "/" + userName, IOTool.LOCAL_DATA));
            reader = new BufferedReader(new InputStreamReader(in));
            while ((temp = reader.readLine()) != null) {
                localMemoInfo.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //第一次创建找不到文件，先初始化文件
            initLocalMemoList(userName);
            return getLocalMemoList(userName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JSONHandler.getMemoList(localMemoInfo.toString());
    }

    /**
     * 在第一次登录成功或没有登录时，本地没有保存的Memo列表，这个方法初始化了这个列表，添加了一条简单的消息
     *
     * @param userName
     */
    private static void initLocalMemoList(String userName) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        File localFile = IOTool.createFile(IOTool.DEFAULT_SAVING_DIR +
                "/" + userName, IOTool.LOCAL_DATA);

        ArrayList<MemoVO> memoList = new ArrayList<MemoVO>();
        memoList.add(IOTool.DEFAULT_MEMO);
        try {
            out = new FileOutputStream(localFile);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(JSONHandler.getMemoListJSON(memoList));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
