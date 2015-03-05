package com.example.music.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.music.adapter.LocalMusicAdapter;
import com.example.music.R;
import com.example.music.db.MusicDB;
import com.example.music.model.MusicInfo;
import com.example.music.util.FragmentCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhisong on 15-1-30.
 * 读取数据库获取本地歌曲列表和地址。获取到地址后，判断文件是否存在，如果存在添加到显示列表中
 */
public class MusicLocalFragment extends BaseFragment implements OnItemClickListener{
    private List<MusicInfo> list;
    //本地音乐适配器
    private LocalMusicAdapter adapter;
    //歌曲列表
    private ListView listView;
    //数据库句柄
    private MusicDB musicDB;
    //传递list数据到activity中
    FragmentCallBack fragmentCallBack = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //关联布局文件
        Log.i("MusicLocalFragment","onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.music_list_layout,null);
        //listView和适配器和布局文件相关联
        list = new ArrayList<MusicInfo>();
        adapter = new LocalMusicAdapter(fragmentActivity,R.layout.local_music_content,list);
        listView = (ListView)view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        //获取数据库句柄
        musicDB = MusicDB.getInstance(fragmentActivity);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
        queryMusicInfo();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //保证Activity实现了回调接口
        try{
            fragmentCallBack = (FragmentCallBack)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //响应点击，点击后播放该音乐
        //发送广播，操作在activity中进行
        fragmentCallBack.playSelectedMusic(position);
    }

    private void queryMusicInfo(){
        musicDB.getLoaclMusicInfo(list);
        //获取到数据后更新列表
        if(list.size() > 0){
            //通知适配器数据发生变化，重新加载
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }
        closeProgressDialog();
        //通过调用接口传递信息
        fragmentCallBack.callbackFun(list);
    }
}
