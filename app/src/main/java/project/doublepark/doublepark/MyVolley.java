package project.doublepark.doublepark;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by jabez on 18/3/2017.
 */

public class MyVolley {
    private static MyVolley mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MyVolley(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance(Context context){
        if(mInstance == null){
            mInstance = new MyVolley(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null){
            //getApplicationContext is key,it keeps you from leaking
            //Activity or BroadCastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
}
