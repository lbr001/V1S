package com.sunmi.helper.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


import com.sunmi.helper.net.HttpUtil;

import java.io.File;

/**
 * Created by Adminstrator on 2018-4-3.
 * 产品售卖加载网络图片工具类
 */

public class ImageUtil {


    private File cache;

    public ImageUtil(File cache) {
        this.cache = cache;
    }

    /**
     * 异步下载产品图片
     *
     * @param mImageView
     * @param picturePath
     */
    public void asyncloadImage(ImageView mImageView, String picturePath) {
        Log.e("async", "async");
        HttpUtil httpUtil = new HttpUtil();
        AsyncImageTask task = new AsyncImageTask(mImageView, httpUtil);
        task.execute(picturePath);

    }

    /**
     * 异步任务
     */
    public final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {
        private ImageView imageView;
        private HttpUtil httpUtil;

        public AsyncImageTask(ImageView mImageView, HttpUtil httpUtil) {
            this.imageView = mImageView;
            this.httpUtil = httpUtil;
        }


        @Override
        protected Uri doInBackground(String... strings) {
            try {
                return httpUtil.getImageURI(strings[0], cache);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            //完成图片绑定
            if (imageView != null && uri != null) {
                imageView.setImageURI(uri);
            }
        }
    }
}
