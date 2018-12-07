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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * 功能描述:设置
 * 创建时间: 2018-10-18 13:54
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //标题
    private TextView tv_title;
    //域名地址
    private TextView tv_address;
    //设备码
    private TextView tv_machine_code;
    //设备密码
    private TextView tv_password;
    //销票 不销票 radiobutton
    private RadioButton rb_destory_ticket, rb_not_destory_ticket;
    //启用 不启用刷ic卡 radiobutton
    private RadioButton rb_open_IcCard, rb_not_open_IcCard;
    //启用 不启用刷身份证 radiobutton
    private RadioButton rb_open_IdCard, rb_not_open_IdCard;
    //打印票类型 小票  纸质票 radiobutton
    private RadioButton rb_tickettype_small, rb_tickettype_paper;
    //票码类型  一码一票 一码多票radiobutton
    private RadioButton rb_ticket_code_type_one, rb_ticket_code_type_lot;
    //启用 不启用 打印机  radiobutton
    private RadioButton rb_open_printer, rb_not_open_printer;
    //销票 radiogroup
    private RadioGroup rg_destory_ticket;
    //ic卡 radiogroup
    private RadioGroup rg_open_iccard;
    //身份证 radiogroup
    private RadioGroup rg_Idgroup;
    //票类型 radiogroup
    private RadioGroup rg_ticket_type;
    //票码类型 radiogroup
    private RadioGroup rg_ticket_code_type;
    //打印机 radiogroup
    private RadioGroup rg_printer;

    //加载动画
    private Dialog mDialog;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(SettingActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(SettingActivity.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("系统设置");
        rg_destory_ticket = (RadioGroup) findViewById(R.id.rg_destory_ticket);
        rb_destory_ticket = (RadioButton) findViewById(R.id.rb_destory_ticket);
        rb_not_destory_ticket = (RadioButton) findViewById(R.id.rb_not_destory);
        rg_destory_ticket.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_destory_ticket:
                        PublicMethod.DESTOTYTICKET = "0";
                        rb_destory_ticket.setChecked(true);
                        break;
                    case R.id.rb_not_destory:
                        PublicMethod.DESTOTYTICKET = "1";
                        rb_not_destory_ticket.setChecked(true);
                        break;
                }
            }
        });
        tv_address = (TextView) findViewById(R.id.tv_domain_address);
        tv_machine_code = (TextView) findViewById(R.id.tv_machine_code);
        tv_password = (TextView) findViewById(R.id.tv_machine_password);
        rg_open_iccard = (RadioGroup) findViewById(R.id.rg_open_iccard);
        rb_open_IcCard = (RadioButton) findViewById(R.id.rb_open_iccard);
        rb_not_open_IcCard = (RadioButton) findViewById(R.id.rb_not_open_iccard);
        rg_open_iccard.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_open_iccard:
                        PublicMethod.ISNEEDICCARD = "0";
                        rb_open_IcCard.setChecked(true);
                        break;
                    case R.id.rb_not_open_iccard:
                        PublicMethod.ISNEEDICCARD = "1";
                        rb_not_open_IcCard.setChecked(true);
                        break;
                }
            }
        });
        rg_Idgroup = (RadioGroup) findViewById(R.id.rg_Idgroup);
        rb_open_IdCard = (RadioButton) findViewById(R.id.rb_open_idcard);
        rb_not_open_IdCard = (RadioButton) findViewById(R.id.not_open_idcard);
        rg_Idgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_open_idcard:
                        PublicMethod.ISNEEDIDCARD = "0";
                        rb_open_IdCard.setChecked(true);
                        break;
                    case R.id.not_open_idcard:
                        PublicMethod.ISNEEDIDCARD = "1";
                        rb_not_open_IdCard.setChecked(true);
                        break;
                }
            }
        });
        rg_ticket_type = (RadioGroup) findViewById(R.id.rg_ticket_type);
        rb_tickettype_small = (RadioButton) findViewById(R.id.rb_ticket_type_small);
        rb_tickettype_paper = (RadioButton) findViewById(R.id.rb_ticket_type_paper);
        rg_ticket_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_ticket_type_small:
                        PublicMethod.TICKETTYPE = "0";
                        rb_tickettype_small.setChecked(true);
                        break;
                    case R.id.rb_ticket_type_paper:
                        PublicMethod.TICKETTYPE = "1";
                        rb_tickettype_paper.setChecked(true);
                        break;
                }
            }
        });
        rg_ticket_code_type = (RadioGroup) findViewById(R.id.rg_ticket_code_type);
        rb_ticket_code_type_one = (RadioButton) findViewById(R.id.rb_ticket_code_type_one);
        rb_ticket_code_type_lot = (RadioButton) findViewById(R.id.rb_ticket_code_type_lot);
        rg_ticket_code_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_ticket_code_type_one:
                        PublicMethod.CODETYPE = "0";
                        rb_ticket_code_type_one.setChecked(true);
                        break;
                    case R.id.rb_ticket_code_type_lot:
                        PublicMethod.CODETYPE = "1";
                        rb_ticket_code_type_lot.setChecked(true);
                        break;
                }
            }
        });
        rg_printer = findViewById(R.id.rg_open_printer);
        rb_open_printer = findViewById(R.id.rb_open_printer);
        rb_not_open_printer = findViewById(R.id.rb_not_open_printer);
        rg_printer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_open_printer:
                        PublicMethod.ISPRINTER = "1";
                        rb_open_printer.setChecked(true);
                        break;
                    case R.id.rb_not_open_printer:
                        PublicMethod.ISPRINTER = "0";
                        rb_not_open_printer.setChecked(true);
                        break;
                }
            }
        });
        setData();
    }

    /**
     * *******************************
     * 控件赋值
     *
     * @Author lbr
     * create time 2018-10-18  15:46
     * *******************************
     */
    private void setData() {
        tv_address.setText(PublicMethod.SYSTEMSETTINGADDRESS.trim());
        tv_machine_code.setText(PublicMethod.SYSTEMSETTINGMACHINECODE.trim());
        tv_password.setText(PublicMethod.SYSTEMSETTINGPASSWORD.trim());
        if ("0".equals(PublicMethod.DESTOTYTICKET)) {
            rb_destory_ticket.setChecked(true);
        } else {
            rb_not_destory_ticket.setChecked(true);
        }
        if ("0".equals(PublicMethod.ISNEEDICCARD)) {
            rb_open_IcCard.setChecked(true);
        } else {
            rb_not_open_IcCard.setChecked(true);
        }
        if ("0".equals(PublicMethod.ISNEEDIDCARD)) {
            rb_open_IdCard.setChecked(true);
        } else {
            rb_not_open_IdCard.setChecked(true);
        }
        if ("0".equals(PublicMethod.TICKETTYPE)) {
            rb_tickettype_small.setChecked(true);
        } else {
            rb_tickettype_paper.setChecked(true);
        }
        if ("0".equals(PublicMethod.CODETYPE)) {
            rb_ticket_code_type_one.setChecked(true);
        } else {
            rb_ticket_code_type_lot.setChecked(true);
        }
        if ("0".equals(PublicMethod.ISPRINTER)) {
            rb_not_open_printer.setChecked(true);
        } else {
            rb_open_printer.setChecked(true);
        }
    }

    /**
     * *******************************
     * 按钮点击事件
     *
     * @Author lbr
     * create time 2018-10-18  15:46
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回按钮
            case R.id.header_iv:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    startActivity(new Intent(SettingActivity.this, MainActivity.class));

                } else {
                    if ("0".equals(PublicMethod.BACK_MARK)) {
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SettingActivity.this, AdminActivity.class));
                    }
                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //上传设置信息
            case R.id.system_settings_btn_sure:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    warn_info = "当前账号无法使用该功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    mDialog = DialogUtils.createLoadingDialog(SettingActivity.this, "上传中...");
                    uploadSettingToServer();
                }
                break;
        }
    }

    /**
     * *******************************
     * 上传设置信息
     *
     * @Author lbr
     * create time 2018-10-18  15:48
     * *******************************
     */
    private void uploadSettingToServer() {
        if (PublicMethod.isNetworkAvailable(SettingActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                Map modelmap = new HashMap();
                modelmap.put("check_juyoucard_type", PublicMethod.ISNEEDICCARD.trim());
                modelmap.put("check_personcard_type", PublicMethod.ISNEEDIDCARD.trim());
                modelmap.put("code", PublicMethod.SYSTEMSETTINGMACHINECODE.trim());
                modelmap.put("id", PublicMethod.INFOID.trim());
                modelmap.put("netarea", PublicMethod.SYSTEMSETTINGADDRESS.trim());
                modelmap.put("websocket", PublicMethod.SYSTEMSETTINGWEBSOCKETADDRESS.trim());
                modelmap.put("print_destory_type", PublicMethod.DESTOTYTICKET.trim());
                modelmap.put("print_ticket_type", PublicMethod.TICKETTYPE.trim());
                modelmap.put("ticket_code_type", PublicMethod.CODETYPE.trim());
                Map updatemap = httpUtil.uploadSettingToServer(modelmap);
                Log.e("updatemap", String.valueOf(updatemap));
                if (PublicMethod.checkIfNull(updatemap)) {
                    warn_info = "上传信息失败1";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if (updatemap.containsKey("errcode") && "0".equals(updatemap.get("errcode").toString())) {
                        warn_info = "上传成功";
                        handler.sendEmptyMessage(0);
                    } else {
                        warn_info = "错误信息:" + updatemap.get("errmsg").toString().trim();
                        handler.sendEmptyMessage(0);
                    }
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                startActivity(new Intent(SettingActivity.this, MainActivity.class));

            } else {
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(SettingActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SettingActivity.this, AdminActivity.class));
                }
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
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
