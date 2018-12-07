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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.AttcrtAdapter;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author lbr
 * 功能描述:招徕奖励
 * 创建时间: 2018-11-8 14:26
 */
public class AttcrtActivity extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //游团listview
    private ListView listView;
    //游团适配器
    private AttcrtAdapter attcrtAdapter;
    //游团信息布局
    private LinearLayout linearLayout, linearLayout1;
    //游团信息list
    private List list = new ArrayList();
    //加载动画
    private Dialog mDialog;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(AttcrtActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    linearLayout.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    setData();
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout1.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attcrt);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(AttcrtActivity.this);
        linearLayout = findViewById(R.id.att_linear);
        linearLayout1 = findViewById(R.id.att_linear1);
        listView = findViewById(R.id.attract_listview);
        mDialog = DialogUtils.createLoadingDialog(AttcrtActivity.this, "加载中...");
        getGroupInfo(PublicMethod.SPOT_VIEW);
    }

    /**
     * 获取团信息
     *
     * @param spotView
     */
    private void getGroupInfo(final String spotView) {
        if (PublicMethod.isNetworkAvailable(AttcrtActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("view_code", spotView);
                    Map groupmap = httpUtil.getTourInfo(modelmap);
                    Log.e("groupmap", "" + groupmap);
                    if (PublicMethod.checkIfNull(groupmap)) {
                        handler.sendEmptyMessage(1);
                    } else {
                        if (groupmap.containsKey("errcode") && "0".equals(groupmap.get("errcode").toString())) {
                            if (groupmap.containsKey("data")) {
                                JSONArray array = (JSONArray) groupmap.get("data");
                                if (array.size() < 1) {
                                    handler.sendEmptyMessage(1);
                                } else {
                                    if (!list.isEmpty() || list.size() > 0) {
                                        list.clear();
                                    }
                                    for (int i = 0; i < array.size(); i++) {
                                        list.add(array.get(i));
                                    }
                                    handler.sendEmptyMessage(2);
                                }
                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        } else {
                            if (groupmap.containsKey("errmsg")) {
                                warn_info = groupmap.get("errmsg").toString();
                            } else {
                                warn_info = "错误信息11";
                            }
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
        attcrtAdapter = new AttcrtAdapter(AttcrtActivity.this, list);
        listView.setAdapter(attcrtAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = (Map) list.get(position);
                String code = map.get("code") == null ? "" : String.valueOf(map.get("code"));
                String groupname = map.get("order_name") == null ? "" : String.valueOf(map.get("order_name"));
                Intent intent = new Intent(AttcrtActivity.this, GroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("code", code);
                bundle.putString("groupname", groupname);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
            }
        });

    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-11-23  15:31
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.Ima_att:
                startActivity(new Intent(AttcrtActivity.this, AreaActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //刷新
            case R.id.btn_refresh:
                mDialog = DialogUtils.createLoadingDialog(AttcrtActivity.this, "刷新中...");
                getGroupInfo(PublicMethod.SPOT_VIEW);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(AttcrtActivity.this, AreaActivity.class));
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
