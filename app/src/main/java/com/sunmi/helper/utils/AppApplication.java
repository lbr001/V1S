package com.sunmi.helper.utils;

import android.app.Application;

import com.tencent.bugly.Bugly;

import org.litepal.LitePal;

/**
 * @Author lbr
 * 功能描述:
 * 创建时间: 2018-11-27 11:13
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "9042d2d3db", false);
        try {
        } catch (Exception e) {
            e.printStackTrace();
            LitePal.getDatabase();
        }

    }
}
