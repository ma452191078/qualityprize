package com.majy.zlj.qualityprize.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class BaiduDwz {
    final static String CREATE_API = "https://dwz.cn/admin/v2/create";
    final static String TOKEN = "b21712783f46f9817c4942e5da57a6e4";

    class UrlResponse {
        @SerializedName("Code")
        private int code;

        @SerializedName("ErrMsg")
        private String errMsg;

        @SerializedName("LongUrl")
        private String longUrl;

        @SerializedName("ShortUrl")
        private String shortUrl;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
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
    public static String createShortUrl(String longUrl, String termOfValidity) {
        String params = "{\"Url\":\""+ longUrl + "\",\"TermOfValidity\":\""+ termOfValidity + "\"}";

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
            // 设置发送数据的格式");
            connection.setRequestProperty("Token", TOKEN);

            // 发起请求
            connection.connect();
            // utf-8编码
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.append(params);
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
                System.out.println(urlResponse.getErrMsg());
            }

            return "";
        } catch (IOException e) {

            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String res = createShortUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxe83ec6aeca12528c&redirect_uri=http%3A%2F%2Fweixin.shidanli.cn%3A8081%2Fzlj%2Fmobile%2Findex.html%3FgameId%3Df59707c9-8da6-4379-abfb-57822c957706&response_type=code&scope=snsapi_base#wechat_redirect","1-year");
        System.out.println(res);

    }

}