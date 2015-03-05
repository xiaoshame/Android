package com.example.music.model;

/**
 * Created by xiaozhisong on 15-2-10.
 */
public class DownloadInfo {
    //歌曲名
    private String musicName = "";
    //下载状态，下载或停止,false 表示停止，ture表示正在下载
    private boolean downloading = false;
    //下载百分比
    private int progress = 0;
    //下载链接
    private String downloadUrl = "";
    public DownloadInfo(String downloadUrl,String musicName,boolean downloading,int progress){
        this.downloadUrl = downloadUrl;
        this.musicName = musicName;
        this.downloading = downloading;
        this.progress = progress;
    }
    public String getMusicName() {
        return musicName;
    }

    public boolean isDownloading() {
        return downloading;
    }

    //获取进度
    public int getProgress() {
        return progress;
    }
    //获取下载链接，也是task中的key
    public String getDownloadUrl(){
        return downloadUrl;
    }
}
