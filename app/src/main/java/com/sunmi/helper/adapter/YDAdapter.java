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
 * 功能描述:团队预定适配器
 * 创建时间: 2018-10-24 15:43
 */
public class YDAdapter extends BaseAdapter {
    private Context mContext;
    private List mList;

    public YDAdapter(Context context, List list) {
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
        YDAdapterHolder holder = new YDAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.yd_item, null);
            holder.tv_name = convertView.findViewById(R.id.ticket_name);
            holder.tv_num = convertView.findViewById(R.id.yd_num);
            holder.tv_time = convertView.findViewById(R.id.yd_modify_time);
            holder.tv_guide = convertView.findViewById(R.id.tour_info);
            holder.tv_carnum = convertView.findViewById(R.id.car_num);
            holder.tv_mark = convertView.findViewById(R.id.tv_mark);
            convertView.setTag(holder);
        } else {
            holder = (YDAdapterHolder) convertView.getTag();
        }
        if (mList.get(position) instanceof Map) {
            Map map = (Map) mList.get(position);
            if (map.containsKey("type_name")) {
                String name = map.get("type_name") == null ? "" : String.valueOf(map.get("type_name"));
                if ("".equals(name.toString()) || "null".equals(String.valueOf(name))) {
                    holder.tv_name.setText("");
                } else {
                    holder.tv_name.setText(name.toString());
                }
            } else {
                holder.tv_name.setText("");
            }
            if (map.containsKey("book_count")) {
                String name = map.get("book_count") == null ? "" : String.valueOf(map.get("book_count"));
                if ("".equals(name.toString()) || "null".equals(String.valueOf(name))) {
                    holder.tv_num.setText("");
                } else {
                    holder.tv_num.setText(name.toString() + "人");
                }
            } else {
                holder.tv_num.setText("");
            }
            if (map.containsKey("update_time")) {
                String name = map.get("update_time") == null ? "" : String.valueOf(map.get("update_time"));
                if ("".equals(name.toString()) || "null".equals(String.valueOf(name))) {
                    holder.tv_time.setText("");
                } else {
                    holder.tv_time.setText(name.toString());
                }
            } else {
                holder.tv_time.setText("");
            }
            if (map.containsKey("vehicle_number")) {
                String name = map.get("vehicle_number") == null ? "" : String.valueOf(map.get("vehicle_number"));
                if ("".equals(name.toString()) || "null".equals(String.valueOf(name))) {
                    holder.tv_carnum.setText("");
                } else {
                    holder.tv_carnum.setText(name.toString());
                }
            } else {
                holder.tv_carnum.setText("");
            }
            if (map.containsKey("guide_name")) {
                String name = map.get("guide_name") == null ? "" : String.valueOf(map.get("guide_name"));
                if ("".equals(name.toString()) || "null".equals(String.valueOf(name))) {
                    holder.tv_guide.setText("");
                } else {
                    holder.tv_guide.setText(name.toString());
                }
            } else {
                holder.tv_guide.setText("");
            }
            if (map.containsKey("remark")) {
                String name = map.get("remark") == null ? "" : String.valueOf(map.get("remark"));
                if ("".equals(name.toString()) || "null".equals(String.valueOf(name))) {
                    holder.tv_mark.setText("");
                } else {
                    holder.tv_mark.setText(name.toString());
                }
            } else {
                holder.tv_mark.setText("");
            }
        }
        return convertView;
    }

    class YDAdapterHolder {
        TextView tv_name, tv_num, tv_time, tv_guide, tv_carnum, tv_mark;
    }
}
