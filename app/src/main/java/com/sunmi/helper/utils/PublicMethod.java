package com.sunmi.helper.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:公共变量、方法
 * 创建时间: 2018-10-17 16:23
 */
public class PublicMethod {

    //设备码
    public static String SERIZLNUMBER;
    //图片文件
    public static File cache;
    //接口地址
    public static String BASE_URL = "http://ts.juyouhx.com/";
    //景区名称
    public static String SPOT_NAME;
    //景区编号
    public static String SPOT_VIEW;
    //权限标记
    public static String ACTIVITIES;
    //登录成功拼接key
    public static String LOGINCOOKIE;
    //登录成功key
    public static String COOKIE;

    public static String INFOID = "";
    //设备码
    public static String SYSTEMSETTINGMACHINECODE = "";
    //设备域名
    public static String SYSTEMSETTINGADDRESS = "";
    //设备密码
    public static String SYSTEMSETTINGPASSWORD = "";
    //设备websocket地址
    public static String SYSTEMSETTINGWEBSOCKETADDRESS = "";
    /**
     * 是否销票(0--销票 1--不销票)
     */
    public static String DESTOTYTICKET;
    /**
     * 是否启用居游卡(0--启用  1--不启用)
     */
    public static String ISNEEDICCARD;
    /**
     * 是否启用刷身份证（0--启用 1--不启用）
     */
    public static String ISNEEDIDCARD;
    /**
     * 打印票类型(0--小票 1--纸质票)
     */
    public static String TICKETTYPE;
    /**
     * 票码类型(0--一码一票 1--一码多票)
     */
    public static String CODETYPE;
    /**
     * 启用打印机(0--不启用 1--启用)
     */
    public static String ISPRINTER;
    //登录用户名
    public static String login_name;
    //登录密码
    public static String machinecode;
    //返回标记
    public static String BACK_MARK;
    //支付域名
    public static String Bar_URL = "https://ts.juyouhx.com/";


    /**
     * 获取当前应用的版本名称(versionName)
     *
     * @param ctx 上下文环境
     * @return versionName
     */
    public static String getVersionName(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = manager.getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi.versionName;
    }

    /**
     * 判断手机是否联网
     *
     * @param ctx 上下文环境
     * @return true | false
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean result = info != null && info.isConnected();
        return (!result);
    }

    /***
     * 判断获取的map内容是否为空
     *
     * @param map
     * @return
     */
    public static boolean checkIfNull(Map map) {
        return (map == null) || (PublicMethod.stringNullConvert(String.valueOf(map)).equals(""))
                || (PublicMethod.stringNullConvert(String.valueOf(map)).equals("{}") || map.size() < 1);
    }

    /**
     * 字符串如果为null就返回""，否则返回原值
     *
     * @param nullValueConvert
     * @return
     * @author hui
     */
    public static String stringNullConvert(String nullValueConvert) {
        if (nullValueConvert == null) {
            return "";
        }
        return nullValueConvert.trim();
    }
}
