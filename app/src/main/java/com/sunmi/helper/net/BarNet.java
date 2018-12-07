package com.sunmi.helper.net;

import com.sunmi.helper.utils.PublicMethod;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author lbr
 * 功能描述:扫码支付网络请求
 * 创建时间: 2018-10-23 10:15
 */
public class BarNet {


    /**
     * 扫码支付
     *
     * @param params
     * @return
     */
    public String sendSSLPostRequest(Map<String, String> params) {
        String result = "";
        Map map = null;
        HttpClient httpClient = new HttpClient();
        PostMethod method = new PostMethod(PublicMethod.Bar_URL + "Api/Api/payBar");
        Part[] parts = new Part[params.size()];
        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            parts[i++] = new StringPart(entry.getKey(), entry.getValue());
        }
        method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        try {
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }
            InputStream responseBody = method.getResponseBodyAsStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(responseBody));
            StringBuffer stringBuffer = new StringBuffer();

            while ((result = br.readLine()) != null) {
                stringBuffer.append(result);
            }
            result = stringBuffer.toString();
            System.out.println(result);
        } catch (HttpException e) {
            System.out.println("222" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("111" + e.getMessage());
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return result;
    }
}
