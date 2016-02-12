package org.sensation.snapmemo.httpservice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.sensation.snapmemo.VO.MemoVO;
import org.sensation.snapmemo.VO.MemoVOLite;
import org.sensation.snapmemo.VO.UserVOLite;
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
import java.util.List;

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
     * @return memoVO
     */
    public MemoVO transPic(ByteArrayOutputStream os) {
        String resultJSONString;
        MemoVO memoVO;

        setConnection(RequestMethod.POST, RequestProperty.STREAM);
        conn.setRequestProperty("Request-Type", "Resolve-Image");

        try {
            flushInfo(os);

            resultJSONString = getJSONString();

            closeConn();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        memoVO = JSONHandler.getMemoVO(resultJSONString);

        return memoVO;
    }

    /**
     * 登录后根据userName获得memo列表
     *
     * @param userID 用户ID
     * @return
     */
    public List<MemoVO> getMemoList(String userID) {
        String resultJSONString;
        List<MemoVO> memoVOList;

        setConnection(RequestMethod.POST, RequestProperty.PLAINTEXT);
        conn.setRequestProperty("Request-Type", "Get-List");

        try {
            flushInfo(userID);

            resultJSONString = getJSONString();

            closeConn();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        memoVOList = JSONHandler.getMemoList(resultJSONString);
        return memoVOList;


    }

    /**
     * 根据MemoID删除该Memo
     *
     * @param memoID
     * @return 是否删除成功
     */
    public boolean deleteMemo(String memoID) {
        boolean result;
        setConnection(RequestMethod.POST, RequestProperty.JSON);
        conn.setRequestProperty("Request-Type", "Delete-Memo");

        try {
            flushInfo(JSONHandler.getMemoIDJSON(memoID));
            result = isResponseSucceed();
            closeConn();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    /**
     * 根据Memo内容修改该Memo
     *
     * @param memoVO
     * @return 是否修改成功
     */
    public boolean modifyMemo(MemoVOLite memoVO) {
        boolean result;
        setConnection(RequestMethod.POST, RequestProperty.JSON);
        conn.setRequestProperty("Request-Type", "Modify-Memo");

        try {
            flushInfo(JSONHandler.getMemoJSON(memoVO));
            result = isResponseSucceed();
            closeConn();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    /**
     * 用户登录
     *
     * @param userName 用户名
     * @param password 密码
     * @return 成功就返回userID，失败就返回null
     */
    public String signIn(String userName, String password) {
        boolean result;
        String resultString = null;
        setConnection(RequestMethod.POST, RequestProperty.JSON);
        conn.setRequestProperty("Request-Type", "Sign-In");

        try {
            flushInfo(JSONHandler.getSignInJSON(userName, password));
            result = isResponseSucceed();
            if (result) {
                resultString = getJSONString();
                resultString = JSONHandler.getUserID(resultString);
            }
            closeConn();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return resultString;
    }

    /**
     * @param userID
     * @return 返回服务器中的用户信息
     */
    public UserVOLite getUserInfo(String userID) {
        String resultString = null;
        setConnection(RequestMethod.POST, RequestProperty.JSON);
        conn.setRequestProperty("Request-Type", "Get-User-Info");

        try {
            flushInfo(JSONHandler.getUserIDJSON(userID));
            resultString = getJSONString();
            closeConn();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSONHandler.getUserInfo(resultString);
    }

    /**
     * @param userID
     * @return 返回服务器中的用户头像
     */
    public Bitmap getUserLogo(String userID) {
        Bitmap logo = null;
        boolean result;
        setConnection(RequestMethod.POST, RequestProperty.JSON);
        conn.setRequestProperty("Request-Type", "Get-Logo");

        try {
            flushInfo(JSONHandler.getUserIDJSON(userID));
            result = isResponseSucceed();
            if (result) {
                logo = BitmapFactory.decodeStream(conn.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return logo;
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
            Log.d("SnapMemo", "setConnection: 0");
            e.printStackTrace();
        }
    }

    /**
     * 将String类型信息刷入网络输出流
     *
     * @param info 字符串信息（一般为JSON）
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
     * @param os 图片的字节流
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
     * 获得从服务器传来的JSONString
     *
     * @throws IOException
     */
    private String getJSONString() throws IOException {
        StringBuffer resultJSONStringBuffer = new StringBuffer();
        //从服务器获得返回的JSON语句
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String tempLine;
        while ((tempLine = bufferedReader.readLine()) != null) {
            resultJSONStringBuffer.append(tempLine);
        }
        closeStream(bufferedReader);

        return resultJSONStringBuffer.toString();
    }

    /**
     * 判断是否成功在服务器进行操作
     *
     * @return 是否成功
     * @throws IOException
     */
    private boolean isResponseSucceed() throws IOException {
        return (conn.getResponseCode() == 200);
    }

    /**
     * 响应失败处理
     *
     * @throws IOException
     */
    private void handleError() throws IOException {
        if (conn.getResponseCode() >= 300) {
            Log.d("SnapMemo", "handleError: ");
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
