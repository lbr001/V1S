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
 * 功能描述:招徕奖励旅游团适配器
 * 创建时间: 2018-11-12 11:04
 */
public class AttcrtAdapter extends BaseAdapter {

    private List mList;
    private Context mContext;

    public AttcrtAdapter(Context context, List list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AttcrtAdapterHolder holder = new AttcrtAdapterHolder();
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.attartadapter_item, null);
            holder.tv_name = view.findViewById(R.id.group_name);
            holder.tv_car_num = view.findViewById(R.id.car_num);
            holder.tv_guide = view.findViewById(R.id.guide_name);
            holder.tv_guide_mobile = view.findViewById(R.id.guide_mobile);
            view.setTag(holder);
        } else {
            holder = (AttcrtAdapterHolder) view.getTag();
        }
        if (mList.get(i) instanceof Map) {
            Map map = (Map) mList.get(i);
            if (map.containsKey("order_name")) {
                holder.tv_name.setText(map.get("order_name") == null ? "" : String.valueOf(map.get("order_name")));
            }
            if (map.containsKey("guide_mobile")) {
                holder.tv_guide_mobile.setText(map.get("guide_mobile") == null ? "" : String.valueOf(map.get("guide_mobile")));
            }
            if (map.containsKey("guide_name")) {
                holder.tv_guide.setText(map.get("guide_name") == null ? "" : String.valueOf(map.get("guide_name")));
            }
            if (map.containsKey("vehicle_number")) {
                holder.tv_car_num.setText(map.get("vehicle_number") == null ? "" : String.valueOf(map.get("vehicle_number")));
            }
        }
        return view;
    }

    class AttcrtAdapterHolder {
        TextView tv_name, tv_car_num, tv_guide, tv_guide_mobile;
    }
}
