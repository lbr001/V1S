package com.sunmi.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.sunmi.R;

import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:团队预定Spinner适配器
 * 创建时间: 2018-10-24 15:55
 */
public class SpinnerAdapter extends BaseAdapter {

    private List mList;
    private Context mContext;

    public SpinnerAdapter(Context context, List list) {
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
        SpinnerAdapterHolder holder = new SpinnerAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_list, null);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (SpinnerAdapterHolder) convertView.getTag();
        }
        if (mList.get(position) instanceof Map) {
            Map map = (Map) mList.get(position);
            holder.tv_name.setText(map.get("name") == null ? "" : String.valueOf(map.get("name")));

        }
        return convertView;
    }

    class SpinnerAdapterHolder {
        CheckedTextView tv_name;
    }
}
