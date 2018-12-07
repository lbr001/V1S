package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.HistoryAdapter;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:销售历史
 * 创建时间: 2018-10-18 13:48
 */
public class SaleHistory extends Activity implements View.OnClickListener {
    //标题
    private TextView tv_title;
    //listview
    private ListView mlistview;
    //无数据的布局
    private LinearLayout linearLayout;
    //加载动画
    private Dialog mDialog;
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //接收数据的list
    private List<Map<String, Object>> orderlist = new ArrayList<Map<String, Object>>();
    //listview适配器
    private HistoryAdapter historyAdapter;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(SaleHistory.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    mlistview.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    setData();
                    break;
                case 3:
                    DialogUtils.closeDialog(mDialog);
                    mDialog = DialogUtils.createLoadingDialog(SaleHistory.this, "打印中...");
                    break;
                case 4:
                    DialogUtils.closeDialog(mDialog);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_history);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(SaleHistory.this);
        AidlUtil.getInstance().connectPrinterService(SaleHistory.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("当日销售历史查询");
        linearLayout = findViewById(R.id.history_linear);
        mlistview = findViewById(R.id.history_list);
        mDialog = DialogUtils.createLoadingDialog(SaleHistory.this, "加载中...");
        gethistoryData();
    }

    /**
     * 获取数据源
     */
    private void gethistoryData() {
        if (PublicMethod.isNetworkAvailable(SaleHistory.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("pageNo", "1");
                    modelmap.put("pageSize", "20000");
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Map map = httpUtil.getHistoryInfo(modelmap);
                    Log.e("historymap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            Map datamap = (Map) map.get("data");
                            orderlist = (List<Map<String, Object>>) datamap.get("results");
                            if (orderlist.size() < 1) {
                                handler.sendEmptyMessage(1);
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        } else {
                            warn_info = map.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 添加适配器
     */
    private void setData() {
        historyAdapter = new HistoryAdapter(orderlist, this);
        mlistview.setAdapter(historyAdapter);
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-26  11:27
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.header_iv:
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(SaleHistory.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SaleHistory.this, AdminActivity.class));
                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ("0".equals(PublicMethod.BACK_MARK)) {
                startActivity(new Intent(SaleHistory.this, MainActivity.class));
            } else {
                startActivity(new Intent(SaleHistory.this, AdminActivity.class));
            }
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重打门票
     *
     * @param code
     */
    public void getOrderInfo(final String code) {
        if (PublicMethod.isNetworkAvailable(SaleHistory.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            handler.sendEmptyMessage(3);
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("order_code", code);
                    Map map = httpUtil.getOrderInfo(modelmap);
                    Log.e("mapiqiq", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无信息1";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            Map datamap = (Map) map.get("data");
                            Print(datamap);
                        } else {
                            warn_info = map.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 重新打印门票
     *
     * @Author lbr
     * create time 2018-10-26  11:30
     * *******************************
     */
    private void Print(Map map) {
        Log.e("mnmap", map.toString());
        float size = Integer.parseInt("24");
        String qrcode = map.get("order_unique_code") == null ? "" : String.valueOf(map.get("order_unique_code"));
        String titile = map.get("title") == null ? "" : String.valueOf(map.get("title"));
        String pay_fee = map.get("pay_fee") == null ? "" : String.valueOf(map.get("pay_fee"));
        String num = map.get("num") == null ? "" : String.valueOf(map.get("num"));
        Log.e("num1", num);
        String used = map.get("used") == null ? "" : String.valueOf(map.get("used"));
        Log.e("used1", used);
        String back = map.get("back") == null ? "" : String.valueOf(map.get("back"));
        Log.e("back1", back);
        String code = map.get("code") == null ? "" : String.valueOf(map.get("code"));
        String tourdate = map.get("periodstart") == null ? "" : String.valueOf(map.get("periodstart"));
        String tourdateend = map.get("periodend") == null ? "" : String.valueOf(map.get("periodend"));
        String create_time = map.get("create_time") == null ? "" : String.valueOf(map.get("create_time"));
        String name = map.get("name") == null ? "" : String.valueOf(map.get("name"));
        String mobile = map.get("mobile") == null ? "" : String.valueOf(map.get("mobile"));
        double pay1 = ((Integer.parseInt(pay_fee)) * 0.01);
        DecimalFormat df = new DecimalFormat("######0.00");
        String pay = df.format(pay1);
        int man = (Integer.parseInt(num) - Integer.parseInt(used) - Integer.parseInt(back));
        Log.e("man1", String.valueOf(man));
        if ("null".equals(name)) {
            name = "";
        }
        if ("null".equals(mobile)) {
            mobile = "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("票种名称:" + titile + "\n");
        sb.append("支付金额:" + pay + "元" + "\n");
        sb.append("限用人数:" + String.valueOf(man) + "人" + "\n");
        sb.append("订单编号:" + code + "\n");
        sb.append("凭证号码:" + qrcode + "\n");
        sb.append("下单时间:" + create_time + "\n");
        sb.append("有效时间:" + tourdate + "-" + tourdateend + "\n");
        sb.append("联系方式:" + name + mobile + "\n");
        AidlUtil.getInstance().printQr(qrcode, 8, 3);
        AidlUtil.getInstance().printText(sb.toString(), size, false, false);
        handler.sendEmptyMessage(4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
