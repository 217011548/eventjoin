package com.example.eventjoin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapSelect extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView locationinfo;
    private Location lastKnownLocation;
    private List<Address> addresses;
    public double lat = 0.0d;
    public double lon = 0.0d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapselect);
        locationinfo = findViewById(R.id.locationinfo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        View sure = findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("lat", lat);
                intent.putExtra("lon", lon);
                intent.putExtra("name", locationinfo.getText().toString());
                MapSelect.this.setResult(RESULT_OK, intent);
                MapSelect.this.finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(6.0f);
        map.setMaxZoomPreference(20.0f);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                LatLng latLngtemp = latLng;
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Select this address")
                        .draggable(true)
                );
                lat = latLngtemp.latitude;
                lon = latLngtemp.longitude;
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                List<Address> addresses;
                Geocoder geocoder = new Geocoder(MapSelect.this, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String zipCode = addresses.get(0).getPostalCode();
                    String country = addresses.get(0).getCountryCode();
                    locationinfo.setText(addresses.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        enableLocation();
    }

    private void enableLocation() {

        map.setOnMyLocationButtonClickListener(MapSelect.this);
        map.setOnMyLocationClickListener(MapSelect.this);
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
        Places.createClient(MapSelect.this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapSelect.this);
        try {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(MapSelect.this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 18.0f));
                            lat = lastKnownLocation.getLatitude();
                            lon = lastKnownLocation.getLongitude();
                            List<Address> addresses;
                            Geocoder geocoder = new Geocoder(MapSelect.this, Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String zipCode = addresses.get(0).getPostalCode();
                                String country = addresses.get(0).getCountryCode();

                                for (int i = 0; i < addresses.size(); i++) {
                                    Log.e("====>",addresses.get(i).getAddressLine(0));
                                }

                                locationinfo.setText(addresses.get(0).getAddressLine(0));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(0.0,
                                        0.0), 10f));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    private int type = 1;
    @Override
    public boolean onMyLocationButtonClick() {
        type = 1;
        Location myLocation = map.getMyLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()),
                18.0f));
        Geocoder geocoder = new Geocoder(MapSelect.this, Locale.getDefault());
        lat = myLocation.getLatitude();
        lon = myLocation.getLongitude();
        try {
            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zipCode = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryCode();

            for (int i = 0; i < addresses.size(); i++) {
                Log.e("====>", addresses.get(i).getAddressLine(0));
            }

            locationinfo.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
