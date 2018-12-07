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
 * 功能描述:团队统计适配器
 * 创建时间: 2018-10-24 15:05
 */
public class GroupAdapterOne extends BaseAdapter {

    private Context mContext;
    private List mList;

    public GroupAdapterOne(Context context, List list) {
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
        GroupAdapterOneHolder holder = new GroupAdapterOneHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_item, null);
            holder.tv_name = convertView.findViewById(R.id.ticket_name);
            holder.tv_man = convertView.findViewById(R.id.sd_man);
            convertView.setTag(holder);
        } else {
            holder = (GroupAdapterOneHolder) convertView.getTag();
        }
        Object object = mList.get(position);
        if (object instanceof Map) {
            Map map = (Map) object;
            holder.tv_name.setText(map.get("type_name") == null ? "" : String.valueOf(map.get("type_name")));
            String man = map.get("sum") == null ? "" : String.valueOf(map.get("sum"));
            if ("".equals(man.toString()) || "null".equals(String.valueOf(man))) {
                holder.tv_man.setText("");
            } else {
                holder.tv_man.setText(man + "人");
            }
        }

        return convertView;
    }

    class GroupAdapterOneHolder {
        TextView tv_name, tv_man;
    }
}
