package com.prostage.lifecard;

import android.app.Application;

/**
 * Created by congnguyen on 9/26/17.
 */

public class MyApp extends Application {
    private static MyApp singleton;

    public static MyApp getInstance() {
        return singleton;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
