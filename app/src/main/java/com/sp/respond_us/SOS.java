package com.sp.respond_us;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SOS extends AppCompatActivity {

    ProgressBar progressBar;
    Button stop_timer;
    MyCountDownTimer myCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);


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
            Toast.makeText(SOS.this,"Calling for help!", Toast.LENGTH_LONG).show();
        }
    }


}