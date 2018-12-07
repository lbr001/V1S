package com.sunmi.helper.utils;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Adminstrator on 2018-4-2.
 * Activity管理工具类
 */

public class ExitAppUtils extends Application {

    private List<Activity> activityList = new LinkedList<Activity>();
    private static ExitAppUtils instance;

    private ExitAppUtils() {
    }

    //单例模式中获取唯一的ExitTAppUtils实例
    public static ExitAppUtils getInstance() {
        if (null == instance) {
            instance = new ExitAppUtils();
        }
        return instance;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);

    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
