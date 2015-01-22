package com.example.music.Activity;

/**
 * Created by Lijinpu on 2015/1/22.
 */
public class MusicInfo {
    private String musicName;
    private String musicAuthor;
    private String musicOtherInfo;
    private int musicSize;
    private int musicTime;
    private int musicPlayTime;

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
}