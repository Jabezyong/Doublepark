package project.doublepark.doublepark;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ReportActivity extends AppCompatActivity {
    EditText etContent;
    Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        etContent = (EditText) findViewById(R.id.editTextReport);
        btnSend = (Button) findViewById(R.id.button_send_report);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        setupFloatingButton();
    }

    public void setupFloatingButton() {
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.ic_car);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setPosition(6)
                .setContentView(icon)
                .build();

        //Set the size of the icon
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(120,120);

        // First SubActionButton (Profile)
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        //set te size of first icon
        itemBuilder.setLayoutParams(params);
        // repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.ic_profile);
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();

        // Start the activity after the first icon is being tap
        itemIcon.setClickable(true);
        itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this,ProfileActivity.class));
            }
        });

        // Second SubActionButton (Notification)
        SubActionButton.Builder itemBuilderSecond = new SubActionButton.Builder(this);
        //set te size of second icon
        itemBuilderSecond.setLayoutParams(params);
        // repeat many times:
        ImageView itemIconSecond = new ImageView(this);
        itemIconSecond.setImageResource(R.drawable.ic_notifications_button);
        SubActionButton button2 = itemBuilderSecond.setContentView(itemIconSecond).build();
        itemIconSecond.setClickable(true);
        itemIconSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReportActivity.this,HomepageActivity.class));
            }
        });


        // Third SubActionButton (Report/Suggestion)
        SubActionButton.Builder itemBuilderThird = new SubActionButton.Builder(this);
        //set te size of third icon
        itemBuilderThird.setLayoutParams(params);
        // repeat many times:
        ImageView itemIconThird = new ImageView(this);
        itemIconThird.setImageResource(R.drawable.ic_report);
        SubActionButton button3 = itemBuilderThird.setContentView(itemIconThird).build();
        itemIconThird.setClickable(true);
        itemIconThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Fourth SubActionButton (About Us)
        SubActionButton.Builder itemBuilderForth = new SubActionButton.Builder(this);
        //set te size of fourth icon
        itemBuilderForth.setLayoutParams(params);
        // repeat many times:
        ImageView itemIconForth = new ImageView(this);
        itemIconForth.setImageResource(R.drawable.ic_aboutus_button);
        SubActionButton button4 = itemBuilderForth.setContentView(itemIconForth).build();
        itemIconForth.setClickable(true);
        itemIconForth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReportActivity.this,AboutUsActivity.class));
            }
        });



        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .setStartAngle(270)
                .setEndAngle(360)
                .attachTo(actionButton)
                .build();
    }

    public void sendEmail() {
        String content = etContent.toString().trim();
//        SharePrefManager manager = SharePrefManager.getInstance(getApplicationContext());
//        String emailFromSender = manager.getEmail();
//        String carplate = manager.getCarPlate();
//        //Adding message
//        Spanned spanned = Html.fromHtml("Carplate : " + carplate + "<br/>" + "Email : " + emailFromSender + "<br/>" + "Content : " + content);
//        GMailSender sender = new GMailSender(EndPoints.USER_EMAIL, EndPoints.PASSWORD_EMAIL);
//        try {
//            sender.sendMail("Report From "+emailFromSender,
//                    spanned.toString(),
//                    EndPoints.USER_EMAIL,
//                    "doublepark1018@gmail.com");
//        } catch (Exception e) {
//            Log.e("SendMail", e.getMessage(), e);
//        }
        SendEmail email = new SendEmail(ReportActivity.this,content);
        email.execute();
    }


}
