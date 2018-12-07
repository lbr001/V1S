package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:退票
 * 创建时间: 2018-10-18 13:49
 */
public class RefundTicket extends Activity implements View.OnClickListener {
    //标题
    private TextView tv_title;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //用户输入订单唯一码
    private EditText et_codenum;
    //详情布局   退票输入数量布局
    private LinearLayout order_linear, need_linear;
    //票种名称
    private TextView tv_name;
    //门票价格
    private TextView tv_price;
    //门票总数量
    private TextView tv_count_num;
    //门票使用数量
    private TextView tv_used_num;
    //退票数量
    private TextView tv_back_num;
    //支付方式
    private TextView tv_pay_type;
    //退还金额
    private TextView tv_refund_money;
    //选择退票的数量
    private TextView tv_refund_count;
    //提示信息
    private String warn_info;
    //加载动画
    private Dialog mDialog;
    //显示确认退票对话框
    private Dialog dialog;
    //票种名称
    private String ticketname;
    //票总数量
    private String ticket_count;
    //使用数量
    private String ticket_used;
    //退票数量
    private String ticket_back;
    //支付方式
    private String ticket_pay_way;
    //门票价格
    private String ticket_price;
    //初始化输入退票数量
    private int ticket = 0;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    Toast.makeText(RefundTicket.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    tv_name.setText(ticketname);
                    tv_count_num.setText(ticket_count + "张");
                    tv_used_num.setText(ticket_used + "张");
                    tv_back_num.setText(ticket_back + "张");
                    ticket = (Integer.parseInt(ticket_count) - Integer.parseInt(ticket_used) - Integer.parseInt(ticket_back));
                    double price = ((Integer.parseInt(ticket_price)) * 0.01);
                    tv_price.setText(String.valueOf(price) + "元");
                    double backmoney = (ticket * price);
                    tv_refund_money.setText(String.valueOf(backmoney) + "元");
                    if ("1".equals(ticket_pay_way)) {
                        tv_pay_type.setText("支付宝");
                    } else if ("2".equals(ticket_pay_way)) {
                        tv_pay_type.setText("微信");
                    } else if ("5".equals(ticket_pay_way)) {
                        tv_pay_type.setText("现金");
                    } else {
                        tv_pay_type.setText("其它");
                    }
                    order_linear.setVisibility(View.VISIBLE);
                    if (ticket > 0) {
                        need_linear.setVisibility(View.VISIBLE);
                    } else {
                        need_linear.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    HiddenSoftKeyboard();
                    mDialog = DialogUtils.createLoadingDialog(RefundTicket.this, "查询中...");
                    getTicketInfo(et_codenum.getText().toString());
                    break;
                case 3:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(RefundTicket.this, "退票成功", Toast.LENGTH_SHORT).show();
                    clearUI();
                    break;
                case 5:
                    DialogUtils.closeDialog(mDialog);
                    mDialog = DialogUtils.createLoadingDialog(RefundTicket.this, "退票中...");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(RefundTicket.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("退票申请");
        et_codenum = findViewById(R.id.refund_num);
        order_linear = findViewById(R.id.refund_linear);
        tv_name = findViewById(R.id.refund_ticket_name);
        tv_count_num = findViewById(R.id.ticket_count);
        tv_used_num = findViewById(R.id.ticket_used_count);
        tv_back_num = findViewById(R.id.ticket_back_count);
        tv_price = findViewById(R.id.ticket_price);
        tv_pay_type = findViewById(R.id.ticket_pay_type);
        tv_refund_money = findViewById(R.id.ticket_refund_money);
        need_linear = findViewById(R.id.need_refund);
        tv_refund_count = findViewById(R.id.refund_count);
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-26  11:58
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.header_iv:
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(RefundTicket.this, MainActivity.class));
                } else {
                    startActivity(new Intent(RefundTicket.this, AdminActivity.class));
                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            case R.id.refund_check:
                String et_value = et_codenum.getText().toString();
                if (TextUtils.isEmpty(et_value)) {
                    warn_info = "请输入订单唯一码";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    handler.sendEmptyMessage(2);
                }
                break;
            //确定退票
            case R.id.btn_sure:
                String num = tv_refund_count.getText().toString();
                if ("0".equals(num)) {
                    warn_info = "请输入退退票数量";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    String codenum = et_codenum.getText().toString().trim();
                    String countnum = tv_refund_count.getText().toString().trim();
                    String backmoney = (tv_refund_money.getText().toString().trim().substring(0, tv_refund_money.getText().toString().trim().indexOf("元"))).trim();
                    String type = tv_pay_type.getText().toString().trim();
                    showDialog(codenum, type, countnum, backmoney);
                }
                break;
            //减少退票数量
            case R.id.refund_jian:
                int count = Integer.parseInt(tv_refund_count.getText().toString().trim());
                if (count > 0) {
                    count -= 1;
                    if (count == 0) {
                        Toast.makeText(this, "退票数量达到下限", Toast.LENGTH_SHORT).show();
                        tv_refund_money.setText(String.valueOf((Integer.parseInt(ticket_price) * ticket * 0.01)) + "元");
                        tv_refund_count.setText("0");
                    } else {
                        tv_refund_money.setText(String.valueOf((Integer.parseInt(ticket_price) * count * 0.01)) + "元");
                        tv_refund_count.setText(String.valueOf(count));
                    }
                } else {
                    Toast.makeText(this, "退票数量达到下限", Toast.LENGTH_SHORT).show();
                    tv_refund_money.setText(String.valueOf((Integer.parseInt(ticket_price) * ticket * 0.01)) + "元");
                    tv_refund_count.setText("0");
                }
                break;
            //增加退票数量
            case R.id.refund_jia:
                int countone = Integer.parseInt(tv_refund_count.getText().toString().trim());
                if (countone < ticket) {
                    countone += 1;
                    tv_refund_money.setText(String.valueOf((Integer.parseInt(ticket_price) * countone * 0.01)) + "元");
                    tv_refund_count.setText(String.valueOf(countone));
                } else if (countone == ticket) {
                    Toast.makeText(this, "退票数量达到上限", Toast.LENGTH_SHORT).show();
                    tv_refund_money.setText(String.valueOf((Integer.parseInt(ticket_price) * ticket * 0.01)) + "元");
                    tv_refund_count.setText(String.valueOf(countone));
                }
                break;
        }
    }

