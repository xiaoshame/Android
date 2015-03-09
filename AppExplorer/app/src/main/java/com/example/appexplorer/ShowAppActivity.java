package com.example.appexplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ShowAppActivity extends Activity implements View.OnClickListener,Runnable,AdapterView.OnItemClickListener{
    //GridView
    private GridView gridView;
    private ListView listView;
    //用户app信息列表
    private List<PackageInfo> userPackageInfos;
    //所有应用程序列表
    private List<PackageInfo> allPackageInfos;
    //app种类按钮
    private ImageButton ib_change_category;
    //list显示样式按钮
    private ImageButton ib_change_view;

    //显示app种类的标记(默认只显示所有的app)
    private boolean isAllApplication = false;
    //app默认显示的样式,默认为GridView样式
    private boolean isListViewStyle = false;

    //进度对话框
    private ProgressDialog progressDialog;

    //消息类型
    private static final int SEARCH_USERAPP = 0;
    private static final int DELETE_APP = 1;
    //Handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //默认显示所有应用程序
            if(msg.what == SEARCH_USERAPP){
                progressDialog.dismiss();
                if(false == isAllApplication){
                    //改变为显示用户安装app和用户升级的系统app
                    //设置适配器
                    if(isListViewStyle){
                        listView.setAdapter(new ListViewAdapter(ShowAppActivity.this,userPackageInfos));
                    }else{
                        gridView.setAdapter(new GridViewAdapter(ShowAppActivity.this,userPackageInfos));
                    }
                    //修改对应的图标
                    ib_change_category.setImageResource(R.drawable.user);
                }else {
                    //改变为显示系统app
                    //设置适配器
                    //设置适配器
                    if(isListViewStyle){
                        listView.setAdapter(new ListViewAdapter(ShowAppActivity.this,allPackageInfos));
                    }else{
                        gridView.setAdapter(new GridViewAdapter(ShowAppActivity.this,allPackageInfos));
                    }
                    //修改对应的图标
                    ib_change_category.setImageResource(R.drawable.all);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.show_app_grid);
        //设置进度信息
        //setProgressBarIndeterminateVisibility(true);
        gridView = (GridView)findViewById(R.id.gv_apps);
        listView = (ListView)findViewById(R.id.lv_apps);
        ib_change_category = (ImageButton)findViewById(R.id.ib_change_category);
        ib_change_category.setOnClickListener(this);
        ib_change_view = (ImageButton)findViewById(R.id.ib_change_view);
        ib_change_view.setOnClickListener(this);

        //设置列表点击响应
        gridView.setOnItemClickListener(this);
        listView.setOnItemClickListener(this);
        scanPackageInfo();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ib_change_category:
                //扫描应用程序信息
                isAllApplication = !isAllApplication;
                scanPackageInfo();
                break;
            case R.id.ib_change_view:
                if(true == isListViewStyle){
                    //隐藏listView
                    listView.setVisibility(View.GONE);
                    //以GridView样式显示
                    gridView.setVisibility(View.VISIBLE);
                    //设置适配器
                    if(true == isAllApplication){
                        gridView.setAdapter(new GridViewAdapter(ShowAppActivity.this,allPackageInfos));
                    }else{
                        gridView.setAdapter(new GridViewAdapter(ShowAppActivity.this,userPackageInfos));
                    }
                }else{
                    //隐藏GridView
                    gridView.setVisibility(View.GONE);
                    //以listView样式显示
                    listView.setVisibility(View.VISIBLE);
                    //设置适配器
                    if(true == isAllApplication){
                        listView.setAdapter(new ListViewAdapter(ShowAppActivity.this,allPackageInfos));
                    }else{
                        listView.setAdapter(new ListViewAdapter(ShowAppActivity.this,userPackageInfos));
                    }
                }
                isListViewStyle = !isListViewStyle;
                break;
            default:
                break;
        }
    }

    private void scanPackageInfo(){
        progressDialog = ProgressDialog.show(this,"请稍候...","正在搜索应用程序信息...",true,false);
        //开启线程进行扫描应用程序信息
        Thread thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run() {
        //获取所有应用程序信息
        allPackageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
        userPackageInfos = new ArrayList<PackageInfo>();
        //扫描用户安装的应用程序信息
        for(int i = 0;i < allPackageInfos.size();i++){
            PackageInfo temp = allPackageInfos.get(i);
            ApplicationInfo applicationInfo = temp.applicationInfo;
            if(0 != (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) ||
                    0 == (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)){
                //被用户升级过的系统app和用户安装的app
                userPackageInfos.add(temp);
            }
        }
        handler.sendEmptyMessage(SEARCH_USERAPP);
    }

    //点击list item

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //弹出一个对话框
        //创建Dialog的构造器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选项");
        builder.setItems(R.array.choice,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PackageInfo packageInfo = null;
                if(false == isAllApplication){
                    packageInfo = userPackageInfos.get(position);
                }else {
                    packageInfo = allPackageInfos.get(position);
                }
                switch (which){
                    case 0:
                        //第一项启动对应程序
                        //获取包名
                        String packageName = packageInfo.packageName;
                        //获取报名
                        ActivityInfo activityInfo =  packageInfo.activities[0];
                        if(activityInfo == null){
                            Toast.makeText(ShowAppActivity.this,"没有可用Activity",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName,activityInfo.name));
                        startActivity(intent);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }
}
