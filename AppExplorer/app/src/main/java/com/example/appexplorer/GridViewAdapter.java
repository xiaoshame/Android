package com.example.appexplorer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiaozhisong on 15-3-6.
 */
public class GridViewAdapter extends BaseAdapter{
    //
    LayoutInflater layoutInflater;
    List<PackageInfo> packageInfos;
    //应用程序管理
    PackageManager packageManager;

    public GridViewAdapter(Context context,List<PackageInfo> packageInfos){
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
            view = layoutInflater.inflate(R.layout.gv_item,null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView)view.findViewById(R.id.gv_item_appname);
            viewHolder.imageView = (ImageView)view.findViewById(R.id.gv_item_icon);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        //app的基本信息
        viewHolder.textView.setText(packageInfos.get(position).applicationInfo.loadLabel(packageManager));
        viewHolder.imageView.setImageDrawable(packageInfos.get(position).applicationInfo.loadIcon(packageManager));
        return view;
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }
}
