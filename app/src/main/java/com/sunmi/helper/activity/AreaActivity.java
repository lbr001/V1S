package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

/**
 * @Author lbr
 * 功能描述:活动专区
 * 创建时间: 2018-10-18 13:54
 */
public class AreaActivity extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    //标题
    private TextView tv_title;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(AreaActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(AreaActivity.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("活动专区");
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-11-8  14:17
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.header_iv:
                if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                    startActivity(new Intent(AreaActivity.this, MainActivity.class));
                } else {
                    if ("0".equals(PublicMethod.BACK_MARK)) {
                        startActivity(new Intent(AreaActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(AreaActivity.this, AdminActivity.class));
                    }
                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //招徕奖励
            case R.id.btn_attcrt:
                if (TextUtils.isEmpty(PublicMethod.ACTIVITIES)) {
                    warn_info = "权限不足";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if (PublicMethod.ACTIVITIES.contains("a")) {
                        startActivity(new Intent(AreaActivity.this, AttcrtActivity.class));
                        overridePendingTransition(R.anim.scale_rotate,
                                R.anim.my_alpha_action);
                    } else {
                        warn_info = "权限不足";
                        handler.sendEmptyMessage(0);
                        return;
                    }
                }
                break;
            //领取补贴
            case R.id.btn_ly:
                if (TextUtils.isEmpty(PublicMethod.ACTIVITIES)) {
                    warn_info = "权限不足";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if (PublicMethod.ACTIVITIES.contains("b")) {
                        startActivity(new Intent(AreaActivity.this, SubsidyActivity.class));
                        overridePendingTransition(R.anim.scale_rotate,
                                R.anim.my_alpha_action);
                    } else {
                        warn_info = "权限不足";
                        handler.sendEmptyMessage(0);
                        return;
                    }
                }
                break;
            //激活Ic卡
            case R.id.jh_ic:
                if (TextUtils.isEmpty(PublicMethod.ACTIVITIES)) {
                    warn_info = "权限不足";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if (PublicMethod.ACTIVITIES.contains("c")) {
                        startActivity(new Intent(AreaActivity.this, SensitizedActivity.class));
                        overridePendingTransition(R.anim.scale_rotate,
                                R.anim.my_alpha_action);
                    } else {
                        warn_info = "权限不足";
                        handler.sendEmptyMessage(0);
                        return;
                    }
                }
                break;
            //会员专区
            case R.id.btn_hy:
                Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
                break;
            //实名认证
            case R.id.btn_smrz:
                if (TextUtils.isEmpty(PublicMethod.ACTIVITIES)) {
                    warn_info = "权限不足";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    if (PublicMethod.ACTIVITIES.contains("c")) {
                        startActivity(new Intent(AreaActivity.this, Certification.class));
                        overridePendingTransition(R.anim.scale_rotate,
                                R.anim.my_alpha_action);
                    } else {
                        warn_info = "权限不足";
                        handler.sendEmptyMessage(0);
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (PublicMethod.SERIZLNUMBER.equals(PublicMethod.login_name)) {
                startActivity(new Intent(AreaActivity.this, MainActivity.class));
            } else {
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(AreaActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(AreaActivity.this, AdminActivity.class));
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
        handler.removeCallbacksAndMessages(null);
    }
}
