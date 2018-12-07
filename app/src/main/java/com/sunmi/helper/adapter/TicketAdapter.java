package com.sunmi.helper.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
 * 功能描述:验票界面票种列表适配器
 * 创建时间: 2018-10-24 10:31
 */
public class TicketAdapter extends BaseAdapter {

    private Context mContext;
    private List mList;

    public TicketAdapter(Context context, List list) {
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
        TicketAdapterHolder holder = new TicketAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ticket_info, null);
            holder.tv_name = convertView.findViewById(R.id.ticket_name);
            holder.tv_count = convertView.findViewById(R.id.ticket_count);
            convertView.setTag(holder);
        } else {
            holder = (TicketAdapterHolder) convertView.getTag();
        }
        if (mList.get(position) instanceof Map) {
            Map map = (Map) mList.get(position);
            Log.e("mapdadad", "" + map);
            if (map.containsKey("type_name")) {
                holder.tv_name.setText(map.get("type_name") == null ? "" : String.valueOf(map.get("type_name")));
            } else {
                holder.tv_name.setText("");
            }
            if (map.containsKey("count")) {
                String count = map.get("count") == null ? "" : String.valueOf(map.get("count"));
                if (TextUtils.isEmpty(count)) {
                    holder.tv_count.setText("");
                } else {
                    holder.tv_count.setText(count.trim() + "张");
                }

            } else {
                holder.tv_count.setText("");
            }
        }
        return convertView;
    }

    class TicketAdapterHolder {
        TextView tv_name, tv_count;
    }
}
