package com.sunmi.helper.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.R;
import com.sunmi.helper.activity.SaleHistory;
import com.sunmi.helper.utils.PublicMethod;

import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:
 * 创建时间: 2018-10-26 11:13
 */
public class HistoryAdapter extends BaseAdapter {

    private List<Map<String, Object>> mList;
    private SaleHistory activity;
    private String warn_info;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(activity, warn_info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public HistoryAdapter(List<Map<String, Object>> list, SaleHistory activity) {
        this.mList = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryAdapterHolder holder = new HistoryAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.history_item, null);
            holder.tv_name = convertView.findViewById(R.id.ticket_history_name);
            holder.tv_code = convertView.findViewById(R.id.ticket_code);
            holder.tv_num = convertView.findViewById(R.id.tv_num_one);
            holder.tv_create_time = convertView.findViewById(R.id.create_time);
            holder.tv_price = convertView.findViewById(R.id.money_history_count);
            holder.btn_reprinter = convertView.findViewById(R.id.btn_reprinter);
            holder.tv_pay_type = convertView.findViewById(R.id.pay_type);
            convertView.setTag(holder);
        } else {
            holder = (HistoryAdapterHolder) convertView.getTag();
        }
        holder.btn_reprinter.setTag(position);
        if (mList.get(position) instanceof Map) {
            Map map = mList.get(position);
            String num = "";
            if (map.containsKey("num")) {
                num = map.get("num") == null ? "" : String.valueOf(map.get("num"));
            }
            String back = "";
            if (map.containsKey("back")) {
                back = map.get("back") == null ? "" : String.valueOf(map.get("back"));
            }
            String used = "";
            if (map.containsKey("used")) {
                used = map.get("used") == null ? "" : String.valueOf(map.get("used"));
            }
            holder.tv_num.setText(used + "/" + num + "/" + back);
            if ((Integer.parseInt(num) - Integer.parseInt(back) - Integer.parseInt(used)) == 0) {
                holder.btn_reprinter.setVisibility(View.GONE);
            } else {
                holder.btn_reprinter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int mposition = (Integer) v.getTag();
                        Map map = mList.get(mposition);
                        Log.e("mmmmap", map.toString());
                        activity.getOrderInfo(map.get("code").toString());
                    }
                });
            }
            if (map.containsKey("title")) {
                holder.tv_name.setText(map.get("title") == null ? "" : String.valueOf(map.get("title")));
            } else {
                holder.tv_name.setText("");
            }
            if (map.containsKey("code")) {
                holder.tv_code.setText(map.get("code") == null ? "" : String.valueOf(map.get("code")));
            } else {
                holder.tv_code.setText("");
            }
            if (map.containsKey("create_time")) {
                holder.tv_create_time.setText(map.get("create_time") == null ? "" : String.valueOf(map.get("create_time")));
            } else {
                holder.tv_create_time.setText("");
            }
            if (map.containsKey("unit_price")) {
                String price_one = map.get("unit_price") == null ? "" : String.valueOf(map.get("unit_price"));
                Log.e("price", price_one);
                if (!("".equals(price_one) || "null".equals(String.valueOf(price_one)))) {
                    double price = (Double.valueOf(price_one) * 0.01);
                    holder.tv_price.setText("¥" + String.valueOf((Integer.parseInt(num) * price)));
                } else {
                    holder.tv_price.setText("");
                }
            } else {
                holder.tv_price.setText("");
            }
            if (map.containsKey("payment_type_name")) {
                holder.tv_pay_type.setText(map.get("payment_type_name") == null ? "" : String.valueOf(map.get("payment_type_name")));
            } else {
                holder.tv_pay_type.setText("");
            }

        }
        return convertView;
    }

    class HistoryAdapterHolder {
        TextView tv_name, tv_price, tv_create_time, tv_num, tv_code, tv_pay_type;
        Button btn_reprinter;
    }
}
