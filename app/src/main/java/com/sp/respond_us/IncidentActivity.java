package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;



public class IncidentActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private CollectionReference incidentRef;
    private DocumentReference actualIncident;
    private IncidentAdapter adapter;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    Incident model;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        incidentRef = db.collection("user").document(userID).collection("incidents");

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = incidentRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Incident> options = new FirestoreRecyclerOptions.Builder<Incident>()
                .setQuery(query,Incident.class)
                .build();

        adapter = new IncidentAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new IncidentAdapter.OnItemClickListener(){

            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                //Incident searchModel = documentSnapshot.toObject(SearchModel.class);
                String id = documentSnapshot.getId();
                latitude=documentSnapshot.getDouble("latitude");
                longitude=documentSnapshot.getDouble("longitude");

                //Toast.makeText(IncidentActivity.this,latitude + " and "  + longitude,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IncidentActivity.this ,MapsActivity.class);
                intent.putExtra("key",id);
                intent.putExtra("lat",latitude);
                intent.putExtra("lon",longitude);
                startActivity(intent);

                //Open into google maps with polyline
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }
}