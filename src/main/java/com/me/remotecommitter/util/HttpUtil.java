package com.me.remotecommitter.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpUtil {

    private static OkHttpClient client;

    private HttpUtil() {

    }

    public static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (HttpUtil.class) {
                if (client == null) {
                    client = new OkHttpClient();
                }
            }
        }
        return client;
    }

    public static void get(String url, String json) {
        String encodeJson = "";
        try {
            encodeJson = URLEncoder.encode(json, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url = url + "?param=" + encodeJson;
        Request request = new Request.Builder()
                .url(url).get().build();
        Call call = HttpUtil.getInstance().newCall(request);
        Response response = null;
        long start = System.currentTimeMillis();
        try {
            response = call.execute();
            long end = System.currentTimeMillis();
            System.out.println(response.body().string() + " used:" + (end - start) + " ms");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("发送失败...检查网络地址...");

        }


    }


    public static void post(String url, String json) {
        System.out.println(json);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody) //post请求
                .build();
        Call call = HttpUtil.getInstance().newCall(request);
        Response response = null;
        long start = System.currentTimeMillis();
        try {
            response = call.execute();
            long end = System.currentTimeMillis();
            System.out.println(response.body().string() + " used:" + (end - start) + " ms");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("发送失败...检查网络地址...");

        }

    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "huangwuyi");
        jsonObject.put("sex", "男");
        jsonObject.put("QQ", "9999999");
        jsonObject.put("Min.score", new Integer(99));
        jsonObject.put("nickname", "梦中心境");
        try {
            String jsonString = JSON.toJSONString(jsonObject);
            HttpUtil.post("http://localhost:8266/spark-submit", jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("远程提交失败");
        }
    }
}
