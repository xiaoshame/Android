package com.example.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.music.activity.MusicTabActivity;
import com.example.music.R;
import com.example.music.model.DownloadInfo;

import java.util.List;

/**
 * Created by xiaozhisong on 15-2-10.
 */
public class InternetDownloadAdapter extends ArrayAdapter<DownloadInfo> {
    private int resourceId;        //关联的配置文件id
    private final Context context;       //父活动
    private List<DownloadInfo> list;

    public InternetDownloadAdapter(Context context, int textViewResourceId, List<DownloadInfo> object){
        super(context,textViewResourceId,object);
        resourceId = textViewResourceId;
        this.context = context;
        list = object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadInfo downloadInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        //下载按钮响应
        OnClick listener = null;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.musicName = (TextView)view.findViewById(R.id.music_down_name);
            viewHolder.progressBar = (ProgressBar)view.findViewById(R.id.music_down_progress);
            viewHolder.musicDownControl = (Button)view.findViewById(R.id.music_down_control);
            viewHolder.progressText = (TextView)view.findViewById(R.id.music_down_progresstext);
            //添加代码 查询数据库得到对应的歌曲是否已经下载
            //新建监听对象
            listener = new OnClick();
            viewHolder.musicDownControl.setOnClickListener(listener);
            view.setTag(viewHolder);
            view.setTag(viewHolder.musicDownControl.getId(),listener);  //保存监听对象
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
            //重新获取监听对象
            listener = (OnClick)view.getTag(viewHolder.musicDownControl.getId());
        }
        viewHolder.musicName.setText(downloadInfo.getMusicName());
        viewHolder.progressBar.setProgress(downloadInfo.getProgress());
        viewHolder.progressText.setText(downloadInfo.getProgress()+"%");
        if(downloadInfo.isDownloading()){
            //正在下载
            viewHolder.musicDownControl.setText("停止");
        }else{
            viewHolder.musicDownControl.setText("下载");
        }
        listener.setPosition(position);
        return view;
    }

    class ViewHolder{
        TextView musicName;
        TextView progressText;
        ProgressBar progressBar;
        Button musicDownControl;
    }

    //下载按钮的响应
    class OnClick implements View.OnClickListener{
        int position;
        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v){
            //暂停功能
            DownloadInfo downloadInfo = list.get(position);
            String downloadUrl = downloadInfo.getDownloadUrl();
            Intent intent = null;
            Button musicDownControl = (Button)v;
            if(downloadInfo.isDownloading()){
                //发送广播通知activity停止下载指定歌曲
                intent = new Intent(MusicTabActivity.ACTION_MUSIC_STOPDOWN);
                //修改指定按钮显示
                musicDownControl.setText("下载");
            }else{
                //发送广播通知activity继续下载指定歌曲
                intent = new Intent(MusicTabActivity.ACTION_MUSIC_CONTINUEDOWN);
                //修改指定按钮显示
                musicDownControl.setText("停止");
            }
            intent.putExtra("DownloadUrl",downloadUrl);
            //发送一个广播
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }
}
