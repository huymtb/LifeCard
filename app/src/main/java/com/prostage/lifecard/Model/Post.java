package com.prostage.lifecard.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HuyMTB on 3/24/2017.
 *
 */

public class Post {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("code")
    @Expose
    private Integer code;

    @SerializedName("entry_number")
    @Expose
    private String entry_number;

    @SerializedName("birthday")
    @Expose
    private String birthday;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getEntry_number() {
        return entry_number;
    }

    public void setEntry_number(String entry_number) {
        this.entry_number = entry_number;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Post(String entry_number, String birthday) {
        this.entry_number = entry_number;
        this.birthday = birthday;
    }
}
