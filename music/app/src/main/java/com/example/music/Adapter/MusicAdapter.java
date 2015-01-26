package com.example.music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.music.model.MusicInfo;
import com.example.music.R;

import java.util.List;

/**
 * Created by Lijinpu on 2015/1/22.
 */
public class MusicAdapter extends ArrayAdapter<MusicInfo>{

    private int resourceId;        //关联的配置文件id
    private Context context;       //父活动

    public MusicAdapter(Context context,int textViewResourceId,List<MusicInfo> object){
        super(context,textViewResourceId,object);
        resourceId = textViewResourceId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicInfo musicInfo = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.musicName = (TextView)view.findViewById(R.id.internet_music_name);
            viewHolder.musicBaseInfo = (TextView)view.findViewById(R.id.internet_music_baseinfo);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.musicName.setText(musicInfo.getMusicName());
        viewHolder.musicBaseInfo.setText(musicInfo.getMusicAuthor() + musicInfo.getMusicOtherInfo());
        return view;
    }

    class ViewHolder{
        TextView musicName;
        TextView musicBaseInfo;
    }
}
