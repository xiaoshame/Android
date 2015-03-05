package com.example.music.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.music.model.Charts;
import com.example.music.model.MusicInfo;
import com.example.music.util.FileUtility;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public class MusicDB {
    //数据库名
    public static final String DB_NAME = "music";
    //数据库版本
    public static final int VERSION = 1;
    //数据库写句柄
    private SQLiteDatabase db = null;
    //MusicDB实例
    private static MusicDB musicDB;
    MusicOpenHelper dbHelper;
    //将构造方法私有化 保证在全局范围内只有一个MusicDB的实例
    private MusicDB(Context context){
        dbHelper = new MusicOpenHelper(context,DB_NAME,null,VERSION);
    }

    //获取MusicDB实例
    public synchronized static MusicDB getInstance(Context context){
        if(musicDB == null){
            musicDB = new MusicDB(context);
        }
        return musicDB;
    }

    //获取数据库读句柄
    public SQLiteDatabase getReadableDatabase() {
        return dbHelper.getReadableDatabase();
    }

    //获取已下载得长度，支持断点续传
    public synchronized int getDownloadedBytes(String downloadUrl){
        int downloadedLength = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT downloadedBytes FROM downloadInfo WHERE downloadUrl=?";
        Cursor cursor = db.rawQuery(sql,new String[] {downloadUrl});
        while(cursor.moveToNext()){
            downloadedLength = cursor.getInt(0);
        }
        db.close();
        return downloadedLength;
    }
    //更新歌曲数据库信息
    public synchronized void updateDownloadingInfo(String downloadUrl,int downloadedBytes,String downloadStatus){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try{
            db.beginTransaction();
            String sql = "UPDATE downloadInfo SET downloadedBytes=?, downloadStatus = ? WHERE downloadUrl=?";
            db.execSQL(sql,new String[] {downloadedBytes + "",downloadStatus,downloadUrl});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    //更新歌曲数据库信息
    public synchronized void updateDownloadingInfo(String downloadUrl,int downloadedBytes,String path,String downloadStatus){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try{
            db.beginTransaction();
            String sql = "UPDATE downloadInfo SET path=?,downloadedBytes=?,downloadStatus=? WHERE downloadUrl=?";
            db.execSQL(sql,new String[] {path + "",downloadedBytes + "",downloadStatus,downloadUrl});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    //保存下载得数据
    public synchronized void saveDownloadingInfo(String musicName,String musicAuthor,String downloadUrl,String path,int downloadedBytes,int totalBytes,String downloadStatus){
        //只有这个任务不再数据库中才会进入到这个函数中
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            //使用事务
            db.beginTransaction();
            String sql = "INSERT INTO downloadInfo(musicName,musicAuthor,downloadUrl,path,downloadedBytes,totalBytes,downloadStatus) "
                    + "values(?,?,?,?,?,?,?)";
            db.execSQL(sql, new Object[]{musicName,musicAuthor,downloadUrl, path, downloadedBytes, totalBytes, downloadStatus});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
    }

    //通过下载地址在数据库中匹配，进行删除
    public synchronized void deleteDownloadingInfo(String downloadUrl){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try{
            db.beginTransaction();
            String sql = "DELETE FROM downloadInfo WHERE downloadUrl=?";
            db.execSQL(sql,new String[] {downloadUrl});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    public synchronized void deleteDownloadedInfoWithLocalPath(String path){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try{
            db.beginTransaction();
            String sql = "DELETE FROM downloadInfo WHERE path=?";
            db.execSQL(sql,new String[] {path});
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            db.close();
        }
    }
    //将排行榜实例存储到数据库
    public synchronized void saveCharts(Charts charts){
        db = dbHelper.getWritableDatabase();
        if(charts != null){
            ContentValues values = new ContentValues();
            values.put("charts_name",charts.getName());
            values.put("charts_id",charts.getId());
            values.put("music_num",charts.getCount());
            db.insert("Charts",null,values);
        }
        db.close();
    }
    //读取所有排行榜的信息
    public synchronized void loadCharts(List<Charts> list){
        db = dbHelper.getWritableDatabase();
        list.clear();
        Cursor cursor = db.query("Charts",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                Charts charts = new Charts();
                charts.setName(cursor.getString(cursor.getColumnIndex("charts_name")));
                charts.setCount(cursor.getString(cursor.getColumnIndex("music_num")));
                charts.setId(cursor.getString(cursor.getColumnIndex("charts_id")));
                list.add(charts);
            }while(cursor.moveToNext());
        }
        db.close();
    }

    //读取数据库信息，获取下载完成的数据
    public synchronized void getLoaclMusicInfo(List<MusicInfo> list){
        db = dbHelper.getReadableDatabase();
        list.clear();
        String sql = "SELECT musicName,musicAuthor,path FROM downloadInfo WHERE downloadStatus=?";
        Cursor cursor = db.rawQuery(sql,new String[] {"finished"});
        while(cursor.moveToNext()){
            String musicName = cursor.getString(cursor.getColumnIndex("musicName"));
            String musicAuthor = cursor.getString(cursor.getColumnIndex("musicAuthor"));
            String path = cursor.getString(cursor.getColumnIndex("path"));
            if(FileUtility.isFileExists(path)){
                MusicInfo musicInfo = new MusicInfo(musicName,musicAuthor,path);
                list.add(musicInfo);
            }
        }
        db.close();
    }
}
