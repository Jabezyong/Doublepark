package project.doublepark.doublepark;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jabez on 18/3/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        updateToken(refreshedToken);
        storeToken(refreshedToken);

//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
    private void storeToken(String refreshedToken) {
        SharePrefManager.getInstance(getApplicationContext()).saveDeviceToken(refreshedToken);

    }

    private void sendRegistrationToServer(String refreshedToken) {
    }
    private void updateToken(final String token) {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebase.getReference();
        final SharePrefManager manager = SharePrefManager.getInstance(getApplicationContext());
        final String carPlate = manager.getCarPlate();
        if(carPlate == null){
            FirebaseAuth.getInstance().signOut();
            return;
        }
        saveTokenToServer(carPlate, token);
        //TO UPDATE THE Token on fireabase
        Query query = reference.child("User").orderByChild("carPlate").equalTo(carPlate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
//                if(iterator.hasNext()) {
                    DataSnapshot nodeDataSnapshot = iterator.next();
                    String key = nodeDataSnapshot.getKey();
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("token", token);
                    reference.child(path).updateChildren(result);

//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveTokenToServer(final String carplate, final String token){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoints.URL_UPDATE_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
//                            Toast.makeText(HomepageActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
//                        Toast.makeText(HomepageActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("carplate",carplate);
                params.put("token",token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
