package com.sunmi.helper.net;

import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sunmi.helper.utils.MD5;
import com.sunmi.helper.utils.PublicMethod;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:网络请求工具类
 * 创建时间: 2018-10-17 16:42
 */
public class HttpUtil {
    private HttpClient mHttpClient = new HttpClient();

    /**
     * 获取系统名称
     *
     * @param para
     * @return
     */
    public Map<String, Object> getSystemName(Map<String, Object> para) {
        Map<String, Object> systemname = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "tktapi/sc/queryService/login");
        NameValuePair device = new NameValuePair("device", para.get("device").toString());
        NameValuePair password = new NameValuePair("password", para.get("password").toString());
        get.setQueryString(new NameValuePair[]{device, password});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String Info = String.valueOf(get.getResponseBodyAsString());
                systemname = JSON.parseObject(Info, Map.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return systemname;
    }

    /**
     * 获取设置信息
     *
     * @param para
     * @return
     */
    public Map<String, Object> getPrinterSetting(Map<String, Object> para) {
        Map<String, Object> hismap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/us/tc/device/infoByCode");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                hismap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hismap;
    }

    /**
     * 设备码登录
     *
     * @param params
     * @return
     */
    public Map<String, Object> DeviceCodeLogin(Map<String, Object> params) {
        Map loginmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "tktapi/sc/queryService/login");
        NameValuePair device = new NameValuePair("device", params.get("device").toString());
        NameValuePair password = new NameValuePair("password", params.get("password").toString());
        get.setQueryString(new NameValuePair[]{device, password});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                loginmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginmap;
    }

    /**
     * 账号登录
     *
     * @param params
     * @return
     */
    public Map<String, Object> login(Map<String, Object> params) {
        Map loginmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "manager/ajaxlogin");
        NameValuePair username = new NameValuePair("username", params.get("username").toString());
        NameValuePair password = new NameValuePair("password", params.get("password").toString());
        get.setQueryString(new NameValuePair[]{username, password});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                loginmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginmap;
    }

    /**
     * 营销统计详情
     *
     * @param para
     * @return
     */
    public Map<String, Object> getMarketInfo(Map<String, Object> para) {
        Map<String, Object> marketmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/viewCountList");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                marketmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return marketmap;
    }

    /**
     * 获取昨天今天的销售核销数量
     *
     * @param para
     * @return
     */
    public Map<String, Object> getSaleInfo(Map<String, Object> para) {
        Map<String, Object> marketmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/getCountForDevice");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                marketmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return marketmap;
    }

    /**
     * 上传系统设置数据
     *
     * @param para
     * @return
     */
    public Map<String, Object> uploadSettingToServer(Map<String, Object> para) {
        Map<String, Object> marketmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/as/tc/device/update");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                marketmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return marketmap;
    }

    /*
     * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
     * 这里的path是图片的地址
     */
    public Uri getImageURI(String path, File cache) throws Exception {
        String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    /**
     * 获取产品列表
     *
     * @param para
     * @return
     */
    public Map<String, Object> getProducts(Map<String, Object> para) {
        Map<String, Object> promap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/appTicketPlaceService/findSaleDiscountPriceList");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                Log.e("proInfo", proInfo);
                promap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return promap;
    }

    /**
     * 下单
     *
     * @param para
     * @return
     */
    public Map<String, Object> getOrder(Map<String, Object> para) {
        Map ordermap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/createOrderMarket");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                ordermap = JSON.parseObject(proInfo, Map.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("exception", e.toString());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ordermap;
    }

    /**
     * 出票
     *
     * @param para
     * @return
     */
    public Map<String, Object> getTicketResult(Map<String, Object> para) {
        Map<String, Object> ticketmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/createTicketForOtherPay");
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair order_code = new NameValuePair("order_code", para.get("order_code").toString().trim());
        NameValuePair auto_destory = new NameValuePair("auto_destory", para.get("auto_destory").toString().trim());
        NameValuePair device_code = new NameValuePair("device_code", para.get("device_code").toString().trim());
        get.setQueryString(new NameValuePair[]{order_code, auto_destory, device_code});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticketmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.e("exception1", e.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticketmap;
    }

    /**
     * 获取设备核销门票总数量
     *
     * @param para
     * @return
     */
    public Map<String, Object> getDestoryCount(Map<String, Object> para) {
        Map<String, Object> marketmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/uc/tc/equipmentProductService/getUsedCount");
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                marketmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return marketmap;
    }

    /**
     * 验票（票详情）
     *
     * @param para
     * @return
     */
    public Map<String, Object> TicketInfo(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "tktapi/sc/queryService/" + para.get("way").toString().trim());
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        Map<String, Object> postMap = new HashMap<String, Object>();
        if ("byID".equals(para.get("way"))) {
            postMap.put("name", para.get("name").toString().trim());
            postMap.put("address", para.get("address").toString().trim());
            postMap.put(para.get("arg").toString().trim(), para.get("code").toString().trim());
            postMap.put("device", PublicMethod.SERIZLNUMBER);
        } else {
            postMap.put(para.get("arg").toString().trim(), para.get("code").toString().trim());
            postMap.put("device", PublicMethod.SERIZLNUMBER);
        }
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(postMap), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String loginInfo = String.valueOf(post.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 销票
     *
     * @param para
     * @return
     */
    public Map<String, Object> VenCheckTicket(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "tktapi/sc/destoryService/" + para.get("way").toString().trim());
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        if ("byCard".equals(para.get("method").toString()) || "byID".equals(para.get("method"))) {
            NameValuePair code = new NameValuePair(para.get("arg").toString().trim(), para.get("trim").toString());
            NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
            NameValuePair reqkey = new NameValuePair("reqkey", para.get("reqkey").toString());
            NameValuePair num = new NameValuePair("num", para.get("num").toString());
            NameValuePair type = new NameValuePair("type", para.get("type").toString());
            NameValuePair type_attr = new NameValuePair("type_attr", para.get("type_attr").toString());
            NameValuePair goods_code = new NameValuePair("goods_code", para.get("goods_code").toString());
            get.setQueryString(new NameValuePair[]{code, device, reqkey, type, goods_code, type_attr, num});
        } else {
            NameValuePair code = new NameValuePair(para.get("arg").toString().trim(), para.get("trim").toString().trim());
            NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
            NameValuePair reqkey = new NameValuePair("reqkey", para.get("reqkey").toString().trim());
            NameValuePair num = new NameValuePair("num", para.get("num").toString().trim());
            get.setQueryString(new NameValuePair[]{code, device, reqkey, num});
        }
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 销票详情
     *
     * @param para
     * @return
     */
    public Map<String, Object> cancleInfo(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/us/tc/devicequerycount/receliptlist");
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
        NameValuePair pageNo = new NameValuePair("pageNo", para.get("pageNo").toString().trim());
        NameValuePair pageSize = new NameValuePair("pageSize", para.get("pageSize").toString().trim());
        get.setQueryString(new NameValuePair[]{device, pageNo, pageSize});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 团队统计
     *
     * @param para
     * @return
     */
    public Map<String, Object> grouptotalBytypeList(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/us/tc/devicequerycount/grouptotalbytpyelist");
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
        NameValuePair view_code = new NameValuePair("view_code", para.get("view_code").toString().trim());
        get.setQueryString(new NameValuePair[]{device, view_code});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 团队统计item详情
     *
     * @param para
     * @return
     */
    public Map<String, Object> groupListInfo(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/us/tc/devicequerycount/grouptotalbytpyeinfolist");
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair view_code = new NameValuePair("view_code", PublicMethod.SPOT_VIEW);
        NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
        NameValuePair goods_code = new NameValuePair("goods_code", para.get("goods_code").toString().trim());
        NameValuePair ticket_type = new NameValuePair("ticket_type", para.get("ticket_type").toString().trim());
        get.setQueryString(new NameValuePair[]{device, goods_code, ticket_type, view_code});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 自驾统计
     *
     * @param para
     * @return
     */
    public Map<String, Object> drivertotalBytypeList(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/us/tc/devicequerycount/destorytotalbytypelist");
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
        NameValuePair view = new NameValuePair("view", para.get("view").toString().trim());
        get.setQueryString(new NameValuePair[]{device, view});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            String loginInfo = String.valueOf(get.getResponseBodyAsString());
            ticmap = JSON.parseObject(loginInfo, Map.class);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 自驾Item详情
     *
     * @param para
     * @return
     */
    public Map<String, Object> driverItemInfo(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/us/tc/devicequerycount/destorytotalbytypeinfolist");
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair device = new NameValuePair("device", PublicMethod.SERIZLNUMBER);
        NameValuePair view = new NameValuePair("view", PublicMethod.SPOT_VIEW.trim());
        NameValuePair goods_code = new NameValuePair("goods_code", para.get("goods_code").toString().trim());
        NameValuePair type = new NameValuePair("type", para.get("type").toString());
        get.setQueryString(new NameValuePair[]{device, view, goods_code, type});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 团队预定
     *
     * @param para
     * @return
     */
    public Map<String, Object> TeamBooking(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        GetMethod get = new GetMethod(PublicMethod.BASE_URL + "api/us/tc/devicequerycount/" + para.get("type").toString().toString());
        get.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        NameValuePair view_code = new NameValuePair("view_code", PublicMethod.SPOT_VIEW.trim());
        get.setQueryString(new NameValuePair[]{view_code});
        try {
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(get);
            try {
                String loginInfo = String.valueOf(get.getResponseBodyAsString());
                ticmap = JSON.parseObject(loginInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 历史查询详情
     *
     * @param para
     * @return
     */
    public Map<String, Object> getHistoryInfo(Map<String, Object> para) {
        Map<String, Object> hismap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/getOrderList");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                hismap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hismap;
    }

    /**
     * 重新打印门票
     *
     * @param para
     * @return
     */
    public Map<String, Object> getOrderInfo(Map<String, Object> para) {
        Map<String, Object> marketmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/getOrderInfoByOrderCode");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                marketmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return marketmap;
    }

    /**
     * 查询退票详情
     *
     * @param para
     * @return
     */
    public Map<String, Object> checkTicket(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/getOrderByUniqueCode");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                ticmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 退票
     *
     * @param para
     * @return
     */
    public Map<String, Object> RefundTicket(Map<String, Object> para) {
        Map<String, Object> ticmap = null;
        PostMethod post = new PostMethod(PublicMethod.BASE_URL + "api/ac/tc/equipmentProductService/createBackOrder");
        post.addRequestHeader("Cookie", PublicMethod.LOGINCOOKIE);
        try {
            RequestEntity re = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            post.setRequestEntity(re);
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(post);
            try {
                String proInfo = post.getResponseBodyAsString();
                ticmap = JSON.parseObject(proInfo, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ticmap;
    }

    /**
     * 获取团信息
     *
     * @author lbr
     * @date 2018年2月8日 下午1:45:20
     * @descript:
     */
    public Map<String, Object> getTourInfo(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(PublicMethod.BASE_URL + "api/us/tc/grouporder/groupForViewList");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            try {
                String order = postMethod.getResponseBodyAsString();
                Log.e("cardInfo", order);
                map = JSON.parseObject(order, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 招徕奖励实名制列表
     *
     * @param para
     * @return
     */
    public Map<String, Object> getListInfo(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(PublicMethod.BASE_URL + "api/us/tc/grouporder/getVerifyList");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            try {
                String order = postMethod.getResponseBodyAsString();
                Log.e("cardInfo", order);
                map = JSON.parseObject(order, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 根据卡内号获取IC卡号
     *
     * @param para
     * @return
     */
    public Map<String, Object> getCardNum(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(
                PublicMethod.BASE_URL + "api/uc/cdc/userActiveService/getCardInfoByInside");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            String order = postMethod.getResponseBodyAsString();
            map = JSON.parseObject(order, Map.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 激活IC卡
     *
     * @param para
     * @return
     */
    public Map<String, Object> ActiveCard(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(
                PublicMethod.BASE_URL + "api/uc/cdc/userActiveService/insertActiveLimit");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            String order = postMethod.getResponseBodyAsString();
            map = JSON.parseObject(order, Map.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 领取补贴
     *
     * @author lbr
     * @date 2017年12月28日 下午3:54:25
     * @descript:
     */
    public Map<String, Object> getCouOrder(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(
                PublicMethod.BASE_URL + "api/uc/puc/couponOrderService/createCouOrderRealtimePiaoji");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            String order = postMethod.getResponseBodyAsString();
            map = JSON.parseObject(order, Map.class);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取补贴详情
     *
     * @author lbr
     * @date 2017年12月28日 下午3:39:03
     * @descript:
     */
    public Map<String, Object> getLQinfo(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(PublicMethod.BASE_URL + "api/us/puc/couponorder/getorderinfo");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            try {
                String order = postMethod.getResponseBodyAsString();
                Log.e("order", order);
                map = JSON.parseObject(order, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 实名制认证
     *
     * @param para
     * @return
     */
    public Map<String, Object> getCeInfo(Map<String, Object> para) {
        Map<String, Object> map = null;
        PostMethod postMethod = new PostMethod(PublicMethod.BASE_URL + "api/uc/uc/userService/insertUserAuthPJ");
        RequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(JSON.toJSONString(para), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            // 设置网络延时
            mHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            mHttpClient.executeMethod(postMethod);
            try {
                String order = postMethod.getResponseBodyAsString();
                Log.e("cardInfo", order);
                map = JSON.parseObject(order, Map.class);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
