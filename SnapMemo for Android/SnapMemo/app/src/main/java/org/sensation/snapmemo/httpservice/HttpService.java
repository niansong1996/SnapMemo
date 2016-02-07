package org.sensation.snapmemo.httpservice;

import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.tool.ClientData;
import org.sensation.snapmemo.tool.JSONHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 网络服务
 * Created by Alan on 2016/2/5.
 */
public class HttpService {

    /**
     * 定向网络http连接
     */
    HttpURLConnection conn;
    private URL url;

    public HttpService() {
        try {
            url = new URL(ClientData.getInstance().getServerIP());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片上传并利用JSONHandler转换为MemoVO返回
     *
     * @param os
     * @return
     */
    public MemoVO transPic(ByteArrayOutputStream os) {
        StringBuffer resultJSONStringBuffer = new StringBuffer();
        setConnection(RequestMethod.POST, RequestProperty.JSON);
        MemoVO memoVO;

        try {
            flushInfo(os);

            //从服务器获得返回的JSON语句
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String tempLine;
            while ((tempLine = bufferedReader.readLine()) != null) {
                resultJSONStringBuffer.append(tempLine);
            }

            closeStream(bufferedReader);
            closeConn();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        memoVO = JSONHandler.getMemoVO(resultJSONStringBuffer.toString());

        return memoVO;
    }

    /**
     * 网络连接基本设置
     *
     * @param requestMethod   请求方式
     * @param requestProperty 请求格式
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

            //设置内容格式，采用JSON、纯文本或图片流等
            String contentType = "Content-Type";
            switch (requestProperty) {
                case JSON:
                    conn.setRequestProperty(contentType, "application/json");
                    break;
                case PLAINTEXT:
                    conn.setRequestProperty(contentType, "text/plain");
                    break;
                case STREAM:
                    conn.setRequestProperty(contentType, "application/octet-stream");
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将String类型信息刷入网络输出流
     *
     * @param info
     * @throws IOException
     */
    private void flushInfo(String info) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
        outputStreamWriter.write(info);
        outputStreamWriter.flush();
        handleError();

        closeStream(outputStreamWriter);
    }

    /**
     * 将ByteArrayOutputStream类型信息刷入网络输出流
     *
     * @param os
     * @throws IOException
     */
    private void flushInfo(ByteArrayOutputStream os) throws IOException {
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(os.toByteArray());
        outputStream.flush();
        handleError();

        closeStream(outputStream);
    }

    /**
     * 响应失败处理
     *
     * @throws IOException
     */
    private void handleError() throws IOException {
        if (conn.getResponseCode() >= 300) {
            throw new IOException("HTTP Request is not success, Response code is " + conn.getResponseCode());
        }
    }

    /**
     * 关闭流
     *
     * @param os
     * @throws IOException
     */
    private void closeStream(Closeable os) throws IOException {
        if (os != null) {
            os.close();
        }
    }

    /**
     * 关闭连接
     */
    private void closeConn() {
        conn.disconnect();
    }

    /**
     * 请求方式
     */
    private enum RequestMethod {
        POST, GET;
    }

    /**
     * 请求格式
     */
    private enum RequestProperty {
        JSON, PLAINTEXT, STREAM;
    }
}
