package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class familyMember extends AppCompatActivity {
    TextView profileName,profileNumber,profileEmail,profileBloodType,profileMedicalConditions,profileAllergies,profileDescription;
    ImageView profilePfp;
    private FirebaseFirestore db;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);
        profileName=findViewById(R.id.profileUsername1);
        profileNumber=findViewById(R.id.profilePhonenumber1);
        profileEmail=findViewById(R.id.profileEmail1);
        profileBloodType=findViewById(R.id.profileBloodType1);
        profileMedicalConditions=findViewById(R.id.profileMedicalConditions1);
        profileAllergies=findViewById(R.id.profileAllergies1);
        profileDescription=findViewById(R.id.profileAddress1);
        profilePfp=findViewById(R.id.profileImage1);
        db =FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String uID = intent.getStringExtra("key");

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference pathRef = storageRef.child("images/"+uID);
        final long ONE_MEGABYTE = 1024 * 1024;
        pathRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>(){
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilePfp.setImageBitmap(bitmap);

            }
        });

        DocumentReference documentReference = db.collection("user").document(uID);
        Log.d("test", uID);
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
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }
}