package com.sunmi.helper.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;
import com.sunmi.helper.view.AlwaysMarqueeTextView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @Author lbr
 * 功能描述:登录界面
 * 创建时间: 2018-10-17 15:15
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    //版本号
    private TextView tv_version_code;
    //景区名称
    private AlwaysMarqueeTextView tv_spot_name;
    //用户名  密码
    private EditText et_username, et_password;
    //获取设备码  清空设备码
    private Button btn_get_num, btn_clear_num;
    //提示信息
    private String warn_info;
    //加载框
    private Dialog mDialog;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //物理按键点击时间
    private long down = 0;
    private RelativeLayout relativeLayout;
    /******************************** 记住用户名、密码**********************************************/
    //checkbox 用户名 密码
    private CheckBox checkBox_name, checkBox_pwd;
    //声明一个SharedPreferences 对象和一个Editor对象
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /******************************** 记住用户名、密码**********************************************/
    private ToggleButton tbPasswordVisibility;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    MakeToast(warn_info);
                    break;
                case 1:
                    tv_spot_name.setText(PublicMethod.SPOT_NAME);
                    getSystemSetting();
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    startMainActivity();
                    break;
                case 3:
                    getMarketInfo();
                    break;
                case 4:
                    DialogUtils.closeDialog(mDialog);
                    startMainActivityOne();
                    break;
                case 5:
                    DialogUtils.closeDialog(mDialog);
                    startMainActivity();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color_17));
        ExitAppUtils.getInstance().addActivity(LoginActivity.this);
        initViews();
    }

    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-10-17  16:19
     * *******************************
     */
    private void initViews() {
        String version_code = PublicMethod.getVersionName(LoginActivity.this);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String imei = telephonyManager.getDeviceId();
        PublicMethod.SERIZLNUMBER = imei.trim();
        relativeLayout = findViewById(R.id.login_relative);
        tv_version_code = findViewById(R.id.tv_version_code);
        tv_version_code.setText("当前版本:" + "beta" + version_code.trim());
        tv_spot_name = findViewById(R.id.tv_spot_name);
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        btn_get_num = findViewById(R.id.get_num);
        btn_clear_num = findViewById(R.id.btn_clear);
        checkBox_name = findViewById(R.id.checkbox1);
        checkBox_pwd = findViewById(R.id.checkbox2);
        //获取preferences和editor对象
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        /*
    启动程序时首先检查sharedPreferences中是否储存有用户名和密码
    若无，则将checkbox状态显示为未选中
    若有，则直接中sharedPreferences中读取用户名和密码，并将checkbox状态显示为已选中
    这里getString()方法需要两个参数，第一个是键，第二个是值。
    启动程序时我们传入需要读取的键，值填null即可。若有值则会自动显示，没有则为空。
     */
        String name = preferences.getString("userName", null);
        if (name == null) {
            checkBox_name.setChecked(false);
        } else {
            et_username.setText(name);
            checkBox_name.setChecked(true);
        }
        String password = preferences.getString("userPassword", null);
        if (password == null) {
            checkBox_pwd.setChecked(false);
        } else {
            et_password.setText(password);
            checkBox_pwd.setChecked(true);
        }
        tbPasswordVisibility = findViewById(R.id.tb_password_visibility);
        tbPasswordVisibility.setOnCheckedChangeListener(new ToggleButtonClick());
        getSpotName();
    }

    //密码可见性按钮监听
    private class ToggleButtonClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            //5、判断事件源的选中状态
            if (b) {

                //显示密码
                //etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // 隐藏密码
                //etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
            //6、每次显示或者关闭时，密码显示编辑的线不统一在最后，下面是为了统一
            et_password.setSelection(et_password.length());
        }
    }

    /**
     * *******************************
     * 获取景区名称
     *
     * @Author lbr
     * create time 2018-10-17  16:34
     * *******************************
     */
    private void getSpotName() {
        if (PublicMethod.isNetworkAvailable(LoginActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("device", PublicMethod.SERIZLNUMBER);
                    modelmap.put("password", "000000");
                    Map spotMap = httpUtil.getSystemName(modelmap);
                    Log.e("spotmap", String.valueOf(spotMap));
                    if (PublicMethod.checkIfNull(spotMap)) {
                        warn_info = "未获取到景区名称";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (spotMap.containsKey("errcode") && "0".equals(spotMap.get("errcode").toString())) {
                            Map map = (Map) spotMap.get("data");
                            Log.e("dsds", map.toString());
                            if (PublicMethod.checkIfNull(map)) {
                                warn_info = "未获取到景区名称";
                                handler.sendEmptyMessage(0);
                                return;
                            } else {
                                PublicMethod.SPOT_NAME = map.get("name") == null ? "" : String.valueOf(map.get("name"));
                                PublicMethod.SPOT_VIEW = map.get("view") == null ? "" : String.valueOf(map.get("view"));
                                if (map.containsKey("activities")) {
                                    PublicMethod.ACTIVITIES = map.get("activities") == null ? "" : String.valueOf(map.get("activities"));
                                    Log.e("dsjd111", PublicMethod.ACTIVITIES);
                                } else {
                                    PublicMethod.ACTIVITIES = "";
                                }
                                handler.sendEmptyMessage(1);
                            }
                        } else {
                            warn_info = spotMap.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    private static final float SCALE_END = 1.15F;
    private static final int ANIM_TIME = 2000;


    private void startMainActivity() {

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        startAnim();
                    }
                });
    }

    private void startAnim() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                et_password.setText("");
                et_username.setText("");
                LoginActivity.this.finish();
            }
        });
    }

    private void startMainActivityOne() {

        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {

                    @Override
                    public void call(Long aLong) {
                        startAnimOne();
                    }
                });
    }

    private void startAnimOne() {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(relativeLayout, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(relativeLayout, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIM_TIME).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                et_username.setText("");
                et_password.setText("");
                LoginActivity.this.finish();
            }
        });
    }

    /**
     * *******************************
     * 获取设置信息
     *
     * @Author lbr
     * create time 2018-10-18  9:21
     * *******************************
     */
    private void getSystemSetting() {
        if (PublicMethod.isNetworkAvailable(LoginActivity.this)) {
            warn_info = "请检查网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("code", PublicMethod.SERIZLNUMBER.trim());
                    Map settingmap = httpUtil.getPrinterSetting(modelmap);
                    if (PublicMethod.checkIfNull(settingmap)) {
                        warn_info = "暂无设置信息";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (settingmap.containsKey("errcode") && "0".equals(settingmap.get("errcode").toString())) {
                            if (settingmap.containsKey("data")) {
                                Map datamap = (Map) settingmap.get("data");
                                if (datamap != null) {
                                    PublicMethod.INFOID = datamap.get("id") == null ? "" : String.valueOf(datamap.get("id"));
                                    PublicMethod.SYSTEMSETTINGWEBSOCKETADDRESS = datamap.get("websocket") == null ? "" : String.valueOf(datamap.get("websocket"));
                                    PublicMethod.SYSTEMSETTINGMACHINECODE = datamap.get("code") == null ? "" : String.valueOf(datamap.get("code"));
                                    PublicMethod.SYSTEMSETTINGADDRESS = datamap.get("netarea") == null ? "" : String.valueOf(datamap.get("netarea"));
                                    PublicMethod.SYSTEMSETTINGPASSWORD = datamap.get("pas") == null ? "" : String.valueOf(datamap.get("pas"));
                                    //打印是否销票
                                    String destory_type = datamap.get("print_destory_type") == null ? "" : String.valueOf(datamap.get("print_destory_type"));
                                    if (TextUtils.isEmpty(PublicMethod.DESTOTYTICKET)) {
                                        if ("0".equals(destory_type)) {
                                            PublicMethod.DESTOTYTICKET = "0";
                                        } else {
                                            PublicMethod.DESTOTYTICKET = "1";
                                        }
                                    } else {
                                        if (PublicMethod.DESTOTYTICKET.equals(destory_type)) {
                                            if ("0".equals(destory_type)) {
                                                PublicMethod.DESTOTYTICKET = "0";
                                            } else {
                                                PublicMethod.DESTOTYTICKET = "1";
                                            }
                                        }
                                    }
                                    //是否启用打印机
                                    String isprinter = datamap.get("printer_open") == null ? "" : String.valueOf(datamap.get("printer_open"));
                                    if (TextUtils.isEmpty(PublicMethod.ISPRINTER)) {
                                        if ("0".equals(isprinter)) {
                                            PublicMethod.ISPRINTER = "0";
                                        } else {
                                            PublicMethod.ISPRINTER = "1";
                                        }
                                    } else {
                                        if (isprinter.equals(PublicMethod.ISPRINTER)) {
                                            if ("0".equals(isprinter)) {
                                                PublicMethod.ISPRINTER = "0";
                                            } else {
                                                PublicMethod.ISPRINTER = "1";
                                            }
                                        }
                                    }
                                    //是否启用刷居游卡
                                    String iccard_type = datamap.get("check_juyoucard_type") == null ? "" : String.valueOf(datamap.get("check_juyoucard_type"));
                                    if (TextUtils.isEmpty(PublicMethod.ISNEEDICCARD)) {
                                        if ("0".equals(iccard_type)) {
                                            PublicMethod.ISNEEDICCARD = "0";
                                        } else {
                                            PublicMethod.ISNEEDICCARD = "1";
                                        }
                                    } else {
                                        if (iccard_type.equals(PublicMethod.ISNEEDICCARD)) {
                                            if ("0".equals(iccard_type)) {
                                                PublicMethod.ISNEEDICCARD = "0";
                                            } else {
                                                PublicMethod.ISNEEDICCARD = "1";
                                            }
                                        }
                                    }
                                    //是否启用刷身份证
                                    String idcard_type = datamap.get("check_personcard_type") == null ? "" : String.valueOf(datamap.get("check_personcard_type"));
                                    if (TextUtils.isEmpty(PublicMethod.ISNEEDIDCARD)) {
                                        if ("0".equals(idcard_type)) {
                                            PublicMethod.ISNEEDIDCARD = "0";
                                        } else {
                                            PublicMethod.ISNEEDIDCARD = "1";
                                        }
                                    } else {
                                        if (idcard_type.equals(PublicMethod.ISNEEDIDCARD)) {
                                            if ("0".equals(idcard_type)) {
                                                PublicMethod.ISNEEDIDCARD = "0";
                                            } else {
                                                PublicMethod.ISNEEDIDCARD = "1";
                                            }
                                        }
                                    }
                                    //打印票类型
                                    String ticket_type = datamap.get("print_ticket_type") == null ? "" : String.valueOf(datamap.get("print_ticket_type"));
                                    if (TextUtils.isEmpty(PublicMethod.TICKETTYPE)) {
                                        if ("0".equals(ticket_type)) {
                                            PublicMethod.TICKETTYPE = "0";
                                        } else {
                                            PublicMethod.TICKETTYPE = "1";
                                        }
                                    } else {
                                        if (ticket_type.equals(PublicMethod.TICKETTYPE)) {
                                            if ("0".equals(ticket_type)) {
                                                PublicMethod.TICKETTYPE = "0";
                                            } else {
                                                PublicMethod.TICKETTYPE = "1";
                                            }
                                        }
                                    }
                                    //打印票码类型
                                    String code_type = datamap.get("ticket_code_type") == null ? "" : String.valueOf(datamap.get("ticket_code_type"));
                                    if (TextUtils.isEmpty(PublicMethod.CODETYPE)) {
                                        if ("0".equals(code_type)) {
                                            PublicMethod.CODETYPE = "0";
                                        } else {
                                            PublicMethod.CODETYPE = "1";
                                        }
                                    } else {
                                        if (code_type.equals(PublicMethod.CODETYPE)) {
                                            if ("0".equals(code_type)) {
                                                PublicMethod.CODETYPE = "0";
                                            } else {
                                                PublicMethod.CODETYPE = "1";
                                            }
                                        }
                                    }
                                } else {
                                    warn_info = "暂无设置信息";
                                    handler.sendEmptyMessage(0);
                                    return;
                                }
                            } else {
                                warn_info = "暂无设置信息";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        } else {
                            warn_info = settingmap.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 按钮点击事件
     *
     * @Author lbr
     * create time 2018-10-17  15:46
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //登录按钮
            case R.id.btn_login:
                String username = String.valueOf(et_username.getText());
                String password = String.valueOf(et_password.getText());
                if (TextUtils.isEmpty(username)) {
                    warn_info = "用户名不能为空";
                    handler.sendEmptyMessage(0);
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    warn_info = "密码不能为空";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    mDialog = DialogUtils.createLoadingDialog(LoginActivity.this, "登录中...");
                    Login(username, password);
                }
                break;
            //清空按钮
            case R.id.btn_clear:
                et_username.setText("");
                btn_clear_num.setVisibility(View.GONE);
                btn_get_num.setVisibility(View.VISIBLE);
                break;
            //获取设备码
            case R.id.get_num:
                try {
                    btn_get_num.setVisibility(View.GONE);
                    et_username.setText(PublicMethod.SERIZLNUMBER.trim());
                    btn_clear_num.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
                break;

        }

    }

    /**
     * *******************************
     * 登录方法
     *
     * @Author lbr
     * create time 2018-10-18  9:54
     * *******************************
     */
    private void Login(final String username, final String password) {
        if (PublicMethod.isNetworkAvailable(LoginActivity.this)) {
            warn_info = "请检查网络连接";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    if (PublicMethod.SERIZLNUMBER.equals(username)) {
                        modelmap.put("device", username);
                        modelmap.put("password", password);
                        Map loginmap = httpUtil.DeviceCodeLogin(modelmap);
                        if (PublicMethod.checkIfNull(loginmap)) {
                            warn_info = "暂无登录信息,请稍后重试";
                            handler.sendEmptyMessage(0);
                            return;
                        } else {
                            if (loginmap.containsKey("errcode") && "0".equals(loginmap.get("errcode").toString())) {
                                if (loginmap.containsKey("data")) {
                                    Map map = (Map) loginmap.get("data");
                                    PublicMethod.SPOT_NAME = map.get("name") == null ? "" : String.valueOf(map.get("name"));
                                    PublicMethod.SPOT_VIEW = map.get("view") == null ? "" : String.valueOf(map.get("view"));
                                    PublicMethod.login_name = username.trim();
                                    PublicMethod.machinecode = password.trim();
                                    PublicMethod.BACK_MARK = "0";
                                    if (checkBox_name.isChecked()) {
                                        //如果用户选择了记住用户名
                                        //将用户输入的用户名存入储存中，键为userName
                                        editor.putString("userName", username);
                                        editor.commit();
                                    } else {
                                        //否则将用户名清除
                                        editor.remove("userName");
                                        editor.commit();
                                    }
                                    if (checkBox_pwd.isChecked()) {
                                        //如果用户选择了记住密码
                                        //将用户输入的密码存入储存中，键为userName
                                        editor.putString("userPassword", password);
                                        editor.commit();
                                    } else {
                                        editor.remove("userPassword");
                                        editor.commit();
                                    }
                                    handler.sendEmptyMessage(2);
                                } else {
                                    if (loginmap.containsKey("errmsg")) {
                                        warn_info = loginmap.get("errmsg").toString();
                                    } else {
                                        warn_info = "登录请求失败1";
                                    }
                                    editor.remove("userName");
                                    editor.remove("userPassword");
                                    editor.commit();
                                    handler.sendEmptyMessage(0);
                                    return;
                                }
                            }
                        }
                    } else {
                        modelmap.put("username", username);
                        modelmap.put("password", password);
                        Map loginmap = httpUtil.login(modelmap);
                        if (PublicMethod.checkIfNull(loginmap)) {
                            warn_info = "暂无登录信息,请稍后重试";
                            handler.sendEmptyMessage(0);
                            return;
                        } else {
                            if (loginmap.containsKey("errcode") && "0".equals(loginmap.get("errcode").toString())) {
                                PublicMethod.LOGINCOOKIE = "juyou.session.id=" + loginmap.get("key").toString();
                                PublicMethod.COOKIE = loginmap.get("key") == null ? "" : String.valueOf(loginmap.get("key"));
                                PublicMethod.login_name = username.trim();
                                PublicMethod.machinecode = password.trim();
                                PublicMethod.BACK_MARK = "0";
                                if (checkBox_name.isChecked()) {
                                    //如果用户选择了记住用户名
                                    //将用户输入的用户名存入储存中，键为userName
                                    editor.putString("userName", username);
                                    editor.commit();
                                } else {
                                    //否则将用户名清除
                                    editor.remove("userName");
                                    editor.commit();
                                }
                                if (checkBox_pwd.isChecked()) {
                                    //如果用户选择了记住密码
                                    //将用户输入的密码存入储存中，键为userName
                                    editor.putString("userPassword", password);
                                    editor.commit();
                                } else {
                                    editor.remove("userPassword");
                                    editor.commit();
                                }
                                handler.sendEmptyMessage(3);
                            } else {
                                if (loginmap.containsKey("errmsg")) {
                                    warn_info = loginmap.get("errmsg").toString();
                                } else {
                                    warn_info = "登录请求失败2";
                                }
                                editor.remove("userName");
                                editor.remove("userPassword");
                                editor.commit();
                                handler.sendEmptyMessage(0);
                            }
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 获取营销数据根据权限 判断登录跳转
     *
     * @Author lbr
     * create time 2018-10-18  10:15
     * *******************************
     */
    private void getMarketInfo() {
        if (PublicMethod.isNetworkAvailable(LoginActivity.this)) {
            warn_info = "请检查网络连接";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Map markmap = httpUtil.getMarketInfo(modelmap);
                    if (PublicMethod.checkIfNull(markmap)) {
                        warn_info = "数据错误1";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (markmap.containsKey("errcode")) {
                            String errcode = markmap.get("errcode") == null ? "" : String.valueOf(markmap.get("errcode"));
                            if (TextUtils.isEmpty(errcode)) {
                                warn_info = "数据错误2";
                                handler.sendEmptyMessage(0);
                                return;
                            } else if ("0".equals(errcode)) {
                                PublicMethod.BACK_MARK = "1";
                                handler.sendEmptyMessage(4);
                            } else {
                                PublicMethod.BACK_MARK = "0";
                                handler.sendEmptyMessage(5);
                            }
                        } else {
                            if (markmap.containsKey("errmsg")) {
                                warn_info = markmap.get("errmsg").toString();
                            } else {
                                warn_info = "数据错误3";
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
     * 提示信息
     *
     * @Author lbr
     * create time 2018-10-17  16:37
     * *******************************
     */
    private void MakeToast(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * *******************************
     * 物理返回按键
     *
     * @Author lbr
     * create time 2018-10-18  9:41
     * *******************************
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            long now = System.currentTimeMillis();
            if (now - down < 2000) {
                ExitAppUtils.getInstance().exit();
                return true;
            } else {
                warn_info = "再按一次推出";
                handler.sendEmptyMessage(0);
                down = now;
                return false;
            }
        }
        return true;
    }
}
