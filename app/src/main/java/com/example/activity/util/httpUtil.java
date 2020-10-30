package com.example.activity.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class httpUtil {

    private static final int GET_DATA_SUCCESS = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int SERVER_ERROR = 3;
    public httpUtil(){

    }
    //子线程不能操作UI，通过Handler设置图片
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_DATA_SUCCESS:
                    String res = msg.obj.toString();
//                    parseJsonMulti(res);
                    break;
                case NETWORK_ERROR:
//                    Toast.makeText(getContext(),"网络连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case SERVER_ERROR:
//                    Toast.makeText(getContext(),"服务器发生错误",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    /**
     * 新建线程发起http post请求
     * @param httpUrl String
     * @param params Map<String, String>
     */
    public void httpPostOnThread(final String httpUrl, final String params){
        //开启一个线程用于联网
        new Thread() {
            @Override
            public void run() {
                String result;
                try {
                    URL url = new URL(httpUrl);//请求地址
                    //表示设置请求体的类型是json类型
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    //conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setConnectTimeout(10000);//超时时间
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(URLEncoder.encode(params.toString(), "UTF-8").getBytes());
                    outputStream.flush();
                    outputStream.close();
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream inputStream=conn.getInputStream();
                        result = changeInputStream(inputStream, "UTF-8");
                        //利用Message把result发给Handler
                        Message msg = Message.obtain();
                        msg.obj = result;
                        msg.what = GET_DATA_SUCCESS;
                        handler.sendMessage(msg);
                        System.out.println(result);
                    }else{
                        //网络连接错误
                        handler.sendEmptyMessage(NETWORK_ERROR);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                    //网络连接错误
                    handler.sendEmptyMessage(NETWORK_ERROR);
                }
            }
        }.start();
    }
    /**
     * 发起http post请求
     * @param httpUrl String
     * @param params Map<String, String>
     * @return result static String
     */
    public String httpPost(String httpUrl, Map<String, String> params){
        String result;
        try {
            URL url = new URL(httpUrl);//请求地址
            //表示设置请求体的类型是json类型
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(10000);//超时时间
            System.out.println("params "+params.toString());
            OutputStream outputStream = conn.getOutputStream();
            byte[] data = getRequestData(params, "UTF-8").toString().getBytes();//获得请求体
            // 我日你妈耶的下面这种方法根本无效，搞了我半天都没弄好
            //outputStream.write(URLEncoder.encode(params, "UTF-8").getBytes());
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream=conn.getInputStream();
                result = changeInputStream(inputStream, "UTF-8");
                System.out.println(result);
                return result;
            }else{
                return "{'success': false}";
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return "{'success': false}";
        }
    }
    public void httpGet(){

    }
    /**
     * 将一个输入流转换成字符串
     * @param inputStream InputStream
     * @param encode String
     * @return result static String
     */
    private static String changeInputStream(InputStream inputStream,String encode) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();

                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(),encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * 封装请求体信息
     *@param  params 请求体内容，
     *@param  encode 编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
