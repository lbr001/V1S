package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.CheckGroupAdapter;
import com.sunmi.helper.adapter.TicketAdapter;
import com.sunmi.helper.bean.IDBean;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.nfcutil.NfcOperation;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;
import com.sunmi.helper.view.AlwaysMarqueeTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author lbr
 * 功能描述:门票核销
 * 创建时间: 2018-10-18 13:46
 */
public class TicketActivity extends Activity implements View.OnClickListener {
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //标题
    private AlwaysMarqueeTextView tv_titleName;
    //票详情对话框  票种名称   票总数量
    private TextView tv_name, tv_count;
    //销票详情对话框  销票数量
    private TextView tv_num;
    //输入的订单唯一码
    private EditText et_code;
    //提示信息
    private String warn_info;
    //设备核销门票数量
    private TextView tv_destory_count;
    //加载动画
    private Dialog mDialog;
    //票信息dialog
    private Dialog dialog;
    //核销门票数量
    private String des_count = "";
    //验票方式
    private String manystate;
    private String method;
    private String arg;
    //票码
    private String cardnum;
    //身份证信息
    private IDBean idBean = new IDBean();
    //验票Map
    private Map map;
    //验票数据
    private List ticketList = new ArrayList();
    //请求key;
    private String reqkey;
    //验票数据长度为1时的数据
    private Map ticketmap;
    //票种名称
    private String ticket_name;
    //预约信息
    private String ticket_appointment_time;
    //是否需要预约
    private String prompt_info;
    //票种数量
    private String ticket_count;
    //按钮销票方式
    private String pictureway;
    //选择销票的当前线程
    private String isread;
    //读卡线程
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    //读取IC卡util
    private NfcOperation nfcOperation;
    //读卡定时器
    private Timer timer;
    private TimerTask timerTask;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    Toast.makeText(TicketActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    mDialog = DialogUtils.createLoadingDialog(TicketActivity.this, "查询中...");
                    getTicketInfo();
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    showCodeDialog();
                    break;
                case 4:
                    if (dialog != null) {
                        dialog.cancel();
                        dialog = null;
                    }
                    DialogUtils.closeDialog(mDialog);
                    cardnum = "";
                    Toast.makeText(TicketActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    getCountInfo();
                    break;
                case 6:
                    if (dialog != null) {
                        dialog.cancel();
                        dialog = null;
                    }
                    DialogUtils.closeDialog(mDialog);
                    showGroupDialog();
                    break;
                case 7:
                    tv_destory_count.setText("当前设备总核销门票数量:" + des_count + "张");
                    break;
                case 8:
                    Toast.makeText(TicketActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    tv_destory_count.setText("当前设备总核销门票数量:0张");
                    break;
                case 9:
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    DialogUtils.closeDialog(mDialog);
                    nfcOperation.disEnableDispatch(TicketActivity.this);
                    mDialog = DialogUtils.createLoadingDialog(TicketActivity.this, "查询中");
                    getTicketInfo();
                    break;
                case 10:
                    DialogUtils.closeDialog(mDialog);
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    showTicketList();
                    break;
                case 11:
                    pictureway = "1";
                    showPictureDialog();
                    dialogListener();
                    startRead();
//                    String thisread1 = UUID.randomUUID().toString();
//                    isread = thisread1;
//                    getIcInfo(thisread1);
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what > 0) {
                Log.e("dsaaaa", String.valueOf(msg.what));
                if ("1".equals(pictureway) && dialog != null) {
                    String thisread1 = UUID.randomUUID().toString();
                    isread = thisread1;
                    getIcInfo(thisread1);
                }
            } else {
                if ("1".equals(pictureway)) {
                    warn_info = "请重新刷IC卡";
                    nfcOperation.disEnableDispatch(TicketActivity.this);
                } else {
                    warn_info = "请重新刷身份证";
                }
                handler.sendEmptyMessage(0);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        nfcOperation = new NfcOperation(TicketActivity.this);
        ExitAppUtils.getInstance().addActivity(TicketActivity.this);
        AidlUtil.getInstance().connectPrinterService(TicketActivity.this);
        tv_titleName = findViewById(R.id.tv_tilte);
        tv_titleName.setText(PublicMethod.SPOT_NAME + "验票核销");
        et_code = findViewById(R.id.et_num);
        tv_destory_count = findViewById(R.id.tv_destory_count);
        getCountInfo();
    }

    /**
     * 启动读卡定时器
     */
    private void startRead() {
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
        timer = new Timer();
        timerTask = new TimerTask();
        timer.schedule(timerTask, 0, 3000);
    }

    /**
     * 读卡定时器
     */
    class TimerTask extends java.util.TimerTask {
        int i = 6;

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = i--;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 获取设备核销总数量
     */
    private void getCountInfo() {
        if (PublicMethod.isNetworkAvailable(TicketActivity.this)) {
            warn_info = "请检查网络连接";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Map desmap = httpUtil.getDestoryCount(modelmap);
                    Log.e("desmap", "" + desmap);
                    if (PublicMethod.checkIfNull(desmap)) {
                        warn_info = "当前设备暂无销票记录";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (desmap.containsKey("errcode") && "0".equals(desmap.get("errcode").toString())) {
                            if (desmap.containsKey("data")) {
                                Map map = (Map) desmap.get("data");
                                if (map.containsKey("num")) {
                                    des_count = map.get("num") == null ? "" : String.valueOf(map.get("num"));
                                } else {
                                    des_count = "0";
                                }
                            } else {
                                des_count = "0";
                            }
                            handler.sendEmptyMessage(7);
                        } else {
                            if (desmap.containsKey("errmsg")) {
                                warn_info = desmap.get("errmsg").toString();
                            } else {
                                warn_info = "网络请求失败，请稍后重试";
                            }
                            handler.sendEmptyMessage(8);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 获取IC卡信息
     */
    private void getIcInfo(String thisread) {
        executorService.execute(new ICThread(thisread));
    }

    class ICThread implements Runnable {
        private String thisread = "";

        public ICThread() {
        }

        public ICThread(String thisread) {
            this.thisread = thisread;
        }

        @Override
        public void run() {
            if ("1".equals(pictureway) && dialog != null && thisread.equals(isread)) {
                nfcOperation.enableDispatch(TicketActivity.this);
            } else {
                return;
            }
        }
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-23  15:33
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.header_iv:
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(TicketActivity.this, MainActivity.class));

                } else {
                    startActivity(new Intent(TicketActivity.this, AdminActivity.class));

                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //验票
            case R.id.check:
                String code_num = et_code.getText().toString();
                if (TextUtils.isEmpty(code_num)) {
                    warn_info = "请输入订单唯一码";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    checkByInput(code_num);
                }
                break;
            //减少销票数量按钮
            case R.id.ticket_num_jian:
                //销票数量减少
                String ticketNum = tv_num.getText().toString();
                if ("0".equals(ticketNum)) {
                    Toast.makeText(this, "可销票数量达到下限", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int count = Integer.parseInt(ticketNum);
                    count -= 1;
                    tv_num.setText(String.valueOf(count));
                }
                break;
            //增加销票数量按钮
            case R.id.ticket_count_jia:
                String ticketNum1 = tv_num.getText().toString().trim();
                if (ticketNum1.equals(ticket_count)) {
                    Toast.makeText(this, "可销票数量达到上限", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    int counts = Integer.parseInt(ticketNum1);
                    counts += 1;
                    tv_num.setText(String.valueOf(counts));
                }
                break;
            //销票对话框取消按钮
            case R.id.btn_canle:
                if (dialog != null) {
                    dialog.cancel();
                }
                break;
            //销票对话框确定按钮
            case R.id.btn_sure:
                if ("0".equals(tv_num.getText().toString().trim())) {
                    Toast.makeText(this, "请输入销票数量", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    DialogUtils.closeDialog(mDialog);
                    mDialog = DialogUtils.createLoadingDialog(TicketActivity.this, "销票中");
                    DestotyTicket(tv_num.getText().toString().trim());
                }
                break;
            //扫码验票
            case R.id.bar_check:
                Intent intent = new Intent("com.summi.scan");
                intent.setClassName("com.sunmi.sunmiqrcodescanner", "com.sunmi.sunmiqrcodescanner.activity.ScanActivity");
                intent.putExtra("CURRENT_PPI", 0x0003);
                intent.putExtra("PLAY_SOUND", true);
                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    Log.e("异常进入:", "888888");
                    Toast.makeText(TicketActivity.this, getResources().getString(R.string.identify_fail), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next:
                startActivity(new Intent(TicketActivity.this, HistoryOfCancleActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //刷IC卡验票
            case R.id.ic_check:
                if (dialog != null) {
                    dialog.cancel();
                    dialog = null;
                }
                if ("1".equals(PublicMethod.ISNEEDICCARD)) {
                    warn_info = "请先进入设置界面,启用刷IC卡功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    handler.sendEmptyMessage(11);
                }
                break;
        }
    }

    /**
     * *******************************
     * 销票
     *
     * @Author lbr
     * create time 2018-10-23  16:07
     * *******************************
     */
    private void DestotyTicket(final String destory_num) {
        if (PublicMethod.isNetworkAvailable(TicketActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    String way = "";
                    String arg = "";
                    switch (cardnum.length()) {
                        case 7:
                            way = "updateByGroupCode";
                            arg = "code";
                            break;
                        case 8:
                            way = "updateByCode";
                            arg = "code";
                            break;
                        case 10:
                            way = "updateByCode";
                            arg = "code";
                            break;
                        case 16:
                            way = "updateByCard";
                            arg = "card";
                            break;
                        case 18:
                            way = "updateByID";
                            arg = "ID";
                            break;
                        default:
                            break;
                    }
                    if ("byCard".equals(method) || "byID".equals(method)) {
                        modelmap.put("type", ticketmap.get("type") == null ? "" : String.valueOf(ticketmap.get("type")));
                        modelmap.put("type_attr", ticketmap.get("type_attr") == null ? "" : String.valueOf(ticketmap.get("type_attr")));
                        modelmap.put("goods_code", ticketmap.get("goods_code") == null ? "" : String.valueOf(ticketmap.get("goods_code")));
                    }
                    modelmap.put("way", way);
                    modelmap.put("arg", arg);
                    modelmap.put("reqkey", reqkey);
                    modelmap.put("trim", cardnum);
                    modelmap.put("num", destory_num);
                    modelmap.put("method", method);
                    Map destorymap = httpUtil.VenCheckTicket(modelmap);
                    Log.e("desmap", "" + destorymap);
                    if (PublicMethod.checkIfNull(destorymap)) {
                        warn_info = "暂无销票详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (destorymap.containsKey("errcode") && "0".equals(destorymap.get("errcode").toString())) {
                            warn_info = "销票成功";
                            float size = Integer.parseInt("24");
                            AidlUtil.getInstance().printText(String.valueOf(destorymap.get("data")).replaceAll("##", "\n"), size, false, false);
                            handler.sendEmptyMessage(4);
                        } else {
                            warn_info = destorymap.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 验票
     *
     * @Author lbr
     * create time 2018-10-23  15:42
     * *******************************
     */
    private void getTicketInfo() {
        if (PublicMethod.isNetworkAvailable(TicketActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Log.e("cardnum", cardnum);
                    Map modelmap = new HashMap();
                    if (method.equals("byID")) {
                        if (TextUtils.isEmpty(idBean.getName())) {
                            modelmap.put("name", "");
                            modelmap.put("address", "");
                        } else {
                            modelmap.put("name", idBean.getName());
                            modelmap.put("address", idBean.getAddress());
                        }
                    }
                    modelmap.put("way", method);
                    modelmap.put("device", PublicMethod.SERIZLNUMBER);
                    modelmap.put("code", cardnum);
                    modelmap.put("arg", arg);
                    map = httpUtil.TicketInfo(modelmap);
                    Log.e("mapmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无信息";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            if (map.containsKey("data")) {
                                Object object = map.get("data");
                                if (!(object instanceof Map)) {
                                    warn_info = "数据错误2";
                                    handler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    Map data = (Map) object;
                                    if (data.containsKey("reqkey")) {
                                        reqkey = data.get("reqkey") == null ? "" : String.valueOf(data.get("reqkey"));
                                        Log.e("reqkey", reqkey);
                                    }
                                    if (data.containsKey("manystate")) {
                                        manystate = data.get("manystate") == null ? "" : String.valueOf(data.get("manystate"));
                                    }
                                    if (method.equals("byCard")) {
                                        if (object == null || !(object instanceof Map)) {
                                            warn_info = "数据错误1";
                                            handler.sendEmptyMessage(0);
                                            return;
                                        } else {
                                            Map data1 = (Map) object;
                                            if (data1.containsKey("ticketList")) {
                                                Object object1 = data1.get("ticketList");
                                                if (!(object1 instanceof List) || object1 == null) {
                                                    warn_info = "数据错误3";
                                                    handler.sendEmptyMessage(0);
                                                    return;
                                                } else {
                                                    if (ticketList != null) {
                                                        ticketList.clear();
                                                    }
                                                    ticketList = (List) object1;
                                                    if (ticketList.size() < 1) {
                                                        warn_info = "此用户无票可销";
                                                        handler.sendEmptyMessage(0);
                                                        return;
                                                    } else {
                                                        if (ticketList.size() == 1) {
                                                            if (!PublicMethod.checkIfNull(ticketmap)) {
                                                                ticketmap = null;
                                                            }
                                                            ticketmap = (Map) ticketList.get(0);
                                                            ticket_name = ticketmap.get("type_name") == null ? "" : String.valueOf(ticketmap.get("type_name"));
                                                            ticket_count = ticketmap.get("count") == null ? "" : String.valueOf(ticketmap.get("count"));
                                                            if (ticketmap.containsKey("appoint")) {
                                                                prompt_info = ticketmap.get("appoint") == null ? "" : String.valueOf(ticketmap.get("appoint"));
                                                                if ("1".equals(prompt_info)) {
                                                                    if (ticketmap.containsKey("appoint_infos")) {
                                                                        ticket_appointment_time = ticketmap.get("appoint_infos") == null ? "" : String.valueOf(ticketmap.get("appoint_infos"));
                                                                    } else {
                                                                        ticket_appointment_time = "";
                                                                    }
                                                                }
                                                            } else {
                                                                prompt_info = "";
                                                            }
                                                            if (TextUtils.isEmpty(ticket_count)) {
                                                                warn_info = "此用户无票可销";
                                                                handler.sendEmptyMessage(0);
                                                                return;
                                                            } else {
                                                                if (Integer.parseInt(ticket_count) == 0) {
                                                                    warn_info = "此用户无票可销";
                                                                    handler.sendEmptyMessage(0);
                                                                    return;
                                                                } else {
                                                                    handler.sendEmptyMessage(2);
                                                                }
                                                            }
                                                        } else {
                                                            handler.sendEmptyMessage(10);
                                                        }
                                                    }
                                                }
                                            } else {
                                                warn_info = "数据错误2";
                                                handler.sendEmptyMessage(0);
                                                return;
                                            }
                                        }
                                    } else if (method.equals("byGroupCode")) {
                                        if (PublicMethod.checkIfNull(data)) {
                                            warn_info = "暂无团信息";
                                            handler.sendEmptyMessage(0);
                                            return;
                                        } else {
                                            if (data.containsKey("ticketInfo")) {
                                                Object mobject = data.get("ticketInfo");
                                                if (mobject == null || !(mobject instanceof Map)) {
                                                    warn_info = "暂无团信息3";
                                                    handler.sendEmptyMessage(0);
                                                    return;
                                                } else {
                                                    if (!ticketList.isEmpty()) {
                                                        ticketList.clear();
                                                    }
                                                    Map map = (Map) mobject;
                                                    ticketList.add(map);
                                                    if (ticketList.size() < 1) {
                                                        warn_info = "此团无票可销";
                                                        handler.sendEmptyMessage(0);
                                                        return;
                                                    } else {
                                                        handler.sendEmptyMessage(6);
                                                    }
                                                }

                                            } else {
                                                warn_info = "暂无团信息1";
                                                handler.sendEmptyMessage(0);
                                                return;
                                            }
                                        }
                                    } else if (method.equals("byCode")) {
                                        if (data.containsKey("prompt_info")) {
                                            ticket_appointment_time = data.get("prompt_info") == null ? "" : String.valueOf(data.get("prompt_info"));
                                        } else {
                                            ticket_appointment_time = "";
                                        }
                                        if (data.containsKey("appoint")) {
                                            prompt_info = data.get("appoint") == null ? "" : String.valueOf(data.get("appoint"));
                                        } else {
                                            prompt_info = "";
                                        }
                                        Object object1 = data.get("ticketInfo");
                                        if (object1 instanceof Map) {
                                            Map ticketInfo = (Map) object1;
                                            if (ticketInfo.containsKey("count")) {
                                                ticket_count = ticketInfo.get("count") == null ? "" : String.valueOf(ticketInfo.get("count"));
                                            }
                                            if (ticketInfo.containsKey("type_name")) {
                                                ticket_name = ticketInfo.get("type_name") == null ? "" : String.valueOf(ticketInfo.get("type_name"));
                                            }
                                        }
                                        handler.sendEmptyMessage(2);
                                    } else if (method.equals("byID")) {
                                        Object object1 = data.get("ticketList");
                                        if (object1 instanceof List) {
                                            if (ticketList != null) {
                                                ticketList.clear();
                                            }
                                            ticketList = (List) object1;
                                            if (ticketList != null && ticketList.size() > 0) {
                                                if (ticketList.size() > 1) {
                                                    handler.sendEmptyMessage(10);
                                                }
                                                if (ticketList.size() == 1) {
                                                    if (!PublicMethod.checkIfNull(ticketmap)) {
                                                        ticketmap = null;
                                                    }
                                                    ticketmap = (Map) ticketList.get(0);
                                                    ticket_name = ticketmap.get("type_name") == null ? "" : String.valueOf(ticketmap.get("type_name"));
                                                    ticket_count = ticketmap.get("count") == null ? "" : String.valueOf(ticketmap.get("count"));
                                                    if (ticketmap.containsKey("appoint")) {
                                                        prompt_info = ticketmap.get("appoint") == null ? "" : String.valueOf(ticketmap.get("appoint"));
                                                        if ("1".equals(prompt_info)) {
                                                            if (ticketmap.containsKey("appoint_infos")) {
                                                                ticket_appointment_time = ticketmap.get("appoint_infos") == null ? "" : String.valueOf(ticketmap.get("appoint_infos"));
                                                            } else {
                                                                ticket_appointment_time = "";
                                                            }
                                                        }
                                                    } else {
                                                        prompt_info = "";
                                                    }
                                                    if (TextUtils.isEmpty(ticket_count)) {
                                                        warn_info = "此用户无票可销";
                                                        handler.sendEmptyMessage(0);
                                                        return;
                                                    } else {
                                                        if (Integer.parseInt(ticket_count) == 0) {
                                                            warn_info = "此用户无票可销";
                                                            handler.sendEmptyMessage(0);
                                                            return;
                                                        } else {
                                                            handler.sendEmptyMessage(2);
                                                        }
                                                    }
                                                }
                                            } else {
                                                warn_info = "用户无票可销";
                                                handler.sendEmptyMessage(0);
                                            }
                                        }
                                    }
                                }
                            } else {
                                warn_info = "数据错误1";
                                handler.sendEmptyMessage(0);
                            }
                        } else {
                            if (map.containsKey("errmsg")) {
                                warn_info = map.get("errmsg").toString();
                            } else {
                                warn_info = "数据错误";
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * dialog消失事件
     */
    private void dialogListener() {
        if (dialog != null) {
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (timerTask != null) {
                        timerTask.cancel();
                    }
                    if ("0".equals(pictureway)) {
                        Log.e("在刷身份证的时候", "我消失了");
                        pictureway = "2";
                    } else if ("1".equals(pictureway)) {
                        Log.e("在刷IC卡的时候", "我消失了");
                        pictureway = "2";
                    }
                }
            });
        }
    }

    /**
     * 显示提示图片
     */
    private void showPictureDialog() {
        View view = LayoutInflater.from(TicketActivity.this).inflate(R.layout.pic_dialog, null);
        ImageView imageView = view.findViewById(R.id.image_ic);
        ImageView imageView1 = view.findViewById(R.id.image_id);
        if ("0".equals(pictureway)) {
            imageView.setVisibility(View.GONE);
            imageView1.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView1.setVisibility(View.GONE);
        }
        dialog = new Dialog(TicketActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogwindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = 560;
        lp.height = 560;
        dialogwindow.setAttributes(lp);
        dialogwindow.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * *******************************
     * 显示票详情对话框
     *
     * @Author lbr
     * create time 2018-10-23  15:53
     * *******************************
     */
    private void showCodeDialog() {
        View view = LayoutInflater.from(TicketActivity.this).inflate(R.layout.ticket_dialog, null);
        tv_name = view.findViewById(R.id.ticket_name_one);
        TextView tv_appoint = view.findViewById(R.id.tv_appoint);
        TextView tv_prompt_info = view.findViewById(R.id.tv_prompt_info);
        TextView tv_promt_info_two = view.findViewById(R.id.pro_info_two);
        LinearLayout linearLayout = view.findViewById(R.id.info_linear);
        if (ticket_name != null) {
            tv_name.setText(ticket_name);
        }
        if ("0".equals(prompt_info)) {
            tv_appoint.setText("是否需要预约:不需要");
            linearLayout.setVisibility(View.GONE);
        } else if ("1".equals(prompt_info)) {
            tv_appoint.setText("是否需要预约:需要");
            tv_prompt_info.setText("预约信息:");
            tv_promt_info_two.setText(ticket_appointment_time);
            linearLayout.setVisibility(View.VISIBLE);
        }
        tv_count = view.findViewById(R.id.ticket_count);
        if (ticket_count != null) {
            tv_count.setText("共" + ticket_count.trim() + "张");
        }
        tv_num = view.findViewById(R.id.ticket_destory_count);
        dialog = new Dialog(TicketActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogwindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = 600;
        lp.height = 600;
        dialogwindow.setAttributes(lp);
        dialogwindow.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * *******************************
     * 通过用户输入验票
     *
     * @Author lbr
     * create time 2018-10-23  15:36
     * *******************************
     */
    private void checkByInput(String codenum) {
        if (method != null) {
            method = null;
        }
        if (arg != null) {
            arg = null;
        }
        if (cardnum != null) {
            cardnum = null;
        }
        if (codenum.length() == 18) {
            et_code.setText("");
            warn_info = "不支持身份证号码验票,如需身份证验票请刷身份证";
            handler.sendEmptyMessage(0);
            return;
        } else if (codenum.length() == 7) {
            //团票码
            method = "byGroupCode";
            arg = "code";
        } else if (codenum.length() == 8 || codenum.length() == 10) {
            //票码
            method = "byCode";
            arg = "code";
        } else if (codenum.length() == 16) {
            //IC卡卡内号
            method = "byCode";
            arg = "code";
        } else {
            et_code.setText("");
            warn_info = "输入号码不正确";
            handler.sendEmptyMessage(0);
            return;
        }
        cardnum = codenum.trim();
        Log.e("cardnum", cardnum.trim());
        et_code.setText("");
        handler.sendEmptyMessage(1);
    }

    /**
     * 显示游团列表
     */
    private void showGroupDialog() {
        View view = LayoutInflater.from(TicketActivity.this).inflate(R.layout.group_choose, null);
        ListView listView = view.findViewById(R.id.group_list);
        CheckGroupAdapter checkGroupAdapter = new CheckGroupAdapter(TicketActivity.this, ticketList);
        listView.setAdapter(checkGroupAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ticketList.get(position) instanceof Map) {
                    if (!PublicMethod.checkIfNull(ticketmap)) {
                        ticketmap.clear();
                    }
                    ticketmap = (Map) ticketList.get(position);
                    ticket_name = ticketmap.get("type_name") == null ? "" : String.valueOf(ticketmap.get("type_name"));
                    ticket_count = ticketmap.get("count") == null ? "" : String.valueOf(ticketmap.get("count"));
                    if (Integer.parseInt(ticket_count) > 0) {
                        handler.sendEmptyMessage(2);
                    } else {
                        warn_info = "当前票种无票可销";
                        handler.sendEmptyMessage(0);
                        return;
                    }
                }
            }
        });
        dialog = new Dialog(TicketActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogwindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = 600;
        lp.height = 600;
        dialogwindow.setAttributes(lp);
        dialogwindow.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 显示票订单列表
     */
    private void showTicketList() {
        View view = LayoutInflater.from(TicketActivity.this).inflate(R.layout.ticket_choose, null);
        ListView listView = view.findViewById(R.id.ticket_list);
        TicketAdapter ticketAdapter = new TicketAdapter(TicketActivity.this, ticketList);
        listView.setAdapter(ticketAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ticketList.get(position) instanceof Map) {
                    if (!PublicMethod.checkIfNull(ticketmap)) {
                        ticketmap.clear();
                    }
                    ticketmap = (Map) ticketList.get(position);
                    ticket_name = ticketmap.get("type_name") == null ? "" : String.valueOf(ticketmap.get("type_name"));
                    ticket_count = ticketmap.get("count") == null ? "" : String.valueOf(ticketmap.get("count"));
                    if (ticketmap.containsKey("appoint")) {
                        prompt_info = ticketmap.get("appoint") == null ? "" : String.valueOf(ticketmap.get("appoint"));
                        if ("1".equals(prompt_info)) {
                            if (ticketmap.containsKey("appoint_infos")) {
                                ticket_appointment_time = ticketmap.get("appoint_infos") == null ? "" : String.valueOf(ticketmap.get("appoint_infos"));
                            } else {
                                ticket_appointment_time = "";
                            }
                        }
                    } else {
                        prompt_info = "";
                    }
                    if (Integer.parseInt(ticket_count) > 0) {
                        handler.sendEmptyMessage(2);
                    } else {
                        warn_info = "当前票种无票可销";
                        handler.sendEmptyMessage(0);
                        return;
                    }
                }
            }
        });
        dialog = new Dialog(TicketActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogwindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = 600;
        lp.height = 600;
        dialogwindow.setAttributes(lp);
        dialogwindow.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * 扫码事件回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null) {
            Bundle bundle = data.getExtras();
            ArrayList<HashMap<String, String>> results = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
            Iterator<HashMap<String, String>> it = results.iterator();
            while (it.hasNext()) {
                HashMap<String, String> hashMap = it.next();
                Log.e("扫码类型:", hashMap.get("TYPE"));
                Log.e("扫码结果:", hashMap.get("VALUE"));
                cardnum = hashMap.get("VALUE");
            }
            if (!TextUtils.isEmpty(cardnum)) {
                switch (cardnum.length()) {
                    case 7:
                        method = "byGroupCode";
                        arg = "code";
                        handler.sendEmptyMessage(1);
                        break;
                    case 8:
                        method = "byCode";
                        arg = "code";
                        handler.sendEmptyMessage(1);
                        break;
                    case 10:
                        method = "byCode";
                        arg = "code";
                        handler.sendEmptyMessage(1);
                        break;
                    case 16:
                        method = "byCard";
                        arg = "card";
                        handler.sendEmptyMessage(1);
                        break;
                    case 18:
                        method = "byID";
                        arg = "ID";
                        handler.sendEmptyMessage(1);
                        break;
                    default:
                        Toast.makeText(this, "二维码格式不正确", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(this, "无法获取二维码", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        mHandler.removeCallbacksAndMessages(null);
        DialogUtils.closeDialog(mDialog);
        if (dialog != null) {
            dialog.cancel();
        }
        System.gc();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String ic_num = nfcOperation.readIcCardByBlock(intent);
        if (!TextUtils.isEmpty(ic_num)) {
            cardnum = ic_num;
            method = "byCard";
            arg = "card";
            handler.sendEmptyMessage(9);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ("0".equals(PublicMethod.BACK_MARK)) {
                startActivity(new Intent(TicketActivity.this, MainActivity.class));

            } else {
                startActivity(new Intent(TicketActivity.this, AdminActivity.class));

            }
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcOperation.disEnableDispatch(TicketActivity.this);
    }
}
