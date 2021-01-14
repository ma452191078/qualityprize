package com.majy.zlj.qualityprize.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 百度短网址
 * @Author majingyuan
 * @Date Create in 2020/3/12 15:32
 */
public class StanleyDwz {
    final static String CREATE_API = "http://dw.shidanli.cn/dwzapi/create";
    final static String TOKEN = "123456";


    class UrlResponse {
        @SerializedName("code")
        private int code;

        @SerializedName("msg")
        private String msg;

        @SerializedName("longUrl")
        private String longUrl;

        @SerializedName("shortUrl")
        private String shortUrl;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getLongUrl() {
            return longUrl;
        }

        public void setLongUrl(String longUrl) {
            this.longUrl = longUrl;
        }

        public String getShortUrl() {
            return shortUrl;
        }

        public void setShortUrl(String shortUrl) {
            this.shortUrl = shortUrl;
        }
    }

    /**
     * 创建短网址
     *
     * @param longUrl
     *            长网址：即原网址
     *        termOfValidity
     *            有效期：默认值为long-term, 长期long-term,1年：1-year
     * @return  成功：短网址
     *          失败：返回空字符串
     */
    public static String createShortUrl(String longUrl) {
        String params = "{\"longUrl\":\""+ longUrl + "\",\"token\":\""+ TOKEN + "\"}";

        BufferedReader reader = null;
        try {
            // 创建连接
            URL url = new URL(CREATE_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置发送数据的格式
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("CharSet", "UTF-8");
            // 发起请求
            connection.connect();
            // utf-8编码
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            out.flush();
            out.close();

            // 读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();

            // 抽取生成短网址
            UrlResponse urlResponse = new Gson().fromJson(res, UrlResponse.class);
            if (urlResponse.getCode() == 0) {
                return urlResponse.getShortUrl();
            } else {
                System.out.println(urlResponse.getMsg());
            }

            return "";
        } catch (IOException e) {

            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String res = createShortUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe83ec6aeca12528c&redirect_uri=http%3A%2F%2Fweixin.shidanli.cn%3A8081%2Fzlj%2Fmobile%2Findex.html%3FgameId%3Dd9b57402-35c4-4fc6-82a3-43ceddc08954&response_type=code&scope=snsapi_base#wechat_redirect");
        System.out.println(res);

    }

}