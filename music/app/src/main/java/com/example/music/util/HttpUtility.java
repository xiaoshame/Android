package com.example.music.util;

import android.util.Log;

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
public class HttpUtility {
    public static void sendRequestWithHttpURLConnectionInThread(final String address,final String format,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(listener != null) {
                        //回调onFinish
                        listener.onFinish(sendRequestWithHttpURLConnection(address,format));
                    }
                }catch (Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }
            }
        }).start();

    }

    public static String sendRequestWithHttpURLConnection(final String address,final String format) throws Exception{
        HttpURLConnection connection = null;
        try{
            URL url = new URL(address);
            Log.d("sendRequestWithHttpURLConnection",address);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,format));
            StringBuilder response = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }finally{
            if(connection != null){
                Log.d("sendRequestWithHttpURLConnection","disconnect");
                connection.disconnect();
            }
        }
    }

    public static void sendRequestWithHttpClientInThread(final String address,final String format,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(listener != null) {
                        listener.onFinish(sendRequestWithHttpClient(address,format));
                    }
                }catch(Exception e){
                    if(listener != null){
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }

    public static String sendRequestWithHttpClient(final String address,final String format) throws Exception{
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httGet = new HttpGet(address);
        Log.d("sendRequestWithHttpClient",address);
        HttpResponse httpResponse = httpClient.execute(httGet);
        if(httpResponse.getStatusLine().getStatusCode() == 200){
            return EntityUtils.toString(httpResponse.getEntity(), format);
        }
        return null;
    }
}
