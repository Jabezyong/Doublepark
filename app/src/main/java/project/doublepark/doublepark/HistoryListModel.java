package project.doublepark.doublepark;

import java.util.Date;

/**
 * Created by jabez on 18/4/2017.
 */

public class HistoryListModel {

    private String dateString;
    private String sender_carplate;
    private String recipient_carplate;
    private String key;
    private Date date;
    private boolean isSender = false;
    public HistoryListModel() {
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getSender_carplate() {
        return sender_carplate;
    }

    public void setSender_carplate(String sender_carplate) {
        this.sender_carplate = sender_carplate;
    }

    public String getRecipient_carplate() {
        return recipient_carplate;
    }

    public void setRecipient_carplate(String recipient_carplate) {
        this.recipient_carplate = recipient_carplate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }
}
