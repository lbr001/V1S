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
import com.sunmi.helper.adapter.SpinnerAdapter;
import com.sunmi.helper.adapter.YDAdapter;
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
 * 功能描述:团队预定 Team scheduled
 * 创建时间: 2018-10-24 14:52
 */
public class TeamScheduledFragment extends BaseFragment implements View.OnClickListener {

    // 标志fragment是否初始化完成
    private boolean isPrepared;
    private View view;

    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //提示信息
    private String warn_info;
    //有、无数据布局
    private LinearLayout linearLayoutone;
    private LinearLayout linearLayouttwo;
    //数据控件listview
    private ListView listView;
    //数据接收list
    private List list;

    private Button button;
    private YDAdapter ydAdapter;
    //spinner选择器
    private Spinner spinner;
    //    private ProgressDialog progressDialog;
    private Dialog mDialog;
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
                    linearLayoutone.setVisibility(View.VISIBLE);
                    linearLayouttwo.setVisibility(View.GONE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    linearLayoutone.setVisibility(View.GONE);
                    linearLayouttwo.setVisibility(View.VISIBLE);
                    setData();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_yd, container, false);
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

    /**
     * *******************************
     * 初始化控件
     *
     * @Author lbr
     * create time 2018-10-25  14:03
     * *******************************
     */
    private void initViews() {
        linearLayoutone = view.findViewById(R.id.yd_linear_one);
        linearLayouttwo = view.findViewById(R.id.yd_linear_two);
        listView = view.findViewById(R.id.yd_listview);
        spinner = view.findViewById(R.id.yd_spinner);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map map = new HashMap();
        map.put("name", "今日预定");
        Map map1 = new HashMap();
        map1.put("name", "明日预定");
        list.add(map);
        list.add(map1);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity(), list);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("我进入了这里", "come in ");
                mDialog = DialogUtils.createLoadingDialog(getActivity(), "加载中...");
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button = view.findViewById(R.id.btn_next);
        button.setOnClickListener(this);
//        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (PublicMethod.isNetworkAvailable(getActivity())) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    switch (spinner.getSelectedItemPosition()) {
                        case 0:
                            modelmap.put("type", "grouptotaltodaylist");
                            break;
                        case 1:
                            modelmap.put("type", "grouptotaltomlist");
                            break;
                    }
                    modelmap.put("view_code", PublicMethod.SPOT_VIEW.trim());
                    Map map = httpUtil.TeamBooking(modelmap);
                    Log.e("ydmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            if (map.containsKey("data")) {
                                Object object = map.get("data");
                                Log.e("mobject", String.valueOf(object));
                                if (object instanceof List) {
                                    list = (List) object;
                                    if (list.size() > 0) {
                                        handler.sendEmptyMessage(2);
                                        return;
                                    } else {
                                        handler.sendEmptyMessage(1);
                                        return;
                                    }
                                }
                            } else {
                                handler.sendEmptyMessage(1);
                                return;
                            }
                        } else {
                            handler.sendEmptyMessage(1);
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
        ydAdapter = new YDAdapter(getActivity(), list);
        listView.setAdapter(ydAdapter);
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-25  14:05
     * *******************************
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                Print();
                break;
        }
    }

    /**
     * *******************************
     * 打印
     *
     * @Author lbr
     * create time 2018-10-25  14:05
     * *******************************
     */
    private void Print() {
        float size = Integer.parseInt("24");
        StringBuffer sb = new StringBuffer();
        sb.append("团队预定电子票统计" + "\n");
        sb.append("******************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            StringBuffer sb1 = new StringBuffer();
            String name = map.get("type_name") == null ? "" : String.valueOf(map.get("type_name"));
            String num = map.get("book_count") == null ? "" : String.valueOf(map.get("book_count"));
            String time = map.get("update_time") == null ? "" : String.valueOf(map.get("update_time"));
            String guide_info = map.get("guide_name") == null ? "" : String.valueOf(map.get("guide_name"));
            String car_num = map.get("vehicle_number") == null ? "" : String.valueOf(map.get("vehicle_number"));
            String mark_info = map.get("remark") == null ? "" : String.valueOf(map.get("remark"));
            sb1.append("票种名称:" + name + "\n");
            sb1.append("预订人数:" + num + "\n");
            sb1.append("最后修改时间:" + time + "\n");
            sb1.append("导游信息:" + guide_info + "\n");
            sb1.append("车牌号码:" + car_num + "\n");
            sb1.append("备注信息:" + mark_info + "\n");
            AidlUtil.getInstance().printText(sb1.toString(), size, false, false);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        handler.removeCallbacksAndMessages(null);
    }
}
