package com.example.music.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.music.activity.MusicTabActivity;
import com.example.music.db.MusicDB;
import com.example.music.model.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by xiaozhisong on 15-2-6.
 * 下载线程
 */
public class AsyncUtility extends AsyncTask<String,Integer,String> {

    //用于管理下载数据
    private Task task;
    //用于查询数据库
    private MusicDB musicDB;
    private Context context;

    //歌曲名
    String musicName;
    //作者和基本信息
    String musicAuthor;
    //下载得数据长度
    private int curSize = 0;
    private int length = 0;
    //歌曲数据保存的路径
    private String path = null;
    //文件管理工具
    FileUtility fileUtility = null;
    //默认下载还没有完成
    private boolean finished = false;
    //默认这个下载线程停止
    private boolean downloading = false;
    //进度
    private int progress = 0;

    //下载链接，也是数据库和map中的查询一句
    private String downloadUrl = null;
    //启动新的任务
    public AsyncUtility(Context context,String musicName,String musicAuthor) {
        super();
        musicDB = MusicDB.getInstance(context);
        this.context = context;
        this.musicName = musicName;
        this.musicAuthor = musicAuthor;
        task = Task.getInstance(context);
    }
    //未完成的任务申请下载线程
    public AsyncUtility(Context context,String musicName,String musicAuthor,int progress) {
        super();
        musicDB = MusicDB.getInstance(context);
        this.context = context;
        this.musicName = musicName;
        this.progress = progress;
        this.musicAuthor = musicAuthor;
        task = Task.getInstance(context);
    }

    //此函数中得代码都运行在线程中,函数的返回值做为onPostExecute方法的传入参数
    @Override
    protected String doInBackground(String... params) {
        //开始下载
        downloading = true;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        fileUtility = new FileUtility();
        //创建文件夹,获取保存的路径
        path = fileUtility.createSDDir("Music") + "/" + musicName;
        try{
            downloadUrl = params[0];
            Log.d("doInBackground",downloadUrl);
            //开始下载位置
            int startPosition = 0;
            Log.d("doInBackground",path);
            //获取已下载的数据长度
            startPosition = musicDB.getDownloadedBytes(downloadUrl);
            //获取需要下载得数据长度
            length = getContentLength();
            //判断是中断的任务还是新启的任务
            if(startPosition == 0){
                //新建立的下载任务,添加到数据库
                saveDownloading(path + ".dat",0,length,"start");
            }else if(startPosition == length){
                //查询结果是这个任务已经下载完成了，给界面发送消息
                //发送广播通知activity有歌曲需要下载
                Intent intent = new Intent(MusicTabActivity.ACTION_MUSIC_DOWN);
                intent.putExtra("musicName",musicName);
                //发送一个广播
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                return null;
            }

            //开始从中断的地方开始下载
            URL url = new URL(downloadUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setAllowUserInteraction(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(8000);
            httpURLConnection.setRequestProperty("User-Agent","NetFox");
            httpURLConnection.setRequestProperty("Range", "bytes=" + startPosition + "-");
            inputStream = httpURLConnection.getInputStream();

            File outFile = new File(path + ".dat");
            //使用java中的RandomAccessFile对文件进行随机读写操作
            randomAccessFile = new RandomAccessFile(outFile,"rw");
            //设置开始写文件的位置
            randomAccessFile.seek(startPosition);
            //长度太小，限制了下载速度
            byte[] buf = new byte[1024 * 100];
            int read = 0;
            curSize = startPosition;
            while (false == finished){
                while(false == downloading){
                    //暂停
                    Thread.sleep(500);
                }
                read = inputStream.read(buf);
                if(-1 == read){
                    break;
                }
                randomAccessFile.write(buf,0,read);
                curSize += read;
                //当调用这个方法的时候会自动去调用onProgressUpdate方法，传递下载进度
                publishProgress((int)(curSize * 100.0f / length));
                if(curSize == length){
                    break;
                }
                Thread.sleep(500);
                //更新数据库中的信息
                musicDB.updateDownloadingInfo(downloadUrl,curSize,"pause");
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            if(curSize == length){
                finished = true;
                Log.d("doInBackground","finished");
                //下载完成
                deleteDownloading();
            }else{
                musicDB.updateDownloadingInfo(downloadUrl,curSize,"pause");
            }
            if(null != inputStream){
                try{
                    inputStream.close();
                    if(null != randomAccessFile){
                        randomAccessFile.close();
                    }
                    if(null != httpURLConnection){
                        httpURLConnection.disconnect();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        // 这里的返回值将会被作为onPostExecute方法的传入参数
        return downloadUrl;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //当一个下载任务成功下载完成的时候回来调用这个方法
    @Override
    protected void onPostExecute(String s) {
//        try{
//            Log.d("onPostExecute",downloadUrl);
//            if(true == task.containsTask(downloadUrl) && curSize == length){
//                Log.d("onPostExecute",curSize+"");
//                deleteDownloading();
//            }
//        }catch (NumberFormatException e){
//            e.printStackTrace();
//        }
        super.onPostExecute(s);
    }

    //更新下载进度，当publicProgress方法被调用时自动调用此方法
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progress = values[0];
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    //取消一个下载任务，系统自己调用
    @Override
    protected void onCancelled() {
        super.onCancelled();
        //查询这个链接是否在任务map中
        if(true == task.containsTask(downloadUrl)){
            task.removeTask(downloadUrl);
            musicDB.deleteDownloadingInfo(downloadUrl);
        }
    }

    //获取要下载内容得长度
    private int getContentLength(){
        HttpURLConnection connection = null;
        try{
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection)url.openConnection();
            int length = connection.getContentLength();
            connection.disconnect();
            return length;
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        return 0;
    }

    //保存下载的信息
    private void saveDownloading(String path,int downloadedBytes,int totalBytes,String downloadStatus){
        musicDB.saveDownloadingInfo(musicName,musicAuthor,downloadUrl,path,downloadedBytes,totalBytes,downloadStatus);
        task.putTask(downloadUrl,this);
    }


    //删除完成的任务
    private void deleteDownloading(){
        task.removeTask(downloadUrl);
        //修改数据路径和歌曲状态
        fileUtility.changeFileName(path + ".dat",path + ".mp3");
        musicDB.updateDownloadingInfo(downloadUrl,curSize,path + ".mp3","finished");
    }

    //获取下载的歌曲名
    public String getMusicName(){
        return musicName;
    }
    //获取下载状态
    public boolean isDownloading() {
        return downloading;
    }

    //暂停下载
    public void stopDownloading() {
        downloading = false;
    }

    //继续下载
    public void continueDownloading(){
        downloading = true;
    }

    public int getProgress() {
        return progress;
    }
}
