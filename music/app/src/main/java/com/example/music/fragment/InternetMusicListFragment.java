package com.example.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.music.adapter.InternetMusicAdapter;
import com.example.music.R;
import com.example.music.model.Charts;
import com.example.music.model.MusicInfo;
import com.example.music.util.HttpCallbackListener;
import com.example.music.util.HttpUtility;
import com.example.music.util.XmlUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhisong on 15-2-2.
 */
public class InternetMusicListFragment extends BaseFragment implements AdapterView.OnItemClickListener{
    //歌曲查询地址
    private final static String baseAddress = "http://box.zhangmen.baidu.com/x?op=22&listid=";
    //歌曲列表
    private ListView listView;
    //排行榜对象
    private Charts charts;
    //歌曲列表
    private List<MusicInfo> musiclist = new ArrayList<MusicInfo>();
    //listView适配器
    private InternetMusicAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("InternetMusicListFragment","onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        //关联布局文件
        View view = inflater.inflate(R.layout.music_list_layout,null);
        //listView和适配器和布局文件相关联
        adapter = new InternetMusicAdapter(fragmentActivity,R.layout.internet_music_content,musiclist);
        //关联适配器
        listView = (ListView)view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        //获取传递得参数
        charts = (Charts)getArguments().getSerializable("ChartsInstanceState");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
        queryMusicInfo();
    }

    //查询服务器，每次都是查询服务器
    private void queryMusicInfo(){
        musiclist.clear();
        HttpUtility.sendRequestWithHttpURLConnectionInThread(baseAddress + charts.getId(), "gb2312", new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("InternetMusicActivity", "Get Music Msg");
                XmlUtility.handlerMusicResponseWithSAX(musiclist, response);
                closeProgressDialog();
                //更新UI数据
                fragmentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.setSelection(0);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //模拟qq音乐的在线播放
        Log.d("onItemClick", "click");
    }
}
