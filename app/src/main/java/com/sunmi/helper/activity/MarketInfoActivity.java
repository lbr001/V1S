package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.FragmentAdapter;
import com.sunmi.helper.fragment.DataStatisticsFragment;
import com.sunmi.helper.fragment.PieGraphFragment;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述: 营销统计
 * 创建时间: 2018-10-18 13:49
 */
public class MarketInfoActivity extends FragmentActivity implements View.OnClickListener {
    //fragment集合
    private List<Fragment> fragmentlist = new ArrayList<Fragment>();
    //viewpager
    private ViewPager viewPager;
    //数据统计fragment
    private DataStatisticsFragment dataStatisticsFragment;
    //图形统计fragment
    private PieGraphFragment pieGraphFragment;
    //数据统计、图形统计下方条形view
    private View v_sjtj, v_txtj;
    //标题
    private TextView tv_title;
    //销售金额
    private TextView tv_sale_money;
    //核销总数
    private TextView tv_cancle_money;
    //销售张数
    private TextView tv_ticket_count;
    //退票张数
    private TextView tv_back_count;
    //退票总金额
    private TextView tv_ticcount;
    //数据统计
    private TextView tv_sjtj;
    //图形统计
    private TextView tv_txtj;
    //viewpager
    private ViewPager mviewpager;
    //营销数据
    private List<Fragment> mFragmentList = new ArrayList<>();
    //加载动画
    private Dialog mDialog;
    //提示信息
    private String warn_info;
    //销售金额  退票金额
    private String sale_money, cancle_money;
    //核销总数量、使用数量、  退票数量
    private int ticket_count = 0, used_count = 0, back_count = 0;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //查询数据
    private List<Map<String, Object>> infolist = new ArrayList<Map<String, Object>>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.closeDialog(mDialog);
                    Toast.makeText(MarketInfoActivity.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    DialogUtils.closeDialog(mDialog);
                    tv_sale_money.setText(sale_money.trim());
                    tv_ticket_count.setText(String.valueOf(ticket_count).trim());
                    tv_back_count.setText(String.valueOf(back_count).trim());
                    tv_cancle_money.setText(String.valueOf(used_count).trim());
                    tv_ticcount.setText(cancle_money.trim());
                    Log.e("infolist", "" + infolist);
                    dataStatisticsFragment.setData(infolist, sale_money, cancle_money, ticket_count, used_count, back_count);
                    pieGraphFragment.setvalues(ticket_count, infolist);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(MarketInfoActivity.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("营销统计明细");
        tv_sale_money = findViewById(R.id.sale_money);
        tv_cancle_money = findViewById(R.id.tv_cancle_count);
        tv_ticket_count = findViewById(R.id.tv_sale_count);
        tv_back_count = findViewById(R.id.tv_back_count);
        tv_ticcount = findViewById(R.id.tv_ticket_count);
        mviewpager = findViewById(R.id.market_viewpager);
        tv_sjtj = findViewById(R.id.sjtj);
        tv_txtj = findViewById(R.id.txtj);
        v_sjtj = findViewById(R.id.v_sjtj);
        v_txtj = findViewById(R.id.v_txtj);
        viewPager = findViewById(R.id.market_viewpager);
        dataStatisticsFragment = new DataStatisticsFragment();
        pieGraphFragment = new PieGraphFragment();
        fragmentlist.add(dataStatisticsFragment);
        fragmentlist.add(pieGraphFragment);
        MyAdapter myAdapter = new MyAdapter(getSupportFragmentManager(), fragmentlist);
        viewPager.setAdapter(myAdapter);
        changelistener();
        mDialog = DialogUtils.createLoadingDialog(MarketInfoActivity.this, "加载中...");
        getmarketInfo();
    }

    /**
     * 监听事件
     */
    private void changelistener() {
        // 监听viewPager选中页面，改变顶部标题栏
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    //数据统计
                    case 0:
                        tv_sjtj.setTextColor(getResources().getColor(R.color.color14));
                        v_sjtj.setVisibility(View.VISIBLE);
                        tv_txtj.setTextColor(getResources().getColor(R.color.color4));
                        v_txtj.setVisibility(View.GONE);
                        break;
                    //图形统计
                    case 1:
                        tv_sjtj.setTextColor(getResources().getColor(R.color.color4));
                        v_txtj.setVisibility(View.VISIBLE);
                        tv_txtj.setTextColor(getResources().getColor(R.color.color14));
                        v_sjtj.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 获取营销数据
     */
    private void getmarketInfo() {
        if (PublicMethod.isNetworkAvailable(MarketInfoActivity.this)) {
            warn_info = "请连接网络";
            handler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap();
                    modelmap.put("device_code", PublicMethod.SERIZLNUMBER);
                    Map map = httpUtil.getMarketInfo(modelmap);
                    Log.e("marketmap", "" + map);
                    if (PublicMethod.checkIfNull(map)) {
                        warn_info = "暂无统计数据";
                        sale_money = "0";
                        cancle_money = "0";
                        ticket_count = 0;
                        used_count = 0;
                        back_count = 0;
                        handler.sendEmptyMessage(4);
                        return;
                    } else {
                        if (map.containsKey("errcode") && "0".equals(map.get("errcode").toString())) {
                            Log.e("2222", "2222");
                            Map data = (Map) map.get("data");
                            if (!PublicMethod.checkIfNull(data)) {
                                if (data.containsKey("total")) {
                                    Map totalmap = (Map) data.get("total");
                                    Log.e("3333", "3333");
                                    sale_money = totalmap.get("total_money") == null ? "" : String.valueOf(totalmap.get("total_money"));
                                    if (TextUtils.isEmpty(sale_money)) {
                                        sale_money = "0";
                                    } else {
                                        sale_money = totalmap.get("total_money") == null ? "" : String.valueOf(totalmap.get("total_money"));
                                    }
                                    cancle_money = totalmap.get("back_money") == null ? "" : String.valueOf(totalmap.get("back_money"));
                                    if (TextUtils.isEmpty(cancle_money)) {
                                        cancle_money = "0";
                                    } else {
                                        cancle_money = totalmap.get("back_money") == null ? "" : String.valueOf(totalmap.get("back_money"));
                                    }
                                } else {
                                    sale_money = "0";
                                    cancle_money = "0";
                                }
                                if (data.containsKey("page")) {
                                    Map pagemap = (Map) data.get("page");
                                    if (pagemap.containsKey("results")) {
                                        JSONArray jsonArray = (JSONArray) pagemap.get("results");
                                        for (int i = 0; i < jsonArray.size(); i++) {
                                            Map resultmap = (Map) jsonArray.get(i);
                                            ticket_count += Integer.parseInt(String.valueOf(resultmap.get("num") == null ? "" : String.valueOf(resultmap.get("num"))));
                                            used_count += Integer.parseInt(String.valueOf(resultmap.get("used") == null ? "" : String.valueOf(resultmap.get("used"))));
                                            back_count += Integer.parseInt(String.valueOf(resultmap.get("back") == null ? "" : String.valueOf(resultmap.get("back"))));
                                            infolist.add(resultmap);
                                        }
                                    } else {
                                        ticket_count = 0;
                                        used_count = 0;
                                        back_count = 0;
                                    }
                                }
                                handler.sendEmptyMessage(4);
                            } else {
                                warn_info = "暂无数据";
                                handler.sendEmptyMessage(0);
                            }
                        } else {
                            warn_info = map.get("errmsg").toString();
                            sale_money = "0";
                            cancle_money = "0";
                            ticket_count = 0;
                            used_count = 0;
                            back_count = 0;
                            handler.sendEmptyMessage(4);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-25  14:25
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_iv:
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(MarketInfoActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(MarketInfoActivity.this, AdminActivity.class));
                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //数据统计
            case R.id.linear_sjtj:
                viewPager.setCurrentItem(0, true);
                tv_sjtj.setTextColor(getResources().getColor(R.color.color14));
                v_sjtj.setVisibility(View.VISIBLE);
                tv_txtj.setTextColor(getResources().getColor(R.color.color4));
                v_txtj.setVisibility(View.GONE);
                break;
            //图形统计
            case R.id.linear_txtj:
                viewPager.setCurrentItem(1, true);
                tv_sjtj.setTextColor(getResources().getColor(R.color.color4));
                v_txtj.setVisibility(View.VISIBLE);
                tv_txtj.setTextColor(getResources().getColor(R.color.color14));
                v_sjtj.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * mViewPager适配器
     */
    public class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> mList;

        public MyAdapter(FragmentManager fragmentManager, List<Fragment> list) {
            super(fragmentManager);
            this.mList = list;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ("0".equals(PublicMethod.BACK_MARK)) {
                startActivity(new Intent(MarketInfoActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(MarketInfoActivity.this, AdminActivity.class));
            }
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.closeDialog(mDialog);
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }
}
