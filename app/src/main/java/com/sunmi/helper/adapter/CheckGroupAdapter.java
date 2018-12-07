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
 * 功能描述:团票适配器(验票界面)
 * 创建时间: 2018-10-24 10:21
 */
public class CheckGroupAdapter extends BaseAdapter {
    private Context mContext;
    private List mList;

    public CheckGroupAdapter(Context context, List list) {
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
        CheckGroupAdapterHolder holder = new CheckGroupAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cgroup_item, null);
            holder.tv_group_name = convertView.findViewById(R.id.cgroup_name);
            holder.tv_guide_name = convertView.findViewById(R.id.cgroup_guide_name);
            holder.tv_guide_mobile = convertView.findViewById(R.id.cgroup_mobile);
            holder.tv_carnum = convertView.findViewById(R.id.cgroup_carNum);
            holder.tv_markinfo = convertView.findViewById(R.id.cgroup_markinfo);
            holder.tv_time = convertView.findViewById(R.id.cgroup_update_time);
            convertView.setTag(holder);
        } else {
            holder = (CheckGroupAdapterHolder) convertView.getTag();
        }
        if (mList.get(position) instanceof Map) {
            Map map = (Map) mList.get(position);
            if (map.containsKey("type_name")) {
                holder.tv_group_name.setText(map.get("type_name") == null ? "" : String.valueOf(map.get("type_name")));
            }
            if (map.containsKey("guide_name")) {
                holder.tv_guide_name.setText(map.get("guide_name") == null ? "" : String.valueOf(map.get("guide_name")));
            }
            if (map.containsKey("guide_mobile")) {
                holder.tv_guide_mobile.setText(map.get("guide_mobile") == null ? "" : String.valueOf(map.get("guide_mobile")));
            }
            if (map.containsKey("vehicle_number")) {
                holder.tv_carnum.setText(map.get("vehicle_number") == null ? "" : String.valueOf(map.get("vehicle_number")));
            }
            if (map.containsKey("remark")) {
                holder.tv_markinfo.setText(map.get("remark") == null ? "" : String.valueOf(map.get("remark")));
            }
            if (map.containsKey("update_time")) {
                holder.tv_time.setText(map.get("update_time") == null ? "" : String.valueOf(map.get("update_time")));
            }

        }
        return convertView;
    }

    class CheckGroupAdapterHolder {
        TextView tv_group_name, tv_guide_name, tv_guide_mobile, tv_markinfo, tv_carnum, tv_time;
    }
}
