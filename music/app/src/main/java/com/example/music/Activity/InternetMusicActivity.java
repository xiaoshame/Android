package com.example.music.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.music.Adapter.MusicAdapter;
import com.example.music.R;
import com.example.music.model.Charts;
import com.example.music.model.MusicInfo;
import com.example.music.util.DownFile;
import com.example.music.util.HttpCallbackListener;
import com.example.music.util.HttpUtil;
import com.example.music.util.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhisong on 15-1-26.
 */
public class InternetMusicActivity extends BaseActivity {
    private TextView textTitle;
    private ListView listView;
    //排行榜对象
    private Charts charts;
    //歌曲列表
    private List<MusicInfo> musiclist = new ArrayList<MusicInfo>();
    //listView适配器
    private MusicAdapter adapter;
    //歌曲查询地址
    private final static String baseAddress = "http://box.zhangmen.baidu.com/x?op=22&listid=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_list_view);
        listView = (ListView)findViewById(R.id.internet_list_view);
        textTitle = (TextView)findViewById(R.id.internet_list_title);
        Intent intent = getIntent();
        //获取点击的排行榜对象
        charts = (Charts)intent.getSerializableExtra("ChartsInstanceState");
        //获取设置标题
        textTitle.setText(charts.getName());
        //listView和适配器和布局文件相关联
        adapter = new MusicAdapter(InternetMusicActivity.this,R.layout.internet_music_content,musiclist);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog();
        queryMusicInfo();
    }

    //查询服务器，每次都是查询服务器
    private void queryMusicInfo(){
        musiclist.clear();
        HttpUtil.sendRequestWithHttpURLConnection(baseAddress + charts.getId(),new HttpCallbackListener() {
            @Override
            public void onFinish(Object response) {
                Log.d("InternetMusicActivity","Get Music Msg");
                //DownFile.DownFileToPath(InternetMusicActivity.this,(InputStream)response);
                Utility.handlerMusicResponseWithSAX(musiclist,(InputStream)response);
                closeProgressDialog();
                //更新UI数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
            @Override
            public void onError(Exception e) {
                closeProgressDialog();
                e.printStackTrace();
            }
        });
    }
}
