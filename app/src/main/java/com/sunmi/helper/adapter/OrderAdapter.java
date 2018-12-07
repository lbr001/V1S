package com.sunmi.helper.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.R;
import com.sunmi.helper.bean.ProductBean;
import com.sunmi.helper.utils.ImageUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @Author lbr
 * 功能描述:产品购买详情页listview适配器
 * 创建时间: 2018-10-23 9:52
 */
public class OrderAdapter extends BaseAdapter {

    private Context mContext;
    private List<ProductBean> mlist;
    private File cache;
    private ImageUtil mImageUtil;

    public OrderAdapter(Context context, List<ProductBean> list, File cache) {
        this.mContext = context;
        this.mlist = list;
        this.cache = cache;
        mImageUtil = new ImageUtil(this.cache);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderAdapterHolder holder = new OrderAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_list_item, null);
            holder.imageView = convertView.findViewById(R.id.order_picture);
            holder.tv_pirce = convertView.findViewById(R.id.order_price);
            holder.tv_name = convertView.findViewById(R.id.order_name);
            holder.tv_num = convertView.findViewById(R.id.order_num);
            holder.tv_logo = convertView.findViewById(R.id.tv_logo);
            holder.tv_count_logo = convertView.findViewById(R.id.tv_count_logo);
            holder.tv_money_count = convertView.findViewById(R.id.money_count);
            holder.tv_money_logo = convertView.findViewById(R.id.tv_ji);
            convertView.setTag(holder);
        } else {
            holder = (OrderAdapterHolder) convertView.getTag();
        }
        ProductBean productBean = mlist.get(position);
        holder.tv_name.setText(productBean.ticketName);
        mImageUtil.asyncloadImage(holder.imageView, productBean.picturePath);
        String name = String.valueOf(productBean.ticketPrice);
        if (TextUtils.isEmpty(name)) {
            holder.tv_logo.setVisibility(View.GONE);
        } else {
            holder.tv_logo.setVisibility(View.VISIBLE);
        }
        holder.tv_pirce.setText(name);
        String num = String.valueOf(productBean.ticketNum);
        if (TextUtils.isEmpty(num)) {
            holder.tv_count_logo.setVisibility(View.GONE);
        } else {
            holder.tv_count_logo.setVisibility(View.VISIBLE);
        }
        holder.tv_num.setText(num.trim());
        double money_count = (productBean.ticketPrice * productBean.ticketNum);
        if (TextUtils.isEmpty(String.valueOf(money_count))) {
            holder.tv_money_logo.setVisibility(View.GONE);
        } else {
            DecimalFormat df = new DecimalFormat("######0.00");
            String money = df.format(money_count);
            holder.tv_money_count.setText(money);
            holder.tv_money_logo.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class OrderAdapterHolder {
        TextView tv_name, tv_pirce, tv_num, tv_money_count, tv_logo, tv_count_logo, tv_money_logo;
        ImageView imageView;
    }
}
