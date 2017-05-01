package project.doublepark.doublepark;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jabez on 18/3/2017.
 */

public class ReplyActionReceiver extends BroadcastReceiver {
    public static String notificationTAG = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras()!=null){
            Bundle bundle = intent.getBundleExtra(notificationTAG);
            String carplate = bundle.getString("carplate");
            String key = bundle.getString("key");

            FirebaseDatabase database  = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child(notificationTAG).child(key);
            MyFirebaseNotification noti = new MyFirebaseNotification(Tags.receiveTAG);
            DatabaseReference push = myRef.push();
            String a = push.getRef().toString();
            String b = push.getKey();
            myRef.push().setValue(noti);

        }
    }
}
