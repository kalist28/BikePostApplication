package com.kalistratov.bikepost.views;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.api.Action;
import com.kalistratov.bikepost.api.Server;
import com.kalistratov.bikepost.api.inquiry.PostsListInquiry;
import com.kalistratov.bikepost.entitys.BikePost;
import com.kalistratov.bikepost.entitys.Entity;
import com.kalistratov.bikepost.entitys.EntityList;
import com.kalistratov.bikepost.map.PostsActivity;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private List<LatLng> locationList;
    private boolean action = false;

    private LocationCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationList = new LinkedList<>();

        Server.get().getResponse(new Action<EntityList<BikePost>>() {
            @Override
            public void action(final Response<EntityList<BikePost>> response) {
                Marker marker = null;
                BitmapDescriptor bitmap;
                System.out.println("****************************************************" + response.body().data);

                for (Entity<BikePost> p : response.body().data) {
                    BikePost post = p.getProperty();
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(post.getCoordinates())
                            .title(post.name)
                    );
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }

            @Override
            public Call<EntityList<BikePost>> getCall(Retrofit retrofit) {
                return retrofit.create(PostsListInquiry.class).getPosts();
            }
        });

        if (checkPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1);
        }


        Button button = findViewById(R.id.btn_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if ((action = !action)) {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    callback = createLocationRequest();
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
                    fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            callback,
                            Looper.myLooper()
                    );
                } else {
                    //fusedLocationClient.removeLocationUpdates(callback);
                    PolylineOptions polyline = new PolylineOptions();
                    for (LatLng l : locationList) {
                        polyline.add(l);
                    }
                    mMap.addPolyline(polyline);
                }
            }
        });
    }

    /**
     * Настройки запроса положения пользователя
     */
    protected LocationCallback createLocationRequest() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                Location l = locationResult.getLastLocation();
                locationList.add(new LatLng(l.getLatitude(), l.getLongitude()));
                System.out.println("** " + locationList.size());
            }
        };
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationCallback;
    }

    private boolean checkPermission(){
        return ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1: {
                boolean check = grantResults.length > 0;
                for (int r : grantResults){
                    check = check && r == PackageManager.PERMISSION_GRANTED;
                }
                if (!checkPermission()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                    }
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (!checkPermission()) mMap.setMyLocationEnabled(true);
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, BikePostActivity.class);
        intent.putExtra("Name", marker.getTitle());
        startActivity(intent);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

}