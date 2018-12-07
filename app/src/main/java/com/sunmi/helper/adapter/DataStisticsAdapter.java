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
 * 功能描述:营销统计 数据统计适配器
 * 创建时间: 2018-10-25 15:38
 */
public class DataStisticsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Map<String, Object>> mList;

    public DataStisticsAdapter(Context context, List<Map<String, Object>> list) {
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
        DataStisticsAdapterHolder holder = new DataStisticsAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.datastatistics_item, null);
            holder.tv_name = convertView.findViewById(R.id.tv_title);
            holder.tv_num = convertView.findViewById(R.id.tv_num_two);
            holder.tv_from_app = convertView.findViewById(R.id.tv_from_app);
            convertView.setTag(holder);
        } else {
            holder = (DataStisticsAdapterHolder) convertView.getTag();
        }
        if (mList.get(position) instanceof Map) {
            Map map = mList.get(position);
            if (map.containsKey("title")) {
                holder.tv_name.setText(map.get("title") == null ? "" : String.valueOf(map.get("title")));
            }
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
            if (map.containsKey("from_app_name")) {
                String order_name = map.get("from_app_name") == null ? "" : String.valueOf(map.get("from_app_name"));
                if (!("".equals(order_name.toString()) || "null".equals(String.valueOf(order_name)))) {
                    if ("juyou".equals(order_name)) {
                        holder.tv_from_app.setText("直销");
                    } else if ("mtp".equals(order_name)) {
                        holder.tv_from_app.setText("美团");
                    } else {
                        holder.tv_from_app.setText("分销");
                    }
                }
            }
        }


        return convertView;
    }

    class DataStisticsAdapterHolder {
        TextView tv_name, tv_num, tv_from_app;
    }
}
