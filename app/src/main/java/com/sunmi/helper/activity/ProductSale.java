package com.sunmi.helper.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.githang.statusbar.StatusBarCompat;
import com.sunmi.R;
import com.sunmi.helper.adapter.ProductAdapter;
import com.sunmi.helper.adapter.SelectAdapter;
import com.sunmi.helper.bean.ProductBean;
import com.sunmi.helper.net.HttpUtil;
import com.sunmi.helper.utils.ExitAppUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * @Author lbr
 * 功能描述: 产品售卖
 * 创建时间: 2018-10-18 13:44
 */
public class ProductSale extends Activity implements View.OnClickListener {
    //标题头
    private TextView tv_title;
    //产品listview
    private StickyListHeadersListView listView;
    //UI底部视图布局
    private BottomSheetLayout bottomSheetLayout;
    //购票数量
    private TextView tv_listSize;
    //购票金额
    private TextView tv_money;
    private RecyclerView rvSelected;
    //网络请求
    private HttpUtil httpUtil = new HttpUtil();
    //产品信息
    private List<ProductBean> prolist = new ArrayList<ProductBean>();
    //当前UI
    private ViewGroup anim_mask_layout;
    private Handler handler;
    //提示信息
    private String warn_info;
    //保留小数工具
    private NumberFormat nf;
    //这是一个Map
    private SparseIntArray groupSelect;
    //底部UI数量图标
    private ImageView imgCart;
    //产品价格
    private double mprice;
    //选中产品list
    private SparseArray<ProductBean> selectedList = new SparseArray<ProductBean>();
    //选中产品适配器
    private SelectAdapter selectAdapter;
    //产品适配器
    private ProductAdapter myAdapter;
    //获取产品条数
    private String pageSize = "100";
    //获取产品页数
    private String pageNo = "1";
    //底部视图
    private View bottomSheet;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(ProductSale.this, warn_info, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    setData();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sale);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.color5));
        ExitAppUtils.getInstance().addActivity(ProductSale.this);
        PublicMethod.cache = new File(Environment.getExternalStorageDirectory(), "cache");
        if (!PublicMethod.cache.exists()) {
            PublicMethod.cache.mkdirs();
        }
        tv_title = findViewById(R.id.tv_tilte);
        tv_title.setText("产品售卖");
        listView = findViewById(R.id.itemListView);
        bottomSheetLayout = findViewById(R.id.bottomSheetLayout);
        anim_mask_layout = findViewById(R.id.contant_linear);
        tv_listSize = findViewById(R.id.list_size);
        imgCart = findViewById(R.id.order_ima);
        tv_money = findViewById(R.id.money_count);
        handler = new Handler(getMainLooper());
        //返回一个数字格式的实例
        nf = NumberFormat.getCurrencyInstance();
        //保留两位有效数字
        nf.setMaximumFractionDigits(2);
        groupSelect = new SparseIntArray();
        getProducts("J12", pageNo, pageSize);
    }

    /**
     * 获取产品
     *
     * @param sale_category
     * @param pageNo
     * @param pageSize
     */
    private void getProducts(final String sale_category, final String pageNo, final String pageSize) {
        if (PublicMethod.isNetworkAvailable(ProductSale.this)) {
            warn_info = "请检查网络连接";
            mHandler.sendEmptyMessage(0);
            return;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Map modelmap = new HashMap<>();
                    modelmap.put("sale_category", sale_category);
                    modelmap.put("pageNo", pageNo);
                    modelmap.put("PageSize", pageSize);
                    Map promap = httpUtil.getProducts(modelmap);
                    Log.e("promap", "" + promap);
                    if (PublicMethod.checkIfNull(promap)) {
                        warn_info = "暂无产品信息";
                        mHandler.sendEmptyMessage(0);
                        return;
                    } else {
                        if (promap.containsKey("errcode") && "0".equals(promap.get("errcode").toString())) {
                            Map map = (Map) promap.get("data");
                            JSONArray array = (JSONArray) map.get("results");
                            if (array.size() <= 0) {
                                warn_info = "暂无产品详情";
                                mHandler.sendEmptyMessage(0);
                                return;
                            } else {
                                for (int i = 0; i < array.size(); i++) {
                                    Map data = (Map) array.get(i);
                                    ProductBean productBean = new ProductBean();
                                    productBean.ticketName = data.get("sale_name") == null ? "" : String.valueOf(data.get("sale_name"));
                                    productBean.code = data.get("sale_code") == null ? "" : String.valueOf(data.get("sale_code"));
                                    productBean.ticketNum = 0;
                                    String price = data.get("market_price") == null ? "" : String.valueOf(data.get("market_price"));
                                    double mprice = ((Double.valueOf(price)) * 0.01);
                                    productBean.ticketPrice = mprice;
                                    productBean.picturePath = data.get("logo") == null ? "" : String.valueOf(data.get("logo"));
                                    productBean.Id = i;
                                    productBean.typeId = i;
                                    productBean.typeName = "种类" + i;
                                    prolist.add(productBean);
                                    mHandler.sendEmptyMessage(1);
                                }
                            }
                        } else {
                            warn_info = promap.get("errmsg").toString();
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 添加产品适配器
     */
    private void setData() {
        myAdapter = new ProductAdapter(this, prolist, PublicMethod.cache);
        listView.setAdapter(myAdapter);
    }


    /*****************************************************设置动画效果********************************************************/
    //开始动画效果
    public void playAnimation(int[] start_location) {
        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.image_jia);
        setAnim(img, start_location);
    }

    //创建动画
    private Animation createAnim(int startX, int startY) {
        int[] des = new int[2];
        imgCart.getLocationInWindow(des);
        AnimationSet set = new AnimationSet(false);
        Animation translationX = new TranslateAnimation(0, des[0] - startX, 0, 0);
        translationX.setInterpolator(new LinearInterpolator());
        Animation translationY = new TranslateAnimation(0, 0, 0, des[1] - startY);
        translationY.setInterpolator(new AccelerateInterpolator());
        Animation alpha = new AlphaAnimation(1, 0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    //添加视图位置
    private void addViewToAnimLayout(final ViewGroup vg, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y - loc[1]);
        vg.addView(view);
    }

    //添加动画
    private void setAnim(final View v, int[] start_location) {

        addViewToAnimLayout(anim_mask_layout, v, start_location);
        Animation set = createAnim(start_location[0], start_location[1]);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim_mask_layout.removeView(v);
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(set);
    }

    /*************************************************************************************************************/
    /**
     * *******************************
     * 点击事件
     *
     * @Author lbr
     * create time 2018-10-23  9:22
     * *******************************
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.header_iv:
                if ("0".equals(PublicMethod.BACK_MARK)) {
                    startActivity(new Intent(ProductSale.this, MainActivity.class));
                } else {
                    startActivity(new Intent(ProductSale.this, AdminActivity.class));

                }
                overridePendingTransition(R.anim.scale_rotate,
                        R.anim.my_alpha_action);
                break;
            //购物车图标
            case R.id.order_ima:
                if (selectedList.size() < 1) {
                    Toast.makeText(ProductSale.this, "购物车暂无产品", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    showBottomSheet();
                }
                break;
            //购物车区域
            case R.id.linear_pay:
                if (selectedList.size() < 1) {
                    Toast.makeText(ProductSale.this, "购物车暂无产品", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    showBottomSheet();
                }
                break;
            //提交订单
            case R.id.btn_pay:
                if (selectedList.size() < 1) {
                    Toast.makeText(ProductSale.this, "购物车暂无产品", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    List<ProductBean> list = new ArrayList<ProductBean>();
                    Log.e("selec", String.valueOf(selectedList.size()));
                    for (int i = 0; i < selectedList.size(); i++) {
                        int key = selectedList.keyAt(i);
                        ProductBean productBean = selectedList.get(key);
                        Log.e("31312", productBean.ticketName);
                        list.add(productBean);
                    }
                    Log.e("list", "" + list);
                    Intent intent = new Intent(ProductSale.this, OrderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("listsize", tv_listSize.getText().toString().trim());
                    bundle.putSerializable("list", (Serializable) list);
                    bundle.putString("money", String.valueOf(mprice));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.scale_rotate,
                            R.anim.my_alpha_action);
                }
                break;
            //清空购物车图标
            case R.id.clear:
                clearCart();
                break;
            //清空购物车
            case R.id.tv_dsd:
                clearCart();
                break;

        }

    }

    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id) {
        ProductBean temp = selectedList.get(id);
        if (temp == null) {
            return 0;
        }
        return temp.ticketNum;
    }

    //清空购物车
    public void clearCart() {
        selectedList.clear();
        groupSelect.clear();
        update(true);

    }

    //添加商品
    public void add(ProductBean item, boolean refreshGoodList) {
        int groupCount = groupSelect.get(item.typeId);
        if (groupCount == 0) {
            groupSelect.append(item.typeId, 1);
        } else {
            groupSelect.append(item.typeId, ++groupCount);
        }
        ProductBean temp = selectedList.get(item.Id);
        if (temp == null) {
            item.ticketNum = 1;
            selectedList.append(item.Id, item);
        } else {
            temp.ticketNum++;
        }
        update(refreshGoodList);
    }

    //移除商品
    public void remove(ProductBean item, boolean refreshGoodList) {
        int groupCount = groupSelect.get(item.typeId);
        if (groupCount == 1) {
            groupSelect.delete(item.typeId);
        } else if (groupCount > 1) {
            groupSelect.append(item.typeId, --groupCount);
        }
        ProductBean temp = selectedList.get(item.Id);
        if (temp != null) {
            if (temp.ticketNum < 2) {
                selectedList.remove(item.Id);
            } else {
                item.ticketNum--;
            }
        }
        update(refreshGoodList);
    }

    //刷新布局 总价、购买数量等
    private void update(boolean refreshGoodList) {
        int size = selectedList.size();
        int count = 0;
        double cost = 0;
        for (int i = 0; i < size; i++) {
            ProductBean item = selectedList.valueAt(i);
            count += item.ticketNum;
            cost += item.ticketNum * item.ticketPrice;
        }
        if (count < 1) {
            tv_listSize.setVisibility(View.GONE);
        } else {
            tv_listSize.setVisibility(View.VISIBLE);
        }
        tv_listSize.setText(String.valueOf(count));
        if (cost > 0) {
            mprice = cost;
            Log.e("mprice", String.valueOf(mprice));
            tv_money.setText(nf.format(cost));
        } else {
            tv_money.setText("");
        }
        if (myAdapter != null && refreshGoodList) {
            myAdapter.notifyDataSetChanged();
        }
        if (selectAdapter != null) {
            selectAdapter.notifyDataSetChanged();
        }
        if (bottomSheetLayout.isSheetShowing() && selectedList.size() < 1) {
            bottomSheetLayout.dismissSheet();
        }
    }

    /**
     * 创建底部视窗
     *
     * @return
     */
    private View createBottomSheetView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet, (ViewGroup) getWindow().getDecorView(), false);
        rvSelected = (RecyclerView) view.findViewById(R.id.selectRecyclerView);
        rvSelected.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout clear = (LinearLayout) view.findViewById(R.id.clear);
        TextView tv_one = view.findViewById(R.id.tv_dsd);
        tv_one.setOnClickListener(this);
        clear.setOnClickListener(this);
        selectAdapter = new SelectAdapter(this, selectedList);
        rvSelected.setAdapter(selectAdapter);
        return view;
    }

    /**
     * 显示底部视窗
     */
    private void showBottomSheet() {
        if (bottomSheet == null) {
            bottomSheet = createBottomSheetView();
        }
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
        } else {
            if (selectedList.size() != 0) {
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ("0".equals(PublicMethod.BACK_MARK)) {
                startActivity(new Intent(ProductSale.this, MainActivity.class));
            } else {
                startActivity(new Intent(ProductSale.this, AdminActivity.class));

            }
            overridePendingTransition(R.anim.scale_rotate,
                    R.anim.my_alpha_action);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }
}
