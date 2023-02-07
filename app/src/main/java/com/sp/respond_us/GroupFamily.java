package com.sp.respond_us;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class GroupFamily extends AppCompatActivity {

    public Button searchFor;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference familyRef;
    private FirebaseAuth fAuth;
    private GroupFamilyAdapter adapter;
    String userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_family);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        familyRef = db.collection("user").document(userId).collection("dbFamily");
        searchFor = findViewById(R.id.searchformembers);

        searchFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupFamily.this, MyFamily.class);
                startActivity(intent);
            }
        });

        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        Query query = familyRef.orderBy("userName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<family> options = new FirestoreRecyclerOptions.Builder<family>()
                .setQuery(query,family.class)
                .build();

        adapter = new GroupFamilyAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GroupFamilyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                //Toast.makeText(GroupFamily.this, "It works!", Toast.LENGTH_SHORT).show();
                String id = documentSnapshot.getId();
                Intent intent = new Intent(GroupFamily.this ,familyMember.class);
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}