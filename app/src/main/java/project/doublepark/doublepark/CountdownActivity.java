package project.doublepark.doublepark;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.iwgang.countdownview.CountdownView;

import static project.doublepark.doublepark.ReplyActionReceiver.notificationTAG;


public class CountdownActivity extends AppCompatActivity {

    Button btnNotifyAgain, btnDone;
    TextView txtViewCarPlateNumber, txtViewContactNumber, txtViewNotified,txtViewName;
    FirebaseDatabase database  = FirebaseDatabase.getInstance();
    String key;
    String phone;
    boolean firstTime = true;
    boolean firstNotification = true;
    String carPlateNum;
    Context mCtx;
    boolean isFirstTime = true;
    ChildEventListener listener;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        final CountdownView countdownView = (CountdownView) findViewById(R.id.countdownView);
        countdownView.start(300000); //5 minutes in millisecond
        firstTime = true;
        firstNotification = true;
        //Get the car plate number and owner name from previous activity
//        carPlateNum = getIntent().getStringExtra("CAR_PLATE_NUMBER");
        String name = getIntent().getBundleExtra("BUNDLE").getString("CAR_OWNER_NAME");
        key = getIntent().getBundleExtra("BUNDLE").getString("KEY");
        phone = getIntent().getBundleExtra("BUNDLE").getString("PHONE_NUMBER");
        carPlateNum = getIntent().getBundleExtra("BUNDLE").getString("CAR_PLATE_NUMBER");
        mCtx = getApplicationContext();
        txtViewCarPlateNumber = (TextView) findViewById(R.id.textView_Notify_CarPlateNum);
        txtViewCarPlateNumber.setText(carPlateNum);
        txtViewContactNumber = (TextView) findViewById(R.id.textView_Notify_ContactNumber);
        txtViewNotified = (TextView) findViewById(R.id.textView_notified);
        txtViewName = (TextView) findViewById(R.id.textView_Notify_CarOwnerName);
        txtViewName.setText(name);
       btnNotifyAgain = (Button) findViewById(R.id.button_notify_again);
        btnDone = (Button) findViewById(R.id.button_done);

        //Getting the key to send notification

        txtViewContactNumber.setText(phone);
        listenToFirebase(key);
        btnNotifyAgain.setEnabled(false);

        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                btnNotifyAgain.setEnabled(true);
            }
        });

        btnNotifyAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Need to display the contact number after 5 minutes


                if(firstNotification) {
                    countdownView.start(60000);
                    btnNotifyAgain.setEnabled(false);
                    firstNotification = false;
                }else {
                    countdownView.start(0);
                    txtViewContactNumber.setVisibility(View.VISIBLE);
                }
                String c = carPlateNum;
                String k = key;
                StringRequest stringRequest = PushNotification.sendPush(carPlateNum,key,getApplicationContext());
                MyVolley.getInstance(CountdownActivity.this).addToRequestQueue(stringRequest);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(CountdownActivity.this,HomepageActivity.class));
            }
        });

        setupFontForTitle();

    }

    private void listenToFirebase(String key) {

        myRef = database.getReference().child(notificationTAG).child(key);

        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String what = s;
                if(!firstTime) {
                    if (dataSnapshot.hasChildren()) {
                        MyFirebaseNotification noti = dataSnapshot.getValue(MyFirebaseNotification.class);
                        String tag = noti.notificationTAG;
                        if (tag.equals(Tags.receiveTAG)) {
                            txtViewNotified.setTextColor(getResources().getColor(R.color.green_notification));
                            NotificationDialog dialog = new NotificationDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("NOTIFICATION_MSG"
                                    ,getString(R.string.notification_received));
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), "show");
                        }
                    }
                }else{
                    firstTime = false;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        if(listener!=null)
        myRef.addChildEventListener(listener);



    }
    public void notifyAgain(View view){
    }
    @Override
    public void onBackPressed() {
        //Do not do anything as they cannot press back.
        //Override to re-direct them back to the homepage activity
        finish();
        startActivity(new Intent(this,HomepageActivity.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listener != null){
            myRef.removeEventListener(listener);
        }
    }

    private void setupFontForTitle() {
        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(R.string.notify);
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
}
