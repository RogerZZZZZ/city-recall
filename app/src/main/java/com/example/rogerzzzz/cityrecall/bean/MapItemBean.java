package com.example.rogerzzzz.cityrecall.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by rogerzzzz on 16/3/23.
 */
public class MapItemBean {
    private String _name;
    private String _location;
    private String _address;
    private String username;
    private String content;

    @JSONField(name = "_name")
    public String get_name() {
        return _name;
    }

    @JSONField(name = "_name")
    public void set_name(String _name) {
        this._name = _name;
    }

    @JSONField(name = "_location")
    public String get_location() {
        return _location;
    }

    @JSONField(name = "_location")
    public void set_location(String _location) {
        this._location = _location;
    }

    @JSONField(name = "_address")
    public String get_address() {
        return _address;
    }

    @JSONField(name = "_address")
    public void set_address(String _address) {
        this._address = _address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
