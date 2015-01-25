package com.example.music.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public class MusicOpenHelper extends SQLiteOpenHelper{
    //排行榜建表
    public static final String CREATE_CHARTS = "create table Charts ("
//            + "id integer primary key autoincrement, "
            + "charts_name text, "
            + "charts_id integer, "
            + "music_num integer)";
    //本地音乐表

    public MusicOpenHelper(Context context,String name,CursorFactory factory,int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHARTS);     //创建排行榜表单
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
