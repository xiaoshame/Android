package com.example.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.music.activity.MusicTabActivity;
import com.example.music.model.MusicAddress;
import com.example.music.model.MusicInfo;
import com.example.music.R;
import com.example.music.util.HttpUtility;
import com.example.music.util.XmlUtility;

import java.util.List;

/**
 * Created by Lijinpu on 2015/1/22.
 */
public class InternetMusicAdapter extends ArrayAdapter<MusicInfo>{

    private int resourceId;        //关联的配置文件id
    private final Context context;       //父活动
    private List<MusicInfo> list;

    public InternetMusicAdapter(Context context, int textViewResourceId, List<MusicInfo> object){
        super(context,textViewResourceId,object);
        resourceId = textViewResourceId;
        this.context = context;
        list = object;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicInfo musicInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        //下载按钮响应
        OnClick listener = null;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.musicName = (TextView)view.findViewById(R.id.internet_music_name);
            viewHolder.musicBaseInfo = (TextView)view.findViewById(R.id.internet_music_baseinfo);
            viewHolder.musicDown = (Button)view.findViewById(R.id.internet_audio_down);
            //添加代码 查询数据库得到对应的歌曲是否已经下载
            //新建监听对象
            listener = new OnClick();
            viewHolder.musicDown.setOnClickListener(listener);
            view.setTag(viewHolder);
            view.setTag(viewHolder.musicDown.getId(),listener);  //保存监听对象
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
            //重新获取监听对象
            listener = (OnClick)view.getTag(viewHolder.musicDown.getId());
        }
        //convertview不一定和position相匹配，所以数据需要重新设置
        viewHolder.musicName.setText(musicInfo.getMusicName());
        viewHolder.musicBaseInfo.setText(musicInfo.getMusicAuthor() + musicInfo.getMusicOtherInfo());
        listener.setPosition(position);
        viewHolder.musicDown.setEnabled(musicInfo.isDownloadButtonEnable());
        return view;
    }

    class ViewHolder{
        TextView musicName;
        TextView musicBaseInfo;
        Button musicDown;
    }

    //下载按钮的响应
    class OnClick implements View.OnClickListener{
        int position;
        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v){
            v.setEnabled(false);
            //下载对应的歌曲和字幕
            MusicInfo musicInfo = list.get(position);
            //记录点击
            musicInfo.setDownloadButtonEnable();
            //在线程中获取下载地址
            ParseThread parseThread = new ParseThread(musicInfo);
            parseThread.start();
        }
    }

    class ParseThread extends Thread{
        private MusicInfo musicInfo;
        public ParseThread(MusicInfo musicInfo){
            this.musicInfo = musicInfo;
        }
        @Override
        public void run() {
            MusicAddress musicAddress = new MusicAddress();
            try{
                //获取包含下载地址的XML
                String response = HttpUtility.sendRequestWithHttpClient(musicInfo.getMusicDownXmlUrl(), "gb2312");
                //解析包含下载地址得XML,只提取xml中的一个地址
                XmlUtility.handleMusicAddressWithSAX(musicAddress, response);
                musicInfo.setDownloadUrl(musicAddress.getMusicUrl());
                musicInfo.setMusicType(musicAddress.getType());

                //发送广播通知activity有歌曲需要下载
                Intent intent = new Intent(MusicTabActivity.ACTION_MUSIC_DOWN);
                intent.putExtra("MusicInfoDown",musicInfo);
                //发送一个广播
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
