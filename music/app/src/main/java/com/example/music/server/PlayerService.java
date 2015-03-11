package com.example.music.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.music.R;
import com.example.music.activity.MusicTabActivity;
import com.example.music.db.MusicDB;
import com.example.music.model.MusicInfo;

import java.util.List;

/**
 * Created by xiaozhisong on 15-2-27.
 */
public class PlayerService extends Service{
    //用于查询数据库
    private MusicDB musicDB;
    private Context context;

    private MediaPlayer mediaPlayer;    //媒体播放器对象
    private List<MusicInfo> musicInfos;   //存放MusicInfo对象的集合
    private MediaBinder mediaBinder = new MediaBinder();    //播放控制

    private int current = 0;                       //记录当前正在播放的音乐
    private int currentTime = 0;                   //正在播放的音乐的当前时间
    private String path;                           //音乐文件路径

    //通知栏控制
    private Notification notification;
    private RemoteViews contentView;
    private NotificationManager notificationManager;
    private boolean isNotificationOnGoing = false;

    public class MediaBinder extends Binder{
        //更新播放列表
        public void RefreshMusicInfos(List<MusicInfo> musicInfoList){
            musicInfos = musicInfoList;
        }
        //开始播放
        public boolean startPlay(){
            //播放默认歌曲或继续上次继续播放
            if(musicInfos.size() == 0){
                //播放列表是空的
                Toast.makeText(context,"本地没有歌曲，请下载歌曲后播放",Toast.LENGTH_SHORT).show();
                current = 0;
                currentTime = 0;
                return false;
            }else{
                play();
                return true;
            }

        }
        //播放指定的歌曲
        public void playSelectedMusic(int position){
            if(position > musicInfos.size() - 1){
                return;
            }
            if(current != position){
                currentTime = 0;
            }
            current = position;
            play();
        }
        //暂停
        public void pausePlay(){
            //暂停正在播放的歌曲
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                //更新通知
                updateNotification();
            }
        }
        //下一首
        public void toNext(){
            current++;
            if(current > musicInfos.size() - 1){
                //变为第一首的位置继续播放
                current = 0;
            }
            currentTime = 0;
            play();
        }
        //上一首
        public void toPrevious(){
            current--;
            if(current < 0){
                //变为最后一首的位置继续播放
                current = musicInfos.size() - 1;
            }
            currentTime = 0;
            play();
        }
        //有人拖动Seekbar,快进功能，修改播放的位置
        public void changeProgress(int changeTime){
            currentTime = changeTime;
            play();
        }

        public boolean isPlay(){
            if(mediaPlayer == null){
                return false;
            }else{
                return mediaPlayer.isPlaying();
            }
        }

        public void deleteListItem(int position,MusicInfo musicInfo){
            //删除正在播放的歌曲，切换到下一首
            if(mediaPlayer.isPlaying() && current == position && musicInfos.size() != 0){
                //切换歌曲，当前播放时间修改为0
                currentTime = 0;
                if(current > musicInfos.size() - 1){
                    current = 0;
                }
                playSelectedMusic(current);
            }
            //删除数据库中的数据
            musicDB.deleteDownloadedInfoWithLocalPath(musicInfo.getPath());
        }

        //取消通知栏
        public void cancleNotification(){
            notificationManager.cancel(1);
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1){
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    //获取当前音乐播放的位置
                    currentTime = mediaPlayer.getCurrentPosition();
                    Intent intent = new Intent(MusicTabActivity.ACTION_MUSIC_CURRENTTIME);
                    intent.putExtra("CurrentTime",currentTime);
                    //发送播放进度更新广播
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    //1秒钟发送一次进度更新广播
                    handler.sendEmptyMessageDelayed(1,1000);   //形成一个循环
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mediaBinder;
    }

