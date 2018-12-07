package com.sunmi.trans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author lbr
 * 功能描述:
 * 创建时间: 2018-10-19 16:21
 */
public class TransBean implements Parcelable {
    protected TransBean(Parcel in) {
    }

    public static final Creator<TransBean> CREATOR = new Creator<TransBean>() {
        @Override
        public TransBean createFromParcel(Parcel in) {
            return new TransBean(in);
        }

        @Override
        public TransBean[] newArray(int size) {
            return new TransBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
