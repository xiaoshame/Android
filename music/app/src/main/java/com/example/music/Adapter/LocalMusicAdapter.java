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

import com.example.music.R;
import com.example.music.activity.MusicTabActivity;
import com.example.music.model.MusicInfo;


import java.util.List;

/**
 * Created by xiaozhisong on 15-2-15.
 * 网络歌曲适配器和本地音乐适配器可以改写成使用一个适配器
 */
public class LocalMusicAdapter extends ArrayAdapter<MusicInfo>{
    private int resourceId;        //关联的配置文件id
    private final Context context;       //父活动
    private List<MusicInfo> list;

    public LocalMusicAdapter(Context context, int textViewResourceId, List<MusicInfo> object){
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
        //删除按钮响应
        OnClick listener = null;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.musicName = (TextView)view.findViewById(R.id.local_music_name);
            viewHolder.musicBaseInfo = (TextView)view.findViewById(R.id.local_music_baseinfo);
            viewHolder.musicDelete = (Button)view.findViewById(R.id.loacl_audio_delete);

            //新建监听对象
            listener = new OnClick();
            viewHolder.musicDelete.setOnClickListener(listener);
            view.setTag(viewHolder);
            view.setTag(viewHolder.musicDelete.getId(),listener);  //保存监听对象
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
            //重新获取监听对象
            listener = (OnClick)view.getTag(viewHolder.musicDelete.getId());
        }
        viewHolder.musicName.setText(musicInfo.getMusicName());
        viewHolder.musicBaseInfo.setText(musicInfo.getMusicAuthor());
        listener.setPosition(position);
        return view;
    }

    class ViewHolder{
        TextView musicName;
        TextView musicBaseInfo;
        Button musicDelete;
    }

    //删除按钮的响应
    class OnClick implements View.OnClickListener{
        int position;
        public void setPosition(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v){
            //发送广播给activity,删除数据库的信息
            Intent intent = new Intent(MusicTabActivity.ACTION_MUSIC_DELETEITEM);
            intent.putExtra("Position",position);
            intent.putExtra("MusicInfo",list.get(position));
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            //更新列表
            list.remove(position);
            notifyDataSetChanged();
        }
    }
}
