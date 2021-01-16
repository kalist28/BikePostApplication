package com.kalistratov.bikepost.map;

import android.Manifest;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.tools.permissions.PermissionChecker;

/**
 * Abstract class AMapActivity.
 *
 * Minimum settings for the map and the API for working with it.
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 */
public abstract class AMapActivity
        extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener {

    /**
     * Logging tag.
     */
    private final String TAG = getClass().getSimpleName();

    /** The main class of the card. */
    protected GoogleMap map;

    /**
     * The main entry point for interacting with the fused location provider.
     */
    protected FusedLocationProviderClient fusedLocationClient;

    /**
     * A data object that contains quality of service parameters for requests.
     * Additional settings for getting the coordinates of the location.
     */
    protected LocationRequest locationRequest;

    /**
     * Card readiness for work.
     */
    protected boolean mapIsReady;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkMyLocationPermission();

        setContentView(viewLayout());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.map        = googleMap;
        this.mapIsReady = true;

        checkMyLocationPermission();

        setDefaultUiSetting();
        setMyLocationEnabled(true);
        map.setOnMyLocationClickListener(this);
        map.setOnMyLocationButtonClickListener(this);

        moveCameraToLocation();
    }

    /**
     * Starts the location request service.
     * When receiving the location, the phone sets the camera on it.
     */
    @SuppressLint("MissingPermission")
    protected void moveCameraToLocation() {
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            map.moveCamera(CameraUpdateFactory.zoomTo(18));
                        }
                    }
                });
    }

    @Override
    public void onMyLocationClick(@NonNull final Location location) {
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    /**
     * Set user location enabled.
     */
    protected final void setMyLocationEnabled(final boolean bool) {
        if (checkMyLocationPermission())
            map.setMyLocationEnabled(bool);
    }

    /**
     * Checking the permissions for displaying personal tags on the map.
     * @return user permission.
     */
    protected boolean checkMyLocationPermission(){
        PermissionChecker checker = PermissionChecker.get(this);
        boolean fineLoc = checker
                .permGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean coarseLoc = checker
                .permGranted(Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLoc && coarseLoc;
    }

    /**
     * Setting up the visual display of tools on the map.
     */
    protected final void setDefaultUiSetting() {
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * @return object of setting up the visual display of tools on the map.
     */
    protected UiSettings getUiSetting() {
        return map.getUiSettings();
    }

    /**
     * @return view for activity.
     */
    public abstract int viewLayout();
}