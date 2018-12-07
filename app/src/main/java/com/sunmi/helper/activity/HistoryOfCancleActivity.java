package com.sunmi.helper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.FragmentAdapter;
import com.sunmi.helper.fragment.DriverFragment;
import com.sunmi.helper.fragment.TeamScheduledFragment;
import com.sunmi.helper.fragment.TicketFragment;
import com.sunmi.helper.fragment.TeamStatisticsFragment;
import com.sunmi.helper.utils.ExitAppUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author lbr
 * 功能描述:核销历史
 * 创建时间: 2018-10-24 13:43
 */
public class HistoryOfCancleActivity extends FragmentActivity implements View.OnClickListener {

    //标题
    private TextView tv_title;
    //viewpager
    private ViewPager mviewpager;
    //按钮控件
    private Button btn_ticket, btn_group, btn_driver, btn_tj;
    //fragment
    private Fragment mFragmentOne, mFragmentTwo, mFragmentThree, mFragmentFour;
    //fragment 适配器
    private FragmentAdapter mFragmentAdapter = null;
    //fragment 集合
    private List<Fragment> mFragmentList = new ArrayList<>();
    //Button集合
    private List<Button> mButtonList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_of_cancel);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(HistoryOfCancleActivity.this);
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("核销历史");
        mviewpager = findViewById(R.id.mviewpager);
        btn_ticket = findViewById(R.id.t_btn_ticket_true);
        btn_group = findViewById(R.id.t_btn_group_true);
        btn_driver = findViewById(R.id.t_btn_driver_true);
        btn_tj = findViewById(R.id.t_btn_tj_true);
        initFragment();
        initAdapter();
        changAlpha(0);
    }

    /**
     * *******************************
     * 初始化fragment
     *
     * @Author lbr
     * create time 2018-10-25  14:24
     * *******************************
     */
    private void initFragment() {
        mFragmentOne = new TicketFragment();
        mFragmentTwo = new TeamScheduledFragment();
        mFragmentThree = new DriverFragment();
        mFragmentFour = new TeamStatisticsFragment();
        mFragmentList.add(mFragmentOne);
        mFragmentList.add(mFragmentTwo);
        mFragmentList.add(mFragmentThree);
        mFragmentList.add(mFragmentFour);
        mButtonList.add(btn_ticket);
        mButtonList.add(btn_group);
        mButtonList.add(btn_driver);
        mButtonList.add(btn_tj);
    }

    /**
     * *******************************
     * viewpager添加布局 添加监听事件
     *
     * @Author lbr
     * create time 2018-10-25  14:24
     * *******************************
     */
    private void initAdapter() {
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mviewpager.setAdapter(mFragmentAdapter);
        //viewpager滑动监听
        mviewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changAlpha(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                changAlpha(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
            //返回
            case R.id.header_iv:
                startActivity(new Intent(HistoryOfCancleActivity.this, TicketActivity.class));
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //门票核销
            case R.id.t_btn_ticket_true:
                mviewpager.setCurrentItem(0, false);
                break;
            //团队统计
            case R.id.t_btn_group_true:
                mviewpager.setCurrentItem(1, false);
                break;
            //自驾统计
            case R.id.t_btn_driver_true:
                mviewpager.setCurrentItem(2, false);
                break;
            //团队预定
            case R.id.t_btn_tj_true:
                mviewpager.setCurrentItem(3, false);
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(HistoryOfCancleActivity.this, TicketActivity.class));
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 一开始运行、滑动和点击tab结束后设置tab的透明度，fragment的透明度和大小
     */
    private void changAlpha(int postion) {
        for (int i = 0; i < mButtonList.size(); i++) {
            if (i == postion) {
                mButtonList.get(i).setAlpha(1.0f);
                if (null != mFragmentList.get(i).getView()) {
                    mFragmentList.get(i).getView().setAlpha(1.0f);
                    mFragmentList.get(i).getView().setScaleX(1.0f);
                    mFragmentList.get(i).getView().setScaleY(1.0f);
                }
            } else {
                mButtonList.get(i).setAlpha(0.0f);
                if (null != mFragmentList.get(i).getView()) {
                    mFragmentList.get(i).getView().setAlpha(0.0f);
                    mFragmentList.get(i).getView().setScaleX(0.0f);
                    mFragmentList.get(i).getView().setScaleY(0.0f);
                }
            }
        }
    }

    /**
     * 根据滑动设置透明度
     */
    private void changAlpha(int pos, float posOffset) {
        int nextIndex = pos + 1;
        if (posOffset > 0) {
            //设置tab的颜色渐变效果
            mButtonList.get(nextIndex).setAlpha(posOffset);
            mButtonList.get(pos).setAlpha(1 - posOffset);
            //设置fragment的颜色渐变效果
            mFragmentList.get(nextIndex).getView().setAlpha(posOffset);
            mFragmentList.get(pos).getView().setAlpha(1 - posOffset);
            //设置fragment滑动视图由大到小，由小到大的效果
            mFragmentList.get(nextIndex).getView().setScaleX(0.5F + posOffset / 2);
            mFragmentList.get(nextIndex).getView().setScaleY(0.5F + posOffset / 2);
            mFragmentList.get(pos).getView().setScaleX(1 - (posOffset / 2));
            mFragmentList.get(pos).getView().setScaleY(1 - (posOffset / 2));
        }
    }

}
