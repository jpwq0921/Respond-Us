package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SOS extends AppCompatActivity {

    ProgressBar progressBar;
    Button stop_timer, getLocation;
    MyCountDownTimer myCountDownTimer;
    TextToSpeech mTTS;
    SeekBar mSeekBarPitch, mSeekBarSpeed;
    Vibrator vibrator;


    TextView location = null;
    private GPSTracker gpsTracker;
    private double latitude =0.0d;
    private double longitude = 0.0d;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public String userID = mAuth.getCurrentUser().getUid();
    public FieldValue time_added;
    public String offenderName;
    ArrayList<String> uids = new ArrayList<>();
    public CollectionReference collectionReference;
    private boolean red = false;
    private Handler handler = new Handler();
    private View layout;
    String address;
    private TextView countdownText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);


        gpsTracker = new GPSTracker(SOS.this);
        layout = findViewById(android.R.id.content);
        handler.post(blink);



        countdownText = findViewById(R.id.countdownText);

        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (millisUntilFinished / 1000));
                countdownText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                progressBar.setProgress(0);
                countdownText.setText("0");
            }
        }.start();


        //mAuth = FirebaseAuth.getInstance();
        collectionReference = db.collection("user").document(userID).collection("dbFamily");



        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if(result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language Not Supported");
                    } else{

                    }
                } else{
                    Log.e("TTS", "Language Not Supported");
                }
            }
        });


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        progressBar =  findViewById(R.id.progressBar);
        stop_timer =  findViewById(R.id.button2);

        myCountDownTimer = new MyCountDownTimer(5000,1000);
        myCountDownTimer.start();


        stop_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCountDownTimer.cancel();
                Toast.makeText(SOS.this,"SOS cancelled", Toast.LENGTH_LONG).show();
                finish();
            }
        });



        db.collection("user").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                offenderName = documentSnapshot.getString("userName");
            }
        });
    }
    private Runnable blink = new Runnable() {
        @Override
        public void run() {
            if (red) {
                layout.setBackgroundColor(getResources().getColor(android.R.color.white));
                red = false;
            } else {
                layout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                red = true;
            }
            handler.postDelayed(blink, 500);
        }
    };

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished/1000);

            progressBar.setProgress(progressBar.getMax()-progress);
        }

        @Override
        public void onFinish() {
            speak();
            vibratee();
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String currentTime = simpleDateFormat.format(new Date());

            SimpleDateFormat simpleDayFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = simpleDayFormat.format(new Date());

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0).getAddressLine(0);
                    // do something with the address
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, Object> location = new HashMap<>();
            location.put("latitude", latitude);
            location.put("longitude",longitude);
            location.put("timeOfIncident",currentTime);
            location.put("dateOfIncident",currentDate);
            location.put("offenderName",offenderName);
            location.put("offenderID",userID);
            location.put("timestamp",FieldValue.serverTimestamp());
            location.put("address",address);

            /*db.collection("user").document(userID).collection("incidents").document()
                    .set(location, SetOptions.merge());*/

            db.collection("incidents").document().set(location,SetOptions.merge());
            Toast.makeText(SOS.this, address, Toast.LENGTH_SHORT).show();

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            uids.add(document.getId());

                        }
                        for (String uid : uids) {
                            db.collection("user").document(uid).collection("incidents").document()
                                    .set(location, SetOptions.merge());
                        }
                    }
                }
            });









            //Toast.makeText(SOS.this,"Calling for help!", Toast.LENGTH_LONG).show();
        }
    }


    private void vibratee() {
        long[] pattern = {0,200,10,500};
        vibrator.vibrate(pattern,0);

    }

    private void speak(){
        String text = "Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me Help me";
        float pitch = 500 / 50;
        /*if (pitch < 0.1){
            pitch = 0.1f;
        }*/

        float speed = 50 / 50;
        /*if (speed < 0.1){
            pitch = 0.1f;
        }*/

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(0.01f);


        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);



    }


    @Override
    protected void onDestroy() {
        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        vibrator.cancel();
        super.onDestroy();
        gpsTracker.stopUsingGPS();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }
}