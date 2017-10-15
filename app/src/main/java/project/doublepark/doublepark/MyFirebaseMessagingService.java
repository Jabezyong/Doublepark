package project.doublepark.doublepark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static project.doublepark.doublepark.ReplyActionReceiver.notificationTAG;


/**
 * Created by jabez on 18/3/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getData().size() > 0){
            Log.e(TAG,"Data Payload : "+remoteMessage.getData().toString());
            try{
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            }catch (Exception e){
                Log.e(TAG,"Exception : "+e.getMessage());
            }
        }
    }
    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        Log.e(TAG,"Notification JSON "+json.toString());
        try{
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");
            String carplate = data.getString("carplate");
            String key = data.getString("key");
            //creating MyNotificationManager object
            NotificationManager manager = new NotificationManager(getApplication());

            //creating an intent for the notification
            Intent intent = new Intent(getApplicationContext(),ReplyActionReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putString("carplate",carplate);
            bundle.putString("key",key);
            intent.putExtra(notificationTAG,bundle);
            //if there is no imaage
            if(imageUrl.equals("null")){
                //display small notification
                manager.showSmallNotification(title,message,carplate,intent);
            }else{
                //if there is a image
                //then show big notification
                manager.showBigNotification(title,message,imageUrl,carplate,intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            Log.e(TAG,"Exception: " +e.getMessage());
        }
    }
}
