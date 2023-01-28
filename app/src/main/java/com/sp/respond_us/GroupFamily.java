package com.sp.respond_us;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GroupFamily extends AppCompatActivity {

    public Button searchFor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_family);

        searchFor = findViewById(R.id.searchformembers);

        searchFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupFamily.this, MyFamily.class);
                startActivity(intent);
            }
        });



    }
}