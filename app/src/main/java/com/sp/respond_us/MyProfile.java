package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

public class MyProfile extends AppCompatActivity {

    ImageButton button;
    Button update;
    TextView profileName,profileNumber,profileEmail,profileBloodType,profileMedicalConditions,profileAllergies,profileDescription;
    ImageView profilePfp;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    String userId;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        button = findViewById(R.id.imageButton);
        profileName=findViewById(R.id.profileUsername);
        profileNumber=findViewById(R.id.profilePhonenumber);
        profileEmail=findViewById(R.id.profileEmail);
        profileBloodType=findViewById(R.id.profileBloodType);
        profileMedicalConditions=findViewById(R.id.profileMedicalConditions);
        profileAllergies=findViewById(R.id.profileAllergies);
        profileDescription=findViewById(R.id.profileAddress);
        update = findViewById(R.id.updateProfile);
        profilePfp = findViewById(R.id.profileImage);


        db =FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        userId = fAuth.getCurrentUser().getUid();
        StorageReference pathRef = storageRef.child("images/"+userId);

        final long ONE_MEGABYTE = 1024 * 1024;
        pathRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>(){
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePfp.setImageBitmap(bitmap);

            }
        });


        //Checks the document for the logged in user's UID
        DocumentReference documentReference = db.collection("user").document(userId);
        Log.d("test", userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                profileName.setText(documentSnapshot.getString("userName"));
                profileNumber.setText(documentSnapshot.getString("phoneNumber"));
                profileEmail.setText(documentSnapshot.getString("email"));
                profileBloodType.setText(documentSnapshot.getString("blood"));
                profileMedicalConditions.setText(documentSnapshot.getString("medicalConditions"));
                profileAllergies.setText(documentSnapshot.getString("allergies"));
                profileDescription.setText(documentSnapshot.getString("address"));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MyProfile.this,updateProfile.class);
                startActivity(intent1);
                finish();
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfile.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }
}