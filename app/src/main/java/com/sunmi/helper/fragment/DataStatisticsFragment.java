package com.sunmi.helper.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sunmi.R;
import com.sunmi.helper.adapter.DataStisticsAdapter;
import com.sunmi.helper.utils.AidlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:销售历史(数据统计)
 * 创建时间: 2018-10-25 15:20
 */
public class DataStatisticsFragment extends Fragment implements View.OnClickListener {

    //listview
    private ListView mlistView;
    //提示信息
    private String warn_info;
    //打印按钮
    private Button btn_print;
    private DataStisticsAdapter dataStisticsAdapter;
    //有、无数据的布局
    private LinearLayout linearLayout, linearLayout_two;
    //销售金额  退票金额
    private String sale_money, cancle_money;
    //门票 使用、退还、总数量
    private int ticket_count = 0, used_count = 0, back_count = 0;
    //数据list
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity(), warn_info, Toast.LENGTH_SHORT).show();
                    linearLayout_two.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datastatistics, null);
        linearLayout = view.findViewById(R.id.market_linear_one);
        linearLayout_two = view.findViewById(R.id.mlin_two);
        mlistView = view.findViewById(R.id.datastatistics_list);
        btn_print = view.findViewById(R.id.btn_market_info);
        btn_print.setOnClickListener(this);
        AidlUtil.getInstance().connectPrinterService(getActivity());
        return view;
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-25  15:53
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_market_info:
                print(list, sale_money, cancle_money, ticket_count, used_count, back_count
                );
                break;
        }

    }

    /**
     * *******************************
     * dayin
     *
     * @Author lbr
     * create time 2018-10-25  15:44
     * *******************************
     */
    private void print(List<Map<String, Object>> list, String sale_money, String cancle_money, int ticket_count, int used_count, int back_count) {
        float size = Integer.parseInt("24");
        StringBuffer sb = new StringBuffer();
        sb.append("营销统计明细" + "\n");
        sb.append("*************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        StringBuffer sb1 = new StringBuffer();
        sb1.append("销票数量:" + ticket_count + "张" + "\n");
        sb1.append("核销数量:" + used_count + "张" + "\n");
        sb1.append("退票数量:" + back_count + "张" + "\n");
        sb1.append("销售金额:" + sale_money + "元" + "\n");
        sb1.append("退票总额:" + cancle_money + "元" + "\n");
        AidlUtil.getInstance().printText(sb1.toString(), size, false, false);
        for (int i = 0; i < list.size(); i++) {
            StringBuffer sb2 = new StringBuffer();
            Map map = list.get(i);
            String title = map.get("title") == null ? "" : String.valueOf(map.get("title"));
            String num = map.get("num") == null ? "" : String.valueOf(map.get("num"));
            String back = map.get("back") == null ? "" : String.valueOf(map.get("back"));
            String used = map.get("used") == null ? "" : String.valueOf(map.get("used"));
            String order_from = map.get("from_app_name") == null ? "" : String.valueOf(map.get("from_app_name"));
            String from_app_name = "";
            if (!("".equals(order_from.toString()) || "null".equals(String.valueOf(order_from)))) {
                if ("juyou".equals(order_from)) {
                    from_app_name = "直销";
                } else if ("mtp".equals(order_from)) {
                    from_app_name = "美团";
                } else {
                    from_app_name = "分销";
                }
            }
            sb2.append("票种名称:" + title + "\n");
            sb2.append("总数/使用/退票:" + num + "/" + used + "/" + back + "\n");
            sb2.append("订单来源:" + from_app_name + "\n");
            AidlUtil.getInstance().printText(sb2.toString(), size, false, false);
        }
    }

    /**
     * 初始化数据
     *
     * @param mList        显示的列表
     * @param sale_money   销售总金额
     * @param cancle_money 退票金额
     * @param ticket_count 票总数量
     * @param used_count   票使用数量
     * @param back_count   退票数量
     */
    public void setData(List<Map<String, Object>> mList, String sale_money, String cancle_money, int ticket_count, int used_count, int back_count) {
        Log.e("mlist", "" + mList);
        if (mList.size() < 1) {
            Log.e("1111", "1111");
            warn_info = "暂无数据";
            handler.sendEmptyMessage(0);
            return;
        } else {
            this.sale_money = sale_money;
            this.cancle_money = cancle_money;
            this.ticket_count = ticket_count;
            this.used_count = used_count;
            this.back_count = back_count;
            this.list = mList;
            mlistView.setVisibility(View.VISIBLE);
            dataStisticsAdapter = new DataStisticsAdapter(getActivity(), mList);
            mlistView.setAdapter(dataStisticsAdapter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}
