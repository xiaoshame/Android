package com.example.music.util;

import android.util.Log;

import com.example.music.db.MusicDB;
import com.example.music.model.Charts;
import com.example.music.model.MusicAddress;
import com.example.music.model.MusicInfo;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public class XmlUtility {
    //解析和处理服务器返回的排行榜数据
    public synchronized static boolean handleChartsResponseWithPull(MusicDB musicDB,InputStream response){
        if(response != null){
            try{
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(response,"gb2312");
                int eventType = xmlPullParser.getEventType();
                Charts charts = new Charts();
                while(eventType != XmlPullParser.END_DOCUMENT){
                    String nodeName = xmlPullParser.getName();
                    switch(eventType){
                        case XmlPullParser.START_TAG:{
                            if("id".equals(nodeName)){
                                charts.setId(xmlPullParser.nextText());
                            }else if("name".equals(nodeName)){
                                charts.setName(xmlPullParser.nextText());
                            }else if("tcount".equals(nodeName)){
                                charts.setCount(xmlPullParser.nextText());
                            }else if("isnew".equals(nodeName)){
                                //这个节点暂时不处理
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:{
                            //完成解析某个节点
                            if("data".equals(nodeName)){
                                musicDB.saveCharts(charts);
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    eventType = xmlPullParser.next();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    //解析查询排行榜列表的数据
    public synchronized static boolean handleChartsResponseWithSAX(MusicDB musicDB,String response) {
        try{
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            ChartsSaxHandler xmlHandler = new ChartsSaxHandler(musicDB);
            xmlReader.setContentHandler(xmlHandler);
            //开始执行解析
            xmlReader.parse(new InputSource(new StringReader(response)));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //解析查询歌曲列表的数据
    public synchronized static boolean handlerMusicResponseWithSAX(List<MusicInfo> musicInfoList,String response){
        try{
            Log.d("handlerMusicResponseWithSAX","Begin Parse Music List Info");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            MusicSaxHandler xmlHandler = new MusicSaxHandler(musicInfoList);
            xmlReader.setContentHandler(xmlHandler);

            //开始解析
            xmlReader.parse(new InputSource(new StringReader(response)));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //解析歌曲地址的xml
    public synchronized static boolean handleMusicAddressWithSAX(MusicAddress musicAddress,String response){
        try{
            Log.d("handleMusicAddressWithSAX","Begin Parse Music Address");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();
            AddressSaxHandler addressSaxHandler = new AddressSaxHandler(musicAddress);
            xmlReader.setContentHandler(addressSaxHandler);
            xmlReader.parse(new InputSource(new StringReader(response)));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
