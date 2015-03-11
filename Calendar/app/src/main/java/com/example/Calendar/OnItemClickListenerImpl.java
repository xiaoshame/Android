package com.example.Calendar;

import com.example.Calendar.adapter.CalendarAdapter;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by xiaozhisong on 15-1-18.
 */
//GridView点击监听器
public class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {
    private CalendarAdapter adapter = null;
    private MainActivity activity = null;

    public OnItemClickListenerImpl(CalendarAdapter adapter, MainActivity activity){
        this.adapter = adapter;
        this.activity = activity;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        //判断点击的是不是这个月的日期
        if(activity.currentList.get(position).isThisMonth() == false){
            return;
        }
        adapter.setSelectedPosition(position);
        adapter.notifyDataSetInvalidated();
        activity.lastSelected = activity.currentList.get(position).getDate();
        activity.showDetailLunar.setText(activity.currentList.get(position).getDetailLunar());
    }
}
