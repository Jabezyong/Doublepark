package project.doublepark.doublepark;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        setupFontForTitle();
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
                startActivity(new Intent(ReportActivity.this,HistoryActivity.class));
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
        String content = etContent.getText().toString().trim();
       //Using intent first

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        String emailList[] = {"doubleparkingapp@gmail.com"};

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Double Park App Feedback");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);

        startActivity(emailIntent);


    }
    private void setupFontForTitle() {
        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(R.string.report_title);
        tv.setTextSize(28);
        tv.setTextColor(Color.parseColor("#FFFFFF"));

        Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getResources().getAssets(),"Spork.ttf");
        tv.setTypeface(typeFace);
        SpannableString s = new SpannableString("Double Park");
        s.setSpan(typeFace, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
    }
    @Override
    public void onBackPressed() {
        //Override to re-direct them back to the homepage activity
        Intent intent = new Intent(ReportActivity.this, HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
