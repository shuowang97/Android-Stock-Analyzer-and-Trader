package com.example.androidapp_hw9.util;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MyHttp {
    static RequestQueue queue;
    public static void get(String path, final Context context, Response.Listener<JSONObject> res) {
        if(queue==null){
            queue  = Volley.newRequestQueue(context);
        }

        queue.add(new JsonObjectRequest(path, res, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "access error!" + error, Toast.LENGTH_LONG).show();
            }
        }));
    }

    public static void getArray(String path, final Context context, Response.Listener<JSONArray> res) {
        if(queue==null){
            queue  = Volley.newRequestQueue(context);
        }

        queue.add(new JsonArrayRequest(path, res, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "access error!" + error, Toast.LENGTH_LONG).show();
            }
        }));
    }

//    public static String syncCall(final Context context){
//
//        String URL = "https://sw-571-hw8.azurewebsites.net/routes/lastprice/AAPL";
//        String response = new String();
//
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//
//        RequestFuture<JSONObject> future = RequestFuture.newFuture();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(), future, future);
//        requestQueue.add(request);
//
//        try {
//            response = future.get(3000, TimeUnit.MILLISECONDS).toString();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//
//        Log.d("aaaa", response);
//        return response;
//
//
//    }

}

