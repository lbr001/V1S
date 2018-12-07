package com.sunmi.helper.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sunmi.R;
import com.sunmi.helper.adapter.CancleAdapter;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:景区门票
 * 创建时间: 2018-10-24 14:08
 */
public class TicketFragment extends BaseFragment implements View.OnClickListener {

    // 标志fragment是否初始化完成
    private boolean isPrepared;
    private View view;

    //查询输入框
    private EditText et_num;
    //销票详情list
    private ListView mListView;
    //提示信息
    private String wain_info;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //无信息控件布局  有信息控件布局
    private LinearLayout info_linear, info_linear_one;
    //加载动画
//    private ProgressDialog progressDialog;
    private Dialog mDialog;
    //接收查询信息list
    private List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
    //搜索用到的list
    private List<Map<String, Object>> chlist = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> chlistone = new ArrayList<Map<String, Object>>();
    //适配器
    private CancleAdapter cancleAdapter;
    //搜索按钮
    private ImageButton imageButton;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(getActivity(), wain_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    DialogUtils.closeDialog(mDialog);
                    info_linear_one.setVisibility(View.GONE);
                    info_linear.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    DialogUtils.closeDialog(mDialog);
                    info_linear_one.setVisibility(View.VISIBLE);
                    setData();
                    break;
                case 4:
                    DialogUtils.closeDialog(mDialog);
                    Log.e("bnbnbn", "" + chlistone);
                    if (chlistone != null && !chlistone.isEmpty()) {
                        chlist.clear();
                        for (int i = 0; i < chlistone.size(); i++) {
                            Map map = chlistone.get(i);
                            chlist.add(map);
                        }
                        cancleAdapter.notifyDataSetChanged();
                    } else {
                        wain_info = "暂无查询结果";
                        et_num.setText("");
                        handler.sendEmptyMessage(0);
                    }
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_ticket, container, false);
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
        et_num = view.findViewById(R.id.et_num_one);
        et_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                Log.e("value", value);
                if ("".equals(s.toString()) || "null".equals(value)) {
                    if (!chlist.isEmpty()) {
                        chlist.clear();
                    }
                    for (int i = 0; i < mlist.size(); i++) {
                        Map map = mlist.get(i);
                        chlist.add(map);
                    }
                    Log.e("chlist", "" + chlist);
                    if (!chlist.isEmpty()) {
                        cancleAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

        info_linear = view.findViewById(R.id.info_linear);
        info_linear_one = view.findViewById(R.id.info_linear_one);
        imageButton = view.findViewById(R.id.check_one);
        imageButton.setOnClickListener(this);
        mListView = view.findViewById(R.id.fragment_ticket_list);
        mDialog = DialogUtils.createLoadingDialog(getActivity(), "加载中...");
        getInfo();
    }

    /**
     * 获取详情列表
     */
    private void getInfo() {
        if (PublicMethod.isNetworkAvailable(getActivity())) {
            wain_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("pageNo", "1");
                    modelmap.put("pageSize", "20000");
                    Map canclemap = httpUtil.cancleInfo(modelmap);
                    Log.e("canclemap", "" + canclemap);
                    if (PublicMethod.checkIfNull(canclemap) || "null".equals(String.valueOf(canclemap))) {
                        handler.sendEmptyMessage(1);
                        return;
                    } else {
                        if (canclemap.containsKey("errcode") && "0".equals(canclemap.get("errcode").toString())) {
                            Map datamap = (Map) canclemap.get("data");
                            Log.e("datamap", "" + datamap);
                            if (datamap.containsKey("results")) {
                                Object object = datamap.get("results");
                                if (object instanceof List) {
                                    if (mlist != null) {
                                        mlist.clear();
                                    }
                                    mlist = (List<Map<String, Object>>) object;
                                    if (mlist.size() < 1) {
                                        handler.sendEmptyMessage(1);
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
                            wain_info = canclemap.get("errmsg").toString();
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
        if (chlist != null) {
            chlist.clear();
        }
        for (int i = 0; i < mlist.size(); i++) {
            Map map = mlist.get(i);
            chlist.add(map);
        }
        Log.e("dsds", "" + chlist);
        cancleAdapter = new CancleAdapter(getActivity(), chlist);
        mListView.setAdapter(cancleAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //查询按钮
            case R.id.check_one:
                String codenum = et_num.getText().toString().trim();
                if (TextUtils.isEmpty(codenum)) {
                    wain_info = "请输入搜索的内容";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    HiddenSoftKeyboard();
                    if (chlistone != null) {
                        chlistone.clear();
                    }
                    DialogUtils.closeDialog(mDialog);
                    DialogUtils.createLoadingDialog(getActivity(), "查询中...");
                    search(codenum);
                }
                break;
        }
    }

    /**
     * 隐藏软键盘
     */
    private void HiddenSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 过滤列表信息 获取搜索内容
     *
     * @param codenum
     */
    private void search(String codenum) {
        for (int i = 0; i < mlist.size(); i++) {
            String values = mlist.get(i).get("destorytype") == null ? "" : String.valueOf(mlist.get(i).get("destorytype"));
            String values1 = mlist.get(i).get("tkt_name") == null ? "" : String.valueOf(mlist.get(i).get("tkt_name"));
            String values2 = mlist.get(i).get("update_time") == null ? "" : String.valueOf(mlist.get(i).get("update_time"));
            if (!TextUtils.isEmpty(values)) {
                if (values.trim().contains(codenum)) {
                    Map map = mlist.get(i);
                    chlistone.add(map);
                }
            }
            if (!TextUtils.isEmpty(values1)) {
                if (values1.trim().contains(codenum)) {
                    Map map = mlist.get(i);
                    chlistone.add(map);
                }
            }
            if (!TextUtils.isEmpty(values2)) {
                if (values2
                        .trim().contains(codenum)) {
                    Map map = mlist.get(i);
                    chlistone.add(map);
                }
            }
        }
        handler.sendEmptyMessage(4);
    }
}
