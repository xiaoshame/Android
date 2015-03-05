package com.example.music.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhisong on 15-1-29.
 */
public class MusicAddress {
    private List<Address> list;
    private String type;
    private String size;

    public MusicAddress(){
        list = new ArrayList<Address>();
    }
    public int getListCount(){
        return list.size();
    }

    public void addlist(Address address) {
        list.add(address);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMusicUrl(){
        if(0 == list.size()){
            return null;
        }
        return list.get(0).getMusicUrl();
    }
    public String getlrcUrl(int position){
        return list.get(position).getLrcUrl();
    }
    public class Address{
        private String musicUrl;
        private String lrcUrl;

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String getLrcUrl() {
            return lrcUrl;
        }

        public void setLrcUrl(String lrcUrl) {
            this.lrcUrl = lrcUrl;
        }
    }
}
