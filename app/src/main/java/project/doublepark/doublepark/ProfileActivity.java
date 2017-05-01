package project.doublepark.doublepark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profilePicture;
    TextView textViewCarPlate, textViewEmail;
    EditText editTextName;
    Button btnAddContact;
    Button btnUpdateProfile;
    Button btnLogout;
    Spinner contactList;
    UpdateProfilePicTask mAsynTask;
    AdView adView;
    boolean flag; // used when user updated his profile photo then upload to firebase too;
    //Array list of contact number
    //Need to retrieve from Firebase
    ArrayList<String> contactNum = new ArrayList<String>();

    //Sharepreferencemanager
    SharePrefManager manager;
    ProgressDialog progressDialog;
    private final static int RESULT_SELECT_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle("Profile");
        setupFloatingButton();
        progressDialog = new ProgressDialog(ProfileActivity.this);
        manager = SharePrefManager.getInstance(getApplicationContext());

        textViewCarPlate = (TextView) findViewById(R.id.profile_car_number);
        editTextName = (EditText) findViewById(R.id.profile_name);
        textViewEmail = (TextView) findViewById(R.id.profile_email);
        btnLogout = (Button) findViewById(R.id.button_logout);

        textViewCarPlate.setText(manager.getCarPlate());
        editTextName.setText(manager.getName());
        textViewEmail.setText(manager.getEmail());

        contactList = (Spinner) findViewById(R.id.spinner_contact_no);
        //Get the contact number from shared preferences
        contactNum = manager.getContactList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,contactNum);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        contactList.setAdapter(adapter);
        contactList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Collections.swap(contactNum,0,i);

                //Update the contact list on Firebase
                FirebaseDatabase.getInstance().getReference().child("User")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("User");

                                firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactNo").setValue(contactNum);
                                manager.saveContactList(contactNum);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //Alert dialog box for user to add contact number into their profile
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();
        //Add an edit text for user to input contact number
        final EditText editTextNewContact = new EditText(getApplicationContext());
        alertDialog.setMessage("Enter a new contact number");
        alertDialog.setTitle("Add Contact");
        alertDialog.setView(editTextNewContact);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newContact = editTextNewContact.getText().toString();
                contactNum.add(newContact);

                String carPlate = manager.getCarPlate();


                //Add the new contact number to firebase here ---
                FirebaseDatabase.getInstance().getReference().child("User")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("User");

                                firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("contactNo").setValue(contactNum);
                                manager.saveContactList(contactNum);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });

        btnAddContact = (Button) findViewById(R.id.button_add_contact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make sure that the alert dialog is not in the view
                alertDialog.cancel();
                alertDialog.dismiss();


                alertDialog.show();
            }
        });

        //Setup the image selection for the profile picture
        profilePicture = (CircleImageView) findViewById(R.id.profile_picture);
        loadProfilePicture();
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    //Pick Image From Gallery
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_SELECT_IMAGE);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


        btnUpdateProfile = (Button) findViewById(R.id.button_update_profile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Retrieve the latest text from the textbox
                //Contact No does not need to be updated


                progressDialog.setMessage("Updating profile...");
                progressDialog.show();

                FirebaseDatabase.getInstance().getReference().child("User")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("User");

                                String name = editTextName.getText().toString();

                                firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(name);

                                manager.saveContactList(contactNum);
                                if(flag){
                                    profilePicture.buildDrawingCache();
                                    mAsynTask = new UpdateProfilePicTask(profilePicture.getDrawingCache(),ProfileActivity.this);
                                    mAsynTask.execute();
//            mAuthTask.execute((Void) null);
                                }else{
                                    progressDialog.dismiss();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),FirstScreenActivity.class));
            }
        });
        setupAds();
    }

    private void setupAds() {
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);
    }

    private void loadProfilePicture() {
        Cache instance = Cache.getInstance();
        Bitmap bitmap = (Bitmap) instance.getLru().get(Tags.PERSONAL_PROFILE);
        if(bitmap == null){
            String url = SharePrefManager.getInstance(getApplicationContext()).getProfilePicture();
            if(url != null) {
                loadImageToCache(url);
            }
        }else{
            profilePicture.setImageBitmap(bitmap);
        }

    }
    private void loadImageToCache(String url) {
        final Cache instance = Cache.getInstance();
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                instance.getLru().put(Tags.PERSONAL_PROFILE,response);
                profilePicture.setImageBitmap(response);
//                dismissDialogAndNewActivity();
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle error

            }
        });
        MyVolley.getInstance(this).addToRequestQueue(request);

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
                startActivity(new Intent(ProfileActivity.this,FloatingButtonActivity.class));
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

        // Fourth SubActionButton (About Us)
        SubActionButton.Builder itemBuilderForth = new SubActionButton.Builder(this);
        //set te size of fourth icon
        itemBuilderForth.setLayoutParams(params);
        // repeat many times:
        ImageView itemIconForth = new ImageView(this);
        itemIconForth.setImageResource(R.drawable.ic_aboutus_button);
        SubActionButton button4 = itemBuilderForth.setContentView(itemIconForth).build();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                ImageView imageView = (ImageView) findViewById(R.id.createEventImageFile);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri uri = data.getData();

            doCrop(uri);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //Bitmap bitmap = CropImageView
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    bitmap = scale(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Log.d(TAG, String.valueOf(bitmap));


                profilePicture.setImageBitmap(bitmap);
                flag = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void doCrop(Uri imageUri){
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(136,136)
                .setCropShape(CropImageView.CropShape.OVAL)
                //.setMaxCropResultSize(1000,480)
                .setAspectRatio(1,1)
                .start(this);
    }

    private Bitmap scale(Bitmap b) {
        return Bitmap.createScaledBitmap(b,profilePicture.getWidth(),profilePicture.getHeight(),  false);
    }

    public class UpdateProfilePicTask extends AsyncTask<Void,Void,Boolean> {
        Bitmap drawingCache;
        Context context;
        public UpdateProfilePicTask(Bitmap bitmap,Context context){
            this.drawingCache = bitmap;
            this.context = context;
        }

        private void updateProfileUrl(final String url, final Context context) {
            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebase.getReference();
            final String carPlate = manager.getCarPlate();
            if(carPlate == null){
                FirebaseAuth.getInstance().signOut();
                return;
            }
            //TO UPDATE THE Token on fireabase
            Query query = reference.child("User").orderByChild("carPlate").equalTo(carPlate);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
//                if(iterator.hasNext()) {
                    DataSnapshot nodeDataSnapshot = iterator.next();
                    String key = nodeDataSnapshot.getKey();
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("photo", url);
                    reference.child(path).updateChildren(result);
                    progressDialog.dismiss();
                    Toast.makeText(context, "Successfully updated profile!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(profilePicture != null){

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                drawingCache.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                SharePrefManager manager = SharePrefManager.getInstance(getApplicationContext());
                StorageReference filepath =
                        mStorage.child("UserPhotos").child( manager.getEmail()+ ".png");;
                UploadTask uploadTask = filepath.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")  String photo
                                = taskSnapshot.getDownloadUrl().toString();
                        updateProfileUrl(photo,context);
                    }
                });
            }
            return true;
        }
    }
}
