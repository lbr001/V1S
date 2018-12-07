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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:领取补贴
 * 创建时间: 2018-11-8 14:26
 */
public class SubsidyActivity extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //身份证头像
    private ImageView imageView;
    //姓名
    private TextView tv_name;
    //出生年份
    private TextView tv_born_nian;
    //出生月份
    private TextView tv_born_yue;
    //出生日期
    private TextView tv_born_ri;
    //身份证地址
    private TextView tv_address;
    //性别
    private TextView tv_sex;
    //民族
    private TextView tv_nation;
    //身份证号码
    private TextView tv_cardnum;
    //补贴领取时间
    private TextView tv_lqsj;
    //补贴领取状态
    private TextView tv_lqzt;
    //证件号码
    private TextView tv_zjhm;
    //领取补贴按钮
    private Button btn_lq;
    //初始界面布局
    private RelativeLayout relativeLayout;
    //已领取补贴布局
    private LinearLayout linearLayout;
    //头像
    private Bitmap bitmap;
    //加载动画
    private Dialog mDialog;
    //补贴领取时间
    private String otime;
    //补贴领取详情
    private Map datamap;
    //补贴领取状态
    private String state;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(SubsidyActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    tv_zjhm.setText(tv_cardnum.getText().toString().trim());
                    tv_lqzt.setText(state.trim());
                    tv_lqsj.setText(datamap.get("otime") == null ? "" : String.valueOf(datamap.get("otime")));
                    relativeLayout.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    relativeLayout.setVisibility(View.GONE);
                    btn_lq.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    Toast.makeText(SubsidyActivity.this, "领取成功", Toast.LENGTH_SHORT).show();
                    Print();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subsidies);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(SubsidyActivity.this);
        //初始化打印机(连接)
        AidlUtil.getInstance().connectPrinterService(SubsidyActivity.this);
        initView();
    }

    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-11-20  14:00
     * *******************************
     */
    private void initView() {
        imageView = findViewById(R.id.subsidies_man);
        tv_name = findViewById(R.id.tv_man_name);
        tv_sex = findViewById(R.id.tv_man_sex);
        tv_nation = findViewById(R.id.tv_man_nation);
        tv_born_nian = findViewById(R.id.tv_man_born_nian);
        tv_born_yue = findViewById(R.id.tv_man_born_yue);
        tv_born_ri = findViewById(R.id.tv_man_born_ri);
        tv_address = findViewById(R.id.tv_man_address);
        tv_cardnum = findViewById(R.id.tv_man_no);
        relativeLayout = findViewById(R.id.sub_relativelayout);
        btn_lq = findViewById(R.id.btn_lq);
        tv_lqsj = findViewById(R.id.lqsj);
        tv_lqzt = findViewById(R.id.lqzt);
        linearLayout = findViewById(R.id.info_linear);
        tv_zjhm = findViewById(R.id.zjhm);
    }

    /**
     * 重置UI
     */
    private void clear() {
        tv_cardnum.setText("");
        tv_name.setText("");
        tv_sex.setText("");
        tv_born_nian.setText("");
        tv_born_ri.setText("");
        tv_born_yue.setText("");
        tv_nation.setText("");
        tv_address.setText("");
        if (bitmap != null) {
            bitmap = null;
            imageView.setImageBitmap(bitmap);
        }
        relativeLayout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        btn_lq.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.Ima_sub:
                startActivity(new Intent(SubsidyActivity.this, AreaActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //领取补贴
            case R.id.btn_lq:
                mDialog = DialogUtils.createLoadingDialog(SubsidyActivity.this, "领取中...");
                getCouOrder();
                break;
            //重置
            case R.id.btn_sub_clear:
                clear();
                break;
            //已领取确认
            case R.id.btn_sure:
                clear();
                break;
            //领取详情
            case R.id.btn_subinfo:
                startActivity(new Intent(SubsidyActivity.this, SubListInfo.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
        }
    }

    /**
     * 查看补贴详情
     */
    private void getLQInfo() {
        if (PublicMethod.isNetworkAvailable(SubsidyActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("idcard", tv_cardnum.getText().toString().trim());
                    Map infomap = httpUtil.getLQinfo(modelmap);
                    Log.e("infomap", "" + infomap);
                    if (PublicMethod.checkIfNull(infomap)) {
                        warn_info = "暂无领取详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (infomap.containsKey("errcode") && "0".equals(infomap.get("errcode").toString())) {
                            if (infomap.containsKey("data")) {
                                datamap = (Map) infomap.get("data");
                                if (PublicMethod.checkIfNull(datamap)) {
                                    warn_info = "暂无数据3";
                                    handler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    state = "已领取";
                                    handler.sendEmptyMessage(2);
                                }

                            } else {
                                warn_info = "暂无数据2";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        } else if ("10003".equals(infomap.get("errcode").toString())) {
                            handler.sendEmptyMessage(4);
                        } else {
                            if (infomap.containsKey("errmsg")) {
                                warn_info = infomap.get("errmsg").toString();
                            } else {
                                warn_info = "数据错误1";
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
     * 领取补贴
     *
     * @Author lbr
     * create time 2018-11-20  14:15
     * *******************************
     */
    private void getCouOrder() {
        if (PublicMethod.isNetworkAvailable(SubsidyActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("name", tv_name.getText().toString());
                    modelmap.put("address", tv_address.getText().toString().trim());
                    modelmap.put("idcard", tv_cardnum.getText().toString().trim());
                    modelmap.put("view", PublicMethod.SPOT_VIEW.trim());
                    Map coumap = httpUtil.getCouOrder(modelmap);
                    Log.e("coumap", "" + coumap);
                    if (PublicMethod.checkIfNull(coumap)) {
                        warn_info = "暂无领取信息";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (coumap.containsKey("errcode") && "0".equals(coumap.get("errcode").toString())) {
                            DialogUtils.closeDialog(mDialog);
                            if (coumap.containsKey("data")) {
                                otime = coumap.get("data") == null ? "" : String.valueOf(coumap.get("data"));
                                handler.sendEmptyMessage(5);
                            } else {
                                warn_info = "领取失败";
                                handler.sendEmptyMessage(0);
                            }

                        } else {
                            if (coumap.containsKey("errmsg")) {
                                warn_info = coumap.get("errmsg").toString();
                            } else {
                                warn_info = "数据错误5";
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
     * 领取补贴打印
     *
     * @Author lbr
     * create time 2018-11-20  14:26
     * *******************************
     */
    private void Print() {
        float size = Integer.parseInt("24");
        StringBuffer sb = new StringBuffer();
        sb.append("领取补贴凭证" + "\n");
        sb.append("*******************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        StringBuffer sb1 = new StringBuffer();
        String name = tv_name.getText().toString();
        String id_num = tv_cardnum.getText().toString();
        sb1.append("景区编码:" + PublicMethod.SPOT_VIEW + "\n");
        sb1.append("顾客姓名:" + name + "\n");
        sb1.append("证件号码:" + id_num + "\n");
        sb1.append("领取状态:" + "已领取" + "\n");
        sb1.append("领取时间:" + otime + "\n");
        AidlUtil.getInstance().printText(sb1.toString(), size, false, false);
        clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SubsidyActivity.this, AreaActivity.class));
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
