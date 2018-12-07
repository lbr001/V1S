package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
 * 功能描述:实名制认证
 * 创建时间: 2018-11-8 14:28
 */
public class Certification extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //加载动画
    private Dialog mDialog;
    //手机号码输入框
    private EditText et_phoneNum;
    //姓名
    private TextView tv_name;
    //性别
    private TextView tv_sex;
    //民族
    private TextView tv_nation;
    //出生年份
    private TextView tv_born_nian;
    //出生月份
    private TextView tv_born_yue;
    //出生日期
    private TextView tv_born_ri;
    //身份证地址
    private TextView tv_address;
    //身份证号码
    private TextView tv_id_num;
    //身份证头像
    private ImageView imageView;
    //头像数据
    private Bitmap bitmap;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(Certification.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    mDialog = DialogUtils.createLoadingDialog(Certification.this, "认证中...");
                    String mobile = et_phoneNum.getText().toString().trim();
                    String username = tv_name.getText().toString().trim();
                    String papersno = tv_id_num.getText().toString().trim();
                    String postaddress = tv_address.getText().toString().trim();
//                    canread = false;
                    getCeInfo(mobile, username, postaddress, papersno);
                    break;
                case 4:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(Certification.this, "实名制认证成功", Toast.LENGTH_SHORT).show();
                    clearUI();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smrz);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(Certification.this);
        initView();
    }

    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-11-21  10:38
     * *******************************
     */
    private void initView() {
        tv_name = findViewById(R.id.smrz_name);
        imageView = findViewById(R.id.smrz_man);
        tv_sex = findViewById(R.id.smrz_sex);
        tv_address = findViewById(R.id.smrz_address);
        tv_born_nian = findViewById(R.id.smrz_born_nian);
        tv_born_yue = findViewById(R.id.smrz_born_yue);
        tv_born_ri = findViewById(R.id.smrz_born_ri);
        tv_id_num = findViewById(R.id.smrz_no);
        tv_nation = findViewById(R.id.smrz_nation);
        et_phoneNum = findViewById(R.id.smrz_et_phoneNum);
    }

    /**
     * 重置UI
     */
    private void clearUI() {
        tv_id_num.setText("");
        tv_address.setText("");
        tv_nation.setText("");
        tv_born_nian.setText("");
        tv_born_ri.setText("");
        tv_born_yue.setText("");
        tv_sex.setText("");
        tv_name.setText("");
        et_phoneNum.setText("");
        if (bitmap != null) {
            bitmap = null;
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * *******************************
     * 实名制认证
     *
     * @Author lbr
     * create time 2018-11-21  10:42
     * *******************************
     */
    private void getCeInfo(final String mobile, final String username, final String postaddress, final String papersno) {
        if (PublicMethod.isNetworkAvailable(this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("mobile", mobile);
                    modelmap.put("username", username);
                    modelmap.put("postaddress", postaddress);
                    modelmap.put("papersno", papersno);
                    Map cemap = httpUtil.getCeInfo(modelmap);
                    Log.e("cemap", "" + cemap);
                    if (PublicMethod.checkIfNull(cemap)) {
                        warn_info = "暂无实名制认证信息1";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (cemap.containsKey("errcode") && "0".equals(cemap.get("errcode").toString())) {
                            if (cemap.containsKey("data")) {
                                String data = (String) cemap.get("data");
                                if ("1000".equals(data)) {
                                    handler.sendEmptyMessage(4);
                                }
                            } else {
                                warn_info = "暂无实名制信息3";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        } else {
                            if (cemap.containsKey("errmsg")) {
                                warn_info = cemap.get("errmsg").toString();
                            } else {
                                warn_info = "暂无实名制认证信息2";
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-11-21  10:40
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //重置
            case R.id.smrz_clear:
                clearUI();
                break;
            //返回
            case R.id.Ima_smrz:
                startActivity(new Intent(Certification.this, AreaActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //实名认证
            case R.id.smrz_btn_sure:
                if (TextUtils.isEmpty(tv_name.getText().toString())) {
                    warn_info = "请刷身份证";
                    handler.sendEmptyMessage(0);
                    return;
                } else if (TextUtils.isEmpty(et_phoneNum.getText().toString())) {
                    warn_info = "请输入手机号码";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    handler.sendEmptyMessage(2);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(Certification.this, AreaActivity.class));
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
    }
}
