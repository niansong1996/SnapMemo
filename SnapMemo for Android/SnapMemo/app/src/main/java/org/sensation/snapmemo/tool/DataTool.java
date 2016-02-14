package org.sensation.snapmemo.tool;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 文件在外部设备存储时的检查读写工具类
 * Created by Alan on 2016/2/12.
 */
public class DataTool {
    /**
     * 默认保存文件根路径
     */
    public static String defaultSaveDir = Environment.getExternalStorageDirectory() + "/snapmemo";

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
}
