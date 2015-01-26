package com.example.music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.music.R;
import com.example.music.model.Charts;

import java.util.List;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public class ChartsAdapter extends ArrayAdapter<Charts> {
    private int resourceId;
    private Context context;
    public ChartsAdapter(Context context, int textViewResourceId, List<Charts> object) {
        super(context, textViewResourceId, object);
        resourceId = textViewResourceId;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Charts charts = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.internet_charts_name);
            viewHolder.count = (TextView)view.findViewById(R.id.internet_charts_count);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.name.setText(charts.getName());
        viewHolder.count.setText(charts.getCount()+"首");
        return view;
    }

    class ViewHolder{
        TextView name;
        TextView count;
    }
}
