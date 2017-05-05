package project.doublepark.doublepark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotifyOwnerActivity extends AppCompatActivity {

    Button btnNotify;
    TextView txtViewName, txtViewCarPlate;
    CircleImageView imageView;
    LruCache<String, Bitmap> mMemoryCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_owner);

        final String carPlateNum = getIntent().getStringExtra(Tags.CAR_PLATE);
        final String name = getIntent().getStringExtra(Tags.NAME);
        final String email = getIntent().getStringExtra(Tags.EMAIL);
        final String phone = getIntent().getStringExtra(Tags.PHONE_NUMBER);
        final String photo_url = getIntent().getStringExtra(Tags.PHOTO);

        FirebaseDatabase database  = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child(ReplyActionReceiver.notificationTAG);
        txtViewName = (TextView) findViewById(R.id.textViewCarOwnerName);

        txtViewCarPlate = (TextView) findViewById(R.id.textViewCarPlateNumber);
        txtViewCarPlate.setText(carPlateNum);
        txtViewName.setText(name);
        imageView = (CircleImageView) findViewById(R.id.profile_picture);
        retrieveBitmapFromCache();

//        if(photo_url !=null){
//            ImageRequest request = new ImageRequest(photo_url, new Response.Listener<Bitmap>() {
//                @Override
//                public void onResponse(Bitmap response) {
//                    imageView.setImageBitmap(response);
//                    setContentView(R.layout.activity_notify_owner);
//                }
//            }, 0, 0, null, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //set default image
//                    imageView.setImageResource(R.drawable.ic_profile);
//                    setContentView(R.layout.activity_notify_owner);
//                }
//            });
//            MyVolley.getInstance(this).addToRequestQueue(request);
//        }
        btnNotify = (Button) findViewById(R.id.button_Notify);

        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NotifyOwnerActivity.this, CountdownActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putString("CAR_PLATE_NUMBER",carPlateNum);
                DatabaseReference push = myRef.push();
                String key = push.getKey();


                SharePrefManager manager = SharePrefManager.getInstance(getApplicationContext());;
                MyFirebaseNotification noti = new MyFirebaseNotification();
                noti.sender = manager.getEmail();
                noti.sender_carplate = manager.getCarPlate();
                noti.recipient = email;
                noti.recipient_carplate = carPlateNum;
                noti.date = new Date();
//                Map<String,String> map = new HashMap<String, String>();
//                map.put("sender",carPlateNum);
//                map.put("recipient","Jabez");
                //save into database

                push.setValue(noti);
                StringRequest stringRequest = PushNotification.sendPush(carPlateNum,key,getApplicationContext());
                MyVolley.getInstance(NotifyOwnerActivity.this).addToRequestQueue(stringRequest);
                bundle.putString("CAR_OWNER_NAME", name);
                bundle.putString("KEY",key);
                bundle.putString("CAR_PLATE_NUMBER",carPlateNum);
                bundle.putString("PHONE_NUMBER",phone);
                intent.putExtra("BUNDLE",bundle);
                startActivity(intent);
            }
        });

    }

    public void retrieveBitmapFromCache(){



        //To get bitmap from cache using the key. Must cast retrieved cache Object to Bitmap
        Bitmap bitmap = (Bitmap)Cache.getInstance().getLru().get(Tags.CAR_OWNER_PHOTO);

        //Setting imageView to retrieved bitmap from cache
        if(bitmap !=null) {
            imageView.setImageBitmap(bitmap);
        }else{
            imageView.setImageResource(R.drawable.ic_profile);
        }
        Cache.getInstance().getLru().remove(Tags.CAR_OWNER_PHOTO);

    }


    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
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
