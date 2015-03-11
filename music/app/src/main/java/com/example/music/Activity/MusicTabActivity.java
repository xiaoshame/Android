package com.example.music.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.fragment.InternetChartsListFragment;
import com.example.music.fragment.InternetMusicDownFragment;
import com.example.music.fragment.MusicLocalFragment;
import com.example.music.model.MusicInfo;
import com.example.music.model.Task;
import com.example.music.server.PlayerService;
import com.example.music.server.PlayerService.MediaBinder;
import com.example.music.util.AsyncUtility;
import com.example.music.util.FragmentCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by xiaozhisong on 15-1-30.
 * 控制中心，负责各个fragment之间的数据通信转发，线程与下载fragment的通信
 */
public class MusicTabActivity extends FragmentActivity implements View.OnClickListener,FragmentCallBack,SeekBar.OnSeekBarChangeListener{
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //定义一个布局
    private LayoutInflater layoutInflater;
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {MusicLocalFragment.class, InternetChartsListFragment.class,InternetMusicDownFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_local_btn,R.drawable.tab_internet_btn,R.drawable.tab_down_btn};
    //fragment得标签
    private String mFragmentTag[] = {"localmusic","charts","download"};
    //Tab选项卡的文字
    private String mTextViewArray[] = {"本地音乐","排行榜","下载"};
    //音乐控制按钮
    private Button playBtn;
    private Button previousBtn;
    private Button nextBtn;
    //播放进度信息
    private SeekBar seekBar;
    private TextView currentTimeView;
    private TextView currentAllTimeView;
    private TextView musicBaseInfoView;

    //当前播放的音乐的总时长
    private int currentTime;
    private int currentAllTime = 0;

    //音乐播放控制
    private MediaBinder mediaBinder;
    //在服务未绑定前存放播放列表信息
    private List<MusicInfo> musicInfoList;

    //任务管理类
    private static Task task;
    //广播信息
    public static String ACTION_MUSIC_DOWN = "com.example.music.musicdown";
    public static String ACTION_MUSIC_FINISHED = "com.example.music.musicfinised";
    public static String ACTION_MUSIC_STOPDOWN = "com.example.music.stopdown";
    public static String ACTION_MUSIC_CONTINUEDOWN = "com.example.music.continuedown";
    //音乐播放过程中更新信息
    public static String ACTION_MUSIC_CURRENTTIME = "com.example.music.currenttime";
    public static String ACTION_MUSIC_CURRENTALLTIME = "com.example.music.alltime";

    //删除播放列表中的一项广播
    public static String ACTION_MUSIC_DELETEITEM = "com.example.music.deleteitem";
    //广播管理
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver;

