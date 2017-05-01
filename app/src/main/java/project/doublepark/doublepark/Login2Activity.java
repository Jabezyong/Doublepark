package project.doublepark.doublepark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login2Activity extends AppCompatActivity {

    EditText editTextCarPlate, editTextPassword;
    final List<UserInformation> userList = new ArrayList<>();
    //final String userEmail ="";

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        editTextCarPlate = (EditText) findViewById(R.id.editTextCarPlate);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            //close this activity
            //finish();
            //opening profile activity
            //startActivity(new Intent(getApplicationContext(), FloatingButtonActivity.class));
            firebaseAuth.signOut();
        }

        progressDialog = new ProgressDialog(this);
    }

    public void signIn(View view) {
        final String carplate = editTextCarPlate.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference().child("User")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //It is asynchronous, so need to insert inside
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserInformation user = snapshot.getValue(UserInformation.class);

                            //Will check the loop whether it match any car plate
                            if (user.carPlate.equals(carplate)) {

                                //Below is the sign in function
                                firebaseAuth.signInWithEmailAndPassword(user.email, password)
                                        .addOnCompleteListener(Login2Activity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                progressDialog.dismiss();
                                                //if the task is successfull
                                                if (task.isSuccessful()) {
                                                    //start the profile activity
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(), FloatingButtonActivity.class));
                                                } else {
                                                    Toast.makeText(Login2Activity.this, "Authentication failed:" +
                                                            task.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Login2Activity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
