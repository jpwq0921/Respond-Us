package com.sp.respond_us;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class updateProfile extends AppCompatActivity {
    Button updateButton, updateFromGallery, updateFromPhone;
    EditText updateUsername,updatePhone,updateEmail,updateMedicalConditions,updateBloodType,updateAllergies,updateDescription;
    ImageView updateImage;
    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    String userId;
    public Uri uri;
    FirebaseStorage storage;
    public String newUsername,newMedicalConditions,newBloodType,newAllergies,newDescription;
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
        updateMedicalConditions=findViewById(R.id.updateMedicalConditions);
        updateBloodType=findViewById(R.id.updateBloodType);
        updateAllergies=findViewById(R.id.updateAllergies);
        updateDescription=findViewById(R.id.updateAddress);


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
                updateMedicalConditions.setText(documentSnapshot.getString("medicalConditions"));
                updateBloodType.setText(documentSnapshot.getString("blood"));
                updateAllergies.setText(documentSnapshot.getString("allergies"));
                updateDescription.setText(documentSnapshot.getString("address"));
            }
        });







        updateFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        updateFromPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
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
                newMedicalConditions = updateMedicalConditions.getText().toString().trim();
                newBloodType = updateBloodType.getText().toString().trim();
                newAllergies = updateAllergies.getText().toString().trim();
                newDescription = updateDescription.getText().toString().trim();

                StorageReference anusref = storageRef.child("images/"+userId);


                Uri uri = (Uri) updateImage.getTag();
                if (uri == null) {
                    Drawable drawable = updateImage.getDrawable();
                    if (drawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        //Convert bitmap to a URI object
                        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null));
                    }
                }

                UploadTask uploadTask =anusref.putFile(uri);

                db.collection("user").document(userId).update("email", newEmail, "phoneNumber", newPhone, "" +
                        "userName", newUsername,"medicalConditions", newMedicalConditions,"blood",newBloodType,"allergies",newAllergies,"address",newDescription).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        startActivity(new Intent(this,MyProfile.class));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            updateImage.setImageBitmap(imageBitmap);
            uri = saveImageToUri(imageBitmap);
        }
    }

    private Uri saveImageToUri(Bitmap image) {
        // Create a file to save the image
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            // Compress the bitmap into the file
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            Log.e("saveImageToUri", e.getMessage());
        }
        // Return the file's Uri
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
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