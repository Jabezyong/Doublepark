package project.doublepark.doublepark;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    //Decalre variable for palette
    private EditText editTextCarPlate;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextContactNo;
    private EditText editTextPassword;
    private EditText editTextConPassword;
    private CircleImageView profilePicture;
    //Firebase declareation
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    //Declare UI
    ProgressDialog progressDialog;
    boolean flag; // used when user updated his profile photo then upload to firebase too;
    private final static int RESULT_SELECT_IMAGE = 100;
    String carPlate ="";
    UpdateProfilePicTask mAsynTask;
    DatabaseReference userReference;
    ValueEventListener checkUserExistenceListence;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Assign resources to the variable
        editTextCarPlate = (EditText)findViewById(R.id.editTextCarPlate);
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextContactNo = (EditText)findViewById(R.id.editTextContactNo);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextConPassword = (EditText)findViewById(R.id.editTextReenterPassword);
        profilePicture = (CircleImageView) findViewById(R.id.imageViewProfile);
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
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        setupFontForTitle();
    }

    public boolean checkCarPlateTaken(final String c,final String email, final String password){
        boolean isTaken;
        userReference = FirebaseDatabase.getInstance().getReference().child("User");
        checkUserExistenceListence = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserInformation user = snapshot.getValue(UserInformation.class);
                    if(user.carPlate.equals(carPlate)){
                        Toast.makeText(RegisterActivity.this,"The car plate has been taken",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                }
                createUser(email,password);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userReference.addValueEventListener(checkUserExistenceListence);
//        FirebaseDatabase.getInstance().getReference().child("User")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        //It is asynchronous, so need to insert inside
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            UserInformation user = snapshot.getValue(UserInformation.class);
//                            if(user.carPlate.equals(carPlate)){
//                                Toast.makeText(RegisterActivity.this,"The car plate has been taken",Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
//                                return;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        return false;
    }

    private void newActivity() {
        Toast.makeText(RegisterActivity.this,"Registered Successful",Toast.LENGTH_SHORT).show();
        finish();
        Intent intent = new Intent(RegisterActivity.this, HomepageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void registerUser(View view){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String conPassword = editTextConPassword.getText().toString().trim();
        carPlate = editTextCarPlate.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(conPassword)){
            Toast.makeText(this,"The password you entered does not match",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();
        // Make no space between and upper every character.

        String carPlateNoSpace = carPlate.replaceAll("\\s+","");
        carPlate = carPlateNoSpace.toUpperCase();
        checkCarPlateTaken(carPlate,email,password);
    }

    public void saveUserInformation(){
        String name = editTextName.getText().toString();
        String contactNo = editTextContactNo.getText().toString().trim();

        ArrayList<String> contactList = new ArrayList<String>();
        contactList.add(contactNo);


        String email = editTextEmail.getText().toString().trim();
        //token is used to notify user via server
        final String token = SharePrefManager.getInstance(this).getDeviceToken();
        SharePrefManager.getInstance(this).saveCarPlate(carPlate);

        SharePrefManager.getInstance(this).saveEmail(email);
        SharePrefManager.getInstance(this).saveName(name);
        SharePrefManager.getInstance(this).saveContactList(contactList);

        saveTokenToServer(carPlate,token);
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserInformation userInformation = new UserInformation(carPlate,name,contactList,email,token);
        mDatabase.child("User").child(user.getUid()).setValue(userInformation);
        Toast.makeText(RegisterActivity.this,"User Information saved",Toast.LENGTH_SHORT).show();
    }

    public void saveTokenToServer(final String carplate, final String token){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoints.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
//                            Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
//                        Toast.makeText(HomepageActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",carplate);
                params.put("token",token);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

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

    public class UpdateProfilePicTask extends AsyncTask<Void,Void,Void> {
        Bitmap drawingCache;
        Context context;
        public UpdateProfilePicTask(Bitmap bitmap,Context context){
            this.drawingCache = bitmap;
            this.context = context;
        }

        private void updateProfileUrl(final String url, final Context context) {
            FirebaseDatabase firebase = FirebaseDatabase.getInstance();
            final DatabaseReference reference = firebase.getReference();
            SharePrefManager manager = SharePrefManager.getInstance(getApplicationContext());
            manager.saveProfilePicUrl(url);
            final String carPlate = manager.getCarPlate();
            if(carPlate == null){
                FirebaseAuth.getInstance().signOut();
                return;
            }
            //TO UPDATE THE photo url on fireabase
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
                    if(progressDialog!=null)
                    progressDialog.dismiss();
                    newActivity();
//                    Toast.makeText(context, "Successfully updated profile!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
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
                        //Save image into cache
                        Cache.getInstance().saveProfileImage(drawingCache);
                        updateProfileUrl(photo,context);
                    }
                });
            }
            return null;
        }
    }

    private void createUser(String email,String password){
        userReference.removeEventListener(checkUserExistenceListence);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    saveUserInformation();
                    progressDialog.dismiss();

                    if(flag){
                        profilePicture.buildDrawingCache();
                        mAsynTask = new UpdateProfilePicTask(profilePicture.getDrawingCache(),RegisterActivity.this);
                        mAsynTask.execute();
                    }else {
                        //go to another activity
                        newActivity();

                    }
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupFontForTitle() {
        TextView tv = new TextView(getApplicationContext());

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(R.string.action_register);
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
