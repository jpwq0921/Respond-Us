package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity  {



    EditText signupPhone, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    //FirebaseDatabase database;
    //DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore db;
    private static final String TAG = "Register";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        signupPhone = findViewById(R.id.signup_phoneNumber);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);


        mAuth = FirebaseAuth.getInstance();
        //mUser=mAuth.getCurrentUser();

        db =FirebaseFirestore.getInstance();



        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = signupUsername.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String phoneNumber = signupPhone.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();

                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(Register.this,"Please enter your username", Toast.LENGTH_LONG).show();
                    signupUsername.setError("Username required!");
                    signupUsername.requestFocus();
                } else if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this,"Please enter your Email", Toast.LENGTH_LONG).show();
                    signupEmail.setError("Email required!");
                    signupEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(Register.this,"Please re-enter your email", Toast.LENGTH_LONG).show();
                    signupEmail.setError("Please provide a valid email!");
                    signupEmail.requestFocus();
                } else if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(Register.this,"Please enter your phone number", Toast.LENGTH_LONG).show();
                    signupPhone.setError("Number required!");
                    signupPhone.requestFocus();
                } else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this,"Please enter your password", Toast.LENGTH_LONG).show();
                    signupPassword.setError("Password required!");
                    signupPassword.requestFocus();
                } else if(password.length() < 6){
                    Toast.makeText(Register.this,"Too short", Toast.LENGTH_LONG).show();
                    signupPassword.setError("A minimum of 6 characters is required");
                    signupPassword.requestFocus();
                } else{
                    registerUser(userName,password,phoneNumber,email);
                }
            }
        });

    }

    private void registerUser(String userName, String password, String phoneNumber, String email){
        //FirebaseAuth auth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    Map<String, Object> user = new HashMap<>();
                    user.put("userName", userName);
                    user.put("phoneNumber", phoneNumber);
                    user.put("email", email);

                    //User user = new User(phoneNumber,email,userName);

                    db.collection("user").document(firebaseUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_LONG).show();
                        }
                    });



                    /*DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(Register.this,"User registered successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register.this,Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Register.this,"User registration has failed ", Toast.LENGTH_LONG).show();

                            }


                        }
                    });
                }else{
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e){
                        signupPassword.setError("Your password is too weak. Kindly use a mix of alphabets and numbers");
                        signupPassword.requestFocus();
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        signupPassword.setError("Your email is invalid or is already in use. Kindly re-enter");
                        signupPassword.requestFocus();
                    } catch(FirebaseAuthUserCollisionException e) {
                        signupPassword.setError("User is already registered with this email. Use another email");
                        signupPassword.requestFocus();
                    } catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Register.this,e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });*/
                }
            }

        });

}}