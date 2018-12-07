package com.sunmi.helper.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.R;
import com.sunmi.helper.adapter.DriverAdapter;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:自驾统计
 * 创建时间: 2018-10-24 15:14
 */
public class DriverFragment extends BaseFragment implements View.OnClickListener {

    // 标志fragment是否初始化完成
    private boolean isPrepared;
    private View view;
    //数据listview控件
    private ListView mListView;
    //数据list
    private List mList;
    private DriverAdapter driverAdapter;
    //有、无数据的布局
    private LinearLayout driver_linear_one;
    private LinearLayout linear_two;
    //提示信息
    private String warn_info;
    //listview item信息
    private List ItemInfo;
    //加载动画
    private Dialog mDialog;
    private Dialog dialog;

    private Button button;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    if (!(getActivity() == null)) {
                        Toast.makeText(getActivity(), warn_info, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    driver_linear_one.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    linear_two.setVisibility(View.VISIBLE);
                    setData();
                    break;
                case 3:
                    showItemDialog(ItemInfo);
                    dialogListener();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_driver, container, false);
            isPrepared = true;
            lazyLoad();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                mDialog = DialogUtils.createLoadingDialog(getActivity(), "打印中...");
                Print();
                break;
        }

    }

    /**
     * *******************************
     * 打印
     *
     * @Author lbr
     * create time 2018-10-24  15:36
     * *******************************
     */
    private void Print() {
        float size = Integer.parseInt("24");
        StringBuffer sb = new StringBuffer();
        sb.append("自驾电子票统计" + "\n");
        sb.append("*******************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        for (int i = 0; i < mList.size(); i++) {
            Map map = (Map) mList.get(i);
            StringBuffer sb1 = new StringBuffer();
            String name = map.get("type_name") == null ? "" : String.valueOf(map.get("type_name"));
            String count = map.get("count") == null ? "" : String.valueOf(map.get("count"));
            sb1.append("票种名称:" + name + "\n");
            sb1.append("销票人数:" + count + "人" + "\n");
            AidlUtil.getInstance().printText(sb1.toString(), size, false, false);
        }
        DialogUtils.closeDialog(mDialog);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initViews();
    }

    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-10-25  13:35
     * *******************************
     */
    private void initViews() {
        driver_linear_one = view.findViewById(R.id.driver_linear_one);
        linear_two = view.findViewById(R.id.linear_two);
        mListView = view.findViewById(R.id.driver_list);
        button = view.findViewById(R.id.btn_next);
        button.setOnClickListener(this);
        AidlUtil.getInstance().connectPrinterService(getActivity());
        mDialog = DialogUtils.createLoadingDialog(getActivity(), "加载中...");
        getticketInfo();
    }


    /**
     * 获取数据
     */
    private void getticketInfo() {
        if (PublicMethod.isNetworkAvailable(getActivity())) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("view", PublicMethod.SPOT_VIEW.trim());
                    Map map = httpUtil.drivertotalBytypeList(modelmap);
                    Log.e("drivermap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            if (map.containsKey("data")) {
                                Object object = map.get("data");
                                Log.e("dhhd", "" + object);
                                if (object != null) {
                                    if (object instanceof List) {
                                        mList = (List) object;
                                        if (mList.size() > 0) {
                                            handler.sendEmptyMessage(2);
                                        } else {
                                            handler.sendEmptyMessage(1);
                                            return;
                                        }
                                    } else {
                                        handler.sendEmptyMessage(1);
                                        return;
                                    }

                                } else {
                                    handler.sendEmptyMessage(1);
                                    return;
                                }
                            } else {
                                handler.sendEmptyMessage(1);
                                return;
                            }

                        } else {
                            if (map.containsKey("errmsg")) {
                                warn_info = map.get("errmsg").toString();
                            } else {
                                warn_info = "暂无详情";
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        DialogUtils.closeDialog(mDialog);
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    /**
     * 添加适配器
     */
    private void setData() {
        driverAdapter = new DriverAdapter(getActivity(), mList);
        mListView.setAdapter(driverAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mList.get(position) instanceof Map) {
                    Map map = (Map) mList.get(position);
                    String goods_code = map.get("goods_code") == null ? "" : String.valueOf(map.get("goods_code"));
                    String type = map.get("type") == null ? "" : String.valueOf(map.get("type"));
                    getTicketInfo(goods_code, type);
                }
            }
        });
    }

    /**
     * 自驾游票详情
     *
     * @param goods_code
     * @param type
     */
    private void getTicketInfo(final String goods_code, final String type) {
        if (PublicMethod.isNetworkAvailable(getActivity())) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("goods_code", goods_code);
                    modelmap.put("type", type);
                    Map map = httpUtil.driverItemInfo(modelmap);
                    Log.e("driveritemmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            if (map.containsKey("data")) {
                                Object object = map.get("data");
                                if (object == null) {
                                    warn_info = "暂无详情1";
                                    handler.sendEmptyMessage(0);
                                    return;
                                } else {
                                    if (object instanceof List) {
                                        if (ItemInfo != null) {
                                            ItemInfo.clear();
                                        }
                                        ItemInfo = (List) object;
                                        handler.sendEmptyMessage(3);
                                        return;
                                    } else {
                                        warn_info = "暂无详情2";
                                        handler.sendEmptyMessage(0);
                                        return;
                                    }

                                }
                            } else {
                                warn_info = "暂无详情3";
                                handler.sendEmptyMessage(0);
                                return;
                            }

                        } else {
                            if (map.containsKey("errmsg")) {
                                warn_info = map.get("errmsg").toString();
                            } else {
                                warn_info = "暂无详情4";
                            }
                            handler.sendEmptyMessage(0);

                        }
                    }
                }
            }.start();
        }
    }

    /**
     * Item 详情对话框dialog
     */
    private void showItemDialog(List list) {
        Log.e("lilihjs", "" + list);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.driver_item_info, null);
        TextView tv_one = view.findViewById(R.id.code_num);
        TextView tv_two = view.findViewById(R.id.tv_mobile);
        TextView tv_three = view.findViewById(R.id.used_time);
        Map map = null;
        if (list.get(0) instanceof Map) {
            map = (Map) list.get(0);
        }
        tv_one.setText(map.get("code") == null ? "" : String.valueOf(map.get("code")));
        tv_two.setText(map.get("mobile") == null ? "" : String.valueOf(map.get("mobile")));
        tv_three.setText(map.get("otime") == null ? "" : String.valueOf(map.get("otime")));
        dialog = new Dialog(DriverFragment.this.getActivity(), R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogwindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogwindow.getAttributes();
        lp.width = 600;
        lp.height = 420;
        dialogwindow.setAttributes(lp);
        dialogwindow.setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * dialog监听
     */
    private void dialogListener() {
        if (dialog != null) {
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            });
        }
    }

}
