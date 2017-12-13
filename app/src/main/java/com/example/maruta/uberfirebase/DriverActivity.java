package com.example.maruta.uberfirebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DriverActivity extends AppCompatActivity {

    private ListView callsList;
    private List<String> list1;
    private ArrayAdapter<String> listAdp;
    private List<UserLocation> usrLoc;

    //Get the user location
    private Location crrLoc;
    private LocationManager locMag;
    private boolean permLocationGranted;
    private LocationListener locList;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SharedPreferences sh;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permLocationGranted = false;

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                permLocationGranted = true;

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        initSh();

        initUi();

        initGeolocation();

        initLocation();

        getLocationPerm();

    }

    private void initSh(){

        sh = getSharedPreferences("com.example.maruta.uberfirebase", Context.MODE_PRIVATE);

        sh.edit().putString("DriverLat", "").apply();
        sh.edit().putString("DriverLong", "").apply();
        sh.edit().putString("RiderLat", "").apply();
        sh.edit().putString("RiderLong", "").apply();
    }

    private void initLocation() {

        Criteria crit = new Criteria();

        String prov = locMag.getBestProvider(crit, false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            crrLoc = locMag.getLastKnownLocation(prov);
        }

        if(crrLoc == null){

            crrLoc = new Location("");

            crrLoc.setLongitude(23.5613026);
            crrLoc.setLatitude(45.6841343);

        }

        Log.i("zile", crrLoc.toString());

    }

    private void initGeolocation() {

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

    private void initUi() {

        callsList = findViewById(R.id.callsList);

        list1 = new ArrayList<>();

        usrLoc = new ArrayList<>();

        listAdp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list1);

        populateList();

        callsList.setAdapter(listAdp);

        callsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent it = new Intent(DriverActivity.this, CallActivity.class);

                sh.edit().putString("DriverLat", Double.toString(crrLoc.getLatitude())).apply();
                sh.edit().putString("DriverLong", Double.toString(crrLoc.getLongitude())).apply();

                sh.edit().putString("RiderLat", Double.toString(usrLoc.get(position).getLatitude())).apply();
                sh.edit().putString("RiderLong", Double.toString(usrLoc.get(position).getLongitude())).apply();

                startActivity(it);

            }
        });

    }

    private void populateList() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snap:dataSnapshot.getChildren()){

                    UserLocation temp = snap.child("Location").getValue(UserLocation.class);

                    double buffer = calculateDistance(temp);

                    if(buffer != 0d){

                        if(buffer > 1000){

                            listAdp.add("Distance to call is " + (int)buffer/1000 + " km");
                            usrLoc.add(temp);
                        }else if(buffer < 1) {

                            listAdp.add("Distance to call is less than 1 km");
                            usrLoc.add(temp);
                        }else{

                            listAdp.add("Distance to call is " + (int)buffer + " meters");
                            usrLoc.add(temp);
                        }

                        listAdp.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private double calculateDistance(UserLocation a){

        Location tempLoc = new Location("Location");

        tempLoc.setLatitude(a.getLatitude());
        tempLoc.setLongitude(a.getLongitude());

        double z = crrLoc.distanceTo(tempLoc);

        //if(z < 250000){

            return z;
        //}

        //return 0d;

    }

    private void getUserLocation(){

        try{

            if(permLocationGranted){

                Task locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(this, new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful()) {

                            crrLoc = (Location) task.getResult();

                            Log.i("Locatie", crrLoc.toString());

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

    private void getLocationPerm() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            permLocationGranted = true;
            locMag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locList);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
