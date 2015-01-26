package com.example.music.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.Adapter.ChartsAdapter;
import com.example.music.R;
import com.example.music.db.MusicDB;
import com.example.music.model.Charts;
import com.example.music.util.HttpCallbackListener;
import com.example.music.util.HttpUtil;
import com.example.music.util.Utility;

import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InternetChartsActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<Charts> chartsList = new ArrayList<Charts>();      //排行榜list
    private MusicDB musicDB;      //数据库句柄
    //排行榜网址
    public final static String CHARTSADDRESS = "http://box.zhangmen.baidu.com/x?op=3&list_cat=1&.r=%25f";
    //排行榜适配器
    private ChartsAdapter adapter;
    //排行榜列表
    private ListView listView;
    //标题
    private TextView titletext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_list_view);
        Log.d("InternetChartsActivity","before adapter");
        //listView和适配器和布局文件相关联
        adapter = new ChartsAdapter(InternetChartsActivity.this,R.layout.internet_charts_content,chartsList);
        listView = (ListView)findViewById(R.id.internet_list_view);
        titletext = (TextView)findViewById(R.id.internet_list_title);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        //获取数据数据,第一次向数据库中写数据
        musicDB = MusicDB.getInstance(this);
        Log.d("InternetChartsActivity","before queryCharts");
        titletext.setText("排行榜");
        showProgressDialog();
        queryChartsInfo();        //加载排行榜信息
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
            HttpUtil.sendRequestWithHttpURLConnection(CHARTSADDRESS,new HttpCallbackListener() {
                @Override
                public void onFinish(Object response) {
                    if(Utility.handleChartsResponseWithSAX(musicDB,(InputStream)response)){
                        //回到UI线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryChartsInfo();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(InternetChartsActivity.this,"加载排行榜失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(InternetChartsActivity.this,InternetMusicActivity.class);
        intent.putExtra("ChartsInstanceState",chartsList.get(position));
        startActivity(intent);
    }
}
