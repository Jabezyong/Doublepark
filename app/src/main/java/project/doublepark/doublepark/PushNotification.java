package project.doublepark.doublepark;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jabez on 18/3/2017.
 */

public class PushNotification {

    public static StringRequest sendPush(final String carplate,final String key,final Context context) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "Notification is sent", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", "Double park");
                params.put("message", "Your car is blocking.");
                params.put("email", carplate);
                params.put("key", key);
                return params;
            }
        };
        return stringRequest;
    }
}
