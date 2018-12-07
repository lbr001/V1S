package com.sunmi.helper.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.R;
import com.sunmi.helper.activity.ProductSale;
import com.sunmi.helper.bean.ProductBean;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @Author lbr
 * 功能描述:购物车产品适配器
 * 创建时间: 2018-10-22 16:25
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

    private ProductSale activity;
    private SparseArray<ProductBean> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;

    public SelectAdapter(ProductSale activity, SparseArray<ProductBean> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_product_sale_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductBean item = dataList.valueAt(position);
        holder.bindData(item);

    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProductBean item;
        private TextView tvCost, tvCount, tvAdd, tvMinus, tvName, tv_logo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.dialog_ticket_name);
            tvCost = (TextView) itemView.findViewById(R.id.dialog_ticket_price);
            tvCount = (TextView) itemView.findViewById(R.id.dialog_ticket_num);
            tv_logo = itemView.findViewById(R.id.dialog_tv_logo);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
        }

        @Override

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tvAdd:
                    activity.add(item, true);
                    break;
                case R.id.tvMinus:
                    activity.remove(item, true);
                    break;
                default:
                    break;
            }
        }

        public void bindData(ProductBean item) {
            this.item = item;
            tvName.setText(item.ticketName);
            tv_logo.setText("¥");
            double price = (item.ticketPrice * item.ticketNum);
            DecimalFormat df = new DecimalFormat("######0.00");
            String money = df.format(price);
            tvCost.setText(money);
            tvCount.setText(String.valueOf(item.ticketNum));
        }
    }
}
