package com.sp.respond_us;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class updateProfile extends AppCompatActivity {
    Button updateButton, updateFromGallery, updateFromPhone;
    EditText updateUsername,updatePhone,updateEmail;
    ImageView updateImage;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    String userId;
    public Uri uri;
    FirebaseStorage storage;
    public String newUsername;
    public String newPhone;
    public String newEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        updateImage = findViewById(R.id.updateImage);
        updateButton = findViewById(R.id.updateButton);
        updateUsername = findViewById(R.id.updateUsername);
        updatePhone = findViewById(R.id.updatePhone);
        updateEmail = findViewById(R.id.updateEmail);
        updateFromGallery = findViewById(R.id.galleryUpdate);
        updateFromPhone = findViewById(R.id.phoneCameraUpdate);


        if(ContextCompat.checkSelfPermission(updateProfile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            },100);
        }




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
                updateImage.setImageBitmap(bitmap);

            }
        });

        DocumentReference documentReference = db.collection("user").document(userId);
        Log.d("test", userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                updateUsername.setText(documentSnapshot.getString("userName"));
                updatePhone.setText(documentSnapshot.getString("phoneNumber"));
                updateEmail.setText(documentSnapshot.getString("email"));
            }
        });







        updateFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });




        userId = fAuth.getCurrentUser().getUid();


        FirebaseUser firebaseUser = fAuth.getCurrentUser();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newUsername = updateUsername.getText().toString().trim();
                newPhone = updatePhone.getText().toString().trim();
                newEmail = updateEmail.getText().toString().trim();






                StorageReference anusref = storageRef.child("images/"+userId);

                UploadTask uploadTask =anusref.putFile(uri);



                db.collection("user").document(userId).update("email", newEmail, "phoneNumber", newPhone, "" +
                        "userName", newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(updateProfile.this, "Updated!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(updateProfile.this,MyProfile.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });



    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }



    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == this.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        uri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    uri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateImage.setImageBitmap(
                                selectedImageBitmap);

                    }
                }
            });
}