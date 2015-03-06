package com.example.appexplorer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ShowAppActivity extends Activity implements View.OnClickListener,Runnable{
    //GridView
    private GridView gridView;
    //GridView 适配器
    private GridViewAdapter gridViewAdapter;
    //用户app信息列表
    private List<PackageInfo> userPackageInfos;
    //所有应用程序列表
    private List<PackageInfo> allPackageInfos;
    //app种类按钮
    private ImageButton ib_change_category;
    //显示app种类的标记(默认只显示所有的app)
    private boolean allApplication = true;

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
                allApplication = false;
                setProgressBarIndeterminateVisibility(false);
                gridViewAdapter.notifyDataSetChanged();
            }

            if(msg.what == DELETE_APP){
                System.out.println("Delete App Success!");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.show_app_grid);
        //设置进度信息
        setProgressBarIndeterminateVisibility(true);
        gridView = (GridView)findViewById(R.id.gv_apps);
        //设置适配器
        userPackageInfos = new ArrayList<PackageInfo>();
        gridViewAdapter = new GridViewAdapter(ShowAppActivity.this,userPackageInfos);
        gridView.setAdapter(gridViewAdapter);
        ib_change_category = (ImageButton)findViewById(R.id.ib_change_category);
        ib_change_category.setOnClickListener(this);

        scanPackageInfo();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ib_change_category:
                //获取所有应用程序信息
                allPackageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                if(true == allApplication){
                    //显示用户安装的app
                    ib_change_category.setImageResource(R.drawable.user);
                    allApplication = false;
                    gridView.setAdapter(gridViewAdapter);
                    scanPackageInfo();
                }else{
                    ib_change_category.setImageResource(R.drawable.all);
                    allApplication = true;
                    //设置适配器
                    gridView.setAdapter(new GridViewAdapter(ShowAppActivity.this,allPackageInfos));
                }
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
        allPackageInfos = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        userPackageInfos.clear();
        //扫描用户安装的应用程序信息
        for(int i = 0;i < allPackageInfos.size();i++){
            PackageInfo temp = allPackageInfos.get(i);
            ApplicationInfo applicationInfo = temp.applicationInfo;
            if((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                //system app
            }else if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                //Non-system app
                userPackageInfos.add(temp);
            }
        }
//        try{
//            Thread.currentThread().sleep(2000);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        handler.sendEmptyMessage(SEARCH_USERAPP);
//        try{
//            Thread.currentThread().sleep(5000);
//            handler.sendEmptyMessage(DELETE_APP);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
    }
}
