package org.sensation.snapmemo.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.UserVO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
     * 判断是数据第一次添加还是后序修改
     */
    boolean isAdd;

    /**
     * 需要更新的item位置，用于重启主界面时替换该位置上的memo
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

    /**
     * 判断是否需要联网，默认不需要联网；这个标志位用于判断ContentActivity的取消键，
     * 在新建时取消需要发送删除请求，而在正常查看时取消不需要
     */
    boolean isConnected = false;

    /**
     * 判断是否用户已登录，默认未登录；这个标志位用于判断是否应该提供用户添加和删除功能，
     * 未登录的状态下，添加和删除信息是不进行网络通信的
     */
    boolean isOnline = false;

    /**
     * 是否是刚刚通过注册的第一次登录
     */
    boolean isFirstSigned = false;

    String firstSignedUserID;
    String firstSignedPassword;

    /**
     * 新的列表内容，登录界面登录后会设置，会在重启主界面时检测到并更新
     */
    ArrayList<MemoVO> newMemoVOList;

    private ClientData() {
        //加载本地用户信息
        initInfo();
    }

    public static ClientData getInstance() {
        return clientData;
    }


    /**
     * @return 是否登录过
     */
    public boolean isSigned() {
        try {
            MyApplication.getContext().openFileInput("userSignedInfo");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @return 已经登录过的帐号和密码，作为数组的0和1位置传回
     */
    public String[] getUserSignInfo() {
        String userName = "", password = "";
        FileInputStream in = null;
        BufferedReader reader = null;

        try {
            in = MyApplication.getContext().openFileInput("userSignedInfo");
            reader = new BufferedReader(new InputStreamReader(in));
            userName = reader.readLine();
            password = reader.readLine();
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
        return new String[]{userName, password};
    }

    /**
     * 保存已经登录的帐号密码信息
     *
     * @param signedInfo 以数组形式传入的帐号密码
     * @return 是否保存成功
     */
    public boolean saveUserSignedInfo(String[] signedInfo) {
        String userName = signedInfo[0], password = signedInfo[1];
        FileOutputStream out = null;
        BufferedWriter writer = null;

        try {
            out = MyApplication.getContext().openFileOutput("userSignedInfo", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(userName);
            writer.newLine();
            writer.write(password);
            writer.newLine();
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
     * 通过读取配置文件来获得IP地址
     *
     * @return 服务器的IP地址
     */
    public String getServerIP() {
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

    /**
     * @return 是否是首次运行
     */
    //TODO 首次运行进行引导
    public boolean isFirstRun() {
        boolean isFirstRun = true;
        Properties runtime = new Properties();
        try {
            runtime.load(MyApplication.getContext().getAssets().open("runtime.properties"));
            isFirstRun = Boolean.parseBoolean(runtime.getProperty("isFirstRun"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isFirstRun;
    }

    /**
     * 初始化用户信息（userID,userName,educationInfo,signiture,userLogo），如果之前没有过就采用默认的加载方案<br><br>
     * <strong>注意:</strong><br>
     * 由于采用静态单例模式，加载程序时就会提前加载信息，即使联网后可能将原有数据覆盖
     */
    private void initInfo() {
        String userID, userName, signature;
        Bitmap userLogo;
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader;
        Properties userInfo = new Properties();
        try {
            //加载用户信息
            fileInputStream = MyApplication.getContext().openFileInput("userInfo");
            //再包装一层BufferedReader是因为中文编码问题
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            userInfo.load(bufferedReader);

            userID = userInfo.getProperty("userID");
            if (userID == null) {
                initUserInfo();
            }
            userName = userInfo.getProperty("userName");
            signature = userInfo.getProperty("signature");


            //加载用户头像
            fileInputStream = MyApplication.getContext().openFileInput("userLogo");

            userLogo = BitmapFactory.decodeStream(fileInputStream);

            userVO = new UserVO(userID, userName, signature, userLogo);
        } catch (IOException e) {
            e.printStackTrace();
            if (userInfo.size() == 0) {
                initUserInfo();
            }
            if (fileInputStream == null) {
                initLogo();
            }
            //重新加载
            initInfo();
        }
    }

    /**
     * 在本地没有上一次的用户数据时加载的原始设定数据(用户信息)
     */
    private void initUserInfo() {
        String defaultUserID = MyApplication.getContext().getString(R.string.default_user_id),
                defaultUserName = MyApplication.getContext().getString(R.string.default_user_name),
                defaultCondition = MyApplication.getContext().getString(R.string.default_signature);
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = MyApplication.getContext().openFileOutput("userInfo", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(defaultUserID);
            writer.newLine();
            writer.write(defaultUserName);
            writer.newLine();
            writer.write(defaultCondition);
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

    /**
     * 在本地没有上一次的用户数据时加载的原始设定数据(用户头像)
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

    public UserVO getUserVO() {
        return userVO;
    }

    public MemoVO getNewMemoVO() {
        return newMemoVO;
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

    public boolean isUserInfoChanged() {
        return isUserInfoChanged;
    }

    public void setUserInfoChanged(boolean isUserInfoChanged) {
        this.isUserInfoChanged = isUserInfoChanged;
    }

    public void setUserInfo(UserVO userVO) {
        this.userVO = userVO;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public ArrayList<MemoVO> getNewMemoVOList() {
        return newMemoVOList;
    }

    public void setNewMemoVOList(ArrayList<MemoVO> newMemoVOList) {
        this.newMemoVOList = newMemoVOList;
    }

    public void setUserID(String userID) {
        userVO.setUserID(userID);
    }

    public boolean isFirstSigned() {
        return isFirstSigned;
    }

    public void setFirstSigned(boolean isFirstSigned) {
        this.isFirstSigned = isFirstSigned;
    }

    public String getFirstSignedUserID() {
        return firstSignedUserID;
    }

    public void setFirstSignedUserID(String userID) {
        firstSignedUserID = userID;
    }

    public String getFirstSignedPassword() {
        return firstSignedPassword;
    }

    public void setFirstSignedPassword(String password) {
        firstSignedPassword = password;
    }
}
