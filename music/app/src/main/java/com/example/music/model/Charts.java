package com.example.music.model;

import java.io.Serializable;

/**
 * Created by xiaozhisong on 15-1-23.
 */
public class Charts implements Serializable{
    private String id = "";
    private String count = "";
    private String name = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