    /**
     * 退票确认dialog
     *
     * @param codenum
     * @param type
     * @param countnum
     * @param backmoney
     */
    private void showDialog(final String codenum, String type, final String countnum, String backmoney) {
        View inflater = LayoutInflater.from(RefundTicket.this).inflate(R.layout.refund_ticket_dialog, null);
        TextView tv_pay = inflater.findViewById(R.id.refund_type);
        TextView tv_money = inflater.findViewById(R.id.money);
        Button btn_sure = inflater.findViewById(R.id.btn_to_sure);
        Button btn_cancle = inflater.findViewById(R.id.btn_to_cancle);
        tv_money.setText(backmoney.trim() + "元");
        tv_pay.setText(type.trim());
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefundTicket(codenum, countnum);
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                tv_refund_count.setText("0");
                getTicketInfo(codenum);
            }
        });
        dialog = new Dialog(RefundTicket.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(inflater);
        Window dialogwindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = 600;
        lp.height = 600;
        dialogwindow.setAttributes(lp);
        dialogwindow.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 重置UI
     */
    private void clearUI() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
        tv_refund_count.setText("0");
        tv_refund_money.setText("");
        tv_pay_type.setText("");
        tv_back_num.setText("");
        tv_used_num.setText("");
        tv_price.setText("");
        tv_count_num.setText("");
        tv_name.setText("");
        ticket = 0;
        et_codenum.setText("");
        order_linear.setVisibility(View.GONE);
        need_linear.setVisibility(View.GONE);
    }

    /**
     * 退票
     *
     * @param codenum
     * @param countnum
     */
    private void RefundTicket(final String codenum, final String countnum) {
        if (PublicMethod.isNetworkAvailable(RefundTicket.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(5);
                    Map modelmap = new HashMap();
                    modelmap.put("order_unique_code", codenum);
                    modelmap.put("back_count", countnum);
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Map map = httpUtil.RefundTicket(modelmap);
                    Log.e("mapmapmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无退票信息";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            handler.sendEmptyMessage(3);
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
     * 获取订单详情
     *
     * @param code
     */
    private void getTicketInfo(final String code) {
        if (PublicMethod.isNetworkAvailable(RefundTicket.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("order_unique_code", code);
                    Map map = httpUtil.checkTicket(modelmap);
                    Log.e("refundmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无当前订单详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            DialogUtils.closeDialog(mDialog);
                            Map datamap = (Map) map.get("data");
                            ticketname = datamap.get("order_title") == null ? "" : String.valueOf(datamap.get("order_title"));
                            ticket_price = datamap.get("price") == null ? "" : String.valueOf(datamap.get("price"));
                            ticket_count = datamap.get("num") == null ? "" : String.valueOf(datamap.get("num"));
                            ticket_used = datamap.get("used") == null ? "" : String.valueOf(datamap.get("used"));
                            ticket_back = datamap.get("back") == null ? "" : String.valueOf(datamap.get("back"));
                            ticket_pay_way = datamap.get("payment_type") == null ? "" : String.valueOf(datamap.get("payment_type"));
                            handler.sendEmptyMessage(1);
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
     * 隐藏软键盘
     */
    private void HiddenSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ("0".equals(PublicMethod.BACK_MARK)) {
                startActivity(new Intent(RefundTicket.this, MainActivity.class));
            } else {
                startActivity(new Intent(RefundTicket.this, AdminActivity.class));
            }
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
