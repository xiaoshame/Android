package com.example.appexplorer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiaozhisong on 15-3-9.
 */
public class ListViewAdapter extends BaseAdapter{
    //
    LayoutInflater layoutInflater;
    List<PackageInfo> packageInfos;
    //应用程序管理
    PackageManager packageManager;
    private int resourceId;        //关联的配置文件id

    public ListViewAdapter(Context context,List<PackageInfo> packageInfos){
        layoutInflater = LayoutInflater.from(context);
        packageManager = context.getPackageManager();
        this.packageInfos = packageInfos;
    }

    @Override
    public int getCount() {
        return packageInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return packageInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        if(convertView == null){
            //获取每个app的样式
            view = layoutInflater.inflate(R.layout.lv_item,null);
            viewHolder = new ViewHolder();
            viewHolder.appNameTextView = (TextView)view.findViewById(R.id.lv_item_appname);
            viewHolder.packageNameTextView = (TextView)view.findViewById(R.id.lv_item_packagename);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.lv_item_icon);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        //app的基本信息
        viewHolder.appNameTextView.setText(packageInfos.get(position).applicationInfo.loadLabel(packageManager));
        viewHolder.packageNameTextView.setText(packageInfos.get(position).applicationInfo.packageName);
        viewHolder.imageView.setImageDrawable(packageInfos.get(position).applicationInfo.loadIcon(packageManager));
        return view;
    }

    class ViewHolder{
        TextView appNameTextView;
        TextView packageNameTextView;
        ImageView imageView;
    }
}
