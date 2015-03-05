package com.example.music.model;

import java.io.Serializable;

/**
 * Created by Lijinpu on 2015/1/22.
 */
public class MusicInfo implements Serializable{

    //歌曲基础地址
    private final static String baseAddress = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";

    private String musicName = "";
    private String musicAuthor = "";
    private String musicOtherInfo = "";
    private String musicId = "";
    private String downloadUrl = "";
    private String musicType = "";

    private int musicSize;
    private int musicTime;
    private int musicPlayTime;

    //歌曲存放路径
    private String path = "";

    //false表示按钮可用，true表示按钮不可用
    private boolean downloadButtonEnable = true;

    public MusicInfo(){

    }

    public MusicInfo(String musicName,String musicAuthor,String path){
        this.musicName = musicName;
        this.musicAuthor = musicAuthor;
        this.path = path;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    public int getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(int musicTime) {
        this.musicTime = musicTime;
    }

    public int getMusicPlayTime() {
        return musicPlayTime;
    }

    public void setMusicPlayTime(int musicPlayTime) {
        this.musicPlayTime = musicPlayTime;
    }

    public String getMusicOtherInfo() {
        return musicOtherInfo;
    }

    public void setMusicOtherInfo(String musicOtherInfo) {
        this.musicOtherInfo = musicOtherInfo;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getMusicDownXmlUrl(){
        return  baseAddress + musicName + "$$" + musicAuthor + "$$$";
    }

    public String getMusicType() {
        return musicType;
    }

    public void setMusicType(String musicType) {
        this.musicType = musicType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setDownloadButtonEnable(){
        downloadButtonEnable = false;
    }

    public boolean isDownloadButtonEnable(){
        return downloadButtonEnable;
    }

    public String getPath(){
        return path;
    }
}