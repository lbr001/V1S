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
import com.sunmi.helper.nfcutil.NfcOperation;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PhoneUtil;
import com.sunmi.helper.utils.PublicMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author lbr
 * 功能描述:激活IC卡
 * 创建时间: 2018-11-8 14:27
 */
public class SensitizedActivity extends Activity implements View.OnClickListener {

    //读取ic卡工具类
    private NfcOperation nfcOperation;
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //加载动画
    private Dialog mDialog;
    //IC卡卡内号
    private String Ic_Num;
    //IC卡卡号
    private String Ic_Num_one;
    //输入电话号码控件
    private EditText et_phoneNum;
    //姓名
    private TextView tv_name;
    //性别
    private TextView tv_sex;
    //民族
    private TextView tv_nation;
    //出生年
    private TextView tv_born_nian;
    //出生月
    private TextView tv_born_yue;
    //出生日
    private TextView tv_born_ri;
    //身份证地址
    private TextView tv_address;
    //身份证号码
    private TextView tv_id_num;
    //IC卡卡号
    private TextView tv_ic_num;
    //身份证头像
    private ImageView imageView;
    private ExecutorService singleThreadPool = null;
    private boolean canread;
    private boolean run;
    private Bitmap bitmap;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(SensitizedActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    getIcNum();
                    break;
                case 3:
                    tv_ic_num.setText(Ic_Num_one.trim());
                    break;
                case 5:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(SensitizedActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    clearUI();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitized);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(SensitizedActivity.this);
        initViews();
        nfcOperation = new NfcOperation(SensitizedActivity.this);
//        run = true;
//        readCard();
    }

//    /**
//     * 循环读卡
//     */
//    private void readCard() {
//        if (singleThreadPool == null) {
//            singleThreadPool = Executors.newSingleThreadExecutor();
//        }
//        singleThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (run) {
//                    if (canread) {
//                        getICInfo();
//                    } else {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//    }


    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-11-16  10:38
     * *******************************
     */
    private void initViews() {
        tv_name = findViewById(R.id.sen_name);
        tv_sex = findViewById(R.id.sen_sex);
        tv_nation = findViewById(R.id.sen_nation);
        tv_born_nian = findViewById(R.id.sen_born_nian);
        tv_born_yue = findViewById(R.id.sen_born_yue);
        tv_born_ri = findViewById(R.id.sen_born_ri);
        tv_address = findViewById(R.id.sen_address);
        tv_id_num = findViewById(R.id.sen_no);
        tv_ic_num = findViewById(R.id.iccard_num);
        imageView = findViewById(R.id.sen_man);
        et_phoneNum = findViewById(R.id.et_phoneNum);
    }

    /**
     * *******************************
     * 获取IC卡 卡号
     *
     * @Author lbr
     * create time 2018-11-16  11:02
     * *******************************
     */
    private void getIcNum() {
        if (PublicMethod.isNetworkAvailable(SensitizedActivity.this)) {
            warn_info = "请检查网络连接";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("cardinsidenum", Ic_Num);
                    Map cardmap = httpUtil.getCardNum(modelmap);
                    Log.e("Cardmap", cardmap.toString());
                    if (PublicMethod.checkIfNull(cardmap)) {
                        warn_info = "暂无IC卡详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (cardmap.containsKey("errcode") && "0".equals(cardmap.get("errcode").toString())) {
                            if (cardmap.containsKey("data")) {
                                Object object = cardmap.get("data");
                                if (object == null || !(object instanceof Map)) {
                                    warn_info = "无数据返回1";
                                    handler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    Map datamap = (Map) object;
                                    if (datamap.containsKey("cardno")) {
                                        Ic_Num_one = datamap.get("cardno") == null ? "" : String.valueOf(datamap.get("cardno"));
                                        handler.sendEmptyMessage(3);
                                    } else {
                                        warn_info = "无数据返回2";
                                        handler.sendEmptyMessage(0);
                                        return;
                                    }
                                }
                            } else {
                                warn_info = "无数据返回";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        } else {
                            if (cardmap.containsKey("errmsg")) {
                                warn_info = cardmap.get("errmsg").toString();
                            } else {
                                warn_info = "IC卡数据错误";
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }

                }
            }.start();
        }
    }

    /**
     * 重置UI
     */
    private void clearUI() {
        canread = true;
        tv_ic_num.setText("");
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
     * 点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.Ima_sen:
                startActivity(new Intent(SensitizedActivity.this, AreaActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //重置
            case R.id.Sen_clear:
                clearUI();
                break;
            //确认激活
            case R.id.sen_btn_sure:
                if (TextUtils.isEmpty(tv_name.getText().toString())) {
                    warn_info = "请刷身份证";
                    handler.sendEmptyMessage(0);
                    return;
                } else if (TextUtils.isEmpty(tv_ic_num.getText().toString())) {
                    warn_info = "请刷IC卡";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    String phoneNum = et_phoneNum.getText().toString();
                    if (TextUtils.isEmpty(phoneNum)) {
                        warn_info = "请输入手机号码";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        boolean isnum = PhoneUtil.isPhoneLegal(phoneNum);
                        if ("false".equals(isnum)) {
                            warn_info = "请输入正确的手机号码";
                            handler.sendEmptyMessage(0);
                            return;
                        } else {
                            canread = false;
                            mDialog = DialogUtils.createLoadingDialog(SensitizedActivity.this, "激活中...");
                            getLQInfo(tv_name.getText().toString().trim(), tv_id_num.getText().toString().trim(), Ic_Num.trim(), tv_address.getText().toString().trim(), PublicMethod.login_name.trim(), PublicMethod.machinecode.trim(), phoneNum);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 确认激活
     *
     * @param username//姓名
     * @param papersno//身份证号码
     * @param cardinsidenum//ic卡卡内号
     * @param postaddress//地址
     * @param loginname//登录名
     * @param machinecode//登录密码
     */
    private void getLQInfo(final String username, final String papersno, final String cardinsidenum, final String postaddress, final String loginname, final String machinecode, final String mobile) {
        if (PublicMethod.isNetworkAvailable(SensitizedActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("username", username);
                    modelmap.put("papersno", papersno);
                    modelmap.put("cardinsidenum", cardinsidenum);
                    modelmap.put("postaddress", postaddress);
                    modelmap.put("loginname", loginname);
                    modelmap.put("machinecode", machinecode);
                    modelmap.put("mobile", mobile);
                    Map cardmap = httpUtil.ActiveCard(modelmap);
                    Log.e("cardmap1111", "" + cardmap);
                    if (PublicMethod.checkIfNull(cardmap)) {
                        warn_info = "暂无激活数据";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (cardmap.containsKey("errcode") && "0".equals(cardmap.get("errcode").toString())) {
                            warn_info = "激活成功";
                            handler.sendEmptyMessage(5);
                        } else {
                            if (cardmap.containsKey("errmsg")) {
                                warn_info = cardmap.get("errmsg").toString();
                            } else {
                                warn_info = "激活数据错误1";
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Ic_Num = nfcOperation.readIcCardByBlock(intent);
        if (!TextUtils.isEmpty(Ic_Num)) {
            Log.e("mnum", Ic_Num);
            handler.sendEmptyMessage(2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcOperation.enableDispatch(SensitizedActivity.this);
        canread = true;
        run = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcOperation.disEnableDispatch(SensitizedActivity.this);
        run = false;
        canread = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        run = false;
        canread = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SensitizedActivity.this, AreaActivity.class));
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
        run = false;
        canread = false;
    }

}
