package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.respond_us.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double crashLat;
    private double crashlon;
    private double myLat;
    private double myLon;
    private LatLng ACCIDENT;
    private LatLng ME;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userID;
    String accidentTeeName;


    private GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();
        gpsTracker = new GPSTracker(MapsActivity.this);

        myLat = gpsTracker.getLatitude();
        myLon = gpsTracker.getLongitude();

        Intent intent = getIntent();
        String uID = intent.getStringExtra("key");

        DocumentReference incidentRef = db.collection("user").document(userID).collection("incidents").document(uID);

        incidentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                crashLat = documentSnapshot.getDouble("latitude");
                crashlon = documentSnapshot.getDouble("longitude");
                accidentTeeName = documentSnapshot.getString("offenderName");
                //Toast.makeText(MapsActivity.this,crashLat + " " + crashlon,Toast.LENGTH_SHORT).show();
            }
        });




        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.incidentMap);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ACCIDENT = new LatLng(crashLat,crashlon);
        ME = new LatLng(myLat,myLon);

        Marker incident = mMap.addMarker(new MarkerOptions().position(ACCIDENT).title(accidentTeeName));
        Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("You").snippet("My Location").icon(BitmapDescriptorFactory
                .fromResource(R.drawable.youarehere)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ACCIDENT));
    }
}