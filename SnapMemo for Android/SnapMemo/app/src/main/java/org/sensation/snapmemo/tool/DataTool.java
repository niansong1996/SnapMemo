package org.sensation.snapmemo.tool;

import android.os.Environment;

import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.MemoVOLite;

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
 * 文件在外部设备存储时的检查读写工具类
 * Created by Alan on 2016/2/12.
 */
public class DataTool {
    /**
     * 默认保存文件根路径
     */
    public static final String DEFAULT_SAVING_DIR = Environment.getExternalStorageDirectory() + "/snapmemo";
    /**
     * 未登录时的默认用户名
     */
    public static final String DEFAULT_USER_NAME = "Alan";
    /**
     * 本地数据保存文件名
     */
    private static final String LOCAL_DATA = "localMemoList";

    /**
     * 创建文件
     *
     * @param dir      保存目录
     * @param fileName 文件名
     * @return 输出文件的File格式
     */
    public static File createFile(String dir, String fileName) {
        //首先确定是否有这个目录，如果没有先创建这个目录
        File outputDir = new File(dir);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        //创建文件，如存在则进行覆盖操作
        File outputFile = new File(dir, fileName);
        try {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

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

        localMemoFile = createFile(DEFAULT_SAVING_DIR + "/" + userName, LOCAL_DATA);
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
                if (out != null) {
                    out.close();
                }
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
        return saveLocalMemoList(newLocalMemoList, DEFAULT_USER_NAME);
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
            in = new FileInputStream(new File(DEFAULT_SAVING_DIR + "/" + userName, LOCAL_DATA));
            reader = new BufferedReader(new InputStreamReader(in));
            while ((temp = reader.readLine()) != null) {
                localMemoInfo.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //第一次创建找不到文件，先创建文件
            createFile(DEFAULT_SAVING_DIR + "/" + userName, LOCAL_DATA);
            getLocalMemoList(userName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JSONHandler.getMemoList(localMemoInfo.toString());
    }

    private static void initLocalMemoList(String userName) {
        FileOutputStream out = null;
        BufferedWriter writer = null;

        File localFile = createFile(DEFAULT_SAVING_DIR + "/" + userName, LOCAL_DATA);
        MemoVOLite defaultMemoVO = new MemoVOLite("000000", "欢迎来到SnapMemo", "2016-1-1 08:00", "第一次使用SnapMemo，单击进入Memo详情，右滑删除Memo");
        try {
            out = new FileOutputStream(localFile);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(JSONHandler.getMemoJSON(defaultMemoVO));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
