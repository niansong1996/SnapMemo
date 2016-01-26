package com.alan.sphare.logic.httpBl;

import com.alan.sphare.logic.tool.NetAddress;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.httpservice.HttpHandlerService;
import com.alan.sphare.model.httpservice.JSONHandlerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <strong>Http传输处理器</strong><br>
 * Created by Alan on 2016/1/22.
 */
public class HttpHandler implements HttpHandlerService {

    /**
     * 网络URL
     */
    URL url;

    /**
     * 定向网络http连接
     */
    HttpURLConnection conn;

    /**
     * JSON处理器
     */
    JSONHandlerService jsonHandler;

    /**
     * 网络输出流
     */
    OutputStreamWriter outputStreamWriter;

    public HttpHandler() {

        jsonHandler = new JSONHandler();

        try {
            url = new URL(NetAddress.hostAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public GroupVO getGroupInfo(String groupID) {
        StringBuffer resultJSONStringBuffer = new StringBuffer();
        GroupVO resultGroupVO;

        setConnection(RequestMethod.POST, RequestProperty.PLAINTEXT);

        try {
            //设置内容长度
            conn.setRequestProperty("Content-Length", String.valueOf(groupID.length()));
            //设置请求方式
            conn.setRequestProperty("Request-Type", "Get-Group-Info");

            flushInfo(groupID);

            //从服务器获得返回的JSON语句
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String tempLine = null;
            while ((tempLine = bufferedReader.readLine()) != null) {
                resultJSONStringBuffer.append(tempLine);
            }

            //关闭输出流
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            closeConn();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //利用JSONHandler将数据解析成GroupVO返回
        resultGroupVO = jsonHandler.getGroupInfo(resultJSONStringBuffer.toString());

        return resultGroupVO;
    }

    @Override
    public boolean addFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {
        boolean result = false;
        //获得生成的JSON字符串
        String sendFreeTime = jsonHandler.getFreeTimeJSON(freeDateTimeVO, groupID);

        setConnection(RequestMethod.POST, RequestProperty.JSON);

        try {
            //设置内容长度
            conn.setRequestProperty("Content-Length", String.valueOf(sendFreeTime.length()));
            //设置请求方式
            conn.setRequestProperty("Request-Type", "Add-Free-Time");

            //将生成的JSON字符串刷入输出流传至服务器
            if (sendFreeTime == null && sendFreeTime.length() == 0) {
                return false;
            }

            flushInfo(sendFreeTime);

            //读取返回的布尔值判断是否更新成功
            ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
            result = objectInputStream.readBoolean();

            //关闭输入流
            if (objectInputStream != null) {
                objectInputStream.close();
            }

            closeConn();

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

    @Override
    public boolean deleteFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {
        boolean result = false;
        //获得生成的JSON字符串
        String sendFreeTime = jsonHandler.getFreeTimeJSON(freeDateTimeVO, groupID);

        setConnection(RequestMethod.POST, RequestProperty.JSON);

        try {
            //设置内容长度
            conn.setRequestProperty("Content-Length", String.valueOf(sendFreeTime.length()));
            //设置请求方式
            conn.setRequestProperty("Request-Type", "Delete-Free-Time");

            //将生成的JSON字符串刷入输出流传至服务器
            if (sendFreeTime == null && sendFreeTime.length() == 0) {
                return false;
            }

            flushInfo(sendFreeTime);

            //读取返回的布尔值判断是否更新成功
            ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
            result = objectInputStream.readBoolean();

            //关闭输入流
            if (objectInputStream != null) {
                objectInputStream.close();
            }

            closeConn();


            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * 设置连接参数
     *
     * @param requestMethod
     * @param requestProperty
     */
    private void setConnection(RequestMethod requestMethod, RequestProperty requestProperty) {

        try {
            //打开连接
            conn = (HttpURLConnection) url.openConnection();

            //允许输出
            conn.setDoOutput(true);

            //设置请求方式为POST
            conn.setRequestMethod(requestMethod.name());

            //设置字符集
            conn.setRequestProperty("Accept-Charset", "utf-8");

            //设置不使用缓存
            conn.setUseCaches(false);

            //设置连接超时为3秒
            conn.setConnectTimeout(3 * 1000);

            //设置内容格式，这里采用JSON或纯文本
            switch (requestProperty) {
                case JSON:
                    conn.setRequestProperty("Content-Type", "application/json");
                    break;
                case PLAINTEXT:
                    conn.setRequestProperty("Content-Type", "text/plain");
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将信息刷入网络输出流
     *
     * @param info
     * @throws IOException
     */
    private void flushInfo(String info) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
        outputStreamWriter.write(info);
        outputStreamWriter.flush();
        //响应失败处理
        if (conn.getResponseCode() >= 300) {
            throw new IOException("HTTP Request is not success, Response code is " + conn.getResponseCode());
        }
    }

    /**
     * 关闭流,关闭连接
     */
    private void closeConn() throws IOException {

        //关闭输出流
        if (outputStreamWriter != null) {
            outputStreamWriter.close();
        }
        //关闭连接
        conn.disconnect();
    }
}
