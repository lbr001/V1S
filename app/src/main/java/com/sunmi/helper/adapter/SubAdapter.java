package com.sunmi.helper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.R;
import com.sunmi.helper.activity.SubListInfo;
import com.sunmi.helper.utils.PublicMethod;

import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:领取补贴列表详情适配器
 * 创建时间: 2018-11-20 14:51
 */
public class SubAdapter extends BaseAdapter {

    private SubListInfo subListInfo;
    private List mList;

    public SubAdapter(SubListInfo subListInfo, List list) {
        this.subListInfo = subListInfo;
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
        SubAdapterHolder holder = new SubAdapterHolder();
        if (view == null) {
            view = LayoutInflater.from(subListInfo).inflate(R.layout.sub_item, null);
            holder.tv_name = view.findViewById(R.id.sub_member_name);
            holder.tv_view_num = view.findViewById(R.id.sub_view_num);
            holder.tv_num = view.findViewById(R.id.sub_member_num);
            holder.tv_state = view.findViewById(R.id.lq_state);
            holder.tv_time = view.findViewById(R.id.sub_lq_time);
            holder.btn_printer = view.findViewById(R.id.btn_sub_printer);
            view.setTag(holder);
        } else {
            holder = (SubAdapterHolder) view.getTag();
        }
        holder.btn_printer.setTag(i);
        if (mList.get(i) instanceof Map) {
            Map map = (Map) mList.get(i);
            holder.tv_view_num.setText(PublicMethod.SPOT_VIEW.trim());
            if (map.containsKey("otime")) {
                holder.tv_time.setText(map.get("otime") == null ? "" : String.valueOf(map.get("otime")));
            }
            if (map.containsKey("name")) {
                holder.tv_name.setText(map.get("name") == null ? "" : String.valueOf(map.get("name")));
            }
            if (map.containsKey("idcard")) {
                holder.tv_num.setText(map.get("idcard") == null ? "" : String.valueOf(map.get("idcard")));
            }
            holder.tv_state.setText("已领取");
        }
        holder.btn_printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mposition = (Integer) v.getTag();
                Map map = (Map) mList.get(mposition);
                subListInfo.reprinter(map);
            }
        });
        return view;
    }

    class SubAdapterHolder {
        TextView tv_name, tv_num, tv_view_num, tv_state, tv_time;
        Button btn_printer;
    }
}
