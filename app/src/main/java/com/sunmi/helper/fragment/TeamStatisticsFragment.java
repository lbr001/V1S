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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.R;
import com.sunmi.helper.adapter.GroupAdapterOne;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:团队统计 Team statistics
 * 创建时间: 2018-10-24 15:40
 */
public class TeamStatisticsFragment extends BaseFragment implements View.OnClickListener {

    // 标志fragment是否初始化完成
    private boolean isPrepared;
    private View view;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //提示信息
    private String warn_info;
    //网络请求查询数据有、无的布局
    private LinearLayout linearLayout1, linearLayout2;
    //数据list
    private ListView listView;
    //接收查询信息(有数据)
    private List list;
    private List listInfo = new ArrayList();
    //    private ProgressDialog progressDialog;
    private Dialog mDialog;
    private GroupAdapterOne groupAdapterOne;
    private Dialog dialog;
    private Button button;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(getActivity(), warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.GONE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    linearLayout2.setVisibility(View.VISIBLE);
                    setData();
                    break;
                case 5:
                    showDialog(listInfo);
                    dialogListener();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_group, container, false);
            isPrepared = true;
            lazyLoad();
        }
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initViews();

    }

    private void initViews() {
        linearLayout1 = view.findViewById(R.id.tj_linear);
        linearLayout2 = view.findViewById(R.id.group_tj_linear);
        listView = view.findViewById(R.id.group_tj_list);
        button = view.findViewById(R.id.btn_next);
        button.setOnClickListener(this);
        AidlUtil.getInstance().connectPrinterService(getActivity());
        mDialog = DialogUtils.createLoadingDialog(getActivity(), "加载中...");
        getInfo();
    }

    /**
     * 获取团队统计数据
     */
    private void getInfo() {
        if (PublicMethod.isNetworkAvailable(getActivity())) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("view_code", PublicMethod.SPOT_VIEW.trim());
                    Map map = httpUtil.grouptotalBytypeList(modelmap);
                    Log.e("mapmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            if (map.containsKey("data")) {
                                Object object = map.get("data");
                                if (object != null) {
                                    if (object instanceof List) {
                                        list = (List) object;
                                        Log.e("listlist", "" + list);
                                        if (list.size() < 1) {
                                            handler.sendEmptyMessage(1);
                                            return;
                                        } else {
                                            handler.sendEmptyMessage(2);
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
                            warn_info = map.get("errmsg").toString();
                            handler.sendEmptyMessage(0);
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 添加适配器Adapter listview item 点击事件
     */
    private void setData() {
        groupAdapterOne = new GroupAdapterOne(getActivity(), list);
        listView.setAdapter(groupAdapterOne);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = (Map) list.get(position);
                String goods_code = map.get("goods_code") == null ? "" : String.valueOf(map.get("goods_code"));
                Log.e("djskd", goods_code);
                String ticket_type = map.get("ticket_type") == null ? "" : String.valueOf(map.get("ticket_type"));
                getTicketInfo(goods_code, ticket_type);
            }
        });
    }

    /**
     * 获取item详情
     *
     * @param goods_code
     * @param ticket_type
     */
    private void getTicketInfo(final String goods_code, final String ticket_type) {
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
                    modelmap.put("ticket_type", ticket_type);
                    Map map = httpUtil.groupListInfo(modelmap);
                    Log.e("groupmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无订单详情";
                        handler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            Object object = map.get("data");
                            Log.e("onject", "" + object);
                            if (object != null) {
                                if (object instanceof List) {
                                    if (listInfo != null) {
                                        listInfo.clear();
                                    }
                                    listInfo = (List) object;
                                    handler.sendEmptyMessage(5);
                                } else {
                                    warn_info = "暂无订单详情";
                                    handler.sendEmptyMessage(0);
                                    return;
                                }
                            } else {
                                warn_info = "暂无订单详情";
                                handler.sendEmptyMessage(0);
                                return;
                            }
                        }
                    }
                }
            }.start();
        }
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

    /**
     * 显示票详情对话框
     *
     * @param infoList
     */

    private void showDialog(List infoList) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.groupfragment_item, null);
        TextView tv_one = view.findViewById(R.id.ticket_name);
        TextView tv_two = view.findViewById(R.id.yd_man);
        TextView tv_three = view.findViewById(R.id.sd_man);
        TextView tv_four = view.findViewById(R.id.tv_guide);
        TextView tv_five = view.findViewById(R.id.last_modify_time);
        Map map = null;
        if (infoList.get(0) instanceof Map) {
            map = (Map) infoList.get(0);
        }
        Log.e("mapdjsaklda", "" + map);
        tv_one.setText(map.get("order_name") == null ? "" : String.valueOf(map.get("order_name")));
        tv_two.setText(map.get("book_count") == null ? "" : String.valueOf(map.get("book_count")) + "人");
        tv_three.setText(map.get("actual_count") == null ? "" : String.valueOf(map.get("actual_count")) + "人");
        tv_four.setText("(" + map.get("guide_mobile") == null ? "" : String.valueOf(map.get("guide_mobile")) + ")" + map.get("vehicle_number") == null ? "" : String.valueOf(map.get("vehicle_number")));
        dialog = new Dialog(TeamStatisticsFragment.this.getActivity(), R.style.ActionSheetDialogStyle);
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
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-25  13:50
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                print();
                break;
        }
    }

    /**
     * *******************************
     * 打印
     *
     * @Author lbr
     * create time 2018-10-24  15:13
     * *******************************
     */
    private void print() {

        float size = Integer.parseInt("24");
        StringBuffer sb = new StringBuffer();
        sb.append("团队电子票统计" + "\n");
        sb.append("*******************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            StringBuffer sb1 = new StringBuffer();
            sb1.append("票种名称:" + map.get("type_name") == null ? "" : String.valueOf(map.get("type_name")) + "\n");
            sb1.append("销票人数:" + map.get("sum") == null ? "" : String.valueOf(map.get("sum")) + "\n");
            AidlUtil.getInstance().printText(sb1.toString(), size, false, false);
        }
    }
}

