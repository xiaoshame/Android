package com.example.music.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


import com.example.music.adapter.InternetChartsAdapter;
import com.example.music.R;
import com.example.music.db.MusicDB;
import com.example.music.model.Charts;
import com.example.music.util.HttpCallbackListener;
import com.example.music.util.HttpUtility;
import com.example.music.util.XmlUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhisong on 15-1-31.
 */
public class InternetChartsListFragment extends BaseFragment implements OnItemClickListener{
    private List<Charts> chartsList = new ArrayList<Charts>();      //排行榜list
    private MusicDB musicDB;      //数据库句柄
    //排行榜网址
    public final static String CHARTSADDRESS = "http://box.zhangmen.baidu.com/x?op=3&list_cat=1&.r=%25f";
    //排行榜适配器
    private InternetChartsAdapter adapter;
    //排行榜列表
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("InternetChartsListFragment","onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        //关联布局文件
        View view = inflater.inflate(R.layout.music_list_layout,null);
        //listView和适配器和布局文件相关联
        adapter = new InternetChartsAdapter(fragmentActivity,R.layout.internet_charts_content,chartsList);
        listView = (ListView)view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        //获取数据库管理对象,第一次向数据库中写数据
        musicDB = MusicDB.getInstance(fragmentActivity);
        Log.d("InternetChartsActivity","before queryCharts");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
        queryChartsInfo();
    }

    //优先查询数据库，如果没有查询服务器
    private void queryChartsInfo(){
        musicDB.loadCharts(chartsList);
        if(chartsList.size() > 0){
            closeProgressDialog();
            //通知适配器数据发生变化，重新加载
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }else{
            //数据库中没有数据从服务器读取数据
            //http://box.zhangmen.baidu.com/x?op=3&list_cat=1&.r=%f获取失败
            //换成http://box.zhangmen.baidu.com/x?op=3&list_cat=1&.r=%25f
            HttpUtility.sendRequestWithHttpURLConnectionInThread(CHARTSADDRESS, "gb2312", new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    if (XmlUtility.handleChartsResponseWithSAX(musicDB, response)) {
                        //回到UI线程
                        fragmentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryChartsInfo();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    fragmentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(fragmentActivity, "加载排行榜失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //启动下一个Fragment显示音乐列表
        InternetMusicListFragment internetMusicListFragment = new InternetMusicListFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //传递参数
        Bundle bundle = new Bundle();
        bundle.putSerializable("ChartsInstanceState",chartsList.get(position));
        internetMusicListFragment.setArguments(bundle);
        //隐藏当前fragment ,add这种方式使这层数据还保存
        ft.hide(this);
        ft.add(R.id.realtabcontent,internetMusicListFragment,"internetmusic");
        //ft.replace(R.id.realtabcontent,internetMusicListFragment);
        //fragment进栈
        ft.addToBackStack(null);
        ft.commit();
    }
}
