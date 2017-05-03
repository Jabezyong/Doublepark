package project.doublepark.doublepark;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Config;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by jabez on 3/5/2017.
 */

public class SendEmail extends AsyncTask<Void,Void,Void> {
    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String message;
    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;
    boolean flag = true;
    //Class Constructor
    public SendEmail(Context context, String message){
        //Initializing variables
        this.context = context;
        this.message = message;
        progressDialog = new ProgressDialog(context);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog.setMessage("Sending message,Please wait...");
        progressDialog.show();
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        if(flag)
        Toast.makeText(context,"Message Sent",Toast.LENGTH_LONG).show();
        else{
            Toast.makeText(context,"Message Failed to Send",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.user", EndPoints.USER_EMAIL);
        props.put("mail.smtp.password", EndPoints.PASSWORD_EMAIL);
        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EndPoints.USER_EMAIL, EndPoints.PASSWORD_EMAIL);
                    }
                });
        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress("doublepark1018@gmail.com"));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress("doublepark1018@gmail.com"));
            //Adding subject
            SharePrefManager manager = SharePrefManager.getInstance(context);
            String emailFromSender = manager.getEmail();
            String carplate = manager.getCarPlate();
            mm.setSubject("Report From "+emailFromSender);
            //Adding message
            Spanned spanned = Html.fromHtml("Carplate : " + carplate + "<br/>" + "Email : " + emailFromSender + "<br/>" + "Content : " + message);
            mm.setText(spanned.toString());

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e) {
            flag = false;
            e.printStackTrace();

        }

        return null;
    }
}
