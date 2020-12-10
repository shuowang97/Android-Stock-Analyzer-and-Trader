package com.example.androidapp_hw9.util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class GsonParser {

    /**
     * 解析没有数据头的纯数组
     */
    public static <T> void parseNoHeaderJSONArray(List<T> objBeanList, JSONArray jsonArray, Class cls) {

        Gson gson = new Gson();

//        Log.d("finish", jsonArray.toString());

        //加强for循环遍历JsonArray
        for (int i = 0; i < jsonArray.length(); i++) {
            Object jsonObject = null;
            try {
                jsonObject = jsonArray.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //使用GSON，直接转成Bean对象
//            AutoSuggestEntity autoSuggestEntity = gson.fromJson(jsonObject.toString(), AutoSuggestEntity.class);

            T objBean = (T) gson.fromJson(jsonObject.toString(), cls);
            objBeanList.add(objBean);

//            Log.d("finish", objBean.toString());
        }
    }
}
