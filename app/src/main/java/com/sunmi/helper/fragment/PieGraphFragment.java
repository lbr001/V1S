package com.sunmi.helper.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.sunmi.R;
import com.sunmi.helper.utils.ColorUtil;
import com.sunmi.helper.utils.PublicMethod;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:
 * 创建时间: 2018-10-25 15:22
 */
public class PieGraphFragment extends Fragment {
    //绘图工具
    private PieChart mpiechart;
    //提示信息
    private String warn_info;
    //数据list
    private List<Map<String, Object>> colorList = new ArrayList<Map<String, Object>>();
    //颜色工具
    private ColorUtil colorUtil = new ColorUtil();
    //布局文件
    private LinearLayout linearLayout;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    linearLayout.setVisibility(View.VISIBLE);
                    mpiechart.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie, null);
        mpiechart = view.findViewById(R.id.mpiechart);
        linearLayout = view.findViewById(R.id.market_linear_two);
        colorList = colorUtil.initColor();
        return view;
    }

    public void setvalues(int count, List<Map<String, Object>> list) {
        if (list.size() < 1) {
            handler.sendEmptyMessage(0);
            return;
        } else {
            mpiechart.setVisibility(View.VISIBLE);
            PieData mPieData = getPieData(list, count);
            showChart(mpiechart, mPieData);
        }
    }

    private void showChart(PieChart pieChart, PieData pieData) {
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(30f); //半径
        pieChart.setTransparentCircleRadius(40f); // 半透明圈
        pieChart.setExtraOffsets(20, 0, 0, 0);
        pieChart.setDescription("");
//        //设置底部文字大小
//        pieChart.setDescriptionTextSize(20f);
        pieChart.setDrawCenterText(true); //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);       //设置piecahrt图表点击Item高亮是否可用
        pieChart.setRotationAngle(90); // 初始旋转角度
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setUsePercentValues(true); //显示成百分比
        pieChart.setCenterText("销售数据百分比"); //饼状图中间的文字
        pieChart.setCenterTextSize(10f);
//设置数据
        pieChart.setData(pieData);
        Legend mLegend = pieChart.getLegend(); //设置比例图
        mLegend.setPosition(Legend.LegendPosition.LEFT_OF_CHART); //最右边显示
// mLegend.setForm(LegendForm.LINE); //设置比例图的形状，默认是方形
        mLegend.setFormSize(10);
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);
        pieChart.animateXY(1000, 1000); //设置动画
    }

//    private PieData getPieData(int count, float range) {
//        ArrayList<String> xValues = new ArrayList<String>();//xVals用来表示每个饼块上的内容
//        for (int i = 0; i < count; i++) {
//            xValues.add("Quarterly" + (i + 1)); //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
//        }
//        ArrayList<Entry> yValues = new ArrayList<Entry>(); //yVals用来表示封装每个饼块的实际数据
//        // 饼图数据
///**
// * 将一个饼形图分成四部分，四部分的数值比例为14:14:34:38
// * 所以 14代表的百分比就是14%
// */
//        float quarterly1 = 14;
//        float quarterly2 = 14;
//        float quarterly3 = 34;
//        float quarterly4 = 38;
//        yValues.add(new Entry(quarterly1, 0));
//        yValues.add(new Entry(quarterly2, 1));
//        yValues.add(new Entry(quarterly3, 2));
//        yValues.add(new Entry(quarterly4, 3));
//        //y轴的集合
//        PieDataSet pieDataSet = new PieDataSet(yValues, "销售历史查询数据"/*显示在比例图上*/);
//        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
//        ArrayList<Integer> colors = new ArrayList<Integer>();
//// 饼图颜色
//        colors.add(Color.rgb(205, 205, 205));
//        colors.add(Color.rgb(114, 188, 223));
//        colors.add(Color.rgb(255, 123, 124));
//        colors.add(Color.rgb(57, 135, 200));
//        pieDataSet.setColors(colors);
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        float px = 5 * (metrics.densityDpi / 160f);
//        pieDataSet.setSelectionShift(px); // 选中态多出的长度
//        PieData pieData = new PieData(xValues, pieDataSet);
//        //设置圆环上显示字体的大小
//        pieData.setValueTextSize(10f);
//        return pieData;
//    }

    private PieData getPieData(List<Map<String, Object>> list, int num) {
        ArrayList<String> xValues = new ArrayList<String>();//xVals用来表示每个饼块上的内容
        for (int i = 0; i < list.size(); i++) {
            xValues.add(list.get(i).get("title").toString()); //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
        }
        ArrayList<Entry> yValues = new ArrayList<Entry>(); //yVals用来表示封装每个饼块的实际数据
        // 饼图数据
/**
 * 将一个饼形图分成四部分，四部分的数值比例为14:14:34:38
 * 所以 14代表的百分比就是14%
 */
        for (int i = 0; i < list.size(); i++) {
            Map map = list.get(i);
            double quarterly = Double.valueOf(map.get("num").toString());
            double one = ((quarterly) / num);
            DecimalFormat decimalFormat = new DecimalFormat("#####0.00");
            String m1 = decimalFormat.format(one);
            double m2 = Double.parseDouble(m1);
            double m3 = (m2 * 100);
            float f = (float) m3;
            yValues.add(new Entry(f, i));
        }

        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "销售历史查询数据"/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离
        ArrayList<Integer> colors = new ArrayList<Integer>();
// 饼图颜色
        for (int i = 0; i < list.size(); i++) {
            int index = (int) (Math.random() * 19);
            Map map = colorList.get(index);
            Log.e("ds", "" + map);
            if (PublicMethod.checkIfNull(map)) {
                int n = (80 + i + 1);
                int n1 = (198 + i + 1);
                int n2 = (216 + i + 1);
                colors.add(Color.rgb(n, n1, n2));
            } else {
                int n = Integer.parseInt(map.get("R").toString().trim());
                int n1 = Integer.parseInt(map.get("G").toString().trim());
                int n2 = Integer.parseInt(map.get("B").toString().trim());
                colors.add(Color.rgb(n, n1, n2));
            }

        }
//        colors.add(Color.rgb(205, 205, 205));
//        colors.add(Color.rgb(114, 188, 223));
//        colors.add(Color.rgb(255, 123, 124));
//        colors.add(Color.rgb(57, 135, 200));
        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度
        PieData pieData = new PieData(xValues, pieDataSet);
        //设置圆环上显示字体的大小
        pieData.setValueTextSize(10f);
        return pieData;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}
