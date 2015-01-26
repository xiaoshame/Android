package com.example.music.util;

import com.example.music.model.MusicInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhisong on 15-1-26.
 */
public class MusicSaxHandler extends DefaultHandler {
    //歌曲列表信息
    private List<MusicInfo> musicInfoList;
    private MusicInfo musicInfo;
    //节点标记
    private String nodeName;

    public MusicSaxHandler(List<MusicInfo> musicInfoList){
        this.musicInfoList = musicInfoList;
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
        if("data".equals(localName)){
            musicInfo = new MusicInfo();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("data".equals(localName)){
            musicInfoList.add(musicInfo);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if("id".equals(nodeName)){
            musicInfo.setMusicId(new String(ch,start,length));
        }else if("name".equals(nodeName)){
            musicInfo.setMusicName(new String(ch,start,length));
        }
    }
}
