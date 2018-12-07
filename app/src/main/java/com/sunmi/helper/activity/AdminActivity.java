package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:账号登录主页(管理员)
 * 创建时间: 2018-10-18 10:23
 */
public class AdminActivity extends Activity implements View.OnClickListener {
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //今日销量  昨日销量
    private TextView tv_today_sale, tv_yesterday_sale;
    //今日核销  昨日核销
    private TextView tv_today_destory, tv_yesterday_destory;
    //获取核销 销售数量参数(日期)
    private String today, yesterday;
    //今日销量  昨日销量  今日核销 昨日核销  数量
    private String today_sale, yesterday_sale, today_destory, yesterday_destory;
    //提示信息
    private String warn_info;
    //获取信息标记
    private String mark = "1";
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(AdminActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    if ("2".equals(mark)) {
                        tv_today_sale.setText(today_sale + "张");
                        tv_yesterday_sale.setText(yesterday_sale + "张");
                        tv_today_destory.setText(today_destory + "张");
                        tv_yesterday_destory.setText(yesterday_destory + "张");
                    }
                    break;
                case 1:
                    tv_today_sale.setText(today_sale + "张");
                    tv_yesterday_sale.setText(yesterday_sale + "张");
                    tv_today_destory.setText(today_destory + "张");
                    tv_yesterday_destory.setText(yesterday_destory + "张");
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(AdminActivity.this);
        tv_today_sale = findViewById(R.id.tv_admin_today_sale);
        tv_yesterday_sale = findViewById(R.id.tv_admin_yesterday_sale);
        tv_today_destory = findViewById(R.id.tv_admin_today_destory);
        tv_yesterday_destory = findViewById(R.id.tv_admin_yesterday_destory);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = simpleDateFormat.format(new Date());
        String m1 = today.substring(0, today.length() - 2);
        String m2 = today.substring(today.length() - 2, today.length());
        int num = (Integer.parseInt(m2) - 1);
        yesterday = m1 + String.valueOf(num);
        getInfo();
    }

    /**
     * *******************************
     * 获取销售与核销数量
     *
     * @Author lbr
     * create time 2018-10-18  11:46
     * *******************************
     */
    private void getInfo() {
        if (PublicMethod.isNetworkAvailable(AdminActivity.this)) {
            warn_info = "请检查网络连接";
            mHandler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Map salemap = httpUtil.getSaleInfo(modelmap);
                    Log.e("salemap", String.valueOf(salemap));
                    if (PublicMethod.checkIfNull(salemap)) {
                        today_sale = "0";
                        today_destory = "0";
                        yesterday_sale = "0";
                        yesterday_destory = "0";
                        mark = "2";
                        warn_info = "暂无数据1";
                        mHandler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (salemap.containsKey("errcode") && "0".equals(salemap.get("errcode").toString())) {
                            if (salemap.containsKey("data")) {
                                Map datamap = (Map) salemap.get("data");
                                Log.e("dadsdsd", datamap.toString());
                                if (PublicMethod.checkIfNull(datamap)) {
                                    today_sale = "0";
                                    today_destory = "0";
                                    yesterday_sale = "0";
                                    yesterday_destory = "0";
                                    mark = "2";
                                    warn_info = "当前设备暂无售卖与核销记录";
                                    mHandler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    if (datamap.containsKey("saleCount1")) {
                                        Map map1 = (Map) datamap.get("saleCount1");
                                        if (PublicMethod.checkIfNull(map1)) {
                                            today_sale = "0";
                                        } else {
                                            today_sale = map1.get("num") == null ? "" : String.valueOf(map1.get("num"));
                                        }
                                    } else {
                                        today_sale = "0";
                                    }
                                    if (datamap.containsKey("saleCount2")) {
                                        Map map2 = (Map) datamap.get("saleCount2");
                                        if (PublicMethod.checkIfNull(map2)) {
                                            yesterday_sale = "0";
                                        } else {
                                            yesterday_sale = map2.get("num") == null ? "" : String.valueOf(map2.get("num"));
                                        }
                                    } else {
                                        yesterday_sale = "0";
                                    }
                                    if (datamap.containsKey("usedCount1")) {
                                        Map map3 = (Map) datamap.get("usedCount1");
                                        if (PublicMethod.checkIfNull(map3)) {
                                            today_destory = "0";
                                        } else {
                                            today_destory = map3.get("num") == null ? "" : String.valueOf(map3.get("num"));
                                            Log.e("dddd", today_destory);
                                        }
                                    } else {
                                        today_destory = "0";
                                    }
                                    if (datamap.containsKey("usedCount2")) {
                                        Map map4 = (Map) datamap.get("usedCount2");
                                        if (PublicMethod.checkIfNull(map4)) {
                                            yesterday_destory = "0";
                                        } else {
                                            yesterday_destory = map4.get("num") == null ? "" : String.valueOf(map4.get("num"));
                                        }
                                    } else {
                                        yesterday_destory = "0";
                                    }
                                    mHandler.sendEmptyMessage(1);
                                }
                            }
                        } else {
                            warn_info = "暂无数据2";
                            today_sale = "0";
                            today_destory = "0";
                            yesterday_sale = "0";
                            yesterday_destory = "0";
                            mark = "2";
                            mHandler.sendEmptyMessage(0);
                            return;
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //产品售卖
            case R.id.admin_liear1:
                startActivity(new Intent(AdminActivity.this, ProductSale.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //门票核销
            case R.id.admin_linear2:
                startActivity(new Intent(AdminActivity.this, TicketActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //销售历史
            case R.id.admin_history:
                startActivity(new Intent(AdminActivity.this, SaleHistory.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //退票详情
            case R.id.admin_refund:
                startActivity(new Intent(AdminActivity.this, RefundTicket.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //营销统计
            case R.id.admin_market:
                startActivity(new Intent(AdminActivity.this, MarketInfoActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //活动专区
            case R.id.admin_member:
                startActivity(new Intent(AdminActivity.this, AreaActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //设置
            case R.id.admin_settings:
                startActivity(new Intent(AdminActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //注销
            case R.id.admin_exit:
                dialog();
                break;
        }

    }

    /**
     * 退出提示框
     */
    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setMessage("确认退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dialog();
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
