package org.sensation.snapmemo.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.UserVO;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * 用于用户的本地数据存储
 * Created by Alan on 2016/2/5.
 */
public class ClientData {

    private static ClientData clientData = new ClientData();
    /**
     * 更新的MemoVO
     */
    MemoVO newMemoVO;

    /**
     * 判断是否为一次更新
     */
    boolean isUpdate;

    /**
     * 判断是数据第一次添加还是后序修改
     */
    boolean isAdd;

    /**
     * 需要更新的item位置
     */
    int position;

    /**
     * 用户信息
     */
    UserVO userVO;

    /**
     * 判断用户信息是否被更改
     */
    boolean isUserInfoChanged;

    private ClientData() {
        //加载本地用户信息
        initInfo();
    }

    public static ClientData getInstance() {
        return clientData;
    }

    private void initInfo() {
        String userName, condition, educationInfo;
        Bitmap userLogo;
        FileInputStream fileInputStream = null;
        Properties userInfo = new Properties();
        try {
            //加载用户信息
            userInfo.load(MyApplication.getContext().openFileInput("userInfo"));

            userName = userInfo.getProperty("userName");
            educationInfo = userInfo.getProperty("educationInfo");
            condition = userInfo.getProperty("condition");


            //加载用户头像
            fileInputStream = MyApplication.getContext().openFileInput("userLogo");

            userLogo = BitmapFactory.decodeStream(fileInputStream);

            userVO = new UserVO(userName, educationInfo, condition, userLogo);
        } catch (IOException e) {
            e.printStackTrace();
            if (userInfo.size() == 0) {
                initUserInfo();
            }
            if (fileInputStream == null) {
                initLogo();
            }
            initInfo();
        }
    }

    /**
     * 初始化本地数据，包括用户信息
     */
    private void initUserInfo() {
        String defaultUserName = "userName=Alan",
                defaultEducationInfo = "educationInfo=Nanjing University",
                defaultCondition = "condition=In love with Grace";
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = MyApplication.getContext().openFileOutput("userInfo", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(defaultUserName);
            writer.newLine();
            writer.write(defaultEducationInfo);
            writer.newLine();
            writer.write(defaultCondition);
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

    /**
     * 初始化本地数据，包括用户头像
     */
    private void initLogo() {
        FileOutputStream out = null;
        InputStream in = null;
        try {
            out = MyApplication.getContext().openFileOutput("userLogo", Context.MODE_PRIVATE);
            in = MyApplication.getContext().getAssets().open("defaultUserLogo.png");
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

    /**
     * 通过读取配置文件来获得IP地址
     *
     * @return
     */
    public String getServerIP() {
        //TODO 通过读取配置文件来获得IP地址
        String IP = null;
        Properties IPAddress = new Properties();
        try {
            IPAddress.load(MyApplication.getContext().getAssets().open("ip_address.properties"));
            IP = IPAddress.getProperty("ip");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IP;
    }

    public UserVO getUserVO() {
        return userVO;
    }

    public MemoVO getNewMemoVO() {
        if (isUpdate) {
            setUpdateCondition(false);
            return newMemoVO;
        } else {
            return null;
        }
    }

    public void setNewMemoVO(MemoVO newMemoVO) {
        this.newMemoVO = newMemoVO;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public void setUpdateCondition(boolean condition) {
        isUpdate = condition;
    }

    public boolean isUserInfoChanged() {
        return isUserInfoChanged;
    }

    public void setUserInfoChanged(boolean isUserInfoChanged) {
        this.isUserInfoChanged = isUserInfoChanged;
    }

    public void setUserInfo(UserVO userVO) {
        this.userVO = userVO;
    }
}
