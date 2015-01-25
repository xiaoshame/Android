package com.example.music.util;

import com.example.music.encoding.ParseEncoding;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiaozhisong on 15-1-23.
 */
//公用http类
//使用回调函数才可以适应不同清空下的http请求,如果使用handler 也可以需要修改参数，将message.what传递进来（那个handler进行处理？？）
public class HttpUtil {
    public static void sendRequestWithHttpURLConnection(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    if(listener != null){
                        //回调onFinish
                        listener.onFinish(connection.getInputStream());
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    public static void sendRequestWithHttpClient(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httGet);
                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        if(listener != null){
                            listener.onFinish(httpResponse.getEntity());
                        }
                    }
                }catch(Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
}
