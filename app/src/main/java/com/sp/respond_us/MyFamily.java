package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MyFamily extends AppCompatActivity    {

    private RecyclerView mFirestoreList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SearchAdapter adapter;
    private CollectionReference userRef = db.collection("user");
    SearchModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_family);

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Query query = userRef.orderBy("email", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<SearchModel> options = new FirestoreRecyclerOptions.Builder<SearchModel>()
                .setQuery(query,SearchModel.class)
                .build();
        adapter = new SearchAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener(){

            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                SearchModel searchModel = documentSnapshot.toObject(SearchModel.class);
                String id = documentSnapshot.getId();
                Toast.makeText(MyFamily.this,"Postion!" + position + "ID" + id,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MyFamily.this,OpenSearchedUser.class);
                intent.putExtra("key",id);
                startActivity(intent);


            }
        });
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
}