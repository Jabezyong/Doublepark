package project.doublepark.doublepark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Decalre variable for palette
    private EditText editTextCarPlate;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextContactNo;
    private EditText editTextPassword;
    private EditText editTextConPassword;

    //Firebase declareation
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    //Declare UI
    ProgressDialog progressDialog;

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

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public boolean checkCarPlateTaken(final String c,final String email, final String password){
        boolean isTaken;
        //Remove the space between character in carplate
        String carPlateNoSpace = c.replaceAll("\\s+","");
        final String carPlate = carPlateNoSpace.toUpperCase();
        FirebaseDatabase.getInstance().getReference().child("User")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //It is asynchronous, so need to insert inside
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserInformation user = snapshot.getValue(UserInformation.class);
                            if(user.carPlate.equals(carPlate)){
                                Toast.makeText(RegisterActivity.this,"The car plate has been taken",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }
                            else{
                                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {

                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            saveUserInformation();
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this,"Registered Successful",Toast.LENGTH_SHORT).show();



                                            //go to another activity
                                            finish();
                                            Intent intent = new Intent(RegisterActivity.this,HomepageActivity.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return false;
    }

    public void registerUser(View view){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String conPassword = editTextConPassword.getText().toString().trim();
        String carPlate = editTextCarPlate.getText().toString().trim();

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

        checkCarPlateTaken(carPlate,email,password);
    }

    public void saveUserInformation(){
        String name = editTextName.getText().toString();
        String contactNo = editTextContactNo.getText().toString().trim();

        ArrayList<String> contactList = new ArrayList<String>();
        contactList.add(contactNo);

        String carPlate = editTextCarPlate.getText().toString().trim();
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
//                            Toast.makeText(HomepageActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
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

}
