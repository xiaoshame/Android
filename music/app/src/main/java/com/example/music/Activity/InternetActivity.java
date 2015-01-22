package com.example.music.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.music.Adapter.MusicAdapter;
import com.example.music.R;

import java.util.ArrayList;
import java.util.List;

public class InternetActivity extends ActionBarActivity {
    //网络歌曲列表信息
    List<MusicInfo> musicInfoList = new ArrayList<MusicInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取网络音乐列表信息
        InitInternetMusicInfoList();
        //listView和适配器和布局文件相关联
        MusicAdapter adapter = new MusicAdapter(InternetActivity.this,R.layout.internet_music_list,musicInfoList);
        ListView listView = (ListView)findViewById(R.id.internet_music_list);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void InitInternetMusicInfoList(){

    }
}
