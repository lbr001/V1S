package com.sunmi.helper.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.R;

/**
 * @Author lbr
 * 功能描述:
 * 创建时间: 2018-11-22 15:47
 */
public class DialogUtils {

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);//得到加载view
        //加载布局
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_loading_view);
        //提示文字
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        //设置加载信息
        tipTextView.setText(msg);
        //创建自定义样式Dialog
        Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);
        //是否可以按“返回键”消失
        loadingDialog.setCancelable(true);
        //点击加载框以外的区域是否可以消失
        loadingDialog.setCanceledOnTouchOutside(true);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));//设置布局
        /**
         *将显示dialog的方法封装在这里面
         */
        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
        loadingDialog.show();
        return loadingDialog;
    }

    /**
     * 关闭dialog
     *
     * @param mDialogUtils
     */
    public static void closeDialog(Dialog mDialogUtils) {
        if (mDialogUtils != null && mDialogUtils.isShowing()) {
            mDialogUtils.cancel();
        }
    }
}
