package project.doublepark.doublepark;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jabez on 18/3/2017.
 */

public class NotificationManager {
    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;
    public static int UNIQUE_NUMBER = 0;
    private Context mCtx;

    public NotificationManager(Context mCtx){
        this.mCtx = mCtx;
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification

    public void showBigNotification(String title,String msg,String url,String carplate,Intent intent){
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.BigPictureStyle bigPictureStyle =
                new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(msg).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.logo).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(),R.mipmap.ic_launcher))
                .setContentText(msg)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        android.app.NotificationManager notificationManager=
                (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION,notification);
    }

    public void showSmallNotification(String title,String msg,String carplate,Intent intent){
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        mCtx,
//                        ID_SMALL_NOTIFICATION,
//                        intent,
//                        PendingIntent
//                                .FLAG_UPDATE_CURRENT
//                );
        PendingIntent resultPendingIntent =
                PendingIntent.getBroadcast(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent
                                .FLAG_UPDATE_CURRENT
                );
        //default sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.logo).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(),R.mipmap.ic_launcher))
                .setContentText(msg)
                //set vibration
                .setVibrate(new long[]{1000,1000})
                .setSound(alarmSound)
                .addAction(android.R.drawable.ic_menu_send,"Reply",resultPendingIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        android.app.NotificationManager notificationManager=
                (android.app.NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION,notification);
        ++UNIQUE_NUMBER;
    }

    private Bitmap getBitmapFromURL(String strURL){
        try{
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bp = BitmapFactory.decodeStream(input);
            return bp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
