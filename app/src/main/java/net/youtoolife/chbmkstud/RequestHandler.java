package net.youtoolife.chbmkstud;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * Created by youtoolife on 4/29/18.
 */

public class RequestHandler {

    private Context context = null;

    private String url = null;
    private String result = "{\"id\":\"-1\",\"msg\":\"[]\"}";
    private Map<String, String> postData = null;


    public RequestHandler(String url, Map<String, String> map, Context context) {
        this.context = context;
        this.url = url;
        this.postData = map;
    }

    public void setSettings(String url, Map<String, String> map) {
        this.url = url;
        this.postData = map;
    }

    public void setResult(String result1) {
        result = result1;
    }

    public void request(final CallBack callBack) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RequestHandler: response:", response);
                        setResult(response);
                        callBack.callBackFunc(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null)
                            error.printStackTrace();
                        if (error != null && error.getMessage() != null) {
                            Log.d("RequestHandler: Error:", error.getMessage());
                            String result1 = null;
                            try {
                                result1 = "{\"id\":\"-2\",\"msg\":\""
                                        + Base64.encodeToString(error.getMessage().getBytes("UTF-8"), Base64.DEFAULT) + "\"}";
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            setResult(result1);

                            callBack.callBackFunc(result);
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return  postData;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack(null, getSocketFactory()));
        requestQueue.add(stringRequest);

        // Usually getting the request queue shall be in singleton like in {@see Act_SimpleRequest}
        // Current approach is used just for brevity

        //return result;
    }

}
