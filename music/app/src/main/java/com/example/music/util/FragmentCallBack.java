package com.example.music.util;

import com.example.music.model.MusicInfo;

import java.util.List;

/**
 * Created by xiaozhisong on 15-2-27.
 */
public interface FragmentCallBack {
    public void callbackFun(List<MusicInfo> value);
    public void playSelectedMusic(int position);
}
