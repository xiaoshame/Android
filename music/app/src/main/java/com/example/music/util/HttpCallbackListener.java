package com.example.music.util;

import org.apache.http.HttpEntity;

import java.io.InputStream;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public interface HttpCallbackListener {
    void onFinish(Object response);
    void onError(Exception e);
}