    @Override
    //服务创建时调用
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification();
        musicDB = MusicDB.getInstance(context);
        mediaPlayer = new MediaPlayer();
        //设置音乐播放完成时的监听器
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //自动播放下一首
                current++;
                if(current > musicInfos.size() - 1){
                    //变为第一首的位置继续播放
                    current = 0;
                }
                currentTime = 0;
                play();
            }
        });
        handler.sendEmptyMessage(1);
    }

    @Override
    //服务启动时调用
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    //服务销毁时调用
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    //播放音乐
    private void play(){
        try{
            path = musicInfos.get(current).getPath();
            mediaPlayer.reset();    //把各项参数恢复到初始状态
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            //注册一个监听器
            mediaPlayer.setOnPreparedListener(new PreparedListener());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //实现一个OnPrepareLister接口，当音乐准备好的时候开始播放
    private final class PreparedListener implements OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //开始播放
            mediaPlayer.start();
            //如果音乐不是从头播放
            if(currentTime > 0){
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent = new Intent(MusicTabActivity.ACTION_MUSIC_CURRENTALLTIME);
            intent.putExtra("CurrentAllTime",mediaPlayer.getDuration());
            MusicInfo musicInfo = musicInfos.get(current);
            intent.putExtra("MusicBaseInfo",musicInfo.getMusicName() + " " + musicInfo.getMusicAuthor());
            //发送播放进度更新广播
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            handler.sendEmptyMessage(1);
            //更新通知
            updateNotification();
        }
    }

    //通知栏控制播放
    public void updateNotification(){
        contentView = new RemoteViews(getPackageName(), R.layout.notification_control);

        //建立意图，action标记为Play
        Intent intentPlay = new Intent("com.example.music.play");
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this,0,intentPlay,0);
        //为play按钮注册事件
        contentView.setOnClickPendingIntent(R.id.bt_notic_play,pendingIntentPlay);

        Intent intentNext = new Intent("com.example.music.next");
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(this,0,intentNext,0);
        contentView.setOnClickPendingIntent(R.id.bt_notic_next,pendingIntentNext);

        Intent intentPrevious = new Intent("com.example.music.previous");
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(this,0,intentPrevious,0);
        contentView.setOnClickPendingIntent(R.id.bt_notic_previous,pendingIntentPrevious);

        Intent intentClose = new Intent("com.example.music.close");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(this,0,intentClose,0);
        contentView.setOnClickPendingIntent(R.id.music_close,pendingIntentClose);

        //初始化通知
        //根据播放状态，来决定 暂停/开始 图标
        if(mediaPlayer.isPlaying()){
            notification.icon = R.drawable.player_pause_highlight;
            contentView.setInt(R.id.bt_notic_play, "setBackgroundResource", R.drawable.player_pause_highlight);
        }else{
            notification.icon = R.drawable.player_play_highlight;
            contentView.setInt(R.id.bt_notic_play,"setBackgroundResource",R.drawable.player_play_highlight);
        }
        if(current < musicInfos.size()){
            MusicInfo musicInfo = musicInfos.get(current);
            contentView.setTextViewText(R.id.music_base_info,musicInfo.getMusicName() + " " + musicInfo.getMusicAuthor());
        }
        //显示歌曲封面
        showMusicAlbumPicture();
        notification.contentView = contentView;
        //设置通知点击或滑动时不被清除
        notification.flags = notification.FLAG_NO_CLEAR;
        //开启通知
        notificationManager.notify(1,notification);
    }

    //通过内容提供器获取指定的歌曲的albumId，如果歌曲的信息不再内容提供器中，此方法将失效
    private void showMusicAlbumPicture(){
        String musicPath = null;
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor.moveToFirst()){
            do{
                //通过Cursor获取路径，如果路径相同则break
                musicPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                if(musicPath.equals(path)){
                    //此时cursorPosition便是指向路径所指向的Cursor
                    break;
                }
            }while(cursor.moveToNext());
        }
        if(musicPath != null && musicPath.equals(path)){
            //获取歌曲专辑id
            int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            //通过albumId查找albumArt
            Bitmap bitmap = null;
            String albumArt = getAlbumArt(albumId);
            if(albumArt != null){
                bitmap = BitmapFactory.decodeFile(albumArt);
                contentView.setImageViewBitmap(R.id.music_album_picture,bitmap);
                return;
            }
        }
        contentView.setImageViewResource(R.id.music_album_picture,R.drawable.icon_album_default);
    }

    //通过albumId查找albumArt，如果找不到返回null
    private String getAlbumArt(int albumId){
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cursor = getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(albumId)),
                projection,null,null,null);
        String albumArt = null;
        if(cursor.getCount() > 0 && cursor.getColumnCount() > 0){
            cursor.moveToNext();
            albumArt = cursor.getString(0);
        }
        cursor.close();
        cursor = null;
        return albumArt;
    }
}
