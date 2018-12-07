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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.RealNameAdapter;
import com.sunmi.helper.bean.IDBean;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @Author lbr
 * 功能描述:招徕奖励游团
 * 创建时间: 2018-11-12 11:10
 */
public class GroupActivity extends Activity implements View.OnClickListener {
    //标题
    private TextView tv_title;
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //布局文件
    private LinearLayout linearLayout, linearLayout1;
    //搜索框
    private EditText et_info;
    //listview
    private ListView listView;
    //网络请求是否有实名制认证信息 0-有 1-无
    private String isn = "0";
    //实名制认证状态
    private String results = "0";
    private String isNeedRefresh = "0";
    //实名制认证列表信息
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    //筛选信息
    private List<Map<String, Object>> list_one = new ArrayList<Map<String, Object>>();
    //加载动画
    private Dialog mDialog;
    //实名制认证列表适配器
    private RealNameAdapter realNameAdapter;
    private IDBean idBean = new IDBean();
    //页面跳转之后传递过来的游团编号
    private String code;
    //实名制读卡
    private boolean run;
    private boolean canread;
    private ExecutorService singleThreadPool = null;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(GroupActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    if ("1".equals(isNeedRefresh)) {
                        isNeedRefresh = "0";
                        canread = true;
                        getgroupInfo("20000", "1");
                    }
                    if ("1".equals(isn)) {
                        isn = "0";
                        handler.sendEmptyMessage(1);
                    }
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout1.setVisibility(View.GONE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    setData();
                    linearLayout.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    Log.e("lisy", "" + list_one);
                    if (!list_one.isEmpty() && list_one.size() > 0) {
                        realNameAdapter.notifyDataSetChanged();
                    } else {
                        warn_info = "暂无查询结果";
                        handler.sendEmptyMessage(0);
                        et_info.setText("");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color3));
        ExitAppUtils.getInstance().addActivity(this);
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("code");
        linearLayout = findViewById(R.id.attr_linear1);
        linearLayout1 = findViewById(R.id.attr_linear2);
        et_info = findViewById(R.id.group_et_info);
        et_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if ("".equals(value.toString()) || "null".equals(value)) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    if (!list_one.isEmpty() || list_one.size() > 0) {
                        list_one.clear();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        list_one.add(list.get(i));
                    }
                    if (list.size() > 0 && !list.isEmpty()) {
                        realNameAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        listView = findViewById(R.id.attr_listview);
        mDialog = DialogUtils.createLoadingDialog(GroupActivity.this, "加载中...");
        getgroupInfo("20000", "1");
    }

    /**
     * 获取实名制列表
     *
     * @param pageSize
     * @param pageNo
     */
    private void getgroupInfo(final String pageSize, final String pageNo) {
        if (PublicMethod.isNetworkAvailable(GroupActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("device", PublicMethod.SERIZLNUMBER);
                    modelmap.put("code", code);
                    modelmap.put("pageNo", pageNo);
                    modelmap.put("pageSize", pageSize);
                    Map groupinfo = httpUtil.getListInfo(modelmap);
                    Log.e("groupinfo", "" + groupinfo);
                    if (PublicMethod.checkIfNull(groupinfo)) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (groupinfo.containsKey("errcode") && "0".equals(groupinfo.get("errcode").toString())) {
                            if (groupinfo.containsKey("data")) {
                                Map datamap = (Map) groupinfo.get("data");
                                if (PublicMethod.checkIfNull(datamap)) {
                                    warn_info = "暂无实名制认证信息";
                                    isn = "1";
                                    handler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    Object object = datamap.get("results");
                                    if (object == null) {
                                        warn_info = "暂无实名制认证信息01";
                                        isn = "1";
                                        handler.sendEmptyMessage(0);
                                        return;
                                    } else {
                                        if (object instanceof List) {
                                            if (!list.isEmpty() || list.size() > 0) {
                                                list.clear();
                                            }
                                            list = (List<Map<String, Object>>) object;
                                            handler.sendEmptyMessage(2);
                                        } else {
                                            warn_info = "暂无实名制认证信息02";
                                            isn = "1";
                                            handler.sendEmptyMessage(0);
                                            return;
                                        }
                                    }
                                }
                            } else {
                                isn = "1";
                                warn_info = "数据错误22";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        } else {
                            if (groupinfo.containsKey("errmsg")) {
                                warn_info = groupinfo.get("errmsg").toString();
                            } else {
                                warn_info = "数据错误33";
                            }
                            isn = "1";
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
        Log.e("djklasjda", "" + list);
        if (!list_one.isEmpty() || list_one.size() > 0) {
            list_one.clear();
        }
        for (int i = 0; i < list.size(); i++) {
            list_one.add(list.get(i));
        }
        realNameAdapter = new RealNameAdapter(GroupActivity.this, list_one);
        listView.setAdapter(realNameAdapter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.header_iv:
                startActivity(new Intent(GroupActivity.this, AttcrtActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //搜索按钮
            case R.id.group_check:
                if (TextUtils.isEmpty(et_info.getText().toString())) {
                    warn_info = "请输入有效信息进行查询";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    String Edit_value = et_info.getText().toString().trim();
                    Sline(Edit_value);
                }
                break;
        }
    }

    /**
     * list筛选
     *
     * @param values
     */
    private void Sline(String values) {
        if (!list_one.isEmpty() || list_one.size() > 0) {
            list_one.clear();
        }
        Log.e("values", values);
        Log.e("listone", "" + list_one);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof Map) {
                Map map = list.get(i);
                Log.e("mapmap", "" + map);
                if (map.get("name").toString().contains(values) || map.get("cardno").toString().contains(values)) {
                    list_one.add(map);
                }
            }
        }
        handler.sendEmptyMessage(3);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(GroupActivity.this, AttcrtActivity.class));
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
