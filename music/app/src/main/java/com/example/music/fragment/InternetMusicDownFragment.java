package com.example.music.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.music.adapter.InternetDownloadAdapter;
import com.example.music.R;
import com.example.music.model.DownloadInfo;
import com.example.music.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaozhisong on 15-1-30.
 * 用于显示下载列表
 */
public class InternetMusicDownFragment extends BaseFragment implements OnItemClickListener{

    //下载列表结构
    private Task task;
    //列表适配器
    private InternetDownloadAdapter adapter;
    //列表数据
    private List<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
    //列表视图
    private ListView listView;
    //定时器
    Timer timer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //关联布局文件
        Log.i("InternetMusicDownFragment","onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.music_list_layout,null);

        //listView和适配器和布局文件相关联
        adapter = new InternetDownloadAdapter(fragmentActivity,R.layout.internet_down_content,downloadInfoList);
        listView = (ListView)view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    //使用fragment的生存期来控制定时器
    @Override
    public void onStart() {
        super.onStart();
        //设置定时器
        startTimer();
    }

    //fragment结束时停止定时器
    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
        timer = null;
    }

    //点击下载页面时才会运行到这里
    //根据task构建list
    private void queryDownloadInfo(){
        //获取下载管理数据类
        task = Task.getInstance(fragmentActivity);
        //为了安全在Task类中获取list数据
        task.refresh(downloadInfoList);
        //更新adapter中的数据
        adapter.notifyDataSetChanged();
    }

    //开启定时器
    private void startTimer(){
        if(timer == null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("schedule","run");
                fragmentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        queryDownloadInfo();
                    }
                });
            }
        },0,1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //添加删除功能
    }
}
