package project.doublepark.doublepark;

import java.util.Date;

/**
 * Created by jabez on 18/3/2017.
 */

public class MyFirebaseNotification {
    public String sender;
    public String sender_carplate;
    public String recipient;
    public String recipient_carplate;
    public String notificationTAG;
    public Date date;
//    public String sender;
//    public String recipient;
    public MyFirebaseNotification(){

    }
    public MyFirebaseNotification(String notificationTAG){
        this.notificationTAG = notificationTAG;
    }
    public MyFirebaseNotification(String notificationTAG,Date date){
        this.notificationTAG = notificationTAG;
    }
//    public MyFirebaseNotification(String notificationTAG,Date date,String sender,String recipient){
//        this.notificationTAG = notificationTAG;
//        this.date = date;
//        this.sender = sender;
//        this.recipient = recipient;
//    }
}
