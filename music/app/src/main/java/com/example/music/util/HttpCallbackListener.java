package com.example.music.util;

import org.apache.http.HttpEntity;

import java.io.InputStream;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public interface HttpCallbackListener {
    //此处的接口还可以进一步优化，接受的参数类型使用object
    //HttpURLConnection返回接口
    void onFinish(InputStream response);
    //HttpClient返回接口
    void onFinish(HttpEntity response);
    void onError(Exception e);
}
