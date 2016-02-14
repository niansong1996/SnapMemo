package org.sensation.snapmemo.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.sensation.snapmemo.R;
import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.UserVO;

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

    /**
     * 保存userVO至本地文件
     *
     * @param userVO
     */
    public static void saveUserInfo(UserVO userVO) {
        FileOutputStream out;
        BufferedWriter writer = null;

        try {
            out = MyApplication.getContext().openFileOutput("userInfo", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write("userID=" + userVO.getUserID());
            writer.newLine();
            writer.write("userName=" + userVO.getUserName());
            writer.newLine();
            writer.write("educationInfo=" + userVO.getEducationInfo());
            writer.newLine();
            writer.write("condition=" + userVO.getCondition());
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
        FileOutputStream out = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream in = null;

        try {
            out = MyApplication.getContext().openFileOutput("userLogo", Context.MODE_PRIVATE);
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

    public static ArrayList<String> getUserPreference() {
        String tempPreference;
        ArrayList<String> userPreferenceList = new ArrayList<String>();
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;

        try {
            fileInputStream = MyApplication.getContext().openFileInput("userLoginPreference");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            while ((tempPreference = bufferedReader.readLine()) != null) {
                userPreferenceList.add(tempPreference);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            try {
                MyApplication.getContext().openFileOutput("userLoginPreference", Context.MODE_PRIVATE);
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
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userPreferenceList;
    }

    public static void saveUserPreference(String userSucceedInput) {
        FileOutputStream out;
        BufferedWriter writer = null;

        try {
            out = MyApplication.getContext().openFileOutput("userLoginPreference", Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initInfo() {
        String userID, userName, condition, educationInfo;
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
            educationInfo = userInfo.getProperty("educationInfo");
            condition = userInfo.getProperty("condition");


            //加载用户头像
            fileInputStream = MyApplication.getContext().openFileInput("userLogo");

            userLogo = BitmapFactory.decodeStream(fileInputStream);

            userVO = new UserVO(userID, userName, educationInfo, condition, userLogo);
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
     * 初始化本地数据，包括用户信息
     */
    private void initUserInfo() {
        String defaultUserID = MyApplication.getContext().getString(R.string.default_user_id),
                defaultUserName = MyApplication.getContext().getString(R.string.default_user_name),
                defaultEducationInfo = MyApplication.getContext().getString(R.string.default_education_info),
                defaultCondition = MyApplication.getContext().getString(R.string.default_condition);
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = MyApplication.getContext().openFileOutput("userInfo", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(defaultUserID);
            writer.newLine();
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
