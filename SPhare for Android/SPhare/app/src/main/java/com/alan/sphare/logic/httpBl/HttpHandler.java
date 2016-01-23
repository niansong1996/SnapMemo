package com.alan.sphare.logic.httpBl;

import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.httpservice.HttpHandlerService;
import com.alan.sphare.model.httpservice.JSONHandlerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alan on 2016/1/22.
 */
public class HttpHandler implements HttpHandlerService {

    String host = "http://139.129.40.103:5678/SPhare";
    URL url;
    HttpURLConnection conn;
    JSONHandlerService jsonHandler;

    public HttpHandler() {
        //实例化JSON处理器
        jsonHandler = new JSONHandler();

        try {
            url = new URL(host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public GroupVO getGroupInfo(String groupID) {
        StringBuffer resultJSONStringBuffer = new StringBuffer();
        GroupVO resultGroupVO;
        try {
            //打开连接
            conn = (HttpURLConnection) url.openConnection();
            //允许输出
            conn.setDoOutput(true);
            //设置请求方式为POST
            conn.setRequestMethod("POST");
            //设置字符集
            conn.setRequestProperty("Accept-Charset", "utf-8");
            //设置内容格式，这里采用纯文本方式传输
            conn.setRequestProperty("Content-Type", "text/plain");
            //设置内容长度
            conn.setRequestProperty("Content-Length", String.valueOf(groupID.length()));
            //设置请求方式
            conn.setRequestProperty("Request-Type", "getGroupInfo");
            //设置不使用缓存
            conn.setUseCaches(false);
            //设置连接超时为3秒
            conn.setConnectTimeout(3 * 1000);

            //将groupID刷入输出流传至服务器
            OutputStream outputStream = conn.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(groupID);
            outputStreamWriter.flush();

            //响应失败处理
            if (conn.getResponseCode() >= 300) {
                throw new IOException("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }

            //从服务器获得返回的JSON语句
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String tempLine = null;
            while ((tempLine = bufferedReader.readLine()) != null) {
                resultJSONStringBuffer.append(tempLine);
            }

            //关闭输入输出流
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }

            //断开连接
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //利用JSONHandler将数据解析成GroupVO返回
        resultGroupVO = jsonHandler.getGroupInfo(resultJSONStringBuffer.toString());

        return resultGroupVO;
    }

    @Override
    public boolean setFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {
        boolean result = false;
        //获得生成的JSON字符串
        String sendFreeTime = jsonHandler.getFreeTimeJSON(freeDateTimeVO, groupID);

        try {
            conn = (HttpURLConnection) url.openConnection();
            //允许输出
            conn.setDoOutput(true);
            //设置请求方式为POST
            conn.setRequestMethod("POST");
            //设置字符集
            conn.setRequestProperty("Accept-Charset", "utf-8");
            //设置内容格式，这里采用纯文本
            conn.setRequestProperty("Content-Type", "text/plain");
            //设置内容长度
            conn.setRequestProperty("Content-Length", String.valueOf(sendFreeTime.length()));
            //设置请求方式
            conn.setRequestProperty("Request-Type", "setFreeTime");

            //将生成的JSON字符串刷入输出流传至服务器
            if (sendFreeTime == null && sendFreeTime.length() == 0) {
                return false;
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(sendFreeTime);
            outputStreamWriter.flush();

            //响应失败处理
            if (conn.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }

            //读取返回的布尔值判断是否更新成功
            ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
            result = objectInputStream.readBoolean();

            //关闭输入输出流


            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
}
