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
 * 功能描述:招徕奖励实名制认证列表适配器
 * 创建时间: 2018-11-12 11:33
 */
public class RealNameAdapter extends BaseAdapter {

    private Context mContext;
    private List mList;

    public RealNameAdapter(Context context, List list) {
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
        RealNameAdapterHolder holder = new RealNameAdapterHolder();
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.real_name_item, null);
            holder.tv_name = view.findViewById(R.id.real_name);
            holder.tv_cardnum = view.findViewById(R.id.real_cardnum);
            holder.tv_time = view.findViewById(R.id.real_time);
            holder.tv_state = view.findViewById(R.id.real_name_state);
            view.setTag(holder);
        } else {
            holder = (RealNameAdapterHolder) view.getTag();
        }
        if (mList.get(i) instanceof Map) {
            Map map = (Map) mList.get(i);
            if (map.containsKey("name")) {
                holder.tv_name.setText(map.get("name") == null ? "" : String.valueOf(map.get("name")));
            }
            if (map.containsKey("cardno")) {
                holder.tv_cardnum.setText(map.get("cardno") == null ? "" : String.valueOf(map.get("cardno")));
            }
            if (map.containsKey("verify_time")) {
                holder.tv_time.setText(map.get("verify_time") == null ? "" : String.valueOf(map.get("verify_time")));
            }
            holder.tv_state.setText("已认证");
        }
        return view;
    }

    class RealNameAdapterHolder {
        TextView tv_name, tv_cardnum, tv_time, tv_state;
    }
}
