package com.sunmi.helper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.OrderAdapter;
import com.sunmi.helper.bean.ProductBean;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author lbr
 * 功能描述:订单列表页
 * 创建时间: 2018-10-23 9:25
 */
public class OrderActivity extends Activity implements View.OnClickListener {

    //订单列表详情信息
    private List<ProductBean> orderlist = new ArrayList<ProductBean>();
    //商品金额  实付款   商品数量
    private TextView tv_money_one, tv_money_two, tv_ticket_count;
    //标题头
    private TextView tv_title;
    //订单列表list
    private ListView orderListView;
    //页面传值
    private Bundle bundle;
    //适配器
    private OrderAdapter orderAdapter;
    //接收前以页面传过来的数据
    //数量
    private String size;
    //金额
    private String money;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(OrderActivity.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("订单确认");
        orderListView = findViewById(R.id.order_list);
        tv_money_one = findViewById(R.id.tv_money_one);
        tv_ticket_count = findViewById(R.id.tv_ticket_num);
        tv_money_two = findViewById(R.id.tv_money_count);
        bundle = getIntent().getExtras();
        size = String.valueOf(bundle.get("listsize"));
        tv_ticket_count.setText("共" + size.trim() + "张");
        String money_one = String.valueOf(bundle.get("money"));
        double mon = Double.parseDouble(money_one);
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        money = decimalFormat.format(mon);
        tv_money_two.setText("¥" + money.trim());
        tv_money_one.setText("¥" + money.trim());
        orderlist = (List<ProductBean>) bundle.get("list");
        Log.e("orderlist", "" + orderlist);
        orderAdapter = new OrderAdapter(OrderActivity.this, orderlist, PublicMethod.cache);
        orderListView.setAdapter(orderAdapter);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.header_iv:
                startActivity(new Intent(OrderActivity.this, ProductSale.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //提交订单
            case R.id.commit_order:
                Intent intent = new Intent(OrderActivity.this, PayActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putSerializable("mlist", (Serializable) orderlist);
                bundle1.putSerializable("mmoney", money);
                Log.e("size", size);
                bundle1.putString("count", size.trim());
                intent.putExtras(bundle1);
                startActivity(intent);
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(OrderActivity.this, ProductSale.class));
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
