package com.example.maruta.uberfirebase;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RiderActivitty extends FragmentActivity implements OnMapReadyCallback {

    private Button callBtn;

    private GoogleMap mMap;
    private LocationManager locMag;
    private LocationListener locList;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location crrLoc;//current location

    private FirebaseAuth fAuth;
    private String crrUserEmail;

    private boolean permLocationGranted;
    private boolean isOn = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permLocationGranted = false;

        if(requestCode == 1){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                permLocationGranted = true;

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_activitty);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initUi();

        initFirebase();

        initGeoLocation();

        //Check for permission
        getLocationPerm();

    }

    private void initGeoLocation() {

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locMag = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locList = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                getUserLocation();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    private void initFirebase(){

        fAuth = FirebaseAuth.getInstance();

        crrUserEmail = fAuth.getCurrentUser().getEmail();

        crrUserEmail = buildString(crrUserEmail);

        fAuth = FirebaseAuth.getInstance();

    }

    private void initUi(){

        callBtn = findViewById(R.id.callButton);

        callBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!isOn){

                    callBtn.setText("Cancel Uber");

                    isOn = true;

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    Map<String, UserLocation> mp = new HashMap<>();

                    if(crrLoc != null) {

                        mp.put("Location", new UserLocation(crrLoc.getLatitude(), crrLoc.getLongitude(), true));

                    }

                    ref.child(crrUserEmail).setValue(mp);

                }else{

                    callBtn.setText("Call Uber");

                    isOn = false;

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    ref.child(crrUserEmail).child("Location").child("available").setValue(false);


                }

            }
        });
    }

    private String buildString(String aux) {

        aux = aux.replace("@", "");
        aux = aux.replace(".","");

        return aux;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        updateLocationUi();

        getUserLocation();
    }

    private void getUserLocation() {

        try{

            if(permLocationGranted){

                Task locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(this, new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful()){

                            crrLoc = (Location)task.getResult();

                            LatLng tempLat;

                            if(crrLoc == null){

                                tempLat = new LatLng(-34, 151);

                            }else {

                                tempLat = new LatLng(crrLoc.getLatitude(), crrLoc.getLongitude());
                            }

                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(tempLat).title("Current Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tempLat, 4));

                        }else{

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-34, 151), 4));
                        }
                    }
                });
            }

        }catch(SecurityException ex){

            ex.printStackTrace();

        }catch (NullPointerException ex){

            ex.printStackTrace();
        }

    }

    private void updateLocationUi() {


        if(mMap == null){

            return;
        }

        try{

            if(permLocationGranted){

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                locMag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locList);

            }else {

                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPerm();
            }

        }catch(SecurityException ex){

            ex.printStackTrace();
        }

    }

    private void getLocationPerm() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            permLocationGranted = true;
            locMag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locList);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
