package org.sensation.snapmemo.tool;

import android.os.Environment;

import org.sensation.snapmemo.VO.MemoVO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * 文件在内、外部设备存储时的检查读写工具类，储存一些默认数据和创建方法
 */
public class IOTool {
    /**
     * 默认外部保存文件根路径
     */
    public static final String DEFAULT_SAVING_DIR = Environment.getExternalStorageDirectory() + "/snapmemo";

    /**
     * 未登录时的默认用户名
     */
    public static final String DEFAULT_USER_NAME = "Default";

    /**
     * 本地数据保存文件名
     */
    public static final String LOCAL_DATA = "localMemoList";

    /**
     * 默认本地原始Memo数据
     */
    public static final MemoVO DEFAULT_MEMO = new MemoVO("000000", "欢迎来到SnapMemo", "2016-1-1 08:00", "", "第一次用SnapMemo，单击进入Memo详情，右滑删除Memo");

    /**
     * 本地用户信息储存的文件名
     */
    public static final String USER_INFO = "userInfo";

    /**
     * 用户名登录选择列表储存的用户名文件名
     */
    public static final String USER_LOGIN_PREFERENCE = "userLoginPreference";

    /**
     * 用户头像文件名
     */
    public static final String USER_LOGO = "userLogo";

    /**
     * propertoes文件中的键名
     */
    public static final String USER_ID = "userID", USER_NAME = "userName", SIGNATURE = "signature";

    /**
     * 用户信息键列表
     */
    public static final String[] USER_INFO_TITLE_LIST = new String[]{USER_ID, USER_NAME, SIGNATURE};

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
            outputDir.mkdirs();
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
     * 向writer中以properties的格式写入内容
     *
     * @param writer BufferedWriter
     * @param key    键
     * @param value  值
     * @return 传入的修饰后的BufferedWriter
     */
    public static BufferedWriter writeProperties(BufferedWriter writer, String key, String value) {
        try {
            writer.write(key + "=" + value);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    /**
     * 向writer写入一系列以properties格式的内容
     *
     * @param writer BufferedWriter
     * @param key    键
     * @param value  值
     * @return 传入的修饰后的BufferedWriter
     */
    public static BufferedWriter writeProperties(BufferedWriter writer, String[] key, String[] value) {
        for (int i = 0; i < key.length; i++) {
            writeProperties(writer, key[i], value[i]);
        }
        return writer;
    }
}
