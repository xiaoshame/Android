package com.example.music.util;


import com.example.music.model.MusicAddress;
import com.example.music.model.MusicAddress.Address;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by xiaozhisong on 15-1-29.
 */
public class AddressSaxHandler extends DefaultHandler{

    //节点标记
    private String nodeName;
    private Address address = null;
    private MusicAddress musicAddress;
    private StringBuilder musicurl = null;

    public AddressSaxHandler(MusicAddress musicAddress){
        this.musicAddress = musicAddress;
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
        //需要解析的节点
        if("durl".equals(localName)){
            //通过address是否为null区分url节点和durl节点，url节点中数据质量太差丢弃
            address = musicAddress.new Address();
            musicurl = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("durl".equals(localName)){
            musicAddress.addlist(address);
            address = null;
            musicurl = null;
        }
    }
    private static final String lrcBase = "http://box.zhangmen.baidu.com/bdlrc/";
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(address != null && "encode".equals(nodeName)){
            String values = new String(ch,start,length);
            //最后一个/之后的内容截断
            values = values.substring(0,values.lastIndexOf("/") + 1);
            musicurl.append(values);
        }else if(address != null && "decode".equals(nodeName)){
            musicurl.append(new String(ch,start,length));
            //保存music的url
            address.setMusicUrl(musicurl.toString());
        }else if(address != null && "lrcid".equals(nodeName)){
            //拼凑歌词地址
            String values = new String(ch,start,length);
            address.setLrcUrl(lrcBase + Integer.valueOf(values) / 100 + "/" + values + ".lrc");
        }else if("type".equals(nodeName)){
            String value = new String(ch,start,length);
            if(value == "1" || value == "8"){
                musicAddress.setType("mp3");
            }
        }else if("size".equals(nodeName)){
            musicAddress.setSize(new String(ch,start,length));
        }
    }
}
