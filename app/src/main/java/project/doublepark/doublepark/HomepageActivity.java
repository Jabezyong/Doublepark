package project.doublepark.doublepark;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class HomepageActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    EditText editTextCarPlate;
    Button enterBtn;
    LruCache<String, Bitmap> mMemoryCache;
    Intent intent;
    private ProgressDialog progressDialog;
    AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        //update token every time. to ensure it is active.
//        updateToken();
        setupFloatingButton();
        setupFontForTitle();
        progressDialog = new ProgressDialog(HomepageActivity.this);
        editTextCarPlate = (EditText) findViewById(R.id.editText_Search_CarPlateNumber);
        enterBtn = (Button) findViewById(R.id.buttonFindCarOwner);
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("5AAF75E33FCD2FCD8081C82F5481AA1B")
                .build();
        adView.loadAd(adRequest);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findCarOwner();
            }
        });
    }

    private void setupFontForTitle() {
        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("Double Park");
        tv.setTextSize(28);
        tv.setTextColor(Color.parseColor("#FFFFFF"));

        Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getResources().getAssets(),"Spork.ttf");
        tv.setTypeface(typeFace);
        SpannableString s = new SpannableString("Double Park");
        s.setSpan(typeFace, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
    }

    public void findCarOwner(){

        //Check firebase from here to get the car owner details
        //This is temporary
        progressDialog.setMessage("Searching");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("User")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Go through the snapshots from the database
                        //Snapshot is like the items in the database
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //Convert the snapshot into a user object
                            UserInformation user = snapshot.getValue(UserInformation.class);
                            //Check if the user's car plate number matches the input car plate number
                            String carPlateNumber = editTextCarPlate.getText().toString();
                            carPlateNumber = carPlateNumber.toUpperCase();

                            if(user.carPlate.equals(carPlateNumber)) {
                                intent = new Intent(HomepageActivity.this,NotifyOwnerActivity.class);
                                intent.putExtra(Tags.CAR_PLATE,carPlateNumber);
                                intent.putExtra(Tags.NAME,user.name);
                                intent.putExtra(Tags.EMAIL,user.email);
                                intent.putExtra(Tags.PHONE_NUMBER,user.contactNo.get(0));

                                intent.putExtra(Tags.PHOTO,user.photo);
                                if(user.photo != null){
                                    loadImageToCache(user.photo);
                                }else {
                                    dismissDialogAndNewActivity();
                                }
                                return;
                            }
                        }

                        //Car plate number cannot be found in the database
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Car plate number not found!", Toast.LENGTH_SHORT).show();
                        editTextCarPlate.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





    }
    public void dismissDialogAndNewActivity(){
        progressDialog.dismiss();
        startActivity(intent);
    }
    //save image to cache then can retrieve within few seconds in another activity
    private void loadImageToCache(String url) {
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                addBitmapToMemoryCache(Tags.CAR_OWNER_PHOTO,response);
                dismissDialogAndNewActivity();
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle error

            }
        });
        MyVolley.getInstance(this).addToRequestQueue(request);

    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Cache.getInstance().getLru().put(key, bitmap);
    }


    public void registerToken(View v) {
        FirebaseAuth.getInstance().signOut();
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
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
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
                startActivity(new Intent(getApplicationContext(),AboutUsActivity.class));
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
