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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.SubAdapter;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:补贴订单列表
 * 创建时间: 2018-11-20 14:42
 */
public class SubListInfo extends Activity implements View.OnClickListener {
    //提示信息
    private String warn_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //补贴详情listview
    private ListView listView;
    //补贴详情list
    private List list = new ArrayList();
    //加载动画
    private Dialog mDialog;
    //适配器
    private SubAdapter subAdapter;
    //无补贴数据布局
    private LinearLayout linearLayout;
    private String isn = "0";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(SubListInfo.this, warn_info, Toast.LENGTH_SHORT).show();
                    if ("1".equals(isn)) {
                        isn = "0";
                        handler.sendEmptyMessage(1);
                    }
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    linearLayout.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    setData();
                    linearLayout.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subinfo);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(SubListInfo.this);
        //初始化打印机(连接)
        AidlUtil.getInstance().connectPrinterService(SubListInfo.this);
        initView();
        getData("加载中");
    }

    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-11-20  15:11
     * *******************************
     */
    private void initView() {
        linearLayout = findViewById(R.id.sub_item_linearlayout);
        listView = findViewById(R.id.subinfo_listview);
    }

    /**
     * *******************************
     * 获取补贴数据
     *
     * @Author lbr
     * create time 2018-11-20  15:13
     * *******************************
     */
    private void getData(String message) {
        mDialog = DialogUtils.createLoadingDialog(SubListInfo.this, message);
        getSubInfo();
    }

    /**
     * *******************************
     * 补贴网络请求
     *
     * @Author lbr
     * create time 2018-11-20  15:14
     * *******************************
     */
    private void getSubInfo() {
        if (PublicMethod.isNetworkAvailable(SubListInfo.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("view", PublicMethod.SPOT_VIEW.trim());
                    Map submap = httpUtil.getSaleInfo(modelmap);
                    Log.e("submap", "" + submap);
                    if (PublicMethod.checkIfNull(submap)) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (submap.containsKey("errcode") && "0".equals(submap.get("errcode").toString())) {
                            if (submap.containsKey("data")) {
                                Object obj = submap.get("data");
                                Log.e("dakld", String.valueOf(obj));
                                if ("{}".equals(String.valueOf(obj))) {
                                    Log.e("null", "null");
                                    isn = "1";
                                    warn_info = "暂无数据1";
                                    handler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    if (obj instanceof List) {
                                        if (!list.isEmpty() || list.size() > 0) {
                                            list.clear();
                                        }
                                        list = (List) obj;
                                        if (list.isEmpty()) {
                                            isn = "1";
                                            warn_info = "暂无数据1";
                                            handler.sendEmptyMessage(0);
                                            return;
                                        } else {
                                            handler.sendEmptyMessage(2);
                                        }
                                    }
                                }

                            } else {
                                isn = "1";
                                warn_info = "数据错误3";
                                handler.sendEmptyMessage(0);
                                return;
                            }

                        } else {
                            if (submap.containsKey("errmsg")) {
                                warn_info = submap.get("errmsg").toString();
                            } else {
                                warn_info = "数据错误4";
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
        Log.e("Dsdsds", list.toString());
        subAdapter = new SubAdapter(SubListInfo.this, list);
        listView.setAdapter(subAdapter);
    }

    /**
     * *******************************
     * 按钮点击事件
     *
     * @Author lbr
     * create time 2018-11-21  9:48
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.Ima_sub:
                startActivity(new Intent(SubListInfo.this, SubsidyActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //刷新
            case R.id.btn_sub_clear:
                getData("刷新中...");
                break;
        }
    }

    /**
     * *******************************
     * 重新打印领取信息
     *
     * @Author lbr
     * create time 2018-11-21  9:57
     * *******************************
     */
    public void reprinter(Map map) {
        float size = Integer.parseInt("24");
        String name = map.get("name") == null ? "" : String.valueOf(map.get("name"));
        String otime = map.get("otime") == null ? "" : String.valueOf(map.get("otime"));
        String idcard = map.get("idcard") == null ? "" : String.valueOf(map.get("idcard"));
        StringBuffer sb = new StringBuffer();
        sb.append("领取补贴凭证(重打)" + "\n");
        sb.append("*******************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        StringBuffer sb1 = new StringBuffer();
        sb1.append("景区编号:" + PublicMethod.SPOT_VIEW + "\n");
        sb1.append("顾客姓名:" + name + "\n");
        sb1.append("证件号码:" + idcard + "\n");
        sb1.append("领取状态:已领取" + "\n");
        sb1.append("领取时间:" + otime + "\n");
        AidlUtil.getInstance().printText(sb1.toString(), size, false, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(SubListInfo.this, SubsidyActivity.class));
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
