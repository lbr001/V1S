package com.sunmi.helper.model;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.sunmi.helper.nfcutil.MifareBlock;
import com.sunmi.helper.nfcutil.MifareClassCard;
import com.sunmi.helper.nfcutil.MifareSector;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author lbr
 * 功能描述:
 * 创建时间: 2018-11-12 13:49
 */
public class ReadModel {
    private NfcAdapter mAdapter;// 相当于一个NFC适配器，类似于电脑装了网络适配器才能上网，手机装了NfcAdapter才能发起NFC通信。
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    long long_time;
    byte[] testByte = {0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2, 0x2,
            0x2, 0x2, 0x2, 0x2, 0x2};
    private static final String TAG = "NFCData";
    private static Context mContext;
    byte[] byteData;

    public ReadModel(Context context) {
        mContext = context;
        mAdapter = NfcAdapter.getDefaultAdapter(mContext);
    }

    // 判断系统设置是否启用NFC
    public boolean isNFCEnable() {
        if (mAdapter != null) {
            return mAdapter.isEnabled();
        } else if (mAdapter == null) {
            Toast.makeText(mContext, "设备不支持NFC功能", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!mAdapter.isEnabled()) {
            Toast.makeText(mContext, "请在系统设置里面开启NFC功能!", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        } else {
            return false;
        }
    }

    /**
     * 初始化读卡
     */
    public void deviceInit() {

        mPendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, mContext.getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter ndef1 = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter ndef2 = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[]{ndef, ndef1, ndef2};
        mTechLists = new String[][]{new String[]{MifareClassic.class
                .getName()}};
    }

    /**
     * 开始调度  写在activity  onResume中
     *
     * @param activity
     */
    public void enableDispatch(Activity activity) {
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(activity, mPendingIntent,
                    mFilters, mTechLists);
        }
    }

    /**
     * 取消调度   写在activity onPause中
     *
     * @param activity
     */
    public void disEnableDispatch(Activity activity) {
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(activity);
        }
    }

    // 读取IC卡扇区信息
    @SuppressWarnings("finally")
    public String readIcCardByBlock(Intent intent) {
        String icCardData = "";
        String action = intent.getAction();
        Log.e("action11", action);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.e("22222", tagFromIntent.toString());
            MifareClassic mfc = MifareClassic.get(tagFromIntent);
            MifareClassCard mifareClassCard = null;
            try {
                mfc.connect();
                boolean auth = false;
                int secCount = mfc.getSectorCount();// 扇区
                mifareClassCard = new MifareClassCard(secCount);
                int bCount = 0;
                int bIndex = 0;
                for (int j = 0; j < secCount; j++) {
                    MifareSector mifareSector = new MifareSector();
                    mifareSector.sectorIndex = j;
                    auth = mfc.authenticateSectorWithKeyA(j,
                            MifareClassic.KEY_DEFAULT);// 鉴权，即密码判断
                    mifareSector.authorized = auth;
                    if (auth) {// 鉴权成功
                        bCount = mfc.getBlockCountInSector(j);// 读取某扇区块数
                        bCount = Math.min(bCount, MifareSector.BLOCKCOUNT);
                        bIndex = mfc.sectorToBlock(j);
                        for (int i = 0; i < bCount; i++) {
                            byte[] data = mfc.readBlock(bIndex);
                            if (j == 12 && i == 2) {
                                try {
                                    mfc.writeBlock(bIndex, testByte);// 写数据
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    // showAlert(3, "666");
                                }
                            }
                            MifareBlock mifareBlock = new MifareBlock(data);
                            mifareBlock.blockIndex = bIndex;
                            bIndex++;
                            mifareSector.blocks[i] = mifareBlock;
                        }
                        mifareClassCard.setSector(mifareSector.sectorIndex,
                                mifareSector);
                    } else {
                        Log.e("AuthResult", "failed");
                    }
                }
                ArrayList<String> blockData = new ArrayList<String>();
                MifareSector mifareSector = mifareClassCard.getSector(2);// 指定2扇区
                MifareBlock mifareBlock = mifareSector.blocks[0];// 0块
                byteData = mifareBlock.getData();
                if (byteData != null) {
                    blockData.add("指定扇区数据: " + byteToASCII(byteData));
                    icCardData = byteToASCII(byteData);
                    return icCardData;
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } finally {
                if (mifareClassCard != null) {
                    mifareClassCard.debugPrint();
                }
                return icCardData;
            }
        }
        return icCardData;
    }

    // 将数组转换为ASCII码
    public static String byteToASCII(byte[] byteData) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append((char) byteData[i]);
        }
        return sb.toString();
    }

}
