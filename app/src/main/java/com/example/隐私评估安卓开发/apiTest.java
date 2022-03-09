package com.example.隐私评估安卓开发;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class apiTest {
    public static String BASE_URL = "https://www.tianqiapi.com/free/day";
    public static String APP_ID = "76389821";
    public static String APP_SECRET = "9esSbUJz";


    public static String doGET(String url){
        String result = "";
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            // 建立连接
            HttpURLConnection httpURLConnection = null;
            URL requestUrl = new URL(url);
            httpURLConnection = (HttpURLConnection)requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();

            // 获取二进制流
            InputStream inputStream = httpURLConnection.getInputStream();

            // 将二进制流包装
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // 从BufferedReader中读取string字符串
            String line;
            StringBuilder builder = new StringBuilder();

            while((line = reader.readLine()) != null){
                builder.append(line);
                builder.append("\n");
            }

            if(builder.length() == 0){
                return  null;
            }

            result = builder.toString();

        } catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public static String getWeatherOfCity(String city){
        String weatherUrl = BASE_URL+"?"+"appid="+APP_ID+"&"+"appsecret="+APP_SECRET+"&city="+city;
        Log.d("fan","----weatherUrl----"+weatherUrl);

        String weatherResult = doGET(weatherUrl);
        return weatherResult;
    }


}
