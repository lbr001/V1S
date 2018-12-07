package com.sunmi.helper.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.R;
import com.sunmi.helper.activity.ProductSale;
import com.sunmi.helper.bean.ProductBean;
import com.sunmi.helper.utils.ImageUtil;

import java.io.File;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * @Author lbr
 * 功能描述:产品列表适配器
 * 创建时间: 2018-10-22 16:39
 */
public class ProductAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<ProductBean> mList;
    private File cache;
    private ProductSale mContext;
    private ImageUtil mImageUtil;

    public ProductAdapter(ProductSale context, List<ProductBean> list, File cache) {
        this.mContext = context;
        this.mList = list;
        this.cache = cache;
        mImageUtil = new ImageUtil(this.cache);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_header_view, parent, false);
        }
        ((TextView) (convertView)).setText(mList.get(position).typeName);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mList.get(position).typeId;

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
        ProductHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.pro_sale_list_item, null);
            holder = new ProductHolder(view);
            view.setTag(holder);
        } else {
            holder = (ProductHolder) view.getTag();
        }
        ProductBean productBean = mList.get(i);
        holder.bindData(productBean);
        return view;
    }

    class ProductHolder implements View.OnClickListener {
        private ImageView mImageView;
        private TextView Ima_jian, Ima_jia;
        private TextView tv_ticketName, tv_ticketPrice, tv_ticketCount;
        private ProductBean productBean;
        private TextView tv_one;

        public ProductHolder(View itemView) {
            mImageView = itemView.findViewById(R.id.pro_sale_picture);
            tv_ticketName = itemView.findViewById(R.id.pro_ticket_name);
            tv_ticketPrice = itemView.findViewById(R.id.pro_ticket_price);
            tv_ticketCount = itemView.findViewById(R.id.pro_ticket_count);
            tv_one = itemView.findViewById(R.id.tv_one);
            Ima_jian = itemView.findViewById(R.id.tvMinus);
            Ima_jia = itemView.findViewById(R.id.tvAdd);
            Ima_jian.setOnClickListener(this);
            Ima_jia.setOnClickListener(this);
        }

        public void bindData(ProductBean productBean) {
            this.productBean = productBean;
            mImageUtil.asyncloadImage(mImageView, productBean.picturePath);
            tv_ticketName.setText(productBean.ticketName);
            if (TextUtils.isEmpty(String.valueOf(productBean.ticketPrice))) {
                tv_one.setVisibility(View.GONE);
            } else {
                tv_one.setVisibility(View.VISIBLE);
            }
            tv_ticketPrice.setText(String.valueOf(productBean.ticketPrice));
            productBean.ticketNum = mContext.getSelectedItemCountById(productBean.Id);
            tv_ticketCount.setText(String.valueOf(productBean.ticketNum));
            if (productBean.ticketNum < 1) {
                tv_ticketCount.setVisibility(View.GONE);
                Ima_jian.setVisibility(View.GONE);
            } else {
                tv_ticketCount.setVisibility(View.VISIBLE);
                Ima_jian.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            ProductSale activity = mContext;
            switch (v.getId()) {
                case R.id.tvMinus:
                    int count = activity.getSelectedItemCountById(productBean.Id);
                    if (count < 2) {
                        Ima_jian.setAnimation(getHiddenAnimation());
                        Ima_jian.setVisibility(View.GONE);
                        tv_ticketCount.setVisibility(View.GONE);
                    }
                    count--;
                    activity.remove(productBean, false);
                    tv_ticketCount.setText(String.valueOf(count));
                    break;
                case R.id.tvAdd:
                    int countone = activity.getSelectedItemCountById(productBean.Id);
                    if (countone < 1) {
                        Ima_jian.setAnimation(getShowAnimation());
                        Ima_jian.setVisibility(View.VISIBLE);
                        tv_ticketCount.setVisibility(View.VISIBLE);
                    }
                    activity.add(productBean, false);
                    countone++;
                    tv_ticketCount.setText(String.valueOf(countone));
                    int[] loc = new int[2];
                    v.getLocationInWindow(loc);
                    activity.playAnimation(loc);
                    break;
            }
        }
    }

    /**
     * 获取显示动画
     *
     * @return
     */
    private Animation getShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    /**
     * 获取隐藏动画
     *
     * @return
     */
    private Animation getHiddenAnimation() {
        AnimationSet set = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(rotate);
        TranslateAnimation translate = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 2f
                , TranslateAnimation.RELATIVE_TO_SELF, 0
                , TranslateAnimation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translate);
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }
}
