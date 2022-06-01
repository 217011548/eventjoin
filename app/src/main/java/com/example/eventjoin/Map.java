package com.example.eventjoin;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private List<Double> latitude = new ArrayList<Double>();
    private List<Double> longitude = new ArrayList<Double>();
    private List<String> name = new ArrayList<String>();
    private Double myLat, myLong;
    private LocationManager locationManager;
    private MapView mapView;
    private GoogleMap map;
    private Location myLocation;
    private Location lastKnownLocation;
    private List<Address> addresses;
    public double lat = 0.0d;
    public double lon = 0.0d;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }




    @Override
    public boolean onMyLocationButtonClick() {
        Location myLocation = map.getMyLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()),
                16.0f));
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(6.0f);
        map.setMaxZoomPreference(20.0f);
        enableLocation();
    }

    private void enableLocation() {

        map.setOnMyLocationButtonClickListener(Map.this);
        map.setOnMyLocationClickListener(Map.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        Places.createClient(Map.this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Map.this);
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(Map.this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                       /* if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 18.0f));
                            lat = lastKnownLocation.getLatitude();
                            lon = lastKnownLocation.getLongitude();
                        }*/
                    } else {
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(0.0,
                                        0.0), 10f));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });



            DatabaseReference event = FirebaseDatabase.getInstance().getReference("Events");
            event.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot keynode : dataSnapshot.getChildren()) {
                        Event event = keynode.getValue(Event.class);
                        latitude.add(event.getLatitude());
                        longitude.add(event.getLongitude());
                        name.add(event.getEvent_name());

                    }
                    for (int i = 0; i < latitude.size(); i++) {
                        Double lat = latitude.get(i);
                        Double lon = longitude.get(i);
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(name.get(i))
                                .draggable(true)
                        );

                    }
                    map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(latitude.get(0), longitude.get(0)), 12f));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
}
