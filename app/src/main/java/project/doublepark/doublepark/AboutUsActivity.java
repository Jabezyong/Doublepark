package project.doublepark.doublepark;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

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
                startActivity(new Intent(AboutUsActivity.this,ProfileActivity.class));
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
                startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
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
                startActivity(new Intent(getApplicationContext(),ReportActivity.class));
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

    private void setupFontForTitle() {
        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(R.string.aboutus_title);
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
        Intent intent = new Intent(AboutUsActivity.this, HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_us, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_privacy_policy:
                Intent intent = new Intent(AboutUsActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
