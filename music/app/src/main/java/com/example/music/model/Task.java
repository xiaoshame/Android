package com.example.music.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.music.db.MusicDB;
import com.example.music.util.AsyncUtility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by xiaozhisong on 15-2-10.
 * 下载任务管理类，共享资源
 */
//下载线程和界面通信的结构
public class Task{
    public final static String START = "start";
    public final static String SUSPEND = "supspend";
    public final static String FINISH = "finish";

    //Task 对象
    private static Task task = null;
    // 数据库句柄
    private MusicDB musicDB;
    //下载记录
    private HashMap<String,AsyncUtility> mapTask;
    //保证全局只有一个Task对象
    private Task(Context context){
        mapTask = new HashMap<String,AsyncUtility>();
        //从数据库中读取被中断的下载任务
        musicDB = MusicDB.getInstance(context);
        SQLiteDatabase db = musicDB.getReadableDatabase();
        String sql = "SELECT * FROM downloadInfo WHERE downloadStatus=?";
        Cursor cursor = db.rawQuery(sql, new String[]{"pause"});
        while(cursor.moveToNext()){
            //未完成的任务添加到下载列表中
            String musicName = cursor.getString(cursor.getColumnIndex("musicName"));
            String downloadUrl = cursor.getString(cursor.getColumnIndex("downloadUrl"));
            String musicAuthor = cursor.getString(cursor.getColumnIndex("musicAuthor"));
            //已下载长度
            int downloadedBytes = cursor.getInt(cursor.getColumnIndex("downloadedBytes"));
            //需要下载的数据总长
            int totalBytes = cursor.getInt(cursor.getColumnIndex("totalBytes"));
            AsyncUtility asyncUtility = new AsyncUtility(context,musicName,musicAuthor,(int)(downloadedBytes * 100.0f / totalBytes));
            mapTask.put(downloadUrl,asyncUtility);
        }
        db.close();
    }
    //获取task 对象
    public synchronized static Task getInstance(Context context){
        if(task == null){
            task = new Task(context);
        }
        return task;
    }
    //判断map中是否包含某任务
    public synchronized boolean containsTask(String key){
        return mapTask.containsKey(key);
    }
    //添加任务
    public synchronized void putTask(String downloadUrl,AsyncUtility asyncUtility){
        mapTask.put(downloadUrl, asyncUtility);
    }
    //删除任务
    public synchronized void removeTask(String downlaodUrl){
        mapTask.remove(downlaodUrl);
    }
    public synchronized boolean startTask(String downloadUrl){
        AsyncUtility asyncUtility = mapTask.get(downloadUrl);
        if(null != asyncUtility){
            //启动下载
            //当调用AsyncTask的方法execute时，就会去自动调用doInBackground方法
            asyncUtility.executeOnExecutor(Executors.newCachedThreadPool(),downloadUrl);
            return true;
        }
        return false;
    }

    //刷新显示列表数据
    public synchronized void refresh(List<DownloadInfo> list){
        list.clear();
        Iterator iterator = mapTask.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String downloadUrl = (String)entry.getKey();
            AsyncUtility asyncUtility = (AsyncUtility)entry.getValue();
            DownloadInfo downloadInfo = new DownloadInfo(downloadUrl,asyncUtility.getMusicName(),asyncUtility.isDownloading(),asyncUtility.getProgress());
            list.add(downloadInfo);
        }
    }

    //暂停下载指定歌曲
    public synchronized void stopTask(String downloadUrl){
        AsyncUtility asyncUtility = mapTask.get(downloadUrl);
        asyncUtility.stopDownloading();
    }

    //继续下载指定歌曲
    public synchronized void continueTask(String downloadUrl){
        AsyncUtility asyncUtility = mapTask.get(downloadUrl);
        asyncUtility.continueDownloading();
    }
}