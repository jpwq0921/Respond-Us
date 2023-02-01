package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private double latitude;
    private double longitude;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public String userID = mAuth.getCurrentUser().getUid();
    public FieldValue time_added;
    public String offenderName;
    ArrayList<String> uids = new ArrayList<>();
    public CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        location = findViewById(R.id.Location);
        gpsTracker = new GPSTracker(SOS.this);


        getLocation = findViewById(R.id.getLoacation);

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

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String currentTime = simpleDateFormat.format(new Date());

            SimpleDateFormat simpleDayFormat = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = simpleDayFormat.format(new Date());

            Map<String, Object> location = new HashMap<>();
            location.put("latitude", latitude);
            location.put("longitude",longitude);
            location.put("timeOfIncident",currentTime);
            location.put("dateOfIncident",currentDate);
            location.put("offenderName",offenderName);

            db.collection("user").document(userID).collection("incidents").document()
                    .set(location, SetOptions.merge());

            db.collection("incidents").document().set(location,SetOptions.merge());

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            uids.add(document.getId());

                        }
                        for (String uid : uids) {
                            db.collection("user").document(uid).collection("incidents").document()
                                    .set(location,SetOptions.merge());                        }
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
}