package org.sensation.snapmemo.dao;

import android.content.Context;
import android.graphics.Bitmap;

import org.sensation.snapmemo.VO.UserVO;
import org.sensation.snapmemo.tool.IOTool;
import org.sensation.snapmemo.tool.MyApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * 用户信息：用户头像、用户名、用户签名、用户名联想的DAO
 * Created by Alan on 2016/3/1.
 */
public class UserInfoDao {

    /**
     * 保存userVO至本地文件
     *
     * @param userVO
     */
    public static void saveUserInfo(UserVO userVO) {
        FileOutputStream out;
        BufferedWriter writer = null;
        String[] userInfoList = new String[]{userVO.getUserID(), userVO.getUserName(), userVO.getSignature()};
        try {
            out = MyApplication.getContext().openFileOutput(IOTool.USER_INFO, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            IOTool.writeProperties(writer, IOTool.USER_INFO_TITLE_LIST, userInfoList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveUserLogo(Bitmap userLogo) {
        if (userLogo != null) {
            FileOutputStream out = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream in = null;

            try {
                out = MyApplication.getContext().openFileOutput(IOTool.USER_LOGO, Context.MODE_PRIVATE);
                //TODO 图片格式问题
                userLogo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                in = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                byte[] b = new byte[10 * 1024];
                while (in.read(b, 0, 10240) != -1) {
                    out.write(b, 0, 10240);
                }
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return 获得用户名输入偏好，在输入用户名时利用AutoCompleteTextView提前加载可能的预选项
     */
    public static ArrayList<String> getUserPreference() {
        String tempPreference;
        ArrayList<String> userPreferenceList = new ArrayList<String>();
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;

        try {
            fileInputStream = MyApplication.getContext().openFileInput(IOTool.USER_LOGIN_PREFERENCE);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            while ((tempPreference = bufferedReader.readLine()) != null) {
                userPreferenceList.add(tempPreference);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            try {
                MyApplication.getContext().openFileOutput(IOTool.USER_LOGIN_PREFERENCE, Context.MODE_PRIVATE);
                return getUserPreference();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userPreferenceList;
    }

    /**
     * 保存成功登录的用户名数据
     *
     * @param userSucceedInput 成功登录的用户名
     */
    public static void saveUserPreference(String userSucceedInput) {
        FileOutputStream out = null;
        BufferedWriter writer = null;

        try {
            out = MyApplication.getContext().openFileOutput(IOTool.USER_LOGIN_PREFERENCE, Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(userSucceedInput);
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
