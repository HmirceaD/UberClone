package com.example.maruta.uberfirebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CallActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SharedPreferences sh;
    private Button callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sh = getSharedPreferences("com.example.maruta.uberfirebase", Context.MODE_PRIVATE);

        callButton = findViewById(R.id.callButton);

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

        double drLat = Double.parseDouble(sh.getString("DriverLat", ""));
        double drLong = Double.parseDouble(sh.getString("DriverLong", ""));

        double rdLat = Double.parseDouble(sh.getString("RiderLat", ""));
        double rdLong = Double.parseDouble(sh.getString("RiderLong", ""));

        LatLng driver = new LatLng(drLat, drLong);
        LatLng rider = new LatLng(rdLat, rdLong);

        // Add a marker in Sydney and move the camera

        mMap.addMarker(new MarkerOptions().position(driver).title("Your location")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMap.addMarker(new MarkerOptions().position(rider).title("Rider's location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        callButton.setOnClickListener((View event) -> {

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + drLat + "," + drLong + "&daddr=" + rdLat +"," + rdLong));
            startActivity(intent);
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driver, 8));
    }
}
