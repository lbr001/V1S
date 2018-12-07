package com.sunmi.helper.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.R;
import com.sunmi.helper.utils.AidlUtil;
import com.sunmi.helper.utils.DialogUtils;
import com.sunmi.helper.utils.PublicMethod;

import java.util.List;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:销票详情(景区门票)适配器
 * 创建时间: 2018-10-24 14:29
 */
public class CancleAdapter extends BaseAdapter {

    private Context mContext;
    private List<Map<String, Object>> mList;

    private String warn_info;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(mContext, warn_info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public CancleAdapter(Context context, List<Map<String, Object>> list) {
        this.mContext = context;
        this.mList = list;
        Log.e("mList", "" + mList);
        AidlUtil.getInstance().connectPrinterService(mContext);
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
        CancleAdapterHolder holder = new CancleAdapterHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cancle_item, null);
            holder.tv_name = convertView.findViewById(R.id.tv_cancle_name);
            holder.tv_time = convertView.findViewById(R.id.tv_cancle_time);
            holder.tv_type = convertView.findViewById(R.id.tv_cancle_type);
            holder.tv_num = convertView.findViewById(R.id.tv_cancle_count);
            holder.btn_reprinter = convertView.findViewById(R.id.btn_reprinter);
            convertView.setTag(holder);
        } else {
            holder = (CancleAdapterHolder) convertView.getTag();
        }
        holder.btn_reprinter.setTag(position);
        Map map = mList.get(position);
        holder.tv_name.setText(map.get("tkt_name") == null ? "" : String.valueOf(map.get("tkt_name")));
        holder.tv_type.setText(map.get("destorytype") == null ? "" : String.valueOf(map.get("destorytype")));
        holder.tv_time.setText(map.get("update_time") == null ? "" : String.valueOf(map.get("update_time")));
        holder.tv_num.setText("共" + map.get("num") == null ? "" : String.valueOf(map.get("num")) + "张");
        holder.btn_reprinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ("0".equals(PublicMethod.ISPRINTER.trim())) {
                    warn_info = "请先进入设置界面,启用打印机功能";
                    handler.sendEmptyMessage(0);
                    return;
                } else {
                    int mp = (Integer) v.getTag();
                    Map map1 = mList.get(mp);
                    print(map1);
                }

            }
        });
        return convertView;
    }

    /**
     * *******************************
     * 打印
     *
     * @Author lbr
     * create time 2018-10-24  14:37
     * *******************************
     */
    private void print(Map map) {
        float size = Integer.parseInt("24");
        StringBuffer sb = new StringBuffer();
        sb.append("销票凭证" + "\n");
        sb.append("*******************************" + "\n");
        AidlUtil.getInstance().printTextOne(sb.toString(), size, false, false);
        StringBuffer sb1 = new StringBuffer();
        String name = map.get("tkt_name") == null ? "" : String.valueOf(map.get("tkt_name"));
        String type = map.get("destorytype") == null ? "" : String.valueOf(map.get("destorytype"));
        String time = map.get("update_time") == null ? "" : String.valueOf(map.get("update_time"));
        String num = map.get("num") == null ? "" : String.valueOf(map.get("num"));
        sb1.append("票种名称:" + name + "\n");
        sb1.append("销票方式:" + type + "\n");
        sb1.append("销票时间:" + time + "\n");
        sb1.append("销票数量:" + num + "张" + "\n");
        Log.e("mlog", sb1.toString());
        AidlUtil.getInstance().printText(sb1.toString(), size, false, false);
    }


    class CancleAdapterHolder {
        TextView tv_name, tv_time, tv_num, tv_type;
        Button btn_reprinter;
    }
}
