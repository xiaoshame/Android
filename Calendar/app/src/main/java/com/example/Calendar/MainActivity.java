package com.example.Calendar;

import com.example.Calendar.adapter.CalendarAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import com.example.Calendar.data.DateInfo;
import com.example.Calendar.utils.DataUtils;
import com.example.Calendar.utils.TimeUtils;

import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    // viewpager相关的变量
    public ViewPager viewPager = null;
    public MyPagerAdapter pagerAdapter = null;
    private int currentPager = 500;      //当前所处的页面

    //使用GridView显示日期
    private GridView gridView = null;
    public CalendarAdapter adapter = null;
    private GridView currentView = null;
    public List<DateInfo> currentList = null;
    public List<DateInfo> list = null;
    public int lastSelected = 0;

    //当前所在的年月
    public TextView showYearMonth = null;
    public TextView showDetailLunar = null;
    //当前页面的年，月
    private int currentYear;
    private int currentMonth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化日期数据
        initData();
        //初始化日期显示视图
        initView();
    }
    private void initData(){
        currentYear = TimeUtils.getCurrentYear();
        currentMonth = TimeUtils.getCurrentMonth();
        lastSelected = TimeUtils.getCurrentDay();

        showYearMonth = (TextView)findViewById(R.id.main_year_month);
        showYearMonth.setText(String.format("%04d年%02d月",currentYear,currentMonth));

        showDetailLunar =(TextView)findViewById(R.id.detail_lunar);
        try{
            showDetailLunar.setText(TimeUtils.getDetailLunar(currentYear,currentMonth,lastSelected));
        }catch (Exception e){
            showDetailLunar.setText("");
        }
    }
    private void initView(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        pagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        //设置当前显示的页面的位置
        viewPager.setCurrentItem(currentPager);
        viewPager.setPageMargin(0);
        //设置ViewPager滑动监听器
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                //在当前年月的基础上获取当前选择的页面的年
                int year = TimeUtils.getTimeByPosition(i,currentYear,currentMonth,"year");
                int month = TimeUtils.getTimeByPosition(i,currentYear,currentMonth,"month");
                //更新年月
                showYearMonth.setText(String.format("%04d年%02d月", year, month));
                //更新阴历详细信息
                try{
                    showDetailLunar.setText(TimeUtils.getDetailLunar(year,month,lastSelected));
                }catch (Exception e){
                    showDetailLunar.setText("");
                }
                currentPager = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if(i == 0){
                    //在ViewPager中找到对应的页面
                    currentView = (GridView)viewPager.findViewById(currentPager);
                    if(currentView != null){
                        adapter = (CalendarAdapter)currentView.getAdapter();
                        currentList = adapter.getList();
                        int pos = DataUtils.getDayFlag(currentList,lastSelected);
                        adapter.setSelectedPosition(pos);
                        adapter.notifyDataSetInvalidated();
                    }
                }
            }
        });
    }

    //初始化日历的gridview,在这里显示一个月的具体信息
    private GridView initCalendarView(int position){
        int year = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "year");
        int month = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "month");
        String formatDate = TimeUtils.getFormatDate(year,month);
        //获取当前这个月的信息
        try{
            list = TimeUtils.initCalendar(formatDate,month);
        }catch (Exception e){
            finish();
        }
        gridView = new GridView(this);
        adapter = new CalendarAdapter(this,list);
        if(position == 500){
            //我们要显示的页面
            currentList = list;
            int pos = DataUtils.getDayFlag(list,lastSelected);
            adapter.setSelectedPosition(pos);
        }
        gridView.setAdapter(adapter);
        gridView.setNumColumns(7);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setGravity(Gravity.CENTER);
        gridView.setOnItemClickListener(new OnItemClickListenerImpl(adapter,this));
        return gridView;
    }
    //viewpager的适配器，从500页开始，最多支持0-1000页
    //ViewPager的作用使视图可以滑动
    private class MyPagerAdapter extends PagerAdapter{
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object){
            currentView = (GridView)object;
            adapter = (CalendarAdapter)currentView.getAdapter();
        }
        //返回可以使用的页面个数
        @Override
        public int getCount(){
            return 1000;
        }
        @Override
        public boolean isViewFromObject(View arg0,Object arg1){
            return arg0 == arg1;
        }
        @Override
        public void destroyItem(ViewGroup container,int position,Object object){
            container.removeView((View)object);
        }
        //给ViewPager中添加页面
        @Override
        public Object instantiateItem(ViewGroup container,int position){
            GridView gv = initCalendarView(position);
            gv.setId(position);
            container.addView(gv);
            return gv;
        }
    }
}
