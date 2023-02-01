package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class OpenSearchedUser extends AppCompatActivity {

    private Button addButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String userId;
    public String familyName;
    public String familyEmail;
    public String familyPhone;

    public String adderName;
    public String adderEmail;
    public String adderPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_searched_user);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        //SearchModel model = (SearchModel) getIntent().getSerializableExtra("model");
        Intent intent = getIntent();
        String uID = intent.getStringExtra("key");
        TextView name = findViewById(R.id.openedUsername);
        TextView openedPhone = findViewById(R.id.openedPhone);
        TextView openedEmail = findViewById(R.id.openedEmail);
        TextView openeduID = findViewById(R.id.openeduID);

        DocumentReference documentReference = db.collection("user").document(uID);
        DocumentReference documentReference1 = db.collection("user").document(userId);

        DocumentReference familyReference = db.collection("user").document(userId).collection("dbFamily").document(userId);
        DocumentReference familyReference2 = db.collection("user").document(userId).collection("dbFamily").document(uID);
        DocumentReference adderReference = db.collection("user").document(uID).collection("dbFamily").document(uID);
        DocumentReference adderReference2 = db.collection("user").document(uID).collection("dbFamily").document(userId);


        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                name.setText(documentSnapshot.getString("userName"));
                openedPhone.setText(documentSnapshot.getString("phoneNumber"));
                openedEmail.setText(documentSnapshot.getString("email"));

                familyName = documentSnapshot.getString("userName");
                familyPhone = documentSnapshot.getString("phoneNumber");
                familyEmail = documentSnapshot.getString("email");
            }
        });

        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                adderName = documentSnapshot.getString("userName");
                adderPhone = documentSnapshot.getString("phoneNumber");
                adderEmail = documentSnapshot.getString("email");
            }
        });






        addButton = findViewById(R.id.addButton);


        openeduID.setText(uID);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> user = new HashMap<>();
                user.put("userName", familyName);
                user.put("phoneNumber",familyPhone);
                user.put("email",familyEmail);


                Map<String, Object> adder = new HashMap<>();
                adder.put("userName", adderName);
                adder.put("phoneNumber",adderPhone);
                adder.put("email",adderEmail);


                //documentReference3.set(user);
                //documentReference4.set(adder);
                familyReference.set(user, SetOptions.merge());
                familyReference2.set(adder,SetOptions.merge());
                adderReference.set(adder,SetOptions.merge());
                adderReference2.set(user,SetOptions.merge());

                Toast.makeText(OpenSearchedUser.this,"Added "+ familyName + " to your family!", Toast.LENGTH_SHORT).show();

            }
        });





    }
}