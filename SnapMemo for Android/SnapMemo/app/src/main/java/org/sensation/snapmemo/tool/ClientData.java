package org.sensation.snapmemo.tool;

import org.sensation.snapmemo.VO.MemoVO;

import java.io.IOException;
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

    private ClientData() {
    }

    public static ClientData getInstance() {
        return clientData;
    }

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
}
