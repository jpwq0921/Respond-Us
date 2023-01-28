package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class Login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    String email, password;
    Button loginButton;
    TextView signupRedirectText;


    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signupRedirectText = findViewById(R.id.signupRedirectText);
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);


        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        //=mAuth.getCurrentUser();

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();

            }
        });
        
    }

    private void reLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String check = sharedPreferences.getString("name","");
        if(check.equals("true")) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(Login.this, "Auto Login Successful!", Toast.LENGTH_LONG).show();
        }
    }

    private void performLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();


        if(email.isEmpty())
        {
            loginEmail.setError("Email required!");
            loginEmail.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please provide a valid email!");
            loginEmail.requestFocus();
        }else if(password.isEmpty()){
            loginPassword.setError("Password required!");
            loginPassword.requestFocus();
        }else if(password.length() < 6){
            loginPassword.setError("A minimum of 6 characters is required");
            loginPassword.requestFocus();
        }else{
            authenticationUser(email,password);
        }
    }


    private void sendUserToNextActivity() {
        Intent intent = new Intent(Login.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void authenticationUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //FirebaseUser  user = mAuth.getCurrentUser();
                    //String user1 = user.getUid();

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", "true");
                    editor.apply();

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(Login.this,"Login Successful!",Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(Login.this,"Error signing in ",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
