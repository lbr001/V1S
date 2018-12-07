package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;


/**
 * *******************************
 * 设备码登录主页
 *
 * @Author lbr
 * create time 2018-10-18  10:24
 * *******************************
 */
public class MainActivity extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(MainActivity.this);
    }

    /**
     * *******************************
     * 按钮点击事件
     *
     * @Author lbr
     * create time 2018-10-18  14:28
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //产品售卖
            case R.id.btn_main_product_sale:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    warn_info = "当前账号无法操作此功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    startActivity(new Intent(MainActivity.this, ProductSale.class));
                    overridePendingTransition(R.anim.scale_rotate,
                            R.anim.my_alpha_action);
                }
                break;
            //门票核销
            case R.id.btn_main_ticket_destory:
                startActivity(new Intent(MainActivity.this, TicketActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //销售历史
            case R.id.btn_main_sale_history:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    warn_info = "当前账号无法操作此功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    startActivity(new Intent(MainActivity.this, SaleHistory.class));
                    overridePendingTransition(R.anim.scale_rotate,
                            R.anim.my_alpha_action);
                }
                break;
            //退票详情
            case R.id.btn_main_refund_ticket:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    warn_info = "当前账号无法操作此功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if ("0".equals(PublicMethod.BACK_MARK)) {
                        warn_info = "当前账号无权限操作改功能";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        startActivity(new Intent(MainActivity.this, RefundTicket.class));
                        overridePendingTransition(R.anim.scale_rotate,
                                R.anim.my_alpha_action);
                    }
                }
                break;
            //营销统计
            case R.id.btn_main_market:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    warn_info = "无法操作此功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if ("0".equals(PublicMethod.BACK_MARK)) {
                        warn_info = "当前账号无权限操作该功能";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        startActivity(new Intent(MainActivity.this, MarketInfoActivity.class));
                        overridePendingTransition(R.anim.scale_rotate,
                                R.anim.my_alpha_action);
                    }
                }
                break;
            //会员专区
            case R.id.btn_main_area:
                startActivity(new Intent(MainActivity.this, AreaActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //设置
            case R.id.btn_main_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                return;
            //退出
            case R.id.btn_main_exit:
                dialog();
                break;
        }

    }

    /**
     * 退出提示框
     */
    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确认退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    private long down;

    /**
     * 物理按键(返回键)点击事件
     *
     * @param keyCode
     * @param event
     * @return
     */
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
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
