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

    private Button addButton, noButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String userId;
    public String familyName, familyBloodType,familyConditions,familyAllergies,familyDescription;
    public String familyEmail;
    public String familyPhone;

    public String adderName,adderBloodType,adderConditions,adderAllergies,adderDescription;
    public String adderEmail;
    public String adderPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_searched_user);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        noButton=findViewById(R.id.button);

        //SearchModel model = (SearchModel) getIntent().getSerializableExtra("model");
        Intent intent = getIntent();
        String uID = intent.getStringExtra("key");


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

                familyName = documentSnapshot.getString("userName");
                familyPhone = documentSnapshot.getString("phoneNumber");
                familyEmail = documentSnapshot.getString("email");
                familyBloodType = documentSnapshot.getString("blood");
                familyConditions = documentSnapshot.getString("medicalConditions");
                familyAllergies = documentSnapshot.getString("allergies");
                familyDescription = documentSnapshot.getString("address");

            }
        });

        documentReference1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                adderName = documentSnapshot.getString("userName");
                adderPhone = documentSnapshot.getString("phoneNumber");
                adderEmail = documentSnapshot.getString("email");
                adderBloodType = documentSnapshot.getString("blood");
                adderConditions = documentSnapshot.getString("medicalConditions");
                adderAllergies = documentSnapshot.getString("allergies");
                adderDescription = documentSnapshot.getString("address");
            }
        });






        addButton = findViewById(R.id.addButton);



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> user = new HashMap<>();
                user.put("userName", familyName);
                user.put("phoneNumber",familyPhone);
                user.put("email",familyEmail);
                user.put("blood",
                        familyBloodType);
                user.put("medicalConditions",familyConditions);
                user.put("allergies",familyAllergies);
                user.put("address",familyDescription);
                user.put("uID",uID);



                Map<String, Object> adder = new HashMap<>();
                adder.put("userName", adderName);
                adder.put("phoneNumber",adderPhone);
                adder.put("email",adderEmail);
                adder.put("blood",
                        adderBloodType);
                adder.put("medicalConditions",adderConditions);
                adder.put("allergies",adderAllergies);
                adder.put("address",adderDescription);
                adder.put("uID",userId);



                //documentReference3.set(user);
                //documentReference4.set(adder);
                familyReference.set(adder, SetOptions.merge());
                familyReference2.set(user,SetOptions.merge());
                adderReference.set(user,SetOptions.merge());
                adderReference2.set(adder,SetOptions.merge());

                Toast.makeText(OpenSearchedUser.this,"Added "+ familyName + " to your family!", Toast.LENGTH_SHORT).show();

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });





    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MyFamily.class));
    }
}