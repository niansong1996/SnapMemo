package com.alan.sphare.logic.httpBl;

import com.alan.sphare.model.PO.FreeTimePO;
import com.alan.sphare.model.VO.FreeDateTimeVO;
import com.alan.sphare.model.VO.GroupVO;
import com.alan.sphare.model.httpservice.HttpHandlerService;
import com.alan.sphare.model.httpservice.JSONHandlerService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

            //将groupID刷入输出流传至服务器
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
            outputStreamWriter.write(groupID);
            outputStreamWriter.flush();

            //响应失败处理
            if (conn.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        //利用JSONHandler将数据解析成GroupVO返回
        resultGroupVO = jsonHandler.getGroupInfo(resultJSONStringBuffer.toString());

        return resultGroupVO;
    }

    @Override
    public boolean setFreeTime(FreeDateTimeVO freeDateTimeVO, String groupID) {
        boolean result = false;
        FreeTimePO freeTimePO = new FreeTimePO(groupID, freeDateTimeVO.getUserID(),
                freeDateTimeVO.getDate(), freeDateTimeVO.getFreeDateTime()[0].getStartTime(),
                freeDateTimeVO.getFreeDateTime()[0].getEndTime());

        try {
            conn = (HttpURLConnection) url.openConnection();
            //允许输出
            conn.setDoOutput(true);
            //设置请求方式为POST
            conn.setRequestMethod("POST");
            //设置字符集
            conn.setRequestProperty("Accept-Charset", "utf-8");
            //设置内容格式，这里采用二进制流传输
            conn.setRequestProperty("Content-Type", "application/octet-stream");

            //将序列化的freeDateTimePO刷入输出流传至服务器
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(conn.getOutputStream());
            objectOutputStream.writeObject(freeTimePO);
            objectOutputStream.flush();

            //响应失败处理
            if (conn.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + conn.getResponseCode());
            }

            //读取返回的布尔值判断是否更新成功
            ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
            result = objectInputStream.readBoolean();

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
}
