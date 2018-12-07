package com.sunmi.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sunmi.R;

import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:自驾统计适配器
 * 创建时间: 2018-10-24 15:37
 */
public class DriverAdapter extends BaseAdapter {
    private Context mContext;
    private List mList;

    public DriverAdapter(Context context, List list) {
        this.mContext = context;
        this.mList = list;
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
        DriverAdapterHolder holder = new DriverAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.driver_item, null);
            holder.tv_name = convertView.findViewById(R.id.ticket_name);
            holder.tv_num = convertView.findViewById(R.id.cancle_num);
            convertView.setTag(holder);
        } else {
            holder = (DriverAdapterHolder) convertView.getTag();
        }
        if (mList.get(position) instanceof Map) {
            Map map = (Map) mList.get(position);
            if (map.containsKey("type_name")) {
                holder.tv_name.setText(map.get("type_name") == null ? "" : String.valueOf(map.get("type_name")));
            } else {
                holder.tv_name.setText("");
            }
            if (map.containsKey("count")) {
                String count = map.get("count") == null ? "" : String.valueOf(map.get("count"));
                if ("".equals(count.trim().toString()) || "null".equals(String.valueOf(count))) {
                    holder.tv_num.setText("");
                } else {
                    holder.tv_num.setText(count.trim());
                }
            } else {
                holder.tv_num.setText("");
            }
        }
        return convertView;
    }

    class DriverAdapterHolder {
        TextView tv_name, tv_num;
    }
}
