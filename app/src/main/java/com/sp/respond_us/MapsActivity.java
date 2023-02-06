package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.respond_us.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;



public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Marker currentLocationMarker;
    private Polyline polyline;
    private LatLng markedLocation;

    private GoogleMap googleMap;
    private ActivityMapsBinding binding;
    private double crashLat = 0.0d;
    private double crashlon = 0.0d;
    private boolean focusOnUser = true;

    private LatLng ACCIDENT;
    private LatLng ME;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userID;
    String accidentTeeName;


    Button toggleFocusButton;

    double distance;
    private TextView distanceBet;


    private double latitude = 2.3953132;
    private double longitude = -112.7511896;

    private double myLat = 0.0d;
    private double myLon = 0.0d;


    private GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        distanceBet = findViewById(R.id.distance);
        toggleFocusButton= findViewById(R.id.focus_button);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.youarehere);
        Intent intent = getIntent();
        String uID = intent.getStringExtra("key");
        crashLat = intent.getDoubleExtra("lat",0.0);
        crashlon = intent.getDoubleExtra("lon",0.0);



        gpsTracker = new GPSTracker(MapsActivity.this);
        myLat = gpsTracker.getLatitude();
        myLon = gpsTracker.getLongitude();





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.incidentMap);
        mapFragment.getMapAsync(this);*/



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the LocationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (currentLocationMarker == null) {
                        currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.youarehereee)));
                    } else {
                        currentLocationMarker.setPosition(currentLocation);
                    }

                    // Update the polyline between the user's location and the marked location
                    /*PolylineOptions options = new PolylineOptions().add(currentLocation, markedLocation).width(5).color(Color.RED);
                    googleMap.addPolyline(options);*/
                    updatePolyline(currentLocation);
                    distance = SphericalUtil.computeDistanceBetween(currentLocation,markedLocation);
                    userID = new Double(distance).toString();
                    distanceBet.setText(userID);

                    // Move the camera to focus on either the user's location or the marked location
                    if (focusOnUser) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17f));
                    } else {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markedLocation, 17f));
                    }
                }
            }
        };
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.incidentMap);
        mapFragment.getMapAsync(this);
    }


    private void updatePolyline(LatLng currentLocation) {
        if (polyline != null) {
            polyline.remove();
        }
        polyline = googleMap.addPolyline(new PolylineOptions()
                .add(currentLocation, markedLocation)
                .width(5)
                .color(Color.RED));
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
        this.googleMap = googleMap;
        googleMap.getUiSettings().setCompassEnabled(true);
        markedLocation = new LatLng(crashLat, crashlon);
        googleMap.addMarker(new MarkerOptions().position(markedLocation).title("Crashed Location")
                .icon(BitmapDescriptorFactory.defaultMarker()));
        //googleMap.addPolyline(new PolylineOptions().add(latLng, mMarker2));
        toggleFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFocus(view);
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    public void toggleFocus(View view) {
        focusOnUser = !focusOnUser;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        startActivity(new Intent(this,IncidentActivity.class));
    }
}