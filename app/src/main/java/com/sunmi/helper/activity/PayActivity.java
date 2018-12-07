package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.bean.ProductBean;
import com.sunmi.helper.net.BarNet;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;
import com.sunmi.helper.view.AlwaysMarqueeTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:订单支付页
 * 创建时间: 2018-10-23 9:59
 */
public class PayActivity extends Activity implements View.OnClickListener {
    //支付产品信息
    private List<ProductBean> orderlist = new ArrayList<ProductBean>();
    //接收前面页面传值信息
    private Bundle bundle;
    //扫码支付网络请求
    private BarNet barNet = new BarNet();
    //底部支付UI中支付方式图标
    private ImageView mImageView;
    //支付界面标题
    private AlwaysMarqueeTextView tv_name;
    //支付总金额
    private TextView tv_price;
    private String money_count, list_count;
    //支付方式
    private String pay_type, pay_way;
    //加载动画
    private Dialog mDialog;
    //底部支付ui
    private LinearLayout linearLayout;
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //订单码
    private String order_code;
    //扫码结果
    private String BarCodeNum;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(PayActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    if ("0".equals(pay_way)) {
                        mDialog = DialogUtils.createLoadingDialog(PayActivity.this, "出票中");
                        getTicketResults();
                    } else if ("1".equals(pay_way)) {
                        pay_type = "ali";
                        Intent intent = new Intent("com.summi.scan");
                        intent.setClassName("com.sunmi.sunmiqrcodescanner", "com.sunmi.sunmiqrcodescanner.activity.ScanActivity");
                        intent.putExtra("CURRENT_PPI", 0x0003);
                        intent.putExtra("PLAY_SOUND", true);
                        try {
                            startActivityForResult(intent, 1);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(PayActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        pay_type = "wx";
                        Intent intent = new Intent("com.summi.scan");
                        intent.setClassName("com.sunmi.sunmiqrcodescanner", "com.sunmi.sunmiqrcodescanner.activity.ScanActivity");
                        intent.putExtra("CURRENT_PPI", 0x0003);
                        intent.putExtra("PLAY_SOUND", true);
                        try {
                            startActivityForResult(intent, 1);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(PayActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 3:
                    DialogUtils.closeDialog(mDialog);
                    startActivity(new Intent(PayActivity.this, ProductSale.class));
                    overridePendingTransition(R.anim.scale_rotate,
                            R.anim.my_alpha_action);
                    break;
                case 4:
                    DialogUtils.closeDialog(mDialog);
                    mDialog = DialogUtils.createLoadingDialog(PayActivity.this, "打印中...");
                    break;
                case 5:
                    DialogUtils.closeDialog(mDialog);
                    mDialog = DialogUtils.createLoadingDialog(PayActivity.this, "出票中...");
                    getTicketResults();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(PayActivity.this);
        //初始化打印机(连接)
        AidlUtil.getInstance().connectPrinterService(PayActivity.this);
        mImageView = findViewById(R.id.pay_Image_logo);
        tv_name = findViewById(R.id.tv_tilte);
        tv_price = findViewById(R.id.pay_count);
        tv_name.setText(PublicMethod.SPOT_NAME + "收银台");
        linearLayout = findViewById(R.id.pay_linear);
        bundle = getIntent().getExtras();
        money_count = String.valueOf(bundle.get("mmoney"));
        list_count = String.valueOf(bundle.get("count"));
        Log.e("JJJJ", list_count);
        tv_price.setText(money_count.trim());
        orderlist = (List<ProductBean>) bundle.get("mlist");
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.header_iv:
                handler.sendEmptyMessage(3);
                break;
            //现金支付
            case R.id.Ima_money_pay:
                pay_type = "1";
                pay_way = "0";
                mImageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.money));
                linearLayout.setBackgroundColor(getResources().getColor(R.color.color10));
                break;
            //支付宝支付
            case R.id.Ima_ali_pay:
                pay_type = "0";
                pay_way = "1";
                mImageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ali));
                linearLayout.setBackgroundColor(getResources().getColor(R.color.color10));
                break;
            //微信支付
            case R.id.Ima_wx_pay:
                pay_type = "0";
                pay_way = "2";
                mImageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.wx));
                linearLayout.setBackgroundColor(getResources().getColor(R.color.color10));
                break;
            //支付
            case R.id.pay_linear:
                if (TextUtils.isEmpty(pay_way)) {
                    warn_info = "请选择支付方式";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    showLoadingDialog();
                }
                break;
        }
    }

    /**
     * 显示等待对话框
     */
    private void showLoadingDialog() {
        mDialog = DialogUtils.createLoadingDialog(PayActivity.this, "下单中...");
        makeOrder();
    }

    /**
     * 下单
     */

    private void makeOrder() {
        if (PublicMethod.isNetworkAvailable(PayActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < orderlist.size(); i++) {
                        Map map = new HashMap();
                        map.put("channel_list_code", "J12");
                        map.put("sale_code", orderlist.get(i).code);
                        Log.e("num", String.valueOf(orderlist.get(i).ticketNum));
                        map.put("num", String.valueOf(orderlist.get(i).ticketNum));
                        map.put("pay_fee", String.valueOf((orderlist.get(i).ticketPrice) * (orderlist.get(i).ticketNum)));
                        list.add(map);
                    }
                    String saleJson = JSON.toJSONString(list);
                    JSONArray array = JSON.parseArray(saleJson);
                    Map modelmap = new HashMap();
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Log.e("list_count", list_count);
                    if ("0".equals(PublicMethod.DESTOTYTICKET)) {
                        modelmap.put("auto_destory", "1");
                    } else {
                        modelmap.put("auto_destory", "0");
                    }
                    modelmap.put("num", list_count);
                    modelmap.put("pay_fee", money_count);
                    modelmap.put("sale_list", array);
                    modelmap.put("pay_type", pay_type);
                    modelmap.put("sale_num", String.valueOf(orderlist.size()));
                    Map ordermap = httpUtil.getOrder(modelmap);
                    Log.e("ordermap", "" + ordermap);
                    if (PublicMethod.checkIfNull(ordermap)) {
                        warn_info = "暂无下单信息";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (ordermap.containsKey("errcode") && "0".equals(ordermap.get("errcode").toString())) {
                            Map datamap = (Map) ordermap.get("data");
                            order_code = datamap.get("code") == null ? "" : String.valueOf(datamap.get("code"));
                            if ("0".equals(pay_way)) {
                                JSONArray array1 = (JSONArray) datamap.get("ticketList");
                                if (array1.size() > 0) {
                                    handler.sendEmptyMessage(4);
                                    Printer(array1);
                                }
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        } else {
                            warn_info = ordermap.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }

                    }
                }
            }.start();
        }
    }

    /**
     * 出票
     */
    private void getTicketResults() {
        if (PublicMethod.isNetworkAvailable(PayActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    if ("0".equals(PublicMethod.DESTOTYTICKET)) {
                        modelmap.put("auto_destory", "1");
                    } else {
                        modelmap.put("auto_destory", "0");
                    }
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    modelmap.put("order_code", order_code);
                    Map resmap = httpUtil.getTicketResult(modelmap);
                    Log.e("resmap", "" + resmap);
                    if (PublicMethod.checkIfNull(resmap)) {
                        warn_info = "暂无详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (resmap.containsKey("errcode") && "0".equals(resmap.get("errcode").toString())) {
                            Map datamap = (Map) resmap.get("data");
                            Log.e("datamap", "" + datamap);
                            JSONArray array = (JSONArray) datamap.get("ticketList");
                            Log.e("ticketlist", "" + array);
                            if (array.size() > 0) {
                                DialogUtils.closeDialog(mDialog);
                                handler.sendEmptyMessage(4);
                                Log.e("sizesize", String.valueOf(array.size()));
                                Printer(array);
                            } else {
                                warn_info = "暂无票详情";
                                handler.sendEmptyMessage(0);
                            }
                        } else {
                            warn_info = resmap.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 打印
     *
     * @Author lbr
     * create time 2018-10-23  14:56
     * *******************************
     */
    private void Printer(JSONArray array) {
        float size = Integer.parseInt("24");
        for (int i = 0; i < array.size(); i++) {
            Map map = (Map) array.get(i);
            StringBuffer sb = new StringBuffer();
            int pay_fee = (int) map.get("pay_fee");
            String name = (String) map.get("name");
            String mobile = (String) map.get("mobile");
            String code = (String) map.get("order_unique_code");
            if ("null".equals(name)) {
                name = "";
            }
            if ("null".equals(mobile)) {
                mobile = "";
            }
            double pay1 = ((pay_fee) * 0.01);
            DecimalFormat df = new DecimalFormat("######0.00");
            String pay = df.format(pay1);
            sb.append("票种名称:" + map.get("title") + "\n");
            sb.append("支付金额:" + pay + "元" + "\n");
            sb.append("限用人数：" + map.get("num") + "人" + "\n");
            sb.append("订单编号：" + map.get("code") + "\n");
            sb.append("凭证号码:" + map.get("order_unique_code") + "\n");
            sb.append("下单时间:" + map.get("create_time") + "\n");
            sb.append("有效时间:" + map.get("tour_date") + "\n");
            sb.append("联系方式:" + name + mobile + "\n");
            AidlUtil.getInstance().printQr(code, 8, 3);
            AidlUtil.getInstance().printText(sb.toString(), size, false, false);
        }
        handler.sendEmptyMessage(3);
    }

    /**
     * 扫码回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Bundle bundle = data.getExtras();
            ArrayList<HashMap<String, String>> results = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
            Iterator<HashMap<String, String>> it = results.iterator();
            while (it.hasNext()) {
                HashMap<String, String> hashMap = it.next();
                Log.e("扫码类型:", hashMap.get("TYPE"));
                Log.e("扫码结果:", hashMap.get("VALUE"));
                BarCodeNum = hashMap.get("VALUE");
            }
            if (BarCodeNum != null) {
                PayByBar();
            } else {
                warn_info = "无法获取二维码";
                handler.sendEmptyMessage(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 通过扫码支付
     */
    private void PayByBar() {
        if (PublicMethod.isNetworkAvailable(PayActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    Log.e("morderid", order_code);
                    Log.e("msession", PublicMethod.LOGINCOOKIE);
                    Log.e("mtype", pay_type);
                    Log.e("mauth", BarCodeNum);
                    modelmap.put("orderid", order_code.trim());
                    modelmap.put("session_id", PublicMethod.COOKIE.trim());
                    modelmap.put("type", pay_type.trim());
                    modelmap.put("auth_code", BarCodeNum.trim());
                    String payresults = barNet.sendSSLPostRequest(modelmap);
                    Log.e("payresults", payresults);
                    JSON json = (JSON) JSON.parse(payresults);
                    Map paymap = (Map) json;
                    Log.e("paymap", "" + paymap);
                    if (PublicMethod.checkIfNull(paymap)) {
                        warn_info = "暂无支付结果";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (paymap.containsKey("status") && "1".equals(paymap.get("status").toString())) {
                            handler.sendEmptyMessage(5);
                        } else {
                            warn_info = paymap.get("msg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(PayActivity.this, ProductSale.class));
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