    //通知栏广播接收器
    private NotificationReceiver notificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab_layout);
        playBtn = (Button)findViewById(R.id.music_control_play);
        previousBtn = (Button)findViewById(R.id.music_progress_previous);
        nextBtn = (Button)findViewById(R.id.music_progress_next);
        seekBar = (SeekBar)findViewById(R.id.music_play_seekBar);
        currentTimeView = (TextView)findViewById(R.id.music_play_time);
        currentAllTimeView = (TextView)findViewById(R.id.music_all_time);
        musicBaseInfoView = (TextView)findViewById(R.id.music_base_info);

        playBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        //没开始播放歌曲，禁止拖动进度条
        seekBar.setEnabled(false);
        //生成下载管理类
        task = Task.getInstance(this);
        initView();
        //绑定服务
        Intent bindIntent = new Intent(this, PlayerService.class);
        bindService(bindIntent,serviceConnection,BIND_AUTO_CREATE);//绑定服务
        //注册Receiver
        initBroadcastListener();
        initNotificationReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭通知栏通知
        mediaBinder.cancleNotification();
        //注销本地广播接收器
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        //注销通知栏的广播接收器
        this.unregisterReceiver(notificationReceiver);
        //解除服务绑定
        unbindService(serviceConnection);
    }

    private String makeTimeString(int time){
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(date);
    }

    //activity负责消息下发
    private void initBroadcastListener(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_MUSIC_DOWN);
        intentFilter.addAction(ACTION_MUSIC_FINISHED);
        intentFilter.addAction(ACTION_MUSIC_STOPDOWN);
        intentFilter.addAction(ACTION_MUSIC_CONTINUEDOWN);
        intentFilter.addAction(ACTION_MUSIC_CURRENTTIME);
        intentFilter.addAction(ACTION_MUSIC_CURRENTALLTIME);
        intentFilter.addAction(ACTION_MUSIC_DELETEITEM);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //对广播进行处理
                if(intent.getAction().equals(ACTION_MUSIC_DOWN)){
                    Log.d("initBroadcastListener","recevice broadcast");
                    //在下载页面添加一个下载项
                    MusicInfo musicInfo = (MusicInfo)intent.getSerializableExtra("MusicInfoDown");
                    if(null == musicInfo.getDownloadUrl()){
                        Toast.makeText(context,"获取" + musicInfo.getMusicName()+"下载地址失败",Toast.LENGTH_SHORT).show();
                    }else if(false == task.containsTask(musicInfo.getDownloadUrl())) {
                        Log.d("initBroadcastListener", "download music " + musicInfo.getMusicName());
                        //如果下载项不再记录列表中添加进去
                        AsyncUtility asyncUtility = new AsyncUtility(context,musicInfo.getMusicName(), musicInfo.getMusicAuthor());
                        //当调用AsyncTask的方法execute时，就会去自动调用doInBackground方法
                        asyncUtility.executeOnExecutor(Executors.newCachedThreadPool(), musicInfo.getDownloadUrl());
                    }
                }else if(intent.getAction().equals(ACTION_MUSIC_FINISHED)){
                    Toast.makeText(context,intent.getStringExtra("musicName") + "已经下载",Toast.LENGTH_SHORT).show();
                }else if(intent.getAction().equals(ACTION_MUSIC_STOPDOWN)){
                    //停止指定歌曲下载
                    task.stopTask(intent.getStringExtra("DownloadUrl"));
                }else if(intent.getAction().equals(ACTION_MUSIC_CONTINUEDOWN)){
                    //继续指定歌曲下载
                    task.continueTask(intent.getStringExtra("DownloadUrl"));
                }else if(intent.getAction().equals(ACTION_MUSIC_CURRENTTIME)){
                    int currentTime = intent.getIntExtra("CurrentTime",0);
                    //更新当前时间
                    currentTimeView.setText(makeTimeString(currentTime));
                    //更新播放进度
                    seekBar.setProgress(currentTime * seekBar.getMax() / currentAllTime);
                }else if(intent.getAction().equals(ACTION_MUSIC_CURRENTALLTIME)){
                    currentAllTime = intent.getIntExtra("CurrentAllTime",0);
                    //更新总时间
                    currentAllTimeView.setText(makeTimeString(currentAllTime));
                    musicBaseInfoView.setText(intent.getStringExtra("MusicBaseInfo"));
                }else if(intent.getAction().equals(ACTION_MUSIC_DELETEITEM)){
                    if(musicInfoList.size() == 0 || mediaBinder.isPlay() == false){
                        //暂停
                        mediaBinder.pausePlay();
                        //清空界面播放信息
                        currentAllTimeView.setText("00:00");
                        currentTimeView.setText("00:00");
                        seekBar.setProgress(0);
                        seekBar.setEnabled(false);
                        //更换成开始播放图片
                        playBtn.setBackgroundResource(R.drawable.player_play_highlight);
                    }
                    //删除对应项
                    int position = intent.getIntExtra("Position",0);
                    MusicInfo musicInfo = (MusicInfo)intent.getSerializableExtra("MusicInfo");
                    mediaBinder.deleteListItem(position,musicInfo);
                }
            }
        };
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }
    
    private void initView(){
        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0;i < count;i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mFragmentTag[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec,fragmentArray[i],null);
            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                //如果上一个页面是歌曲列表页面，需要将该页面隐藏
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fm.findFragmentByTag("internetmusic");
                //选择排行榜页面
                if(true == tabId.equals(mFragmentTag[1])){
                    if(fragment != null){
                        ft.show(fragment);
                    }
                }else{
                    //切换到本地或下载页面
                    if(fragment != null){
                        ft.hide(fragment);
                    }
                }
                ft.commit();
            }
        });
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextViewArray[index]);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.music_control_play:
                //播放或停止，默认是停止
                if(mediaBinder.isPlay()){
                    //正在播放，暂停
                    mediaBinder.pausePlay();
                    //更换成开始播放图片
                    playBtn.setBackgroundResource(R.drawable.player_play_highlight);
                }else{
                    //暂停状态，播放默认歌曲
                    if(mediaBinder.startPlay()){
                        //更换成暂停图片,播放失败不替换
                        seekBar.setEnabled(true);
                        playBtn.setBackgroundResource(R.drawable.player_pause_highlight);
                    }
                }
                break;
            case R.id.music_progress_next:
                //下一首
                mediaBinder.toNext();
                break;
            case R.id.music_progress_previous:
                //上一首
                mediaBinder.toPrevious();
                break;
            default:
                break;
        }
    }

    //拖动进度条响应函数
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //跟随进度条的拖动，修改当前播放时间
        currentTime = progress * currentAllTime / seekBar.getMax();
        currentTimeView.setText(makeTimeString(currentTime));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //开始拖动进度条，暂停播放
        mediaBinder.pausePlay();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaBinder.changeProgress(currentTime);
        playBtn.setBackgroundResource(R.drawable.player_pause_highlight);
    }

    //活动与服务成功绑定或解除绑定的时候调用
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaBinder = (MediaBinder)service;
            mediaBinder.RefreshMusicInfos(musicInfoList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //fragment与Activity的沟通方式
    @Override
    public void callbackFun(List<MusicInfo> value) {
        if(mediaBinder != null){
            mediaBinder.RefreshMusicInfos(value);
        }else{
            musicInfoList = value;
        }
    }

    @Override
    public void playSelectedMusic(int position) {
        if(mediaBinder != null){
            mediaBinder.playSelectedMusic(position);
            //更换成暂停图片,播放失败不替换
            seekBar.setEnabled(true);
            playBtn.setBackgroundResource(R.drawable.player_pause_highlight);
        }
    }

    private void initNotificationReceiver(){
        notificationReceiver = new NotificationReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.music.play");
        intentFilter.addAction("com.example.music.next");
        intentFilter.addAction("com.example.music.previous");
        intentFilter.addAction("com.example.music.close");
        this.registerReceiver(notificationReceiver,intentFilter);
    }
    //接收通知栏发送的广播
    public class NotificationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String strContrl = intent.getAction();    //获取action标记，用户区分点击事件
            if("com.example.music.play".equals(strContrl)){
                if(mediaBinder.isPlay()){
                    mediaBinder.pausePlay();
                    //更换成开始播放图片
                    playBtn.setBackgroundResource(R.drawable.player_play_highlight);
                }else{
                    if(mediaBinder.startPlay()){
                        //更换成暂停图片,播放失败不替换
                        seekBar.setEnabled(true);
                        playBtn.setBackgroundResource(R.drawable.player_pause_highlight);
                    }
                }
            }else if("com.example.music.next".equals(strContrl)){
                mediaBinder.toNext();
            }else if("com.example.music.previous".equals(strContrl)){
                mediaBinder.toPrevious();
            }
            if("com.example.music.close".equals(strContrl)){
                //关闭程序
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //如果当前fragment是网络歌曲页面,调用默认，返回网络排行榜页面
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag("internetmusic");
        if(fragment == null && mediaBinder.isPlay()){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //如果intent不指定category，那么无论intent filter的内容是什么都应该是匹配的。
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }else {
            super.onBackPressed();
        }
    }
}
