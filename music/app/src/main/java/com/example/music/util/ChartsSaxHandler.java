package com.example.music.util;

import android.util.Log;

import com.example.music.db.MusicDB;
import com.example.music.model.Charts;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by xiaozhisong on 15-1-24.
 */
//如何编写这个类，使xml的解析复用性更好
public class ChartsSaxHandler extends DefaultHandler {
    //节点标记
    private String nodeName;

    private MusicDB musicDB;
    private Charts charts;

    public ChartsSaxHandler(MusicDB musicDB) {
        this.musicDB = musicDB;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        nodeName = localName;
        //节点开始
        if("data".equals(localName)){
            charts = new Charts();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("data".equals(localName)){
            musicDB.saveCharts(charts);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String values = new String(ch,start,length);
        Log.d("SAXHandler",values);
        if("id".equals(nodeName)){
            charts.setId(values);
        }else if("name".equals(nodeName)){
            charts.setName(values);
        }else if("tcount".equals(nodeName)){
            charts.setCount(values);
        }
    }
}
