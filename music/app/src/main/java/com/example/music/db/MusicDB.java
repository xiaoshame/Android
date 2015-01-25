package com.example.music.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.music.model.Charts;

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
    private SQLiteDatabase db;
    //MusicDB实例
    private static MusicDB musicDB;
    //将构造方法私有化 保证在全局范围内只有一个MusicDB的实例
    private MusicDB(Context context){
        MusicOpenHelper dbHelper = new MusicOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    //获取MusicDB实例
    public synchronized static MusicDB getInstance(Context context){
        if(musicDB == null){
            musicDB = new MusicDB(context);
        }
        return musicDB;
    }

    //将排行榜实例存储到数据库
    public void saveCharts(Charts charts){
        if(charts != null){
            ContentValues values = new ContentValues();
            values.put("charts_name",charts.getName());
            values.put("charts_id",charts.getId());
            values.put("music_num",charts.getCount());
            db.insert("Charts",null,values);
        }
    }
    //读取所有排行榜的信息
    public void loadCharts(List<Charts> list){
        Log.d("MusicDB","inloadCharts");
        list.clear();
        Cursor cursor = db.query("Charts",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                Charts charts = new Charts();
                charts.setName(cursor.getString(cursor.getColumnIndex("charts_name")));
                charts.setCount(cursor.getInt(cursor.getColumnIndex("music_num")));
                charts.setId(cursor.getInt(cursor.getColumnIndex("charts_id")));
                list.add(charts);
            }while(cursor.moveToNext());
        }
    }
}
