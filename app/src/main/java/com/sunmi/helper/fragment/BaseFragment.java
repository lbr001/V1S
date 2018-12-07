package com.sunmi.helper.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * @Author lbr
 * 功能描述:* ViewPager + Fragment 结构中
 * ViewPager 有预加载功能，在访问网络的时候会同时加载多个页面的网络，体验很不好，
 * 更会影响一些带有页面进度条的显示
 * 所以ViewPager中的Fragment 都继承这个类。 效果是只预加载布局，但是不会访问网络。
 * 创建时间: 2018-10-24 14:09
 */
public abstract class BaseFragment extends Fragment {

    //用于标记视图是否初始化
    protected boolean isVisible;
    //在onCreate方法之前调用，用来判断Fragment的UI是否是可见的
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }
    /**
     * 视图可见
     * */
    protected void onVisible(){
        lazyLoad();
    }
    /**
     * 自定义抽象加载数据方法
     * */
    protected abstract void lazyLoad();
    /**
     * 视图不可见
     * */
    protected void onInvisible(){}

}
